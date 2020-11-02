$package("phis.application.cfg.script")

$import("phis.script.SimpleList", "phis.script.SimpleForm")

phis.application.cfg.script.AlreadyNoClassList = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.cfg.script.AlreadyNoClassList.superclass.constructor.apply(this,
			[ cfg ]);
}
Ext.extend(phis.application.cfg.script.AlreadyNoClassList, phis.script.SimpleList, {
	loadData : function() {
		this.clear();
		this.requestData.serviceId = "phis.configSubstancesClassService";
		this.requestData.serviceAction = "alreadyNoClassQuery";

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