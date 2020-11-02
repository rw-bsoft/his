$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedAdviceJFList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.modal = true;
	phis.application.fsb.script.FamilySickBedAdviceJFList.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.winShow, this);
}
Ext.extend(phis.application.fsb.script.FamilySickBedAdviceJFList,
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
