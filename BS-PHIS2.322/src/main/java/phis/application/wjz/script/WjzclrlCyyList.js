$package("phis.application.wjz.script")

$import("phis.script.SelectList")

phis.application.wjz.script.WjzclrlCyyList = function(cfg) {
	cfg.modal=true
	cfg.disablePagingTbr = true;
	phis.application.wjz.script.WjzclrlCyyList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.wjz.script.WjzclrlCyyList,
		phis.script.SelectList, {
			doQr:function(){
			var data=this.getSelectedRecords();
			var cly="";
			var count=data.length;
			for(var i=0;i<count;i++){
				if(cly.length>0){
				cly+=","+data[i].data.CYY;
				}else{
				cly=data[i].data.CYY
				}
			}
			this.fireEvent("qr",cly);
			this.doCancel();
			},
			doCancel : function() {
				this.getWin().hide();
			},
			loadData:function(){
			this.requestData.serviceId="phis.wjzManageService";
			this.requestData.serviceAction="loadWjzcyy";
			phis.application.wjz.script.WjzclrlCyyList.superclass.loadData.call(this)
			}
		})