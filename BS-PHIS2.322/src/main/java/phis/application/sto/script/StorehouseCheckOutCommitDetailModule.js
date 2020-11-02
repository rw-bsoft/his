/**
 * 药库出库提交界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailModule");

phis.application.sto.script.StorehouseCheckOutCommitDetailModule = function(cfg) {
	phis.application.sto.script.StorehouseCheckOutCommitDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutCommitDetailModule,
		phis.application.sto.script.StorehouseMySimpleDetailModule, {
			unlock : function() {
				var initData = this.initDataBody;
				if (!initData)
					return;
				var p = {};
				p.YWXH = '1022';
				p.SDXH = initData.xtsb + '-' + initData.ckfs + '-'
						+ initData.ckdh;
				this.opener.bclUnlock(p);
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.CKDH);
			},
			doLoad : function(initDataBody) {
				if (this.isRead) {
					// initDataBody["ksly"] = this.condition.ksly;
					this.form.loadData(initDataBody);
					this.list.requestData.body = initDataBody;
					this.list.requestData.body.isRead = 1;
					this.list.requestData.serviceId = "phis.storehouseManageService";
					this.list.requestData.serviceAction = this.queryActionId;
					this.list.loadData();
				} else {
					initDataBody["ksly"] = this.condition.ksly;
					this.form.loadData(initDataBody);
					this.list.requestData.body = initDataBody;
					this.list.requestData.serviceId = "phis.storehouseManageService";
					this.list.requestData.serviceAction = this.queryActionId;
					this.list.dyfs = this.condition.dyfs;
					this.list.loadData();
				}
			},
			doCommit : function() {
				var body = {};
				var ck01 = this.form.getFormData();
				body["YK_CK01"] = ck01;
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var ck02 = [];
				var _ctr = this;
				var count = this.list.store.getCount();
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						if (_ctr.condition.dyfs != 6
								&& _ctr.list.store.getAt(i).data.SFSL < 0) {
							MyMessageTip.msg("提示", "第" + (i + 1)
											+ "行实发数量不能小于零!如果是退库,请从药房退库模块中操作!",
									true);
							_ctr.panel.el.unmask();
							return;
						}
						// 测试说去掉为0的判断,zw2确认过
						// if (_ctr.list.store.getAt(i).data.SFSL == 0
						// || _ctr.list.store.getAt(i).data.SFSL == ""
						// || _ctr.list.store.getAt(i).data.SFSL == undefined) {
						// MyMessageTip.msg("提示",
						// "第" + (i + 1) + "行实发数量不能为空!", true);
						// _ctr.panel.el.unmask();
						// return;
						// }
						if (_ctr.list.store.getAt(i).data.SFSL > _ctr.list.store
								.getAt(i).data.KCSL) {
							MyMessageTip.msg("提示", "第" + (i + 1)
											+ "实发数量大于库存数量!", true);
							_ctr.panel.el.unmask();
							return;
						}
						ck02.push(_ctr.list.store.getAt(i).data);
					}
					body["YK_CK02"] = ck02;
					for (var i = 0; i < count; i++) {
						if (_ctr.list.store.getAt(i).data.SFSL < _ctr.list.store
								.getAt(i).data.SQSL) {
							_ctr.panel.el.unmask();
							Ext.Msg.show({
								title : "提示",
								msg : "第" + (i + 1) + "行实发数量小于申请数量,是否继续?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										_ctr.panel.el.mask("正在保存...",
												"x-mask-loading");
										var r = phis.script.rmi
												.miniJsonRequestSync({
													serviceId : _ctr.serviceId,
													serviceAction : _ctr.saveCheckOutActionId,
													body : body
												});
										_ctr.panel.el.unmask();
										if (r.code > 300) {
											_ctr.processReturnMsg(r.code,
													r.msg, _ctr.doCommit);
											return;
										} else {
											_ctr.list.isEdit = false;
											MyMessageTip.msg("提示", "提交成功!",
													true);
											_ctr.fireEvent("winClose", _ctr);
											_ctr.fireEvent("commit", _ctr);
										}
									}
								},
								scope : _ctr
							});
							return;
						}
					}
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveCheckOutActionId,
								body : body
							});
					if (r.code > 300) {
						_ctr.processReturnMsg(r.code, r.msg, _ctr.doCommit);
						_ctr.panel.el.unmask();
						return;
					} else {
						_ctr.list.isEdit = false;
						MyMessageTip.msg("提示", "提交成功!", true);
						_ctr.fireEvent("winClose", _ctr);
						_ctr.fireEvent("commit", _ctr);
					}
					_ctr.panel.el.unmask();
				}
				whatsthetime.defer(500);

			},
			doPrint : function() {
				var module = this.createModule("storehouseoutprint",
						this.refStorehouseListPrint)
				var data = this.form.getFormData();

				module.yfsb = data.YFSB;
				module.ckfs = data.CKFS;
				module.ckdh = data.CKDH;
				if (data.CKKS) {
					module.ckks = data.CKKS;
				} else {
					module.ckks = -1;
				}

				// module.fdjs = data.FDJS;
				module.initPanel();
				module.doPrint();
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				this.form.isRead = false;
				this.list.isRead = false;
				if (state == "read" || state == "commit") {
					this.form.isRead = true;
					if (state == "read") {
						this.list.isRead = true;
					}
					this.list.setButtonsState(["create", "remove"], false);
				} else {
					this.list.setButtonsState(["create", "remove"], true);
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.id == "cancel" || action.id == "print") {
						continue;
					}
					if (state == "read") {
						this.setButtonsState([action.id], false);
					} else if (state == "update" || state == "new") {
						if (action.id == "commit") {
							this.setButtonsState([action.id], false);
							continue;
						}
						this.setButtonsState([action.id], true);
					} else if (state == "commit") {
						if (action.id == "commit") {
							this.setButtonsState([action.id], true);
							continue;
						}
						this.setButtonsState([action.id], false);
					}
				}

			}
		})