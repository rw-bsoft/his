/**
 * @include "../../desktop/Module.js"
 * @include "../common.js"
 * @include "../../../util/Accredit.js"
 * 
 */
$package("com.bsoft.phis.pub")

com.bsoft.phis.pub.UserCollateList = function(cfg) {
	cfg.disablePagingTbr = true;
	com.bsoft.phis.pub.UserCollateList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(com.bsoft.phis.pub.UserCollateList, com.bsoft.phis.SimpleList, {
			onWinShow : function() {
				this.grid.el.mask("数据载入中...");
				phis.script.rmi.jsonRequest({
							serviceId : "userManageService",
							serviceAction : "loadPubHealthUsers",
							body : {
								manaUnitId : this.manaUnitId
							}
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code < 300) {
								var body = json.body;
								if (body) {
									// alert(Ext.encode(body))
									this.loadDataByLocal(body);
								}
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this)
			},
			doSave : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					MyMessageTip.msg("警告", "请先选择需要对照的用户信息!", true);
					return;
				}
				this.fireEvent("doChoose", r);
				this.win.hide();
			},
			doCancel : function() {
				this.win.hide();
			},
			onDblClick : function(grid, index, e) {
				this.doSave();
			}
		});
