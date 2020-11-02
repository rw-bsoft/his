$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyWindowInfomationList = function(cfg) {
	phis.application.pha.script.PharmacyWindowInfomationList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyWindowInfomationList,
		phis.script.SimpleList, {
			doCancel : function() {
				this.win.hide();
			},

			// // 用于第一次打开窗口时居中
			addPanelToWin : function() {
				if (!this.fireEvent("panelInit", this.grid)) {
					return;
				};
				var win = this.getWin();
				win.add(this.grid);
				win.show();
				win.center();
			},
			doClosePharmacyWin : function() {
				// 判断是否需要停用
				var r = this.getSelectedRecord();
				if (!r)
					return;
				if (r.get("QYPB") != 1) {
					MyMessageTip.msg("提示", "选中的窗口已关闭!", true)
					return;
				}
				Ext.MessageBox.confirm("提示", "确认要关闭发药窗口【" + r.get("CKMC")
								+ "】？", function() {
							// 判断是否有发药
							this.grid.el.mask("正在保存数据...", "x-mask-loading")
							var body = r.data;
							body.QYPB = 0;
							phis.script.rmi.jsonRequest({
										serviceId : 'simpleSave',
										op : 'update',
										schema : this.entryName,
										body : body
									}, function(code, msg, json) {
										this.grid.el.unmask()
										this.saving = false
										if (code > 300) {
											this.processReturnMsg(code, msg);
											return
										}
										MyMessageTip.msg("提示", "成功关闭窗口!", true)
										this.refresh()
									}, this)// jsonRequest

						}, this)

			}
		});
