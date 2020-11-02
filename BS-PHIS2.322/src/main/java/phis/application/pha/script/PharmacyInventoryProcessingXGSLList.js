$package("phis.application.pha.script");

$import("phis.script.EditorList", "org.ext.ux.ColumnHeaderGroup");

phis.application.pha.script.PharmacyInventoryProcessingXGSLList = function(cfg) {
	 cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyInventoryProcessingXGSLList.superclass.constructor.apply(
			this, [cfg]);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
	//this.on("afterCellEdit", this.onAfterCellEdit, this);
}

Ext.extend(phis.application.pha.script.PharmacyInventoryProcessingXGSLList,
		phis.script.EditorList, {
			initPanel : function(sc) {
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

				var items = schema.items
				if (!items) {
					return;
				}

				this.store = this.getStore(items)
				if (this.mutiSelect) {
					this.sm = new Ext.grid.CheckboxSelectionModel({header:""})
					this.sm.handleMouseDown = Ext.emptyFn;
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var group = new Ext.ux.grid.ColumnHeaderGroup({
							rows : [[{
										header : "",
										colspan : 2,
										align : 'center'
									}, {
										header : "药库包装录入",
										colspan : 2,
										align : 'center'
									}, {
										header : "药房包装录入",
										colspan : 2,
										align : 'center'
									}]]
						});

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

					plugins :  this.rowExpander,// modife
					// by
					viewConfig : {
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
					}
				}
				if (this.headPlug) {
					cfg.plugins = group;
				}
				if (this.group)
					cfg.view = new Ext.grid.GroupingView({
								forceFit : true,
								groupTextTpl : '{text} ({[values.rs.length]} 条记录)'
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
						}, this)
				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				this.grid.on("afteredit", this.afterCellEdit, this)
				this.grid.on("beforeedit", this.beforeCellEdit, this)
				this.grid.on("doNewColumn", this.doInsertAfter, this)
				return this.grid
			},
			// 编辑前判断 如果完成 则不能修改. 如果药库包装和药房包装相同则不能修改药库数量
			onBeforeCellEdit : function(it, record, field, v) {
				if (this.pdwc == 1) {
					return false;
				}
				if (it.id == "YKSL") {
					if (record.get("YKBZ") == record.get("YFBZ")) {
						return false;
					}
				}
				return true;
			},
			//为了解决SYS_Personnel的PERSONNAME引用过来不能去掉查询的缺陷,暂时现在不要查询的 以后需要的加代码
			getCndBar:function(items){
			return [];
			}
		});