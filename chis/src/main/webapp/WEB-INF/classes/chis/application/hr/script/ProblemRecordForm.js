/**
 * 个人主要问题表单
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.hr.script.ProblemRecordForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 249;
	cfg.colCount = 2;
	cfg.width = 710;
	chis.application.hr.script.ProblemRecordForm.superclass.constructor.apply(this, [cfg]);
	this.on("save", this.onSave, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.hr.script.ProblemRecordForm, chis.script.BizTableFormView, {
	doNew : function() {
		chis.application.hr.script.ProblemRecordForm.superclass.doNew.call(this);
		if (this.exContext.ids.empiId) {
			this.data["empiId"] = this.exContext.ids.empiId;
		}
		if (this.op == "create" && !this.initDataId) {
			var form = this.form.getForm();
			var occurDate = form.findField("occurDate");
			if (occurDate) {
				occurDate.setValue(this.mainApp.serverDate);
				this.onSolveDateChange(occurDate);
			}
			var solveDate = form.findField("solveDate");
			if (solveDate) {
				solveDate.setValue(this.mainApp.serverDate);
			}
		}
	},

	onReady : function() {
		chis.application.hr.script.ProblemRecordForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var occurDate = form.findField("occurDate");
		if (occurDate) {
			occurDate.on("valid", this.getCycle, this);
			occurDate.on("blur", this.onSolveDateChange, this);
			occurDate.on("keyup", this.onSolveDateChange, this);
		}
	},

	onLoadData : function() {
		var occurDate = this.form.getForm().findField("occurDate");
		this.onSolveDateChange(occurDate);
	},

	getCycle : function(field) {
		var occurDate = field.getValue();
		if (!occurDate) {
			return;
		}
		util.rmi.jsonRequest({
					serviceId : "chis.healthRecordService",
					serviceAction : "queryLifeCycleId",
					method:"execute",
					body : {
						"empiId" : this.exContext.ids.empiId,
						"occurDate" : occurDate
					}
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.getCycle,[field]);
						return;
					}
					if (json.body) {
						var cycleId = json.body.cycleId;
						var cycleName = json.body.cycleName;
						this.form.getForm().findField("cycleId")
										.setValue({
													key : cycleId,
													text : cycleName
												});
					}
				}, this)
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
	
	doCancer : function() {
		this.getWin().hide();
	},
	
	onSave : function() {
		this.getWin().hide();
	}
});