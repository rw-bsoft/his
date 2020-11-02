$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
phis.application.cic.script.ClinicDoctorDepartmentModule = function(cfg) {
	cfg.colCount = 2;
	cfg.fldDefaultWidth = 600;
	cfg.defaultHeight = 150;
	this.plugins = ["undoRedo", "removeFmt", "subSuper", "speChar"];
	phis.application.cic.script.ClinicDoctorDepartmentModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.cic.script.ClinicDoctorDepartmentModule,
		phis.script.SimpleModule, {
			initPanel : function() {
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
										region : 'west',
										// height : 200,
										width : 338,
										items : this
												.getDoctorDepartmentList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										// height : 200,
										width : 600,
										items : this.getDoctorDepartmentDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getDoctorDepartmentList : function() {
				var module = this.createModule("getDoctorDepartmentList",
						this.refDoctorDepartmentList);
				var list = module.initPanel();
				//module.on("dblChoose", this.dblChoose, this);
				//module.on("choose", this.choose, this);
				return list;
			},
			getDoctorDepartmentDetailList : function() {
				var module = this.createModule("getDoctorDepartmentDetailList",
						this.refDoctorDepartmentDetailList);
				var list = module.initPanel();
				return list;

			}/*,
			dblChoose : function(record) {
				var zlxh = record.data.ZLXH;
				var ksdm = record.data.KSDM;
				var data = {};
				data['ZLXH'] = zlxh;
				this.data = data;
				var TRDList = this.midiModules['getDoctorDepartmentAuthorityDetailList'];
				TRDList.exContext.initDataId = zlxh;
				// TRDList.onReady();
				TRDList.requestData.cnd = ['eq', ['$', 'ZLXH'], ['s', zlxh]];
				TRDList.loadData();
			},
			choose : function(record) {
				var zlxh = record.data.ZLXH;
				var TRDList = this.midiModules['getDoctorDepartmentAuthorityDetailList'];
				TRDList.exContext.initDataId = zlxh;
			}*/
		});