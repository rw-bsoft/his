/**
 * 孕妇特殊情况表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.special")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.special.PregnantSpecialForm = function(cfg) {
	cfg.width = 850;
	cfg.fldDefaultWidth = 150
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.special.PregnantSpecialForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("loadData", this.onLoadData, this)
}
Ext.extend(chis.application.mhc.script.special.PregnantSpecialForm, chis.script.BizTableFormView, {

	beforeCreate : function() {
		this.data.empiId = this.exContext.ids.empiId;
		this.data.phrId = this.exContext.ids.phrId;
		this.data.pregnantId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
	},

	onReady : function() {
		chis.application.mhc.script.special.PregnantSpecialForm.superclass.onReady.call(this)
		var form = this.form.getForm();

		var occurDate = form.findField("occurDate");
		if (occurDate) {
			occurDate.on("valid", this.setGestationalWeeks, this);
			occurDate.on("blur", this.onOccurDateChange, this);
			occurDate.on("keyup", this.onOccurDateChange, this);
		}
	},

	loadData : function() {
		this.doNew();
		var datas = this.exContext.args.formDatas
		if (!datas) {
			return;
		}
		this.initFormData(datas);
		this.fireEvent("loadData", this);
	},

	onLoadData : function() {
		var occurDate = this.form.getForm().findField("occurDate");
		if (occurDate) {
			this.onOccurDateChange(occurDate);
		}
	},

	setGestationalWeeks : function(field) {
		var date = field.getValue();
		if (!date) {
			return;
		}

		// ** 计算孕周
		var result = util.rmi.miniJsonRequestSync({
			serviceId : this.saveServiceId,
			serviceAction : "getPregnantWeek",
			method:"execute",
			body : {
				"datum" : date,
				"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
			}
		})
		var pregnantWeek = result.json.body.pregnantWeek
		if (-1 == pregnantWeek) {
			pregnantWeek = 0;
		}
		this.form.getForm().findField("gestationalWeeks")
				.setValue(pregnantWeek);
	},

	onOccurDateChange : function(field) {
		if (!field.validate()) {
			return;
		}
		var solveDate = this.form.getForm().findField("transactDate");
		var constriction = field.getValue();
		if (!constriction) {
			solveDate.setMinValue(null);
			solveDate.validate();
			return;
		}
		solveDate.setMinValue(constriction);
		solveDate.validate();
	}
});