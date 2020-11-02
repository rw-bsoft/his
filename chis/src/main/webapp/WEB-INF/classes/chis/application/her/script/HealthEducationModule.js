$package("chis.application.her.script");

$import("chis.script.BizTabModule");

chis.application.her.script.HealthEducationModule = function(cfg) {
	chis.application.her.script.HealthEducationModule.superclass.constructor
			.apply(this, [cfg]);
	this.width = 750;
	this.height = 482;
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.her.script.HealthEducationModule,
		chis.script.BizTabModule, {
			initPanel : function() {
				var tab = chis.application.her.script.HealthEducationModule.superclass.initPanel
						.call(this);
				this.tab = tab;
				return this.tab;
			},

			onLoadModule : function(moduleName, module) {
				if (moduleName == this.actions[0].id) {
					this.HERCForm = module;
					Ext.apply(this.HERCForm.exContext, this.exContext);
					this.HERCForm.on("save", this.onSaveHERCForm, this);
				}
				if (moduleName == this.actions[1].id) {
					this.HERRList = module;
					Ext.apply(this.HERRList.exContext, this.exContext);
					this.HERRList.loadData();
				}
			},

			onSaveHERCForm : function(entryName, op, json, data) {
				this.exContext.args.recordId = json.body.recordId;
				this.setTabItemDisabled(1, false);
				if (this.HERRList) {
					Ext.apply(this.HERRList.exContext, this.exContext);
				}
				this.fireEvent("save", entryName, op, json, data);
			},

			doNew : function() {
				this.exContext.args.recordId = null;
				if (!this.HERCForm) {
					this.activeModule(0);
				} else {
					this.HERCForm.doCreate();
					this.activeModule(0);
				}
				this.setTabItemDisabled(1, true);
				if (this.HERRList) {
					Ext.apply(this.HERRList.exContext, this.exContext);
					// this.HERRList.loadData();
				}
			},

			loadData : function() {
				this.activeModule(0);
				if (this.HERCForm) {
					this.HERCForm.initDataId = this.exContext.args.recordId;
					this.HERCForm.loadData();
				}
				this.setTabItemDisabled(1, false);
				if (this.HERRList) {
					Ext.apply(this.HERRList.exContext, this.exContext);
					this.HERRList.loadData();
				}
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title || this.name,
								width : this.width,
								height : this.height,
								constrain : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || false,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				win.center();
				return win;
			}

		});