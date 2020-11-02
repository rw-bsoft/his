$package("phis.application.war.script");

$import("phis.script.TabModule")

phis.application.war.script.WardDoctorAdviceTab = function(cfg) {
	phis.application.war.script.WardDoctorAdviceTab.superclass.constructor
			.apply(this, [ cfg ])
	this.on("tabchange", this.onMyTabChange, this);
}

Ext.extend(phis.application.war.script.WardDoctorAdviceTab,
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
								frame : this.frame,
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
							// frame : true,
							resizeTabs : this.resizeTabs,
							tabPosition : this.tabPosition || "top",
							// autoHeight : true,
							defaults : {
								border : false
							// autoHeight : true,
							// autoWidth : true
							},
							items : tabItems
						})
						tab.on("tabchange", this.onTabChange, this);
						tab.on("beforetabchange", this.onBeforeTabChange, this);
						tab.activate(this.activateId)
						this.tab = tab
						return tab;
					},
					onBeforeTabChange : function(tabPanel, newTab, curTab) {
						if (curTab && this.midiModules[curTab.id]) {
							var m = this.midiModules[curTab.id];
							var rs = m.store.getModifiedRecords();
							for ( var i = 0; i < rs.length; i++) {
								if (rs[i].get("YPXH")) {
									if (confirm('当前医嘱数据已经修改，是否保存?')) {
										return this.opener.doSave()
									} else {
										m.store.rejectChanges();
										m.refresh();
										return true;
									}
								}
							}
							m.removeEmptyRecord();
						}
						return true;
					},
					onTabChange : function(tabPanel, newTab, curTab) {
						var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardPatientManageService",
							serviceAction : "queryJzyfsz"
						});
						if (newTab) {
							this.newTableft = newTab;
						}
						if (newTab.__inited) {
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
							if (newTab.id == "EmergencyMedicationTab"
									|| newTab.id == "d_EmergencyMedicationTab") {
								if (res.json.JZYF == 0) {
									this.tab.activate(0);
									MyMessageTip.msg("提示",
											"没有设置急诊用药药房,已跳转到长期医嘱!", true);
								} else {
									this.mc.remoteDicStore.baseParams = {
										"YFSZ" : 2
									}
									this.opener.YFSZ = 2
								}
							}else if (newTab.id == "DischargeMedicationTab"
									|| newTab.id == "d_DischargeMedicationTab") {
								if (res.json.CYDY == 0) {
									this.tab.activate(0);
									MyMessageTip.msg("提示",
											"没有设置出院带药药房,已跳转到长期医嘱!", true);
								} else {
									this.mc.remoteDicStore.baseParams = {
										"YFSZ" : 3
									}
									this.opener.YFSZ = 3
								}
							}else{
								this.opener.YFSZ = 1
							}
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
						$require(
								cls,
								[
										function() {
											var m = eval("new " + cls + "(cfg)");
											m.setMainApp(this.mainApp);
											m.openBy = this.openBy;
											m.opener = this.opener;
											this.mc = m;
											if (this.exContext) {
												m.exContext = this.exContext;
											}
											this.midiModules[newTab.id] = m;
											var p = m.initPanel();
											m.on("save", this.onSuperRefresh,
													this)
											m.on("yszhlyy", this.onYszhlyy,
													this)
											newTab.add(p);
											newTab.__inited = true
											this.tab.doLayout();
											this.fireEvent("tabchange",
													tabPanel, newTab, curTab);
											if (newTab.id == "EmergencyMedicationTab"
													|| newTab.id == "d_EmergencyMedicationTab") {
												if (res.json.JZYF == 0) {
													this.tab.activate(0);
													MyMessageTip
															.msg(
																	"提示",
																	"没有设置急诊用药药房,已跳转到长期医嘱!",
																	true);
												} else {
													m.remoteDicStore.baseParams = {
														"YFSZ" : 2
													}
													this.opener.YFSZ = 2
												}
											}else if (newTab.id == "DischargeMedicationTab"
													|| newTab.id == "d_DischargeMedicationTab") {
												if (res.json.CYDY == 0) {
													this.tab.activate(0);
													MyMessageTip
															.msg(
																	"提示",
																	"没有设置出院带药药房,已跳转到长期医嘱!",
																	true);
												} else {
													m.remoteDicStore.baseParams = {
														"YFSZ" : 3
													}
													this.opener.YFSZ = 3
												}
											}else{
												this.opener.YFSZ = 1
											}
										}, this ])
					},
					onMyTabChange : function(tabPanel, newTab, curTab) {
						this.moduleLoadData(newTab.id);
						var curModule = this.midiModules[newTab.id];
						if (curModule.adviceType == "longtime") {
							// setDisabled()方法有样式控制'x-item-disabled'，因为浏览器版本对样式显示效果不同，所以暂且用show和hide解决
							this.opener.panel.getTopToolbar().items.items[3].show();
							this.opener.panel.getTopToolbar().items.items[4].show();
							this.opener.panel.getTopToolbar().items.items[5].show();
						} else if(curModule.adviceType == "temporary"){
							this.opener.panel.getTopToolbar().items.items[3].show();
							this.opener.panel.getTopToolbar().items.items[4].hide();
							this.opener.panel.getTopToolbar().items.items[5].hide();
						}else {
							this.opener.panel.getTopToolbar().items.items[3].hide();
							this.opener.panel.getTopToolbar().items.items[4].hide();
							this.opener.panel.getTopToolbar().items.items[5].hide();
						}
					},
					moduleLoadData : function(id) {
						var curModule = this.midiModules[id];
						if (curModule) {
							// alert(curModule.initDataId + " : " +
							// this.opener.initDataId);
							// if (curModule.initDataId !=
							// this.opener.initDataId) {
							curModule.exContext = this.exContext;
							curModule.initDataId = this.opener.initDataId;
							if (curModule.exList) {
								curModule.exList.requestData.cnd = [
										'and',
										[
												'and',
												[
														'eq',
														[ '$', 'ZYH' ],
														[
																'd',
																this.opener.initDataId ] ],
												[ 'ne', [ '$', 'YZPB' ],
														[ 'i', 0 ] ] ],
										[ 'eq', [ '$', 'JGID' ],
												[ 's', this.mainApp['phisApp'].deptId ] ] ]
								curModule.exList.loadData();
							}
							if (curModule.requestData) {
								var lsyz = curModule.adviceType == "longtime" ? 0
										: 1;
								var cnd = [
										'and',
										[
												'and',
												[
														'and',
														[
																'eq',
																[ '$', 'a.ZYH' ],
																[
																		'd',
																		this.opener.initDataId ] ],
														[
																'eq',
																[ '$', 'a.LSYZ' ],
																[ 'i', lsyz ] ] ],
												[ 'eq', [ '$', 'a.LSBZ' ],
														[ 'i', 0 ] ] ],
										[
												'and',
												[ 'eq', [ '$', 'a.YZPB' ],
														[ 'i', 0 ] ],
												[ 'eq', [ '$', 'a.ZFBZ' ],
														[ 'i', 0 ] ] ] ];
								if (curModule.adviceType == "EmergencyMedication") {
									cnd == [
											'and',
											[
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'a.ZYH' ],
																			[
																					'd',
																					this.opener.initDataId ] ],
																	[
																			'eq',
																			[
																					'$',
																					'a.LSYZ' ],
																			[
																					'i',
																					lsyz ] ] ],
															[
																	'eq',
																	[ '$',
																			'a.LSBZ' ],
																	[ 'i', 0 ] ] ],
													[
															'and',
															[
																	'eq',
																	[ '$',
																			'a.YZPB' ],
																	[ 'i', 0 ] ],
															[
																	'eq',
																	[ '$',
																			'a.ZFBZ' ],
																	[ 'i', 0 ] ] ],
													[ 'eq', [ '$', 'a.XMLX' ],
															[ 'i', 2 ] ] ] ];
								}
								if (curModule.adviceType == "DischargeMedication") {
									cnd == [
											'and',
											[
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'a.ZYH' ],
																			[
																					'd',
																					this.opener.initDataId ] ],
																	[
																			'eq',
																			[
																					'$',
																					'a.LSYZ' ],
																			[
																					'i',
																					lsyz ] ] ],
															[
																	'eq',
																	[ '$',
																			'a.LSBZ' ],
																	[ 'i', 0 ] ] ],
													[
															'and',
															[
																	'eq',
																	[ '$',
																			'a.YZPB' ],
																	[ 'i', 0 ] ],
															[
																	'eq',
																	[ '$',
																			'a.ZFBZ' ],
																	[ 'i', 0 ] ] ],
													[ 'eq', [ '$', 'a.XMLX' ],
															[ 'i', 3 ] ] ] ];
								}
								if (this.openBy == 'nurse') {
									cnd = [
											'and',
											[
													'and',
													cnd,
													[
															'or',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'a.YSBZ' ],
																			[
																					'i',
																					1 ] ],
																	[
																			'eq',
																			[
																					'$',
																					'a.YSTJ ' ],
																			[
																					'i',
																					1 ] ] ],
															[
																	'eq',
																	[ '$',
																			'a.YSBZ' ],
																	[ 'i', 0 ] ] ] ],
											[ 'eq', [ '$', 'a.LSYZ' ],
													[ 'i', lsyz ] ] ]
									if (curModule.adviceType == "EmergencyMedication") {
										cnd = [
												'and',
												[
														'and',
														[
																'and',
																cnd,
																[
																		'or',
																		[
																				'and',
																				[
																						'eq',
																						[
																								'$',
																								'a.YSBZ' ],
																						[
																								'i',
																								1 ] ],
																				[
																						'eq',
																						[
																								'$',
																								'a.YSTJ ' ],
																						[
																								'i',
																								1 ] ] ],
																		[
																				'eq',
																				[
																						'$',
																						'a.YSBZ' ],
																				[
																						'i',
																						0 ] ] ] ],
														[
																'eq',
																[ '$', 'a.LSYZ' ],
																[ 'i', lsyz ] ],
														[
																'eq',
																[ '$', 'a.XMLX' ],
																[ 'i', 2 ] ] ] ]
									}
									if (curModule.adviceType == "DischargeMedication") {
										cnd = [
												'and',
												[
														'and',
														[
																'and',
																cnd,
																[
																		'or',
																		[
																				'and',
																				[
																						'eq',
																						[
																								'$',
																								'a.YSBZ' ],
																						[
																								'i',
																								1 ] ],
																				[
																						'eq',
																						[
																								'$',
																								'a.YSTJ ' ],
																						[
																								'i',
																								1 ] ] ],
																		[
																				'eq',
																				[
																						'$',
																						'a.YSBZ' ],
																				[
																						'i',
																						0 ] ] ] ],
														[
																'eq',
																[ '$', 'a.LSYZ' ],
																[ 'i', lsyz ] ],
														[
																'eq',
																[ '$', 'a.XMLX' ],
																[ 'i', 3 ] ] ] ]
									}
								} else {
									cnd = [
											'and',
											cnd,
											[ 'eq', [ '$', 'a.YSBZ' ],
													[ 'i', 1 ] ] ]
									if (curModule.adviceType == "EmergencyMedication") {
										cnd = [
												'and',
												[
														'and',
														cnd,
														[
																'eq',
																[ '$', 'a.YSBZ' ],
																[ 'i', 1 ] ] ],
												[ 'eq', [ '$', 'a.XMLX' ],
														[ 'i', 2 ] ] ]
									}
									if (curModule.adviceType == "DischargeMedication") {
										cnd = [
												'and',
												[
														'and',
														cnd,
														[
																'eq',
																[ '$', 'a.YSBZ' ],
																[ 'i', 1 ] ] ],
												[ 'eq', [ '$', 'a.XMLX' ],
														[ 'i', 3 ] ] ]
									}
								}
								curModule.initCnd = cnd;
								curModule.requestData.cnd = cnd;
							}
							curModule.loadData();
							// }

						}
					},
					onYszhlyy:function(ypxh){
					var module = this.createModule("YSZHLYYModule",
						"phis.application.mds.MDS/MDS/MDS010101");
				module.name = "药品信息调阅";
				module.yszhlyy = true;
				module.op = "read";
				module.initDataId = ypxh
				var win = module.getWin();
				win.add(module.initPanel());
				win.show();
				win.center();
				module.loadData("read");
					}
				})
