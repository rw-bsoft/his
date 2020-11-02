// 高血压就诊信息。
$package("app.biz.mdc");

$import("chis.script.BizModule", "chis.script.BizTableFormView");

app.biz.mdc.HypertensionClinicRecordForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "确定"
			}];
	cfg.entryName = "chis.application.hy.schemas.MDC_HypertensionClinicRecord";
	cfg.saveServiceId = "chis.simpleSave";
	cfg.colCount = 2;
	cfg.labelWidth = 90;
	cfg.title = "高血压就诊信息";
	cfg.showButtonOnTop = true;
	cfg.width = 500;
	app.biz.mdc.HypertensionClinicRecordForm.superclass.constructor.apply(this,
			[cfg]);
	this.serviceId = "chis.hypertensionService";
	this.on("winShow", this.onWinShow, this);
	this.on("save", this.onSave, this);
};

Ext.extend(app.biz.mdc.HypertensionClinicRecordForm, chis.script.BizTableFormView, {
	onReady : function() {
		app.biz.mdc.HypertensionClinicRecordForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("constriction").on("blur", this.onConstrictionChange, this);
		form.findField("constriction").on("keyup", this.onConstrictionChange, this);
		form.findField("diastolic").on("blur", this.onDiastolicChange, this);
		form.findField("diastolic").on("keyup", this.onDiastolicChange, this);
		form.findField("weight").on("blur", this.onWeightChange, this);
		form.findField("weight").on("keyup", this.onWeightChange, this);
	},

	doSave : function() {
		this.data.empiId = this.empiId;
		app.biz.mdc.HypertensionClinicRecordForm.superclass.doSave.call(this);
	},

	onWinShow : function() {
		if (this.form && this.form.el) {
			this.form.el.mask("正在载入数据...", "x-mask-loading");
		}
		this.loading = true;
		util.rmi.jsonRequest({
				serviceId : 'chis.simpleQuery',
				schema : "chis.application.hy.schemas.MDC_HypertensionRecord",
				method:"execute",
				cnd : ['eq', ['$', 'a.empiId'], ['s', this.empiId]]
			}, function(code, msg, json) {
				if (this.form && this.form.el) {
					this.form.el.unmask();
				}
				this.loading = false;
				if (code > 300) {
					this.processReturnMsg(code, msg, this.onWinShow);
					return;
				}
				if (json.body) {
					var data = json.body[0];
					if (data) {
						this.validate();
						this.form.getForm().findField("height").setValue(data.height);
						this.form.getForm().findField("weight").setValue(data.weight);
						this.data.empiId = this.empiId;
					}
				}
			}, this);
		if (!this.data) {
			this.data = {};
		}
	},

	onSave : function() {
		this.onEsc();
	},

	onConstrictionChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var cons = field.getValue();
		if (!cons) {
			return;
		}
		if (cons >500 || cons <50) {
			field.markInvalid("收缩压必须在50到500之间！");
			return;
		}
		var diaFld = this.form.getForm().findField("diastolic");
		var dia = diaFld.getValue();
		if (cons <= dia) {
			field.markInvalid("收缩压应该大于舒张压！");
			diaFld.markInvalid("舒张压应该小于收缩压！");
			return;
		} else {
			diaFld.clearInvalid();
		}
	},

	onDiastolicChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var dia = field.getValue();
		if (!dia) {
			return;
		}
		if (dia >500 || dia <50) {
			field.markInvalid("舒张压必须在50到500之间！");
			return;
		}
		var consFld = this.form.getForm().findField("constriction");
		var cons = consFld.getValue();
		if (cons <= dia) {
			field.markInvalid("舒张压应该小于收缩压！");
			consFld.markInvalid("收缩压应该大于舒张压！");
			return;
		} else {
			consFld.clearInvalid();
		}
	},

	onHeightChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var height = field.getValue();
		if (!height) {
			return;
		}
		if (height >= 300 || height <= 0) {
			field.markInvalid("身高数值应在0到300之间！");
			return;
		}
	},

	onWeightChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var weight = field.getValue();
		if (!weight) {
			return;
		}
		if (weight > 500 || weight <= 0) {
			field.markInvalid("体重数值应在0到500之间！");
			return;
		}
	},

	validate : function() {
		if (!this.form) {
			return false;
		}
		if (!this.schema) {
			var re = util.schema.loadSync(this.entryName);
			if (re.code == 200) {
				this.schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.validate);
				return;
			}
		}
		var items = this.schema.items;
		var n = items.length;
		var flag = true;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var f = this.form.getForm().findField(it.id);
			if (f) {
				if (!f.getValue()) {
					f.setValue(it.defaultValue);
				}
				if (!f.validate()) {
					flag = false;
				}
			}
		}
		return flag;
	}
});