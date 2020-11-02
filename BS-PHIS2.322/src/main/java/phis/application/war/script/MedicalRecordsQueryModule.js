$package("phis.application.war.script")

$import("phis.script.SimpleModule");
phis.application.war.script.MedicalRecordsQueryModule = function(cfg) {
	phis.application.war.script.MedicalRecordsQueryModule.superclass.constructor.apply(
			this, [cfg]);

},

Ext.extend(phis.application.war.script.MedicalRecordsQueryModule,
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
							items : [{
										id : "queryForm",
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 140,
										items : this.getQueryForm()
									}, {
										id : "listModule",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRecordsList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getQueryForm : function() {
				var module = this.createModule("refQueryForm",
						this.refQueryForm);
				this.form = module;
				module.on("select", this.onDoSelect, this);
				module.on("cancel", this.onCancel, this);
				var qForm = module.initPanel();
				this.qForm = qForm;
				return qForm;
			},

			getRecordsList : function() {
				var module = this.createModule("refRecordsList",
						this.refRecordsList);
				module.initCnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['ne', ['$', 'BLZT'], ['s', '9']]];
				module.requestData.cnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['ne', ['$', 'BLZT'], ['s', '9']]];
				this.list = module;
				var grid = module.initPanel();
				this.grid = grid;
				return grid;
			},
			onDoSelect : function(cnd) {
				var cnd1 = ['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]
				if (cnd != null) {
					cnd1 = ['and', cnd, cnd1];
				}
				this.list.requestData.serviceId = "phis.medicalRecordsQueryService";
				this.list.requestData.serviceAction = "listMedicalRecords";
				this.list.requestData.cnd = cnd1;
				this.list.initCnd = cnd1;
				this.list.loadData();
			},
			onCancel : function() {
				if (this.panel) {
					this.panel.destroy();
				}
			}
		});