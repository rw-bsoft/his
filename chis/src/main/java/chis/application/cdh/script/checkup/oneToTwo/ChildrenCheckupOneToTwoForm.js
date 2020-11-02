/**
 * 儿童1-2岁以内体格检查表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup.oneToTwo")
$import("chis.application.cdh.script.checkup.ChildrenCheckupFormUtil")
chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm = function(cfg) {
	chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm,
		chis.application.cdh.script.checkup.ChildrenCheckupFormUtil, {

			onDoNew : function() {
				chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm.superclass.onDoNew
						.call(this);
				var checkupStage = this.exContext.args[this.checkupType
						+ "_param"].checkupStage;
				var gt24 = false;
				if (checkupStage > 24) {
					gt24 = true;
				}
				this.changeFieldState(gt24, "development");

				var bregmaClose = this.form.getForm().findField("bregmaClose");
				if (bregmaClose) {
					bregmaClose.setDisabled(gt24);
					bregmaClose.allowBlank = gt24;
				}
				var bregmaTransverse = this.form.getForm().findField("bregmaTransverse");
				if (bregmaTransverse) {
					bregmaTransverse.setDisabled(gt24);
					bregmaTransverse.allowBlank = gt24;
				}
				var bregmaLongitudinal = this.form.getForm().findField("bregmaLongitudinal");
				if (bregmaLongitudinal) {
					bregmaLongitudinal.setDisabled(gt24);
					bregmaLongitudinal.allowBlank = gt24;
				}
				var le12 = false;
				if (checkupStage <= 12) {
					le12 = true;
				}
				this.changeFieldState(le12, "gait");
			},

			onReady : function() {
				chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm.superclass.onReady
						.call(this);

				var form = this.form.getForm();
				var bregmaClose = form.findField("bregmaClose");
				if (bregmaClose) {
					bregmaClose.on("select", function(field) {
								var v = field.getValue();
								this.onBregmaCloseSelect(v);
							}, this);
				}
			},

			onLoadData : function(entryName, body) {
				chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoForm.superclass.onLoadData
						.call(this,entryName, body);
				var bregmaClose = body.bregmaClose;
				if (bregmaClose && bregmaClose.key) {
					this.onBregmaCloseSelect(bregmaClose.key);
				}
			},

			onBregmaCloseSelect : function(v) {
				var form = this.form.getForm();
				var bt = form.findField("bregmaTransverse");
				var bl = form.findField("bregmaLongitudinal");
				if (v == "1") {
					bt.setValue("");
					bt.disable();
					bt.allowBlank = true;
					bl.setValue("");
					bl.disable();
					bl.allowBlank = true;
				} else {
					bt.allowBlank = false;
					bt.invalidText = "必填字段";
					bt.enable();
					bl.allowBlank = false;
					bl.invalidText = "必填字段";
					bl.enable();
				}
				this.validate();
			}
		})
