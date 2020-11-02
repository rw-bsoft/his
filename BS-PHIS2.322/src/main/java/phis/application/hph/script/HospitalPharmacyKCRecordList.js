$package("phis.application.hph.script")

$import("phis.script.SimpleList")

phis.application.hph.script.HospitalPharmacyKCRecordList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop=false;
	cfg.disablePagingTbr = true;
	phis.application.hph.script.HospitalPharmacyKCRecordList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.hph.script.HospitalPharmacyKCRecordList,
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
			if(record.data.YPSL<this.YPSL){
			Ext.Msg.show({
					title : "提示",
					msg : "当前选择库存数小于页面输入数量,是否继续?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.getWin().hide();
							this.fireEvent("checkData",record.data.SBXH,record.data.JHJG,record.data.LSJG,record.data.YPSL);
						}
					},
					scope : this
				});
			}else{
			this.getWin().hide();
			this.fireEvent("checkData",record.data.SBXH,record.data.JHJG,record.data.LSJG);
			}
			
			},
			//关闭
			doClose:function(){
			this.fireEvent("close",this);
			this.getWin().hide();
			}
		})