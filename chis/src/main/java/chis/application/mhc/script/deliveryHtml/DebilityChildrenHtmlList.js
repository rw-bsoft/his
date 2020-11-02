/**
 * 新生儿列表
 * 
 * @author :zhouw
 */
$package("chis.application.mhc.script.deliveryHtml")

$import("chis.script.BizSimpleListView")

chis.application.mhc.script.deliveryHtml.DebilityChildrenHtmlList = function(cfg) {
	cfg.pageSize = -1;
	chis.application.mhc.script.deliveryHtml.DebilityChildrenHtmlList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;

}

Ext.extend(chis.application.mhc.script.deliveryHtml.DebilityChildrenHtmlList,
		chis.script.BizSimpleListView, {

			loadData : function() {
				
				this.requestData.cnd = this.requestData.cnd
						|| ["eq", ["$", "pregnantId"],
								["s", this.exContext.ids["MHC_PregnantRecord.pregnantId"] || '']];
				chis.application.mhc.script.deliveryHtml.DebilityChildrenHtmlList.superclass.loadData
						.call(this);

			}

		});