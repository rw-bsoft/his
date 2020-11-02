$package("phis.application.sup.script");
$import("phis.script.SimpleModule");
/**
 * 转科管理新增修改界面
 * 
 * @author gaof
 */
phis.application.sup.script.TransferManagementDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	phis.application.sup.script.TransferManagementDetailModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.TransferManagementDetailModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (this.panel) {
							// this.list.grid.getColumnModel().setHidden(
							// this.list.grid.getColumnModel()
							// .getIndexById("TJSL"), true);
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
								height : 100,
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
						this.list.zblb = this.zblb;
						this.list.grid = this.list.initPanel();
						// this.list.grid.getColumnModel().setHidden(
						// this.list.grid.getColumnModel().getIndexById("TJSL"),
						// true);
						return this.list.grid;
					},
					doNew : function() {
						this.changeButtonState("blank");
						this.form.op = "create";
						this.form.doNew();
						// this.form.doIs(0);
						this.list.op = "create";
						this.list.clear();
					},
					doCreate : function() {
						var winclose = false;
						this.beforeClose(winclose);
						this.list.editRecords = [];
						this.doNew();
					},
					doSave : function(winclose) {
						var body = {};
						body["WL_ZK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							if (this.list.store.getAt(i).data["WZSL"] == 0
									|| this.list.store.getAt(i).data["WZSL"] == null) {
								Ext.Msg.alert("提示", "第" + (i + 1) + "行数量为0");
								return false;
							}
							if (this.list.store.getAt(i).data["TJSL"]
									&& this.list.store.getAt(i).data["TJSL"] != ""
									&& (Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
											.getAt(i).data["TJSL"]))) {
								Ext.Msg.alert("提示", "第" + (i + 1)
										+ "行转科数量大于推荐数量");
								return false;
							}

							body["WL_ZK02"].push(this.list.store.getAt(i).data);
						}
						if (body["WL_ZK02"].length < 1) {
							Ext.Msg.alert("提示", "没有明细信息,保存失败");
							return false;
						}
						var DJBZ = this.form.getFormData().DJBZ;
						if (DJBZ.length > 80) {
							Ext.Msg.alert("提示", "单据备注过长！");
							return false;
						}
						body["WL_ZK01"] = this.form.getFormData();
						if (body["WL_ZK01"].ZCKS == body["WL_ZK01"].ZRKS) {
							Ext.Msg.alert("提示", "转出科室和转入科室应不能为同一科室");
							return false;
						}
						if (!body["WL_ZK01"]) {
							return false;
						}
						body["WL_ZK01"].ZBLB = this.zblb;

						this.panel.el.mask("正在保存...", "x-mask-loading");
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
								this.fireEvent("winClose", this);
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

						body["WL_ZK01"] = this.form.getFormData();
						if (!body["WL_ZK01"]) {
							return;
						}
						body["WL_ZK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							body["WL_ZK02"].push(this.list.store.getAt(i).data);
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
							MyMessageTip.msg("提示", "审核成功!", true);
							this.loadData(this.initDataBody);
							this.changeButtonState("verified");
							this.fireEvent("save", this);
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

						body["WL_ZK01"] = this.form.getFormData();
						if (!body["WL_ZK01"]) {
							return;
						}
						body["WL_ZK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							body["WL_ZK02"].push(this.list.store.getAt(i).data);
						}

						// alert(Ext.encode(body["WL_ZK01"]))
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
						body["WL_ZK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							body["WL_ZK02"].push(this.list.store.getAt(i).data);
						}

						// 增加库存,记账
						body["WL_ZK01"] = this.form.getFormData();
						body["WL_ZK01"].ZBLB = this.zblb;
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveCommit",
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
							this.fireEvent("winClose", this);
							this.fireEvent("save", this);
						}
					},
					doClose : function() {
						this.beforeClose();
						this.list.editRecords = [];
						this.fireEvent("winClose", this);
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
							this.setButtonsState([ "create", "import", "save",
									"print", "close" ], true);
						}
						if (state == "new") {
							this.setButtonsState([ "create", "import", "save",
									"verify", "print", "close" ], true);
						}
						if (state == "verified") {
							this.setButtonsState([ "create", "cancelVerify",
									"commit", "print", "close" ], true);
							// this.list.setButtonsState(["create", "remove"],
							// false);
							// this.form.isRead = true;
						}
						if (state == "commited") {
							this.setButtonsState([ "create", "reject", "print",
									"close" ], true);
							// this.list.setButtonsState(["create", "remove"],
							// false);
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
						// this.form.initDataBody = initDataBody;
						// this.form.requestData.cnd = ['eq', ['$', 'DJXH'],
						// ['i', initDataBody.DJXH]];
						this.form.loadData();
						this.list.op = "create";
						// this.list.requestData.cnd = ['eq', ['$', 'DJXH'],
						// ['i', initDataBody.DJXH]];
						this.list.requestData.DJXH = initDataBody.DJXH;
						// this.list.requestData.KSDM =
						// this.form.getFormData().ZCKS;
						this.list.loadData();
					},
					doImport : function() {
						this.transferManagementKCList = this.createModule(
								"transferManagementKCList", this.refEditorList);
						this.transferManagementKCList.on("save", this.onSave,
								this);
						this.transferManagementKCList.on("winClose",
								this.onClose, this);
						// 账簿类别
						this.transferManagementKCList.zblb = this.zblb;
						this.transferManagementKCList.on("checkData",
								this.onCheckData, this);
						// this.transferManagementDetailModule.initPanel();
						var win = this.getWin();
						win.add(this.transferManagementKCList.initPanel());
						var formData = this.form.getFormData();
						var ksdm = "";
						if (formData) {
							ksdm = formData.ZCKS;
						}
						if (ksdm == null || ksdm == "" || ksdm == 0) {
							Ext.Msg.alert("提示", "请选择科室或者人员信息");
							return;
						}
						this.transferManagementKCList.loadData(ksdm);
						win.show()
						win.center()
						if (!win.hidden) {
							this.transferManagementKCList.op = "create";
							// this.transferManagementKCList.doNew();
						}
					},
					// 处理库存界面返回
					onCheckData : function(records) {
						var store = this.list.grid.getStore();

						this.list.clear();
						store.add(records);
						// 增加一列可退数量
						this.list.grid.getColumnModel().setHidden(
								this.list.grid.getColumnModel().getIndexById(
										"TJSL"), false);
						// this.list.grid.getView().refresh();
						// this.checkRecord.set("KCSB", sbxh);
						// this.checkRecord.set("YPPH", ypph);
						// this.checkRecord.set("YPXQ", ypxq);
						// this.checkRecord.set("JHJG", jhjg);
						// if (kcsl < -this.checkRecord.get("RKSL")) {
						// MyMessageTip.msg("提示", "库存不够!", true);
						// this.checkRecord.set("RKSL", -kcsl);
						// this.checkRecord.set("LSJE",
						// (parseFloat(this.checkRecord.get("LSJG")) *
						// parseFloat(this.checkRecord.get("RKSL"))).toFixed(2));
						// }
						// this.checkRecord.set("JHHJ", (parseFloat(jhjg) *
						// parseFloat(this.checkRecord.get("RKSL"))).toFixed(2));
						// this.doJshj();
						this.onClose();
					},
					onClose : function() {
						this.getWin().hide();
						// this.refresh();
					},
					onSave : function() {
						this.fireEvent("save", this);
					},
					getWin : function() {
						var win = this.win
						if (!win) {
							win = new Ext.Window({
								id : this.id,
								title : "转科管理物资引入",
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : "hide",
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || false
							// add by huangpf.
							})
							var renderToEl = this.getRenderToEl()
							if (renderToEl) {
								win.render(renderToEl)
							}
							win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
							win.on("add", function() {
								this.win.doLayout()
							}, this)
							win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
							win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
							win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
							win.on("hide", function() { // ** add by yzh
														// 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
							this.win = win
						}
						return win;
					},
					doPrint : function() {
						var module = this.createModule("storeOfTransferPrint",
								this.refStoreOfTransferPrint)
						if (!this.form.getFormData()) {
							MyMessageTip.msg("提示", "打印失败：无效的转科单信息!", true);
							return;
						}
						// if (this.form.getFormData().DJZT == 0) {
						// MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
						// return;
						// }
						module.djxh = this.form.getFormData().DJXH;
						module.zblb = this.zblb
						module.initPanel();
						module.doPrint();
					}
				});