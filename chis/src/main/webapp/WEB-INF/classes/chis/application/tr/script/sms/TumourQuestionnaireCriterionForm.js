$package("chis.application.tr.script.sms")

$import("chis.script.BizTableFormView", "util.widgets.LookUpField",
		"chis.script.util.query.QueryModule")

chis.application.tr.script.sms.TumourQuestionnaireCriterionForm = function(cfg) {
	chis.application.tr.script.sms.TumourQuestionnaireCriterionForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.tr.script.sms.TumourQuestionnaireCriterionForm,
		chis.script.BizTableFormView, {
			onBeforeCreate : function() {
				var form = this.form.getForm();
				var args = this.exContext.args;
				if (args) {
					this.initFormData(args);
					var highRiskType = args.highRiskType;
					var highRiskTypeFld = form.findField("highRiskType");
					if (highRiskTypeFld) {
						if (highRiskType) {
							highRiskTypeFld.disable();
						} else {
							highRiskTypeFld.enable();
						}
					}
					var criterionExplain = args.criterionExplain;
					var criterionExplainFld = form
							.findField("criterionExplain");
					if (criterionExplainFld) {
						if (criterionExplain) {
							criterionExplainFld.disable();
						} else {
							criterionExplainFld.enable();
						}
					}
				}
			},
			onLoadData : function(entryName, body) {
				var hrcId = body.hrcId;
				var form = this.form.getForm();
				var highRiskTypeFld = form.findField("highRiskType");
				if (highRiskTypeFld) {
					if (hrcId) {
						highRiskTypeFld.disable();
					} else {
						highRiskTypeFld.enable();
					}
				}
				var criterionExplainFld = form.findField("criterionExplain");
				if (criterionExplainFld) {
					if (hrcId) {
						criterionExplainFld.disable();
					} else {
						criterionExplainFld.enable();
					}
				}
			},
			onReady : function() {
				var frm = this.form.getForm();
				var masterplateNameFld = frm.findField("masterplateName");
				if (masterplateNameFld) {
					masterplateNameFld.on("lookup", this.doQuery, this);
					masterplateNameFld.on("clear", this.doClearName, this);
					masterplateNameFld.validate();
				}
				chis.application.tr.script.sms.TumourQuestionnaireCriterionForm.superclass.onReady
						.call(this);
			},

			doQuery : function(field) {
				if (!field.disabled) {
					var masterplateQuery = this.midiModules["MasterplateMaintainQuery"];
					if (!masterplateQuery) {
						masterplateQuery = new chis.script.util.query.QueryModule(
								{
									title : "问卷模板查询",
									autoLoadSchema : true,
									isCombined : true,
									autoLoadData : false,
									mutiSelect : false,
									queryCndsType : "1",
									entryName : "chis.application.mpm.schemas.MPM_MasterplateMaintainQuery",
									buttonIndex : 3,
									selectFormColCount : 3,
									itemHeight : 150
								});
						this.midiModules["MasterplateMaintainQuery"] = masterplateQuery;
					}
					masterplateQuery.on("recordSelected", function(r) {
								if (!r) {
									return;
								}
								// var personName = r[0].get("ownerName");
								var frmData = r[0].data;
								this.initDataBefShow(frmData);
							}, this);
					var win = masterplateQuery.getWin();
					win.setPosition(250, 100);
					win.show();
					masterplateQuery.form.initCnd = [
							'like',
							['$', 'a.manaUnitId'],
							[
									'concat',
									['substring', ['$', '%user.manageUnit.id'],
											0, 9], ['s', '%']]];
					var form = this.form.getForm();
					var highRiskTypeFld = form.findField("highRiskType");
					if (highRiskTypeFld) {
						var highRiskType = highRiskTypeFld.getValue();
						if(highRiskType){
							var whmbFld = masterplateQuery.form.form.getForm()
									.findField("whmb");
							if (whmbFld) {
								var key = '0' + (parseInt(highRiskType) + 2);
								var text = "";
								if (key == "03") {
									text = "肿瘤-大肠";
								} else if (key == "04") {
									text = "肿瘤-胃";
								} else if (key == "05") {
									text = "肿瘤-肝";
								} else if (key == "06") {
									text = "肿瘤-肺";
								} else if (key == "07") {
									text = "肿瘤-乳腺";
								} else if (key == "08") {
									text = "肿瘤-宫颈";
								}
								var whmVal = {
									"key" : key,
									"text" : text
								};
								whmbFld.setValue(whmVal);
							}
						}
					}
					masterplateQuery.form.doSelect();
				}
			},

			initDataBefShow : function(frmData) {
				var QMId = frmData.masterplateId;
				var masterplateName = frmData.masterplateName;
				var frm = this.form.getForm();
				var masterplateNameFld = frm.findField("masterplateName");
				if (masterplateNameFld) {
					masterplateNameFld.setValue(masterplateName);
				}
				this.data.QMId = QMId;
				this.fireEvent("selectMasterplate", QMId);
			},
			doClearName : function() {
				var frm = this.form.getForm();
				var masterplateNameFld = frm.findField("masterplateName");
				if (masterplateNameFld) {
					masterplateNameFld.setValue();
				}
				this.fireEvent("selectNoMasterplate");
			},

			doSave : function() {
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.data.hrcId = this.exContext.args.hrcId || '';
				this.fireEvent("saveToServer", this.data);
			}
		});