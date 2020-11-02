$package("chis.application.mov.script.batch");

$import("chis.application.mov.script.ehr.EHRMoveUtilForm",
		"util.widgets.LookUpField",
		"chis.application.mpi.script.EMPIInfoModule",
		"chis.application.mov.script.util.QueryModule");

chis.application.mov.script.batch.ManaInfoBatchChangebyareagridForm = function(cfg) {
	cfg.autoLoadSchema = false
	chis.application.mov.script.batch.ManaInfoBatchChangebyareagridForm.superclass.constructor
			.apply(this, [cfg]);
	this.colCount = 2;
	this.op1 = "";

};

Ext.extend(chis.application.mov.script.batch.ManaInfoBatchChangebyareagridForm,
		 chis.script.BizTableFormView, {
		 	onReady : function() {
				chis.application.mov.script.batch.ManaInfoBatchChangebyareagridForm.superclass.onReady
						.call(this);
				var targetDoctor = this.form.getForm()
						.findField("targetDoctor");
				if (targetDoctor) {
					targetDoctor.on("select", this.changeManaUnit, this);
				}
			},
			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method:"execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},
				setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("targetUnit");
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.reset();
					return;
				}
				combox.setValue(manageUnit)
				combox.disable();
			},
			doSave : function() {
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				if(values.changeareagrid.length < 12){
				  Ext.Msg.alert("友情提示：","迁移的网格地址范围太广，请联系管理员迁移！");
				  return;
				}
				Ext.apply(this.data, values);
				this.saveToServer(values)
			}
		});