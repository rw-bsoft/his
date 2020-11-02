/**
 * 产后42天列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.postnatal42day")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.postnatal42day.Postnatal42dayRecordList = function(cfg) {
	chis.application.mhc.script.postnatal42day.Postnatal42dayRecordList.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.mhc.script.postnatal42day.Postnatal42dayRecordList,
		chis.script.BizSimpleListView, {
			onLoadData : function(){
				if (this.G06 && this.G07) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemCommonManageService",
							serviceAction : "getSystemConfigValues",
							method : "execute",
							body : "postnatal42dayType,postnatalVisitType"
						});
				if (result.code > 300) {
					alert("页面参数获取失败，默认为纸质页面！")
					this.G06 = "G_06";
					this.G07 = "G_07";
					return
				}
				if (result.json.body) {
					var body = result.json.body;
					if (body.postnatal42dayType == "paper") {
						this.G06 = "G_06_html";
					} else {
						this.G06 = "G_06";
					}
					if (body.postnatalVisitType == "paper") {
						this.G07 = "G_07_html";
					} else {
						this.G07 = "G_07";
					}
				}
			},

			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["PostnatalRecord"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onEmpiSelected, this)
					this.midiModules["PostnatalRecord"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(data) {
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
										this.onEmpiSelected, [data]);
								return
							}
							if (json.body) {
								var body = json.body;
								var needCreate = body.needCreate;
								var result = body.result;
								// ** 不存在档案,可以建档
								if (needCreate == 1 && !result) {
									this.pregnantId = null;
									this.initModules = ['G_01', this.G06, this.G07,
											'G_10'];
									this.showModule(empiId);
								} else if (result) { // ** 有档案存在
									var selectView = this.midiModules["DeliveryRecordSelectList"];
									if (!selectView) {
										$import("chis.application.mhc.script.record.PregnantRecordSelectList")
										selectView = new chis.application.mhc.script.record.PregnantRecordSelectList(
												{
													formRecordList : false
												});
										selectView.on("update", function(
														pregnantId) {
													this.pregnantId = pregnantId;
													this.initModules = [this.G06,
															this.G07, 'G_10'];
													this.showModule(empiId);
												}, this);
										selectView.on("create", function() {
											if (needCreate == 0) { // ** 档案已经存在
												  Ext.Msg.alert("提示信息", body.message);
											} else if (needCreate == 1) { // **
																			// 可以建档
												this.pregnantId = null;
												this.initModules = ['G_01',
														this.G06, this.G07, 'G_10'];
												this.showModule(empiId);
											}
										}, this);
										this.midiModules["DeliveryRecordSelectList"] = selectView;
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
				if (entryName == this.entryName) {
					this.refresh();
				}
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.pregnantId = r.get("pregnantId");
				this.initModules = [this.G06, this.G07, 'G_10'];
				this.showModule(r.get("empiId"));
			},

			showModule : function(empiId) {
				var module = this.midiModules["Postnatal42dayRecordList_EHRView"]
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
					this.midiModules["Postnatal42dayRecordList_EHRView"] = module
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
			}

		});