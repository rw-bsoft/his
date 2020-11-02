$package("phis.application.war.script")
$import("phis.script.SimpleList")
phis.application.war.script.WardDoctorPrintManagementRightModuleThirdList = function(
		cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr=true;
	cfg.modal=true
	cfg.width=980;
	phis.application.war.script.WardDoctorPrintManagementRightModuleThirdList.superclass.constructor
			.apply(this, [cfg])
}

Ext
		.extend(
				phis.application.war.script.WardDoctorPrintManagementRightModuleThirdList,
				phis.script.SimpleList, {
				loadData : function() {
						this.requestData.serviceId = this.serviceId;
						this.requestData.serviceAction = this.serviceAction;
						phis.application.war.script.WardDoctorPrintManagementRightModuleThirdList.superclass.loadData.call(this)
					},
				doSave:function(){
				var r=this.getSelectedRecord();
				if(r==null){
				return}
				this.fireEvent("qr",r);
				},
				doClose:function(){
				this.fireEvent("close",this);
				},
				doZs:function(){
				MyMessageTip.msg("提示", "从选择页码,行号的记录开始往下打印", true);
				}
				})