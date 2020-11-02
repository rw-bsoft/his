/**
 * 儿童基本信息页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.base")
$import("util.Accredit", "chis.script.BizTableFormView",
		"chis.application.mpi.script.CombinationSelect",
		"chis.script.util.Vtype", "chis.script.ICCardField",
		"util.widgets.LookUpField")
chis.application.cdh.script.base.ChildInfoForm = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.labelAlign = "left";
	cfg.wi=cfg.wi||121;
	cfg.showButtonOnTop = true;
	cfg.autoLoadData = false;
	cfg.labelWidth = 90;
	cfg.actions = cfg.actions || [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	chis.application.cdh.script.base.ChildInfoForm.superclass.constructor
			.apply(this, [cfg])
	this.on("afterPanelInit", this.onAfterPanelInit, this);
	this.on("saveData", this.onSaveData, this)
	this.on("winShow", this.onWinShow);
	this.on("doNew", this.onDoNew, this);
	this.saveServiceId = "chis.childrenHealthRecordService"
	this.queryServiceId = "chis.childrenHealthRecordService";
	this.serviceAction = "saveChildBaseMessage"
	this.healthServiceId = "chis.healthRecordService"
	this.healthServiceAction = "queryHealthRecord"
}
Ext.extend(chis.application.cdh.script.base.ChildInfoForm,
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
					var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
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
					
					if (it.id == "regionCode") {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							it.otherConfig={
									'not-null':'true',
									colspan:2,
									width:399,
									allowBlank:false,
									invalidText:"必填字段"
							};
							//it.afterSelect=this.afterSelect;
							var areaGrid=this.createAreaGridField(it);
							table.items.push(areaGrid)
							continue;
						}
					}

					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "regionCode") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
										name : it.id,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick()
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										colspan:2,
										fieldLabel : "<font  color=red>网格地址:<font>",
										"width" : 399
                                         //_ctr.wi 两个地方调用
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
//					if ("form" == this.mainApp.exContext.areaGridShowType) {
//						f.anchor = it.anchor || "90%"
//					} else {
						f.anchor = it.anchor || "100%"
				//	}
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

			onRegionCodeClick : function() {
				 if ("update" == this.op) {
					return;
				}
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
				// m.loadData();
			},

			onQd : function(data) {

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode_text);

					this.data.regionCode_text = data.regionCode_text;

				}
				if ("tree" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode);
					this.data.regionCode_text = data.regionCode_text;
				}
				this.data.regionCode = data.regionCode;

			},
//			initFormData : function(data) {
//				
//				if ("tree" == this.mainApp.exContext.areaGridShowType) {
//					this.form.getForm().findField("regionCode")
//							.setValue(data.regionCode.key);
//					this.data.regionCode_text = data.regionCode.text;
//
//				}
//				chis.application.cdh.script.base.ChildInfoForm.superclass.initFormData
//						.call(this, data);
//
//			},
			// 覆盖父类函数增加控件事件处理
			onReady : function() {
				chis.application.cdh.script.base.ChildInfoForm.superclass.onReady
						.call(this);

				var form = this.form.getForm();
				var birthday = form.findField("birthday");
				if (birthday) {
					birthday.on("focus", this.onSelectDate, this);
				}

				var cardNo = form.findField("cardNo");
				if (cardNo) {
					cardNo.on("change", this.onCardNoFilled, this);
					cardNo.on("keyup", function(field) {
								var value = field.getValue();
								if (value.trim().length == 0) {
									this.changeBtnStatus(false);
									return;
								}
								var startValue = field.startValue;
								if (value != startValue) {
									this.changeBtnStatus(true);
								}
							}, this);
				}

				var idCard = form.findField("idCard");
				if (idCard) {
					idCard.on("change", this.onIdCardBlur, this)
					idCard.on("change", this.onCheckBirth, this);
					idCard.on("keyup", function(field) {
								var value = field.getValue();
								if (value.trim().length == 0) {
									this.changeBtnStatus(false);
									return;
								}
								var startValue = field.startValue;
								if (value != startValue) {
									this.changeBtnStatus(true);
								}
							}, this);
				}

				var personName = form.findField("personName");
				if (personName) {
					personName.on("change", function(f) {
						this.onBaseInfoFilled();
							// this.recordChecked = false;
						}, this)
				}

				var sexCode = form.findField("sexCode");
				if (sexCode) {
					sexCode.on("change", function(f) {
						this.onBaseInfoFilled();
							// this.recordChecked = false;
						}, this)
				}

				if (birthday) {
					birthday.on("change", function(f) {
								this.onBaseInfoFilled();
								// this.recordChecked = false;
								this.getAgeFromServer(f);
							}, this)
				}

				var relativeIdCard = form.findField("relativeIdCard");
				if (relativeIdCard) {
					relativeIdCard
							.on("change", this.onRelativeIdCardBlur, this)
					relativeIdCard.on("keyup", function(field, e) {
						if (e.getKey() == e.ENTER) {
							return;
						}
						var value = field.getValue();
						if (value.trim().length == 0) {
							this.changeBtnStatus(false);
							return;
						}
							// var startValue = field.startValue;
							// if (value != startValue) {
							// this.changeBtnStatus(true);
							// }
						}, this);
					relativeIdCard.on("specialkey", function(f, e) {
								if (e.getKey() == e.ENTER) {
									var value = f.getValue();
									var startValue = f.startValue;
									if (value != startValue) {
										this.onRelativeIdCardBlur(f);
									}
								}
							}, this)
				}

				var relativeName = form.findField("relativeName");
				if (relativeName) {
					relativeName.on("keyup", this.setContact, this);
				}
				if ("tree" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
					
					var regionCode = form.findField("regionCode");
					if (regionCode) {
						regionCode.on("afterSelect", this.getManaDoctor, this);
						regionCode.on("select", this.getManaDoctor, this);
						regionCode.on("beforeselect", this.getFamilyRegion,
								this);
					}
				}
				
				if ("form" == this.mainApp.exContext.areaGridShowType) {
				if(this.ff){
				
				this.ff.on("select", this.getManaDoctor, this);
						this.ff.on("beforeselect", this.getFamilyRegion,
								this);
				}
				}
				var insuranceCode = form.findField("insuranceCode");
				if (insuranceCode) {
					insuranceCode.on("select", this.onInsuranceCode, this);
					insuranceCode.on("blur", this.onInsuranceCode, this)
				}

				var manaDoctorId = form.findField("manaDoctorId");
				if (manaDoctorId) {
					manaDoctorId.on("select", this.changeManaUnit, this);
				}

				var manaUnitId = form.findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.checkManaUnit, this);
					manaUnitId.on("expand", this.filterManaUnit, this);
					manaUnitId.on("beforeQuery", this.onQeforeQuery, this)
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
				if (key.length >= 11) {
					return true;
				} else {
					return false;
				}
			},

			onInsuranceCode : function(f) {
				var form = this.form.getForm();
				var insuranceType = form.findField("insuranceType");
				if ("99" == f.getValue()) {
					insuranceType.enable();
					insuranceType.focus(false, 1);
				} else {
					insuranceType.reset();
					insuranceType.disable();
				}
			},

			onSelectDate : function(field) {
				field.setMaxValue(this.mainApp.serverDate);
			},

			onCheckBirth : function(field) {
				var form = this.form.getForm();
				var birthDay = form.findField("birthday");
				var checkValue = this.getAgeFromServer(birthDay);
				if (!checkValue) {
					birthDay.enable();
				}
			},

			getAgeFromServer : function(field) {
				var birthday = field.getValue();
				if (!birthday) {
					return true;
				}
				if (!field.validate()) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "calculateAge",
							method : "execute",
							body : {
								birthday : birthday
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				var age = result.json.body.age
				var childrenDieAge = this.mainApp.exContext.childrenDieAge
				var childrenRegisterAge = this.mainApp.exContext.childrenRegisterAge
				if (!childrenDieAge || !childrenRegisterAge) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '请先配置儿童的建档年龄和死亡登记年龄',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;
				}
				if (this.isDeadRegist) {
					if (age > childrenDieAge) {
						field.setValue("");
						Ext.Msg.alert("提示信息", "该用户已经超过" + childrenDieAge
										+ "周岁,无法进行死亡登记!");
						return false;
					}
				} else {
					if (age > childrenRegisterAge) {
						field.setValue("");
						Ext.Msg.alert("提示信息", "该用户已经超过" + childrenRegisterAge
										+ "周岁,无法建立档案!");
						return false;
					}
				}
				return true;
			},

			getManaDoctor : function(field, node) {
				var value = field.getValue();
				if (value == null || value == "") {
					return;
				}
				var form = this.form.getForm();
				this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : 'chis.childrenHealthRecordService',
							serviceAction : "getManaDoctorInfo",
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
							var manaDocField = form.findField("manaDoctorId");
							var manaDoctor = json.body.manaDoctor;
							if (manaDoctor && manaDocField) {
								manaDocField.setValue(manaDoctor);
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

				if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
					combox.setValue(manageUnit)
					combox.disable();
				} else {
					combox.enable();
					combox.reset();
				}
			},

			getFamilyRegion : function(comb, node) {
				var isFamily = node.attributes['isFamily'];
				if (isFamily > 'b1' && isFamily > 'a2' || isFamily == '1') {
					return true;
				} else {
					return false;
				}
			},

			setContact : function(field) {
				var value = field.getValue();
				if (!value) {
					return;
				}
				this.form.getForm().findField("contact").setValue(value);
			},

			// 每次获取一次查询条件的快照与上次的保存的快照进行对比
			// 决定是否进行查询。
			getQueryInfoSnap : function() {
				var form = this.form.getForm();
				var snap = {};
				snap["cardNo"] = form.findField("cardNo").getValue();
				snap["idCard"] = form.findField("idCard").getValue();
				snap["personName"] = form.findField("personName").getValue();
				snap["sexCode"] = form.findField("sexCode").getValue();
				snap["birthday"] = form.findField("birthday").getValue();
				snap["relativeIdCard"] = form.findField("relativeIdCard")
						.getValue();
				return snap;
			},

			// 判断是否需要进行查询,EMPIInfoModule调用
			needsQuery : function() {
				if (this.op == "update") {
					return false;
				}

				var snap = this.getQueryInfoSnap();
				if (!this.snap) {
					return true;
				}
				if (snap["idCard"] != this.snap["idCard"]) {
					return true;
				}
				if (snap["cardNo"] != this.snap["cardNo"]) {
					return true;
				}
				if (snap["relativeIdCard"] != this.snap["relativeIdCard"]) {
					return true;
				}
				// 姓名为空的时候不做查询,该语句顺序不能调换
				if (!snap["personName"] || snap["personName"] == "") {
					return false;
				}

				if (snap["personName"] != this.snap["personName"]) {
					return true;
				}
				if ((snap["birthday"] + "") != (this.snap["birthday"] + "")) {
					return true
				}
				if (snap["sexCode"] != this.snap["sexCode"]) {
					return true;
				}

				return false;
			},

			onCardNoFilled : function(field) {
				if (!field.validate()) {
					return;
				}
				var birthdayField = this.form.getForm().findField("birthday");
				var checkBirth = this.getAgeFromServer(birthdayField);
				if (!checkBirth) {
					this.changeBtnStatus(false);
					return;
				}
				var value = field.getValue();
				if (value.trim().length == 0) {
					this.changeBtnStatus(false);
					return;
				}
				var queryData = {
					"cardNo" : value,
					"isDeadRegist" : this.isDeadRegist
				};
				this.queryServiceActioin = "getChildBaseInfoByCardNo";
				var body = this.queryData(queryData);
				var data;
				if (body) {
					data = body.data;
				}
				if (!data || data.length < 1) {
					var ageInvalidMsg;
					if (body) {
						ageInvalidMsg = body.ageInvalidMsg;
					}
					if (ageInvalidMsg) {
						Ext.Msg.alert("提示信息", ageInvalidMsg);
					}
					this.changeBtnStatus(false);
					return;
				} else if (data.length == 1) {
					var result = data[0];
					
					
					this.initFormData(result);
					if (!this.data["childEmpiId"]) {
						this.initDataId = null;
					}
					if (this.dataSource != "pix") {
						this.op = "update";
					}
				} else {
					this.showDataInSelectView(data);
				}
				this.changeBtnStatus(false);
			},

			onIdCardBlur : function(field) {

				var cardNo = field.getValue();
				cardNo = cardNo.replace(/(^\s*)|(\s*$)/g, "");
				field.setValue(cardNo);
				if (!field.isValid()) {
					return;
				}
				var cardNo = field.getValue();
				field.setValue(cardNo.toUpperCase());
				var birthdayField = this.form.getForm().findField("birthday");
				if (!cardNo || cardNo.trim().length == 0) {
					birthdayField.enable();
					this.changeBtnStatus(false);
					return;
				} else {
					birthdayField.disable();
				}
				var info = this.getInfo(cardNo);
				var sex = info[1];
				var birthday = info[0];
				if (birthday) {
					birthdayField.setValue(birthday);
					var isChild = this.getAgeFromServer(birthdayField);
					if (!isChild) {
						// 20131121新发现的问题
						birthdayField.enable();
						this.changeBtnStatus(false);
						return;
					}
				}
				if (sex) {
					var sexCodeField = this.form.getForm().findField("sexCode");
					if (sex == 1) {
						sexCodeField.setValue({
									key : sex,
									text : "男"
								});
					} else {
						sexCodeField.setValue({
									key : sex,
									text : "女"
								});
					}
					sexCodeField.disable();
				}
				this.queryServiceActioin = "getChildBaseInfoByIdCard"
				var queryData = {
					"idCard" : cardNo
				};
				var body = this.queryData(queryData);
				if (body && body.length > 0) {

					var initData = body[0];
					
					
					this.initFormData(initData);
					if (this.dataSource != "pix") {
						this.op = "update";
					}
					if (this.initDataId) {
						if (!this.data["childEmpiId"]) {
							this.initDataId = null;
						}
					}
				}
				this.changeBtnStatus(false);
			},

			onRelativeIdCardBlur : function(field) {
				var cardNo = field.getValue();
				cardNo = cardNo.replace(/(^\s*)|(\s*$)/g, "");
				field.setValue(cardNo);
				if (!field.validate()) {
					return;
				}
				var cardNo = field.getValue();
				if (!cardNo || cardNo.trim().length == 0) {
					this.changeBtnStatus(false);
					return;
				}
				field.setValue(cardNo.toUpperCase());
				
				var regionCode = this.form.getForm().findField("regionCode");
			
				var region = regionCode.getValue();
				var manaDoctorId = this.form.getForm()
						.findField("manaDoctorId");
				var manaDoctor = manaDoctorId.getValue();
				var recordExists = false;
				if (region && manaDoctor) {
					recordExists = true;
				}
				this.queryServiceActioin = "getChildBaseInfoByRelative"
				var queryData = {
					"idCard" : cardNo,
					"recordExists" : recordExists
				};
				var body = this.queryData(queryData);
				if (!body || body.length < 1) {
					this.changeBtnStatus(false);
					return;
				}
				var hasChild = body.hasChild || false;
				var data = body.relativeData[0];
				if (data) {
					if (!this.initDataId) {

						this.initFormData(data);
						this.initDataId = null;
					} else {
						this.initFormData(data);
					}
					if (this.dataSource != "pix") {
						this.op = "update";
					}
				}
				if (hasChild) {
					this.showDataInSelectView(body.childData);
				}
				this.changeBtnStatus(false);
			},
			onDoNew : function() {
				this.form.getForm().findField("idCard").enable()
			},

			onBaseInfoFilled : function() {
				var personName = this.form.getForm().findField("personName")
						.getValue();
				if (!personName || personName.length == 0) {
					return;
				}
				var sexCode = this.form.getForm().findField("sexCode")
						.getValue();
				if (!sexCode || sexCode.length == 0) {
					return;
				}
				var birthday = this.form.getForm().findField("birthday")
						.getValue().format('Y-m-d');
				if (!birthday || birthday.length == 0) {
					return;
				}
				var queryData = {
					"personName" : personName,
					"sexCode" : sexCode,
					"birthday" : birthday
				};
				this.queryServiceActioin = "getChildBaseInfoByIdCard";
				var body = this.queryData(queryData);
				if (body && body.length > 0) {
					this.showDataInSelectView(body)
				}
			},

			showDataInSelectView : function(data) {
				var empiIdSelectView = this.midiModules["empiIdSelectView"];
				if (!empiIdSelectView) {
					var empiIdSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : this.entryName,
								disablePagingTbr : true,
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择个人记录",
								width : 600,
								height : 400
							});
					empiIdSelectView.on("onSelect", function(r) {
								var data = r.data;
								this.doNew();
								this.initFormData(this.castListDataToForm(data,
										this.schema));
								if (this.dataSource != "pix") {
									this.op = "update";
								}
							}, this);
					this.midiModules["empiIdSelectView"] = empiIdSelectView;
					empiIdSelectView.initPanel();
				}
				empiIdSelectView.getWin().show();
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					var record = new Ext.data.Record(r);
					records.push(record);
				}
				empiIdSelectView.setRecords(records);
			},

			queryData : function(queryData) {
				if (!this.needsQuery()) {
					return;
				}

				if (this.op == "update") {
					return;
				}
				this.snap = this.getQueryInfoSnap();
				this.form.el.mask("正在查询,请稍后...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.queryServiceId,
							serviceAction : this.queryServiceActioin,
							method : "execute",
							body : queryData
						})
				this.form.el.unmask()
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				this.dataSource = result.json["dataSource"];
				return result.json["body"];
			},

			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					
					return;
				}

				if (this.needsQuery()) {
					return;
				}
				this.saveData = saveData;
				var empiId = saveData.empiId;
				var empiId = saveData.empiId;
				var cardNo = saveData.cardNo;
				if (!empiId && cardNo) {
					if (!this.cardChecked) {
						
						this.fireEvent("saveData");
					} else {
						
						Ext.Msg.show({
									title : '新卡提示',
									msg : '是否将新卡纳入管理?',
									buttons : Ext.Msg.YESNO,
									fn : function(btn, text) {
										if (btn == "yes") {
											this.showCardForm();
										} else if (btn == "no") {
											this.cardChecked = false;
										}
									},
									scope : this
								});
					}
				} else {
					
					this.fireEvent("saveData");
				}

			},

			showRecordInSelectView : function(data, formEmpi) {
				this.formEmpi = formEmpi;
				var healthSelectView = this.midiModules["healthSelectView"];
				if (!healthSelectView) {
					var healthSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : "EHR_HealthRecordList",
								disablePagingTbr : true,
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择个人健康档案记录",
								width : 860,
								height : 400
							});
					healthSelectView.on("onSelect", function(r) {
								var empiId = r.data.empiId;
								this.selectedEmpi = empiId;
								this.queryServiceActioin = "getChildBaseInfoByEmpiId";
								this.form.el.mask("正在查询,请稍后...",
										"x-mask-loading")
								util.rmi.jsonRequest({
											serviceId : this.queryServiceId,
											serviceAction : this.queryServiceActioin,
											method : "execute",
											body : {
												"empiId" : empiId
											}
										}, function(code, msg, json) {
											this.form.el.unmask()
											if (code > 300) {
												this
														.processReturnMsg(code,
																msg);
												return
											}
											var body = json.body
											if (body) {
												this.doNew();
												this.initFormData(body);
											}
										}, this)
							}, this);
					healthSelectView.on("hide", function() {
								if (!this.selectedEmpi) {
									this.selectedEmpi = this.formEmpi
								}
								healthSelectView.getWin().hide();
							}, this)
					this.midiModules["healthSelectView"] = healthSelectView;
					healthSelectView.initPanel();
				}
				var win = healthSelectView.getWin();
				win.setPosition(250, 100);
				win.show();
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					var record = new Ext.data.Record(r);
					records.push(record);
				}
				healthSelectView.setRecords(records);
			},

			showCardForm : function() {
				var cardNo = this.saveData.cardNo;
				this.cardNo = cardNo
				var createView = this.midiModules["cardForm"];
				if (!createView) {
					$import("chis.application.cdh.script.base.CardForm");
					createView = new chis.application.cdh.script.base.CardForm(
							{
								cardNo : cardNo,
								autoLoadData : false,
								autoLoadSchema : false,
								entryName : "chis.application.mpi.schemas.MPI_Card",
								isCombined : false,
								mainApp : this.mainApp,
								title : "新建卡"
							});
					this.midiModules["cardForm"] = createView;
					createView.on("saveCard", function(values) {
								if (values) {
									this.card = values;
									this.fireEvent("saveData");
								}
							}, this);
					createView.on("close", function() {
								this.cardChecked = false;
							}, this);
					// createView.on("winShow",this.onCardWinShow,this)

				}
				createView.cardNo = cardNo;
				var form = createView.initPanel();
				var win = createView.getWin();
				win.add(form)
				win.minimizable = false;
				win.maximizable = false;
				win.show();
			},
			onSaveData : function() {

				this.saveData["manaUnitId"] = this.data.manaUnitId;
				
				this.saveData["card"] = this.card;
				
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true
				
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							method : "execute",
							op : this.op,
							body : this.saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300 && code != 4000) {
							
								this.processReturnMsg(code, msg,
										this.saveToServer, [this.saveData]);
								return;
							}
							if (code == 4000) {
								
								Ext.Msg.alert("提示", msg, function() {
											Ext.apply(this.data, this.saveData);
											if (json.body) {
												var data = json.body;
												// this.initFormData(data);
												this.fireEvent("save",
														this.entryName,
														this.op, json, data)
											}
											this.op = "update"
											this.doCancel();
										}, this);
							} else {
								
								Ext.apply(this.data, this.saveData);
								if (json.body) {
									
									var data = json.body;
									
									// this.initFormData(data);
									this.fireEvent("save", this.entryName,
											this.op, json, data)
								}
								
								this.op = "update"
								this.doCancel();
							}
						}, this)
			},

			onWinShow : function() {
				this.initDataId = null;
				this.recordChecked = false;
				this.snap = {};
				this.op = "create";
				this.dataSource = "";
				this.selectedEmpi = null;
				this.cardChecked = true;
				this.card = {};
				this.data = {};
				this.saveData = {};
				if (this.form) {
					var form = this.form.getForm();
					var birthDay = form.findField("birthday");
					if (birthDay) {
						birthDay.enable();
					}
					var sexCode = form.findField("sexCode");
					if (sexCode) {
						sexCode.enable();
					}
					this.changeBtnStatus(false);
				}
				this.doNew();
			},

			onAfterPanelInit : function() {
				this.validate();
			},

			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 18)
					return "身份证号共有18位";
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai))
					return "身份证除最后一位外，必须为数字！";
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy))
					return "身份证输入错误！";
				for (var i = 0, ret = 0; i < 17; i++)
					ret += Ai.charAt(i) * Wi[i];
				Ai += arrVerifyCode[ret %= 11];
				return pId.length == 18 && pId.toLowerCase() != Ai
						? "身份证输入错误！"
						: Ai;
			},

			// 判断时间是否合法
			isValidDate : function(day, month, year) {
				if (month == 2) {
					var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
					if (day > 29 || (day == 29 && !leap)) {
						return false;
					}
				}
				return true;
			},

			getInfo : function(id) {
				// 根据身份证取 省份,生日，性别
				id = this.checkIdcard(id)
				var fid = id.substring(0, 16), lid = id.substring(17);
				if (isNaN(fid) || (isNaN(lid) && (lid != "x")))
					return []
				var id = String(id), sex = id.slice(14, 17) % 2 ? "1" : "2"
				var birthday = new Date(id.slice(6, 10), id.slice(10, 12) - 1,
						id.slice(12, 14))
				return [birthday, sex]
			},

			initFormData : function(data) {

				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v) {
							f.setValue(v)
						}
						if (this.data["childEmpiId"] || this.data["phrId"]) {
							if (it.update == false || it.update == "false") {
								f.disable();
							}
						}
					}
				}
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode.text);
							
					this.data.regionCode = data.regionCode.key;
					this.data.regionCode_text = data.regionCode.text;

				}
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode.text);
					this.form.getForm().findField("regionCode")
					.selectData.regionCode=data.regionCode.key;	
					this.data.regionCode = data.regionCode.key;
					this.data.regionCode_text = data.regionCode.text;

				}
				this.setKeyReadOnly(true)
				// 设置出生证编号权限
				var certificateNo = form.findField("certificateNo");
				if (certificateNo) {
					if (this.data["certificateNo"]
							&& this.data["certificateNo"] != ""
							&& this.data["existCno"]) {
						certificateNo.disable()
					} else {
						certificateNo.enable();
					}
				}

				var insuranceCode = this.data["insuranceCode"];
				if (insuranceCode) {
					var f = form.findField("insuranceType");
					if (f) {
						if (insuranceCode.key == "99") {
							f.enable();
						} else {
							f.reset();
							f.disable();
						}
					}
				}

				var idCard = this.data["idCard"];
				var idCardField = form.findField("idCard");
				var birthdayField = form.findField("birthday");
				var sexCodeField = form.findField("sexCode");
				if (idCard) {
					if (idCardField) {
						idCardField.disable();
					}
					if (birthdayField) {
						birthdayField.disable();
						sexCodeField.disable();
					}
				} else {
					if (idCardField) {
						idCardField.enable();
					}
					if (birthdayField) {
						birthdayField.enable();
						sexCodeField.enable();
					}
				}
			},

			changeBtnStatus : function(stats) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var btns = this.form.getTopToolbar().items;
				var btn;
				if (btns) {
					btn = btns.item(0);
				}
				if (stats) {
					btn.disable();
				} else {
					btn.enable();
				}
			},

			doSave : function() {

				if (this.saving) {
					return
				}
				
				var values = this.getFormData();
				
				if (!values) {
					return;
				}

				Ext.apply(this.data, values);

				this.saveToServer(values)
				
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
							if(it.id=='regionCode')
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

						if ("form" == this.mainApp.exContext.areaGridShowType) {
							if ("regionCode_text" == it.id) {

								v = this.data.regionCode_text;

							}
							if ("regionCode" == it.id) {

								v = this.data.regionCode;
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

		});