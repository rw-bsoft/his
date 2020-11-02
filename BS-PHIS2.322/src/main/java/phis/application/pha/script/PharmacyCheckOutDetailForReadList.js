$package("phis.application.pha.script")

$import("phis.script.EditorList")

phis.application.pha.script.PharmacyCheckOutDetailForReadList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.pha.script.PharmacyCheckOutDetailForReadList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyCheckOutDetailForReadList,
		phis.script.EditorList, {
			
			onRenderer_two : function(value, metaData, r) {
				if (value!=null&&value!=0) {
					return parseFloat(value).toFixed(2);
				}
				return value;
			},	
			onRenderer_four : function(value, metaData, r) {
				if (value!=null&&value!=0) {
					return parseFloat(value).toFixed(4);
				}
				return value;
			},
			//计算金额
			calculatEmount:function(){
				var count=this.store.getCount();
				var allJhje=0;
				var allLsje=0;
				for(var i=0;i<count;i++){
				var jhje= this.store.getAt(i).data["JHJE"];
				var lsje= this.store.getAt(i).data["LSJE"];
				allJhje= parseFloat(parseFloat(allJhje) + parseFloat(jhje)).toFixed(4);
				allLsje= parseFloat(parseFloat(allLsje) + parseFloat(lsje)).toFixed(4);
				}
			this.fireEvent("recordAdd", allJhje, allLsje);
			}
		})