$package("chis.application.gdr.script")
$import("util.Accredit", "chis.script.BizTableFormView", "chis.script.util.helper.Helper")
chis.application.gdr.script.FirstGuideForm = function(cfg) {
	this.entryName = 'chis.application.gdr.schemas.GDR_FirstGuide';
	chis.application.gdr.script.FirstGuideForm.superclass.constructor.apply(this, [cfg]);
	this.nowDate = this.mainApp.serverDate;
	this.saveServiceId = "chis.groupDinnerService";
	this.saveAction = "saveFirstGuide";
}
Ext.extend(chis.application.gdr.script.FirstGuideForm, chis.script.BizTableFormView, {

	onReady : function() {
		chis.application.gdr.script.FirstGuideForm.superclass.onReady.call(this)
		var form = this.form.getForm();
		var teachDate = form.findField("teachDate");
		teachDate.on("select", this.controlDate, this);
	},
	controlDate:function(){
		this.fireEvent("controlDate")
	}
	,
	saveToServer : function(saveData) {
		saveData.gdrId = this.gdrId;
		this.initDataId = saveData.recordId;
		chis.application.gdr.script.FirstGuideForm.superclass.saveToServer.call(this, saveData);
	},

	loadData : function() {
		this.doNew();
		if (this.form && this.form.el) {
			this.form.el.mask("正在载入数据...", "x-mask-loading");
		}
		this.loading = true;
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.groupDinnerService",
					serviceAction : "getFirstGuideInfo",
					method:"execute",
					schema : "chis.application.gdr.schemas.GDR_FirstGuide",
					cnd : ["eq", ["$", "gdrId"], ["s", this.gdrId]]
				});
		if (this.form && this.form.el) {
			this.form.el.unmask();
		}
		this.loading = false;
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg, this.loadData);
			return
		}
		if (result.json.body) {
			this.initDataId = result.json.body.recordId;
			this.initFormData(result.json.body);
			this.fireEvent("loadData", this.entryName, result.json.body);
		} else {
			this.initDataId = null;
		}
		if (this.op == 'create') {
			this.op = "update";
		}
	}
});