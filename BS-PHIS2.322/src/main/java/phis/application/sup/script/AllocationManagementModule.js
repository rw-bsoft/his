/**
 * 入库管理修改界面
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.AllocationManagementModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.AllocationManagementModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sup.script.AllocationManagementModule,
		phis.script.SimpleModule, {
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
							items : [{
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
									}],
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
				this.list.grid.getColumnModel().setHidden(
						this.list.grid.getColumnModel().getIndexById("KTSL"),
						true);
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
				this.changeButtonState("new");

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
					if (!this.form.form.getForm().findField("LZFS").getValue()) {
						MyMessageTip.msg("提示", "流转方式不能为空!", true);
						this.panel.el.unmask();
						return;
					}
					if (this.type == "commitedReject") {
						var bodyck = {};
						bodyck["WL_CK02"] = [];
						var count = this.list.store.getCount();
						var _ctr = this;
						var sig = 0;
						var sig1 = 0;
						if (count == 1
								&& _ctr.list.store.getAt(0).data["WZXH"] == undefined) {
							MyMessageTip.msg("提示", "明细不能为空!", true);
							_ctr.panel.el.unmask();
							return;
						}
						for (var i = 0; i < count; i++) {
							if (_ctr.list.store.getAt(i).data["WZSL"] > _ctr.list.store
									.getAt(i).data["TJCKSL"]) {
								sig = i + 1;
								break;
							}
						}
						for (var i = 0; i < count; i++) {
							if (_ctr.list.store.getAt(i).data["WZSL"] > _ctr.list.store
									.getAt(i).data["KTSL"]) {
								sig1 = i + 1;
								break;
							}
						}
						if (this.type != "commitedReject") {
							if (sig != 0) {
								MyMessageTip.msg("提示", "第" + sig
												+ "行调拨数量不能大于推荐出库数量!", true);
								_ctr.panel.el.unmask();
								return;
							}
						}
						if (this.type == "commitedReject") {
							if (sig1 != 0) {
								MyMessageTip.msg("提示", "第" + sig1
												+ "行调拨数量不能大于可退数量!", true);
								_ctr.panel.el.unmask();
								return;
							}
						}
						var whatsthetime = function() {
							for (var i = 0; i < count; i++) {
								if (count == 1) {
									if (_ctr.list.store.getAt(i).data["WZXH"] == undefined
											|| _ctr.list.store.getAt(i).data["WZSL"] == undefined
											|| _ctr.list.store.getAt(i).data["WZSL"] == 0) {
										MyMessageTip.msg("提示", "第" + (i + 1)
														+ "行物资名称、出库数量不能为空!",
												true);
										_ctr.panel.el.unmask();
										return;
									}
								}
								if (_ctr.list.store.getAt(i).data["WZXH"]) {
									if (_ctr.list.store.getAt(i).data["WZXH"] == undefined
											|| _ctr.list.store.getAt(i).data["WZSL"] == undefined
											|| _ctr.list.store.getAt(i).data["WZSL"] == 0) {
										MyMessageTip.msg("提示", "第" + (i + 1)
														+ "行物资名称、出库数量不能为空!",
												true);
										_ctr.panel.el.unmask();
										return;
									}
								}
								if (_ctr.list.store.getAt(i).data["WZXH"] != undefined
										&& _ctr.list.store.getAt(i).data["WZSL"] != undefined
										&& _ctr.list.store.getAt(i).data["WZSL"] != 0) {
									bodyck["WL_CK02"].push(_ctr.list.store
											.getAt(i).data);
								}
							}
							if (bodyck["WL_CK02"].length < 1) {
								_ctr.panel.el.unmask();
								return;
							}
							bodyck["WL_CK01"] = _ctr.form.getFormData();
							if (_ctr.type == "commitedReject") {
								bodyck["WL_CK01"].THDJ = _ctr.thdj;
                                bodyck["WL_CK01"].DBTH = true;
							} else if (_ctr.type == "reject") {
								bodyck["WL_CK01"].THDJ = -1;
							}
							if (!bodyck["WL_CK01"]) {
								_ctr.panel.el.unmask();
								return;
							}
							bodyck["WL_CK01"].KFXH = _ctr.mainApp.treasuryId;
							bodyck["WL_CK01"].ZBLB = _ctr.zblb;
							var r = phis.script.rmi.miniJsonRequestSync({
										serviceId : _ctr.serviceId,
										serviceAction : _ctr.saveMaterialsOutActionId,
										body : bodyck,
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
					} else {
						var body = {
							LZFS : this.form.getFormData().LZFS,
							DJXH : this.form.getFormData().DJXH
						};
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : "updateLZFS",
									body : body
								});
						this.panel.el.unmask();
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							this.panel.el.unmask();
							return;
						} else {
							this.fireEvent("save", this);
							//this.doClose();
						}
						this.op = "update";
					}
				}
			},
			doVerify : function() {
				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行审核");
					return;
				}

				if (this.form.getFormData()) {
					// 判断单据状态是否异步改变
					var body = {}
					body["DJXH"] = this.form.getFormData().DJXH;
					var rs = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "queryMaterialsOutLZFS",
								body : body
							});
					if (rs.code == 600) {
						MyMessageTip.msg("提示", "请先保存数据!", true);
						return;
					}
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
					if (this.form.getFormData().THDJ != 0) {
						// 表示退回或冲红处理，则要判断每条记录的库存序号在库存中是否有足够的库存，如果库存不足则提示用户并返回
					}
					if (this.form.getFormData().DJZT == 0) {
						var r = phis.script.rmi.miniJsonRequestSync({
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
								MyMessageTip.msg("提示", "物资" + r.json.WZMC
												+ "库存不足,不能审核!", true);
							} else {
								MyMessageTip.msg("提示", "审核成功!", true);
								this.loadData(this.initDataBody);
								this.changeButtonState("verified");
								this.fireEvent("save", this);
							}
						}
					} else if (this.form.getFormData().DJZT == 1) {
						var r = phis.script.rmi.miniJsonRequestSync({
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
							this.changeButtonState("new");
							this.fireEvent("save", this);

						}
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
					var LZFS = this.form.form.getForm().findField("LZFS")
							.getValue();
					if (!LZFS) {
						MyMessageTip.msg("提示", "流转方式不能为空!", true);
						return;
					}
					var LZFS1 = this.form.getFormData().LZFS;
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
					for (var i = 0; i < count; i++) {
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
							body["DJLX"] = 0;
							body["YWFS"] = 0;
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
						this.processReturnMsg(ret.code, ret.msg, this.doSave);
						return;
					}
					MyMessageTip.msg("提示", "确认成功", true);
					this.fireEvent("save", this);
					this.doClose();
				}
			},
			doClose : function() {
				this.beforeClose();
				this.getWin().hide();
				return true;
			},
			beforeClose : function(winclose) {
				if (this.list.editRecords && this.list.editRecords.length > 0) {
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
				this.list.remoteDicStore.baseParams = {
					"zblb" : 0,
					"kfxh" : this.form.ckkf
				}
				this.listIsUpdate = true;
				this.doLoad(initDataBody, true);
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					this.setButtonsState([action.id], false);
				}
				// this.form.isRead = false;
				// if (state == "read") {
				// this.form.isRead = true;
				// }
				if (state == "blank") {
					this.setButtonsState(["create", "save", "print", "close"],
							true);
				}
				if (state == "new") {
					this.setButtonsState(["create", "save", "verify", "print",
									"close"], true);
				}
				if (state == "verified") {
					this.setButtonsState(["verify", "commit",
									"print", "close"], true);
					this.list.setButtonsState(["create", "remove"], false);
					// this.form.isRead = true;
				}
				if (state == "commited") {
					this.setButtonsState(
							["reject", "print", "close"], true);
					this.list.setButtonsState(["create", "remove"], false);
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
				this.form.initDataId = initDataBody.DJXH;
				// this.form.initDataBody = initDataBody;
				// this.form.requestData.cnd = ['eq', ['$', 'DJXH'],
				// ['i', initDataBody.DJXH]];
				this.form.loadData();
				this.list.op = "update";
				this.list.requestData.cnd = ['eq', ['$', 'a.DJXH'],
						['i', initDataBody.DJXH]];
				this.list.initCnd = ['eq', ['$', 'a.DJXH'],
						['i', initDataBody.DJXH]];
				this.list.loadData();
			},
			doReject : function() {
				var djxh = this.form.data.DJXH;
				var ksdm = this.form.data.KSDM.key;
				var ckkf = this.form.data.CKKF.key;
				var kfxh = this.form.data.KFXH.key;
				var lzdh = this.form.data.LZDH;
				var thdj = this.form.data.THDJ;
				if (thdj) {
					if (parseInt(thdj) > 0 || thdj == "-1") {
						MyMessageTip.msg("提示", "该单据为退回单据或者冲红单据,不可以再退回!", true);
						return;
					}
				}
				this.form.doNew();
				this.form.form.getForm().findField("KSDM").setValue(ksdm);
				this.form.form.getForm().findField("CKKF").setValue(ckkf);
				this.form.form.getForm().findField("KFXH").setValue(kfxh);
				this.form.form.getForm().findField("DJBZ").setValue("由单据【"
						+ lzdh + "】冲红产生");
				// this.form.form.getForm().findField("LZFS").setValue("");

				this.changeButtonState("new");
				this.op = "create";
				this.thdj = djxh;
				this.type = "commitedReject";
				this.form.doIs("back");
				// 增加一列可退数量
				this.list.grid.getColumnModel().setHidden(
						this.list.grid.getColumnModel().getIndexById("KTSL"),
						false);
				for (var i = 0; i < this.list.grid.store.data.length; i++) {
					if (this.list.grid.store.data.itemAt(i).get("KCXH")) {
						var body = {}
						body["KCXH"] = this.list.grid.store.data.itemAt(i).get("KCXH");
						body["JLXH"] = this.list.grid.store.data.itemAt(i).get("JLXH");
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
							this.list.grid.store.data.itemAt(i).set('KTSL',
									r.json.THSL);
							var wzje = parseInt(r.json.THSL)
									* parseInt(this.list.grid.store.data
											.itemAt(i).get('WZJG'))
							this.list.grid.store.data.itemAt(i).set('WZSL',
									r.json.THSL);
							this.list.grid.store.data.itemAt(i).set('WZJE',
									wzje);
						}
					}
				}
			}
		});