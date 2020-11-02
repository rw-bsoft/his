$package("phis.application.fsb.script")


$import("phis.script.SimpleList")

phis.application.fsb.script.FsbCostList = function(cfg) {
	cfg.autoLoadData = false;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
	phis.application.fsb.script.FsbCostList.superclass.constructor.apply(this,
			[cfg]);

}

Ext.extend(phis.application.fsb.script.FsbCostList, phis.script.SimpleList, {
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
				this.schema = schema;
				this.isCompositeKey = schema.isCompositeKey;
				var items = schema.items
				this.items = items;
				if (!items) {
					return;
				}

				this.store = this.getStore(items)
				if (this.mutiSelect) {
					this.sm = new Ext.grid.CheckboxSelectionModel()
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				if(!this.array){
					this.array = [];
				}
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
					plugins : this.array.length == 0 ? null : this.array,// modife
					viewConfig : {
						// forceFit : true,
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
					}
				}
				if (this.group)
					cfg.view = new Ext.grid.GroupingView({
								forceFit : true,
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
			getStore : function(items) {
				Ext.apply(this.requestData, this.serverParams)
				var o = this.getStoreFields(items)
				var readCfg = {
					root : 'body',
					totalProperty : 'totalCount',
					fields : o.fields
				}
				if (!this.isCompositeKey) {
					readCfg.id = o.pkey;
				}
				var reader = new Ext.data.JsonReader(readCfg)
				var url = ClassLoader.serverAppUrl || "";
				// add by yangl 请求统一加前缀
				if (this.requestData) {
					this.requestData.serviceId = 'phis.'
							+ this.requestData.serviceId
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'post',
							jsonData : this.requestData
						})
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["x-response-code"]
									var msg = json["x-response-msg"]
									this.processReturnMsg(code, msg,
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)
				if (this.group) {
					var store = new Ext.data.GroupingStore({
								reader : reader,
								proxy : proxy,
								sortInfo : {
									field : this.groupSort || this.group,
									direction : "ASC"
								},
								groupField : this.group
							});
				} else {
					var store = new Ext.data.Store({
								proxy : proxy,
								reader : reader
							})
				}
				store.on("load", this.onStoreLoadData, this)
				store.on("beforeload", this.onStoreBeforeLoad, this)
				return store

			},
			refresh : function() {
				//this.getStore(this.items);
				this.onStoreBeforeLoad();
				this.fireEvent("refresh")
				this.loadData();

			}
		});