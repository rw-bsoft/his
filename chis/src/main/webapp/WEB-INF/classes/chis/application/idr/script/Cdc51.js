$package("chis.application.idr.script")

$import("app.desktop.Module")

chis.application.idr.script.Cdc51 = function(cfg) {
	chis.application.idr.script.Cdc51.superclass.constructor.apply(this,
			[cfg])
			
}
Ext.extend(chis.application.idr.script.Cdc51,app.desktop.Module, {
	initPanel : function(sc) {
		if(this.name=='肠道门诊就诊卡'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
					"gnbh=H1020302&username=sq_crb&password=1&otheruser=" +
					"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='传染病报告卡'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020305&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='传染病管理列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020306&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='病家消毒记录单'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020307&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='肠道症候群列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020309&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='监测月报表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020313&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='感染性腹泻病原谱'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020310&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='腹泻病监测报告'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020312&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='霍乱监测报表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020314&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='菌痢监测报表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020315&username=sq_crb&password=1&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='三小场所列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020231&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='宾馆列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020232&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='其他娱乐场所列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020233&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='建筑工地列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020234&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='集贸市场列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020235&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='流动人口聚集地'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020236&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='居委列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020237&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='学校列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020238&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='工厂列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020239&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='场所干预记录列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020212&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='场所档案信息列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020211&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='被干预人员信息列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020213&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='被干预人员档案匹配'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020221&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='个体干预人员信息'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020228&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='高危库'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020259&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='患者人群列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020219&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='初筛阳性高危人群'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020216&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='送检单列表'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020229&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		if(this.name=='HIV确诊患者'){
			var url="http://10.96.36.154:8410/CDCPro/hospital/loginhospital.action?" +
			"gnbh=H1020260&username=axsqzs&password=123456&param={'czsq':'"+this.mainApp.deptId+"'}&otheruser=" +
			"{'zsyhxm':'"+this.mainApp.uname+"','sqdm':'"+this.mainApp.deptId+"','zsyhbm':'"+this.mainApp.uid+"'}";
			window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", " +
			"top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
		};
		
	}
	
})