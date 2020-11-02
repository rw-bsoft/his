$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicInCommonUseList = function(cfg) {
	phis.application.cic.script.ClinicInCommonUseList.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.cic.script.ClinicInCommonUseList,
				phis.script.SimpleList,
				{
					loadData : function() {
						if (!this.CFLX) {
							this.CFLX == 1;
						}
						this.clear();
						this.requestData.serviceId = "phis.clinicProjectComboUserService";
						this.requestData.serviceAction = "queryInCommonUseInfo";
						this.requestData.CFLX = this.CFLX;

						if (this.store) {
							if (this.disablePagingTbr) {
								this.store.load()
							} else {
								var pt = this.grid.getBottomToolbar()
								if (this.requestData.pageNo == 1) {
									pt.cursor = 0;
								}
								pt.doLoad(pt.cursor)
							}
						}
						this.resetButtons();
					}
				})