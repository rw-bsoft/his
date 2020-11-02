$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseInitialTransfer = function(cfg) {
	cfg.cnds = [
			'and',
			[
					'and',
					['and', ['eq', ['$', 'e.YKZF'], ['i', 0]],
							['eq', ['$', 'd.ZFPB'], ['i', 0]]],
					['eq', ['$', 'a.ZFPB'], ['i', 0]]],
			['eq', ['$', 'c.ZFPB'], ['i', 0]]];
	phis.application.sto.script.StorehouseInitialTransfer.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseInitialTransfer,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, "药库未初始化!", this.initPanel);
					return null;
				}
				var grid = phis.application.sto.script.StorehouseInitialTransfer.superclass.initPanel
						.call(this, sc);
				this.grid = grid;
				return grid;
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.grid.getTopToolbar();
				} else {
					btns = this.grid.buttons;
				}

				if (!btns) {
					return;
				}

				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns.items.item(m[j]);
						} else {
							btn = btns.find("cmd", m[j]);
							btn = btn[0];
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			// 页面加载时候判断是否已经初始化
			onReady : function() {
				phis.application.sto.script.StorehouseInitialTransfer.superclass.onReady
						.call(this);
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryInitialServiceAction
						});
				if (ret.code == 200) {
					this.setButtonsState(['transfer'], false);
				}
			},
			// 转账
			doTransfer : function() {
				Ext.Msg.show({
					title : "提示",
					msg : "转账前务必认真校对账册，并确保所有用户都已退出初始建账窗口，否则转账后将可能发生难以预料的数据错误 。是否继续?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.grid.el.mask("正在进行初始账册数据校对,时间可能很长,请耐心等待...",
									"x-mask-loading");
							var _ctr = this;
							var whatsthetime = function() {
								var ret = phis.script.rmi.miniJsonRequestSync({
											serviceId : _ctr.serviceId,
											serviceAction : _ctr.transferAction
										});
								if (ret.code > 300) {
									_ctr.processReturnMsg(ret.code, ret.msg,
											_ctr.doTransfer);
									_ctr.grid.el.unmask();
									return;
								}
								_ctr.grid.el.unmask();
								Ext.Msg.alert("提示", "转账成功");
								// MyMessageTip.msg("提示", "转账成功", true);
								_ctr.setButtonsState(['transfer'], false);
							}
							whatsthetime.defer(1000);
						}
					},
					scope : this
				});

			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
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
				// ** add by yzh **
				//this.resetButtons();
			},
			doPrint : function() {
				var ids = [];
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push("'"+r.get("JGID")+"_"+r.get("YPXH")+"_"+r.get("YPCD")+"'")
				}
				var cfg = {
					requestData : ids
				}
				var module = this.createModule("InitialTransfer", this.refInitialTransferPrint)
				module.requestData = ids;
				module.getWin().show();
			}
		})