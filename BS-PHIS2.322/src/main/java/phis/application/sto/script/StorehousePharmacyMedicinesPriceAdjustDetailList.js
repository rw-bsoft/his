$package("phis.application.sto.script")

$import("phis.script.SelectList")

phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailList = function(cfg) {
	this.editRecords = [];
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailList,
		phis.script.SelectList, {
			//调价金额计算
			onRenderer:function(value, metaData, r){
			if(value==0||value==null){
				return (r.data.TJSL * (r.data.XLSJ-r.data.YLSJ)).toFixed(4)
				}
				return parseFloat(value).toFixed(4);
			}
,
			onRenderer_two : function(value, metaData, r) {
				if (value != null && value != 0) {
					return parseFloat(value).toFixed(2);
				}
				return value;
			},
			onRenderer_four : function(value, metaData, r) {
				if (value != null && value != 0) {
					return parseFloat(value).toFixed(4);
				}
				return value;
			},
			rendererNull:function(value, metaData, r){
				if(value==null||value==undefined){
				return "";}else{
				return value;
				}
			}
		})