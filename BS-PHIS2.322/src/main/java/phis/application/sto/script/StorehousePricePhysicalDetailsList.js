$package("phis.application.sto.script")

$import("phis.script.SimpleList"
		 ,"phis.application.sto.script.StorehousePricePhysicalDetailsPrintView")

phis.application.sto.script.StorehousePricePhysicalDetailsList = function(cfg) {
	phis.application.sto.script.StorehousePricePhysicalDetailsList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehousePricePhysicalDetailsList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.serviceActionId;
				phis.application.sto.script.StorehousePricePhysicalDetailsList.superclass.loadData
						.call(this);
			},
			doUpload : function() {
			var url = "http://10.2.202.21:8280/services/upmedicalbusiness"
				var payapi = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.SptService",
							serviceAction : "upLoadphysicalDetail",
							body : {
								url : url
							}
						});
						var json=payapi.json;
						var body=json.body;
				if 	(payapi.code !=200){
					MyMessageTip.msg("提示",payapi.msg , true);
					return;
				}else if (body.code!=200){
					MyMessageTip.msg("提示",body.msg , true);
					return;
				}else {
					MyMessageTip.msg("提示", "库存信息上传成功!", true);
				}
				this.refresh();
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("YPXH"))
				}
			 	var pWin = this.midiModules["StorehousePricePhysicalDetailsPrintView"]
				var cfg = {
					requestData : ids
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
			 	pWin = new phis.application.sto.script.StorehousePricePhysicalDetailsPrintView(cfg)
				 this.midiModules["StorehousePricePhysicalDetailsPrintView"] = pWin
				pWin.getWin().show()
			}
		})