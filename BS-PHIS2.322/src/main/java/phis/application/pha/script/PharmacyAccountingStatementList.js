$package("phis.application.pha.script")
$import("phis.script.SimpleList")
phis.application.pha.script.PharmacyAccountingStatementList = function(cfg) {
	cfg.modal = this.modal = true;
	cfg.closeAction = "hide";
	phis.application.pha.script.PharmacyAccountingStatementList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyAccountingStatementList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceActionId
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onBeforeSave);
					return null;
				}
				var grid = phis.application.pha.script.PharmacyAccountingStatementList.superclass.initPanel
						.call(this, sc);
				this.grid = grid;
				return grid;
			},
			// 打开月结form
			doMonthly : function() {
				this.monthlyForm = this.createModule("monthlyForm",
						this.refForm);
				this.monthlyForm.on("save", this.onSave, this);
				this.monthlyForm.on("close", this.onClose, this);
				var win = this.getWin();
				win.add(this.monthlyForm.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.monthlyForm.loadData();
				}
			},
			onClose : function() {
				var win = this.getWin();
				if (!win.hidden) {
					win.hide();
				}
			}

		})