$package("phis.application.sup.script");

$import("phis.script.SimpleModule");

phis.application.sup.script.SecondaryStorageOfMaterialsDetailModule = function(
		cfg) {
	this.width = 1024;
	this.height = 500;
	cfg.listIsUpdate = true;
	phis.application.sup.script.SecondaryStorageOfMaterialsDetailModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.sup.script.SecondaryStorageOfMaterialsDetailModule,
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
						this.form.doIs(0);
						this.list.op = "create";
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
						body["WL_RK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							if (this.list.store.getAt(i).data["WZMC"] == ""
									|| this.list.store.getAt(i).data["WZMC"] == null) {
								continue;
							}
							// 判断退回数量是否大于可退数量
							if (this.type == "commitedReject"
									|| this.type == "reject") {
								if ((Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
										.getAt(i).data["KTSL"]))) {
									Ext.Msg.alert("提示", "第" + (i + 1)
											+ "行退回数量大于可退数量");
									this.panel.el.unmask();
									return false;
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
							}
							if (this.list.store.getAt(i).data["WZJG"] == 0
									|| this.list.store.getAt(i).data["WZJG"] == null) {
								Ext.Msg.alert("提示", "第" + (i + 1) + "行价格为0");
								this.panel.el.unmask();
								return false;
							}

							// 失效日期应大于生产日期和当前日期
							if (this.list.store.getAt(i).data["SXRQ"]
									&& this.list.store.getAt(i).data["SXRQ"] < new Date()
											.format("Y-m-d")) {
								Ext.Msg.alert("提示", "第" + (i + 1)
										+ "行失效日期小于当前日期");
								this.panel.el.unmask();
								return false;
							}
							if (this.list.store.getAt(i).data["SCRQ"]
									&& this.list.store.getAt(i).data["SXRQ"]
									&& this.list.store.getAt(i).data["SCRQ"] > this.list.store
											.getAt(i).data["SXRQ"]) {
								Ext.MessageBox.alert("提示", "第" + (i + 1)
										+ "行生产日期大于失效时间");
								this.panel.el.unmask();
								return false;
							}

							body["WL_RK02"].push(this.list.store.getAt(i).data);
						}
						if (body["WL_RK02"].length < 1) {
							this.panel.el.unmask();
							Ext.Msg.alert("提示", "没有明细信息,保存失败");
							return false;
						}
						body["WL_RK01"] = this.form.getFormData();
						if (!body["WL_RK01"]) {
							this.panel.el.unmask();
							return false;
						}
						if (this.type == "reject") {
							body["WL_RK01"].THDJ = -1;
						}
						if (this.type == "commitedReject") {
							body["WL_RK01"].THDJ = this.thdj;
						}
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveCheckIn",
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
							}
							this.fireEvent("save", this);
						}
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

						var body = {}
						body["DJXH"] = this.form.getFormData().DJXH;
						var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
						if (r1.json.djzt != this.form.getFormData().DJZT) {
							Ext.Msg.alert("提示", "单据状态已改变，不能审核");
							return;
						}

						body = {};
						body["WL_RK01"] = this.form.getFormData();
						if (!body["WL_RK01"]) {
							return;
						}
						if (body["WL_RK01"].THDJ != 0) {
							// 表示退回或冲红处理，则要判断每条记录的库存序号在库存中是否有足够的库存，如果库存不足则提示用户并返回
							body["WL_RK02"] = [];
							var count = this.list.store.getCount();
							for ( var i = 0; i < count; i++) {
								body["WL_RK02"]
										.push(this.list.store.getAt(i).data);
							}
							var r2 = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "isEnoughInventory",
								body : body,
								op : this.op
							});
							if (!r2.json.isEnoughInventory) {
								Ext.Msg.alert("提示", "库存不足，不能审核");
								return;
							}

						}
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "verify",
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
						}
					},
					doCancelVerify : function() {

						if (this.form.getFormData().DJZT != 1) {
							Ext.Msg.alert("提示", "非审核状态，不能弃审");
							return;
						}
						var body = {}
						body["DJXH"] = this.form.getFormData().DJXH;
						var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
						if (r1.json.djzt != this.form.getFormData().DJZT) {
							Ext.Msg.alert("提示", "单据状态已改变，不能审核");
							return;
						}

						var body = {};
						body["WL_RK01"] = this.form.getFormData();
						if (!body["WL_RK01"]) {
							return;
						}
						body["WL_RK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							body["WL_RK02"].push(this.list.store.getAt(i).data);
						}

						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "cancelVerify",
							body : body,
							op : this.op
						});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							return;
						} else {
							this.loadData(this.initDataBody);
							this.changeButtonState("new");
							this.fireEvent("save", this);
						}
					},
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
						body["WL_RK02"] = [];
						var count = this.list.store.getCount();
						for ( var i = 0; i < count; i++) {
							body["WL_RK02"].push(this.list.store.getAt(i).data);
						}

						if (this.form.getFormData().THDJ == 0) {
							// 把入库明细中每条记录的单价，更新物资厂家(WL_WZCJ)对应物资序号
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "updateWzcj",
								body : body,
								op : this.op
							});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg,
										this.onBeforeSave);
								this.panel.el.unmask();
								return false;
							}
						}

						body["WL_RK01"] = this.form.getFormData();
						var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "commit",
							body : body,
							op : this.op
						});
						if (r1.code > 300) {
							this.processReturnMsg(r1.code, r1.msg,
									this.onBeforeSave);
							this.panel.el.unmask();
							return false;
						} else {
							this.getWin().hide();
							this.fireEvent("save", this);
						}
					},
					doReject : function() {
						var djxh = this.form.getFormData().DJXH;
						var lzdh = this.form.getFormData().LZDH;
						this.form.doNew();
						this.form.form.getForm().findField("DJBZ").setValue(
								"由单据【" + lzdh + "】冲红产生");
						this.changeButtonState("blank");
						this.op = "create";
						this.thdj = djxh;
						this.type = "commitedReject";

						this.form.doIs("back");

						this.setButtonsState([ "create" ], false);
						this.list.setButtonsState([ "create" ], false);

						this.list.grid.getColumnModel().setHidden(
								this.list.grid.getColumnModel().getIndexById(
										"KTSL"), false);
						for ( var i = 0; i < this.list.grid.store.data.length; i++) {
							var body = {}
							body["KCXH"] = this.list.grid.store.data.itemAt(i)
									.get("KCXH");
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : "getKtslByKcxh",
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
										r.json.ktsl);
								this.list.grid.store.data.itemAt(i).set('WZSL',
										0);
								this.list.grid.store.data.itemAt(i).set('WZJE',
										0);
							}
						}
					},
					doClose : function() {
						this.beforeClose();
						this.getWin().hide();
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
					loadData : function(initDataBody) {
						this.list.remoteDicStore.baseParams = {
							"zblb" : 0
						}
						this.listIsUpdate = true;
						this.doLoad(initDataBody, true);
					},
					changeButtonState : function(state) {
						var actions = this.actions;
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							this.setButtonsState([ action.id ], false);
						}
						if (state == "blank") {
							this.setButtonsState([ "create", "save", "print",
									"close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									true);
						}
						if (state == "new") {
							this.setButtonsState(["save", "verify",
									"print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									true);
						}
						if (state == "verified") {
							this.setButtonsState(["cancelVerify",
									"commit", "print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
						}
						if (state == "commited") {
							this.setButtonsState(["reject", "print",
									"close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
						}
						if (state == "rejectCommited") {
							this.setButtonsState(
									[ "create", "print", "close" ], true);
							this.list.setButtonsState([ "create", "remove" ],
									false);
						}

					},
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
					doLoad : function(initDataBody) {
						this.form.op = "update";
						this.form.initDataId = initDataBody.DJXH;
						this.form.loadData();
						this.list.op = "create";
						this.list.requestData.cnd = [ 'eq', [ '$', 'DJXH' ],
								[ 'i', initDataBody.DJXH ] ];
						this.list.loadData();
					},
					doPrint : function() {
						var module = this.createModule("storeOfMaterialsPrint",
								this.refStoreOfMaterialsPrint)
						if (!this.form.getFormData()) {
							MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
							return;
						}
						module.djxh = this.form.getFormData().DJXH;
						module.initPanel();
						module.doPrint();
					}
				});