$package("phis.application.ivc.script")

$import("phis.script.TableForm")

phis.application.ivc.script.TreatmentNumberForm = function(cfg) {
	cfg.width = 750;
	phis.application.ivc.script.TreatmentNumberForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeSave", this.onBeforeSave, this);
}
Ext.extend(phis.application.ivc.script.TreatmentNumberForm, phis.script.TableForm, {
			onReady : function() {
				phis.application.ivc.script.TreatmentNumberForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				this.SYHM = form.findField("SYHM");
				this.QSHM = form.findField("QSHM");
				if (this.op == 'update') {
					if (this.SYHM) {
						this.SYHM.on("focus", this.onSYHMFocus, this);
					}
				}
				if (this.op == 'create') {
					if (this.QSHM) {
						this.QSHM.on("blur", this.onQSHMBlur, this);
					}
				}
			},
			// add by caijy for checkbox
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
						if (it.id != "SYHM") {
							f.setDisabled("true");
						}
						var v = data[it.id]
						if (v != undefined) {
							if (f.getXType() == "checkbox") {
								var setValue = "";
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
									if (v == checkValue) {
										setValue = true;
									} else if (v == unCheckValue) {
										setValue = false;
									}
								}
								if (setValue == "") {
									if (v == 1) {
										setValue = true;
									} else {
										setValue = false;
									}
								}
								f.setValue(setValue);
							} else {
								if (it.dic && v == 0) {// add by yangl
									// 解决字典类型值为0(int)时无法设置的BUG
									v = "0";
								}
								f.setValue(v)
								if (it.dic && v != "0" && f.getValue() != v) {
									f.counter = 1;
									this.setValueAgain(f, v, it);
								}

							}
						}
						if (it.update == "false") {
							f.disable();
						}
					}
					this.setKeyReadOnly(true)
					this.focusFieldAfter(-1, 800)
				}
			},
			onSYHMFocus : function() {
				if (!this.SYHMVLAUE) {
					this.SYHMVLAUE = this.SYHM.getValue();
				}
			},
			onQSHMBlur : function() {
				this.SYHM.setValue(this.QSHM.getValue());
			},
			onBeforeSave : function(entryName, op, saveData) {
				var s = /^[0-9]+$/i;
				if (!s.test(saveData.QSHM) && saveData.QSHM != '') {
					MyMessageTip.msg("提示", "起始号码只能输入数字", true);
					return false;
				}
				if (!s.test(saveData.ZZHM) && saveData.ZZHM != '') {
					MyMessageTip.msg("提示", "终止号码只能输入数字", true);
					return false;
				}
				if (!s.test(saveData.SYHM) && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "使用号码只能输入数字", true);
					return false;
				}
				// 判断所有号码是否存在
				var dataRepeat = {
					"QSHM" : saveData.QSHM,
					"ZZHM" : saveData.ZZHM,
					"SYHM" : saveData.SYHM
				};
				if (this.initDataId) {
					dataRepeat["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "treatmentNumberService",
							serviceAction : "treatmentNumberVerification",
							schemaDetailsList : "MS_YGPJ",
							op : this.op,
							body : dataRepeat
						});
				if (r.code == 608) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 609) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 610) {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (saveData.QSHM.length != saveData.ZZHM.length
						&& saveData.QSHM != '' && saveData.ZZHM != '') {
					MyMessageTip.msg("提示", "起始号码和终止号码长度必须一致", true);
					return false;
				}
				if (saveData.QSHM.length != saveData.SYHM.length
						&& saveData.QSHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "起始号码和使用号码长度必须一致", true);
					return false;
				}
				if (saveData.ZZHM.length != saveData.SYHM.length
						&& saveData.ZZHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "终止号码和使用号码长度必须一致", true);
					return false;
				}
				if (parseInt(saveData.QSHM, 10) > parseInt(saveData.SYHM, 10)
						&& saveData.QSHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "起始号码不能大于使用号码", true);
					return false;
				}
				if (parseInt(saveData.ZZHM, 10) < parseInt(saveData.SYHM, 10)
						&& saveData.ZZHM != '' && saveData.SYHM != '') {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "终止号码 不能小于使用号码", true);
					return false;
				}
				// 判断号码是否段冲突
				var dataConflict = {
					"QSHM" : saveData.QSHM,
					"ZZHM" : saveData.ZZHM,
					"SYHM" : saveData.SYHM
				};
				if (this.initDataId) {
					dataConflict["JLXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "treatmentNumberService",
							serviceAction : "treatmentNumberRangeVerification",
							schemaDetailsList : "MS_YGPJ",
							op : this.op,
							body : dataConflict
						});
				if (r.code >= 300) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (parseInt(this.SYHMVLAUE, 10) > parseInt(saveData.SYHM, 10)) {
					this.SYHM.setValue(this.SYHMVLAUE);
					MyMessageTip.msg("提示", "使用号码要大于上次使用号码", true);
					return false;
				}
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							this.SYHMVLAUE = this.SYHM.getValue();
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
						}, this)
			},
			doNew : function() {
				this.op = "create"
				if (this.data) {
					this.data = {}
				}
				if (!this.schema) {
					return;
				}
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.setValue(it.defaultValue)
						// @@ 2010-01-07 modified by chinnsii, changed the
						// condition
						// "it.update" to "!=false"
						if (!it.fixed && !it.evalOnServer) {
							f.enable();
						} else {
							f.disable();
						}

						if (it.type == "date") { // ** add by yzh 20100919 **
							if (it.minValue)
								f.setMinValue(it.minValue)
							if (it.maxValue)
								f.setMaxValue(it.maxValue)
						}
						if (it.id == "LYRQ") {
							f.setValue(new Date().format('Y-m-d H:i:s'));
						}
						// add by yangl 2012-06-29
						if (it.dic && it.dic.defaultIndex) {
							if (f.store.getCount() == 0)
								return;
							if (isNaN(it.dic.defaultIndex)
									|| f.store.getCount() <= it.dic.defaultIndex)
								it.dic.defaultIndex = 0;
							f.setValue(f.store.getAt(it.dic.defaultIndex)
									.get('key'));
						}
					}
				}
				this.setKeyReadOnly(false)
				this.resetButtons(); // ** add by yzh **
				this.fireEvent("doNew")
				this.focusFieldAfter(-1, 800)
				this.validate()
			}
		})