/**
 * 儿童1岁以内体格检查表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup.inOne")
$import("chis.application.cdh.script.checkup.ChildrenCheckupFormUtil",
		"chis.script.util.helper.Helper")
chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm = function(
		cfg) {
	this.oneCase = "1";
	this.twoCase = "3";
	this.threeCase = "6";
	this.fourCase = "9";
	chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm,
		chis.application.cdh.script.checkup.ChildrenCheckupFormUtil, {

			onDoNew : function() {
				chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm.superclass.onDoNew
						.call(this);
				var checkupStage = this.exContext.args[this.checkupType
						+ "_param"].checkupStage;

				var eqOne = false;
				if (checkupStage == 1) {
					eqOne = true;
				}
				this.changeFieldState(!eqOne, "navel");

				var gtThree = false;
				if (checkupStage > 3) {
					gtThree = true;
				}
				this.changeFieldState(gtThree, "navelState");

				var ltSix = false;
				if (checkupStage < 6) {
					ltSix = true;
				}
				this.changeFieldState(ltSix, "hearing");
				this.changeFieldState(ltSix, "decayedTooth");

				var gtSix = false;
				if (checkupStage > 6) {
					gtSix = true;
				}
				this.changeFieldState(gtSix, "neckMass");
			},

			onReady : function() {
				chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm.superclass.onReady
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
				chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneForm.superclass.onLoadData
						.call(this, entryName, body);
				var bregmaClose = body.bregmaClose;
				if (bregmaClose && bregmaClose.key) {
					this.onBregmaCloseSelect(bregmaClose.key);
				}
				var checkupStage = this.exContext.args[this.checkupType
						+ "_param"].checkupStage;
				var form = this.form.getForm();
				var kyglbtz = form.findField("kyglbtz");
				var kyglbtzValue = body.kyglbtz;
				if (!kyglbtzValue) {
					return;
				}
				kyglbtzValue = kyglbtzValue.key;
				var dic = [];
				if (checkupStage == this.oneCase
						|| checkupStage == this.twoCase) {
					dic = [{
								key : "1",
								text : "无"
							}, {
								key : "2",
								text : "颅骨软化"
							}, {
								key : "3",
								text : "方颅"
							}, {
								key : "4",
								text : "枕秃"
							}];
				} else {
					dic = [{
								key : "1",
								text : "肋串珠"
							}, {
								key : "2",
								text : "肋外翻"
							}, {
								key : "3",
								text : "肋软骨沟"
							}, {
								key : "4",
								text : "鸡胸"
							}, {
								key : "5",
								text : "手镯征"
							}];
				}
				var newValue=dic[parseInt(kyglbtzValue)-1];
				kyglbtz.setValue(newValue);
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