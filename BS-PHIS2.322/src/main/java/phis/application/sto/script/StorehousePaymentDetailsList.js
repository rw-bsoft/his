/**
 * 初始账册界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList","phis.application.sto.script.StorehousePaymentDetailsPrintView");

phis.application.sto.script.StorehousePaymentDetailsList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.entryName="phis.application.sto.schemas.YK_FKCL02";
	phis.application.sto.script.StorehousePaymentDetailsList.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehousePaymentDetailsList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.storehouseManageService";
				this.requestData.serviceAction = "queryPaymentDetails";
				this.requestData.body = {};
				phis.application.sto.script.StorehousePaymentDetailsList.superclass.loadData.call(this);
			},
			onDblClick : function(grid, index, e) {
				this.opener.doPayment();
			},
			doPrint : function() {
				var ids = [];
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				ids = r.data.DWXH;
		  var pWin = this.midiModules["StorehousePaymentDetailsPrintView"]
				var cfg = {
					requestData : ids
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
			 pWin = new phis.application.sto.script.StorehousePaymentDetailsPrintView(cfg)
		 	this.midiModules["StorehousePaymentDetailsPrintView"] = pWin
				pWin.getWin().show()
			}
		});