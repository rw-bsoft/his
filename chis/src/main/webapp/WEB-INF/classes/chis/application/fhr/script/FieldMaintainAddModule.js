$package("chis.application.fhr.script")

$import("chis.script.BizCombinedModule2")

chis.application.fhr.script.FieldMaintainAddModule = function(cfg) {
	chis.application.fhr.script.FieldMaintainAddModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north";
	this.itemCollapsible = false;
	this.width = 800;
	this.height = 560;
	this.itemHeight = 120;
	this.exContext = {};
	this.on("winShow", this.onWinShow, this);

}

Ext.extend(chis.application.fhr.script.FieldMaintainAddModule, chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.fhr.script.FieldMaintainAddModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("save", this.onSave, this);
				this.form.on("select", this.onSelect, this);
				this.form.on("cancel", this.onCancel, this);
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("refresh", this.onRefresh, this);
				this.list.on("cancel", this.onCancel, this);
				this.grid = this.list.grid;
				return panel;
			},

			onRefresh : function() {
				this.fireEvent("refresh", this);
			},

			onCancel : function() {
				this.fireEvent("save", this);
				if (this.win) {
					this.win.hide();
				}
			},

			onSelect : function(cnd) {
				cnd.push(this.masterplateId)
				this.list.requestData.cnd = cnd;
				this.list.requestData.serviceId = "chis.templateService";
				this.list.requestData.serviceAction = "listSelectField";
				this.list.refresh();
				this.list.clearSelect();
			},

			onWinShow : function() {
				this.form.masterplateId = this.masterplateId;
				this.list.masterplateId = this.masterplateId;
				this.form.doCreate();
				var cnd = []
				cnd.push(this.masterplateId)
				this.list.requestData.cnd = cnd;
				this.list.requestData.serviceId = "chis.templateService";
				this.list.requestData.serviceAction = "listSelectField";
				this.list.refresh();
				this.list.clearSelect();
			},

			onSave : function(entryName, op, json, data) {
				this.form.masterplateId = this.masterplateId;
				this.list.masterplateId = this.masterplateId;
				var cnd = []
				cnd.push(this.masterplateId)
				this.list.requestData.cnd = cnd;
				this.list.requestData.serviceId = "chis.templateService";
				this.list.requestData.serviceAction = "listSelectField";
				this.grid.enable();
				this.list.refresh();
				this.fireEvent("save", entryName, op, json, data);
			},

			getWin : function() {
				var win = this.win;
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								constrain : true,
								layout : "fit",
								plain : true,
								autoScroll : true,
								minimizable : false,
								maximizable : false,
								shadow : false,
								buttonAlign : 'center',
								modal : true,
								items : this.initPanel()
							});
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
				return win
			}

		})