$package("chis.application.hq.script")

$import("chis.script.BizSimpleListView","chis.script.EHRView","chis.application.mpi.script.EMPIInfoModule")

chis.application.hq.script.HighRiskVisitList= function(cfg) {
	cfg.initCnd = cfg.cnds || ['and',['eq', ['$', 'a.businessType'], ['s', '18']]];
	this.needOwnerBar = true;
	chis.application.hq.script.HighRiskVisitList.superclass.constructor
			.apply(this, [cfg]);
	this.businessType = "1";

}

Ext.extend(chis.application.hq.script.HighRiskVisitList, chis.script.BizSimpleListView, {
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
							html : "&nbsp;计划随访日期:",
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
									['$', 'a.planDate'],
									[
											'todate',
											[
													's',
													date1.format("Y-m-d")
															+ " 00:00:00"],
											['s', 'yyyy-mm-dd HH24:mi:ss']]],
							[
									'le',
									['$', 'a.planDate'],
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
	refresh : function() {
		if (this.store) {
			this.store.load();
		}
	},	
	doVisit : function(item, e) {
		debugger;
		var r = this.getSelectedRecord();
		this.empiId = r.data["empiId"];
		if(this.empiId)
		this.showEhrViewWin();
	}
	,showEhrViewWin : function() {
		debugger;
		var cfg = {};
		cfg.closeNav = true;
		var visitModule = ['HQ_01'];
		cfg.initModules = visitModule;
		cfg.mainApp = this.mainApp;
		cfg.activeTab =0;
		cfg.needInitFirstPanel = true
		var module= new chis.script.EHRView(cfg);
		module.exContext.ids["empiId"] = this.empiId;
		module.on("save", this.refresh, this);
		module.exContext.ids.recordStatus = this.recordStatus;
		module.getWin().show();
	},
	onDblClick : function() {
		this.doVisit();
	}
});