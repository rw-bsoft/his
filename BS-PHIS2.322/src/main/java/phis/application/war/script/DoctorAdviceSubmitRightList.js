
$package("phis.application.war.script")
$import("phis.script.SelectList")

phis.application.war.script.DoctorAdviceSubmitRightList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.war.script.DoctorAdviceSubmitRightList.superclass.constructor.apply(this,
			[cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.war.script.DoctorAdviceSubmitRightList, phis.script.SelectList, {
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
			loadData:function(){
			this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.war.script.DoctorAdviceSubmitRightList.superclass.loadData
						.call(this);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("afterLoadData",this)
				phis.script.SelectList.superclass.onStoreLoadData.call(this,
						store, records, ops)
				this.clearSelect();
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				this.grid.getSelectionModel().selectAll();
			}
		})