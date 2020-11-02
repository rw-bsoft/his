$package("chis.application.def.script.limb")

$import("chis.script.BizSimpleListView", "chis.script.EHRView")

chis.application.def.script.limb.LimbDeformityRecordListView = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]]
	this.entryName = "chis.application.def.schemas.DEF_LimbDeformityRecord"
	chis.application.def.script.limb.LimbDeformityRecordListView.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.def.script.limb.LimbDeformityRecordListView,
		chis.script.BizSimpleListView, {
			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["DefLimb_EMPIInfoModule"];
				if (!m) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					m = new chis.application.mpi.script.EMPIInfoModule({
								entryName : "chis.application.mpi.schemas.MPI_DemographicInfo",
								title : "个人基本信息查询",
								height : 450,
								modal : true,
								mainApp : this.mainApp
							})
					m.on("onEmpiReturn", this.onEmpiReturn, this)
					this.midiModules["DefLimb_EMPIInfoModule"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiReturn : function(data) {
				var empiId = data.empiId
				this.showModule(empiId)
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (this.store.getCount() == 0) {
					return
				}
				var cfg = this.loadModuleCfg("chis.application.def.DEF/DEF/DEF01_1_1_7")
				cfg.title = "肢体残疾训练登记注销"
				cfg.id = r.get("id")
				cfg.autoLoadSchema = false
				cfg.autoLoadData = true
				cfg.isCombined = true
				cfg.showButtonOnTop = true
				cfg.colCount = 3
				cfg.autoFieldWidth = false
				cfg.fldDefaultWidth = 145
				cfg.record = r
				cfg.mainApp = this.mainApp
				// cfg.exContext = this.exContext
				var module = this.midiModules["LimbDeformityRecordLogoutFormView"]
				if (!module) {
					$import("chis.application.def.script.limb.LimbDeformityRecordLogoutFormView")
					module = new chis.application.def.script.limb.LimbDeformityRecordLogoutFormView(cfg)
					module.on("writeOff", this.onWriteOff, this)
					this.midiModules["LimbDeformityRecordLogoutFormView"] = module
				} else {
					Ext.apply(module, cfg)
				}
				module.getWin().show()
			},
			onWriteOff : function(entryName, op, json, data) {
				var index = this.grid.store.find("phrId", data.phrId);
				var r = this.grid.store.getAt(index);
				if (!r) {
					return
				}
				this.store.remove(r)
				this.store.commitChanges()
			},
			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.exContext.selectedId = r.get("id")
				this.showModule(r.get("empiId"))
			},
			showModule : function(empiId) {
				var module = this.midiModules["DefLimb_EHRView"]
				var cfg = {}
				cfg.initModules = ['DEF_01']
				cfg.empiId = empiId
				cfg.closeNav = true
				cfg.mainApp = this.mainApp
				cfg.exContext = this.exContext
				Ext.apply(cfg.exContext, this.exContext)
				if (!module) {
					module = new chis.script.EHRView(cfg)
					this.midiModules["DefLimb_EHRView"] = module
					module.on("save", this.refreshData, this);

				} else {
					module.exContext.ids.empiId = empiId
					module.refresh();
				}
				module.getWin().show();
			},
			onDblClick : function(grid, index, e) {
				this.doModify();
			},
			refreshData : function(entryName, op, json, data) {
				if (this.store) {
					this.store.load()
				}
			},
			onSave : function(entryName, op, json, data) {
				if(op == "create"){
					this.refresh()
				}
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
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						})
				comb.on("select", this.radioChanged, this)
				comb.setWidth(80)
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
				if (bts.items[7]) {
					var btn = bts.items[7];
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
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}

				var bts = this.grid.getTopToolbar().items;
				if (bts.item(8)) {
					if (r.get("closeFlag") != 'y') {
						bts.item(8).disable();
					} else {
						bts.item(8).enable();
					}
				}
				if (bts.item(7)) {
					if (r.get("status") == '0') {
						bts.item(7).enable();
					} else {
						bts.item(7).disable();
					}
				}

			},
			onStoreLoadData : function(store, records, ops) {
				chis.application.def.script.limb.LimbDeformityRecordListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				this.onRowClick()
			},
			doSee : function() {
				var r = this.grid.getSelectionModel().getSelected()
				if (!r) {
					return
				}
				var cfg = {}
				cfg.title = "肢体残疾结案"
				cfg.defId = r.id
				cfg.autoLoadSchema = false
				cfg.autoLoadData = true
				cfg.isCombined = true
				cfg.showButtonOnTop = false
				cfg.colCount = 3
				cfg.autoFieldWidth = false
				cfg.fldDefaultWidth = 145
				cfg.labelWidth = 110
				cfg.actions = {}
				cfg.mainApp = this.mainApp
				var module = this.midiModules["LimbTerminalEvaluateForm"]
				if (!module) {
					$import("chis.application.def.script.limb.LimbTerminalEvaluateForm")
					module = new chis.application.def.script.limb.LimbTerminalEvaluateForm(cfg)
					module.on("save", this.onSave, this)
					this.midiModules["LimbTerminalEvaluateForm"] = module
				} else {
					Ext.apply(module, cfg)
				}
				module.getWin().show()
			}
		});