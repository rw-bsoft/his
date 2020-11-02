$package("chis.application.dbs.script.ogtt");

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule",
		"chis.script.util.widgets.MyMessageTip");

chis.application.dbs.script.ogtt.DiabetesOGTTRecordListView = function(cfg) {
	cfg.listServiceId = "chis.diabetesOGTTService";
	cfg.listAction = "listDiabetesOGTTRecord";
	chis.application.dbs.script.ogtt.DiabetesOGTTRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.businessType="17";
}

Ext.extend(chis.application.dbs.script.ogtt.DiabetesOGTTRecordListView,
		chis.script.BizSimpleListView, {
			fixRequestData : function(requestData) {
				requestData.serviceAction = "listDiabetesOGTTRecord";
			},
			getCndBar : function(items) {
				var manageLabel = new Ext.form.Label({
							html : "管辖机构：",
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
				this.manageField = manageField;
				var dateLabel1 = new Ext.form.Label({
							html : "登记日期：",
							width : 80
						});
				var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
				var startDateValue = this.getStartDate(this.businessType);
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							value : startDateValue,
							emptyText : "登记开始日期",
							name : "registerDate1"
						});
				this.dateField1 = dateField1;
				var dateLabel2 = new Ext.form.Label({
							html : " -> ",
							width : 30
						});
				var dateField2 = new Ext.form.DateField({
							width : this.cndFieldWidth || 100,
							enableKeyEvents : true,
							emptyText : "登记结束日期",
							value : curDate,
							name : "registerDate2"
						});
				this.dateField2 = dateField2;
				var queryBtn = new Ext.Toolbar.SplitButton({
							iconCls : "query",
							menu : new Ext.menu.Menu({
										items : {
											text : "高级查询",
											iconCls : "common_query",
											handler : this.doAdvancedQuery,
											scope : this
										}
									})
						})
				this.queryBtn = queryBtn;
				this.manageField.on("blur", this.clearValue, this);
				queryBtn.on("click", this.onCheckAllBox, this);
				return [manageLabel, manageField, '-', dateLabel1, dateField1,
						dateLabel2, dateField2, '-', queryBtn]
			},
			clearValue : function(f) {
				if (f.getRawValue() == "" || f.getRawValue() == null) {
					f.setValue();
				}
			},
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : this.pageSize || 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}

				this.checkBoxZC = new Ext.form.Checkbox({
							id : "ZC",
							boxLabel : "正常",
							style : 'margin-left:14px;'
						});
				this.checkBoxZC.on("check", this.onCheckAllBox, this);
				this.checkBoxIFG = new Ext.form.Checkbox({
							id : "IFG",
							boxLabel : "IFG",
							style : 'margin-left:14px;'
						});
				this.checkBoxIFG.on("check", this.onCheckAllBox, this);
				this.checkBoxIGT = new Ext.form.Checkbox({
							id : "IGT",
							boxLabel : "IGT",
							style : 'margin-left:14px;'
						});
				this.checkBoxIGT.on("check", this.onCheckAllBox, this);
				this.checkBoxYS = new Ext.form.Checkbox({
							id : "YSTNB",
							boxLabel : "疑是糖尿病",
							style : 'margin-left:14px;'
						});
				this.checkBoxYS.on("check", this.onCheckAllBox, this);
				this.checkBoxTNB = new Ext.form.Checkbox({
							id : "TNB",
							boxLabel : "糖尿病",
							style : 'margin-left:14px;'
						});
				this.checkBoxTNB.on("check", this.onCheckAllBox, this);
				this.checkBoxWZ = new Ext.form.Checkbox({
							id : "WZOGTT",
							boxLabel : "未做OGTT",
							style : 'margin-left:14px;'
						});
				this.checkBoxWZ.on("check", this.onCheckAllBox, this);
				var items = [this.checkBoxZC, this.checkBoxIFG,
						this.checkBoxIGT, this.checkBoxYS, this.checkBoxTNB,
						this.checkBoxWZ];
				cfg.items = items;
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar
			},
			onCheckAllBox : function(box, flag) {
				var cnd = [];
				if (this.checkBoxZC.getValue() == true) {
					var cnd1 = [
							'and',
							[
									'eq',
									['$',
											'coalesce(a.result3,a.result2,a.result1)'],
									['s', '1']],
							['notNull', ['$', 'a.result1']]];

					if (cnd.length == 0) {
						cnd = cnd1;
					} else if (cnd[0] == 'and') {
						cnd = ['or', cnd1, cnd];
					} else {
						cnd.push(cnd1);
					}
				}
				if (this.checkBoxIFG.getValue() == true) {
					var cnd1 = [
							'and',
							[
									'eq',
									['$',
											'coalesce(a.result3,a.result2,a.result1)'],
									['s', '2']],
							['notNull', ['$', 'a.result1']]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else if (cnd[0] == 'and') {
						cnd = ['or', cnd1, cnd];
					} else {
						cnd.push(cnd1);
					}
				}
				if (this.checkBoxIGT.getValue() == true) {
					var cnd1 = [
							'and',
							[
									'eq',
									['$',
											'coalesce(a.result3,a.result2,a.result1)'],
									['s', '3']],
							['notNull', ['$', 'a.result1']]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else if (cnd[0] == 'and') {
						cnd = ['or', cnd1, cnd];
					} else {
						cnd.push(cnd1);
					}
				}
				if (this.checkBoxYS.getValue() == true) {
					var cnd1 = [
							'and',
							[
									'eq',
									['$',
											'coalesce(a.result3,a.result2,a.result1)'],
									['s', '4']],
							['notNull', ['$', 'a.result1']]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else if (cnd[0] == 'and') {
						cnd = ['or', cnd1, cnd];
					} else {
						cnd.push(cnd1);
					}
				}
				if (this.checkBoxTNB.getValue() == true) {
					var cnd1 = [
							'and',
							[
									'eq',
									['$',
											'coalesce(a.result3,a.result2,a.result1)'],
									['s', '5']],
							['notNull', ['$', 'a.result1']]];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else if (cnd[0] == 'and') {
						cnd = ['or', cnd1, cnd];
					} else {
						cnd.push(cnd1);
					}
				}
				if (this.checkBoxWZ.getValue() == true) {
					var cnd1 = ['isNull', ['$', 'a.result1']];
					if (cnd.length == 0) {
						cnd = cnd1;
					} else if (cnd[0] == 'and') {
						cnd = ['or', cnd1, cnd];
					} else {
						cnd.push(cnd1);
					}
				}
				if (this.manageField.getValue() != null
						&& this.manageField.getValue() != "") {
					var manageUnit = this.manageField.getValue();
					var cnd2 = ['like', ['$', 'c.manaUnitId'],
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
				this.requestData.cnd = cnd;
				this.refresh();
			},
			onDblClick : function() {
				this.doConfirm();
			},		
			doConfirm : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				this.empiId = r.get("empiId");
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['D_0101']
				cfg.mainApp = this.mainApp;
				cfg.activeTab = 0;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["DiabetesOGTTModule_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["DiabetesOGTTModule_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.refresh();
				}
				module.exContext.args["OGTTID"] = r.get("OGTTID");
				module.getWin().show();
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				this.activeTab = 0;
				this.showEhrViewWin();
			},
			showEhrViewWin : function() {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['D_01', 'D_02', 'D_03', 'D_05', 'D_04']
				cfg.mainApp = this.mainApp;
				cfg.activeTab = this.activeTab;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["DiabetesRecordListView_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["DiabetesRecordListView_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.refresh();
				}
				module.getWin().show();
			}
		});