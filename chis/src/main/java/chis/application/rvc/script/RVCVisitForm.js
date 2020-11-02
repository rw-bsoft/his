$package("chis.application.rvc.script")

$import("chis.script.BizTableFormView");

chis.application.rvc.script.RVCVisitForm = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 180;
	cfg.autoFieldWidth = false;
	cfg.labelWidth = 100;
	chis.application.rvc.script.RVCVisitForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeCreate", this.onNewForm, this);
	this.on("beforeUpdate", this.onNewForm, this);
	this.on("loadData", this.onLoadData, this);
	// this.on("loadNoData", this.onLoadNoData, this);
}

Ext.extend(chis.application.rvc.script.RVCVisitForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				this.initDataId = this.exContext.args.initDataId;
				chis.application.rvc.script.RVCVisitForm.superclass.doNew
						.call(this);
			},

			onNewForm : function() {
				var form = this.form.getForm();
				var nextDate = form.findField("nextDate");
				this.resetVisitDate(form); // 控制随访日期最大值或最小值
				if (this.mainApp.exContext.rvcMode == 2
						&& this.exContext.args.planDate && nextDate) {
					var serverDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					var currDate = Date.parseDate(this.exContext.args.endDate,
							"Y-m-d");
					if (serverDate > currDate) {
						nextDate.setMinValue(serverDate);
					} else {
						var e = new Date(this.exContext.args.endDate);
						var nextMinDate = new Date(e.getFullYear(), e
										.getMonth(), e.getDate() + 1);
						nextDate.setMinValue(nextMinDate);
					}
					nextDate.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>下次预约时间:</span>";
					var items = this.schema.items;
					nextDate.allowBlank = false;
					for (var i = 0; i < items.length; i++) {
						if (items[i].id == "nextDate")
							items[i]["not-null"] = true;
					}
//				} else {
//					if (this.exContext.args.nextDateDisable) {
//						nextDate.disable();
//					} else {
//						nextDate.enable();
//					}
				}
			},

			resetVisitDate : function(form) {
				var visitDate = form.findField("visitDate");
				if (visitDate && this.exContext.args.endDate) {
					var serverDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					var currDate = Date.parseDate(this.exContext.args.endDate,
							"Y-m-d");
					visitDate.setMinValue(this.exContext.args.beginDate)
					if (currDate > serverDate) {
						visitDate.setMaxValue(serverDate);
					} else {
						visitDate.setMaxValue(currDate);
					}
				}
//				visitDate.on("blur", this.setNextDateMinValue, this);
//				visitDate.on("select", this.setNextDateMinValue, this);
			},

			setNextDateMinValue : function() {
				var form = this.form.getForm();
				var visitDate = form.findField("visitDate");
				var value = visitDate.getValue()
				var nextDate = form.findField("nextDate");
				nextDate.setMinValue(value);
			},

			onReady : function() {
				var form = this.form.getForm();

				var sbp = form.findField("sbp");
				if (sbp) {
					sbp.on("blur", this.onSbpChange, this);
					sbp.on("keyup", this.onSbpChange, this);
				}
				var dbp = form.findField("dbp");
				if (dbp) {
					dbp.on("blur", this.onDbpChange, this);
					dbp.on("keyup", this.onDbpChange, this);
				}
				var visitUser = form.findField("visitUser");
				if (visitUser) {
					visitUser.on("select", this.onVisitUserSelect, this);
				}
				var currentStatus = form.findField("currentStatus");
				currentStatus.on("select", this.onCurrentStatusSelect, this);
				var excrement = form.findField("excrement");
				excrement.on("select", this.onExcrementSelect, this);
				var emotion = form.findField("emotion");
				emotion.on("select", this.onEmotionSelect, this);

				this.resetVisitDate(form);
				chis.application.rvc.script.RVCVisitForm.superclass.onReady
						.call(this)
			},

			onLoadData : function(entryName, data) {
				this.onEmotionSelect();
				this.onExcrementSelect();
				var form = this.form.getForm();
				var currentStatus = form.findField("currentStatus");
				var otherStatus = form.findField("otherStatus");
				var v = currentStatus.getValue();
				if (v.indexOf("99") != -1) {
					otherStatus.enable();
				} else {
					otherStatus.setValue();
					otherStatus.disable();
				}
			},

			// onLoadNoData :function(){
			// var form = this.form.getForm();
			// var other = form.findField("other");
			// other.disable();
			// var excrementOther = form.findField("excrementOther");
			// excrementOther.disable();
			// var otherStatus = this.form.getForm().findField("otherStatus");
			// otherStatus.disable();
			// },

			onEmotionSelect : function(f) {
				var form = this.form.getForm();
				var emotion = form.findField("emotion");
				var other = form.findField("other");
				var v = emotion.getValue();
				if (v == 9) {
					other.enable();
				} else {
					other.setValue();
					other.disable();
				}
			},

			onExcrementSelect : function(f) {
				var form = this.form.getForm();
				var excrement = form.findField("excrement");
				var excrementOther = form.findField("excrementOther");
				var v = excrement.getValue();
				if (v == 9) {
					excrementOther.enable();
				} else {
					excrementOther.setValue();
					excrementOther.disable();
				}
			},

			onCurrentStatusSelect : function(combo, record, index) {
				var value = combo.getValue();
				var valueArray = value.split(",");
				var selValue = record.data.key;
				var otherStatus = this.form.getForm().findField("otherStatus");
				if (valueArray.indexOf("98") != -1) {
					combo.clearValue();
					if (record.data.key == 98) {
						combo.setValue(98);
						otherStatus.setValue();
						otherStatus.disable();
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue(98);
					otherStatus.setValue();
					otherStatus.disable();
				}
				if (record.data.key == 99) {
					if (valueArray.indexOf("99") != -1) {
						otherStatus.enable();
					} else {
						otherStatus.setValue();
						otherStatus.disable();
					}
				}
			},

			onVisitUserSelect : function(combo, node) {
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
				this.setVisitUnit(result.json.manageUnit)
			},

			setVisitUnit : function(manageUnit) {
				var combox = this.form.getForm().findField("visitUnit");
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

			onSbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var constriction = field.getValue();
				if (!constriction) {
					return;
				}
				if (constriction > 500 || constriction < 10) {
					field.markInvalid("收缩压必须在10到500之间！");
					return;
				}
				var diastolicFld = this.form.getForm().findField("dbp");
				var diastolic = diastolicFld.getValue();
				if (constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					diastolicFld.clearInvalid();
				}
			},

			onDbpChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var diastolic = field.getValue();
				if (!diastolic) {
					return;
				}
				if (diastolic > 500 || diastolic < 10) {
					field.markInvalid("舒张压必须在10到500之间！");
					return;
				}
				var constrictionFld = this.form.getForm().findField("sbp");
				var constriction = constrictionFld.getValue();
				if (constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
					return;
				} else {
					constrictionFld.clearInvalid();
				}
			},

			getFormData : function() {
				var values = {};
				values = chis.application.rvc.script.RVCVisitForm.superclass.getFormData
						.call(this);
				values["visitId"] = this.exContext.args.initDataId
				values["planId"] = this.exContext.args.planId;
				values["planDate"] = this.exContext.args.planDate;
				values["empiId"] = this.exContext.args.empiId;
				values["phrId"] = this.exContext.args.phrId;
				if (this.mainApp.exContext.oldPeopleMode == 2) {
					var now = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
					if (!this.exContext.args.nextDateDisable
							&& values["nextDate"] <= now) {
						Ext.MessageBox.alert("提示", "预约日期必须大于当前日期");
						return;
					}
				}
				return values;
			},

			getLoadRequest : function() {
				if (this.exContext.args.initDataId) {
					this.initDataId = null;
					return {
						"fieldName" : "visitId",
						"fieldValue" : this.exContext.args.initDataId
					};
				} else {
					return null;
				}
			}
		})