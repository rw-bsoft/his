/**
 * 家庭基本信息表单 该类为解决IE8 stack overflow at line 0 原来继承gp.script.BizTableFormView改为继承
 * app.modules.form.TableFormView
 */
$package("chis.application.fhr.script");

$import("app.modules.form.TableFormView", "chis.script.BizCommon",
		"chis.script.BizFormCommon")

chis.application.fhr.script.FamilyRecordForm = function(cfg) {
	cfg.width = 830;
	cfg.colCount = 3;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 180;
	cfg.colCount = cfg.colCount || 3;
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.isAutoScroll = cfg.isAutoScroll || false;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.autoHeight = !cfg.isAutoScroll
	cfg.saveServiceId = cfg.saveServiceId || "chis.simpleSave"
	cfg.loadServiceId = cfg.loadServiceId || "chis.simpleLoad"
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizFormCommon);
	chis.application.fhr.script.FamilyRecordForm.superclass.constructor.apply(
			this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("doNew", this.onDoNew, this);
	this.on("loadData", this.onLoadData, this);
	this.regionCode_value = "";// 给网格地址的文本赋值
	this.regionCode_key = "";// 给网格地址的编码赋值
}

Ext.extend(chis.application.fhr.script.FamilyRecordForm,
		app.modules.form.TableFormView, {
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
						var forceViewWidth = (defaultWidth + (this.labelWidth || 112))
								* colCount

					} else {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
								* colCount
					}
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					if ("tree" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "regionCode_text") {
							// 把id为“regionCode_text”屏蔽掉,不显示出来,由于schema没有
							// 配置displays属性
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
						if (it.id == "regionCode") {
							// 把id为“regioncode”屏蔽掉,不显示出来,由于schema没有 配置displays属性
							continue;
						}

					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						if (it.id == "regionCode_text") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
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
									// fieldStyle:"color:red"

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
				this.getFamilyMess1();
			},
			initFormData : function(data) {

				// if("tree"==this.mainApp.exContext.areaGridShowType){
				//				
				// if(data["regionCode"]["key"]){
				// //添加家庭档案的情况
				// data["regionCode_text"]=data["regionCode"]["text"]
				//						
				// }else{
				// //查看的情况
				// var r={};
				// r["key"]=data["regionCode"];
				// r["text"]=data["regionCode_text"];
				// delete data["regionCode"];
				// data["regionCode_text"];
				// data["regionCode"]=r;
				// }
				// }else{
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode_text")
							.setValue(data.regionCode_text);
					this.form.getForm().findField("regionCode_text").disable();
					this.data.regionCode = data.regionCode;

				}
				chis.application.fhr.script.FamilyRecordForm.superclass.initFormData
						.call(this, data);
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					this.form.getForm().findField("regionCode")
					.setValue(data.regionCode_text);
					this.form.getForm().findField("regionCode").disable();
					this.data.regionCode = data.regionCode;
					this.data.regionCode_text = data.regionCode_text;
					
				}
			},
			changeCfg : function(cfg) {
				if (this.isAutoScroll) {
					delete cfg.autoWidth;
					delete cfg.autoHeight;
					cfg.autoScroll = true;
				}
			},

			getLoadRequest : function() {
				if (this.initDataId) {
					return {
						pkey : this.initDataId
					};
				} else {
					return null;
				}
			},
			doNew : function() {
				this.initDataId = this.exContext.args.initDataId;

				chis.application.fhr.script.FamilyRecordForm.superclass.doNew
						.call(this);
			},
			// 户主姓名是可自填写的字典
			createField : function(it) {
				this.creatingField = it.id;
				return chis.application.fhr.script.FamilyRecordForm.superclass.createField
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

				var ownerName = form.findField("familyId");
				if (ownerName) {
					ownerName.disable();
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
					regionCode_text.enable();
				}

				var manaDoctorId = this.form.getForm()
						.findField("manaDoctorId");
				if (manaDoctorId) {
					manaDoctorId.enable();
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
				chis.application.fhr.script.FamilyRecordForm.superclass.onReady
						.call(this);
				var manaDoctorId = this.form.getForm()
						.findField("manaDoctorId");
				if (manaDoctorId) {
					manaDoctorId.on("select", this.changeManaUnit, this);
				}

				var regionCode = this.form.getForm().findField("regionCode");
				if (regionCode) {
					regionCode.on("afterSelect", this.getFamilyMess, this);//
					regionCode.on("select", this.getFamilyMess, this);//
					regionCode.on("beforeselect", this.getFamilyRegion, this);
				}

				var manaUnitId = this.form.getForm().findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.checkManaUnit, this);
					manaUnitId.on("expand", this.filterManaUnit, this);
					manaUnitId.on("beforeQuery", this.onQeforeQuery, this)
				}

				// if ("form" == this.mainApp.exContext.areaGridShowType) {
				// var
				// regionCode_text=this.form.getForm().findField("regionCode_text");
				// alert(1)
				// if (regionCode_text) {
				// alert(2)
				// regionCode_text.on("select", this.getFamilyMess1, this);//
				// //regionCode_text.on("beforeselect", this.getFamilyRegion,
				// this);
				// }
				//						
				// }
				var ownerName = this.form.getForm().findField("ownerName");
				ownerName.on("blur", function() {
							var v = this.form.getForm().findField("ownerName")
									.getRawValue();
							ownerName.setValue(v);
							ownerName.setRawValue(v);
						}, this)

				var r = this.form.getForm().findField("regionCode_text")
				if (r) {

					r.addClass("x-form-invalid");
				}
				// =========网格地址(tree)的创建==========
				// if ("tree" == this.mainApp.exContext.areaGridShowType) {
				//			
				// if (it.id == "regionCode") {
				// var t = "regionCode_" + this.id
				// this.regionCode = this.createDicField({
				// "width" : 250,
				// "defaultIndex" : 0,
				// "id" : "chis.dictionary.areaGrid",
				// "render" : "Tree",
				// onlySelectLeaf : true,
				// "selectOnFocus" : true,
				// parentKey : this.mainApp.deptId
				//
				// });
				// Ext.apply(this.regionCode, {
				// name : "regionCode"
				// });
				// this.regionCode.fieldLabel = "网格地址：";
				// // this.regionCode.tree.expandAll();展开全部列表
				// this.regionCode.render(t);
				//				
				// this.regionCode.on("select", this.changeManaUnit1
				// , this);
				//						
				//				
				//						
				// }
				// }
			},
			changeManaUnit1 : function(combo, node) {

				if (!node.attributes['key']) {
					return
				}
				this.regionCode.setValue({
							key : node.attributes["key"],
							text : node.attributes["text"]
						});

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
				if (body.ownerName) {
					if (body.ownerName.key != null) {
						f.disable();
					} else {
						f.enable();
					}
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
						// 当网格地址为树状，给regionCode_text赋值
						if ("tree" == this.mainApp.exContext.areaGridShowType||"pycode" == this.mainApp.exContext.areaGridShowType) {

							if ("regionCode_text" == it.id) {
								v = this.regionCode_value;
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

			getFamilyMess1 : function() {

				var value = this.data.regionCode;
				var text = this.data.regionCode_text;
				var form = this.form.getForm();
				this.regionCode_value = text;// 给网格地址 文本赋值

				// form.findField("regionCode").setValue(value);

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
									this.ff.disable();
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