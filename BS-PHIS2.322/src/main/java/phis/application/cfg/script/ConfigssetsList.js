$package("phis.application.cfg.script")

$import("phis.script.SimpleList")
phis.application.cfg.script.ConfigssetsList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.cfg.script.ConfigssetsList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cfg.script.ConfigssetsList, phis.script.SimpleList, {
	onReady : function() {
		phis.application.cfg.script.ConfigssetsList.superclass.onReady.call(this);
		this.initCnd = ['eq', ['$', ' a.KFXH'], ['d', this.mainApp['phis'].treasuryId]];
		this.requestData.cnd = ['eq', ['$', ' a.KFXH'],
				['d', this.mainApp['phis'].treasuryId]];
		this.loadData();
	},
	onWinShow : function() {
		if (this.configInventoryInModuleUpd) {
			this.configInventoryInModuleUpd.doUpdat(r.get("JLXH"), r
							.get("WZXH"), r.get("CJXH"));
			this.configInventoryInModuleUpd.opener = this;
		}
		if (this.configInventoryInModuleAdd) {
			this.configInventoryInModuleAdd.doNew();
			this.configInventoryInModuleAdd.opener = this;
		}
	},
	doAdd : function(grid, index, e) {
		if (this.mainApp['phis'].treasuryEjkf != 0) {
			MyMessageTip.msg("提示", "二级库房,不能初始化资产设备",false);
			return;
		}
		this.configInventoryInModuleAdd = this.createModule(
				"configInventoryInModule", this.neworupdRef);
		var win = this.configInventoryInModuleAdd.getWin();
		win.show();
		win.center();
		this.configInventoryInModuleAdd.opener = this;
		if (!win.hidden) {
			this.configInventoryInModuleAdd.doNew();
		}
	},
	doUpd : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return;
		}
		this.configInventoryInModuleUpd = this.createModule(
				"configInventoryInModule", this.neworupdRef);
		var win = this.configInventoryInModuleUpd.getWin();
		win.show();
		win.center();
		if (!win.hidden) {
			this.configInventoryInModuleUpd.opener = this;
			this.configInventoryInModuleUpd.doUpdat(r.get("JLXH"), r
							.get("WZXH"), r.get("CJXH"));
		}
	},
	doRefresh : function() {
		this.refresh()
	},
	onDblClick : function(grid, index, e) {
		var item = {};
		item.text = "修改";
		item.cmd = "upd";
		this.doAction(item, e)

	},
	doExecute : function() {
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "configInventoryInitialService",
					serviceAction : "saveTransferInventoryIn"
				});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			MyMessageTip.msg("提示", "转账失败!", true);
			return;
		} else {
			MyMessageTip.msg("提示", "转账成功!", true);
		}
	}
})