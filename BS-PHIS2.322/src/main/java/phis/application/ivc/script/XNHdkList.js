$package("phis.application.ivc.script");

$import("app.modules.list.SimpleListView","phis.script.SMCardReader");

phis.application.ivc.script.XNHdkList = function(cfg) {
	this.autoLoadData=false;
	Ext.apply(this,phis.script.SMCardReader);
	phis.application.ivc.script.XNHdkList.superclass.constructor
			.apply(this, [cfg]);		
};

Ext.extend(phis.application.ivc.script.XNHdkList,
		app.modules.list.SimpleListView, {
		createButtons : function() {
		if (this.op == 'read') {
			return [];
		}
		var actions = this.actions;
		var buttons = [];
		if (!actions) {
			return buttons;
		}
		var nhkharea= new Ext.form.TextField({
			id:"nhkharea",
			width:180,
			regex: /^\d+$/,
			regexText: '只能输入数字',
			enableKeyEvents : true//,
			//disabled:true
		});
		var me=this;
	nhkharea.on('keyup', function(f,e){
                    // 监听回车按键
                    if (e.getKey() == e.ENTER) {
                        me.doCheckbyself();
                    }});
	this.nhkharea=nhkharea;	
	buttons.push(nhkharea);
	this.nhkharea.focus(false,200);
	var f1 = 112;
	for (var i = 0; i < actions.length; i++) {
		var action = actions[i];
		var btn = {};
		btn.id=action.id;
		btn.accessKey = f1 + i;
		btn.cmd = action.id;
		btn.text = action.name + "(F" + (i + 1) + ")";
		btn.iconCls = action.iconCls || action.id;
		btn.script = action.script;
		btn.handler = this.doAction;
		btn.notReadOnly = action.notReadOnly;
		btn.scope = this;
		buttons.push(btn);
	}
		return buttons;
	},
			doReadcard:function(){
				var v = window.location.pathname.split("/")
				var http = "http://"+window.location.host+"/"+v[1]+"/"+"xnhdk/ick_pk.html";
		        window.showModalDialog(http, this, "dialogWidth:200px; dialogHeight:50px;status:no; directories:yes;scrollbars:no;Resizable=no;dialogLeft:600px;dialogTop:400px;");
			},
			//农合读卡（火狐浏览器） zhaojian 2017-09-21
			doReadcard_hh:function(){
				var _this = this;
				$.ajax({
        			type:'get',
        			url:"http://127.0.0.1:8888",
        			success:function(body,heads,status){
						//解决火狐浏览器读卡没有反应问题 zhaojian 2017-11-17
						try{
							_this.doCheck(body.split("|")[1]);
						}catch(e){
							_this.doCheck(status.responseText.split("|")[1]);
						}
        		},
        			error:function(XmlHttpRequest,textStatus, errorThrown){
  					alert("读卡失败："+XmlHttpRequest.responseText);
  				}
    			});
			},
			doReadsmcard:function(){
				//实现市民卡读卡
				this.initHtmlElement();
				this.doSmCheck(this.readCard());
			},
			//手动输入卡号敲回车
			doCheckbyself:function(){
				var val=this.nhkharea.getValue();
				if(!val){
					return;
				}else{
					this.doCheck(val);
				}
			},
			doSmCheck:function(cardid){
				this.nhkharea.setValue(cardid);
				if(cardid.length >6){
					//实现农合刷卡程序
					var dicName = {
	            		 id : "phis.dictionary.Hcnqzj"
	          		    };
					var qzj=util.dictionary.DictionaryLoader.load(dicName);
					var di = qzj.wraper[this.mainApp.deptId];
					var ipandport=""
					if (di) {
						ipandport = di.text;
					}
					if(ipandport==""){
						alert("未找到当前操作员工所在的医院的农合前置机配置！")
						return;
					}
					
					var body={};
					body.ipandport=ipandport;
					body.deptId=this.mainApp.deptId;
					body.operator=this.mainApp.uid;
					body.cardid=cardid;
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "phis.xnhService",
								serviceAction : "checkgrzh",
								body:body
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.refresh());
					} else {
						this.store.removeAll();
						if(ret.json.body.length){
							var records = this.getExtRecord(ret.json.body);
							this.store.add(records);
							this.selectRow(0);
							this.win.hide();
							this.fireEvent("nhdkreturn", records[0].data.NHKH);
						}
					}
				}
			},
			doCheck:function(cardid){
				
				this.nhkharea.setValue(cardid);
				if(cardid.length >6){
					this.win.hide();
					this.fireEvent("nhdkreturn", cardid);
				}
			},
			//处理下数据
			getExtRecord : function(data) {
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var record = new Ext.data.Record(data[i]);
					records.push(record);
				}
				return records;
			}
		});