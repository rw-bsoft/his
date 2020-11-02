$package("phis.application.njjb.script");

$import("phis.script.TableForm",
		"phis.script.Phisinterface");

phis.application.njjb.script.Njjbmessageform_ZYJS = function(cfg) {
	this.entryName="phis.application.njjb.schemas.NJJB_KXX_ZY";
	Ext.apply(this, phis.script.Phisinterface);
	phis.application.njjb.script.Njjbmessageform_ZYJS.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.njjb.script.Njjbmessageform_ZYJS,
		phis.script.TableForm, {
			// 退出
			doTc : function() {
				this.getWin().hide();//destroy();
			},
			// 确认
			doQr : function() {
				var yllb=this.form.getForm().findField('YLLB').getValue();
				if(yllb=="" ||yllb.length==0 ){
					MyMessageTip.msg("提示：","请选择医疗类别！", true);
					return;
				}
				this.ybxx.YLLB=yllb;
				this.fireEvent("qr", this.ybxx);
				this.doTc();
			},
			doDk : function(){
				this.addPKPHISOBJHtmlElement();
				this.drinterfaceinit();
				//先执行读卡程序
				var body={};
				body.USERID=this.mainApp.uid;
				//获取业务周期号
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "getywzqh",
						body:body
						});
				if (ret.code <= 300) {
					var ywzqh=ret.json.YWZQH;
					var str=this.buildstr("2100",ywzqh,"");
					var drre=this.drinterfacebusinesshandle(str);
					var arr=drre.split("^");
					if(arr[0]=="0"){ 
						var canshu=arr[2].split("|")
						var data={};
						data.SHBZKH=canshu[0];//社会保障卡号
						data.DWBH=canshu[1];//单位编号
						data.DWMC=canshu[2];//单位名称
						data.SFZH=canshu[3];//身份证号
						data.XM=canshu[4];//姓名
						data.XB=canshu[5];//性别
						data.YLRYLB=canshu[6];//医疗人员类别
						data.YDRYBZ=canshu[7];//异地人员标志
						data.TCQH=canshu[8];//统筹区号
						data.DQZHYE=canshu[9];//当前帐户余额
						data.ZYZT=canshu[10];//在院状态
						data.BNZYCS=canshu[11];//本年住院次数
						data.DYXSBZ=canshu[12];//待遇享受标志
						data.DYBXSYY=canshu[13];//待遇不享受原因
						data.BZDJQK=canshu[14];//病种登记情况
						data.YBMMZG=canshu[15];//医保门慢资格
						data.YBMMBZ=canshu[16];//医保门慢病种
						data.YBMJZG=canshu[17];//医保门精资格
						data.YBMJBZ=canshu[18];//医保门精病种
						data.YBMAZG=canshu[19];//医保门艾资格
						data.YBMABZ=canshu[20];//医保门艾病种
						data.YBBGGRSZG=canshu[21];//医保丙肝干扰素资格
						data.YBBGGRSBZ=canshu[22];//医保丙肝干扰素病种
						data.YBMZXYBZG=canshu[23];//医保门诊血友病资格
						data.YBMZXYBBZ=canshu[24];//医保门诊血友病病种
						data.YBMTZG=canshu[25];//医保门特资格
						data.YBMTBZ=canshu[26];//医保门特病种
						data.YBTYZG=canshu[27];//医保特药资格
						data.YBTYBZ=canshu[28];//医保特药病种
						data.YBTYMCBM=canshu[29];//医保特药名称编码
						data.JMMDZG=canshu[30];//居民门大资格
						data.JMMDBZ=canshu[31];//居民门大病种
						data.JMMZXYBZG=canshu[32];//居民门诊血友病资格
						data.JMMZXYBBZ=canshu[33];//居民门诊血友病病种
						data.JMTYZG=canshu[34];//居民特药资格
						data.JMTYBZ=canshu[35];//居民特药病种
						data.JMTYMCBM=canshu[36];//居民特药名称编码
						data.NMGMDZG=canshu[37];//农民工门大资格
						data.NMGMDBZ=canshu[38];//农民工门大病种
						data.NMGTYZG=canshu[39];//农民工特药资格
						data.NMGTYBZ=canshu[40];//农民工特药病种
						data.NMGTYMCBM=canshu[41];//农民工特药名称编码
						data.NFXSZGMZTC=canshu[42];//能否享受职工门诊统筹
						data.SYSPLX=canshu[43];//生育审批类型
						data.FSYY=canshu[44];//封锁原因
						data.MMSYKBJE=canshu[45];//门慢剩余可报金额
						data.MTFZZLSYKBJE=canshu[46];//门特辅助治疗剩余可报金额
						data.GSDYZG=canshu[47];//工伤待遇资格
						data.GSDYBZ=canshu[48];//工伤待遇病种
						data.GSZDJL=canshu[49];//工伤诊断结论
						data.DKSYKBJE=canshu[50];//大卡剩余可报金额
						data.MTSYKBJE=canshu[51];//门统剩余可报金额
						data.YBJCZG=canshu[52];//医保家床资格
//						this.initFormData(data);
						/****增加建档立卡低收入人群参数  hj****/
						data.YBMZZXZG = canshu[53];//医保门诊专项资格
						data.YBMZZXYPTYMBM = canshu[54];//医保门诊专项药品通用名编码
						data.SFKNJDLK = canshu[55];//是否困难建档立卡人员
						this.initFormData(data);
						this.ybxx = data;
						return data;
					}else{
						MyMessageTip.msg("南京金保提示：",drre, true);
						return;	
					}
				} else {
					MyMessageTip.msg("提示：",ret.msg, true);
					return;
				}
			}
		});
