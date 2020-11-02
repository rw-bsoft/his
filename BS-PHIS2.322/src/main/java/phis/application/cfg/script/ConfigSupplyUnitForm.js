$package("phis.application.cfg.script")
/**
 * 供货单位维护from gaof 2013.3.6
 */
$import("phis.script.SimpleForm", "phis.script.TableForm")

phis.application.cfg.script.ConfigSupplyUnitForm = function(cfg) {
	cfg.width = 600;
	cfg.modal = true;
	cfg.showButtonOnTop = false;
	cfg.entryName="phis.application.cfg.schemas.WL_GHDW_FORM";
	phis.application.cfg.script.ConfigSupplyUnitForm.superclass.constructor.apply(this,
			[ cfg ])
}
Ext.extend(phis.application.cfg.script.ConfigSupplyUnitForm, phis.script.SimpleForm,
		{
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
					labelWidth : 85, // label settings here cascade
					// unless overridden
					frame : true,
					// bodyStyle : 'padding:5px 5px 0',
					width : 600,
					autoHeight : true,
					items : [ {
						xtype : 'fieldset',
						title : '基本信息',
						// collapsible : true,
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('JBXX')
					}, {
						xtype : 'fieldset',
						title : '联系方式',
						// collapsible : true,
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('LXFS')
					} ]
				});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this);
				var schema = sc;
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
		})