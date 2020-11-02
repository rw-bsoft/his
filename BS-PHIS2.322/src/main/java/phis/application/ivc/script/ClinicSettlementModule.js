/**
 * 门诊收费处理上面2个form
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule", "util.helper.Helper");

phis.application.ivc.script.ClinicSettlementModule = function(cfg) {
	this.width = 380;
	this.height = 380;
	cfg.modal = this.modal = true;
	this.printurl = util.helper.Helper.getUrl();
	phis.application.ivc.script.ClinicSettlementModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('winShow', this.onWinShow, this);
	/**
	 * 监听快捷键 shortcutKeyFunc common.js有默认实现类
	 * 如有特殊需求要重写，需要重新定义监听的方法名称，否则会被common中的默认方法覆盖
	 */
	this.on("shortcutKey", this.shortcutKeyFunc, this);
}
Ext.extend(phis.application.ivc.script.ClinicSettlementModule,
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
			// ctrl_D : function() {
			// MyMessageTip.msg("提示", "收费页面的ctrl_D", true);
			// },
			onWinShow : function() {
				if (this.settlementForm) {
					this.settlementForm.afterShow(this.settlementForm);
				}
			},
			getSettlementForm : function() {
				var module = this.createModule("SettlementForm",
						this.refSettlementForm);
				module.opener = this;
				module.on("settlement", this.settlement, this);
				module.on("settlementFinish", this.settlementFinish, this);
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
			settlementFinish : function() {
				this.fireEvent("settlementFinish", this);
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
								title : this.name,
								width : 380,
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
				this.fireEvent("winClose", this);
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('mzfp');"
			},
			doPrintFp : function(fphm) {
	    		
	    	}
//			doFp : function(fphm) {
//					var pages="phis.prints.jrxml.Invoice";
//					 var url="resources/"+pages+".print?silentPrint=1&execJs="
//							+ this.getExecJs();
//					url += "&temp=" + new Date().getTime() + "&fphm=" + fphm
//							+ "&flag=true";
//					var printWin = window
//							.open(
//									url,
//									"",
//									"height="
//											+ (screen.height - 100)
//											+ ", width="
//											+ (screen.width - 10)
//											+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
//					printWin.onafterprint = function() {
//						printWin.close();
//					};
//			}
		});