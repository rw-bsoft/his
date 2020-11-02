$package("phis.application.cic.script")

$import("phis.script.SelectList")

phis.application.cic.script.FzjcList1 = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.cic.script.FzjcList1.superclass.constructor.apply(this,
			[ cfg ]);

}
var records = new Array();
Ext.extend(phis.application.cic.script.FzjcList1, phis.script.SelectList, {
	onReady : function() {
		this.requestData.serviceId = "phis.userDataBoxService";
		this.requestData.serviceAction = "GetFzjcList";
		this.requestData.brid = this.opener.brid;
		phis.application.cic.script.FzjcList1.superclass.onReady.call(this);
		alert("这里取检验检查项目……")
	},
	expansion : function(cfg) {
		cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
		cfg.sm.on("rowselect", this.rowSelect, this);
		cfg.sm.on("rowdeselect", this.rowdeSelect, this);
	},
	onDblClick:function(grid,index,e){
	},
	rowSelect : function(e, rowIndex, record) {
		if (!this.containsArray(records, record.get("SAMPLENO")))
			records.push({
				id : record.get("SAMPLENO")
			});
		this.fireEvent("rowSelect",this.getSAMPLENO(records),this)
	},
	getSAMPLENO : function(array){
		var SAMPLENOS = "";
		for ( var i = 0; i < array.length; i++) {
			if(i==0){
				SAMPLENOS += "'"+array[i].id+"'";
			}else{
				SAMPLENOS += ",'"+array[i].id+"'";
			}
		}
		return SAMPLENOS;
	},
	rowdeSelect : function(e, rowIndex, record) {
		if (this.containsArray(records, record.get("SAMPLENO"))) {
			this.RemoveArray(records, record.get("SAMPLENO"));
		}
		this.fireEvent("rowSelect",this.getSAMPLENO(records),this)
	},
	containsArray : function(array, attachId) {
		for ( var i = 0; i < array.length; i++) {
			if (array[i].id == attachId) {
				return true;
				break;
			}
		}
		return false;
	},
	RemoveArray : function(array, attachId) {
		for ( var i = 0, n = 0; i < array.length; i++) {
			if (array[i].id != attachId) {
				array[n++] = array[i]
			}
		}
		array.length -= 1;
	}
});