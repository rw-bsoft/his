$package("phis.application.cfg.script")

/**
 * 分类类别维护 gaof 2013.03.18
 */
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigClassifyComboList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.showRowNumber = true;

	phis.application.cfg.script.ConfigClassifyComboList.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
	this.on("remove", this.onRemove, this);
}
Ext.extend(phis.application.cfg.script.ConfigClassifyComboList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.configClassifyService";
				this.requestData.serviceAction = "classifyListQuery";

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
			onReady : function() {
				phis.application.cfg.script.ConfigClassifyComboList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (String(index) == 'false')
					return;
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record.data.ZFBZ == 1) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">类别已启用 </div>'
								}, false);
					} else {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">类别已注销 </div>'
								}, false);
					}
				}

			},
			onRenderer : function(value, metaData, r) {
				var ZFBZ = r.get("ZFBZ");
				var src = (ZFBZ == 1) ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
				+ "resources/phis/resources/images/" + src + ".png'/>";
			},
			doExecute : function() {
				var r = this.getSelectedRecord();
				var data = {};
				if (r == null) {
					Ext.Msg.alert("提示", "请选择类别");
				}
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				data["LBXH"] = r.get("LBXH");
				data["LBMC"] = r.get("LBMC");
				if (r.get("ZFBZ") == "1") {
					data["ZFBZ"] = "-1";
					this.grid.el.mask("正在注销...", "x-mask-loading")
				} else {
					data["ZFBZ"] = "1";
					this.grid.el.mask("正在启用...", "x-mask-loading")
				}
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "excute",
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							}
							if (0 != json.countKFXX) {
								MyMessageTip
										.msg("提示", "当前分类在库房中已使用,不能注销", true);
								return;
							}
							if (0 == json.countFLGZ) {
								MyMessageTip.msg("提示", "未维护分类规则,不能启用", true);
								return;
							}
							var btns = this.grid.getTopToolbar();
							var btn = btns.find("cmd", "execute");
							btn = btn[0];
							if (r.data.ZFBZ == 1) {
								// if (btn.getText().indexOf("注销") > -1) {
								// return;
								// }
								btn.setText(btn.getText().replace("启用", "注销"));
							} else {
								btn.setText(btn.getText().replace("注销", "启用"));
							}
							this.refresh();
							if (!this.selectedIndex) {
								this.selectRow(0);
								this.onRowClick();
							} else {
								this.selectRow(this.selectedIndex);
								this.selectedIndex = 0;
								this.onRowClick();
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
				if (r.data.ZFBZ == 1) {
					if (btn.getText().indexOf("注销") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("启用", "注销"));
				} else {
					btn.setText(btn.getText().replace("注销", "启用"));
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
			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					Ext.Msg.alert("提示", "请选择类别");
					return;
				}
				var data = {};
				data["LBXH"] = r.id;

				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : "isClassifyUsed",
							body : data
						}, function(code, msg, json) {
							if (code >= 300) {
								MyMessageTip.msg("提示", msg, true);
							}
							if (0 != json.countKFXX) {
								Ext.Msg.alert("提示", "当前分类在库房中已使用，不能删除");
								return;
							}
							if (0 != json.countFLZD) {
								Ext.Msg.alert("提示", "当前分类已有分类的物资，不能删除");
								return;
							}
							Ext.MessageBox.confirm('提示', '是否删除该分类?', function(
									btn) {
								if (btn == "yes") {
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "deleteClassify",
												body : data
											}, function(code, msg, json) {
												if (code >= 300) {
													MyMessageTip.msg("提示", msg,
															true);
												}
												this.refresh();
											}, this)
								}
							}, this);
						}, this)
			},
			onContextMenu : function(view, rowIndex, e) {
				var r = this.getSelectedRecord();
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
//				this.curView.selectRow(rowIndex)
//				var cmenu = this.midiMenus['ContextMenu']
//				if (!cmenu) {
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
						if(r.get("ZFBZ")==1 && i==1){
							it.text = '注销'
						}else{
							it.text = action.name
						}
//						it.text = action.name
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					cmenu = new Ext.menu.Menu({
								items : items
							})
//					this.midiMenus['ContextMenu'] = cmenu
//				}
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
				var data = view.store.getAt(rowIndex).data;
//				this.changeMenuStatus(data, cmenu)
				cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
			},
			onDblClick : function(grid, index, e) {
			},
		})
