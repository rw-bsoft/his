$package("phis.application.cfg.script")

$import("app.modules.list.TreeNavListView")

phis.application.cfg.script.ConfigSystemParamsList = function(cfg) {
	cfg.gridCreator = Ext.grid.GridPanel
	cfg.listServiceId = "phis.paramterQuery"
	cfg.removeServiceId = "phis.systemParamsRemove"
	cfg.navDic = "phis.dictionary.businessType"
	cfg.navField = "SSLB"
	cfg.rootVisible = true;
	cfg.navParentKey = ""
	cfg.navParentText = "全部"
	cfg.autoLoadData = false;
	 cfg.pageSize=100;
	phis.application.cfg.script.ConfigSystemParamsList.superclass.constructor
			.apply(this, [cfg])
	this.on("remove", this.afterRemove, this);
}
Ext.extend(phis.application.cfg.script.ConfigSystemParamsList,
		app.modules.list.TreeNavListView, {
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
										title : this.treeTitle,
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
				this.tree.getLoader().on("load", this.onTreeLoad, this);
				// this.warpPanel = panel
				tf.tree.expand()
				return panel
			},
			onTreeLoad : function(loader, node) {
				if (node) {
					if (!this.select) {
						node.select();
						this.select = true;
						this.onCatalogChanage(node, this);
					}
					if (node.getDepth() == 0) {
						if (node.hasChildNodes()) {
							var untypeNode = new Ext.tree.TreeNode({
										id : "00",
										text : '未分类参数',
										leaf : true
									})
							node.insertBefore(untypeNode, node.firstChild);
						}
					}
				}
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var title = r.get("JGID_text") + "-" + r.get("CSMC");
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
			},
			afterRemove : function() {
				this.refresh()
			},
			onCatalogChanage : function(node, e) {
				var navField = this.navField
				var initCnd = this.initCnd
				var queryCnd = this.queryCnd

				var cnd;
				if (node.leaf) {
					if (node.id == '00') {
						cnd = ['isNull', ['$', navField]]
					} else {
						cnd = ['like', ['$', navField],
								['s', '%' + node.id + '%']]
					}
				} else {
					if (node.attributes.root) {
						cnd = []
					} else {
						cnd = ['like', ['$', navField],
								['s', '%' + node.id + '%']]
					}
				}

				this.navCnd = cnd
				if (initCnd || queryCnd) {
					if(cnd.length){
						cnd = ['and', cnd]
						if (initCnd) {
							cnd.push(initCnd)
						}
						if (queryCnd) {
							cnd.push(queryCnd)
						}
					}else{
						if (initCnd) {
							cnd = nitCnd
							if (queryCnd) {
								cnd.push(queryCnd)
							}
						}else{
							if (queryCnd) {
								cnd = queryCnd
							}
						}
						
					}
				}
				this.resetFirstPage()
				this.requestData.cnd = cnd
				this.refresh()
			},
			expansion : function(cfg) {
				$import("org.ext.ux.grid.RowExpander");
				var rowExpander = new Ext.ux.grid.RowExpander({
					tpl : new Ext.Template(
							// 样式暂时先这么写,需要改的话再设计
							'<p><b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp详细说明:</b>{XXSM}</p>')
				})
				cfg.plugins = rowExpander;
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable || it.queryable == 'false') {
						continue
					}
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias,
								item : it
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 100
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
					defaultItem = fields[0].item;
				}

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 150
						cndField = this.createDicField(defaultItem.dic)
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 150,
								selectOnFocus : true,
								name : "dftcndfld"
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				var queryBtn = new Ext.Toolbar.SplitButton({
							text : '',
							iconCls : "query",
							notReadOnly : true, // ** add by yzh **
							menu : new Ext.menu.Menu({
										items : {
											text : "高级查询",
											iconCls : "common_query",
											handler : this.doAdvancedQuery,
											scope : this
										}
									})
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
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
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM"
									|| it.id == "CSMC") {
								v = v.toUpperCase();
							}
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
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			}
		})