$package("chis.application.her.script")
$import("chis.script.BizSimpleListView", "util.dictionary.TreeDicFactory")

chis.application.her.script.PlanSetRecord = function(cfg) {
	console.log(cfg.actions)
	if(cfg.actions==undefined)
	{
		cfg.actions = [{
				id : "create",
				name : "新建",
				ref : "chis.application.her.HER/HER/HE01_01"
			},{
				id : "update",
				name : "查看",
				ref : "chis.application.her.HER/HER/HE01_01"
			}
			]
	}
	cfg.mutiSelect = true;
	cfg.gridDDGroup = 'gridDDGroup';
	cfg.showNav = true;
	cfg.westWidth = 150;
	cfg.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.her.script.PlanSetRecord.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.her.script.PlanSetRecord, chis.script.BizSimpleListView, {
			warpPanel : function(grid) {
				if (!this.showNav) {
					return grid
				}
				var navDic = this.navDic
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : navDic,
							parentKey : this.navParentKey,
							parentText : this.navParentText,
							lengthLimit : "12",
							rootVisible : this.rootVisible || false
						})
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
										title : '',
										region : 'west',
										width : this.westWidth,
										items : tf.tree
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : grid
									}]
						});
				this.tree = tf.tree
				grid.__this = this
				tf.tree.on("click", this.onCatalogChanage, this)
				// this.warpPanel = panel
				tf.tree.expand()
				return panel
			},

			onCatalogChanage : function(node, e) {
				var navField = this.navField
				var initCnd = this.initCnd
				var queryCnd = this.queryCnd

				var cnd;
				if (node.leaf) {
					cnd = ['eq', ['$', navField], ['s', node.id]]
				} else {
					cnd = ['like', ['$', navField], ['s', node.id + '%']]
				}

				this.navCnd = cnd
				if (initCnd || queryCnd) {
					cnd = ['and', cnd]
					if (initCnd) {
						cnd.push(initCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.resetFirstPage()
				this.requestData.cnd = cnd
				this.refresh()
			},

			doCndQuery : function() {
				var initCnd = this.initCnd
				var navCnd = this.navCnd
						|| [
								'like',
								['$', this.navField],
								['concat', ['$', '%user.manageUnit.id'],
										['s', '%']]]
				var index = this.cndFldCombox.getValue()
				var items = this.schema.items
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == index) {
						it = items[i]
						break
					}
				}
				if (!it) {
					return;
				}
				var v = this.cndField.getValue()
				this.requestData.pageNo = 1
				var pt = this.grid.getBottomToolbar()
				if (pt) {
					pt.cursor = 0;
				}
				if (v == null || v == "") {
					var cnd = [];

					this.queryCnd = null;
					if (navCnd) {
						if (initCnd) {
							cnd.push("and")
							cnd.push(navCnd)
							cnd.push(initCnd)
						} else {
							cnd = navCnd
						}
					}
					this.requestData.cnd = cnd
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode
						if (!node.isLeaf()) {
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
						} else {
							cnd.push(['s', v])
						}
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd || navCnd) {
					cnd = ['and', cnd]
					if (initCnd) {
						cnd.push(initCnd)
					}
					if (navCnd) {
						cnd.push(navCnd)
					}
				}
				this.requestData.cnd = cnd
				this.refresh()
			},

			onTreeNotifyDrop : function(dd, e, data) {
				var n = this.getTargetFromEvent(e);
				var r = dd.dragData.selections[0];
				var node = n.node
				var ctx = dd.grid.__this

				if (!node.leaf || node.id == r.data[ctx.navField]) {
					return false
				}
				var updateData = {}
				updateData[ctx.schema.pkey] = r.id
				updateData[ctx.navField] = node.attributes.key
				ctx.saveToServer(updateData, r)
				// node.expand()
			},

			addPanelToWin : function() {
				if (!this.fireEvent("panelInit", this.grid)) {
					return;
				};
				var win = this.getWin();
				win.add(this.warpPanel(this.grid))
				win.doLayout()
			},

			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}
				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.statusPer",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});

				comb.on("select", this.radioChanged, this)
				comb.setValue("01")
				comb.setWidth(80)
				cfg.items = ["状态", "-", comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},

			radioChanged : function(r) {
				var status = r.getValue()
				var navCnd = this.navCnd
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.status'], ['s', status]]
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}

				var bts = this.grid.getTopToolbar().items;
				var btn = bts.items[7];
				if (btn) {
					if (status != "0") {
						btn.disable();
					} else {
						btn.enable();
					}
				}

				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},

			doLogOut : function(item, e) {
				var cmd = item.cmd;
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var count = this.queryCount(r);
				var message = "";
				if (count > 0) {
					message = '该计划已经执行过[' + count + ']笔任务,'
				}
				Ext.Msg.show({
							title : '确认作废健康教育记录[' + r.id + ']',
							msg : message + '记录作废后将无法操作,是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									this.logOutRecord(r.data);
								}
							},
							scope : this
						})
			},

			logOutRecord : function(saveData) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				this.saving = true
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.logOutAction,
							method:"execute",
							body : saveData
						}, function(code, msg, json) {
							this.grid.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.logOutRecord, [saveData]);
								return
							} else {
								this.store.remove(r);
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.fireEvent("save", this.entryName,
										"update", json, this.data)
							}
						}, this)
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var count = this.queryCount(r);
				if (r.get("status") == '1') {
					Ext.Msg.show({
								title : '提示!',
								msg : '计划编号:[' + r.id + ']已经作废，不能删除!',
								modal : true,
								width : 400,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							})
					return;
				}
				if (count > 0) {
					Ext.Msg.show({
								title : '提示!',
								msg : '计划编号:[' + r.id + ']已经执行过[' + count
										+ ']笔任务不能删除,如需关闭请走作废流程!',
								modal : true,
								width : 400,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							})
					return;
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.id + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},

			queryCount : function(r) {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.queryAction,
							method:"execute",
							body : {
								"setId" : r.id
							}
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				return result.json.count;
			}
		})