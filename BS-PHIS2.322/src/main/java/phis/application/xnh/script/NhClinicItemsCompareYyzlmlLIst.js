$package("phis.application.xnh.script");

$import("phis.script.SimpleList");

phis.application.xnh.script.NhClinicItemsCompareYyzlmlLIst = function(cfg) {
	this.exContext = {};
	phis.application.xnh.script.NhClinicItemsCompareYyzlmlLIst.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhClinicItemsCompareYyzlmlLIst,
		phis.script.SimpleList, {
			doCancelmatch:function (){
				var r=this.getSelectedRecord();
				var body={};
				
				body.YPXH=r.get("FYXH");
				if(r.get("NHBM_SH")=="1"){
					MyMessageTip.msg("提示", "已审核的药品不能取消匹配", true);
					return;
				}
				body.type="2";
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "updatenonhbm",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
			},
			doMatchself:function (){
				this.mask();
				var body={};
				body.type="2";
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "updatenhbmself",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				this.unmask();
			}
		});
