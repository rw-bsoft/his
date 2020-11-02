$package("chis.application.ohr.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.ohr.script.OldPeopleRecordForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.fldDefaultWidth = 180;
	cfg.autoFieldWidth = false;
	chis.application.ohr.script.OldPeopleRecordForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
	this.on("loadNoData", this.onLoadNoData, this);
	this.on("beforeCreate", this.onBeforeCreate, this)
}

Ext.extend(chis.application.ohr.script.OldPeopleRecordForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				var empiData=this.exContext.empiData;
				var birthDay = empiData.birthday;
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth;
				if ((typeof birthDay == 'object')
						&& birthDay.constructor == Date) {
					birth = birthDay;
				} else {
					birth = Date.parseDate(birthDay, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear() - 60);
				if (birth.getFullYear() > currDate.getFullYear()) {
					Ext.Msg.show({
								title : '温馨提示',
								msg : '年龄小于'
										+ '60'
										+ '岁不允许建立老年人档案',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;	
				}else{
				this.initDataId = this.exContext.ids["MDC_OldPeopleRecord.phrId"];
				
					this.form.getForm().findField("manaDoctorId").disable();
					this.form.getForm().findField("createUser").enable();
					this.form.getForm().findField("createDate").enable();
			
				chis.application.ohr.script.OldPeopleRecordForm.superclass.doNew
						.call(this);
				if(this.form && this.form.getTopToolbar() && this.form.getTopToolbar().find("cmd", "save") )
				this.form.getTopToolbar().find("cmd", "save")[0].enable();
				}
			},

			doCheck : function() {
				var module = "B_10";
				if (this.mainApp.exContext.healthCheckType == 'paper') {
					module = "B_10_HTML";
				}
				this.fireEvent("addModule", module,true);
				this.fireEvent("activeModule", module, {
							dataSources : "2"
						});
			},

			onBeforeCreate : function() {
				var form = this.form.getForm();
				var phrId = form.findField("phrId");
				phrId.setValue(this.exContext.ids.phrId);
				phrId.disable();
				this.data.empiId = this.exContext.ids.empiId
				this.form.el.mask("正在初始化数据，请稍后...")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "initializeRecord",
							method : "execute",
							body : {
								empiId : this.exContext.ids.empiId
							}
						})
				this.form.el.unmask()
				var body = result.json.body;
				this.initFormData(body);
			},

			onReady : function() {
				var form = this.form.getForm();
				form.findField("symptomsCode").on("select",
						this.onSymptomsCodeSelect, this);
				form.findField("currentProblem").on("select",
						this.onCurrentProblemSelect, this);
				form.findField("manaDoctorId").on("select",
						this.onManaDoctorIdSelect, this);
				
				form.findField("createUser").on("select",
						this.onCreateDoctorIdSelect, this);
				
				form.findField("workExposeFlag").on("select",
						this.workExposeFlagSelect, this);
				form.findField("workExposeFlag").on("change",
						this.workExposeFlagSelect, this);
				form.findField("familyColeWarmFlag").on("select",
						this.familyColeWarmFlagSelect, this);
				form.findField("familyColeWarmFlag").on("change",
						this.familyColeWarmFlagSelect, this);
				chis.application.ohr.script.OldPeopleRecordForm.superclass.onReady
						.call(this);
			},
			
			onLoadNoData : function(){
//				this.setButton(["check"], false);
			},
			onLoadData : function(entryName, body) {
//				this.setButton(["check"], true);
				var form = this.form.getForm();
				var workExposeFlag = form.findField("workExposeFlag");
				if (workExposeFlag) {
					this.workExposeFlagSelect(workExposeFlag);
				}
				var familyColeWarmFlag = form.findField("familyColeWarmFlag");
				if (familyColeWarmFlag) {
					this.familyColeWarmFlagSelect(familyColeWarmFlag);
				}
				//他妈比的不知在哪控制确定按钮不可用只能在加载完数据后重置成可用
				this.form.getTopToolbar().find("cmd", "save")[0].enable();
			},

			onSymptomsCodeSelect : function(combo, record, index) {
				var value = combo.getValue();
				if (value.indexOf("01") != -1) {
					combo.clearValue();
					combo.setValue(record.data.key);
				}
		},

			onCurrentProblemSelect : function(combo, record, index) {
				var value = combo.getValue();
				if (value.indexOf("98") != -1) {
					combo.clearValue();
					combo.setValue(record.data.key);
				}
			},

			familyColeWarmFlagSelect : function(combo) {
				var form = this.form.getForm();
				var value = combo.getValue();
				var familyColeWarmYear = form.findField("familyColeWarmYear");
				if (value == 'y') {
					familyColeWarmYear.enable();
				} else if (value == 'n') {
					familyColeWarmYear.disable();
					familyColeWarmYear.setValue();
				}
			},

			workExposeFlagSelect : function(combo) {
				var value = combo.getValue();
				var form = this.form.getForm();

				var workExposeRiskType = form.findField("workExposeRiskType");
				var workExposeRisk = form.findField("workExposeRisk");
				var protectFlag = form.findField("protectFlag");
				var workName = form.findField("workName");
				var workYear = form.findField("workYear");

				if (value == 'y') {
					workExposeRiskType.enable();
					workExposeRisk.enable();
					protectFlag.enable();
					workName.enable();
					workYear.enable();
				} else if (value == 'n') {
					workExposeRiskType.disable();
					workExposeRisk.disable();
 				    protectFlag.disable();
					workName.disable();
					workYear.disable();
					workExposeRiskType.setValue();
					workExposeRisk.setValue();
					protectFlag.setValue();
					workName.setValue();
					workYear.setValue();
				}
			this.validate();
		},

			onManaDoctorIdSelect : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},

			setManaUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("manaUnitId");
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.setValue({
								key : "",
								text : ""
							});
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit)
					combox.disable();
				}
			},
			
			onCreateDoctorIdSelect : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method : "execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setCreateUnit(result.json.manageUnit)
			},

			setCreateUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("createUnit");
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.setValue({
								key : "",
								text : ""
							});
					return;
				}

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit)
					combox.disable();
				}
			},

