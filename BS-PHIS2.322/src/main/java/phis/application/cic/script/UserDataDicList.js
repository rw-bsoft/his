$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.UserDataDicList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = true;
	phis.application.cic.script.UserDataDicList.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(phis.application.cic.script.UserDataDicList, phis.script.SimpleList, {
			onRowClick : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var ZDID = r.get("ZDID");
				this.fireEvent("rowClick", ZDID);
			},
			onLoadData : function() {
				var r = this.store.getAt(0);
				if (r == null) {
					return
				}
				var ZDID = r.get("ZDID");
				this.fireEvent("rowClick", ZDID);
			}
		});