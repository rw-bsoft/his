/**
 * 医嘱发药-右边tab
 * 
 * @author : caijy
 */
$package("phis.application.hph.script")

$import("phis.script.TabModule")

phis.application.hph.script.HospitalPharmacyDispensingLeftModule = function(cfg) {
	phis.application.hph.script.HospitalPharmacyDispensingLeftModule.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext
		.extend(
				phis.application.hph.script.HospitalPharmacyDispensingLeftModule,
				phis.script.TabModule,
				{
					initPanel : function() {
						if (this.tab) {
							return this.tab;
						}
						var tabItems = []
						var actions = this.actions
						for ( var i = 0; i < actions.length; i++) {
							var ac = actions[i];
							tabItems.push({
								frame : false,
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
						}
						var tab = new Ext.TabPanel({
							title : " ",
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							defaults : {
								border : false
							},
							items : tabItems,
							tbar : []
						})
						tab.on("tabchange", this.onTabChange, this);
						tab.activate(this.activateId)
						this.tab = tab
						return tab;
					},
					showRecord : function() {
						this.doNew();
						if (this.midiModules["tjd"]) {
							this.moduleOperation("loadData",
									this.midiModules["tjd"]);
						}
						if (this.midiModules["br"]) {
							this.moduleOperation("loadData",
									this.midiModules["br"]);
						}
					},
					moduleOperation : function(op, module) {
						var viewType = module.viewType;
						if (op == "loadData") {
							if (!this.fyfs || !this.tjbq) {
								this.doNew();
								return;
							}
							var cnd = [];
							if (viewType == "tjd") {
								cnd = [
										'and',
										[ 'eq', [ '$', 'a.FYFS' ],
												[ 'd', this.fyfs ] ],
										[
												'and',
												[ 'eq', [ '$', 'a.TJBQ' ],
														[ 's', this.tjbq ] ],
												module.cnds ],
										[
												'eq',
												[ '$', 'a.TJYF' ],
												[ 'd', this.mainApp['phis'].pharmacyId ] ] ];
								// module.requestData.initCnd=['and',['eq',['$','a.FYFS'],['d',this.fyfs]],['and',['eq',['$','a.TJBQ'],['s',this.tjbq]],module.cnds],['eq',['$','a.TJYF'],['d',this.mainApp['phis'].pharmacyId]]];
								cnd = this.pzcnd(cnd);
								module.requestData.cnd = cnd;
								module.requestData.initCnd = cnd;
							} else if (viewType == "br") {
								cnd = [
										'and',
										[ 'eq', [ '$', 'a.FYFS' ],
												[ 'd', this.fyfs ] ],
										[
												'and',
												[ 'eq', [ '$', 'a.TJBQ' ],
														[ 's', this.tjbq ] ],
												module.cnds ] ];
								cnd = this.pzcnd(cnd);
								module.requestData.cnd = cnd;
								module.requestData.initCnd = cnd;
								module.requestData.serviceId = module.fullserviceId;
								module.requestData.serviceAction = module.queryServiceActionID;
							}
							module.clearSelect();
							module.loadData();
						}
					},
					// 根据医嘱类型拼装cnd
					pzcnd : function(cnd) {
						if (this.yzlx == 0) {
							return [ 'and',

							[ 'eq', [ '$', 'a.YZLX' ], [ "i", 1 ] ], cnd ];
						} else if (this.yzlx == 1) {
							return [ 'and',
									[ 'eq', [ '$', 'b.LSYZ' ], [ "i", 1 ] ],
									[ 'eq', [ '$', 'a.YZLX' ], [ "i", 1 ] ],
									cnd ];
						} else if (this.yzlx == 2) {
							return [ 'and',
									[ 'eq', [ '$', 'b.LSYZ' ], [ "i", 0 ] ],
									[ 'eq', [ '$', 'a.YZLX' ], [ "i", 1 ] ],
									cnd ];
						} else if (this.yzlx == 3) {
							return [ 'and',
									[ 'eq', [ '$', 'a.YZLX' ], [ "i", 2 ] ],
									cnd ];
						} else if (this.yzlx == 4) {
							return [ 'and',
									[ 'eq', [ '$', 'a.YZLX' ], [ "i", 3 ] ],
									cnd ];
						} else if (this.yzlx == -1) {
							return [ 'and',
									[ 'eq', [ '$', 'a.YZLX' ], [ "i", -1 ] ],
									cnd ];
						}
					},
					doNew : function() {
						if (this.midiModules["tjd"]) {
							this.midiModules["tjd"].clear();
							this.midiModules["tjd"].clearSelect();
						}
						if (this.midiModules["br"]) {
							this.midiModules["br"].clear();
						}
					},
					onTabChange : function(tabPanel, newTab, curTab) {
						this.fireEvent("clear");
						if (newTab.__inited) {
							this.leftm.on("BeforeLoadData",
									this.onBeforeLoadDataLeft, this)
							this.leftm
									.on("loadData", this.onLoadDataLeft, this)
							this.moduleOperation("loadData",
									this.midiModules[newTab.id]);
							return;
						}
						var exCfg = newTab.exCfg
						var cfg = {
							showButtonOnTop : true,
							autoLoadSchema : false,
							isCombined : true
						}
						Ext.apply(cfg, exCfg);
						var ref = exCfg.ref
						if (ref) {
							var body = this.loadModuleCfg(ref);
							Ext.apply(cfg, body)
						}
						var cls = cfg.script
						if (!cls) {
							return;
						}

						if (!this.fireEvent("beforeload", cfg)) {
							return;
						}
						$require(cls, [
								function() {
									var m = eval("new " + cls + "(cfg)");
									m.setMainApp(this.mainApp);
									if (this.initDataId) {
										m.initDataId = this.initDataId;
									}
									this.midiModules[newTab.id] = m;
									this.leftm = m;
									m.on("BeforeLoadData",
											this.onBeforeLoadDataLeft, this)
									m.on("loadData", this.onLoadDataLeft, this)
									var p = m.initPanel();
									m.on("recordClick", this.onRecordClick,
											this)
									m.on("recordClick_br",
											this.onRecordClick_br, this)
									newTab.add(p);
									newTab.__inited = true
									this.tab.doLayout();
									this.moduleOperation("loadData",
											this.midiModules[newTab.id]);
								}, this ])
					},
					// 选中记录
					onRecordClick : function(r) {
						this.fireEvent("recordClick", r);
					},
					// 选中记录(病人TAB)
					onRecordClick_br : function(r) {
						this.fireEvent("recordClick_br", r);
					},
					getData : function() {
						var m = this.midiModules[this.tab.getActiveTab().id]
						var record = m.getSelectedRecords();
						var ret = new Array();
						for ( var i = 0; i < record.length; i++) {
							ret.push(record[i].json);
						}
						return ret;
					},
					onBeforeLoadDataLeft : function() {
						this.fireEvent("BeforeLoadDataLeft");
					},
					onLoadDataLeft : function() {
						this.fireEvent("LoadDataLeft");
					}
				});