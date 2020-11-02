$package("chis.application.conf.script")
$import("chis.script.BizFieldSetFormView")
chis.application.conf.script.SystemConfigUtilForm = function(cfg) {
	chis.application.conf.script.SystemConfigUtilForm.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.SystemConfigUtilForm, chis.script.BizFieldSetFormView, {

			loadData : function() {
				if (!this.loadAction) {
					return;
				}
				chis.application.conf.script.SystemConfigUtilForm.superclass.loadData
						.call(this);
				this.resetFieldReadOnly();
				this.resetButtonsReadOnly();
			},

			getLoadRequest : function() {
				return {};
			},

			createField : function(it) {
				var cfg = chis.application.conf.script.SystemConfigUtilForm.superclass.createField
						.call(this, it);
				if (it.xtype == "checkbox") {
					cfg.hideLabel = true;
					cfg.boxLabel = it.alias;
				} else if (it.xtype == "label") {
					cfg.text = it.alias;
					cfg.height = 80;
				} else {
					cfg.fieldLabel = it.alias;
				}
				return cfg;
			},

			getFormData : function() {
				if (this.saving) {
					return
				}
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items

				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var v = this.data[it.id] || it.defaultValue
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							if (it.dic) {
								
								values[it.id + "_text"] = f.getRawValue();
							}
						}
						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示信息", it.alias + "不能为空")
								return;
							}
						}
						if (typeof v == "object") {
							values[it.id] = v.key
						} else {
							values[it.id] = v
						}
						if (it.type == "date" && values[it.id]
								&& values[it.id] != it.defaultValue)
							values[it.id] = values[it.id].format("Y-m-d");
					}
				}
				return values;
			},

			resetFieldReadOnly : function() {
				if (!this.fieldReadOnly) {
					return;
				}
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var id = it.id;
					var readOnly = this.fieldReadOnly[id + "_readOnly"] || false;
					var field = this.form.getForm().findField(id);
					if (field) {
						field.setDisabled(readOnly);
					} else {
						continue;
					}
				}
			},

			resetButtonsReadOnly : function() {
				if (this.readOnly == null) {
					return;
				}
				if (this.showButtonOnTop && this.form.getTopToolbar()) {
					var btns = this.form.getTopToolbar().items;
					if (!btns) {
						return;
					}
					var n = btns.getCount();
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i);
						if (btn.prop&&btn.prop.notReadOnly) {
							continue;
						}
						var status = this.readOnly;
						if (status == null) {
							return;
						}
						this.changeButtonStatus(btn, !status);
					}
				} else {
					var btns = this.form.buttons;
					if (!btns) {
						return;
					}
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i];
						if (btn.prop&&btn.prop.notReadOnly) {
							continue;
						}
						var status = this.readOnly;
						if (status == null) {
							return;
						}
						this.changeButtonStatus(btn, !status);
					}
				}
			}
		});