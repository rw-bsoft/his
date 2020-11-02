$package("phis.application.war.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.war.script.InhospLisMedicalRecordModule = function(cfg) {
	this.isModify = false;
	phis.application.war.script.InhospLisMedicalRecordModule.superclass.constructor.apply(
			this, [cfg]);
	this.data = {};
}

/***
 * LIS开单页面内嵌HIS
 */
Ext.extend(phis.application.war.script.InhospLisMedicalRecordModule,
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
				
				if(this.exContext.empiData && this.exContext.ids){
					var json = eval(this.exContext.empiData);
					if(json.AGE <= 1){
						this.data.yebz = 1;
					}else{
						this.data.yebz = 0;
					}
					this.data.jgdm = "";//this.mainApp['phisApp'].deptId
					this.data.zyh = this.exContext.ids.clinicId;
					this.data.ysgh = this.mainApp.uid;
					/*modify by zhaojian 2017-05-08*/
					//this.data.ks = json.BRKS;
					this.data.ks = json.BRBQ;
				}
				
				/**
				 *yjlb:调用类别(1:门诊/2:住院)birthday
				 *jgdm:机构代码
				 *zyh：住院号
				 *ysgh：医生工号
				 *ks：病区科室
				 *yebz:婴儿唯一号（成人传0）
				 */
					var jbmc = "";//疾病名称
					var zdxh = 0;//诊断序号
					var url = "module=sqd01&yjlb=2&jgdm="+this.mainApp['phisApp'].deptId+"&zyh="+this.data.zyh+"&ysgh="+this.data.ysgh+"&yebz="+this.data.yebz+"&ks="+this.data.ks+"&icd=&icdname="
					//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
					//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
					if(this.mainApp['phisApp'].deptId=="320124005016"){
						url= "module=sqd01&yjlb=2&jgdm=320124005&zyh="+this.data.zyh+"&ysgh="+this.data.ysgh+"&yebz="+this.data.yebz+"&ks=1107&icd=&icdname="
					}
					//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
					//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
					else if(this.mainApp['phisApp'].deptId=="320124004018"){
						url= "module=sqd01&yjlb=2&jgdm=320124004&zyh="+this.data.zyh+"&ysgh="+this.data.ysgh+"&yebz="+this.data.yebz+"&ks=1109&icd=&icdname="
					}
					var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : "hisGetPatientDiagnoseRecordService",
						serviceAction : "getPatientHospital",
						body : {
									jgid : this.mainApp['phisApp'].deptId,
									zyh :  this.data.zyh
								}
						});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
					if(eval(r.json).ZDXH != 0 && "" != eval(r.json).JBMC){																						
							zdxh = eval(r.json).ZDXH;
							jbmc = eval(r.json).JBMC;
							url = "module=sqd01&yjlb=2&jgdm="+this.mainApp['phisApp'].deptId+"&zyh="+this.data.zyh+"&ysgh="+this.data.ysgh+"&yebz="+this.data.yebz+"&ks="+this.data.ks+"&icd="+zdxh+"&icdname="+jbmc
							//2017-10-18 zhaojian 【溧水】实现石湫卫生服务中心下属的明觉分院可以开检验单，模拟为石湫医院的请求。
							//开申请单时机构编码修改为320124005，科室代码修改为1107（明觉分院）
							if(this.mainApp['phisApp'].deptId=="320124005016"){
								url= "module=sqd01&yjlb=2&jgdm=320124005&zyh="+this.data.zyh+"&ysgh="+this.data.ysgh+"&yebz="+this.data.yebz+"&ks=1107&icd="+zdxh+"&icdname="+jbmc
							}
							//2017-10-31 zhaojian 【溧水】实现柘塘卫生服务中心下属的群力分院可以开检验单，模拟为柘塘医院的请求。
							//开申请单时机构编码修改为320124004，科室代码修改为1109（群力分院）
							else if(this.mainApp['phisApp'].deptId=="320124004018"){
								url= "module=sqd01&yjlb=2&jgdm=320124004&zyh="+this.data.zyh+"&ysgh="+this.data.ysgh+"&yebz="+this.data.yebz+"&ks=1109&icd="+zdxh+"&icdname="+jbmc
							}
						}
					}
					var encodeurl = this.base64encode(encodeURI(url));
				var panel = new Ext.Panel({
							border : false,
							html : "<iframe src='"+jyip+"/interface.jshtml?" +
									url
									+"' scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
							frame : true,
							autoScroll : true,
							autoDestroy  : true
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
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
								width : 1024,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.panel
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.panel.destroy();
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			}
		});