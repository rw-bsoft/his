$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.ForRegistrationModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.ForRegistrationModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.ForRegistrationModule,
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
							this.changeButtonState("blank");
							this.form.op = "create";
							this.list.op = "create";
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
					doClose : function() {
						this.beforeClose();
						this.getWin().hide();
						return true;
					},
					beforeClose : function() {
						if (this.list.editRecords
								&& this.list.editRecords.length > 0) {
							if (confirm('数据已经修改，是否保存?')) {
								return this.doSave()
							} else {
								return true;
							}
						}
						return true;
					},
					doSave : function(winclose) {
						if (!this.form.form.getForm().findField("KFXH")
								.getValue()) {
							MyMessageTip.msg("提示", "调出库房不能为空!", true);
							return;
						}
						if (this.form.getFormData()) {
							this.panel.el.mask("正在保存...", "x-mask-loading");
							var body = {};
							body["WL_CK02"] = [];
							var count = this.list.store.getCount();
							var _ctr = this;
							if (!_ctr.form.form.getForm().findField("QRRK")
									.getValue()) {
								MyMessageTip.msg("提示", "流转方式不能为空!", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (this.form) {
								if (this.form.form.getForm().findField("DJBZ")
										.getValue().length > 80) {
									MyMessageTip.msg("提示", "单据备注过长!", true);
									_ctr.panel.el.unmask();
									return;
								}
							}
							var whatsthetime = function() {
								for ( var i = 0; i < count; i++) {
									if (_ctr.list.store.getAt(i).data["WZXH"] != undefined
											&& _ctr.list.store.getAt(i).data["WZSL"] != undefined
											&& _ctr.list.store.getAt(i).data["WZSL"] != 0) {
										body["WL_CK02"].push(_ctr.list.store
												.getAt(i).data);
									}
								}
								if (body["WL_CK02"].length < 1) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_CK01"] = _ctr.form.getFormData();
								body["WL_CK01"].KSDM = _ctr.mainApp.treasuryEjkf
								if (this.Type == "commitedReject") {
									body["WL_CK01"].THDJ = this.thdj;
								} else if (this.Type == "reject") {
									body["WL_CK01"].THDJ = -1;
								}
								if (!body["WL_CK01"]) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_CK01"].CKKF = _ctr.mainApp.treasuryId;
								body["WL_CK01"].ZBLB = 0;
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : _ctr.serviceId,
											serviceAction : _ctr.saveMaterialsOutActionId,
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
									_ctr.changeButtonState("new");
									_ctr.fireEvent("save", _ctr);
									_ctr.doClose();
								}
								_ctr.op = "update";
								_ctr.panel.el.unmask();
							}
							whatsthetime.defer(500);
						}
					},
					doVerify : function() {
						if (this.mainApp['phis'].treasuryPdzt == 1) {
							Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行审核");
							return;
						}
						if (this.form.getFormData()) {
							if (this.form.getFormData().DJZT != -1) {
								Ext.Msg.alert("提示", "非新增状态，不能提交");
								return;
							}
							// 判断单据状态是否异步改变
							var body = {}
							body["DJXH"] = this.form.getFormData().DJXH;
							var r1 = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.materialsOutDJZTActionId,
								body : body,
								op : this.op
							});
							if (r1.json.djzt != this.form.getFormData().DJZT) {
								Ext.Msg.alert("提示", "单据状态已改变，不能提交");
								return;
							}
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "updateVerify",
								body : body,
								op : this.op
							});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg,
										this.onBeforeSave);
								return;
							} else {
								this.loadData(this.initDataBody);
								this.changeButtonState("verified");
								this.fireEvent("save", this);
							}
						}
					},
					doCommit : function() {
						if (this.form.getFormData()) {
							if (this.list.store.getCount() < 1) {
								MyMessageTip.msg("提示", "没有明细不能确认!", true);
								return;
							}
							// 流转方式
							var LZFS = this.form.form.getForm().findField(
									"QRRK").getValue();
							if (!LZFS) {
								MyMessageTip.msg("提示", "流转方式不能为空!", true);
								return;
							}
							var LZFS1 = this.form.getFormData().QRRK;
							if (LZFS != LZFS1) {
								MyMessageTip.msg("提示", "流转方式已改变，请核对!", true);
								return;
							}
							// 科室代码
							var KSDM = this.form.getFormData().KSDM;
							if (!KSDM) {
								MyMessageTip.msg("提示", "科室代码不能为空!", true);
								return;
							}
							// 盘点状态
							if (this.mainApp['phis'].treasuryPdzt == 1) {
								Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行操作");
								return;
							}
							// 判断确认标志
							if (this.form.getFormData().QRBZ != 0) {
								Ext.Msg.alert("提示", "不在确认入库状态!");
								return;
							}
							var count = this.list.store.getCount();
							var bodys = [];
							for ( var i = 0; i < count; i++) {
								var body = {};
								var r = this.list.store.getAt(i);
								if (r.get("WZXH") != undefined
										&& r.get("WZSL") != undefined
										&& r.get("WZSL") != 0) {
									body["DJXH"] = r.get("DJXH");
									body["JLXH"] = r.get("JLXH");
									body["ZBLB"] = this.form.getFormData().ZBLB;
									body["KFXH"] = this.form.getFormData().CKKF;
									body["CKRQ"] = this.form.getFormData().CKRQ;
									body["DJLX"] = 7;
									body["YWFS"] = 0;
									body["GLFS"] = 1;
									body["CJXH"] = r.get("CJXH");
									body["KCXH"] = r.get("KCXH");
									body["WZXH"] = r.get("WZXH");
									body["SCRQ"] = r.get("SCRQ");
									body["SXRQ"] = r.get("SXRQ");
									body["WZPH"] = r.get("WZPH");
									body["WZSL"] = r.get("WZSL");
									body["WZJG"] = r.get("WZJG");
									body["WZJE"] = r.get("WZJE");
									body["LSJG"] = r.get("LSJG");
									body["LSJE"] = r.get("LSJE");
									body["MJPH"] = r.get("MJPH");
									body["ZBXH"] = r.get("ZBXH");
									body["LZFS"] = LZFS;
									body["KSDM"] = KSDM;
									bodys[i] = body;
								}
							}
							var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "materialsOutService",
								serviceAction : "saveCommit",
								body : bodys
							});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doSave);
								return;
							}
							MyMessageTip.msg("提示", "确认成功", true);
							this.fireEvent("save", this);
							this.doClose();
						}
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
					loadQRRK : function(thdj) {
						if (this.form) {
							this.form.setQRRK(thdj);
						}
					},
					// 改变按钮状态
					changeButtonState : function(state) {
						var actions = this.actions;
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							this.setButtonsState([ action.id ], false);
						}
						// this.form.isRead = false;
						// if (state == "read") {
						// this.form.isRead = true;
						// }
						if (state == "blank") {
							this.setButtonsState([ "create", "save", "print",
									"close" ], true);
						}
						if (state == "new") {
							this.setButtonsState(["save", "verify",
									"print", "close" ], true);
						}
						if (state == "verified") {
							this.setButtonsState([ "close" ], true);

							this.list.setButtonsState([ "create", "remove" ],
									false);
							// this.form.isRead = true;
						}
						if (state == "commit") {
							this.setButtonsState(
									[ "commit", "print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
							// this.form.isRead = true;
						}
						if (state == "commited") {
							this.setButtonsState(
									[ "reject", "print", "close" ], true);
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
						this.form.initDataId = initDataBody.DJXH;
						this.form.loadData();
						this.list.op = "update";
						this.list.requestData.cnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.initCnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.loadData();
					}
				});