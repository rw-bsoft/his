/**
 * 孕妇特殊情况列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.special")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.special.PregnantSpecialList = function(cfg) {
	chis.application.mhc.script.special.PregnantSpecialList.superclass.constructor.apply(this,
			[cfg]);
	this.enableCnd = false
	this.disablePagingTbr = true
	this.pageSize = 100;
}

Ext.extend(chis.application.mhc.script.special.PregnantSpecialList, chis.script.BizSimpleListView,
		{

			loadData : function() {
				this.initCnd = [
						"eq",
						["$", "pregnantId"],
						[
								"s",
								this.exContext.ids["MHC_PregnantRecord.pregnantId"]]]
				this.requestData.cnd = this.initCnd
				chis.application.mhc.script.special.PregnantSpecialList.superclass.loadData
						.call(this);
			},

			refresh : function() {
				if (this.store) {
					this.store.load()
				}
			}
		});