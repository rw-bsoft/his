$package("phis.application.pha.script");

$import("phis.application.pha.script.PharmacyMySimpleDetailModule");

phis.application.pha.script.PharmacyDeployInventoryDetailModule = function(cfg) {
	cfg.title = "调拨出库单";
	phis.application.pha.script.PharmacyDeployInventoryDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyDeployInventoryDetailModule,
		phis.application.pha.script.PharmacyMySimpleDetailModule, {
			doOpneCommit : function(initDataBody) {
				this.initDataBody = initDataBody;
				//this.changeButtonState("commit");
				this.doLoad(initDataBody);
			},
			doLoad : function(initDataBody) {
			this.form.initDataBody=initDataBody;
			this.form.op=this.op;
			this.form.loadData();
			this.list.op=this.op;
			this.list.requestData.body={"SQYF":initDataBody.SQYF,"SQDH":initDataBody.SQDH};
			this.list.requestData.cnd=['and',['eq',['$','SQYF'],['d',initDataBody.SQYF]],['eq',['$','SQDH'],['i',initDataBody.SQDH]]];
			this.list.requestData.serviceId = this.list.fullserviceId;
			this.list.requestData.serviceAction = this.list.queryActionId;
			this.list.loadData();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.SQDH);
			}
		});