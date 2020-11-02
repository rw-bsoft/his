/**
 * 孕妇档案随访列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizSimpleListView", "chis.script.EHRView")
chis.application.mhc.script.visit.PregnantVisitRecordList = function(cfg) {
	chis.application.mhc.script.visit.PregnantVisitRecordList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.mhc.script.visit.PregnantVisitRecordList,
		chis.script.BizSimpleListView, {

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.initDataId = r.get("pregnantId");
				this.empiId = r.get("empiId");
				this.visitId = r.get("visitId");
				this.showModule();
			},

			showModule : function() {
				var module = this.midiModules["PregnantRecordVisit_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['G_01', 'G_02'],
								activeTab : 1,
								empiId : this.empiId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["PregnantRecordVisit_EHRView"] = module
					module.exContext.ids["pregnantId"] = this.initDataId;
					module.exContext.args["selectVisitId"] = this.visitId;
					module.on("save", this.refreshList, this);
				} else {
					module.exContext.ids = {}
					module.exContext.ids["empiId"] = this.empiId
					module.exContext.ids["pregnantId"] = this.initDataId;
					module.exContext.args["selectVisitId"] = this.visitId;
					module.refresh();
				}
				module.getWin().show();
			},

			refreshList : function(entryName, op, json, data) {
				if (entryName == this.entryName) {
					this.refresh();
				}
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			}
		});