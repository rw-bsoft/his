$package("phis.application.sas.script")
$import("phis.script.SimpleList")

phis.application.sas.script.ResetSearchList = function(cfg) {
	phis.application.sas.script.ResetSearchList.superclass.constructor.apply(
			this, [ cfg ])
}
Ext
		.extend(
				phis.application.sas.script.ResetSearchList,
				phis.script.SimpleList,
				{
					onReady : function() {
						if (this.grid) {
							phis.application.sas.script.ResetSearchList.superclass.onReady
									.call(this);
							this.initCnd = [
									'and',
									[ 'ge', [ '$', 'c.DJZT' ], [ 'i', 2 ] ],
									[
											'eq',
											[ '$', 'c.KFXH' ],
											[ '$',
													'%user.properties.treasuryId' ] ] ];
							this.requestData.cnd = [
									'and',
									[ 'ge', [ '$', 'c.DJZT' ], [ 'i', 2 ] ],
									[
											'eq',
											[ '$', 'c.KFXH' ],
											[ '$',
													'%user.properties.treasuryId' ] ] ];
							// 设置分页信息
							this.loadData();
						}
					},
					doRefresh : function() {
						this.requestData.cnd = [
								'and',
								[ 'ge', [ '$', 'c.DJZT' ], [ 'i', 2 ] ],
								[ 'eq', [ '$', 'c.KFXH' ],
										[ '$', '%user.properties.treasuryId' ] ] ];
						this.loadData();
					}
				})