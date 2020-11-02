/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("phis.application.cic.script")

$import("app.biz.mds.MedicinesManageModule")

phis.application.cic.script.ClinicPatientList = function(cfg) {
	cfg.activateId = 0;
	phis.application.cic.script.ClinicPatientList.superclass.constructor.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicPatientList, phis.script.TabModule, {
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
								title : ac.title,
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
							autoHeight : true,
							defaults : {
								border : false,
								autoHeight : true,
								autoWidth : true
							},
							items : tabItems
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				if (newTab.__inited) {
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
				$import(cls);
				$require(cls, [function() {
									var m = eval("new " + cls + "(cfg)")
									this.midiModules[newTab.id] = m;
									var p = m.initPanel();
									m.on("save", this.onSuperFormRefresh, this);
									newTab.add(p);
									newTab.__inited = true;
									p.on('rowdblclick',this.rowDblClick,m);
								}, this])
								
				this.tab.doLayout();
			},
			rowDblClick : function(grid, index, e) {
				var record = grid.store.getAt(index);
				var module = this.midiModules["HealthRecord_EHRView"];
						if (!module) {
							$import("com.bsoft.phis.clinic.EMRView");
							module = new com.bsoft.phis.clinic.EMRView({
										empiId : record.data.BRID,
										closeNav : true,
										mainApp : this.mainApp
									});
							this.midiModules["HealthRecord_EHRView"] = module;
							}else {
								module.exContext.ids["empiId"] = record.data.BRID;
								module.refreshTopEmpi();
							}
				module.getWin().show();
			}
		});