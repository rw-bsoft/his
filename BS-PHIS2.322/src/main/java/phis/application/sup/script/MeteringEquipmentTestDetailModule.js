$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.sup.script.MeteringEquipmentTestDetailModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.sup.script.MeteringEquipmentTestDetailModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.sup.script.MeteringEquipmentTestDetailModule,
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
						title : '计量器具检定',
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
					tbar : (this.tbar || []).concat(this.createButtons())
				});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
				this.list.clear();
				this.form.doNew();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			// 引入
			doYr : function() {
				this.yrList = this.createModule("yrList", this.refYrList);
				this.yrList.on("qd", this.onQd, this)
				var win = this.yrList.getWin();
				win.add(this.yrList.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.yrList.clearSelect();
					this.yrList.refresh();
				}
			},
			// 检定
			doJd : function() {
				this.panel.el.mask("正在检定...", "x-mask-loading");
				var body = {};
				body["jlxx"] = [];
				var count = this.list.store.getCount();
				for ( var i = 0; i < count; i++) {
					body["jlxx"].push(this.list.store.getAt(i).data);
				}
				if (body["jlxx"].length < 1) {
					MyMessageTip.msg("提示", "没有明细", true);
					this.panel.el.unmask();
					return;
				}
				body["jdxx"] = this.form.getFormData();
				if (!body["jdxx"]) {
					this.panel.el.unmask();
					return;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.saveActionId,
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doJd);
					this.panel.el.unmask();
					return;
				}
				this.panel.el.unmask();
				this.fireEvent("save", this)
				this.doClose();
			},
			// 引入窗口关闭
			doClose : function() {
				this.getWin().hide();
			},
			// 引入窗口确定
			onQd : function(record) {
				this.list.requestData.cnd = [ 'in', [ '$', 'JLXH' ], record ];
				this.list.refresh();
			}
		})