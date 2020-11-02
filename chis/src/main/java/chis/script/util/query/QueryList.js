/**
 * 公共查询列表页面
 * 
 * @author : yaozh
 */
$package("chis.script.util.query")
$import("chis.script.BizSelectListView")
chis.script.util.query.QueryList = function(cfg) {
	chis.script.util.query.QueryList.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.util.query.QueryList, chis.script.BizSelectListView, {

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