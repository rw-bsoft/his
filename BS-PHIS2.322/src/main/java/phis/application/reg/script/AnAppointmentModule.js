$package("phis.application.reg.script");

$import("phis.script.SimpleModule")

phis.application.reg.script.AnAppointmentModule = function(cfg) {
	cfg.width = 500;
	phis.application.reg.script.AnAppointmentModule.superclass.constructor.apply(this,
			[cfg])
	this.on('winShow', this.onWinShow, this);
	this.on("shortcutKey",this.shortcutKeyFunc,this);
}

Ext.extend(phis.application.reg.script.AnAppointmentModule, phis.script.SimpleModule,
		{
			initPanel : function() {
				if (this.complexPanel) {
					return this.complexPanel;
				}
				this.tabIndex = 1;
				var panel = new Ext.Panel({
							border : false,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 376,
							items : [{
										layout : "fit",
										region : 'center',
										height : 290,
										items : this
												.getAnAppointmentDetailsList()
									}, {
										layout : "fit",
										border : false,
										region : 'north',
										height : 102,
										items : this.getAnAppointmentForm()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getAnAppointmentForm : function() {
				this.AnAppointmentForm = this.createModule(
						"refAnAppointmentForm", this.refAnAppointmentForm);
				this.AnAppointmentFormView = this.AnAppointmentForm.initPanel();
				this.AnAppointmentForm.opener = this;
				var form = this.AnAppointmentForm.form.getForm()
				var f = form.findField("JZKH")
				if (f) {
					f.un("specialkey",
							this.AnAppointmentForm.onFieldSpecialkey, this)
					f.on("specialkey", function(f, e) {
								var key = e.getKey()
								if (key == e.ENTER) {
									this.AnAppointmentForm.doENTER(f)
								}
							}, this)
					f.focus(false, 200);
				}
				return this.AnAppointmentFormView;
			},
			getAnAppointmentDetailsList : function() {
				this.AnAppointmentDetailsList = this.createModule(
						"refAnAppointmentDetailsList",
						this.refAnAppointmentDetailsList);
				this.AnAppointmentDetailsList.opener = this;
				this.AnAppointmentDetailsGrid = this.AnAppointmentDetailsList
						.initPanel();
				this.AnAppointmentDetailsList.on("reservationChoose",
						this.doReservationChoose, this);
				this.AnAppointmentDetailsList.on("specialkey",
						this.doSpecialkey, this);
				return this.AnAppointmentDetailsGrid;
			},
			showCFD : function(brid) {
				this.AnAppointmentDetailsList.requestData.serviceId = "phis.anAppointmentService";
				this.AnAppointmentDetailsList.requestData.serviceAction = "getAnAppointment";
				this.AnAppointmentDetailsList.requestData.schema = "MS_YYGL_MX";
				this.AnAppointmentDetailsList.requestData.cnd = [brid,
						this.GHSJ, this.ZBLB];
				this.AnAppointmentDetailsList.loadData();
			},
			doReservationChoose : function(grid, record) {
				if (record) {
					this.fireEvent("reservationChoose", grid, record);
				}
			},
			doSpecialkey : function(field, e) {
				if (e.getKey() == Ext.EventObject.ENTER) {
					var value = field.getValue();
					if (value) {
						this.AnAppointmentDetailsList.doCommit();
					}
				}
			},
			onWinShow : function() {
				var form = this.AnAppointmentForm.form.getForm();
				var JZKH = form.findField("JZKH");
				var brxm = form.findField("BRXM");
				var brxb = form.findField("BRXB_text");
				var brnl = form.findField("BRNL");
				var brxz = form.findField("BRXZ");
				var lxdz = form.findField("LXDZ");
				JZKH.setValue();
				brxm.setValue();
				brxb.setValue();
				brnl.setValue();
				brxz.setValue();
				lxdz.setValue();
				this.AnAppointmentDetailsList.clear();
			}
		});