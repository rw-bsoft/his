$package("phis.application.cfg.script")

$import("phis.script.SimpleList", "app.modules.config.unit.manageUnitBuilder")

phis.application.cfg.script.CirculationMethodsModule = function(cfg) {
	cfg.selectFirst = false
	cfg.autoLoadData = false;
	phis.application.cfg.script.CirculationMethodsModule.superclass.constructor
			.apply(this, [ cfg ])
}

Ext
		.extend(
				phis.application.cfg.script.CirculationMethodsModule,
				phis.script.SimpleList,
				{
					warpPanel : function(grid) {
						this.navParentKey = this.mainApp['phis'].treasuryEjkf > 0 ? 2
								: 1;
							this.navParentText=this.mainApp['phis'].treasuryName;
						// this.entryName=this.mainApp['phis'].treasuryEjkf>0?"WL_LZFS1":"WL_LZFS";
						var tree = util.dictionary.TreeDicFactory.createTree({
							id : this.dicId,
							parentKey : this.navParentKey || {},
							parentText : this.navParentText,
							rootVisible : this.rootVisible || false
						})
						tree.autoScroll = true
						tree.on("click", this.onTreeClick, this)
						tree.on("load", this.onTreeLoad, this);
						this.tree = tree;
						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [ {
								layout : "fit",
								split : true,
								region : 'west',
								bodyStyle : 'padding:5px',
								width : 200,
								items : tree
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								width : 280,
								items : grid
							} ]
						});
						this.panel = panel;
						this.tree.on("contextmenu", this.onContextMenu, this);
						if (!this.mainApp['phis'].treasuryId) {
							Ext.Msg.alert("提示", "请选择库房!", function() {
							});
							return;
						}
						return panel
					},
					onTreeClick : function(node, e) {
						this.selectedNode = node
						node.expand();
						this.requestData.pageNo = 1;
						this.requestData.cnd = [
								'and',
								[ 'eq', [ '$', 'KFXH' ],
										[ 'i', this.mainApp['phis'].treasuryId ] ],
								[ 'eq', [ '$', 'JGID' ],
										[ 's', this.mainApp['phisApp'].deptId ] ] ];
						if (node.attributes.djlx) {
							this.requestData.cnd = [
									'and',
									[
											'and',
											[
													'eq',
													[ '$', 'KFXH' ],
													[
															'i',
															this.mainApp['phis'].treasuryId ] ],
											[
													'eq',
													[ '$', 'JGID' ],
													[ 's', this.mainApp['phisApp'].deptId ] ] ],
									[ 'eq', [ '$', 'DJLX' ],
											[ 's', node.attributes.djlx ] ] ];
						}
						this.refresh();
					},
					doInsertItem : function() {
						this.resetButtons()
						var fields = this.getFields();
						var buttons = this.panel.getTopToolbar().items
						buttons.get(0).disable()
						buttons.get(2).disable()
						// buttons.get(3).disable()

						var parent = null;

						fields.unitId.setValue("")
						fields.organizName.setValue("")
						fields.pyCode.setValue("")
						fields.type.setValue({
							key : "a",
							text : "市"
						})
						fields.type.setDisabled(true);
						fields.unitId.enable()
						this.saveItemOp = "insert"

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
						} else {
							this.doInsertItem();
						}
					},
					// 单击时改变作废按钮
					onRowClick : function() {
						var r = this.getSelectedRecord();
						if (r == null) {
							return;
						}
						var btns = this.grid.getTopToolbar();
						var btn = btns.find("cmd", "invalid");
						btn = btn[0];
						if (r.data.FSZT == -1) {
							if (btn.getText().indexOf("取消") > -1) {
								return;
							}
							btn.setText(btn.getText().replace("注销", "取消注销"));
						} else {
							btn.setText(btn.getText().replace("取消注销", "注销"));
						}
					},
					doInvalid : function() {// 点击注销按钮
						var r = this.getSelectedRecord();
						if (r == null) {
							MyMessageTip.msg("提示", "请先选择需要注销的方式!", true);
							return;
						}
						if (r.get("BLSX") == 1) {
							MyMessageTip.msg("提示", "系统默认生成方式不容许注销!", true);
							return;
						}
						var msg = "确定取消注销流转方式 ";
						if (r.get("FSZT") == 1) {
							msg = "确定要注销流转方式 ";
						}
						Ext.MessageBox.show({
							title : '提示',
							msg : msg + "[" + r.get("FSMC") + "]吗?",
							buttons : Ext.MessageBox.YESNO,
							fn : this.messageTeate,
							icon : Ext.MessageBox.QUESTION,
							scope : this
						});
					},
					messageTeate : function(btn, text) {
						var r = this.getSelectedRecord().data;
						if (btn == "yes") {
							var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "saveDisable",
								pk : r.FSXH,
								FSZT : r.FSZT
							});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doLoadReport);
							}
						}
						if (btn == "no") {
							return;
						}
						this.refresh();
					},
					// module.opener=this;
					openModule : function(cmd, r, xy) {
						if (!r) {
							if (this.selectedNode == "[Node 1]") {
								MyMessageTip.msg("提示", "请先选择流传方式类型!", true);
								return;
							}
						}
						var module = this.midiModules[cmd]
						module.opener = this;
						if (module) {
							var win = module.getWin()
							win.setTitle(module.title)
							win.show()
							if (this.winState) {
								if (this.winState == 'center') {
									win.center();
								} else {
									xy = this.winState;
									win.setPosition(this.xy[0] || xy[0],
											this.xy[1] || xy[1])
								}
							} else {
								var default_xy = win.el.getAlignToXY(
										win.container, 'c-c');
								win.setPagePosition(default_xy[0],
										default_xy[1] - 100);
							}

							this.fireEvent("openModule", module)
							if (!win.hidden) {
								switch (cmd) {
								case "create":
									module.doNew()
									break;
								case "read":
								case "update":
									module.loadData()
								}
							}
						}
					},
					initData : function(module) {
						var node = this.tree.getSelectionModel()
								.getSelectedNode();
						module.initFormData({
							"FSXH" : module.initDataId,
							"KFXH" : this.mainApp['phis'].treasuryId,
							"DJLX" : node.attributes.djlx
						});
						// if(this.mainApp['phis'].treasuryEjkf>0){
						// module.form.getForm().findField("DJLX").getStore().loadData([["RK",
						// '入库'],
						// ["CK", '出库'],
						// ["SL", '申领'],
						// ["PD", '盘点'],
						// ["QT", '其他'],
						// ["DB", '调拨'],
						// ["DJ", '登记'],
						// ["DR", '调入']]);
						//				  														         
						// }else{
						// module.form.getForm().findField("DJLX").getStore().loadData([["RK"
						// ,"入库"],
						// ["CK" ,"出库"],
						// ["SL" ,"申领"],
						// ["ZK" ,"转科"],
						// ["BS" ,"报损"],
						// ["TK" ,"退库"],
						// ["FC" ,"封库"],
						// ["PD" ,"盘点"],
						// ["YH" ,"养护"],
						// ["CZ" ,"重置"],
						// ["YS" ,"验收"],
						// ["JH" ,"计划"],
						// ["QT" ,"其他"]]);
						// }

					}

				})
