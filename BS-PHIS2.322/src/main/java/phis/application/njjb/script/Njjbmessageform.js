$package("phis.application.njjb.script");

$import("phis.script.TableForm");

phis.application.njjb.script.Njjbmessageform = function(cfg) {
	this.entryName="phis.application.njjb.schemas.NJJB_KXX";
	phis.application.njjb.script.Njjbmessageform.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.njjb.script.Njjbmessageform,
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
			doSave:function(){
//				this.data.YLLB=this.form.getForm().findField("YLLB").value
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "savenjjbkxx",
						body:this.data
						});
				if (ret.code <=300) {
					this.getWin().hide();
					this.fireEvent("njjbdkreturn",ret.json.body)
				} else {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}		
			}
		});
