$package("chis.application.smtj.script")

$import("app.desktop.Module")

chis.application.smtj.script.Smtj = function(cfg) {
	chis.application.smtj.script.Smtj.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(chis.application.smtj.script.Smtj,app.desktop.Module, {
	initPanel : function(sc) {
		if(this.name=='死亡证明单管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020104&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='死亡推断书管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020105&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='死亡确认书管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020106&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='死亡监测管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020108&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='非户籍死亡证明单'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020112&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='非户籍死亡推断书'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020113&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='非户籍死亡确认书'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020114&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='公安名单自动匹配'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020115&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='公安名单手动匹配'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020116&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='院外分娩'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020118&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='上海危重儿追踪调查'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020120&username=100&password=123456&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
	}
})