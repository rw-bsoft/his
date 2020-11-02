/**
 * 药库高低储提示
 * 
 * @author shiwy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList","phis.prints.script.StorehouseGDCPrintView");

phis.application.sto.script.StorehouseGDCList = function(cfg) {
	phis.application.sto.script.StorehouseGDCList.superclass.constructor.apply(
			this, [ cfg ]);
}
Ext.extend(phis.application.sto.script.StorehouseGDCList,
		phis.script.SimpleList, {
			// 加载数据
			loadData : function() {
				this.requestData.serviceId = "phis.storehouseheightstoreservice";
				this.requestData.serviceAction = this.serviceAction;
				phis.application.sto.script.StorehouseGDCList.superclass.loadData
						.call(this);
			},
			doRefresh:function(){
				this.doCndQuery();
			},
			// 当库存大于等于高储时,显示红色
			onRenderer_gc : function(value, metaData, r) {
				if (r.data.KCSL >= value) {
					return "<font color='red'>" + value + "</font>";
				}
				return value;
			},
			// 当库存小于等于低储时,显示红色
			onRenderer_dc : function(value, metaData, r) {
				if (r.data.KCSL <= value) {
					return "<font color='red'>" + value + "</font>";
				}
				return value;
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				if(n==0){
					MyMessageTip.msg("提示", "没有需要打印的记录", true);
				}else{
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						ids.push(r.get("YPXH"))
					}
					var pWin = this.midiModules["StorehouseGDCPrintView"]
					var cfg = {
							requestData : ids
					}
					if (pWin) {
						Ext.apply(pWin, cfg)
						pWin.getWin().show()
						return
					}
					pWin = new phis.prints.script.StorehouseGDCPrintView(cfg)
					this.midiModules["StorehouseGDCPrintView"] = pWin
					pWin.getWin().show()
				}
			}
		});