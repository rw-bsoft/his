/**
 * 个人健康档案表单页面
 * 
 * @author tianj
 */
$package("chis.application.hr.script")

$import("util.Accredit", "chis.script.BizTableFormView",
		"util.widgets.LookUpField")

chis.application.hr.script.HealthRecordForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.hr.script.HealthRecordForm.superclass.constructor.apply(
			this, [cfg]);
	this.fldDefaultWidth = cfg.fldDefaultWidth || 180;
	this.colCount = cfg.colCount || 3;
	this.isFamily = false // 判断该网格地址是否属于户这一级
	this.showOwnerName = true;
	this.initDataId = this.exContext.ids.phrId;
	this.on("changeDic", this.onChangeDic, this)
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadData", this.onLoadData, this);
	this.on("save", this.onSave, this);
	this.on("exception", this.onException, this);
}

Ext.extend(chis.application.hr.script.HealthRecordForm,
		chis.script.BizTableFormView, {
			initPanel : function(sc) {
				var systemData = {};
				systemData = this.getSystemInfo();

				if (systemData.areaGridShowType) {
					this.mainApp.exContext.areaGridShowType = systemData.areaGridShowType;
				}
				if ("form" == systemData.areaGridShowType) {

					this.mainApp.exContext.areaGridShowType = "form";
				}

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
					if ("form" == this.mainApp.exContext.areaGridShowType
							|| "pycode" == this.mainApp.exContext.areaGridShowType) {
						var forceViewWidth = (defaultWidth + (this.labelWidth || 140))
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
					if (it.id == "regionCode") {
						if ("pycode" == this.mainApp.exContext.areaGridShowType) {
							it.otherConfig = {
								'not-null' : 'true',
								colspan : 2,
								width : 523,
								allowBlank : false,
								invalidText : "必填字段"
							};
							// it.afterSelect=this.afterSelect;
							var areaGrid = this.createAreaGridField(it);
							table.items.push(areaGrid)
							continue;
						}
					}
					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "regionCode") {
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
										fieldLabel : "<font  color=red>网格地址:<font>",
										"width" : 523

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
					// if ("form" == this.mainApp.exContext.areaGridShowType) {
					// f.anchor = it.anchor || "90%"
					// } else {
					f.anchor = it.anchor || "100%"
					// }
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
					labelWidth : this.labelWidth,// || 80,
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
				var regionCodeF = this.form.getForm().findField("regionCode");
				regionCodeF.setValue(data.regionCode_text);
				this.data.regionCode_text = data.regionCode_text;
				this.data.regionCode = data.regionCode;
				this.getRegionInfo(regionCodeF);

			},
			initFormData : function(data) {
				chis.application.hr.script.HealthRecordForm.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType
						|| "pycode" == this.mainApp.exContext.areaGridShowType) {
					if (data.regionCode) {
						if (data.regionCode.key) {

							this.form.getForm().findField("regionCode")
									.setValue(data.regionCode.text);
							if ("pycode" == this.mainApp.exContext.areaGridShowType) {
								this.form.getForm().findField("regionCode").selectData.regionCode = data.regionCode.key;
							}
							this.form.getForm().findField("regionCode")
									.disable();
							this.data.regionCode = data.regionCode.key;
							this.data.regionCode_text = data.regionCode.text;
						}
					}
				}
			},
			onChangeDic : function(items) {
				var isExit = false;
				var size = items.length
				for (var i = 0; i < size; i++) {
					if (items[i].id == 'hostName') {
						return;
					}
				}
				var field = {
					"type" : "string",
					"length" : "20",
					"acValue" : "1111",
					"id" : "hostName",
					"alias" : "所属家庭",
					"fixed" : "true",
					"display" : "2"
				};
				items.splice(16, 0, field)
				return true;
			},

			onBeforeCreate : function() {
				this.setKeyReadOnly(true);
				this.validate();
				var form = this.form.getForm();
				var manaUnitId = form.findField("manaUnitId");
				if (manaUnitId) {
					manaUnitId.setValue(null);
				}
			},

			loadData : function() {
				this.initDataId = this.exContext.ids.phrId;
				chis.application.hr.script.HealthRecordForm.superclass.loadData
						.call(this);
			},

			onLoadData : function(entryName, body) {
				var form = this.form.getForm();
				this.status = body.status.key;
				var familyNo = body["familyId"];
				this.isFamily = body["isFamily"];
				form.findField("regionCode").disable();
				form.findField("manaDoctorId").disable();
				
				var createUser = form.findField("createUser").getValue();
				if(createUser.length==0){
					form.findField("createUser").enable();
				}else{
					form.findField("createUser").disable();
				}
				var createDate = form.findField("createDate").getValue();
				if(createDate.length==0){
					form.findField("createDate").enable();
				}else{
					form.findField("createDate").disable();
				}
				
				var inputUser = form.findField("inputUser").getValue();
				if(inputUser.length==0){
					form.findField("inputUser").enable();
				}else{
					form.findField("inputUser").disable();
				}
				var inputDate = form.findField("inputDate").getValue();
				if(inputDate.length==0){
					form.findField("inputDate").enable();
				}else{
					form.findField("inputDate").disable();
				}
				
				debugger;
				var createUser = form.findField("createUser").getValue();
				if(createUser == null || createUser == ""){
					form.findField("createUser").enable();
				}else{
					form.findField("createUser").disable();
				}


				if (familyNo && this.isFamily) {
					form.findField("hostName").setValue(body["ownerName"]);
					this.data["preFamily"] = familyNo;
					this.data["ownerName"] = body["ownerName"];
					if (!body["isMaster"]) {
						form.findField("masterFlag").setValue({
									key : "n",
									text : "否"
								});
						if (body["hasMaster"]) {
							form.findField("masterFlag").disable();
						}
					}
				} else {
					form.findField("masterFlag").enable();
				}

				var partnerName = form.findField("partnerName");
				if (partnerName && body["maritalStatusCode"] == "1") {
					partnerName.disable();
				}

				var deadFlag = body["deadFlag"];
				if (deadFlag) {
					this.setDeadDate(deadFlag);
				}

				if (!body["isSame"]) {
					Ext.Msg.show({
								title : '提示信息',
								msg : "档案中儿童父母亲信息与儿童档案不一致,请确认！",
								modal : true,
								width : 350,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
				}
			},

			onReady : function() {
				var form = this.form.getForm();
				var fatherName = form.findField("fatherName");
				var motherName = form.findField("motherName");
				var partnerName = form.findField("partnerName");
				var deadFlag = form.findField("deadFlag");
				var manaDoctorId = form.findField("manaDoctorId");
				var manaUnitId = form.findField("manaUnitId");
				var createUser = form.findField("createUser");
				var createUnit = form.findField("createUnit");
				var inputUser = form.findField("inputUser");
				var inputUnit = form.findField("inputUnit");
				var relaCode = form.findField("relaCode");
				var regionCode = form.findField("regionCode");
				var masterFlag = form.findField("masterFlag");
//				var signFlag = form.findField("signFlag");
//				signFlag.disable();
				if (masterFlag) {
					masterFlag.on("select", this.setRelations, this);
				}
				if (relaCode) {
					relaCode.on("select", this.setRelations, this);
					relaCode.tree.on("beforeclick", this.checkRelaCode, this);
				}
				if (regionCode) {
					regionCode.on("afterSelect", this.getRegionInfo, this);
					regionCode.on("select", this.getRegionInfo, this);
					regionCode.on("select", this.setRelations, this);
				}
				if (deadFlag) {
					deadFlag.on("select", this.isDead, this);
				}
				if (fatherName) {
					fatherName.on("lookup", this.findNo, this);
					fatherName.on("clear", function() {
								this.data["fatherId"] = "";
							}, this);
				}
				if (motherName) {
					motherName.on("lookup", this.findNo, this);
					motherName.on("clear", function() {
								this.data["motherId"] = "";
							}, this);
				}
				if (partnerName) {
					partnerName.on("lookup", this.findNo, this);
					partnerName.on("clear", function() {
								this.data["partnerId"] = "";
							}, this);
				}

				if (manaDoctorId) {
					manaDoctorId.on("select", this.changeManaUnit, this);
				}
				
				if (createUser) {
					createUser.on("select", this.changeCreateUnit, this);
				}

				if (manaUnitId) {
					manaUnitId.on("beforeselect", this.checkManaUnit, this);
					manaUnitId.on("expand", this.filterManaUnit, this);
					manaUnitId.on("beforeQuery", this.onQeforeQuery, this);
				}
				
			

				chis.application.hr.script.HealthRecordForm.superclass.onReady
						.call(this)
			},

			/**
			 * 选择成员关系时,检查关系与性别是否对应
			 * 
			 * @param {}
			 *            combo
			 * @param {}
			 *            record
			 * @param {}
			 *            index
			 * @return {Boolean}
			 */
			checkRelaCode : function(node, e) {
				var key = node.id;
				var sexConf = {
					"11" : 1,// "夫"
					"12" : 2,// "妻"
					"21" : 1,// "独生子"
					"22" : 1,// "长子"
					"23" : 1,// "次子"
					"24" : 1,// "三子"
					"25" : 1,// "四子"
					"26" : 1,// "五子"
					"27" : 1,// "养子或继子"
					"28" : 1,// "女婿"
					"29" : 1,// "其他儿子"

					"31" : 2,// "独生女"
					"32" : 2,// "长女"
					"33" : 2,// "次女"
					"34" : 2,// "三女"
					"35" : 2,// "四女"
					"36" : 2,// "五女"
					"37" : 2,// "养女或继女"
					"38" : 2,// "儿媳"
					"39" : 2,// "其他女儿"

					"41" : 1,// "孙子"
					"42" : 2,// "孙女"
					"43" : 1,// "外孙子"
					"44" : 2,// "外孙女"
					"45" : 2,// "孙媳妇或外孙媳妇"
					"46" : 1,// "孙女婿或外孙女婿"
					"47" : 1,// "曾孙子或外曾孙子"
					"48" : 2,// "曾孙女或外曾孙女"

					"51" : 1,// "父亲"
					"52" : 2,// "母亲"
					"53" : 1,// "公公"
					"54" : 2,// "婆婆"
					"55" : 1,// "岳父"
					"56" : 2,// "岳母"
					"57" : 1,// "继父或养父"
					"58" : 2,// "继母或养母"

					"61" : 1,// "祖父"
					"62" : 2,// "祖母"
					"63" : 1,// "外祖父"
					"64" : 2,// "外祖母"
					"66" : 1,// "曾祖父"
					"67" : 2,// "曾祖母"

					"71" : 1,// "兄"
					"72" : 2,// "嫂"
					"73" : 1,// "第"
					"74" : 2,// "弟媳"
					"75" : 2,// "姐姐"
					"76" : 1,// "姐夫"
					"77" : 2,// "妹妹"
					"78" : 1,// "妹夫"

					"81" : 1,// "伯父"
					"82" : 2,// "伯母"
					"83" : 1,// "叔父"
					"84" : 2,// "婶母"
					"85" : 1,// "舅父"
					"86" : 2,// "舅母"
					"87" : 1,// "姨父"
					"88" : 2,// "姨母"

					"89" : 1,// "姑父"
					"90" : 2,// "姑母"
					"93" : 1,// "侄子"
					"94" : 2,// "侄女"
					"95" : 1,// "外甥"
					"96" : 2
					// "外甥女"
				}

				var v = sexConf[key];
				if (!v) {// 没定义的 ， 不限制男女
					return true;
				}
				if (v != this.exContext.empiData.sexCode) {
					Ext.Msg.alert("错误", "所选与户主关系与该人性别不匹配,请重新选择!");
					return false;
				}
			},

			/**
			 * 选择成员关系后，加载相应的人的EMPIID和姓名
			 * 
			 * @param {}
			 *            field
			 */
			setRelations : function(field) {
				var form = this.form.getForm();
				var fieldName = field.getName();
				if (fieldName == "masterFlag") {
					this.isOwner(field);
				} else if (fieldName == "relaCode") {
					this.setMasterFlag(field);
				}
				var relaCode = form.findField("relaCode");
				var relaCodeValue = relaCode.getValue();
				this.fillRelations({});
				if (!relaCodeValue
						|| !((relaCodeValue.substr(0, 1) == '2' && relaCodeValue != '28')
								|| (relaCodeValue.substr(0, 1) == '3' && relaCodeValue != '38')
								|| relaCodeValue == '11'
								|| relaCodeValue == '12'
								|| relaCodeValue == '51'
								|| relaCodeValue == '52' || relaCodeValue == '02')) {
					return;
				}
				if (!this.isFamily) {
					return;
				}
				var regionCode = form.findField("regionCode");
				var regionCode = regionCode.getValue();
				// 防止先选择关系绕过关系检查。
				if ((regionCode == null || regionCode.length == 0)
						&& (!this.data["familyId"] || this.data["familyId"].length == 0)) {
					Ext.Msg.alert("错误", "请先选择网格地址!");
					relaCode.reset();
					return;
				}
				var body = {};
				body["familyId"] = this.data["familyId"];
				body["regionCode"] = regionCode;
				body["empiId"] = this.exContext.ids.empiId;
				body["relaCode"] = relaCodeValue;
				body["sexCode"] = this.exContext.empiData.sexCode;

				this.form.el.mask("正在载入家庭成员关系");
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "dealFamilyRelations",
							method : "execute",
							schema : this.entryName,
							body : body
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (!json.body) {
								return;
							}
							if (json.body.isExsit) {
								Ext.Msg.alert('错误!', json.body.message);
								relaCode.reset();

								if (relaCodeValue == '02') {
									form.findField("masterFlag").setValue({
												key : "n",
												text : "否"
											});
								}
								return;
							}

							this.fillRelations(json.body);
						}, this)
			},

			fillRelations : function(records) {
				var father = records["father"];
				var mother = records["mother"];
				var partner = records["partner"];
				var fatherField = this.form.getForm().findField("fatherName");
				var motherField = this.form.getForm().findField("motherName");
				var partnerField = this.form.getForm().findField("partnerName");

				if (father && (father.empiId != this.exContext.ids.empiId)) {
					fatherField.setValue(father.personName);
					this.data["fatherId"] = father.empiId;
				} else {
					fatherField.reset();
					this.data["fatherId"] = "";
				}

				if (mother && (mother.empiId != this.exContext.ids.empiId)) {
					motherField.setValue(mother.personName);
					this.data["motherId"] = mother.empiId;
				} else {
					motherField.reset();
					this.data["motherId"] = "";
				}

				if (partner && (partner.empiId != this.exContext.ids.empiId)) {
					partnerField.setValue(partner.personName);
					this.data["partnerId"] = partner.empiId;
				} else {
					partnerField.reset();
					this.data["partnerId"] = "";
				}
			},

			getSaveRequest : function(saveData) {
				if (this.exContext.ids.phrId) {
					saveData["lastModifyUser"] = this.mainApp.uid;
					saveData["lastModifyUnit"] = this.mainApp.deptId;
					saveData["lastModifyDate"] = this.mainApp.serverDate;
					saveData["status"] = this.status;
				} else {
					saveData["status"] = '0';
				}
				if (this.data["familyId"]) {
					saveData["familyId"] = this.data["familyId"];
				} else {
					saveData["familyId"] = "";
				}
				if (this.data["preFamily"]) {
					saveData["preFamily"] = this.data["preFamily"];
				} else {
					saveData["preFamily"] = "";
				}
				saveData["empiId"] = this.exContext.ids.empiId;
				saveData["phrId"] = this.exContext.ids.phrId;
				saveData.areaGridType = this.mainApp.exContext.areaGridType;
				return saveData;
			},

			findNo : function(field) {
				this.fieldName = field.name;
				$import("chis.application.mpi.script.EMPIInfoModule");
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
				}
				expertQuery.on("onEmpiReturn", function(r) {
							if (!r) {
								return;
							}
							var empiId = r.empiId;
							if (this.fieldName == "fatherName") {
								if (r.sexCode != '1') {
									Ext.Msg.alert('提示！', '所输入身份证性别应该为男性')
									return;
								}
								if (this.exContext.empiData.empiId == r.empiId) {
									Ext.Msg.alert('提示！', '所输入身份证与本人相同')
									return;
								}
								if (this.data["partnerId"] == r.empiId) {
									Ext.Msg.alert('提示！', '所输入身份证为配偶身份证')
									return;
								}
								this.data["fatherId"] = empiId;
							} else if (this.fieldName == "motherName") {
								if (r.sexCode != '2') {
									Ext.Msg.alert('提示！', '所输入身份证性别应该为女性')
									return;
								}
								if (this.exContext.ids.empiId == r.empiId) {
									Ext.Msg.alert('提示！', '所输入身份证与本人相同')
									return;
								}
								if (this.data["partnerId"] == r.empiId) {
									Ext.Msg.alert('提示！', '所输入身份证为配偶身份证')
									return;
								}
								this.data["motherId"] = empiId;
							} else {
								if (this.exContext.empiData.sexCode == r.sexCode) {
									Ext.Msg.alert('提示！', '所输入身份证性别与配偶相同')
									return;
								}
								if (this.data["motherId"] == r.empiId
										|| this.data["fatherId"] == r.empiId) {
									Ext.Msg.alert('提示！', '所输入身份证为父母身份证');
									return;
								}
								this.data["partnerId"] = empiId;
							}
							var personName = r.personName;
							var textField = this.form.getForm()
									.findField(this.fieldName);
							if (textField) {
								textField.setValue(personName);
							}
							fieldName = "";
						}, this);
				var win = expertQuery.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			findItemExists : function(schema, item) {
				for (var i = 0; i < schema.items.length; i++) {
					var schemaItem = schema.items[i];
					if (schemaItem.id == item.id) {
						return true;
					}
				}
				return false;
			},

			isOwner : function(field) {
				var value = field.getValue();
				this.setRelaCode(value);
			},

			setRelaCode : function(value) {
				var relaCode = this.form.getForm().findField("relaCode");
				if (value == "y") {
					relaCode.setValue({
								key : "02",
								text : "户主"
							});
				} else {
					relaCode.reset();
				}
			},

			isDead : function(deadFlag) {
				var value = deadFlag.getValue();
				var form = this.form.getForm()
				var deadDate = form.findField("deadDate");
				var deadReason = form.findField("deadReason");
				var masterFlag = form.findField("masterFlag");
				var relaCode = form.findField("relaCode");

				if (value == "n") {
					deadDate.reset();
					deadDate.disable();
					deadReason.reset();
					deadReason.disable();
					deadDate.allowBlank = true;
					deadReason.allowBlank = true;
					deadDate.validate();
					deadReason.validate();
				} else {
					deadDate.enable();
					deadReason.enable();
					deadDate.allowBlank = false;
					deadReason.allowBlank = false;
					deadDate.validate();
					deadReason.validate();

					masterFlag.reset();
					masterFlag.enable();
					relaCode.reset();
				}
			},

			setDeadDate : function(value) {
				var deadDate = this.form.getForm().findField("deadDate");
				var deadFlag = this.form.getForm().findField("deadFlag");
				var deadReason = this.form.getForm().findField("deadReason");

				if (value == "y") {
					deadFlag.disable();
					deadDate.disable();
					deadReason.disable();
					deadDate.allowBlank = false;
					deadReason.allowBlank = false;
					deadDate.validate();
					deadReason.validate();
				} else {
					deadFlag.enable();
					deadDate.disable();
					deadReason.disable();
					deadDate.allowBlank = true;
					deadReason.allowBlank = true;
					deadDate.validate();
					deadReason.validate();
				}
			},

			doSave : function() {

				// var values =
				// chis.application.hr.script.HealthRecordForm.superclass.getFormData
				// .call(this);
				var values = this.getFormData();
				if (!values) {
					return;
				}
				Ext.apply(this.data, values);
				this.checkDeadFlag(values);
			},

			checkDeadFlag : function(saveData) {

				var form = this.form.getForm();
				var deadFlag = form.findField("deadFlag").getValue();
				var phrId = form.findField("phrId").getValue();
				if (deadFlag == 'y' && phrId != undefined) {
					Ext.Msg.show({
								title : '提示',
								msg : '死亡标志为"是"将注销用户所有档案 ，是否继续',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.saveToServer(saveData)
									}
								},
								scope : this
							});
				} else {
					this.saveToServer(saveData);
				}
			},

			onSave : function(entryName, op, json, data) {
				this.fireEvent("chisSave");// phis中用于通知刷新emrView左边树
				if (data["deadFlag"] == 'n') {
					this.activeOldPeople();
				}
				this.fireEvent("refreshEhrView", "B_01");
				if (data["incomeSource"].indexOf("1") > -1) {
					var jobtitleId = this.mainApp.jobtitleId;
					if (jobtitleId != "chis.01" && jobtitleId != "chis.05"
							&& jobtitleId != "system") {
						return;
					}
					this.fireEvent("addModule", "B_08");
				}
				if (this.ehrview) {
					this.ehrview.refreshInfo();
				}
				if (this.emrview) {
					this.emrview.refreshTopEmpi();
				}
				this.refreshEhrTopIcon();
			},
			onException : function(code, msg, data) {
				if (code == 555) {
					this.form.getForm().findField("masterFlag").enable();
				}
			},

			activeOldPeople : function() {
				if (this.exContext.ids["MDC_OldPeopleRecord.phrId"]) {
					return;
				}
				// add by lyl 20110724 只有责任医生团队长和系统管理员才能建老年人档案 ,其他用户不能新建也不出现提示
				var jobtitleId = this.mainApp.jobtitleId;
				if (jobtitleId != "chis.01" && jobtitleId != "chis.05"
						&& jobtitleId != "system" && jobtitleId != "gp.100"
						&& jobtitleId != "gp.101") {
					return;
				}
				if (!this.mainApp.exContext.oldPeopleAge) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '请先配置老年人年龄',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return
				}
				if (this.exContext.empiData.age >= this.mainApp.exContext.oldPeopleAge) {
					Ext.Msg.show({
								title : '提示',
								msg : '该用户是老年人是否建立老年人档案',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.fireEvent("activeModule", "B_07");
									}
								},
								scope : this
							});
				}
			},

			setMasterFlag : function(field) {
				var value = field.getValue();
				var masterFlag = this.form.getForm().findField("masterFlag");
				if (!masterFlag) {
					return;
				}
				if (masterFlag.disabled) {
					var master = masterFlag.getValue();
					if (master == 'y') {
						if (value != "02") {
							Ext.Msg.alert("提示信息", "与户主关系填写错误,请重新填写!");
							field.reset();
						}
					} else {
						if (value == "02") {
							Ext.Msg.alert("提示信息", "与户主关系填写错误,请重新填写!");
							field.reset();
						}
					}
				} else {
					if (value == "02") {
						masterFlag.setValue({
									key : "y",
									text : "是"
								});
					} else {
						masterFlag.setValue({
									key : "n",
									text : "否"
								});
					}
				}
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
					combox.setValue(manageUnit);
					combox.disable();
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
						});
				this.setManaUnit(result.json.manageUnit)
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
					combox.setValue(manageUnit);
					combox.disable();
				}
			},
			
			changeCreateUnit : function(combo, node) {
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
						});
				this.setCreateUnit(result.json.manageUnit)
			},

			checkManaUnit : function(comb, node) {
				var key = node.attributes['key'];
				if (key.length >= 11) {
					return true;
				} else {
					return false;
				}
			},
			getRegionInfo : function(field, node) {
				if ("pycode" == this.mainApp.exContext.areaGridShowType) {
					this.isFamily = true;
					if (field.selectData) {
						this.data.regionCode = field.selectData.regionCode;
						this.data.regionCode_text = field.selectData.regionName;
					}
				} else if ("tree" == this.mainApp.exContext.areaGridShowType) {
					this.isFamily = node.attributes["isFamily"] == "1"
							? true
							: false;
					this.data.regionCode = field.getValue();
					this.data.regionCode_text = field.getRawValue();
				} else {
					this.isFamily = true;
				}
				if (!this.data.regionCode) {
					return;
				}
				this.form.el.mask("正在查询,请稍后..");
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : "findInfoByRegionCode",
							method : "execute",
							schema : this.entryName,
							body : {
								regionCode : this.data.regionCode,
								empiId : this.exContext.ids.empiId
							}
						}, function(code, msg, json) {
							this.form.el.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.getFamilyMess, [null]);
								return;
							}
							var body = json.body;
							if (!body) {
								return;
							}
							var form = this.form.getForm();
							var partnerName = form.findField("partnerName");
							if (partnerName) {
								if (body.maritalStatusCode == "1") {
									partnerName.disable();
									this.data["partnerId"] = "";
								} else {
									partnerName.enable();
									partnerName.setValue(body.partnerName);
									this.data["partnerId"] = body.partnerId;
								}
							}
							// ** 设置家庭对应信息 **
							var familyId = body.familyId;
							var hasMaster = body.hasMaster;
							var ownerName = body.ownerName;
							var hostName = form.findField("hostName");
							var masterFlag = form.findField("masterFlag");
							var relaCode = form.findField("relaCode");
							if (hasMaster) {
								masterFlag.setValue({
											key : "n",
											text : "否"
										});
								masterFlag.disable();
							} else {
								masterFlag.enable();
								masterFlag.reset();
							}
							relaCode.reset();

							if (ownerName) {
								hostName.setValue(ownerName);
							} else {
								hostName.reset();
							}

							if (familyId) {
								this.data["familyId"] = familyId;
							} else {
								this.data["familyId"] = null;
							}
							// ** 设置网格地址对应信息 **
							var manaDocField = form.findField("manaDoctorId");
							var manaDoctor = body.manaDoctor;
							if (manaDocField) {
								manaDocField.setValue(manaDoctor);
								var manageUnits = json.body.manageUnits;
								if (manageUnits && manageUnits.length == 1) {
									this.setManaUnit(manageUnits[0]);
								} else {
									// 该责任医生可能归属的管辖机构编码集合
									this.manageUnits = manageUnits
									this.setManaUnit("");
								}
							}
						}, this)
			},

			getLoadRequest : function() {
				if (this.exContext.ids.phrId) {
					return {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId
					};
				} else {
					return null;
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
							if (it.id == 'regionCode') {
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

							if ("regionCode" == it.id) {
								v = this.data.regionCode;
							}
							if ("regionCode_text" == it.id) {
								v = this.data.regionCode_text;
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
			getSystemInfo : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.basicPersonalInformationService",
							serviceAction : "getSystemInfo",
							method : "execute"

						})

				if (result.code != 200) {
					Ext.Msg.alert("提示", msg);
					return null;
				}
				return result.json.body;
						},
			doVerify : function() {
				var  r= this.getFormData();
				if (!r) {
					return;
				}
				var uid = this.mainApp.uid;
                 if (r.manaDoctorId !=uid) {
			     MyMessageTip.msg("提示", "当前登录工号不是该档案责任医生，请核对工号后，在进行档案审核！", true);
			    return;
				}
				if (r.isSecondVerify =="2") {
			     MyMessageTip.msg("提示", "该档案已开放！", true);
			    return;
				}else if(r.isFirstVerify =="1"){
		         MyMessageTip.msg("提示", "该档案二级已审核！", true);
			    return;	
		        }else if(r.isOffer =="y"){
		         MyMessageTip.msg("提示", "该档案已审核！", true);
			    return;	
		        }
				var phrId = r.phrId;
				var jobId = this.mainApp.jobId;
				if (jobId == "chis.01") {// 责任医生角色
					var data = {
									"phrId" : phrId
								};
					util.rmi.jsonRequest({
								serviceId : this.verifyServiceId,
								serviceAction : this.verifyAction,
								method : "execute",
								body : data
							}, function(code, msg, json) {
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}   
							}, this)
							MyMessageTip.msg("通知", "档案审核成功！", true);
							this.fireEvent("refreshEhrView", "B_01");
				}else{
					Ext.Msg.alert("提示","请登录责任医生角色");
					return;
				}
				
			},
			doCancelVerify : function() {
				var  r= this.getFormData();
				if (!r) {
					return;
				}
				var uid = this.mainApp.uid;
                 if (r.manaDoctorId !=uid) {
			     MyMessageTip.msg("提示", "当前登录工号不是该档案责任医生，请核对工号后，在进行档案审核！", true);
			    return;
				}
				if (r.isSecondVerify =="2") {//判定是否已审核通过	
			     MyMessageTip.msg("提示", "该档案已开放,不能取消审核！", true);
			    return;
				}else if(r.isFirstVerify =="1"){
		         MyMessageTip.msg("提示", "该档案二级已审核,不能取消审核！", true);
			    return;	
		        }else if(r.isOffer =="b"){
		         MyMessageTip.msg("提示", "该档案为退回档案，请重新审核！", true);
			    return;	
		        }else if((r.isOffer =="") || (r.isOffer =="n") || (r.isOffer =="c")){
		         MyMessageTip.msg("提示", "该档案为未审核/取消档案，请重新审核！", true);
			    return;	
		        }
				var phrId = r.phrId;
				var jobId = this.mainApp.jobId;
				if (jobId == "chis.01") {// 责任医生角色
					var data = {
									"phrId" : phrId
								};
					util.rmi.jsonRequest({
								serviceId : this.cancelVerifyServiceId,
								serviceAction : this.cancelVerifyAction,
								method : "execute",
								body : data
							}, function(code, msg, json) {
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								} 
							}, this)	
							MyMessageTip.msg("通知", "档案取消审核成功！", true);
							this.fireEvent("refreshEhrView", "B_01");
				}else{
					Ext.Msg.alert("提示","请登录责任医生角色");
					return;
				}
			}
		});