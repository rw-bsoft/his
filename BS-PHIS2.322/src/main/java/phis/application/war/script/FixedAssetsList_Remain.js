$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.FixedAssetsList_Remain = function(cfg) {
	cfg.modal = true;
	cfg.width = 550;
	cfg.height = 250;
	this.disablePagingTbr = true;
	this.isCombined = true;
	phis.application.war.script.FixedAssetsList_Remain.superclass.constructor
			.apply(this, [cfg])

}
Ext.extend(phis.application.war.script.FixedAssetsList_Remain,
		phis.script.SimpleList, {

			loadData : function() {
				this.requestData.serviceAction = "queryFp";
				this.requestData.body = {
					wardId : this.mainApp['phis'].wardId,
					ksdm : this.hhks,
					zyh : this.zyh
				};
				phis.application.war.script.FixedAssetsList_Remain.superclass.loadData
						.call(this);
			},
			onDblClick : function(grid, index, e) {
				var r = this.getSelectedRecord();
				this.operater.fpcw = r.data.BRCH;
				this.operater.doCre();
				this.getWin().hide();
			},
			doSave : function() {
				var r = this.getSelectedRecord();
				this.operater.fpcw = r.data.BRCH;
				this.operater.doCre();
				this.getWin().hide();
			},
			doCancel : function() {
				this.getWin().hide();
			}
		})