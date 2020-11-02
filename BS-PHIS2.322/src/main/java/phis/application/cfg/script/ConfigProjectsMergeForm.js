$package("phis.application.cfg.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.cfg.script.ConfigProjectsMergeForm = function(cfg) {
	cfg.modal = true;
	phis.application.cfg.script.ConfigProjectsMergeForm.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWindShow, this);

}
Ext.extend(phis.application.cfg.script.ConfigProjectsMergeForm,
		phis.script.TableForm, {
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide"
				if (!this.mainApp || this.closeAction == true) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
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
				this.schema = schema;
				this.form = new Ext.FormPanel({
							labelWidth : 85,
							frame : true,
							bodyStyle : 'padding:5px 5px 0',
							width : 1024,
							autoHeight : true,
							items : [{
										xtype : 'fieldset',
										title : '项目归并',
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
										listeners : {
											"afterrender" : this.onReady,
											scope : this
										},
										defaultType : 'textfield',
										items : this.getItems('XMGB')
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},
			onReady : function() {
				this.loadData();
			},
			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.getItems)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
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
			},
			expand : function() {
				this.win.center();
			},

			onWindShow : function() {
				if (this.form) {
					this.loadData();
				}
			}
		});
