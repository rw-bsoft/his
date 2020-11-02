$package("chis.application.per.script.setmeal");

$import("chis.script.BizTableFormView");

chis.application.per.script.setmeal.CheckupSetMealDetailForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.entryName = "chis.application.per.schemas.PER_ComboDetail";
	chis.application.per.script.setmeal.CheckupSetMealDetailForm.superclass.constructor
			.apply(this, [cfg]);
	this.width = 500;
	this.height = 100; 
	this.saveServiceId = "chis.checkupSetMealService";
	this.saveAction = "saveSetMealDetail";
	this.on("save", this.onSave, this);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.per.script.setmeal.CheckupSetMealDetailForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				chis.application.per.script.setmeal.CheckupSetMealDetailForm.superclass.doNew
						.call(this);
				this.initDataId = null;
				this.setMealIsUsed = null;
				this.validate();
			},
			loadData : function() {
				if (this.op == "create") {
					this.doNew();
					return;
				}
				var itemId = this.form.getForm().findField("itemId");
				var iv = this.exContext.args.setmealDetailData.itemId;
				var itemValue = [];
				if (iv) {
					var values = iv.split(",");
					for (var i = 0; i < values.length; i++) {
						if (values[i].length == 2
								&& !this.checkNeedNode(values[i], iv)) {
							continue;
						}
						itemValue.push(values[i])
					}
				}
//				this.exContext.args.setmealDetailData.itemId = itemValue;
				var setmealDetailFormData = this.castListDataToForm(
						this.exContext.args.setmealDetailData, this.schema);
				this.initFormData(setmealDetailFormData);
				this.validate();
			},
			checkNeedNode : function(val, values) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.checkupSetMealService",
							serviceAction : "checkNeedNode",
							method : "execute",
							val : val,
							values : values
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.checkNeedNode);
					return;
				}
				return result.json.needNode;
			},
			doSave : function() {
				this.data.comboId = this.comboId;
				var itemId = this.form.getForm().findField("itemId");
				var iv = itemId.getValue();
				if (iv && iv.length > 1990) {
					itemId.addClass("x-form-invalid");
					Ext.Msg.alert("提示", "项目编号超过最大长度");
					return;
				}
				chis.application.per.script.setmeal.CheckupSetMealDetailForm.superclass.doSave
						.call(this);
			},
			getSaveRequest : function(saveData) {
				var projectOfficeId = this.form.getForm()
						.findField("projectOfficeId");
				var projectOfficeId_text = projectOfficeId.getRawValue();
				saveData.projectOfficeId_text = projectOfficeId_text;
				return saveData;
			},
			onSave : function(entryName, op, json, data) {
				var itemId = this.form.getForm().findField("itemId");
				data.itemId_text = itemId.getRawValue();
				Ext.apply(data, json.body);
				this.fireEvent("saveAfter", data);
				// this.doCancel();
			},
			onReady : function() {
				var projectOfficeId = this.form.getForm()
						.findField("projectOfficeId");
				if (projectOfficeId) {
					projectOfficeId.on("beforeSelect", this.onBeforeSelect,
							this);
				}
				var itemId = this.form.getForm().findField("itemId");
				if (itemId) {
//					itemId.tree.on("check", this.onTreeCheckBox, this);
				}
				chis.application.per.script.setmeal.CheckupSetMealDetailForm.superclass.onReady
						.call(this);
			},
			onBeforeSelect : function(combo, record, index) {
				var projectOfficeId = this.form.getForm()
						.findField("projectOfficeId");
				var officeName = projectOfficeId.getRawValue();
				if (this.fireEvent("projectOfficeBeforeSelect", record)) {
					Ext.Msg.alert("科室代码重复", "科室代码[" + officeName + "]已经存在!");
				}
			},
			onTreeCheckBox : function(node, checked) {
				var parentNode = node.parentNode;
				if (parentNode && parentNode.childNodes) {
					var nodes = parentNode.childNodes;
					for (var i = 0; i < nodes.length; i++) {
						if (nodes[i].getUI().checkbox.checked) {
							if (this.setMealIsUsed) {
								nodes[i].getUI().checkbox.disabled = true;
							} else {
								nodes[i].getUI().checkbox.disabled = false;
							}
						} else {
							nodes[i].getUI().checkbox.disabled = false;
						}
					}
				}
			},
			onWinShow : function() {
				var itemId = this.form.getForm().findField("itemId");
				itemId.setValue();
				itemId.setRawValue();
				this.loadData();
			}
		});