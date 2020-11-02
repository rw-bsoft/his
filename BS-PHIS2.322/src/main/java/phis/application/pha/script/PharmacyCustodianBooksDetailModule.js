/**
 * 药房模块,打开详情界面Module
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyCustodianBooksDetailModule = function(cfg) {
	phis.application.pha.script.PharmacyCustodianBooksDetailModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyCustodianBooksDetailModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : 800,
							height : 500,
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
										width : 800,
										height : 45,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 800,
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
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			doCancel : function() {
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
			// 修改
			loadData : function(data) {
				this.form.doNew();
				this.form.initFormData(data);
				this.list.requestData.serviceId=this.serviceId;
				this.list.requestData.serviceAction=this.listActionId;
				this.list.requestData.body=data;
				this.list.loadData();
			}
		});