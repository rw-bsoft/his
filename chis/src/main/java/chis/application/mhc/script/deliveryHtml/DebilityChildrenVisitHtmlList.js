/**
 * 儿童随访信息
 * 
 * @author :zhouw
 */
$package("chis.application.cdh.script")

$import("chis.script.BizSimpleListView")

chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlList = function(
		cfg) {
	cfg.pageSize = -1;
	chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;

}

Ext.extend(
		chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlList,
		chis.script.BizSimpleListView, {

			loadData : function() {
				
				if (!this.exContext.args.babyId) {
					return
				}
				this.requestData.cnd = ["eq", ["$", "babyId"],
						["s", this.exContext.args.babyId || '']];

				chis.application.mhc.script.deliveryHtml.DebilityChildrenVisitHtmlList.superclass.loadData
						.call(this);

			}

		});