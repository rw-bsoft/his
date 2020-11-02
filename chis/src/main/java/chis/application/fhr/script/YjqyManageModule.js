$package("chis.application.fhr.script")
$import("app.desktop.Module")
//亿家签约模块
chis.application.fhr.script.YjqyManageModule = function(cfg) {
	cfg.width = 810;
	cfg.modal = true
	chis.application.fhr.script.YjqyManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.YjqyManageModule,
		app.desktop.Module, {
			initPanel : function() {
				var res = util.rmi.miniJsonRequestSync({
								serviceId : "chis.mobileAppService",
								serviceAction : "getUserMsg",
								method : "execute"
							});
				if (res.code != 200) {
					this.processReturnMsg(res.code, res.msg);
					return null;
				}
				//http://workstation.njpkjk.com
				var url="http://workstation.njpkjk.com/medical/team/list?idcard="+res.json.data.cardnum;
//				var url="http://32.33.1.232:8007/medical/team/list?idcard=320116198701021769";
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