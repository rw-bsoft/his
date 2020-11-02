$package("phis.application.cfg.script")


$import("phis.script.TableForm")

phis.application.cfg.script.ConfigHsqxYgForm = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 280;
	cfg.remoteUrl = 'Staff'
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{#}.</td><td width="150px">{PERSONNAME}</td><td width="120px">{OFFICENAME}</td>';
	phis.application.cfg.script.ConfigHsqxYgForm.superclass.constructor.apply(this,
			[cfg])
	this.ygid = 0;
}
Ext.extend(phis.application.cfg.script.ConfigHsqxYgForm, phis.script.TableForm, {
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count',
					id : 'mdssearch'
				}, [{
							name : 'PERSONID'
						}, {
							name : 'PERSONNAME'
						}, {
							name : 'OFFICENAME'
						}]);
	},
	setBackInfo : function(obj, r) {
		var form = this.form.getForm();
		form.findField("PERSONNAME").setValue(r.get("PERSONNAME"));
		form.findField("PERSONID").setValue(r.get("PERSONID"));
		this.ygid = r.get("PERSONID");
		obj.collapse();
	},
	createField : function(it) {
		var ac = util.Accredit;
		var defaultWidth = this.fldDefaultWidth || 200
		// alert(defaultWidth)
		var cfg = {
			name : it.id,
			fieldLabel : it.alias,
			xtype : it.xtype || "textfield",
			vtype : it.vtype,
			width : defaultWidth,
			value : it.defaultValue,
			enableKeyEvents : it.enableKeyEvents,
			validationEvent : it.validationEvent
		}
		cfg.listeners = {
			specialkey : this.onFieldSpecialkey,
			scope : this
		}
		if (it.inputType) {
			cfg.inputType = it.inputType
		}
		if (it['not-null']) {
			cfg.allowBlank = false
			cfg.invalidText = "必填字段"
			cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
					+ "</span>"
		}
		if (it['showRed']) {
			cfg.fieldLabel = "<span style='color:red'>" + cfg.fieldLabel
					+ "</span>"
		}
		if (it.fixed || it.fixed) {
			cfg.disabled = true
		}
		if (it.pkey && it.generator == 'auto') {
			cfg.disabled = true
		}
		if (it.evalOnServer && ac.canRead(it.acValue)) {
			cfg.disabled = true
		}
		if (this.op == "create" && !ac.canCreate(it.acValue)) {
			cfg.disabled = true
		}
		if (this.op == "update" && !ac.canUpdate(it.acValue)) {
			cfg.disabled = true
		}
		if (it.properties.mode == "remote") {
			return this.createRemoteDicField(it);
		} else if (it.dic) {
			if (it.dic.render == "TreeCheck") {
				if (it.length) {
					cfg.maxLength = it.length;
				}
			}

			it.dic.src = this.entryName + "." + it.id
			it.dic.defaultValue = it.defaultValue
			it.dic.width = defaultWidth
			var combox = this.createDicField(it.dic)
			Ext.apply(combox, cfg)
			combox.on("specialkey", this.onFieldSpecialkey, this)
			return combox;
		}

		if (it.length) {
			cfg.maxLength = it.length;
		}

		if (it.xtype) {
			return cfg;
		}
		switch (it.type) {
			case 'int' :
			case 'double' :
			case 'bigDecimal' :
				cfg.xtype = "numberfield"
				if (it.type == 'int') {
					cfg.decimalPrecision = 0;
					cfg.allowDecimals = false
				} else {
					cfg.decimalPrecision = it.precision || 2;
				}
				if (it.minValue) {
					cfg.minValue = it.minValue;
				} else {
					cfg.minValue = 0;
				}
				if (it.maxValue) {
					cfg.maxValue = it.maxValue;
				}
				break;
			case 'date' :
				cfg.xtype = 'datefield'
				cfg.emptyText = "请选择日期"
				cfg.format = 'Y-m-d'
				break;
			case 'text' :
				cfg.xtype = "htmleditor"
				cfg.enableSourceEdit = false
				cfg.enableLinks = false
				cfg.width = 700
				cfg.height = 450
				break;
		}
		return cfg;
	},
	saveToServer : function(saveData) {
		if (!this.ygid)
			return;
		var form = this.form.getForm();
		var HSZBZ = form.findField("HSZBZ").getValue();
		if (!HSZBZ) {
			HSZBZ = 0;
		}
		saveData = {
			"YGID" : this.ygid,
			"KSDM" : "0",
			"JGID" : this.mainApp['phisApp'].deptId,
			"MRZ" : "0",
			"HSZBZ" : HSZBZ
		};
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveData)) {
			return;
		}
		// if (this.initDataId == null) {
		// this.op = "create";
		// }
		this.saving = true
		this.form.el.mask("正在保存数据...", "x-mask-loading")
		phis.script.rmi.jsonRequest({
					serviceId : "configHsqxYgService",
					serviceAction : "saveHSQX",
					body : saveData
				}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						if (code == 612) {
							MyMessageTip.msg("提示", "该员工已经分配科室!", true);
							return;
						}
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData]);
						return
					}
					Ext.apply(this.data, saveData);
					if (json.body) {
						this.initFormData(json.body)
						this.fireEvent("save", this.entryName, this.op, json,
								this.data)
					}
					this.op = "update"
				}, this)// jsonRequest
	}
})