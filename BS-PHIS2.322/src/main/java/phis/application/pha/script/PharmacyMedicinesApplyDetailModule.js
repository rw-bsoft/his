$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyMedicinesApplyDetailModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.pha.script.PharmacyMedicinesApplyDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.pha.script.PharmacyMedicinesApplyDetailModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '药品申领单',
										region : 'north',
										width : 960,
										height : 98,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			// 改变按钮状态
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.panel.getTopToolbar();
				if (!btns) {
					return;
				}
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
			},
			doNew : function() {
				this.form.isCommit = false;
				this.list.isCommit = false;
				this.list.setButtonsState(['create', 'remove'], true);
				this.list.isEdit = false;
				this.setButtonsState(['save'], true);
				this.setButtonsState(['commit'], false);
				this.form.op = "create";
				this.panel.items.items[0].setTitle("药品申领单");
				this.form.yksb = this.yksb.yksb;
				this.form.ckfs = this.ckfs;
				this.form.doNew();
				this.list.op = "create";
				this.list.remoteDicStore.baseParams = {
					"tag" : "ypsl",
					"yksb" : this.yksb.yksb
				}
				this.list.yksb = this.yksb.yksb;
				this.form.ckfs = this.ckfs;
				this.list.doNew();
				this.list.doCreate();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData", this.afterLoad, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			},
			doClose : function() {
				if (this.list.isEdit) {
					Ext.Msg.show({
								title : "提示",
								msg : "有已修改未保存的数据,确定不保存直接关闭?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.list.isEdit = false;
										// this.getWin().hide();
										this.fireEvent("winClose", this);
									}
								},
								scope : this
							});
					return false;
				}
				var initData = this.form.initDataBody;
				if (!initData)
					return;
				var p = {};
				p.YWXH = '1013';
				p.SDXH = initData.XTSB + '-' + initData.CKFS + '-'
						+ initData.CKDH;
				this.opener.bclUnlock(p);
				return true;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.CKDH);
			},
			// 保存
			doSave : function() {
				var body = {};
				var ck01 = this.form.getFormData();
				if (ck01 == null) {
					return;
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				ck01["XTSB"] = this.yksb.yksb;
				body["YK_CK01"] = ck01;
				var count = this.list.store.getCount();
				var ck02 = [];
				var _ctr = this;
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						if (_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != null
								&& _ctr.list.store.getAt(i).data["SQSL"] != ''
								&& _ctr.list.store.getAt(i).data["SQSL"] != 0
								&& _ctr.list.store.getAt(i).data["SQSL"] != null) {
							if (_ctr.list.store.getAt(i).data.LSJE > 99999999.99) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行零售金额超过最大值", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (_ctr.list.store.getAt(i).data.JHJE > 99999999.99) {
								MyMessageTip.msg("提示", "第" + (i + 1)
												+ "行进货金额超过最大值", true);
								_ctr.panel.el.unmask();
								return;
							}
							ck02.push(_ctr.list.store.getAt(i).data);
						}
					}
					body["YK_CK02"] = ck02;
					if (body["YK_CK02"].length < 1) {
						_ctr.panel.el.unmask();
						MyMessageTip.msg("提示", "无明细不能保存!", true);
						return;
					}
					body["op"] = _ctr.op;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveApplyActionId,
								body : body
							});
					if (r.code > 300) {
						_ctr.processReturnMsg(r.code, r.msg, _ctr.onBeforeSave);
						_ctr.panel.el.unmask();
						return;
					} else {
						_ctr.list.doInit();
						_ctr.fireEvent("winClose", _ctr);
						_ctr.fireEvent("save", _ctr);
					}
					_ctr.op = "update";
					_ctr.panel.el.unmask();
					_ctr.list.isEdit = false;
					_ctr.doCancel();
				}
				whatsthetime.defer(500);
				this.list.isEdit = false;
			},
			loadData : function(initDataBody) {
				this.list.isEdit = false;
				var czpb = initDataBody.czpb;
				if (czpb == 1) {
					this.setButtonsState(['save'], true);
					this.setButtonsState(['commit'], false);
				} else if (czpb == 2) {
					this.setButtonsState(['commit', 'save'], false);
				} else if (czpb == 3) {
					this.setButtonsState(['commit'], true);
					this.setButtonsState(['save'], false);
				} else {
					this.setButtonsState(['save', 'commit'], false);
				}
				if (czpb == 1 || czpb == 2) {
					this.form.isCommit = false;
					this.list.isCommit = false;
					this.list.setButtonsState(['create', 'remove'], true);
				} else {
					this.form.isCommit = true;
					this.list.isCommit = true;
					this.list.setButtonsState(['create', 'remove'], false);
				}
				this.form.op = "update";
				var body = {};
				body["XTSB"] = initDataBody.xtsb;
				body["CKFS"] = initDataBody.ckfs;
				body["CKDH"] = initDataBody.ckdh;
				this.form.initDataBody = body;
				this.form.loadData();
				this.list.op = "update";
				this.list.yksb = this.yksb.yksb
				this.list.remoteDicStore.baseParams = {
					"tag" : "ypsl",
					"yksb" : this.yksb.yksb
				}
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.CKFS'], ['i', initDataBody.ckfs]],
						[
								'and',
								['eq', ['$', 'a.CKDH'],
										['i', initDataBody.ckdh]],
								['eq', ['$', 'a.XTSB'],
										['i', initDataBody.xtsb]]]];
				this.list.loadData();
			},
			doCommit : function() {
				var body = {};
				var ck01 = this.form.getFormData();
				body["YK_CK01"] = ck01;
				var count = this.list.store.getCount();
				var ck02 = [];
				var _ctr = this;
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						ck02.push(_ctr.list.store.getAt(i).data);
					}
					body["YK_CK02"] = ck02;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveApplyCommitActionId,
								body : body
							});
					if (r.code > 300) {
						_ctr.processReturnMsg(r.code, r.msg, _ctr.onBeforeSave);
						_ctr.panel.el.unmask();
						return;
					} else {
						_ctr.list.doInit();
						_ctr.fireEvent("winClose", _ctr);
						_ctr.fireEvent("save", _ctr);
					}
				}
				whatsthetime.defer(500);
				this.list.isEdit = false;

			}
		})