$package("phis.application.sto.script");
$import("phis.script.SimpleList","util.helper.Helper");

phis.application.sto.script.StorehousePH_DrugDetailsList = function(cfg) {
	cfg.autoLoadData = false;//不自动加载
	cfg.disablePagingTbr = true;//隐藏分页
	this.serverParams = {serviceAction:cfg.listActionId};
	this.serverParams.serviceId=cfg.serviceId;
	phis.application.sto.script.StorehousePH_DrugDetailsList.superclass.constructor.apply(
			this, [cfg]);
};
Ext.extend(phis.application.sto.script.StorehousePH_DrugDetailsList,
		phis.script.SimpleList, {
	RKSLSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return "合计:" + v.toFixed(4)
	},
	JHJGSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  v.toFixed(2)
	},
	PFJGSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  + v.toFixed(2)
	},
	LSJGSummaryRenderer : function(v, params) {
		if (params.style) {
			params.style += "font-size:16px;"
		}
		return  + v.toFixed(2)
	}	
});