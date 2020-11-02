$package("phis.application.emr.script")

$import("phis.script.SimpleList")

phis.application.emr.script.EMRForcedUnlockList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	phis.application.emr.script.EMRForcedUnlockList.superclass.constructor.apply(this,
			[cfg]);

	// this.requestData.serviceId = "userDataBoxService";
	// this.requestData.serviceAction = "listUserDataByCnd";
	// this.on("loadData",this.onLoadData,this);
}

Ext.extend(phis.application.emr.script.EMRForcedUnlockList, phis.script.SimpleList, {
	onStoreBeforeLoad : function(store, op) {
		this.requestData.sortInfo="BLBH";
		this.requestData.cnd = ['and', ['eq', ['$', 'SDZT'], ['s', '1']],
				['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]];
		var r = this.getSelectedRecord()
		var n = this.store.indexOf(r)
		if (n > -1) {
			this.selectedIndex = n
		}
	},
	doForcedUnlock : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		var BLBH = r.get("BLBH");
		Ext.Msg.show({
					title : '确认解锁[' + BLBH + ']',
					msg : '确认解锁病历编号为' + BLBH + '的记录?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							phis.script.rmi.jsonRequest({
										serviceId : "emrManageService",
										serviceAction : "saveForcedUnlock",
										BLBH : BLBH
									}, function(code, msg, json) {
										if (code < 300) {
											this.refresh();
											if (this.subList) {
												this.subList.refresh();
											}
										} else {
											Ext.MessageBox.alert("错误", msg)
										}
									}, this)
						}
					},
					scope : this
				})
	},
	doUnlockRecord : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
		}
		var BLBH = r.get("BLBH");
		var module = this.createModule("refUnlockRecord", this.refUnlockRecord);
		this.subList = module;
		module.requestData.sortInfo="BLBH";
		module.requestData.cnd =['and', ['eq', ['$', 'BLBH'], ['s', BLBH]],
				['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]];
		var win = module.getWin();
		module.loadData();
		win.add(module.initPanel());
		win.setWidth(600);
		win.setHeight(450);
		win.show();
	},
	doCancel : function() {
		if (this.grid) {
			this.grid.destroy();
		}
	}
});