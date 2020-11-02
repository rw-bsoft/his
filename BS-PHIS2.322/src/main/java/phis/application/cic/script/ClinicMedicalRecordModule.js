$package("phis.application.cic.script")
$import("phis.script.SimpleModule",
		"phis.application.cic.script.ClinicDiagnosisEasyInputModule",
		"phis.application.cic.script.ClinicHealthEducationInputModule",
		"phis.script.common")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicMedicalRecordModule = function(cfg) {
	this.tcmstate = 0;//0表示未打开任何中医馆服务窗口 1表示中医馆服务窗口已开启
	this.isModify = false;
	this.isCZ = false;
	this.BRQX = 1;
	this.JKJY = "";
	this.CSZ = "0";
	this.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{MSZD}</td></td>';
	this.healthEduTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="60px">{RECIPENAME}</td><td width="40px">{ICD10}</td><td width="240px">{DIAGNOSENAME}</td>';
	this.isPause = false; // 是否是暂挂(暂挂时健康处方不必填 )
	Ext.apply(cfg, phis.application.cic.script.ClinicDiagnosisEasyInputModule); // 诊断录入快捷录入
	Ext.apply(cfg,phis.application.cic.script.ClinicHealthEducationInputModule);// 健康教育快捷录入
	phis.application.cic.script.ClinicMedicalRecordModule.superclass.constructor
			.apply(this, [cfg]);
	this.JKCFRecords = {};
}
var clinicMedicalRecord_ctx = null;
function openSkinTestWin(sbxh, ypxh, cfsb, gmywlb, gmywlbtext, ysid) {
	var module = clinicMedicalRecord_ctx.createModule("skintestWin",
			clinicMedicalRecord_ctx.refSkinTestForm);
	module.exContext = clinicMedicalRecord_ctx.exContext;
	module.on("doSave", clinicMedicalRecord_ctx.onClinicSave,clinicMedicalRecord_ctx);
	this.skinTestModule = module;
	this.skinTestModule.sbxh = sbxh;
	this.skinTestModule.ypxh = ypxh;
	this.skinTestModule.cfsb = cfsb;
	this.skinTestModule.gmywlb = gmywlb;
	this.skinTestModule.gmywlbtext = gmywlbtext;
	this.skinTestModule.ysid = ysid;
	var win = module.getWin();
	win.show();
}
/**
 * 用于体温、脉率、呼吸频率、收缩压、舒张压的回车事件
 * 
 * @param {}
 *            keycode
 * @param {}
 *            onfocs 当前焦点 体温 : T、脉率 : P 、呼吸频率 : R 、收缩压 : SSY 、舒张压 : SZY
 */
function keydown(keycode, onfocs) {
	if (keycode == 13) {// Enter事件
		if ("T" == onfocs) {
			document.getElementById('P').focus();
		} else if ("P" == onfocs) {
			document.getElementById('R').focus();
		} else if ("R" == onfocs) {
			document.getElementById('SSY').focus();
		} else if ("SSY" == onfocs) {
			document.getElementById('SZY').focus();
		} else if ("SZY" == onfocs) {
			document.getElementById('H').focus();
		} else if ("H" == onfocs) {
			document.getElementById('W').focus();
			clinicMedicalRecord_ctx.setBMIDoc();
		} else if ("W" == onfocs) {
			document.getElementById('T').focus();
			clinicMedicalRecord_ctx.setBMIDoc();
		}
	}

}
/**
 * 根据身高体重生成bmi
 * 
 * @param {}
 *            id
 */
function blurField(id) {
	clinicMedicalRecord_ctx.setBMIDoc();
}
Ext.extend(phis.application.cic.script.ClinicMedicalRecordModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							html : this.getHtml(),
							frame : true,
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				panel.on("bodyresize", this.bodyResize, this)
				return panel;
			},
			checkboxcheck : function() {
				// alert(1111);
			},
			bodyResize : function() {
				// var topDiv = Ext.get(this.panel.body.query("div.clear")[0]);
				var div = Ext.get(this.panel.body.query("div.Rlist")[0]);
				if (this.panel.getWidth() < 1196) {
					div.dom.style.top = '52px';
				} else {
					div.dom.style.top = '18px';
				}
			},
			onReady : function() {
				if (this.mainApp.reg_departmentId == this.zyks) {
					this.textareas = ["ZSXX","XBS","JWS","TGJC","FZJC","BQGZ","ZZ","GMS","JKJYNR"];
				} else {
					this.textareas = ["ZSXX","XBS","JWS","TGJC","FZJC","BQGZ","GMS","JKJYNR"];
				}
				this.panel.body.position("static");
				clinicMedicalRecord_ctx = this;

				for (var i = 0; i < this.textareas.length; i++) {
					new Ext.form.TextArea({
								id : this.textareas[i],
								grow : true,
								growMin : (this.textareas[i] == 'JKJY'? 108: 30),
								growMax : 200,
								maxLength : (this.textareas[i] == 'JKJY'? 2000: 500),
								width : (this.textareas[i] == 'JKJY'? "98%": "90%"),
								renderTo : "div_" + this.textareas[i]
							});
				}

//				this.createHealthEduField(); // 健康教育
				// this.medicareSmartReminde("1");
				this.initClinicRecord("5");// 初始化病历信息
				this.attachLnkEnvents();
				// modify by yangl 统一请求参数，减少交互次数
				// var qysxzz = this.loadSystemParams({
				// "privates" : ['QYSXZZ']
				// });
				this.CSZ = this.exContext.systemParams.QYSXZZ;// qysxzz['QYSXZZ'];
				this.hiddenSXZZBtn();
				if (this.mainApp.chisActive) {
					this.getHasFCBPRecord();
				}
				this.YFS = this.loadSystemParams({
							"privates" : ['YFS']
						});// 牙防所代码
				var dpy = document.getElementById("DPY");
				dpy.onclick = function() {
					if (dpy.checked) {
						Ext.getCmp('BQGZ').setValue('建议本人就诊已告知');
					}
				}
			},
			getHasFCBPRecord : function() {
				this.hasFCBPRecord = false;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "chis.chisRecordFilter",
							serviceAction : "getHasFCBPRecord",
							empiId : this.exContext.ids["empiId"]
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				} else {
					this.hasFCBPRecord = r.json.hasFCBPRecord
				}
			},
			refreshWhenTabChange : function() {
				if (this.mainApp.chisActive) {
					this.getHasFCBPRecord();
				}
			},
			// 根据参数值，判断是否隐藏双向转诊按钮
			hiddenSXZZBtn : function() {
				if (this.CSZ == 0) {
					var lnks = this.panel.body.query("li.topBtn")
					if (lnks) {
						for (var i = 0; i < lnks.length; i++) {
							var lnk = Ext.get(lnks[i])
							if (lnk.id == "RPY" || lnk.id == "EPY"
									|| lnk.id == "HPY") {
								lnk.hide();
							}
						}
					}
				}
			},
			beforeClose : function() {
				var flag = true;
				// 关闭EMRView时判断是否有需要保存的数据
				if (this.isModify) {
					if (confirm('病历页面数据已经修改，是否保存?')) {
						if (!this.doSave()) {
							flag = false;
						} else {
							this.isModify = false;
							flag = true;
						}
					} else {
						this.isModify = false;
					}
				}
				// 因修改选择不保存已修改病历数据却保存数据的bug，而将if中的代码从该方法首句移至现行
				if (!this.closeing) {
					this.clinicFinish(9);
				}

				return flag;
			},
			attachLnkEnvents : function() {
				var lnks = this.panel.body.query("li.topBtn")
				if (lnks) {
					for (var i = 0; i < lnks.length; i++) {
						var lnk = Ext.get(lnks[i])
						lnk.on("click", this.onTopLnkClick, this)
					}
				}
				var btns = this.panel.body.query("img,span");
				if (btns) {
					for (var i = 0; i < btns.length; i++) {
						var btn = Ext.get(btns[i])
						btn.on("click", this.onBtnLnkClick, this)
					}
				}
				var textareas = this.panel.body.query("textarea");
				if (textareas) {
					for (var i = 0; i < textareas.length; i++) {
						var textarea = Ext.get(textareas[i])
						textarea.on("change", this.onInputFieldCheck, this)
					}
				}
				var inputs = this.panel.body.query("input");
				if (inputs) {
					for (var i = 0; i < inputs.length; i++) {
						var input = Ext.get(inputs[i])
						if (inputs[i].name == 'healthEdu') {
							continue;
						}
						input.on("change", this.onInputFieldCheck, this)
						input.on(Ext.EventObject.ENTER, this.inputFieldSpeKey,
								this)
					}
				}
				var selects = this.panel.body.query("select");
				if(selects){
					for (var i = 0; i < selects.length; i++) {
						var select = Ext.get(selects[i])
						select.on("change", this.onSelectFieldCheck, this)
					}
				}
			},
			inputFieldSpeKey : function(f, e) {
				// alert(1)
				if (e.getKey() == e.ENTER) {
					// alert(f.tabIndex);
				}
			},
			// by cqd 转诊预约 2015.3.2
			yuyue : function() {
				//浦口预约跳转到12320接口
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "queryyuyuexx",
							body : {
								"empiid" : this.exContext.ids.empiId
							}
						});
				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg);
					return false;
				}
				var sex=0;
					if(res.json.SEXCODE=="2"){
						sex=1;
					}
					var jgid=this.mainApp.deptId;
					var url="http://";
					if(jgid.indexOf("320111")>=0){
						url+="32.26.3.75"
					}else if(jgid.indexOf("320124")>=0) {
						url+="11.40.33.64"
					}
					url+=":8081/nj12320area/login/toLogin?patientIdnum="+
					res.json.IDCARD+"&idCard="+res.json.CARDNUM+"&name="+res.json.PERSONNAME+"&gender="+sex+
					"&homephone="+res.json.MOBILENUMBER+"&birthday="+res.json.BIRTHDAY
//					alert(url)
					window.open (url,'new','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no, status=no')
				// var url = 'phis.application.cic.CIC/CIC/CIC0099';
				// this.module = this.createModule("ClinicPeopleYuyue", url, {
				// isCombined : false
				// });
				// this.module.psname = this.exContext.empiData.personName;
				// this.module.idcard = this.exContext.empiData.idCard;
				// this.module.empiId = this.exContext.ids["empiId"];
				// this.module.age = this.exContext.empiData.age;
				// this.module.phoneNumber =
				// this.exContext.empiData.phoneNumber;
				// this.module.address = this.exContext.empiData.address;
				// this.module.sex = this.exContext.empiData.sexCode_text;
				// this.module.cardNo = this.exContext.empiData.cardNo;
				// var p = this.module.initPanel();
				// var w = this.module.getWin().show();
				// w.add(p);
				// w.show();
			},
			plus3 : function(moduleId, parms) {
				if(moduleId == 'GP03'){//转诊
					debugger;
//					var brxz=this.exContext.empiData.BRXZ;
//					var costType=""
//					if(brxz=="2000"||brxz=="3000"||brxz=="5000"){
//						costType="1"
//					}else if(brxz=="6000"){
//						costType="3"
//					}else{
//						costType="7"
//					}
					var empiData=this.exContext.empiData;
					var old=this.mainApp.serverDate.substring(0,4)-empiData.birthday.substring(0,4);
					var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "getjsdqyjcxx",
							body : {
								"empiid" :empiData.empiId,
								"card":"1"
							}
						});
					if(!res && !res.json && !res.json.signer_city){
						MyMessageTip.msg("提示", "该病人未建立档案，不能签约！", false);
						return;
					}
					var zzxx={};
					zzxx.referral_iden=empiData.idCard?empiData.idCard:"";
					zzxx.referral_name=empiData.personName;
					zzxx.referral_sex=empiData.sexCode==1?"1":"0";
					zzxx.referral_birthday=empiData.birthday;
					zzxx.referral_age=old;
					zzxx.referral_phone=empiData.mobileNumber?empiData.mobileNumber:"";
					zzxx.referral_contact=empiData.contact?empiData.contact:"";
					zzxx.referral_cont_phone=empiData.contactPhone?empiData.contactPhone:"";
					zzxx.referral_minum=res.json.referral_minum?res.json.referral_minum:"";
					zzxx.referral_inpatnum=this.exContext.ids.clinicId;
					zzxx.referral_address=empiData.address?empiData.address:"";
					zzxx.referral_city=res.json.signer_city?res.json.signer_city:"";
					zzxx.referral_district=res.json.signer_district?res.json.signer_district:"";
					zzxx.referral_street=res.json.signer_street?res.json.signer_street:"";
					zzxx.referral_community=res.json.signer_community?res.json.signer_community:"";
					zzxx.referral_unit=res.json.signer_unit?res.json.signer_unit:"";
					zzxx.referral_diagnosis=this.ZZDMC?this.ZZDMC:"";
					zzxx.referral_diagnosis_id=this.ZZDICD10?this.ZZDICD10:"";
					zzxx.referral_illness=document.getElementById("ZSXX").value?document.getElementById("ZSXX").value:"";
					zzxx.referral_medical_history=document.getElementById("JWS").value?document.getElementById("JWS").value:"";
					zzxx.doct_iden=res.json.doct_iden;
					
//					var str="hospitalCode="+this.mainApp.deptId+"&doctorCode="+this.mainApp.uid+
//					"&doctorPwd=1111&patientId="+empiData.empiId+
//					"&idCardNo="+empiData.idCard+
//					"&patientName="+empiData.personName+
//					"&phoneNumber="+empiData.mobileNumber+
//					"&patientSource=门诊&inHospitalNo=&cardNo="+empiData.MZHM+
//					"&gender="+empiData.sexCode+"&age="+old+
//					"&birthday="+empiData.birthday+"&address="+empiData.address+
//					"&costType="+costType+"&InDiagnosisCode="+this.ZZDICD10+"&inDiagnosisName="+this.ZZDMC+
//					"&description="+document.getElementById("ZSXX").value+"&outSummary=&contactPerson=" +empiData.contact+
//					"&contactPhone="+empiData.contactPhone+"&illnessHistory="+document.getElementById("JWS").value;
					var jgid=this.mainApp.deptId;
					var url="http://";
					if(jgid.indexOf("320111")>=0){
						url+="32.26.3.95"
					}else if(jgid.indexOf("320124")>=0) {
						url+="12.43.53.4"
					}
					url+=":8081/FamilyDoctor2.0/sysAdmin/referralSkip.action?jsonData="+JSON.stringify(zzxx)
					window.open (url,'new','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no, status=no')
				}
				else if (moduleId == 'GP02'){//签约
					var empiData=this.exContext.empiData;
					var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "getjsdqyjcxx",
							body : {
								"empiid" :empiData.empiId
							}
						});
					if(!res && !res.json && !res.json.signer_city){
						MyMessageTip.msg("提示", "该病人未建立档案，不能签约！", false);
						return;
					}
