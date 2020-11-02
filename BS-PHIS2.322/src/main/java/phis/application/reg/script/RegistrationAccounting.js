$package("phis.application.reg.script")

$import("app.modules.chart.DiggerChartView")

phis.application.reg.script.RegistrationAccounting = function(cfg) {

	phis.application.reg.script.RegistrationAccounting.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.reg.script.RegistrationAccounting,
		app.modules.chart.DiggerChartView, {
			initPanel : function() {
				if (this.panel) {
					return this.panel
				}
				var cfg = {
					layout : "fit",
//					width : this.width,
//					height : this.height,
					border : false,
					applyTo : '_user_chart',
					html : this.initChartHTML()
				}
//				if (this.showPrtActionOnBottom) {
//					if (this.showCndsBar) {
//						cfg.tbar = [];
//					}
//					cfg.bbar = this.createButtons()
//				} else {
//					if (this.showCndsBar) {
//						cfg.tbar = this.createButtons()
//					}
//				}
				var panel = new Ext.Panel(cfg)
				if (this.isCombined) {
					panel.on("render", this.onWinShow, this)
				}
				this.panel = panel
				return panel;
			}
		})
