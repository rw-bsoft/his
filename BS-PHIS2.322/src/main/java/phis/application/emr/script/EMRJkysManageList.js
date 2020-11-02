$package("phis.application.emr.script")

$import("phis.script.EditorList")

phis.application.emr.script.EMRJkysManageList = function(cfg) {

	phis.application.emr.script.EMRJkysManageList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRJkysManageList,
		phis.script.EditorList, {
			doLoadYljs : function(){
				var data = [];
				var store = this.store.data.items;
				var n = this.store.data.items.length;
				for ( var i = 0; i < n; i++) {
					var r = store[i];
					data.push(r.data);
				}
				this.grid.el.mask("正在更新签名元素...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.emrManageService",
					serviceAction : "loadQmys",
					body : data
				});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}else{
					this.loadDataByLocal(ret.json.body,ret.json.add);
					MyMessageTip.msg("提示", "更新完成!", true);
				}
			},
			doSave : function() {
				var data = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for ( var i = 0; i < n; i++) {
					var r = store.getAt(i);
					data.push(r.data);
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.emrManageService",
					serviceAction : "saveQmys",
					body : data
				});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				} else {
					MyMessageTip.msg("提示", "保存成功!", true);
					this.refresh()
				}
			}
		})