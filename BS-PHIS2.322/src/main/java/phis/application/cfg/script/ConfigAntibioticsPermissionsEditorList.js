$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ConfigAntibioticsPermissionsEditorList = function(
		cfg) {
	cfg.modal = true;
	this.xyyp = [];
	phis.application.cfg.script.ConfigAntibioticsPermissionsEditorList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cfg.script.ConfigAntibioticsPermissionsEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.cfg.script.ConfigAntibioticsPermissionsEditorList.superclass.initPanel
						.call(this, sc)
				var this1 = this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey();
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						if (this1.xyyp.length == 0) {
							var store = this1.grid.getStore();
							var n = store.getCount()
							if (n > 0) {
								var body = {};
								var data = []
								for (var i = 0; i < n; i++) {
									var r = store.getAt(i)
									this1.xyyp.push(r);
								}
							}
						}
						this1.doCreate();
						return;
					}
					this.selModel.onEditorKey(field, e);
				}
			},
			doCreate : function(item, e) {
				var store = this.grid.getStore();
				if (this.xyyp.length == 0) {
					var n = store.getCount()
					if (n > 0) {
						for (var i = 0; i < n; i++) {
							var r = store.getAt(i)
							this.xyyp.push(r);
						}
					}
				}
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var data = {
					'_opStatus' : 'create'
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var v = null
					if (it.defaultValue) {
						v = it.defaultValue
						data[it.id] = v
						var dic = it.dic
						if (dic) {
							data[it.id] = v.key;
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v.key];
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
					if (it.type && it.type == "int") {
						data[it.id] = (data[it.id] == "0" || data[it.id] == "" || data[it.id] == undefined)
								? 0
								: parseInt(data[it.id]);
					}
				}
				var r = new Record(data)
				store.insert(0, [r]);
				this.grid.getView().refresh()// 刷新行号
				this.grid.startEditing(0, 1);
			},
			onReady : function() {
				this.requestData.cnd = ["eq", ["$", "YSGH"],
						["s", this.initDataId]];
				this.pagingToolbar.on("beforechange", this.beforeStorechange,
						this);
				this.loadData();
			},
			beforeStorechange : function(pagingToolbar, o) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var save = false;
				if (this.xyyp.length != n && this.xyyp.length != 0) {
					save = true;
				}
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						save = true;
						break;
					}
				}
				if (save) {
					Ext.Msg.show({
								title : '提示',
								msg : '是否保存已作的修改?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										if (this.doSave()) {
											this.xyyp = [];
											pagingToolbar.store.load({
														params : o
													});
										}
									} else {
										this.xyyp = [];
										pagingToolbar.store.load({
													params : o
												});
									}
								},
								scope : this
							})
					return false;
				} else {
					var r = this.getSelectedRecord()
					var n = this.store.indexOf(r)
					if (n > -1) {
						this.selectedIndex = n
					}
					return true;
				}
				return false;
			},
			createRemoteDicField : function(dic) {
				var mds_reader = new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPXH'
								}, {
									name : 'YPMC'

								}, {
									name : 'YFGG'
								}, {
									name : 'YPDW'
								}]);
				// store远程url
				var remoteurl = ClassLoader.appRootOffsetPath + 'MedicineAntibiotic.search';
				var mdsstore = new Ext.data.Store({
							url : remoteurl,
							reader : mds_reader
						});
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr><td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><tr>',
						'</table>', '</div>', '</tpl>');
				// var resultTpl = new Ext.XTemplate('<tpl for=".">'
				// + '<div class="search-item">'
				// + '<span style="line-height:25px;color:#A52A2A;"><font
				// style="color:#4B0082;font-weight:bold;">{numKey}</font>.<font
				// style="color:#000000;font-weight:bold;" >{YPMC}</font>
				// {YFGG}{YPDW}</span>'
				// + '</div>' + '</tpl>');
				var my_ctx = this;
				var remoteField = new Ext.form.ComboBox({
							id : "KSSYP",
							width : 280,
							name : "抗生素药品",
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							minListWidth : 250,
							minHeight : 400,
							tpl : resultTpl,
							minChars : 2,
							enableKeyEvents : true,
							hiddenName : "YPXH",
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override default
								// onSelect to do
								my_ctx.setBackInfo(this, record);
							}
						});
				remoteField.on("keydown", function(obj, e) {
							var key = e.getKey();
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								if (key == 48 || key == 96)
									key = key + 10;
								key = key < 59 ? key - 49 : key - 97;
								var record = this.getStore().getAt(key);
								my_ctx.setBackInfo(obj, record);
							}
						})
				return remoteField
			},
			onWinShow : function() {
				if (this.grid) {
					this.requestData.cnd = ["eq", ["$", "YSGH"],
							["s", this.initDataId]];;
					this.loadData();
				}
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			doSave : function(item, e) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					r.set('YSGH', this.initDataId);
					data.push(r.data)
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionSave,
							schema : this.entryName,
							body : data
						}, function(code, msg, json) {
							this.grid.el.unmask()
							this.xyyp = [];
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.loadData();
						}, this)
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("YPXH") == record.get("YPXH")) {
						MyMessageTip.msg("提示", "\"" + record.get("YPMC")
										+ "\"在这组中已存在，请进行修改！", true);
						return;
					}
				}
				obj.collapse();
				obj.triggerBlur();
				rowItem.set('YPXH', record.get("YPXH"));
				rowItem.set('YFGG', record.get("YFGG"));
				obj.setValue(record.get("YPMC"));
			},
			doRemove : function() {
				var body = {};
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord();
				if (!r)
					return;
				body['SBXH'] = r.get("SBXH");
				if (!r.get("SBXH")) {
					this.store.remove(r);
					var count = this.store.getCount();// 移除之后焦点定位
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					}
					this.grid.getView().refresh();
					this.grid.startEditing(0, 1);
					return;
				}
				if (r == null) {
					return
				}
				Ext.Msg.show({
					title : '确认删除记录[' + r.get("YPMC") + ']',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.grid.el.mask("正在删除数据...", "x-mask-loading")
							phis.script.rmi.jsonRequest({
										serviceId : this.serviceId,
										serviceAction : "removeAntibioticsPermissions",
										schema : this.entryName,
										pkey : r.get("SBXH")
									}, function(code, msg, json) {
										this.grid.el.unmask()
										if (code >= 300) {
											this.processReturnMsg(code, msg);
											return;
										}
										this.store.remove(r);
										var count = this.store.getCount();// 移除之后焦点定位
										if (count > 0) {
											cm.select(cell[0] < count
															? cell[0]
															: (count - 1),
													cell[1]);
										}
										this.grid.getView().refresh();
										this.grid.startEditing(0, 1);
									}, this)
						}
					},
					scope : this
				})
			}
		});
