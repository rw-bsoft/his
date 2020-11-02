$package("chis.application.tr.script.tprc");

$import("chis.script.BizSimpleListView", "chis.script.EHRView")

chis.application.tr.script.tprc.TumourPatientReportCardListView = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.tr.script.tprc.TumourPatientReportCardListView.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.tr.script.tprc.TumourPatientReportCardListView,
		chis.script.BizSimpleListView, {
			onRowClick : function(grid, index, e) {
				this.selectedIndex = index
				var r = this.getSelectedRecord();
				var bts = this.grid.getTopToolbar();
				var visitBtn = bts.find("cmd", "visit");
				if (visitBtn && visitBtn[0]) {
					if (r) {
						var dieFlag = r.get("dieFlag");
						var status = r.get("status");
						if (dieFlag != "y" && status == 0) {
							visitBtn[0].enable();
							return;
						}
					}
					visitBtn[0].disable();
				}
			},

			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				};
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				comb.on("select", this.radioChanged, this);
				comb.setValue("01");
				comb.setWidth(80);
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				cfg.items = [lab, comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd);
					}
					if (queryCnd) {
						cnd.push(queryCnd);
					}
				}

				var bts = this.grid.getTopToolbar().items;
				var btn = bts.items[9];
				if (btn) {
					if (status != "0") {
						btn.disable();
					} else {
						btn.enable();
					}
				}
				var visitBtn = bts.items[7];
				if (visitBtn) {
					if (status != "0") {
						visitBtn.disable();
					} else {
						visitBtn.enable();
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			doCreateTPC : function() {
				var advancedSearchView = this.midiModules["TPC_EMPIInfoModule"];
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
					this.midiModules["TPC_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				this.empiId = data.empiId;
				this.TPRCID = null;
				this.highRiskType = "n";
				this.recordStatus = 0;
				this.activeTab = 0;
				this.showEhrViewWin();
			},
			showEhrViewWin : function() {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['T_04', 'T_05'];
				cfg.mainApp = this.mainApp;
				cfg.activeTab = this.activeTab;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["TumourPatientReportCard_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["TumourPatientReportCard_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.exContext.ids["TPRCID"] = this.TPRCID;
					module.exContext.ids["highRiskType"] = this.highRiskType;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.exContext.ids["TPRCID"] = this.TPRCID;
					module.exContext.ids["highRiskType"] = this.highRiskType;
					module.refresh();
				}
				module.exContext.ids.recordStatus = this.recordStatus;
				module.exContext.args = {};
				module.exContext.args["empiId"] = this.empiId;
				module.exContext.args["TPRCID"] = this.TPRCID;
				module.exContext.args["highRiskType"] = this.highRiskType;
				module.getWin().show();
			},
			doCreateDie : function() {
				var advancedSearchView = this.midiModules["TPC_EMPIInfoModule2"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected2,
							this)
					this.midiModules["TPC_EMPIInfoModule2"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onEmpiSelected2 : function(data) {
				this.empiId = data.empiId;
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.tumourPatientReportCardService",
							serviceAction : "checkUpRecordByEmpiId",
							method : "execute",
							body : {
								empiId : this.empiId
							}
						});
				var body = result.json;
				this.phrId = body.phrId;
				if (body.pkey) {
					this.updateRecord(body.pkey, {key:body.regionCode,text:body.regionCode_text});
				} else if(body.regionCode){
					this.updateRecord(null,{key:body.regionCode,text:body.regionCode_text});
				}else {
					this.updateRecord();
				}
			},

			updateRecord : function(id, regionCode) {
				var module = this.midiModules["TumourPatientReportCard_Form"];
				if (!module) {
					$import("chis.application.tr.script.tprc.TumourPatientReportCardForm2")
					module = new chis.application.tr.script.tprc.TumourPatientReportCardForm2(
							{
								entryName : "chis.application.tr.schemas.MDC_TumourPatientReportCard2",
								title : "肿瘤患者报告卡",
								modal : true,
								width : 765,
								mainApp : this.mainApp
							});
					module.on("save", this.refresh, this);
					this.midiModules["TumourPatientReportCard_Form"] = module;
				}
				module.empiId = this.empiId;
				module.phrId = this.phrId;
				var win = module.getWin();
				var form = module.initPanel();
				win.add(form)
				module.doCreate();
				win.show();
				if(regionCode){
					module.regionCode = regionCode;
					//module.initRegionCode();
				}
				if (id) {
					module.initDataId = id;
					module.loadData();
				}
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.TPRCID = r.get("TPRCID");
				this.highRiskType = r.get("highRiskType");
				var dieFlag = r.get("dieFlag");
				if (dieFlag != "y") {
					this.recordStatus = r.get("status");
					this.activeTab = 0;
					this.showEhrViewWin();
				} else {
					this.phrId = r.get("phrId");
					var TPRCID = r.get("TPRCID");
					var regionCode = {key:r.get("regionCode"),text:r.get("regionCode_text")};
					this.updateRecord(TPRCID,regionCode);
				}
			},
			onDblClick : function() {
				this.doModify();
			},
			doVisit : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.TPRCID = r.get("TPRCID");
				this.highRiskType = r.get("highRiskType");
				this.recordStatus = r.get("status");
				this.activeTab = 1;
				this.showEhrViewWin();
			},
			doWriteOff : function() {
				var r = this.getSelectedRecord();
				if (this.store.getCount() == 0) {
					return;
				}
				var cfg = {
					title : "肿瘤患者报告卡注销",
					TPRCID : r.get("TPRCID"),
					phrId : r.get("phrId"),
					personName : r.get("personName"),
					empiId : r.get("empiId"),
					highRiskType : r.get("highRiskType"),
					mainApp : this.mainApp
				};
				var module = this.midiModules["TumourPatientReportCardLogoutForm"];
				if (!module) {
					$import("chis.application.tr.script.tprc.TumourPatientReportCardLogoutForm");
					module = new chis.application.tr.script.tprc.TumourPatientReportCardLogoutForm(cfg);
					module.on("writeOff", this.onWriteOff, this);
					module.initPanel();
					this.midiModules["TumourPatientReportCardLogoutForm"] = module;
				} else {
					Ext.apply(module, cfg);
				}
				module.getWin().show();
			},
			onWriteOff : function() {
				this.refresh();
			}
		});