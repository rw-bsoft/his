$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.CredentialsMsgModule = function(cfg) {
	phis.application.cfg.script.CredentialsMsgModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.cfg.script.CredentialsMsgModule,
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
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 150,
										items : this.getPatientList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getDetailsList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getPatientList : function() {
				this.patientList = this.createModule("patientList","phis.application.cfg.CFG/CFG/CFG53010401");
				this.patientGrid = this.patientList.initPanel();
				this.patientGrid.on("rowclick", this.onListRowClick, this);
				this.patientList.on("loadData", this.onListLoadData, this);
				return this.patientGrid;
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认选中第一行
				if (store) {
					this.patientGrid.fireEvent("rowclick",this.patientGrid, 0);
				}
			},
			onListRowClick : function(patientGrid, rowIndex, e) {
				var r = patientGrid.store.getAt(rowIndex);
				if (!r) {
					return;
				}
				this.detailsList.ZJXH = r.data.ZJXH;
				this.detailsList.SCCJ = r.data.DXXH;
				this.detailsList.loadData(r.data.DXXH);
				//this.detailsList.clearSelect();
			},
			doRefresh : function(store){
				this.patientList.requestData.cnd = ['eq',['$','DXXH'],['i',store]];
				this.patientList.loadData();
				this.detailsList.CJXH = store;
				this.CJXH = store;
				this.detailsList.loadData(store);
			},
			getDetailsList : function() {
				this.detailsList = this.createModule("detailsList","phis.application.cfg.CFG/CFG/CFG53010402");
				this.detailsGrid = this.detailsList.initPanel();
				return this.detailsGrid;
			}
		});