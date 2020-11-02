$package("chis.application.div.script");

$import("chis.script.BizTreeNavListView");

chis.application.div.script.ConfirmIndexDataList = function(cfg) {
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.listServiceId = "chis.masterplateMaintainService";
	cfg.serverParams = {
		serviceAction : "listMasterplate"
	}
	cfg.removeServiceId = "chis.masterplateMaintainService";
	cfg.removeAction = "removeMasterplateDate";
	chis.application.div.script.ConfirmIndexDataList.superclass.constructor
			.apply(this, [cfg]);
	this.navField = "masterplateId";

};

Ext.extend(chis.application.div.script.ConfirmIndexDataList,
		chis.script.BizTreeNavListView, {
			warpPanel : function(grid) {
				if (!this.showNav) {
					return grid;
				}
				var tf = util.dictionary.TreeDicFactory.createDic({
							dropConfig : {
								ddGroup : 'gridDDGroup',
								notifyDrop : this.onTreeNotifyDrop,
								scope : this
							},
							id : "chis.dictionary.confirmeIndex"
						});
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
				this.tree = tf.tree;
				grid.__this = this;
				tf.tree.on("click", this.onCatalogChanage, this);
				tf.tree.expand();
				return panel;
			},

			onCatalogChanage : function(node, e) {
				var navField = this.navField;
				var initCnd = this.initCnd;
				var queryCnd = null;
				this.cndFldCombox.setValue();
				this.cndField.setValue();
				var cnd;
				if (node.leaf) {
					cnd = [
							'and',
							['eq', ['$', 'masterplateId'], ['s', node.id]],
							['like', ['$', 'a.manaUnitId'],
									['s', this.mainApp.deptId+"%"]]];
					this.masterplateId = node.id;
					this.dateType = node.attributes.dateType;
					var store = this.getChangedStore();
					this.cndFldCombox.bindStore(store);
				} else {
					cnd = [
							'and',
							['like', ['$', 'masterplateTypezb'],
									['s', node.id + '%']],
							['like', ['$', 'a.manaUnitId'],
									['s', this.mainApp.deptId+"%"]]];;
					this.masterplateId = null;
					this.dateType = null;
				}

				this.navCnd = cnd;
				if (initCnd || queryCnd) {
					cnd = ['and', cnd];
					if (initCnd) {
						cnd.push(initCnd);
					}
					if (queryCnd) {
						cnd.push(queryCnd);
					}
				}
				this.resetFirstPage();
				this.requestData.cnd = cnd;
				this.requestData.serviceAction = "listMasterplate";
				this.refresh();
			},
			fixRemoveCfg : function(removeCfg) {
				removeCfg.removeServiceId = "chis.masterplateMaintainService";
				removeCfg.serviceAction = "removeMasterplateDate";
			},
			openModule : function(cmd, r, xy) {
				var module = this.midiModules[cmd]
				if (module) {
					if (cmd == "create" && !this.masterplateId) {
						Ext.Msg.alert("提示", "请选择模版！");
						return;
					}
					if (this.masterplateId) {
						module.masterplateId = this.masterplateId;
						module.dateType = this.dateType;
					} else if (r) {
						module.masterplateId = r.data.masterplateId;
						module.dateType = r.data.dateType;
					}
					var win = module.getWin()
					if (xy) {
						win.setPosition(xy[0] || this.xy[0], xy[1]
										|| this.xy[1])
					}
					win.setTitle(module.title)
					win.show()
					this.fireEvent("openModule", module)
					if (!win.hidden) {
						switch (cmd) {
							case "create" :
								module.doCreate()
								break;
							case "read" :
							case "update" :
								module.loadData()
						}
					}
				}
			},
			getChangedStore : function() {
				var items = this.schema.items;
				var fields = [];
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!(it.queryable)) {
						continue
					}
					if (this.dateType == "1"
							&& (it.id == "season" || it.id == "month")) {
						continue
					}
					if (this.dateType == "2" && it.id == "month") {
						continue
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				return store;
			},
			doCndQuery : function() {
				var initCnd = this.initCnd
				var navCnd = this.navCnd
				var index = this.cndFldCombox.getValue()
				var it;
				for (var i = 0; i < this.schema.items.length; i++) {
					if (this.schema.items[i].id == index) {
						it = this.schema.items[i]
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
						if (node && !node.isLeaf()) {
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
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								v = v.format("Y-m-d")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								v = v.format("Y-m-d H:i:s")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH:mm:ss')"]
								cnd.push(['s', v])
							}
							break;
					}
				}
				if (it.id == "years") {
					cnd = ['eq',
							['$', "str(" + refAlias + ".dateValue,'yyyy')"],
							['s', v]]
				} else if (it.id == "season") {
					var values = []
					if (v == 1) {
						values = ['01', '02', '03'];
					} else if (v == 2) {
						values = ['04', '05', '06'];
					} else if (v == 3) {
						values = ['07', '08', '09'];
					} else if (v == 4) {
						values = ['10', '11', '12'];
					}
					cnd = ['in', ['$', "str(" + refAlias + ".dateValue,'MM')"],
							values]
				} else if (it.id == "month") {
					cnd = ['eq', ['$', "str(" + refAlias + ".dateValue,'MM')"],
							['s', v]]
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
			}
			,
		});