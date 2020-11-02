/**
 * 药房统计明细列表
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleList");

phis.application.pha.script.PharmacyStatisticalDetailsList = function(cfg) {
	cfg.autoLoadData = false;//不自动加载
//	cfg.disablePagingTbr = true;//隐藏分页
//	this.serverParams = {serviceAction:cfg.listActionId};
//	this.serverParams.serviceId=cfg.serviceId;
	phis.application.pha.script.PharmacyStatisticalDetailsList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyStatisticalDetailsList,
		phis.script.SimpleList, {
		FYSLSummaryRenderer : function(v, params) {
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return "合计:" + v.toFixed(2)
		},
		FYJESummaryRenderer : function(v, params) {
			if (params.style) {
				params.style += "font-size:16px;"
			}
			return  + v.toFixed(2)
		},
		doCndQuery : function(button, e, addNavCnd){
			this.fireEvent("showRecord",this)
			//this.opener.doShowDetailsByPYDM(this.cndField.getValue().toUpperCase());
		},
		loadData:function(){
		this.requestData.serviceId=this.serviceId;
		this.requestData.serviceAction=this.listActionId
		phis.application.pha.script.PharmacyStatisticalDetailsList.superclass.loadData.call(this);
		},
		onDblClick:function(grid,index,e){
		this.fireEvent("showDetail",this)
		}
		
})