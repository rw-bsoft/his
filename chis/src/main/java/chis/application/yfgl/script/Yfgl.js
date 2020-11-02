$package("chis.application.yfgl.script")

$import("app.desktop.Module")

chis.application.yfgl.script.Yfgl = function(cfg) {
	chis.application.yfgl.script.Yfgl.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(chis.application.yfgl.script.Yfgl,app.desktop.Module, {
	initPanel : function(sc) {
		if(this.name=='视力档案管理'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020700&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='眼防档案随访质控'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020701&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='眼防查询'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020703&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='视力档案报表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020706&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='老人视力新建档情况'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020707&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='老人视力建档情况'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020708&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='老人配镜情况表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action" +
			"?gnbh=H1020709&username=sq_yanf&password=123456&otheruser={'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
	}
})