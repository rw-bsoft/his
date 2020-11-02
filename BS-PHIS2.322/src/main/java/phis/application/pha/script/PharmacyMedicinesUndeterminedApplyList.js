$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyMedicinesUndeterminedApplyList = function(
		cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds = ['ne', ['$', 'LYPB'], ['i', 1]];
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	this.isfirst = 0;// 控制出库方式下拉框
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyMedicinesUndeterminedApplyList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.pha.script.PharmacyMedicinesUndeterminedApplyList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			getCndBar : function(items) {
				this.storehouse = this.getStorehouse();
				var filelable = new Ext.form.Label({
							text : "领药库房"
						})

				this.storehouse.on("select", this.onStorehouseSelect, this)
				return [filelable, '-', this.storehouse];
			},
			// 选中药库抛出事件,刷新两边界面
			onStorehouseSelect : function(item, record, e) {
				this.fireEvent("storehouseSelect", record);
			},
			doRefresh : function() {
				var record = {};
				record["data"] = this.yksb;
				this.fireEvent("storehouseSelect", record);
			},
			// 刷新页面
			doRefreshWin : function() {
				var body = {};
				body["yksb"] = this.yksb.yksb;
				body["cnd"] = this.cnds;
				this.requestData.body = body;
				this.refresh();
			},
			// 新增
			doAdd : function() {
				if (!this.yksb) {
					Ext.Msg.alert("提示", "请先选择领药库房");
					return;
				}
				var body = {};
				body["yksb"] = this.yksb.yksb
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryCkfsActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doAdd);
					return;
				}
				var ckfs = ret.json.ckfs;
				this.applyModule = this
						.createModule("applyModule", this.addRef);
				this.applyModule.on("save", this.doRefresh, this);
				this.applyModule.on("winClose", this.onClose, this);
				this.applyModule.yksb = this.yksb;
				this.applyModule.ckfs = ckfs;
				this.applyModule.initPanel();
				var win = this.getWin();
				win.add(this.applyModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.applyModule.op = "create";
					this.applyModule.doNew();
				}
			},
			onClose : function() {
				this.getWin().hide();
			},
			doCancel : function() {
				if (this.applyModule) {
					return this.applyModule.doClose();
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["xtsb"] = r.data.XTSB;
				initDataBody["ckfs"] = r.data.CKFS;
				initDataBody["ckdh"] = r.data.CKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationApplyDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doUpd);
					this.doRefresh();
					return;
				}
				if (!this.lock(r))
					return;
				this.applyModule = this
						.createModule("applyModule", this.addRef);
				this.applyModule.on("save", this.doRefresh, this);
				this.applyModule.on("winClose", this.onClose, this);
				this.applyModule.opener = this;
				var win = this.getWin();
				this.applyModule.yksb = this.yksb;
				win.add(this.applyModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.applyModule.op = "update";
					initDataBody["czpb"] = r.data.CZPB;
					this.applyModule.loadData(initDataBody);
				}

			},
			lock : function(r) {
				var p = {};
				p.YWXH = '1013';
				p.SDXH = r.data.XTSB + '-' + r.data.CKFS + '-' + r.data.CKDH;
				return this.bclLock(p);
			},
			unlock : function(r) {
				var p = {};
				p.YWXH = '1013';
				p.SDXH = r.data.XTSB + '-' + r.data.CKFS + '-' + r.data.CKDH;
				return this.bclUnlock(p);
			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "修改";
				item.cmd = "upd";
				this.doAction(item, e)

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
				if (r.data.CZPB != 1) {
					MyMessageTip.msg("提示", "已提交未确认领药单不能删除", true);
					return;
				}
				if (!this.lock(r))
					return;
				var body = {};
				body["xtsb"] = r.data.XTSB;
				body["ckfs"] = r.data.CKFS;
				body["ckdh"] = r.data.CKDH;
				var record = {};
				var data = {};
				data["key"] = this.checkOutWayValue;
				record["data"] = data;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeApplyDataActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							this.unlock(r);
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
							this.doRefresh();
						}, this)
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title || this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
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
			getStorehouse : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryStorehouseActionID
						});
				if (ret.code > 300) {
					this
							.processReturnMsg(ret.code, ret.msg,
									this.getStorehouse);
					return null;
				}
				var yklb = ret.json.yklb;
				var proxy = new Ext.data.MemoryProxy(yklb);
				var YKLB = Ext.data.Record.create([{
							name : "yksb",
							type : "long",
							mapping : 0
						}, {
							name : "ykmc",
							type : "string",
							mapping : 1
						}]);
				var reader = new Ext.data.ArrayReader({}, YKLB);
				var store = new Ext.data.Store({
							proxy : proxy,
							reader : reader
						});
				store.load();
				var lb = new Ext.form.ComboBox({
							name : "yklb",
							mode : "local",
							triggerAction : "all",
							displayField : "ykmc",
							valueField : "yksb",
							store : store
						})
				if (ret.json.yklb.length > 0) {
					lb.setValue(ret.json.yklb[0][0]);
					this.yksb = {
						"yksb" : ret.json.yklb[0][0],
						"ykmc" : ret.json.yklb[0][1]
					};
				}
				return lb;
			},
			doCommit : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				if (r.data.CZPB != 1) {
					return;
				}
				var body = {};
				body["xtsb"] = r.data.XTSB;
				body["ckfs"] = r.data.CKFS;
				body["ckdh"] = r.data.CKDH;
				if (!this.lock(r))
					return;
				Ext.Msg.show({
					title : "提示",
					msg : "确实提交该领药单" + r.data.CKDH + "吗?",
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						this.unlock(r);
						if (btn == "ok") {
							this.mask("在提交数据...");
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.updateApplyDataActionId,
										body : body
									});
							this.unmask()
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doUpd);
								this.doRefresh();
								return;
							}
							this.doRefresh();
						}
					},
					scope : this
				});
			},
			doQr : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				if (r.data.CZPB != 3) {
					return;
				}
				this.doUpd();
			},
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				if (r.data.CZPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/add.gif'/>"
					// return "<img src='images/add.gif'/>";
				}
				if (r.data.CZPB == 2) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/i_warn.gif'/>"
					// return "<img src='images/i_warn.gif'/>";
				}
				if (r.data.CZPB == 3) {
					return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/grid.png'/>"
					// return "<img src='images/grid.png'/>";
				}

			}
		})