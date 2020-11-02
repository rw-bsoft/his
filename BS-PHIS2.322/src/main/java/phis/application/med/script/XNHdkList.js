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
						_this.doCheck(body.split("|")[1]);
        		},
        			error:function(XmlHttpRequest,textStatus, errorThrown){
  					alert("读卡失败："+XmlHttpRequest.responseText);
  				}
    			});
			},
			doReadsmcard:function(){
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
				
				this.nhkharea.setValue(cardid);
				if(cardid.length >6){
					this.win.hide();
					this.fireEvent("nhdkreturn", cardid);
				}
			}
		});