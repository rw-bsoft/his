$package("phis.application.twr.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.twr.script.DRNewHospitalModule = function(cfg) {
	this.exContext = {};
	phis.application.twr.script.DRNewHospitalModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.twr.script.DRNewHospitalModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				// 体检模块
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : {
								type : 'hbox',
								align : 'stretch'
							},
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										flex : 1,
										width : "20%",
										items : this.getDateForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										flex : 1,
										items : this.getHisList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getDateForm : function() {
				var dateModule = this
						.createModule("dateForm", this.refDateForm);
				dateModule.on("openWin", this.onOpenWin, this);
				var dateForm = dateModule.initPanel();
				return dateForm;
			},
			getHisList : function() {
				var hisModule = this.createModule("hisList", this.refHisList);
				this.hisModule = hisModule;
				this.hisModule.opener = this;
				var hisList = hisModule.initPanel();
				return hisList;
			},
			doSave : function() {
				this.hisModule.brid = this.brid;
				this.hisModule.mzhm = this.mzhm;
				this.hisModule.csny = this.csny;
				this.hisModule.brxb = this.brxb;
				this.hisModule.zzzd = this.zzzd;
				this.hisModule.empiId = this.empiId;
				this.hisModule.brxxform = this.brxxform;
				this.hisModule.doSave();
			},
			onOpenWin : function(e, t) {
				this.hisModule.showZYSQ(t);
			}
		});