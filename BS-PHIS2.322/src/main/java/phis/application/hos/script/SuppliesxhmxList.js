$package("phis.application.hos.script")

//$import("phis.script.SimpleList",
//		"app.modules.print.HospitalBedManagementPrintView")
$import("phis.script.SimpleList","phis.script.widgets.DatetimeField")
phis.application.hos.script.SuppliesxhmxList = function(cfg) {
	phis.application.hos.script.SuppliesxhmxList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.hos.script.SuppliesxhmxList,phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.suppliesxhmxInfoService";
				this.requestData.serviceAction = "getSuppliesxhmxInfo";
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
			},
			doPrint : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var brid = r.get("BRID");
				var xhrq = r.get("XHRQ");
				var ksdm = r.get("KSDM");
				var module = this.createModule("fpprintxhmx", this.relPrint)
				module.BRID = brid;
				module.XHRQ = xhrq;
				module.KSMC = 0;
				module.KSDM = ksdm;
				module.initPanel();
				module.doPrint();
			}
		})
