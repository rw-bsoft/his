$package("chis.application.hy.script.similarity")

$import("chis.script.BizSimpleListView")

chis.application.hy.script.similarity.HypertensionSimilarityListView = function(
		cfg) {
	// cfg.initCnd = ['eq',['$','a.diagnosisType'],['s','0']]
	this.initCnd = cfg.cnds || ['eq', ['$', 'a.diagnosisType'], ['s', '2']]
	chis.application.hy.script.similarity.HypertensionSimilarityListView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(
		chis.application.hy.script.similarity.HypertensionSimilarityListView,
		chis.script.BizSimpleListView, {
			onRowClick : function() {
				var r = this.getSelectedRecord();
				var bts = this.grid.getTopToolbar();
				var checkBtn = bts.find("cmd", "estimate");
				if (checkBtn && checkBtn[0]) {
					if (r) {
						var diagnosisType = r.get("diagnosisType");
						if (diagnosisType == 1) {
							checkBtn[0].enable();
							return;
						}
					}
					checkBtn[0].disable();
				}
			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.hy.script.similarity.HypertensionSimilarityListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}

				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.diagnosisType",
							forceSelection : true,
							defaultValue : {
								key : "2",
								text : "疑似"
							}
						})
				comb.on("select", this.radioChanged, this)
				comb.setValue("2")
				comb.setWidth(100)
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb]
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},
			radioChanged : function(r) {
				var status = r.getValue()
				var navCnd = this.navCnd
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.diagnosisType'], ['s', status]]
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["HypertensionSimilarityListView_EMPIInfoModule"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this)
					this.midiModules["HypertensionSimilarityListView_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				var empiId = data.empiId
				// 判断是否已经有疑似记录，如果有则弹出核实窗口
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.hypertensionSimilarityService",
							serviceAction : "isExistHypertensionSimilarityRecord",
							method : "execute",
							body : {
								empiId : empiId
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				var isExistHS = result.json.body.isExistHS
				if (isExistHS) {
					var module = this.createSimpleModule(
							"HypertensionSimilarityCheckModule",
							"chis.application.hy.HY/HY/C20-2");
					this.refreshExContextData(module, this.exContext);
					Ext.apply(module.exContext.args, result.json.body.hsData);
					module.on("save", this.onCheck, this)
					module.getWin().show();
				} else {
					var module = this.createSimpleModule(
							"HypertensionSimilarityCheck",
							"chis.application.hy.HY/HY/C20-1");
					this.refreshExContextData(module, this.exContext);
					Ext.apply(module.exContext.args, data)
					module.initDataId = null
					module.on("save", this.onSave, this)
					module.getWin().show();
				}
			},
			doCheck : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var module = this.createSimpleModule(
						"HypertensionSimilarityCheckModule",
						"chis.application.hy.HY/HY/C20-2");
				this.refreshExContextData(module, this.exContext);
				Ext.apply(module.exContext.args, r.data)
				// module.initDataId = r.data.similarityId
				module.cachaData = r.data
				module.on("save", this.onCheck, this)
				module.getWin().show();
			},
			onDblClick : function(grid, index, e) {
				this.doCheck()
			},
			onSave : function() {
				this.refresh()
			},
			onCheck : function(entryName, op, json, data) {
				this.refresh()
				if (data.diagnosisType == "1" && !data.hasHyRecord) {
					Ext.Msg.confirm("消息提示", "是否新建高血压档案", function(btn) {
								if (btn == "yes") {
									this.createHypertensionRecord(data.empiId);
								}
							}, this);
				}
			},
			createHypertensionRecord : function(empiId) {
				var cfg = {}
				cfg.empiId = empiId
				cfg.initModules = ['C_01', 'C_02', 'C_03', 'C_05', 'C_04']
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				cfg.activeTab = 0
				var module = this.midiModules["HypertensionRecordListView_EHRView"]
				if (!module) {
					$import("chis.script.EHRView")
					module = new chis.script.EHRView(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["HypertensionRecordListView_EHRView"] = module
				} else {
					Ext.apply(module, cfg)
					module.exContext.ids = {}
					module.exContext.ids.empiId = empiId
					module.refresh()
				}
				module.getWin().show()
			},
			doEstimate : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (r == null) {
					return;
				}
				var empiId = r.get("empiId")
				this.createHypertensionRecord(empiId);
			}
		});