$package("phis.application.cfg.script")

$import("phis.script.SimpleList","phis.script.SimpleForm")

phis.application.cfg.script.CredentialsViewList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.entryName="phis.application.cfg.schemas.WL_ZJXX";
	phis.application.cfg.script.CredentialsViewList.superclass.constructor
			.apply(this, [cfg]);

}
Ext.extend(phis.application.cfg.script.CredentialsViewList,
		phis.script.SimpleList, {
			onRenderer : function(value, metaData, r) {
				var TPXX = r.get("TPXX");
				var src = (TPXX == null || TPXX == "") ? "yes" : "no";
				return "<img src='images/" + src + ".png'/>";
			},
			loadData : function(WZXH,CJXH) {
				this.clear(); 
				var body = {
					"WZXH" : WZXH,
					"CJXH" : CJXH
				};
				this.requestData.serviceId = "phis.configManufacturerForWZService";
				this.requestData.serviceAction = "certificateQuery";
				this.requestData.cnd = body;
				
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