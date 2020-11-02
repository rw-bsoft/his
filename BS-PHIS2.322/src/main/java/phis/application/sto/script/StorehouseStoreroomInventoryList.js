$package("phis.application.sto.script")

$import("phis.script.SimpleList","phis.application.sto.script.StorehouseStoreroomInventoryPrintView")

phis.application.sto.script.StorehouseStoreroomInventoryList = function(cfg) {
	phis.application.sto.script.StorehouseStoreroomInventoryList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseStoreroomInventoryList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				return phis.application.sto.script.StorehouseStoreroomInventoryList.superclass.initPanel.call(this,sc);
			},
			// 新增
			doAdd : function() {
				var count=this.store.getCount();
				for(var i=0;i<count;i++){
				var YSGH=this.store.getAt(i).get("YSGH");
				if(YSGH==null||YSGH==""||YSGH==undefined){
				MyMessageTip.msg("提示", "还有未完成的盘点单 不能新增盘点单!", true);
				return;
				}
				}
				var KCPD_PC=this.getKCPD_PC();
				if(KCPD_PC==null){
				return ;
				}
				this.inventoryModule = this.createModule("inventoryModule",
						this.addRef);
				this.inventoryModule.on("save", this.onSave, this);
				this.inventoryModule.KCPD_PC=KCPD_PC;
				//this.inventoryModule.on("winClose", this.onClose, this);
				this.inventoryModule.initPanel();
				var win = this.inventoryModule.getWin();
				
				win.show()
				win.center()
				if (!win.hidden) {
					this.inventoryModule.op = "create";
					this.inventoryModule.isRead=false;
					this.inventoryModule.doNew();
				}
			},
			getKCPD_PC:function(){
				if(this.KCPD_PC!=undefined){
				return this.KCPD_PC;
				}
					var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryKCPD_PCAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, "查询系统参数失败", this.getKCPD_PC);
					return null;
				}
				this.KCPD_PC=ret.json.body;
				return ret.json.body;
			},
			// 修改
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var KCPD_PC=this.getKCPD_PC();
				if(KCPD_PC==null){
				return ;
				}
				this.inventoryModule = this.createModule("inventoryModule",
						this.addRef);
				this.inventoryModule.on("save", this.onSave, this);
				//this.inventoryModule.on("winClose", this.onClose, this);
				this.inventoryModule.KCPD_PC=KCPD_PC;
				this.inventoryModule.initPanel();
				var win = this.inventoryModule.getWin();
				win.show()
				win.center()
				if (!win.hidden) {
					this.inventoryModule.op = "update";
					if(r.data.YSGH==null||r.data.YSGH==""||r.data.YSGH==undefined){
					this.inventoryModule.isRead=false;
					}else{
					this.inventoryModule.isRead=true;
					}
					this.inventoryModule.doLoad(r.data.XTSB,r.data.PDDH);
					this.inventoryModule.XTSB=r.data.XTSB;
					this.inventoryModule.PDDH=r.data.PDDH;
				}
			},
			onDblClick : function(grid, index, e) {
			this.doUpd();
			},
			onRenderer:function(value, metaData, r){
			if(r.data.YSGH==null||r.data.YSGH==""||r.data.YSGH==undefined){
			return "盘点中"
			}
			return "已完成";
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "未选择记录", true);
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				if (r.data.YSGH != null&&r.data.YSGH !=""&&r.data.YSGH !=undefined) {
					MyMessageTip.msg("提示", "已完成盘点单不能删除", true);
					return;
				}
				var body = {};
				body["xtsb"] = r.data.XTSB;
				body["pddh"] = r.data.PDDH;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
							this.doRefresh();
						}, this)
			},
			//打印 zhaojian 2017-09-21 增加药库库存盘点导出功能
			doPrint:function(){
			var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "未选择记录", true);
					return
				}
				var pWin = this.midiModules["StorehouseStoreroomInventoryPrintView"]
				var cfg = {
					xtsb : r.get("XTSB"),
					pddh : r.get("PDDH")
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.application.sto.script.StorehouseStoreroomInventoryPrintView(cfg)
				this.midiModules["StorehouseStoreroomInventoryPrintView"] = pWin
				pWin.getWin().show()
			}
		})