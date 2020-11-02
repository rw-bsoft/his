/**
 * 儿童体格检查中医遍体表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup")
$import("chis.script.BizTableFormView")
chis.application.cdh.script.checkup.ChildrenCheckupDescriptionForm = function(cfg) {
	cfg.colCount = 1;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 500;
	cfg.labelWidth = 100;
	chis.application.cdh.script.checkup.ChildrenCheckupDescriptionForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadNoData", this.onLoadNoData, this)
}

Ext.extend(chis.application.cdh.script.checkup.ChildrenCheckupDescriptionForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				var checkupStage = this.exContext.args.checkupStage;
				var otherField = this.form.getForm().findField("other");
				var description = this.form.getForm().findField("description");
				var description1 = this.form.getForm()
						.findField("description1");
				var description2 = this.form.getForm()
						.findField("description2");
				var description3 = this.form.getForm()
						.findField("description3");
				if (description1) {
					description1.on("select", this.onDescriptionSelect, this);
				}
				if (description2) {
					description2.on("select", this.onDescriptionSelect, this);
				}
				if (description3) {
					description3.on("select", this.onDescriptionSelect, this);
				}
			},
			onDescriptionSelect : function(combo, r, index) {
				var v = combo.getValue();
				var otherField = this.form.getForm().findField("other");
				if (v.indexOf("9") > -1) {
					otherField.enable();
				} else {
					otherField.setValue();
					otherField.disable();
				}
			},
			changeFieldByAge : function(form) {
				var checkupStage = this.exContext.args.checkupStage;
				var otherField = this.form.getForm().findField("other");
				var description = this.form.getForm().findField("description");
				var description1 = this.form.getForm()
						.findField("description1");
				var description2 = this.form.getForm()
						.findField("description2");
				var description3 = this.form.getForm()
						.findField("description3");
				if (checkupStage == '6' || checkupStage == '12') {
					otherField.show();
					otherField.disable();
					description1.show();
					description2.setValue();
					description3.setValue();
					description.setValue();
					description2.hide();
					description3.hide();
					description.hide();
				} else if (checkupStage == '18' || checkupStage == '24') {
					description2.show();
					description1.setValue();
					description3.setValue();
					description.setValue();
					description1.hide();
					description3.hide();
					description.hide();
					otherField.show();
					otherField.disable();
				} else if (checkupStage == '30' || checkupStage == '36') {
					description3.show();
					description1.setValue();
					description2.setValue();
					description.setValue();
					description1.hide();
					description2.hide();
					description.hide();
					otherField.show();
				} else {
					description.show();
					description1.setValue();
					description2.setValue();
					description3.setValue();
					description1.hide();
					description2.hide();
					description3.hide();
					otherField.setValue();
					otherField.hide();
				}
				otherField.disable();
			},
			onBeforeSave : function(entryName, op, saveData) {
				saveData.checkupId = this.exContext.args[this.checkupType
						+ "_param"].checkupId
						|| this.exContext.args.checkupId;
				saveData.checkupType = this.checkupType;
				
				saveData.phrId = this.exContext.ids["CDH_HealthCard.phrId"];

				var checkupStage = this.exContext.args.checkupStage;
				var otherField = this.form.getForm().findField("other");
				var description = this.form.getForm().findField("description");
				var description1 = this.form.getForm()
						.findField("description1");
				var description2 = this.form.getForm()
						.findField("description2");
				var description3 = this.form.getForm()
						.findField("description3");
				if (checkupStage == '6' || checkupStage == '12') {
					saveData.description = description1.getValue();
				} else if (checkupStage == '18' || checkupStage == '24') {
					saveData.description = description2.getValue();
				} else if (checkupStage == '30' || checkupStage == '36') {
					saveData.description = description3.getValue();
				} else {
					saveData.description = description.getValue();
				}
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"checkupId" : this.exContext.args[this.checkupType
							+ "_param"].checkupId,
					"checkupType" : this.checkupType
				};
			},
			initFormData : function(data) {
				this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				var checkupStage = this.exContext.args.checkupStage;
				if (checkupStage == '6' || checkupStage == '12') {
					data.description1 = data.description;
				} else if (checkupStage == '18' || checkupStage == '24') {
					data.description2 = data.description;
				} else if (checkupStage == '30' || checkupStage == '36') {
					data.description3 = data.description;
				} else {
					data.description = data.description;
				}
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
						if (v != undefined) {
							if ((it.type == 'date' || it.xtype == 'datefield')
									&& typeof v == 'string' && v.length > 10) {
								v = v.substring(0, 10)
							} else if (it.type == 'datetime') {
								v = v.substring(0, 16)
							}

							f.setValue(v)

						}

						if (this.initDataId) {
							if (it.update == false || it.update == "false") {
								f.disable();
							}
						}
						if (it.id == "description1"&&(checkupStage == '6' || checkupStage == '12')) {
							this.onDescriptionSelect(f);
						}
						if (it.id == "description2"&&(checkupStage == '18' || checkupStage == '24')) {
							this.onDescriptionSelect(f);
						}
						if (it.id == "description3"&&(checkupStage == '30' || checkupStage == '36')) {
							this.onDescriptionSelect(f);
						}
					}
				}
				this.setKeyReadOnly(true)
				this.resetButtons(); // ** 用于页面按钮权限控制
				this.focusFieldAfter(-1, 800)
			},

			onLoadNoData : function() {
				this.doNew();
			}

		})