/**
 * 入库管理修改界面
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.InventoryKSModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.InventoryKSModule.superclass.constructor.apply(
			this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.InventoryKSModule,
				phis.script.SimpleModule,
				{
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
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'north',
								width : 960,
								height : 85,
								items : this.getForm()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								width : 960,
								items : this.getList()
							} ],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
						this.panel = panel;
						return panel;
					},
					getForm : function() {
						this.form = this.createModule("form", this.refForm);
						return this.form.initPanel();
					},
					getList : function() {
						this.list = this.createModule("list", this.refList);
						this.list.grid = this.list.initPanel();
						return this.list.grid;
					},
					doNew : function() {
						if (this.list) {
							this.form.listoper = this.list;
							this.form.doNew();
							this.changeButtonState("new");
							this.form.op = "create";
							this.list.op = "create";
							this.list.clear();
							this.list.editRecords = [];
						}
					},
					doUpd : function() {
						if (this.list) {
							this.form.listoper = this.list;
							this.form.op = "update";
							this.list.op = "update";
							this.list.clear();
							this.list.doCreate();
						}
						var btns = this.panel.getTopToolbar();
						var btn = btns.find("cmd", "verify");
						btn = btn[0];
						this.form.btn = btn;
					},
					doCreate : function() {
						var winclose = false;
						this.beforeClose(winclose);
						this.doNew();
					},
					doSave : function() {
						if (this.form.getFormData()) {
							this.panel.el.mask("正在保存...", "x-mask-loading");
							var body = {};
							body["WL_PD02"] = [];
							var count = this.list.store.getCount();
							var _ctr = this;
							if (!_ctr.form.form.getForm().findField("LZFS")
									.getValue()) {
								MyMessageTip.msg("提示", "流转方式不能为空!", true);
								_ctr.panel.el.unmask();
								return;
							}
							var whatsthetime = function() {
								for ( var i = 0; i < count; i++) {
									body["WL_PD02"].push(_ctr.list.store
											.getAt(i).data);
								}
								if (body["WL_PD02"].length < 1) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_PD01"] = _ctr.form.getFormData();
								if (!body["WL_PD01"]) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_PD01"].KFXH = _ctr.mainApp.treasuryId;
								body["WL_PD01"].ZBLB = _ctr.zblb;
								var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : _ctr.serviceId,
									serviceAction : _ctr.saveInventoryActionId,
									body : body,
									op : _ctr.op
								});
								if (r.code > 300) {
									_ctr.processReturnMsg(r.code, r.msg,
											_ctr.onBeforeSave);
									_ctr.panel.el.unmask();
									return;
								} else {
									_ctr.list.doInit();
									_ctr.fireEvent("save", _ctr);
									_ctr.changeButtonState("save");
									_ctr.mainApp.treasuryPdzt = 1
									_ctr.doClose();
								}
								_ctr.op = "update";
								_ctr.panel.el.unmask();
							}
							whatsthetime.defer(500);
						}
					},
					doVerify : function() {
						if (this.form.getFormData()) {
							// 判断单据状态是否异步改变
							var body = {}
							body["DJXH"] = this.form.getFormData().DJXH;
							if (!body["DJXH"]) {
								return;
							}
							if (this.list.store.getCount() < 1) {
								MyMessageTip.msg("提示", "没有明细不能审核!", true);
								return;
							}

							var r1 = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.inventoryDJZTActionId,
								body : body,
								op : this.op
							});
							if (r1.json.djzt != this.form.getFormData().DJZT) {
								MyMessageTip.msg("提示", "单据状态已改变，不能审核!", true);
								return;
							}
							if (this.form.getFormData().THDJ != 0) {
								// 表示退回或冲红处理，则要判断每条记录的库存序号在库存中是否有足够的库存，如果库存不足则提示用户并返回
							}
							if (this.form.getFormData().DJZT == 0) {
								var rc = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.inventoryDJSHActionId,
									body : body,
									op : this.op
								});
								if (rc.code == 603) {
									MyMessageTip.msg("提示",
											"没有录入盘点或者是录入盘点没有提交,不能审核!", true);
									return;
								}
								var rc = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.inventoryDJSHKSActionId,
											body : body,
											op : this.op
										});
								if (rc.code == 604) {
									MyMessageTip.msg("提示",
											"录入盘点没有选择全部科室,不能审核!", true);
									return;
								}
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.inventoryVerifyActionId,
											body : body
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg,
											this.onBeforeSave);
									return;
								} else {
									MyMessageTip.msg("提示", "审核成功!", true);
									this.loadData(this.initDataBody);
									this.changeButtonState("verified");
									this.fireEvent("save", this);
								}
							} else {
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.inventoryNoVerifyActionId,
											body : body
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg,
											this.onBeforeSave);
									return;
								} else {
									MyMessageTip.msg("提示", "弃审成功!", true);
									this.loadData(this.initDataBody);
									this.changeButtonState("verified");
									this.fireEvent("save", this);
								}
							}
						}
					},
					doCommit : function() {
						if (this.form.getFormData()) {
							if (this.list.store.getCount() < 1) {
								MyMessageTip.msg("提示", "没有明细不能记账!", true);
								return;
							}
							// 流转方式
							var LZFS = this.form.form.getForm().findField(
									"LZFS").getValue();
							if (!LZFS) {
								MyMessageTip.msg("提示", "流转方式不能为空!", true);
								return;
							}
							var LZFS1 = this.form.getFormData().LZFS;
							if (LZFS != LZFS1) {
								MyMessageTip.msg("提示", "流转方式已改变，请核对!", true);
								return;
							}
							// 判断确认标志
							if (this.form.getFormData().DJZT != 1) {
								Ext.Msg.alert("提示", "不在审核入库状态!");
								return;
							}
							var body = {}
							body["DJXH"] = this.form.getFormData().DJXH;
							body["PDLX"] = 2;
							var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.inventoryCommitActionId,
								body : body
							});
							if (ret.code > 300) {
								if (ret.code == 601) {
									MyMessageTip.msg("提示", "记账成功", true);
									this.fireEvent("save", this);
									var exCfg = this.mainApp.taskManager.tasks
											.item(userDomain);
									if (exCfg) {
										exCfg.treasuryPdzt = 0;
									}
									this.mainApp['phis'].treasuryPdzt = 0
									this.changeButtonState("commited");
									this.doClose();
								} else {
									this.processReturnMsg(ret.code, ret.msg,
											this.doSave);
									return;
								}
							} else {
								MyMessageTip.msg("提示", "记账成功", true);
								this.fireEvent("save", this);
								this.changeButtonState("commited");
								this.doClose();
							}
						}
					},
					doClose : function() {
						this.beforeClose();
						this.getWin().hide();
						return true;
					},
					beforeClose : function(winclose) {
						if (this.list.editRecords
								&& this.list.editRecords.length > 0) {
							if (confirm('数据已经修改，是否保存?')) {
								return this.doSave(winclose)
							} else {
								return true;
							}
						}
						return true;
					},
					doAction : function(item, e) {
						var cmd = item.cmd
						var script = item.script
						cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
						if (script) {
							$require(script, [
									function() {
										eval(script + '.do' + cmd
												+ '.apply(this,[item,e])')
									}, this ])
						} else {
							var action = this["do" + cmd]
							if (action) {
								action.apply(this, [ item, e ])
							}
						}
					},
					// 修改
					loadData : function(initDataBody) {
						this.listIsUpdate = true;
						this.doLoad(initDataBody, true);
					},
					// 改变按钮状态
					changeButtonState : function(state) {
						var actions = this.actions;
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							this.setButtonsState([ action.id ], false);
						}
						if (state == "new") {
							this.setButtonsState([ "create", "save", "callIn",
									"print", "close" ], true);
						}
						if (state == "save") {
							this.setButtonsState([ "create", "save", "callIn",
									"verify", "print", "close" ], true);
						}
						if (state == "verified") {
							this.setButtonsState([ "create", "verify",
									"commit", "print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
						}
						if (state == "commited") {
							this.setButtonsState(
									[ "create", "print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
						}

					},
					// 设置按钮状态
					setButtonsState : function(m, enable) {
						var btns;
						var btn;
						btns = this.panel.getTopToolbar();
						if (!btns) {
							return;
						}
						for ( var j = 0; j < m.length; j++) {
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
						this.form.initDataId = initDataBody.DJXH;
						this.form.loadData();
						this.list.op = "update";
						this.list.requestData.cnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.loadData();
					},
					doCallIn : function() {
						if (this.form.getFormData()) {
							// 判断单据状态是否异步改变
							var body = {}
							body["DJXH"] = this.form.getFormData().DJXH;
							if (body["DJXH"]) {
								var rc = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.inventoryDJSHActionId,
									body : body,
									op : this.op
								});
								if (rc.code == 606) {
									MyMessageTip.msg("提示", "录入盘点单已提交,不能引入!",
											true);
									return;
								}
							}
						}
						var module = this.createModule("getSUP10010203",
								"phis.application.sup.SUP/SUP/SUP10010203");

						this.module = module;
						if (this.list) {
							module.operater = this.list;
						}
						this.win1 = module.getWin();
						this.win1.add(module.initPanel());
						module.loadData();
						this.win1.show();
					},
		            doPrint : function() {
		                var module = this.createModule("inventoryKsPrint",
		                        this.refInventoryKsPrint)
		                var r = this.form.getFormData()
		                if (r == null) {
		                    MyMessageTip.msg("提示", "打印失败：无效的报损单信息!", true);
		                    return;
		                }
//		                if(r.DJZT==0){
//		                    MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
//		                    return;
//		                }
		                if(r.DJXH==null){
		                    MyMessageTip.msg("提示", "打印失败：请先保存", true);
		                    return;
		                }
		                module.djxh=r.DJXH;
		                module.initPanel();
		                module.doPrint();
		            }
				});