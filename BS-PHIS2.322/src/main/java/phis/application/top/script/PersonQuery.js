$package("com.bsoft.phis.pub")

$import("com.bsoft.phis.SimpleList")

com.bsoft.phis.pub.PersonQuery = function(cfg) {
	this.YGBH = "YGBH"
	com.bsoft.phis.pub.PersonQuery.superclass.constructor.apply(this, [cfg])

}
Ext.extend(com.bsoft.phis.pub.PersonQuery, com.bsoft.phis.SimpleList, {
			onDblClick : function(grid, index, e) {
				var r = this.getSelectedRecord()
				this.fireEvent("Backfill", r.data, this.win);
				/*
				 * if(this.win){ this.win.hide() }
				 */
			},

			doSave : function() {
				this.onDblClick();
			},

			onSave : function(entryName, op, json, rec) {

				var YGBH = rec[this.YGBH]
				this.requestData.cnd = ['eq', ['$', this.YGBH], ['s', YGBH]]
				this.fireEvent("save", entryName, op, json, rec);
				this.refresh()
			},
			doCndQuery : function() {
				this.initCnd = ""
				com.bsoft.phis.pub.PersonQuery.superclass.doCndQuery.call(this)
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : 'hide',
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : true
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				return win;
			},
			doCanceled : function() {
				var win = this.getWin();
				if (win)
					win.hide();
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
					if (!it.queryable) {
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
							text : '',
							iconCls : "query",
							notReadOnly : true
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			doCndQuery : function(button, e, addNavCnd) {
				var v = this.cndField.getValue()
				var ygbhcnd = ['not in',['$','YGBH'],[this.opener.ygbhsstr]];
				var pycnd = ['like', ['$', 'a.PYDM'],['s', v.toUpperCase() + '%']];
				this.requestData.cnd = ['and', pycnd, ygbhcnd];
				this.refresh()
			}
		})