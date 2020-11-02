$package("chis.application.dr.script");

$import("app.biz.dr.apply.DRMPIBaseSelect")

chis.application.dr.script.DRMPISelectView=function(cfg){
	chis.application.dr.script.DRMPISelectView.superclass.constructor.apply(this,[cfg]);
	
}
Ext.extend(chis.application.dr.script.DRMPISelectView,chis.application.dr.script.DRMPIBaseSelect,{
	getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								height : this.height,
//								autoWidth : true,
//								autoHeight : true,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction :closeAction,
								constrainHeader : true,
								minimizable : false,
								resizable : false,
								maximizable : false,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					this.win = win;
				}
				return win;
			}
});