$package("phis.application.hos.script");

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalPatientCardsViewModule = function(cfg) {
	Ext.apply(this, app.modules.common)
	cfg.width = 680;
	cfg.height = 500;
	cfg.modal = true;
	phis.application.hos.script.HospitalPatientCardsViewModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalPatientCardsViewModule,
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
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 138,
										items : this.getForm()
									}],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});

				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				 this.doFillIn(this.data);
			},
			getList : function() {
				var module = this.createModule("refSettlementManagementList",
						this.refList);
				this.list = module;
				module.opener = this;
				return module.initPanel();
			},
			getForm : function() {
				var module = this.createModule("refSettlementManagementForm",
						this.refForm);
				this.form = module;
				var form = module.initPanel();
				module.opener = this;
				return form
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref
				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
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
			// 缴款
			doContributions : function() {
				if (!this.body) {
					MyMessageTip.msg("提示", "请先选择病人", true);
					return;
				}
				var module = this.createModule("jkjlList", this.refcontributions);
				// module.on("commit",this.doFillIn,this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.requestData.cnd = ['and',
						['eq', ['$', 'a.ZFPB'], ['i', 0]],
						['eq', ['$', 'a.ZYH'], ['i', this.body.ZYH]]];
				module.refresh();
				// this.form.doNew();
				// this.list.store.removeAll();
				// module.doNew();
				// win.center();
				win.show();
			},
			// 清单
			doInventory : function() {
				if (!this.body) {
					MyMessageTip.msg("提示", "请先选择病人", true);
					return;
				}
				var module = this.createModule("fyqdModule",this.refinventory);
				module.data = this.body;
				module.JSJE=this.form.form.getForm().findField("JSJE")
						.getValue();
				var win = module.getWin();
				win.add(module.initPanel());
				win.show();
			},
			doFillIn : function(data) {
				if(data.JSLX != -1){
					data.JSLX = 0
				}
				this.form.ldata = data;
				this.list.ldata = data;
				this.form.loadData();
				this.list.loadData();
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}

		});