$package("chis.application.idr.script")

$import("app.desktop.Module")

chis.application.idr.script.Cdc55 = function(cfg) {
	chis.application.idr.script.Cdc55.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(chis.application.idr.script.Cdc55,app.desktop.Module, {
	initPanel : function(sc) {
		var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
				"gnbh=H1020308&username=sq_crb&password=1&otheruser=" +
				"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
		window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
	}
	
})