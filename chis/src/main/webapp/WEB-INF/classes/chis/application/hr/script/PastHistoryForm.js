/**
 * 个人既往史表单页面
 * 
 * @author : tianj
 */
$package("chis.application.hr.script")

$import("util.Accredit", "chis.script.BizTableFormView")

chis.application.hr.script.PastHistoryForm = function(cfg) {
	cfg.labelAlign = "left";
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 249;
	cfg.colCount = 2;
	cfg.width = 710;
	chis.application.hr.script.PastHistoryForm.superclass.constructor.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.hr.script.PastHistoryForm, chis.script.BizTableFormView, {
	getFormData : function(values) {
		this.data["empiId"] = this.empiId;
		var ac = util.Accredit;
		var form = this.form.getForm()
		if (!this.validate()) {
			return
		}
		if (!this.schema) {
			return
		}
		var items = this.schema.items
		Ext.apply(this.data, this.exContext.empiData)
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
					if (v) {
						if (it.dic) {
							values[it.id + "_text"] = f.getRawValue();
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
				}

				if (v == null || v === "") {
					if (!it.pkey && it["not-null"] && !it.ref) {
						Ext.Msg.alert("提示", it.alias + "不能为空")
						return;
					}
				}
				values[it.id] = v;
			}
		}
		return values;
	},

	onReady : function() {
		chis.application.hr.script.PastHistoryForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var startDate = form.findField("startDate");
		if (startDate) {
			startDate.on("blur", this.onStartDateChange, this);
			startDate.on("keyup", this.onStartDateChange, this);
		}
	},

	onStartDateChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var endDate = this.form.getForm().findField("endDate");
		if (!endDate) {
			return;
		}
		var constriction = field.getValue();
		if (!constriction) {
			endDate.setMinValue(null);
			endDate.validate();
			return;
		}
		endDate.setMinValue(constriction);
		endDate.validate()
	},

	onLoadData : function(entryName, body) {
		var field = this.form.getForm().findField("startDate")
		this.onStartDateChange(field);
		var diseaseCode = this.data["diseaseCode"]
		var protect = this.form.getForm().findField("protect");
		if (diseaseCode && (diseaseCode.substring(0, 2) == "15")) {
			protect.enable();
		} else {
			protect.disable();
		}
	},
	
	doSave : function() {
		if (this.saving) {
			return
		}
		var values = chis.application.hr.script.PastHistoryForm.superclass.getFormData.call(this);
		if (!values) {
			return;
		}
		Ext.apply(this.data, values);
		this.saveToServer(values);
	},
	
	afterSaveData : function(entryName,op,json,data){
      this.fireEvent("save", entryName, op, json,data);
      this.doCancel();
 	}
});