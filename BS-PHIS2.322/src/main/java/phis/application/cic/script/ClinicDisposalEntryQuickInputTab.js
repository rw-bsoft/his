/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.cic.script");

$import("phis.script.TabModule");

phis.application.cic.script.ClinicDisposalEntryQuickInputTab = function(cfg) {
	phis.application.cic.script.ClinicDisposalEntryQuickInputTab.superclass.constructor
			.apply(this, [cfg]);

}

Ext.extend(phis.application.cic.script.ClinicDisposalEntryQuickInputTab,
		phis.script.TabModule, {
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
							autoHeight : true,
							buttonAlign : "center",
							defaults : {
								border : false,
								autoHeight : true,
								autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				// tab.activate(this.activateId)
				this.tab = tab
				return tab;
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
//					var result = phis.script.rmi.miniJsonRequestSync({
//								serviceId : "moduleConfigLocator",
//								id : ref
//							})
//					if (result.code == 200) {
//						Ext.apply(cfg, result.json.body)
//					}
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
				$require(cls, [function() {
					var m = eval("new " + cls + "(cfg)")
					this.midiModules[newTab.id] = m;
					// m.onDblClick = function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id, grid.getStore()
					// .getAt(index));
					// }
					m.on("choose", function(record) {
								this.fireEvent("quickInput", newTab.id, record);
							}, this)
					var p = m.initPanel();
					// p.on("rowdblclick", function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id, grid
					// .getStore().getAt(index));
					// }, this)
					newTab.add(p);
					newTab.__inited = true;
					this.fireEvent("afterTabChange", m);
					this.tab.doLayout();
					this.refershModule(newTab.id, m);
				}, this])
			},
			refershModule : function(tabId, module) {
				var cnd = null;
				if (tabId == 'disposalCommon') {
					if (this.mainApp['phis'].departmentId) {
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
																		[
																				's',
																				this.mainApp['phis'].departmentId]]]],
												['eq', ['$', 'c.SSLB'],
														['i', 6]]],
										['eq', ['$', 'c.ZTLB'], ['i', 4]]],
								['eq', ['$', 'SFQY'], ['i', 1]]];
					} else {
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
																				this.mainApp.uid]]]],
												['eq', ['$', 'c.SSLB'],
														['i', 6]]],
										['eq', ['$', 'c.ZTLB'], ['i', 4]]],
								['eq', ['$', 'SFQY'], ['i', 1]]];
					}

				} else if (tabId == 'disposalPersonalSet') {
					if (this.mainApp['phis'].departmentId) {
						cnd = [
								'and',
								[
										'and',
										[
												'and',
												['eq', ['$', 'ZTLB'], ['i', 4]],
												[
														'eq',
														['$', 'JGID'],
														[
																's',
																this.mainApp['phisApp'].deptId]]],
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
																		[
																				"s",
																				this.mainApp['phis'].departmentId]],
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
					} else {
						cnd = [
								'and',
								[
										'and',
										[
												'and',
												['eq', ['$', 'ZTLB'], ['i', 4]],
												[
														'eq',
														['$', 'JGID'],
														[
																's',
																this.mainApp['phisApp'].deptId]]],
										[
												'or',
												[
														'or',

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

				} else if (tabId == 'disposalAll') {
					cnd = [
							'and',
							[
									'and',
									[
											'and',
											['eq', ['$', 'a.ZFPB'], ['i', 0]],
											['eq', ['$', 'a.MZSY'], ['i', 1]],
											['eq', ['$', 'b.JGID'],
													['s', this.mainApp['phisApp'].deptId]]]],
							['eq', ['$', 'b.ZFPB'], ['i', 0]]]
				}
				module.initCnd = cnd;
				module.requestData.cnd = cnd;
				module.loadData();
			}
		});