/**
 * 家庭档案-->问题列表-->新建或查看表单页面
 * 
 * @author tianj
 */
$package("chis.application.fhr.script");

$import("chis.script.BizTableFormView");

chis.application.fhr.script.FamilyProblemForm = function(cfg) {
	cfg.colCount = 2;
	chis.application.fhr.script.FamilyProblemForm.superclass.constructor.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.fhr.script.FamilyProblemForm, chis.script.BizTableFormView, {
	onReady : function() {
		chis.application.fhr.script.FamilyProblemForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var occurDate = form.findField("happenDate");
		if (occurDate) {
			occurDate.on("blur", this.onSolveDateChange, this);
			occurDate.on("keyup", this.onSolveDateChange, this);
		}
	},
	
	onLoadData : function() {
		var form = this.form.getForm();
		var f = form.findField("happenDate");
		if (f) {
			this.onSolveDateChange(f);
		}
	},
	
	onSolveDateChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var solveDate = this.form.getForm().findField("solveDate");
		var constriction = field.getValue();
		if (!constriction) {
			solveDate.setMinValue(null);
			solveDate.validate();
			return;
		}
		solveDate.setMinValue(constriction);
		solveDate.validate();
	},
	
	doSave : function() {
		this.data["familyId"] = this.exContext.args.initDataId;
		chis.application.fhr.script.FamilyProblemForm.superclass.doSave.call(this);
	}
});