// 高血压档案管理主页面
$package("chis.application.hy.script.record");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.record.HypertensionRecordEndListView = function(cfg) {
	this.initCnd = cfg.cnds || ['eq', ['$', 'a.visitEffect'], ['s', '9']];
	this.needOwnerBar = true;
	chis.application.hy.script.record.HypertensionRecordEndListView.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(chis.application.hy.script.record.HypertensionRecordEndListView,
		chis.script.BizSimpleListView, {
			createOwnerBar : function() {
				var dateLabel1 = new Ext.form.Label({
							html : "&nbsp;终止日期:",
							width : 80
						});
				var curDate = Date.parseDate(this.mainApp.serverDate, 'Y-m-d');
				var startDateValue = this.getStartDate(this.businessType);
				var dateField1 = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "开始日期",
							value : startDateValue,
							name : "visitDate1"
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
							name : "visitDate2"
						});
				this.dateField2 = dateField2;
				this.dateField1.on("select", this.selectDateField1, this);
				this.dateField2.on("select", this.selectDateField2, this);
				var cnd = this.getOwnerCnd([]);
				if (this.requestData.cnd) {
					cnd = ['and', this.requestData.cnd, cnd]
				}
				this.requestData.cnd = cnd;
				return [dateLabel1, dateField1, dateLabel2, dateField2]
			},
			getOwnerCnd : function(cnd) {
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
				if (this.navCnd && this.navCnd.length > 0) {
					if (cnd.length == 0) {
						cnd = this.navCnd;
					} else {
						cnd = ['and', this.navCnd, cnd];
					}
				}
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
				this.noVisitReason = new Ext.form.CheckboxGroup({
							name : 'noVisitReason',
							disabled : false,
							width : 200,
							style : 'margin-left:14px;',
							items : [{
										boxLabel : "死亡",
										inputValue : "1",
										name : "reason"
									}, {
										boxLabel : "迁出",
										inputValue : "2",
										name : "reason"
									}, {
										boxLabel : "失访",
										inputValue : "3",
										name : "reason"
									}, {
										boxLabel : "拒绝",
										inputValue : "4",
										name : "reason"
									}],
							listeners : {
								change : this.onCheckAllBox,
								scope : this
							}
						});
				return [this.noVisitReason];
			},
			getNavCnd : function() {
				// 计划状态
				var cnd = [];
				var psArr = this.noVisitReason.getValue();
				var nv = "";
				for (var ni = 0, nlen = psArr.length; ni < nlen; ni++) {
					nv += psArr[ni].inputValue
					if (ni < nlen - 1) {
						nv += ',';
					}
				}
				if (nv.length == 1) {
					var ncnd = ['eq', ['$', 'a.noVisitReason'], ['s', nv]]
					if (cnd.length > 0) {
						cnd = ['and', cnd, ncnd];
					} else {
						cnd = ncnd;
					}
				} else if (nv.length > 1) {
					var ncnd = ['in', ['$', 'a.noVisitReason'], [['s', nv]]]
					if (cnd.length > 0) {
						cnd = ['and', cnd, ncnd];
					} else {
						cnd = ncnd;
					}
				}
				return cnd;
			},
			onCheckAllBox : function(box, flag) {
				var cnd = this.getNavCnd();
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
			doCheck : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var endCheck = r.get("endCheck");
				if (endCheck != "1") {
					return;
				}
				var module = this.createSimpleModule(
						"HypertensionRecordEndForm", this.refEndForm);
				this.refreshExContextData(module, this.exContext);
				module.on("save", this.onFormSave, this);
				var win = module.getWin();
				this.formWin = win;
				var formData = this.castListDataToForm(r.data, this.schema);
				module.initFormData(formData);
				win.show();
			},
			onFormSave : function() {
				if (this.formWin) {
					this.formWin.hide();
				}
				this.refresh()
			},
			onDblClick : function() {
				this.doCheck();
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				var bar = this.grid.getTopToolbar();
				var btn1 = bar.find("cmd", "check")[0];
				if (btn1) {
					if (r.get("endCheck") != "1") {
						btn1.disable();
					} else {
						btn1.enable();
					}
				}
			}
		});
