/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.ClinicHospitalAppointmentList = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.cic.script.ClinicHospitalAppointmentList.superclass.constructor
			.apply(this, [ cfg ])
	this.on("beforeCellEdit", this.beforeGridEdit, this);
}
Ext
		.extend(
				phis.application.cic.script.ClinicHospitalAppointmentList,
				phis.script.SimpleList,
				{
					loadData : function(yyrq) {
						this.clear();
						this.requestData.serviceId = "phis.clinicHospitalAppointmentService";
						this.requestData.serviceAction = "queryYyksInfo";
						if (yyrq) {
							this.requestData.cnds = yyrq;
						} else {
							this.requestData.cnds = 0;
						}
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
				});