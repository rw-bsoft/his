$package("chis.application.dr.script")
$import("util.Accredit", "chis.script.util.helper.Helper","chis.script.ICCardField",
"util.widgets.LookUpField","chis.script.util.Vtype", "chis.script.BizTableFormView",
"chis.application.dr.script.DRMPIBaseSelect", "util.dictionary.DictionaryLoader",
"chis.script.util.widgets.MyMessageTip")

chis.application.dr.script.DRReferralsForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "提交",
				iconCls : "common_select"
			},{
				id : "reset",
				name : "重置",
				iconCls : "create"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}, {
				id : "printList",
				name : "打印转诊通知单",
				iconCls : "print"
			}];
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.dr.script.DRReferralsForm.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.person = {};
	this.cardQueryInfo = {};
	
	this.cardTypeCode = cfg.cardTypeCode;
	this.cardNo = cfg.cardNo;
	this.personName = cfg.personName;
	this.idCard = cfg.idCard;
	this.contactNo = cfg.contactNo;
	this.submitAgency = cfg.hospitalCode;
	
	this.payType = cfg.payType;
	this.brxz = cfg.brxz;
	this.emrNo = cfg.emrNo;
	this.hosNo = cfg.hosNo;
	this.reserveNo = null;
}

Ext.extend(chis.application.dr.script.DRReferralsForm, chis.script.BizTableFormView, {
	loadData : function() {
				chis.application.dr.script.DRReferralsForm.superclass.loadData
						.call(this);
		var Form = this.form.getForm();
		//Wangjl
		var centerUnit = this.mainApp.centerUnit;
		var empiData=this.exContext.empiData;
		debugger;
		if(this.mainApp.jobId=="phis.55"){
			Form.findField("idCard").setValue(empiData.idCard);
			Form.findField("personName").setValue(empiData.personName);
			Form.findField("emrNo").setValue(empiData.MZHM);
			Form.findField("cardNo").setValue(empiData.MZHM);
			Form.findField("birthday").setValue(empiData.birthday);
			Form.findField("contactNo").setValue(empiData.mobileNumber);
			Form.findField("businessType").setValue(empiData.phrId);
			Form.findField("sexCode").setValue({
									key : empiData.sexCode,
									text : empiData.sexCode_text
								});
			Form.findField("brxz").setValue({
									key : "2",
									text : "门诊"
								});
			Form.findField("payType").setValue({
									key : empiData.BRXZ,
									text : empiData.BRXZ==2000?"医疗保险":"自费"
								});
			Form.findField("submitAgency").setValue({
									key : this.mainApp.deptId,
									text : this.mainApp.dept
								});
		}
	},
	initPanel : function(sc) {
		this.form = chis.application.dr.script.DRReferralsForm.superclass.initPanel
				.call(this);
		var Form = this.form.getForm();
		var cardTypeCode = Form.findField("cardTypeCode");
		var cardNo = Form.findField("cardNo");
		var emrNo = Form.findField("emrNo");
		var payType = Form.findField("payType");
		var personName = Form.findField("personName");
		var businessType = Form.findField("businessType");
		var idCard = Form.findField("idCard");
		var sexCode = Form.findField("sexCode");
		var birthday = Form.findField("birthday");
		var contactNo = Form.findField("contactNo");
		var DiseaseDescription = Form.findField("DiseaseDescription");
		var announcements = Form.findField("announcements");
		var turnReason = Form.findField("turnReason");
		var hospitalCode = Form.findField("hospitalCode");
		var submitAgency = Form.findField("submitAgency");
		
		
		var ZZRQ = Form.findField("ZZRQ");//转诊日期
		var JZYS = Form.findField("JZYS");//接诊医生
		var JYZRKS = Form.findField("JYZRKS");//建议转入科室
		var JYJCXM = Form.findField("JYJCXM");//检验/检查项目
		var JYJCXMJG = Form.findField("JYJCXMJG");//检验/检查项目结果
		var KFCSZD = Form.findField("KFCSZD");//康复措施指导
		
		this.reserveNo = null;
		
		
		
		
		cardNo.on("blur", this.onCardNoFilled, this);
		cardTypeCode.on("select", this.onCardTypeCodeFilled,this);
		idCard.on("blur", this.setSexAndBirthday, this);
		idCard.on("blur", this.onIdCardFilled, this);
		birthday.on("blur", this.queryInfoFilled, this);
		birthday.on("blur", this.onBirthdayFilled, this);
		sexCode.on("blur", this.queryInfoFilled, this);
		personName.on("blur", this.queryInfoFilled, this);
		var mpiId = new Ext.form.Hidden({
					name : "mpiId"
				});
		var drData = new Ext.form.Hidden({
					name : "drData"
				});
		var mpiData = new Ext.form.Hidden({
					name : "mpiData"
				});

		DiseaseDescription.on("change", this.checkChineseChar, this);
		announcements.on("change", this.checkChineseChar, this);
		turnReason.on("change", this.checkChineseChar, this);
		JZYS.on("change", this.checkChineseChar, this);
		JYZRKS.on("change", this.checkChineseChar, this);
		JYJCXM.on("change", this.checkChineseChar, this);
		JYJCXMJG.on("change", this.checkChineseChar, this);
		KFCSZD.on("change", this.checkChineseChar, this);
		var today = this.getServerDate();
		birthday.setMaxValue(today);
		ZZRQ.setMaxValue(today);
		
		if(typeof(this.idCard)!="undefined" && this.idCard!="" && this.idCard!=null){
			idCard.setValue(this.idCard);
			idCard.fireEvent("blur",this.idCard,this);
		}
		
		if(typeof(this.cardTypeCode)!="undefined" && this.cardTypeCode!="" && this.cardTypeCode!=null){
			var cardTypeCodeDicName = {
		            		 id : "chis.dictionary.cardTypeCode"
		          		    };
			var klx=util.dictionary.DictionaryLoader.load(cardTypeCodeDicName);
			var cardTypeCodeDi = klx.wraper[this.cardTypeCode];
			var cardTypeCodeText=""
			if (cardTypeCodeDi) {
				cardTypeCodeText = cardTypeCodeDi.text;
			}
			cardTypeCode.setValue({
								key : this.cardTypeCode,
								text : cardTypeCodeText
							});
			if(typeof(this.cardNo)!="undefined" && this.cardNo!="" && this.cardNo!=null){
				cardNo.setValue(this.cardNo);
			}
		}
		if(typeof(this.personName)!="undefined" && this.personName!="" && this.personName!=null){
			/**
			 * JavaScript中有三个可以对字符串编码的函数，
			 * 分别是： escape,encodeURI,encodeURIComponent，相应3个解码函数：unescape,decodeURI,decodeURIComponent
			 * java中：Java.NET.URLEncoder和java.net.URLDecoder
			 */
			 personName.setValue(decodeURI(this.personName));
		}
		if(typeof(this.submitAgency)!="undefined" && this.submitAgency!="" && this.submitAgency!=null){
			var submitAgencyDicName = {
		            		 id : "chis.dictionary.communityCode"
		          		    };
			var klx=util.dictionary.DictionaryLoader.load(submitAgencyDicName);
			var submitAgencyDi = klx.wraper[this.submitAgency];
			var submitAgencyText="";
			if (submitAgencyDi) {
				submitAgencyText = submitAgencyDi.text;
			}
			submitAgency.setValue({
								key : this.submitAgency,
								text : submitAgencyText
							});
		}
		if(typeof(this.contactNo)!="undefined" && this.contactNo!="" && this.contactNo!=null){
			contactNo.setValue(this.contactNo);
		}
		
		if(typeof(this.brxz)!="undefined" && this.brxz!="" && this.brxz!=null){
			var brxzDicName = {
		            		 id : "chis.dictionary.patientProperties"
		          		    };
			var klx=util.dictionary.DictionaryLoader.load(brxzDicName);
			var brxzDi = klx.wraper[this.brxz];
			var brxzText="";
			if (brxzDi) {
				brxzText = brxzDi.text;
			}
			brxz.setValue({
								key : this.brxz,
								text : brxzText
							});
		}
		if(typeof(this.payType)!="undefined" && this.payType!="" && this.payType!=null){
			var payTypeDicName = {
		            		 id : "chis.dictionary.payMode"
		          		    };
			var klx=util.dictionary.DictionaryLoader.load(payTypeDicName);
			var payTypeDi = klx.wraper[this.payType];
			var payTypeText="";
			if (payTypeDi) {
				payTypeText = payTypeDi.text;
			}
			payType.setValue({
								key : this.payType,
								text : payTypeText
							});
		}
		if(typeof(this.emrNo)!="undefined" && this.emrNo!="" && this.emrNo!=null){
			emrNo.setValue(this.emrNo);
		}
		if(typeof(this.hosNo)!="undefined" && this.hosNo!="" && this.hosNo!=null){
			hosNo.setValue(this.hosNo);
		}
		this.form.add(mpiId);
		this.form.add(drData);
		this.form.add(mpiData);
		return this.form;
	},
	onBirthdayFilled : function() {
		var birthday = this.form.getForm().findField("birthday");
		var birthdayValue = birthday.getValue();
		if (!birthdayValue) {
			return;
		}
		var age = this.calculateAge(birthdayValue, new Date());
		this.age = age;
		var personName = this.form.getForm().findField("personName");
		if (age < 3) {
			personName.clearInvalid()
			this.personNameEmptry = true;
			personName.allowBlank = true;
		} else {
			if (personName.getValue().length == 0) {
				personName.markInvalid("必填字段");
			}
			personName.allowBlank = false;
			this.personNameEmptry = false;
		}
	},
	queryInfoFilled : function() {
		var personName = this.form.getForm().findField("personName");
		var personNameValue = personName.getValue();
		if (this.personNameEmptry || personNameValue.trim().length != 0) {
			personName.clearInvalid();
		} else {
			personName.markInvalid('必填字段')
		}
		if (personNameValue.trim().length == 0) {
			return;
		}

		var sexCode = this.form.getForm().findField("sexCode");
		var sexCodeValue = sexCode.getValue();
		if (sexCodeValue.trim().length == 0) {
			return;
		}
		var birthday = this.form.getForm().findField("birthday");
		var birthdayValue = birthday.getValue();
		if (!birthdayValue) {
			return;
		}

		var queryData = {};
		this.queryBy = "baseInfo";
		queryData["personName"] = personNameValue;
		queryData["sexCode"] = sexCodeValue;
		queryData["birthday"] = birthdayValue.format("Y-m-d\\TH:i:s\\Z");
		this.doQuery(queryData);
	},
	calculateAge : function(birthday, calculateDate) {//计算年龄
		var age = calculateDate.getFullYear() - birthday.getFullYear();
		calculateDate.setFullYear(birthday.getFullYear());
		if (calculateDate.getTime() - birthday.getTime() < 0) {
			age = age - 1;
		}
		return age;
	},
	onCardTypeCodeFilled : function(){
		var cardNo = this.form.getForm().findField("cardNo");
		this.onCardNoFilled(cardNo);
	},
	onCardNoFilled : function(field) {
		if(!field.validate()){
			return;
		}
		var form = this.form.getForm();
		var cardTypeCode = form.findField("cardTypeCode").getValue();
		var value = field.getValue();
		if(cardTypeCode.trim().length == 0 || value.trim().length == 0){
			return;
		}
		this.cardQueryInfo["cardNo"] = value;
		this.cardQueryInfo["cardTypeCode"] = cardTypeCode;
		var queryData = {};
		queryData["cardNo"] = value;
		queryData["cardTypeCode"] = cardTypeCode;
		this.doCardQuery(queryData);
	},
	setSexAndBirthday : function() {
		if (arguments == null || arguments.length == 0) {
			return;
		}
		var Form = this.form.getForm();
		var idCard = Form.findField("idCard");
		var sexCode = Form.findField("sexCode");
		var birthday = Form.findField("birthday");
		if (idCard.isValid()) {
			var idNum = idCard.getValue();
			if (idNum.length == 15) {
				var year = idNum.substring(6, 8);
				var month = idNum.substring(8, 10);
				var day = idNum.substring(10, 12);
				var sex = idNum.substring(12, 14);
				var bdate = new Date(year, month - 1, day);
				birthday.setValue(bdate);
				if (sex % 2 == 0) {
					sexCode.setValue({
								key : "2",
								text : "女"
							});
				} else {
					sexCode.setValue({
								key : "1",
								text : "男"
							});
				}
			} else if (idNum.length == 18) {
				var year = idNum.substring(6, 10);
				var month = idNum.substring(10, 12);
				var day = idNum.substring(12, 14);
				var sex = idNum.substring(14, 17);
				var bdate = new Date(year, month - 1, day);
				birthday.setValue(bdate);
				if (sex % 2 == 0) {
					sexCode.setValue({
								key : "2",
								text : "女"
							});
				} else {
					sexCode.setValue({
								key : "1",
								text : "男"
							});
				}
			}
		}
		this.disableAllNeed(sexCode, birthday);
		this.onBirthdayFilled();
	},
	onIdCardFilled : function(field) {
		if (this.serviceAction == "updatePerson") {
			return;
		}
		var Form = this.form.getForm();
		var idCard = Form.findField("idCard");
		if (!idCard.validate()) {
			return;
		}
		var idCardNo = idCard.getValue();
		if (idCardNo.trim().length == 0) {
			return;
		}
		
		var queryData = {};
		queryData["idCard"] = idCardNo;
		this.cardQueryInfo["idCard"] = idCardNo;
		var personName = this.form.getForm().findField("personName").getValue();
		if (personName.trim().length > 0) {
			queryData["personName"] = personName;
		}
		this.doQuery(queryData);
	},
	onIdCardFilledWithOutPop : function(field) {
		if (this.serviceAction == "updatePerson") {
			return;
		}
		var Form = this.form.getForm();
		var idCard = Form.findField("idCard");
		if (!idCard.validate()) {
			return;
		}
		var idCardNo = idCard.getValue();
		if (idCardNo.trim().length == 0) {
			return;
		}
		
		var queryData = {};
		queryData["idCard"] = idCardNo;
		this.cardQueryInfo["idCard"] = idCardNo;
		var personName = this.form.getForm().findField("personName").getValue();
		if (personName.trim().length > 0) {
			queryData["personName"] = personName;
		}
		this.doQueryData(queryData);
	},
	checkChineseChar : function() {
		if (arguments == null || arguments.length == 0) {
			return;
		}
		var currObj = arguments[0];
		var value = currObj.getValue();
		var name = currObj.name;
		var length = 0;
		var chinese = /[\u4e00-\u9fa5]/
		for (var i = 0; i < value.length; i++) {
			var ch = value.charAt(i);
			if (chinese.test(ch)) {
				length += 3;
			} else {
				length += 1;
			}
		}
		if ("DiseaseDescription" == name) {
			if (length > 500) {
				currObj
						.markInvalid("字符长度超出允许的范围[1(2)-500(250)]，1个汉字长度记为3；当前长度："
								+ length);
				return false;
			} else {
				currObj.clearInvalid();
				return true;
			}
		}else if ("announcements" == name) {
			if (length > 200) {
				currObj
						.markInvalid("字符长度超出允许的范围[1(2)-200(100)]，1个汉字长度记为3；当前长度："
								+ length);
				return false;
			} else {
				currObj.clearInvalid();
				return true;
			}
		} else if ("turnReason" == name) {
			if (length > 200) {
				currObj
						.markInvalid("字符长度超出允许的范围[1(2)-200(100)]，1个汉字长度记为3；当前长度："
								+ length);
				return false;
			} else {
				currObj.clearInvalid();
				return true;
			}
		} else {
			return false;
		}

	},
	doQuery : function(queryData) {
		this.queried = true;
		this.form.el.mask("正在查询数据...", "x-mask-loading");
		util.rmi.jsonRequest({
			method:"execute",
					serviceId : this.getMPIControllorId,
					serviceAction : this.getMPIAction,
					body : queryData
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code,	msg);
						return;
					} else {
						var data = json["body"];
//						this.fireEvent("queryFinished");
						if (!data || data.length == 0) {
							return;
						}
						this.person.personName = data[0].personName;
						this.person.sexCode = data[0].sexCode;
						this.person.birthday = data[0].birthday;
						this.idCardNumber = data[0].idCard;
						this.showDataInSelectView(data);
					}
				}, this);
	},
	doQueryData : function(queryData) {
		this.queried = true;
		this.form.el.mask("正在查询数据...", "x-mask-loading");
		util.rmi.jsonRequest({
			method:"execute",
					serviceId : this.getMPIControllorId,
					serviceAction : this.getMPIAction,
					body : queryData
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code,	msg);
						return;
					} else {
						var data = json["body"];
						if (!data || data.length == 0) {
							return;
						}
						var records = [];
						for (var i = 0; i < data.length; i++) {
							var r = data[i];
							var record = new Ext.data.Record(r);
							records.push(record);
						}
						this.setFormData(records[0]);
					}
				}, this);
	},
	doCardQuery : function(queryData){
		this.queried = true;
		this.form.el.mask("正在查询数据...", "x-mask-loading");
		util.rmi.jsonRequest({
			method:"execute",
			serviceId : this.getMPIControllorId,
			serviceAction : this.getMPIAction,
			body : queryData
		}, function(code, msg, json) {
			this.form.el.unmask();
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			} else {
				var data = json["body"];
//				this.fireEvent("cardQueryFinished");
				if (!data || data.length == 0) {
					return;
				}
				this.cardNo = data[0].cardNo;
				this.showDataInSelectView(data);
			}
		}, this);
	},
	showDataInSelectView : function(data) {
		if (data == null || data.length == 0) {
			return;
		}
		var drMPIBaseSelect = this.midiModules["drMPISelectView"];
		if (!drMPIBaseSelect) {
			var drMPIBaseSelect = new chis.application.dr.script.DRMPIBaseSelect({
						entryName : this.demographicInfo,
						autoLoadData : false,
						enableCnd : false,
						modal : true,
						title : "选择个人记录",
						width : 500,
						height : 300
					});
			drMPIBaseSelect.initPanel();
			drMPIBaseSelect.on("onSelect", function(r) {
						this.setFormData(r);
					}, this);
		}
		drMPIBaseSelect.getWin().show();
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var r = data[i];
			var record = new Ext.data.Record(r);
			records.push(record);
		}
		drMPIBaseSelect.setRecords(records);
	},
	setFormData : function() {
		if (arguments == null || arguments.length == 0) {
			return;
		}
		var regex = /^[\s]*$/
		var record = arguments[0];
		var Form = this.form.getForm();
		var cardTypeCode = Form.findField("cardTypeCode");
		var cardNo = Form.findField("cardNo");
		var personName = Form.findField("personName");
		var idCard = Form.findField("idCard");
		var sexCode = Form.findField("sexCode");
		var birthday = Form.findField("birthday");
		var hospitalCode = Form.findField("hospitalCode");
		var businessType = Form.findField("businessType");
//		var operator = Form.findField("operator");
		var departmentCode = Form.findField("departmentCode");
		var submitorDoctor = Form.findField("submitorDoctor");
		var submitAgency = Form.findField("submitAgency");
		var turnReason = Form.findField("turnReason");
		var DiseaseDescription = Form.findField("DiseaseDescription");
		var announcements = Form.findField("announcements");
		var mpiId = Form.findField("mpiId");
		var mpiData = Form.findField("mpiData");
		var applyInfo = Form.findField("applyInfo");
		var contactNo = Form.findField("contactNo");
		
		var brxz = Form.findField("brxz");
		var payType = Form.findField("payType");
		var emrNo = Form.findField("emrNo");
		var hosNo = Form.findField("hosNo");
		
		
		var ZZRQ = Form.findField("ZZRQ");//转诊日期
		var JZYS = Form.findField("JZYS");//接诊医生
		var JYZRKS = Form.findField("JYZRKS");//建议转入科室
		var JYJCXM = Form.findField("JYJCXM");//检验/检查项目
		var JYJCXMJG = Form.findField("JYJCXMJG");//检验/检查项目结果
		var KFCSZD = Form.findField("KFCSZD");//康复措施指导
		
		
		//alert(JSON.stringify(record.data));
		brxz.setValue({
					key : record.data.brxz,
					text : record.data.brxz_text
				});
		payType.setValue({
					key : record.data.payType,
					text : record.data.payType_text
				});
		emrNo.setValue(record.data.emrNo);
		hosNo.setValue(record.data.hosNo);
				
		if(!record.data.cardNo||record.data.cardNo==""){
			record.data.cardTypeCode="01";
			record.data.cardTypeCode_text="健康卡";
			record.data.cardNo=record.data.idCard;
		}
		cardTypeCode.setValue({
					key : record.data.cardTypeCode,
					text : record.data.cardTypeCode_text
				});
		cardNo.setValue(record.data.cardNo);
		personName.setValue(record.data.personName);
		idCard.setValue(record.data.idCard);
		sexCode.setValue({
					key : record.data.sexCode,
					text : record.data.sexCode_text
				});
		submitAgency.setValue({
					key : record.data.submitAgency,
					text : record.data.submitAgency_text
				});
		if (record.data.birthday) {
			var bdate = new Date(Date.parse(record.data.birthday.replace(/-/g, "/")));
			birthday.setValue(bdate);
		}
		DiseaseDescription.setValue(record.data.DiseaseDescription);
		announcements.setValue(record.data.announcements);
		turnReason.setValue(record.data.turnReason);
		mpiId.setValue(record.data.mpiId);
		mpiData.setValue(record.data);
		contactNo.setValue(record.data.contactNo);
		
		//转诊日期
		if (record.data.ZZRQ) {
			var bdate = new Date(Date.parse(record.data.ZZRQ.replace(/-/g, "/")));
			ZZRQ.setValue(bdate);
		}
		JZYS.setValue(record.data.JZYS);//接诊医生
		JYZRKS.setValue(record.data.JYZRKS);//建议转入科室
		JYJCXM.setValue(record.data.JYJCXM);//检验/检查项目
		JYJCXMJG.setValue(record.data.JYJCXMJG);//检验/检查项目结果
		KFCSZD.setValue(record.data.KFCSZD);//康复措施指导
		
		
		this.disableAllNeed(cardTypeCode, cardNo, mpiId, personName, idCard,
				sexCode, birthday);
				
		if (mpiId.getValue() != null && !regex.test(mpiId.getValue())) {
			this.form.el.mask("正在加载数据");
			util.rmi.jsonRequest({
				method:"execute",
						body : {
							mpiId : mpiId.getValue(),
							submitTime : this.getServerDate()
						},
						serviceId : this.serviceControllorId,
						serviceAction : "cndQuery",
						schema : this.entryName
					}, function(code, msg, json) {
						this.form.el.unmask();
						if (code == 200) {
							if (json.result[0] != null) {
								var recordStr = "医院名称："
										+ json.result[0].hospitalCode_text + "；";
								recordStr += "业务类型：" + json.result[0].businessType_text
										+ "；";
								recordStr += "转入科室：" + json.result[0].departmentCode_text
										+ "；";
								recordStr += "转诊日期：" + json.result[0].submitTime
										+ "；";
								applyInfo.setValue(recordStr);
								hospitalCode.setValue({
									key : json.result[0].hospitalCode,
									text : json.result[0].hospitalCode_text
								});
								businessType.setValue({
									key : json.result[0].businessType,
									text : json.result[0].businessType_text
								});
								departmentCode.setValue({
									key : json.result[0].departmentCode,
									text : json.result[0].departmentCode_text
								});
//								operator.setValue({
//									key : json.result[0].operator,
//									text : json.result[0].operator_text
//								});
								submitAgency.setValue({
									key : json.result[0].submitAgency,
									text : json.result[0].submitAgency_text
								});
								submitorDoctor.setValue({
									key : json.result[0].submitorDoctor,
									text : json.result[0].submitorDoctor_text
								});
								DiseaseDescription
										.setValue(json.result[0]["DiseaseDescription"]);
								announcements
										.setValue(json.result[0]["announcements"]);
								turnReason
										.setValue(json.result[0]["turnReason"]);
								//转诊日期
								if (json.result[0]["ZZRQ"]) {
									var bdate = new Date(Date.parse(json.result[0]["ZZRQ"].replace(/-/g, "/")));
									ZZRQ.setValue(bdate);
								}
								JZYS.setValue(json.result[0]["JZYS"]);//接诊医生
								JYZRKS.setValue(json.result[0]["JYZRKS"]);//建议转入科室
								JYJCXM.setValue(json.result[0]["JYJCXM"]);//检验/检查项目
								JYJCXMJG.setValue(json.result[0]["JYJCXMJG"]);//检验/检查项目结果
								KFCSZD.setValue(json.result[0]["KFCSZD"]);//康复措施指导
								this.isverify = json.result[0].isverify.key;
							} 
						} else {
							this.processReturnMsg(code, msg);
							return;
						}
					}, this);
		}
	},
	getServerDate : function() {
		var serverDate = this.mainApp.serverDate;
		return new Date(Date.parse(serverDate.replace(/-/g, "/")));
	},
	doSave : function() {
		var Form = this.form.getForm();
		var cardTypeCode = Form.findField("cardTypeCode");
		var cardNo = Form.findField("cardNo");
		var personName = Form.findField("personName");
		var idCard = Form.findField("idCard");
		var sexCode = Form.findField("sexCode");
		var birthday = Form.findField("birthday");
		var mpiId = Form.findField("mpiId");
		var hospitalCode = Form.findField("hospitalCode");
		var departmentCode = Form.findField("departmentCode");
		var submitorDoctor = Form.findField("submitorDoctor");
//		var operator=Form.findField("operator");
		var businessType=Form.findField("businessType");
		var DiseaseDescription = Form.findField("DiseaseDescription");
		var announcements = Form.findField("announcements");
		var turnReason = Form.findField("turnReason");
		var submitAgency=Form.findField("submitAgency");
		var drDataObj = Form.findField("drData");
		var mpiDataObj = Form.findField("mpiData");
		var applyInfo = Form.findField("applyInfo");
		var contactNo = Form.findField("contactNo");
		//add by lizhi 2017-11-27增加病人性质、支付方式、门诊号码、住院号码
		var brxz = Form.findField("brxz");
		var payType = Form.findField("payType");
		var emrNo = Form.findField("emrNo");
		var hosNo = Form.findField("hosNo");		
		
		var ZZRQ = Form.findField("ZZRQ");//转诊日期
		var JZYS = Form.findField("JZYS");//接诊医生
		var JYZRKS = Form.findField("JYZRKS");//建议转入科室
		var JYJCXM = Form.findField("JYJCXM");//检验/检查项目
		var JYJCXMJG = Form.findField("JYJCXMJG");//检验/检查项目结果
		var KFCSZD = Form.findField("KFCSZD");//康复措施指导
		
		var body = {};
		var drData = {};
		var mpiData = {};
		
		var regex = /^[\s]*$/
		if (regex.test(idCard.getValue()) && regex.test(cardNo.getValue())) {
			MyMessageTip.msg("提示", "卡号或身份证必填一项!", true);
			return;
		}
		if(personName.getValue()==null || personName.getValue()==""){
			MyMessageTip.msg("提示", "请输入姓名!", true);
			return;
		}
		if(hospitalCode.getValue()==null || hospitalCode.getValue()==""){
			MyMessageTip.msg("提示", "请选择转入医院!", true);
			return;
		}
		if(submitAgency.getValue()==null || submitAgency.getValue()==""){
			MyMessageTip.msg("提示", "请选择申请机构!", true);
			return;
		}
		body["reserveNo"] = (typeof(this.reserveNo)!="undefined" && this.reserveNo!=null)?this.reserveNo:"";
//		body["payType"] = (typeof(this.payType)!="undefined" && this.payType!=null)?this.payType:"";
//		body["brxz"] = (typeof(this.brxz)!="undefined" && this.brxz!=null)?this.brxz:"";
//		body["emrNo"] = (typeof(this.emrNo)!="undefined" && this.emrNo!=null)?this.emrNo:"";
//		body["hosNo"] = (typeof(this.hosNo)!="undefined" && this.hosNo!=null)?this.hosNo:"";
		body["brxz"] = brxz!=null?brxz.getValue():"";
		body["payType"] = payType!=null?payType.getValue():"";
		body["emrNo"] = emrNo!=null?emrNo.getValue():"";
		body["hosNo"] = hosNo!=null?hosNo.getValue():"";
		body["mpiId"] = mpiId.getValue();
		body["hospitalCode"] = hospitalCode.getValue();
		body["departmentCode"] = departmentCode!=null?departmentCode.getValue():"";
		body["businessType"] = businessType.getValue();
		body["DiseaseDescription"] = DiseaseDescription.getValue();
		body["announcements"] = announcements.getValue();
		body["turnReason"] = turnReason.getValue();
//		body["submitor"] = submitor.getRawValue();
//		body["operator"] = operator.getRawValue();
		body["submitorDoctor"] = submitorDoctor.getRawValue();
		body["submitAgency"] = submitAgency.getRawValue();
		body["submitTime"] = new Date();
		body["operationTime"] = this.getServerDate();
		
		body["ZZRQ"] = ZZRQ.getValue();//转诊日期
		body["JZYS"] = JZYS.getValue();//接诊医生
		body["JYZRKS"] = JYZRKS.getValue();//建议转入科室
		body["JYJCXM"] = JYJCXM.getValue();//检验/检查项目
		body["JYJCXMJG"] = JYJCXMJG.getValue();//检验/检查项目结果
		body["KFCSZD"] =KFCSZD.getValue();//康复措施指导
		
		if (mpiDataObj.value == null || mpiDataObj.value == "") {
			mpiData["mpiId"] = null;
			mpiData["cardTypeCode"] = cardTypeCode.getValue();
			mpiData["cardNo"] = cardNo.getValue();
			mpiData["personName"] = personName.getValue();
			mpiData["idCard"] = idCard.getValue();
			mpiData["sexCode"] = sexCode.getValue();
			mpiData["birthday"] = birthday.getValue();
			mpiData["contactNo"] = contactNo.getValue();
		} else {
			mpiData["mpiId"] = mpiDataObj.value.mpiId;
			mpiData["cardTypeCode"] = mpiDataObj.value.cardTypeCode;
			mpiData["cardNo"] = mpiDataObj.value.cardNo;
			mpiData["personName"] = mpiDataObj.value.personName;
			mpiData["idCard"] = mpiDataObj.value.idCard;
			mpiData["sexCode"] = mpiDataObj.value.sexCode;
			mpiData["birthday"] = mpiDataObj.value.birthday;
			mpiData["contactNo"] = mpiDataObj.value.contactNo;
		}
//		if (drDataObj.value == null) {
//			Ext.Msg.alert("提示", "请选择医院!");
//			return;
//		} else {
//			drData["recordId"] = drDataObj.value.recordId;
//			drData["hospitalCode"] = drDataObj.value.hospitalCode;
//			drData["departmentCode"] = drDataObj.value.departmentCode;
//			drData["department"] = drDataObj.value.department;
//			drData["workDate"] = drDataObj.value.workDate;
////			drData["submitorDoctor"] = drDataObj.value.submitorDoctor == null
////						? -1 : drDataObj.value.submitorDoctor;
//			drData["doctorId"] = drDataObj.value.doctorId;
//			drData["reserveLimit"] = drDataObj.value.reserveLimit;
//			drData["reservedCount"] = drDataObj.value.reservedCount;
//			drData["timeInterval"] = drDataObj.value.timeInterval;
//			drData["charge"] = drDataObj.value.charge;
//			drData["businessType"] = drDataObj.value.businessType;
//			drData["sourceRecordId"] = drDataObj.value.sourceRecordId;
//			body["submitTime"] = drDataObj.value.workDate;
//		}
		this.processSave(body, drData, mpiData);
	},
	processSave : function() {
		if (arguments == null || arguments.length == 0) {
			return;
		}
		var body = arguments[0];
		var drData = arguments[1];
		var mpiData = arguments[2];
		this.form.el.mask("正在转诊-预约处理");
		util.rmi.jsonRequest({
			method:"execute",
					body : {
						"body" : arguments[0],
						"drData" : arguments[1],
						"mpiData" : arguments[2],
						"schema" : arguments[3]
					},
					serviceId : this.serviceControllorId,
					serviceAction : this.saveAction
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code == 200) {
						MyMessageTip.msg("提示", "保存转诊记录成功!", true);
						if(json.JIESHOUSZSQ == null){
							return;
						}
						this.reserveNo = json.JIESHOUSZSQ.reserveNo;
						//this.doCancel();
					} else if (code == 500) {
						if(json.body == null){
							return;
						}
						var hospitalCodeDicName = {
		            		 id : "chis.dictionary.hospitalCode"
		          		    };
						var zryy=util.dictionary.DictionaryLoader.load(hospitalCodeDicName);
						var hospitalCodeDi = zryy.wraper[json.result[0]["hospitalCode"].text];
						var hospitalText=""
						if (hospitalCodeDi) {
							hospitalText = hospitalCodeDi.text;
						}
						
						var departmentText=""
						if(json.result[0]["departmentCode"]!=null){
							var departmentCodeDicName = {
			            		 id : "chis.dictionary.departmentCode"
			          		    };
							var zrks=util.dictionary.DictionaryLoader.load(departmentCodeDicName);
							var departmentCodeDi = zrks.wraper[json.result[0]["departmentCode"].text];
							if (departmentCodeDi) {
								departmentText = departmentCodeDi.text;
							}
						}
						
						var businessTypeText=""
						if(json.result[0]["businessType"]!=null){
							var businessTypeDicName = {
			            		 id : "chis.dictionary.businessType"
			          		    };
							var ywlx=util.dictionary.DictionaryLoader.load(businessTypeDicName);
							var businessTypeDi = ywlx.wraper[json.result[0]["businessType"].text];
							if (businessTypeDi) {
								businessTypeText = businessTypeDi.text;
							}
						}
						
						var recordStr = "<div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>转入医院：</span>"
								+ (json.result[0]["hospitalCode"] == null
										? ""
										: hospitalText)
								+ ";</div>";
//						recordStr += "<div><span style='color:rgb(255,0,0)'>转入科室：</span>"
//								+ (json.result[0]["departmentCode"] == null
//										? ""
//										: departmentText)
//								+ ";</div>";
//						recordStr += "<div><span style='color:rgb(255,0,0)'>业务类型：</span>"
//								+ (json.result[0]["businessType"] == null
//										? ""
//										: businessTypeText)
//								+ ";</div>";
//						recordStr += "<div><span style='color:rgb(255,0,0)'>操作人：</span>"
//								+ json.result[0]["operator"]
//								+ ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>申请医生：</span>"
								+ json.result[0]["submitorDoctor"]
								+ ";</div>";
							
						recordStr += "<div><span style='color:rgb(255,0,0)'>转出原因：</span>"
								+ json.result[0]["turnReason"] + ";</div>";
								
						recordStr += "<div><span style='color:rgb(255,0,0)'>病情描述：</span>"
								+ json.result[0]["DiseaseDescription"]
								+ ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>转诊注意事项：</span>"
								+ json.result[0]["announcements"] + ";</div>";
								+ (json.result[0]["submitorDoctor"] == null
										? ""
										: json.result[0]["submitorDoctor"].text)
								+ ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>接诊医生：</span>"
								+ json.result[0]["JZYS"] + ";</div>";	
						recordStr += "<div><span style='color:rgb(255,0,0)'>建议转入科室：</span>"
								+ json.result[0]["JYZRKS"] + ";</div>";	
						recordStr += "<div><span style='color:rgb(255,0,0)'>检验/检查项目：</span>"
								+ json.result[0]["JYJCXM"] + ";</div>";								
						recordStr += "<div><span style='color:rgb(255,0,0)'>检验/检查项目结果：</span>"
								+ json.result[0]["JYJCXMJG"] + ";</div>";								
						recordStr += "<div><span style='color:rgb(255,0,0)'>康复措施指导：</span>"
								+ json.result[0]["KFCSZD"] + ";</div>";
								
						recordStr += "</div>"
						Ext.Msg.show({
							title : msg,
							msg : "<div align='center' style='margin-bottom:5px;font-weight:bold '>是否覆盖原有记录?</div>"
									+ recordStr,
							buttons : Ext.MessageBox.YESNO,
							modal : true,
							multiline : false,
							width : this.msgBoxWidth,
							fn : function(btn, text) {
								if ("yes" == btn) {
									body["recordId"] = json.result[0]["recordId"];
									body["reserveNo"] = json.result[0]["reserveNo"];
									this.processUpdate(body,drData,mpiData);
									this.reserveNo = json.result[0]["reserveNo"];
								}
							},
							scope : this
						});
					}else {
						Ext.Msg.show({
						title:"错误提示",
						msg:msg	
						});
						return;
					}
				}, this);
	},
	processUpdate : function() {
		if (arguments == null || arguments.length == 0) {
			return;
		}
		util.rmi.jsonRequest({
			method:"execute",
					body : arguments[0],
					drData : arguments[1],
					mpiData : arguments[2],
					serviceId : this.serviceControllorId,
					serviceAction : this.updateAction
				}, function(code, msg, json) {
					if (code == 200) {
						MyMessageTip.msg("提示", "更新转诊记录成功!", true);
//						this.doCancel();
					} else {
						this.processReturnMsg(code, msg);
						return;
					}
				}, this);
	},
	onWinShow : function() {
		this.setButton();
		if (this.op == "create") {
			this.doCreate();
		}
		this.win.doLayout();
	},
	setButton : function() {
//		if (!this.form.getTopToolbar()) {
//			return;
//		}
//		var bts = this.form.getTopToolbar().items;
//		if (this.op == "read") {
//			bts.items[0].disable();
//		} else {
//			bts.items[0].enable();
//		}
	},
	doReset : function() {
		this.resetForm();
	},
	resetForm : function() {
		this.isverify = null;
		var Form = this.form.getForm();
		var cardTypeCode = Form.findField("cardTypeCode");
		var cardNo = Form.findField("cardNo");
		var personName = Form.findField("personName");
		var idCard = Form.findField("idCard");
		var sexCode = Form.findField("sexCode");
		var birthday = Form.findField("birthday");
		var mpiId = Form.findField("mpiId");
		var hospitalCode = Form.findField("hospitalCode");
		var departmentCode = Form.findField("departmentCode");
		var submitorDoctor = Form.findField("submitorDoctor");
//		var operator = Form.findField("operator");
		var DiseaseDescription = Form.findField("DiseaseDescription");
		var announcements = Form.findField("announcements");
		var turnReason = Form.findField("turnReason");
		var businessType = Form.findField("businessType");
//		var saveCmd = this.form.getTopToolbar().find("cmd", "save")[0];
//		this.enableAllNeed(saveCmd);
		var drDataObj = Form.findField("drData");
		var mpiDataObj = Form.findField("mpiData");
		var submitAgency = Form.findField("submitAgency");
//		var applyInfo = Form.findField("applyInfo");
		var contactNo = Form.findField("contactNo");
		
		var payType = Form.findField("payType");
		var emrNo = Form.findField("emrNo");
		var hosNo = Form.findField("hosNo");
		
		var ZZRQ = Form.findField("ZZRQ");//转诊日期
		var JZYS = Form.findField("JZYS");//接诊医生
		var JYZRKS = Form.findField("JYZRKS");//建议转入科室
		var JYJCXM = Form.findField("JYJCXM");//检验/检查项目
		var JYJCXMJG = Form.findField("JYJCXMJG");//检验/检查项目结果
		var KFCSZD = Form.findField("KFCSZD");//康复措施指导

		drDataObj.value = null;
		mpiDataObj.value = null;
		this.enableAllNeed(cardTypeCode, cardNo, personName, idCard, sexCode,
				birthday);
		cardNo.setValue("");
		personName.setValue("");
		idCard.setValue("");
		sexCode.setValue(-1);
		birthday.setValue("");
		mpiId.setValue("");
		this.reserveNo=null;
//		applyInfo.setValue("");
		hospitalCode.setValue(-1);
//		departmentCode.setValue(-1);
		submitorDoctor.setValue(-1);
//		operator.setValue(-1);
		businessType.setValue(-1);
		submitAgency.setValue(-1);
		DiseaseDescription.setValue("");
		announcements.setValue("");
		turnReason.setValue("");
		contactNo.setValue("");
		
		payType.setValue(-1);
		emrNo.setValue("");
		hosNo.setValue("");		
		ZZRQ.setValue("");//转诊日期
		JZYS.setValue("");//接诊医生
		JYZRKS.setValue("");//建议转入科室
		JYJCXM.setValue("");//检验/检查项目
		JYJCXMJG.setValue("");//检验/检查项目结果
		KFCSZD.setValue("");//康复措施指导
	},
	disableAllNeed : function() {
		if (arguments == null && arguments.length == 0) {
			return;
		}
		for (var i = 0; i < arguments.length; i++) {
			arguments[i].disable();
		}
	},
	enableAllNeed : function() {
		if (arguments == null && arguments.length == 0) {
			return;
		}
		for (var i = 0; i < arguments.length; i++) {
			if (arguments[i]) {
				arguments[i].enable();
			}
		}
	},
	setParent:function(parent){
		this.parent=parent;
	},
	doCancel:function(){
		this.doReset();
		var win = this.getWin();
		if(win)
			win.hide();
	},
	doPrintList:function(){
		var Form = this.form.getForm();
		var idCard = Form.findField("idCard");
		if(typeof(this.reserveNo)=="undefined" || this.reserveNo==null || this.reserveNo==""){
			MyMessageTip.msg("提示", "转诊单号查询失败，请先提交!", true);
			return;
		}
		var url = "resources/chis.prints.template.ReferralInfoFile.print?type=" + 1
						+ "&reserveNo=" + this.reserveNo;
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
//		var win = window.showModelessDialog(url,window,"dialogHeight="+ (screen.height - 100)+ 
//		", dialogWidth="+ (screen.width - 10)+ 
//		", dialogTop=0, dialogLeft=0, center=yes, scroll=yes, resizable=yes");
		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
		
//		var LODOP=getLodop();
//		LODOP.PRINT_INIT("打印控件");
//		LODOP.SET_PRINT_PAGESIZE("0","","","");
//		LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
//		LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
//		//预览
////		LODOP.PREVIEW();
//		//直接打印
//		LODOP.PRINT();
	}
})