$package("phis.application.stm.script")

$import("phis.script.SimpleList")

phis.application.stm.script.ClinicSkinTestResultList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.initCnd = ['and', ['eq', ['$', 'a.WCBZ'], ['i', 1]],
			['eq', ['$', 'a.JGID'], ['$', '%user.manageUnit.id']]];
	phis.application.stm.script.ClinicSkinTestResultList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.stm.script.ClinicSkinTestResultList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.stm.script.ClinicSkinTestResultList.superclass.onReady
						.call(this);
				this.doCndQuery();
			},
			expansion : function(cfg) {
				var label1 = new Ext.form.Label({
							text : "皮试时间"
						});
				var serverDate = Date.getServerDate();
				this.dateFieldks = new Ext.ux.form.Spinner({
							name : 'storeDate',
							value : serverDate,
							strategy : {
								xtype : "date"
							}
						});
				var label2 = new Ext.form.Label({
							text : "-至-"
						});
				this.dateFieldjs = new Ext.ux.form.Spinner({
							name : 'storeDate',
							value : serverDate,
							strategy : {
								xtype : "date"
							}
						});
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([label1, '-', this.dateFieldks, label2,
						this.dateFieldjs, '-', tbar]);
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
				var queryBtn = new Ext.Toolbar.Button({
							text : '查询',
							iconCls : "query",
							notReadOnly : true
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			onCndFieldSelect : function(item, record, e) {
				var tbar = this.grid.getTopToolbar()
				var tbarItems = tbar.items.items
				var itid = record.data.value
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				var field = this.cndField
				// field.destroy()
				field.hide();
				var f = this.midiComponents[it.id]
				if (!f) {
					if (it.dic) {
						it.dic.src = this.entryName + "." + it.id
						it.dic.defaultValue = it.defaultValue
						it.dic.width = 150
						f = this.createDicField(it.dic)
					} else {
						f = this.createNormalField(it)
					}
					f.on("specialkey", this.onQueryFieldEnter, this)
					this.midiComponents[it.id] = f
				} else {
					f.on("specialkey", this.onQueryFieldEnter, this)
					// f.rendered = false
					f.show();
				}
				this.cndField = f
				tbarItems[8] = f
				tbar.doLayout()
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				var kssj = this.mainApp.serverDate;
				if (this.dateFieldks) {
					kssj = this.dateFieldks.getValue();
				};
				var jssj = this.mainApp.serverDate;
				if (this.dateFieldjs) {
					jssj = this.dateFieldjs.getValue();;
				};
				var dateCnd = ['and',
						['ge', ['$', "str(a.PSSJ,'yyyy-MM-dd')"], ['s', kssj]],
						['le', ['$', "str(a.PSSJ,'yyyy-MM-dd')"], ['s', jssj]]]
				initCnd = ['and', initCnd, dateCnd];
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
				// 替换'，解决拼sql语句查询的时候报错
				if (typeof v == 'string') {
					v = (v + "").replace(/'/g, "''")
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
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
						case "datetime" :
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
		});