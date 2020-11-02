$package("phis.application.sas.script")
$import("phis.script.SimpleList")

phis.application.sas.script.ReportedLossList = function(cfg) {
	phis.application.sas.script.ReportedLossList.superclass.constructor.apply(
			this, [ cfg ])
}
Ext
		.extend(
				phis.application.sas.script.ReportedLossList,
				phis.script.SimpleList,
				{
					onReady : function() {
						if (this.grid) {
							phis.application.sas.script.ReportedLossList.superclass.onReady
									.call(this);
							this.initCnd = [
									'and',
									[ 'ge', [ '$', 'b.DJZT' ], [ 'i', 1 ] ],
									[
											'eq',
											[ '$', 'b.KFXH' ],
											[ '$',
													'%user.properties.treasuryId' ] ] ];
							this.requestData.cnd = [
									'and',
									[ 'ge', [ '$', 'b.DJZT' ], [ 'i', 1 ] ],
									[
											'eq',
											[ '$', 'b.KFXH' ],
											[ '$',
													'%user.properties.treasuryId' ] ] ];
							// 设置分页信息
							this.loadData();
						}
					},
					doRefresh : function() {
						this.requestData.cnd = [
								'and',
								[ 'ge', [ '$', 'b.DJZT' ], [ 'i', 1 ] ],
								[ 'eq', [ '$', 'b.KFXH' ],
										[ '$', '%user.properties.treasuryId' ] ] ];
						this.loadData();
					}
				})