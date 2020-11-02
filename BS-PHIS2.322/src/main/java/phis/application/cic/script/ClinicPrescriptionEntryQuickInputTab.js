/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.cic.script");

$import("phis.script.common", "phis.script.TabModule");

phis.application.cic.script.ClinicPrescriptionEntryQuickInputTab = function(cfg) {
	cfg.activateId = 0;
	this.serviceId = cfg.serviceId;
	this.saveServiceAction = cfg.saveServiceAction;
	Ext.apply(this, phis.script.common);
	phis.application.cic.script.ClinicPrescriptionEntryQuickInputTab.superclass.constructor
			.apply(this, [cfg]);

}

Ext.extend(phis.application.cic.script.ClinicPrescriptionEntryQuickInputTab,
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
					// var result = phis.script.rmi.miniJsonRequestSync({
					// serviceId : "moduleConfigLocator",
					// id : ref
					// })
					// if (result.code == 200) {
					// Ext.apply(cfg, result.json.body)
					// }
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
					var m = eval("new " + cls + "(cfg)");
					m.mainApp = this.mainApp;
					m.exContext = this.exContext;
					this.midiModules[newTab.id] = m;
					// m.onDblClick = function(grid, index, e) {
					// this.fireEvent("quickInput", newTab.id,
					// grid
					// .getStore().getAt(index));
					// }
					m.on("choose", function(record) {
								this.fireEvent("quickInput", newTab.id, record);
							}, this)
					m.on("shortcutKey", function(keyCode, keyName) {
								this.fireEvent("shortcutKey", keyCode, keyName)
							}, this);
					var p = m.initPanel();
					// p.on("rowdblclick", function(grid, index,
					// e) {
					// this.fireEvent("quickInput", newTab.id,
					// grid.getStore().getAt(index));
					// }, this)
					newTab.add(p);
					newTab.__inited = true;
					this.fireEvent("afterTabChange", m);
					this.tab.doLayout();
					this.refershModule(newTab.id, m);
				}, this])
			},
			refershModule : function(tabId, module) {
				if (!module) {
					module = this.midiModules[tabId];
				}
				if (module.lastCondition == this.exContext.clinicType) {
					module.selectFirstRow();
					return;
				}
				module.lastCondition = this.exContext.clinicType;
				var cnd = null;
				if (tabId == 'clinicCommon') {
					cnd = [
							'and',
							[
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
																			[
																					'$',
																					'c.SSLB'],
																			[
																					'i',
																					4]],
																	[
																			'eq',
																			[
																					'$',
																					'c.YGDM'],
																			[
																					's',
																					this.mainApp.uid]]],

															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'c.SSLB'],
																			[
																					'i',
																					5]],
																	[
																			'eq',
																			[
																					'$',
																					'c.KSDM'],
																			[
																					's',
																					this.mainApp['phis'].departmentId]]]],
													['eq', ['$', 'c.SSLB'],
															['i', 6]]],
											[
													'eq',
													['$', 'c.ZTLB'],
													[
															'i',
															this.exContext.clinicType]]],
									['eq', ['$', 'SFQY'], ['i', 1]]],
							['eq', ['$', 'c.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
				} else if (tabId == 'clinicPersonalSet') {
					cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													[
															'eq',
															['$', 'JGID'],
															[
																	's',
																	this.mainApp['phisApp'].deptId]],
													[
															'eq',
															['$', 'YGDM'],
															[
																	's',
																	this.mainApp.uid]]],
											['eq', ['$', 'SSLB'], ['i', 1]]],
									['eq', ['$', 'SFQY'], ['i', 1]]],
							['eq', ['$', 'ZTLB'],
									['i', this.exContext.clinicType]]];;
				} else if (tabId == 'clinicAll') {
					cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'b.ZFPB'],
															['i', 0]],
													['eq', ['$', 'a.YFZF'],
															['i', 0]]],
											[
													'eq',
													['$', 'a.YFSB'],
													[
															'd',
															this.exContext.pharmacyId]]],
									['eq', ['$', 'd.CFLX'],
											['i', this.exContext.clinicType]]],
							['eq', ['$', 'd.JGID'], ['s', this.mainApp['phisApp'].deptId]]]
				}
				module.initCnd = cnd;
				module.requestData.cnd = cnd;
				module.loadData();
			}
		});