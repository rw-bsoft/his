$package("phis.application.fsb.script")

/**
 * 处方组套维护
 */
$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedClinicPlanNameList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.showRowNumber = true;
	this.serviceId = "familySickBedManageService"
	phis.application.fsb.script.FamilySickBedClinicPlanNameList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
	this.on("remove", this.onRemove, this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedClinicPlanNameList,
		phis.script.SimpleList, {
			openModule : function(cmd, r, xy) {
				phis.application.fsb.script.FamilySickBedClinicPlanNameList.superclass.openModule
						.call(this, cmd, r, xy)
				var module = this.midiModules[cmd];
				module.SSLB = this.SSLB;
				module.ZTLB = this.ZTLB;
			},
			onReady : function() {
				phis.application.fsb.script.FamilySickBedClinicPlanNameList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (String(index) == 'false')
					return;
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record.data.SFQY == 1) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">组套已启用 </div>'
								}, false);
					} else {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">组套已禁用 </div>'
								}, false);
					}
				}

			},
			onRenderer : function(value, metaData, r) {
				var SFQY = r.get("SFQY");
				var src = (SFQY == 1) ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
						+ "resources/phis/resources/images/" + src + ".png'/>";
			},
			doExecute : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r == null) {
					return
				}
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				data["ZTBH"] = r.get("ZTBH");
				if (r.get("SFQY") == "1") {
					data["SFQY"] = "0";
					this.grid.el.mask("正在取消启用...", "x-mask-loading")
				} else {
					data["SFQY"] = "1";
					this.grid.el.mask("正在启用...", "x-mask-loading")
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "updatePrescriptionStack",
							method : "execute",
							schemaList : "JC_ZL_ZT01",
							schemaDetailsList : "JC_ZL_ZT02",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
								var btns = this.grid.getTopToolbar();
								var btn = btns.find("cmd", "execute");
								btn = btn[0];
								if (r.data.SFQY == 0) {
									if (btn.getText().indexOf("禁用") > -1) {
										return;
									}
									btn.setText(btn.getText().replace("启用",
											"禁用"));
								} else {
									btn.setText(btn.getText().replace("禁用",
											"启用"));
								}
								this.refresh();
								if (code != 603) {
									this.loadData();
								}
								if (this.SSLB == 4) {
									this.setButtonsState(['add', 'update',
													'remove'], false);
								}
								return;
							}
						}, this)
				this.fireEvent("beforeclose", this);
			},
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.grid.getSelectionModel().selectRow(rowIndex)
				var cmenu = this.midiMenus['gridContextMenu']
				if (!cmenu) {
					var items = [];
					var actions = this.actions
					if (!actions) {
						return;
					}
					for (var i = 0; i < actions.length; i++) {
						var action = actions[i];
						var it = {}
						it.cmd = action.id
						it.ref = action.ref
						it.iconCls = action.iconCls || action.id
						it.script = action.script
						it.text = action.name
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['gridContextMenu'] = cmenu
				}

				var toolBar = this.grid.getTopToolbar();
				if (toolBar) {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (this.actions[i].id == "execute") {
							var btnResult = btn[0].getText().substring(0, 2);
							cmenu.items.itemAt(i).setText(btnResult);
						}
						if (!btn || btn.length == 0) {
							continue;
						}
						if (btn[0].disabled) {
							cmenu.items.itemAt(i).disable();
						} else {
							cmenu.items.itemAt(i).enable();
						}

					}
				}
				cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
			},
			onBeforeRemove : function(entryName, r) {
				var data = {
					"ZTBH" : r.json.ZTBH
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "removePrescriptionDetails",
							method : "execute",
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			// 单击时改变作废按钮
			onRowClick : function(grid,index,e) {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				this.selectedIndex = index
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "execute");
				btn = btn[0];
				if (r.data.SFQY == 1) {
					if (btn.getText().indexOf("禁用") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("启用", "禁用"));
				} else {
					btn.setText(btn.getText().replace("禁用", "启用"));
				}

			},
			// 刚打开页面时候默认选中数据,这时候判断下作废按钮
			onStoreLoadData : function(store, records, ops) {
				var index=0;
				if (this.selectedIndex && this.selectedIndex < records.length) {
					index=this.selectedIndex
				}
				this.fireEvent("loadData", store,index) // ** 不管是否有记录，都fire出该事件
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
			onRemove : function() {
				this.loadData();
			},
			onDblClick : function(grid, index, e) {

			}
		})
