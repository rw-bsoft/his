/**
 * 儿童户籍地址迁移表单公用页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.cdh")
$import("chis.script.BizTableFormView", "util.widgets.LookUpField")
chis.application.mov.script.cdh.CDHMoveUtilForm = function(cfg) {
	cfg.fldDefaultWidth = 132;
	cfg.autoLoadData = true
	chis.application.mov.script.cdh.CDHMoveUtilForm.superclass.constructor.apply(this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(chis.application.mov.script.cdh.CDHMoveUtilForm, chis.script.BizTableFormView, {

	onReady : function() {
		chis.application.mov.script.cdh.CDHMoveUtilForm.superclass.onReady.call(this);
		var form = this.form.getForm();

		var targetOwnerArea = form.findField("targetOwnerArea")
		if (targetOwnerArea) {
			targetOwnerArea.on("select", function(field) {
						var value = field.getValue();
						this.setTargetHomeMess(value);
					}, this);
		}

		var targetHomeAddress = form.findField("targetHomeAddress")
		if (targetHomeAddress) {
			targetHomeAddress.on("select", this.onHomeAddressSelect, this);
		}

		var targetCdhDoctorId = form.findField("targetCdhDoctorId")
		if (targetCdhDoctorId) {
			targetCdhDoctorId.on("select", this.changeManaUnit, this);
			targetCdhDoctorId.on("change", this.doctorChange, this)
		}

		var targetManaUnitId = form.findField("targetManaUnitId");
		if (targetManaUnitId) {
			targetManaUnitId.on("beforeselect", this.checkManaUnit, this)
			targetManaUnitId.on("expand", this.filterManaUnit, this);
			targetManaUnitId.on("beforeQuery", this.onQeforeQuery, this)
		}
	},

	doctorChange : function(field) {
		var rawValue = field.getRawValue();
		if (rawValue) {
			return;
		} else {
			field.reset();
			this.manageUnits = null;
		}
		var combox = this.form.getForm().findField("targetManaUnitId");
		if (!combox) {
			return;
		}
		combox.enable();
		combox.reset();
	},

	changeManaUnit : function(combo, node) {
		combo.startValue = combo.getValue();
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
				})
		this.setManaUnit(result.json.manageUnit)
	},

	setManaUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("targetManaUnitId");
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

	initFormData : function(body) {

		chis.application.mov.script.cdh.CDHMoveUtilForm.superclass.initFormData
				.call(this, body);
						
		var toa = body.targetOwnerArea;
		if (toa && toa.key) {
			this.setTargetHomeMess(toa.key);
		}
		var soa = body.sourceOwnerArea;
		if (soa && soa.key) {
			this.setSourceHomeMess(soa.key);
		}

		var tcdh = body.targetCdhDoctorId;
		var tunit = body.targetManaUnitId;
		if (tcdh && tcdh.key && tunit && tunit.key) {
			this.setTargetUnitDisable();
		}

	},

	onBeforeSave : function(entryName, op, saveData) {
		// ** 判断现户籍地址和原户籍地址
		var sourceHomeAddress = saveData["sourceHomeAddress"];
		var targetHomeAddress = saveData["targetHomeAddress"];
		var sourceOwnerArea = saveData["sourceOwnerArea"];
		var targetOwnerArea = saveData["targetOwnerArea"];
		if (sourceHomeAddress == targetHomeAddress
				&& sourceOwnerArea == targetOwnerArea) {
			Ext.Msg.alert("提示", "现户籍地址与原户籍地址相同，且现归属地和原归属地相同，无需迁移。")
			return false;
		}
		// ** 判断原单位与现单位
		var targetUnit = saveData["targetManaUnitId"];
		var sourceUnit = saveData["sourceManaUnitId"];
		var myUnit = this.mainApp.deptId;
		if (false == this.startWith(targetUnit, myUnit)
				&& false == this.startWith(sourceUnit, myUnit)) {
			Ext.Msg.alert("提示信息", "待迁移档案的原单位以及迁移的目标单位与您所在的机构没有任何关联，无法进行迁移操作！");
			return false;
		}
	},

	onHomeAddressSelect : function(targetField) {
		var form = this.form.getForm();
		var target = targetField.getValue();
		var sourceField = form.findField("sourceHomeAddress");
		var source = sourceField.getValue();
		if (target == source) {
			Ext.Msg.show({
						title : "提示",
						msg : "原户籍地址与现户籍地址相同，是否继续？",
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "no") {
								var targetUnit = form
										.findField("targetManaUnitId");
								var targetDoctor = form
										.findField("targetCdhDoctorId");
								targetField.reset();
								targetUnit.reset();
								targetDoctor.reset();
							} else {
								this.getCdhDoctor(target);
							}
						},
						scope : this
					})
		} else {
			this.getCdhDoctor(target);
		}
	},

	filterManaUnit : function(field) {
		var tree = field.tree;
		tree.getRootNode().reload();
		var field = this.form.getForm().findField("targetCdhDoctorId");
		var rawValue = field.getRawValue();
		if (!rawValue) {
			return;
		}
		if (!this.manageUnits) {
			return;
		}
		tree.expandAll();
		tree.on("expandnode", function(node) {
					var childs = node.childNodes
					for (var i = childs.length - 1; i >= 0; i--) {
						var child = childs[i]
						child.on("load", function() {
									tree.filter.filterBy(this.filterTree, this)
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

	checkManaUnit : function(comb, node) {
		var key = node.attributes['key'];
		if (key.length == 9) {
			return true;
		} else {
			return false;
		}
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

	setTargetHomeMess : function(value) {
		var form = this.form.getForm();
		var homeAddress = form.findField("targetHomeAddress");
		// 若本市 homeAddress为户籍地址
		if (value == "11" || !value) {
			Ext.getCmp(homeAddress.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + "现户籍地址" + ":</span>");
			homeAddress.validate()
		} else {// 若为外省等，则homeAddress为暂住地址，
			Ext.getCmp(homeAddress.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + "现暂住地址" + ":</span>");
			homeAddress.validate()
		}
	},

	setSourceHomeMess : function(value) {
		var form = this.form.getForm();
		var homeAddress = form.findField("sourceHomeAddress");
		// 若本市 homeAddress为户籍地址
		if (value == "11" || !value) {
			Ext.getCmp(homeAddress.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + "原户籍地址" + ":</span>");
			homeAddress.validate()
		} else {// 若为外省等，则homeAddress为暂住地址，
			Ext.getCmp(homeAddress.id).getEl().up('.x-form-item')
					.child('.x-form-item-label')
					.update("<span style='color:red'>" + "原暂住地址" + ":</span>");
			homeAddress.validate()
		}
	},

	getCdhDoctor : function(value) {
		if (value == null || value == "") {
			return;
		}
		var form = this.form.getForm();
		this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : 'chis.childrenHealthRecordService',
					serviceAction : "getCdhDoctorInfo",
					method:"execute",
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
					var targetCdhDocField = form.findField("targetCdhDoctorId");
					var cdhDoctor = json.body.cdhDoctor;
					if (cdhDoctor && targetCdhDocField) {
						targetCdhDocField.setValue(cdhDoctor);
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

	getManaUnit : function(value) {
		var form = this.form.getForm();
		this.form.el.mask("正在查询数据,请稍后...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : "chis.childrenHealthRecordService",
					serviceAction : "getManageUnit",
					method:"execute",
					body : {
						"cdhDoctorId" : value
					}
				}, function(code, msg, json) {
					this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					if (json.body) {
						var data = json.body;
						var manaUnitId = data.manaUnitId;
						var targetManaUnitId = form
								.findField("targetManaUnitId");
						if (targetManaUnitId) {
							targetManaUnitId.setValue(manaUnitId);
							// targetManaUnitId.disable();
						} else {
							targetManaUnitId.reset();
							// targetManaUnitId.enable();
						}
					}
				}, this)

	},

	setTargetUnitDisable : function() {
		var combox = this.form.getForm().findField("targetManaUnitId");
		combox.disable();
	},

	onWinShow : function() {
		this.win.doLayout();
		this.manageUnits = null;
		if (this.form) {
			this.validate();
		}
	}
})