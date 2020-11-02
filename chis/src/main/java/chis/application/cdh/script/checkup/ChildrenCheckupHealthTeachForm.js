/**
 * 儿童体格检查健康教育表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.checkup")
$import("chis.script.BizTableFormView")
chis.application.cdh.script.checkup.ChildrenCheckupHealthTeachForm = function(cfg) {
	cfg.colCount = 1;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 500;
	chis.application.cdh.script.checkup.ChildrenCheckupHealthTeachForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadNoData", this.onLoadNoData, this)
}

Ext.extend(chis.application.cdh.script.checkup.ChildrenCheckupHealthTeachForm,
		chis.script.BizTableFormView, {

			onBeforeSave : function(entryName, op, saveData) {
				saveData.checkupId = this.exContext.args[this.checkupType
						+ "_param"].checkupId;
				saveData.checkupType = this.checkupType;
				saveData.phrId = this.exContext.ids["CDH_HealthCard.phrId"];
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"checkupId" : this.exContext.args[this.checkupType
							+ "_param"].checkupId,
					"checkupType" : this.checkupType
				};
			},

			onLoadNoData : function() {
				this.doNew();
			}

		})