//					var brxz=this.exContext.empiData.BRXZ;
//					var costType=""
//					if(brxz=="2000"||brxz=="3000"||brxz=="5000"){
//						costType="02"
//					}else if(brxz=="6000"){
//						costType="3"
//					}else{
//						costType="7"
//					}
					var old=this.mainApp.serverDate.substring(0,4)-empiData.birthday.substring(0,4);
//					var str="Users="+this.mainApp.uid+"&Pwd=1111&Id="+empiData.idCard+
//					"&Name="+empiData.personName+"&Sex="+empiData.sexCode+
//					"&Telephone="+empiData.mobileNumber+"&Age="+old+"&Addressstr="+empiData.address+
//					"&payment="+costType
					var qyxx={};
					qyxx.signer_iden=empiData.idCard;
					qyxx.signer_name=empiData.personName;
					qyxx.signer_sex=empiData.sexCode==1?"1":"0";
					qyxx.signer_birthday=empiData.birthday;
					qyxx.signer_age=old;
					qyxx.signer_phone=empiData.mobileNumber;
					qyxx.emergency_contact=empiData.contact;
					qyxx.emergency_cont_phone=empiData.contactPhone;
					qyxx.signer_workunit=empiData.workPlace;
					qyxx.signer_work_address="";
					qyxx.signer_address=empiData.address;
					qyxx.signer_city=res.json.signer_city;
					qyxx.signer_district=res.json.signer_district;
					qyxx.signer_street=res.json.signer_street;
					qyxx.signer_community=res.json.signer_community;
					qyxx.signer_unit=res.json.signer_unit;
					qyxx.signer_pay=empiData.insuranceCode;
					qyxx.signer_pay_type=empiData.insuranceText;
					qyxx.signer_category=res.json.signer_category;
					qyxx.signer_category_type=res.json.signer_category_type;
					qyxx.doct_iden=res.json.doct_iden;
					var jgid=this.mainApp.deptId;
					var url="http://";
					if(jgid.indexOf("320111")>=0){
						url+="32.26.3.95"
					}else if(jgid.indexOf("320124")>=0) {
						url+="12.43.53.4"
					}
					url+=":8081/FamilyDoctor2.0/sysAdmin/signSkip.action?jsonData="+JSON.stringify(qyxx)
					window.open (url,'new','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no, status=no')
				}else{
					alert("大区开发中……")
					return;
				}
			},

			zhuanzhen : function() {
				// var zzdlr = this.midiModules['ZhuanzhendanLuru'];
				// if (!zzdlr) {
				// $import('phis.application.cic.script.ZhuanzhendanLuru');
				// zzdlr = new phis.application.cic.script.ZhuanzhendanLuru(
				// {
				// name : '病人转诊单录入',
				// jzxh : this.exContext.ids.clinicId,
				// width : 900,
				// height : 450,
				// mainApp:this.mainApp
				// });
				// this.midiModules['ZhuanzhendanLuru'] = zzdlr;
				// }
				// zzdlr.initPanel();
			},
			onTopLnkClick : function(e) {
				var lnk = e.getTarget();
				var cmd = lnk.id;
				switch (cmd) {
					case "IP" :
						// 导入病历
						this.openRefClinicHistroyWin();
						break;
					case "GP" :
						// 导入诊疗
						this.openRefClinicTherapeuticWin();
						break
					case "CL" :
						// 清空
						this.clearMedicalInfo();
						break
					case "SV" :
						this.doSave();
						break;
					case "TS" :
						this.doClinicFinish(2);
						break;
					case "ST" :
						this.openReferralAppointment();
						break;
					case "FY" :
						this.openReferralAppointment();
						break;
					case "RPY" :
						this.doRPY();
						break;
					case "EPY" :
						this.doEPY();
						break;
					case "HPY" :
						this.doHPY();
						break;
					case "PRINT" :
						this.doPrint();
						break;
					case "zhuanzhen" :
						this.zhuanzhen();
						break;
					case "yuyue" :
						this.yuyue();
						break;
					case "signIn" :
						this.plus3('GP02', {
									print : false
								});
						break;
					case "fncfk" :
						this.plus3('GP07', {
									print : false
								});
						break;
					case "transfer" :
						this.plus3('GP03', {
									print : false
								});
						break;
					case "continue" :
						this.plus3('GP04', {
									print : true
								});
						break;
					case "JYJG" :
						this.jyjg();
						break;
					case "BLFZ" :// 病历复制
						this.blfz();
						break;
					/**
					 * modified by gaof
					 */
					// case "newDiagnosis" :// 诊断
					// this.openCardLayoutWin(1);
					// break;
					// case "newPrescription" :// 处方
					// this.openCardLayoutWin(2);
					// break
					// case "newDisposal" :// 处置
					// this.openCardLayoutWin(3);
					// break
					case "EHR" :
						this.linkEHR();
						break;
				}
			},
			// update by caijy for 古美需求,打印前提示是否要保存
			doPrint : function() {
				Ext.Msg.show({
							title : "提示",
							msg : "打印前是否保存记录?",
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									if (this.doSave()) {
										this.emrview.doPrint()
									};
								} else {
									this.emrview.doPrint()
								}
							},
							scope : this
						});
			},
			jyjg : function() {
				var objId = "showWinID";
				var clsid = "464DF263-A34C-407F-A17E-6234614C7EA8";
				var paname = "C:/MyWork/file/lisreport/fwtprint.exe 1|5|"
						+ this.exContext.empiData.cardNo + "|";
				// var paname = "C:/MyWork/file/lisreport/fwtprint.exe
				// 1|7|20150411WFL002|";
				var obj = this.getObject(objId, clsid);
				if (obj) {
					obj.showWin(paname, 1);
				} else {
					this.createObj(objId, clsid);
					this.Obj.showWin(paname, 1);
				}
			},
			blfz : function() {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "blfzQuery",
							brid : this.exContext.ids.brid,
							jzxh : this.exContext.ids.clinicId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				}
				var bcjl = r.json.bcjl;
				if (bcjl == null) {
					MyMessageTip.msg("提示", "没有可以复制的病历", false);
					return;
				}
				this.setMedicalInfo(bcjl, 1);
			},

			// 链接到RHR
			linkEHR : function() {
				var idCard="";
				if(this.exContext && this.exContext.empiData)
				idCard=this.exContext.empiData.idCard
				if(idCard.length <18){
					MyMessageTip.msg("提示", "身份证号不正确！", true);
					return;
				}
				var url="http://";
				var jgid=this.mainApp.deptId;
				if(jgid.indexOf("320111")>=0){
					url+="32.33.1.75:9999"
				}else if(jgid.indexOf("320124")>=0) {
					url+="10.2.202.13:8083"
				}
				url+="/ehrview/EhrLogonService?user=system&pwd=123&idcard="+idCard
				window.open (url,'new','toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no, status=no')
				
				// var empiId =this.exContext.ids.empiId;
//				util.rmi.jsonRequest({
//							serviceId : "phis.clinicManageService",
//							serviceAction : "getBrxx",
//							body : {
//								brid : brid
//							}
//						}, function(code, msg, json) {
//							if (code == 200) {
//								if (!json.body || !json.body.PHRID) {
//									MyMessageTip.msg('提示', '该病人无健康档案', true);
//									return;
//								}
//								var phrid = json.body.PHRID;
//								alert("调用平台EHRView……");
//								// this.plus3('GP06', {
//								// phrid : phrid
//								// });
//							} else {
//								this.processReturnMsg(code, msg);
//								return false;
//							}
//						}, this);
			},
			openKnowledgeBase : function() {//【中医馆】知识库
				var module = this.createModule("knowledgeBase",
						this.refKnowledgeBase);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, module);
				var win = module.getWin();
				win.setHeight(document.body.clientHeight);
				win.setWidth(document.body.clientWidth);
				win.add(module.initPanel());
				win.show();
				win.center();
			},
			openClinicMedicalRecordTCM : function() {//【中医馆】电子病历
				var module = this.createModule("clinicMedicalRecordTcm",
						this.refClinicMedicalRecordTcm);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, module);
				var win = module.getWin();
				win.setHeight(document.body.clientHeight);
				win.setWidth(document.body.clientWidth);
				win.add(module.initPanel());
				win.show();
				win.center();
			},
			openSyndromeDifferentiationAndRreatment : function() {//【中医馆】辨证论治
				var module = this.createModule("syndromeDifferentiationAndRreatment",
						this.refSyndromeDifferentiationAndRreatment);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, module);
				var win = module.getWin();
				win.setHeight(document.body.clientHeight);
				win.setWidth(document.body.clientWidth);
				win.add(module.initPanel());
				win.show();
				win.center();
			},
			openPreventiveTreatmentOfDiseases : function() {//【中医馆】治未病
				var module = this.createModule("preventiveTreatmentOfDiseases",
						this.refPreventiveTreatmentOfDiseases);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, module);
				var win = module.getWin();
				win.setHeight(document.body.clientHeight);
				win.setWidth(document.body.clientWidth);
				win.add(module.initPanel());
				win.show();
				win.center();
			},
			openDistanceLearning : function() {//【中医馆】远程教育
				var module = this.createModule("distanceLearning",
						this.refDistanceLearning);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, module);
				var win = module.getWin();
				win.setHeight(document.body.clientHeight);
				win.setWidth(document.body.clientWidth);
				win.add(module.initPanel());
				win.show();
				win.center();
			},
			openTelemedicine : function() {//【中医馆】远程会诊
				var module = this.createModule("telemedicine",
						this.refTelemedicine);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, module);
				var win = module.getWin();
				win.setHeight(document.body.clientHeight);
				win.setWidth(document.body.clientWidth);
				win.add(module.initPanel());
				win.show();
				win.center();
			},
			createObj : function(objId, clsid) {
				var html;
				if (Ext.isIE) {
					html = "<OBJECT classid='clsid:" + clsid
							+ "' width='0' height='0' id='" + objId
							+ "' name='" + objId + "'></OBJECT>";
				} else {
					html = '<object id="'
							+ objId
							+ '" TYPE="application/xhanhan-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{'
							+ clsid + '}" ></object>';
				}
				if (this.Obj) {
					return;
				}
				var ele = document.createElement("div");
				ele.setAttribute("width", "0px");
				ele.setAttribute("height", "0px");
				ele.innerHTML = html;
				document.body.appendChild(ele);
				this.Obj = this.getObject(objId, clsid);
			},
			getObject : function(objId, clsid) {
				if (window.document[objId]) {
					return window.document[objId];
				}
				if (Ext.isIE) {
					if (document.embeds && document.embeds[objId])
						return document.embeds[objId];
				} else {
					return document.getElementById(objId);
				}
			},
			onBtnLnkClick : function(e) {
				debugger;
				var lnk = e.getTarget();
				var cmd = lnk.id;
				/***************add by hujian 增加处方流转判别***************/
				var cflz = lnk.innerText;
				if(cflz=="处方流转"){
					cmd = "cflz";
				}
				debugger;
				/***************add by lizhi 开处方、处置之前先判断是否有诊断***************/
//				if(cmd == "newPrescription" || cmd == "newDisposal"){
//					var r = phis.script.rmi.miniJsonRequestSync({
//							serviceId : "clinicManageService",
//							serviceAction : "checkHasDiagnose",
//							body : {
//								"BRID" : this.exContext.empiData.BRID
//							}
//						});
//					if (r.code > 300) {
//						this.processReturnMsg(r.code, r.msg);
//						return false;
//					}
//					if(!r.json.HasDiagnose){//未录入诊断
//						MyMessageTip.msg("提示", "请先录入诊断！", true);
//						this.openCardLayoutWin(1);
//						return;
//					}
//				}
				/***************add by lizhi 开处方、处置之前先判断是否有诊断***************/
				switch (cmd) {
					case "newDiagnosis" :// 诊断
						this.openCardLayoutWin(1,0);
						break;
					case "newPrescription" :// 处方
						this.openCardLayoutWin(2,0);
						break
					case "newDisposal" :// 处置
						this.openCardLayoutWin(3,0);
						break
//					case "importHER" :// 引入健康处方
//						this.openCardLayoutWin(4);
//						break
					case "TGJC_TC" :// 体格检查
						this.openKJLLWin("体格检查", "TGJC");
						break
					case "XBS_TC" :// 现病史
						this.openKJLLWin("简要病史", "XBS");
						break
					case "JWS_TC" :// 既往史
						this.openKJLLWin("简要病史", "JWS");
						break
					case "FZJC_TC" :// 辅助检查
						this.openFZJCWin();
						break
					case "BQGZ_TC" :// 辅助检查
						this.openKJLLWin("医生医嘱", "BQGZ");
						break
					case "cflz" :// 处方流转
						debugger;
						this.openCardLayoutWin(2,"cflz");
						break

					// default :
					// this.checkInputValidity(cmd, lnk);
				}
			},

			openFZJCWin : function() {
				var module = this
						.createModule("FZJCModule", this.refFZJCModule);
				module.on("appoint", this.onAppointData, this);
				module.brid = this.exContext.empiData.BRID;
				var win = module.getWin();
				win.add(module.initPanel())
				win.show();
			},
			onAppointData : function(value, type) {
				Ext.getCmp('FZJC').setValue(value);
			},
			openKJLLWin : function(mc, id) {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "queryBlsxxd",
							body : {
								"XDLB" : mc
							}
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				}
				var xdbh = parseInt(r.json.XDBH)
				var module = this.createModule("KJLLModule",
						"phis.application.cic.CIC/CIC/CIC0107");
				module.on("comfire", this.onKJLLWinComfire, this)
				module.xdbh = xdbh;
				module.docid = id;
				var win = module.getWin();
				win.add(module.initPanel())
				win.show();
				module.form.setValue("");
				module.tree.getRootNode().reload({})
			},
			onKJLLWinComfire : function(id, value) {
				Ext.getCmp(id).setValue(Ext.getCmp(id).getValue() + value);
			},
			onInputFieldCheck : function(e) {
				var lnk = e.getTarget();
				var cmd = lnk.id
				this.checkInputValidity(cmd, lnk);
			},
			onSelectFieldCheck : function(e) {
				var lnk = e.getTarget();
				var cmd = lnk.id
				//中医馆服务
				if(cmd=="tcm"){
					switch(parseInt(lnk.value)){
						case 1://知识库
							this.openKnowledgeBase();
							this.tcmstate = 1;
						break;
						case 2://电子病历
							this.openClinicMedicalRecordTCM();
							this.tcmstate = 1;
						break;
						case 3://辨证论治
							this.openSyndromeDifferentiationAndRreatment();
							this.tcmstate = 1;
						break;
						case 4://治未病
							this.openPreventiveTreatmentOfDiseases();
							this.tcmstate = 1;
						break;
						case 5://远程教育
							this.openDistanceLearning();
							this.tcmstate = 1;
						break;
						case 6://远程会诊
							this.openTelemedicine();
							this.tcmstate = 1;
						break;
						default:
					}
				}
			},
			checkInputValidity : function(cmd, target) {
				this.isModify = true;
				if (!target) {
					return;
				}
				target.value = target.value.trim();
				if (target.type == "textarea") {
					if (target.value.length > 500) {
						MyMessageTip.msg("提示", target.name + "超过最大长度!", true);
						target.value = target.value.substring(0, 500);
						return;
					}
				} else if (target.type == "text") {
					if (target.value == "")
						return;
					if (isNaN(target.value)) {
						MyMessageTip.msg("提示", "该项只能输入数字", true);
						target.value = "";
						return;
					} else {
						if (target.id == "T") {
							target.value = parseFloat(target.value).toFixed(1);
							if (target.value < 0) {
								target.value = 0;
							} else if (target.value > 99.9) {
								target.value = 99.9;
							}
						} else if (target.id == "LEFTVISION"
								|| target.id == "RIGHTVISION"
								|| target.id == "LEFTCORRECTEDVISION"
								|| target.id == "RIGHTCORRECTEDVISION") {
							target.value = parseInt(parseFloat(target.value)
									* 100)
									/ 100;
							if (target.value < 0) {
								target.value = 0;
							} else if (target.value > 99.99) {
								target.value = 99.99;
							}

						} else {
							target.value = parseInt(target.value);
							if (target.value < 0) {
								target.value = 0;
							} else if (target.value > 999) {
								target.value = 999;
							}
							if (target.id == "SSY" || target.id == "SZY") {
								var SSY = document.getElementById("SSY").value
										.trim();
								var SZY = document.getElementById("SZY").value
										.trim();
								if (SSY != "" && SZY != "") {
									if (parseInt(SSY) < parseInt(SZY)) {
										MyMessageTip.msg("提示", "收缩压必须大于舒张压!",
												true);
										target.value = "";
									}
								}
							}
						}
					}

				}
			},
