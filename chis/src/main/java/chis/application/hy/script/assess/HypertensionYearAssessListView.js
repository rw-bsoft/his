// 糖尿病档案管理主页面
$package("chis.application.hy.script.assess");

$import("chis.script.BizSimpleListView",
		"chis.script.util.widgets.MyMessageTip");

chis.application.hy.script.assess.HypertensionYearAssessListView = function(cfg) {
	this.needOwnerBar = true;
	chis.application.hy.script.assess.HypertensionYearAssessListView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.hy.script.assess.HypertensionYearAssessListView,
		chis.script.BizSimpleListView, {
			onReady : function() {
				chis.application.hy.script.assess.HypertensionYearAssessListView.superclass.onReady
						.call(this);
				var info = this.getLastThreeDay();
				if (info) {
					var assessType = info.assessType;
					var bar = this.grid.getTopToolbar();
					var btn = bar.find("cmd", "check")[0];
					if (btn) {
						if (assessType == "1") {
							btn.disable();
						} else {
							btn.enable();
						}
					}

				}
			},
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
				var dateLabel = new Ext.form.Label({
							html : "&nbsp;评估日期:",
							width : 80
						});
				var dateField = new Ext.form.DateField({
							width : this.cndFieldWidth || 120,
							enableKeyEvents : true,
							emptyText : "评估日期",
							value : new Date(),
							maxValue : new Date(),
							name : "inputDate"
						});
				this.dateField = dateField;
				var cnd = this.getOwnerCnd([]);
				if (this.requestData.cnd) {
					cnd = ['and', this.requestData.cnd, cnd]
				}
				this.requestData.cnd = cnd;
				return [manageLabel, manageField, dateLabel, dateField]
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
				if (this.dateField.getValue() != null
						&& this.dateField.getValue() != "") {
					var date = this.dateField.getValue();
					var cnd2 = ['eq',
							['$', "to_char(a.inputDate,'yyyy-mm-dd')"],
							['s', date.format("Y-m-d")]];
					if (cnd.length == 0) {
						cnd = cnd2;
					} else {
						cnd = ['and', cnd2, cnd];
					}
				}
				return cnd;
			},
			doCheck : function() {
				var lastThreeDay = this.getLastThreeDay();
				var days = lastThreeDay.days;
				var endMonth = lastThreeDay.endMonth;
				var assessHour1 = lastThreeDay.assessHour1;
				var assessHour2 = lastThreeDay.assessHour2;
				var planMode = lastThreeDay.planMode;
				var day = new Date().getDate() + "";
				var month = (new Date().getMonth() + 1) + "";
				 if (planMode == "2") {
					MyMessageTip.msg("提示", "生成方式请选择按随访结果生成！", true)
					return
				}
				if (month != endMonth) {
					MyMessageTip.msg("提示", "为提高评估准确度，建议在距年末"+days.length+"天内的"+assessHour1+"时至"+assessHour2+"时进行评估！", true)
					return
				}
				var flag = false;
				for (var i = 0; i < days.length; i++) {
					if (day == days[i]) {
						flag = true;
					}
				}
				if (!flag) {
					MyMessageTip.msg("提示", "为提高评估准确度，建议在距年末"+days.length+"天内的"+assessHour1+"时至"+assessHour2+"时进行评估！", true)
					return
				}
				var hour = new Date().getHours();
				if (hour >= parseInt(assessHour2)
						&& hour < parseInt(assessHour1)) {
					MyMessageTip.msg("提示",
							"为提高评估准确度，建议在距年末"+days.length+"天内的"+assessHour1+"时至"+assessHour2+"时进行评估！", true)
					return
				}
				this.mask("正在进行年度评估...");
				util.rmi.jsonRequest({
							serviceId : "chis.hypertensionVisitService",
							method : "execute",
							serviceAction : "saveHypertensionYearAssess"
						}, function(code, msg, json) {
							this.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, []);
								return
							}
							this.refresh();
						}, this)
			},
			getLastThreeDay : function() {
				if (this.midiInfo) {
					return this.midiInfo;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionVisitService",
							serviceAction : "getLastThreeDay",
							method : "execute"
						});
				this.midiInfo = result.json.body;
				return result.json.body;
			}
		});