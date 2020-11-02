$package("chis.application.hy.script.medicine");

$import("chis.script.BizSimpleListView");

chis.application.hy.script.medicine.HypertensionMedicineImportList = function(cfg) {
	chis.application.hy.script.medicine.HypertensionMedicineImportList.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
};

Ext.extend(chis.application.hy.script.medicine.HypertensionMedicineImportList,
		chis.script.BizSimpleListView, {
			onWinShow : function() {
				this.requestData.cnd = ['and',
						['eq', ['$', 'c.empiId'], ['s', this.empiId]],
						['eq', ['$', 'b.mdcUse'], ['s', '1']]];
				this.loadData();
			},
			doImport : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				this.store.remove(r);
				if (this.store.getCount() > 0) {
					this.grid.getSelectionModel().selectRow(0);
				}
				this.fireEvent("import", r);
			},
			onDblClick : function(grid, index, e) {
				this.doImport();
			}
		});