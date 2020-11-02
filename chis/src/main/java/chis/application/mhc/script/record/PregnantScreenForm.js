/**
 * 孕妇产前筛查表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.record.PregnantScreenForm = function(cfg) {
	cfg.colCount = 2;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	cfg.autoFieldWidth = false
	chis.application.mhc.script.record.PregnantScreenForm.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeCreate", this.onBeforeCreate, this);
	this.on("loadNoData",this.onLoadNoData,this);
}

Ext.extend(chis.application.mhc.script.record.PregnantScreenForm, chis.script.BizTableFormView, {

	onBeforeCreate : function() {
		this.data["empiId"] = this.exContext.ids.empiId;
		this.data["phrId"] = this.exContext.ids.phrId;
		this.data["pregnantId"] = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
	},
  
	 getLoadRequest : function() {
        this.initDataId = null;
        return {
          "fieldName" : "pregnantId",
          "fieldValue" : this.exContext.ids["MHC_PregnantRecord.pregnantId"]
        };
      },
      onLoadNoData:function(){
      	this.doNew();
      }
});