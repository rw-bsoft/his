/**
 * 儿童档案表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.record")
$import("util.widgets.LookUpField", "chis.script.BizTableFormView")
chis.application.cdh.script.record.ChildrenHealthRecordForm = function(cfg) {
	cfg.colCount = 4;
	cfg.fldDefaultWidth = 150
	this.autoFieldWidth = false;
	chis.application.cdh.script.record.ChildrenHealthRecordForm.superclass.constructor
			.apply(this, [cfg])
	this.initServiceAction = "initChildHealth";
	this.on("changeDic", this.onChangeDic, this)
	this.on("doNew", this.onDoNew, this)
	this.on("beforeCreate", this.onBeforeCreate, this)
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadData", this.onLoadData, this);
	this.deliveryColumns = ["apgar1", "apgar5", "apgar10", "gestation",
			"birthHeight", "birthWeight", "headSize", "circumference"];
}
Ext.extend(chis.application.cdh.script.record.ChildrenHealthRecordForm,
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
					if (it.id == "homeAddress") {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							it.otherConfig={
									'not-null':'true',
									colspan:2,
									width:570,
									anchor:"100%",
									allowBlank:false,
									invalidText:"必填字段"
							};
							it.filterType='falmily';
							//it.afterSelect=this.afterSelect;
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
										"width" : 604
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

				chis.application.cdh.script.record.ChildrenHealthRecordForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
					if (data.homeAddress) {

						if (data.homeAddress.key) {

							this.form.getForm().findField("homeAddress")
									.setValue(data.homeAddress.text);
							this.form.getForm().findField("homeAddress")
									.disable();
							if("form" == this.mainApp.exContext.areaGridShowType)
							{
								this.form.getForm().findField("homeAddress")
								.selectData.homeAddress=data.homeAddress.key;
							}
							this.data.homeAddress = data.homeAddress.key;
							this.data.homeAddress_text = data.homeAddress.text;

						}
					}

				}
			},

			onRegionCodeClick : function(r) {
				
				if ("update" == this.op||"create" == this.op) {
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

			},
			onChangeDic : function(items) {
				var fields = [{
							"type" : "string",
							"length" : "20",
							"acValue" : "1111",
							"id" : "motherName",
							"alias" : "母亲姓名",
							"xtype" : "lookupfieldex"
						}, {
							"type" : "string",
							"length" : "20",
							"acValue" : "1111",
							"id" : "fatherName",
							"alias" : "父亲姓名",
							"xtype" : "lookupfieldex"
						}];
				for (var i = 0; i < fields.length; i++) {
					var field = fields[i];
					var exists = this.findSchemaItemExists(this.schema, field);
					if (!exists) {
						items.splice(14, 0, field);
					}
				}
				return true;
			},

			resetDeliveryColumns : function() {
				var form = this.form.getForm();
				for (var i = 0; i < this.deliveryColumns.length; i++) {
					var field = form.findField(this.deliveryColumns[i])
					if (field) {
						field.reset();
						field.enable();
					}
				}
			},

			onBeforeCreate : function() {
				this.data["empiId"] = this.exContext.ids.empiId;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.initServiceAction,
							method : "execute",
							schema : this.entryName,
							body : {
								"empiId" : this.exContext.ids.empiId
							}
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body) {

								this.initFormData(body);
								var form = this.form.getForm();
								var cdhDoctorId = body.cdhDoctorId;
								var manageUnits = body.manageUnits;
								if (manageUnits && manageUnits.length == 1) {
									this.setManaUnit(manageUnits[0]);
								} else {
									// 该责任医生可能归属的管辖机构编码集合
									this.manageUnits = manageUnits
									if (cdhDoctorId && cdhDoctorId.key) {
										this.setManaUnit(null);
									}
								}
								var otherDeformity = body.otherDeformity;
								if (otherDeformity) {
									var otherDeformityF = form
											.findField("otherDeformity")
									otherDeformityF.enable();
								}
								var otherAllergyDrug = json.otherAllergyDrug;
								if (otherAllergyDrug) {
									var otherAllergyDrugF = form
											.findField("otherAllergyDrug");
									otherAllergyDrugF.enable();
								}
								this.oneYearLive();
								var ownerArea = form.findField("ownerArea")
								if (ownerArea) {
									this.changeLabel(ownerArea.getValue(), "0");
								}

							}
						}, this)
			},

			onDoNew : function() {
				this.resetDeliveryColumns();
			},

			afterSaveData : function(entryName, op, json, data) {
				chis.application.cdh.script.record.ChildrenHealthRecordForm.superclass.afterSaveData
						.call(this, entryName, op, json, data);
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
				if (op == "create") {
					this.fireEvent("refreshModule", "H_09");
				}
				var body = json.body;
				if (!body) {
					return;
				}
				var disabilityMonitor = body.disabilityMonitor;
				if (disabilityMonitor) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '发现残疾信息，是否需要建立疑似残疾报告?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.fireEvent("activeModule", "H_14");
									}
								},
								scope : this
							});
				}

				var updatedm = body.updateDisabilityMonitor;
				if (updatedm) {
					this.fireEvent("refreshData", "H_14");
				}
				this.refreshEhrTopIcon();
			},

			onBeforeSave : function(entryName, op, saveData) {

				var form = this.form.getForm();
				var screen = form.findField("screen");
				saveData["screen_text"] = screen.getRawValue();
				var allergicHistory = form.findField("allergicHistory");
				saveData["allergicHistory_text"] = allergicHistory
						.getRawValue();
				var deformity = form.findField("deformity");
				saveData["deformity_text"] = deformity.getRawValue();
				saveData["birthday"] = this.exContext.empiData.birthday;
				saveData["phrId"] = this.exContext.ids.phrId;

				if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
					saveData["homeAddress"] = this.data.homeAddress;
					saveData["homeAddress_text"] = this.data.homeAddress_text;

				}

			},

			onReady : function() {
				chis.application.cdh.script.record.ChildrenHealthRecordForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				var motherName = form.findField("motherName");
				if (motherName) {
					motherName.on("lookup", this.getParent, this);
					motherName.on("clear", function() {
								this.data["motherEmpiId"] = "";
								if (this.skey == "motherCardNo") {
									this.resetDeliveryColumns();
								}
							}, this)
				}

				var fatherName = form.findField("fatherName");
				if (fatherName) {
					fatherName.on("lookup", this.getParent, this);
					fatherName.on("clear", function() {
								this.data["fatherEmpiId"] = "";
								if (this.skey == "fatherCardNo") {
									this.resetDeliveryColumns();
								}
							}, this)
				}

				var homeAddress = form.findField("homeAddress");
				if (homeAddress) {
					homeAddress.on("afterSelect", this.getCdhDoctor, this);
					homeAddress.on("select", this.getCdhDoctor, this);
				}

				var cdhDoctor = form.findField("cdhDoctorId");
				if (cdhDoctor) {
					cdhDoctor.on("select", this.changeManaUnit, this);
				}

				var manaUnitId = form.findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.checkManaUnit, this);
					manaUnitId.on("expand", this.filterManaUnit, this);
					manaUnitId.on("beforeQuery", this.onQeforeQuery, this)
				}

				var boneCondition = form.findField("boneCondition");
				if (boneCondition) {
					boneCondition.on("select", this.setOtherBone, this);
				}

				var motherAbnormal = form.findField("motherAbnormal");
				if (motherAbnormal) {
					motherAbnormal.on("select", this.setAbnormal, this);
				}

				var screenRecord = form.findField("screenRecord");
				if (screenRecord) {
					screenRecord.on("select", this.setScreenResult, this);
				}

				var screenFlage = form.findField("screenFlage");
				if (screenFlage) {
					screenFlage.on("select", this.setScreen, this);
				}

				var screen = form.findField("screen");
				if (screen) {
					screen.on("select", this.setOtherScreen, this);
					screen.on("valid", this.resetValue, this);
				}

				var deformity = form.findField("deformity");
				if (deformity) {
					deformity.on("select", this.setOtherDeformity, this);
					deformity.on("valid", this.resetValue, this);
				}

				var familyHistory = form.findField("familyHistory");
				if (familyHistory) {
					familyHistory.on("select", this.setOtherFamily, this);
					familyHistory.on("valid", this.resetValue, this);
				}

				var allergicHistory = form.findField("allergicHistory");
				if (allergicHistory) {
					allergicHistory
							.on("select", this.setOtherAllergicHis, this);
					allergicHistory.on("valid", this.resetValue, this);
				}

				var birthDefects = form.findField("birthDefects");
				if (birthDefects) {
					birthDefects.on("select", this.setDefectsType, this);
				}

				var defectsType = form.findField("defectsType");
				if (defectsType) {
					defectsType.on("valid", this.resetValue, this);
				}

				var isHighRisk = form.findField("isHighRisk");
				if (isHighRisk) {
					isHighRisk.on("select", this.setHighRiskType, this);
				}

				var highRiskType = form.findField("highRiskType");
				if (highRiskType) {
					highRiskType.on("select", this.setOtherType, this);
				}

				var ownerArea = form.findField("ownerArea");
				if (ownerArea) {
					ownerArea.on("change", function() {
								this.changeLabel(ownerArea.getValue());
							}, this)
				}

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

			resetValue : function(field) {
				field.setEditable(false);
			},

			changeLabel : function(n, c) {
				var ownerArea = n
				var address = this.form.getForm().findField("homeAddress")
				if (!c) {
					address.reset()
				}
				if (address) {
					if (ownerArea == "11") {
						Ext
								.getCmp(address.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'> 户籍地址:</span>");
					} else {
						Ext
								.getCmp(address.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'> 暂住证地址:</span>");
					}
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

			setHighRiskType : function(field) {
				var value = field.value
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "highRiskType");
			},

			setOtherType : function(field) {
				var value = field.value
				var disable = true;
				if (value.indexOf("17") != -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherType");
			},

			setScreenResult : function(field) {
				var value = field.value
				var disable = true;
				if (value == "5") {
					disable = false;
				}
				this.changeFieldState(disable, "otherScreenResult");
			},

			setScreen : function(field) {
				var value = field.value
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "screen");
			},

			setAbnormal : function(field) {
				var value = field.value
				var disable = true;
				if (value == "y") {
					disable = false;
				}
				this.changeFieldState(disable, "abnormal");
			},

			setOtherAllergicHis : function(combo, record, index) {
				var value = combo.value
				var valueArray = value.split(",");
				if (valueArray.indexOf("0101") != -1) {
					combo.clearValue();
					if (record.data.key == 0101) {
						combo.setValue({
									key : 0301,
									text : "无"
								});
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue({
								key : 0301,
								text : "无"
							});
				}
				var lastValue = combo.getValue();
				var disable = true;
				if (lastValue.indexOf("0109") > -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherAllergyDrug");
			},

			setOtherFamily : function(combo, record, index) {
				var value = combo.value
				var valueArray = value.split(",");
				if (valueArray.indexOf("1") != -1) {
					combo.clearValue();
					if (record.data.key == 1) {
						combo.setValue({
									key : 1,
									text : "无"
								});
						this.changeFieldState(true, "relationship");
					} else {
						combo.setValue(record.data);
						this.changeFieldState(false, "relationship");
					}
				}
				if (value == "") {
					combo.setValue({
								key : 1,
								text : "无"
							});
					this.changeFieldState(true, "relationship");
				}
				var lastValue = combo.getValue();
				var disable = true;
				if (lastValue) {
					if (lastValue.indexOf("10") > -1) {
						disable = false;
					}
					this.changeFieldState(disable, "otherFamily");
				}
			},

			setOtherDeformity : function(combo, record, index) {
				var value = combo.value
				var valueArray = value.split(",");
				if (valueArray.indexOf("1101") != -1) {
					combo.clearValue();
					if (record.data.key == 1101) {
						combo.setValue({
									key : 1101,
									text : "无残疾"
								});
					} else {
						combo.setValue(record.data);
					}
				}
				if (value == "") {
					combo.setValue({
								key : 1101,
								text : "无残疾"
							});
				}

				var lastValue = combo.getValue();
				var disable = true;
				if (lastValue.indexOf("1199") > -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherDeformity");
			},

			setOtherScreen : function(field) {
				var value = field.value
				var disable = true;
				if (value.indexOf("21") > -1) {
					disable = false;
				}
				this.changeFieldState(disable, "otherScreen");
			},

			setOtherBone : function(field) {
				var value = field.value
				var disable = true;
				if (value == "7") {
					disable = false;
				}
				this.changeFieldState(disable, "otherBone");
			},

			setDefectsType : function(field) {
				var value = field.value
				var disable = false;
				if (value == "n") {
					disable = true;
				}
				this.changeFieldState(disable, "defectsType");
			},

			getCdhDoctor : function(field) {
				var value = field.getValue();
				if (value == null || value == "") {
					return;
				}
				var form = this.form.getForm();
				this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : 'chis.childrenHealthRecordService',
							serviceAction : "getCdhDoctorInfo",
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
							var cdhDocField = form.findField("cdhDoctorId");
							var cdhDoctor = json.body.cdhDoctor;
							if (cdhDoctor && cdhDocField) {
								cdhDocField.setValue(cdhDoctor);
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

			getParent : function(field) {
				this.fieldName = field.name;
				$import("chis.application.mpi.script.EMPIInfoModule")
				var expertQuery = this.midiModules["expertQuery"];
				if (!expertQuery) {
					expertQuery = new chis.application.mpi.script.EMPIInfoModule(
							{
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
					this.midiModules["expertQuery"] = expertQuery;
					expertQuery.on("onEmpiReturn", function(r) {
								if (!r) {
									return;
								}
								if (this.fieldName == "fatherName") {
									if (r.sexCode != '1') {
										Ext.Msg.alert('提示信息', '性别不符!')
										return;
									}
								} else if (this.fieldName == "motherName") {
									if (r.sexCode != '2') {
										Ext.Msg.alert('提示信息', '性别不符!')
										return;
									}
								}
								var empiId = r.empiId;
								var skey = "";
								var personName = r.personName;
								var textField = this.form.getForm()
										.findField(this.fieldName);
								if (textField) {
									textField.setValue(personName);
								}
								if (this.fieldName == "fatherName") {
									this.data["fatherEmpiId"] = empiId;
									this.skey = "fatherCardNo";
								} else if (this.fieldName == "motherName") {
									this.data["motherEmpiId"] = empiId;
									this.skey = "motherCardNo";
								}
								var idcard = r.idCard
								this.queryDeliveryRecord(idcard);
								fieldName = "";
							}, this);
				}
				var win = expertQuery.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			queryDeliveryRecord : function(idcard) {
				var cnd = ['eq', ['s', idcard], ['$', this.skey]]
				var req = {
					serviceId : this.saveServiceId,
					serviceAction : "queryDeliveryRecord",
					method : "execute",
					body : {
						idcard : idcard,
						skey : this.skey
					}
				}
				util.rmi.jsonRequest(req, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					var records = json.body;
					if (!records || records.length == 0)
						return;
					if (records.length == 1) {
						var record = records[0];
						var r = new Ext.Record(record);
						setDeliveryRecordData(r);
						return;
					}
					var selectView = this.midiModules["selectView"];
					if (!selectView) {
						$import("chis.application.cdh.script.record.DeliveryRecordSelectList")
						selectView = new chis.application.cdh.script.record.DeliveryRecordSelectList(
								{});
						selectView.on("onSelect", function(r) {
									this.setDeliveryRecordData(r)
								}, this);
						this.midiModules["selectView"] = selectView;
					}
					var win = selectView.getWin();
					win.setPosition(250, 100);
					win.show();
					selectView.setData(records)
				}, this)// jsonRequest
			},

			setDeliveryRecordData : function(r) {
				var form = this.form.getForm();
				for (var i = 0; i < this.deliveryColumns.length; i++) {
					var field = form.findField(this.deliveryColumns[i])
					if (field == null) {
						continue;
					}
					if (field) {
						field.setValue(r.get(this.deliveryColumns[i]))
						field.disable();
					}
				}
				var queryKey = "";
				var fieldKey = "";
				var empiIdKey = "";
				// set parent info
				if (this.skey == "fatherCardNo") {
					queryKey = "motherCardNo";
					fieldKey = "motherName";
					empiIdKey = "motherEmpiId";
				} else {
					queryKey = "fatherCardNo";
					fieldKey = "fatherName";
					empiIdKey = "fatherEmpiId";
				}

				var idCard = r.get(queryKey);
				var queryData = {};
				var cards = [];
				var card = {
					"certificateTypeCode" : "01",
					"certificateNo" : idCard
				};
				cards.push(card);
				queryData["certificates"] = cards;
				queryData["queryBy"] = "idCard";
				util.rmi.jsonRequest({
					serviceId : "chis.empiService",
					schema : "chis.application.mpi.schemas.MPI_DemographicInfo",
					serviceAction : "advancedSearch",
					method : "execute",
					body : queryData
				}, function(code, msg, json) {
					this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					if (json.body) {
						var data = json.body[0];
						if (!data)
							return;
						var personName = data["personName"];
						var empiId = data["empiId"];
						var parentField = form.findField(fieldKey);
						parentField.setValue(personName);
						this.data[empiIdKey] = empiId;
					}
				}, this)
			},

			loadData : function() {
				this.initDataId = this.exContext.ids["CDH_HealthCard.phrId"];
				chis.application.cdh.script.record.ChildrenHealthRecordForm.superclass.loadData
						.call(this);
			},

			oneYearLive : function() {
				var rp = this.exContext.empiData.registeredPermanent
				if (!rp) {
					return;
				}
				var oneYearLive = this.form.getForm().findField("oneYearLive");
				if (rp == "2") {
					Ext.getCmp(oneYearLive.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:red'>" + "居住一年以上"
									+ ":</span>");
					oneYearLive.allowBlank = false
					oneYearLive.enable();
				} else {
					Ext.getCmp(oneYearLive.id).getEl().up('.x-form-item')
							.child('.x-form-item-label')
							.update("<span style='color:black'>" + "居住一年以上"
									+ ":</span>");
					oneYearLive.reset();
					oneYearLive.allowBlank = true
					oneYearLive.disable();
				}
				this.validate()
			},

			onLoadData : function(entryName, body) {
				var ownerArea = body.ownerArea;
				if (ownerArea && ownerArea.key) {
					this.changeLabel(ownerArea.key, "0")
				}

				var motherAbnormal = body["motherAbnormal"]
				if (motherAbnormal) {
					var disable = true;
					if (motherAbnormal.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "abnormal");
				}

				var boneCondition = body["boneCondition"]
				if (boneCondition) {
					var disable = true;
					if (boneCondition.key == "7") {
						disable = false;
					}
					this.changeFieldState(disable, "otherBone");
				}

				var screenRecord = body["screenRecord"]
				if (screenRecord) {
					var disable = true;
					if (screenRecord.key == "5") {
						disable = false;
					}
					this.changeFieldState(disable, "otherScreenResult");
				}

				var screenFlage = body["screenFlage"]
				if (screenFlage) {
					var disable = true;
					if (screenFlage.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "screen");
				}

				var screen = body["screen"]
				if (screen) {
					var disable = true;
					if (screen.key)
						if (screen.key.indexOf("21") > -1) {
							disable = false;
						}
					this.changeFieldState(disable, "otherScreen");
				}

				var deformity = body["deformity"]
				if (deformity) {
					var disable = true;
					if (deformity.key)
						if (deformity.key.indexOf("1199") > -1) {
							disable = false;
						}
					this.changeFieldState(disable, "otherDeformity");
				}

				var familyHistory = body["familyHistory"]
				if (familyHistory && familyHistory.key) {
					var key = familyHistory.key;
					var valueArray = key.split(",");
					if (valueArray.indexOf("1") != -1) {
						this.changeFieldState(true, "relationship");
					} else {
						this.changeFieldState(false, "relationship");
					}
					var disable = true;
					if (key.indexOf("10") > -1) {
						disable = false;
					}
					this.changeFieldState(disable, "otherFamily");
				}

				var allergicHistory = body["allergicHistory"]
				if (allergicHistory) {
					var disable = true;
					if (allergicHistory.key)
						if (allergicHistory.key.indexOf("0109") > -1) {
							disable = false;
						}
					this.changeFieldState(disable, "otherAllergyDrug");
				}

				var birthDefects = body["birthDefects"]
				if (birthDefects) {
					var disable = false;
					if (birthDefects.key == "n") {
						disable = true;
					}
					this.changeFieldState(disable, "defectsType");
				}

				var isHighRisk = body["isHighRisk"]
				if (isHighRisk) {
					var disable = true;
					if (isHighRisk.key == "y") {
						disable = false;
					}
					this.changeFieldState(disable, "highRiskType");
				}

				var highRiskType = body["highRiskType"]
				if (highRiskType && highRiskType.key) {
					var disable = true;
					if (highRiskType.key.indexOf("17") != -1) {
						disable = false;
					}
					this.changeFieldState(disable, "otherType");
				}

				this.oneYearLive();
			},
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
							if(it.id=='homeAddress')
							{
								if ("pycode" == this.mainApp.exContext.areaGridShowType) {
									v = f.getAreaCodeValue();
								}
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
			}
		})