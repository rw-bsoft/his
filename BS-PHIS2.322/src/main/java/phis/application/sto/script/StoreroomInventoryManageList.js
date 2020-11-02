$package("phis.application.sto.script")

$import("phis.script.SimpleList")

phis.application.sto.script.StoreroomInventoryManageList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop=false;
	cfg.modal=true;
	phis.application.sto.script.StoreroomInventoryManageList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StoreroomInventoryManageList,
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
			this.fireEvent("checkData",record.data.SBXH,record.data.KCSL,record.data.YPPH,record.data.YPXQ,record.data.JHJG,record.data.LSJG);
			this.getWin().hide();
			},
			//关闭
			doClose:function(){
			this.getWin().hide();
			//this.fireEvent("hide",this);
			}
		})