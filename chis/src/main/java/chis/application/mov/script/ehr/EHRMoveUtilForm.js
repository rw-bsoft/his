$package("chis.application.mov.script.ehr");

$import("chis.script.BizTableFormView", "chis.application.mov.script.util.QueryModule",
		"util.widgets.LookUpField", "chis.application.mpi.script.EMPIInfoModule");

chis.application.mov.script.ehr.EHRMoveUtilForm = function(cfg) {
	cfg.fldDefaultWidth = 142;
	cfg.labelWidth = 80;
	cfg.colCount = 3;
	chis.application.mov.script.ehr.EHRMoveUtilForm.superclass.constructor.apply(this, [cfg]);
	this.loadServiceId = "chis.ehrMoveService";
	this.loadAction = "getMoveEHR";
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("saveData", this.onSaveData, this);
	this.mutiSelect = true;
	this.regionCode_key="";//传递参数到EHRMoveApplyForm.js页面
	this.regionCode_text="";
};

Ext.extend(chis.application.mov.script.ehr.EHRMoveUtilForm, chis.script.BizTableFormView, {
	onReady : function() {
		chis.application.mov.script.ehr.EHRMoveUtilForm.superclass.onReady.call(this);
		var frm = this.form.getForm();
		// 档案类型
		var archiveType = frm.findField("archiveType");
		if (archiveType) {
			archiveType.on("select", this.setFiledName, this);
			archiveType.validate();
		}
		// 姓名或户主*查询档案
		var personNameField = frm.findField("personName");
		if ("create" == this.op) {
			if (personNameField) {
				personNameField.on("lookup", this.doQuery, this);
				personNameField.on("clear", this.doNew, this);
				personNameField.validate();
			}
		} else {
			if (personNameField) {
				// personNameField.removeListener("clear",this.doNew, this);
			}
		}

		// 现网格地址
		var targetArea = frm.findField("targetArea");
		if (targetArea) {
			targetArea.on("select", this.changeTargetArea, this);
		}
		// 现责任医生
		var targetDoctor = frm.findField("targetDoctor");
		if (targetDoctor) {
			targetDoctor.on("select", this.changeTargetUnit, this);
//			targetDoctor.on("change", this.onToChangeTargetUnit, this);
			// targetDoctor.on("blur", this.onToChangeTargetUnit, this);
			targetDoctor.on("expand", this.filterTargetDoctor, this);
		}

		var targetUnit = frm.findField("targetUnit");
		if (targetUnit) {
			targetUnit.on("beforeselect", this.checkManaUnit, this);
			targetUnit.on("expand", this.filterManaUnit, this);
			targetUnit.on("beforeQuery", this.onQeforeQuery, this);
		}

		
	},

	filterTargetDoctor : function(field) {
		var targetUnitField = this.form.getForm().findField("targetUnit");
		this.targetUnitVal = targetUnitField.getValue();
		var targetUnitText = targetUnitField.getRawValue();
		this.unitNormal = false;
		if (this.targetUnitVal && this.targetUnitVal.trim().length > 0
				&& targetUnitText && targetUnitText.trim().length > 0) {
			this.unitNormal = true;
		}
		var tree = field.tree;
		tree.getRootNode().reload();
		// tree.expandAll();
		tree.on("expandnode", function(node) {
					var childs = node.childNodes;
					for (var i = childs.length - 1; i >= 0; i--) {
						var child = childs[i];
						child.on("load", function() {
									tree.filter.filterBy(
											this.filterTargetDoctorTree, this);
								}, this);
					}
				}, this);
	},

	filterTargetDoctorTree : function(node) {
		if (!node) {
			return true;
		}
		if (!node.parentNode) {
			return true;
		}
		var key = node.parentNode.attributes["key"];
		if (this.unitNormal) {
			if (this.startWith(this.targetUnitVal, key)) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	},

	checkManaUnit : function(comb, node) {
		var key = node.attributes['key'];
		if (key.length >= 11) {
			return true;
		} else {
			return false;
		}
	},

	filterManaUnit : function(field) {
		var tree = field.tree;
		tree.getRootNode().reload();
		var field = this.form.getForm().findField("targetDoctor");
		var rawValue = field.getRawValue();
		if (!rawValue) {
			return;
		}
		if (!this.manageUnits) {
			return;
		}
		tree.expandAll();
		tree.on("expandnode", function(node) {
					var childs = node.childNodes;
					for (var i = childs.length - 1; i >= 0; i--) {
						var child = childs[i];
						child.on("load", function() {
									tree.filter.filterBy(this.filterTree, this);
								}, this);
					}
				}, this);
	},

	filterTree : function(node) {
		if (!node || !this.manageUnits) {
			return true;
		}
		var key = node.attributes["key"];
		for (var i = 0; i < this.manageUnits.length; i++) {
			var manageUnit = this.manageUnits[i];
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
		params["filter"] = "['or'," + cnd.substring(0, cnd.length - 1) + "]";
	},

	changeTargetUnit : function(combo, node) {
		if (!node.attributes['key']) {
			return
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getManageUnit",
					method:"execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				});
		this.setManaUnit(result.json.manageUnit);
	},

	onToChangeTargetUnit : function() {
		var frm = this.form.getForm();
		var targetDoctor = frm.findField("targetDoctor");
		var rawValue = targetDoctor.getRawValue();
		var targetUnit = frm.findField("targetUnit");
		if (rawValue) {
			targetUnit.enable();
            targetUnit.reset();
			return;
		} else {
			targetDoctor.reset();
			this.manageUnits = null;
		}
		if (!targetUnit) {
    		targetUnit.enable();
    		targetUnit.reset();
			return;
		}
	},

	setManaUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("targetUnit");
		if (!combox) {
			return;
		}
		if (!manageUnit) {
			combox.enable();
			combox.reset();
			return;
		}

		if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
			combox.setValue(manageUnit);
			combox.disable();
		}
	},

	setFiledName : function(field) {
		var value = field.getValue();
		if (value)
			this.changeLabelName(value);
	},

	changeLabelName : function(value) {
		if (value) {
			var personName = this.form.getForm().findField("personName");
			if (value == "1") {
				personName.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>姓名:</span>";
			} else {
				personName.el.parent().parent().parent().first().dom.innerHTML = "<span style='color:red'>户主:</span>";
			}
		}
	},

	doQuery : function(field) {
		if (!field.disabled) {
			var moveField = this.form.getForm().findField("archiveType");
			var moveType = moveField.getValue();
			if (!moveType) {
				Ext.Msg.alert("提示信息", "请选择档案迁移类型!");
				moveField.focus();
				return;
			}
			// 查找个人档案
			if (moveType == "1") {
				var expertQuery = this.midiModules["EMPI.ExpertQuery"];
				if (!expertQuery) {
					expertQuery = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							});
					expertQuery.on("onEmpiReturn", function(r) {
								var empiId = r.empiId;
								var personName = r.personName;
								util.rmi.jsonRequest({
											serviceId : "chis.simpleQuery",
											// serviceAction : this.loadAction
											// || "",
											schema : "chis.application.hr.schemas.EHR_HealthRecord",
											method:"execute",
											queryCndsType : "1",
											cnd : ['eq', ['$', 'a.empiId'],
													['s', empiId]]
										}, function(code, msg, json) {
											if (this.form && this.form.el) {
												this.form.el.unmask();
											}
											if (code > 300) {
												this.processReturnMsg(code,
														msg, this.loadData);
												return
											}
											if (json.body.length == 0) {
												Ext.Msg.alert("提示信息",
														personName
																+ "未建立个人健康档案！");
												return;
											}
											if (json.body) {
												var doc = json.body[0];
												personName = doc.personName;
												if (!doc) {
													Ext.Msg
															.alert(
																	"提示信息",
																	personName
																			+ "未建立个人健康档案！");
													return;
												}
												this.initDataBefShow(doc,
														moveType, personName);
											}
										}, this);
							}, this);
					this.midiModules["EMPI.ExpertQuery"] = expertQuery;
				}
				var win = expertQuery.getWin();
				win.setPosition(250, 100);
				win.show();
				return;
			}
			// 查找家庭档案
			if (moveType == "2") {
				var familyQuery = this.midiModules["familyQuery"];
				if (!familyQuery) {
					familyQuery = new chis.application.mov.script.util.QueryModule({
								title : "家庭信息查询",
								autoLoadSchema : true,
								isCombined : true,
								autoLoadData : false,
								mutiSelect : false,
								queryCndsType : "1",
								entryName : "chis.application.mov.schemas.MOV_FamilyRecordQuery",
								buttonIndex : 3,
								mainApp : this.mainApp
							});
					this.midiModules["familyQuery"] = familyQuery;
				}
				familyQuery.on("recordSelected", function(r) {
							if (!r) {
								return;
							}
							var personName = r[0].get("ownerName");
							var frmData = r[0].data;
							this.initDataBefShow(frmData, moveType, personName);
						}, this);
				var win = familyQuery.getWin();
				win.setPosition(250, 100);
				win.show();
			}
		}
	},

	initDataBefShow : function(doc, type, personName) {
		var docId;
		if (type == "1") {
			docId = doc["phrId"];
		} else {
			docId = doc["familyId"];
		}
		var manageUnit = doc["manaUnitId"];
		var manageUnit_text = doc["manaUnitId_text"];
		var regionCode = doc["regionCode"];
		var regionCode_text = doc["regionCode_text"];
		var manaDoctorId = doc["manaDoctorId"];
		var manaDoctorId_text = doc["manaDoctorId_text"];
		if (manageUnit) {
			// 判断迁入、迁出
			// var targetUnitField =
			// this.form.getForm().findField("targetUnit");
			// var targetAreaField =
			// this.form.getForm().findField("targetArea");
			// var targetDoctorField =
			// this.form.getForm().findField("targetDoctor");
			var sourceUnitField = this.form.getForm().findField("sourceUnit");
			if("form"==this.mainApp.exContext.areaGridShowType){
				var sourceAreaField = this.form.getForm().findField("sourceArea_text");
			}
			if("tree"==this.mainApp.exContext.areaGridShowType||"pycode"==this.mainApp.exContext.areaGridShowType){
				var sourceAreaField = this.form.getForm().findField("sourceArea");
			}
			var sourceDoctorField = this.form.getForm()
					.findField("sourceDoctor");
			sourceUnitField.setValue({
						key : manageUnit,
						text : manageUnit_text
					});
			sourceUnitField.disable();
			if("form"==this.mainApp.exContext.areaGridShowType){	
			sourceAreaField.setValue(regionCode_text);
			this.regionCode_key=regionCode;
			this.regionCode_text=regionCode_text;
			sourceAreaField.disable();
			}
			if("tree"==this.mainApp.exContext.areaGridShowType){
			sourceAreaField.setValue({
						key : regionCode,
						text : regionCode_text
					});
			sourceAreaField.disable();
			}
			if("pycode"==this.mainApp.exContext.areaGridShowType){	
				sourceAreaField.setValue(regionCode_text);
				sourceAreaField.selectData.regionCode=regionCode
				this.regionCode_key=regionCode;
				this.regionCode_text=regionCode_text;
				sourceAreaField.disable();
				}
			sourceDoctorField.setValue({
						key : manaDoctorId,
						text : manaDoctorId_text
					});
			sourceDoctorField.disable();
			if (this.startWith(manageUnit, this.mainApp.deptId)) {
				this.data["moveType"] = "2";
			} else {
				this.data["moveType"] = "1";
			}
		}
		var archiveIdField = this.form.getForm().findField("archiveId");
		var personNameField = this.form.getForm().findField("personName");
		archiveIdField.setValue(docId);
		personNameField.setValue(personName);
	},

	startWith : function(str1, str2) {
		if (str1 == null || str2 == null) {
			return false;
		}
		if (str2.length > str1.length) {
			return false;
		}
		if (str1.substr(0, str2.length) == str2) {
			return true;
		}
		return false;
	},

	changeTargetArea : function(combox, node) {
		var regioncode = node.attributes["key"];
		if("form"==this.mainApp.exContext.areaGridShowType){
		var sourceAreaField = this.form.getForm().findField("sourceArea_text");
		
		}
       if("tree"==this.mainApp.exContext.areaGridShowType){
	
		var sourceAreaField = this.form.getForm().findField("sourceArea");
		}
		
		var sourceGridCode = sourceAreaField.getValue();
		if (regioncode == sourceGridCode) {
			Ext.Msg.alert("提示信息", "原网格地址与现网格地址相同，请重新选择!");
			if (this.form && this.form.el) {
				this.form.el.unmask();
			}
			combox.reset();
			var targetUnit = this.form.getForm().findField("targetUnit");
			targetUnit.reset();
			return;
		}

		var form = this.form.getForm();
		this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : "getManaDoctorInfo",
					method:"execute",
					body : {
						"regionCode" : regioncode
					}
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					if (!json.body) {
						return;
					}
					var targetDoctor = form.findField("targetDoctor");
					var manaDoctor = json.body.manaDoctor;
					if (manaDoctor && targetDoctor) {
						targetDoctor.setValue(manaDoctor);
						if (!json.body.manageUnits) {
							return;
						}
						var manageUnits = json.body.manageUnits;
						if (manageUnits && manageUnits.length == 1) {
							this.setManaUnit(manageUnits[0]);
						} else {
							// 该责任医生可能归属的管辖机构编码集合
							this.manageUnits = manageUnits;
							this.setManaUnit(null);
						}
					}
				}, this);

	},

	onBeforeSave : function(entryName, op, saveData) {
		var affirmType = saveData["affirmType"];
		if (affirmType == '1') {
			saveData['status'] = '4';
		} else if (affirmType == '2') {
			saveData['status'] = '9';
		} else {
			saveData['status'] = '1';
		}

		// var sourceArea = this.form.getForm().findField("sourceArea");
		// if (sourceArea) {
		// saveData["sourceArea_text"] = sourceArea.getRawValue();
		// }
		// var targetArea = this.form.getForm().findField("targetArea");
		// if (targetArea) {
		// saveData["targetArea_text"] = targetArea.getRawValue();
		// }
	},

	saveToServer : function(saveData) {
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveData)) {
			return;
		}
		if (this.initDataId == null) {
			this.op = "create";
		} else {
			this.op = "update";
		}

		if (!affirmType || affirmType.length == 0) {
			// 如果源和目标机构跟登陆人都没有瓜葛 不允许保存。
			var targetUnit = saveData["targetUnit"];
			var sourceUnit = saveData["sourceUnit"];
			var myUnit = this.mainApp.deptId;
			if (false == this.startWith(targetUnit, myUnit)
					&& false == this.startWith(sourceUnit, myUnit)) {
				Ext.Msg.alert("提示信息",
						"待迁移档案的原单位以及迁移的目标单位与您所在的机构没有任何关联，无法进行迁移操作！");
				return;
			}
		}
		var affirmType = saveData["affirmType"];
		var archiveType = saveData["archiveType"];
		// 家庭档案迁移确认时如果目标地址上已经有家庭，询问是否合并。
		if (archiveType == "2" && affirmType == "1") {
			var result = util.rmi.miniJsonRequestSync({
				serviceId : 'chis.simpleQuery',
				method:"execute",
				schema : "chis.application.fhr.schemas.EHR_FamilyRecord",
				queryCndsType : "",
				cnd : ['eq', ['$', 'regionCode'], ['s', saveData["targetArea"]]]
			}, this);
			if (result.code != 200) {
				this.processReturnMsg(result.code, result, msg,
						this.saveToServer);
				return;
			}
			var body = result.json.body;
			if (body && body.length > 0) {
				var family = body[0];

				if (family) {
					var existFamily = family.familyId;
					var existOwner = family.ownerName;
					if (!existOwner)
						existOwner = " ";
					Ext.Msg.show({
								title : '提示信息',
								msg : '该网格地址已经存在户主为[' + existOwner + ']的家庭['
										+ existFamily + '],是否需要合并',
								modal : true,
								width : 500,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										saveData["existFamily"] = existFamily;
										this.fireEvent("saveData", saveData);
									}
								},
								scope : this
							});
				} else {
					this.fireEvent("saveData", saveData);
				}
			} else {
				this.fireEvent("saveData", saveData);
			}
			// 个人档案申请保存时,查询该人是否户主,如果是则提示是否迁移整个家庭.迁移家庭则将本次迁移记录保存为家庭迁移。
		} else if (archiveType == "1"
				&& (!affirmType || affirmType.length == 0)) {
			var result = util.rmi.miniJsonRequestSync({
						serviceId : 'chis.simpleQuery',
						schema : "chis.application.hr.schemas.EHR_HealthRecord",
						method:"execute",
						queryCndsType : "",
						cnd : ['eq', ['$', 'phrId'],
								['s', saveData["archiveId"]]]
					}, this);
			if (result.code != 200) {
				this.processReturnMsg(result.code, result, msg,
						this.saveToServer);
				return;
			}
			var body = result.json.body;
			var healthRecord = body[0];
			if (healthRecord) {
				var masterFlag = healthRecord["masterFlag"];
				var familyId = healthRecord["familyId"];
				if (masterFlag == 'y' && familyId && familyId.length > 0) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '该人是户主,是否迁移整个家庭?',
								modal : true,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										saveData["archiveType"] = "2";
										saveData["archiveId"] = familyId;
										this.fireEvent("saveData", saveData);
									} else {
										// this.joinFamily(saveData);
										this.fireEvent("saveData", saveData);
									}
								},
								scope : this
							});
				} else {
					// this.joinFamily(saveData);
					this.fireEvent("saveData", saveData);
				}
			} else {
				// this.joinFamily(saveData);
				this.fireEvent("saveData", saveData);
			}
		} else {
			this.fireEvent("saveData", saveData);
		}
	},

	onSaveData : function(saveData) {
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction || "simpleSave",
					method:"execute",
					op : this.op,
					schema : this.entryName,
					body : saveData
				}, function(code, msg, json) {
					this.form.el.unmask();
					this.saving = false;
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData]);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						//this.initFormData(json.body);
						this.fireEvent("save", this.entryName, this.op, json,
								this.data);
						var win = this.getWin();
						if (win)
							win.hide();
					}
					this.op = "update";
				}, this);
	},

	initFormData : function(body) {
		chis.application.mov.script.ehr.EHRMoveUtilForm.superclass.initFormData
				.call(this, body);
		var targetDoctor = body.targetDoctor;
		var targetUnit = body.targetUnit;
		if (targetDoctor && targetDoctor.key && targetUnit && targetUnit.key) {
			this.setTargetUnitDisable();
		}
	},

	setTargetUnitDisable : function() {
		var combox = this.form.getForm().findField("targetUnit");
		combox.disable();
	}
});