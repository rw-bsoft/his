$package("phis.application.emr.script")

$import("phis.script.SimpleModule")

phis.application.emr.script.EMRQxbmWhModule = function(cfg) {
	phis.application.emr.script.EMRQxbmWhModule.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.emr.script.EMRQxbmWhModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
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
										width : '30%',
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getLList : function() {
				var module = this.createModule("controlLlist", this.refLList);
				this.llist = module;
				var lGrid = module.initPanel();
				this.lGrid = lGrid;
				lGrid.on("rowClick", this.onLListRowClick, this);
				module.on("loadData", this.onLoadData, this);
				// module.on("save", this.onLListSave, this);
				// module.on("remove", this.onLListRemove, this);
				return lGrid;
			},
			getRList : function() {
				var module = this.createModule("controlRlist", this.refRList);
				this.rlist = module;
				return module.initPanel();
			},
			onLoadData : function(store) {
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.lGrid.fireEvent("rowclick", this.lGrid, 0);
					}
				} else {
					this.llist.DWBM = "";
				}
			},
			onLListRowClick : function(lGrid, rowIndex, e) {
				// if(this.llist.store.getCount() > 0){
				//					
				// }else{
				//					
				// }
				var r = lGrid.store.getAt(rowIndex);
				if (!r) return;
				this.rlist.DWBM = r.data.DWBM;
				this.rlist.requestData.cnd = ['eq',
						['$', 'DWBM'], ['d', r.data.DWBM]];
				this.rlist.loadData();
//				this.rlist.store.rejectChanges();
			}
		})
