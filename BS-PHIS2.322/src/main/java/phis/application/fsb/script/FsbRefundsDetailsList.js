$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FsbRefundsDetailsList = function(cfg) {
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	cfg.autoLoadData = false;
	phis.application.fsb.script.FsbRefundsDetailsList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FsbRefundsDetailsList,
		phis.script.SimpleList, {
//			initPanel : function(sc) {
//				if (this.grid) {
//					if (!this.isCombined) {
//						this.fireEvent("beforeAddToWin", this.grid)
//						this.addPanelToWin();
//					}
//					return this.grid;
//				}
//				var schema = sc
//				if (!schema) {
//					var re = util.schema.loadSync(this.entryName)
//					if (re.code == 200) {
//						schema = re.schema;
//					} else {
//						this.processReturnMsg(re.code, re.msg, this.initPanel)
//						return;
//					}
//				}
//				this.schema = schema;
//				this.isCompositeKey = schema.isCompositeKey;
//				var items = schema.items
//				this.items = items;
//				
//				if (!items) {
//					return;
//				}
//
//				this.store = this.getStore(items)
//				if (this.mutiSelect) {
//					this.sm = new Ext.grid.CheckboxSelectionModel()
//				}
//				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
//				var cfg = {
//					stripeRows : true,
//					border : false,
//					store : this.store,
//					cm : this.cm,
//					sm : this.sm,
//					height : this.height,
//					loadMask : {
//						msg : '正在加载数据...',
//						msgCls : 'x-mask-loading'
//					},
//					buttonAlign : 'center',
//					clicksToEdit : true,
//					frame : true,
//					plugins : this.array.length == 0 ? null : this.array,// modife
//					viewConfig : {
//						// forceFit : true,
//						enableRowBody : this.enableRowBody,
//						getRowClass : this.getRowClass
//					}
//				}
//				if (this.group)
//					cfg.view = new Ext.grid.GroupingView({
//								forceFit : true,
//								showGroupName : true,
//								enableNoGroups : false,
//								hideGroupedColumn : true,
//								enableGroupingMenu : false,
//								columnsText : "表格字段",
//								groupByText : "使用当前字段进行分组",
//								showGroupsText : "表格分组",
//								groupTextTpl : this.groupTextTpl
//							});
//				if (this.gridDDGroup) {
//					cfg.ddGroup = this.gridDDGroup;
//					cfg.enableDragDrop = true
//				}
//				if (this.summaryable) {
//					$import("org.ext.ux.grid.GridSummary");
//					var summary = new org.ext.ux.grid.GridSummary();
//					cfg.plugins = [summary]
//					this.summary = summary;
//				}
//				var cndbars = this.getCndBar(items)
//				if (!this.disablePagingTbr) {
//					cfg.bbar = this.getPagingToolbar(this.store)
//				} else {
//					cfg.bbar = this.bbar
//				}
//				if (!this.showButtonOnPT) {
//					if (this.showButtonOnTop) {
//						cfg.tbar = (cndbars.concat(this.tbar || []))
//								.concat(this.createButtons())
//					} else {
//						cfg.tbar = cndbars.concat(this.tbar || [])
//						cfg.buttons = this.createButtons()
//					}
//				}
//
//				if (this.disableBar) {
//					delete cfg.tbar;
//					delete cfg.bbar;
//					cfg.autoHeight = true;
//					cfg.frame = false;
//				}
//				this.expansion(cfg);// add by yangl
//				this.grid = new this.gridCreator(cfg)
//				this.schema = schema;
//				this.grid.on("afterrender", this.onReady, this)
//				this.grid.on("contextmenu", function(e) {
//							e.stopEvent()
//						})
//				this.grid.on("rowcontextmenu", this.onContextMenu, this)
//				this.grid.on("rowdblclick", this.onDblClick, this)
//				this.grid.on("rowclick", this.onRowClick, this)
//				this.grid.on("keydown", function(e) {
//							if (e.getKey() == e.PAGEDOWN) {
//								e.stopEvent()
//								this.pagingToolbar.nextPage()
//								return
//							}
//							if (e.getKey() == e.PAGEUP) {
//								e.stopEvent()
//								this.pagingToolbar.prevPage()
//								return
//							}
//							if (e.getKey() == e.ESC) {
//								if (this.onESCKey) {
//									this.onESCKey();
//								}
//								return
//							}
//						}, this)
//				if (!this.isCombined) {
//					this.fireEvent("beforeAddToWin", this.grid)
//					this.addPanelToWin();
//				}
//				return this.grid
//			},
			doCommit : function() {
				var r = this.getSelectedRecord();
				this.opener.fireEvent("choose", r)
				this.opener.runderwin.hide();
				this.opener.runderwinopening = false;
			},
			onDblClick : function(grid, index, e) {
				this.doCommit();
			},
			doCancel : function() {
				this.opener.runderwin.hide();
				this.opener.runderwinopening = false;
				var form = this.opener.form.form.getForm();
				
				form.findField("FYMC").setValue("");
				
				form.findField("FYSL").setValue("1.00");
				form.findField("FYDJ").setValue("0.0000");
				
				form.findField("FYSL").setDisabled(true);
				form.findField("FYDJ").setDisabled(true);
				form.findField("ZXKS").setDisabled(true);
				form.findField("YSGH").setDisabled(true);
				form.findField("FYRQ").setDisabled(true);
				
				this.opener.FYLR.FYMC = "";
				this.opener.XMXX = "";
				form.findField("FYMC").focus(true,200);
				return;
				
			}
		});