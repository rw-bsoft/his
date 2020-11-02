$package("phis.application.cfg.script")

/**
 * 供货单位维护list gaof 2013.03.06
 */
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigSupplyUnitList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.showRowNumber = true;
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以
	cfg.width = 800;
	phis.application.cfg.script.ConfigSupplyUnitList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.ConfigSupplyUnitList, phis.script.SimpleList,
		{
			initPanel : function(sc) {
				if (!this.mainApp['phis'].treasuryId
						&& (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId)) {
					Ext.MessageBox.alert("提示", "您还没有选择库房， 请先选择库房 !");
					return;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0
						&& this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				return phis.application.cfg.script.ConfigSupplyUnitList.superclass.initPanel
						.apply(this, [sc]);
			},
			loadData : function() {
				this.clear();
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = "supplyUnitQuery";

				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
            // 刚打开页面时候默认选中第一条数据,这时候判断下作废按钮
            onStoreLoadData : function(store, records, ops) {
                this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
                if (records.length == 0) {
                    return
                }
                if (!this.selectedIndex || this.selectedIndex >= records.length) {
                    this.selectRow(0);
                    this.onRowClick();
                } else {
                    this.selectRow(this.selectedIndex);
                    this.selectedIndex = 0;
                    this.onRowClick();
                }
            },
            // 单击时改变作废按钮
            onRowClick : function() {
                var r = this.getSelectedRecord();
                if (r == null) {
                    return;
                }
                var btns = this.grid.getTopToolbar();
                var btn = btns.find("cmd", "execute");
                btn = btn[0];
                if (r.data.DWZT == -1) {
                    if (btn.getText().indexOf("取消") > -1) {
                        return;
                    }
                    btn.setText(btn.getText().replace("注销", "取消注销"));
                } else {
                    btn.setText(btn.getText().replace("取消注销", "注销"));
                }

            },
			doExecute : function() {
				var r = this.getSelectedRecord();
				var data = {};

				if (r == null) {
					MyMessageTip.msg("提示", '请选择需要注销的记录!', true);
					return
				}
				data["DWXH"] = r.get("DWXH");

				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "getJGID",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							} else {
								if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId
										&& json.jgid == this.mainApp.topUnitId) {
									MyMessageTip
											.msg("提示", '公共供货单位不允许注销!', true);
									return
								}

								var n = this.store.indexOf(r)
								if (n > -1) {
									this.selectedIndex = n
								}
								// 目前已经是注销状态，则提示是否取消注销
								if (-1 == r.get("DWZT")) {
									Ext.MessageBox.confirm('提示',
											'该单位已是注销状态，是否取消注销?', function(btn) {
												if (btn == "yes") {
													this.grid.el.mask(
															"正在取消注销...",
															"x-mask-loading")
													phis.script.rmi.jsonRequest(
															{
																serviceId : this.serviceId,
																serviceAction : "updateConfigSupplyUnitNormal",
																schemaList : "WL_GHDW",
																body : data
															},
															function(code, msg,
																	json) {
																this.grid.el
																		.unmask()
																if (code >= 300) {
																	MyMessageTip
																			.msg(
																					"提示",
																					msg,
																					true);
																} else {
																	MyMessageTip
																			.msg(
																					"提示",
																					"操作成功",
																					true);
																	this
																			.refresh();
																}
															}, this)
												}
											}, this);

								}

								// 目前处于正常状态，判断当前单位是否在用
								if (1 == r.get("DWZT")) {
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "isUnitUsed",
												body : data
											}, function(code, msg, json) {
												if (code >= 300) {
													MyMessageTip.msg("提示", msg,
															true);
												} else {
													// 如果不在用
													if (0 == json.countRK01
															&& 0 == json.countBHXX
															&& 0 == json.countCSZC) {
														// 提示是否删除
														Ext.MessageBox
																.confirm(
																		'提示',
																		'该供货单位不在用，是否删除?',
																		function(
																				btn) {
																			if (btn == "yes") {
																				// 如果选择是，则删除WL_GHDW中指定供货单位，并同时删除WL_ZJXX中zjdx=2，dxxh=指定供货单位的所有记录。
																				this.grid.el
																						.mask(
																								"正在删除...",
																								"x-mask-loading")
																				phis.script.rmi
																						.jsonRequest(
																								{
																									serviceId : this.serviceId,
																									serviceAction : "deleteConfigSupplyUnit",
																									schemaList : "WL_GHDW",
																									body : data
																								},
																								function(
																										code,
																										msg,
																										json) {
																									this.grid.el
																											.unmask()
																									if (code >= 300) {
																										MyMessageTip
																												.msg(
																														"提示",
																														msg,
																														true);
																									} else {
																										MyMessageTip
																												.msg(
																														"提示",
																														"操作成功",
																														true);
																										this
																												.refresh();
																									}
																								},
																								this)
																			} else {
																				// 否则提示是否注销
																				Ext.MessageBox
																						.confirm(
																								'提示',
																								'是否注销?',
																								function(
																										btn) {
																									if (btn == "yes") {
																										this.grid.el
																												.mask(
																														"正在注销...",
																														"x-mask-loading")
																										phis.script.rmi
																												.jsonRequest(
																														{
																															serviceId : this.serviceId,
																															serviceAction : "updateConfigSupplyUnitCancel",
																															schemaList : "WL_GHDW",
																															body : data
																														},
																														function(
																																code,
																																msg,
																																json) {
																															this.grid.el
																																	.unmask()
																															if (code >= 300) {
																																MyMessageTip
																																		.msg(
																																				"提示",
																																				msg,
																																				true);
																															} else {
																																MyMessageTip
																																		.msg(
																																				"提示",
																																				"操作成功",
																																				true);
																																this
																																		.refresh();
																															}
																														},
																														this)
																									}
																								},
																								this);
																			}
																		}, this);

													} else {
														// 如果在用,提示是否注销，如果选择是，则将WL_GHDW的指定供货单位的单位状态dwzt改为-1
														Ext.MessageBox.confirm(
																'提示', '是否注销?',
																function(btn) {
																	if (btn == "yes") {
																		this.grid.el
																				.mask(
																						"正在注销...",
																						"x-mask-loading")
																		phis.script.rmi
																				.jsonRequest(
																						{
																							serviceId : this.serviceId,
																							serviceAction : "updateConfigSupplyUnitCancel",
																							schemaList : "WL_GHDW",
																							body : data
																						},
																						function(
																								code,
																								msg,
																								json) {
																							this.grid.el
																									.unmask()
																							if (code >= 300) {
																								MyMessageTip
																										.msg(
																												"提示",
																												msg,
																												true);
																							} else {
																								MyMessageTip
																										.msg(
																												"提示",
																												"操作成功",
																												true);
																								this
																										.refresh();
																							}
																						},
																						this)
																	}
																}, this);

														// if (confirm('是否注销'))
														// {
														// this.grid.el.mask("正在注销...",
														// "x-mask-loading")
														// phis.script.rmi.jsonRequest({
														// serviceId :
														// this.serviceId,
														// serviceAction :
														// "updateConfigSupplyUnitCancel",
														// schemaList :
														// "WL_GHDW",
														// body : data
														// }, function(code,
														// msg, json) {
														// this.grid.el.unmask()
														// if (code >= 300) {
														// MyMessageTip.msg("提示",
														// msg,
														// true);
														// } else {
														// MyMessageTip.msg("提示",
														// "操作成功", true);
														// this.refresh();
														// }
														// }, this)
														// }
													}
												}
											}, this)

								}
							}
						}, this)

			},
			onEnterKey : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				Ext.EventObject.stopEvent()
				this.onDblClick(this.grid)
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			}
		})
