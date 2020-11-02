$package("phis.application.pha.script");

$import("phis.application.pha.script.PharmacyMySimpleDetailModule");

phis.application.pha.script.PharmacyRequisitionDetailModule = function(cfg) {
	cfg.title = "调拨申请单";
	phis.application.pha.script.PharmacyRequisitionDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyRequisitionDetailModule,
		phis.application.pha.script.PharmacyMySimpleDetailModule, {
			doNew : function() {
				phis.application.pha.script.PharmacyRequisitionDetailModule.superclass.doNew
						.call(this);
				this.list.remoteDicStore.baseParams = {
					"tag" : "db",
					"yfsb" : this.selectValue
				}
				this.list.MBYF = this.selectValue;
			},
			doLoad : function(initDataBody) {
			this.list.remoteDicStore.baseParams = {
					"tag" : "db",
					"yfsb" : this.selectValue
			}
			this.form.initDataBody=initDataBody;
			this.form.op=this.op;
			this.form.loadData();
			this.list.op=this.op;
			this.list.requestData.cnd=['and',['eq',['$','SQYF'],['l',initDataBody.SQYF]],['eq',['$','SQDH'],['i',initDataBody.SQDH]]];
			this.list.loadData();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.SQDH);
			}
		});