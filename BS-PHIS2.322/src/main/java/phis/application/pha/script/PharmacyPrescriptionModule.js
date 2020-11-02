$package("phis.application.pha.script")
$import("phis.script.SimpleModule");
phis.application.pha.script.PharmacyPrescriptionModule = function(cfg) {
	phis.application.pha.script.PharmacyPrescriptionModule.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyPrescriptionModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								items : this.getList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								width : 960,
								height : 120,
								region : 'north',
								items : this.getForm()
							}]
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.cfForm = this.createModule("cfForm", this.refForm);
				return this.cfForm.initPanel();
			},
			getList : function() {
				this.cfDetaiList = this.createModule("cfDetaiList",
						this.refList);
				return this.cfDetaiList.initPanel();
			},
			getAuditDetailList : function() {
				return this.cfDetaiList;
			},
			getPrescriptionForm : function() {
				return this.cfForm;
			}
		});