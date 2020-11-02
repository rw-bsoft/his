/**
 * 药库采购历史查询_供应商明细列表
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.StorehousePH_SupplierDetailsList = function(cfg) {
	cfg.autoLoadData = false;//不自动加载
	cfg.disablePagingTbr = true;//隐藏分页
	this.serverParams = {serviceAction:cfg.listActionId};
	this.serverParams.serviceId=cfg.serviceId;
	phis.application.sto.script.StorehousePH_SupplierDetailsList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.sto.script.StorehousePH_SupplierDetailsList,
		phis.script.SimpleList, {
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