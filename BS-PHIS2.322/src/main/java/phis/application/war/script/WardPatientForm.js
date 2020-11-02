$package("phis.application.war.script")

$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.war.script.WardPatientForm = function(cfg) {
	phis.application.war.script.WardPatientForm.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.war.script.WardPatientForm, phis.script.SimpleForm, {
	initPanel : function(sc) {

		if (this.form) {
			if (!this.isCombined) {
				this.addPanelToWin();
			}
			return this.form;
		}
		this.form = new Ext.FormPanel({
					labelWidth : 55,
					frame : true,
					bodyStyle : 'padding:5px 5px 0',
					items : [{
								xtype : 'fieldset',
								title : '基本信息',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 3,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('JBXX')
							}, {
								xtype : 'fieldset',
								title : '在院信息',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 3,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('ZYXX')
							}, {
								xtype : 'fieldset',
								title : '病人医师',
								autoHeight : true,
								layout : 'tableform',
								layoutConfig : {
									columns : 3,
									tableAttrs : {
										border : 0,
										cellpadding : '2',
										cellspacing : "2"
									}
								},
								defaultType : 'textfield',
								items : this.getItems('BRYS')
							}]
				});
		if (!this.isCombined) {
			this.addPanelToWin();
		}
		this.form.on("afterrender", this.onReady, this)
		return this.form
	},
	onReady : function() {
		if (this.autoLoadData) {
			this.loadData();
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
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				f.setValue(it.defaultValue)
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
			}
		}
		this.setKeyReadOnly(false)
		this.fireEvent("doNew")
		this.focusFieldAfter(-1, 800)
		this.validate()
	},

	getItems : function(para) {
		var ac = util.Accredit;
		var MyItems = [];
		var schema = null;
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
		if (re.code == 200) {
			schema = re.schema;
		} else {
			this.processReturnMsg(re.code, re.msg, this.initPanel)
			return;
		}
		var items = schema.items
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if (!it.layout || it.layout != para) {
				continue;
			}
			if ((it.display == 0 || it.display == 1) || !ac.canRead(it.acValue)) {
				// alert(it.acValue);
				continue;
			}
			var f = this.createField(it)
			f.labelSeparator = ":"
			f.index = i;
			f.anchor = it.anchor || "100%"
			delete f.width

			f.colspan = parseInt(it.colspan)
			f.rowspan = parseInt(it.rowspan)
			MyItems.push(f);
		}
		return MyItems;
	},
	expand : function() {
		this.win.center();
	},
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
				var v = this.data[it.id] // ** modify by yzh 2010-08-04
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
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
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
					if (f.getXType() == "datefield" && v != null && v != "") {
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
