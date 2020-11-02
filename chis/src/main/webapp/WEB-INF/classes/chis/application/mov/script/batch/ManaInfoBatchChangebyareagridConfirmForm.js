$package("chis.application.mov.script.batch");

$import("chis.script.BizTableFormView",
		"util.widgets.LookUpField");

chis.application.mov.script.batch.ManaInfoBatchChangebyareagridConfirmForm = function(cfg) {
cfg.autoLoadSchema = false
	chis.application.mov.script.batch.ManaInfoBatchChangebyareagridConfirmForm.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.mov.script.batch.ManaInfoBatchChangebyareagridConfirmForm,
		chis.script.BizTableFormView, {
			doConfirm : function() {
				if(this.data["changeareagrid"].key.length < 12){
				  Ext.Msg.alert("友情提示：","迁移的网格地址范围太广，请联系管理员迁移！");
				  return;
				}
				this.data["affirmType"] = "1";
				this.op = "update";
				if(this.data["status"].key=="4"){
					Ext.Msg.alert("提示：","已确认！")
					return;
				}
				if(this.data["status"].key=="9"){
					Ext.Msg.alert("提示：","已退回！")
					return;
				}
				this.saveServiceId = "chis.manaInfoChangeService";
				this.saveAction = "saveConfirmareagridRecord";
				this.doSave();
				this.getWin().hide();
			},

			doReject : function() {
				this.data["affirmType"] = "2";
				if(this.data["status"].key=="4"){
					Ext.Msg.alert("提示：","已确认不能退回！")
					return;
				}
				if(this.data["status"].key=="9"){
					Ext.Msg.alert("提示：","已退回！")
					return;
				}
				this.op = "update";
				this.saveServiceId = "chis.manaInfoChangeService";
				this.saveAction = "saveRejectareagridRecord";
				this.doSave();
				this.getWin().hide();
			}
		});