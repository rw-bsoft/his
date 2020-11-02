$package("phis.application.njjb.script");

$import("phis.script.TableForm");

phis.application.njjb.script.Njjbmessagenomalform = function(cfg) {
	this.entryName="phis.application.njjb.schemas.NJJB_KXX_DK";
	phis.application.njjb.script.Njjbmessagenomalform.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.njjb.script.Njjbmessagenomalform,
		phis.script.TableForm, {
			createButtons : function() {
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
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
			onReady : function() {
				phis.application.njjb.script.Njjbmessagenomalform.superclass.onReady.call(this);
				var form = this.form.getForm();
				var ybmcs = form.findField("YBMCS");
				if(ybmcs){
					this.ybmcs=ybmcs
					this.ybmcs.on("select",this.ybmcchange,this)
				}
				var ybmc = form.findField("YBMC");
				if(ybmc){
					this.ybmc=ybmc;
				}
			},
			ybmcchange: function() {
				var ybmctemp=this.ybmcs.getValue();
				var ybmcbm=this.ybmc.getValue();
				var yllb=this.form.getForm().findField("NJJBYLLB").getValue();
				if(yllb!="51"){
					ybmcbm=ybmctemp;
				}else{
					if(ybmcbm.indexOf(ybmctemp)>=0){
						if(ybmcbm.indexOf(","+ybmctemp+",")>=0 ){
							ybmcbm=ybmcbm.replace(","+ybmctemp+",",",")
						}else if(ybmcbm.indexOf(","+ybmctemp)>0 &&
						(ybmcbm.indexOf(","+ybmctemp)+ybmctemp.length+1)==ybmcbm.length){
							ybmcbm=ybmcbm.replace(","+ybmctemp,"")
						}else if(ybmcbm.indexOf(ybmctemp)==0 && ybmctemp.length==ybmcbm.length){
							ybmcbm="";
						} else{
							ybmcbm=ybmcbm+","+ybmctemp;
						}
					}else{
						ybmcbm=ybmcbm+","+ybmctemp;
					}
				}
				this.ybmc.setValue(ybmcbm);
			},
			createButtons : function() {
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
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
			doSave:function(){
				var form = this.form.getForm();
				this.data.NJJBYLLB=form.findField("NJJBYLLB").getValue();
				if(!this.data.NJJBYLLB || this.data.NJJBYLLB=="" || this.data.NJJBYLLB.length>3){
					form.findField("NJJBYLLB").setDisabled(false);
					MyMessageTip.msg("提示","请选择医疗类别！", true);
					return;
				}
				var ybzy=form.findField("YBZY").getValue();
				this.data.YBZY="";
				if(ybzy && ybzy.length >0){
					this.data.YBZY=ybzy;
				}
				var YBMC=form.findField("YBMC").getValue();
				if(YBMC && YBMC.length >0){
					var arr=YBMC.split(",")
					if(arr.length >3){
						MyMessageTip.msg("提示","医保病种最多只能选择三个！", true);
						return;
					}
					if(this.data.NJJBYLLB!="51" && arr.length>1){
						MyMessageTip.msg("提示","除职工计划生育外，其他医疗类别只能选一个病种！", true);
						return;
					}
					this.data.YBMC=YBMC;
				}
				var tempdata={};
				tempdata.YBMC=this.data.YBMC;
				tempdata.NJJBYLLB=this.data.NJJBYLLB;
				var re = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getYllbByJb",
						body:tempdata
					});
				if(re.code <=300){
					if(this.data.NJJBYLLB!=re.json.NJJBYLLB){
						MyMessageTip.msg("HIS提示","由于您选择的病种导致医疗类别改变可能不能结算！", true);
//						this.data.NJJBYLLB=re.json.NJJBYLLB
					}
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "savenjjbghyllb",
						body:this.data
						});
				if (ret.code <=300) {
					this.getWin().hide();
					this.fireEvent("njjbdkreturn",this.data)
				} else {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}		
			}
		});
