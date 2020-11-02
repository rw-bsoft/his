$package("phis.application.pha.script")

$import("phis.script.SimpleList","phis.application.mds.script.MySimplePagingToolbar")
phis.application.pha.script.PharmacyInventoryManagementList = function(cfg) {
	phis.application.pha.script.PharmacyInventoryManagementList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryManagementList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = {};
				phis.application.pha.script.PharmacyInventoryManagementList.superclass.loadData
						.call(this);
			},
			initPanel : function(sc) {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return null;
				}
				var grid = phis.application.pha.script.PharmacyInventoryManagementList.superclass.initPanel
						.call(this);
				return grid;
			},

			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "disable");
				btn = btn[0];
				if (r.data.JYBZ == 1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("禁用", "取消禁用"));
				} else {
					btn.setText(btn.getText().replace("取消禁用", "禁用"));
				}

			},
			// 上下时改变作废按钮
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},// 提交修改的数据
			doDisable : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var jybz = 0;
				if (r.data.JYBZ == 0) {
					jybz = 1;
				}
				var body = {};
				body["SBXH"] = r.data.SBXH;
				body["JYBZ"] = jybz;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.disableInventoryActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doDisable);
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "disable");
				btn = btn[0];
				if (r.data.JYBZ == 0) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("禁用", "取消禁用"));
				} else {
					btn.setText(btn.getText().replace("取消禁用", "禁用"));
				}
				this.refresh();
			},
			// 页面加载时判断库存是否已禁用,如禁用则加上图片
			onRenderer : function(value, metaData, r) {
				if (r.data.JYBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/(00,04).png'/>"
				}
				return "";
			},
			// 刚打开页面时候默认选中数据,这时候判断下禁用按钮
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件			
				//zhaojian 2017-08-23 药房库存管理 增加总计金额
				if (records.length == 0) {
					document.getElementById("YFKC_JE").innerHTML = "进货金额总计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;零售金额总计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;当前页零售金额总计:￥0.00";
					this.fireEvent("noRecord", this);
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
				
				//zhaojian 2017-08-23 药房库存管理 增加总计金额
				var store = this.grid.getStore();
				var JHHJ = 0;
				var LSJE = 0;
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (r.get("JHJE")) {
						JHHJ += r.get("JHJE");
					}
					if (r.get("LSJE")) {
						LSJE += r.get("LSJE");
					}
				}
				document.getElementById("YFKC_JE").innerHTML = "进货金额总计:￥"
						+ store.reader.jsonData.totalJhje
						+ "&nbsp;&nbsp;&nbsp;&nbsp;零售金额总计:￥"
						+ store.reader.jsonData.totalLsje
						+ "&nbsp;&nbsp;&nbsp;&nbsp;当前页零售金额总计:￥"
						+ LSJE.toFixed(4);
			},// 加上鼠标移动提示记录是否已禁用功能
			onReady : function() {
				phis.application.pha.script.PharmacyInventoryManagementList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			// 鼠标移动提示记录是否已作废功能
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.JYBZ == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '库存已禁用'
									}, false);

						}
					}
				}
			},
			// 右键 作废和取消作废相应改变
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.grid.getSelectionModel().selectRow(rowIndex)
				var r = this.getSelectedRecord();
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
						if (action.id == "disable") {
							if (r.data.JYBZ == 1) {
								it.text = "取消禁用";
							}
						}
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['gridContextMenu'] = cmenu
				} else {
					for (var i = 0; i < cmenu.items.length; i++) {
						if (cmenu.items.itemAt(i).cmd == "disable") {
							if (r.data.JYBZ == 1) {
								cmenu.items.itemAt(i).setText("取消禁用");
							} else {
								cmenu.items.itemAt(i).setText("禁用");
							}
						}
					}

				}
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
				var toolBar = this.grid.getTopToolbar();
				if (toolBar) {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
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
			}
			,			
		    //zhaojian 2017-08-23 药房库存管理 增加总计金额
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : this.pageSize || 25, // ** modify by yzh ,
					// 2010-06-18 **
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录",
					divHtml:"<div id='YFKC_JE' align='center' style='color:blue'>进货金额总计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;零售金额总计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;当前页零售金额总计:￥0.00</div>"
				}
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}
				var pagingToolbar = new phis.application.mds.script.MySimplePagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				this.pagingToolbar.on("beforePageChange",
						this.beforeStorechange);
				return pagingToolbar
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("YPXH"))
				}
//				var pWin = this.midiModules["InventoryDetailsPrintView"]
				var cfg = {
					requestData : ids
				}
//				if (pWin) {
//					Ext.apply(pWin, cfg)
//					pWin.getWin().show()
//					return
//				}
//				pWin = new phis.prints.script.InventoryDetailsPrintView(cfg)
//				this.midiModules["InventoryDetailsPrintView"] = pWin
//				pWin.getWin().show()
				
				//
				var module = this.createModule("InventoryDetailsPrintView", this.refInventoryDetailsPrint)
				module.requestData = ids;
//				module.initPanel();
				module.getWin().show();
			}
		})