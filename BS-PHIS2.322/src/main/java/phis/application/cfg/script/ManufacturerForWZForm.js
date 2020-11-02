$package("phis.application.cfg.script")
$import("phis.script.SimpleForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cfg.script.ManufacturerForWZForm = function(cfg) {
	cfg.fldDefaultWidth = 100;
	cfg.width = 800;
	cfg.showButtonOnTop = false;
	phis.application.cfg.script.ManufacturerForWZForm.superclass.constructor
			.apply(this, [ cfg ])
	this.entryName = "phis.application.cfg.schemas.WL_SCCJ_QB";
	this.serviceId = "configManufacturerForWZService";
	this.verifiedUsingServiceAction = "operatForManufacturer";
}
Ext.extend(phis.application.cfg.script.ManufacturerForWZForm,
		phis.script.SimpleForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				this.form = new Ext.FormPanel({
					labelWidth : 60,
					frame : true,
					width : 900,
					autoHeight : true,
					items : [ {
						xtype : 'fieldset',
						title : '基本信息',
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
						items : this.getItems('LXXX')
					} ]
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