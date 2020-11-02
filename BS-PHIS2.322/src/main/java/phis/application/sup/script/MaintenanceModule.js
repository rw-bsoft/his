$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.MaintenanceModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.MaintenanceModule.superclass.constructor.apply(
			this, [ cfg ]);
}
Ext.extend(phis.application.sup.script.MaintenanceModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
					border : false,
					width : this.width,
					height : this.height,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [ {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'north',
						width : 960,
						height : 255,
						items : this.getForm()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'center',
						width : 960,
						items : this.getList()
					} ],
					tbar : (this.tbar || []).concat(this.createButtons())
				});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData", this.onLoadData, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.zblb = this.zblb;
				this.list.tabid = this.tabid;
				this.list.on("setclfy", this.onSetclfy, this);
				this.list.grid = this.list.initPanel();
				return this.list.grid;
			},
			loadData : function() {
				this.form.initDataId = this.initDataId;
				this.form.oper = this.oper;
				this.form.loadData();
				if (this.initDataId) {
					this.list.requestData.cnd = [ 'eq', [ '$', 'WXXH' ],
							[ 'i', this.initDataId ] ];
					this.list.loadData();
				} else {
					this.list.clear();
					this.list.editRecords = [];
				}
			},
			onLoadData : function() {
				this.fireEvent("loadData", this);
			},
			onSetclfy : function(clf) {
				this.form.form.getForm().findField("CLFY").setValue(clf);
			}
		});