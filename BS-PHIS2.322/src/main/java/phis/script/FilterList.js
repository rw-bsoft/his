/**
 * 可以对列表进行自定义的过滤和表头的分组
 *
 * @author AD
 */
$package("phis.script");

$import("phis.script.SimpleList");
$styleSheet("phis.script.ux.gridfilters.css.RangeMenu");
$styleSheet("phis.script.ux.gridfilters.css.GridFilters");
$import("phis.script.ux.gridfilters.filter.Filter");
$import("phis.script.ux.gridfilters.filter.BooleanFilter");
$import("phis.script.ux.gridfilters.filter.DateFilter");
$import("phis.script.ux.gridfilters.filter.ListFilter");
$import("phis.script.ux.gridfilters.filter.NumericFilter");
$import("phis.script.ux.gridfilters.filter.StringFilter");
$import("phis.script.ux.gridfilters.menu.ListMenu");
$import("phis.script.ux.gridfilters.menu.RangeMenu");
$import("phis.script.ux.gridfilters.GridFilters");

phis.script.FilterList = function(cfg) {
	phis.script.FilterList.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(phis.script.FilterList,phis.script.SimpleList, {
	initPanel: function(sc) {
		var schema = sc;
		if (!schema) {
			var re = util.schema.loadSync(this.entryName);
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel);
				return;
			}
		}

		var items = schema.items;
		this.store = this.getStore(items);
		this.cm = new Ext.grid.ColumnModel(this.getCM(items));//已经生成this.filters

		var cfg = {
				stripeRows : true,
				border : false,
				store : this.store,
				cm : this.cm,
				height : this.height,
				loadMask : {
					msg : '正在加载数据...',
					msgCls : 'x-mask-loading'
				},
				buttonAlign : 'center',
				clicksToEdit : true,
				frame : true,

				plugins :[],
				viewConfig : {
					enableRowBody : this.enableRowBody,
					getRowClass : this.getRowClass
				}
		};
		if (this.summaryable) {
			$import("org.ext.ux.GridSummary");
			var summary = new org.ext.ux.grid.GridSummary();
			cfg.plugins.push(summary);
			this.summary = summary;
		}
		if(this.custPlug && this.custPlug.length > 0){
			cfg.plugins = cfg.plugins.concat(this.custPlug);
			this.plugMerged = true;
		}
		cfg.plugins.push(this.filters);
		var cndbars = this.getCndBar(items);
		if (!this.disablePagingTbr) {
			cfg.bbar = this.getPagingToolbar(this.store);
		} else {
			cfg.bbar = this.bbar;
		}
		if (!this.showButtonOnPT) {
			if (this.showButtonOnTop) {
				cfg.tbar = (cndbars.concat(this.tbar || []))
				.concat(this.createButtons());
			} else {
				cfg.tbar = cndbars.concat(this.tbar || []);
				cfg.buttons = this.createButtons();
			}
		}

		if (this.disableBar) {
			delete cfg.tbar;
			delete cfg.bbar;
			cfg.autoHeight = true;
			cfg.frame = false;
		}
		this.expansion(cfg);
		this.grid = new this.gridCreator(cfg);
		this.schema = schema;
		this.grid.on("afterrender", this.onReady, this);
		this.grid.on("contextmenu", function(e) {
			e.stopEvent();
		});
		this.grid.on("rowcontextmenu", this.onContextMenu, this);
		this.grid.on("rowdblclick", this.onDblClick, this);
		this.grid.on("rowclick", this.onRowClick, this);
		this.grid.on("keydown", function(e) {
			if (e.getKey() == e.PAGEDOWN) {
				e.stopEvent();
				this.pagingToolbar.nextPage();
				return
			}
			if (e.getKey() == e.PAGEUP) {
				e.stopEvent();
				this.pagingToolbar.prevPage();
				return
			}
		}, this);
		if (!this.isCombined) {
			this.fireEvent("beforeAddToWin", this.grid);
			this.addPanelToWin();
		}
		return this.grid;
	},
	getCM:function(items){
		var cm = [];
		var ac = util.Accredit;
		var expands = [];
		if (this.showRowNumber) {
			cm.push(new Ext.grid.RowNumberer());
		}

		var filters = [];
		for (var i = 0; i < items.length; i++) {
			var it = items[i];
			if ((it.display <= 0 || it.display == 2 || it.hidden == true)
					|| !ac.canRead(it.acValue)) {
				continue;
			}
			if (it.expand) {
				var expand = {
					id : it.dic ? it.id + "_text" : it.id,
					alias : it.alias,
					xtype : it.xtype
				};
				expands.push(expand);
				continue;
			}
			if (!this.fireEvent("onGetCM", it)) { // **
				// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
				continue;
			}
			if(it.filter==="true"){
				filters.push({
					type: it.filterType || 'string',
					dataIndex: it.id
				});
			}

			var width = parseInt(it.width || 80);
			// if(width < 80){width = 80}
			var c = {
					header : it.alias,
					width : width,
					sortable : true,
					filterable: true,
					dataIndex : it.dic ? it.id + "_text" : it.id
			};
			if (!this.isCompositeKey && it.pkey) {
				c.id = it.id;
			}
			if (it.summaryType) {
				c.summaryType = it.summaryType;
			}
			switch (it.type) {
				case 'int' :
				case 'double' :
				case 'bigDecimal' :
					if (!it.dic) {
						c.css = "color:#00AA00;font-weight:bold;";
						c.align = "right";
						if (it.precision > 0) {
							var nf = '0.';
							for (var j = 0; j < it.precision; j++) {
								nf += '0';
							}
							c.renderer = Ext.util.Format
									.numberRenderer(nf);
						}
					}
					break;
				case 'date' :
					c.renderer = function(v) {
						if (v && typeof v == 'string' && v.length > 10) {
							return v.substring(0, 10);
						}
						return v;
					};
					break;
				case 'timestamp' :
				case 'datetime' :
					if (it.xtype == 'datefield') {
						c.renderer = function(v) {
							if (v && typeof v == 'string'
									&& v.length > 10) {
								return v.substring(0, 10);
							} else {
								return v;
							}
						};
					}
					break;
			}
			if (it.renderer) {
				var func = eval("this." + it.renderer);
				if (typeof func == 'function') {
					c.renderer = func;
				}
			}
			if (it.summaryType) {
				c.summaryType = it.summaryType;
				if (it.summaryRenderer) {
					var func = eval("this." + it.summaryRenderer);
					if (typeof func == 'function') {
						c.summaryRenderer = func;
					}
				}
			}
			if (this.fireEvent("addfield", c, it)) {
				cm.push(c);
			}
		}
		if (expands.length > 0) {
			this.rowExpander = this.getExpander(expands);
			cm = [this.rowExpander].concat(cm);
		}

		this.filters = new Ext.ux.grid.GridFilters({
	        encode: true,
	        local: true,
	        filters: filters
	    });
		return cm;
	}
});