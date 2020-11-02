$package("chis.application.mhc.script.dea")

$import("chis.script.BizSimpleListView")

chis.application.mhc.script.dea.PrenatalDeathReportList = function(cfg) {
	chis.application.mhc.script.dea.PrenatalDeathReportList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.mhc.script.dea.PrenatalDeathReportList, chis.script.BizSimpleListView, {
			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["DeathReport"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onAddPregnant, this);
					this.midiModules["DeathReport"] = m;
				} 
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onAddPregnant : function(data) {
				if (data.sexCode == "1") {
					Ext.Msg.alert("提示信息", "性别不符")
					return
				}
				var empiId = data.empiId
				
				util.rmi.jsonRequest({
							serviceId : "chis.pregnantRecordService",
							serviceAction : "whetherNeedPregnantRecord",
							method:"execute",
							body : {
								"empiId" : empiId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.onAddPregnant, [empiId]);
								return
							}
							if (json.body) {
								var body = json.body;
								var needCreate = body.needCreate;
								var result = body.result;
								// ** 不存在档案,可以建档
								if (needCreate == 1 && !result) {
									this.pregnantId = null;
									this.initModules = ['G_01', 'G_16'];
									this.showModule(empiId);
								} else if (result) { // ** 有档案存在
									var selectView = this.midiModules["PrenatalDeathReportList"];
									if (!selectView) {
										$import("chis.application.mhc.script.record.PregnantRecordSelectList")
										selectView = new chis.application.mhc.script.record.PregnantRecordSelectList(
												{
													formRecordList : false
												});
										selectView.on("update", function(
														pregnantId) {
													this.pregnantId = pregnantId;
													this.initModules = ['G_16'];
													this.showModule(empiId);
												}, this);
										selectView.on("create", function() {
											if (needCreate == 0) { // ** 档案已经存在
												  Ext.Msg.alert("提示信息", body.message);
											} else if (needCreate == 1) { // **
																			// 可以建档
												this.pregnantId = null; 
												this.initModules = ['G_01','G_16'];
												this.showModule(empiId);
											}
										}, this);
										this.midiModules["PrenatalDeathReportList"] = selectView;
									}
									var win = selectView.getWin();
									win.setPosition(300, 100);
									win.show();
									selectView.setData(body.result);
								}
							}
						}, this)
			},

			onSave : function(entryName, op, json, data) {
				this.refresh();
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.initModules = ['G_16']; 
				this.showModule(r.get("empiId"));
			},

			showModule : function(empiId) {
				var module = this.midiModules["PrenatalDeathReportList_EHRView"]
				if (!module) {
					$import("chis.script.EHRView");
					module = new chis.script.EHRView({
								initModules : this.initModules,
								empiId : empiId,
								activeTab : 0,
								closeNav : true,
								mainApp : this.mainApp
							})
					module.on("save", this.onSave, this)
					module.exContext.ids["pregnantId"] = this.pregnantId;
					this.midiModules["PrenatalDeathReportList_EHRView"] = module
				} else {
					module.exContext.ids = {}
					module.exContext.ids["empiId"] = empiId
					module.exContext.ids["pregnantId"] = this.pregnantId;
					module.refresh();
				}
				module.getWin().show()
			},

			onDblClick : function() {
				this.doModify()
			},

			openModule : function(cmd, r, xy) {
				if (r == null) {
					return
				}
				var empiId = r.get("empiId");
				var pregnantId = r.get("pregnantId");
				var personName = r.get("personName");
				var cardId = r.get("cardId");
				var module = this.midiModules[cmd]
				if (module) {
					var win = module.getWin()
					win.setTitle('[' + personName + '] 的孕产妇死亡调查报告附卷')
					win.show()
					win.center();
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "survey" :
								var args = {};
								args.empiId = empiId;
								args.pregnantId = pregnantId;
								args.cardId = cardId;
								module.exContext.args = args;
								module.onSurvey()
						}
					}
				}
			}
		})
