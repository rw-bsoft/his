$package("phis.application.sto.script");
$import("phis.script.SimpleList","util.helper.Helper");

phis.application.sto.script.StorehousePH_DrugRecordList = function(cfg) {
	cfg.autoLoadData = true;//不自动加载
	cfg.disablePagingTbr = true;//隐藏分页
	this.serverParams = {serviceAction:cfg.listActionId};
	this.serverParams.serviceId=cfg.serviceId;
	phis.application.sto.script.StorehousePH_DrugRecordList.superclass.constructor.apply(
			this, [cfg]);
};
Ext.extend(phis.application.sto.script.StorehousePH_DrugRecordList,
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
	RKSLSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  + v.toFixed(2)
	},
	JHJGSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  v.toFixed(4)
	},
	PFJGSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  + v.toFixed(4)
	},
	LSJGSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  + v.toFixed(4)
	}
});