//			createJKJYHTML : function(healthTeach, recordId) {
//				var html = '<table cellspacing="0" id="the-table">';
//				html += '<tr style="height:40px;border-bottom:1px solid #ccd3dc;"><td>'
//						+ '<a ondblclick="onJKJYDblClick('
//						+ recordId
//						+ ');"><pre id="JKCF'
//						+ recordId
//						+ '" style="white-space:pre-wrap;word-wrap:break-word;">'
//						+ healthTeach + '</pre></a></td></tr>'
//				html += '</table>';
//				return html;
//			},
			onImportRecipe : function(records) {
				this.isModify = true;
				// document.getElementById("div_JKJY").innerHTML = jkjyHtml;
//				var content = Ext.getCmp("JKJY").getValue();
//				content = content + this.createAllJKJYHTML(records);
//				Ext.getCmp("JKJY").setValue(content);
//				Ext.getCmp("JKJY").focus(100);
			},
			openCardLayoutWin : function(num,data) {
				debugger;
				if (this.isModify && num != 4) {
					this.doSave(true, true);
				}
				if (this.diagnosisChange) {
					this.doSaveDiagnosis();
				}
				if (num == 4) {
					var module = clinicMedicalRecord_ctx.createModule(
							"refRecipeImportModule",
							clinicMedicalRecord_ctx.refRecipeImportModule);
					module.exContext = clinicMedicalRecord_ctx.exContext;
					module.on("importRecipe",
							clinicMedicalRecord_ctx.onImportRecipe,
							clinicMedicalRecord_ctx);
					module.fromId = "record";
					this.recipeImportModule = module;
					// module.ZZDICD10 = this.ZZDICD10;//屏蔽健康教育ICD10 进去看到全部处方 by
					// cqd 20150813
					module.ZZDMC = this.ZZDMC;
					module.JKCFRecords = this.JKCFRecords;
					var win = module.getWin();
					win.setHeight(580);
					win.setWidth(1000);
					win.show();
					return;
				}
				/**
				 * modified by gaof 2013-9-23 修改需求：根据系统参数QYCFCZQZTJ
				 * 未录入诊断，不允许录入处方处置
				 */
				if (num == 2 ) {
					debugger;
					// if(num == 3){//徐亮要求 辅检的时候保存下
					// this.doSave();
					// }
					// modify by yangl 先判断参数，减少交互次数
					if (this.exContext.systemParams.QYCFCZQZTJ == '1') {
						var body = {};
						body["JZXH"] = this.exContext.ids.clinicId;
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicManageService",
									serviceAction : "queryIsAllowed",
									body : body
								});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg);
							return false;
						} else {
							if (r.json.isAllowed == 0) {
								Ext.MessageBox.alert("提示", "请先录入诊断");
								return;
							}
						}
					}
				}

				var firstOpen = false;
				var goText, backText, title,cflzbz;
				if (num == 1) {
					goText = "处方录入";
					backText = "处置录入";
					title = "诊断录入";
					this.exContext.cflzbz = "nocflzbz";
				} else if (num == 2 && data != "cflz") {
					debugger;
					goText = "处置录入";
					backText = "诊断录入";
					title = "处方录入";
					this.exContext.cflzbz = "nocflzbz";
				} else if (num == 3 && data != "cflz") {
					goText = "处方录入";
					backText = "诊断录入";
					title = "处置录入";
					this.exContext.cflzbz = "nocflzbz";
				}else if (num == 2 && data == "cflz") {
					debugger;
					goText = "处置录入";
					backText = "诊断录入";
					title = "处方流转处方录入";
					cflzbz = "cflzbz";//处方流转标志
					this.exContext.cflzbz = "cflzbz";
				}
				title = title + '-' + '【' + this.mainApp.departmentName + '】';
				this.panel.el.mask("载入中...");
				if (!this.cardWin) {
					var lbtn = new Ext.form.Label({
								html : "<div class='selfBtn'><a><span id='move-prev'>"
										+ backText + "</span></a></div>"
							});
					var rbtn = new Ext.form.Label({
								html : "<div class='selfBtn'><a><span id='move-next'>"
										+ goText + "</span></a></div>"
							});
					var cflz = new Ext.form.Label({
						html : "<div class='selfBtn'><a><span id='move-next'>"
								+ cflzbz + "</span></a></div>"
					});
					var brxxLabel = new Ext.form.Label({
						html : ('<div style="font-weight:bold; font-size:12pt; color:#5867FF;">'
								+ this.exContext.empiData.personName
								+ "&nbsp&nbsp"
								+ this.exContext.empiData.sexCode_text
								+ "&nbsp&nbsp"
								+ this.exContext.empiData.birthday
								+ "("
								+ this.exContext.empiData.age + ")"
								+ "&nbsp&nbsp"
								+ this.exContext.empiData.BRXZ_text + '</div>')
					});
					debugger;
					this.cardWin = new Ext.Window({
								title : title,
								width : 1024,
								layout : 'card',
								activeItem : num - 1,
								iconCls : 'icon-grid',
								shim : true,
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : "hide",
								constrainHeader : true,
								minimizable : false,
								maximizable : true,
								shadow : false,
								modal : true,
								defaults : {
									border : false
								},
								tbar : [brxxLabel, '->', lbtn, '-', rbtn],
								// bbar : [lbtn, '->', rbtn],
								items : [this.openDiagnosisWin(),
										this.openPrescriptionWin(),
										this.openDisposalWin()],
								scope : this
							})
					debugger;
					this.cardWin.on("show", function() {
						if (!this.inited) {
							var lnks = Ext.query("div.selfBtn");
							if (lnks) {
								for (var i = 0; i < lnks.length; i++) {
									var lnk = Ext.get(lnks[i])
									lnk.on("click", function(e) {
												var lnk = e.getTarget();
												if (lnk.id == 'move-next') {
													this.goTab();
												} else if (lnk.id == 'move-prev') {
													this.backTab();
												}

											}, this)
								}
							}
							this.inited = true;
						}
						this.fireEvent("winShow");
					}, this);
					this.cardWin.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					this.cardWin.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					this.cardWin.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					this.cardWin.on("beforehide", function() {
								return this.beforeCardWinClose();
							}, this);
					this.cardWin.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					firstOpen = true;
					this.midiWins['cardWin'] = this.cardWin;
				} else {
					debugger;
					this.cardWin.setTitle(title);
					this.cardWin.getLayout().setActiveItem(num - 1);
					var prev = document.getElementById('move-prev');
					var next = document.getElementById('move-next');

					prev.innerHTML = (backText);
					next.innerHTML = (goText);
				}
				debugger;
				this.cardWin.show();
				debugger;
				if (firstOpen) {
				debugger;
					// this.cardWin.setWidth(1024);
					// this.cardWin.setHeight(600);
					// this.cardWin.center();
					this.cardWin.maximize();
				}
				debugger;
				id = this.cardWin.getLayout().activeItem.id;
				this.midiModules[id].onWinShow();
				this.cardWin.instance = this.midiModules[id];
				this.panel.el.unmask();
				this.lock(id, this.midiModules[id]);
			},
			lock : function(id, m) {
				debugger;
				if (id == 'clinicPrescriptionEntry') {
					var p = {};
					p.YWXH = '1001';
					p.BRID = this.exContext.ids.brid;
					m.bclLock(p, m.complexPanel.el)
				} else if (id == 'clinicopenDisposalEntry') {
					var p = {};
					p.YWXH = '1002';
					p.BRID = this.exContext.ids.brid;
					m.bclLock(p, m.panel.el)
				}
			},
			unlock : function(id, m) {
				if (id == 'clinicPrescriptionEntry') {
					var p = {};
					p.YWXH = '1001';
					p.BRID = this.exContext.ids.brid;
					m.bclUnlock(p, m.complexPanel.el);
				} else if (id == 'clinicopenDisposalEntry') {
					var p = {};
					p.YWXH = '1002';
					p.BRID = this.exContext.ids.brid;
					m.bclUnlock(p, m.panel.el);
				}
			},
			beforeCardWinClose : function() {
				var id = this.cardWin.getLayout().activeItem.id;
				// 关闭时取消业务锁
				if (this.midiModules[id].beforeclose()) {
					this.unlock(id, this.midiModules[id]);
					return true;
				} else {
					return false;
				}
				// return this.midiModules[id].beforeclose();
			},
			goTab : function() {
				/**
				 * modified by gaof 2013-9-23 修改需求：根据系统参数QYCFCZQZTJ
				 * 未录入诊断，不允许录入处方处置
				 */
				var id = this.cardWin.getLayout().activeItem.id;
				// alert(id + ":" + this.currModuleId)
				// if (id == this.currModuleId)// 如果是当前，则不切换
				// return;
				if (id == "clinicDiagnosisEntry"
						|| id == "clinicopenDisposalEntry") {
					var body = {};
					body["JZXH"] = this.exContext.ids.clinicId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "queryIsAllowed",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return false;
					} else {
						if (r.json.isAllowed == 0) {
							var store = this.midiModules["clinicDiagnosisEntry"].list.grid
									.getStore();
							if (store.getCount() > 0) {
								var r = store.getAt(0);
								if (r.get("ICD10") == null
										|| r.get("ICD10") == ""
										|| r.get("ICD10") == 0) {
									Ext.MessageBox.alert("提示", "请先录入诊断");
									return;
								} else {
									// Ext.MessageBox.alert("提示", "请先保存诊断");
									// return;
								}
							} else {
								Ext.MessageBox.alert("提示", "请先录入诊断");
								return;
							}
						}
					}
				}

				if (!this.midiModules[id].beforeclose()) {
					return;
				}

				var prev = document.getElementById('move-prev');
				var next = document.getElementById('move-next');
				if (id == 'clinicDiagnosisEntry') {
					prev.innerHTML = "诊断录入";
					next.innerHTML = "处置录入";
					this.cardWin.setTitle("处方录入" + '-' + '【'
							+ this.mainApp.departmentName + '】');
					this.cardWin.getLayout().setActiveItem(1);
				}
				if (id == 'clinicPrescriptionEntry') {
					prev.innerHTML = ("诊断录入");
					next.innerHTML = ("处方录入");
					this.cardWin.setTitle("处置录入" + '-' + '【'
							+ this.mainApp.departmentName + '】');
					this.cardWin.getLayout().setActiveItem(2);
				}
				if (id == 'clinicopenDisposalEntry') {
					prev.innerHTML = ("诊断录入");
					next.innerHTML = ("处置录入");
					this.cardWin.setTitle("处方录入" + '-' + '【'
							+ this.mainApp.departmentName + '】');
					this.cardWin.getLayout().setActiveItem(1);
				}
				this.unlock(id, this.midiModules[id]);
				id = this.cardWin.getLayout().activeItem.id;
				this.currModuleId = id;
				this.midiModules[id].onWinShow();
				//2019-09-10-解决诊断录入切换到处方录入后默认光标没有锁定到药品名称单元格问题
			    if(this.midiModules[id].tab.getItem(0).title.indexOf("新处方")>=0){
			    	debugger;
			        this.midiModules[id].doNew();
			    }
				this.cardWin.instance = this.midiModules[id];
				// BCL lock
				this.lock(id, this.midiModules[id]);
			},
			backTab : function() {
				/**
				 * modified by gaof 2013-9-23 修改需求：根据系统参数QYCFCZQZTJ
				 * 未录入诊断，不允许录入处方处置
				 */
				var id = this.cardWin.getLayout().activeItem.id;

				if (!this.midiModules[id].beforeclose()) {
					return;
				}
				var prev = document.getElementById('move-prev');
				var next = document.getElementById('move-next');
				if (id == 'clinicDiagnosisEntry') {
					prev.innerHTML = ("诊断录入");
					next.innerHTML = ("处方录入");
					this.cardWin.setTitle("处置录入");
					this.cardWin.getLayout().setActiveItem(2);
				}
				if (id == 'clinicPrescriptionEntry') {
					prev.innerHTML = ("处置录入");
					next.innerHTML = ("处方录入");
					this.cardWin.setTitle("诊断录入");
					this.cardWin.getLayout().setActiveItem(0);
				}
				if (id == 'clinicopenDisposalEntry') {
					prev.innerHTML = ("处置录入");
					next.innerHTML = ("处方录入");
					this.cardWin.setTitle("诊断录入");
					this.cardWin.getLayout().setActiveItem(0);
				}
				this.lock(id, this.midiModules[id]);
				id = this.cardWin.getLayout().activeItem.id;
				this.cardWin.instance = this.midiModules[id];
				this.midiModules[id].onWinShow();
			},
			openDiagnosisWin : function() {
				debugger;
				// if (!this.exContext || !this.exContext.ids
				// || !this.exContext.ids["empiId"]
				// || !this.exContext.ids["clinicId"]) {
				// Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
				// return;
				// }
				// this.panel.getEl().mask("业务模块载入中...");
				var module = this.createModule("clinicDiagnosisEntry",
						this.refDiagnosisEntryModule, {
							personInfo : this.pinfo
						});
				module.opener = this;
				module.on("close", this.onWinClose, this);
				module.on("beforeclose", this.onBeforeWinClose, module);
				module.on("doSave", this.onClinicSave, this);
				module.exContext = this.exContext;
				module.on("doRemove", this.onClinicRemove, this);
				module.on("toOpenPagesOfCHIS", this.onOpenPagesOfCHIS, this);
				var p = module.initPanel();
				p.id = 'clinicDiagnosisEntry';
				return p;
				// if (!this.clinicDiagnosisEntry_win) {

				// var win = module.getWin();
				// this.clinicDiagnosisEntry_win = win;
				// }
				// this.clinicDiagnosisEntry_win.show();
				// this.panel.getEl().unmask();
			},
			openRefClinicHistroyWin : function() {
				// 病历信息
				var ClinicHistroy = this.createModule(
						"getRefClinicHistroyList", this.refClinicHistroyList);
				ClinicHistroy.exContext = this.exContext;
				ClinicHistroy.opener = this;
				if (!this.clinicHistroy_win) {
					var win = ClinicHistroy.getWin();
					this.clinicHistroy_win = win;
				}
				this.clinicHistroy_win.setHeight(580)
				this.clinicHistroy_win.setWidth(860)
				this.clinicHistroy_win.show();
				this.clinicHistroy_win.center();

			},
			openRefClinicTherapeuticWin : function() {
				// 诊疗模板
				var Therapeutic = this.createModule("getRefTherapeuticList",
						this.refClinicTherapeuticList);
				Therapeutic.exContext = this.exContext;
				Therapeutic.opener = this;
				if (!this.therapeutic_win) {
					var module = this.createModule("clinicPrescriptionEntry",
							this.refPrescriptionEntryModule);
					module.exContext = this.exContext;
					// 判断初始参数是否正确设置
					if (module.getClinicInitParams() == -1) {
						Ext.Msg.alert("错误", "获取初始参数错误!请联系管理员!");
						return;
					}
					var win = Therapeutic.getWin();
					win.add(Therapeutic.initPanel())
					this.therapeutic_win = win;
				}
				this.therapeutic_win.show();
				this.therapeutic_win.center();

			},
			openReferralAppointment : function() {
				if (this.isModify) {
					this.doSave(true);
				}
				// add by yangl 判断病历是否修改
//				if (this.mainApp.reg_departmentId != this.YFS.YFS) {// 牙防所不提示打印
//					// 判断打印完后才能结束就诊 by cqd 2015.4.23
//					var ret = phis.script.rmi.miniJsonRequestSync({
//								serviceId : "clinicManageService",
//								serviceAction : "selectdybz",
//								body : {
//									jzxh : this.exContext.ids.clinicId
//								}
//							});
//					if (ret.json.hasclinic) { // 判断是否要提示首诊测压
//						// 判断35岁病人必须测量血压
//						// alert(this.exContext.ids.age)
//						var DPYValue = document.getElementById("DPY").checked;
//						if (this.mainApp.reg_departmentId != this.YFS.YFS
//								&& this.exContext.ids.age >= 35
//								&& !this.hasFCBPRecord && !DPYValue) {
////							var ssy = document.getElementById("SSY").value
////									.trim();// 获得收缩压和舒张压
////							var szy = document.getElementById("SZY").value
////									.trim();
////							if (ssy == null || szy == null || ssy.length == 0
////									|| ssy.length == 0) {
////								MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！", true);
////								return;
////							}
//							var me = this;
//							var rootNode = me.emrview.emrNavTree.getRootNode();
//							if (rootNode.findChild("id", "A")) {
//								var hyNode = rootNode.findChild("id", "A")
//										.findChild("id", "A02");
//								if (hyNode.attributes.ref) {
//									var ref = hyNode.attributes.ref;
//									var nodeStatus = null;
//									if(me.emrview.treeNodeStatusMap) {
//										nodeStatus = me.emrview.treeNodeStatusMap[ref];
//									}
//								}
//								if (nodeStatus == 'create') {
//									MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！！",
//											true);
//									return;
//								}
//							}
//						}
//					}
//					if (ret.code > 200) {
//						MyMessageTip.msg("提示", ret.msg, true);
//						return;
//					}
//				}
				
				/*****************add by lizhi 2017-11-22控制不进行首诊测压不允许暂挂或就诊结束。********************/
				if (this.exContext.ids.age >= 35){
					var result = util.rmi.miniJsonRequestSync({
							serviceId : "phis.clinicManageService",
							serviceAction : "queryMsBcjl",
							body : {
								JZXH : this.exContext.idss.clinicId,
								BRID : this.exContext.ids.brid
							}
						});
					if (result.code > 300) {
						alert("查询失败");
						return
					}else{
						var me = this;
						var rootNode = me.emrview.emrNavTree.getRootNode();
						if (rootNode.findChild("id", "A")) {
							var hyNode = rootNode.findChild("id", "A")
									.findChild("id", "A02");
							if (hyNode.attributes.ref) {
								var ref = hyNode.attributes.ref;
								var nodeStatus = null;
								if(me.emrview.treeNodeStatusMap) {
									nodeStatus = me.emrview.treeNodeStatusMap[ref];
								}
							}
							if (nodeStatus == 'create') {
								if(result.json.errorMsg){
									MyMessageTip.msg("提示", result.json.errorMsg, true);
									return;
								}
//								MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！！！", true);
							}
//							else if(nodeStatus == 'read'){
//								if(result.json.MS_BCJL){
//									if(result.json.MS_BCJL.SSY==null || result.json.MS_BCJL.SSY==""
//										||result.json.MS_BCJL.SZY==null || result.json.MS_BCJL.SZY==""){
//										Ext.Msg.alert("提示", "请保存首诊测压！！！");
//										return;
//									}
//								}else{
//									Ext.Msg.alert("提示", "请保存首诊测压！！！");
//									return;
//								}
//							}
						}
					}
				}

				// 弹出就诊信息之前，先保存页面数据
				if (this.isModify) {
					var flg = this.doSave(true);
					if (!flg) {
						return false;
					}
				}
				
				var param=phis.script.common.loadSystemParams({"privates" : ['CRBBGK']}).CRBBGK;
				if(this.needSaveCrb && param == "1"){//add by lizhi at 2018-01-24传染病填写传染病报告卡
					var _this = this;
					Ext.Msg.alert("提示", "传染病诊断未填写传染病报告卡,请先填写！",function(){_this.popBgkWin();});
					return;
				}
				var getReferralAppointment = this.createModule("getReferralAppointment", this.refTheTreatmentForm);
				getReferralAppointment.exContext = this.exContext;
				getReferralAppointment.opener = this;
				getReferralAppointment.YYJGTS = this.exContext.systemParams.YYJGTS;
				if (!this.getReferralAppointment_win) {
					var win = getReferralAppointment.getWin();
					win.add(getReferralAppointment.initPanel())
					this.getReferralAppointment_win = win;
				}
				this.getReferralAppointment_win.show();
				this.getReferralAppointment_win.center();
				this.showCHISMsgWin();
			},
			clone:function( obj ){
				var result={};
				for(var i in obj) {
					result[i]   =   obj[i] 
				} 
				return result 
			},
			showCHISMsgWin : function() {
				if (this.exContext.ids.manaUnitId) {
					if (this.exContext.ids.manaUnitId.substring(0, 9) != '310112051') {// 非古美中心不提示
						return;
					}
				}
				var ts = "";
				var rsMsgList = [];
				if(this.emrview.rsMsgList){
					for (var i = 0; i < this.emrview.rsMsgList.length; i++) {
						rsMsgList.push(this.clone(this.emrview.rsMsgList[i]));
					}
				}
				var resData = phis.script.rmi.miniJsonRequestSync({
					serviceId : "chis.chisRecordFilter",
					serviceAction : "getSCMMsgInfo",//家医签约系统履约提醒
					method : "execute",
					body : {
						empiId : this.exContext.empiData.empiId,
						itemnames : "'基本医疗-测血糖','基本医疗-测血压','基本医疗-健康咨询与指导'"
					}
				});
				if(resData.code < 300){
					var body = resData.json.body;
					if (body) {
						for(var i=0;i<body.rsMsgList.length;i++){
  							rsMsgList.push(body.rsMsgList[i]);
						}
					}
				}				
				var len = rsMsgList.length;
				if (len == 0) {
					return;
				}
				ts = "<table id='chisJDTS_Table'><tbody style='font-size:15px;font-weight:bold;color:#ff0000;'>";
				for (var i = 0; i < len; i++) {
					var nd = rsMsgList[i];
					var ndname = nd.nodeName;
					if(nd.nodeKey == "A91"){
						ndname = "需履约" + nd.nodeName;
					}else{
						ndname = "需创建" + nd.nodeName;
					}
					ts += "<tr><td id='CHIS_"
							+ nd.nodeKey
							+ "' onMouseOver='this.style.cursor= \"pointer\";this.style.borderBottom=\"1px solid blue\";' onMouseOut='this.style.borderBottom=\"none\";'>"
							+ ndname + "</td></tr>";
				}
				ts += "</tbody></table>";
				ymPrompt.confirmInfo({
							message : ts,
							title : "您有待办社区业务",
							winPos : 'rb',
							okTxt : "执行",
							cancelTxt : "退出",
							scope : this,
							hashcode : "CHIS_DBYW",
							showMask : false,
							useSlide : true,
							width : 360,
							height : 200,
							handler : this.CHISJDTSBtnClick,
							autoClose : false
						});
				var me = this;
				for (var i = 0; i < len; i++) {
					var nd = rsMsgList[i];
					var obj = document.getElementById("CHIS_" + nd.nodeKey);
					var handleFun = function(key, obj, me) {
						return function() {
							me.openEMRNode(key, obj, me);
						}
					}
					this.addEvent(obj, "click", handleFun(nd.nodeKey, obj, me));
				}
			},
			addEvent : function(o, c, h) {// HTML对象增加事件控制方法
				if (o.attachEvent) {
					o.attachEvent('on' + c, h);
				} else {
					o.addEventListener(c, h, false);
				}
				return true;
			},
			CHISJDTSBtnClick : function(sign) {
				if (sign == "cancel" || sign == "close") {
					ymPrompt.doHandler('doClose', true);
				} else if (sign == "ok") {
					if (this.getReferralAppointment_win) {
						if (this.getReferralAppointment_win.isVisible()) {
							this.getReferralAppointment_win.hide();
						}
					}
					var rsMsgList = this.emrview.rsMsgList;
					var len = rsMsgList.length;
					for (var i = 0; i < len; i++) {
						var nd = rsMsgList[i];
						var node = this.emrview.emrNavTree
								.getNodeById(nd.nodeKey);
						if(typeof(node) == "undefined"){
							continue;
						}
						this.emrview.onNavTreeClick(node, {});
					}
					ymPrompt.doHandler('doClose', true);
				}
			},
			openEMRNode : function(key, obj, me) {
				if (this.getReferralAppointment_win) {
					if (me.getReferralAppointment_win.isVisible()) {
						me.getReferralAppointment_win.hide();
					}
				}
				var node = me.emrview.emrNavTree.getNodeById(key);
				me.emrview.onNavTreeClick(node, {});
			},
			onClinicSave : function(busType) {
				this.modify = true;
				this.initClinicRecord(busType);
				// this.medicareSmartReminde(busType);
				if (busType == 2) {
					this.emrview.refreshEMRNavTree();
				}
			},
			onClinicRemove : function() {
				this.initClinicRecord("2");
				this.emrview.refreshEMRNavTree();
			},
			onOpenPagesOfCHIS : function(openNodes, empiId) {
				if (this.cardWin) {
					this.cardWin.hide();
				}
				if (!this.emrview && !this.emrview.win
						&& !this.emrview.win.isVisible()) {
					MyMessageTip.msg('提示', '当前不在门诊病历界面！', true);
					return;
				}
				if (this.exContext.empiData.empiId != empiId) {
					MyMessageTip.msg('提示', '当前病人不是原来诊断的病人！', true);
					return;
				}
				for (var i = 0, len = openNodes.length; i < len; i++) {
					var nd = openNodes[i];
					var node = {};
					node.leaf = true;
					node.attributes = {};
					node.attributes.key = nd.key;
					node.attributes.type = nd.type;
					node.attributes.ref = nd.ref;
					var e = {};
					this.emrview.onNavTreeClick(node, e);
				}
				ymPrompt.doHandler('doClose', true);
			},
			openPrescriptionWin : function() {
				debugger;
				// 处方编辑
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
					return;
				}
				var module = this.createModule("clinicPrescriptionEntry",
						this.refPrescriptionEntryModule, {
							id : "card-1"
						});
				module.opener = this;
				module.exContext = this.exContext;
				if (module.getClinicInitParams() == -1) {
					return;
				}
				if (this.exContext.systemParams.YS_MZ_FYYF_XY == -1
						|| this.exContext.systemParams.YS_MZ_FYYF_ZY == -1
						|| this.exContext.systemParams.YS_MZ_FYYF_CY == -1
						|| this.exContext.systemParams.YS_MZ_FYYF_XY == ""
						|| this.exContext.systemParams.YS_MZ_FYYF_ZY == ""
						|| this.exContext.systemParams.YS_MZ_FYYF_CY == "") {
					MyMessageTip.msg("错误", "请先设置门诊发药药房!", true);
					this.panel.getEl().unmask();
					return;
				}
				module.on("close", this.onWinClose, this);
				module.on("doSave", this.onClinicSave, this);
				var p = module.initPanel();
				p.id = 'clinicPrescriptionEntry';
				return p;
				// this.on("beforeclose", this.onBeforeWinClose, this);
				// if (!this.clinicPrescriptionEntry_win) {
				// var win = module.getWin();
				// win.setTitle('处方录入-' + '【' +
				// this.mainApp.departmentName
				// + '】');
				// this.clinicPrescriptionEntry_win = win;
				// }
				// this.clinicPrescriptionEntry_win.show();
				// this.clinicPrescriptionEntry_win.maximize();
				// this.panel.getEl().unmask();
			},
			openDisposalWin : function() {
				debugger;
				// 处置编辑
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
					return;
				}
				// this.panel.getEl().mask("业务模块载入中...");
				var module = this.createModule("clinicopenDisposalEntry",
						this.refDisposalEntryModule);
				module.opener = this;
				module.exContext = this.exContext;
				module.on("close", this.onWinClose, this);
				module.on("doSave", this.onClinicSave, this);
				// if (!this.clinicopenDisposalEntry_win) {
				// var win = module.getWin();
				// this.clinicopenDisposalEntry_win = win;
				// }
				// this.clinicopenDisposalEntry_win.show();
				// this.panel.getEl().unmask();
				var p = module.initPanel();
				p.id = 'clinicopenDisposalEntry';
				return p;
			},
			onWinClose : function() {
				// 关闭事件
				switch(this.name){
					case "【中医馆服务】电子病历":
						if(this.opener.tcmstate == 1){
							this.opener.saveClinicMedicalRecordTcm();
							this.opener.resetTcmSelectItem(0);
							this.opener.tcmstate = 0;
						}
					break;
					case "【中医馆服务】辩证论治":
						if(this.opener.tcmstate == 1){
							this.opener.saveSyndromeDifferentiationAndRreatment();
							this.opener.resetTcmSelectItem(0);
							this.opener.tcmstate = 0;
						}
					break;
					case "【中医馆服务】知识库":
					case "【中医馆服务】治未病":
					case "【中医馆服务】远程教育":
					case "【中医馆服务】远程会诊":
						if(this.opener.tcmstate == 1){
							this.opener.resetTcmSelectItem(0);
							this.opener.tcmstate = 0;
						}
					break;
					default:
				}
			},
			resetTcmSelectItem : function(value){
				var obj = document.getElementById('tcm'); 
				$.each(obj.options, function (i, n) {
				    if (n.value == value) {
				        n.selected = true;
				    }
				});
			},
			saveClinicMedicalRecordTcm : function(){
				var jzxh = this.exContext.ids.clinicId;
				//获取【中医馆】诊断信息
				phis.script.rmi.jsonRequest({
							serviceId : "phis.TcmService",
							serviceAction : "getDzblFromTcm",
							body : {
								"jzxh" : jzxh
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return false;
							}
							if(!json.body.tcmdzbl){	
								MyMessageTip.msg("提示", "未获取到电子病历信息，请确认已在中医馆平台录入并成功保存电子病历信息!", true, 2);
								return;
							}
							//获取【中医馆】诊断信息
							if(json.body.tcmzd){
								var data = [];
								for(j = 0,len=json.body.tcmzd.length; j < len; j++) {
									var d = {};
									d.ZDYS = this.mainApp.uid;
									d.ZDYS_text = this.mainApp.uname;
									d.CFLX = json.body.tcmzd[j].CFLX;
									d.CFLX_text = json.body.tcmzd[j].CFLX_text;
									d.DEEP = json.body.tcmzd[j].DEEP;
									d.FBRQ = json.body.tcmzd[j].FBRQ;
									d.FZBZ = json.body.tcmzd[j].FZBZ;
									d.FZBZ_text = json.body.tcmzd[j].FZBZ_text;
									d.ICD10 = json.body.tcmzd[j].ICD10;
									d.JBPB_text = json.body.tcmzd[j].JBPB_text;
									d.PLXH = json.body.tcmzd[j].PLXH;
									d.SJZD = json.body.tcmzd[j].SJZD;
									d.ZDBW = json.body.tcmzd[j].ZDBW;
									d.ZDLB = json.body.tcmzd[j].ZDLB;
									d.ZDLB_text = json.body.tcmzd[j].ZDLB_text;
									d.ZDMC = json.body.tcmzd[j].ZDMC;
									d.ZDSJ = json.body.tcmzd[j].ZDSJ;
									d.ZDXH = json.body.tcmzd[j].ZDXH;
									d.ZXLB = json.body.tcmzd[j].ZXLB;
									d.ZXLB_text = json.body.tcmzd[j].ZXLB_text;
									d.ZZBZ = json.body.tcmzd[j].ZZBZ;
									data.push(d);
								}
								phis.script.rmi.jsonRequest({
									serviceId : "clinicDiagnossisService",
									serviceAction : "saveDiagnossis",
									body : {
										"jzxh" : jzxh,
										"brid" : this.exContext.empiData.BRID,
										"dignosisList" : data
									}
								}, function(code, msg, json) {
									if (code > 300) {
										this.processReturnMsg(code, msg)
										//return false;
									}
								}, this)
							}
							//获取并保存【中医馆】电子病历信息	
							this.setMedicalInfo(json.body.tcmdzbl, 0);
							this.doSave(false,false);
							this.onClinicRemove();
						}, this)
			},
			CRBBGK : function(MS_BRZD_JLBH,ICD10){
				var param=phis.script.common.loadSystemParams({"privates" : ['CRBBGK']}).CRBBGK;
				if(param!="1"){
					return;
				}
				this.exContext.args.MS_BRZD_JLBH = MS_BRZD_JLBH || '';
				this.exContext.args.ICD10 = ICD10 || '';
				var _this = this;
				//根据门诊诊断记录编号查询是否已保存传染病报告卡
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "queryIdrReport",
							body : {
								"MS_BRZD_JLBH" : MS_BRZD_JLBH,
								"EMPIID" : this.exContext.empiData.empiId,
								"ICD10" : ICD10
							}
						});
				if(resData.code > 200){
					return;
				}
				if(resData.json.result){
					var isSaved = resData.json.result.isSaved;
					if(typeof(isSaved)!="undefined" && !isSaved){
						_this.opener.needSaveCrb = true;
						Ext.Msg.show({
							title : '确认',
							msg : '该诊断为传染病诊断，是否填写传染病报告卡？',
							modal : false,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									_this.popBgkWin();
								}
							},
							scope : this
						});
					}
				}
			},
			saveSyndromeDifferentiationAndRreatment  : function() {
				var jzxh = this.exContext.ids.clinicId;
				//获取【中医馆】处方信息
				phis.script.rmi.jsonRequest({
							serviceId : "phis.TcmService",
							serviceAction : "getCfxxFromTcm",
							body : {
								"jzxh" : jzxh
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return false;
							}
							if(!json.body.tcmprescription || json.body.tcmprescription.length==0){	
								MyMessageTip.msg("提示", "未获取到处方信息，请确认已在中医馆平台录入并成功保存处方信息!", true, 2);
								return;
							}
							//获取【中医馆】药品信息
							for(i = 0,len=json.body.tcmprescription.length; i < len; i++) {
								var kfrq = json.body.tcmprescription[i].kfrq;
								var ypxxs = json.body.tcmprescription[i].ypxxs;								
								var data = {};
								var formData = {};
								var fjxxData =[];
								var listData = [];
								formData.BRID = this.exContext.empiData.BRID;
								formData.BRXM = this.exContext.empiData.personName;
								formData.CFHM = "";
								formData.CFLX = "3";
								formData.CFSB = undefined;
								formData.CFTS = 1;//默认处方贴数为1
								formData.DJLY = "1";
								formData.DJYBZ = "0";
								formData.JGID = this.mainApp.deptId;
								formData.JZXH = jzxh;
								formData.KFRQ = kfrq;
								formData.KSDM = this.mainApp.phis.departmentId;
								formData.YFSB = this.exContext.systemParams.YS_MZ_FYYF_CY;
								formData.YSDM = this.mainApp.uid;
								for(j = 0,len2=ypxxs.length; j < len2; j++) {
									var d = {};
									d.BZXX = ypxxs[j].BZXX;
									d.CFLX = ypxxs[j].CFLX;
									d.CFLX_TEXT = ypxxs[j].CFLX_TEXT;
									d.CFTS = ypxxs[j].CFTS;
									d.FYGB = ypxxs[j].FYGB;
									d.GMYWLB = ypxxs[j].GMYWLB;
									d.GYTJ = ypxxs[j].GYTJ;
									d.GYTJ_TEXT = ypxxs[j].GYTJ_TEXT;
									d.JLDW = ypxxs[j].JLDW;
									d.JYLX = ypxxs[j].JYLX;
									d.JYLX_TEXT = ypxxs[j].JYLX_TEXT;
									d.KPDY = ypxxs[j].KPDY;
									d.KSBZ = ypxxs[j].KSBZ;
									d.KSSDJ = ypxxs[j].KSSDJ;
									d.MRCS = ypxxs[j].MRCS;
									d.MTSL = ypxxs[j].MTSL;
									d.PSPB = ypxxs[j].PSPB;
									d.SFJG = ypxxs[j].SFJG;
									d.TSYP = ypxxs[j].TSYP;
									d.TYPE = ypxxs[j].TYPE;
									d.YCJL = ypxxs[j].YCJL;
									d.YCYL = ypxxs[j].YCYL;
									d.YFBZ = ypxxs[j].YFBZ;
									d.YFDW = ypxxs[j].YFDW;
									d.YFGG = ypxxs[j].YFGG;
									d.YPCD = ypxxs[j].YPCD;
									d.YPCD_TEXT = ypxxs[j].YPCD_TEXT;
									d.YPDJ = ypxxs[j].YPDJ;
									d.YPJL = ypxxs[j].YPJL;
									d.YPMC = ypxxs[j].YPMC;
									d.YPSL = ypxxs[j].YPSL;
									d.YPXH = ypxxs[j].YPXH;
									d.YPYF = ypxxs[j].YPYF;
									d.YPYF_TEXT = ypxxs[j].YPYF_TEXT;
									d.YPZH_SHOW = ypxxs[j].YPZH_SHOW;
									d.YPZS = ypxxs[j].YPZS;
									d.YQSY = ypxxs[j].YQSY;
									d.YQSYFS = ypxxs[j].YQSYFS;
									d.YYTS = ypxxs[j].YYTS;
									d.ZBY = ypxxs[j].ZBY;
									d.ZFBL = ypxxs[j].ZFBL;
									d.ZFPB = ypxxs[j].ZFPB;
									d.ZFYP = ypxxs[j].ZFYP;
									d._opStatus = "create";
									listData.push(d);
								}
								data.BRXZ=this.exContext.empiData.BRXZ;
								data.JZXH=jzxh;
								data.fjxxData = fjxxData;
								data.formData = formData;
								data.listData = listData;
								var resData = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicManageService",
									serviceAction : "saveClinicInfo",
									body : data
								});
								var code2 = resData.code;
								var msg2 = resData.msg;
								var json2 = resData.json;
								this.panel.el.unmask()
								if (code2 >= 300) {
									this.processReturnMsg(code2, msg2);
									return;
								}
							}
							//this.onClinicRemove();
							//this.openPrescriptionWin();
							this.openCardLayoutWin(2);
						}, this)
			},
			getHtml : function() {
				//zhaojian 2017-11-14 门诊医生站增加症状的下拉框选择功能：数据库ms_bcjl表中增加字段ZZDM、ZZMC
                var dicName = {
                    id : "phis.dictionary.mzzz"
                };
                var mzzz=util.dictionary.DictionaryLoader.load(dicName);
                var mzzz_items="<option value=\"\" selected=\"true\">--请选择--</option>";
                for(var i=0;i<mzzz.items.length;i++){
                    mzzz_items+="<option value=\""+mzzz.items[i].key+"\">"+mzzz.items[i].text+"</option>";
                }
                  var ybmc = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "queryYBMC",
							body:{
							idCard : this.exContext.empiData.idCard
							}
							//body : this.data
						});
					var ybmc_items="<option value=\"\" selected=\"true\">--请选择--</option>";
						if(ybmc.json.body.ybmc){
                 for(var i=0;i<ybmc.json.body.ybmc.length;i++){
               ybmc_items+="<option value=\""+ybmc.json.body.ybmc[i].JBBM+"\">"+ybmc.json.body.ybmc[i].JBMC+"</option>";
              }
						}
                //增加中医馆服务下拉框，判定当前科室为中医科时才显示该功能
                var tcmSelect = "";
                //科室类型为中医科时，才提供中医馆服务
                if(this.mainApp.departmentType=="AA50" || this.mainApp.deptId.length>9)
                {
                	tcmSelect = '<div id="divTcm"><select id="tcm"><option value="0">中医馆服务</option><option value="1">【中医馆】知识库</option><option value="2">【中医馆】电子病历</option><option value="3">【中医馆】辨证论治</option><option value="4">【中医馆】治未病</option><option value="5">【中医馆】远程教育</option><option value="6">【中医馆】远程会诊</option></select></div>';
                }
				return '<div class="clear" style="position:absolute;z-index:1000;background-color:white;left:10px;top:-4px;width:96%">'
						+ '<ul class="Btns">'
						+ '<li id="SV" class="topBtn"><a id="SV" ><span id="SV">保&nbsp;&nbsp;存</span></a></li>'
						+ '<li id="PRINT" class="topBtn"><a id="PRINT" ><span id="PRINT">打&nbsp;&nbsp;印</span></a></li>'
						+ '<li id="zhuanzhen" class="topBtn"><a id="zhuanzhen" ><span id="zhuanzhen">定向转诊</span></a></li>'
						+ '<li id="yuyue" class="topBtn"><a id="yuyue" ><span id="yuyue">预&nbsp;&nbsp;约</span></a></li>'
						+ '<li id="signIn" class="topBtn"><a id="signIn" ><span id="signIn">捷士达签约</span></a></li>'
						+ '<li id="fncfk" class="topBtn"><a id="fncfk" ><span id="fncfk">NFC发卡</span></a></li>'
						+ '<li id="transfer" class="topBtn"><a id="transfer" ><span id="transfer">捷士达转诊</span></a></li>'
						+ '<li id="continue" class="topBtn"><a id="continue" ><span id="continue">处方延伸</span></a></li>'
						+ '<li id="EHR" class="topBtn"><a id="EHR" ><span id="EHR">EHR</span></a></li>'
						+ '<li id="TS" class="topBtn"><a id="TS" ><span id="TS">暂&nbsp;&nbsp;挂</span></a></li>'
						+ '<li id="BLFZ" class="topBtn"><a id="BLFZ" ><span id="BLFZ">复制上次病历</span></a></li>'
						+ '<li id="ST" class="topBtn"><a id="ST" ><span id="ST">就诊结束</span></a></li>'
						+ (this.exContext.systemParams.QYJYBZ == 1
								? '<li id="JYJG" class="topBtn"><a id="JYJG" ><span id="JYJG">检验结果</span></a></li>'
								: '')
						+ '<li id="CFLZ"  class="topBtn"><a id="newPrescription" ><span id="newPrescription" name="cflz">处方流转</span></a></li>'
						// + '<li id="RPY" class="topBtn"><a id="RPY" ><span
						// id="RPY">门诊转诊申请</span></a></li>'
						// + '<li id="EPY" class="topBtn"><a id="EPY" ><span
						// id="EPY">检查申请</span></a></li>'
						// + '<li id="HPY" class="topBtn"><a id="HPY" ><span
						// id="HPY">住院转诊申请</span></a></li>'
						+ tcmSelect
						+ '</ul>'
						+ '<ul class="Btns2">'
						+ '<li id="newDiagnosis" class="topBtn"><a title="诊断编辑" id="newDiagnosis"><span id="newDiagnosis">诊&nbsp;&nbsp;断</span></a></li>'
						+ '<li id="newPrescription" class="topBtn"><a title="处方编辑" id="newPrescription"><span id="newPrescription">处&nbsp;&nbsp;方</span></a></li>'
						+ '<li id="newDisposal" class="topBtn"><a title="处置编辑" id="newDisposal"><span id="newDisposal">辅&nbsp;&nbsp;检</span></a></li>'
						+ '</ul>'
						+ '<ul class="Btns3">'
						+ '<li class="topBtn" id="IP"><a id="IP" title="提取病历模板"><span id="IP">病&nbsp;&nbsp;历</span></a></li>'
						+ '<li class="topBtn" id="GP"><a id="GP" title="提取诊疗模板"><span id="GP">诊&nbsp;&nbsp;疗</span></a></li>'
						+ '<li class="topBtn" id="CL"><a id="CL" title="清空页面"><span id="CL">清&nbsp;&nbsp;空</span></a></li>'
						+ '</ul>'
						+ '</div>'
						+ '<div class="rtmain"><div class="Rlist" style="position:relative;top:18px;">'
						+ '<div style="width:100%; overflow-x:hidden;overflow-y:hidden;z-index:0;">'
						+ '<table cellpadding="1" cellspacing="0" class="BL_table" id="L_table">'
						+ '<tr><th width="15%">主诉：</th><td colspan="2"><div id="div_ZSXX" /></td></tr>'
						+ '<tr>'
						+ '<th>现病史：</th><td colspan="2"><div id="div_XBS" /><div style="float:right;margin-right: 20px;margin-top: 7px"> <a title="现病史" style="float:right;"><span><img id="XBS_TC" src="photo/add.png" style="float:right;"></span></a></div></td>'
						+ '</tr>'
						+ '<tr><th>过敏史：</th><td colspan="2"><div id="div_GMS" /></td></tr>'
						+ '<tr>'
						+ '<th>既往史：</th><td colspan="2"><div id="div_JWS" /><a title="既往史" style="float:right;"></a></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<th >&nbsp;</th><td colspan="2"><label><input type="checkbox" title="代配药" id="DPY" name="DPY"/>代配药</label><label>T:<input type="text" title="体温" id="T" name="T" class="inputs" onkeyup="this.value=this.value.replace(/\D/g,\'\')" onkeydown="keydown(event.keyCode, \'T\')"/>℃</label><label>P:<input type="text" title="脉率" id="P" name="P" class="inputs" onkeydown="keydown(event.keyCode, \'P\')"/>次/分</label><label>R:<input type="text" title="呼吸频率" id="R" name="R" class="inputs" onkeydown="keydown(event.keyCode, \'R\')"/>次/分</label><label>BP:<input type="text" title="收缩压" id="SSY" name="SSY" class="inputs" onkeydown="keydown(event.keyCode, \'SSY\')" /></label>/<label><input type="text" title="舒张压" id="SZY" name="SZY" name="input5" class="inputs" onkeydown="keydown(event.keyCode, \'SZY\')" />mmHg</label><label>H:<input type="text" title="身高" id="H" name="H" name="H" class="inputs" onkeydown="keydown(event.keyCode, \'H\')" onblur="blurField(\'H\')"/>cm</label><label>W:<input type="text" title="体重" id="W" name="W" name="W" class="inputs" onkeydown="keydown(event.keyCode, \'W\')" onblur="blurField(\'W\')" />kg</label><label>BMI:<input type="text" title="BMI" id="BMI" disabled="disabled" name="BMI" name="BMI" class="inputs" onkeydown="keydown(event.keyCode, \'BMI\')" /></label></td>'
						+ '</tr>'
						+ '<tr style="display:'
						+ (this.isOphthal ? "" : "none")
						+ '; " >'
						+ '<th >&nbsp;</th><td colspan="2"><label>左眼视力:<input type="text" title="左眼视力" id="LEFTVISION" name="LEFTVISION" class="inputs"  /></label><label>右眼视力:<input type="text" title="右眼视力" id="RIGHTVISION" name="RIGHTVISION" class="inputs"  /></label><label>左眼矫正视力:<input type="text" title="左眼矫正视力" id="LEFTCORRECTEDVISION" name="LEFTCORRECTEDVISION" class="inputs"  /></label><label>右眼矫正视力:<input type="text" title="右眼矫正视力" id="RIGHTCORRECTEDVISION" name="RIGHTCORRECTEDVISION" class="inputs"  /></label></td>'
						+ '</tr>'
						+ '<tr>'
						//zhaojian 2017-11-14 门诊医生站增加症状的下拉框选择功能：数据库ms_bcjl表中增加字段ZZDM、ZZMC                       
                        /*+ '<th>临床表现：</th><td colspan="2"><label><input type="checkbox" name="KS" id="KS" >&nbsp咳嗽</label><label><input type="checkbox" name="YT" id="YT" >&nbsp咽痛</label><label><input type="checkbox" name="HXKN" id="HXKN" >&nbsp呼吸困难</label><label><input type="checkbox" name="OT" id="OT" >&nbsp呕吐</label><label><input type="checkbox" name="FT" id="FT" >&nbsp腹痛</label><label><input type="checkbox" name="FX" id="FX" >&nbsp腹泻</label><label><input type="checkbox" name="PZ" id="PZ" >&nbsp皮疹</label><label><input type="checkbox" name="QT2" id="QT2" >&nbsp其他</label>'
                        + '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp妊娠标志：<label><input type="radio" value="0" checked name="RSBZ">无</label><label><input type="radio" value="1" name="RSBZ">妊娠期</label><label><input type="radio" value="2" name="RSBZ">哺乳期</label></td>'*/
                       + '<tr>'
						 + '<th>门慢/门特补登记信息：</th><td colspan="2">'
						+ '&nbsp&nbsp&nbsp&nbsp门诊标志：<label><input type="radio" value="11" checked name="MSBZ">普通门诊</label><label><input type="radio" value="16" name="MSBZ">门诊慢性病</label><label><input type="radio" value="171" name="MSBZ">门特</label>'
                        + '&nbsp&nbsp&nbsp&nbsp疾病名称：<label><select id="ybmc" style="width:140px; height:28px">'+ybmc_items+'</select></label><label hidden="true"><input hidden="true" type="checkbox" name="tnb" id="tnb" >&nbsp糖尿病II型</label></td>'
						+ '</tr>'
						+ '<th>临床表现：</th><td colspan="2">&nbsp&nbsp&nbsp症状：<label><select id="MZZZ" style="width:140px; height:28px">'+mzzz_items+'</select></label><label hidden="true"><input hidden="true" type="checkbox" name="KS" id="KS" >&nbsp咳嗽</label><label hidden="true"><input type="checkbox" hidden="true" name="YT" id="YT" >&nbsp咽痛</label><label hidden="true"><input type="checkbox" hidden="true" name="HXKN" id="HXKN" >&nbsp呼吸困难</label><label hidden="true"><input type="checkbox" hidden="true" name="OT" id="OT" >&nbsp呕吐</label><label hidden="true"><input type="checkbox" hidden="true" name="FT" id="FT" >&nbsp腹痛</label><label hidden="true"><input type="checkbox" hidden="true" name="FX" id="FX" >&nbsp腹泻</label><label hidden="true"><input type="checkbox" hidden="true" name="PZ" id="PZ" >&nbsp皮疹</label><label hidden="true"><input type="checkbox" hidden="true" name="QT2" id="QT2" >&nbsp其他</label>'
                        + '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp妊娠标志：<label><input type="radio" value="0" checked name="RSBZ">无</label><label><input type="radio" value="1" name="RSBZ">妊娠期</label><label><input type="radio" value="2" name="RSBZ">哺乳期</label></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<th>体格检查：</th><td colspan="2"><div id="div_TGJC" /><div  style="float:right;margin-right: 20px"><a title="体格检查" ><span><img id="TGJC_TC" src="photo/add.png" style="float:right;"></span></a></div></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<th>辅助检查：</th><td colspan="2"><div id="div_FZJC" /><div style="float:right;margin-right: 20px"><a title="辅助检查" style="float:right;"><span><img id="FZJC_TC" src="photo/add.png" style="float:right;"></span></a></div> </td>'
						+ '</tr>'
						+ this.getZyksHtml()
						+ ' <th>初步诊断：</th><td>'
						+ '<div id="clinicDiv">'
						// + this.getDiagnosisHtml(null)
						+ '</div>'
						+ '</td>'
						+ '<td width="15%" class="edit_btn" ><p class="mzBtns"><a title="诊断编辑"><span id="newDiagnosis">诊&nbsp;&nbsp;断</span></a></p></td>'
						+ '</tr>'
						+ '<tr><th>处理措施：</th><td>'
						+ '<div id="measuresDiv">'
						+ this.getMeasuresHtml(null)
						+ '</div></td>'
						+ '<td class="edit_btn">'
						+ '<p style="width:100%;" class="mzBtns"><a title="处方编辑" style="float:right;"><span id="newPrescription">处&nbsp;&nbsp;方</span></a></p>'
						+ '<p class="mzBtns"><a title="处置编辑"><span id="newDisposal">辅&nbsp;&nbsp;检</span></a></p></td>'
						+ '</tr>'
						+ '<tr><th>健康教育：</th><td colspan="2"><div id="div_JKJYNR" /></td></tr>'
						+ '<tr>'
							+ '<th>处理意见：</th>'
							+ '<td colspan="2"><div id="div_BQGZ" /><div style="float:right;margin-right: 20px"><a title="处理意见" style="float:right;"><span><img id="BQGZ_TC" src="photo/add.png" style="float:right;"></span></a></div> </td>'
						+ '</tr>'
						//不知道为什么这行会隐掉
						+ '<tr><th></th><td colspan="2"><div id="div_zuihou" /></td></tr>'
