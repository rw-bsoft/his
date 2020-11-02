$package("phis.application.cfg.script")

/**
 * 医院库房维护list shiwy 2013.11.07
 */
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigTreasuryInformationList = function(cfg) {
	cfg.winState = "center";
	phis.application.cfg.script.ConfigTreasuryInformationList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.ConfigTreasuryInformationList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configTreasuryInformationService",
							serviceAction : "queryZBLBXX"
						});
				if (r1.code == 600) {
					MyMessageTip.msg("提示", "请先维护账簿类别或者启用维护的账簿类别!", true);
					return;
				}
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
					plugins : (this.array&&this.array.length == 0) ? null : this.array,// modife
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
				phis.application.cfg.script.ConfigTreasuryInformationList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			onRenderer : function(value, metaData, r) {
				var KFZT = r.get("KFZT");
				var src = (KFZT == 1) ? "yes" : "no";
				return "<img src='images/" + src + ".png'/>";
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
				data["KFXH"] = r.get("KFXH");
				data["EJKF"] = r.get("EJKF");
				if (r.get("KFZT") == "1") {
					MyMessageTip.msg("提示", '该条记录已经启用!', true);
					return
				}
				this.grid.el.mask("正在启用...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							schemaList : "WL_KFXX",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							} else {
								MyMessageTip.msg("提示", "启用成功", true);
								this.refresh();
							}
						}, this)
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
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "update");
				btn = btn[0];
				if (r.data.KFZT == 1) {
					if (btn.getText().indexOf("查看") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("修改", "查看"));
				} else {
					btn.setText(btn.getText().replace("查看", "修改"));
				}

			},
			doInitialize : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r == null) {
					MyMessageTip.msg("提示", '请选择需要启用的记录!', true);
					return
				}
				if (r.get("CSBZ") != "0") {
					MyMessageTip.msg("提示", '该库房已经账册初始化,不能初始化', true);
					return
				}
				data["KFXH"] = r.get("KFXH");

				Ext.Msg.show({
							title : '确认初始化库房[' + r.get("KFMC") + ']',
							msg : '初始化操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.grid.el.mask("正在初始化...",
											"x-mask-loading")
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "initialize",
												body : data
											}, function(code, msg, json) {
												this.grid.el.unmask()
												if (code >= 300) {
													MyMessageTip.msg("提示", msg,
															true);
												} else {
													MyMessageTip.msg("提示",
															"初始化成功", true);
													this.refresh();
												}
											}, this)
								}
							},
							scope : this
						})

			}
		})
