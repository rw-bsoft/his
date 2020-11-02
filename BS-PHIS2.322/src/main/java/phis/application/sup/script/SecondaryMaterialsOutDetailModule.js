/**
 * 物资出库确认（二级）
 * 
 * @author gaof
 */
$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.SecondaryMaterialsOutDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.SecondaryMaterialsOutDetailModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.SecondaryMaterialsOutDetailModule,
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
								height : 120,
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
						this.list.grid.getColumnModel().setHidden(
								this.list.grid.getColumnModel().getIndexById(
										"KTSL"), true);
						return this.list.grid;
					},
					doNew : function() {
						this.changeButtonState("blank");
						this.form.op = "create";
						this.form.doNew();
						// 如果是冲红 流转方式是 出库退回 by dingcj
						if (this.type == "reject") {
							this.form.doIs("back");
							this.type = 0;
						} else {
							this.form.doIs(0);
						}
						this.list.op = "create";
						// this.list.remoteDicStore.baseParams = {
						// "zblb" : this.zblb
						// }
						this.list.clear();
						this.list.editRecords = [];
						this.list.doCreate();
						this.list.setCountInfo();
					},
					doCreate : function() {
						var winclose = false;
						this.beforeClose(winclose);
						this.doNew();
					},
					doSave : function(winclose) {
						this.panel.el.mask("正在保存...", "x-mask-loading");
						if (this.list.grid.activeEditor != null) {
							this.list.grid.activeEditor.completeEdit();
						}
						var body = {};
						body["WL_CK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							if (this.list.store.getAt(i).data["WZMC"] == ""
									|| this.list.store.getAt(i).data["WZMC"] == null) {
								continue;
							}
							// alert(this.type)
							// 判断退回数量是否大于可退数量
							if (this.type == "commitedReject") {
								if (this.list.store.getAt(i).data["KTSL"]) {
									if ((Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
											.getAt(i).data["KTSL"]))) {
										Ext.Msg.alert("提示", "第" + (i + 1)
												+ "行退回数量大于可退数量");
										this.panel.el.unmask();
										return false;
									}
								}
								if (this.list.store.getAt(i).data["WZSL"] == 0
										|| this.list.store.getAt(i).data["WZSL"] == null) {
									continue;
								}
							} else {
								if (this.list.store.getAt(i).data["WZSL"] == 0
										|| this.list.store.getAt(i).data["WZSL"] == null) {
									Ext.Msg
											.alert("提示", "第" + (i + 1)
													+ "行数量为0");
									this.panel.el.unmask();
									return false;
								}
								if ((Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
										.getAt(i).data["TJCKSL"]))) {
									Ext.Msg.alert("提示", "第" + (i + 1)
											+ "行出库数量大于推荐出库数量");
									this.panel.el.unmask();
									return false;
								}
							}

							// if (this.list.store.getAt(i).data["WZJG"] == 0
							// || this.list.store.getAt(i).data["WZJG"] == null)
							// {
							// Ext.Msg.alert("提示", "第" + (i + 1) + "行价格为0");
							// this.panel.el.unmask();
							// return false;
							// }
							body["WL_CK02"].push(this.list.store.getAt(i).data);
						}
						if (body["WL_CK02"].length < 1) {
							this.panel.el.unmask();
							Ext.Msg.alert("提示", "没有明细信息,保存失败");
							return false;
						}
						body["WL_CK01"] = this.form.getFormData();
						if (!body["WL_CK01"]) {
							this.panel.el.unmask();
							return false;
						}
						if (body["WL_CK01"].CKFS == null
								|| body["WL_CK01"].CKFS == "") {
							this.panel.el.unmask();
							Ext.Msg.alert("提示", "出库方式不能为空");
							return false;
						}
						if (this.type == "reject") {
							body["WL_CK01"].THDJ = -1;
						}
						if (this.type == "commitedReject") {
							body["WL_CK01"].THDJ = this.thdj;
						}
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveCheckIn",
							method : "execute",
							body : body,
							op : this.op
						});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							this.panel.el.unmask();
							return false;
						} else {
							if (winclose != false) {
								this.getWin().hide();
								// this.fireEvent("winClose", this);
							}
							this.fireEvent("save", this);
						}
						// this.op = "update";
						this.panel.el.unmask();
						return true;
					},
					doVerify : function() {
						if (this.mainApp['phis'].treasuryPdzt == 1) {
							Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行审核");
							return;
						}
						if (this.form.getFormData().DJZT != 0) {
							Ext.Msg.alert("提示", "非新增状态，不能审核");
							return;
						}

						// 判断单据状态是否异步改变
						var body = {}
						body["DJXH"] = this.form.getFormData().DJXH;
						var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							method : "execute",
							body : body,
							op : this.op
						});
						if (r1.json.djzt != this.form.getFormData().DJZT) {
							Ext.Msg.alert("提示", "单据状态已改变，不能审核");
							return;
						}

						// 调用“保存”按钮事件
						// var re = this.doSave(false)
						// if (!re) {
						// return;
						// }

						body["WL_CK01"] = this.form.getFormData();
						if (!body["WL_CK01"]) {
							return;
						}

						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "verify",
							method : "execute",
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
					},
					doCancelVerify : function() {

						if (this.form.getFormData().DJZT != 1) {
							Ext.Msg.alert("提示", "非审核状态，不能弃审");
							return;
						}
						// 调用“保存”按钮事件
						// var re = this.doSave(false)
						// if (!re) {
						// return;
						// }

						// 判断单据状态是否异步改变
						var body = {}
						body["DJXH"] = this.form.getFormData().DJXH;
						var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							method : "execute",
							body : body,
							op : this.op
						});
						if (r1.json.djzt != this.form.getFormData().DJZT) {
							Ext.Msg.alert("提示", "单据状态已改变，不能审核");
							return;
						}

						body["WL_CK01"] = this.form.getFormData();
						if (!body["WL_CK01"]) {
							return;
						}

						// alert(Ext.encode(body["WL_CK01"]))
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "cancelVerify",
							method : "execute",
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
							// this.fireEvent("winClose", this);
							this.fireEvent("save", this);
						}
						// this.op = "update";
					},
					// 记账
					doCommit : function() {

						if (this.form.getFormData().DJZT != 1) {
							Ext.Msg.alert("提示", "非审核状态，不能记账");
							return;
						}
						// 判断单据状态是否异步改变
						var body = {}
						body["DJXH"] = this.form.getFormData().DJXH;
						var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							method : "execute",
							body : body,
							op : this.op
						});
						if (r1.json.djzt != this.form.getFormData().DJZT) {
							Ext.Msg.alert("提示", "单据状态已改变，不能审核");
							return;
						}

						if (this.mainApp['phis'].treasuryPdzt == 1) {
							Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行记账");
							return;
						}

						var body = {};
						body["WL_CK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							body["WL_CK02"].push(this.list.store.getAt(i).data);
						}

						// if (this.form.getFormData().THDJ == 0) {
						// // 把入库明细中每条记录的单价，更新物资厂家(WL_WZCJ)对应物资序号
						// var r = phis.script.rmi.miniJsonRequestSync({
						// serviceId : this.serviceId,
						// serviceAction : "updateWzcj",
						// body : body,
						// op : this.op
						// });
						// if (r.code > 300) {
						// this.processReturnMsg(r.code, r.msg,
						// this.onBeforeSave);
						// this.panel.el.unmask();
						// return false;
						// }
						// }

						// 增加库存,记账
						body["WL_CK01"] = this.form.getFormData();
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "commit",
							method : "execute",
							body : body,
							op : this.op
						});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							this.panel.el.unmask();
							return false;
						} else {
							this.getWin().hide();
							// this.fireEvent("winClose", this);
							this.fireEvent("save", this);
						}
					},
					doReject : function() {
						var thdj = this.form.data.THDJ;
						if (thdj) {
							if (parseInt(thdj) > 0 || thdj == "-1") {
								MyMessageTip.msg("提示",
										"该单据为退回单据或者是冲红单据,不可以再退回!", true);
								return;
							}
						}
						var djxh = this.form.data.DJXH;
						var ksdm = this.form.data.KSDM.key;
						var lzdh = this.form.data.LZDH;
						var ckfs = this.form.data.CKFS.key;
						var jbgh = this.form.data.JBGH.key;
						var djlx = this.form.data.DJLX;
						this.form.doNew();
						// this.form.form.getForm().findField("THDJ").setValue(djxh);
						this.form.form.getForm().findField("KSDM").setValue(
								ksdm);
						this.form.form.getForm().findField("DJBZ").setValue(
								"由单据【" + lzdh + "】冲红产生");
						if (jbgh != null) {
							this.form.form.getForm().findField("JBGH")
									.setValue(jbgh);
						}
						if (ckfs != null) {
							this.form.form.getForm().findField("CKFS")
									.setValue(ckfs);
						}
						this.form.data.DJLX = djlx;
						this.changeButtonState("blank");
						this.op = "create";
						this.thdj = djxh;
						this.type = "commitedReject";
						// 改变流转方式
						this.form.doIs("back");

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
									method : "execute",
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
							// this.list.grid.store.data.itemAt(i).set('KTSL',
							// this.list.grid.store.data.itemAt(i).data["WZSL"]);
						}
					},
					doClose : function() {
						this.beforeClose();
						this.list.editRecords = [];
						// console.debug(this.getWin());
						this.getWin().hide();
						// this.fireEvent("winClose", this);
						return true;
					},
					beforeClose : function(winclose) {
						var data = this.form.getFormData();
						if (!data) {
							return true;
						}
						if (data.DJZT == 0 && this.list.editRecords
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
						// this.list.remoteDicStore.baseParams = {
						// "zblb" : this.zblb
						// }
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
						// this.form.isRead = false;
						// if (state == "read") {
						// this.form.isRead = true;
						// }
						if (state == "blank") {
							this.type = "";
							this.setButtonsState([ "create", "save", "close" ],
									true);
							this.list.setButtonsState([ "create", "remove" ],
									true);
						}
						if (state == "new") {
							this.setButtonsState([ "create", "save", "verify",
									"close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									true);
						}
						if (state == "verified") {
							this.setButtonsState([ "create", "cancelVerify",
									"commit", "print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
							// this.form.isRead = true;
						}
						if (state == "commited") {
							this.setButtonsState([ "create", "reject", "print",
									"close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
							// this.form.isRead = true;
						}
						if(state=='ly'){
						this.setButtonsState([ "print",
									"close" ], true);
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
						this.form.initDataBody = initDataBody;
						this.form.loadData();
						// this.form.onLoadData();
						this.list.op = "create";
						this.list.requestData.cnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.initCnd = [ 'eq', [ '$', 'a.DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.loadData();
					},
					doPrint : function() {
						var module = this.createModule("noStoreListPrint",
								this.refNoStoreListPrint)
						var r = this.form.getFormData();
						if (r == null) {
							MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
							return;
						}
						if (r.DJZT == 0) {
							MyMessageTip.msg("提示", "打印失败：没有审核不能打印!", true);
							return;
						}
						module.DJXH = r.DJXH;
						module.initPanel();
						module.doPrint();
					}
				});