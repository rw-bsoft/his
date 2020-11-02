/**
 * 孕妇高危因素一览表列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizSimpleListView","chis.script.EHRView")
chis.application.mhc.script.visit.PregnantHighRiskReasonList = function(cfg) {
	chis.application.mhc.script.visit.PregnantHighRiskReasonList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.mhc.script.visit.PregnantHighRiskReasonList,
		chis.script.BizSimpleListView, {

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.showModule(r.get("empiId"), r.get("pregnantId"))
			},
      
			showModule : function(empiId, pregnantId) {
				var module = this.midiModules["PregnantRecord_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['G_01'],
								empiId : empiId,
								pregnantId : pregnantId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["PregnantRecord_EHRView"] = module
					module.on("save", this.refreshData, this);
				} else {
					module.exContext.ids = {}
					module.exContext.ids["empiId"] = empiId
					module.exContext.ids["pregnantId"] = pregnantId;
					module.refresh();
				}
				module.getWin().show();
			},
      
			onDblClick : function(grid, index, e) {
				this.doModify();
			}

		});