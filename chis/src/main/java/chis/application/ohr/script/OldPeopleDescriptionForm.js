/**
 * 老年人随访管理（中医辨体描述）
 * 
 * @param {}
 *            cfg
 */
$package("chis.application.ohr.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.ohr.script.OldPeopleDescriptionForm = function(cfg) {
	cfg.colCount = 1;
	cfg.fldDefaultWidth = 500;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true
	chis.application.ohr.script.OldPeopleDescriptionForm.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(chis.application.ohr.script.OldPeopleDescriptionForm, chis.script.BizTableFormView, {
			getDescription : function() {
				var data = {};
				data.recordId = this.exContext.args.initDataId;
				data.phrId = this.exContext.args.phrId;
				data.empiId = this.exContext.args.empiId;
				data.visitId = this.exContext.args.initDataId;
				data.description = this.form.getForm().findField("description")
						.getValue();
				data.inputUser = this.form.getForm().findField("inputUser")
						.getValue();
				data.inputDate = this.form.getForm().findField("inputDate")
						.getValue();
				data.inputUnit = this.form.getForm().findField("inputUnit")
						.getValue();
				return data;
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "visitId",
					"fieldValue" : this.exContext.args.initDataId
				};
			},

			doSave : function() {
				this.fireEvent("save");
			}
		});