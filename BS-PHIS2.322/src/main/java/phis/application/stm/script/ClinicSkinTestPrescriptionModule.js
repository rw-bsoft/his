$package("phis.application.stm.script")

$import("phis.script.SimpleModule")

phis.application.stm.script.ClinicSkinTestPrescriptionModule = function(cfg) {
	phis.application.stm.script.ClinicSkinTestPrescriptionModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.stm.script.ClinicSkinTestPrescriptionModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
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
										region : 'center',
										items : [this.getList()]
									}, {
										layout : "fit",
										border : false,
										region : 'north',
										height : 90,
										items : this.getForm()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				var module = this.createModule("refForm", this.refSkinTestForm);
				this.form = module;
				return this.form.initPanel();

			},
			getList : function() {
				var module = this.createModule("refList",
						this.refSkinTestRecordList);
				this.list = module;
				return this.list.initPanel();

			},
			doNew : function() {
				this.form.doNew();
				this.list.store.removeAll();
				this.list.setCountInfo();
			},
			loadData : function() {
				if (!this.initDataId)
					return;
				this.form.initDataId = this.initDataId;
				this.form.loadData();
				this.list.requestData.cnd = ['eq', ['$', 'a.CFSB'],
						['l', this.initDataId]];
				this.list.loadData();
			}
		});