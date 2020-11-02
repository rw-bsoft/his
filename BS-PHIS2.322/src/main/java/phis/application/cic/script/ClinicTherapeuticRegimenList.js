$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicTherapeuticRegimenList = function(cfg) {
	phis.application.cic.script.ClinicTherapeuticRegimenList.superclass.constructor
			.apply(this, [cfg]);
	this.on("remove", this.onRemove, this);
}

Ext.extend(phis.application.cic.script.ClinicTherapeuticRegimenList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.cic.script.ClinicTherapeuticRegimenList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (String(index) == 'false')
					return;
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record.data.QYBZ == 1) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">方案已启用 </div>'
								}, false);
					} else {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">方案已禁用 </div>'
								}, false);
					}
				}

			},
			onRenderer : function(value, metaData, r) {
				var QYBZ = r.get("QYBZ");
				var src = (QYBZ == 1) ? "yes" : "no";
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
				data["ZLXH"] = r.get("ZLXH");
				if (r.get("QYBZ") == "1") {
					data["QYBZ"] = "0";
					this.grid.el.mask("正在取消启用...", "x-mask-loading")
				} else {
					data["QYBZ"] = "1";
					this.grid.el.mask("正在启用...", "x-mask-loading")
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							schemaList : "GY_ZLFA",
							schemaDetailsList : "GY_ZLFA",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
								var btns = this.grid.getTopToolbar();
								var btn = btns.find("cmd", "execute");
								btn = btn[0];
								if (r.data.QYBZ == 0) {
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
								if (code != 607) {
									this.loadData();
								}
								return;
							}
						}, this)
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
				if (r.data.QYBZ == 1) {
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
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
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
			onRemove : function() {
				this.loadData();
			}
		});