/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.ClinicFeeModule3 = function(cfg) {
	this.width = 130;
//	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.ivc.script.ClinicFeeModule3.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ivc.script.ClinicFeeModule3,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
//							border : false,
							width : this.width,
							height : this.height,
//							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
//										border : false,
//										split : true,
										title : '',
										region : 'east',
										width : 155,
										items : this.getModule4()
									}, {
										layout : "fit",
//										border : false,
//										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : new Ext.Toolbar({
								enableOverflow : true,
								items : (this.tbar || []).concat([this
										.createButton()])
							})
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
			},
			getModule4 : function() {
				var module4 = this.createModule("module4", this.refModule4);
				module4.exContext = this.exContext;
				module4.opener = this;
				this.module = module4;
				return module4.initPanel();
			},
			getList : function() {
				var module = this.createModule("List2", this.refList);
				module.exContext = this.exContext;
				var list = module.initPanel();
				this.list = module;
				module.opener = this;
				return list;
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
				if (this.butRule) {
					var ac = util.Accredit;
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								});
					}
				}
				// var f1 = 112

				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.cmd = action.id;
					btn.text = action.name;
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			doJX : function(){
				this.list.doJX();
			},
			doXY : function(){
				this.list.doXY();
			},
			doZY : function(){
				this.list.doZY();
			},
			doCY : function(){
				this.list.doCY();
			},
			doInsert : function(){
				this.list.doInsert();
			},
			doNewGroup : function(){
				this.list.doNewGroup();
			},
			doRemove : function() {
				this.list.doRemove();
			},
			doDelGroup : function(){
				this.list.doDelGroup();
			}
		});