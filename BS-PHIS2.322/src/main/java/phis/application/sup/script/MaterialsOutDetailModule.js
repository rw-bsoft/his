$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.MaterialsOutDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.modal = this.modal = true;
	cfg.listIsUpdate = true;
	phis.application.sup.script.MaterialsOutDetailModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.on("close", this.onCloseck, this);
}
Ext
		.extend(
				phis.application.sup.script.MaterialsOutDetailModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (this.panel) {
							this.list.grid.getColumnModel().setHidden(
									this.list.grid.getColumnModel()
											.getIndexById("KTSL"), true);
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
								height : 95,
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
						this.list = this.createModule("list", this.refList)
						this.list.zblb = this.zblb;
						this.list.on("loadData", this.onLoadData, this);
						this.list.grid = this.list.initPanel();
						this.list.grid.getColumnModel().setHidden(
								this.list.grid.getColumnModel().getIndexById(
										"KTSL"), true);
						return this.list.grid;
					},
					doNew : function() {
						if (this.list) {
							this.form.doNew();
							// 如果是冲红 流转方式是 出库退回 by dingcj
							if (this.type == "reject") {
								this.form.doIs("back");
								this.type = 0;
							} else {
								this.form.doIs(0);
							}
							this.changeButtonState("new");
							this.form.op = "create";
							this.list.remoteDicStore.baseParams = {
								"zblb" : this.zblb,
								"kfxh" : this.mainApp['phis'].treasuryId
							}
							this.list.remoteDic.lastQuery = "";
							this.list.op = "create";
							this.list.clear();
							this.list.djlx = 0;
							this.list.editRecords = [];
							this.list.doCreate();
							this.list.setCountInfo();
						}
					},
					doUpd : function() {
						if (this.list) {
							this.list.remoteDicStore.baseParams = {
								"zblb" : this.zblb,
								"kfxh" : this.mainApp['phis'].treasuryId
							}
							this.form.op = "update";
							this.list.op = "update";
							this.list.doCreate();
						}
						var btns = this.panel.getTopToolbar();
						var btn = btns.find("cmd", "verify");
						btn = btn[0];
						this.form.btn = btn;
					},
					onLoadData : function(store) {
						if (this.form.getFormData()) {
							this.list.djlx = this.form.getFormData().DJLX;
						}
						if (this.listIsUpdate) {
							this.list.doCreate();
						}
					},
					doSave : function() {
						if (this.form.getFormData()) {
							this.panel.el.mask("正在保存...", "x-mask-loading");
							var body = {};
							body["WL_CK02"] = [];
							var count = this.list.store.getCount();
							var _ctr = this;
							if (!this.form.getFormData().PDDJ) {
								if (!_ctr.form.form.getForm().findField("KSDM").disabled) {
									if (!_ctr.form.form.getForm().findField(
											"KSDM").getValue()) {
										MyMessageTip.msg("提示", "出库科室不能为空!",
												true);
										_ctr.panel.el.unmask();
										return;
									}
								}
							}
							if (!_ctr.form.form.getForm().findField("JBGH").disabled) {
								if (!_ctr.form.form.getForm().findField("JBGH")
										.getValue()) {
									MyMessageTip.msg("提示", "经办人员不能为空!", true);
									_ctr.panel.el.unmask();
									return;
								}
							}
							if (!_ctr.form.form.getForm().findField("LZFS")
									.getValue()) {
								MyMessageTip.msg("提示", "流转方式不能为空!", true);
								_ctr.panel.el.unmask();
								return;
							}
							if (!_ctr.form.form.getForm().findField("CKRQ")
									.getValue()) {
								MyMessageTip.msg("提示", "出库日期不能为空!", true);
								_ctr.panel.el.unmask();
								return;
							}

							var sig = 0;
							var sig1 = 0;
							if (count == 1
									&& _ctr.list.store.getAt(0).data["WZXH"] == undefined) {
								MyMessageTip.msg("提示", "明细不能为空!", true);
								_ctr.panel.el.unmask();
								return;
							}
							for ( var i = 0; i < count; i++) {
								if (_ctr.list.store.getAt(i).data["WZSL"] > _ctr.list.store
										.getAt(i).data["TJCKSL"]) {
									sig = i + 1;
									break;
								}
							}
							for ( var i = 0; i < count; i++) {
								if (_ctr.list.store.getAt(i).data["WZSL"] > _ctr.list.store
										.getAt(i).data["KTSL"]) {
									sig1 = i + 1;
									break;
								}
							}
							if (this.type != "commitedReject") {
								if (sig != 0) {
									MyMessageTip.msg("提示", "第" + sig
											+ "行出库数量不能大于推荐出库数量!", true);
									_ctr.panel.el.unmask();
									return;
								}
							}
							if (this.type == "commitedReject") {
								if (sig1 != 0) {
									MyMessageTip.msg("提示", "第" + sig1
											+ "行出库数量不能大于可退数量!", true);
									_ctr.panel.el.unmask();
									return;
								}
							}
							var whatsthetime = function() {
								for ( var i = 0; i < count; i++) {
									if (count == 1) {
										if (_ctr.list.store.getAt(i).data["WZXH"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == 0) {
											MyMessageTip.msg("提示", "第"
													+ (i + 1)
													+ "行物资名称、出库数量不能为空!", true);
											_ctr.panel.el.unmask();
											return;
										}
										if (_ctr.list.store.getAt(i).data["GLFS"] == "3") {
											if (!_ctr.list.store.getAt(i).data["ZBXH"]) {
												MyMessageTip
														.msg(
																"提示",
																"第"
																		+ (i + 1)
																		+ "行没有选择对应的物资明细!",
																true);
												_ctr.panel.el.unmask();
												return;
											}
										}
									}
									if (_ctr.list.store.getAt(i).data["WZXH"]) {
										if (_ctr.list.store.getAt(i).data["WZXH"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == undefined
												|| _ctr.list.store.getAt(i).data["WZSL"] == 0) {
											MyMessageTip.msg("提示", "第"
													+ (i + 1)
													+ "行物资名称、出库数量不能为空!", true);
											_ctr.panel.el.unmask();
											return;
										}
										if (_ctr.list.store.getAt(i).data["GLFS"] == "3") {
											if (!_ctr.list.store.getAt(i).data["ZBXH"]) {
												MyMessageTip.msg("提示", "第"
														+ (i + 1)
														+ "行没有选择对应的批号!", true);
												_ctr.panel.el.unmask();
												return;
											}
										}
									}
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
								if (_ctr.type == "commitedReject") {
									body["WL_CK01"].THDJ = _ctr.thdj;
								} else if (_ctr.type == "reject") {
									body["WL_CK01"].THDJ = -1;
								}
								if (!body["WL_CK01"]) {
									_ctr.panel.el.unmask();
									return;
								}
								body["WL_CK01"].KFXH = _ctr.mainApp.treasuryId;
								body["WL_CK01"].ZBLB = _ctr.zblb;
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : _ctr.serviceId,
											serviceAction : _ctr.saveMaterialsOutActionId,
											body : body,
											op : _ctr.op
										});
								_ctr.panel.el.unmask();
								if (r.code > 300) {
									_ctr.processReturnMsg(r.code, r.msg,
											_ctr.onBeforeSave);
									_ctr.panel.el.unmask();
									return;
								} else {
									_ctr.list.doInit();
									_ctr.fireEvent("save", _ctr);
									_ctr.doClose();
									_ctr.changeButtonState("save");
								}
								_ctr.op = "update";
							}
							whatsthetime.defer(500);
						}
					},
					doVerify : function() {
						if (this.mainApp['phis'].treasuryPdzt == 1) {
							Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行审核");
							return;
						}
						// 判断单据状态是否异步改变
						if (this.form.getFormData()) {
							var body = {}
							body["DJXH"] = this.form.getFormData().DJXH;
							var r1 = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.materialsOutDJZTActionId,
								body : body,
								op : this.op
							});
							if (r1.json.djzt != this.form.getFormData().DJZT) {
								Ext.Msg.alert("提示", "单据状态已改变，不能审核");
								return;
							}
							if (!body["DJXH"]) {
								return;
							}
							if (this.form.getFormData().THDJ != 0) {
								// 表示退回或冲红处理，则要判断每条记录的库存序号在库存中是否有足够的库存，如果库存不足则提示用户并返回
							}
							if (this.form.getFormData().DJZT == 0) {
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.materialsOutVerifyActionId,
											body : body,
											op : this.op
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg,
											this.onBeforeSave);
									return;
								} else {
									if (r.json.WZMC) {
										MyMessageTip.msg("提示", "物资"
												+ r.json.WZMC + "库存不足,不能审核!",
												true);
									} else {
										MyMessageTip.msg("提示", "审核成功!", true);
										this.changeButtonState("verified");
										this.loadData(this.initDataBody);
										this.fireEvent("save", this);
									}
								}
							} else if (this.form.getFormData().DJZT == 1) {
								var r = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : this.serviceId,
											serviceAction : this.materialsOutNoVerifyActionId,
											body : body,
											op : this.op
										});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg,
											this.onBeforeSave);
									return;
								} else {
									MyMessageTip.msg("提示", "弃审成功!", true);
									this.loadData(this.initDataBody);
									this.changeButtonState("reverified");
									this.fireEvent("save", this);
								}
							}
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
					doClose : function() {
						this.beforeClose();
						this.list.th = 0;
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
					// 修改
					loadData : function(initDataBody) {
						this.listIsUpdate = true;
						this.doLoad(initDataBody);
					},
					// 修改,查看,提交数据回填
					doLoad : function(initDataBody) {
						this.form.op = "update";
						this.form.initDataBody = initDataBody;
						this.form.initDataId = initDataBody.DJXH;
						this.form.loadData();
						this.list.op = "update";
						this.list.requestData.cnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.initCnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.loadData();

					},
					doCreate : function() {
						this.beforeClose();
						this.list.th = 0;
						this.doNew();
					},
					doCommit : function() {
						if (this.form.getFormData()) {
							if (this.list.store.getCount() < 1) {
								MyMessageTip.msg("提示", "没有明细不能确认!", true);
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
							if (!this.form.getFormData().PDDJ) {
								// 科室代码
								var KSDM = this.form.getFormData().KSDM;
								if (!KSDM) {
									MyMessageTip.msg("提示", "科室代码不能为空!", true);
									return;
								}
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
									body["KFXH"] = this.form.getFormData().KFXH;
									body["CKRQ"] = this.form.getFormData().CKRQ;
									if (this.form.getFormData().PDDJ > 0) {
										body["YWFS"] = 1;
									} else {
										body["YWFS"] = 0;
									}
									body["DJLX"] = 0;
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
									body["GLFS"] = r.get("GLFS");
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
					// 改变按钮状态
					changeButtonState : function(state) {
						var actions = this.actions;
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							this.setButtonsState([ action.id ], false);
						}
						if (state == "new") {
							this.type = "";
							this.setButtonsState([ "create", "save", "close" ],
									true);
							this.list.setButtonsState([ "create", "remove" ],
									true);
						}
						if (state == "save") {
							this.setButtonsState([ "save", "verify", "close" ],
									true);
						}
						if (state == "verified") {
							this.setButtonsState([ "verify", "commit", "print",
									"close" ], true);
						}
						if (state == "reverified") {
							this.setButtonsState(
									[ "verify", "print", "close" ], true);
						}
						if (state == "commit") {
							this.setButtonsState(
									[ "reject", "print", "close" ], true);
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
					doReject : function() {
						this.list.th = 1;
						var djxh = this.form.data.DJXH;
						var ksdm = this.form.data.KSDM.key;
						var lzdh = this.form.data.LZDH;
						var thdj = this.form.data.THDJ;
						if (thdj) {
							if (parseInt(thdj) > 0 || thdj == "-1") {
								MyMessageTip.msg("提示",
										"该单据为退回单据或者是冲红单据,不可以再退回!", true);
								return;
							}
						}
						this.form.doNew();
						this.form.form.getForm().findField("KSDM").setValue(
								ksdm);
						this.form.form.getForm().findField("DJBZ").setValue(
								"由单据【" + lzdh + "】冲红产生");

						this.changeButtonState("new");
						this.op = "create";
						this.thdj = djxh;
						this.type = "commitedReject";
						this.form.doIs("back");
						this.list.setButtonsState([ "remove" ], true);
						this.list.setButtonsState([ "create" ], false);
						// 增加一列可退数量
						this.list.grid.getColumnModel().setHidden(
								this.list.grid.getColumnModel().getIndexById(
										"KTSL"), false);
						for ( var i = 0; i < this.list.grid.store.data.length; i++) {
							if (this.list.grid.store.data.itemAt(i).get("KCXH")) {
								var body = {}
								body["KCXH"] = this.list.grid.store.data
										.itemAt(i).get("KCXH");
								body["JLXH"] = this.list.grid.store.data
										.itemAt(i).get("JLXH");
								body["DJXH"] = djxh;
								var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : "queryKtslByThdj",
									body : body,
									op : this.op
								});
								if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg,
											this.onBeforeSave);
									this.panel.el.unmask();
									return false;
								} else {
									this.list.grid.store.data.itemAt(i).set(
											'KTSL', r.json.THSL);
									this.list.grid.store.data.itemAt(i).set(
											'WZSL', r.json.THSL);
								}
							}
						}
					},
					doPrint : function() {
						var module = this.createModule("noStoreListPrint",
								this.refNoStoreListPrint)
						var DJXH = this.form.data.DJXH;
						if (DJXH == null) {
							MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
							return;
						}
						module.DJXH = DJXH;
						module.initPanel();
						module.doPrint();
					},
					onCloseck : function() {
						this.form.form.getForm().findField("KSDM").enable();
						this.form.form.getForm().findField("JBGH").enable();
						this.list.th = 0;
					}
				});