$package("chis.application.psy.script.paper");

$import("chis.script.BizTableFormView");

chis.application.psy.script.paper.RecordPaperForm = function(cfg) {
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.colCount = "3";
	cfg.fldDefaultWidth = 140;
	cfg.labelWidth = 100;
	chis.application.psy.script.paper.RecordPaperForm.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.psychosisRecordPaperService";
	this.saveAction = "savePsyRecordPaper";
	this.on("save", this.onSave, this);
};

Ext.extend(chis.application.psy.script.paper.RecordPaperForm,
		chis.script.BizTableFormView, {
			doAdd : function() {
				this.initDataId = null;
				chis.application.psy.script.paper.RecordPaperForm.superclass.doNew
						.call(this);
			},

			doSave : function() {
				this.data.phrId = this.exContext.ids.phrId;
				chis.application.psy.script.paper.RecordPaperForm.superclass.doSave
						.call(this);
			},

			onSave : function(entryName, op, json, data) {
				this.fireEvent("add", op, json, data);
			},

			onReady : function() {
				var content = this.form.getForm().findField("content");
				content.setHeight(300);
				chis.application.psy.script.paper.RecordPaperForm.superclass.onReady
						.call(this);
			}
		});