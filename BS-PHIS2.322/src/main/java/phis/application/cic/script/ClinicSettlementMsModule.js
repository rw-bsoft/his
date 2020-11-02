/**
 * 门诊收费处理上面2个form
 * 
 * @author caijy
 */
$package("phis.application.cic.script");

$import("phis.script.SimpleModule");

phis.application.cic.script.ClinicSettlementMsModule = function(cfg) {
	this.width = 380;
	this.height = 335;
	cfg.modal = this.modal = true;

	phis.application.cic.script.ClinicSettlementMsModule.superclass.constructor.apply(
			this, [cfg]);
	this.on('winShow', this.onWinShow, this);
}
Ext.extend(phis.application.cic.script.ClinicSettlementMsModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var form = this.getSettlementForm();
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
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : form
									}]
						});
				this.panel = panel;
				return panel;
			},
			onWinShow : function() {
				if (this.settlementForm) {
					this.settlementForm.afterShow(this.settlementForm);
				}
			},
			doPrintFp : function(fphm) {//打印注销
				var module = this.createModule("fpprint",
						this.refSettlementPrint)
				module.fphm = fphm;
				module.initPanel();
				module.doPrint();
			},
			getSettlementForm : function() {
				var module = this.createModule("SettlementForm",
						this.refSettlementForm);
				module.opener = this;
				this.JSXX;//该值暂时不用，因ClinicSettlementForm中有反调用
				module.on("settlement", this.settlement, this);
				module.on("fpsc", this.fpsc, this);
				module.on("winClose", this.onWinClose, this);
				var m = module.initPanel();
				var field = m.form.findField('JKJE');
				field.on("blur", module.focusFieldAfter, module);
				this.settlementForm = module;
				return m;
			},
			setData : function(data, MZXX, jsData) {
				this.data = data;
				this.settlementForm.setValue(data, MZXX, jsData);
			},
			settlement : function() {
				this.fireEvent("settlement", this);
			},
			fpsc : function() {
				this.fireEvent("fpsc", this);
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								shadow : false,
								modal : this.modal,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
			onWinClose : function() {
				//this.fireEvent("winClose", this);
			}
		});