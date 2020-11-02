$package("phis.application.pha.script");

$import("phis.script.EditorList", "org.ext.ux.ColumnHeaderGroup");

phis.application.pha.script.PharmacyInventoryEntryList = function(cfg) {
	// cfg.autoLoadData = false;
	this.mutiSelect = cfg.mutiSelect || true;
	this.editData = {}
	cfg.modal = true;
	phis.application.pha.script.PharmacyInventoryEntryList.superclass.constructor
			.apply(this, [cfg]);
	// this.on("beforeAddToWin", this.onWinShow, this)
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}

Ext.extend(phis.application.pha.script.PharmacyInventoryEntryList,
		phis.script.EditorList, {
			init : function() {
				this.addEvents({
							"select" : true
						})
				if (this.mutiSelect) {
					this.selectFirst = false
				}
				this.selects = {}
				this.singleSelect = {}
				phis.script.SimpleList.superclass.init.call(this)
			},
			initPanel : function(sc) {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return null;
				}
				this.pddh = ret.json.body.pddh;
				this.pdwc = ret.json.body.pdwc;
				// 判断业务锁
				var p = {};
				p.YWXH = '1010';
				p.SDXH = this.mainApp.uid + '-' + this.mainApp['phis'].pharmacyId + '-'
						+ this.pddh;
				if (!this.bclLock(p))
					return;
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
					this.sm = new Ext.grid.CheckboxSelectionModel({
								header : ""
							})
					this.sm.handleMouseDown = Ext.emptyFn;
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var group = new Ext.ux.grid.ColumnHeaderGroup({
							rows : [[{
										header : "药品基本信息",
										colspan : 7,
										align : 'center'
									}, {
										header : "药库包装录入",
										colspan : 2,
										align : 'center'
									}, {
										header : "药房包装录入",
										colspan : 2,
										align : 'center'
									}, {
										header : "药品附加信息",
										colspan : 4,
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

					plugins : this.rowExpander,// modife
					// by
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
				this.grid.on("afteredit", this.afterCellEdit, this)
				this.grid.on("beforeedit", this.beforeCellEdit, this)
				// this.sm.onEditorKey = function(field, e) {
				// alert(1)
				// var k = e.getKey(), newCell, g = sm.grid, ed =
				// g.activeEditor;
				// if(k == e.ENTER){
				// alert(1)
				// g.startEditing(ed.row+1,9);
				// }
				// // if(k==38){
				// // g.startEditing(ed.row-1,9);
				// // }
				// };
				// 回车换行
				this.grid.onEditorKey = function(field, e) {
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var ed = this.lastActiveEditor;
						if (ed.col == 9) {
							this.startEditing(ed.row + 1, 9);
						} else {
							this.startEditing(ed.row, 9);
						}
					}
				}
				this.grid.on("destroy", this.onMyDestroy, this)
				return this.grid
			},
			onMyDestroy : function() {
				var p = {};
				p.YWXH = '1010';
				p.SDXH = this.mainApp.uid + '-' + this.mainApp['phis'].pharmacyId + '-'
						+ this.pddh;
				this.bclUnlock(p);
			},

			getCM : function(items) {
				var cm = phis.application.pha.script.PharmacyInventoryEntryList.superclass.getCM
						.call(this, items)
				sm = this.sm;
				sm.on("rowselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								this.selects[record.id] = record
								record.set("LRBZ", 1);
								if (record.id in this.editData) {
									this.editData[record.id].data["LRBZ"] = 1
								} else {
									this.editData[record.id] = record
								}
							} else {
								this.singleSelect = record
							}
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								delete this.selects[record.id]
								record.set("LRBZ", 0);
								if (record.id in this.editData) {
									this.editData[record.id].data["LRBZ"] = 0
								} else {
									this.editData[record.id] = record
								}
							}
						}, this)
				// return [sm].concat(cm);
				return cm;
			},
			clearSelect : function() {
				this.selects = {};
				this.singleSelect = {};
				this.sm.clearSelections();
				this.editData = {};
			},
			getSelectedRecords : function() {
				var records = []
				if (this.mutiSelect) {
					for (var id in this.selects) {
						records.push(this.selects[id])
					}
				} else {
					records[0] = this.singleSelect
				}
				return records
			},
			loadData : function() {
				if (!this.pddh) {
					return;
				}
				var v = this.cndField.getValue()
				if (v == null || v == "") {
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.YFSB'],
									['l', this.mainApp['phis'].pharmacyId]],
							['eq', ['$', 'a.LRRY'], ['s', this.mainApp.uid]],
							['eq', ['$', 'a.PDDH'], ['i', this.pddh]]]
				} else {
					v = v.toUpperCase();
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'a.YFSB'],
									['l', this.mainApp['phis'].pharmacyId]],
							['eq', ['$', 'a.LRRY'], ['s', this.mainApp.uid]],
							['eq', ['$', 'a.PDDH'], ['i', this.pddh]],
							['like', ['$', 'b.PYDM'], ['s', v + "%"]]]
				}

				this.clear();
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
			},
			onStoreLoadData : function(store, records, ops) {
				phis.script.SimpleList.superclass.onStoreLoadData.call(this,
						store, records, ops)
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				var selRecords = []
				var count = this.store.getCount();
				for (var i = 0; i < count; i++) {
					if (this.store.getAt(i).data.LRBZ == 1) {
						if (!this.editData[this.store.getAt(i).id]
								|| this.editData[this.store.getAt(i).id].data.LRBZ == 1) {
							selRecords.push(this.store.getAt(i));
						}
					}
				}
				for (var i = 0; i < count; i++) {
					if (this.editData[this.store.getAt(i).id]) {
						this.store.getAt(i).set(
								"YKSL",
								this.editData[this.store.getAt(i).id]
										.get("YKSL"))
						this.store.getAt(i).set(
								"SPSL",
								this.editData[this.store.getAt(i).id]
										.get("SPSL"))
					}
				}
				for (var id in this.selects) {
					var r = store.getById(id)
					if (r) {
						selRecords.push(r)
					}
				}
				this.grid.getSelectionModel().selectRecords(selRecords)
			},
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if (v > 0) {
					if (record.get("LRBZ") == 0) {
						record.set("LRBZ", 1);
						if (!this.selects[record.id]) {
							this.selects[record.id] = record;
						}
						var selRecords = []
						for (var id in this.selects) {
							var r = this.store.getById(id)
							if (r) {
								selRecords.push(r)
							}
						}
						this.grid.getSelectionModel().selectRecords(selRecords);
					}
					this.editData[record.id] = record;
				}
			},
			doRemove : function() {
				var body = {};
				body["PDDH"] = this.pddh;
				Ext.Msg.show({
							title : '确认删除记录',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.grid.el.mask("正在删除...");
									var ret = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.removeActionId,
												body : body
											});
									this.grid.el.unmask();
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doRemove);
										return;
									}
									this.setButtonsState(['wc', 'save', 'add',
													'cz', 'remove'], false);
									// this.opener.closeCurrentTab()
									this.refresh();
								}
							},
							scope : this
						})
			},
			doSave : function() {
				var ed = this.grid.activeEditor;
				if (!ed) {
					ed = this.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				var body = {};
				var pd02 = new Array();
				for (var id in this.editData) {
					var d = this.editData[id].data;
					pd02.push(this.editData[id].data);
				}
				body["PD02"] = pd02;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body
						});
				this.grid.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				this.clearSelect();
				this.refresh();
			},
			doWc : function() {
				var body = {};
				body["PDDH"] = this.pddh;
				var tag = 1;// 用于标识是完成还是取消完成,1完成,0取消完成
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "wc");
				btn = btn[0];
				if (btn.getText().indexOf("取消") > -1) {
					tag = 0;
				}
				body["TAG"] = tag;
				var pd02 = new Array();
				for (var id in this.editData) {
					pd02.push(this.editData[id].data);
				}
				body["PD02"] = pd02;
				this.grid.el.mask("正在保存...");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.wcActionId,
							body : body
						});
				this.grid.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doWc);
					return;
				}
				if (tag == 0) {
					btn.setText(btn.getText().replace("取消完成", "完成"));
					this.pdwc = 0;
					this.setButtonsState(['remove', 'save'], true);
				} else {
					btn.setText(btn.getText().replace("完成", "取消完成"));
					this.pdwc = 1;
					this.setButtonsState(['remove', 'save'], false);
				}
				this.clearSelect();
				this.refresh();
			},
			onReady : function() {
				if (this.pdwc == 1) {
					this.setButtonsState(['remove', 'save'], false);
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "wc");
					btn = btn[0];
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("完成", "取消完成"));
				}
				phis.application.pha.script.PharmacyInventoryEntryList.superclass.onReady
						.call(this);
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.grid.getTopToolbar();
				if (!btns) {
					return;
				}
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
			},
			// 编辑前判断 如果完成 则不能修改. 如果药库包装和药房包装相同则不能修改药库数量
			onBeforeCellEdit : function(it, record, field, v) {
				if (this.pdwc == 1) {
					return false;
				}
				if (it.id == "YKSL") {
					if (record.get("YKBZ") == record.get("YFBZ")) {
						return false;
					}
				}
				return true;
			},
			// 重置,为了将实盘数量变成0 用于删除盘点单
			doCz : function() {
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "wc");
				btn = btn[0];
				if (btn.getText().indexOf("取消") > -1) {
					MyMessageTip.msg("提示", "已完成单子不能重置!", true);
					return;
				}
				Ext.Msg.show({
							title : "提示",
							msg : "重置将会将实盘数量全部置0 且不能取消重置,确定继续?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var ret = phis.script.rmi
											.miniJsonRequestSync({
														serviceId : this.serviceId,
														serviceAction : this.czActionId
													});
									this.grid.el.unmask();
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doWc);
										return;
									}
									this.clearSelect();
									this.refresh();
								}
							},
							scope : this
						});
			},
			// 新增
			doAdd : function() {
				this.form = this.createModule("form", this.refAddForm);
				this.form.on("save", this.onSave, this);
				this.form.on("close", this.onClose, this);
				var win = this.getWin();
				win.add(this.form.initPanel())
				win.show()
				win.center()
				if (!win.hidden) {
					this.form.doNew();
					this.form.pddh = this.pddh;
				}
			},
			// 去掉右键显示的按钮
			onContextMenu : function() {

			},
			onClose : function() {
				this.getWin().hide();
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
								text : it.alias
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
				var queryBtn = new Ext.Button({
							text : '',
							iconCls : "query",
							notReadOnly : true
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			}
		});