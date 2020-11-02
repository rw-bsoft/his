$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicLisMedicalReportModule = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicLisMedicalReportModule.superclass.constructor.apply(
			this, [cfg]);
}

/***
 * LIS报告页面内嵌HIS
 */
Ext.extend(phis.application.cic.script.ClinicLisMedicalReportModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var jyip = "";
				var res = phis.script.rmi.miniJsonRequestSync({
						serviceId : "hisGetPatientDiagnoseRecordService",
						serviceAction : "getLisXTCS"
					});
					if (res.code > 300) {
						return false;
					}else{
						jyip = eval(res.json).JIANYANSERVERIP;
					}
				
				/**
				 * yjlb:调用类别(1:门诊/2:住院)
				 * jgdm:机构代码  this.mainApp['phisApp'].deptId
				 * brsy：病人ID   this.exContext.ids.brid
				 */
				var zdmc = "";//诊断名称
				var zdxh = 0;//诊断序号
				var url = "module=BGCX&yjlb=1&jgdm="+this.mainApp['phisApp'].deptId+"&patientid="+this.exContext.empiData.MZHM 
				//2017-08-09 实现沿江卫生服务中心下属的复兴社区可以开检验单，模拟为沿江医院的请求。
				//机构编码修改为320111002，科室代码修改为1296（复兴卫生室）
				if(this.mainApp['phisApp'].deptId=="320111002004"){
					url = "module=BGCX&yjlb=1&jgdm=320111002&patientid="+this.exContext.empiData.MZHM 
				}
				//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
				//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
				else if(this.mainApp['phisApp'].deptId=="320124005016"){
					url = "module=BGCX&yjlb=1&jgdm=320124005&patientid="+this.exContext.empiData.MZHM 
				}
				//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
				//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
				else if(this.mainApp['phisApp'].deptId=="320124004018"){
					url = "module=BGCX&yjlb=1&jgdm=320124004&patientid="+this.exContext.empiData.MZHM 
				}
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							html : "<iframe src='"+jyip+"/interface.jshtml?" +
							
									/**********modify by zhaojian 2017-05-11 **************/
									//this.base64encode(encodeURI(url))+
									url+
											"'scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
							frame : true,
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
			},
			//加密
		base64encode :function (str) {
			var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
			var base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
			63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
			50, 51, -1, -1, -1, -1, -1);		
	    	var out, i, len;
	    	var c1, c2, c3;
	    	len = str.length;
	    	i = 0;
		    out = "";
		    while(i < len) {
		        c1 = str.charCodeAt(i++) & 0xff;
		        if(i == len)
		        {
		            out += base64EncodeChars.charAt(c1 >> 2);
		            out += base64EncodeChars.charAt((c1 & 0x3) << 4);
		            out += "==";
		            break;
		        }
		        c2 = str.charCodeAt(i++);
		        if(i == len)
		        {
		            out += base64EncodeChars.charAt(c1 >> 2);
		            out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
		            out += base64EncodeChars.charAt((c2 & 0xF) << 2);
		            out += "=";
		            break;
		        }
		        c3 = str.charCodeAt(i++);
		        out += base64EncodeChars.charAt(c1 >> 2);
		        out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
		        out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >>6));
		        out += base64EncodeChars.charAt(c3 & 0x3F);
		    }
		    return out;
			}	
		});