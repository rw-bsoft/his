$package("chis.application.fhr.script")
$import("app.desktop.Module")
//亿家签约统计模块 zhaojian 2017-11-08
chis.application.fhr.script.YjqytjManageModule = function(cfg) {
	cfg.width = 810;
	cfg.modal = true
	chis.application.fhr.script.YjqytjManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.YjqytjManageModule,
		app.desktop.Module, {
			initPanel : function() {
				var url = "http://32.33.1.232:8007/statistic";
				var html="<iframe src="	+ url + " width='100%' height='107%' frameborder='no'></iframe>";
				var panel = new Ext.Panel({
				frame : false,
				autoScroll : true,
				html : html
				})
				this.panel = panel
				return panel
			}
		})