//			setButton : function(m, flag) {
//				if (this.empiId && this.phrId
//						&& this.manaDoctorId != this.mainApp.uid
//						&& this.mainApp.jobId != "system") {
//					if (this.phrId) {
//						Ext.Msg.alert("提示", "该病人责任医生非本人，不能新增接诊记录");
//					}
//					flag = false;
//				}
//				var btns;
//				var btn;
//				if (this.showButtonOnTop && this.form.getTopToolbar()) {
//					btns = this.form.getTopToolbar();
//				} else {
//					btns = this.form.buttons;
//				}
//				if (!btns) {
//					return;
//				}
//				if (this.showButtonOnTop) {
//					for (var j = 0; j < m.length; j++) {
//						if (!isNaN(m[j])) {
//							btn = btns.items.item(m[j]);
//						} else {
//							btn = btns.find("cmd", m[j]);
//							btn = btn[0];
//						}
//						if (btn) {
//							
//							(flag) ? btn.enable() : btn.disable();
//							true ? btn.enable() : btn.disable();
//						}
//					}
//				} else {
//					for (var j = 0; j < m.length; j++) {
//						if (!isNaN(m[j])) {
//							btn = btns[m[j]];
//						} else {
//							for (var i = 0; i < this.actions.length; i++) {
//								if (this.actions[i].id == m[j]) {
//									btn = btns[i];
//								}
//							}
//						}
//						if (btn) {
//							(flag) ? btn.enable() : btn.disable();
//						}
//					}
//				}
//
//			},

			getLoadRequest : function() {
				if (!this.exContext.ids["MDC_OldPeopleRecord.phrId"]) {
					this.initDataId = null;
					return {
						"fieldName" : "phrId",
						"fieldValue" : this.exContext.ids.phrId
					};
				} else {
					return null;
				}
			},

			getSaveRequest : function(saveData) {
				if (!this.exContext.ids["MDC_OldPeopleRecord.phrId"]) {
					this.initDataId = null;
				}
				saveData["empiId"] = this.exContext.ids.empiId;
				saveData["lastModifyUser"] = this.mainApp.uid
				saveData["lastModifyDate"] = this.mainApp.serverDate
				return saveData;
			},
			
			afterSaveData : function(entryName, op, json, data) {
				this.exContext.ids["MDC_OldPeopleRecord.phrId"] = json.body["phrId"];
				if (op == "create") {
					// ** 重新刷新非重点人群随访叶签
					this.fireEvent("refreshData", 'B_09');
				}
				this.fireEvent("save", entryName, op, json, data);
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
				this.refreshEhrTopIcon();
				//他妈比的不知在哪控制确定按钮不可用只能在保存完数据后重置成可用
				this.form.getTopToolbar().find("cmd", "save")[0].enable();
			}
		});