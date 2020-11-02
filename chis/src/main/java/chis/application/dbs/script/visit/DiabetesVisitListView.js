﻿$package("chis.application.dbs.script.visit")
$import("util.dictionary.RadioDicFactory", "chis.script.BizSimpleListView")

chis.application.dbs.script.visit.DiabetesVisitListView = function(cfg) {
	this.phrId = {}
	this.empiId = cfg.empiId
	cfg.initCnd = cfg.cnds || ['and',['eq', ['$', 'a.businessType'], ['s', '2']],['like',['$','c.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]];
//	this.initCnd = ['eq', ['$', 'a.businessType'], ['s', '2']];
	this.needOwnerBar = true;
	chis.application.dbs.script.visit.DiabetesVisitListView.superclass.constructor
			.apply(this, [cfg]);
	this.businessType="2";
}

Ext.extend(chis.application.dbs.script.visit.DiabetesVisitListView,
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
							html : "&nbsp;实际随访日期:",
							width : 80
						});
				var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
				var startDateValue = this.getStartDate(this.businessType);
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "开始日期",
							value : startDateValue,
							name : "planDate1"
						});
				this.dateField1 = dateField1;
				var dateLabel2 = new Ext.form.Label({
							html : "&nbsp;->&nbsp;",
							width : 30
						});
				var dateField2 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "结束日期",
							value : curDate,
							name : "planDate2"
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
									['$', 'a.visitDate'],
									[
											'todate',
											[
													's',
													date1.format("Y-m-d")
															+ " 00:00:00"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]],
							[
									'le',
									['$', 'a.visitDate'],
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
					MyMessageTip.msg("提示", "请选择开始日期！", true);
					return;
				} else if (this.dateField2.getValue() == null
						|| this.dateField2.getValue() == "") {
					MyMessageTip.msg("提示", "请选择结束日期！", true);
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

				var checkItems = this.getCheckItems();
				cfg.items = [checkItems];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			getCheckItems : function() {
				this.planStatusHYR = new Ext.form.CheckboxGroup({
							name : 'planStatus',
							disabled : false,
							width : 300,
							style : 'margin-left:14px;',
							items : [{
										boxLabel : "应访",
										inputValue : "0",
										name : "nature"
									}, {
										boxLabel : "已访",
										inputValue : "1",
										name : "nature"
									}, {
										boxLabel : "失访",
										inputValue : "2",
										name : "nature"
									}, {
										boxLabel : "未访",
										inputValue : "3",
										name : "nature"
									}, {
										boxLabel : "过访",
										inputValue : "4",
										name : "nature"
									}, {
										boxLabel : "结案",
										inputValue : "8",
										name : "nature"
									}, {
										boxLabel : "注销",
										inputValue : "9",
										name : "nature"
									}],
							listeners : {
								change : this.onCheckAllBox,
								scope : this
							}
						});
				return [this.planStatusHYR];
			},
			onCheckAllBox : function(box, flag) {
				var cnd = [];
				// 计划状态
				var psArr = this.planStatusHYR.getValue();
				var nv = "";
				for (var ni = 0, nlen = psArr.length; ni < nlen; ni++) {
					nv += psArr[ni].inputValue
					if (ni < nlen - 1) {
						nv += ',';
					}
				}
				if (nv.length == 1) {
					var ncnd = ['eq', ['$', 'a.planStatus'], ['s', nv]]
					if (cnd.length > 0) {
						cnd = ['and', cnd, ncnd];
					} else {
						cnd = ncnd;
					}
				} else if (nv.length > 1) {
					var ncnd = ['in', ['$', 'a.planStatus'], [['s', nv]]]
					if (cnd.length > 0) {
						cnd = ['and', cnd, ncnd];
					} else {
						cnd = ncnd;
					}
				}
				this.navCnd = cnd;
				var cnd1 = this.getOwnerCnd([]);
				if (cnd1 && cnd1.length > 0) {
					if (cnd && cnd.length > 0) {
						cnd = ['and', cnd1, cnd]
					} else {
						cnd = cnd1
					}
				}
				if (this.initCnd && this.initCnd.length > 0) {
					if (cnd && cnd.length > 0) {
						cnd = ['and', this.initCnd, cnd]
					} else {
						cnd = this.initCnd
					}
				}
				this.requestData.cnd = cnd;
				this.refresh();
			},
			doView : function() {
				var r = this.grid.getSelectionModel().getSelected()
				$import("chis.application.dbs.script.visit.DiabetesVisitModule")
				var module = this.midiModules["DiabetesVisitModule_List"]
				var cfg = {}
				cfg.empiId = r.get("empiId")
				if (!module) {
					module = new chis.application.dbs.script.visit.DiabetesVisitModule(cfg)
					module.formActions = []
					this.midiModules["DiabetesVisitModule_List"] = module
				} else {
					Ext.apply(module, cfg)
				}
				module.getWin().show()
			},
			doModify : function() {
				if (this.store.getCount() == 0) {
					return
				}
				var r = this.grid.getSelectionModel().getSelected()
				this.onEmpiSelected(r.data)
			},
			onDblClick : function(grid, index, e) {
				var r = grid.getSelectionModel().getSelected()
				var data = {}
				this.onEmpiSelected(r.data)
			},
			onEmpiSelected : function(data) {
				var empiId = data.empiId
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['D_01', 'D_02', 'D_03', 'D_05', 'D_04']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				cfg.activeTab = 2
				var module = this.midiModules["DiabetesVisitListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.args.visitId = data.visitId;
					module.on("save", this.onSave, this)
					this.midiModules["DiabetesVisitListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.exContext.args.visitId = data.visitId;
					module.refresh()
				}
				module.getWin().show()
			},
			radioOnChanged : function(field, newValue, oldValue) {
				var planStatus = field.getValue()
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.DiabetesQuery",
							schema : "MDC_DiabetesVisit",
							cnd : ["eq", ["$", "planStatus"], ["s", planStatus]]
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.radioOnChanged);
					return
				}
				this.initCnd = ["eq", ["$", "planStatus"], ["s", planStatus]]
			},
			doDelete: function() {
				var r = this.getSelectedRecord();
				if(!r){
					MyMessageTip.msg("提示:","亲，请选择一条记录！");
					return;
				}
				Ext.MessageBox.confirm('提示', '删除将无法恢复，是否删除?',
				function(btn) {
					if (btn == "yes") {
						var planId=r.get("planId");
						var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.diabetesVisitService",
								serviceAction : "deleteDiabetesVistPlanbyplanId",
								method : "execute",
								planId:planId
							});
						if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg,
								this.refresh());
						return;
					}else{
						this.refresh();
					}
				}})
			},
			doQuchong: function() {
				var cnd = this.getOwnerCnd([]);
				this.requestData.cnd = cnd;
				this.listAction = "listDiabetesVistPlanQC";
				this.requestData.serviceAction = this.listAction;
				this.refresh()
			}
		});