$package("chis.application.cvd.script")

$import("chis.script.BizTableFormView")

chis.application.cvd.script.TestDictForm = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 158
	cfg.showButtonOnTop = true;
	cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.width = 750;
	chis.application.cvd.script.TestDictForm.superclass.constructor.apply(this, [cfg])
	this.on("save", this.onSave, this)
}
Ext.extend(chis.application.cvd.script.TestDictForm, chis.script.BizTableFormView, {

	onSave : function(entryName, op, json, data) {
		this.doCancel();
	}
		// ,
		// onReady : function() {
		// chis.application.cvd.script.TestDictForm.superclass.onReady.call(this);
		// var testResult = this.form.getForm().findField("testResult");
		// if (testResult)
		// testResult
		// .on("beforeSelect", this.beforeSelectResult, this);
		// },
		// beforeSelectResult : function(field, record, index) {
		// var data = record.data
		// if (!data)
		// return false;
		// var value = data.key
		// var fieldName = "testItem" + value;
		// var testField = this.form.getForm().findField(fieldName);
		// if (!testField)
		// return false;
		// var testValue = testField.getValue();
		// if (!testValue || testValue == "")
		// return false
		// else
		// return true;
		// }

	})