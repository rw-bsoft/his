$package("chis.application.tr.script.phq")

$import("chis.script.BizTableFormView","chis.script.util.Vtype","chis.script.ICCardField")

chis.application.tr.script.phq.TumourHealthQuestionManyGeneralCaseForm = function(cfg){
	cfg.autoLoadSchema = false;
	cfg.autoLoadData = false;
	cfg.colCount=4;
	cfg.labelWidth = 80;
	cfg.autoFieldWidth = false;
	cfg.fldDefaultWidth = 120;
	chis.application.tr.script.phq.TumourHealthQuestionManyGeneralCaseForm.superclass.constructor.apply(this,[cfg]);
	this.queryInfo = {};
	this.queryServiceId = "chis.empiService";
	this.queryServiceActioin = "advancedSearch";
}

Ext.extend(chis.application.tr.script.phq.TumourHealthQuestionManyGeneralCaseForm,chis.script.BizTableFormView,{
	loadData:function(){
	},
	doSave: function(){
		Ext.apply(this.data,this.exContext.args);
		var values = this.getFormData();
		if(!values){
			return;
		}
		Ext.apply(this.data,values);
		this.data.gcId=this.initDataId;
		this.fireEvent("save",this.data);
	},
	onReady : function(){
		var frm = this.form.getForm();
		var surveyUserFld = frm.findField("surveyUser");
		if(surveyUserFld){
			surveyUserFld.on("select", this.changeSurveyUnit, this);
		}
//		var highRiskTypeFld = frm.findField("highRiskType");
//		if (highRiskTypeFld) {
//			var items = highRiskTypeFld.items;
//			for (var i = 0, len = items.length; i < len; i++) {
//				var box = items[i];
//				box.listeners = {
//					'check' : function(checkedBox, checked) {
//						this.fireEvent("hrtBoxCheck",checkedBox,checked);
//					},
//					scope : this
//				}
//			}
//		}
		chis.application.tr.script.phq.TumourHealthQuestionManyGeneralCaseForm.superclass.onReady.call(this);
		this.fireEvent("readyAfter")
	},
	changeSurveyUnit : function(combo, node) {
		if (!node.attributes['key']) {
			return
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getManageUnit",
					method:"execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				});
		this.setSurveyUnit(result.json.manageUnit)
	},
	setSurveyUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("surveyUnit");
		if (!combox) {
			return;
		}
		if (!manageUnit) {
			combox.enable();
			combox.setValue({
						key : "",
						text : ""
					});
			return;
		}
		combox.setValue(manageUnit)
		combox.disable();
	},
	setHighRiskTypeControl : function(hrtValue){
		var highRiskTypeFld = this.form.getForm().findField("highRiskType");
		if (highRiskTypeFld) {
			var items = highRiskTypeFld.items.items;
			for (var i = 0, len = items.length; i < len; i++) {
				var box = items[i];
				if(hrtValue.indexOf(box.inputValue) != -1){
					box.enable();
				}else{
					box.disable();
				}
			}
		}
	},
	//==============**个人基本信息查询**=============
	doCreate : function(){
		this.initDataId = null;
		this.queryInfo = {};
		this.doNew();
		this.addSearchEventListeners()
	},
	addSearchEventListeners : function() {
		var cardNo = this.form.getForm().findField("cardNo");
		var idCard = this.form.getForm().findField("idCard");
		var birthday = this.form.getForm().findField("birthday");
		var sexCode = this.form.getForm().findField("sexCode");
		var personName = this.form.getForm().findField("personName");
		cardNo.enable();
		cardNo.on("blur", this.onCardNoFilled, this);
		idCard.enable();
		idCard.on("blur", this.onIdCardFilled, this);
		birthday.enable();
		birthday.on("blur", this.queryInfoFilled, this);
		sexCode.enable();
		sexCode.on("blur", this.queryInfoFilled, this);
		personName.enable();
		personName.on("blur", this.queryInfoFilled, this);
	},
	removeSearchEventListeners : function() {
		var cardNo = this.form.getForm().findField("cardNo");
		var idCard = this.form.getForm().findField("idCard");
		var birthday = this.form.getForm().findField("birthday");
		var sexCode = this.form.getForm().findField("sexCode");
		var personName = this.form.getForm().findField("personName");
		cardNo.removeListener("blur", this.onCardNoFilled, this);
		cardNo.disable();
		idCard.removeListener("blur", this.onIdCardFilled, this);
		idCard.disable();
		birthday.removeListener("blur", this.queryInfoFilled, this);
		birthday.disable();
		sexCode.removeListener("blur", this.queryInfoFilled, this);
		sexCode.disable();
		personName.removeListener("blur", this.queryInfoFilled, this);
		personName.disable();
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
		queryData["personName"] = this.form.getForm().findField("personName")
				.getValue();
		this.queryBy = "idCard";
		this.doQuery(queryData);
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
					&& this.queryInfo["birthday"] == birthdayValue.toString()) {
				return;
			}
			this.queryInfo["personName"] = personNameValue;
			this.queryInfo["sexCode"] = sexCodeValue;
			this.queryInfo["birthday"] = birthdayValue.toString();
		}

		var queryData = {};
		this.queryBy = "baseInfo";
		queryData["personName"] = personNameValue;
		queryData["sexCode"] = sexCodeValue;
		queryData["birthday"] = birthdayValue;
		this.doQuery(queryData);
	},
	// 判断是否需要进行查询,EMPIInfoModule调用
	needsQuery : function() {
		if (this.serviceAction == "updatePerson") {
			return false;
		}
		var snap = this.getQueryInfoSnap();
		if (!this.snap) {
			return true;
		}
		if (snap["idCard"] != "" && snap["idCard"] != this.snap["idCard"]) {
			return true;
		}
		if (snap["cardNo"] != "" && snap["cardNo"] != this.snap["cardNo"]) {
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
	getQueryInfoSnap : function() {
		var form = this.form.getForm();
		var snap = {};
		snap["cardNo"] = form.findField("cardNo").getValue();
		snap["idCard"] = form.findField("idCard").getValue();
		snap["personName"] = form.findField("personName").getValue();
		snap["sexCode"] = form.findField("sexCode").getValue();
		snap["birthday"] = form.findField("birthday").getValue();
		return snap;
	},
	doQuery : function(queryData) {
//		alert(this.needsQuery()+"==============")
//		if (!this.needsQuery()) {
//			return;
//		}
		this.idCard = queryData.idCard;
		queryData["queryBy"] = this.queryBy;
		this.queried = true;
		// 保存查询信息快照
		//this.snap = this.getQueryInfoSnap();
		this.form.el.mask("正在查询数据...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.queryServiceId,
					schema : "chis.application.mpi.schemas.MPI_DemographicInfo",
					serviceAction : this.queryServiceActioin,
					method:"execute",
					body : queryData
				}, function(code, msg, json) {
					this.form.el.unmask();
					if (code == 403) {
						this.processReturnMsg(result.code, result.msg);
						return;
					}
					if (code == 900) {
						if (json.body && json.body.length > 0) {
							var url = json.body[0]["url"];
							this.getWin().close();
							window.open(url);
							return;
						}
					}
					if (code == 750) {
						nameField = this.form.getForm().findField("personName");
						Ext.Msg.alert("提示", "身份证号码与名字不匹配!");
						nameField.reset();
						return;
					}
					var data = json["body"];
					if (!data || data.length == 0){
						this.fireEvent("gotEmpi", '','')
						return;
					}
					this.dataSource = json.dataSource || "chis";
					if (data.length == 1) {
						// 如果数据是从pix服务器取得,只作为默认值填入。
						if (this.dataSource == "pix") {
							this.setDefaultData(data[0]);
							this.cards = data[0].cards;
							return;
						}
						var empiId = data[0]["empiId"];
						var sexCode = data[0]["sexCode"];
						this.cards = data[0].cards;
						var score = data[0]["score"];
						if (score == 1.0) {
							this.serviceAction = "updatePerson"
							this.fireEvent("gotEmpi", empiId,sexCode)
							this.focusFieldAfter(-1, 0)
						} else {
							this.showDataInSelectView(data)
						}
					} else {
						this.showDataInSelectView(data)
					}
				}, this)// jsonRequest
	},
	setDefaultData : function(data) {
		var form = this.form.getForm()
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
	showDataInSelectView : function(data) {
		var idCard = this.form.getForm().findField("idCard").getValue();
		var IdCardValidate = this.form.getForm().findField("idCard").validate();
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
			$import("chis.application.mpi.script.CombinationSelect");
			var empiIdSelectView = new chis.application.mpi.script.CombinationSelect({
						entryName : this.entryName,
						autoLoadData : false,
						enableCnd : false,
						modal : true,
						title : "选择个人记录",
						width : 500,
						height : 300
					});
					
			empiIdSelectView.on("onSelect", function(r) {
						var empiId = r.get("empiId");
						var sexCode = r.get("sexCode");
						this.fireEvent("gotEmpi", empiId,sexCode);
//						this.cards = r.data.cards;
//						if (r.get("idCard") != '') {
//							this.idCardInput = true;
//						}
//						if (this.dataSource == "pix") {
//							this.setDefaultData(r.data);==========
//							return;
//						}
//						this.serviceAction = "updatePerson";
//						var empiId = r.get("empiId");
//						this.fireEvent("gotEmpi", empiId);
//						var empiField = this.form.getForm().findField("empiId");
//						if (empiField) {
//							empiField.setValue(empiId);
//						}
					}, this);
		}
		empiIdSelectView.getWin().show();
		empiIdSelectView.setRecords(records);
	}
});