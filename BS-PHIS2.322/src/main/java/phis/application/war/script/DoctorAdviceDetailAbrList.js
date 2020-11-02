$package("phis.application.war.script")

$import("phis.script.SelectList", "phis.script.rmi.jsonRequest")

phis.application.war.script.DoctorAdviceDetailAbrList = function(cfg) {
	cfg.disablePagingTbr = true; // 分页暂时不要
	phis.application.war.script.DoctorAdviceDetailAbrList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.war.script.DoctorAdviceDetailAbrList,
		phis.script.SelectList, {
			loadData : function(al_zyh) {
				this.clear();
				this.requestData.serviceId = "phis.doctorAdviceExecuteService";
				this.requestData.serviceAction = "detailChargeQuery";
				 this.requestData.cnd = al_zyh;
				 if(this.ZYHS){
				  this.requestData.ZYHS=this.ZYHS
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
			},
			doRender : function(v, params,record) {
				var zje = record.get("FYCS")*record.get("YCSL")*record.get("YPDJ")
				return parseFloat(zje).toFixed(2);
			},
			/*doQuery : function(al_zyh){
				this.clear();
				this.requestData.serviceId = "doctorAdviceExecuteService";
				this.requestData.serviceAction = "detailChargeQuery";
			    this.requestData.cnd = al_zyh;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load();
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
			},*/
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