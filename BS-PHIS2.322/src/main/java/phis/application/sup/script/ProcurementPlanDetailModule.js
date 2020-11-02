$package("phis.application.sup.script");
$import("phis.script.SimpleModule");
phis.application.sup.script.ProcurementPlanDetailModule = function(cfg) {
	cfg.width = 1024;
	cfg.height = 500;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	phis.application.sup.script.ProcurementPlanDetailModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sup.script.ProcurementPlanDetailModule,
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
					items : [ {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'north',
						width : 960,
						height : 70,
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
					tbar : (this.tbar || []).concat(this.createButtons())
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
				this.changeButtonState("blank");
				this.form.op = "create";
				this.form.doNew();
				this.list.op = "create";
				this.op = "create";
				this.list.clear();
				this.list.editRecords = [];
			},
			doCreate : function() {
				var winclose = false;
				this.beforeClose(winclose);
				this.doNew();
			},
			doSave : function(winclose) {
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var body = {};
				body["WL_JH02"] = [];
				var count = this.list.store.getCount();
				for ( var i = 0; i < count; i++) {

					if (this.list.store.getAt(i).data["WZMC"] == ""
							|| this.list.store.getAt(i).data["WZMC"] == null) {
						this.panel.el.unmask();
						continue;
					}

					if (this.list.store.getAt(i).data["WZSL"] == 0
							|| this.list.store.getAt(i).data["WZSL"] == null) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行数量为0");
						this.panel.el.unmask();
						return false;
					}

					body["WL_JH02"].push(this.list.store.getAt(i).data);
				}
				if (body["WL_JH02"].length < 1) {
					this.panel.el.unmask();
					Ext.Msg.alert("提示", "没有明细信息,保存失败");
					return false;
				}
				body["WL_JH01"] = this.form.getFormData();
				if (!body["WL_JH01"]) {
					this.panel.el.unmask();
					return false;
				}
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

				body["WL_JH01"] = this.form.getFormData();
				if (!body["WL_JH01"]) {
					return;
				}
				body["WL_JH02"] = [];
				var count = this.list.store.getCount();
				for ( var i = 0; i < count; i++) {
					body["WL_JH02"].push(this.list.store.getAt(i).data);
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
					MyMessageTip.msg("提示", "审核成功!", true);
					this.loadData(this.initDataBody);
					this.changeButtonState("verified");
					this.fireEvent("save", this);
				}
			},
			doClose : function() {
				this.beforeClose();
				this.fireEvent("winClose", this);
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
					$require(script, [ function() {
						eval(script + '.do' + cmd + '.apply(this,[item,e])')
					}, this ])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [ item, e ])
					}
				}
			},
			loadData : function(initDataBody) {
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
					this.setButtonsState([ "create", "import", "save", "print",
							"close" ], true);
				}
				if (state == "new") {
					this.setButtonsState([ "create", "import", "save",
							"verify", "print", "close" ], true);
				}
				if (state == "verified") {
					this.setButtonsState([ "create", "cancelVerify", "commit",
							"print", "close" ], true);
				}
				if (state == "commited") {
					this.setButtonsState(
							[ "create", "reject", "print", "close" ], true);
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
				this.list.op = "update";
				this.list.requestData.cnd = [ 'eq', [ '$', 'DJXH' ],
						[ 'i', initDataBody.DJXH ] ];
				this.list.loadData();
				this.list.requestData.cnd = null;
			},
			doImport : function() {
				this.procurementPlanSelectForm = this.createModule(
						"procurementPlanSelectForm", this.refEditorList);
				this.procurementPlanSelectForm.on("save", this.onSave, this);
				this.procurementPlanSelectForm.on("winClose", this.onClose,
						this);
				this.procurementPlanSelectForm.on("checkData",
						this.onCheckData, this);
				var win = this.getWin();
				win.add(this.procurementPlanSelectForm.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.procurementPlanSelectForm.op = "create";
				}
			},
			onCheckData : function(records) {
				this.onClose();
				this.list.loadDatas(records);
				records = null;
				this.list.grid.getView().refresh();
			},
			onClose : function() {
				this.getWin().hide();
			},
			onSave : function() {
				this.fireEvent("save", this);
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : "自动计划选择",
						width : 280,
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
					win.on("hide", function() {
						this.fireEvent("hide", this)
					}, this)
					this.win = win
				}
				return win;
			}
		});