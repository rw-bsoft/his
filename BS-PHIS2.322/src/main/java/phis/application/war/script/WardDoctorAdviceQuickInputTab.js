/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.TableForm", "phis.script.EditorList",
		"app.modules.common", "app.modules.combined.TabModule");

phis.application.war.script.WardDoctorAdviceQuickInputTab = function(cfg) {
	cfg.activateId = 0;
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	Ext.apply(this, app.modules.common);
	phis.application.war.script.WardDoctorAdviceQuickInputTab.superclass.constructor
			.apply(this, [cfg]);
	// this.on("afterTabChange", this.afterQuickInputTabChange, this);
}

Ext.extend(phis.application.war.script.WardDoctorAdviceQuickInputTab,
		app.modules.combined.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
								layout : "fit",
								title : ac.name,
								exCfg : ac,
								id : ac.id
							})
				}
				var tab = new Ext.TabPanel({
							border : false,
							width : this.width,
							// activeTab : 0,
							buttonAlign : "center",
							defaults : {
								border : false,
								autoHeight : true
								// autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			afterQuickInputTabChange : function(module) {
				// alert(module.grid)
				var height = this.opener.tab.getHeight();
				module.grid.setHeight(height - 50);
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
					this.refershModule(newTab.id, this.midiModules[newTab.id]);
					this.fireEvent("afterTabChange",
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
//				if (ref) {
//					var result = phis.script.rmi.miniJsonRequestSync({
//								serviceId : "moduleConfigLocator",
//								id : ref
//							})
//					if (result.code == 200) {
//						Ext.apply(cfg, result.json.body)
//					}
//				}
				var cls = cfg.script
				if (!cls) {
					return;
				}
				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
					var m = eval("new " + cls + "(cfg)")
					this.midiModules[newTab.id] = m;
					if (this.exContext) {
						m.exContext = this.exContext;
					}
					// m.onDblClick = function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id, grid
					// .getStore().getAt(index));
					// }
					m.on("choose", function(record) {
								this.fireEvent("quickInput", newTab.id, record);
							}, this)
					var p = m.initPanel();
					// p.on("rowdblclick", function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id,
					// grid.getStore().getAt(index));
					// }, this)
					newTab.add(p);
					newTab.__inited = true;
					this.afterQuickInputTabChange(m);
					// this.fireEvent("afterTabChange", m);
					this.tab.doLayout();
					this.refershModule(newTab.id, m);
				}, this])
			},
			loadModuleCfg : function(id) {
				var result = phis.script.rmi.miniJsonRequestSync({
					url : 'app/loadModule',
					id : id
				})
				if (result.code != 200) {
					if (result.msg == "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [ id ])
					}
					return null;
				}
				var m = result.json.body
				Ext.apply(m, m.properties)
				return m
			},
			refershModule : function(tabId, module) {
				if (!module) {
					module = this.midiModules[tabId];
				}
				if(module.cndField){
					module.cndField.setValue("");
				}
				// if (module.lastCondition == this.exContext.clinicType) {
				// return;
				// }
				// module.lastCondition = this.exContext.clinicType;
				var wardId = this.openBy == 'doctor' ? this.exContext.brxx
						.get("BRKS") : this.mainApp['phis'].wardId
				var cnd = null;
				if (tabId == 'clinicCommon') {
					cnd = [
							'and',
							[
									'and',
									[
											'or',
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'c.SSLB'],
																	['i', 4]],
															[
																	'eq',
																	['$',
																			'c.YGDM'],
																	[
																			's',
																			this.mainApp.uid]]],

													[
															'and',
															[
																	'eq',
																	['$',
																			'c.SSLB'],
																	['i', 5]],
															[
																	'eq',
																	['$',
																			'c.KSDM'],
																	['s',
																			wardId]]]],
											['eq', ['$', 'c.SSLB'], ['i', 6]]],
									['eq', ['$', 'c.ZTLB'], ['i', 4]]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
				} else if (tabId == "medicineCommon") {
					cnd = [
							'and',
							[
									'and',
									[
											'or',
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'c.SSLB'],
																	['i', 4]],
															[
																	'eq',
																	['$',
																			'c.YGDM'],
																	[
																			's',
																			this.mainApp.uid]]],

													[
															'and',
															[
																	'eq',
																	['$',
																			'c.SSLB'],
																	['i', 5]],
															[
																	'eq',
																	['$',
																			'c.KSDM'],
																	['s',
																			wardId]]]],
											['eq', ['$', 'c.SSLB'], ['i', 6]]],
									['lt', ['$', 'c.ZTLB'], ['i', 4]]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
				} else if (tabId == 'medicinePersonalSet') {
					cnd = [
							'and',
							[
									'and',
									[
											'and',
											['lt', ['$', 'ZTLB'], ['i', 4]],
											['eq', ['$', 'JGID'],
													['s', this.mainApp['phisApp'].deptId]]],
									[
											'or',
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'KSDM'],
																	["s",
																			wardId]],
															[
																	'eq',
																	['$',
																			'SSLB'],
																	['i', 2]]],
													[
															'and',
															[
																	'eq',
																	['$',
																			'YGDM'],
																	[
																			"s",
																			this.mainApp.uid]],
															[
																	'eq',
																	['$',
																			'SSLB'],
																	['i', 1]]]],
											['eq', ['$', 'SSLB'], ['i', 3]]]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
				} else if (tabId == 'clinicPersonalSet') {
					cnd = [
							'and',
							[
									'and',
									[
											'and',
											['eq', ['$', 'ZTLB'], ['i', 4]],
											['eq', ['$', 'JGID'],
													['s', this.mainApp['phisApp'].deptId]]],
									[
											'or',
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'KSDM'],
																	["s",
																			wardId]],
															[
																	'eq',
																	['$',
																			'SSLB'],
																	['i', 2]]],
													[
															'and',
															[
																	'eq',
																	['$',
																			'YGDM'],
																	[
																			"s",
																			this.mainApp.uid]],
															[
																	'eq',
																	['$',
																			'SSLB'],
																	['i', 1]]]],
											['eq', ['$', 'SSLB'], ['i', 3]]]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
				} else if (tabId == 'medicineAll') {
					cnd = [
							'and',
							['and', ['eq', ['$', 'b.ZFPB'], ['i', 0]],
									['eq', ['$', 'a.YFZF'], ['i', 0]]],
							['eq', ['$', 'd.JGID'], ['s', this.mainApp['phisApp'].deptId]]]
				} else if (tabId == 'clinicAll') {
					cnd = [
							'and',
							['and', ['eq', ['$', 'a.ZFPB'], ['i', 0]],
									['eq', ['$', 'b.ZFPB'], ['i', 0]]],
							['eq', ['$', 'a.ZYSY'], ['i', 1]]]
				}else if (tabId == "characterCommon") {
					cnd = [
							'and',
							[
									'and',
									[
											'or',
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'b.SSLB'],
																	['i', 4]],
															[
																	'eq',
																	['$',
																			'b.YGDM'],
																	[
																			's',
																			this.mainApp.uid]]],

													[
															'and',
															[
																	'eq',
																	['$',
																			'b.SSLB'],
																	['i', 5]],
															[
																	'eq',
																	['$',
																			'b.KSDM'],
																	['s',
																			wardId]]]],
											['eq', ['$', 'b.SSLB'], ['i', 6]]],
									['eq', ['$', 'b.ZTLB'], ['i', 5]]],
							['eq', ['$', 'b.SFQY'], ['i', 1]]];
				} else if (tabId == 'characterPersonalSet') {
					cnd = [
							'and',
							[
									'and',
									[
											'and',
											['eq', ['$', 'ZTLB'], ['i', 5]],
											['eq', ['$', 'JGID'],
													['s', this.mainApp['phisApp'].deptId]]],
									[
											'or',
											[
													'or',
													[
															'and',
															[
																	'eq',
																	['$',
																			'KSDM'],
																	["s",
																			wardId]],
															[
																	'eq',
																	['$',
																			'SSLB'],
																	['i', 2]]],
													[
															'and',
															[
																	'eq',
																	['$',
																			'YGDM'],
																	[
																			"s",
																			this.mainApp.uid]],
															[
																	'eq',
																	['$',
																			'SSLB'],
																	['i', 1]]]],
											['eq', ['$', 'SSLB'], ['i', 3]]]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
				}
				module.initCnd = cnd;
				module.requestData.cnd = cnd;
				module.loadData();
			}
		});