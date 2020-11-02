$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRMedicalRecordTemplatesManageModule = function(
		cfg) {
	cfg.exContext = {};
	phis.application.emr.script.EMRMedicalRecordTemplatesManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordTemplatesManageModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										region : 'west',
										// height : 200,
										width : 200,
										items : this.getBllbTree()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getBlmbAndGrmbList()
									}, {
										layout : "fit",
										region : 'east',
										width : 0,
										items : this.getActiveXPanel()
									}]
						});
				this.panel = panel;
				// this.panel.on("beforeclose", this.onBeforeclose, this);
				// this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			// onReady : function() {
			// this.list.grid.getColumnModel().setHidden(2, false);
			// this.list.grid.getColumnModel().setHidden(1, true);
			// },
			getBllbTree : function() {
				var module = this.createModule("refBllbTree", this.refBllbTree);
				module.node = this.node;
				module.on("treeClick", this.onBeforeTreeClick, this);
				this.tree = module;
				var tree = module.initPanel();
				this.treePanel = tree;
				return tree;
			},
			getBlmbAndGrmbList : function() {
				var module = this.createModule("refBlmbList", this.refBlmbList);
				// module.on("listRowClick", this.onListRowClick, this);
				// module.on("afterCellEdit", this.onAfterCellEdit, this);
				this.list = module;
				module.opener = this;
				var list = module.initPanel();
				this.grid = list;
				return list;
			},
			getActiveXPanel : function() {
				var ocxStr = ""
				if (Ext.isIE) {
					ocxStr = "<div style='display:none'><OBJECT id='emrOcx_Personal' name='emrOcx_Personal' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'></OBJECT></div>"
				} else {
					ocxStr = "<div><OBJECT id='emrOcx_Personal' TYPE='application/x-itst-activex' WIDTH='0' HEIGHT='0' clsid='{FFAA1970-287B-4359-93B5-644F6C8190BB}'></OBJECT></div>"
				}
				var panel = new Ext.Panel({
							frame : true,
							border : false,
							html : ocxStr
						});
				return panel;
			},
			onBeforeTreeClick : function(node) {
				this.onTreeClick(node);
			},
			onTreeClick : function(node) {
				// console.debug(node)
				if (node.id == 'root') {
					this.list.SJLBBH = null;
					this.list.requestData.cnd = [];
					this.list.initCnd = [];
				} else {
					this.list.SJLBBH = node.attributes.attributes.SJLBBH;
					this.list.requestData.cnd = ['or',
							['eq', ['$', 'SJLBBH'], ['s', node.id]],
							['eq', ['$', 'LBBH'], ['s', node.id]]];
					this.list.initCnd = ['or',
							['eq', ['$', 'SJLBBH'], ['s', node.id]],
							['eq', ['$', 'LBBH'], ['s', node.id]]];
				}
				this.list.doRefresh();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
			doReload : function() {
				this.tree.tree.root.reload();
			}
		});
