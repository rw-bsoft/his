$package("phis.application.sto.script")

$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseBasicInfomationList = function(cfg) {
	phis.application.sto.script.StorehouseBasicInfomationList.superclass.constructor.apply(
			this, [cfg])
			this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.sto.script.StorehouseBasicInfomationList,
		phis.script.SimpleList, {
			onBeforeRemove : function(entryName, r) {
				if(r.data.SYBZ==2){
				Ext.Msg.alert("提示",r.data.YKMC+"已初始建账，不能删除!");
				return false;
				}
				var body = {};
				body["jgid"] = r.data.JGID;
				body["yksb"]=r.data.YKSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.verifiedUsingServiceId,
							serviceAction : this.verifiedUsingActionId,
							body : body
						});
				if (ret.code > 300) {
					//var msg = "药库[" + r.data.YKMC + "]已经被使用,不能被删除";
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeRemove);
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