$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
phis.application.cic.script.ClinicTherapeuticRegimenModule = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 600;
	cfg.defaultHeight = 150;
	this.plugins = ["undoRedo", "removeFmt", "subSuper", "speChar"];
	phis.application.cic.script.ClinicTherapeuticRegimenModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.ClinicTherapeuticRegimenModule,
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
										width : 480,
										items : this
												.getTherapeuticRegimenList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										width : 600,
										items : this.getTherapeuticDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getTherapeuticRegimenList : function() {
				this.therapeuticRegimenList = this.createModule("getTherapeuticRegimenList",
						this.refTherapeuticRegimenList);
				this.therapeuticRegimenListGrid = this.therapeuticRegimenList.initPanel();
				this.therapeuticRegimenList.on("loadData", this.onListLoadData, this);
				this.therapeuticRegimenListGrid.on("loadData", this.onListLoadData, this);
				this.therapeuticRegimenListGrid.on("rowClick",this.onListRowClick,this);
				return this.therapeuticRegimenListGrid;
			},
			onListLoadData : function(store) {
				//如果第一次打开页面，默认模拟选中第一行
				if(store.getCount()>0){
					if (!this.initDataId) {
						this.therapeuticRegimenListGrid.fireEvent("rowclick", this.therapeuticRegimenListGrid, 0);
					} 
				}else{
					this.therapeuticDetailList.doNew();
					this.therapeuticDetailList.initDataId="";
				}
			},
			onListRowClick : function(therapeuticRegimenListGrid, rowIndex, e) {
				var r = therapeuticRegimenListGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.therapeuticDetailList.initDataId=r.id;
				this.therapeuticDetailList.loadData();
			},
			getTherapeuticDetailList : function() {
				this.therapeuticDetailList = this.createModule("getTherapeuticDetailList",
						this.refTherapeuticDetailList);
				this.therapeuticDetaiGrid = this.therapeuticDetailList.initPanel();
				return this.therapeuticDetaiGrid;

			}
		});