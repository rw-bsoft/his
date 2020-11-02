$package("chis.application.fhr.script")

$import("chis.script.BizCombinedModule2")

chis.application.fhr.script.FamilyServiceRecordModule = function(cfg) {
	cfg.aotuLoadData = false;
	cfg.itemWidth = 200;
	cfg.height = 400;
	cfg.itemCollapsible = false
	chis.application.fhr.script.FamilyServiceRecordModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.fhr.script.FamilyServiceRecordModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.fhr.script.FamilyServiceRecordModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list1 = this.midiModules[this.actions[0].id];
				this.list1.on("firstRowSelected", this.onRowClick, this);
				this.grid1 = this.list1.grid;
				this.grid1.on("rowclick", this.onRowClick, this)
				this.list2 = this.midiModules[this.actions[1].id];
				this.list2.father = this;
				return panel;
			},
			getEmpiId : function() {
				var r = this.list1.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("empiId");
				return empiId;
			},
			refreshList : function() {
				this.loadGridData();
			},
			loadData : function() {
				this.list1.requestData.cnd = [
						'and',
						['eq', ['$', 'a.familyId'],
								['s', this.exContext.args.initDataId]],
						['eq', ['$', 'a.status'], ['s', '0']]]
				this.list2.familyId = this.exContext.args.initDataId;
				this.list1.loadData();
			},
			onRowClick : function(grid, index, e) {
				var r = this.list1.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("empiId");
				this.list2.requestData.cnd = ['eq', ['$', 'empiId'],
						['s', empiId]]
				this.list2.empiId = empiId;
				this.list2.loadData();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
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
								minimizable : true,
								maximizable : true,
								shadow : false,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			}
		});