$package("phis.application.war.script")

$import("phis.script.SelectList", "phis.script.rmi.jsonRequest")

phis.application.war.script.DoctorAdviceExecuteBrAxmList = function(cfg) {
	cfg.disablePagingTbr = true; // 分页暂时不要
	phis.application.war.script.DoctorAdviceExecuteBrAxmList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.war.script.DoctorAdviceExecuteBrAxmList,
		phis.script.SelectList, {
			loadData : function() {
				this.clear();
				if(!this.XMXHS){
				return;
				}
				this.requestData.serviceId = "phis.doctorAdviceExecuteService";
				this.requestData.serviceAction = "detailChargeQuery";
				this.requestData.cnd = 0;
				this.requestData.XMXHS=this.XMXHS
			phis.application.war.script.DoctorAdviceExecuteBrAxmList.superclass.loadData.call(this);
			},
			doRender : function(v, params,record) {
				var zje = record.get("FYCS")*record.get("YCSL")*record.get("YPDJ")
				return parseFloat(zje).toFixed(2);
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			},
			onStoreLoadData : function(store, records, ops) {
				phis.script.SelectList.superclass.onStoreLoadData.call(this,
						store, records, ops)
				this.clearSelect();
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				this.grid.getSelectionModel().selectAll();
				this.fireEvent("afterLoadData",this)
			}
		});