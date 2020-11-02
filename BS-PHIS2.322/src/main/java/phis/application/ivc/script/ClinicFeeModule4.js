/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.ClinicFeeModule4 = function(cfg) {
	this.width = 130;
//	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.ivc.script.ClinicFeeModule4.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ivc.script.ClinicFeeModule4,
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
										region : 'south',
										height : 130,
										items : this.getForm()
									}, {
										layout : "fit",
//										border : false,
//										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
			},
			getForm : function() {
				var module = this.createModule("form2", this.refForm);
				module.exContext = this.exContext;
				module.opener = this;
				this.form = module;
				return module.initPanel();
			},
			getList : function() {
				var module = this.createModule("List3", this.refList);
				module.exContext = this.exContext;
				var list = module.initPanel();
				this.list = module;
				module.opener = this;
				return list;
			}
		});