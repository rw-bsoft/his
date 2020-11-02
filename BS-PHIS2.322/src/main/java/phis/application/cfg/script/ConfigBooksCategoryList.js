$package("phis.application.cfg.script")

/**
 * 账簿类别维护list shiwy 2013.11.05
 */
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigBooksCategoryList = function(cfg) {
	phis.application.cfg.script.ConfigBooksCategoryList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.cfg.script.ConfigBooksCategoryList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].treasuryEjkf != 0 && this.mainApp['phis'].treasuryEjkf
						&& this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				this.requestData.cnd = ['or',['eq',['$','JGID'],['s',this.mainApp.topUnitId]],['eq',['$','JGID'],['s',this.mainApp['phisApp'].deptId]]];
				if (this.grid) {
					if (!this.isCombined) {
						this.fireEvent("beforeAddToWin", this.grid)
						this.addPanelToWin();
					}
					return this.grid;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				this.isCompositeKey = schema.isCompositeKey;
				var items = schema.items
				if (!items) {
					return;
				}

				this.store = this.getStore(items)
				if (this.mutiSelect) {
					this.sm = new Ext.grid.CheckboxSelectionModel()
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var cfg = {
					stripeRows : true,
					border : false,
					store : this.store,
					cm : this.cm,
					sm : this.sm,
					height : this.height,
					loadMask : {
						msg : '正在加载数据...',
						msgCls : 'x-mask-loading'
					},
					buttonAlign : 'center',
					clicksToEdit : true,
					frame : true,
					plugins : this.rowExpander,// modife
					// by
					enableHdMenu : this.enableHdMenu,
					// taoy
					// /*this.rowExpander
					// stripeRows : true,
					viewConfig : {
						// forceFit : true,
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
					}
				}
				if (this.group)
					cfg.view = new Ext.grid.GroupingView({
								// forceFit : true,
								showGroupName : true,
								enableNoGroups : false,
								hideGroupedColumn : true,
								enableGroupingMenu : false,
								columnsText : "表格字段",
								groupByText : "使用当前字段进行分组",
								showGroupsText : "表格分组",
								groupTextTpl : this.groupTextTpl
							});
				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
				}
				if (this.summaryable) {
					$import("org.ext.ux.grid.GridSummary");
					var summary = new org.ext.ux.grid.GridSummary();
					cfg.plugins = [summary]
					this.summary = summary;
				}
				var cndbars = this.getCndBar(items)
				if (!this.disablePagingTbr) {
					cfg.bbar = this.getPagingToolbar(this.store)
				} else {
					cfg.bbar = this.bbar
				}
				if (!this.showButtonOnPT) {
					if (this.showButtonOnTop) {
						cfg.tbar = (cndbars.concat(this.tbar || []))
								.concat(this.createButtons())
					} else {
						cfg.tbar = cndbars.concat(this.tbar || [])
						cfg.buttons = this.createButtons()
					}
				}

				if (this.disableBar) {
					delete cfg.tbar;
					delete cfg.bbar;
					cfg.autoHeight = true;
					cfg.frame = false;
				}
				this.expansion(cfg);// add by yangl
				this.grid = new this.gridCreator(cfg)
				this.schema = schema;
				this.grid.on("afterrender", this.onReady, this)
				this.grid.on("contextmenu", function(e) {
							e.stopEvent()
						})
				this.grid.on("rowcontextmenu", this.onContextMenu, this)
				this.grid.on("rowdblclick", this.onDblClick, this)
				this.grid.on("rowclick", this.onRowClick, this)
				this.grid.on("keydown", function(e) {
							if (e.getKey() == e.PAGEDOWN) {
								e.stopEvent()
								this.pagingToolbar.nextPage()
								return
							}
							if (e.getKey() == e.PAGEUP) {
								e.stopEvent()
								this.pagingToolbar.prevPage()
								return
							}
							if (e.getKey() == e.ESC) {
								if (this.onESCKey) {
									this.onESCKey();
								}
								return
							}
						}, this)
				// add by yangl tpl
				// this.grid.on('render', function(grid) {
				// var store = grid.getStore(); // Capture the Store.
				//			 
				// var view = grid.getView();
				// grid.tip = new Ext.ToolTip({
				// target: view.mainBody,
				// title :"详细信息",
				// delegate: '.x-grid3-row',
				// dismissDelay : 0,//延迟多少秒后自动关闭,0不关闭
				// trackMouse: true,
				// renderTo: document.body,
				// listeners: {
				// beforeshow: function updateTipBody(tip) {
				// var rowIndex = view.findRowIndex(tip.triggerElement);
				// var str = "<h2>当前显示第"+store.getAt(rowIndex).id+"行信息！<h2>";
				// str += "<h2>当前科室为："+store.getAt(rowIndex).data.KSMC+"<h2>";
				// var url = document.URL;
				// url = url +"resources/css/app/desktop/images/icons/AB1.gif";
				// str += "可以显示一些小的图片信息：<img src='"+url+"' />";
				// tip.body.dom.innerHTML = "<div style='padding:10px;1px solid
				// #999;
				// color:#555;background:#f9f9f9;'>" + str+"</div>";
				// }
				// }
				// });
				// });
				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				return this.grid
			},
			onReady : function() {
				phis.application.cfg.script.ConfigBooksCategoryList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			onRenderer : function(value, metaData, r) {
				var ZBZT = r.get("ZBZT");
				var src = (ZBZT == 1) ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
				+ "resources/phis/resources/images/" + src + ".png'/>";
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
			doExecute : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r == null) {
					MyMessageTip.msg("提示", '请选择需要启用的记录!', true);
					return
				}
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				data["ZBLB"] = r.get("ZBLB");
				this.grid.el.mask("正在启用...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							schemaList : "WL_ZBLB",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							} else {
								MyMessageTip.msg("提示", "启用成功", true);
								this.refresh();
								this.setButtonsState(['update', 'remove',
												'execute'], false);
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
				if (r.data.ZBZT == "1"
						|| (r.data.JGID == this.mainApp.topUnitId && this.mainApp.topUnitId != this.mainApp['phisApp'].deptId)) {
					this
							.setButtonsState(['update', 'remove', 'execute'],
									false);
				} else {
					this.setButtonsState(['update', 'remove', 'execute'], true);
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
			// 上下时改变分配和转床状态
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
			onEnterKey : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				if (r.get("ZBZT") == "1")
					return
				Ext.EventObject.stopEvent()
				this.onDblClick(this.grid)
			},
			onDblClick : function(grid, index, e) {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				if (r.get("ZBZT") == "1"
						|| (r.data.JGID == this.mainApp.topUnitId && this.mainApp.topUnitId != this.mainApp['phisApp'].deptId))
					return
				var actions = this.actions
				if (!actions) {
					return;
				}
				this.selectedIndex = index
				var item = {};
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i]
					var cmd = action.id
					if (cmd == "update" || cmd == "read") {
						item.text = action.name
						item.cmd = action.id
						item.ref = action.ref
						item.script = action.script
						if (cmd == "update") {
							break
						}
					}
				}
				if (item.cmd) {
					this.doAction(item, e)
				}
			}
		})

