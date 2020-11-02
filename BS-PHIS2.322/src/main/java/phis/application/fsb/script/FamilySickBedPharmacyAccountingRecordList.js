$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedPharmacyAccountingRecordList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop=false;
	cfg.disablePagingTbr = true;
	phis.application.fsb.script.FamilySickBedPharmacyAccountingRecordList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedPharmacyAccountingRecordList,
		phis.script.SimpleList, {
			//双击
			onDblClick : function(grid, index, e) {
				this.doConfirm();
			},
			//确认
			doConfirm:function(){
			var record=this.getSelectedRecord();
			if(record==null){
			return;}
			this.fireEvent("checkData",record.data.KCSB,record.data.JHJG,record.data.LSJG);
			},
			//关闭
			doClose:function(){
			this.fireEvent("close",this);
			}
		})