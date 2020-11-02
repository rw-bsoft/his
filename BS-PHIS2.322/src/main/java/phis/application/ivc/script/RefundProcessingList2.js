$package("phis.application.ivc.script")

/**
 * 处方组套维护
 */
$import("phis.script.SimpleList")

phis.application.ivc.script.RefundProcessingList2 = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false
	cfg.showRowNumber = true;
	phis.application.ivc.script.RefundProcessingList2.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.RefundProcessingList2, phis.script.SimpleList, {
			loadData : function(data) {
				var records = new Array();
				for (var i = 0; i < this.details.length; i++) {
					var ddata = this.details[i];
					if(data.CFLX == ddata.CFLX && data.CFSB == ddata.CFSB){
						records.push(new Ext.data.Record(ddata));
					}
				}
				this.store.removeAll();
				this.store.add(records);
			},
			setDetails : function(details){
				this.details = details;
			},
	onRenderer:function(value, metaData, r){
	if(r.get("ZFYP")==1){
	return '<span style="font-size:12px;color:red;">(自备)</span>' + value 
	}
	return value;
	}
		})
