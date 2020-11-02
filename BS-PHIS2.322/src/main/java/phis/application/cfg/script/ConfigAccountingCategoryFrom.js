$package("phis.application.cfg.script")

$import("phis.script.TableForm")

phis.application.cfg.script.ConfigAccountingCategoryFrom = function(cfg) {
	phis.application.cfg.script.ConfigAccountingCategoryFrom.superclass.constructor
			.apply(this, [ cfg ])
}

Ext.extend(phis.application.cfg.script.ConfigAccountingCategoryFrom,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form_items = [ {
					xtype : 'fieldset',
					title : '账簿类别信息',
					// collapsible : true,
					autoHeight : true,
					id : 'zblbxx',
					layout : 'tableform',
					layoutConfig : {
						columns : 1
					// tableAttrs : {
					// border : 0,
					// cellpadding : '2',
					// cellspacing : "2"
					// }
					},
					defaultType : 'textfield',
					items : this.getItems('ZBLB')
				}, {
					xtype : 'fieldset',
					id : 'hslbxx',
					title : '核算类别信息',
					// collapsible : true,
					autoHeight : true,
					layout : 'tableform',
					layoutConfig : {
						columns : 1
					// tableAttrs : {
					// border : 0,
					// cellpadding : '2',
					// cellspacing : "2"
					// }
					},
					defaultType : 'textfield',
					items : this.getItems('HSLB')
				} ]
				this.form = new Ext.FormPanel({
					labelWidth : 57, // label settings here cascade
					// unless overridden
					frame : true,
					autoScroll : true,
					// bodyStyle : 'padding:5px 5px 0',
					// autoHeight : true,
					// tbar : this.createButtons(),
					items : this.form_items
				});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this)
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
				this.schema = schema;
				return this.form
			},

			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for ( var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			}
		});