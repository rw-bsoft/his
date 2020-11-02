$package("chis.application.hy.script.similarity")
$styleSheet("chis.resources.app.biz.EHRView")
$import("chis.script.BizCombinedModule2", "util.Accredit")

chis.application.hy.script.similarity.HypertensionSimilarityCheckModule = function(
		cfg) {
	cfg.itemWidth = 200
	cfg.layOutRegion = 'north'
	cfg.itemHeight = 125
	chis.application.hy.script.similarity.HypertensionSimilarityCheckModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
	this.on("afterLoadModule", this.onAfterLoadModule, this)
	this.querySchema = "chis.application.hy.schemas.MDC_HypertensionSimilarity";
}

Ext
		.extend(
				chis.application.hy.script.similarity.HypertensionSimilarityCheckModule,
				chis.script.BizCombinedModule2, {

					onAfterLoadModule : function(moduleName, cfg) {
						if (moduleName == this.actions[0].id) {
							this.formPanel = this.midiModules[this.actions[0].id];
							if (this.cachaData) {
								this.formPanel.cachaData = this.cachaData;
							}
							this.formPanel.on("save", this.onFormSave, this);
							this.formPanel.on("ehrAddModule", this.onEhrAddModule, this)
							this.formPanel.on("chisSave", this.onChisFormSave,
									this)
						}
						if (moduleName == this.actions[1].id) {
							this.listPanel = this.midiModules[this.actions[1].id]
							if (this.cachaData) {
								this.listPanel.cachaData = this.cachaData;
							}
							this.listPanel.on("rowClick", this.onListClick,
									this)
						}
					},
					onFormSave : function(entryName, op, json, data) {
						this.midiModules[this.actions[1].id].loadData();
						this.fireEvent("save", entryName, op, json, data);
						if (this.win) {
							this.win.hide()
						}
					},
					onEhrAddModule:function(visitModule){
						this.fireEvent("ehrAddModule",visitModule);
					},
					onChisFormSave : function(msg) {
						this.fireEvent("chisSave", msg);// phis中用于通知刷新emrView左边树
					},
					onListClick : function(grid, index, e) {
						// 如果测量时间为当天，点击该记录，可将该记录加到核实表单中
						// 避免同一天录入记录后，关闭核实面板后，再来核实会再记录一条记录
						var r = this.listPanel.getSelectedRecord();
						if (r == null) {
							return
						}
						var registerDate = r.data.registerDate;
						var serverDate = this.mainApp.serverDate;
						if (registerDate === serverDate) {
							var formData = this.castListDataToForm(r.data,
									this.listPanel.schema);
							this.formPanel.initFormData(formData);
							this.formPanel.validate();
						}
					},
					recordSelectView : function(data) {
						var recordSelectView = this.midiModules["recordSelectView"];
						if (!recordSelectView) {
							$import("chis.application.mpi.script.CombinationSelect");
							var recordSelectView = new chis.application.mpi.script.CombinationSelect(
									{
										entryName : this.querySchema,
										disablePagingTbr : true,
										autoLoadData : false,
										enableCnd : false,
										autoLoadSchema : false,
										modal : true,
										title : "请选择一条高血压疑似记录",
										width : 600,
										height : 400
									});
							recordSelectView.on("onSelect", function(r) {
								// var data = r.data;
								this.exContext.args.similarityId = r.data.similarityId;
								var cnd = [
										'eq',
										['$', 'a.similarityId'],
										[
												's',
												this.exContext.args.similarityId
														|| '']];
								this.midiModules[this.actions[1].id].requestData.cnd = cnd;
								this.midiModules[this.actions[1].id].loadData();
								this.refreshExContextData(
										this.midiModules[this.actions[0].id],
										this.exContext)
								this.refreshExContextData(
										this.midiModules[this.actions[1].id],
										this.exContext)
							}, this);
							recordSelectView.initPanel();
							this.midiModules["recordSelectView"] = recordSelectView;
						}
						recordSelectView.getWin().show();
						var records = [];
						for (var i = 0; i < data.length; i++) {
							var r = data[i];
							var record = new Ext.data.Record(r);
							records.push(record);
						}
						recordSelectView.setRecords(records);
					},
					loadData : function() {
						if (this.cachaData) {
							Ext.apply(this.exContext.args, this.cachaData);
						}
						if (!this.exContext.args.similarityId) {
							var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.hypertensionSimilarityService",
								serviceAction : "getSimilarityIdByEmpiId",
								method : "execute",
								body : {
									empiId : this.exContext.ids.empiId
								}
							})
							if (result.code > 300) {
								this.processReturnMsg(result.code, result.msg);
								return
							}
							var rsList = result.json.body;
							if (rsList) {
								if (rsList.length == 1) {
									this.exContext.args=rsList[0];
									this.exContext.args.similarityId = rsList[0].similarityId;
								}
								if (rsList.length > 1) {
									this.recordSelectView(rsList);
								}
							}
						}
						this.refreshExContextData(
								this.midiModules[this.actions[0].id],
								this.exContext)
						this.refreshExContextData(
								this.midiModules[this.actions[1].id],
								this.exContext)
						this.midiModules[this.actions[0].id].initDataId = null
						this.midiModules[this.actions[0].id].doNew();
						if (this.exContext.args.similarityId) {
							// 医疗过来，如果一人有多条疑似记录，此处不执行，在选择记录后执行list的loadData
							var cnd = [
									'eq',
									['$', 'a.similarityId'],
									[
											's',
											this.exContext.args.similarityId
													|| '']];
							this.midiModules[this.actions[1].id].requestData.cnd = cnd;
							this.midiModules[this.actions[1].id].loadData();
						}
					},
					onReady : function() {
						chis.application.hy.script.similarity.HypertensionSimilarityCheckModule.superclass.onReady
								.call(this);
						// 从医疗过来，该方法每次都会执行,loadData不执行,EMRView每次执行initPanel方法
						// 从社区打开，该方法只执行一次,loadData通过onWinShow每次执行
						if (!this.exContext.args.similarityId) {
							this.loadData();
						}
					},
					onWinShow : function() {
						this.loadData()
					},
					destory : function() {
						this.exContext.args = {};
					}
				})