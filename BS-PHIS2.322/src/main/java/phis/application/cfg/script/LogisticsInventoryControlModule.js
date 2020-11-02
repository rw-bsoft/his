$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.LogisticsInventoryControlModule = function(cfg) {
	this.exContext = {};
	phis.application.cfg.script.LogisticsInventoryControlModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.cfg.script.LogisticsInventoryControlModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
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
						ddGroup : "firstGrid",
						border : false,
						split : true,
						title : '收费项目',
						region : 'west',
						width : 400,
						items : this.getList()
					}, {
						layout : "fit",
						ddGroup : "secondGrid",
						border : false,
						split : true,
						title : '对照信息',
						region : 'center',
						items : this.getRightModule()
					} ],
					tbar : (this.tbar || []).concat(this.createButtons())
				});
				this.panel = panel;
				panel.on("beforeclose", this.onBeforeclose, this)
				return panel;
			},
			getRightModule : function() {
				this.rightModule = this.createModule("rightModule",
						this.refModule)
				return this.rightModule.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("click", this.onClick, this);
				return this.list.initPanel();
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
			// 单击费用时 刷新右边数据
			onClick : function(d) {
				this.panel.el.mask("正在查询...", "x-mask-loading");
				this.checkData = d;
				this.rightModule.loadData(d);
				this.panel.el.unmask();
			},
			// 新增
			doAdd : function() {
				this.rightModule.rightList.onDblClick();
			},
			// 取消
			doRemove : function() {
				this.rightModule.leftList.doRemove();
			},
			// 保存
			doSave : function() {
				var body = {};
				body["FYXH"] = this.checkData.FYXH;
				var fywz = this.rightModule.getData();
				// if(fywz==null){
				// return;}
				body["fywz"] = fywz;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.saveActionId,
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.list.refresh();
			},
			doRefresh : function() {
				if (this.rightModule.leftList.isEdit == 1) {
					Ext.Msg.show({
						title : '确认刷新',
						msg : '有修改未保存的数据,确定刷新?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								this.list.refresh();
							}
						},
						scope : this
					})
				} else {
					this.list.refresh();
				}
			},
			onBeforeclose : function() {
				if (this.rightModule.leftList.isEdit == 1) {
					if (confirm('有修改未保存的数据,确定关闭?')) {
						return true;
					}
					return false
				} else {
					return true;
				}
			}

		});