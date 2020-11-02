$package("phis.application.ivc.script");

$import("phis.script.SimpleList");
		/**
		 * 该代码已在2013-07-09修改2114bug时,已不用
		 */
phis.application.ivc.script.ClinicOutpatientExpensesDetails = function(cfg) {
		cfg.height = 185;
		cfg.disablePagingTbr = true;
		cfg.autoLoadData = false;
		cfg.group = "DH";
		cfg.groupTextTpl = "<table width='20%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='25%'>&nbsp;&nbsp;<b>{[values.rs[0].data.LB]}</b></td><td width='30%'><b>{[values.rs[0].data.YSDM]}</b></td><td width='45%'><div align='right'><b>{[values.rs[0].data.KDRQ]}</b></div></td></tr></table>"
		phis.application.ivc.script.ClinicOutpatientExpensesDetails.superclass.constructor
					.apply(this, [ cfg ]);
},

Ext.extend(phis.application.ivc.script.ClinicOutpatientExpensesDetails,
			phis.script.SimpleList,{
				loadData : function(JZSJ, KSDM,JZYS) {
						this.clear(); // ** add by yzh , 2010-06-09 **
						recordIds = [];
						this.requestData.serviceId = "phis.clinicOutpatientExpensesInfoService";
						this.requestData.serviceAction = "queryFYMXInfo";
						this.requestData.JZSJ = JZSJ;
						this.requestData.KSDM = KSDM;
						this.requestData.JZYS=JZYS;
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