/**
 * 抗菌药采购明细查询列表
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.AntiMicrobialStorehousePurchaseDetail = function(cfg) {
	cfg.autoLoadData = true;//自动加载
	cfg.disablePagingTbr = true;//隐藏分页
	this.serverParams = {serviceAction:cfg.listActionId};
	this.serverParams.serviceId=cfg.serviceId;
	phis.application.sto.script.AntiMicrobialStorehousePurchaseDetail.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.sto.script.AntiMicrobialStorehousePurchaseDetail,
		phis.script.SimpleList, {
});