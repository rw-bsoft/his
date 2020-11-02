$package("chis.application.sch.script");

$import("chis.script.BizSimpleListView", "chis.script.EHRView",
		"chis.application.sch.script.SchistospmaRecordWriteOff")

chis.application.sch.script.SchistospmaRecordRecord = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]]
	chis.application.sch.script.SchistospmaRecordRecord.superclass.constructor.apply(this,
			[cfg]);
	this.on("loadData", this.onRowClick, this);
}

Ext.extend(chis.application.sch.script.SchistospmaRecordRecord, chis.script.BizSimpleListView, {
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				this.statusCmb = comb;
				comb.on("select", function(field) {
							var value = field.getValue();
							this.radioChanged(value);
						}, this);
				comb.setWidth(80);
				cfg.items = ["状态", "-", comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},

			radioChanged : function(status) {
				var navCnd = this.navCnd
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.status'], ['s', status]]
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
				var bts = this.grid.getTopToolbar().items;
				var btn = bts.items[8];
				if (btn) {
					if (status != "0") {
						btn.notReadOnly = false;
					} else {
						btn.notReadOnly = null;
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},

			onDblClick : function(grid, index, e) {
				this.doModify()
			},

			onStoreLoadData : function(store, records, ops) {
				chis.application.sch.script.SchistospmaRecordRecord.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick();
			},

			onRowClick : function() {
				var r = this.getSelectedRecord();
				var toolBar = this.grid.getTopToolbar();
				if (!r) {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == "createDoc") {
							continue;
						}
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (!btn || btn.length == 0) {
							continue;
						}
						btn[0].disable();
					}
					return;
				} else {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						btn[0].enable();
					}
				}
				var status = r.get("status");
				var woBtn = toolBar.find("cmd", "writeOff");// @@ 注销
				var vBtn = toolBar.find("cmd", "visit");// @@ 随访
				if (status == 1) {
					if (woBtn && woBtn.length > 0) {
						woBtn[0].disable();// @@ 注销
					}
					// if (vBtn && vBtn.length > 0) {
					// vBtn[0].disable();// @@ 随访
					// }
				}

			},

			doVisit : function() {
//				var r = this.getSelectedRecord();
				var r = this.grid.getSelectionModel().getSelected()
				this.selectModuleId = 1;
				this.showModule(r.data)
			},

			showModule : function(r) {
				var empiId = r.empiId;
				$import("chis.script.EHRView")
				var module = this.midiModules["SchistospmaRecord_EHRView"]
				if (!module) {
					module = new chis.script.EHRView({
								initModules : ['X_01'],
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp
							})
					this.midiModules["SchistospmaRecord_EHRView"] = module
					module.on("save", this.refresh, this);
					module.exContext.args['schisRecordId'] = this.getSelectedRecord().id;
					module.exContext.args['selectModuleId'] = this.selectModuleId;
				} else {
					module.exContext.ids = {}
					module.exContext.args["schisRecordId"] = this.getSelectedRecord().id;
					module.exContext.args['selectId'] = this.selectModuleId;
					module.exContext.args['selectModuleId'] = this.selectModuleId;
					module.exContext.ids["empiId"] = empiId
					module.refresh();
				}
				module.actionName = "SCH_SchistospmaRecord";
				module.getWin().show();
			},

			doCreateDoc : function() {
				var advancedSearchView = this.midiModules["EMPI.ExpertQuery"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule");
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule({
								title : "个人基本信息查找",
								modal : true,
								mainApp : this.mainApp
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this);
					this.midiModules["EMPI.ExpertQuery"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected : function(empi) {
				var empiId = empi.empiId;
				this.empiId = empi["empiId"];
				this.showEhrViewWin();
			},

			showEhrViewWin : function() {
				var m = this.midiModules["ehrView"];
				if (!m) {
					m = new chis.script.EHRView({
								closeNav : true,
								initModules : ['X_01'],
								mainApp : this.mainApp,
								empiId : this.empiId
							});
					this.midiModules["ehrView"] = m;
					m.on("save", this.refresh, this);
					m.exContext.args['schisRecordId'] = this.schisRecordId;
				} else {
					m.exContext.ids = {};
					m.exContext.args["schisRecordId"] = this.schisRecordId;
					m.exContext.ids["empiId"] = this.empiId;
					m.refresh();
				}
				m.actionName = "SchistospmaRecord";
				m.getWin().show();
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.schisRecordId = r.id;
				this.showEhrViewWin();
			},

			doWriteOff : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				var writeOff = this.midiModules["SCH.DocWriteOff"];
				if (!writeOff) {
					writeOff = new chis.application.sch.script.SchistospmaRecordWriteOff({
								record : record,
								entryName : "chis.application.hr.schemas.EHR_MultitermWriteOff",
								title : "档案注销",
								mainApp : this.mainApp
							});
					writeOff.on("remove", this.onRemove, this)
					writeOff.initPanel()
//					writeOff.initPanel();
					this.midiModules["SCH.DocWriteOff"] = writeOff;
				} else {
					writeOff.record = record;
				}
				writeOff.getWin().show();
			},

			onRemove : function() {
				this.loadData()
			}
		});
