$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.MedicalHistoryModule = function(cfg) {
	cfg.modal = true;
	phis.application.cic.script.MedicalHistoryModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cic.script.MedicalHistoryModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 340,
										items : this.getMedicalTemplateList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getMedicalDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.medicalTemplateGrid.fireEvent("rowclick",
								this.medicalTemplateGrid, 0);
					}
				} else {
					this.medicalDetailList.doNew();
					this.medicalDetailList.initDataId = "";
				}
			},
			onListRowClick : function(projectComGrid, rowIndex, e) {
				var r = projectComGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.medicalDetailList.initDataId = r.id;
				this.medicalDetailList.loadData();
			},
			getMedicalTemplateList : function() {
				this.medicalTemplateList = this.createModule(
						"getMedicalHistoryList", this.refMedicalHistoryList);
				this.medicalTemplateList.opener = this.opener;
				this.medicalTemplateList.openermodule = this;
				this.medicalTemplateList.initCnd = ['eq', ['$', 'QYBZ'],
						['d', 1]];
				this.medicalTemplateList.requestData.cnd = ['eq',
						['$', 'QYBZ'], ['d', 1]];
				this.medicalTemplateList.loadData();
				this.medicalTemplateGrid = this.medicalTemplateList.initPanel();
				this.medicalTemplateList.on("loadData", this.onListLoadData,
						this);
				this.medicalTemplateGrid.on("loadData", this.onListLoadData,
						this);
				this.medicalTemplateGrid.on("rowClick", this.onListRowClick,
						this);
				return this.medicalTemplateGrid;
			},

			getMedicalDetailList : function() {
				this.medicalDetailList = this.createModule(
						"getMedicalHistoryDetailList",
						this.refMedicalHistoryDetailList);
				this.medicalDetailGrid = this.medicalDetailList.initPanel();
				return this.medicalDetailGrid;
			}
		});