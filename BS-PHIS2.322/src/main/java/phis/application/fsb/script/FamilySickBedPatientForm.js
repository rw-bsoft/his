$package("phis.application.fsb.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FamilySickBedPatientForm = function(cfg) {
	cfg.colCount = 4;
	cfg.labelWidth = 60;
	phis.application.fsb.script.FamilySickBedPatientForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedPatientForm,
		phis.script.TableForm, {
			getSaveData : function() {
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items
				if (items) {
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
							// add by caijy from checkbox
							if (f.getXType() == "checkbox") {
								var checkValue = 1;
								var unCheckValue = 0;
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
								}
								if (v == true) {
									v = checkValue;
								} else {
									v = unCheckValue;
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
							if (f.validate) {
								if (!f.validate()) {
									MyMessageTip.msg("提示", f.fieldLabel + ":"
													+ f.activeError, true)
									return false;
								}
							}
						}
						if (it.type && it.type == "int") {
							v = (v == "0" || v == "" || v == undefined)
									? 0
									: parseInt(v);
						}
						values[it.id] = v;
					}
				}
				return values;
			}

		});
