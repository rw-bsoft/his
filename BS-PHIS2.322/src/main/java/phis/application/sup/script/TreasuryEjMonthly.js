$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.TreasuryEjMonthly = function(cfg) {
	cfg.modal = true;
	cfg.width = 250;
	cfg.height = 300;
	cfg.autoLoadData = false;
	phis.application.sup.script.TreasuryEjMonthly.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.TreasuryEjMonthly, phis.script.SimpleList, {
			initPanel : function(sc) {
				if (!this.mainApp['phis'].treasuryId) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryCsbz == 0) {
					Ext.Msg.alert("提示", "该库房账册未初始化,不能进行业务操作!");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf == 0) {
					Ext.MessageBox.alert("提示", "该库房不是二级库房!");
					return;
				}
				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.MessageBox.alert("提示", "该库房处于盘点状态,不能月结!");
					return;
				}
				var grid = phis.application.sup.script.TreasuryEjMonthly.superclass.initPanel
						.call(this, sc);
				this.grid = grid;
				return grid;
			},
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.TreasuryEjMonthly.superclass.onReady
							.call(this);
					this.requestData.cnd = [
							'and',
							['eq', ['$', 'CSBZ'], ['i', 0]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					this.initCnd = [
							'and',

							['eq', ['$', 'CSBZ'], ['i', 0]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					// 设置分页信息
					this.loadData();
				}
			},
			doMonthly : function() {
				this.moudle = this.createModule("treasuryEjMonthlyForm",
						this.refForm);
				this.moudle.on("save", this.onSave, this);
				this.moudle.on("close", this.onClose, this);
				var p = this.getWin();
				p.add(this.moudle.initPanel())
				p.show();
				p.center()
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
							serviceAction : "deleteTreasuryEjMonthly",
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