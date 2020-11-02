$package("phis.application.hos.script")

$import("phis.script.TableForm")

phis.application.hos.script.HospitalAntibacterialAduitQueryForm = function(cfg) {
	cfg.colCount = 3;
	cfg.showButtonOnTop = false;
	phis.application.hos.script.HospitalAntibacterialAduitQueryForm.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalAntibacterialAduitQueryForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.hos.script.HospitalAntibacterialAduitQueryForm.superclass.onReady
						.call(this)
				var container = new Ext.Container({
							layout : "hbox",
							colspan : 3,
							items : [new Ext.form.Label({
												text : "申请日期:",
												cls : "x-form-item-label",
												width : "84"
											}), {
										xtype : "uxspinner",
										name : 'SQRQBegin',
										value : new Date().format('Y-m-d'),
										strategy : {
											xtype : "date"
										}
									}, new Ext.form.Label({
												text : "~"
											}), new Ext.ux.form.Spinner({
												name : 'SQRQEnd',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												}
											})]
						})
				this.form.insert(3, container);
			}
		});