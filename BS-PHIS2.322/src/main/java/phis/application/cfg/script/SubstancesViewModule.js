$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.SubstancesViewModule = function(cfg) {
	phis.application.cfg.script.SubstancesViewModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(phis.application.cfg.script.SubstancesViewModule,
				phis.script.SimpleModule, {
					initPanel : function() {
						if (this.panel) {
							return this.panel;
						}
						var panel = new Ext.Panel({
							border : true,
							frame : false,
							layout : 'border',
							height : 350,
							defaults : {
								border : true
							},
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'north',
								height : 210,
								items : this.getPatientList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								items : this.getDetailsList()
							} ],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
						this.panel = panel;
						return panel;
					},
					getPatientList : function() {
						this.patientList = this.createModule("patientList",
								"phis.application.cfg.CFG/CFG/CFG53010301");
						this.patientGrid = this.patientList.initPanel();
						this.patientGrid.on("rowclick", this.onListRowClick,
								this);
						this.patientList.on("loadData", this.onListLoadData,
								this);
						return this.patientGrid;
					},
					onListLoadData : function(store) {
						// 如果第一次打开页面，默认选中第一行
						if (store) {
							this.patientGrid.fireEvent("rowclick",
									this.patientGrid, 0);
						}
					},
					doRefresh : function(store) {
						this.patientList.loadData(store);
					},
					onListRowClick : function(patientGrid, rowIndex, e) {
						var r = patientGrid.store.getAt(rowIndex);
						if (!r) {
							this.detailsList.clear();
							return;
						}
						this.detailsList.loadData(r.data.WZXH, r.data.CJXH);
					},
					getDetailsList : function() {
						this.detailsList = this.createModule("detailsList",
								"phis.application.cfg.CFG/CFG/CFG53010302");
						this.detailsGrid = this.detailsList.initPanel();
						return this.detailsGrid;
					}
				});