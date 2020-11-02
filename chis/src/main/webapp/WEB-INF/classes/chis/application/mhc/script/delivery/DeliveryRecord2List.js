/**
 * 艾滋病梅毒感染孕妇妊娠登记列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.delivery")
$import("chis.script.BizSimpleListView", "chis.script.EHRView")
chis.application.mhc.script.delivery.DeliveryRecord2List = function(cfg) {
	chis.application.mhc.script.delivery.DeliveryRecord2List.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.mhc.script.delivery.DeliveryRecord2List,
		chis.script.BizSimpleListView, {

			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["WomanRecord"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onAddPregnant, this)
					this.midiModules["WomanRecord"] = m;
				}
				var win = m.getWin();
				win.setPosition(300, 100);
				win.show();
			},

			onAddPregnant : function(data) {
				if (data.sexCode == "1") {
					Ext.Msg.alert("提示信息", "性别不符")
					return
				}
				this.empiId = data.empiId;
				this.pregnantId = null;
				this.showModule();
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.pregnantId = r.get("pregnantId");
				this.empiId = r.get("empiId");
				this.showModule();
			},

			showModule : function() {
				var module = this.midiModules["DeliveryRecord2_EHRView"];
				var exContext = {
					"isPregnant" : true
				};
				if (this.mainApp.jobtitleId == "21") {
					exContext.hidePregnant = true;
				}
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['G_13'],
								empiId : this.empiId,
								closeNav : true,
								mainApp : this.mainApp,
								exContext : exContext
							})
					this.midiModules["DeliveryRecord2_EHRView"] = module;
					module.noPhrId = true;
					if (this.pregnantId) {
						module.exContext.ids["pregnantId"] = this.pregnantId;
					}
					module.on("save", this.refreshList, this);
				} else {
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					if (this.pregnantId) {
						module.exContext.ids["pregnantId"] = this.pregnantId;
					}
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