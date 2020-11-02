$package("chis.application.cvd.script")
$import("chis.script.BizSimpleListView");
chis.application.cvd.script.DiseaseHistoryListView = function(cfg) {
	debugger;
	this.initCnd=['eq',['$','empiid'],['s',cfg.exContext.empiData.empiId]];
	chis.application.cvd.script.DiseaseHistoryListView.superclass.constructor.apply(this,
			[cfg]);
};
Ext.extend(chis.application.cvd.script.DiseaseHistoryListView,
		chis.script.BizSimpleListView, {
			loadData : function() {
				debugger;
				this.requestData.cnd = ['eq',['$','empiid'],['s',this.exContext.empiData.empiId]];
				chis.application.cvd.script.DiseaseHistoryListView.superclass.loadData
						.call(this);
			},
			onRowClick : function(grid, rowIndex, e) {
				debugger;
				// 加载已经随访的记录
				this.selectedIndex = index
				//this.fireEvent("rowClick", grid, index, e, this)
			},

			onStoreLoadData : function(store, records, ops) {
				debugger;
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				for(var i=0,l=records.length;i<l;i++)
				{
					if(records[i].id==this.exContext.args.selectRecordId)
					{
						this.selectRow(i);	
					}
				}
			},
			onStoreBeforeLoad : function() {

			}
		});
