$package("phis.application.emr.script")

$import("phis.script.SimpleModule")
phis.application.emr.script.EMRModifyRecordModule = function(cfg) {
	cfg.exContext = {};
	this.lastBlbh = null;
	phis.application.emr.script.EMRModifyRecordModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.emr.script.EMRModifyRecordModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										region : 'west',
										width : 340,
										items : this.getModifyList()
									}, {
										layout : "fit",
										region : 'center',
										items : this.getModifyForm()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
				this.on("winShow", this.onWinShow, this);
			},
			onWinShow : function() {
				if (this.lastBlbh != this.blbh) {
					this.cacheHjnr = {};
				}
				if (this.blbh) {
					this.list.requestData.cnd = ['eq', ['$', 'BLBH'],
							['d', this.blbh]];
					this.list.refresh();
				}
			},
			getModifyList : function() {
				var module = this.createModule("refModifyList",
						this.refModifyList);
				module.autoLoadData = false;
				module.on("firstRowSelected", this.selectRow, this);
				this.list = module;
				return module.initPanel();
			},
			getModifyForm : function() {
				var module = this.createModule("refModifyForm",
						this.refModifyPanel);
				module.on("loadData", this.afterFormLoadData, this);
				this.form = module;
				return module.initPanel();
			},
			selectRow : function() {
				var selectedIndex = this.list.selectedIndex;
				var r = this.list.store.getAt(selectedIndex || 0);
				if (!r)
					return;
				if (this.cacheHjnr[r.get("JLXH")]) {
					this.form.form.getForm().findField("HJNR")
							.setValue(this.cacheHjnr[r.get("JLXH")])
				} else {
					this.form.initDataId = r.get("JLXH");
					this.form.loadData();
				}
			},
			afterFormLoadData : function(entryName, body) {
				if (!this.cacheHjnr) {
					this.cacheHjnr = {};
				}
				this.cacheHjnr[body.JLXH] = body.HJNR;
			},
			doClose : function() {
				this.win.hide();
			},
			doCompare : function() {
				var selects = this.list.selects;
				var n = 0;
				var r = [];
				for (var id in selects) {
					n++;
					r.push(selects[id]);
				}
				if (n != 2) {
					MyMessageTip.msg("提示", "必须选中两份病历修改痕迹才能进行对比!", true);
					return;
				}
				if (!this.cacheHjnr[r[0].get("JLXH")]) {
					this.form.initDataId = r[0].get("JLXH");
					this.cacheHjnr[r[0].get("JLXH")] = this.form.loadHjnr();
				}
				if (!this.cacheHjnr[r[1].get("JLXH")]) {
					this.form.initDataId = r[1].get("JLXH");
					this.cacheHjnr[r[1].get("JLXH")] = this.form.loadHjnr();
				}
				var r1 = r[0].get("XGSJ") + "#"
						+ this.cacheHjnr[r[0].get("JLXH")];
				var r2 = r[1].get("XGSJ") + "#"
						+ this.cacheHjnr[r[1].get("JLXH")];
				if (Date.parseDate(r[0].get("XGSJ"), 'Y-m-d H:i:s').getTime() < Date
						.parseDate(r[1].get("XGSJ"), 'Y-m-d H:i:s').getTime()) {
					this.emr.FunActiveXInterface("BsShowMatchForm", r1, r2)
				} else {
					this.emr.FunActiveXInterface("BsShowMatchForm", r2, r1)
				}
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								collapsible : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : false,
								maximizable : false,
								shadow : false,
								modal : true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					// win.on("close", function() {
					// this.fireEvent("close", this);
					// }, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			}

		});