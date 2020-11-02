/**
 * 药房统计方式列表
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleList");

phis.application.pha.script.PharmacyStatisticalMethodList = function(cfg) {
	cfg.autoLoadData = false;//不自动加载
	cfg.disablePagingTbr = true;//隐藏分页
	cfg.serviceId="pharmacyManageService";
	cfg.listActionId="yffytjQuery";
	cfg.entryName="phis.application.pha.schemas.STATISTICALMETHODLIST";
	this.serverParams = {serviceAction:cfg.listActionId};
	this.serverParams.serviceId=cfg.serviceId;
	phis.application.pha.script.PharmacyStatisticalMethodList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyStatisticalMethodList,
		phis.script.SimpleList, {
		
	onDblClick : function(grid, index, e) {
		//获取当前选中行
		var record=this.getSelectedRecord();
		//显示明细
		this.opener.doShowDetails(record);
	},
	loadData : function() {
		this.clear(); 
		if (this.store) {
			if (this.disablePagingTbr) {
				this.store.load({
					callback: function(records, options, success){
						if(this.store.getCount() > 0){
							this.onDblClick();
						}
					},
					scope: this//作用域为this。必须加上否则this.initSelect(); 无法调用
				});
			} else {
				var pt = this.grid.getBottomToolbar()
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		}
		this.resetButtons();
	},
	FYJESummaryRenderer : function(v, params) {
			debugger;
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return "总金额:" + v.toFixed(2)
	}
})