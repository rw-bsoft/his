// @@ 首页公告列表。
$package("chis.application.pif.script")

$import("chis.script.BizSimpleListView")

chis.application.pif.script.PublicInfoListView2 = function(cfg) {
	var manageUnitId = cfg.mainApp.deptId;
	var c = [];
	if (manageUnitId >= 6) {
		var sub = manageUnitId.substr(0, 2);
		if (sub == "11" || sub == "12" || sub == "50" || sub == "31") {
			c.push(manageUnitId.substring(0, 2));
		} else {
			c.push(manageUnitId.substring(0, 4));
		}
		if (manageUnitId.length >= 11) {
			c.push(manageUnitId.substring(0, 6));
			c.push(manageUnitId.substring(0, 9));
			c.push(manageUnitId);
		} else if (manageUnitId.length == 9) {
			c.push(manageUnitId.substring(0, 6));
			c.push(manageUnitId.substring(0, 9));
		} else if (manageUnitId.length == 6) {
			c.push(manageUnitId.substring(0, 6));
		}
	} else {
		c.push(manageUnitId);
	}
	var cnd
	if (cfg.mainApp.jobId == 'chis.system') {
		cnd = ['like', ['$', 'a.publishUnit'], ['s', c[0] + '%']];
	} else {
		if (c.length == 1) {
			cnd = ['eq', ['$', 'a.publishUnit'], ['s', c[0]]];
		} else {
			cnd = ['or'];
			for (var i = 0; i < c.length; i++) {
				cnd.push(['eq', ['$', 'a.publishUnit'], ['s', c[i]]]);
			}
		}
	}
	this.initCnd = cnd;
	chis.application.pif.script.PublicInfoListView2.superclass.constructor.apply(this,
			[cfg]);
	this.schema = cfg.entryName;
	this.showButtonOnTop = false;
}

Ext.extend(chis.application.pif.script.PublicInfoListView2, chis.script.BizSimpleListView,
		{
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd];
				if (module) {
					module.initPanel();
					module.addPanelToWin();
					var win = module.getWin();
					if (xy) {
						win.setPosition(this.xy[0] || xy[0], this.xy[1]
										|| xy[1]);
					}
					win.setTitle(module.title);
					win.show();
					this.fireEvent("openModule", module);
					var formData = {};
					if (r) {
						formData = this.castListDataToForm(r.data, this.schema);
						this.selectedIndex = this.store.find('infoId', r
										.get("infoId"));
					}
					if (!win.hidden) {
						module.initFormData(formData);
					}
				}
			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "查看";
				item.cmd = "update";
				item.ref = "chis.application.index.INDEX/MYPAGE03_1";
				item.script = "chis.application.pif.script.PublicInfoFormView2";
				this.doAction(item, e)
			},

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
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
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
					plugins : null,
					viewConfig : {
						getRowClass : this.getRowClass
					}
				}
				if (this.sm) {
					cfg.sm = this.sm
				}
				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
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
						// cfg.tbar = cndbars.concat(this.tbar || [])
						cfg.tbar = null;
						cfg.buttons = this.createButtons()
					}
				}
				if (this.disableBar) {
					delete cfg.tbar;
					delete cfg.bbar;
					cfg.autoHeight = true;
					cfg.frame = false;
				}
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
			}
		})