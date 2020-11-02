/**
 * 初始账册界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList","phis.application.sto.script.StorehousePaymentPrintView");

phis.application.sto.script.StorehousePaymentList = function(cfg) { 
	 cfg.exContext = {};
	 cfg.autoLoadData = true;//修改420bug modify by gejj
	 cfg.entryName="phis.application.sto.schemas.YK_FKCL01"; 
	phis.application.sto.script.StorehousePaymentList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.sto.script.StorehousePaymentList, phis.script.SimpleList,
		{
			loadData : function() { 
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.storehouseManageService";
				this.requestData.serviceAction = "queryPayment";
				this.requestData.body = {};
				phis.application.sto.script.StorehousePaymentList.superclass.loadData.call(this);
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("DWXH"))
				}
			 var pWin = this.midiModules["StorehousePaymentPrintView"]
				var cfg = {
					requestData : ids
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				 pWin = new phis.application.sto.script.StorehousePaymentPrintView(cfg)
			 	this.midiModules["StorehousePaymentPrintView"] = pWin
				pWin.getWin().show()
			}
		});