$package("chis.application.hcqj.script")

$import("chis.script.BizSimpleListView",
		"chis.script.EHRView",
		"chis.application.hc.script.HealthCheckRepeatRecord")

chis.application.hcqj.script.HealthCheckRecord = function(cfg) {
	this.initCnd = ['and',['eq', ['s', '1'], ['s', '1']],['in', ['$', 'a.createUnit'], ['320124011', '320124010']]]
	chis.application.hcqj.script.HealthCheckRecord.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hcqj.script.HealthCheckRecord, chis.script.BizSimpleListView, {
			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["EMPIInfoModule_HC"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onAddHealthCheck, this)
					this.midiModules["EMPIInfoModule_HC"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onAddHealthCheck : function(data) {
				this.showModule(data,"create");
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.showModule(r.data,"update")
			},

			showModule : function(data,op) {
				var empiId = data.empiId;
				var initModules = ['B_10'];
				if(this.mainApp.exContext.healthCheckType == 'paper'){
					initModules = ['B_10_HTML'];
				}
				var module = this.midiModules["HealthCheck_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["HealthCheck_EHRView"] = module
					module.on("save", this.refreshData, this);
					module.exContext.args["healthCheck"] = data.healthCheck;
				} else {
					module.exContext.ids["empiId"] = empiId;
					module.exContext.args["healthCheck"] = data.healthCheck;
					module.refresh();
				}
				module.exContext.args["dataSources"] = "1";
				module.exContext.args["op"] = op;
				module.exContext.args["id"] = "D20_11";
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