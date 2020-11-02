/**
 * 非重点人群随访module中右边的表单
 * 
 * @author tianj
 */
$package("chis.application.npvr.script")

$import("util.Accredit", "chis.script.BizTableFormView", "util.widgets.LookUpField","chis.script.util.helper.Helper")

chis.application.npvr.script.NormalPopulationVisitForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 135;
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.isCombined = true;
	cfg.showButtonOnTop = true;
	cfg.labelWidth=110;
	chis.application.npvr.script.NormalPopulationVisitForm.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.npvr.script.NormalPopulationVisitForm, chis.script.BizTableFormView, {
			initPanel : function(){
				this.form = chis.application.npvr.script.NormalPopulationVisitForm.superclass.initPanel.call(this);
				this.onNewForm();
				return this.form;
			},
	
			doNew : function() {
				this.initDataId = this.exContext.args.initDataId;
				chis.application.npvr.script.NormalPopulationVisitForm.superclass.doNew
						.call(this);
			},

			onNewForm : function() {
				var nextTime = this.form.getForm().findField("nextTime");
				var p = new Date();
				var minNextTime = new Date(p.getFullYear(), p.getMonth(), p
								.getDate()
								+ 1);
				nextTime.setMinValue(minNextTime);
			},

			onReady : function() {
				chis.application.npvr.script.NormalPopulationVisitForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var con = form.findField("con");
				if (con) {
					con.on("blur", this.onSbpChange, this);
					con.on("keyup", this.onSbpChange, this);
				}

				var dia = form.findField("dia");
				if (dia) {
					dia.on("blur", this.onDbpChange, this);
					dia.on("keyup", this.onDbpChange, this);
				}
			},

			onSbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var constriction = field.getValue();
				if (!constriction) {
					return;
				}
				if (constriction > 500 || constriction < 10) {
					field.markInvalid("收缩压必须在10到500之间！");
					return;
				}
				var diastolicFld = this.form.getForm().findField("dia");
				var diastolic = diastolicFld.getValue();
				if (constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					diastolicFld.clearInvalid();
				}
			},

			onDbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var diastolic = field.getValue();
				if (!diastolic) {
					return;
				}
				if (diastolic > 500 || diastolic < 10) {
					field.markInvalid("舒张压必须在10到500之间！");
					return;
				}
				var constrictionFld = this.form.getForm().findField("con");
				var constriction = constrictionFld.getValue();
				if (constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					constrictionFld.clearInvalid();
				}
			},

			doCreate : function() {
				this.exContext.args.initDataId = null;
				this.doNew();
				
				var nextTime = this.form.getForm().findField("nextTime");
				var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				nextTime.setMinValue(now);
				nextTime.validate();
			},

			getSaveRequest : function(saveData) {
				saveData.phrId = this.exContext.args.phrId;
				saveData.empiId = this.exContext.args.empiId;
				return saveData;
			},

			loadData : function() {
				this.doNew();
				var data = this.exContext.args.data;
				if (!data) {
					this.resetButtons();
					return;
				}
				var inputData =Date.parseDate(data["createDate"].substr(0,10), "Y-m-d");
				data = this.castListDataToForm(data, this.schema);
				this.initFormData(data);
				//修改时最小大时间为录入时间
				var nextTime = this.form.getForm().findField("nextTime");
				nextTime.setMinValue(chis.script.util.helper.Helper.getOneDayAfterDate(inputData));
				nextTime.validate();
			}
		});