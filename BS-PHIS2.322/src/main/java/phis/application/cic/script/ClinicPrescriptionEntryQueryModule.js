$package("phis.application.cic.script")

$import("phis.script.SimpleModule", "phis.script.rmi.miniJsonRequestSync",
		"org.ext.ux.TabCloseMenu")

phis.application.cic.script.ClinicPrescriptionEntryQueryModule = function(cfg) {
	cfg.modal = true;
	cfg.width = 1024;
	this.serviceId = "clinicManageService";
	this.openedBy = "clinicQuery";
	phis.application.cic.script.ClinicPrescriptionEntryQueryModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicPrescriptionEntryQueryModule,
		phis.script.SimpleModule, {
			loadSystemParam : function() {
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "loadSystemParams",
							body : {
								// commons : ['XYF', 'ZYF', 'CYF'],
								privates : ['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_XY', 'YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_ZY',
										'YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_CY']
							}
						});
				this.exContext.systemParams = resData.json.body;
				this.exContext.systemParams.YS_MZ_FYYF_XY = this.exContext.systemParams['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_XY']=='null'?"0":this.exContext.systemParams['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_XY'];
				this.exContext.systemParams.YS_MZ_FYYF_ZY = this.exContext.systemParams['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_ZY']=='null'?"0":this.exContext.systemParams['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_ZY'];
				this.exContext.systemParams.YS_MZ_FYYF_CY = this.exContext.systemParams['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_CY']=='null'?"0":this.exContext.systemParams['YS_MZ_FYYF_'+this.mainApp.phis.reg_departmentId+'_CY'];
			},
			getPharmacyIdByCFLX : function(type) {
				type = parseInt(type);
				switch (type) {
					case 1 :
						return this.exContext.systemParams.YS_MZ_FYYF_XY;
					case 2 :
						return this.exContext.systemParams.YS_MZ_FYYF_ZY;
					case 3 :
						return this.exContext.systemParams.YS_MZ_FYYF_CY;
					default :
						return this.exContext.systemParams.YS_MZ_FYYF_XY;
				}
			},
			initPanel : function() {
				// 获得门诊发药药房信息，该方法包含医生权限信息
				if (this.tab) {
					return this.tab;
				}
				this.loadSystemParam();
				this.tabIndex = 1;
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 474,
							items : [{
										layout : "fit",
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										region : 'north',
										height : 85,
										items : this.getForm()
									}]
						});
				this.panel = panel;
				var tab = new Ext.TabPanel({
							border : false,
							width : this.width,
							activeTab : 0,
							frame : true,
							// autoHeight : true,
							defaults : {
								border : false,
								// autoHeight : true,
								autoWidth : true
							},
							deferredRender : false,
							enableTabScroll : true
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("afterrender", this.onReady, this);
				this.tab = tab;
				return this.tab;
			},
			onReady : function() {
				// this.form.getForm().findField("KSDM").setDisabled(false);
				// this.form.getForm().findField("YSDM").setDisabled(false);
				var firstLoad = true;
				// this.form.getForm().findField("KSDM").getStore().on("load",
				// function() {
				// if (firstLoad) {
				// firstLoad = false;
				// this.form.getForm().findField("KSDM")
				// .setValue(this.mainApp['phis'].departmentId);
				// }
				// }, this);
				// 设置按钮状态
				var btns = this.list.grid.getTopToolbar().items;
				for (var i = 0; i < btns.getCount(); i++) {
					var btn = btns.item(i);
					if (i != 10) { //i != 9
						btn.hide();
					} else {
						btn.show();
					}
				}
				// 设置form状态
				this.form.getForm().findField("CFTS").setDisabled(true);
				this.form.getForm().findField("KFRQ").setDisabled(true);
				this.form.getForm().findField("CFLX").setDisabled(true);
				this.on("winShow", this.onWinShow, this);
			},
			onTabChange : function(tabPanel, newTab, curTab) {
				// this.list.ypzh = 1;

				if (!newTab) {
					this.doNew();
					return;
				}
				newTab.add(this.panel);
				this.tab.doLayout();
				if (newTab.cfsb) {
					this.doLoadCF02(newTab.cfsb, newTab.fphm)
					if (newTab.fphm && newTab.zfpb == 1) {
						this.setChargeInfo(true, 'cancelled.gif');
					} else {
						this.setChargeInfo(newTab.fphm ? true : false);
					}
				} else {
					if (this.hasDeleted) {// 是否删除完成
						this.doNew();
					}
				}
			},
			// 增加指定显示图片显示的功能
			setChargeInfo : function(disabled, picture) {
				if (this.panel.chargeStatus != disabled
						|| this.panel.picture != picture) {
					this.panel.chargeStatus = disabled;
					if (!picture) {
						picture = "charge.png";
					}
					this.panel.picture = picture;
					// 已收费标签
					if (disabled) {
						if (!this.msgCt) {
							this.msgCt = Ext.DomHelper.insertFirst(
									this.list.grid.body, {
										id : 'charge-div',
										style : 'position:absolute;bottom:17px;right:5px;margin:0 auto;z-index:20001;'
									}, true);
						}
						if (this.m) {
							this.m.remove();
						}
						this.m = Ext.DomHelper.append(this.msgCt, {
									html : "<img src='"
											+ ClassLoader.appRootOffsetPath
											+ "resources/phis/resources/images/"
											+ picture + "' />"
								}, true);
						// this.m.slideIn('t');
						this.quickInputPanel.hide();
						this.complexPanel.doLayout();

					} else {
						if (this.msgCt) {
							this.m.remove();
						}
						this.quickInputPanel.show();
						this.complexPanel.doLayout();
					}

					// 设置按钮状态
					var btns = this.list.grid.getTopToolbar().items;
					for (var i = 0; i < btns.getCount(); i++) {
						var btn = btns.item(i);
						if (disabled) {
							if (i != 5 && i != 9 && i != 10 && i != 11) {
								btn.disable();
							}
						} else {
							btn.enable();
						}
					}
					// 设置form状态
					this.form.getForm().findField("CFTS").setDisabled(disabled);
					this.form.getForm().findField("KFRQ").setDisabled(disabled);
					this.form.getForm().findField("CFLX").setDisabled(disabled);
					// 附加项目
					if (this.exList) {
						var btns = this.exList.grid.getTopToolbar().items;
						for (var i = 0; i < btns.getCount(); i++) {
							var btn = btns.item(i);
							if (disabled) {
								btn.disable();
							} else {
								btn.enable();
							}
						}
					}
				}
			},
			onWinShow : function() {
				// winShow时先清空tab的cfsb，防止删除时触发tabChange的查询
				this.hasDeleted = false;
				for (var i = 0; i < this.tab.items.length; i++) {
					var tab = this.tab.getItem(i);
					if (tab.cfsb) {
						delete tab.cfsb;
					}
				}
				try {
					this.tab.removeAll(false);
				} catch (e) {

				}
				this.hasDeleted = true;
				var cf01s = this.cfsbs;
				var tabItems = [];
				if (cf01s == null || cf01s.length == 0) {
					tabItems.push({
								cfsb : "",
								title : "*新处方(1)"
							});
					// 新处方设置处方类型
					this.exContext.clinicType = 1;
					this.exContext.pharmacyId = this.getPharmacyIdByCFLX(1);
					this.tab.add(tabItems);
					this.tab.setActiveTab(0);
					// this.tab.doLayout();
				} else {
					for (var i = 0; i < cf01s.length; i++) {
						var cf01 = cf01s[i];
						if (cf01.CFLX == 1) {
							name = "西药方"
						} else if (cf01.CFLX == 2) {
							name = "中药方"
						} else if ((cf01.CFLX == 3)) {
							name = "草药方"
						}
						tabItems.push({
									cfsb : cf01.CFSB,
									zfpb : cf01.ZFPB,
									fphm : cf01.FPHM,
									title : name + cf01.CFHM
								});
					}
					this.tab.add(tabItems);
					this.tab.setActiveTab(0);
					// this.tab.doLayout();
				}

			},
			getForm : function() {
				var module = this.midiModules['prescriptionEntryForm'];
				if (!module) {
					module = this.createModule("prescriptionEntryForm",
							this.clinicPrescriptionEntryForm);
					module.on("loadData", function() {
						var cflx = module.form.getForm().findField("CFLX").value;
						this.changeWindow(cflx);
						this.changeClinicType(parseInt(cflx))
					}, this);
					module.initFormData = function(data) {
						Ext.apply(this.data, data)
						this.initDataId = this.data[this.schema.pkey]
						var form = this.form.getForm()
						var items = this.schema.items
						var n = items.length
						for (var i = 0; i < n; i++) {
							var it = items[i]
							var f = form.findField(it.id)
							if (f) {
								var v = data[it.id]
								if (v != undefined) {
									f.setValue(v)
								}
								if (it.update == "false") {
									f.disable();
								}
							}
						}
						this.setKeyReadOnly(true)
						// this.focusFieldAfter(-1, 800)
					};
					module.doNew = function() {
						var form = module.form.getForm()
						var items = module.schema.items
						var n = items.length
						for (var i = 0; i < n; i++) {
							var it = items[i]
							var f = form.findField(it.id)
							if (f) {
								f.setValue(it.defaultValue)
								// @@ 2010-01-07 modified by chinnsii, changed
								// the
								// condition
								// "it.update" to "!=false"
								if (it.type == "date") { // ** add by yzh
									// 20100919 **
									if (it.minValue)
										f.setMinValue(it.minValue)
									if (it.maxValue)
										f.setMaxValue(it.maxValue)
								}
								// add by yangl 2012-06-29
								if (it.dic && it.dic.defaultIndex) {
									if (f.store.getCount() == 0)
										return;
									if (isNaN(it.dic.defaultIndex)
											|| f.store.getCount() <= it.dic.defaultIndex)
										it.dic.defaultIndex = 0;
									f.setValue(f.store
											.getAt(it.dic.defaultIndex)
											.get('key'));
								}
							}
						}
						form.findField("KFRQ").setValue(new Date()
								.format("Y-m-d H:i:s"));
					}
					var form = module.initPanel();
					this.form = form;
				}
				var CFLX = form.getForm().findField("CFLX");
				var CFTS = form.getForm().findField("CFTS");
				// CFHM.un("specialkey",module.onFieldSpecialkey,module);
				// CFHM.on("change", this.doSpecialkey, this);
				CFTS.on("focus", function() {
							this.selectText();
						}, CFTS);
				CFTS.on("change", this.onCftsChange, this);
				CFLX.on("select", this.onClinicSelect, this);
				CFLX.on("beforeselect", this.onbeforeClinicSelect, this);
				CFTS.hide();
				return form;
			},
			getList : function() {
				this.list = this.midiModules['prescriptionEntryList'];
				if (!this.list) {
					var module = this.createModule("prescriptionEntryList",
							this.clinicPrescriptionEntryList);
					module.exContext = this.exContext;
					module.openedBy = this.openedBy;
					module.opener = this;
					var list = module.initPanel();
					// 默认隐藏每帖数量
					// list.getColumnModel().setHidden(
					// list.getColumnModel().getIndexById('YPZS'), true);
					var sm = list.getSelectionModel();
					// 重写onEditorKey方法，实现Enter键导航功能
					sm.onEditorKey = function(field, e) {
						var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
						if (k == e.ENTER) {
							e.stopEvent();
							if (!ed) {
								ed = g.lastActiveEditor;
							}
							ed.completeEdit();
							if (e.shiftKey) {
								newCell = g.walkCells(ed.row, ed.col - 1, -1,
										sm.acceptsNav, sm);
							} else {
								newCell = g.walkCells(ed.row, ed.col + 1, 1,
										sm.acceptsNav, sm);
							}

						} else if (k == e.TAB) {
							e.stopEvent();
							ed.completeEdit();
							if (e.shiftKey) {
								newCell = g.walkCells(ed.row, ed.col - 1, -1,
										sm.acceptsNav, sm);
							} else {
								newCell = g.walkCells(ed.row, ed.col + 1, 1,
										sm.acceptsNav, sm);
							}
						} else if (k == e.ESC) {
							ed.cancelEdit();
						}
						if (newCell) {
							r = newCell[0];
							c = newCell[1];
							this.select(r, c);
							if (g.isEditor && !g.editing) {
								ae = g.activeEditor;
								if (ae && ae.field.triggerBlur) {
									ae.field.triggerBlur();
								}
								g.startEditing(r, c);
							}
						}

					};
					this.list = module;
				}
				// 不修改的情况下不触发afterCellEdit
				module.on("beforeCellEdit", this.beforeGridEdit, this);
				module.on("afterCellEdit", this.afterGridEdit, this);
				module.on("doNew", this.doNew, this);
				module.on("doSave", this.doSave, this);
				module.on("doRemove", this.doRemoveCF02, this);
				module.on("loadData", this.listLoadData, this);
				// 删除处方
				var _ctx = this;
				this.list.doDelClinic = function() {
					var curTab = _ctx.tab.getActiveTab();
					Ext.Msg.show({
						title : '确认删除处方信息',
						msg : '删除操作将无法恢复，是否继续?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								// 删除处方信息
								if (curTab.cfsb > 0) {
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "removeClinicInfo",
												pkey : curTab.cfsb
											}, function(code, msg, json) {
												this.list.grid.el.unmask()
												if (code >= 300) {
													this.processReturnMsg(code,
															msg);
													return;
												}
												if (this.tab.items.length > 1) {
													this.tab.remove(curTab);
												} else {
													this.list.store.removeAll();
													var tab = this.tab
															.getActiveTab();
													tab.cfsb = null;
													tab.setTitle("新处方(1)");
													this.doNew();
												}
												this.fireEvent("doRemove",
														curTab.cfsb);
											}, this)
								} else {
									if (this.tab.items.length > 1) {
										this.tab.remove(curTab);
									} else {
										this.list.store.removeAll();
										var tab = this.tab.getActiveTab();
										tab.cfsb = null;
										tab.setTitle("新处方(1)");
										this.doNew();
									}
								}
							}
						},
						scope : _ctx
					})
				}
				return list;
			},
			onCftsChange : function(f, value) {
				if (f.validate()) {
					for (var i = 0; i < this.list.store.getCount(); i++) {
						var r = this.list.store.getAt(i);
						this.setMedQuantity(r);
					}
					this.setCountInfo();
				}
			},
			listLoadData : function() {
				if (this.list.store.getCount() > 0) {
					this.setCountInfo();
				}
			},
			doLoadCF02 : function(value) {
				// 查询处方号码是否存在，若存在，导入处方信息
				if (value) {
					// 若处方号码存在，根据处方状态设置按钮状态
					var peform = this.midiModules['prescriptionEntryForm'];
					if (peform) {
						peform.initDataId = value;
						peform.loadData();
					}
					this.list.requestData.cnd = ['eq', ['$', 'a.CFSB'],
							['d', value]];
					this.list.loadData();
				}
			},
			doRemoveCF02 : function(sbxh) {
				if (!this.removedSBXH) {
					this.removedSBXH = [];
				}
				this.removedSBXH.push({
							_opStatus : "remove",
							SBXH : sbxh
						});
			},
			onbeforeClinicSelect : function() {
				var CFTS = this.form.getForm().findField("CFTS");
				var grid = this.list.grid;
				for (var i = 0; i < grid.getStore().getCount(); i++) {
					if (grid.getStore().getAt(i).get("YPXH")) {
						MyMessageTip.msg("警告", "当前处方已存在处方明细，不允许变更类型!", true);
						return false;
					}
				}
			},
			onClinicSelect : function(f, record) {
				this.changeClinicType(parseInt(f.value))
				this.changeWindow(parseInt(f.value));
				var grid = this.list.grid;
				grid.getStore().removeAll();
				this.list.doInsert();
			},
			changeWindow : function(cflx) {
				if (cflx == 3) {
					this.list.grid.getColumnModel().setColumnHeader(8, "每帖数量");
					this.form.getForm().findField("CFTS").show();
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YCJL'), true);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YYTS'), true);
				} else {
					this.list.grid.getColumnModel().setColumnHeader(8, "总量");
					this.form.getForm().findField("CFTS").hide();
					// this.list.grid.getColumnModel().setHidden(
					// this.list.grid.getColumnModel()
					// .getIndexById('YPZS'), true);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YCJL'), false);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YYTS'), false);
				}
			},
			setCountInfo : function() {
				var totalMoney = 0;
				var selfMoney = 0;
				var cfts = 1;
				if (this.form.getForm().findField("CFLX").getValue() == 3) {
					cfts = parseInt(this.form.getForm().findField("CFTS")
							.getValue());
				}
				for (var i = 0; i < this.list.store.getCount(); i++) {
					var r = this.list.store.getAt(i);
					var ypsl = parseFloat(r.get("YPSL"));
					var ypdj = parseFloat(r.get("YPDJ"));
					var zfbl = parseFloat(r.get("ZFBL"));
					if(!r.get("ZFYP")){
					totalMoney += parseFloat(ypsl * ypdj * cfts);
					selfMoney += parseFloat(ypsl * ypdj * zfbl * cfts);
					}
					if (isNaN(totalMoney)) {
						totalMoney = 0;
					}
					if (isNaN(selfMoney)) {
						selfMoney = 0;
					}
				}

				document.getElementById("cfmx_tjxx_" + this.openedBy).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "合计金额："
						+ parseFloat(totalMoney).toFixed(2)
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "自负金额："
						+ parseFloat(selfMoney).toFixed(2) + "&nbsp;&nbsp;￥";
			},
			doNew : function() {
				// 清空form和list缓存数据
				if (this.removedSBXH) {
					this.removedSBXH = [];
				}
				var form = this.midiModules['prescriptionEntryForm'];
				if (form.data) {
					form.data = {};
				}
				this.setChargeInfo(false);
				this.changeClinicType(1);
				// 判断当前是否是新处方
				for (var i = 0; i < this.tab.items.length; i++) {
					var tab = this.tab.getItem(i);
					if (!tab.cfsb) {
						// 切换到新处方模式
						this.tab.setActiveTab(tab);
						this.midiModules['prescriptionEntryForm'].doNew();
						this.form.getForm().findField("CFTS").setValue(1);
						this.form.getForm().findField("KSDM")
								.setValue(this.mainApp['phis'].departmentId);
						this.changeWindow(1);
						this.list.grid.getStore().removeAll();
						this.list.doInsert();
						this.setCountInfo();
						return;
					}
				}
				this.setCountInfo();
				var curTab = this.tab.getActiveTab();
				if (!curTab) {
					return;
				}
				if (curTab.cfsb) {
					var tabItem = {
						title : "*新处方(" + (++this.tabIndex) + ")"
					}
					var newTab = this.tab.add(tabItem);
					this.tab.setActiveTab(newTab);
				} else {
					this.midiModules['prescriptionEntryForm'].doNew();
					this.form.getForm().findField("KSDM")
							.setValue(this.mainApp['phis'].departmentId);
					this.list.store.removeAll();
					this.list.doInsert();
				}
			},
			changeClinicType : function(clinicType) {
				if (!this.list.remoteDicStore.baseParams) {
					this.list.remoteDicStore.baseParams = {};
				}
				if (this.getPharmacyIdByCFLX(clinicType))
					this.exContext.clinicType = clinicType;
				this.exContext.pharmacyId = this
						.getPharmacyIdByCFLX(clinicType);
				this.list.remoteDic.lastQuery = "";
				this.list.remoteDicStore.baseParams.type = clinicType;
				this.list.remoteDicStore.baseParams.pharmacyId = this
						.getPharmacyIdByCFLX(clinicType);
			}
		});