$package("phis.application.sup.script");
$import("phis.script.SimpleModule");
/**
 * 申领管理新增修改界面
 * 
 * @author gaof
 */
phis.application.sup.script.ApplyManagementDetailModule = function(cfg) {
	cfg.width = 1024;
	cfg.height = 500;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	phis.application.sup.script.ApplyManagementDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sup.script.ApplyManagementDetailModule,
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
				// this.list.grid.getColumnModel().setHidden(
				// this.list.grid.getColumnModel().getIndexById("KTSL"),
				// true);
				return this.list.grid;
			},
			doNew : function() {
				this.changeButtonState("blank");
				this.form.op = "create";
				this.form.doNew();
				// this.form.doIs(0);
				this.list.op = "create";
				// this.list.remoteDicStore.baseParams = {
				// "zblb" : this.zblb
				// }
				this.list.clear();
				// this.list.editRecords = [];
				// this.list.doCreate();
			},
			doCreate : function() {
				var winclose = false;
				this.beforeClose(winclose);
				this.doNew();
			},
			doSave : function(winclose) {
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var body = {};
				body["WL_CK02"] = [];
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					// 判断行实发数量是否大于推荐数量
					if ((Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
							.getAt(i).data["TJSL"]))) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行实发数量大于推荐数量");
						this.panel.el.unmask();
						return false;
					}
					if ((Number(this.list.store.getAt(i).data["WZSL"])) > (Number(this.list.store
							.getAt(i).data["SLSL"]))) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行实发数量大于申领数量");
						this.panel.el.unmask();
						return false;
					}
					if (this.list.store.getAt(i).data["WZSL"] == null) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行实发数量为空");
						this.panel.el.unmask();
						return false;
					}

					// 管理方式为3的，明细要有zbxh
					if (this.list.store.getAt(i).data["GLFS"] == 3
							&& this.list.store.getAt(i).data["KCXH"] != -1
							&& (this.list.store.getAt(i).data["ZBXH"] == null || this.list.store
									.getAt(i).data["GLFS"] == "")) {
						Ext.Msg.alert("提示", "台账物资需要维护资产明细");
						this.panel.el.unmask();
						return false;
					}

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
				if (!body["WL_CK01"].JBGH) {
					this.panel.el.unmask();
					Ext.Msg.alert("提示", "领用人员不能为空");
					return false;
				}
				body["WL_CK01"].ZBLB = this.zblb;
				body["WL_CK01"].KFXH = this.mainApp['phis'].treasuryId;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "saveCheckIn",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
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
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
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
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
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
				for (var i = 0; i < count; i++) {
					body["WL_CK02"].push(this.list.store.getAt(i).data);
				}

				// 增加库存,记账
				body["WL_CK01"] = this.form.getFormData();

				body["WL_CK01"].ZBLB = this.zblb;
				body["WL_CK01"].KFXH = this.mainApp['phis'].treasuryId;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "commit",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
					this.fireEvent("winClose", this);
					this.fireEvent("save", this);
				}
			},
			doReject : function() {
				var djxh = this.form.getFormData().DJXH;
				var ksdm = this.form.getFormData().KSDM;
				var lzdh = this.form.getFormData().LZDH;
				this.form.doNew();
				// this.form.form.getForm().findField("THDJ").setValue(djxh);
				this.form.form.getForm().findField("KSDM").setValue(ksdm);
				this.form.form.getForm().findField("DJBZ").setValue("由单据【"
						+ lzdh + "】冲红产生");
				this.changeButtonState("blank");
				this.op = "create";
				this.thdj = djxh;
				this.type = "commitedReject";
				// 改变流转方式
				// this.form.doIs("back");

				// 增加一列可退数量
				this.list.grid.getColumnModel().setHidden(
						this.list.grid.getColumnModel().getIndexById("KTSL"),
						false);
				for (var i = 0; i < this.list.grid.store.data.length; i++) {
					this.list.grid.store.data.itemAt(i).set('KTSL',
							this.list.grid.store.data.itemAt(i).data["WZSL"]);
					this.list.grid.store.data.itemAt(i).set('WZSL', 0);
					this.list.grid.store.data.itemAt(i).set('WZJE', 0);
				}
			},
			doClose : function() {
				this.beforeClose();
				this.fireEvent("winClose", this);
				return true;
			},
			beforeClose : function(winclose) {
				// if (this.list.editRecords && this.list.editRecords.length >
				// 0) {
				// if (confirm('数据已经修改，是否保存?')) {
				// return this.doSave(winclose)
				// } else {
				// return true;
				// }
				// }
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
				// this.list.remoteDicStore.baseParams = {
				// "zblb" : this.zblb
				// }
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
					this.setButtonsState(["create", "import", "save", "print",
									"close"], true);
				}
				if (state == "new") {
					this.setButtonsState(["create", "import", "save", "verify",
									"print", "close"], true);
				}
				if (state == "verified") {
					this.setButtonsState(["create", "cancelVerify", "commit",
									"print", "close"], true);
					// this.list.setButtonsState(["remove"], false);
					// this.form.isRead = true;
				}
				if (state == "commited") {
					this.setButtonsState(
							["create", "reject", "print", "close"], true);
					// this.list.setButtonsState([ "remove"], false);
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
				this.list.op = "create";
				this.list.requestData.cnd = ['eq', ['$', 'a.DJXH'],
						['i', initDataBody.DJXH]];
				this.list.initCnd = ['eq', ['$', 'a.DJXH'],
						['i', initDataBody.DJXH]];
				this.list.loadData();
			},
			doImport : function() {
				var ksdm = this.form.getFormData().KSDM;
				// if (ksdm == null || ksdm == "" || ksdm == 0) {
				// Ext.Msg.alert("提示", "请选择领用科室");
				// return;
				// }
				this.applyManagementImportModule = this.createModule(
						"applyManagementImportModule", this.refImportModule);
				this.applyManagementImportModule.on("save", this.onSave, this);
				this.applyManagementImportModule.on("winClose", this.onClose,
						this);
				// 账簿类别
				this.applyManagementImportModule.zblb = this.zblb;
				this.applyManagementImportModule.ksdm = ksdm;
				// this.transferManagementDetailModule.initPanel();
				var win = this.getWin();
				win.add(this.applyManagementImportModule.initPanel());
				win.setWidth(920);
				win.setHeight(400);
				this.applyManagementImportModule.comboNameDetailList.on(
						"checkData", this.onCheckData, this);

				// this.applyManagementImportModule.loadData(body);
				win.show();
				win.center()
				if (!win.hidden) {
					this.applyManagementImportModule.op = "create";
					// this.applyManagementImportModule.doNew();
				}
			},
			// 处理库存界面返回
			onCheckData : function(records) {
				var store = this.list.grid.getStore();
				store.removeAll();
				var ksdm = 0;
				var jgbh = 0;
				Ext.each(records, function() {
					ksdm = this.data["SLKS"];
					jgbh = this.data["SLGH"];
						// if (this.data["GLFS"] == 3) {
						// this.data["WZSL"] == null;
						// this.set("WZSL", null);
						// this.commit();
						// }
					})
				store.add(records);
				this.form.form.getForm().findField("KSDM").setValue(ksdm);
				this.form.form.getForm().findField("JBGH").setValue(jgbh);
				// // 增加一列可退数量
				// this.list.grid.getColumnModel().setHidden(
				// this.list.grid.getColumnModel().getIndexById("TJSL"),
				// false);
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
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : "申领管理引入信息",
						width : 800,
						height : 300,
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
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			}
		});