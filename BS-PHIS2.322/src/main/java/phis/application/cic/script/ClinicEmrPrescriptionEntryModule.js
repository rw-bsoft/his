﻿$package("phis.application.cic.script")

$import("phis.script.SimpleModule", "phis.script.rmi.miniJsonRequestSync",
		"org.ext.ux.TabCloseMenu", "phis.script.util.DateUtil")

phis.application.cic.script.ClinicEmrPrescriptionEntryModule = function(cfg) {
	cfg.modal = true;
	cfg.width = 1024;
	this.serviceId = "clinicManageService";
	this.openedBy = "doctorStation";
	this.firstLoad = true;
	phis.application.cic.script.ClinicEmrPrescriptionEntryModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cic.script.ClinicEmrPrescriptionEntryModule,
		phis.script.SimpleModule, {
			/**
			 * 代理实现：适用于组合模块，按钮在其中某个特定的模块的时候，不需要每个事件方法实现
			 * 
			 * @param {}
			 *            keyCode
			 * @param {}
			 *            keyName
			 */
			keyManageFunc : function(keyCode, keyName) {
				this.list.doAction(this.list.btnAccessKeys[keyCode]);
				// MyMessageTip.msg("提示", "处方F1"+keyCode+keyName, true);
			},
			getTab : function() {
				// 判断是否已经存在处方信息，若存在，载入处方信息
				// 名称为“处方一”、“处方二”、“+”
				var jzxh = this.exContext.ids.clinicId;
				// 载入处方信息
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "loadCF01",
							body : {
								"brid" : this.exContext.ids.brid,
								"clinicId" : this.exContext.ids.clinicId,
								"type" : "3"
							}
						});
				return resData.json.cf01s;
			},
			onTabShow : function() {
//				var resData = phis.script.rmi.miniJsonRequestSync({
//					serviceId : "clinicManageService",
//					serviceAction : "loadClinicInfo",
//					body : {
//						"brid" : this.exContext.ids.brid,
//						"clinicId" : this.exContext.ids.clinicId,
//						"type" : '3'
//					}
//				});
//				if(resData.code>=300){
//					this.processReturnMsg(resData.code, resData.msg);
//				}
//				var measures = resData.json.measures;
////				var disposal = resData.json.disposal;
//				var cfStr = '';
//					var num = 1;
//					// var isHerbs = false;
//					if (measures != null && measures.length > 0) {
//						for (var i = 0; i < measures.length; i++) {
//							var r = measures[i];
//							if (num == 1) {
//								cfStr += num + '.';
//								num++;
//							} else {
//								if (r.CFSB != measures[i - 1].CFSB) {
//									cfStr += num + '.';
//									num++;
//								} else {
//									cfStr += '  ';
//								}
//							}
//							cfStr += r.YPMC+"("+r.YCJL+(r.JLDW ? r.JLDW : '')+") ";
//							cfStr += r.YPSL + (r.YFDW ? r.YFDW : '');
//								if (r.YFGG) {
//									cfStr += '/' + r.YFGG + ' ';
//								}
//								cfStr += r.YPYF_text + ' ' + r.GYTJ_text;
//								if(i+1<measures.length){
//									cfStr += '\n';
//								}
//						}
//					}
					this.opener.Backfill("MS_CF01");
			},
			/**
			 * 获得处方模块基础信息 MZ_FYYF : 门诊发药药房信息 MZ_YSQX : 门诊医生处方权限信息 YP_TSYP :
			 * 门诊药品特殊药品标记
			 */
			getClinicInitParams : function() {
				if (!this.mainApp['phis'].departmentId) {
					MyMessageTip.msg("提示", "请先选择科室信息!", true);
					return -1;
				}
				if (!this.exContext) {
					MyMessageTip.msg("提示", "无效的就诊信息!无法打开处方录入模块!", true);
					return -1;
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "loadOutClinicInitParams"
						});
				var docPermissions = resData.json.MZ_YSQX;
				if (!docPermissions || !docPermissions.KCFQ
						|| docPermissions.KCFQ < 1) {
					MyMessageTip.msg("提示", "对不起，您没有开处方的权限!", true);
					return -1
				}
				this.exContext.docPermissions = docPermissions;
				return 1;
			},
			getPharmacyIdByCFLX : function(type) {
				if (this.exContext.systemParams.HQFYYF == '1') {
					return this.form.getForm().findField("YFSB").getValue()
							|| -1;
				}
				type = parseInt(type);
				var PharmacyId = null;
				switch (type) {
					case 1 :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_XY;
						break;
					case 2 :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_ZY;
						break;
					case 3 :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_CY;
						break;
					default :
						PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_XY;
				}
				return PharmacyId;
			},
			initPanel : function() {
				// 处方编辑

				// 获得门诊发药药房信息，该方法包含医生权限信息
				if (this.complexPanel) {
					return this.complexPanel;
				}
				// this.loadSystemParams();
				this.tabIndex = 1;
				var heigh = 700
				var labelHeigh = 700;
				var item = [{
							layout : "fit",
							region : 'center',
							items : this.getList()
						}, {
							layout : "fit",
							border : false,
							region : 'north',
							height : 71,
							items : this.getForm()
						}]
				var fjxmPanel = new Ext.Panel({
							layout : "fit",
							border : false,
							region : 'south',
							height : 150,
							items : this.getFJList()
						});
				if (this.exContext.systemParams.XSFJJJ == 1) {
					fjxmPanel.show();
				} else {
					fjxmPanel.hide();
				}
				item.push(fjxmPanel);
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height : heigh,
							items : item
						});

				var cf01s = this.getTab();
				var tabItems = [];
				if (cf01s == null || cf01s.length == 0) {
					tabItems.push({
								cfsb : "",
								title : "*新处方(1)",
								items : panel
							});
					// 新处方设置处方类型
					this.exContext.clinicType = 1;
					this.exContext.pharmacyId = this.getPharmacyIdByCFLX(1);
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
									fphm : cf01.FPHM,
									zfpb : cf01.ZFPB,
									title : name + cf01.CFHM,
									items : panel
								});
					}
				}
				this.panel = panel;
				this.list.exList = this.exList;
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
							items : tabItems,
							deferredRender : false,
							enableTabScroll : true
						})
				this.on("beforeclose", this.beforeclose, this);
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.beforeTabChange, this);
				tab.on("afterrender", this.onReady, this);
				this.tab = tab;
				this.complexPanel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height : labelHeigh,
							items : [{
										layout : "fit",
										region : 'center',
										items : this.tab
									}, this.getAssistant()]
						});
				this.complexPanel.on("bodyresize", this.onBodyResize, this);
				return this.complexPanel;
			},
			onBodyResize : function(panel, width, height) {
				if (this.panel.getResizeEl()) {
					this.panel.setHeight(height - 25);
					// alert(this.list.grid.getHeight());
				}
				var tab = this.quickInput.tab.getActiveTab();
				if (tab) {
					var m = this.quickInput.midiModules[tab.id];
					m.grid.setHeight(height - 50);
				}
			},
			onReady : function() {
				this.form.getForm().findField("KSDM").getStore().on("load",
						function() {
							this.form.getForm().findField("KSDM")
									.setValue(this.mainApp['phis'].departmentId);
						}, this);
				this.form.getForm().findField("YSDM").getStore().on("load",
						function() {
							this.form.getForm().findField("YSDM")
									.setValue(this.mainApp.uid);
						}, this);
				this.on("winShow", this.onWinShow, this);
				/** modified by gaof 2013-9-26 根据系统参数限制每张处方明细条数 */
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "querySystemArgumentCFMXSL"
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
				} else {
					this.CFXYZYMXSL = r.json.CFXYZYMXSL;
					this.CFCYMXSL = r.json.CFCYMXSL;
					this.list.CFXYZYMXSL = r.json.CFXYZYMXSL;
					this.list.CFCYMXSL = r.json.CFCYMXSL;
				}
			},
			beforeTabChange : function(tabPanel, newTab, curTab) {
				this.unreload = true;
				this.beforeclose(tabPanel, newTab, curTab);
				this.unreload = false;
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
					this.exList.loadData();
					if (newTab.fphm && newTab.zfpb == 1) {
						this.setChargeInfo(true, 'cancelled.gif');
					} else {
						this.setChargeInfo(newTab.fphm ? true : false);
					}
					this.doLoadCF02(newTab.cfsb, newTab.fphm)
				} else {
					this.doNew();
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
				if (this.firstLoad) {
					this.firstLoad = false;
					return;
				}
				this.hasDeleted = false;
				for (var i = 0; i < this.tab.items.length; i++) {
					var tab = this.tab.getItem(i);
					if (tab.cfsb) {
						delete tab.cfsb;
					}
				}
				// try {
				// this.tab.removeAll(false);
				// } catch (e) {
				//
				// }
				this.hasDeleted = true;
				var cf01s = this.getTab();
				var tabItems = [];
				if (cf01s == null || cf01s.length == 0) {
					try {
						this.tab.removeAll(false);
					} catch (e) {

					}
					tabItems.push({
								cfsb : "",
								title : "*新处方(1)"
							});
					// 新处方设置处方类型
					this.exContext.clinicType = 1;
					this.exContext.pharmacyId = this.getPharmacyIdByCFLX(1);
					this.tab.add(tabItems);
					this.tab.setActiveTab(0);
					return;
				} else {
					this.tab.setActiveTab(0);
					for (var i = this.tab.items.length - 1; i > 0; i--) {
						var tab = this.tab.getItem(i);
						// if (tab.cfsb) {
						// delete tab.cfsb;
						// }
						this.tab.remove(tab);
					}
					for (var i = 0; i < cf01s.length; i++) {
						var cf01 = cf01s[i];
						if (cf01.CFLX == 1) {
							name = "西药方"
						} else if (cf01.CFLX == 2) {
							name = "中药方"
						} else if ((cf01.CFLX == 3)) {
							name = "草药方"
						}
						if (i == 0) {
							var tab = this.tab.getItem(0);
							tab.cfsb = cf01.CFSB;
							tab.fphm = cf01.FPHM;
							tab.zfpb = cf01.ZFPB;
							tab.title = name + cf01.CFHM;
							continue;
						}
						tabItems.push({
									cfsb : cf01.CFSB,
									fphm : cf01.FPHM,
									zfpb : cf01.ZFPB,
									title : name + cf01.CFHM
								});
					}
					this.tab.add(tabItems);
					this.onTabChange(this.tab, this.tab.getItem(0), this.tab
									.getItem(0));
					// this.tab.doLayout();
				}
			},
			getForm : function() {
				var module = this.midiModules['prescriptionEntryForm'];
				if (!module) {
					module = this.createModule("prescriptionEntryForm",
							this.clinicPrescriptionEntryForm);
					if (this.exContext.systemParams.HQFYYF == '1') {
						module.colCount = 4;
					}
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
								if (!(arguments[0] == 1)) { // whether
									// set
									// defaultValue, it
									// will be setted
									// when there is no
									// args.
									var dv = it.defaultValue;
									if (dv) {
										if ((it.type == 'date' || it.xtype == 'datefield')
												&& typeof dv == 'string'
												&& dv.length > 10) {
											dv = dv.substring(0, 10);
										}
										f.setValue(dv);
									} else {
										f.setValue("");
									}
								}
								if (!it.update && !it.fixed && !it.evalOnServer) {
									f.enable();
								}
								f.validate();
								// @@ 2010-01-07 modified by chinnsii,
								// changed
								// the
								// condition
								// "it.update" to "!=false"
								if (it.type == "date") { // ** add by
									// yzh
									// 20100919 **
									if (it.minValue)
										f.setMinValue(it.minValue)
									if (it.maxValue)
										f.setMaxValue(it.maxValue)
								}
								// add by yangl 2012-06-29
								if (it.dic && it.dic.defaultIndex) {
									if (f.store.getCount() == 0)
										continue;
									if (isNaN(it.dic.defaultIndex)
											|| f.store.getCount() <= it.dic.defaultIndex)
										it.dic.defaultIndex = 0;
									f.setValue(f.store
											.getAt(it.dic.defaultIndex)
											.get('key'));
								}
							}
						}
						form.findField("KFRQ").setValue(Date
								.getServerDateTime());

					}
					var form = module.initPanel();
					this.form = form;
				}
				var CFLX = form.getForm().findField("CFLX");
				var CFTS = form.getForm().findField("CFTS");
				// CFHM.un("specialkey",module.onFieldSpecialkey,module);
				// CFHM.on("change", this.doSpecialkey, this);
				CFTS.on("change", this.onCftsChange, this);
				CFTS.on("focus", function() {
							this.selectText();
						}, CFTS);
				CFLX.on("select", this.onClinicSelect, this);
				CFLX.on("beforeselect", this.onbeforeClinicSelect, this);
				CFTS.hide();
				// 增加药房切换功能 add by yangl 13.10.10
				var YFSB = form.getForm().findField("YFSB");
				if (this.exContext.systemParams.HQFYYF !== '1') {
					form.remove(YFSB);
				} else {
					var _ctx = this;
					YFSB.on("select", this.onPharmacySelect, this);
					YFSB.on("beforeselect", this.onbeforePharmacySelect, this);
					YFSB.store.on('load', function() {
								if (!this.ysfbFirstLoad) {
									this.ysfbFirstLoad = true;
									if (this.getCount() == 0) {
										_ctx.changeClinicType(parseInt(CFLX
												.getValue()));
									}
									if (this.getCount() >= 1) {
										YFSB.setValue(this.getAt(0).get('key'))
									}
								}
							});
				}
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
					// list.getColumnModel().getIndexById('YPZS'),
					// true);
					list.onEditorKey = function(field, e) {
						if (field.needFocus) {
							field.needFocus = false;
							ed = this.activeEditor;
							if (!ed) {
								ed = this.lastActiveEditor;
							}
							this.startEditing(ed.row, ed.col);
							return;
						}
						if (e.getKey() == e.ENTER && !e.shiftKey) {
							var sm = this.getSelectionModel();
							var cell = sm.getSelectedCell();
							var count = this.colModel.getColumnCount()
							// 判断是否项目
							if (cell[1] + 1 >= 11 && !this.editing) {
								this.fireEvent("doNewColumn");
								return;
							}
						}
						this.selModel.onEditorKey(field, e);
					}
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
							if (field.notNull && !field.value) {
								g.startEditing(ed.row, ed.col);
								return;
							}
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
				// module.on("click", this.loadFJdata, this);
				var gytj = module.grid.getColumnModel().getColumnById("GYTJ").editor;
				gytj.on("select", this.list.ypyfSelect, this.list);
				// 删除处方
				var _ctx = this;
				this.list.doDelClinic = function() {
					if (this.store.getCount() > 0) {
						var record = this.store.getAt(0);
						var sfjg = record.get('SFJG');
						if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
							MyMessageTip.msg("提示", '该处方已审核，不能编辑!', true, 5);
							return;
						}
					}
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
								this.removedSBXH = [];
								this.list.clear();
								// 删除处方信息
								// if (!this.beforeclose())
								// return;
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
												try {
													_ctx.tab.remove(curTab);
												} catch (e) {

												}
												_ctx.fireEvent("doSave", "3");
												if (_ctx.tab.items.length == 0) {
													this.list.store.removeAll();
													this.tabIndex = 1;
													var tabItem = {
														title : "*新处方("
																+ (this.tabIndex)
																+ ")",
														items : this.panel
													}
													var newTab = _ctx.tab
															.add(tabItem);
													_ctx.tab
															.setActiveTab(newTab);
												}
											}, this)
								} else {
									try {
										_ctx.tab.remove(curTab);
									} catch (e) {

									}
								}
								if (_ctx.tab.items.length == 0) {
									this.list.store.removeAll();
									if (this.exList) {
										this.exList.store.removeAll();
									}
									this.tabIndex = 1;
									var tabItem = {
										title : "*新处方(" + (this.tabIndex) + ")",
										items : this.panel
									}
									var newTab = _ctx.tab.add(tabItem);
									_ctx.tab.setActiveTab(newTab);
								}
							}
						},
						scope : _ctx
					})
				}
				return list;
			},
			getAssistant : function() {
				this.quickInput = this.midiModules['prescriptionEntryQuickInput'];
				if (!this.quickInput) {
					var module = this.createModule(
							"prescriptionEntryQuickInput",
							this.clinicPrescriptionQuickInputTab);
					module.on("afterTabChange", this.afterQuickInputTabChange,
							this);
					this.quickInput = module;
				}
				this.quickInput.exContext = this.exContext;
				this.quickInput.on("quickInput", this.quickInputClinic, this);
				var quickInputPanel = new Ext.Panel({
							layout : "fit",
							border : false,
							// split : true,
							region : 'east',
							width : 250,
							collapsible : true,
							collapsed : true,
							bufferResize : 200,
							animCollapse : false,
							titleCollapse : true,
							floatable : false,
							items : this.quickInput.initPanel()
						});
				quickInputPanel.on("expand", this.onQuickInputExpend, this);
				this.quickInputPanel = quickInputPanel;
				return quickInputPanel;
			},
			afterQuickInputTabChange : function(module) {
				var height = this.complexPanel.getHeight();
				module.grid.setHeight(height - 50);
			},
			quickInputClinic : function(tabId, record) {
				var serviceAction = "loadMedcineInfo";
				var body = {};
				if (tabId == "clinicPersonalSet") {
					serviceAction = "loadPersonalSet"
					body.ZTBH = record.get("ZTBH");
				} else {
					body.YPXH = record.get("YPXH");
					body.YPMC = record.get("YPMC");
					body.JLBH = record.get("JLBH");
					body.tabId = tabId
				}
				body.clinicType = this.exContext.clinicType;
				body.pharmacyId = this
						.getPharmacyIdByCFLX(this.exContext.clinicType);
				body.BRXZ = this.exContext.empiData.BRXZ;
				body.FYGB = record.get("ZBLB");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : serviceAction,
							body : body
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							json.body.FYGB = body.FYGB;
							// 组套,调用组套调入功能
							var o = this.list
									.getStoreFields(this.list.schema.items)
							var store = this.list.grid.getStore();
							var Record = Ext.data.Record.create(o.fields)
							var sypc = this.list.grid.getColumnModel()
									.getColumnById("YPYF").editor;
							var gytj = this.list.grid.getColumnModel()
									.getColumnById("GYTJ").editor;
							if (tabId == 'clinicPersonalSet') {
								this.list.removeEmptyRecord();// 清空无效记录
								var ypzh = 1;
								var lastYpzh = -1;
								if (store.getCount() > 0) {
									ypzh = parseInt(store.getAt(store
											.getCount()
											- 1).get("YPZH_SHOW"))
											+ 1;
								}
								this.ztAddR(0, json.body.length, json, Record,
										sypc, gytj, lastYpzh, ypzh)
								// for (var i = 0; i <
								// json.body.length; i++) {
								// var sign = 0;
								// var medInfo = json.body[i];
								// if (medInfo.errorMsg) {
								// MyMessageTip.msg("提示", '药品【'
								// + medInfo.YPMC + '】'
								// + medInfo.errorMsg,
								// true);
								// continue;
								// }
								// /**
								// * 组套中需要皮试的药品进行提示!
								// */
								// if (medInfo.PSPB == 1) {
								// MyMessageTip.msg("提示", '药品【'
								// + medInfo.YPMC
								// + '】为皮试药品!', false);
								// }
								//
								// var sypc_r =
								// sypc.findRecord("key",
								// medInfo.SYPC);
								// var gytj_r =
								// gytj.findRecord("key",
								// medInfo.GYTJ);
								// if (!sypc_r) {
								// MyMessageTip.msg("提示", '药品【'
								// + medInfo.YPMC
								// + '】的频次信息错误，终止调入!',
								// true);
								// continue;
								// }
								// if (medInfo.YPZH != lastYpzh)
								// {
								// if (lastYpzh != -1) {
								// ypzh++;
								// }
								// lastYpzh = medInfo.YPZH;
								// medInfo.YPZH_SHOW = ypzh;
								// sign = 1;
								// } else {
								// medInfo.YPZH_SHOW = ypzh;
								// }
								// delete medInfo.YPZH;
								// var r = new Record(medInfo);
								// if
								// (!this.list.checkDoctorPermission(r,
								// r))
								// continue;
								// r.set("YPYF", medInfo.SYPC);
								// r.set("YPYF_text",
								// medInfo.SYPC_text);
								// r.set("FYGB", body.FYGB);
								// // r.set("ZSSF",
								// medInfo.ZSSF);
								// this.setMedQuantity(r);
								// store.add(r);
								// if (sign) {
								// this.list.ypyfSelect(gytj,
								// gytj_r, -1,
								// r);// 附加项目
								// }
								// }
							} else {// 常用药和全部
								// 1.判断当前是否有选中行，若没有，跳过2判断
								// 2.Grid当前选中行是否是新数据（没有调入过药品信息）
								// 3.如果是新数据，直接插入，否则按是否自动新组功能插入一条新记录
								if (json.body.errorMsg) {
									MyMessageTip.msg("提示", '药品【'
													+ json.body.YPMC + '】'
													+ json.body.errorMsg, true);
									return;
								}
								var r = this.list.getSelectedRecord();
								if (r
										&& (r.get("YPXH") == null
												|| r.get("YPXH") == "" || r
												.get("YPXH") == 0)) {
									if (this.list.setMedRecordIntoList(
											json.body, r)) {
										this.list.getPayProportion(r.data, r);
										this.list.grid.startEditing(
												this.list.grid
														.getSelectionModel()
														.getSelectedCell()[0],
												4);
									}
								} else {
									this.list.doInsertAfter(null, null, false,
											true);
									r = this.list.getSelectedRecord();
									if (!r)
										return;
									json.body.YPZH_SHOW = r.get("YPZH_SHOW");
									if (this.list.setMedRecordIntoList(
											json.body, r)) {
										this.list.getPayProportion(r.data, r);
										this.list.grid.startEditing(
												this.list.grid
														.getSelectionModel()
														.getSelectedCell()[0],
												4);
									}
									// this.setMedQuantity(r);
								}
								if (tabId == 'clinicCommon') {
									r.set("YYTS", json.body.YYTS);
									if (r.get("YPYF") == null
											|| r.get("YPYF") == ""
											|| r.get("YPYF") == 0) {
										r.set("YPYF", json.body.SYPC);
										r.set("MRCS", json.body.MRCS);
										r.set("YPYF_text", json.body.SYPC_text);
									}
								}
								if (r.get("GYTJ") == null
										|| r.get("GYTJ") == ""
										|| r.get("GYTJ") == 0) {
									if (tabId == 'clinicAll') {
										r.set("GYTJ", json.body.GYFF);
										r.set("GYTJ_text", json.body.GYFF_text);
									} else {
										r.set("GYTJ", json.body.GYTJ);
										r.set("GYTJ_text", json.body.GYTJ_text);
									}
									gytj_r = gytj.findRecord("key", r
													.get("GYTJ"));
									this.list.ypyfSelect(gytj, gytj_r, -1, r);// 附加项目
								}
								// r.set("YPJL", r.get("YCJL"));
								r.set("FYGB", body.FYGB);
								this.setMedQuantity(r);
							}
						}, this);

			},
			ztAddR : function(i, longth, json_f, Record, sypc, gytj, lastYpzh,
					ypzh) {
				if (i >= longth)
					return;
				var sign = 0;
				var medInfo = json_f.body[i];
				if (medInfo.errorMsg) {
					MyMessageTip.msg("提示", '药品【' + medInfo.YPMC + '】'
									+ medInfo.errorMsg, true);
					this.ztAddR(++i, longth, json_f, Record, sypc, gytj,
							lastYpzh, ypzh);
					return;
					// continue;
				}
				/**
				 * 组套中需要皮试的药品进行提示!
				 */
				this.ispi = false;
				var sypc_r = sypc.findRecord("key", medInfo.SYPC);
				var gytj_r = gytj.findRecord("key", medInfo.GYTJ);
				if (!sypc_r) {
					MyMessageTip.msg("提示", '药品【' + medInfo.YPMC
									+ '】的频次信息错误，终止调入!', true);
					this.ztAddR(++i, longth, json_f, Record, sypc, gytj,
							lastYpzh, ypzh);
					return;
				}
				if (medInfo.YPZH != lastYpzh) {
					if (lastYpzh != -1) {
						ypzh++;
					}
					lastYpzh = medInfo.YPZH;
					medInfo.YPZH_SHOW = ypzh;
					sign = 1;
				} else {
					medInfo.YPZH_SHOW = ypzh;
				}
				delete medInfo.YPZH;
				var r = new Record(medInfo);
				if (medInfo.PSPB == 1) {
					this.ispi = true;
					// Ext.Msg.show({
					// title : '提示',
					// msg : '药品【' + medInfo.YPMC
					// + '】为皮试药品，是否要进行皮试?',
					// modal : true,
					// width : 300,
					// buttons : Ext.MessageBox.YESNO,
					// multiline : false,
					// fn : function(btn, text) {
					// if (btn == "yes") {
					// // 插入皮试信息
					// var data = {};
					// data.YPXH = medInfo.YPXH;
					// data.BRID = this.exContext.ids.brid;
					// this.saveSkinTest(data);
					// } else {
					// r.set("PSPB",0);
					// }
					// this.ztAddR(++i,longth,json,Record,sypc,gytj,lastYpzh,ypzh);
					// },
					// scope : this
					// })

					/** ******************* */
					var psjg = 0;
					var _ctx = this;
					var data = {};
					data.YPXH = medInfo.YPXH;
					data.BRID = this.exContext.ids.brid;
					var brxm = this.exContext.empiData.personName
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : "getSkinTest",
								body : data
							}, function(code, msg, json) {
								if (code >= 300) {
									this.processReturnMsg(code, msg);
									return;
								}
								if (json.body) {
									psjg = json.body.PSJG;
								}
								// this.grid.stopEditing();
								if (psjg == 1) {
									Ext.Msg.show({
												title : '提示',
												msg : "病人" + brxm + '对药品【'
														+ medInfo.YPMC + '】过敏!',
												modal : true,
												width : 300,
												buttons : {
													yes : '不使用',
													no : '使用',
													cancel : '再做皮试'
												},
												multiline : false,
												fn : function(btn, text) {
													if (btn == "yes") {
														// 删除当前记录
														this.list.grid
																.getStore()
																.remove(r)
													} else if (btn == "no") {
														r.set("PSJG", 1);
														// var cell =
														// this.list.grid
														// .getSelectionModel()
														// .getSelectedCell();
														// this.list.grid.startEditing(cell[0],
														// 4);
													} else {
														// 插入皮试信息
														this.saveSkinTest(data);
														// var cell =
														// this.list.grid
														// .getSelectionModel()
														// .getSelectedCell();
														// this.list.grid.startEditing(cell[0],
														// 4);
													}
													this.ztAddR(++i, longth,
															json_f, Record,
															sypc, gytj,
															lastYpzh, ypzh);
												},
												scope : _ctx
											})
								} else {
									Ext.Msg.show({
												title : '提示',
												msg : '药品【' + medInfo.YPMC
														+ '】为皮试药品，是否要进行皮试?',
												modal : true,
												width : 300,
												buttons : Ext.MessageBox.YESNO,
												multiline : false,
												fn : function(btn, text) {
													if (btn == "yes") {
														// 插入皮试信息
														this.saveSkinTest(data);
													} else {
														r.set("PSPB", 0);
													}
													// var cell = this.list.grid
													// .getSelectionModel()
													// .getSelectedCell();
													// this.list.grid.startEditing(cell[0],
													// 4);
													this.ztAddR(++i, longth,
															json_f, Record,
															sypc, gytj,
															lastYpzh, ypzh);
												},
												scope : this
											})
								}
							}, this);
					/** ******************** */
				}
				if (!this.list.checkDoctorPermission(r, r)) {
					this.ztAddR(++i, longth, json_f, Record, sypc, gytj,
							lastYpzh, ypzh);
					return;
				}
				r.set("YPYF", medInfo.SYPC);
				r.set("YPYF_text", medInfo.SYPC_text);
				r.set("FYGB", medInfo.FYGB);
				// r.set("ZSSF", medInfo.ZSSF);
				this.setMedQuantity(r);
				this.list.grid.getStore().add(r);
				if (sign) {
					this.list.ypyfSelect(gytj, gytj_r, -1, r);// 附加项目
				}
				if (!this.ispi) {
					this.ztAddR(++i, longth, json_f, Record, sypc, gytj,
							lastYpzh, ypzh);
				}
			},
			saveSkinTest : function(data) {
				phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "saveSkinTest",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
						}, this);
			},
			changeWindow : function(cflx) {
				if (cflx == 3) {
					this.list.grid.getColumnModel().setColumnHeader(8, "每帖数量");
					this.form.getForm().findField("CFTS").show();
					// this.list.grid.getColumnModel().setHidden(
					// this.list.grid.getColumnModel()
					// .getIndexById('YPZS'), true);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YCJL'), true);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YYTS'), true);
				} else {
					this.list.grid.getColumnModel().setColumnHeader(8, "总量");
					this.form.getForm().findField("CFTS").setValue(1);
					this.form.getForm().findField("CFTS").hide();
					// this.list.grid.getColumnModel().setHidden(
					// this.list.grid.getColumnModel()
					// .getIndexById('YPZS'), true);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YYTS'), false);
					this.list.grid.getColumnModel().setHidden(
							this.list.grid.getColumnModel()
									.getIndexById('YCJL'), false);
				}
			},
			onCftsChange : function(f, value) {
				if (f.validate()) {
					for (var i = 0; i < this.list.store.getCount(); i++) {
						var r = this.list.store.getAt(i);
						// this.setMedQuantity(r);
						var YPSL = r.get("YPSL");
						var YYZL = YPSL * value;
						this.checkInventory(YYZL, r);
					}
					this.setCountInfo();
				}
			},
			onQuickInputExpend : function() {
				var tab = this.quickInput.tab.getActiveTab();
				if (!tab) {
					this.quickInput.tab.setActiveTab(0);
				}
			},
			listLoadData : function() {
				if (this.list.store.getCount() > 0) {
					this.setCountInfo();
					var count = this.list.store.getCount();
					var yzzh = 0;
					var lastYZZH = -1;
					for (var i = 0; i < count; i++) {
						var now_yzzh = this.list.store.getAt(i).get("YPZH");
						if (now_yzzh != lastYZZH) {
							yzzh++;
							this.list.store.getAt(i).set("YPZH_SHOW", yzzh);
							lastYZZH = now_yzzh;
						} else {
							this.list.store.getAt(i).set("YPZH_SHOW", yzzh);
						}
					}
					this.list.store.commitChanges();
					// 当数据载入后，默认插入一行
					// 默认选中第一行
					this.list.grid.getSelectionModel().select(0, 2);
					this.list.onRowClick();
					this.onTabShow();
				} else {
					this.list.doNewGroup();
				}
				// this.list.doNewGroup();
			},
			beforeGridEdit : function(it, record, field, v) {
				var tab = this.tab.getActiveTab();
				var sfjg = record.get('SFJG');
				if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
					return false;
				}
				if (tab.fphm) {
					return false;
				}
				if (!record.get("YPXH") && record.get("YPXH") != 0
						&& it.id != 'YPMC') {
					return false;
				}
				return true;
			},
			afterGridEdit : function(it, record, field, v) {
				var sign = 1;
				if (it.id == "YPYF") {
					field.getStore().each(function(r) {
								if (r.data.key == v) {
									if (record.get("MRCS") != r.data.MRCS) {
										record.set("MRCS", r.data.MRCS);
									} else {
										sign = 0;// 频次没有改变
									}
								}
							}, this);
				}
				if (it.id == "YCJL" || it.id == "YYTS" || it.id == "YPYF") {// 根据剂量、频次和天数计算总量
					if (this.exContext.systemParams.QYKJYWGL == 1
							&& record.get("KSBZ") == 1 && it.id == "YYTS") {// 启用抗菌药物管理时，抗菌药物限制最大天数
						var kjywts = this.exContext.systemParams.KJYSYTS || 3;
						if (isNaN(kjywts)) {
							MyMessageTip.msg("提示",
									"参数KJYSYTS(抗菌药物使用天数)设置错误，系统默认为3天", true);
							kjywts = 3;
						}
						if (v > kjywts) {
							MyMessageTip.msg("提示", "抗菌药物使用天数不能超过" + kjywts
											+ "天!", true);
							record.set("YYTS", kjywts);
							this.setMedQuantity(record);
							return;
						}
					}
					if (sign == 1 && this.cflx != 3) { // 添加this.cflx!=3
						// ，防止处方类型为草药的时候，每贴数量随着剂量和频次的变化而变化
						this.setMedQuantity(record);
					}

				}
				if (it.id == 'YPYF' || it.id == 'GYTJ' || it.id == 'YYTS'
						|| it.id == 'YPZS') {
					if (it.id == 'GYTJ' && this.cflx == 3) {// 草药方同组用法不用一致
					} else {
						var store = this.list.grid.getStore();
						// if (it.id == "GYTJ") {
						// this.doGytjChange(record, v);
						// }
						store.each(function(r) {
									if (r.get('YPZH_SHOW') == record
											.get('YPZH_SHOW')) {
										if (r.get("YPXH") != record.get("YPXH")) {
											r.set(it.id, v);
											r.set(it.id + '_text', record
															.get(it.id
																	+ '_text'));
											if (it.id == 'YPYF') {
												r.set("MRCS", record
																.get("MRCS"));
											}
											if ((it.id == 'YPYF' || it.id == 'YYTS')
													&& sign == 1
													&& this.cflx != 3) {// 添加this.cflx!=3
												// ，防止处方类型为草药的时候，每贴数量随着剂量和频次的变化而变化
												this.setMedQuantity(r);
											}
										}
									}
								}, this)
					}
				}
				if (it.id == "YPSL") {// 直接修改数量
					/**
					 * modified by gaof 2013-9-27 市医保病人贵重草药不能超固定克数，如9克、14克等
					 */
					if (record.get("TYPE") == 3) {
						var body = {};
						body["YPXH"] = record.get("YPXH");
						body["BRXZ"] = this.exContext.empiData.BRXZ;
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : "queryYCXL",
									body : body
								});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg);
						} else {
							// alert(r.json.YCXL)
							if (r.json.YCXL != 0 && v > r.json.YCXL) {
								MyMessageTip.msg("提示", '市医保病人贵重草药超过固定克数:'
												+ r.json.YCXL + ""
												+ record.get("YFDW"), true, 5);
							}
						}
					}

					var YYZL = v;
					if (this.form.getForm().findField("CFLX").getValue() == 3
							&& this.form.getForm().findField("CFTS")
									.isVisible()) {
						var CFTS = this.form.getForm().findField("CFTS")
								.getValue();
						if (CFTS > 0) {
							YYZL = YYZL * parseInt(CFTS);
						}
					}
					this.checkAntibioticsSum(YYZL, record);
					this.checkInventory(YYZL, record);
				}
				if (it.id == "YPMC") {
					if (v.substr(0, 1) == '*'
							|| (record.get("BZMC") && record.get("BZMC").length > 0)) {
						var ypmc = v.substr(0, 1) == '*' ? v.substr(1) : v;
						if (!record.get("BZMC")
								|| record.get("BZMC").length == 0) {
							record.set("YPCD", 0);
							record.set("YPSL", 0);
							record.set("YCJL", 0);
							record.set("YPDJ", 0);
							record.set("TYPE", 0);
							record.set("FYGB", 0);
							record.set("ZFBL", 0);
							record.set("YFBZ", 1);
							record.set("MRCS", 0);
							record.set("BZXX", "自备药");
						}
						record.set("YPMC", ypmc);
						record.set("BZMC", ypmc);
						record.set("YPXH", 0);
					}
				}
				if (this.exList && (it.id == 'YPYF' || it.id == 'YYTS')) {
					this.exList.store.each(function(ex_r) {
						var uniqueId = (record.get("SBXH") > 0)
								? "YPZH"
								: "uniqueId";
						if (ex_r.get(uniqueId)
								&& (ex_r.get(uniqueId) == record.get(uniqueId))) {
							// 计算附加项目数量和金额
							var ylsl_ys = ex_r.get("YLSL_YS");
							if (!ylsl_ys) {
								ylsl_ys = parseInt(ex_r.get("YLSL")
										/ (record.get("MRCS") * record
												.get("YYTS")))
							}
							ex_r.set("YLSL", record.get("MRCS")
											* record.get("YYTS")
											* (ylsl_ys || 1))
							ex_r.set("HJJE", ex_r.get("YLDJ")
											* ex_r.get("YLSL"))
						}
					});
				}

				/** modified by gaof 2013-9-27 市医保病人贵重草药不能超固定克数，如9克、14克等 */
				if (it.id == "YPYF" && record.get("TYPE") == 3) {
					var body = {};
					body["YPXH"] = record.get("YPXH");
					body["BRXZ"] = this.exContext.empiData.BRXZ;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "queryYCXL",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
					} else {
						if (r.json.YCXL != 0
								&& record.get("YPSL") > r.json.YCXL) {
							MyMessageTip.msg("提示", '市医保病人贵重草药超过固定克数:'
											+ r.json.YCXL + ""
											+ record.get("YFDW"), true, 5);
						}
					}
				}
			},
			setMedQuantity : function(record) {
				var YYTS = record.get("YYTS");
				var YPJL = record.get("YPJL");
				var YCJL = record.get("YCJL");
				var YPYF = record.get("YPYF");
				var MRCS = record.get("MRCS");
				var YFBZ = record.get("YFBZ");
				var CFTS = 1;
				var YYZL;
				if (this.form.getForm().findField("CFLX").getValue() == 3
						&& this.form.getForm().findField("CFTS").isVisible()) {
					// 草药
					CFTS = this.form.getForm().findField("CFTS").getValue();
					YYTS = 1;
				}
				if (YYTS && YCJL && MRCS) {
					if (!YCJL || !YPJL) {
						YYZL = 0;
					} else {
						if (!YFBZ) {
							YFBZ = 1;
						}
						if ((YCJL * 1000 % (YPJL * 1000 * YFBZ) != 0)) {// 乘数和被乘数都乘以1000，解决浮点数运算产生误差问题
							// YCJL = (Math.floor(YCJL / (YPJL * YFBZ))
							// + 1)
							// * ((YPJL * YFBZ)); //
							// 需要输液的药品，如果剂量不是规格的整数倍，则剂量向上取YPJL
							// // * YFBZ的倍数
							YCJL = this.accMul(Math.floor(this.accDiv(YCJL,
											this.accMul(YPJL, YFBZ)))
											+ 1, this.accMul(YPJL, YFBZ));
						}
						// YYZL = Math.ceil(YCJL / (YPJL * YFBZ) * MRCS
						// * YYTS);
						//
						if (this.form.getForm().findField("CFLX").getValue() == 3) {
							// YYZL = record.get("YPSL");
							YYZL = 1;
						} else {
							YYZL = Math.ceil(this.accDiv(YCJL, this.accMul(
											YPJL, YFBZ))
									* MRCS * YYTS);
						}
					}
					record.set("YPSL", YYZL);
					record.set("MTSL", YYZL);
					// this.list.summary.refreshSummary();
					// 发起请求到后台，校验输入的数量是否小于库存数量
					if (CFTS > 0) {
						YYZL = YYZL * parseInt(CFTS);
					}
					this.checkAntibioticsSum(YYZL, record);
					this.checkInventory(YYZL, record);
					// 提示库存信息不足，光标定位
				}
			},
			// 校验抗菌药物总量是否超过限量
			checkAntibioticsSum : function(YYZL, record) {
				if (record.get("YQSY") == 1) {// 抗菌药物越权使用时校验剂量
					var msg = record.get("msg");
					if (YYZL > record.get("YCYL")) {
						MyMessageTip.msg("提示", "越权使用的抗菌药物数量不能大于维护的一日限量!最大限量为："
										+ record.get("YCYL")
										+ record.get("YFDW"), true);
						if (!msg) {
							msg = {};
						}
						msg.error_yrxl = ("抗菌药物超过一日限量!");
						record.set("msg", msg);
					} else {
						if (msg && msg.error_yrxl) {
							msg.error_yrxl = null;
							record.set("msg", msg);
							// 以下操作为了更新
							var ypzh = record.get("YPZH");
							record.set("YPZH", 0)
							record.set("YPZH", ypzh)
						}
					}
				}
			},
			doLoadCF02 : function(value, cfhm) {
				// 查询处方号码是否存在，若存在，导入处方信息
				if (value) {
					// 若处方号码存在，根据处方状态设置按钮状态
					var peform = this.midiModules['prescriptionEntryForm'];
					if (peform) {
						peform.initDataId = value;
						peform.loadData();
					}
					this.list.requestData.cnd = ['eq', ['$', 'CFSB'],
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
			onbeforePharmacySelect : function(f, record, index) {
				var grid = this.list.grid;
				for (var i = 0; i < grid.getStore().getCount(); i++) {
					if (grid.getStore().getAt(i).get("YPXH")) {
						MyMessageTip.msg("警告", "当前处方已存在处方明细，不允许变更发药药房!", true);
						return false;
					}
				}
			},
			onbeforeClinicSelect : function(f, record, index) {
				var grid = this.list.grid;
				for (var i = 0; i < grid.getStore().getCount(); i++) {
					if (grid.getStore().getAt(i).get("YPXH")) {
						MyMessageTip.msg("警告", "当前处方已存在处方明细，不允许变更类型!", true);
						return false;
					}
				}
			},
			onPharmacySelect : function() {
				this.changeClinicType(parseInt(this.form.getForm()
						.findField("CFLX").getValue()))
			},
			onClinicSelect : function(f, record) {
				this.cflx = parseInt(f.value);
				if (this.exContext.systemParams.HQFYYF == '1') {
					this.reloadPharmacy(parseInt(f.value))
				} else {
					this.changeClinicType(parseInt(f.value))
				}
				this.changeWindow(parseInt(f.value));
				var grid = this.list.grid;
				grid.getStore().removeAll();
				this.list.doInsert();
			},
			checkInventory : function(YYZL, record) {
				if (YYZL == null || YYZL == 0)
					return;
				var type = this.form.getForm().findField("CFLX").value;
				var pharmId = this.getPharmacyIdByCFLX(type);
				var data = {};
				data.medId = record.get("YPXH");
				if (data.medId == null || data.medId == "") {
					return;
				}
				data.medsource = record.get("YPCD");
				data.quantity = YYZL;
				data.pharmId = pharmId;
				data.ypmc = record.get("YPMC");
				if (record.get("SBXH") != null && record.get("SBXH") != "") {
					data.jlxh = record.get("SBXH");
				}
				data.lsjg = record.get("YPDJ");
				// 校验是否有足够药品库存
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "checkInventory",
							body : data
						}, function(code, mmsg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, mmsg);
								return;
							}
							var msg = record.get("msg");
							if (json.sign > 0) {
								if (msg && msg.error_kc) {
									msg.error_kc = null;
									record.set("msg", msg);
									var ypzh = record.get("YPZH");
									record.set("YPZH", 0)
									record.set("YPZH", ypzh)
								}
								// 更新统计信息
								this.setCountInfo();
								return;
							}
							MyMessageTip.msg("警告", "药品【" + data.ypmc
											+ "】库存不足!库存数量：" + json.KCZL
											+ ",实际数量：" + data.quantity, true);

							if (!msg)
								msg = {};
							msg.error_kc = ("药品库存不足!");
							record.set("msg", msg);
							var ypzh = record.get("YPZH");
							record.set("YPZH", 0)
							record.set("YPZH", ypzh)// 刷新提示信息
						}, this)
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
					totalMoney += parseFloat(ypsl * ypdj * cfts);
					selfMoney += parseFloat(ypsl * ypdj * zfbl * cfts);
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
				return totalMoney;
			},
			doNew : function() {
				// if (this.exList) {
				// this.exList.doNew();
				// }

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
						this.form.getForm().findField("YSDM")
								.setValue(this.mainApp.uid);
						this.changeWindow(1);
						this.list.grid.getStore().removeAll();
						this.list.doInsert();
						this.setCountInfo();
						return;
					}
				}
				this.setCountInfo();
				var curTab = this.tab.getActiveTab();
				if (curTab.cfsb) {
					var tabItem = {
						title : "*新处方(" + (++this.tabIndex) + ")",
						items : this.panel
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
			/**
			 * 根据处方类型重新载入药房信息
			 */
			reloadPharmacy : function(clinicType) {
				// 如果启用了药房切换，更新药房下拉信息
				if (this.exContext.systemParams.HQFYYF == '1') {
					var cfqx = 'item.properties.XYQX';
					if (clinicType == 2) {
						cfqx = 'item.properties.ZYQX';
					} else if (clinicType == 3) {
						cfqx = 'item.properties.CYQX';
					}
					var yfsb = this.form.getForm().findField("YFSB");
					yfsb.setValue("");
					yfsb.store.removeAll();
					yfsb.store.ysfbFirstLoad = false;
					// ysdm.clearValue();
					var filters = "['and',['eq',['$','"
							+ cfqx
							+ "'],['s','1']],['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]]";
					yfsb.store.proxy = new Ext.data.HttpProxy({
								method : "GET",
								url : util.dictionary.SimpleDicFactory.getUrl({
											id : "phis.dictionary.pharmacy",
											filter : filters
										})
							})
					// ysdm.setValue(json.firstYs);
					yfsb.store.load();

				}
			},
			changeClinicType : function(clinicType) {
				if (!this.list.remoteDicStore.baseParams) {
					this.list.remoteDicStore.baseParams = {};
				}
				this.exContext.clinicType = clinicType;
				this.exContext.pharmacyId = this
						.getPharmacyIdByCFLX(clinicType);
				this.list.remoteDic.lastQuery = "";
				this.list.remoteDicStore.baseParams.type = clinicType;
				this.list.remoteDicStore.baseParams.pharmacyId = this
						.getPharmacyIdByCFLX(clinicType);
				this.cflx = clinicType;
				// 更改助手中的数据
				var tab = this.quickInput.tab.getActiveTab();
				if (tab) {
					this.quickInput.refershModule(tab.id);
				}

			},
			doSave : function() {
				/** modified by gaof 2013-9-29 根据系统参数限制每张处方明细条数 */
				var type = 0;
				var store = this.list.store;
				if (store.getCount() > 0) {
					type = store.getAt(0).get("TYPE");
				}
				if (type == 1 || type == 2) {
					// 西药中药
					if (this.CFXYZYMXSL != 0) {
						if (store.getCount() > this.CFXYZYMXSL) {
							MyMessageTip.msg("提示,纸张容量限制", '西药中药处方不能超过'
											+ (this.CFXYZYMXSL) + '条药品!', true,
									5);
							return false;
						}
					}
				} else if (type == 3) {
					// 草药
					if (this.CFCYMXSL != 0) {
						if (store.getCount() > this.CFCYMXSL) {
							MyMessageTip
									.msg("提示,纸张容量限制", '草药处方不能超过'
													+ (this.CFCYMXSL) + '条药品!',
											true, 5);
							return false;
						}
					}
				}
				/** modified by gaof 2013-9-29 end */
				/**
				 * modified by gaof 2013-9-29 市医保病人草药超额提示:总价不能超过30块、规定病种50块
				 */
				if (type == 3) {
					var body = {};
					body["totalCount"] = this.setCountInfo();
					body["JZXH"] = this.exContext.ids.clinicId;
					body["BRXZ"] = this.exContext.empiData.BRXZ;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "isAllowedSave",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
					} else {
						if (r.json.isAllowed == 0) {
							MyMessageTip.msg("提示", '市医保病人草药方超过限额', true, 5);
							return false;
						}
					}
				}
				/** modified by gaof end */
				if (this.list.store.getCount() > 0) {
					var record = this.list.store.getAt(0);
					var sfjg = record.get('SFJG');
					if (sfjg && sfjg == 1) { // 审核通过的处方不能编辑
						MyMessageTip.msg("提示", '该处方已审核，不能编辑!', true, 5);
						return false;
					}
				}
				if (this.list.grid.activeEditor != null) {
					this.list.grid.activeEditor.completeEdit();
				}
				if (this.exList.grid.activeEditor != null) {
					this.exList.grid.activeEditor.completeEdit();
				}
				var data = {};
				var module = this.midiModules['prescriptionEntryForm'];
				var CFTS = module.form.getForm().findField("CFTS").getValue();
				if (CFTS < 1 || CFTS > 99) {
					MyMessageTip.msg("提示", "草药帖数数量应在1-99之间。", true);
					return false;
				}
				var formData = module.getFormData();
				if (this.exContext.systemParams.HQFYYF == '1') {
					if (!formData.YFSB) {
						MyMessageTip.msg("提示", "发药药房不能为空。", true);
						return false;
					}
				} else {
					var pharmacyId = this.exContext.pharmacyId;
					if (!pharmacyId) {
						formData.YFSB = this.getPharmacyIdByCFLX(1);
					} else {
						formData.YFSB = pharmacyId;
					}
				}
				formData.KFRQ = module.form.getForm().findField("KFRQ")
						.getRawValue();
				formData.JZXH = this.exContext.ids.clinicId;
				formData.BRID = this.exContext.ids.brid;
				formData.BRXM = this.exContext.empiData.personName;
				formData.DJLY = 1;
				if (!formData.KSDM) {
					MyMessageTip.msg("提示", "就诊科室不能为空!", true);
					return false;
				}
				if (!formData.YSDM) {
					MyMessageTip.msg("提示", "开单医生不能为空!", true);
					return false;
				}
				data.formData = formData;
				// 判断数据有效性
				this.list.removeEmptyRecord();
				var store = this.list.grid.getStore();
				var n = store.getCount()
				if (n == 0
						&& (!this.removedSBXH || this.removedSBXH.length < 1)) {
					MyMessageTip.msg("提示", "没有需要保存的处方数据。", true);
					return false;
				}
				var listData = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					// 重新保存处方明细需重审
					var sfjg = r.data['SFJG'];
					if (sfjg > 1) {
						r.set('SFJG', 0);// 重置状态未审
						r.set('SFGH', '');
						r.set('SFYJ', '');
					}
					var items = this.list.schema.items;
					var msg = "保存失败";
					if (r.get("BZMC") && r.get("BZMC").length > 0) {
						r.data['_opStatus'] = r.get("SBXH") > 0
								? 'update'
								: 'create';
						var legal = this.checkRecordData(i, r.data);
						if (!legal) {
							return;
						}
						listData.push(r.data);
						continue;
					}
					if (!r.get("YPXH")) {
						continue;
					}
					r.data['_opStatus'] = r.get("SBXH") > 0
							? 'update'
							: 'create';
					var legal = this.checkRecordData(i, r.data);
					if (!legal) {
						return;
					}
					//update by caijy at 2014.10.10 for  自备药
					r.data["ZFYP"]=r.get("ZFYP")?1:0;
					listData.push(r.data)
				}
				data.listData = listData;
				if (this.removedSBXH) {
					for (var j = 0; j < this.removedSBXH.length; j++) {
						data.listData.push(this.removedSBXH[j]);
					}
				}
				if (this.exList) {
					if (this.exList.grid.activeEditor != null) {
						this.exList.grid.activeEditor.completeEdit();
					}
					var exAdviceRecords = [];
					for (var i = 0; i < this.exList.totalStore.getCount(); i++) {
						var r = this.exList.totalStore.getAt(i);
						if ((r.get("YLXH") == null || r.get("YLXH") == "" || r
								.get("YLXH") == 0)) {
							continue;
						}
						if (!r.get("YLDJ") || r.get("YLDJ") <= 0) {
							MyMessageTip.msg("提示", "附加项目【" + r.get("FYMC")
											+ "】单价必须大于0!", true);
							return false;
						}
						if (r.get("YCSL") <= 0) {
							MyMessageTip.msg("提示", "附加项目【" + r.get("FYMC")
											+ "】数量不能小于等于0!", true);
							return false;
						}
						exAdviceRecords
								.push(this.exList.totalStore.getAt(i).data);
					}
				}
				data.fjxxData = exAdviceRecords;
				data.BRXZ = this.exContext.empiData.BRXZ
				data.JZXH = this.exContext.ids.clinicId
				// data.GDBZ = this.exContext.pdgdbz// 规定病种
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveClinicInfo",
							body : data
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				this.panel.el.unmask()
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return false;
				}
				// 更新处方tab
				this.removedSBXH = [];
				if (!formData.MZHM) {
					var tab = this.tab.getActiveTab();
					if (formData.CFLX == 1) {
						name = "西药方"
					} else if (formData.CFLX == 2) {
						name = "中药方"
					} else if ((formData.CFLX == 3)) {
						name = "草药方"
					}
					tab.setTitle(name + json.CFHM);
					tab.cfsb = json.CFSB;
				}
				if (!this.unreload) {
					this.exList.loadData();
					this.doLoadCF02(json.CFSB);
				} else {
					delete this.unreload;
				}
				MyMessageTip.msg("提示", "处方信息保存成功!", true);
				this.onTabShow();
				this.fireEvent("doSave", "3");
				if (this.needToClose) {
					this.needToClose = false;
					this.win.hide();
				}
				this.list.store.rejectChanges();
				return true;
			},
			// 检测待保存数据合法性
			checkRecordData : function(i, data) {
				var msg = data["msg"];
				if (msg) {
					var msg_str = "";
					if (msg.error_kc) {
						msg_str += msg.error_kc;
					}
					if (msg.error_yrxl) {
						msg_str += msg.error_yrxl;
					}
					if (msg_str.length > 0) {
						MyMessageTip.msg("提示", msg_str + "错误行 " + (i + 1)
										+ " 。", true);
						return false;
					}
				}
				if (data["BZMC"]
						&& data["BZMC"].replace(/[^\x00-\xff]/g, "__").length > 100) {
					MyMessageTip.msg("提示", "自备药名称输入过长！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["BZXX"]
						&& data["BZXX"].replace(/[^\x00-\xff]/g, "__").length > 200) {
					MyMessageTip.msg("提示", "备注信息输入过长！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["YCJL"] <= 0) {
					MyMessageTip.msg("提示", "药品剂量不能小于等于0！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["YPSL"] <= 0) {
					MyMessageTip.msg("提示", "药品总量不能小于等于0！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["YYTS"] <= 0) {
					MyMessageTip.msg("提示", "用药天数不能小于等于0！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (data["MRCS"] < 0 || !data["YPYF"]) {
					MyMessageTip.msg("提示", "用药频次不能为空！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				if (!data["GYTJ"]) {
					MyMessageTip.msg("提示", "药品用法不能为空！错误行 " + (i + 1) + " 。",
							true);
					return false;
				}
				// if (this.exContext.clinicType == 3) {// 草药
				// if (!data["YPZS"]) {
				// MyMessageTip.msg("提示", "服法不能为空！错误行 " + (i + 1) + "
				// 。",
				// true);
				// return false;
				// }
				// }
				return true;
			},
			beforeClose : function(){
				return this.beforeclose();
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
				if (this.list.store.getModifiedRecords().length > 0) {
					if (this.removedSBXH && this.removedSBXH.length > 0) {
						if (confirm('处方录入模块数据已经修改，是否保存?')) {
							// this.needToClose = true;
							return this.doSave()
						}
						this.removedSBXH = [];
						return true;
					}
					for (var i = 0; i < this.list.store.getCount(); i++) {
						if (this.list.store.getAt(i).get("YPXH")) {
							if (confirm('处方录入模块数据已经修改，是否保存?')) {
								// this.needToClose = true;
								return this.doSave()
							} else {
								break;
							}
						}
					}
					this.list.store.rejectChanges();
					if (this.exList) {
						this.exList.store.rejectChanges();
					}
				} else if (this.removedSBXH && this.removedSBXH.length > 0) {
					if (confirm('处方录入模块数据已经修改，是否保存?')) {
						// this.needToClose = true;
						return this.doSave()
					}
					this.removedSBXH = [];
				}
				if (this.exList.store.getModifiedRecords().length > 0) {
					if (confirm('处方录入模块数据已经修改，是否保存?')) {
						// this.needToClose = true;
						return this.doSave()
					}
				}
				return true;
			},
			// 附加项目
			getFJList : function() {
				this.exList = this.createModule("exList", this.refFjList);
				this.exList.exContext = this.exContext;
				this.exList.mainList = this.list;
				this.exList.opener = this;
				return this.exList.initPanel();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								maximized : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
			/**
			 * 处方拷贝
			 */
			perscriptionCopy : function(json, tabId) {
				// var body = {};
				// body.FYGB =
				// this.list.getFygb(this.exContext.clinicType);
				var o = this.list.getStoreFields(this.list.schema.items);
				var store = this.list.grid.getStore();
				var Record = Ext.data.Record.create(o.fields);
				var sypc = this.list.grid.getColumnModel()
						.getColumnById("YPYF").editor;
				var gytj = this.list.grid.getColumnModel()
						.getColumnById("GYTJ").editor;
				if (tabId == 'clinicPersonalSet') {
					this.list.removeEmptyRecord();// 清空无效记录
					var ypzh = 1;
					var lastYpzh = -1;
					if (store.getCount() > 0) {
						ypzh = parseInt(store.getAt(store.getCount() - 1)
								.get("YPZH_SHOW"))
								+ 1;
					}
					for (var i = 0; i < json.body.length; i++) {
						var sign = 0;
						var medInfo = json.body[i];
						if (medInfo.errorMsg) {
							MyMessageTip.msg("提示", '药品【' + medInfo.YPMC + '】'
											+ medInfo.errorMsg, true);
							continue;
						}
						var sypc_r = sypc.findRecord("key", medInfo.SYPC);
						var gytj_r = gytj.findRecord("key", medInfo.GYTJ);
						if (!sypc_r) {
							MyMessageTip.msg("提示", '药品【' + medInfo.YPMC
											+ '】的频次信息错误，终止调入!', true);
							continue;
						}
						if (medInfo.YPZH != lastYpzh) {
							if (lastYpzh != -1) {
								ypzh++;
							}
							lastYpzh = medInfo.YPZH;
							medInfo.YPZH_SHOW = ypzh;
							sign = 1;
						} else {
							medInfo.YPZH_SHOW = ypzh;
						}
						delete medInfo.YPZH;
						delete medInfo.SBXH;
						var r = new Record(medInfo);
						if (!this.list.checkDoctorPermission(r, r))
							continue;
						r.set("YPYF", medInfo.SYPC);
						r.set("YPYF_text", medInfo.SYPC_text);
						r.set("FYGB", medInfo.FYGB);
						this.setMedQuantity_PrescriptionCopy(r);
						store.add(r);
						if (sign) {
							this.list.ypyfSelect(gytj, gytj_r, -1, r);// 附加项目
						}
					}
				}
			},
			setMedQuantity_PrescriptionCopy : function(record) {
				var YYTS = record.get("YYTS");
				var YPJL = record.get("YPJL");
				var YCJL = record.get("YCJL");
				var YPYF = record.get("YPYF");
				var MRCS = record.get("MRCS");
				var YFBZ = record.get("YFBZ");
				var CFTS = 1;
				var YYZL;
				if (this.form.getForm().findField("CFLX").getValue() == 3
						&& this.form.getForm().findField("CFTS").isVisible()) {
					// 草药
					CFTS = this.form.getForm().findField("CFTS").getValue();
					YYTS = 1;
				}
				if (YYTS && YCJL && MRCS) {
					YYZL = record.get("YPSL");
					record.set("YPSL", YYZL);
					record.set("MTSL", YYZL);
					// this.list.summary.refreshSummary();
					// 发起请求到后台，校验输入的数量是否小于库存数量
					if (CFTS > 0) {
						YYZL = YYZL * parseInt(CFTS);
					}
					this.checkInventory(YYZL, record);
					// 提示库存信息不足，光标定位
				}
			},
			// 除法函数，用来得到精确的除法结果
			// 说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
			// 调用：accDiv(arg1,arg2)
			// 返回值：arg1除以arg2的精确结果
			accDiv : function(arg1, arg2) {
				var t1 = 0, t2 = 0, r1, r2;
				try {
					t1 = arg1.toString().split(".")[1].length
				} catch (e) {
				}
				try {
					t2 = arg2.toString().split(".")[1].length
				} catch (e) {
				}
				with (Math) {
					r1 = Number(arg1.toString().replace(".", ""))
					r2 = Number(arg2.toString().replace(".", ""))
					return (r1 / r2) * pow(10, t2 - t1);
				}
			},
			// 乘法函数，用来得到精确的乘法结果
			// 说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
			// 调用：accMul(arg1,arg2)
			// 返回值：arg1乘以arg2的精确结果
			accMul : function(arg1, arg2) {
				var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
				try {
					m += s1.split(".")[1].length
				} catch (e) {
				}
				try {
					m += s2.split(".")[1].length
				} catch (e) {
				}
				return Number(s1.replace(".", ""))
						* Number(s2.replace(".", "")) / Math.pow(10, m)
			}
		});