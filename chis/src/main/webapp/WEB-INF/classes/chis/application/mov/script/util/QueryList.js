/**
 * 公共查询列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.util")
$import("chis.script.BizSelectListView")
chis.application.mov.script.util.QueryList = function(cfg) {
	chis.application.mov.script.util.QueryList.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.application.mov.script.util.QueryList, chis.script.BizSelectListView, {

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

		})