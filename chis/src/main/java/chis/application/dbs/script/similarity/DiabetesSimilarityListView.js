$package("chis.application.dbs.script.similarity")

$import("chis.script.BizSimpleListView")

chis.application.dbs.script.similarity.DiabetesSimilarityListView = function(
		cfg) {
	// cfg.initCnd = ['eq',['$','a.diagnosisType'],['s','0']]
	this.initCnd = cfg.cnds || ['eq', ['$', 'a.diagnosisType'], ['s', '2']]
	this.needOwnerBar = true;
	chis.application.dbs.script.similarity.DiabetesSimilarityListView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.dbs.script.similarity.DiabetesSimilarityListView,
		chis.script.BizSimpleListView, {
			onRowClick : function() {
//				var r = this.getSelectedRecord();
//				var bts = this.grid.getTopToolbar();
//				var checkBtn = bts.find("cmd", "check");
//				if (checkBtn && checkBtn[0]) {
//					if (r) {
//						var diagnosisType = r.get("diagnosisType");
//						if (diagnosisType == 2) {
//							checkBtn[0].enable();
//							return;
//						}
//					}
//					checkBtn[0].disable();
//				}
			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.dbs.script.similarity.DiabetesSimilarityListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},
			createOwnerBar : function() {
				var manageLabel = new Ext.form.Label({
							html : "登记单位：",
							width : 80

						});
				var manageField = this.createDicField({
							'id' : 'chis.@manageUnit',
							'showWholeText' : 'true',
							'includeParentMinLen' : '6',
							'render' : 'Tree',
							defaultValue : {
								"key" : this.mainApp.centerUnit,
								"text" : this.mainApp.centerUnitName
							},
							'parentKey' : this.mainApp.centerUnit,
							rootVisible : true,
							width : 120
						});
				this.manageField = manageField;
				manageField.on("blur", this.manageBlur, this);
				var dateLabel1 = new Ext.form.Label({
							html : "&nbsp;登记日期：",
							width : 80
						});
				var startValue = new Date().getFullYear() + "-01-01";
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							emptyText : "登记开始日期",
							value : Date.parseDate(startValue, "Y-m-d"),
							name : "registerDate1"
						});
				this.dateField1 = dateField1;
				var dateLabel2 = new Ext.form.Label({
							html : "&nbsp;->&nbsp;",
							width : 30
						});
				var dateField2 = new Ext.form.DateField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							emptyText : "登记结束日期",
							value : new Date(),
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
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}

				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.diagnosisType",
							forceSelection : true,
							defaultValue : {
								key : "2",
								text : "疑似"
							}
						})
				comb.on("select", this.radioChanged, this)
				comb.setValue("2")
				comb.setWidth(100)
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}
				cfg.items = [lab, comb]
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},
			getOwnerCnd : function(cnd) {
				if (this.manageField.getValue() != null
						&& this.manageField.getValue() != "") {
					var manageUnit = this.manageField.getValue();
					if (typeof manageUnit == "object") {
						manageUnit = manageUnit.key;
					}
					var cnd2 = ['like', ['$', 'a.registerUnit'],
							['s', manageUnit + "%"]];
					if (cnd.length == 0) {
						cnd = cnd2;
					} else if (cnd[0] != 'and') {
						cnd = ['and', cnd2, cnd];
					} else {
						cnd.push(cnd2);
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
					} else if (cnd[0] != 'and') {
						cnd = ['and', cnd2, cnd];
					} else {
						cnd.push(cnd2);
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
				return cnd;
			},
			onCheckAllBox : function(box, flag) {
				this.doCndQuery();
			},
			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.diagnosisType'], ['s', status]]
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["DiabetesSimilarityListView_EMPIInfoModule"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this)
					this.midiModules["DiabetesSimilarityListView_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				var empiId = data.empiId;
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.diabetesRecordService",
							serviceAction : "getDiabetesRecord",
							method : "execute",
							empiId : empiId
						});
				if (result.json.count == 0 || result.json.count == "0") {
					var module = this.createSimpleModule(
							"DiabetesSimilarityCheck",
							"chis.application.dbs.DBS/DBS/D20-1");
					this.refreshExContextData(module, this.exContext);
					Ext.apply(module.exContext.args, data)
					module.initDataId = null
					module.on("save", this.onSave, this)
					module.getWin().show();
				} else {
					MyMessageTip.msg("提示", "该个人已存在糖尿病档案，无法新建疑似记录！", true);
				}
			},
			doCheck : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var module = this.createSimpleModule("DiabetesSimilarity",
						"chis.application.dbs.DBS/DBS/D20-2");
				this.refreshExContextData(module, this.exContext);
				Ext.apply(module.exContext.args, r.data)
				module.initDataId = r.data.similarityId
				module.on("save", this.onCheck, this)
				module.getWin().show();
			},
			onDblClick : function(grid, index, e) {
				this.doCheck()
			},
			onSave : function() {
				this.refresh()
			},
			onCheck : function(entryName, op, json, data) {
				this.refresh()
			},
			createDiabetesRecord : function(empiId) {
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['D_01', 'D_02', 'D_03', 'D_05', 'D_04']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				cfg.activeTab = 0
				var module = this.midiModules["DiabetesRecordListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["DiabetesRecordListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()
			}
		});