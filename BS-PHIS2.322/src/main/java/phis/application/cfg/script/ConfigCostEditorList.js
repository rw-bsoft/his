/**
 * 药品公用信息-用药限制
 * 
 * @author caijy
 */
$package("phis.application.cfg.script");
$import("phis.script.EditorList");

phis.application.cfg.script.ConfigCostEditorList = function(cfg) {
	cfg.serviceId = "phis.configCostService";
	cfg.listServiceId = "costConstraintsList";
	cfg.actionId = "saveCost";
	cfg.disablePagingTbr = true;
	this.entryName=cfg.properties.entryName;
	// cfg.serverParams.serviceId = "medicinesManageService";
	// cfg.serverParams.serviceAction = "limitInfoList";
	phis.application.cfg.script.ConfigCostEditorList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.ConfigCostEditorList, phis.script.EditorList,
		{
			/*openModule : function(cmd, r, xy) {
				phis.application.cfg.script.ConfigCostEditorList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd]
				var win = module.getWin();
				var default_xy = win.el.getAlignToXY(win.container, 'c-c');
				win.setPagePosition(default_xy[0], 80);
			},*/
			fillStore : function(op) {
				if (op == "create") {
					this.doNew();
				} else if (op == "update") {
					this.loadData();
				}
			},
			doNew : function() {
			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.listServiceId;
				var body={};
				if(this.initDataId){
					body={"fyxh":this.initDataId};
				}else{
					body={"fyxh":0}
				}
				this.requestData.body = body;
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
			}
		})
