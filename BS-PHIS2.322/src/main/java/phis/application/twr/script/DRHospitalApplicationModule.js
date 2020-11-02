$package("phis.application.twr.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.twr.script.DRHospitalApplicationModule = function(cfg) {
	cfg.height = 600;
	phis.application.twr.script.DRHospitalApplicationModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.twr.script.DRHospitalApplicationModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					this.formModule.doNew();
					this.listModule.hisModule.doNew();
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : 1000,
							height : 700,
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
										height : 300,
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
				var formModule = this.createModule("form", this.refForm);
				formModule.brid = this.exContext.ids.brid;
				formModule.empiId = this.exContext.ids.empiId;
				formModule.on("submit", this.onSubmit, this);
				formModule.on("cancel", this.onCancel, this);
				formModule.on("print", this.onPrint, this);
				formModule.on("close", this.onClose, this);
				this.formModule = formModule;
				var form = formModule.initPanel();
				return form;
			},
			getList : function() {
				var listModule = this.createModule("List", this.refList);
				listModule.exContext = this.exContext;
				this.listModule = listModule;
				this.listModule.opener = this;
				var list = listModule.initPanel();
				return list;
			},
			onSubmit : function() {
				if(!(this.listModule.hisModule.zrjgmcvalue&&this.listModule.hisModule.zrjgvalue)){
					MyMessageTip.msg("提示", "转入机构不能为空", true);
					return;
				}else if(!(this.listModule.hisModule.zrksmcvalue&&this.listModule.hisModule.zrksvalue)){
					MyMessageTip.msg("提示", "转入科室不能为空", true);
					return;
				}
				this.listModule.brxxform = this.formModule.form.getForm();
				this.listModule.brid = this.formModule.brid;
				this.listModule.mzhm = this.formModule.mzhm;
				this.listModule.csny = this.formModule.csny;
				this.listModule.brxb = this.formModule.brxb;
				this.listModule.empiId = this.exContext.ids.empiId;
				this.listModule.zzzd = this.formModule.zzzd;
				this.listModule.doSave();
			},
			onClose : function() {
				this.getWin().hide();
				return true;
			}
		});