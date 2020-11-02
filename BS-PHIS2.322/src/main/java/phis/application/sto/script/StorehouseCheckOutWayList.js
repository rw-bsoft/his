$package("phis.application.sto.script")

$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseCheckOutWayList = function(cfg) {
	phis.application.sto.script.StorehouseCheckOutWayList.superclass.constructor.apply(
			this, [cfg])
			this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutWayList,
		phis.script.SimpleList, {
			initPanel:function(sc){
				if(this.mainApp['phis'].storehouseId==null||this.mainApp['phis'].storehouseId==""||this.mainApp['phis'].storehouseId==undefined){
				Ext.Msg.alert("提示","未设置登录药库,请先设置");
				return null;
				}
			var grid = phis.application.sto.script.StorehouseCheckOutWayList.superclass.initPanel.call(this,sc);
			this.grid=grid;
			return grid;
			},
			onBeforeRemove : function(entryName, r) {
				if(r.data.CKFS==99){
				Ext.Msg.alert("提示","系统自定义出库方式,不能删除!");
				return false;
				}
				var body = {};
				body["fspb"] = "ck";
				body["keyValue"] = r.data.CKFS;
				body["yksb"]=r.data.XTSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.verifiedUsingServiceId,
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
			//防止狂按确定导致的打开第一条
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
			},
			onRenderer_reg:function(value, metaData, r){
			return value.replace('<','&lt');
			}
		})