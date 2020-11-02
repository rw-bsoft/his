/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.ClinicReceivablesInvoiceModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.ivc.script.ClinicReceivablesInvoiceModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.ivc.script.ClinicReceivablesInvoiceModule, phis.script.SimpleModule,
		{
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
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 78,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});
				this.panel = panel;
				return panel;
			},
			loadData : function(person){
				this.formModule.MZXX = this.person;
				this.formModule.initDataId = this.initDataId;
				this.formModule.loadData();
				this.listModule.requestData.cnd = ['eq', ['$', 'c.FPHM'], ["s",this.initDataId]];
				this.listModule.djs=this.djs;
				this.listModule.fpcx = 1;
				this.listModule.refresh();
			},
			doCflr : function() {
			},
			getForm : function() {
				var module = this.createModule("Form ", this.refForm);
				var formModule = module.initPanel();
				this.formModule = module;
				
				module.opener = this;
				var form = module.form.getForm()
				var f = form.findField("MZGL")
				if (f) {
					f.setDisabled(true);
				}
				var BRXB = form.findField("BRXB");
				BRXB.getStore().on("load",module.loadData,module);
				var BRXZ = form.findField("BRXZ");
				BRXZ.getStore().on("load",module.loadData,module);
				return formModule;
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				//this.listModule.refresh();
				module.openby = "FPCX";
				var listModule = module.initPanel();
				this.listModule = module;
				module.opener = this;
				return listModule;
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
					/*if(this.person.ZFPB){
						if(action.id=="fpzf"){
							continue;
						}
					}else{
						if(action.id=="qxzf"){
							continue;
						}
					}*/
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					//btn.scale = "large";
					//btn.iconAlign = "top";
					buttons.push(btn, '-');
				}
				this.buttons = buttons;
				return buttons;
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
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}
		});