$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyMedicinesReceiveDrugsWayList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.modal = true;
	phis.application.pha.script.PharmacyMedicinesReceiveDrugsWayList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyMedicinesReceiveDrugsWayList,
		phis.script.SimpleList, {
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			onReady : function() {
				this.on("winShow", this.onWinShow, this);
			},
			onWinShow : function() {
				this.refresh();
			},
			onDblClick : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
							body : r.data
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onDblClick,
							this);
					return;
				}
				var module = this.createModule("list", this.receiveWay);
				var win = module.getWin();
				win.add(module.initPanel());
				module.on("doSubmit", this.doSubmit, this);
				module.requestData.serviceId =this.fullserviceId;
				module.requestData.serviceAction =this.listActionId,
				module.requestData.body = r.data
				module.refresh();
				win.show();
			},
			doSubmit : function(data) {
				var r = this.getSelectedRecord()
				r.set("LYFS", data.CKFS);
				r.set("LYFS_text", data.FSMC);
			},
			doSave : function() {
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						var o = r.data;
						data.push(o)
						continue
					}
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId :this.serviceId,
							serviceAction :this.saveActionId,
							body : data
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave,
							this);
					return;
				}
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.dirty) {
						r.commit();
					}
				}
				Ext.MessageBox.alert('提示', '保存成功');
			},
			doReSet : function(){
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					var o = r.data;
					data.push(o)
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId ,
							serviceAction : this.updateActionId ,
							body : data
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave,
							this);
					return;
				}
				Ext.MessageBox.alert('提示', "重置成功!");
				this.refresh();
			}
		});