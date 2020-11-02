$package("phis.application.sto.script")
$import("phis.application.sto.script.StorehouseMySimpleLeftList");

phis.application.sto.script.StorehouseUndeterminedCheckOutList = function(cfg) {
	cfg.cnds = ['and', ['ne', ['$', 'CKPB'], ['i', 1]],
			['ne', ['$', 'SQTJ'], ['i', 0]]];
	phis.application.sto.script.StorehouseUndeterminedCheckOutList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseUndeterminedCheckOutList,
		phis.application.sto.script.StorehouseMySimpleLeftList, {
			lock : function(r) {
				var p = {};
				p.YWXH = '1022';
				p.SDXH = r.data.XTSB + '-' + r.data.CKFS + '-' + r.data.CKDH;
				return this.bclLock(p);
			},
			unlock : function(r) {
				var p = {};
				p.YWXH = '1022';
				p.SDXH = r.data.XTSB + '-' + r.data.CKFS + '-' + r.data.CKDH;
				this.bclUnlock(p);
			},
			getDicFitle : function() {
				return ['eq', ['$', 'item.properties.XTSB'],
						['l', this.mainApp['phis'].storehouseId]];
			},
			// 选中入库方式抛出事件,刷新两边界面
			onSelect : function(item, record, e) {
				var body = {};
				body["ckfs"] = record.data.key;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.onSelect);
					return;
				}
				this.dyfs = ret.json.body.DYFS;
				this.ksly = ret.json.body.KSPB;
				this.yfsb = ret.json.body.YFSB;
				if (this.dyfs == 6) {
					this.setButtonsState(['add', 'back', 'remove', 'upd'],
							false);
				} else {
					this.setButtonsState(['add', 'remove', 'upd'], true);
					if (this.ksly == 1) {
						this.setButtonsState(['back'], false);
					} else {
						this.setButtonsState(['back'], true);
					}
				}
				record.data["ksly"] = this.ksly;
				this.fireEvent("select", record);
			},
			doRefresh : function() {
				var record = {};
				var data = {};
				data["key"] = this.selectValue;
				data["ksly"] = this.ksly;
				record["data"] = data;
				this.fireEvent("select", record);
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.selectValue) {
					var addCnd = ['eq', ['$', 'CKFS'], ['s', this.selectValue]];
					this.requestData.cnd = ['and', addCnd, this.cnds];
					this.refresh();
					return;
				}
			},
			getAddCondition : function() {
				return {
					"yfsb" : this.yfsb,
					"dyfs" : this.dyfs,
					"ksly" : this.ksly
				};
			},
			getCommitCondition : function() {
				return {
					"yfsb" : this.yfsb,
					"dyfs" : this.dyfs,
					"ksly" : this.ksly
				};
			},
			getUpdateCondition : function() {
				return {
					"yfsb" : this.yfsb,
					"dyfs" : this.dyfs,
					"ksly" : this.ksly
				};
			},
			onCommit : function() {
				var record = {};
				var data = {};
				data["key"] = this.selectValue;
				data["ksly"] = this.ksly;
				record["data"] = data;
				this.fireEvent("select", record);
			},
			onDblClick : function(grid, index, e) {
				if (this.dyfs == 6) {
					return;
				}
				var item = {};
				item.text = "修改";
				item.cmd = "upd";
				this.doAction(item, e)
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
			// 退回
			doBack : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				Ext.Msg.show({
							title : "提示",
							msg : "确定要将出库单" + r.data.CKDH + "退回申领单位吗?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var body = {};
									body["xtsb"] = r.data.XTSB;
									body["ckfs"] = r.data.CKFS;
									body["ckdh"] = r.data.CKDH;
									var ret = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : this.backActionId,
												body : body
											});
									if (ret.code > 300) {
										this.processReturnMsg(ret.code,
												ret.msg, this.doBack);
										this.doRefresh();
										return;
									}
									MyMessageTip.msg("提示", "退回成功", true);
									this.doRefresh();
								}
							},
							scope : this
						});

			},
			// 重写防止改变按钮状态失效
			resetButtons : function() {
			},
			getInitDataBody : function(r) {
				var initDataBody = {};
				initDataBody["xtsb"] = r.data.XTSB;
				initDataBody["ckfs"] = r.data.CKFS;
				initDataBody["ckdh"] = r.data.CKDH;
				return initDataBody;
			},
			doPrint : function() {
				var module = this.createModule("storehouseoutprint",
						this.refStorehouseListPrint)
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
					return;
				}
				module.yfsb = r.data.YFSB;
				module.ckfs = r.data.CKFS;
				module.ckdh = r.data.CKDH;
				if (r.data.CKKS) {
					module.ckks = r.data.CKKS;
				} else {
					module.ckks = -1;
				}

				// module.fdjs = r.data.FDJS;
				module.initPanel();
				module.doPrint();
			}
		})