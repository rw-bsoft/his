$package("chis.application.per.script.checkup");

$import("chis.script.BizTableFormView");

chis.application.per.script.checkup.CheckupSummaryForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.colCount = 4;
	cfg.labelWidth = 60;
	cfg.fldDefaultWidth = 100;
	chis.application.per.script.checkup.CheckupSummaryForm.superclass.constructor.apply(this,
			[cfg]);
};

Ext.extend(chis.application.per.script.checkup.CheckupSummaryForm, chis.script.BizTableFormView, {
			doSave : function() {
				this.fireEvent("save");
			},
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render;
				}
				cls += "DicFactory";

				$import(cls);
				var factory = eval("(" + cls + ")");
				var field = factory.createMixDic(dic);
				return field;
			},
			onReady : function() {
				chis.application.per.script.checkup.CheckupSummaryForm.superclass.onReady.call(this);

				var form = this.form.getForm();
				var summary = form.findField("checkupSummary");
				if (summary){
					summary.on("change", this.summaryChange, this);
				}
				var ifException = form.findField("ifException");
				if (ifException){
					ifException.on("select", this.excpChange, this);
				}
				var checkupDoctor = form.findField("checkupDoctor");
				if (checkupDoctor){
					checkupDoctor.on("blur", this.blurCheckupDoctor, this);
				}
				var inputDoctor = form.findField("inputDoctor");
				if (inputDoctor){
					inputDoctor.on("change", this.summaryChange, this);
				}
			},
			blurCheckupDoctor : function() {
				var checkupDoctor = this.form.getForm().findField("checkupDoctor");
				var key = checkupDoctor.getValue();
				var value = checkupDoctor.getRawValue();
				this.fireEvent("changeValue", key,value);
			},
			summaryChange : function(field, newValue, oldValue) {
				this.fireEvent("cacheData");
			},
			excpChange : function(field, newValue, oldValue) {
				var newText = field.getRawValue();
				var newValue = field.getValue();
				this.fireEvent("excpChange", newValue, newText, this);
			}
		});