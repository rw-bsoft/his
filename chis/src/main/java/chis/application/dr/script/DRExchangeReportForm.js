$package("chis.application.dr.script")
$import("util.Accredit", "chis.script.util.helper.Helper","chis.script.ICCardField",
"util.widgets.LookUpField","chis.script.util.Vtype", "chis.script.BizTableFormView",
"chis.application.dr.script.DRMPIBaseSelect", "util.dictionary.DictionaryLoader",
"chis.script.util.widgets.MyMessageTip")

chis.application.dr.script.DRExchangeReportForm = function(cfg) {
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
	chis.application.dr.script.DRExchangeReportForm.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.person = {};
	this.cardQueryInfo = {};
	
	this.cardTypeCode = cfg.cardTypeCode;
	this.cardNo = cfg.cardNo;
	this.personName = cfg.personName;
	this.idCard = cfg.idCard;
	this.contactNo = cfg.contactNo;
	//this.reserveNo = cfg.reserveNo;
	
	this.payType = cfg.payType;
	this.brxz = cfg.brxz;
	this.emrNo = cfg.emrNo;
	this.hosNo = cfg.hosNo;
}

Ext.extend(chis.application.dr.script.DRExchangeReportForm, chis.script.BizTableFormView, {
	initPanel : function(sc) {
		this.form = chis.application.dr.script.DRExchangeReportForm.superclass.initPanel
				.call(this);
		var Form = this.form.getForm();
		var cardTypeCode = Form.findField("cardTypeCode");
		var cardNo = Form.findField("cardNo");
		var personName = Form.findField("personName");
		var idCard = Form.findField("idCard");
		var sexCode = Form.findField("sexCode");
		var birthday = Form.findField("birthday");
		var contactNo = Form.findField("contactNo");
		var treatResult = Form.findField("treatResult");
		var healthAdvice = Form.findField("healthAdvice");
		var leaveConclusion = Form.findField("leaveConclusion");
		//var reserveNo = Form.findField("reserveNo");
		var hospitalCode = Form.findField("hospitalCode");

		cardNo.on("blur", this.onCardNoFilled, this);
		cardTypeCode.on("select", this.onCardTypeCodeFilled,this);
		idCard.on("blur", this.setSexAndBirthday, this);
		idCard.on("blur", this.onIdCardFilled, this);
		birthday.on("blur", this.queryInfoFilled, this);
		birthday.on("blur", this.onBirthdayFilled, this);
		sexCode.on("blur", this.queryInfoFilled, this);
		personName.on("blur", this.queryInfoFilled, this);
		//reserveNo.on("blur", this.onReserveNoFilled, this);
		
		var mpiId = new Ext.form.Hidden({
					name : "mpiId"
				});
		var drData = new Ext.form.Hidden({
					name : "drData"
				});
		var mpiData = new Ext.form.Hidden({
					name : "mpiData"
				});

		treatResult.on("change", this.checkChineseChar, this);
		healthAdvice.on("change", this.checkChineseChar, this);
		leaveConclusion.on("change", this.checkChineseChar, this);
		var today = this.getServerDate();
		birthday.setMaxValue(today);
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
		if(typeof(this.contactNo)!="undefined" && this.contactNo!="" && this.contactNo!=null){
			contactNo.setValue(this.contactNo);
		}
		if(typeof(this.reserveNo)!="undefined" && this.reserveNo!="" && this.reserveNo!=null){
			reserveNo.setValue(this.reserveNo);
			this.onReserveNoFilledWithOutPop();
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
		queryData["birthday"] = birthdayValue==""?"":birthdayValue.format("Y-m-d\\TH:i:s\\Z");
		//queryData["reserveNo"] = reserveNoValue;
		this.doQuery(queryData);
	}, 
	onReserveNoFilled : function() {
		var reserveNo = this.form.getForm().findField("reserveNo");
		var reserveNoValue = reserveNo.getValue();
		if (!reserveNoValue) {
			return;
		}
		var queryData = {};
		this.queryBy = "baseInfo";
		queryData["reserveNo"] = reserveNoValue;
		this.doQuery(queryData);
	},
	onReserveNoFilledWithOutPop : function() {
		var reserveNo = this.form.getForm().findField("reserveNo");
		var reserveNoValue = reserveNo.getValue();
		if (!reserveNoValue) {
			return;
		}
		var queryData = {};
		this.queryBy = "baseInfo";
		queryData["reserveNo"] = reserveNoValue;
		this.doQueryData(queryData);
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
		if ("treatResult" == name) {
			if (length > 500) {
				currObj
						.markInvalid("字符长度超出允许的范围[1(2)-500(250)]，1个汉字长度记为3；当前长度："
								+ length);
				return false;
			} else {
				currObj.clearInvalid();
				return true;
			}
		}else if ("healthAdvice" == name) {
			if (length > 200) {
				currObj
						.markInvalid("字符长度超出允许的范围[1(2)-200(100)]，1个汉字长度记为3；当前长度："
								+ length);
				return false;
			} else {
				currObj.clearInvalid();
				return true;
			}
		} else if ("leaveConclusion" == name) {
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
			serviceId : this.serviceControllorId,
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
			serviceId : this.serviceControllorId,
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
			serviceId : this.serviceControllorId,
			serviceAction : this.getMPIAction,
			body : queryData
		}, function(code, msg, json) {
			this.form.el.unmask();
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			} else {
				var data = json["body"];
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
		var doctorName = Form.findField("doctorName");
		var submitor = Form.findField("submitor");
		var treatResult = Form.findField("treatResult");
		var healthAdvice = Form.findField("healthAdvice");
		var leaveConclusion = Form.findField("leaveConclusion");
		var reserveNo = Form.findField("reserveNo");
		var mpiId = Form.findField("mpiId");
		var mpiData = Form.findField("mpiData");
		var contactNo = Form.findField("contactNo");
		
		var brxz = Form.findField("brxz");
		var payType = Form.findField("payType");
		var emrNo = Form.findField("emrNo");
		var hosNo = Form.findField("hosNo");
		
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
		
		if(!record.data.cardNo||record.data.cardNo==""||record.data.cardNo==record.data.idCard){
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
		if (record.data.birthday) {
			var bdate = new Date(Date.parse(record.data.birthday.replace(/-/g, "/")));
			birthday.setValue(bdate);
		}
		leaveConclusion.setValue(record.data.leaveConclusion);
		healthAdvice.setValue(record.data.healthAdvice);
		treatResult.setValue(record.data.treatResult);
		mpiId.setValue(record.data.mpiId);
		mpiData.setValue(record.data);
		contactNo.setValue(record.data.contactNo);
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
							if (typeof(json.result[0])!="undefined" && json.result[0] != null) {
								var recordStr = "医院名称："
										+ json.result[0].hospitalCode_text + "；";
								recordStr += "转诊日期：" + json.result[0].submitTime
										+ "；";
								hospitalCode.setValue({
									key : json.result[0].hospitalCode,
									text : json.result[0].hospitalCode_text
								});
								doctorName.setValue({
									key : json.result[0].doctorName,
									text : json.result[0].doctorName_text
								});
								submitor.setValue({
									key : json.result[0].submitor,
									text : json.result[0].submitor_text
								});
								leaveConclusion
										.setValue(json.result[0]["leaveConclusion"]);
								healthAdvice
										.setValue(json.result[0]["healthAdvice"]);
								treatResult
										.setValue(json.result[0]["treatResult"]);
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
		var submitor = Form.findField("submitor");
		var doctorName=Form.findField("doctorName");
		var treatResult = Form.findField("treatResult");
		var healthAdvice = Form.findField("healthAdvice");
		var leaveConclusion = Form.findField("leaveConclusion");
		var reserveNo = Form.findField("reserveNo");
		var submitAgency=Form.findField("submitAgency");
		var drDataObj = Form.findField("drData");
		var mpiDataObj = Form.findField("mpiData");
		var contactNo = Form.findField("contactNo");
		//add by lizhi 2017-11-27增加病人性质、支付方式、门诊号码、住院号码
		var brxz = Form.findField("brxz");
		var payType = Form.findField("payType");
		var emrNo = Form.findField("emrNo");
		var hosNo = Form.findField("hosNo");
		var body = {};
		var drData = {};
		var mpiData = {};
		
		var regex = /^[\s]*$/
		if (regex.test(idCard.getValue()) && regex.test(cardNo.getValue())) {
			Ext.Msg.alert("提示", "卡号或身份证必填一项!");
			Ext.Msg.show({
				title : this.msgBoxTitle,
				msg : "<span align='center' style='color:red;font-weight:bold'><center>卡号或身份证必填一项!</center></span>",
				width : this.msgBoxWidth,
				modal : true,
				buttons : Ext.MessageBox.OK
			})
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
		body["treatResult"] = treatResult.getValue();
		body["healthAdvice"] = healthAdvice.getValue();
		body["leaveConclusion"] = leaveConclusion.getValue();
		body["doctorName"] = doctorName.getRawValue();
		body["submitor"] = submitor.getRawValue();
		body["submitAgency"] = submitAgency.getRawValue();
		body["submitTime"] = new Date();
		body["exchangeTime"] = this.getServerDate();
		if (mpiDataObj.value == null) {
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
//						this.doCancel();
					} else if (code == 500) {
						if(json.body == null){
							return;
						}
						
						var recordStr = "<div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>转入医院：</span>"
								+ (json.result[0]["hospitalCode"] == null
										? ""
										: json.result[0]["hospitalCode"].text)
								+ ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>提交人：</span>"
								+ (json.result[0]["submitor"] == null
										? ""
										: json.result[0]["submitor"].key)
								+ ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>治疗结果：</span>"
								+ json.result[0]["treatResult"] + ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>建议康复内容：</span>"
								+ json.result[0]["healthAdvice"]
								+ ";</div>";
						recordStr += "<div><span style='color:rgb(255,0,0)'>出院小结：</span>"
								+ json.result[0]["leaveConclusion"] + ";</div>";
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
		var submitor = Form.findField("submitor");
		var doctorName = Form.findField("doctorName");
		var treatResult = Form.findField("treatResult");
		var healthAdvice = Form.findField("healthAdvice");
		var leaveConclusion = Form.findField("leaveConclusion");
		var drDataObj = Form.findField("drData");
		var mpiDataObj = Form.findField("mpiData");
		var submitAgency = Form.findField("submitAgency");

		var payType = Form.findField("payType");
		var emrNo = Form.findField("emrNo");
		var hosNo = Form.findField("hosNo");
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
		hospitalCode.setValue(-1);
		submitor.setValue(-1);
		doctorName.setValue(-1);
		submitAgency.setValue(-1);
		treatResult.setValue("");
		healthAdvice.setValue("");
		leaveConclusion.setValue("");
		this.reserveNo = null;
		
		payType.setValue(-1);
		emrNo.setValue("");
		hosNo.setValue("");
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
		var url = "resources/chis.prints.template.ReferralInfoReportFile.print?type=" + 1
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
//		//LODOP.PREVIEW();
//		//直接打印
//		LODOP.PRINT();
	}
})