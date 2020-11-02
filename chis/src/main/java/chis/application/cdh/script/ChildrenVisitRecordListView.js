/**
 * 儿童随访信息
 * 
 * @author :zhouw
 */
$package("chis.application.cdh.script")

$import("chis.script.BizSimpleListView")

chis.application.cdh.script.ChildrenVisitRecordListView = function(cfg) {
	cfg.pageSize = -1;
	chis.application.cdh.script.ChildrenVisitRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;

}

Ext.extend(chis.application.cdh.script.ChildrenVisitRecordListView,
		chis.script.BizSimpleListView, {

			loadData : function() {
				this.requestData.cnd = ["eq", ["$", "empiId"],
								["s", this.exContext.empiData.empiId || '']];
				chis.application.cdh.script.ChildrenVisitRecordListView.superclass.loadData
						.call(this);

			},
			refresh : function() {
				if (this.store) {
					this.store.load()
				}
			},
			onDblClick : function() {
			}

		});