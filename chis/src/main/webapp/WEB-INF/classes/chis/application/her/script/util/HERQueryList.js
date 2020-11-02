/**
 * 健康处方引入公用类（工具）
 */
$package("chis.application.her.script.util");

$import("chis.script.BizSelectListView");

chis.application.her.script.util.HERQueryList = function(cfg) {
	chis.application.her.script.util.HERQueryList.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.her.script.util.HERQueryList, chis.script.BizSelectListView, {
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