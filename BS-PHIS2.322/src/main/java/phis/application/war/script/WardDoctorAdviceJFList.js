$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.WardDoctorAdviceJFList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.modal = true;
	phis.application.war.script.WardDoctorAdviceJFList.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.winShow, this);
}
Ext.extend(phis.application.war.script.WardDoctorAdviceJFList,
		phis.script.SimpleList, {
			winShow : function() {
				this.requestData.cnd = ['eq', ['$', 'YZXH'],
						['d', this.initDataId]];
				this.refresh();
			},
			doClose : function() {
				this.win.hide();
			}
		});
