$package("chis.application.cvd.script")
$import("chis.script.BizSimpleListView");
chis.application.cvd.script.AssessRegisterListView = function(cfg) {
	this.initCnd=['eq',['$','isDelete'],['s','2']];
	chis.application.cvd.script.AssessRegisterListView.superclass.constructor.apply(this,
			[cfg]);
};
Ext.extend(chis.application.cvd.script.AssessRegisterListView,
		chis.script.BizSimpleListView, {

			loadData : function() {
				this.requestData.cnd = ['and',["eq", ["$", "phrId"],
						["s", this.exContext.ids.phrId]],['eq',['$','isDelete'],['s','2']]];;
				chis.application.cvd.script.AssessRegisterListView.superclass.loadData
						.call(this);
			},

			onRowClick : function(grid, rowIndex, e) {
				this.selectedIndex = rowIndex
			},

			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
				}

			},

			onStoreBeforeLoad : function() {

			}
		});
