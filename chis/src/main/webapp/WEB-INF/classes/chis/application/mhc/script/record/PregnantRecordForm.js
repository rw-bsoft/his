/**
 * 孕妇基本信息表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizTableFormView", "util.widgets.LookUpField")
chis.application.mhc.script.record.PregnantRecordForm = function(cfg) {
	cfg.colCount = 5;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 105
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.record.PregnantRecordForm.superclass.constructor
			.apply(this, [cfg])
	this.initAction = "docCreateInitialization"
	this.on("changeDic", this.onChangeDic, this)
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
	this.on("loadNoData", this.onLoadNoData, this);
}
Ext.extend(chis.application.mhc.script.record.PregnantRecordForm,
		chis.script.BizTableFormView, {
			initPanel : function(sc) {

				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}

					return this.form;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : '2'
						}
					},
					items : []
				}

				if (!this.autoFieldWidth) {

					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
							* colCount
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {

						continue;
					}
					if ("pycode" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "homeAddress"||it.id=="restRegionCode") {
							it.otherConfig={
									'not-null':'true',
									colspan:2,
									width:285,
									allowBlank:false,
									invalidText:"必填字段"
							};
							var areaGrid=this.createAreaGridField(it);
							table.items.push(areaGrid)
							continue;
						}
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "homeAddress") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式

										fieldLabel : "<font  color=red>户籍地址:<font>",
										"width" : 285
									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;
						}

					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "restRegionCode") {
							var _ctr = this;
							var ff1 = new Ext.form.TriggerField({
										name : it.id,
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff1.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式

										fieldLabel : "<font  color=red>产休地:<font>",
										"width" : 285
									});
							this.ff1 = ff1;
							this.ff1.allowBlank = false;
							this.ff1.invalidText = "必填字段";
							this.ff1.regex = /(^\S+)/;
							this.ff1.regexText = "前面不能有空格字符";
							table.items.push(ff1)
							continue;
						}

					}
					var f = this.createField(it)
					f.index = i;

					f.anchor = it.anchor || "100%"

					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					table.items.push(f)
				}

				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.changeCfg(cfg);
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			initFormData : function(data) {

				chis.application.mhc.script.record.PregnantRecordForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
					if (data.homeAddress) {

						if (data.homeAddress.key) {

							this.form.getForm().findField("homeAddress")
									.setValue(data.homeAddress.text);
							if("pycode" == this.mainApp.exContext.areaGridShowType)
								{
							this.form.getForm().findField("homeAddress")
							.selectData.regionCode=data.homeAddress.key;
								}
							this.form.getForm().findField("homeAddress")
									.disable();
							this.data.homeAddress = data.homeAddress.key;
							this.data.homeAddress_text = data.homeAddress.text;
						}

					}

					if (data.restRegionCode) {
						if (data.restRegionCode.key) {
							this.form.getForm().findField("restRegionCode")
									.setValue(data.restRegionCode.text);
							if("pycode" == this.mainApp.exContext.areaGridShowType)
							{
							this.form.getForm().findField("restRegionCode")
							.selectData.regionCode=data.restRegionCode.key;							
							}
							this.form.getForm().findField("restRegionCode")
									.disable();
							this.data.restRegionCode = data.restRegionCode.key;
							this.data.restRegionCode_text = data.restRegionCode.text;
						}

					}

				}
			},

			onRegionCodeClick : function(r) {
				if ("update" == this.op) {
					return;
				}
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				m.areaGridListId = r;
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
				// m.loadData();
			},
			onQd : function(data) {
				if ("homeAddress_text" == data.areaGridListId) {
					this.form.getForm().findField("homeAddress_text")
							.setValue(data.regionCode_text);
					this.data.homeAddress = data.regionCode;
					this.data.homeAddress_text = data.regionCode_text;

				}
				if ("restRegionCode_text" == data.areaGridListId) {
					this.form.getForm().findField("restRegionCode_text")
							.setValue(data.regionCode_text);
					this.data.restRegionCode = data.regionCode;
					this.data.restRegionCode = data.regionCode_text;

				}

			},
			onChangeDic : function(items) {
				var field = {
					"type" : "string",
					"length" : "20",
					"acValue" : "1111",
					"id" : "husbandName",
					"alias" : "丈夫姓名",
					"fixed" : "true",
					"display" : "2"
				};
				var exists = this.findSchemaItemExists(this.schema, field);
				if (!exists) {
					items.splice(62, 0, field);
				}
				return true;
			},

			loadData : function() {
				this.preNotNull = false;
				this.initDataId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				if(!this.initDataId){
					this.fireEvent("controlPFVTab",false);
				}
				chis.application.mhc.script.record.PregnantRecordForm.superclass.loadData
						.call(this);
			},
			onLoadNoData : function(){
				this.fireEvent("controlPFVTab",false);
			},
			onBeforeCreate : function() {
				this.data.phrId = this.exContext.ids.phrId
				this.data.empiId = this.exContext.empiData.empiId
				this.form.el.mask("正在初始化数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initAction,
							method : "execute",
							schema : this.entryName,
							body : {
								"empiId" : this.exContext.empiData.empiId
							}
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var resBody = json.body
							if (resBody) {
								this.initFormData(resBody);
								var mhcDoctorId = resBody.mhcDoctorId;
								var manageUnits = resBody.manageUnits;
								if (manageUnits && manageUnits.length == 1) {
									this.setManaUnit(manageUnits[0]);
								} else {
									// 该责任医生可能归属的管辖机构编码集合
									this.manageUnits = manageUnits
									if (mhcDoctorId && mhcDoctorId.key
											&& manageUnits) {
										this.setManaUnit(null);
									}
								}
								this.parityLoad(resBody);
								this.ownerAreaLoad(resBody);
								var pastHistory = resBody.pastHistory;

								if (pastHistory) {
									this.pastHistoryOther(pastHistory.key);
								} else {
									this.changeFieldState(true,
											"otherPastHistory");
								}
								var familyHistory = resBody.familyHistory;
								if (familyHistory) {
									this
											.onFamilyHistorySelect(familyHistory.key);
								} else {
									this.changeFieldState(true,
											"otherFamilyHistory");
								}
								var personHistory = resBody.personHistory;
								if (personHistory) {
									this
											.onPersonHistorySelect(personHistory.key);
								} else {
									this.changeFieldState(true,
											"otherPersonHistory");
								}
							}
							var lmp = this.form.getForm()
									.findField("lastMenstrualPeriod");
							if (lmp) {
								lmp.enable();
							}
						}, this)
			},

			onReady : function() {
				chis.application.mhc.script.record.PregnantRecordForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();

				var restRegionCode = form.findField("restRegionCode");
				if (restRegionCode) {
					restRegionCode.on("beforeselect", this.getFamilyRegion,
							this);
				}

				var homeAddress = form.findField("homeAddress");
				if (homeAddress) {
					homeAddress.on("select", this.getMhcDoctor, this);
					homeAddress.on("beforeselect", this.getFamilyRegion, this);
				}
				var mhcDoctorId = form.findField("mhcDoctorId");
				if (mhcDoctorId) {
					mhcDoctorId.on("select", this.changeManaUnit, this);
				}

				var ownerArea = form.findField("ownerArea");
				if (ownerArea) {
					ownerArea.on("change", function(field) {
								var value = field.getValue();
								this.setHomeMess(value, true);
							}, this);
				}

				var manaUnitId = form.findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.checkManaUnit, this);
					manaUnitId.on("expand", this.filterManaUnit, this);
					manaUnitId.on("beforeQuery", this.onQeforeQuery, this)
				}

				var lastMenstrualPeriod = form.findField("lastMenstrualPeriod");
				if (lastMenstrualPeriod) {
					lastMenstrualPeriod.on("change", this.setGestationalWeeks,
							this);
					lastMenstrualPeriod.on("blur", this.onLMPChange, this);
					lastMenstrualPeriod.on("keyup", this.onLMPChange, this);
				}

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

				var vaginalDelivery = form.findField("vaginalDelivery");
				if (vaginalDelivery) {
					vaginalDelivery.on("change", this.onParityChange, this);
				}

				var abdominalDelivery = form.findField("abdominalDelivery");
				if (abdominalDelivery) {
					abdominalDelivery.on("change", this.onParityChange, this);
				}

				var preGestationDate = form.findField("preGestationDate");
				if (preGestationDate) {
					this.preGestationDate = preGestationDate;
					preGestationDate.on("change", this.onGestationDateChange,
							this);
				}

				var preGestationMode = form.findField("preGestationMode");
				if (preGestationMode) {
					this.preGestationMode = preGestationMode;
					preGestationMode.on("change", this.onGestationModeChange,
							this);
				}

				var preDeliveryDate = form.findField("preDeliveryDate");
				if (preDeliveryDate) {
					this.preDeliveryDate = preDeliveryDate;
					preDeliveryDate.on("change", this.onDeliveryDateChange,
							this);
				}

				var preDeliveryMode = form.findField("preDeliveryMode");
				if (preDeliveryMode) {
					this.preDeliveryMode = preDeliveryMode;
					preDeliveryMode.on("change", this.onDeliveryModeChange,
							this);
				}

				var pastHistory = form.findField("pastHistory");
				if (pastHistory) {
					pastHistory.on("select", this.onPastHistorySelect, this);
				}

				var familyHistory = form.findField("familyHistory");
				if (familyHistory) {
					familyHistory.on("select", function(field) {
								var value = field.getValue();
								this.onFamilyHistorySelect(value);
							}, this);
				}

				var personHistory = form.findField("personHistory");
				if (personHistory) {
					personHistory.on("select", function(field) {
								var value = field.getValue();
								this.onPersonHistorySelect(value);
							}, this);
				}

			},

			onGestationDateChange : function(field, newValue, oldValue) {
				if (!this.preNotNull) {
					return;
				}
				if (!newValue) {
					this.changeGestation(true);
					this.changeDelivery(true);
				} else {
					var gestationMode = this.preGestationMode.getValue();
					if (gestationMode) {
						this.changeDelivery(false);
					} else {
						this.changeDelivery(true);
					}
				}
			},

			onGestationModeChange : function(field, newValue, oldValue) {
				if (!this.preNotNull) {
					return;
				}
				if (!newValue) {
					this.changeGestation(true);
					this.changeDelivery(true);
				} else {
					var gestationDate = this.preGestationDate.getValue();
					if (gestationDate) {
						this.changeDelivery(false);
					} else {
						this.changeDelivery(true);
					}
				}
			},

			onDeliveryDateChange : function(field, newValue, oldValue) {
				if (!this.preNotNull) {
					return;
				}
				if (!newValue) {
					this.changeGestation(true);
					this.changeDelivery(true);
				} else {
					var deliveryMode = this.preDeliveryMode.getValue();
					if (deliveryMode) {
						this.changeGestation(false);
					} else {
						this.changeGestation(true);
					}
				}
			},

			onDeliveryModeChange : function(field, newValue, oldValue) {
				if (!this.preNotNull) {
					return;
				}
				if (!newValue) {
					this.changeGestation(true);
					this.changeDelivery(true);
				} else {
					var deliveryDate = this.preDeliveryDate.getValue();
					if (deliveryDate) {
						this.changeGestation(false);
					} else {
						this.changeGestation(true);
					}
				}
			},

			pastHistoryOther : function(value) {
				var disable = true;
				var valueArray = value.split(",");
				if (valueArray.indexOf("8") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherPastHistory");
			},

			onPastHistorySelect : function(combo, record, index) {
				var value = combo.value;
				var valueArray = value.split(",");
				if (valueArray.indexOf("1") != -1) {
					combo.clearValue();
					if (record.data.key == 1) {
						combo.setValue({
									key : 1,
									text : "无"
								});
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue({
								key : 1,
								text : "无"
							});
				}
				var lastValue = combo.getValue();
				var disable = true;
				if (lastValue.indexOf("8") > -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherPastHistory");
			},

			onPersonHistorySelect : function(value) {
				var disable = true;
				var valueArray = value.split(",");
				if (valueArray.indexOf("6") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherPersonHistory");
			},

			onFamilyHistorySelect : function(value) {
				var disable = true;
				var valueArray = value.split(",");
				if (valueArray.indexOf("3") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherFamilyHistory");
			},

			checkManaUnit : function(comb, node) {
				var key = node.attributes['key'];
				if (key.length >= 9) {
					return true;
				} else {
					return false;
				}
			},

			onQeforeQuery : function(params) {
				if (!this.manageUnits) {
					return;
				}
				var cnd = "";
				var len = this.manageUnits.length;
				for (var i = 0; i < len; i++) {
					cnd = cnd + "['eq',['$map',['s','key']],['s',"
							+ this.manageUnits[i] + "]],";
				}
				params["sliceType"] = "0";
				params["filter"] = "['or'," + cnd.substring(0, cnd.length - 1)
						+ "]";
			},

			getFamilyRegion : function(comb, node) {
				var isBottom = node.attributes['isBottom'];
				if (isBottom == 'y') {
					return true;
				} else {
					return false;
				}
			},

			changeManaUnit : function(combo, node) {
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
					combox.reset();
					return;
				}

				combox.setValue(manageUnit)
				combox.disable();
			},

			filterManaUnit : function(field) {
				var tree = field.tree;
				tree.getRootNode().reload();
				tree.expandAll();
				tree.on("expandnode", function(node) {
							var childs = node.childNodes
							for (var i = childs.length - 1; i >= 0; i--) {
								var child = childs[i]
								child.on("load", function() {
											tree.filter.filterBy(
													this.filterTree, this)
										}, this)
							}
						}, this);
			},

			filterTree : function(node) {
				if (!node || !this.manageUnits) {
					return true;
				}
				var key = node.attributes["key"];
				for (var i = 0; i < this.manageUnits.length; i++) {
					var manageUnit = this.manageUnits[i]
					if (this.startWith(manageUnit, key)) {
						return true;
					}
				}
			},

			startWith : function(str1, str2) {
				if (str1 == null || str2 == null)
					return false;
				if (str2.length > str1.length)
					return false;
				if (str1.substr(0, str2.length) == str2)
					return true;
				return false;
			},

			onParityChange : function(field, newValue, oldValue) {
				var form = this.form.getForm();
				var abdominalDelivery = form.findField("abdominalDelivery");
				var vaginalDelivery = form.findField("vaginalDelivery");
				var abdomial = abdominalDelivery.getValue() || 0;
				var vaginal = vaginalDelivery.getValue() || 0;
				var value = abdomial + vaginal;
				var notNull = false;
				if (value >= 1) {
					this.preNotNull = true;
					notNull = true;
				} else {
					this.preNotNull = false;
				}
				if (!notNull) {
					this.changeGestation(false);
					this.changeDelivery(false);
					return;
				}
				var preGestationDate = this.preGestationDate.getValue();
				var preGestationMode = this.preGestationMode.getValue();
				if (preGestationDate && preGestationMode) {
					this.changeDelivery(false);
				} else {
					this.changeDelivery(true);
				}
				var preDeliveryDate = this.preDeliveryDate.getValue();
				var preDeliveryMode = this.preDeliveryMode.getValue();
				if (preDeliveryDate && preDeliveryMode) {
					this.changeGestation(false);
				} else {
					this.changeGestation(true);
				}
			},

			changeGestation : function(notNull) {
				this.preGestationDate.allowBlank = !notNull;
				this.preGestationDate.validate();
				this.preGestationMode.allowBlank = !notNull;
				this.preGestationMode.validate();
			},

			changeDelivery : function(notNull) {
				this.preDeliveryDate.allowBlank = !notNull;
				this.preDeliveryDate.validate();
				this.preDeliveryMode.allowBlank = !notNull;
				this.preDeliveryMode.validate();
			},

			getMhcDoctor : function(field) {
				var value = field.getValue();
				if (value == null || value == "") {
					return;
				}
				var form = this.form.getForm();
				this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "getMhcDoctorInfo",
							method : "execute",
							body : {
								"regionCode" : value
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (!json.body) {
								return;
							}
							var manaDocField = form.findField("mhcDoctorId");
							var mhcDoctor = json.body.mhcDoctor;
							if (mhcDoctor && manaDocField) {
								manaDocField.setValue(mhcDoctor);
								if (!json.body.manageUnits) {
									return;
								}
								var manageUnits = json.body.manageUnits;
								if (manageUnits && manageUnits.length == 1) {
									this.setManaUnit(manageUnits[0]);
								} else {
									// 该责任医生可能归属的管辖机构编码集合
									this.manageUnits = manageUnits
									this.setManaUnit(null);
								}
							}
						}, this)
			},

			onSbpChange : function(field) {
				var constriction = field.getValue();
				var diastolicFld = this.form.getForm().findField("dbp");
				var diastolic = diastolicFld.getValue();
				if (constriction) {
					diastolicFld.maxValue = constriction - 1;
				} else {
					diastolicFld.maxValue = 500;
				}
				diastolicFld.minValue = 10;
				if (diastolic) {
					field.minValue = diastolic + 1;
				} else {
					field.minValue = 10;
				}
				field.maxValue = 500;
				field.validate();
				diastolicFld.validate();
				if (diastolic && diastolic && constriction <= diastolic) {
					field.markInvalid("收缩压应该大于舒张压！");
					diastolicFld.markInvalid("舒张压应该小于收缩压！");
				}
			},

			onDbpChange : function(field) {
				var diastolic = field.getValue();
				var constrictionFld = this.form.getForm().findField("sbp");
				var constriction = constrictionFld.getValue();
				if (constriction) {
					field.maxValue = constriction - 1;
				} else {
					field.maxValue = 500;
				}
				field.minValue = 10;
				if (diastolic) {
					constrictionFld.minValue = diastolic + 1;
				} else {
					constrictionFld.minValue = 10;
				}
				constrictionFld.maxValue = 500;
				field.validate();
				constrictionFld.validate();
				if (diastolic && diastolic && constriction <= diastolic) {
					constrictionFld.markInvalid("收缩压应该大于舒张压！");
					field.markInvalid("舒张压应该小于收缩压！");
				}
			},

			onLMPChange : function(field) {
				if (!field.validate()) {
					return;
				}
				var form = this.form.getForm();
				var pgdField = form.findField("preGestationDate");
				var pddField = form.findField("preDeliveryDate");
				var constriction = field.getValue();
				if (!constriction) {
					pgdField.setMaxValue(null);
					pgdField.validate();
					pddField.setMaxValue(null);
					pddField.validate();
					return;
				}
				var maxVallue = new Date(constriction.getFullYear(),
						constriction.getMonth(), constriction.getDate() - 1);
				pgdField.setMaxValue(maxVallue);
				pgdField.validate();
				pddField.setMaxValue(maxVallue);
				pddField.validate();
			},

			onLoadData : function(entryName, body) {
				var tab1Enable = false; 
				var pregnantId = body.pregnantId;
				if(pregnantId){
					tab1Enable = true;
				}
				this.fireEvent("controlPFVTab",tab1Enable);
				this.ownerAreaLoad(body);

				this.parityLoad(body);

				var pastHistory = body.pastHistory;
				if (pastHistory) {
					this.pastHistoryOther(pastHistory.key);
				} else {
					this.changeFieldState(true, "otherPastHistory");
				}

				var familyHistory = body.familyHistory;
				if (familyHistory) {
					this.onFamilyHistorySelect(familyHistory.key);
				} else {
					this.changeFieldState(true, "otherFamilyHistory");
				}

				var personHistory = body.personHistory;
				if (personHistory) {
					this.onPersonHistorySelect(personHistory.key);
				} else {
					this.changeFieldState(true, "otherPersonHistory");
				}

				var form = this.form.getForm();
				var lmp = form.findField("lastMenstrualPeriod");
				if (lmp) {
					var hasVisited = body.hasVisited;
					if (hasVisited) {
						lmp.disable();
					} else {
						lmp.enable();
					}
					this.onLMPChange(lmp);
				}

				var sbp = form.findField("sbp");
				if (sbp) {
					this.onSbpChange(sbp);
				}

				var dbp = form.findField("dbp");
				if (dbp) {
					this.onDbpChange(dbp);
				}
			},

			ownerAreaLoad : function(body) {
				var ownerArea = body.ownerArea;
				if (ownerArea) {
					this.setHomeMess(ownerArea.key, false);
				}
			},

			parityLoad : function(body) {
				var vaginalDelivery = body["vaginalDelivery"];
				var abdominalDelivery = body["abdominalDelivery"];
				var parity = vaginalDelivery + abdominalDelivery;
				var notNull = false;
				if (parity >= 1) {
					this.preNotNull = true;
					notNull = true;
				} else {
					this.preNotNull = false;
				}
				if (!notNull) {
					this.changeGestation(false);
					this.changeDelivery(false);
					return;
				}
				var preGestationDate = body.preGestationDate;
				var preGestationMode = body.preGestationMode;
				if (preGestationDate && preGestationMode) {
					this.changeDelivery(false);
				} else {
					this.changeDelivery(true);
				}
				var preDeliveryDate = body.preDeliveryDate;
				var preDeliveryMode = body.preDeliveryMode;
				if (preDeliveryDate && preDeliveryMode) {
					this.changeGestation(false);
				} else {
					this.changeGestation(true);
				}
			},

			setHomeMess : function(value, resetValue) {
				var form = this.form.getForm();
				var homeAddress = form.findField("homeAddress");
				var residenceCode = form.findField("residenceCode");
				var disable = false;
				if (value == "1" || value == "4" || value == "本市"
						|| value == "婚嫁到本市") {
					disable = true;
				}

				if (resetValue) {
					homeAddress.reset();
				}
				homeAddress.validate();
				if (disable) {
					homeAddress.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>户籍地址:</span>";
				} else {
					homeAddress.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>居(暂)住地址:</span>";
				}

				residenceCode.allowBlank = disable;
				residenceCode.setDisabled(disable);
				if (resetValue) {
					residenceCode.reset();
				}
				residenceCode.validate();
				this.changeFieldState(disable, "residencePermit");
			},

			setGestationalWeeks : function(field) {
				var date = field.getValue();
				if (!date) {
					return;
				}
				var form = this.form.getForm();
				// ** 计算建册孕周
				var createDate;
				if (!this.initDataId) {
					if (!this.currentDate) {
						this.currentDate = Date.parseDate(
								this.mainApp.serverDate, "Y-m-d");
					}
					createDate = this.currentDate;
				} else {
					var crdate = form.findField("createDate");
					createDate = crdate.getValue();
				}
				var weeks = (((createDate - date) / 1000 / 60 / 60 / 24) + 1)
						/ 7;
				form.findField("gestationalWeeks").setValue(Math.floor(weeks));

				// ** 计算预产期
				var birthDate = new Date(date.getFullYear() + 1, date
								.getMonth()
								- 3, date.getDate() + 7);
				form.findField("dateOfPrenatal").setValue(birthDate);
			},

			doSave : function() {
				this.fireEvent("recordSave");
			},

			getSaveData : function() {
				var values = this.getFormData();
				if (!values || values.length < 1) {
					return null;
				} else {
					var pastHistory = this.form.getForm()
							.findField("pastHistory");
					if (pastHistory) {
						values["pastHistory_text"] = pastHistory.getRawValue();
					}
					return values;
				}
			}
			,

	getFormData : function() {
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext);
				if (items) {
					var form = this.form.getForm();
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh
						// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if(it.id=='homeAddress'||it.id=='restRegionCode')
							{
								v = f.getValue();
							}
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}

					if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {

							if ("homeAddress" == it.id) {
								v = this.data.homeAddress;
							}
							
							if ("homeAddress_text" == it.id) {
								v = this.data.homeAddress_text;
							}
							
							if ("restRegionCode" == it.id) {
								v = this.data.restRegionCode;
							}
							
							if ("restRegionCode_text" == it.id) {
								v = this.data.restRegionCode_text;
							}
							

						}

						if (v == null || v === "") {
							if (!(it.pkey)
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}

				return values;
			},

	getFormDataBackHtml : function() {
				 
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext);
				if (items) {
					var form = this.form.getForm();
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i] 
						var v = this.data[it.id] // ** modify by yzh
						// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}

					if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("homeAddress" == it.id) {
								v = this.data.homeAddress;
							}
							
							if ("homeAddress_text" == it.id) {
								v = this.data.homeAddress_text;
							}
							
							if ("restRegionCode" == it.id) {
								v = this.data.restRegionCode;
							}
							
							if ("restRegionCode_text" == it.id) {
								v = this.data.restRegionCode_text;
							}
							

						} 
						values[it.id] = v;
					}
				}

				return values;
			}
		});