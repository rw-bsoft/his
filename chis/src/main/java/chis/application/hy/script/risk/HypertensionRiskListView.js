$package("chis.application.hy.script.risk")

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule",
		"chis.application.hy.script.risk.HypertensionRiskWriteOffView",
		"chis.script.util.widgets.MyMessageTip")

chis.application.hy.script.risk.HypertensionRiskListView = function(cfg) {
	cfg.initCnd = ['and', ['eq', ['$', 'a.status'], ['s', '0']],
			['eq', ['$', 'a.statusCase'], ['s', '1']]];
	this.needOwnerBar = true;
	chis.application.hy.script.risk.HypertensionRiskListView.superclass.constructor
			.apply(this, [cfg]);
	this.businessType = "13";
}

Ext.extend(chis.application.hy.script.risk.HypertensionRiskListView,
		chis.script.BizSimpleListView, {
			createOwnerBar : function() {
				var manageLabel = new Ext.form.Label({
							html : "管辖机构:",
							width : 80

						});
				var manageField = this.createDicField({
							'id' : 'chis.@manageUnit',
							'showWholeText' : 'true',
							'includeParentMinLen' : '6',
							'render' : 'Tree',
							defaultValue : {
								"key" : this.mainApp.deptId,
								"text" : this.mainApp.dept
							},
							'parentKey' : this.mainApp.deptId,
							rootVisible : true,
							width : 120
						});
				manageField.on("blur", this.manageBlur, this);
				this.manageField = manageField;
				var dateLabel1 = new Ext.form.Label({
							html : "&nbsp;登记日期:",
							width : 80
						});
				var curDate = Date.parseDate(this.mainApp.serverDate, 'Y-m-d');
				var startDateValue = this.getStartDate(this.businessType);
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "登记开始日期",
							value : startDateValue,
							name : "registerDate1"
						});
				this.dateField1 = dateField1;
				var dateLabel2 = new Ext.form.Label({
							html : "&nbsp;->&nbsp;",
							width : 30
						});
				var dateField2 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "登记结束日期",
							value : curDate,
							name : "registerDate2"
						});
				this.dateField2 = dateField2;
				this.dateField1.on("select", this.selectDateField1, this);
				this.dateField2.on("select", this.selectDateField2, this);
				var cnd = this.getOwnerCnd([]);
				if (this.requestData.cnd) {
					cnd = ['and', this.requestData.cnd, cnd]
				}
				this.requestData.cnd = cnd;
				return [manageLabel, manageField, dateLabel1, dateField1,
						dateLabel2, dateField2]
			},
			getOwnerCnd : function(cnd) {
				if (this.manageField.getValue() != null
						&& this.manageField.getValue() != "") {
					var manageUnit = this.manageField.getValue();
					if (typeof manageUnit != "string") {
						manageUnit = manageUnit.key;
					}
					var cnd1 = ['like', ['$', 'c.manaUnitId'],
							['s', manageUnit + "%"]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else {
						cnd = ['and', cnd1, cnd];
					}
				}
				if (this.dateField1.getValue() != null
						&& this.dateField2.getValue() != null
						&& this.dateField1.getValue() != ""
						&& this.dateField2.getValue() != "") {
					var date1 = this.dateField1.getValue();
					var date2 = this.dateField2.getValue();
					var cnd2 = [
							'and',
							[
									'ge',
									['$', 'a.registerDate'],
									[
											'todate',
											[
													's',
													date1.format("Y-m-d")
															+ " 00:00:00"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]],
							[
									'le',
									['$', 'a.registerDate'],
									[
											'todate',
											[
													's',
													date2.format("Y-m-d")
															+ " 23:59:59"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]]];
					if (cnd.length == 0) {
						cnd = cnd2;
					} else {
						cnd = ['and', cnd2, cnd];
					}
				} else if ((this.dateField1.getValue() == null || this.dateField1
						.getValue() == "")
						&& (this.dateField2.getValue() == null || this.dateField2
								.getValue() == "")) {

				} else if (this.dateField1.getValue() == null
						|| this.dateField1.getValue() == "") {
					MyMessageTip.msg("提示", "请选择登记开始日期！", true);
					return;
				} else if (this.dateField2.getValue() == null
						|| this.dateField2.getValue() == "") {
					MyMessageTip.msg("提示", "请选择登记结束日期！", true);
					return;
				}
				this.queryCnd = cnd;
				return cnd;
			},
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				this.statusCmb = comb;
				comb.on("select", this.radioChanged, this);
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				var combox = util.dictionary.SimpleDicFactory.createDic({
							id : 'chis.dictionary.diabetesRiskConfirmResult',
							forceSelection : true,
							defaultValue : {
								key : "1",
								text : "高危确诊"
							}
						});
				this.statusCaseComb = combox;
				combox.on("select", this.radioChanged2, this);
				combox.on("blur", this.statusCaseCombBlue, this);
				combox.setWidth(100);
				this.statusCaseCnd = ["eq", ["$", "a.statusCase"], ["s", "1"]];
				this.statusCnd = ["eq", ["$", "a.status"], ["s", "0"]];
				var lab2 = new Ext.form.Label({
							html : "&nbsp;&nbsp;核实结果:"
						});
				cfg.items = [lab, comb, lab2, combox];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			radioChanged : function() {
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var status = this.statusCmb.getValue();
				var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
				this.statusCnd = statusCnd;
				if (this.statusCaseCnd && this.statusCaseCnd.length > 0) {
					statusCnd = ["and", statusCnd, this.statusCaseCnd]
				}
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd && navCnd.length > 0) {
						cnd.push(navCnd);
					}
					if (queryCnd && queryCnd.length > 0) {
						cnd.push(queryCnd);
					}
				}
				if (this.grid) {
					var bar = this.grid.getTopToolbar();
					var btn = bar.find("cmd", "writeOff")[0];
					if (btn) {
						if (status != "0") {
							btn.notReadOnly = false;
							btn.disable();
						} else {
							btn.notReadOnly = null;
							btn.enable();
						}
					}
					this.requestData.cnd = cnd;
					this.requestData.pageNo = 1;
					this.refresh();
				}
			},
			statusCaseCombBlue : function() {
				if (this.statusCaseComb
						&& (this.statusCaseComb.getRawValue() == null || this.statusCaseComb
								.getRawValue() == "")) {
					this.statusCaseCnd = null;
					this.statusCaseComb.setValue();
					this.initCnd = this.statusCnd;
					var cnd = this.statusCnd;
					var navCnd = this.navCnd;
					var queryCnd = this.queryCnd;
					if (navCnd || queryCnd) {
						cnd = ['and', cnd];
						if (navCnd && navCnd.length > 0) {
							cnd.push(navCnd);
						}
						if (queryCnd && queryCnd.length > 0) {
							cnd.push(queryCnd);
						}
					}
					this.requestData.cnd = cnd;
					this.requestData.pageNo = 1;
					this.refresh();
				}
			},
			radioChanged2 : function() {
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCase = this.statusCaseComb.getValue();
				var statusCaseCnd = ["eq", ["$", "a.statusCase"],
						["s", statusCase]];
				this.statusCaseCnd = statusCaseCnd;
				if (this.statusCnd && this.statusCnd.length > 0) {
					statusCaseCnd = ["and", statusCaseCnd, this.statusCnd]
				}
				this.initCnd = statusCaseCnd;
				var cnd = statusCaseCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd && navCnd.length > 0) {
						cnd.push(navCnd);
					}
					if (queryCnd && queryCnd.length > 0) {
						cnd.push(queryCnd);
					}
				}
				if (this.grid) {
					this.requestData.cnd = cnd;
					this.requestData.pageNo = 1;
					this.refresh();
				}
			},
			doWriteOff : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				var writeOff = this.midiModules["MDC.DocWritOff"];
				if (!writeOff) {
					writeOff = new chis.application.hy.script.risk.HypertensionRiskWriteOffView(
							{
								record : record,
								mainApp : this.mainApp
							});
					writeOff.on("writeOff", this.onWriteOff, this);
					this.midiModules["MDC.DocWritOff"] = writeOff;
				} else {
					writeOff.record = record;
				}
				writeOff.getWin().show();

			},
			onWriteOff : function() {
				this.refresh();
			},
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["EMPI.ExpertQuery"];
				if (!advancedSearchView) {
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								modal : true,
								mainApp : this.mainApp
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["EMPI.ExpertQuery"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(empi) {
				this.empiId = empi["empiId"];
				this.recordStatus = 0;
				this.activeTab = 0;
				this.showEhrViewWin();
			},
			onDblClick : function() {
				this.doModify();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				this.activeTab = 0;
				this.showEhrViewWin();
			},

			showEhrViewWin : function() {
				var cfg = {};
				cfg.closeNav = true;
				var visitModule = ['C_06', 'B_02', 'B_03', 'C_07'];
				cfg.initModules = visitModule;
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				cfg.activeTab = this.activeTab;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["HypertensionRiskListView_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["HypertensionRiskListView_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.exContext["needB03"] = true;
					module.refresh();
				}
				module.getWin().show();
			},
			doConfirm : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var module = this.createSimpleModule(
						"HypertensionRiskConfirmForm",
						"chis.application.hy.HY/HY/C17-1");
				module.on("confirmSave", this.onConfirmSave, this)
				this.refreshExContextData(module, this.exContext);
				Ext.apply(module.exContext.args, r.data)
				module.initDataId = r.data.riskId
				module.getWin().show();
			},
			onConfirmSave : function(entryName, args) {
				this.onSave();
				if (args && args.result == "1" && args.createFlag == "0") {
					Ext.Msg.show({
								title : '高危档案',
								msg : '是否建立高危人群档案?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										this.empiId = args.empiId;
										this.recordStatus = args.status;
										this.activeTab = 0;
										this.showEhrViewWin();
									}
								},
								scope : this
							});
				}
			},
			doEstimate : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				if (r.get("hyCreate") != "y") {
					MyMessageTip.msg("提示", "该病人尚未建立高血压档案", true, 5);
					return;
				}
				var empiId = r.get("empiId")
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['C_01']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["HypertensionRListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.ids.riskId = r.get("riskId")
					module.on("save", this.onSave, this)
					this.midiModules["HypertensionRListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()

			},
			doVisit : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var empiId = r.get("empiId")
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['C_07']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				var module = this.midiModules["HypertensionRiskVisitListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["HypertensionRiskVisitListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()
			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.hy.script.risk.HypertensionRiskListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				var bar = this.grid.getTopToolbar();
				var btn1 = bar.find("cmd", "confirm")[0];
				if (btn1) {
					if (r.get("statusCase") != "0") {
						btn1.disable();
					} else {
						btn1.enable();
					}
				}
			}
		});