$package("chis.application.jkjy.script")

$import("app.desktop.Module")

chis.application.jkjy.script.Jkjy = function(cfg) {
	chis.application.jkjy.script.Jkjy.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(chis.application.jkjy.script.Jkjy,app.desktop.Module, {
	initPanel : function(sc) {
		if(this.name=='单位管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020411&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='志愿者管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020412&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='注销单位管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020413&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='注销志愿者管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020414&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='卫生宣传库存管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020415&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='卫生宣传发放清单'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020416&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='个体健康干预一览表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020423&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='个体健康干预情况'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020421&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='社区授课培训'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020432&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='健康授课培训地点'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020422&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='健康授课管理报表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020455&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='卫生周日'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020417&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='质量系数管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020454&username=sq_jkjy&password=1&otheruser={'sqdm':'"+this.mainApp.deptId+"','zsyhxm':'健康教育','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
	}
})