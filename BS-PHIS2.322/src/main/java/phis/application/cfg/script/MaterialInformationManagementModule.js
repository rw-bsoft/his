$package("phis.application.cfg.script")
$import("phis.script.common", "phis.script.SimpleList",
		"util.dictionary.TreeDicFactory")

phis.application.cfg.script.MaterialInformationManagementModule = function(cfg) {
	this.westWidth = cfg.westWidth || 200
	this.showNav = true
	this.height = 450

	phis.application.cfg.script.MaterialInformationManagementModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.MaterialInformationManagementModule,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = "getList";
				if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
					this.navDic = "phis.dictionary.AccountingCategory_tree";
					this.LBXH = 0;
					this.entryName = "phis.application.cfg.schemas.WL_WZZD_ZBHS_JG";
					this.requestData.isWSJ = true;
				} else {
					if (!this.mainApp['phis'].treasuryId) {
						Ext.MessageBox.alert("提示", "您还没有选择库房， 请先选择库房 !");
						return;
					}
					if (this.mainApp['phis'].treasuryEjkf != 0) {
						Ext.MessageBox.alert("提示", "该库房不是一级库房!");
						return;
					}
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "queryLBXH",
								KFXH : this.mainApp['phis'].treasuryId
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg,
								this.doLoadReport);
					}
					this.LBXH = ret.json.ret;
					if (this.LBXH == 0) {
						this.navDic = "phis.dictionary.AccountingCategory_tree";
						this.entryName = "phis.application.cfg.schemas.WL_WZZD_ZBHS"
					}
					this.requestData.LBXH = this.LBXH;
					this.requestData.KFXH = this.mainApp['phis'].treasuryId;
				}
				return phis.application.cfg.script.MaterialInformationManagementModule.superclass.initPanel
						.apply(this, [sc]);
			},
			onReady : function() {
				phis.application.cfg.script.MaterialInformationManagementModule.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
				if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
					this.setButtonsState(['introduce'], false);
				}
			},
			warpPanel : function(grid) {
				var navDic = this.navDic
				var filters = "";
				if (navDic == "phis.dictionary.materialInformation_tree") {
					filters = "['and',['eq',['$','item.properties.LBXH'],['i',"
							+ this.mainApp['phis'].treasuryLbxh
							+ "]],['eq',['$','item.properties.JGID'],['s',"
							+ this.mainApp['phisApp'].deptId + "]]]";

				} else {
					if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
						filters = "['eq',['$','item.properties.JGID'],['s',"
								+ this.mainApp['phisApp'].deptId + "]]";
					} else {
						filters = "['in',['$','item.properties.ZBLB'],["
								+ this.mainApp['phis'].treasuryKfzb + "]]";
					}
				}
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : navDic,
							sliceType : 5,
							parentKey : this.navParentKey,
							filter : filters,
							rootVisible : this.rootVisible || false
						})
				this.tree = tf.tree;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										collapsible : true,
										region : 'west',
										width : this.westWidth,
										items : this.tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										items : grid
									}]
						});
				this.tree.on("click", this.onTreeClick, this);
				this.tree.on("load", this.onTreeLoad, this);
				this.tree.expandAll()// 展开树
				this.panel = panel;
				return panel;
			},
			onTreeClick : function(node, e) {
				this.selectedNode = node
				node.expand();
				if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
					this.requestData.HSLB = [];
					this.requestData.HSLB.push(node.id);
					node.eachChild(this.getSelectedHSLB, this);
				}
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId
						&& this.LBXH == 0) {
					this.requestData.HSLB = [];
					this.requestData.HSLB.push(node.id);
					node.eachChild(this.getSelectedHSLB, this);
				}
				if (this.mainApp['phisApp'].deptId != this.mainApp.topUnitId
						&& this.LBXH != 0) {
					this.requestData.FLBM = node.id;
				}

				// if(node.attributes.djlx){
				// this.requestData.cnd=[
				// 'and',
				// [
				// 'and',
				// ['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
				// ['eq', ['$', 'JGID'],
				// ['s', this.mainApp['phisApp'].deptId]]],
				// ['eq', ['$', 'DJLX'], ['s', node.attributes.djlx]]];
				// }
				this.refresh();
				if (this.mainApp['phisApp'].deptId == this.mainApp.topUnitId) {
					this.setButtonsState(['introduce'], false);
				}
			},
			onTreeLoad : function(node) {
				if (node) {
					if (!this.select) {
						node.select();
						this.select = true;
						if (!isNaN(node.id)) {
							this.onTreeClick(node, this);
						}
					}
					if (node.getDepth() == 0) {
						if (node.hasChildNodes()) {
							node.firstChild.expand();
						}
					}
				}
			},
			doAction : function(item) {
				var cmd = item.cmd.charAt(0).toUpperCase() + item.cmd.substr(1);
				eval("this.do" + cmd + "()");
			},
			doCreate : function() {
				if (!this.selectedNode) {
					MyMessageTip.msg("提示", "请选择物资分类后,再新增物资", true);
					return;
				}
				if (this.selectedNode.hasChildNodes()) {
					Ext.MessageBox.alert("提示", "当前分类节点不是最后一层！");
					return;
				}
				var _module = this.createModule("MaterialInformationTabs",this.refModule);
				_module.opener = this;
				this.initModuleCFG(_module, "create");
				this.module = _module;
				var _win = this.get_win(_module);
				_win.show();
			},
			doUpdate : function() {
				r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("GYBZ") == 1) {
					var materialgdcinfotabs = this.createModule(
							"materialgdcinfotabs", "phis.application.cfg.CFG/CFG/CFG5703")
					materialgdcinfotabs.oper = this;
					materialgdcinfotabs.GCSL = r.get("GCSL");
					materialgdcinfotabs.DCSL = r.get("DCSL");
					materialgdcinfotabs.WZXH = r.get("WZXH");
					materialgdcinfotabs.setGDC(r.get("GCSL"), r.get("DCSL"));
					var _win = materialgdcinfotabs.getWin();
					_win.add(materialgdcinfotabs.initPanel());
					_win.show();
					return
				}
				var _module = this.createModule("MaterialInformationTabs",
						this.refModule)
				_module.disableZBLB();
				this.initModuleCFG(_module, "update", r);
				var _win = this.get_win(_module);
				_win.show();
			},
			get_win : function(_module) {
				this.module = _module;
				var _win = _module.getWin();
				_module.opener = this;
				_win.add(_module.initPanel());
				_win.setWidth(800);
				_win.setHeight(550);
				return _win;
			},
			initModuleCFG : function(_module, op, r) {
				_module.cfg = {};
				_module.cfg.op = op;
				_module.cfg.JGID = this.mainApp['phisApp'].deptId;
				_module.cfg.WZXH = 0;
				if (this.LBXH != 0) {
					if (this.selectedNode) {
						_module.cfg.ZDXH = this.selectedNode.attributes.id;
					} else {
						_module.cfg.ZDXH = r.data.ZDXH
					}
				}
				if (this.LBXH == 0) {
					if (this.selectedNode) {
						_module.cfg.HSLB = this.selectedNode.attributes.id;
					} else {
						_module.cfg.HSLB = r.data.HSLB
					}
				}
				_module.cfg.LBXH = this.LBXH;
				_module.cfg.KFXH = this.mainApp['phis'].treasuryId;
				_module.cfg.EJKF = this.mainApp['phis'].treasuryEjkf;

				if (r) {
					_module.cfg._record = r;
					_module.cfg.WZXH = r.get("WZXH");
				}
			},
			getSelectedHSLB : function(nod) {
				if (nod) {
					this.requestData.HSLB.push(nod.id);
					nod.eachChild(this.getSelectedHSLB, this);
				}
			},
			doIntroduce : function() {
				if (this.win1) {
					var module = this.midiModules["getCFG5702"];
					module.initDataId = this.initDataId
					module.mainApp = this.mainApp;
					module.requestData.cnd = null;
					module.loadData();
					this.win1.show();
					this.win1.center();
					return;
				}
				var module = this.createModule("getCFG5702", "phis.application.cfg.CFG/CFG/CFG5702");
				module.on("save", this.onSave, this);
				this.module = module;
				module.initDataId = this.initDataId
				module.mainApp = this.mainApp;
				this.win1 = module.getWin();
				this.win1.add(module.initPanel());
				this.win1.show();
			},
			doInvalid : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r == null) {
					MyMessageTip.msg("提示", '请选择注销的记录!', true);
					return
				}
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				data["WZXH"] = r.get("WZXH");
				data["GYBZ"] = r.get("GYBZ");
				if (r.get("WZZT") == "-1") {
					data["WZZT"] = "1";
					this.grid.el.mask("正在取消注销...", "x-mask-loading")
				} else {
					data["WZZT"] = "-1";
					this.grid.el.mask("正在注销...", "x-mask-loading")
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "updateCanceledMaterial",
							schemaList : "WL_WZZD",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
								var btns = this.grid.getTopToolbar();
								var btn = btns.find("cmd", "invalid");
								btn = btn[0];
								if (r.data.WZZT == 1) {
									if (btn.getText().indexOf("取消注销") > -1) {
										return;
									}
									btn.setText(btn.getText().replace("注销",
											"取消注销"));
								} else {
									btn.setText(btn.getText().replace("取消注销",
											"注销"));
								}
								this.refresh();
								return;
							}
						}, this)
			},
			onContextMenu : function(grid, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.grid.getSelectionModel().selectRow(rowIndex)
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
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['gridContextMenu'] = cmenu
				}
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
				var toolBar = this.grid.getTopToolbar();
				if (toolBar) {
					for (var i = 0; i < this.actions.length; i++) {
						var btn = toolBar.find("cmd", this.actions[i].id);
						if (this.actions[i].id == "invalid") {
							var btnResult = btn[0].getText().substring(0, 2);
							if (btn[0].getText().indexOf("取消注销") > -1) {
								var btnResult = btn[0].getText()
										.substring(0, 4);
							}
							cmenu.items.itemAt(i).setText(btnResult);
						}
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
			},// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "invalid");
				btn = btn[0];
				if (r.data.WZZT == -1) {
					if (btn.getText().indexOf("取消注销") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("注销", "取消注销"));
				} else {
					btn.setText(btn.getText().replace("取消注销", "注销"));
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
			},// 上下时改变分配和转床状态
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
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
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("GYBZ") == 1) {
					MyMessageTip.msg("提示", '公共物资不能删除!', true);
					return
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						title += r.get(this.schema.pkeys[i])
					}
				}
				// add by liyl 2012-06-17 提示信息增加名称显示功能
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(r);
								}
							},
							scope : this
						})
			},onRenderer : function(value, metaData, r) {
                var SFBZ = r.get("SFBZ");
                var src = (SFBZ == 1) ? "yes" : "no";
                return "<img src='" + ClassLoader.appRootOffsetPath+ "resources/phis/resources/images/" + src + ".png'/>";
            }
		})