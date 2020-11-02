$package("phis.application.sas.script")
$import("phis.script.SimpleList")

phis.application.sas.script.PlanSearchList = function(cfg) {
	phis.application.sas.script.PlanSearchList.superclass.constructor.apply(
			this, [ cfg ])
}
Ext
		.extend(
				phis.application.sas.script.PlanSearchList,
				phis.script.SimpleList,
				{
					onReady : function() {
						if (this.grid) {
							phis.application.sas.script.PlanSearchList.superclass.onReady
									.call(this);
							this.initCnd = [ 'eq', [ '$', 'b.KFXH' ],
									[ '$', '%user.properties.treasuryId' ] ];
							this.requestData.cnd = [ 'eq', [ '$', 'b.KFXH' ],
									[ '$', '%user.properties.treasuryId' ] ];
							// 设置分页信息
							this.loadData();
						}
					},
					doRefresh : function() {
						this.requestData.cnd = [ 'eq', [ '$', 'b.KFXH' ],
								[ '$', '%user.properties.treasuryId' ] ];
						this.loadData();
					}
				})