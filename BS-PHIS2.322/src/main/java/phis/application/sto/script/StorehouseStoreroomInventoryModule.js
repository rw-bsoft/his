$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseStoreroomInventoryModule = function(cfg) {
	this.width = 700;
	this.height = 550;
	cfg.change_kcsl = new Array();// 全局变量 用于存哪些库存修改了实盘数量
	cfg.modal = this.modal = true;
	phis.application.sto.script.StorehouseStoreroomInventoryModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	// this.on("beforeclose", this.doClose, this);
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(phis.application.sto.script.StorehouseStoreroomInventoryModule,
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
										title : '库存盘点',
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
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
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
			},
			doNew : function() {
				this.setButtonsState(["commit"], false);
				this.setButtonsState(["save"], true);
				var change_kcsl = this.getPC_KCSL("create", {});
				this.form.op = this.op;
				this.form.doNew();
				this.panel.items.items[0].setTitle('库存盘点');
				this.list.change_kcsl = change_kcsl;
				this.list.op = this.op;
				this.list.requestData.serviceId = this.fullserviceId;
				this.list.requestData.serviceAction = this.list.queryActionId;
				this.list.requestData.op = this.op;
				this.list.requestData.body = {};
				this.list.isRead = this.isRead;
				this.list.loadData();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData", this.afterLoad, this);
				return this.form.initPanel();
			},
			getList : function() {
				var refList = this.refList
				if (this.KCPD_PC == "true" || this.KCPD_PC == true) {
					refList = this.refList_pc;
				}
				this.list = this.createModule("list", refList);
				return this.list.initPanel();
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.PDDH);
			},
			getPC_KCSL : function(op, body) {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryPCActionId,
							op : op,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, "数据查询异常", this.getPC_KCSL);
					return null;
				}
				this.change_kcsl = ret.json.body;
				return this.change_kcsl
			},
			doSave : function() {
				var ed=this.list.grid.activeEditor;
				if (!ed) {
						ed = this.list.grid.lastActiveEditor;
					}
					if(ed){
					ed.completeEdit();
					}
				var body = {};
				var formData = this.form.getFormData();
				if (formData == null) {
					return;
				}
				body["YK_PD01"] = formData;
				body["YK_PD02"] = this.list.getData();
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body,
							op : this.op
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				MyMessageTip.msg("提示", "保存成功", true);
				this.fireEvent("save", this);
				this.getWin().hide();
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
			doLoad : function(xtsb, pddh) {
				this.setButtonsState(["commit"], true);
				if (this.isRead) {
					this.setButtonsState(["commit", "save"], false);
				} else {
					this.setButtonsState(["save"], true);
				}
				var initDataBody = {};
				initDataBody["XTSB"] = xtsb;
				initDataBody["PDDH"] = pddh;
				this.form.initDataBody = initDataBody;
				this.form.op = this.op;
				this.form.loadData();
				var change_kcsl = this.getPC_KCSL("update", initDataBody);
				this.list.change_kcsl = change_kcsl;
				this.list.op = this.op;
				this.list.requestData.serviceId = this.fullserviceId;
				this.list.requestData.serviceAction = this.list.queryActionId;
				this.list.requestData.op = this.op;
				this.list.requestData.body = initDataBody;
				this.list.isRead = this.isRead;
				this.list.loadData();
			},
			doCommit : function() {
				if (this.op != "update") {
					return;
				}
				var body = {};
				var formData = this.form.getFormData();
				if (formData == null) {
					return;
				}
				body["YK_PD01"] = formData;
				body["YK_PD02"] = this.list.getData();
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.commitActionId,
							body : body
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					this.fireEvent("save", this);
					return;
				}
				MyMessageTip.msg("提示", "提交成功", true);
				this.fireEvent("save", this);
				this.getWin().hide();
			},
			onWinShow:function(){
			this.list.cndField.setValue("");
			this.list.requestData.cnd=null;
			},
			//打印 zhaojian 2017-09-21 增加药库库存盘点导出功能
			doPrint:function(){
				var pWin = this.midiModules["StorehouseStoreroomInventoryPrintView"]
				var cfg = {
					xtsb : this.XTSB,
					pddh : this.PDDH
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.application.sto.script.StorehouseStoreroomInventoryPrintView(cfg)
				this.midiModules["StorehouseStoreroomInventoryPrintView"] = pWin
				pWin.getWin().show()
			}
		})