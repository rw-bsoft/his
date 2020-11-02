$package("phis.application.sas.script")
$import("phis.script.SimpleList","phis.prints.script.FixedAssetsSearchPrintView")

phis.application.sas.script.FixedAssetsSearchList = function(cfg) {
	phis.application.sas.script.FixedAssetsSearchList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sas.script.FixedAssetsSearchList, phis.script.SimpleList,
		{
			initPanel : function(sc) {
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId) {
					if (this.mainApp['phis'].treasuryId == null
							|| this.mainApp['phis'].treasuryId == ""
							|| this.mainApp['phis'].treasuryId == undefined) {
						Ext.Msg.alert("提示", "未设置登录库房,请先设置");
						return null;
					}
					if (this.mainApp['phis'].treasuryEjkf != 0) {
						Ext.MessageBox.alert("提示", "该库房不是一级库房!");
						return;
					}
					if (this.mainApp['phis'].treasuryCsbz != 1) {
						Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
						return;
					}
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
					plugins : this.rowExpander,
					enableHdMenu : this.enableHdMenu,
					viewConfig : {
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
				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				return this.grid
			},
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.suppliesStockSearchService";
				this.requestData.serviceAction = "queryFixedAssetsList";

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
			doRefresh : function(){
				this.loadData();
			},
			doPrint : function() {
				var pWin = this.midiModules["FixedAssetsSearchPrintView"]
				var cfg = {
					requestData : this.ZBLB
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.FixedAssetsSearchPrintView(cfg)
				this.midiModules["FixedAssetsSearchPrintView"] = pWin
				pWin.getWin().show()
			}
		})