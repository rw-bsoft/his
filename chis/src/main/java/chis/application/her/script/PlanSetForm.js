$package("chis.application.her.script")

$import("chis.script.BizTableFormView")

chis.application.her.script.PlanSetForm = function(cfg) {
	chis.application.her.script.PlanSetForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadDataByLocal", this.onLoadDataByLocal, this);
	this.on("save", this.onSave, this);
}

Ext.extend(chis.application.her.script.PlanSetForm,
		chis.script.BizTableFormView, {
			onReady : function() {
				chis.application.her.script.PlanSetForm.superclass.onReady
						.call(this);
				var beginDate = this.form.getForm().findField("beginDate");
				if (beginDate) {
					beginDate.on("select", this.onSelectDate, this);
				}

				var executePerson = this.form.getForm()
						.findField("executePerson");
				if (executePerson) {
					executePerson.tree.on("checkchange",
							function(node, checked) {
								var parentNode = node.parentNode;
								var nodes = parentNode.childNodes;
								if (!this.getCheckFlag(node)) {
									for (var i = 0; i < nodes.length; i++) {
										if (nodes[i].id != node.id) {
											nodes[i].getUI().check(false);
										}
									}
								}

								var values = executePerson.getValue()
										.split(",");
								var texts = [];
								for (var c = 0; c < values.length; c++) {
									var node = executePerson.tree
											.getNodeById(values[c]);
									texts.push(node.text);
								}
								executePerson.setRawValue(texts
										.join(executePerson.tree.separator));

							}, this)
				}
			},

			getCheckFlag : function(node) {
				var flag = false;
				var parentNode = node.parentNode;
				var nodes = parentNode.childNodes;
				for (var i = 0; i < nodes.length; i++) {
					var key = nodes[i].attributes["key"];
					if (this.fireEvent("check", key, this)) {
						node.getUI().check(false);
						nodes[i].getUI().check(true);
						flag = true
						break;
					}
				}
				return flag;
			},

			doNew : function() {
				chis.application.her.script.PlanSetForm.superclass.doNew
						.call(this);
				var beginDate = this.form.getForm().findField("beginDate");
				beginDate.setMinValue(new Date());
			},

			onSelectDate : function(combo) {
				var endDate = this.form.getForm().findField("endDate");
				if (endDate) {
					endDate.setMinValue(combo.value);
				}
				endDate.validate();
			},

			onLoadDataByLocal : function(entryName, data, op) {
				var status = data.status.key;
				if (status == "1") {
					this.setButton([0], false);
				} else {
					this.setButton([0], true);
				}
				var beginDate = this.form.getForm().findField("beginDate");
				var planDate = this.form.getForm().findField("planDate");
				if (beginDate) {
					beginDate.setMinValue(planDate.getValue());
					beginDate.clearInvalid();
				}
				this.onSelectDate(beginDate);

				var executePerson = this.form.getForm()
						.findField("executePerson");
				if (executePerson) {
					executePerson.clearInvalid();
				}
				this.setEndDate(data.endDate);
			},

			setEndDate : function(endDate) {
				if (!endDate) {
					return;
				}
				var nowDate = this.mainApp.serverDate;
				var form = this.form.getForm();
				var beginDate = form.findField("beginDate");
				var executeResult = form.findField("executeResult");
				var endDateField = form.findField("endDate");
				var planContent = form.findField("planContent");
				var planType = form.findField("planType");
				var executePerson = form.findField("executePerson");
				if (nowDate > endDate) {
					executeResult.enable();
					beginDate.disable();
					endDateField.disable();
					endDateField.clearInvalid();
					planContent.disable();
					planType.disable();
					executePerson.disable();
				} else {
					executeResult.disable();
					endDateField.enable();
					planContent.enable();
					planType.enable();
					executePerson.enable();
				}
			},

			onSave : function(entryName, op, json, data) {
				var endDate = this.form.getForm().findField("endDate")
						.getValue()
				endDate = endDate.format("Y-m-d")
				this.setEndDate(endDate);
				this.fireEvent("formSave", data.setId);
			},

			onBeforeSave : function(entryName, op, saveData) {
				if (saveData.beginDate > saveData.endDate) {
					Ext.Msg.alert("提示", "执行起始时间不能小于执行终止时间!")
					return false
				}
			},

			setButton : function(m, flag) {
				var btns;
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					btns = this.form.getTopToolbar().items;
				} else {
					btns = this.form.buttons;
				}

				if (btns) {
					var n = btns.getCount();
					for (var i = 0; i < m.length; i++) {
						var btn = btns.item(m[i]);
						if (btn) {
							(flag) ? btn.enable() : btn.disable();
						}
					}
				}
			},

			reSetField : function(flag) {
				var form = this.form.getForm();
				var planType=form.findField("planType");
				var planContent=form.findField("planContent");
				var beginDate=form.findField("beginDate");
				var endDate=form.findField("endDate");
				var executePerson=form.findField("executePerson");
				var executeResult=form.findField("executeResult");
				if(flag){
					planType.enable();
					planContent.enable();
					beginDate.enable();
					endDate.enable();
					executePerson.enable();
					executeResult.disable();
				}else{
					planType.disable();
					planContent.disable();
					beginDate.disable();
//					endDate.disable();
//					executePerson.disable();
					executeResult.enable();
				}
			}

		})