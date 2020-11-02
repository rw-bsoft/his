$package("phis.application.twr.script");
$import("phis.script.SimpleModule");
phis.application.twr.script.TwoWayReferralClinicModule = function(cfg) {
	phis.application.twr.script.TwoWayReferralClinicModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.twr.script.TwoWayReferralClinicModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					this.formModule.doNew();
					this.listModule.doNew();
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : 1030,
							height : 500,
							frame : true,
							layout : "border",
							defaults : {
								border : false
							},
							buttonAlign : "center",
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 281,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				var formModule = this.createModule("formModule",
						this.formModule);
				formModule.empiId = this.exContext.ids.empiId;
				formModule.on("submit", this.onSubmit, this);
				formModule.on("cancel", this.onCancel, this);
				formModule.on("print", this.onPrint, this);
				formModule.on("close", this.onClose, this);
				formModule.exContext=this.exContext;
				this.formModule = formModule;
				var formPanel = formModule.initPanel();
				formModule.loadData();
				return formPanel;
			},
			getList : function() {
//				var xtcs = phis.script.rmi.miniJsonRequestSync({
//							serviceId : "referralService",
//							serviceAction : "getXTCS"
//						});
//				var sjdorehy = xtcs.json.body;
				var listModule = "";
//				if (sjdorehy == "1") {
					listModule = this.createModule("listModule",
							this.listSDModule);
//				} else {
//					listModule = this.createModule("listModule",
//							this.listHMModule);
//				}
				listModule.opener = this;
				listModule.exContext=this.exContext;
				this.listModule = listModule;
				return listModule.initPanel();
			},
			onSubmit : function() {
				this.listModule.brxxform = this.formModule.form.getForm();
				this.listModule.brid = this.formModule.brid;
				this.listModule.mzhm = this.formModule.mzhm;
				this.listModule.csny = this.formModule.csny;
				this.listModule.brxb = this.formModule.brxb;
				this.listModule.jzxh = this.exContext.ids.clinicId;
				this.listModule.empiId = this.exContext.ids.empiId;
				this.listModule.zzzd = this.formModule.zzzd;
				this.listModule.doSave();
			},
			onClose : function() {
				this.getWin().hide();
				return true;
			}
		});