$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.TreasuryMonthly = function(cfg) {
	cfg.modal = true;
	cfg.width = 250;
	cfg.height = 300;
	phis.application.sup.script.TreasuryMonthly.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.TreasuryMonthly, phis.script.SimpleList, {
			initPanel : function(sc) {
				if (!this.mainApp['phis'].treasuryId) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryCsbz == 0) {
					Ext.Msg.alert("提示", "该库房账册未初始化,不能进行业务操作!");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.MessageBox.alert("提示", "该库房处于盘点状态,不能月结!");
					return;
				}
				var grid = phis.application.sup.script.TreasuryMonthly.superclass.initPanel
						.call(this, sc);
				this.grid = grid;
				return grid;
			},
			loadData : function() {
				this.clear();
				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.treasuryMonthlyService";
				this.requestData.serviceAction = "queryYJJLInfo";
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			doMonthly : function() {
				this.moudle = this.createModule("treasuryMonthlyForm",this.refForm);
				this.moudle.on("save", this.onSave, this);
				this.moudle.on("close", this.onClose, this);
				var p = this.getWin();
				p.add(this.moudle.initPanel())
				p.show();
				p.center();
				if (!p.hidden) {
					this.moudle.op = "create";
					this.moudle.doNew();
				}
			},
			doCancelMonthly : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "请选择取消月结的记录!", true);
					return
				}
				var body = {};
				body["CWYF"] = r.data.CWYF;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "treasuryMonthlyService",
							serviceAction : "deleteTreasuryMonthly",
							body : body
						});
				if (ret.code > 200) {
					this.processReturnMsg(ret.code, ret.msg,
							this.doCancelMonthly);
					return;
				}
				MyMessageTip.msg("提示", ret.msg, true);
				this.refresh();
			},
			onClose : function() {
				this.getWin().hide();
				this.refresh();
			},
			onSave : function() {
				this.refresh();
			}
		})