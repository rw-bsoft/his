$package("phis.application.cic.script")

$import("phis.script.SelectList")

phis.application.cic.script.FzjcList2 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.cic.script.FzjcList2.superclass.constructor.apply(this,
			[ cfg ]);
}
var recordIds = new Array();
Ext.extend(phis.application.cic.script.FzjcList2, phis.script.SelectList, {
	expansion : function(cfg) {
		cfg.sm.handleMouseDown = Ext.emptyFn// 只允许点击check列选中
		cfg.sm.on("rowselect", this.rowSelect, this);
		cfg.sm.on("rowdeselect", this.rowdeSelect, this);
	},
	onDblClick:function(grid,index,e){
	},
	rowSelect : function(e, rowIndex, record) {
		if (!this.containsArray(recordIds, record.get("TESTID")+record.get("SAMPLENO")))
			recordIds.push({
				id : record.get("TESTID")+record.get("SAMPLENO"),
				SAMPLENO : record.get("SAMPLENO"),
				EXAMINAIM : record.get("EXAMINAIM"),
				CHECKTIME : record.get("CHECKTIME"),
				CHINESENAME : record.get("CHINESENAME"),
				TESTRESULT : record.get("TESTRESULT"),
				UNIT : record.get("UNIT")
			});
		this.fireEvent("rowSelect",this.getJYYRResult(recordIds),this)
	},
	getJYYRResult : function(array){
		var JYYRResult = "";
		var list1 = []
		for ( var i = 0; i < array.length; i++) {
			var needAdd = true;
			for ( var j = 0; j < list1.length; j++) {
				if(list1[j].SAMPLENO==array[i].SAMPLENO){
					needAdd = false;
				}
			}
			if(needAdd){
				list1.push(array[i]);
			}
		}
		for( var j = 0; j < list1.length; j++){
			JYYRResult += list1[j].EXAMINAIM+"("+list1[j].CHECKTIME+")："
			for ( var i = 0; i < array.length; i++) {
				if(list1[j].SAMPLENO==array[i].SAMPLENO){
					//wangjb 结果与单位间加空格
					JYYRResult += array[i].CHINESENAME+" "+array[i].TESTRESULT;
					var unit=array[i].UNIT;
					if(unit.indexOf('^')<0){
						JYYRResult += " ";
					}else{
						JYYRResult += "*";
					}
					JYYRResult += unit;
					if(array.length-1==i){
						JYYRResult += "。";
					}else{
						JYYRResult += "；";
					}
				}
			}
		}
		console.log(JYYRResult)
		return JYYRResult;
	},
	rowdeSelect : function(e, rowIndex, record) {
		if (this.containsArray(recordIds, record.get("TESTID")+record.get("SAMPLENO"))) {
			this.RemoveArray(recordIds, record.get("TESTID")+record.get("SAMPLENO"));
		}
		this.fireEvent("rowSelect",this.getJYYRResult(recordIds),this)
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