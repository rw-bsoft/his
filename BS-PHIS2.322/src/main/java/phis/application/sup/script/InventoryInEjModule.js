/**
 * 入库管理修改界面
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.InventoryInEjModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.InventoryInEjModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.InventoryInEjModule,
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
							this.list.loadData();
							this.list.clear();
							this.list.editRecords = [];
							this.list.doCreate();
						}
					},
					doUpd : function() {
						if (this.list) {
							this.form.listoper = this.list;
							this.form.op = "update";
							this.list.op = "update";
							this.list.clear();
							this.list.editRecords = [];
							this.list.doCreate();
						}
						this.changeButtonState("new");
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
							body["WL_LRMX"] = [];
							var count = this.list.store.getCount();
							var _ctr = this;
							var whatsthetime = function() {
								for ( var i = 0; i < count; i++) {
									body["WL_LRMX"].push(_ctr.list.store
											.getAt(i).data);
								}
								if (body["WL_LRMX"].length < 1) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_LRPD"] = _ctr.form.getFormData();
								if (this.Type == "commitedReject") {
									body["WL_LRPD"].THDJ = this.thdj;
								} else if (this.Type == "reject") {
									body["WL_LRPD"].THDJ = -1;
								}
								if (!body["WL_LRPD"]) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_LRPD"].KFXH = _ctr.mainApp.treasuryId;
								body["WL_LRPD"].ZBLB = _ctr.zblb;
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : _ctr.serviceId,
											serviceAction : _ctr.saveInventoryInActionId,
											method : "execute",
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
									_ctr.doClose();
								}
								_ctr.op = "update";
								_ctr.panel.el.unmask();
							}
							whatsthetime.defer(500);
						}
					},
					doSubit : function() {
						if (this.form.getFormData()) {
							// 判断单据状态是否异步改变
							var body = {}
							body["LRXH"] = this.form.getFormData().LRXH;
							var r1 = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.inventoryInDJZTActionId,
								method : "execute",
								body : body,
								op : this.op
							});
							if (r1.json.djzt != this.form.getFormData().DJZT) {
								Ext.Msg.alert("提示", "请先保存再提交！");
								return;
							}
							if (!body["LRXH"]) {
								return;
							}
							if (this.form.getFormData().THDJ != 0) {
								// 表示退回或冲红处理，则要判断每条记录的库存序号在库存中是否有足够的库存，如果库存不足则提示用户并返回
							}
							if (this.form.getFormData().DJZT == 0) {
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.inventoryInSubitActionId,
											method : "execute",
											body : body
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg,
											this.onBeforeSave);
									return;
								} else {
									MyMessageTip.msg("提示", "提交成功!", true);
									this.changeButtonState("subt");
									this.fireEvent("save", this);
								}
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
									"subit", "print", "close" ], true);
						}
						if (state == "subt") {
							this.setButtonsState([ "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
							// this.form.isRead = true;
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
						this.form.initDataBody = initDataBody;
						this.form.initDataId = initDataBody.LRXH;
						this.form.loadData();
						this.list.op = "update";
						this.list.requestData.cnd = [ 'eq', [ '$', 'a.LRXH' ],
								[ 'i', initDataBody.LRXH ] ];
						this.list.loadData();
					},
					doCallIn : function() {
						if (this.list.store.getCount() > 0) {
							var module = this.createModule("getSUP15010103",
									"phis.application.sup.SUP/SUP/SUP15010103");
							this.module = module;
							if (this.list) {
								module.operater = this.list;
							}
							this.win1 = module.getWin();
							this.win1.add(module.initPanel());
							if (module.cndField) {
								module.cndField.setValue("");
							}
							module.loadData();
							this.win1.show();
						} else {
							MyMessageTip.msg("提示", "没有盘点单,不能引入物资!", true);
						}
					}
				});