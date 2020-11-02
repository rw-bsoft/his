$package("phis.application.drc.script")

$import("phis.script.SimpleList")

/**
 *@author : chzhxiang
 *@date : 2013.08.14
 */
phis.application.drc.script.DRClinicRecordQueryList = function(cfg) {
	phis.application.drc.script.DRClinicRecordQueryList.superclass.constructor.apply(this, [cfg])
}
var recordIds = {};
var datas = {};

Ext.extend(phis.application.drc.script.DRClinicRecordQueryList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.cnd = ['eq',['$','empiId'],['s',this.exContext.empiData.empiId]];
				this.clear();
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			}
		})