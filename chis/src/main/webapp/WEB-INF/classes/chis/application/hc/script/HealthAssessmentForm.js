$package("chis.mdcwl.client")
$import("chis.script.BizTableFormView","chis.script.util.helper.Helper")

chis.application.hc.script.HealthAssessmentForm = function(cfg) {
	cfg.colCount = 4
	cfg.autoFieldWidth = false
	cfg.labelWidth = 80
	cfg.fldDefaultWidth = 110
	this.printurl = chis.script.util.helper.Helper.getUrl();
	chis.application.hc.script.HealthAssessmentForm.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.hc.script.HealthAssessmentForm, chis.script.BizTableFormView, {
	onReady : function() {
		chis.application.hc.script.HealthAssessmentForm.superclass.onReady.call(this)

		var form = this.form.getForm();

		var recognizeF = this.form.getForm().findField("recognize")
		recognizeF.on("expand", this.onRecognizeExpand, this)
		recognizeF.tree.on("checkchange", this.onRecognize, this)
		this.recognizeF = recognizeF

		var abnormalityF = this.form.getForm().findField("abnormality")
		abnormalityF.on("select", this.onAbnormalityF, this);
		abnormalityF.on("keyup", this.onAbnormalityF, this);
		abnormalityF.on("blur", this.onAbnormalityF, this);
		this.abnormalityF = abnormalityF

		var riskfactorsControlF = this.form.getForm()
				.findField("riskfactorsControl")
		riskfactorsControlF.on("select", this.onRiskfactorsControlF, this);
		riskfactorsControlF.on("keyup", this.onRiskfactorsControlF, this);
		riskfactorsControlF.on("blur", this.onRiskfactorsControlF, this);
		this.riskfactorsControlF = riskfactorsControlF

	},

	doNew : function() {
		chis.application.hc.script.HealthAssessmentForm.superclass.doNew.call(this)
		this.onAbnormalityF()
		this.onRiskfactorsControlF()
	},

	initFormData : function(data) {
		chis.application.hc.script.HealthAssessmentForm.superclass.initFormData
				.call(this, data)
		this.onAbnormalityF()
		this.onRiskfactorsControlF()
	},

	onRecognizeExpand : function(combo) {
		combo.tree.expandAll();
	},

	onRecognize : function(node) {
		var parentNode = node.parentNode
		var nodes = parentNode.childNodes
		for (var i = 0; i < nodes.length; i++) {
			if (nodes[i].id != node.id) {
				nodes[i].getUI().check(false);
			}
		}
	},

	onAbnormalityF : function() {
		var form = this.form.getForm();
		if (this.abnormalityF.getValue() == '2') {
			form.findField("abnormality1").enable()
			form.findField("abnormality2").enable()
			form.findField("abnormality3").enable()
			form.findField("abnormality4").enable()
		} else {
			form.findField("abnormality1").disable()
			form.findField("abnormality1").setValue()
			form.findField("abnormality2").disable()
			form.findField("abnormality2").setValue()
			form.findField("abnormality3").disable()
			form.findField("abnormality3").setValue()
			form.findField("abnormality4").disable()
			form.findField("abnormality4").setValue()
		}
	},

	onRiskfactorsControlF : function() {
		if (this.riskfactorsControlF.getValue().indexOf("5") > -1) {
			this.form.getForm().findField("targetWeight").enable()
		} else {
			this.form.getForm().findField("targetWeight").disable()
			this.form.getForm().findField("targetWeight").setValue()
		}

		if (this.riskfactorsControlF.getValue().indexOf("6") > -1) {
			this.form.getForm().findField("vaccine").enable()
		} else {
			this.form.getForm().findField("vaccine").disable()
			this.form.getForm().findField("vaccine").setValue()
		}

		if (this.riskfactorsControlF.getValue().indexOf("7") > -1) {
			this.form.getForm().findField("pjOther").enable()
		} else {
			this.form.getForm().findField("pjOther").disable()
			this.form.getForm().findField("pjOther").setValue()
		}
	},

	doPrint : function() {
		// alert("健康检查打印需要安装PDF，如果打印未能显示请检查是否安装PDF")
		if (!this.initDataId) {
			return
		}
		var url = "resources/chis.prints.template.healthAssessment.print?type=" + 1 + "&empiId="
				+ this.empiId + "&healthCheck=" + this.healthCheck
				+ "&checkDate=" + this.checkDate
		url += "&temp=" + new Date().getTime()
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
	},

	getSaveRequest : function(saveData) {
		saveData.healthCheck = this.exContext.args.healthCheck;
		return saveData;
	},

	getLoadRequest : function() {
		return {
			"healthCheck" : this.exContext.args.healthCheck
		}
	}
})