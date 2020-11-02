$package("phis.application.med.script");

$import("phis.script.SimpleList");
/**
 * 该代码已在2013-07-09修改2114bug时,已不用
 */
phis.application.med.script.ZYProjectList = function(cfg) {
	cfg.height = 185;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.med.script.ZYProjectList.superclass.constructor.apply(
			this, [ cfg ]);

},

Ext.extend(phis.application.med.script.ZYProjectList, phis.script.SimpleList, {
	loadData : function(yjxh) {
		this.clear(); // ** add by yzh , 2010-06-09 **
		recordIds = [];
		this.requestData.serviceId = "phis.medicalTechnicalSectionService";
		this.requestData.serviceAction = "getZyList_Proj";
		this.requestData.yjxh = yjxh;
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
});