$package("chis.application.jczd.script")

$import("app.desktop.Module")

chis.application.jczd.script.Jczd = function(cfg) {
	chis.application.jczd.script.Jczd.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(chis.application.jczd.script.Jczd,app.desktop.Module, {
	initPanel : function(sc) {
		if(this.name=='查看指导计划'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020803&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='指导计划反馈'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020801&username=zhb_sq&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='生成指导记录'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020801&username=zhb_cdc&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='指导记录评价与审核'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020802&username=zhb_cdc&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='查看指导记录'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020802&username=zhb_sq&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
	}
})