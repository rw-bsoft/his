$package("phis.application.ivc.script");

$import("phis.script.TableForm", "phis.script.SimpleList",
		"phis.script.ICCardField",
		"phis.application.pix.script.CombinationSelect",
		"phis.application.pix.script.ParentsQueryList",
		"phis.script.widgets.LookUpField");
// 参考页面EMPIDemographicInfoForm
phis.application.ivc.script.ClinicPhysicalTjForm = function(cfg) {
	phis.application.ivc.script.ClinicPhysicalTjForm.superclass.constructor.apply(
			this, [cfg]);
	this.queryServiceId = "empiService";
	this.queryServiceActioin = "advancedSearch";
	this.queryInfo = {};
};
Ext.extend(phis.application.ivc.script.ClinicPhysicalTjForm,
		phis.script.TableForm, {
	loadData : function(data) {
	   // this.doNew();
		this.initFormData(data);
	},
	doNew : function() {
		phis.application.ivc.script.ClinicPhysicalTjForm.superclass.doNew
				.call(this);
		this.serviceAction = "submitPerson";
		this.data = {};
		this.queryInfo = {};
		this.snap = {};
		this.queryBy = false;
		var form = this.form.getForm();
		var MZHM = form.findField("MZHM");
		MZHM.setDisabled(true);
		if (MZHM.getValue().trim() == "") {
			var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "empiService",
						serviceAction : "outPatientNumber"
					});
			if (ret.code > 300) {
				this
						.processReturnMsg(ret.code, ret.msg,
								this.doInvalid);
			} else {
				MZHM.setValue(ret.json.MZHM);
				var cardNo = form.findField("cardNo");
				if(!this.cardNoSet){
					var pdms = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "checkCardOrMZHM"
							// cardOrMZHM : data.cardOrMZHM
						});
					if (pdms.code > 300) {
//							this.processReturnMsg(pdms.code, r.msg, this.onBeforeSave);
//							return;
					} else {
						if (pdms.json.cardOrMZHM == 2) {
							cardNo.setValue(ret.json.MZHM);
							this.cardNoSet = true;
						}
					}
				}
				this.snap = this.getQueryInfoSnap();
			}
		}
	},focusFieldAfter:function(index,delay){// 回车
		var items = this.schema.items;
		var form = this.form.getForm();
		for(var i = index + 1; i < items.length; i ++){
			var next = items[i];
			var field = form.findField(next.id)
			if(field && !field.disabled){
				if(next.id=='personName' ||next.id=='sexCode' ||next.id=='age'){
					field.focus(false, delay || 200)
				return;
			   }
			}
		}
		var btns;
		if(this.showButtonOnTop && this.form.getTopToolbar()){
			btns = this.form.getTopToolbar().items
			if(btns){
				var n = btns.getCount()
				for(var i = 0; i < n; i ++){
					var btn = btns.item(i)
					if(btn.cmd == "save"){
						if(btn.rendered){
							btn.focus()
						}
						return;
					}
				}
			}
		}
		else{
			btns =  this.opener.panel.buttons;
			if(btns){
				var n = btns.length
				for(var i = 0; i < n; i ++){
					var btn = btns[i]
					if(btn.cmd == "save"){
						if(btn.rendered){
							btn.focus()
						}
						return;
					}
				}	
			}			
		}	
},
	initFormData : function(data) {
		if(data=="" || data ==null || data =="null"){
			return
		}
		Ext.apply(this.data, data);
		this.initDataId = this.data[this.schema.pkey];
		var form = this.form.getForm();
		var items = this.schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];;
			var f = form.findField(it.id);
			if (f) {
				var v = data[it.id];
				if (v != undefined) {
					if (f.getXType() == "checkbox") {
						var setValue = "";
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
							var c = it.checkValue.split(",");
							checkValue = c[0];
							unCheckValue = c[1];
							if (v == checkValue) {
								setValue = true;
							} else if (v == unCheckValue) {
								setValue = false;
							}
						}
						if (setValue == "") {
							if (v == 1) {
								setValue = true;
							} else {
								setValue = false;
							}
						}
						f.setValue(setValue);
					} else {
						if (it.dic && v !== "" && v === 0) {// add by yangl
							// 解决字典类型值为0(int)时无法设置的BUG
							v = "0";
						}
						f.setValue(v)
						if (it.dic && v != "0" && f.getValue() != v) {
							f.counter = 1;
							this.setValueAgain(f, v, it);
						}

					}
				}
				if (it.update == "false") {
					f.disable();
				}
			}
			this.setKeyReadOnly(true)
			this.focusFieldAfter(-1, 800)
		}
	 	form.findField("personName").focus(false, 100);
	},
	/**
	 * 重写父类doSave方法，将获取数据部分代码独立出来，方便独立调用
	 */
	doSave : function() { 
		var values = this.getFormData();
		if (!values) {
			return;
		}
		var show=this.pdTrue(values);// 条件判断
		if(!show){
			return;
		}
// this.fireEvent("onEmpiReturn");
		Ext.apply(this.data, values);
		this.saveToServer(values);
		this.opener.formData=values;
		this.opener.saveend=true;
	},pdTrue : function(data) {
		var cardNo=data["cardNo"];
		var MZHM=data["MZHM"];
		// k=parseInt(k);
		if(MZHM !=cardNo&&(!this.data.empiId||this.data.empiId==undefined)){
			Ext.Msg.alert("提示",  "卡号和门诊号码必须保持一致！");
			return false;
		}
		return true;
	},

	/**
	 * 保存数据到后台数据库，请求中增加参数serviceAction，指定调用服务中的哪个方法
	 * 
	 * @param {}
	 *            saveData 需要保存的数据
	 */
	saveToServer : function(saveData) {
		var saveRequest = this.getSaveRequest(saveData); // ** 获取保存条件数据
		if (!saveRequest) {
			return;
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			return;
		}
		this.saving = true;
		this.form.el.mask("正在保存数据...", "x-mask-loading");
		if (this.serviceAction == "updatePerson"){// update
			var EMPIID=this.empiId;
			this.open="update";
			this.updateEmpiid(saveData,saveRequest,EMPIID);// 更新EMPPID后台数据 挂号生成
		}else{
			this.open="creart";
			this.empiIdSave(saveData,saveRequest);// empiID获取
		}
		
	},
	empiIdSave : function(saveData,saveRequest) {
		var EMPIID="";
		var cardId="";
		phis.script.rmi.jsonRequest({
			serviceId : "clinicChargesProcessingService",	
			serviceAction : "savePhysicalMr",
			method : "execute",
			op : this.op,
			schema : this.entryName,
			body : saveRequest
		}, function(code, msg, json) {
			this.form.el.unmask()
			if (code > 300) {
			 	this.processReturnMsg(code, msg, this.saveToServer,
				 	[saveData], json.body);
				this.saving = false;
				return;
			}else{
				EMPIID=json.empiId;
				cardId=json.cardId;
				saveRequest.cardId=cardId;
				if(EMPIID=="" || EMPIID==null){
					MyMessageTip.msg("提示", "MPI获取失败!", true);
				}else{
					this.updateEmpiid(saveData,saveRequest,EMPIID);// 更新EMPPID后台数据
																	// 挂号生成
				}
			}
		}, this);
	},
	updateEmpiid : function(saveData,saveRequest,empiId) {
		phis.script.rmi.jsonRequest({ 
			serviceId : "clinicChargesProcessingService",	
			serviceAction : "savePhysical",
			method : "execute",
			op : this.open,
			schema : this.entryName,
			module : this._mId, // 增加module的id
			body : saveRequest,
			empiId : empiId
		}, function(code, msg, json) {
			this.form.el.unmask()
			if (code > 300) {
			 	this.processReturnMsg(code, msg, this.saveToServer,
				 	[saveData], json.body);
				this.saving = false;
				return;
			}
			var HTCS=json.HTCS;
			var CSMC=json.CSMC;
			if(HTCS==1 || HTCS=="1"){
				MyMessageTip.msg("提示",CSMC, true);
				this.saving = false;
				return;
			}
			this.fireEvent("onFormLoad2",true);
			Ext.apply(this.data, saveData);
			if (json.body) {
				this.initFormData(json.body);
				this.fireEvent("save", this.entryName, this.op, json,
						this.data);
			}
			this.op = "update";
			this.saving = false;
			MyMessageTip.msg("提示", "保存成功!", true);
		}, this);
	},

	// 判断时间是否合法
	isValidDate : function(day, month, year) {
		if (month == 2) {
			var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
			if (day > 29 || (day == 29 && !leap)) {
				return false;
			}
		}
		return true;
	}
	,
	onReady : function() {
		phis.application.ivc.script.ClinicPhysicalTjForm.superclass.onReady
		.call(this);
		this.addSearchEventListeners();
		var form = this.form.getForm();
		for (var i = 0; i < this.schema.items.length; i++) {
			var item = this.schema.items[i];
		
			if (item.display && item.display < 2) {
				continue;
			}
		
			var field = form.findField(item.id);
			if (field) {
				field.on("change", function(f) {
							if (f.getName() == "birthday") {
								var age = this.getAgeFromServer(f
										.getValue());
								// 设置婚姻状况状态
								this.onBirthdayChange(age);
							}
							if (f.getName() == "age") {
								this.onAgeChange(f.getValue());
							}
						}, this)
				if (item.id == "idCard") {
					field.on("blur", this.onIdCardBlur, this);
					field.editable = true;
					field.on("lookup", this.onLookupParent, this);
				}
				if (item.id == "birthday") {
					var body = this.getAgeFromServer(field.getValue());
					// 设置婚姻状况状态
					field.on("select", function(f) {
								if (f.getName() == "birthday") {
									var body = this.getAgeFromServer(f
											.getValue());
									// 设置婚姻状况状态
									this.onBirthdayChange(body);
								}
							}, this);
				}
			}
		}
		var MZHM = form.findField("MZHM");
		
		var cardNo = form.findField("cardNo");
		if (MZHM) {
			// MZHM.on("blur", this.onMZHMBlur, this);
			MZHM.on("lookup", function() {
						MZHM.setDisabled(true);
						if (MZHM.getValue().trim() == "") {
							var ret = phis.script.rmi
									.miniJsonRequestSync({
										serviceId : "empiService",
										serviceAction : "outPatientNumber"
									});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code,
										ret.msg, this.doInvalid);
							} else {
								MZHM.setValue(ret.json.MZHM);
								this.snap = this.getQueryInfoSnap();
							}
						}
					}, this)
			MZHM.on("clear", function() {
						MZHM.setValue();
						if (this.needsQuery()) {
							MZHM.setDisabled(false);
						} else {
							this.doNew();
							MZHM.setDisabled(false);
//							this.serviceAction = "submitPerson";
//							this.opener.serviceAction = "submitPerson";
							this.opener.empiId = null;
							this.opener.data = {};
							this.data = {};
							this.addSearchEventListeners();
							// MZHM.setValue(this.MZHM);
							// this.setMZHM(MZHM);
						}
					}, this)
			// MZHM.un("specialkey",this.onFieldSpecialkey,this)
			MZHM.on("specialkey", function(f, e) {
						var key = e.getKey()
						if (key == e.ENTER) {
							e.stopEvent();
							if (f.getValue() != "") {
								this.onMZHMFilled(MZHM);
							}
							if (f.validate()) {
								var field = form.findField("BRXZ");
								if (field) {
									field.focus();
								}
							}
						}
					}, this)
			var pdms = phis.script.rmi.miniJsonRequestSync({
						serviceId : "clinicChargesProcessingService",
						serviceAction : "checkCardOrMZHM"
						});
			if (pdms.code > 300) {
		// this.processReturnMsg(pdms.code, r.msg, this.onBeforeSave);
		// return;
			} else {
				if (pdms.json.cardOrMZHM == 2) {
					MZHM.on("change", function() {
						cardNo.setValue(MZHM.getValue());
					}, this)
				}
			}
		}
	},
	addSearchEventListeners : function() {
		var MZHM = this.form.getForm().findField("MZHM");
		var cardNo = this.form.getForm().findField("cardNo");
		var idCard = this.form.getForm().findField("idCard");
		var birthday = this.form.getForm().findField("birthday");
		var sexCode = this.form.getForm().findField("sexCode");
		var personName = this.form.getForm().findField("personName");
		cardNo.enable();
		MZHM.on("blur", this.onMZHMFilled, this);
		cardNo.on("blur", this.onCardNoFilled, this);
		idCard.on("blur", this.onIdCardFilled, this);
		birthday.on("blur", this.queryInfoFilled, this);
		sexCode.on("blur", this.queryInfoFilled, this);
		personName.on("blur", this.queryInfoFilled, this);
	},
	needsQuery : function() {
		if (this.serviceAction == "updatePerson") {
			return false;
		}

		var snap = this.getQueryInfoSnap();
		if (!this.snap) {
			return true;
		}
		if (snap["MZHM"] != this.snap["MZHM"]) {
			return true;
		}
		if (snap["idCard"] != ""
				&& snap["idCard"] != this.snap["idCard"]) {
			return true;
		}
		if (snap["cardNo"] != ""
				&& snap["cardNo"] != this.snap["cardNo"]) {
			return true;
		}
		if (snap["personName"] != this.snap["personName"]) {
			return true;
		}
		if ((snap["birthday"] + "") != (this.snap["birthday"] + "")) {
			return true
		}
		if (snap["sexCode"] != this.snap["sexCode"]) {
			return true;
		}
		return false;
	},
	onLookupParent : function() {
		var lookView = this.midiModules["lookView"];
		if (!lookView) {
			lookView = new phis.application.pix.script.ParentsQueryList(
					{
						width : 600,
						height : 300
					});
			lookView.on("idCardReturn", function(idcard) {
						this.queryInfo["idCard"] = idcard;
						var queryData = {};
						var cards = [];
						var card = {
							"certificateTypeCode" : "01",
							"certificateNo" : idcard
						};
						cards.push(card);
						queryData["certificates"] = cards;
						this.queryBy = "idCard";
						this.doQuery(queryData);
					}, this);
		}
		var win = lookView.getWin();
		win.setPosition(250, 100);
		win.show();
	},
	onIdCardBlur : function() {
		var field=this.form.getForm().findField("idCard");
	    this.onIdCardFilled(field);// 弹出信息框
		var idCard = field.getValue();
		if (!idCard) {
			return;
		}
		var cardNo =idCard;
		cardNo = cardNo.replace(/(^\s*)|(\s*$)/g, "");
		field.setValue(cardNo);
		if (!field.validate()) {
			return;
		}
		field.setValue(field.getValue().toUpperCase());
		var info = this.getInfo(cardNo);
		var sex = info[1];
		var birthday = info[0];
		if (birthday) {
			var birthdayField = this.form.getForm()
					.findField("birthday");
			birthdayField.setValue(birthday);
			birthdayField.disable();
		}
		if (sex) {
			var sexCodeField = this.form.getForm().findField("sexCode");
			if (sex == 1) {
				sexCodeField.setValue({
							key : sex,
							text : "男"
						});
			} else {
				sexCodeField.setValue({
							key : sex,
							text : "女"
						});
			}
			sexCodeField.disable();
		}
		var age = this.getAgeFromServer(birthday);
		this.onBirthdayChange(age);
	},checkIdcard : function(pId) {
		var arrVerifyCode = [1, 0, "x", 9, 8, 7, 6, 5, 4, 3, 2];
		var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
		var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
		if (pId.length != 15 && pId.length != 18) {
			return "身份证号共有 15 码或18位";
		}
		var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
				6)
				+ "19" + pId.slice(6, 16);
		if (!/^\d+$/.test(Ai)) {
			return "身份证除最后一位外，必须为数字！";
		}
		var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
				.slice(12, 14);
		var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
				.getMonth(), day = d.getDate(), now = Date.parseDate(
				this.mainApp.serverDate, "Y-m-d");
		if (year != yyyy || mon + 1 != mm || day != dd || d > now
				|| now.getFullYear() - year > 110
				|| !this.isValidDate(dd, mm, yyyy)) {
			return "身份证输入错误！";
		}
		for (var i = 0, ret = 0; i < 17; i++) {
			ret += Ai.charAt(i) * Wi[i];
		}
		Ai += arrVerifyCode[ret %= 11];
		return pId.length == 18 && pId.toLowerCase() != Ai
				? "身份证输入错误！"
				: Ai;
	},
	getInfo : function(id) {
		
		// 根据身份证取 省份,生日，性别
		id = this.checkIdcard(id);
		var fid = id.substring(0, 16), lid = id.substring(17);
		if (isNaN(fid) || (isNaN(lid) && (lid != "x"))) {
			return [];
		}
		var id = String(id), sex = id.slice(14, 17) % 2 ? "1" : "2";
		var birthday = new Date(id.slice(6, 10), id.slice(10, 12) - 1,
				id.slice(12, 14));
		return [birthday, sex];
	},
	onAgeChange : function() {
		var age = this.form.getForm().findField("age").getValue();
		if (!isNaN(parseInt(age))) {
			var birthday = new Date();
			age = parseInt(age);
			var year = birthday.getFullYear() - age;
			var birthdayField = this.form.getForm()
					.findField("birthday");
			birthdayField.setValue(year + "-01-01");
		}
		this.queryInfoFilled();
	},
	setBirthday : function() {
		var toDay = new Date();
		var Year=toDay.getFullYear();
		var birthdayField = this.form.getForm().findField("birthday").getValue();
		var birthday=birthdayField.getFullYear();
		var age=Year-birthday;
		if (!isNaN(parseInt(age))) {
			var file = this.form.getForm().findField("age");
			file.setValue(age);
		}
	},

	getAgeFromServer : function(birthday) {
		if (!birthday || birthday == "") {
			return 0;
		}
		var result = phis.script.rmi.miniJsonRequestSync({
					serviceId : "publicService",
					serviceAction : "personAge",
					body : {
						birthday : birthday
					}
				});

		var age = 0;
		var body = null;
		if (result.json.body) {
			body = result.json.body
		}
		return body;

	},

	onBirthdayChange : function(body) {
		var age = body.age;
		var ages = body.ages;
		if (age < 0) {
			return;
		}
		var maritalStatusCode = this.form.getForm()
				.findField("maritalStatusCode");
		var ageField = this.form.getForm().findField("age");
		ageField.setValue(ages);
		if (this.serviceAction == "updatePerson"
				&& maritalStatusCode.getValue()) {
			return;
		}

		if (age >= 28) {
			maritalStatusCode.setValue({
						key : "21",
						text : "初婚"
					})
		} else {
			maritalStatusCode.setValue({
						key : "10",
						text : "未婚"
					})
		}
		// // 设置职业相关
		// var workPlace = this.form.getForm().findField("workPlace");
		// var workCode = this.form.getForm().findField("workCode");
		// if (age < 16) {
		// workPlace.disable();
		// workCode.disable();
		// } else {
		// workPlace.enable();
		// workCode.enable();
		// }
	},

	// 名字、性别、生日填都填写后触发查询。
	queryInfoFilled : function() {
		var personName = this.form.getForm().findField("personName");
		var personNameValue = personName.getValue();
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
		if (this.queryInfo) {
			if (this.queryInfo["personName"] == personNameValue
					&& this.queryInfo["sexCode"] == sexCodeValue
					&& this.queryInfo["birthday"] == birthdayValue
							.toString()) {
			}else{
				this.queryInfo["personName"] = personNameValue;
				this.queryInfo["sexCode"] = sexCodeValue;
				this.queryInfo["birthday"] = birthdayValue.toString();
			}
		}

		var queryData = {};
		this.queryBy = "baseInfo";
		queryData["personName"] = personNameValue;
		queryData["sexCode"] = sexCodeValue;
		queryData["birthday"] = birthdayValue;
		this.doQuery(queryData);
	},

	doQuery : function(queryData) {
		if (!this.needsQuery()) {
			this.opener.save = false;
			return;
		}
		this.idCard = queryData.idCard;
		//queryData["queryBy"] = this.queryBy;
		this.queried = true;
		// 保存查询信息快照
		this.snap = this.getQueryInfoSnap();

		this.form.el.mask("正在查询数据...", "x-mask-loading");
		phis.script.rmi.jsonRequest({
			serviceId : this.queryServiceId,
			schema : "phis.application.cic.schemas.MPI_DemographicInfo_CIC",
			serviceAction : this.queryServiceActioin,
			body : queryData
		}, function(code, msg, json) {
			this.form.el.unmask();
			if (code == 403) {
				this.processReturnMsg(result.code, result.msg);
				this.opener.save = false;
				return;
			}
			if (code == 900) {
				if (json.body && json.body.length > 0) {
					var url = json.body[0]["url"];
					this.getWin().close();
					window.open(url);
					this.opener.save = false;
					return;
				}
			}
			if (code == 750) {
				nameField = this.form.getForm().findField("personName");
				Ext.Msg.alert("提示", "身份证号码与名字不匹配!");
				nameField.reset();
				this.opener.save = false;
				return;
			}
			var data = json["body"];
			if (!data || data.length == 0){
				if(this.validate()){
					this.hasQuery = true;
				}
				if(this.opener.save){
					this.opener.save = false;
					this.opener.onBeforeSave();
				}
				return;
			}
			this.dataSource = json.dataSource || "chis";
			if (data.length == 1) {
				// 如果数据是从pix服务器取得,只作为默认值填入。
				if (this.dataSource == "pix") {
					this.setDefaultData(data[0]);
					this.hasQuery = true;
					if(this.opener.save){
						this.opener.save = false;
						this.opener.onBeforeSave();
					}
					return;
				}
				var empiId = data[0]["empiId"];
				var score = data[0]["score"];
				if (score == 1.0) {
					this.serviceAction = "updatePerson"
//					this.fireEvent("gotEmpi", empiId)
					this.empiId=empiId;
					this.setDefaultData(data[0]);
					this.focusFieldAfter(-1, 0)
					this.hasQuery = true;
					if(this.opener.save){
						this.opener.save = false;
						this.opener.onBeforeSave();
					}
				} else {
					this.opener.save = false;
					this.hasQuery = true;
					this.showDataInSelectView(data)
				}
			} else {
				this.opener.save = false;
				this.hasQuery = true;
				this.showDataInSelectView(data)
			}
		}, this)// jsonRequest
	},

	showDataInSelectView : function(data) {
		var idCard = this.form.getForm().findField("idCard").getValue();
		var IdCardValidate = this.form.getForm().findField("idCard")
				.validate();
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var r = data[i];
			// 如果身份证填写了人没查询到结果，然后使用基本信息查询到的结果列表中，
			// 带有身份证号的记录将被过滤掉。
			if (this.queryBy == "baseInfo" && idCard.length > 0
					&& IdCardValidate) {
				if (r.idCard && r.idCard.length > 0) {
					continue;
				}
			}

			var record = new Ext.data.Record(r);
			records.push(record);
		}

		if (records.length == 0) {
			return;
		}

		var empiIdSelectView = this.midiModules["empiIdSelectView"];
		if (!empiIdSelectView) {
			var empiIdSelectView = new phis.application.pix.script.CombinationSelect(
					{
						entryName : this.entryName,
						autoLoadData : false,
						enableCnd : false,
						modal : true,
						title : "选择个人记录",
						width : 500,
						height : 300
					});
			empiIdSelectView.on("onSelect", function(r) {
				if (r.get("idCard") != '') {
					this.idCardInput = true;
				}
				if (this.dataSource == "pix") {
					this.setDefaultData(r.data);
					return;
				}
				this.serviceAction = "updatePerson";
				var empiId = r.get("empiId");
				if (!empiId) {
					empiId = r.get("mpiId");
				}
//				this.fireEvent("gotEmpi", empiId);
				this.empiId = empiId;
				this.setDefaultData(data);
				var empiField = this.form.getForm().findField("empiId");
				if (empiField) {
					empiField.setValue(empiId);
				}
			}, this);
		}
		empiIdSelectView.getWin().show();
		empiIdSelectView.setRecords(records);
	},
	setDefaultData : function(data) {
		var form = this.form.getForm()
		if (data.idCard) {
			form.findField("idCard").setDisabled(true);
			form.findField("age").setDisabled(true);
		}
		if (data.MZHM) {
			form.findField("MZHM").setDisabled(true);
			form.findField("cardNo").setValue(data.MZHM);
		}
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				var v = data[it.id]
				if (!v) {
					continue;
				}
				if (it.dic) {
					var text = data[it.id + "_text"]
					v = {
						key : v,
						text : text
					}
				}
				f.setValue(v)
			}
		}
		Ext.apply(this.data, data);
		// 从PIX获取数据后补需要再做查询
		this.snap = this.getQueryInfoSnap();
	},

	// 每次获取一次查询条件的快照与上次的保存的快照进行对比
	// 决定是否进行查询。
	getQueryInfoSnap : function() {
		var form = this.form.getForm();
		var snap = {};
		snap["MZHM"] = form.findField("MZHM").getValue();
		snap["cardNo"] = form.findField("cardNo").getValue();
		snap["idCard"] = form.findField("idCard").getValue();
		snap["personName"] = form.findField("personName").getValue();
		snap["sexCode"] = form.findField("sexCode").getValue();
		snap["birthday"] = form.findField("birthday").getValue();
		return snap;
	},

	showDataInSelectView : function(data) {
		var idCard = this.form.getForm().findField("idCard").getValue();
		var IdCardValidate = this.form.getForm().findField("idCard")
				.validate();
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var r = data[i];
			// 如果身份证填写了人没查询到结果，然后使用基本信息查询到的结果列表中，
			// 带有身份证号的记录将被过滤掉。
			if (this.queryBy == "baseInfo" && idCard.length > 0
					&& IdCardValidate) {
				if (r.idCard && r.idCard.length > 0) {
					continue;
				}
			}

			var record = new Ext.data.Record(r);
			records.push(record);
		}

		if (records.length == 0) {
			return;
		}

		var empiIdSelectView = this.midiModules["empiIdSelectView"];
		if (!empiIdSelectView) {
			var empiIdSelectView = new phis.application.pix.script.CombinationSelect(
					{
						entryName : this.entryName,
						autoLoadData : false,
						enableCnd : false,
						modal : true,
						title : "选择个人记录",
						width : 500,
						height : 300
					});
			empiIdSelectView.on("onSelect", function(r) {
				if (r.get("idCard") != '') {
					this.idCardInput = true;
				}
				this.setDefaultData(r.data);
				this.serviceAction = "updatePerson";
				var empiId = r.get("empiId");
				if (!empiId) {
					empiId = r.get("mpiId");
				}
				this.empiId=empiId;
//				this.fireEvent("gotEmpi", empiId);
//				this.setDefaultData(data[0]);
				var empiField = this.form.getForm().findField("empiId");
				if (empiField) {
					empiField.setValue(empiId);
				}
			}, this);
		}
		empiIdSelectView.getWin().show();
		empiIdSelectView.setRecords(records);
	},
	/**
	 * 获取页面数据
	 */
	getFormData : function() {
		var ac = util.Accredit;
		var form = this.form.getForm()
		if (!this.validate()) {
			return
		}
		if (!this.schema) {
			return
		}
		var values = {};
		var items = this.schema.items
		Ext.apply(this.data, this.exContext.empiData)
		if (items) {
			var n = items.length
			for (var i = 0; i < n; i++) {
				var it = items[i]
				if (this.op == "create" && !ac.canCreate(it.acValue)) {
					continue;
				}
				var v = this.data[it.id] // ** modify by yzh 2010-08-04
				if (v == undefined) {
					v = it.defaultValue
					if (it.type == "datetime" && this.op == "create") {//
						// update
						// by
						// caijy
						// 2013-3-21
						// for
						// 新增页面的时间动态生成
						v = Date.getServerDateTime();
					}
				}
				if (v != null && typeof v == "object") {
					v = v.key
				}
				var f = form.findField(it.id)
				if (f) {
					v = f.getValue()
					// add by caijy from checkbox
					if (f.getXType() == "checkbox") {
						var checkValue = 1;
						var unCheckValue = 0;
						if (it.checkValue && it.checkValue.indexOf(",") > -1) {
							var c = it.checkValue.split(",");
							checkValue = c[0];
							unCheckValue = c[1];
						}
						if (v == true) {
							v = checkValue;
						} else {
							v = unCheckValue;
						}
					}
					// add by huangpf
					if (f.getXType() == "treeField") {
						var rawVal = f.getRawValue();
						if (rawVal == null || rawVal == "")
							v = "";
					}
					if (f.getXType() == "datefield" && v != null && v != "") {
						v = v.format('Y-m-d');
					}
					// end
				}

				if (v == null || v === "") {
					if (!it.pkey && it["not-null"] && !it.ref) {
						Ext.Msg.alert("提示", it.alias + "不能为空")
						return;
					}
				}
				if (it.type && it.type == "int") {
					v = (v == "0" || v == "" || v == undefined)
							? 0
							: parseInt(v);
				}
				if(it.id=="photo"){
					values[it.id] = "";
				}else{
					values[it.id] = v;
				}
				
			}
		}
		return values;
	},ZzValue: function(n,value){
		// n 1为去空，二为数字验证
		var v="";
		if(value == null || value === ""){
			v=value
		}else if(n=="1"){// n 1为去空
			v=this.trimRight(value);
			v=this.trimLeft(v);
			// v=value;
		}else if(n=="2"){// n 2 正整数
			var re= /^[0-9]*[1-9][0-9]*$/;  
			if (!re.test(value) && value!="0"){
				v="false"
			}else{
				v=value
			}
		}else if(n=="3"){// 正浮点数
			var re= /^(([0-9]+\\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;  
			if (!re.test(value) && value!="0"){
				v="false"
			}else{
				v=value
			}
		}
		return v
	},// 去掉右边的空白
	 trimRight : function(s){ 
		if(s == null) return ""; 
		var whitespace = new String(" \t\n\r"); 
		var str = new String(s); 
		if (whitespace.indexOf(str.charAt(str.length-1)) != -1){ 
		var i = str.length - 1; 
		while (i >= 0 && whitespace.indexOf(str.charAt(i)) != -1){ 
		i--; 
		} 
		str = str.substring(0, i+1); 
		} 
		return str+""; 
		} ,
	// 去掉左边的空白
	 trimLeft : function(s){ 
		if(s == null) { 
		return ""; 
		} 
		var whitespace = new String(" \t\n\r"); 
		var str = new String(s); 
		if (whitespace.indexOf(str.charAt(0)) != -1) { 
		var j=0, i = str.length; 
		while (j < i && whitespace.indexOf(str.charAt(j)) != -1){ 
		j++; 
		} 
		str = str.substring(j, i); 
		} 
		return str+""; 
	},
	onMZHMFilled : function(field) {
		var value = field.getValue();
		if (value.trim().length == 0) {
			return;
		}
		if (this.queryInfo["MZHM"] == value) {
			return;
		}
		this.queryInfo["MZHM"] = value;
		var queryData = {};
		/*
		 * var cards = []; var card = { // "cardTypeCode" : "02", "MZHM" : value };
		 */
		// cards.push(card);
		queryData["MZHM"] = value;
		this.queryBy = "MZHM";
		this.doQuery(queryData);
	},
	onCardNoFilled : function(field) {
		var value = field.getValue();
		if (value.trim().length == 0) {
			return;
		}
		if (this.queryInfo["cardNo"] == value) {
			return;
		}
		this.queryInfo["cardNo"] = value;
		var queryData = {
			cardNo : value
		};
		// var cards = [];
		// var card = {
		// // "cardTypeCode" : "02",
		// "cardNo" : value
		// };
		// cards.push(card);
		// queryData["cards"] = cards;
		this.queryBy = "cardNo";
		this.doQuery(queryData);
	},

	onIdCardFilled : function(field) {
		if (this.serviceAction == "updatePerson") {
			return;
		}
		var idcard = this.form.getForm().findField("idCard");
		if (!idcard.validate()) {
			return;
		}
		var idCardNo = idcard.getValue();
		if (idCardNo.trim().length == 0) {
			return;
		}

		this.queryInfo["idCard"] = idCardNo
		var queryData = {};
		queryData["idCard"] = idCardNo;
		queryData["personName"] = this.form.getForm()
				.findField("personName").getValue();
		// var cards = [];
		// var card = {
		// "certificateTypeCode" : "01",
		// "certificateNo" : idCardNo
		// };
		// cards.push(card);
		// queryData["certificates"] = cards;
		this.queryBy = "idCard";
		this.doQuery(queryData);
	}
});