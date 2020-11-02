/**
 * 家庭基本信息表单
 * 
 * @author tianj
 */
$package("chis.application.fhr.script");

$import("chis.script.BizTableFormView");

chis.application.fhr.script.FamilyRecordForm1 = function(cfg) {
	cfg.width = 830;
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 180;// 212
	chis.application.fhr.script.FamilyRecordForm1.superclass.constructor.apply(
			this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("doNew", this.onDoNew, this);
	this.on("loadData", this.onLoadData, this);
	this.on("afterSave", this.onAfterSave, this);
	this.ff = "family";// 标识，区分全部网格化和部分网格化
	this.dd1 = {};// 全局变量

}

Ext.extend(chis.application.fhr.script.FamilyRecordForm1,
		chis.script.BizTableFormView, {
			// this.initFormData(chuanDiValue);
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
					if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 112))// 112
								* colCount

					} else {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
								* colCount
					}
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				// console.log(items)
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					if ("tree" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "regionCode_text") {
							// 把id为“”regionCode_text屏蔽掉,由于schema没有 配置displays属性
							continue;
						}
					}
					if (it.id == "regionCode") {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							it.otherConfig={
									'not-null':'true',
									colspan:2,
									width:429,
									allowBlank:false,
									disabled:true,
									invalidText:"必填字段"
							};
							it.filterType='falmily';
							//it.afterSelect=this.afterSelect;
							var areaGrid=this.createAreaGridField(it);
							this.fff = areaGrid;
							table.items.push(areaGrid)
							continue;
						}
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "regionCode") {
							// 把id为“”regioncode屏蔽掉,由于schema没有 配置displays属性
							continue;
						}

					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "regionCode_text") {

							var _ctr = this;
							var fff = new Ext.form.TriggerField({
								name : it.id,
								colspan : 2,
								onTriggerClick : function() {
									_ctr.onRegionCodeClick()
								}, // 单击事件
								triggerClass : 'x-form-search-trigger', // 按钮样式
								// readOnly : true, //只读
								// disabled : true,
								fieldLabel : "<font  color=red>网格地址:<font>",
								"width" : 429
									// 362

								});
							this.fff = fff;
							this.fff.allowBlank = false;
							this.fff.invalidText = "必填字段";
							this.fff.regex = /(^\S+)/;
							this.fff.regexText = "前面不能有空格字符";
							table.items.push(fff)
							continue;

						}

					}
					var f = this.createField(it)
					f.index = i;
					if ("form" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						f.anchor = it.anchor || "90%"
					} else {
						f.anchor = it.anchor || "100%"
					}

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
				if ("part" == this.mainApp.exContext.areaGridType) {

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
				this.form.getForm().findField("regionCode_text")
						.setValue(data.regionCode_text);
				this.data.regionCode = data.regionCode;
			},
			initFormData : function(data) {
				if ("tree" == this.mainApp.exContext.areaGridShowType) {

					if (data["regionCode"]["key"]) {
						// 添加家庭档案的情况
						data["regionCode_text"] = data["regionCode"]["text"]

					} else {
						// 查看的情况
						var r = {};
						r["key"] = data["regionCode"];
						r["text"] = data["regionCode_text"];
						delete data["regionCode"];
						data["regionCode_text"];
						data["regionCode"] = r;

					}

				} 
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode_text")
							.setValue(data.regionCode_text);
					this.form.getForm().findField("regionCode_text").disable();
					this.data.regionCode = data.regionCode;

				}
				chis.application.fhr.script.FamilyRecordForm1.superclass.initFormData
						.call(this, data);
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode")
					.setValue(data.regionCode_text);
					this.form.getForm().findField("regionCode").disable();
					this.data.regionCode = data.regionCode;
					this.data.regionCode_text = data.regionCode_text;
					
				}

			},

			doNew : function() {
				this.initDataId = this.exContext.args.initDataId;
				chis.application.fhr.script.FamilyRecordForm1.superclass.doNew
						.call(this);

			},
			// 户主姓名是可自填写的字典
			createField : function(it) {
				this.creatingField = it.id;
				return chis.application.fhr.script.FamilyRecordForm1.superclass.createField
						.apply(this, [it]);
			},
			// 户主姓名是可自填写的字典
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render;
				}
				cls += "DicFactory";

				$import(cls);
				var factory = eval("(" + cls + ")");
				if (this.creatingField == "ownerName") {
					return factory.createMixDic(dic);
				}
				var field = factory.createDic(dic);
				return field;
			},// 重写父类的方法：打开一个页面定位光标的位置
			focusFieldAfter : function() {

			},

			onDoNew : function() {
				if (this.data) {
					this.data = {};
				}
				var form = this.form.getForm();
				var manaUnitId = form.findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.setValue(null);
				}

				var familyId = form.findField("familyId");
				if (familyId) {
					familyId.disable();
				}

				var ownerName = form.findField("ownerName");
				if (ownerName) {

					ownerName.enable();
				}
				if (this.op == "update") {
					return;
				}
				var regionCode_text = form.findField("regionCode_text");
				if (regionCode_text) {

					// regionCode.enable();
					regionCode_text.disable();// 防止用户操作这个控件
				}
				var manaDoctorId = this.form.getForm()
						.findField("manaDoctorId");
				if (manaDoctorId) {
					// manaDoctorId.enable();
					manaDoctorId.disable();// 防止用户操作这个控件
				}
				// 删除已加载姓名。
				var ownerName = this.form.getForm().findField("ownerName");
				var ownerNameTree = ownerName.tree;
				if (ownerNameTree) {
					var root = ownerNameTree.getRootNode();
					var children = root.childNodes;
					root.childNodes = []
					for (var i = 0; i < children.length; i++) {
						root.removeChild(children[i])
					}
				}
			},

			onBeforeSave : function(entryName, op, saveData) {
				if (op == "update") {
					saveData["lastModifyUser"] = this.mainApp.uid;
					saveData["lastModifyUnit"] = this.mainApp.deptId;
					saveData["lastModifyDate"] = this.mainApp.serverDate;
				} else {
					saveData["status"] = "0";
				}
			},

			onReady : function() {
				chis.application.fhr.script.FamilyRecordForm1.superclass.onReady
						.call(this);

				var manaDoctorId = this.form.getForm()
						.findField("manaDoctorId");
				if (manaDoctorId) {
					manaDoctorId.on("select", this.changeManaUnit, this);
				}

				var regionCode = this.form.getForm().findField("regionCode");
				if (regionCode) {
					regionCode.on("select", this.getFamilyMess, this);
					regionCode.on("beforeselect", this.getFamilyRegion, this);
				}

				var manaUnitId = this.form.getForm().findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.checkManaUnit, this);
					manaUnitId.on("expand", this.filterManaUnit, this);
					manaUnitId.on("beforeQuery", this.onQeforeQuery, this)
				}
				var ownerName = this.form.getForm().findField("ownerName");
				ownerName.on("blur", function() {

							var v = this.form.getForm().findField("ownerName")
									.getRawValue();
							ownerName.setValue(v);
							ownerName.setRawValue(v);
						}, this)

				var vvv = this.form.getForm().findField("ownerName");
				vvv.on("focus", function() {
							// 点击文本框，创建个人信息页面
							var m = this.createCombinedModule("grjbxx2",
									"chis.application.hr.HR/HR/B34101")
							m.on("save1", this.onSave1, this)
							m.on("chuanDi", this.chuanDi, this)

							m.on("imot1", this.saveo, this)
							// this.fireEvent("imot");

							m.op = "create";

							m.id = "familyc";
							m.id1 = "familyc"// 防止与xml的item的id重复
							var familyId = this.initDataId;
							var win = m.getWin();
							win.add(m.initPanel())
							if (familyId) {
								var data = {};
								data["familyId"] = familyId;

								m.masterflag1 = true;
								m.data2 = data;
								m.loadData(data);
							}
							win.setPosition(289, 65);
							win.show();
							m.doNew();
							m.disableMasterFlag();
						}, this)
				// 防止打开这个页面，光标定位
				this.focusFieldAfter();
			}, // 将传递过来的信息，初始化到表单上
			chuanDi : function(value) {

				// this.doNew();
				this.dd1["empiId"] = value["empiId"]

				// 燃料类型
				var chuanDiValue = {}
				chuanDiValue["familyId"] = value["familyId"];
				if (value["shhjCheckRLLX"]) {
					var r = value["shhjCheckRLLX"];
					if (r == 1) {
						chuanDiValue["fuelType"] = {
							"key" : "1",
							"text" : "液化气"
						};
					}
					if (r == 2) {
						chuanDiValue["fuelType"] = {
							"key" : "2",
							"text" : "煤"
						};
					}
					if (r == 3) {
						chuanDiValue["fuelType"] = {
							"key" : "3",
							"text" : "天然气"
						};
					}
					if (r == 4) {
						chuanDiValue["fuelType"] = {
							"key" : "4",
							"text" : "沼气"
						};
					}
					if (r == 5) {
						chuanDiValue["fuelType"] = {
							"key" : "5",
							"text" : "柴火"
						};
					}
					if (r == 9) {
						chuanDiValue["fuelType"] = {
							"key" : "9",
							"text" : "其他"
						};
					}

				}

				if (value["personName"]) {
					chuanDiValue["ownerName"] = value["personName"];
				}

				if (value["manaDoctorId"]) {
					chuanDiValue["manaDoctorId"] = value["manaDoctorId"];

				}

				if (value["regionCode"]
						&& value["regionCode_text"]) {
					var l = {}
					l["key"] = value["regionCodeCode"];
					l["text"] = value["regionCode"];
					chuanDiValue["regionCode"] = l;
				}

				if (value["manaUnitId"]) {
					chuanDiValue["manaUnitId"] = value["manaUnitId"];
				}
				// 厨房排风设施
				if (value["shhjCheckCFPFSS"]) {
					var r = value["shhjCheckCFPFSS"];
					if (r == 2) {
						chuanDiValue["cookAirTool"] = {
							"key" : "2",
							"text" : "油烟机"
						};
					}
					if (r == 3) {
						chuanDiValue["cookAirTool"] = {
							"key" : "3",
							"text" : "换气扇"
						};
					}
					if (r == 4) {
						chuanDiValue["cookAirTool"] = {
							"key" : "4",
							"text" : "烟囱"
						};
					}
					if (r == 9) {
						chuanDiValue["cookAirTool"] = {
							"key" : "9",
							"text" : "其他"
						};
					}

				}

				// 饮水类型
				if (value["shhjCheckYS"]) {
					var r = value["shhjCheckYS"];
					if (r == 1) {
						chuanDiValue["waterSourceCode"] = {
							"key" : "1",
							"text" : "自来水"
						};
					}
					if (r == 2) {
						chuanDiValue["waterSourceCode"] = {
							"key" : "2",
							"text" : "经净化过滤的水"
						};
					}
					if (r == 3) {
						chuanDiValue["waterSourceCode"] = {
							"key" : "3",
							"text" : "井水"
						};
					}
					if (r == 4) {
						chuanDiValue["waterSourceCode"] = {
							"key" : "4",
							"text" : "河湖水"
						};
					}
					if (r == 5) {
						chuanDiValue["waterSourceCode"] = {
							"key" : "5",
							"text" : "塘水"
						};
					}
					if (r == 9) {
						chuanDiValue["waterSourceCode"] = {
							"key" : "9",
							"text" : "其他"
						};
					}
				}

				// 厕所类别
				if (value["shhjCheckCS"]) {
					var r = value["shhjCheckCS"];
					if (r == 1) {
						chuanDiValue["washroom"] = {
							"key" : "1",
							"text" : "卫生厕所"
						};
					}
					if (r == 2) {
						chuanDiValue["washroom"] = {
							"key" : "2",
							"text" : "一格或二格粪池式"
						};
					}
					if (r == 3) {
						chuanDiValue["washroom"] = {
							"key" : "3",
							"text" : "马桶"
						};
					}
					if (r == 4) {
						chuanDiValue["washroom"] = {
							"key" : "4",
							"text" : "露天粪坑"
						};
					}
					if (r == 5) {
						chuanDiValue["washroom"] = {
							"key" : "5",
							"text" : "简易棚厕"
						};
					}
					if (r == 9) {
						chuanDiValue["washroom"] = {
							"key" : "9",
							"text" : "其他"
						};
					}
				}

				// 禽畜栏
				if (value["shhjCheckQCL"]) {
					var r = value["shhjCheckQCL"];
					if (r == 1) {
						chuanDiValue["livestockColumn"] = {
							"key" : "1",
							"text" : "单设"
						};
					}
					if (r == 2) {
						chuanDiValue["livestockColumn"] = {
							"key" : "2",
							"text" : "室内"
						};
					}
					if (r == 3) {
						chuanDiValue["livestockColumn"] = {
							"key" : "3",
							"text" : "室外"
						};
					}

				}

				// 建档单位 建档人 建档时间

				var ml = {}
				ml["key"] = this.mainApp.deptId;
				ml["text"] = this.mainApp.dept;
				chuanDiValue["createUnit"] = ml;
				chuanDiValue["createUser"] = this.mainApp.uname;
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					// 列表形式的网格地址，不需要拼成key：text键值对
					var regionCode1;
					var regionCode_text1;
					if(chuanDiValue.regionCode){
						regionCode1 = chuanDiValue.regionCode.key;
						regionCode_text1 = chuanDiValue.regionCode.text;
					}
					delete chuanDiValue["regionCode"];

					chuanDiValue["regionCode"] = regionCode1;
					chuanDiValue["regionCode_text"] = regionCode_text1;
				}

				// alert(Ext.encode(chuanDiValue))
               
				this.initFormData(chuanDiValue);

			},
			fun1 : function(flagEmpty) {

				if (flagEmpty) {

					this.form.getForm().findField("ownerName").setValue("");
					this.form.getForm().findField("ownerName").enable();// 2014.9.26改

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

			checkManaUnit : function(comb, node) {
				var key = node.attributes['key'];
				if (key.length >= 11) {
					return true;
				} else {
					return false;
				}
			},

			getFamilyRegion : function(comb, node) {
				var key = node.attributes['isFamily'];
				if (key == "1") {
					return true;
				} else {
					return false;
				}
			},

			onLoadData : function(entryName, body) {

				var f = this.form.getForm().findField("ownerName");
				// var r=body["familyId"];

				if (body.ownerName.key != null) {

					f.disable();
				} else {

					f.enable();
				}

				var ownerName = body["ownerName"];
				f.setValue(ownerName.key);
				f.setRawValue(ownerName.key);
			},

			getFamilyMess : function(field, node) {
				var value = field.getValue();
				var text = field.getRawValue();
				var form = this.form.getForm();
				this.regionCode_value = text;// 给网格地址 文本赋值
				form.findField("regionCode").setValue(value);
				if("pycode" == this.mainApp.exContext.areaGridShowType)
				{
					value=field.getAreaCodeValue()
				}
				if (!value) {
					return;
				}
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "loadRegionCodeRelatedRecord",
							method : "execute",
							schema : this.entryName,
							body : {
								regionCode : value,
								op : this.op
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (this.op == 'create') {
								if (json.body.familyRecord) {
									Ext.Msg.alert("提示信息",
											"该网格地址上的家庭档案已经存在,不能重复新建!");
									this.exContext.args.initDataId = json.body.familyRecord.familyId;
									this.loadData();
									this.fireEvent("loadFamilyRecord",
											json.body.familyRecord);
								}
							}

							var ownerName = form.findField("ownerName");
							ownerName.setValue();
							if (ownerName && ownerName.disabled) {
								ownerName.reset();
								ownerName.enable();
							}
							var familyAddr = form.findField("familyAddr");
							var familyHome = form.findField("familyHome");
							var ownerName = this.form.getForm()
									.findField("ownerName");

							var names = ownerName.tree.getRootNode().childNodes;
							for (var i = 0; i < names.length;) {
								names[0].remove();
							}

							if (json.body.masterRecord) {
								var familyId = json.body.masterRecord.familyId;
								if (familyId) {
									this.exContext.args.initDataId = familyId;
									this.op = "update";
									form.findField("familyId")
											.setValue(familyId);
									this.fireEvent("save", this.entryName,
											this.op, null,
											json.body.masterRecord);
								}
								if (json.body.empiInfo) {
									var address = json.body.empiInfo.address;
									var phoneNumber = json.body.empiInfo.phoneNumber;
									var name = json.body.empiInfo.personName

									if (name && ownerName) {
										ownerName.setValue(name);
										ownerName.disable();
									}

									if (address && familyAddr) {
										familyAddr.setValue(address);
									} else {
										familyAddr.setValue(text);
									}

									if (phoneNumber && familyHome) {
										familyHome.setValue(phoneNumber);
									}
								}
							} else {
								familyAddr.setValue(text);
								var member = json.body.member
								if (member) {
									var root = ownerName.tree.getRootNode();
									for (var i = 0; i < member.length; i++) {
										var node = new Ext.tree.TreeNode({});
										node.attributes["key"] = member[i].personName;
										node.attributes["text"] = member[i].personName;
										node.text = member[i].personName;
										root.appendChild(node);
									}
									if (member.length > 0) {
										ownerName.focus()
										ownerName.expand();
									}
								}
							}

							var manaDocField = form.findField("manaDoctorId");
							var manaDoctor = json.body.manaDoctor;
							if (manaDoctor && manaDocField) {
								manaDocField.setValue(manaDoctor);
								if (!json.body.manageUnits) {
									return;
								}
								var manageUnits = json.body.manageUnits;
								if (manageUnits.length == 1) {
									this.setManaUnit(manageUnits[0]);
								} else {
									// 该责任医生可能归属的管辖机构编码集合
									this.manageUnits = manageUnits
									this.setManaUnit(null);
								}
							}

						}, this)
			},
			doSave : function() {

				// 保存之后，将原来的个人家庭档案的信息status 设置为1，不要显示出来
				if (this.saving) {
					return
				}
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);

				// delete values["regionCode"];删除某个key

				this.saveToServer(values);
			},
			// 更新家庭档案的状态
			updateStatus : function(data) {
				if (!data) {
					return;
				}

				if (!this.fireEvent("beforeLoadData", this.entryName)) {

					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.basicPersonalInformationService",
							serviceAction : "updateStatus",
							body : data,
							op : this.op,
							method : "execute"
						}, function(code, msg, json) {

							this.form.el.unmask();

							this.saving = false;

							var resBody = json.body;
							if (code > 300) {

								this.processReturnMsg(code, msg);
								this.fireEvent("exception", code, msg, resBody);
								return;
							}
							// Ext.apply(this.data, resBody);

							if (resBody) {

								// this.initFormData(resBody);
							}
							//						
						}, this)

			},
			saveo : function() {
				this.fireEvent("imot2");
			},
			saveToServer11 : function(saveData) {

				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (!this.initDataId) {
					this.op = "create";
				} else {
					this.op = "update";
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				var saveRequest = this.getSaveRequest(saveData);
				var saveCfg = {
					serviceId : this.saveServiceId,
					method : this.saveMethod,
					op : this.op,
					schema : this.entryName,
					module : this._mId, // 增加module的id
					body : saveRequest
				}
				this.fixSaveCfg(saveCfg);
				util.rmi.jsonRequest(saveCfg, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								this
										.fireEvent("exception", code, msg,
												saveData); // **进行异常处理
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {

								this.initFormData(json.body);
							}
							this.fireEvent("save", this.entryName, this.op,
									json, this.data);
							this.afterSaveData(this.entryName, this.op, json,
									this.data);
							this.op = "update"
						}, this)// jsonRequest
			},
			onSave1 : function(entryName, op, json, data) {

				this.fireEvent("save3", this.entryName, op, json,
						this.data);

			},
			loadData : function() {

				if (this.loadDataByDefaultValue) {
					this.doNew();
				} else {
					this.doNew(1);
				}
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				var loadRequest = this.getLoadRequest();
				if (!this.initDataId && !this.initDataBody && !loadRequest) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId || this.initDataBody, loadRequest)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				var loadCfg = {
					serviceId : this.loadServiceId,
					method : this.loadMethod,
					schema : this.entryName,
					pkey : this.initDataId || this.initDataBody,
					body : loadRequest,
					action : this.op, // 按钮事件
					module : this._mId
					// 增加module的id
				}
				this.fixLoadCfg(loadCfg);
				util.rmi.jsonRequest(loadCfg, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								this.fireEvent("exception", code, msg,
										loadRequest, this.initDataId
												|| this.initDataBody); // **
																		// 用于一些异常处理
								return
							}
							if (json.body) {
								this.initFormData(json.body)
								if ("tree" == this.mainApp.exContext.areaGridShowType) {
									// this.regionCode.disable();
								} else {

									this.fff.disable();

								}
								this.fireEvent("loadData", this.entryName,
										json.body);
							} else {
								this.initDataId = null;
								// **
								// 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
								this.fireEvent("loadNoData", this);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)
			},
			loadData : function() {

				if (this.loadDataByDefaultValue) {
					this.doNew();
				} else {
					this.doNew(1);
				}
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				var loadRequest = this.getLoadRequest();
				if (!this.initDataId && !this.initDataBody && !loadRequest) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId || this.initDataBody, loadRequest)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				var loadCfg = {
					serviceId : this.loadServiceId,
					method : this.loadMethod,
					schema : this.entryName,
					pkey : this.initDataId || this.initDataBody,
					body : loadRequest,
					action : this.op, // 按钮事件
					module : this._mId
					// 增加module的id
				}
				this.fixLoadCfg(loadCfg);
				util.rmi.jsonRequest(loadCfg, function(code, msg, json) {

							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								this.fireEvent("exception", code, msg,
										loadRequest, this.initDataId
												|| this.initDataBody); // **
								// 用于一些异常处理
								return
							}

							if (json.body) {

								this.initFormData(json.body)
								if ("tree" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
									// this.regionCode.disable();
								} else {
									this.fff.disable();
								}
								if (this.op == 'create') {
									this.op = "update"
								}
								this.fireEvent("loadData", this.entryName,
										json.body);
							} else {

								this.initDataId = null;
								// **
								// 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
								this.fireEvent("loadNoData", this);
							}

							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)
			}

		});