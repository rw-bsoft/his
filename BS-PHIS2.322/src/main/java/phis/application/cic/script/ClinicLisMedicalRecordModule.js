$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicLisMedicalRecordModule = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicLisMedicalRecordModule.superclass.constructor.apply(
			this, [cfg]);
}

/***
 * LIS开单页面内嵌HIS
 */
Ext.extend(phis.application.cic.script.ClinicLisMedicalRecordModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var mzks = "";
				var jyip = "";
				var res = phis.script.rmi.miniJsonRequestSync({
						serviceId : "hisGetPatientDiagnoseRecordService",
						serviceAction : "findKsdm",
						ksdm : this.mainApp['phis'].reg_departmentId
					});
					if (res.code > 300) {
						return false;
					}else{
						mzks = eval(res.json).MZKS;
						jyip = eval(res.json).JIANYANSERVERIP;
					}
					
				/**
				 * yjlb:调用类别(1:门诊/2:住院)
				 * jgdm:机构代码  this.mainApp['phisApp'].deptId
				 * brsy：病人ID   this.exContext.ids.brid
				 * ysgh：医生工号 this.mainApp.uid
				 * jzxh:就诊序号  this.exContext.ids.clinicId
				 * ks：科室代码   this.mainApp['phis'].reg_departmentId
				 * icd:ICD编码
				 * icdname:ICD名称
				 */
					var zdmc = "";//诊断名称
					var zdxh = 0;//诊断序号
					//var url = "module=sqd01&yjlb=1&jgdm="+this.mainApp['phisApp'].deptId+"&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks="+mzks+""
					//alert("机构id："+this.mainApp['phisApp'].deptId+"，病人ID："+this.exContext.ids.brid+"，就诊序号："+this.exContext.ids.clinicId);
					
					/********************modify by zhaojian 2017-05-10 去除门诊检验开单时校验医生必须先给病人完成诊断录入************************************/
					var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hisGetPatientDiagnoseRecordService",
					serviceAction : "getPatientDiagnose",
					body : {
								jgid : this.mainApp['phisApp'].deptId,
								brid : this.exContext.ids.brid,
								jzxh : this.exContext.ids.clinicId
							}
						});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						if(eval(r.json).ZDXH != 0 && "" != eval(r.json).ZDMC){																						
							zdxh = eval(r.json).ZDXH;
							zdmc = eval(r.json).ZDMC;
							url= "module=sqd01&yjlb=1&jgdm="+this.mainApp['phisApp'].deptId+"&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks="+mzks+"&icd="+zdxh+"&icdname="+zdmc
							//2017-08-09 实现沿江卫生服务中心下属的复兴社区可以开检验单，模拟为沿江医院的请求。
							//机构编码修改为320111002，科室代码修改为1296（复兴卫生室）
							if(this.mainApp['phisApp'].deptId=="320111002004"){
								url= "module=sqd01&yjlb=1&jgdm=320111002&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks=1296&icd="+zdxh+"&icdname="+zdmc								
							}
							//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
							//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
							else if(this.mainApp['phisApp'].deptId=="320124005016"){
								url= "module=sqd01&yjlb=1&jgdm=320124005&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks=1107&icd="+zdxh+"&icdname="+zdmc 
							}
							//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
							//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
							else if(this.mainApp['phisApp'].deptId=="320124004018"){
								url= "module=sqd01&yjlb=1&jgdm=320124004&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks=1109&icd="+zdxh+"&icdname="+zdmc 
							}
						}else{
							var url = "module=sqd01&yjlb=1&jgdm="+this.mainApp['phisApp'].deptId+"&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks="+mzks+""
							//MyMessageTip.msg("提示", eval(r.json).MSG, true);
							//return;
							//2017-08-09 实现沿江卫生服务中心下属的复兴社区可以开检验单，模拟为沿江医院的请求。
							//机构编码修改为320111002，科室代码修改为1296（复兴卫生室）
							if(this.mainApp['phisApp'].deptId=="320111002004"){
								url= "module=sqd01&yjlb=1&jgdm=320111002&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks=1296"							
							}
							//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
							//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
							else if(this.mainApp['phisApp'].deptId=="320124005016"){
								url= "module=sqd01&yjlb=1&jgdm=320124005&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks=1107" 
							}
							//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
							//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
							else if(this.mainApp['phisApp'].deptId=="320124004018"){
								url= "module=sqd01&yjlb=1&jgdm=320124004&brsy="+this.exContext.ids.brid+"&ysgh="+this.mainApp.uid+"&jzxh="+this.exContext.ids.clinicId+"&ks=1109" 
							}
						}
					}
					var encodeurl = this.base64encode(encodeURI(url));
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							html : "<iframe src='"+jyip+"/interface.jshtml?" +
									//encodeurl+
									//***************modify by zhaojian  2017-05-02 加密后无法解析***********************/
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