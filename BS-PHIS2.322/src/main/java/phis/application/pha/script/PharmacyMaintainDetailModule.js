/**
 * 采购入库详细module
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyMaintainDetailModule = function(cfg) {
	cfg.width = 1020;
	cfg.height = 550;
	cfg.title = "药品养护单";// module的标题
	phis.application.pha.script.PharmacyMaintainDetailModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(phis.application.pha.script.PharmacyMaintainDetailModule,
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
										title : this.title,
										region : 'north',
										width : 960,
										height : 90,
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
				if (this.bsBody) {// 不合格品报损
					this.form.bsBody = this.bsBody;
				}
				if (this.thBody) {// 不合格品退回
					this.form.thBody = this.thBody;
				}
				this.form.on("loadData", this.afterLoad, this);
				this.form.on("createRow", this.onCreateRow, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			// 保存
			doSave : function() {
				var ed = this.list.grid.activeEditor;
				if (!ed) {
					ed = this.list.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				var body = {};
				body["YF_YH01"] = this.form.getFormData();
				if (!body["YF_YH01"]) {
					return;
				}
				body["YF_YH01"].XTSB = this.mainApp.storehouseId;
				body["YF_YH02"] = this.getListData();
				if (body["YF_YH02"] == 0) {
					return;
				}
				if (body["YF_YH02"] == null) {
					MyMessageTip.msg("提示", "没有需要养护的明细", true);
					return;
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.fireEvent("close", this);
				this.fireEvent("save", this);
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "保存成功!", true);
				this.doLoad(r.json.body, "update");
				this.list.clearSelect();

			},// 提交
			doCommit : function() {
				var ed = this.list.grid.activeEditor;
				if (!ed) {
					ed = this.list.grid.lastActiveEditor;
				}
				if (ed) {
					ed.completeEdit();
				}
				var body = {};
				body["YF_YH01"] = this.form.getFormData();
				if (!body["YF_YH01"]) {
					return;
				}
				body["YF_YH02"] = this.getListData();
				if (body["YF_YH02"] == null) {
					MyMessageTip.msg("提示", "没有需要养护的明细", true);
					return;
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.commitActionId,
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.fireEvent("close", this);
				this.fireEvent("save", this);
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "确定成功!", true);
				this.list.clearSelect();
			},
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody, op) {
				this.op = "update";
				this.form.op = op;
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = op;
				this.list.requestData.body = {
					"YHDH" : initDataBody.YHDH,
					"XTSB" : initDataBody.XTSB
				};
				this.list.loadData();
				this.list.clearSelect();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.YHDH);
			},
			// 新增
			doNew : function() {
				this.op = "create";
				this.changeButtonState("new")
				this.form.op = "create";
				this.panel.items.items[0].setTitle(this.title);
				this.form.selectValue = this.selectValue;
				this.form.doNew();
				this.list.op = "create";
				this.list.doNew();
				this.list.clearSelect();
			},
			// 获取list的值
			getListData : function() {
				var ck02 = new Array();
				var records = this.list.getSelectedRecords();
				for (var i = 0; i < records.length; i++) {
					ck02.push(records[i].data);
				}
				return ck02;
			},
			// 关闭
			doCancel : function() {
				// this.fireEvent("close", this);
				this.list.clearSelect();
				this.getWin().hide();
			},
			// 重写 为了双击打开时显示保存和提交按钮
			loadData : function(initDataBody) {
				this.changeButtonState("xg")
				this.doLoad(initDataBody, "update");
			},
			doRead : function(initDataBody) {
				this.changeButtonState("read")
				this.doLoad(initDataBody, "read");
			},
			onWinShow : function() {
				this.list.cndField.setValue("");
				this.list.requestData.cnd = null;
			},
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				this.form.isRead = false;
				this.list.isRead = false;
				if (state == "read" || state == "commit") {
					this.form.isRead = true;
					this.list.isRead = true;
					if (state == "commit") {
						this.list.isCommit = true;
					} else {
						this.list.isCommit = false;
					}
					this.list.setButtonsState(["create", "remove"], false);
				} else {
					this.list.setButtonsState(["create", "remove"], true);
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.id == "cancel" || action.id == "print") {
						continue;
					}
					if (state == "read") {
						this.setButtonsState([action.id], false);
					} else if (state == "update" || state == "new") {
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
					} else {
						this.setButtonsState([action.id], true);
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
			}
		});