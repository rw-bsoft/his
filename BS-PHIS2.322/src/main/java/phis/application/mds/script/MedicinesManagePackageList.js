$package("phis.application.mds.script")

$import("phis.script.SimpleList", "phis.script.widgets.Strategy",
		"org.ext.ux.ColumnHeaderGroup")

phis.application.mds.script.MedicinesManagePackageList = function(cfg) {
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以 
	phis.application.mds.script.MedicinesManagePackageList.superclass.constructor.apply(this, [cfg])
	}
Ext.extend(phis.application.mds.script.MedicinesManagePackageList,
		phis.script.SimpleList, {
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
					this.sm = new Ext.grid.CheckboxSelectionModel()
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var group = new Ext.ux.grid.ColumnHeaderGroup({
							rows : [[{
										header : "药品基本信息",
										colspan : 5,
										align : 'center'
									}, {
										header : "最小包装",
										colspan : 2,
										align : 'center'
									}, {
										header : "门诊药房",
										colspan : 2,
										align : 'center'
									}, {
										header : "病区药房",
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
					plugins : this.rowExpander,
					//plugins : (this.array.length == 0 ? null : this.array),// modife
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
				return this.grid
				
			},
			onRenderer : function(value, metaData, r) {
				if (r.data.ZFPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/(00,04).png'/>"
				}
				return value;
			},
			// 鼠标移动提示记录是否已作废功能
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.ZFPB == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '药品已作废'
									}, false);

						}
					}
				}
			},
			// 加上鼠标移动提示记录是否已作废功能
			onReady : function() {
				phis.application.mds.script.MedicinesManagePackageList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			// 打印 dingcj
	      doPrint : function() {
				var module = this.createModule("mediPackageMsg",
						this.refmediPackageMsg);
				var r = this.getSelectedRecord();
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的药品信息!", true);
					return;
				}
				// 得到检索的类别和值
				module.LB = this.cndFldCombox.getValue();
				module.VALUE = this.cndField.getValue();
				if(this.ZBY){
				module.ZBY=1
				}
				module.initPanel();
				module.doPrint(); 
			}

		})