//						+ '<tr>'
//						+ '<th>健康教育：</th><td colspan="2">'
//						+ '<table width="100%"><tr><td style="height:20px;"><div style="float:left;">健康处方快捷录入:</div><div id="div_healthEduInput" ></div></td></tr><tr height="160px"><td width="95%" style="vertical-align: top;"><div style="margin-top:8px;" id="div_JKJY"/></td>'
//						+ '<td class="edit_btn"><p class="mzBtns" style="height:159px"><a title="引入健康处方">'
//						+ '<span id="importHER">健康教育</span>'
//						+ '</a></p></tr></table></td>'
						+ '</table>'
						+ '</div></div>';
			},
			// 中医科室增加手动输入治则
			getZyksHtml : function() {
				var html = '';
				var SystemParamsZyks = this.loadSystemParams({
							privates : ['ZYKS']
						});
				this.zyks = SystemParamsZyks.ZYKS;
				if (this.mainApp.reg_departmentId == this.zyks) {
					var html = '<tr><th>治则：</th><td colspan="2"><div id="div_ZZ" /><a title="治则" style="float:right;"></a></td></tr>';
				}
				return html;
			},
			// 诊断信息
			getDiagnosisHtml : function(records) {
				this.ZZDICD10 = null;
				this.ZZDMC = null;
				var html = '<table id="diagnosisTable" cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				if (records != null && records.length > 0) {
					var num = 1;
					for (var i = 0; i < records.length; i++) {
						var r = records[i];
						var imageUrl = (i == 0
								? ('&nbsp;&nbsp;<img name="addDiag" src=\''
										+ ClassLoader.appRootOffsetPath + 'resources/phis/resources/css/app/desktop/images/shared/icons/fam/add.gif\' style="cursor:pointer;" title=\'增加\' />')
								: "");
						imageUrl += '&nbsp;&nbsp;<img name="deleteDiag" id="diag_'
								+ r.JLBH
								+ '" src=\''
								+ ClassLoader.appRootOffsetPath
								+ 'resources/phis/resources/css/app/desktop/images/shared/icons/fam/delete.gif\' style="cursor:pointer;" title=\'删除\' />';
						if (r.ZZBZ == 1) {
							this.ZZDICD10 = r.ICD10;
							this.ZZDMC = r.ZDMC;
							var module = this.midiModules["refRecipeImportModule"];
							if (module) {
								module.ZZDICD10 = this.ZZDICD10;
								module.ZZDMC = this.ZZDMC;
							}
						}
						this.JLBH = r.JLBH;
						this.emrview.JLBH = r.JLBH
						var zd = "";
						if (r.FZBZ == 1) {
							zd = '复诊';
						} else if (r.FZBZ == 2) {
							zd = '待查';
						} else {
							zd = '初诊';
						}
						html += '<tr><td >'
								+ (r.DEEP == 0 ? num + '.' : '&nbsp')
								+ '</td><td>'
								+ this.getDeepStr(r.DEEP)
								// 判断是否存在
								+ "<div id='DIV_ZDLR_"
								+ r.JLBH
								+ "' ></div></td><td>"
								// + r.ZDMC
								// + (r.CFLX == 2 ? '&nbsp;&nbsp;'
								// + (r.ZHMC || '') : '')
								+ ((r.ZZBZ == 1 || i == 0)
										? '<img src="'
												+ ClassLoader.appRootOffsetPath
												+ 'resources/phis/resources/css/app/biz/images/zhu.png" title="主诊断" />'
										: '') + '</td><td><div id="DIV_FZBZ_'
								+ r.JLBH + '"></div></td><td>' + imageUrl
								+ '</td></tr>'
						if (r.DEEP == 0)
							num++;
					}
					// this.isCZ = true;
					for (var i = 0; i < records.length; i++) {
						var r = records[i];
						if (r.FZBZ == 1) {
							this.isCZ = false;
							break;
						} else {
							this.isCZ = true;
						}
					}
				} else {
					var imageUrl = '<img name="addDiag" src=\''
							+ ClassLoader.appRootOffsetPath
							+ 'resources/phis/resources/css/app/desktop/images/shared/icons/fam/add.gif\' style="cursor:pointer;" title=\'增加\' />';
					imageUrl += '&nbsp;&nbsp;<img name="deleteDiag" id="diag_NEW" src=\''
							+ ClassLoader.appRootOffsetPath
							+ 'resources/phis/resources/css/app/desktop/images/shared/icons/fam/delete.gif\' style="cursor:pointer;" title=\'删除\' />';
					html += "<tr><td >1</td><td><div id='DIV_ZDLR_NEW'></div></td><td><img src='"
							+ ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/css/app/biz/images/zhu.png' title='主诊断' /><td><div id='DIV_FZBZ_NEW'></div></td><td>"
							+ imageUrl + "</td></tr>";
				}
				html += '</table>';
				return html;
			},
			// 处理措施
			getMeasuresHtml : function(measures, disposal) {
				var html = '<table cellpadding="0" cellspacing="0" border="0" class="BL_ul">'
				var num = 1;
				if (measures != null && measures.length > 0) {
					for (var i = 0; i < measures.length; i++) {
						var r = measures[i];
						html += "<tr>";
						if (num == 1) {
							if (r.TYPE == 3) {
								// isHerbs = true;
							}
							html += '<td>' + num + '.</td>';
							num++;
						} else {
							if (r.CFSB != measures[i - 1].CFSB) {
								html += '<td>' + num + '.</td>';
								num++;
								// isHerbs = true;
							} else {
								html += '<td>&nbsp;</td>';
							}
						}
						if (r.TYPE == "3") {
							// html += '<td>' + r.YPMC + '</td><td colspan="6">'
							// + r.YPSL + r.YFDW + '</td></tr>';
							// 判断是否需要添加尾部信息
							if (i + 1 >= measures.length
									|| r.CFSB != measures[i + 1].CFSB
									|| r.YPZH != measures[i + 1].YPZH) {
								html += '<td colspan="4">' + r.YPMC
										+ '</td><td colspan="1">' + r.YPSL
										+ r.YFDW
										+ '</td><td colspan="5"></td></tr>';
								html += '<td colspan="11" align="center">帖数：'
										+ r.CFTS
										+ '&nbsp;&nbsp;&nbsp;&nbsp;用法：'
										+ r.GYTJ_text
										// +
										// '&nbsp;&nbsp;&nbsp;&nbsp;服法：'
										// + r.YPZS_text
										+ '&nbsp;&nbsp;&nbsp;&nbsp;'
										+ r.YPYF_text + '</td>';
							} else if (i + 1 < measures.length
									&& r.CFSB == measures[i + 1].CFSB
									&& r.YPZH == measures[i + 1].YPZH) {
								html += '<td colspan="4">' + r.YPMC
										+ '</td><td colspan="1">' + r.YPSL
										+ r.YFDW + '</td>';
								i++;
								r = measures[i];
								html += '<td colspan="4">' + r.YPMC
										+ '</td><td >' + r.YPSL + r.YFDW
										+ '</td></tr>';
								if (i + 1 >= measures.length
										|| r.CFSB != measures[i + 1].CFSB
										|| r.YPZH != measures[i + 1].YPZH) {
									html += '<td colspan="11" align="center">帖数：'
											+ r.CFTS
											+ '&nbsp;&nbsp;&nbsp;&nbsp;用法：'
											+ r.GYTJ_text
											// +
											// '&nbsp;&nbsp;&nbsp;&nbsp;服法：'
											// + r.YPZS_text
											+ '&nbsp;&nbsp;&nbsp;&nbsp;'
											+ r.YPYF_text + '</td>';
								}
							}
						} else {
							html += '<td colspan="6">' + r.YPMC;
							if (r.YFGG) {
								html += "/" + r.YFGG + '&nbsp;';
							}
							// 判断是否皮试
							if (r.PSPB > 0) {

								if (r.PSJG) {
									var psjg_text = "<font color='#FFFA4C'>未知</font>";
									if (r.PSJG == 1) {
										psjg_text = "<font color='red'>阳性</font>";
									} else if (r.PSJG == -1) {
										psjg_text = "<font color='green'>阴性</font>";
									}
									html += ' (' + psjg_text + ')';
								} else if (this.exContext.systemParams.QYPSXT != 1) {
									if (!r.FPHM && r.PSPB == 2) {
										html += '<img id="jzxh_'
												+ r.SBXH
												+ '" src="'
												+ ClassLoader.appRootOffsetPath
												+ 'resources/phis/resources/css/app/biz/images/pi.png" width="21" height="21" />';
									} else {
										html += '<img id="jzxh_'
												+ r.SBXH
												+ '" src="'
												+ ClassLoader.appRootOffsetPath
												+ 'resources/phis/resources/css/app/biz/images/pi.png" width="21" height="21"  style="cursor:pointer;" onClick="openSkinTestWin('
												+ r.SBXH + ',' + r.YPXH + ','
												+ r.CFSB + ',' + r.GMYWLB
												+ ',\'' + r.GMYWLB_text + '\','
												+ this.mainApp.uid + ')"/>';
									}
								}
								html += '</td>';
							}
							html += '<td>' + r.YPYF_text + '</td>' + '<td>'
									+ r.YCJL + (r.JLDW ? r.JLDW : '') + '</td>'
									+ '<td>' + r.YPSL + (r.YFDW ? r.YFDW : '')
									+ '</td>' + '<td>' + r.GYTJ_text + '</td>';
							html += '</tr>';
						}
					}
				}
				if (disposal != null && disposal.length > 0) {
					for (var i = 0; i < disposal.length; i++) {
						var r = disposal[i];
						html += "<tr>";
						html += '<td>' + num + '.</td>';
						html += '<td colspan="5">' + r.FYMC + '</td>'
								+ '<td colspan="5">' + r.YLSL + r.FYDW
								+ '</td>';
						html += "</tr>";
						num++;
					}
				}
				html += '</table>';
				return html;
			},
			getDeepStr : function(deep) {
				var s = "";
				for (var j = 0; j < deep; j++) {
					s += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
				}
				return s;
			},
			// 设置病历表中的信息
			setMedicalInfo : function(record, flag) {
				if (record) {
					//zhaojian 2017-11-14 门诊医生站增加症状的下拉框选择功能：数据库ms_bcjl表中增加字段ZZDM、ZZMC
                    if(document.getElementById("MZZZ")){
                        var zzdm = record.ZZDM==null?"":record.ZZDM;
                        for(var i=0;i<document.getElementById("MZZZ").length;i++){
                            if(document.getElementById("MZZZ").options[i].value==zzdm){
                                document.getElementById("MZZZ").options[i].selected = true;
                            }
                        }
                    }
					Ext.getCmp("ZSXX").setValue((record.ZSXX || ""))
					Ext.getCmp("XBS").setValue(record.XBS || "")
					Ext.getCmp("JWS").setValue(record.JWS || "")
					Ext.getCmp("TGJC").setValue(record.TGJC || "")
					Ext.getCmp("BQGZ").setValue(record.BQGZ || "")
					Ext.getCmp("JKJYNR").setValue(record.JKJYNR || "")
					Ext.getCmp("GMS").setValue(record.GMS || "")
					if (this.mainApp.reg_departmentId == this.zyks) {
						Ext.getCmp("ZZ").setValue(record.ZZ || "")
					}

					for (var i = 0; i < document.getElementsByName("RSBZ").length; i++) {
						var radio = document.getElementsByName("RSBZ")[i];
						if (radio.value == record.RSBZ) {
							radio.checked = true;
							break;
						}
					}

					Ext.getCmp("FZJC").setValue(record.FZJC || "")
					document.getElementById("DPY").checked = record.DPY == 1
							? true
							: false;

					document.getElementById("LEFTVISION").value = record.LEFTVISION
							|| "";
					document.getElementById("RIGHTVISION").value = record.RIGHTVISION
							|| "";
					document.getElementById("LEFTCORRECTEDVISION").value = record.LEFTCORRECTEDVISION
							|| "";
					document.getElementById("RIGHTCORRECTEDVISION").value = record.RIGHTCORRECTEDVISION
							|| "";
					if (flag != 1) {
						document.getElementById("T").value = record.T || "";
						document.getElementById("R").value = record.R || "";
						document.getElementById("P").value = record.P || "";
						document.getElementById("SSY").value = record.SSY || "";
						document.getElementById("SZY").value = record.SZY || "";
					}
					document.getElementById("H").value = record.H || "";
					document.getElementById("W").value = record.W || "";
					document.getElementById("BMI").value = record.BMI || "";
					document.getElementById("KS").checked = record.KS == 1
							? true
							: false;
					document.getElementById("YT").checked = record.YT == 1
							? true
							: false;
					document.getElementById("HXKN").checked = record.HXKN == 1
							? true
							: false;
					document.getElementById("OT").checked = record.OT == 1
							? true
							: false;
					document.getElementById("FT").checked = record.FT == 1
							? true
							: false;
					document.getElementById("FX").checked = record.FX == 1
							? true
							: false;
					document.getElementById("PZ").checked = record.PZ == 1
							? true
							: false;
					document.getElementById("QT2").checked = record.QT == 1
							? true
							: false;
					// this.isModify = true;
					var JKCFRecords = record.JKCFRecords;
					if (JKCFRecords && JKCFRecords.length > 0) {
						// var jkjyHtml = this.createAllJKJYHTML(JKCFRecords);
						// document.getElementById("div_JKJY").innerHTML =
						// jkjyHtml;
//						var content = Ext.getCmp("JKJY").getValue();
//						content = content + this.createAllJKJYHTML(JKCFRecords);
//						Ext.getCmp("JKJY").setValue(content);
					}
				}
			},
			createAllJKJYHTML : function(JKCFRecords) {
				this.JKCFRecords = {};
				var content = "";
				for (var i = 0; i < JKCFRecords.length; i++) {
					var r = JKCFRecords[i];
					var healthTeach = r.healthTeach;
					var diagnoseId = parseInt(r.diagnoseId);
					this.JKCFRecords[diagnoseId] = r;
					content += healthTeach
				}
				return content;
			},
			clearMedicalInfo : function() {
				// modify by yangl 病历清空功能调整为清空所有就诊信息
				Ext.Msg.show({
							title : '警告',
							msg : '确认要清空病人的病历信息吗?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.doRemoveBcjl();
								}
							},
							scope : this
						});
			},
			doRemoveBcjl : function() {
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "removeBcjl",
							body : {
								"brid" : this.exContext.ids.brid,
								"clinicId" : this.exContext.ids.clinicId
							}
						}, function(code, msg, json) {
							if (code < 300) {
								Ext.getCmp("ZSXX").setValue("")
								Ext.getCmp("XBS").setValue("")
								Ext.getCmp("JWS").setValue("")
								Ext.getCmp("TGJC").setValue("")
								Ext.getCmp("FZJC").setValue("")
								document.getElementById("DPY").checked = false;
								document.getElementById("T").value = "";
								document.getElementById("R").value = "";
								document.getElementById("P").value = "";
								document.getElementById("H").value = "";
								document.getElementById("W").value = "";
								document.getElementById("BMI").value = "";
								document.getElementById("SSY").value = "";
								document.getElementById("SZY").value = "";
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			/**
			 * 一次请求载入病历信息
			 * 
			 * @param {}
			 *            type 1:病历 2：诊断 3：处方处置 5：所有 isModifyMI
			 *            从诊疗模板中调入时会有该值，当有填写病历模板时为true，没有填写病历模板时为false
			 */
			initClinicRecord : function(type, isModifyMI) {
				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "loadClinicInfo",
							body : {
								"brid" : this.exContext.ids.brid,
								"clinicId" : this.exContext.ids.clinicId,
								"type" : type
							}
						}, function(code, msg, json) {
							if (code < 300) {
								if (type == "1" || type == "5") {
									var bcjl = json.ms_bcjl;
									this.setMedicalInfo(bcjl);
								}
								if (type == "2" || type == "5") {
									var brzds = json.ms_brzd;
									document.getElementById("clinicDiv").innerHTML = this
											.getDiagnosisHtml(brzds);
									this.renderDiagInput(brzds); // 在ClinicDiagnosisEasyInputModule.js中
									if (this.addNewDiagSign) {
										this.onAddClick();
										this.addNewDiagSign = false;
									}
								}
								if (type == "3" || type == "5") {
									var measures = json.measures;
									var disposal = json.disposal;
									document.getElementById("measuresDiv").innerHTML = this
											.getMeasuresHtml(measures, disposal);
								}
								this.isModify = false;
								if (isModifyMI) {
									this.isModify = true;
								}
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},
			onBeforeWinClose : function() {
				if (this.isChanged) {
					Ext.Msg.show({
								title : '保存提示',
								msg : '当前页面数据已经修改，是否保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										var module = this.createModule(
												"cliniclist", "CLINIC0300");
										module.doSave();
									} else {
										this.isChanged = false;
										this.getWin().hide();
									}
								},
								scope : this
							});
					return false;
				}
				return true;
			},
			doSave : function(noMess, isPause) {
				// add by yangl 诊断快捷输入
				this.doSaveDiagnosis();
				// if (this.mainApp.chisActive) {
				//
				// // 判断35岁病人必须测量血压
				// var ssy = document.getElementById("SSY").value.trim();//
				// 获得收缩压和舒张压
				// var szy = document.getElementById("SZY").value.trim();
				// var DPYValue = document.getElementById("DPY").checked;
				// if (this.mainApp.reg_departmentId!=this.YFS.YFS &&
				// this.exContext.ids.age >= 35 && !this.hasFCBPRecord
				// && !DPYValue) {
				// if (ssy == null || szy == null || ssy.length == 0
				// || ssy.length == 0) {
				// MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！", true);
				// return;
				// }
				// var me = this;
				// var rootNode = me.emrview.emrNavTree.getRootNode();
				// if (rootNode.findChild("id", "A")) {
				// var hyNode = rootNode.findChild("id", "A")
				// .findChild("id", "A02");
				// if (hyNode.attributes.ref) {
				// var ref = hyNode.attributes.ref;
				// var nodeStatus = me.emrview.treeNodeStatusMap[ref];
				// }
				// if (nodeStatus == 'create') {
				// MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！", true);
				// return;
				// }
				// }
				//
				// }
				// var body = {
				// empiId : this.exContext.ids.empiId,
				// constriction : ssy,
				// diastolic : szy,
				// isFZ : this.hasFCBPRecord,
				// age : this.exContext.ids.age,
				// op : "create"
				// };
				// util.rmi.jsonRequest({
				// serviceId : "chis.chisRecordFilter",
				// serviceAction : "saveHyperFCBP",
				// method : "execute",
				// body : body
				// }, function(code, msg, json) {
				// if (code > 300) {
				// MyMessageTip.msg("提示", "测量血压保存失败！", true);
				// }
				// });
				// }
				// 保存病历信息ms_bcjl
				this.data = {};
                //zhaojian 2017-11-14 门诊医生站增加症状的录入功能：数据库ms_bcjl表中增加字段ZZDM、ZZMC              
                if(document.getElementById("MZZZ")){
                    this.data.ZZDM=document.getElementById("MZZZ").value;
                    this.data.ZZMC=this.data.ZZDM==""?"":document.getElementById("MZZZ").options[document.getElementById("MZZZ").selectedIndex].text;
                }
                 if(document.getElementById("ybmc")){
                    this.data.YBDM=document.getElementById("ybmc").value;
                    if(this.data.YBDM==""){
                    	 this.data.YBDM=20;
                    }
                    this.data.YBMC=this.data.ZZDM==""?"":document.getElementById("ybmc").options[document.getElementById("ybmc").selectedIndex].text;
                }
                this.data.ZSXX = document.getElementById("ZSXX").value;
				this.data.XBS = document.getElementById("XBS").value;
				this.data.JWS = document.getElementById("JWS").value;
				this.data.TGJC = document.getElementById("TGJC").value;
				this.data.FZJC = document.getElementById("FZJC").value;
				this.data.BQGZ = document.getElementById("BQGZ").value;
				this.data.GMS = document.getElementById("GMS").value;
				this.data.JKJYNR = document.getElementById("JKJYNR").value;
				if (this.mainApp.reg_departmentId == this.zyks) {
					this.data.ZZ = document.getElementById("ZZ").value;
				}
				for (var i = 0; i < document.getElementsByName("RSBZ").length; i++) {
					var radio = document.getElementsByName("RSBZ")[i];
					if (radio.checked) {
						this.data.RSBZ = radio.value;
						break;
					}
				}
             for (var i = 0; i < document.getElementsByName("MSBZ").length; i++) {
					var radio = document.getElementsByName("MSBZ")[i];
					if (radio.checked) {
						this.data.MSBZ = radio.value;
						break;
					}
				}
				if(this.data.MSBZ==11&&this.data.YBDM!=20){
					MyMessageTip.msg("提示","当前门诊标志，与所选病种不符,请重新核对!",true);
					return;
				}
				if(this.data.MSBZ!=11&&this.data.YBDM==20){
					MyMessageTip.msg("提示","当前门诊标志，与所选病种不符,请重新核对!",true);
					return;
				}
				debugger
				if(this.data.MSBZ!=171&&(this.data.YBDM==9101||this.data.YBDM==9102||this.data.YBDM==9103||this.data.YBDM==9104||this.data.YBDM==9112||this.data.YBDM==9123)){
					MyMessageTip.msg("提示","当前门诊标志为门慢，所选病种为门特病种,请重新核对!",true);
					return;
				}
				this.data.DPY = document.getElementById("DPY").checked == true? 1:0;
				this.data.T = document.getElementById("T").value;
				this.data.R = document.getElementById("R").value;
				this.data.P = document.getElementById("P").value;
				this.data.H = document.getElementById("H").value;
				this.data.W = document.getElementById("W").value;
				this.data.BMI = document.getElementById("BMI").value;
				this.data.SSY = document.getElementById("SSY").value;
				this.data.SZY = document.getElementById("SZY").value;

				this.data.LEFTVISION = document.getElementById("LEFTVISION").value;
				this.data.RIGHTVISION = document.getElementById("RIGHTVISION").value;
				this.data.LEFTCORRECTEDVISION = document
						.getElementById("LEFTCORRECTEDVISION").value;
				this.data.RIGHTCORRECTEDVISION = document
						.getElementById("RIGHTCORRECTEDVISION").value;

				this.data.KS = document.getElementById("KS").checked==true?1:0;
				this.data.YT = document.getElementById("YT").checked==true?1:0;
				this.data.HXKN = document.getElementById("HXKN").checked==true?1:0;
				this.data.OT = document.getElementById("OT").checked==true?1:0;
				this.data.FT = document.getElementById("FT").checked==true?1:0;
				this.data.FX = document.getElementById("FX").checked == true?1:0;
				this.data.PZ = document.getElementById("PZ").checked == true?1:0;
				this.data.QT = document.getElementById("QT2").checked == true?1:0;
				this.data.JZXH = this.exContext.ids.clinicId;
				this.data.BRID = this.exContext.ids.brid;
				/**
				 * modified by gaof 2014-1-10 刷新病人信息后无法保存
				 */
				if (!this.data.JZXH) {
					this.data.JZXH = this.exContext.idss.clinicId;
				}
				if (!this.data.BRID) {
					this.data.BRID = this.exContext.idss.brid;
				}
				this.data.JKCF = this.JKCFRecords;

				// if (this.isOphthal) { // && !noMess
				// if ((this.data.LEFTVISION == '' || this.data.RIGHTVISION ==
				// '')
				// && (this.data.LEFTCORRECTEDVISION == '' ||
				// this.data.RIGHTCORRECTEDVISION == '')) {
				// Ext.Msg.alert("提示", "左右眼视力或者左右眼矫正视力必填！")
				// return false;
				// } else if ((this.data.LEFTVISION != '' &&
				// this.data.RIGHTVISION != '')
				// && (this.data.LEFTVISION < 0.3 && this.data.RIGHTVISION <
				// 0.3)
				// && (this.data.LEFTCORRECTEDVISION == '' ||
				// this.data.RIGHTCORRECTEDVISION == '')) {
				// Ext.Msg.alert("提示", "如果左右眼视力同时小于0.3,则需要输入左右眼矫正视力！")
				// return false;
				// }
				// }

				var length = 0;
//				for (var key in this.JKCFRecords) { // add by yangl 健康处方不保存多份
//					length++;
//					if (length == 1) {
//						if (this.JKCFRecords[key]) {
//							this.JKCFRecords[key].healthTeach = Ext
//									.getCmp("JKJY").getValue();
//						}
//					} else {
//						delete this.JKCFRecords[key];
//					}
//				}
//				var jkjyMustFill = this.mainApp.reg_departmentId != 3
//						&& this.mainApp.reg_departmentId != 16;// 健康教育必须填写
//				if (jkjyMustFill && this.mainApp.chisActive && !isPause) {
					//if (length == 0) {
						/*
						 * MyMessageTip.msg("提示", "健康教育处方必须填写！", true);
						 * Ext.MessageBox.confirm('提示','是否填写健康教育？',function(btn){
						 * if(btn=='yes'){ this.openCardLayoutWin(4); return
						 * false; }else{ return this.doSave2(noMess); } },this);
						 */
						//alert('健康教育处方必须填写！');
						//this.openCardLayoutWin(4);
						//return false;
					//} else {
//						return this.doSave2(noMess);
					//}
//				} else {
					return this.doSave2(noMess);
//				}
			},
			doSave2 : function(noMess) {
				/**
				 * modified by gaof 2014-1-10 end
				 */
				this.panel.el.mask("保存中...");
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicManageService",
							serviceAction : "saveMsBcjl",
							body : this.data
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				this.panel.el.unmask();
				if (code < 300) {
					this.isModify = false;
					if (noMess) {
						return true;
					}
					Ext.apply(this.data, json.body)
					// EMRView判断是否增加高血压疑似记录
					this.fireEvent("CMRSave", this.data);// add
					// by
					// CXR
					MyMessageTip.msg("提示", "病历信息保存成功!", true, 1);
					if (json.body) {
//						var JKCFRecords = json.body.JKCFRecords;
//						var content = this.createAllJKJYHTML(JKCFRecords);
						// document.getElementById("div_JKJY").innerHTML =
						// jkjyHtml;
//						Ext.getCmp("JKJY").setValue(content);
					}
				} else {
					this.processReturnMsg(code, msg);
				}
				return true;
			},
			setBMIDoc : function() {
				var H = document.getElementById("H").value;
				var W = document.getElementById("W").value;
				if (!H || !W || H == "" || W == "") {
					return;
				}
				if (W == 0) {
					MyMessageTip.msg("提示", "体重不能为0!", true);
					return;
				}
				if (H == 0) {
					MyMessageTip.msg("提示", "身高不能为0!", true);
					return;
				}
				var b = (W / (H * H / 10000)).toFixed(2)
				document.getElementById("BMI").value = b;
			},
			doClinicFinish : function(type) {
				var ssy = document.getElementById("SSY").value.trim();// 获得收缩压和舒张压
				var szy = document.getElementById("SZY").value.trim();
				var DPYValue = document.getElementById("DPY").checked;
//				if (this.mainApp.reg_departmentId != this.YFS.YFS
//						&& this.exContext.ids.age >= 35 && !this.hasFCBPRecord
//						&& !DPYValue) {
////					if (ssy == null || szy == null || ssy.length == 0
////							|| ssy.length == 0) {
////						MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！", true);
////						return;
////					}
//					var me = this;
//					var rootNode = me.emrview.emrNavTree.getRootNode();
//					if (rootNode.findChild("id", "A")) {
//						var hyNode = rootNode.findChild("id", "A").findChild(
//								"id", "A02");
//						if (hyNode.attributes.ref) {
//							var ref = hyNode.attributes.ref;
//							var nodeStatus = me.emrview.treeNodeStatusMap[ref];
//						}
//						if (nodeStatus == 'create') {
//							MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！！！", true);
//							return;
//						}
//					}
//				}
				/*****************add by lizhi 2017-11-22控制不进行首诊测压不允许暂挂或就诊结束。********************/
				if (this.exContext.ids.age >= 35){
					var result = util.rmi.miniJsonRequestSync({
							serviceId : "phis.clinicManageService",
							serviceAction : "queryMsBcjl",
							body : {
								JZXH : this.exContext.idss.clinicId,
								BRID : this.exContext.ids.brid
							}
						});
					if (result.code > 300) {
						alert("查询失败");
						return
					}else{
						var me = this;
						var rootNode = me.emrview.emrNavTree.getRootNode();
						if (rootNode.findChild("id", "A")) {
							var hyNode = rootNode.findChild("id", "A")
									.findChild("id", "A02");
							if (hyNode.attributes.ref) {
								var ref = hyNode.attributes.ref;
								var nodeStatus = null;
								if(me.emrview.treeNodeStatusMap) {
									nodeStatus = me.emrview.treeNodeStatusMap[ref];
								}
							}
							if (nodeStatus == 'create') {
								if(result.json.errorMsg){
									MyMessageTip.msg("提示", result.json.errorMsg, true);
									return;
								}
//								MyMessageTip.msg("提示", "病人超过35岁,必须测量血压！！！", true);
							}
//							else if(nodeStatus == 'read'){
//								if(result.json.MS_BCJL){
//									if(result.json.MS_BCJL.SSY==null || result.json.MS_BCJL.SSY==""
//										||result.json.MS_BCJL.SZY==null || result.json.MS_BCJL.SZY==""){
//										Ext.Msg.alert("提示", "请保存首诊测压！！！");
//										return;
//									}
//								}else{
//									Ext.Msg.alert("提示", "请保存首诊测压！！！");
//									return;
//								}
//							}
						}
					}
				}
				var param=phis.script.common.loadSystemParams({"privates" : ['CRBBGK']}).CRBBGK;
				if(this.needSaveCrb && param=="1"){//add by lizhi at 2018-01-24传染病填写传染病报告卡
					var _this = this;
					Ext.Msg.alert("提示", "传染病诊断未填写传染病报告卡,请先填写！",function(){_this.popBgkWin();});
					return;
				}
				var msg = type == 2 ? '确认要进行暂挂操作吗?' : '确认要结束就诊吗?';
				Ext.Msg.show({
							title : '提示',
							msg : msg,
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var isPause = false
									if (type == 2) {
										isPause = true; // 暂挂
									}
									var flg = this.doSave(true, isPause);
									if (!flg) {
										return false;
									}

									if (type == 2) {
										this.closeing = true
									}
									this.clinicFinish(type);
								}
							},
							scope : this
						});
			},
			popBgkWin : function(){
				this.midiModules["crbBgkModule"]=null;
				var crbBgkmodule= this.createModule("crbBgkModule", "phis.application.cic.CIC/CIC/CIC0403");
				this.crbModule = crbBgkmodule;
				Ext.apply(this.crbModule.exContext, this.exContext);
				crbBgkmodule.opener = this;
				var win = crbBgkmodule.getWin();
				win.add(crbBgkmodule.initPanel());
				win.setWidth(1000);
				win.setHeight(550);
				win.show();
				win.center();
				this.crbModule.doCreate();
			},
			clinicFinish : function(type) {
				// 结束就诊，暂时只实现更新就诊状态
				this.panel.el.mask("处理中...");
				if (this.isModify) {// 如果病历数据有修改，则调用保存
					this.doSave(true);
				}

				phis.script.rmi.jsonRequest({
							serviceId : "clinicManageService",
							serviceAction : "saveClinicFinish",
							body : {
								JZXH : this.exContext.idss.clinicId,// add by
								// zhouyl
								JZZT : type,
								BRID : this.exContext.ids.brid,
								BRQX : this.BRQX,
								JKJY : this.JKJY,
								GHLX : this.ghlx,
								SBXH : this.sbxh,
								GHKS : this.mainApp.reg_departmentId,
								YSDM : this.mainApp.uid,
								updatingDoctor : this.emrview.updatingDoctor
							}
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code < 300) {
								// 关闭EMRView
								if (!this.emrview.win.closeing) {
									this.emrview.win.close();
								}
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
				ymPrompt.doHandler('doClose', true);
			},
			doRPY : function() {
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取就诊信息！请检查数据是否正确.");
					return;
				}

				if (this.CSZ == '0') {
					MyMessageTip.msg("提示", '未启用双向转诊!请设置私有参数:QYSXZZ', true);
					return;
				}
				var RFList = this.createModule("RFList",
						"phis.application.cic.CIC/CIC/CIC23");
				RFList.exContext = this.exContext;
				var win = RFList.getWin();
				win.add(RFList.initPanel());
				win.show();
			},

			doEPY : function() {
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取检查申请信息！请检查数据是否正确.");
					return;
				}
				if (this.CSZ == '0') {
					return;
				}
				var EFList = this.createModule("EFList",
						"phis.application.cic.CIC/CIC/CIC27");
				EFList.exContext = this.exContext.empiData;
				var win = EFList.getWin();
				win.add(EFList.initPanel());
				win.show();
			},
			doHPY : function() {
				if (!this.exContext || !this.exContext.ids
						|| !this.exContext.ids["empiId"]
						|| !this.exContext.ids["clinicId"]) {
					Ext.Msg.alert("错误", "无法获取住院转诊信息！请检查数据是否正确.");
					return;
				}

				if (this.CSZ == '0') {
					MyMessageTip.msg("提示", '未启用双向转诊!请设置私有参数:QYSXZZ', true);
					return;
				}
				var HFList = this.createModule("HFList","phis.application.cic.CIC/CIC/CIC28");
				HFList.exContext = this.exContext.empiData;
				var win = HFList.getWin();
				win.add(HFList.initPanel());
				win.show();
			},

			getCurrOphthal : function() { // 是否是眼科
				var regDepartmentId = this.mainApp.reg_departmentId;
				var isOphthal = -1;
				var resultData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.ophthalService",
							serviceAction : "isOphthal",
							body : {
								regDepartmentId : regDepartmentId
							}
						});
				if (resultData.code == 200) {// 成功
					isOphthal = resultData.json.body['isOphthal'];
				} else {
					this.processReturnMsg(resultData.code, resultData.code);
				}
				return isOphthal;
			}
		});
