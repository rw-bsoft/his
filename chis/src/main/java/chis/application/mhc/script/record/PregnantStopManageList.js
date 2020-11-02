/**
 * 孕妇终止管理一览表列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizSimpleListView", "chis.script.EHRView")
chis.application.mhc.script.record.PregnantStopManageList = function(cfg) {
	chis.application.mhc.script.record.PregnantStopManageList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.mhc.script.record.PregnantStopManageList,
		chis.script.BizSimpleListView, {
      
			
			doModify:function(){
				if (this.store.getCount() == 0) {
					return
				}
				var r = this.grid.getSelectionModel().getSelected()
				this.initDataId = r.get("pregnantId");
				var data = {}
				data.empiId = r.get("empiId")
				this.onEmpiSelected(data)
			},
      
			onDblClick:function(){
				this.doModify()
			},
      
			onEmpiSelected : function(data) {
				var empiId = data.empiId
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['G_01']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				var module = this.midiModules["PregnantStopManageList_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.exContext.ids["pregnantId"] = this.initDataId;
					module.on("save", this.onSave, this)
					this.midiModules["PregnantStopManageList_EHRView"] = module
				} else {
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.exContext.ids["pregnantId"] = this.initDataId;
					module.refresh()
				}
				module.getWin().show()
			}
		});