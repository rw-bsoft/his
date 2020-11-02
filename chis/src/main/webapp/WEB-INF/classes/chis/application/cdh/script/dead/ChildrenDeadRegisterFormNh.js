/**
 * 儿童死亡登记表单页面(未建档)
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.dead")
$import("chis.script.BizTableFormView", "chis.script.util.helper.Helper")
$import("chis.script.ICCardField", "util.widgets.LookUpField",
		"chis.script.util.Vtype")
chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.labelAlign = "left";
	cfg.width = 780;
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 176
	cfg.autoLoadData = false
	chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh.superclass.constructor
			.apply(this, [cfg])
	this.on("save", this.onSave, this)
	this.on("loadData", this.onLoadData, this);
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh,
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
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>户籍地址:<font>",
										"width" : 378

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
				this.form.getForm().findField("homeAddress")
						.setValue(data.regionCode_text);
				this.data.homeAddress_text = data.regionCode_text;
				this.data.homeAddress = data.regionCode;

			},
			initFormData : function(data) {
				chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if (data.homeAddress) {
						if (data.homeAddress.key) {

							this.form.getForm().findField("homeAddress")
									.setValue(data.homeAddress.text);
							this.form.getForm().findField("homeAddress")
									.disable();
							this.data.homeAddress = data.homeAddress.key;
							this.data.homeAddress_text = data.homeAddress.text;
						}
					}
				}
			},
			onSave : function() {
				this.doCancel();
			},

			doSave : function() {
				if (this.recordExists) {
					Ext.MessageBox.alert("提示", "该身份证已经做过死亡登记！");
					return false;
				}
				if (this.exist) {
					Ext.MessageBox.alert("提示", "死亡登记编号重复，不允许保存！")
					return;
				}
				if (this.invalidateAge) {
					Ext.MessageBox.alert("提示", "死亡日期必须大于等于出生日期.");
					return false;
				}
				chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh.superclass.doSave
						.call(this)
			},

			onReady : function() {
				chis.application.cdh.script.dead.ChildrenDeadRegisterFormNh.superclass.onReady
						.call(this)
				var form = this.form.getForm();

				var deadNo = form.findField("deadNo");
				if (deadNo) {
					deadNo.on("change", this.checkDeadNo, this);
				}

				var treatment = form.findField("treatment");
				if (treatment) {
					treatment.on("select", this.setNoTreatment, this);
				}

				var noTreatReason = form.findField("noTreatmentReason");
				if (noTreatReason) {
					noTreatReason.on("select", this.setOtherReason, this);
				}

				var diagnoseLevel = form.findField("diagnoseLevel");
				if (diagnoseLevel) {
					diagnoseLevel.on("select", this.setNoTreatment, this);
				}

				var deathDate = form.findField("deathDate");
				if (deathDate) {
					deathDate.on("valid", this.getDeadYear, this);
				}

				var birthday = form.findField("birthday");
				if (birthday) {
					birthday.on("blur", function(field) {
								var isChild = this.getAgeFromServer(field);
								if (!isChild) {
									return;
								}
								this.getDeadYear();
							}, this);
				}

				var idCard = form.findField("idCard");
				if (idCard) {
					idCard.on("change", this.onIdCardBlur, this);
				}

				var childRegister = form.findField("childRegister");
				if (childRegister) {
					childRegister.on("change", function(field) {
								var value = field.getValue();
								this.OnChangeRegister(value);
							}, this);
				}

				var homeAddress = form.findField("homeAddress");
				if (homeAddress) {
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
				if (key.length >= 9) {
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

			onIdCardBlur : function(field) {
				if (!field.isValid()) {
					return;
				}
				var idCard = field.getValue();
				field.setValue(idCard.toUpperCase());

				var birthdayField = this.form.getForm().findField("birthday");
				if (!idCard || idCard.trim().length == 0) {
					birthdayField.enable();
					return;
				} else {
					birthdayField.disable();
				}
				var info = this.getInfo(idCard);
				var sex = info[1];
				var birthday = info[0];
				if (birthday) {
					birthdayField.setValue(birthday);
					var isChild = this.getAgeFromServer(birthdayField);
					if (!isChild) {
						return;
					}
				}
				if (sex) {
					var sexCodeField = this.form.getForm()
							.findField("childSex");
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
				this.queryServiceId = "chis.childrenHealthRecordService";
				this.queryServiceActioin = "checkDeadRecordExistsByIdCard"
				var body = this.queryData({
							"idCard" : idCard
						}, false);
				if (body) {
					if (body["recordExists"] == true) {
						this.recordExists = true
						Ext.MessageBox.alert("提示", "该身份证已经做过死亡登记！");
					} else {
						this.recordExists = false
					}
				}
			},

			queryData : function(queryData, recordExists) {
				this.form.el.mask("正在查询,请稍后...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.queryServiceId,
							serviceAction : this.queryServiceActioin,
							method : "execute",
							body : queryData,
							recordExists : recordExists
						})
				this.form.el.unmask()
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				return result.json["body"];
			},

			OnChangeRegister : function(n) {
				var address = this.form.getForm().findField("homeAddress")
				var oneYearLive = this.form.getForm().findField("oneYearLive");
				address.reset()
				if (address) {
					if (n == "1") {
						Ext
								.getCmp(address.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'> 户籍地址:</span>");
						oneYearLive.reset();
						oneYearLive.allowBlank = true;
						oneYearLive.disable();

					} else {
						Ext
								.getCmp(address.id)
								.getEl()
								.up('.x-form-item')
								.child('.x-form-item-label')
								.update("<span style='color:red'> 暂住证地址:</span>");
						oneYearLive.allowBlank = false;
						oneYearLive.enable();
					}
				}
				this.validate();
			},

			setOtherReason : function(field) {
				var value = field.value
				var disable = true;
				if (value == "6") {
					disable = false;
				}
				this.changeFieldState(disable, "noTreatOtherReason");
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

			checkDeadNo : function(f) {
				var value = f.getValue();
				if (!value || value == "") {
					return;
				}
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "checkDeadNo",
							method : "execute",
							body : {
								deadNo : value,
								empiId : this.empiId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (code == 200) {
								var resBody = json.body;
								if (json.body) {
									this.exist = resBody.isRepeat;
									if (this.exist) {
										Ext.MessageBox.alert("提示信息",
												"死亡登记编号重复!", function() {
													f.focus(true, true);
												}, this);
									}
								}
							}
						}, this)
			},

			setNoTreatment : function(field) {
				var value = field.value
				var name = field.name
				var disable = true;
				if (name == "treatment")
					if (value == "3") {
						disable = false;
					} else {
						var diagnose = this.form.getForm()
								.findField("diagnoseLevel");
						if (diagnose) {
							var diaValue = diagnose.value;
							if (diaValue == "06") {
								disable = false;
							}
						}
					}
				else if (name == "diagnoseLevel")
					if (value == "06") {
						disable = false;
					} else {
						var treatment = this.form.getForm()
								.findField("treatment");
						if (treatment) {
							var treatValue = treatment.value;
							if (treatValue == "3") {
								disable = false;
							}
						}
					}
				this.changeFieldState(disable, "noTreatmentReason");
			},

			getDeadYear : function() {
				var form = this.form.getForm();
				var birthday = form.findField("birthday").getValue();
				var deadDate = form.findField("deathDate").getValue();
				if (birthday && deadDate) {
					var diffDate = (deadDate.getTime() - birthday.getTime())
							/ (24 * 60 * 60 * 1000);
					if (diffDate < 0) {
						this.invalidateAge = true
						Ext.MessageBox.alert("提示", "死亡日期必须大于等于出生日期.")
						return
					}
					this.invalidateAge = false
					var diffTime = chis.script.util.helper.Helper
							.getAgeBetween(birthday, deadDate);
					var deathYear = form.findField("deathYear");
					deathYear.setValue(diffTime);
				}
			},

			onLoadData : function(entryName, body) {

				var treatment = body["treatment"]
				if (treatment) {
					var disable = true;
					if (treatment.key == "3") {
						disable = false;
					} else {
						var diag = body["diagnoseLevel"]
						if (diag)
							if (diag.key == "5") {
								disable = false;
							}
					}
					this.changeFieldState(disable, "noTreatmentReason");
				}

				var diagnoseLevel = body["diagnoseLevel"]
				if (diagnoseLevel) {
					var disable = true;
					if (diagnoseLevel.key == "5") {
						disable = false;
					} else {
						var treat = body["treatment"]
						if (treat)
							if (treat.key == "3") {
								disable = false;
							}
					}
					this.changeFieldState(disable, "noTreatmentReason");
				}

				var noTreatmentReason = body["noTreatmentReason"]
				if (noTreatmentReason) {
					var disable = true;
					if (noTreatmentReason.key == "6") {
						disable = false;
					}
					this.changeFieldState(disable, "noTreatOtherReason");
				}

				var childRegister = body.childRegister;
				this.OnChangeRegister(childRegister);
			},

			onWinShow : function() {
				this.win.doLayout();
				this.invalidateAge = false;
				this.recordExists = false;
				this.exist = false;
				this.initDataId = null
				this.doNew();
				if (this.form) {
					this.OnChangeRegister("1");
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
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				var age = result.json.body.age;
				var childrenDieAge = this.mainApp.exContext.childrenDieAge
				if (!childrenDieAge) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '请先配置儿童死亡登记年龄',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;
				}
				if (age > childrenDieAge) {
					field.setValue("");
					Ext.Msg.alert("提示信息", "该用户已经年满" + childrenDieAge
									+ "周岁,无法进行死亡登记!");
					return false;
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