$package("phis.application.pha.script")
$import("phis.script.SimpleList")
phis.application.pha.script.PharmacyCheckOutWayList = function(cfg) {
	phis.application.pha.script.PharmacyCheckOutWayList.superclass.constructor.apply(
			this, [cfg])
	this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckOutWayList,
		phis.script.SimpleList, {
			initPanel:function(sc){
				if(this.mainApp['phis'].pharmacyId==null||this.mainApp['phis'].pharmacyId==""||this.mainApp['phis'].pharmacyId==undefined){
				Ext.Msg.alert("提示","未设置登录药房,请先设置");
				return null;
				}
			var grid = phis.application.pha.script.PharmacyCheckOutWayList.superclass.initPanel.call(this,sc);
			this.grid=grid;
			return grid;
			},
			onBeforeRemove : function(entryName, r) {
				var body = {};
				body["fspb"] = "ck";
				body["keyValue"] = r.data.CKFS;
				body["yfsb"]=r.data.YFSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verifiedUsingActionId,
							body : body
						});
				if (ret.code > 300) {
					var msg = "出库方式[" + r.data.FSMC + "]已经被使用,不能被删除";
					this.processReturnMsg(ret.code, msg, this.onBeforeRemove);
					return false;
				}
				return true;
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
//				if (records.length == 0) {
//					return
//				}
//				if (!this.selectedIndex || this.selectedIndex >= records.length) {
//					this.selectRow(0)
//				} else {
//					this.selectRow(this.selectedIndex);
//					this.selectedIndex = 0;
//				}
			}
			
		});