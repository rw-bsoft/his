$package("chis.application.hc.script")

$import("chis.script.BizSimpleListView", "chis.script.EHRView")

chis.application.hc.script.WProgestationaskRecord = function(cfg) {
	chis.application.hc.script.WProgestationaskRecord.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hc.script.WProgestationaskRecord, chis.script.BizSimpleListView, {
			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["EMPIInfoModule_PC"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onAddPcCheck, this)
					this.midiModules["EMPIInfoModule_PC"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onAddPcCheck : function(data) {
				this.showModule(data,"create");
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.showModule(r.data,"update")
			},

			showModule : function(data,op) {
				var empiId = data.empiId;
				if(data.sexCode && data.sexCode=="1"){
					Ext.Msg.alert("友情提醒：","此居民是男性！");
					return;
				}
				var initModules = ['PC_02_02','PC_04'];
				var module = this.midiModules["WPcCheck_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["WPcCheck_EHRView"] = module
					module.on("save", this.refreshData, this);
					module.exContext.args["phrId"] = data.phrId;
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.exContext.args["phrId"] = data.phrId;
					module.refresh();
				}
				module.exContext.args["op"] = op;
				module.getWin().show();
			},

			onDblClick : function(grid, index, e) {
				this.doModify();
			},

			refreshData : function(entryName, op, json, data) {
				if (this.store) {
					this.store.load()
				}
			}
		});