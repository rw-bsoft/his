$package("phis.application.yb.script");
$import("phis.script.SimpleList");

phis.application.yb.script.SjybYbdzMxBdList_X = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.yb.script.SjybYbdzMxBdList_X.superclass.constructor.apply(this, [ cfg ]);
};
Ext.extend(phis.application.yb.script.SjybYbdzMxBdList_X, phis.script.SimpleList, {
	loadData : function(DZKSSJ,DZJSSJ) {
		this.clear();
		this.requestData.pageNo = 1;
		var body = {}
		body["DZKSSJ"] = DZKSSJ;
		body["DZJSSJ"] = DZJSSJ;
		this.requestData.serviceId = this.serviceId ;
		this.requestData.serviceAction = "queryForXYbdzMxbdList";
		this.requestData.body = body

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