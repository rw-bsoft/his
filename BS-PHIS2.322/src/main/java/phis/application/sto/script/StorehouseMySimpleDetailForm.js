$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.StorehouseMySimpleDetailForm = function(cfg) {
	this.showButtonOnTop = false;
	this.conditionId = "CKFS";// list带过来的需要回填的下拉框
	this.disabledField = ["CKBZ", "CKRQ"];// 如果是查看 界面需要灰掉的标签ID
	phis.application.sto.script.StorehouseMySimpleDetailForm.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.sto.script.StorehouseMySimpleDetailForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.sto.script.StorehouseMySimpleDetailForm.superclass.onReady
						.call(this);
				if (!this.conditionId) {
					return;
				}
				var form = this.form.getForm();
				if (form.findField(this.conditionId)) {
					form.findField(this.conditionId).store.on("load",
							this.fillCondition, this);
				}
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
				form.reset();
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (!(arguments[0] == 1)) { // whether set defaultValue,
													// it will be setted when
													// there is no args.
							var dv = it.defaultValue;
							if (dv) {
								if ((it.type == 'date' || it.xtype == 'datefield')
										&& typeof dv == 'string'
										&& dv.length > 10) {
									dv = dv.substring(0, 10);
								}
								f.setValue(dv);
							}
						}
						if (it.type == "datetime") {
							f.setValue(Date.getServerDateTime());
						}
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
				// this.focusFieldAfter(3, 800)
				this.validate()
				if (this.conditionId) {
				this.fillCondition();
				}
			},
			// 新增页面打开出库方式选中带过来的值
			fillCondition : function() {
				if (this.selectValue && this.op == "create") {
					var form = this.form.getForm();
					form.findField(this.conditionId).setValue(this.selectValue);
				}
			},
			onLoadData : function() {
				var count = this.disabledField.length;
				for (var i = 0; i < count; i++) {
					if (this.isRead) {
						this.form.getForm().findField(this.disabledField[i])
								.setDisabled(true);
					} else {
						this.form.getForm().findField(this.disabledField[i])
								.setDisabled(false);
					}
				}
			},
			// 重写为了打开页面自动增行后光标不跳到form上
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
								if (it.dic && v !== "" && v === 0) {// add by
									// yangl
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
					// this.focusFieldAfter(-1, 800)
				}
			}
		})