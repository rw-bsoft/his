$package("phis.application.hos.script");

$import("app.modules.list.SimpleListView","phis.script.SMCardReader");

phis.application.hos.script.HospitalZynhdjList = function(cfg) {
	this.autoLoadData=false;
	Ext.apply(this,phis.script.SMCardReader);
	phis.application.hos.script.HospitalZynhdjList.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(phis.application.hos.script.HospitalZynhdjList,
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
			enableKeyEvents : true,
			disabled:false
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
			//农合刷卡（火狐浏览器） zhaojian 2017-09-21
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
			doReadybcard:function(){
				//实现市民卡读卡
				this.initHtmlElement();
				this.doCheck(this.readCard());
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
			doCheck:function(cardid){
//			this.mask("*****请耐心等待*****");
				if(cardid.length <7){
					MyMessageTip.msg("提示", "读卡失败！", true);
					return ;
				}
				this.nhkharea.setValue(cardid);
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
						//聚焦到确定按钮
						btnss = this.grid.getTopToolbar();
						var btns = btnss.find("cmd", "save");
						var btn = btns[0];
						btn.focus();
						var records = this.getExtRecord(ret.json.body);
						this.store.add(records);
						this.selectRow(0);
						if(records[0].data.NHKH){
							this.nhkharea.setValue(records[0].data.NHKH);
						}
					}
				}
//				this.unmask();
		},
		onEnterKey : function() {
			this.onDblClick();
		},
		doSave:function(){
			this.onDblClick();
		},
		onDblClick : function(grid, index, e) {
			var r=this.getSelectedRecord();
			if(!r){
//				MyMessageTip.msg("提示", "请选择一条记录！！！", true);
				return;
			}
			var body={};
			body.personName=r.get("BRXM")
			body.idCard=r.get("SFZH")
			body.sexCode=r.get("BRXB")
			body.birthday=r.get("CSNY")
			body.address=r.get("HKDZ")
			body.mobileNumber=r.get("LXDH")
			body.NHKH=r.get("NHKH")
			body.GRBH=r.get("GRBH")
			
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "savegrzhxx",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh()
					this.getWin().hide();
					this.fireEvent("zynhdkreturn", ret.json.body);
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