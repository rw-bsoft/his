$package("chis.application.pub.script");

$import("chis.script.BizSelectListView");

chis.application.pub.script.DrugImportList = function(cfg) {
	chis.application.pub.script.DrugImportList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.pub.script.DrugImportList, chis.script.BizSelectListView, {
			resetFirstPage : function() {
				var pt = this.grid.getBottomToolbar();
				if (pt) {
					pt.cursor = 0;
					this.requestData.pageNo = 1;
					pt.afterTextItem
							.setText(String.format(pt.afterPageText, 1));
					pt.inputItem.setValue(1);
					pt.updateInfo();
				} else {
					this.requestData.pageNo = 1;
				}
			}
		});