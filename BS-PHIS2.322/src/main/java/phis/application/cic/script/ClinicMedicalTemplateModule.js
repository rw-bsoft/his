$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicMedicalTemplateModule = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 600;
	cfg.defaultHeight = 150;
	this.plugins = ["undoRedo", "removeFmt", "subSuper", "speChar"];
	phis.application.cic.script.ClinicMedicalTemplateModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.ClinicMedicalTemplateModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var actions = this.actions;
				var bar = [];
				var sslbValue = 1;
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					bar.push({
						xtype : "radio",
						checked : true,
						boxLabel : ac.name,
						inputValue : ac.properties.value,
						name : "stack",
						listeners : {
							change : function(group, checked) {
								if (this.medicalTemplateList && checked) {
									this.medicalTemplateList.SSLB = group.inputValue;
									sslbValue = group.inputValue;
									this.medicalTemplateList.requestData.cnd = [
											'eq', ['$', 'SSLB'],
											['s', sslbValue]];
									this.medicalTemplateList.initCnd = ['eq',
											['$', 'SSLB'], ['s', sslbValue]];
									this.medicalTemplateList.loadData();
								}
							},
							scope : this
						}
					})
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : [bar],
							items : [{
										layout : "fit",
										border : false,
										split : false,
										region : 'west',
										width : 480,
										items : this.getMedicalTemplateList()
									}, {
										layout : "fit",
										border : false,
										split : false,
										region : 'center',
										width : 600,
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
						"getMedicalTemplateList", this.refMedicalTemplateList);
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
						"getMedicalDetailList", this.refMedicalDetailList);
				this.medicalDetailGrid = this.medicalDetailList.initPanel();
				return this.medicalDetailGrid;
			}
		});