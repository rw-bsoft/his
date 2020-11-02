$package("phis.application.sup.script");

$import("phis.script.SimpleModule", "phis.script.rmi.jsonRequest",
		"phis.script.widgets.Spinner", "phis.script.widgets.Strategy");

phis.application.sup.script.MeteringEquipmentTestModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	cfg.exContext = {};
	phis.application.sup.script.MeteringEquipmentTestModule.superclass.constructor
			.apply(this, [ cfg ]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sup.script.MeteringEquipmentTestModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (!this.mainApp['phis'].treasuryId) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryCsbz == "0") {
					Ext.Msg.alert("提示", "该库房未做账册初始化!");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [ {
						layout : "fit",
						border : false,
						ddGroup : "firstGrid",
						split : true,
						title : '',
						region : 'west',
						width : this.width,
						items : this.getList()
					}, {
						layout : "fit",
						ddGroup : "secondGrid",
						border : false,
						split : true,
						title : '',
						region : 'center',
						items : this.getRList()
					} ],
					tbar : (this.tbar || []).concat(this.createButtons())
				});
				this.panel = panel;
				return panel;
			},
			// 右边list
			getRList : function() {
				this.rlist = this.createModule("rlist", this.refRList);
				return this.rlist.initPanel();
			},
			// 左边list
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("rowClick", this.onRowClick, this);
				return this.list.initPanel();
			},
			// 刷新
			doRefresh : function() {
				this.list.refresh();
				// this.rlist.refresh();
			},
			// 检定
			doJd : function() {
				this.detailModule = this.createModule("detailModule",
						this.refDetailModule);
				var win = this.detailModule.getWin();
				this.detailModule.on("save", this.doRefresh, this);
				win.add(this.detailModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.detailModule.doNew();
				}
			},
			// 左边单击
			onRowClick : function(jlxh) {
				this.rlist.requestData.cnd = [ 'eq', [ '$', 'a.JLXH' ],
						[ 'd', jlxh ] ];
				this.rlist.refresh();
			}
		})