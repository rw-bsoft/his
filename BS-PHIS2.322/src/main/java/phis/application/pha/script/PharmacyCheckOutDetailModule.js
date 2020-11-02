/**
 * 药品出库新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyCheckOutDetailModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	cfg.listIsUpdate = true;
	phis.application.pha.script.PharmacyCheckOutDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckOutDetailModule,
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
										title : '药品出库单',
										region : 'north',
										width : 960,
										height : 65,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'south',
										width : 960,
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
				this.panel.items.items[0].setTitle("药品出库单");
				this.form.checkOutWayValue = this.checkOutWayValue;
				this.form.doNew();
				this.list.op = "create";
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
				body["YF_CK02"] = [];
				var count = this.list.store.getCount();
				var _ctr = this;
				var whatsthetime = function() {
					for (var i = 0; i < count; i++) {
						if (_ctr.list.store.getAt(i).data["YPXH"] != ''
								&& _ctr.list.store.getAt(i).data["YPXH"] != null
								&& _ctr.list.store.getAt(i).data["YPXH"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != ''
								&& _ctr.list.store.getAt(i).data["YPCD"] != 0
								&& _ctr.list.store.getAt(i).data["YPCD"] != null
								&& _ctr.list.store.getAt(i).data["CKSL"] != ''
								&& _ctr.list.store.getAt(i).data["CKSL"] != 0
								&& _ctr.list.store.getAt(i).data["CKSL"] != null) {
							body["YF_CK02"].push(_ctr.list.store.getAt(i).data);
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
						}
					}
					if (body["YF_CK02"].length < 1) {
						MyMessageTip.msg("提示", "无药品明细", true);
						_ctr.panel.el.unmask();
						return;
					}
					body["YF_CK01"] = _ctr.form.getFormData();
					if (!body["YF_CK01"]) {
						_ctr.panel.el.unmask();
						return;
					}
					body["YF_CK01"].YFSB = _ctr.mainApp.pharmacyId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : _ctr.serviceId,
								serviceAction : _ctr.saveCheckOutActionId,
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
				p.YWXH = '1012';
				p.SDXH = initData.YFSB + '-' + initData.CKFS + '-'
						+ initData.CKDH;
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
				this.doLoad(initDataBody);
			},
			// 查看
			// doRead : function(initDataBody) {
			// this.changeButtonState("read");
			// this.doLoad(initDataBody);
			// },
			// 提交按钮打开界面,除了确定按钮其他按钮全屏蔽掉
			doOpneCommit : function(initDataBody) {
				this.initDataBody = initDataBody;
				this.changeButtonState("commit");
				this.listIsUpdate = false;
				this.doLoad(initDataBody);
			},
			// 提交出库单
			doCommit : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveCheckOutToInventoryActionId,
							body : this.initDataBody
						});
				if (ret.code > 300) {
					if (ret.code == 9000 && ret.json.ypmc != undefined) {
						ret.msg = "药品[" + ret.json.ypmc + "]库存不足";
					}
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					return;
				}
				Ext.Msg.alert("提示", "出库单提交成功");
				this.list.doInit();
				this.setButtonsState(["commit"], false);
				this.fireEvent("winClose", this);
				this.fireEvent("commit", this);
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				this.form.isRead = false;
				// if(state=="read"){
				// this.form.isRead=true;
				// }
				if (state == "read" || state == "commit") {
					this.list.setButtonsState(["create", "remove"], false);
					this.form.isRead = true;
				} else {
					this.list.setButtonsState(["create", "remove"], true);
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.id == "cancel" || action.id == "print") {
						continue;
					}
					// if (state == "read") {
					// this.setButtonsState([action.id], false);
					// } else
					if (state == "update" || state == "new") {
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
				this.form.loadData();
				this.list.op = "create";
				this.list.requestData.serviceId = this.fullserviceId;
				this.list.requestData.serviceAction = this.queryActionId;
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.CKFS'], ['i', initDataBody.CKFS]],
						[
								'and',
								['eq', ['$', 'a.CKDH'],
										['i', initDataBody.CKDH]],
								['eq', ['$', 'a.YFSB'],
										['i', initDataBody.YFSB]]]];
				this.list.loadData();

			},
			// 页面加载的时候计算零售合计和进货合计
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
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.CKDH);
			}
		});