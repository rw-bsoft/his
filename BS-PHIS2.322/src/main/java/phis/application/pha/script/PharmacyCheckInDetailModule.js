/**
 * 药品入库新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyCheckInDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 550;
	cfg.listIsUpdate = true;
	phis.application.pha.script.PharmacyCheckInDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckInDetailModule,
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
										title : '药品入库单',
										region : 'north',
										height : 65,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'south',
										height : 65,
										items : this.getDyList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
				this.changeButtonState("new")
				this.form.op = "create";
				this.panel.items.items[0].setTitle("药品入库单");
				this.form.checkInWayValue = this.checkInWayValue;
				this.form.isRead = this.isRead;
				this.form.doNew();
				this.list.op = "create";
				this.list.isRead = this.isRead;
				this.list.doNew();
				this.listIsUpdate = true;
				this.list.doCreate();
				this.dyForm.doNew();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData", this.afterLoad, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("loadData", this.onLoadData, this);
				this.list.on("recordAdd", this.onRecordAdd, this);
				return this.list.initPanel();
			},
			getDyList : function() {
				this.dyForm = this.createModule("dyForm", this.dyForm);
				return this.dyForm.initPanel();
			},
			doSave : function() {
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var ed = this.list.grid.activeEditor;
				if (!ed) {
					ed = this.list.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				var body = {};
				body["YF_RK02"] = [];
				var count = this.list.store.getCount();
				var sfkz = this.getSfkz();
				var _ctr = this;
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						if ((_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0 && _ctr.list.store
								.getAt(i).data["YPCD"] != null)
								&& (_ctr.list.store.getAt(i).data["RKSL"] == ''
										|| _ctr.list.store.getAt(i).data["RKSL"] == 0 || _ctr.list.store
										.getAt(i).data["RKSL"] == null)) {
							MyMessageTip.msg("提示", "第" + (i + 1) + "行数量为0",
									true);
							_ctr.panel.el.unmask();
							return;
						}
						if (_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != null
								&& _ctr.list.store.getAt(i).data["RKSL"] != ''
								&& _ctr.list.store.getAt(i).data["RKSL"] != 0
								&& _ctr.list.store.getAt(i).data["RKSL"] != null) {
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
							// 如果中心控制价格,判断下限制价格有无被修改
							if (sfkz > 0) {
								var body_jgkz = {};
								body_jgkz["YPXH"] = _ctr.list.store.getAt(i).data["YPXH"];
								body_jgkz["YPCD"] = _ctr.list.store.getAt(i).data["YPCD"];
								body_jgkz["ROW"] = i + 1;
								body_jgkz["TAG"] = "rk";
								var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : _ctr.serviceId,
									serviceAction : _ctr.queryPriceChangesServiceAction,
									body : body_jgkz
								});
								if (ret.code > 300) {
									_ctr.processReturnMsg(ret.code, ret.msg,
											_ctr.doSave);
									_ctr.panel.el.unmask();
									return;
								}
							}
							body["YF_RK02"].push(_ctr.list.store.getAt(i).data);
						}
					}
					if (body["YF_RK02"].length < 1) {
						_ctr.panel.el.unmask();
						return;
					}
					body["YF_RK01"] = _ctr.form.getFormData();
					if (!body["YF_RK01"]) {
						_ctr.panel.el.unmask();
						return;
					}
					body["YF_RK01"].YFSB = _ctr.mainApp.pharmacyId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveCheckInActionId,
								body : body,
								op : _ctr.op
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
				}
				whatsthetime.defer(500);
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			},
			doClose : function() {
				var _ctr = this;
				if (this.list.editRecords && this.list.editRecords.length > 0) {
					Ext.Msg.show({
								title : "提示",
								msg : "有已修改未保存的数据,确定不保存直接关闭?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.list.editRecords = [];
										this.fireEvent("winClose", this);
									}
								},
								scope : this
							});
					return false;
				}
				// 释放业务锁
				var initData = this.form.initDataBody;
				if (!initData)
					return;
				var p = {};
				p.YWXH = '1011';
				p.SDXH = initData.YFSB + '-' + initData.RKFS + '-'
						+ initData.RKDH;
						if(this.opener&&this.opener.bclUnlock){
						this.opener.bclUnlock(p);
						}
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
			// 修改
			loadData : function(initDataBody) {
				this.changeButtonState("update")
				this.listIsUpdate = true;
				this.doLoad(initDataBody, true);
			},
			// 查看
			doRead : function(initDataBody) {
				this.changeButtonState("read");
				this.listIsUpdate = false;
				this.doLoad(initDataBody);
			},
			// 提交按钮打开界面,除了确定按钮其他按钮全屏蔽掉
			doOpneCommit : function(initDataBody) {
				this.initDataBody = initDataBody;
				this.changeButtonState("commit");
				this.listIsUpdate = false;
				this.doLoad(initDataBody);
			},
			// 提交入库单
			doCommit : function() {
				// 提交前判断是否中心控制价格和有没超过限制价格
				var sfkz = this.getSfkz();
				if (sfkz > 0) {
					var count = this.list.store.getCount();
					for (var i = 0; i < count; i++) {
						var body_jgkz = {};
						body_jgkz["YPXH"] = this.list.store.getAt(i).data["YPXH"];
						body_jgkz["YPCD"] = this.list.store.getAt(i).data["YPCD"];
						body_jgkz["ROW"] = i + 1;
						body_jgkz["TAG"] = "rk";
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryPriceChangesServiceAction,
							body : body_jgkz
						});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.doSave);
							return;
						}
					}
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveCheckInToInventoryActionId,
							body : this.initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					return;
				}
				Ext.Msg.alert("提示", "入库单提交成功");
				this.list.doInit();
				this.setButtonsState(["commit"], false);
				this.fireEvent("winClose", this);
				this.fireEvent("commit", this);
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				this.form.isRead = false;
				if (state == "read") {
					this.form.isRead = true;
				}
				if (state == "read" || state == "commit") {
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
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody) {
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.isRead = this.isRead;
				this.form.loadData();
				this.list.op = "create";
				this.list.isRead = this.isRead;
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'RKFS'], ['i', initDataBody.RKFS]],
						[
								'and',
								['eq', ['$', 'RKDH'], ['i', initDataBody.RKDH]],
								['eq', ['$', 'YFSB'], ['l', initDataBody.YFSB]]]];
				this.list.loadData();
			},
			// 页面加载的时候集合零售合计和进货合计
			onLoadData : function(store) {
				if (store) {
					var count = store.getCount();
					var allJhje = 0;
					var allLsje = 0;
					for (var i = 0; i < count; i++) {
						var jhje = store.getAt(i).data["JHJE"];
						var lsje = store.getAt(i).data["LSJE"];
						allJhje = parseFloat(parseFloat(allJhje)
								+ parseFloat(jhje)).toFixed(4);
						allLsje = parseFloat(parseFloat(allLsje)
								+ parseFloat(lsje)).toFixed(4);
					}
					this.dyForm.doNew();
					this.dyForm.addJe(allJhje, allLsje);
					if (this.listIsUpdate) {
						this.list.doCreate();
					}
				}
			},
			// 明细增加的时候 增加下面的合计
			onRecordAdd : function(jhje, lsje) {
				this.dyForm.addJe(jhje, lsje);
			},
			// 获取是否需要控制价格
			getSfkz : function() {
				if (this.sfkz == undefined) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.queryControlPricesServiceId,
								serviceAction : this.queryControlPricesServiceAction
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.getSfkz);
						return null;
					}
					this.sfkz = ret.json.sfkz;
				}
				return this.sfkz;
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.RKDH);
			}
		});