$package("phis.application.cic.script");

$import("phis.script.TableForm", "phis.script.util.DateUtil","phis.script.util.helper.Helper");

phis.application.cic.script.IdrReportListView = function(cfg) {
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	phis.application.cic.script.IdrReportListView.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(phis.application.cic.script.IdrReportListView, phis.script.TableForm, {
	onReady : function() {
		phis.application.cic.script.IdrReportListView.superclass.onReady
				.call(this);
		var form = this.form.getForm();
		var patientJob = form.findField("patientJob");
		if (patientJob) {
			patientJob.on("select", this.onSelectPatientJob, this);
		}
		var categoryAInfectious = form.findField("categoryAInfectious")
		this.categoryAInfectious = categoryAInfectious
		if (categoryAInfectious) {
			categoryAInfectious.on("select",
					this.checkCategoryInfectious, this);
		}
		var categoryBInfectious = form.findField("categoryBInfectious");
		this.categoryBInfectious = categoryBInfectious
		if (categoryBInfectious) {
			categoryBInfectious.on("select",
					this.onSelectCategoryBInfectious, this);
		}
		var categoryBInfectious2 = form.findField("phthisis");
		this.categoryBInfectious2 = categoryBInfectious2
		if (categoryBInfectious2) {
			categoryBInfectious2.on("select",
					this.onSelectCategoryBInfectious2, this);
		}
		var categoryCInfectious = form.findField("categoryCInfectious")
		this.categoryCInfectious = categoryCInfectious
		if (categoryCInfectious) {
			categoryCInfectious.on("select",
					this.checkCategoryInfectious, this)
		}
		var viralHepatitis = form.findField("viralHepatitis")
		this.viralHepatitis = viralHepatitis
		if (viralHepatitis) {
			viralHepatitis.on("select",
					this.onSelectViralHepatitis, this)
		}
		var otherCategoryInfectious = form
				.findField("otherCategoryInfectious")
		this.otherCategoryInfectious = otherCategoryInfectious
		if (otherCategoryInfectious) {
			otherCategoryInfectious.on("keyup",
					this.checkCategoryInfectious, this)
			otherCategoryInfectious.on("blur",
					this.checkCategoryInfectious, this)
		}
		// 加上延迟,防止太快执行,参数没有完全加载
		var d = new Ext.util.DelayedTask(function(){
			var fillDate = form.findField("fillDate");
			if (fillDate) {
				fillDate.on("select",
						this.onSelectFillDate, this);
				fillDate.on("change",
						this.onSelectFillDate, this);
			}
		},this);
		d.delay(1000);
	},
	onSelectPatientJob : function(item) {
		if(item.getValue()==17||item.getValue()==4||item.getValue()==9||item.getValue()==16||item.getValue()==22||item.getValue()==3||item.getValue()==1){
	    var workPlace = this.form.getForm().findField("workPlace")
					workPlace.allowBlank=false
		}else{
		var workPlace = this.form.getForm().findField("workPlace")
					workPlace.allowBlank=true
		}
		/*传染病人群分类为“其它”时不能为空；其它具体职业最长15个字。*/
		if (item.getValue() == 29) {
			this.changeFieldState(false, "otherPatientJob");
		} else {
			this.changeFieldState(true, "otherPatientJob");
		}
	},
	checkCategoryInfectious : function() {
		if (this.categoryAInfectious.getValue() == ""
				&& this.categoryBInfectious.getValue() == ""
				&& this.categoryCInfectious.getValue() == ""
				&& this.otherCategoryInfectious.getValue() == "") {
			this.categoryAInfectious.allowBlank = false
			Ext.getCmp(this.categoryAInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("<span style='color:red'>" + "甲类传染病"
							+ ":</span>");
			this.categoryBInfectious.allowBlank = false
			Ext.getCmp(this.categoryBInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("<span style='color:red'>" + "乙类传染病"
							+ ":</span>");
			this.categoryCInfectious.allowBlank = false
			Ext.getCmp(this.categoryCInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("<span style='color:red'>" + "丙类传染病"
							+ ":</span>");
			this.otherCategoryInfectious.allowBlank = false
			Ext.getCmp(this.otherCategoryInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("<span style='color:red'>" + "其他传染病"
							+ ":</span>");
		} else {
			this.categoryAInfectious.allowBlank = true
			Ext.getCmp(this.categoryAInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("甲类传染病:");
			this.categoryBInfectious.allowBlank = true
			Ext.getCmp(this.categoryBInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("乙类传染病:");
			this.categoryCInfectious.allowBlank = true
			Ext.getCmp(this.categoryCInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("丙类传染病:");
			this.otherCategoryInfectious.allowBlank = true
			Ext.getCmp(this.otherCategoryInfectious.id).getEl()
					.up('.x-form-item').child('.x-form-item-label')
					.update("其他传染病:");
		}
		this.validate()
	},
	onSelectCategoryBInfectious : function(item) {	
		if (item.getValue().indexOf("3100")>=0) {
			var casemixCategory2 = this.form.getForm().findField("casemixCategory2");
			casemixCategory2.setValue({
						key : "",
						text : ""
					});
		}
		  if (item.getValue() == "3100"||(item.getValue() =='0300'||this.exContext.empiData.viralHepatitis=='0301')) {
		  	var casemixCategory2 = this.form.getForm().findField("casemixCategory2");
               casemixCategory2.setValue({
                  key : "",
                   text : ""
                   });
         }
		this.checkCategoryInfectious();
		var categoryBInfectious = arguments[5]
				? arguments[5].categoryBInfectious.key
				: arguments[0].getValue();
		var has3, has11, has12, has13, has14, has22, has25 = false;
		if (categoryBInfectious) {
			var adressArray = categoryBInfectious.split(",");
			for (var i = 0; i < adressArray.length; i++) {
				if (adressArray[i] == "0300") { // 如果病毒性肝炎
					has3 = true;
				} else if (adressArray[i] == "1900") {// 炭疽
					has11 = true;
				} else if (adressArray[i] == "0400") {// 痢疾
					has12 = true;
				} else if (adressArray[i] == "2600") {// 肺结核
					has13 = true;
				} else if (adressArray[i] == "0500") {// 伤寒
					has14 = true;
				} else if (adressArray[i] == "0800") {// 梅毒
					has22 = true;
				} else if (adressArray[i] == "2300") {// 疟疾
					has25 = true;
				}

			}
		}
		if (has3) {
			this.changeFieldState(false, "viralHepatitis");
		} else {
			this.changeFieldState(true, "viralHepatitis");
		}
		if (has11) {
			this.changeFieldState(false, "anthrax");
		} else {
			this.changeFieldState(true, "anthrax");
		}
		if (has12) {
			this.changeFieldState(false, "dysentery");
		} else {
			this.changeFieldState(true, "dysentery");
		}
		if (has13) {
			this.changeFieldState(false, "phthisis");
		} else {
			this.changeFieldState(true, "phthisis");
		}
		if (has14) {
			this.changeFieldState(false, "typhia");
		} else {
			this.changeFieldState(true, "typhia");
		}

		if (has22) {
			this.changeFieldState(false, "syphilis");
		} else {
			this.changeFieldState(true, "syphilis");
		}
		if (has25) {
			this.changeFieldState(false, "malaria");
		} else {
			this.changeFieldState(true, "malaria");
		}

	},
	onSelectCategoryBInfectious2 :function(item) {
		  if (item.getValue() == "2601"||item.getValue() == "2602"||item.getValue() == "2601,2602") {
           var casemixCategory1 = this.form.getForm().findField("casemixCategory1");
               casemixCategory1.setValue({
                  key : "2",
                   text : "确诊病例"
                   });
         }else{
         	var casemixCategory1 = this.form.getForm().findField("casemixCategory1");
               casemixCategory1.setValue({
                  key : "",
                   text : ""
                   });
         }
	},
	onSelectFillDate : function(field, date) {
		//传染病要求：必填，报告日期(填卡日期)与出生日期差值，计算周岁，按自然日计算。不足1周岁，按1周岁计算。
		var form = this.form.getForm();
		var birthday = form.findField("birthday").getValue();
		if(typeof birthday=="string"){
			birthday=Date.parseDate(birthday,'Y-m-d H:i:s');
		}
		if(typeof date=="string"){
			date=Date.parseDate(date,'Y-m-d H:i:s');
		}
		var diffTime = chis.script.util.helper.Helper
				.getAgeBetween(birthday,	date);
		if(diffTime.indexOf("0岁")==0){
			diffTime = "1岁";
		}
		var fullAge = form.findField("fullAge");
		fullAge.setValue(diffTime);
	},
	onWinShow : function() {
		this.win.doLayout();
	},
	setButton : function() {
		if (!this.getTopToolbar()) {
			return;
		}
		//var bts = this.getTopToolbar().items;
		var btns = this.form.topToolbar;
		if (this.op == "read") {
			bts.items[0].disable();
		} else {
			bts.items[0].enable();
		}
	},

	doCreate : function(){
				this.initDataId = null;
				this.doNew();
				if(!this.form.data){
					this.form.data={};
				}
				this.op = "create";
			},
	
	
	doVerify : function(){
		debugger
		var r = this.opener.getSelectedRecord();
		
		if (r == null) {
			return;
		}
		//if(r.data && r.data.finishStatus!=""&&r.data.finishStatus!="0"){
			//Ext.Msg.alert("提示", "该记录已审核！");
			//return;
	//	}
	if(r.data.reportFlag=="1" && r.data.finishStatus=="1"){
			Ext.Msg.alert("提示", "该记录已上报！");
			return;
		}
		var m = this.getIdrReportVerifyForm(r);
		m.opener = this;
		var win = m.getWin();
//		win.add(m.initPanel());
		win.setPosition(250, 100);
		win.show();
		var formData = this.castListDataToForm(r.data, this.schema);
		m.initFormData(formData);
	},
	getIdrReportVerifyForm : function(r) {
		var m = this.midiModules["idrReportVerifyForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(this.reportVerifyForm);
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
			m.on("save", this.refresh, this);
			m.on("close", this.active, this);
			this.midiModules["idrReportVerifyForm"] = m;
		}else {
			m.initDataId = r.id;
		}
		return m;
	},
	castListDataToForm : function(data, schema) {
		var formData = {};
		var items = schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var key = it.id;
			if (it.dic) {
				var dicData = {
					"key" : data[key],
					"text" : data[key + "_text"]
				};
				formData[key] = dicData;
			} else {
				formData[key] = data[key];
			}
		}
		Ext.applyIf(formData, data);
		return formData;
	},
	doCancelVerify : function() {
		body = {};
		body["IDR_Report"] = this.getFormData();
//		this.panel.el.mask("正在弃审...", "x-mask-loading");
		var r = phis.script.rmi.miniJsonRequestSync({
			serviceId : this.serviceId,
			serviceAction : "cancelVerify",
			body : body
		});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg,
					this.onBeforeSave);
			return;
		} else {
//			this.panel.el.unmask();
			Ext.Msg.alert("提示", "弃审成功！");
			this.fireEvent("save", this);
			this.changeButtonState("new");
		}
	},
	
	// 改变按钮状态
	changeButtonState : function(state) {
		var actions = this.actions;
		for ( var i = 0; i < actions.length; i++) {
			var action = actions[i];
			this.setButtonsState([ action.id ], false);
		}
		if (state == "new") {
			this.setButtonsState([ "save", "verify",
					"print", "close" ], true);
			this.list.setButtonsState([ "create", "remove" ],
					true);
		}
		if (state == "verified") {
			this.setButtonsState([ "cancelVerify",
					"commit", "print", "close" ], true);
			this.list.setButtonsState([ "create", "remove" ],
					false);
		}
	},
	// 设置按钮状态
	setButtonsState : function(m, enable) {
		var btns;
		var btn;
		btns = this.form.topToolbar;
		//btns = this.panel.getTopToolbar();
		if (!btns) {
			return;
		}
		for ( var j = 0; j < m.length; j++) {
			if (!isNaN(m[j])) {
				btn = btns.items.item(m[j]);
			} else {
				btn = btns.find("cmd", m[j]);
				btn = btn[0];
			}
			if (btn) {
				(enable) ? btn.enable() : btn.disable();
			}
		}
	},
	initFormData : function(data) {
		Ext.apply(this.data, data)
		this.initDataId = this.data[this.schema.pkey]
		var form = this.form.getForm()
		var items = this.schema.items
		var n = items.length
		for (var i = 0; i < n; i++) {
			var it = items[i]
			var f = form.findField(it.id)
			if (f) {
				var v = data[it.id]
				if (v != undefined) {
					if (f.getXType() == "checkbox") {
						var setValue = "";
						if (it.checkValue
								&& it.checkValue.indexOf(",") > -1) {
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
						if (it.dic && v !== "" && v === 0) {
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
			this.setKeyReadOnly(true);
		}
	},
	doSave : function() {
		if(this.beforeSaveDataValidate()){
			phis.application.cic.script.IdrReportListView.superclass.doSave
					.call(this);}
	},
	beforeSaveDataValidate : function() {
		var form = this.form.getForm();
		var personName = form.findField("personName");
		var idCard = form.findField("idCard");
		var sexCode = form.findField("sexCode");
		var parentsName = form.findField("parentsName");
		var birthdayfield=form.findField("birthday");
		var birthday = birthdayfield.value;
		if(typeof birthday=="string"){
			birthday=new Date(Date.parse(birthday));
		}
		var workPlace = form.findField("workPlace");
		var mobileNumber = form.findField("mobileNumber");
		var address = form.findField("address");
		var viralHepatitis = form.findField("viralHepatitis");
		var aids = form.findField("aids");
		var anthrax = form.findField("anthrax");
		var dysentery = form.findField("dysentery");
		var phthisis = form.findField("phthisis");
		var typhia = form.findField("typhia");
		var syphilis = form.findField("syphilis");
		var malaria = form.findField("malaria");
		var dateAccident = form.findField("dateAccident");
		var diagnosedDate = form.findField("diagnosedDate");		
		var fillDate = form.findField("fillDate");	
		var casemixCategory1 = form.findField("casemixCategory1");
		var casemixCategory2 = form.findField("casemixCategory2");
		var comments = form.findField("comments");
		//年龄
		var fullAge = form.findField("fullAge").value;
		var age = fullAge.substr(0,fullAge.indexOf("岁"));
		var msg = "";
		/*患者姓名 传染病要求：必填，2到20个字；患者姓名不能全为数字或特殊字符且首位不能为数字或特殊字符。*/
		if(personName.getValue()=="" || personName.getValue().length<2 || personName.getValue().length>20 
		|| chis.script.util.helper.Helper.isNum(personName.getValue()) 
		|| chis.script.util.helper.Helper.isNum(personName.getValue().substr(0,1))
		|| chis.script.util.helper.Helper.isExistsSpecialCharacters(personName.getValue())){
			msg += "患者姓名不能为空，且为2到20个字；患者姓名不能全为数字或含有特殊字符且首位不能为数字！</br>";
		}
		/*性别 必填，编码不能错误；传染病系统采用国标子集（1 男，2 女）。*/
		if(sexCode.value==""){
			msg += "性别不能为空！</br>";
		}
		/*身份证号码 传染病要求：必填，身份证号码15或18位，身份证号码前2位须是省级标准编码。
		 * 患者年龄大于14岁，填写本人身份证，按身份证的构成，校验出生日期，性别；小于14岁，填写家长身份证。
		 * “证件类型”选择“身份证”，且身份证号不明，可填" 00000000000000000X "代替*/
		if(idCard.getValue()==undefined || idCard.getValue()=="" || !chis.script.util.helper.Helper.validateIdcard(idCard.getValue())){
			msg += "身份证号码不能为空，且为15或18位、前2位必须是省级标准编码、小于14岁可填写家长身份证、身份证号不明可填\"00000000000000000X\"！</br>";
		}
		if(age>14 && idCard.getValue()!="00000000000000000X"){
			var birthday2 = "";
			if(idCard.getValue().length==15){
				birthday2 = "19"+idCard.getValue().slice(6,12);
			}
			else if(idCard.getValue().length==18){
				birthday2 = idCard.getValue().slice(6,14);
			}
			var sexcode2 = parseInt(idCard.getValue().slice(-2, -1)) % 2 == 1?"1" : "2";
			if(birthday2!=birthdayfield.value.substr(0,10).replace("-","").replace("-","") || sexcode2!=sexCode.value){
				msg += "出生日期、性别与身份证号码不一致！</br>";
			}
		}
		/****民族编码（系统未提供选择功能，默认传空） 传染病要求：疾病病种“0600艾滋病”、“0601HIV”时必填。****/
		/****婚姻状况编码（系统未提供选择功能，默认传空） 传染病要求：疾病病种“0600艾滋病”、“0601HIV”时，要求婚姻状况不能为空（现住址类型不是港澳台和外籍且病例分类是临床诊断病例或确诊病例）。婚姻状况编码不能错误。****/
		/****文化程度（系统未提供选择功能，默认传空） 传染病要求：疾病病种“0600艾滋病”、“0601HIV”时，文化程度必填（现住址类型不是港澳台和外籍且病例分类是临床诊断病例或确诊病例），文化程度编码不能错误。****/
		/****现住地址编码（上报时后台使用当前患者健康档案表中的管辖机构字段与CrbbkJgdz.dic字典进行匹配获取areacode属性）****/
		/****户籍地类型编码（上报时后台写死，取birthPlace值） 传染病要求：疾病病种为艾滋、HIV时户籍地址类型不能为空。****/
		/****户籍地址编码（同现住地址编码） 传染病要求：疾病病种为艾滋、HIV时户籍编码不能为空，须具体到乡级；户籍所在地类型与户籍地址关系不能错误。****/
		/****户籍地详细地址（同详细现住地址，取address值） 传染病要求：疾病病种为艾滋、HIV时户籍详细地址不能为空；户籍详细地址最长50个字。****/
		/****死亡日期（系统未提供选择功能，默认传空） 传染病要求：非必填，数据格式为D8。死亡日期须小于等于当前时间；死亡日期格式错误须为不带时分秒的日期格式(YYYY-MM-DD)。****/
		/****疾病病种编码（系统未提供“其他”选项，与最新接口文档对比，很多疾病未在系统中提供选项） 传染病要求：必填；某些大类病种不能录入传染病报告卡；疾病病种“其他”时，具体疾病名称不能为空（其他和其他疾病暂时无ICD10编码）；病种为艾滋病、HIV和手足口病时须传附卡。****/
		/****疾病病种ICD10编（上报时后台使用当前选择的疾病病种编码与CrbbkJbbz.dic字典进行匹配获取） ****/
		/****其他疾病名称（otherCategoryInfectious） 传染病要求：只有当疾病病种选择9899时，才能填写，小于15字。****/
		/****报告单位所属县区编码（上报时后台已写死：32012400） 传染病要求：必填，报告地区编码不能错误；报告地区编码必须具体到县（市、区）级。****/
		/****报告单位编码（上报时后台使用表单中报告单位编码reportUnit与CrbbkJgdz.dic字典进行匹配获取text属性） 传染病要求：必填，报告机构编码不能错误；报告地区编码和机构所属地区编码关系不能错误。****/
		/****报告人/填卡人（上报时获取填卡医生姓名 reportDoctor_text） 传染病要求：必填，2到20个字；患者姓名不能全为数字或特殊字符且首位不能为数字或特殊字符。*****/
		/****录入单位所属县区编码（上报时后台已写死：32012400） 传染病要求：非必填，录入地区编码不能错误；报告地区编码必须具体到县（市、区）级。为空时，默认与报告地区一致。****/
		/****录入单位编码（上报时后台获取录入机构createUnit与CrbbkJgdz.dic字典进行匹配获取text属性） 传染病要求：非必填，录入机构编码不能错误；报告地区编码和机构所属地区编码关系不能错误。为空时，默认与报告机构一致。若录入单位所属地区编码不为空，则此项必填与报告地区编码前6位相同****/
		/****录入人/操作人（上报时获取填卡医生姓名 reportDoctor_text）  传染病要求：非必填，2到20个字；患者姓名不能全为数字或特殊字符且首位不能为数字或特殊字符。为空时，默认与报告人/填卡人一致。****/
		/****录入时间（上报时获取录入时间createDate）  传染病要求：非必填，2到20个字；患者姓名不能全为数字或特殊字符且首位不能为数字或特殊字符。为空时，默认与报告人/填卡人一致。****/
		/****国家大疫情账号（上报时中间接口服务替换处理） 传染病要求：必填，且与报告机构对应。****/
		/****备注（上报时中间接口服务替换处理） 传染病要求：非必填，小于50字。****/
		/****删除类型编码（系统暂未实现） 传染病要求：在操作为删除操作时（Del）删除类型编码不能为空。****/
		/****具体删除原因（系统暂未实现） 传染病要求：在操作为删除操作时（Del）具体删除原因不能为空。****/
		
		if(birthdayfield.activeError!=undefined || birthdayfield.value==undefined){
			msg += "出生日期不能为空！</br>";
		}
		/*现住地址类型编码 传染病要求：必填；病人属性与现住地址关系不能错误。*/
		if (form.findField("birthPlace").value != '1') {
			msg += "病人属于应为“本县区”！</br>";
		}
		/*人群分类 传染病要求：人群分类编码不能为空、不能错误；
			14岁以上（不含）不能是幼托儿童和散居儿童；
			14岁以下（含）只能是幼托儿童、散居儿童、学生，必须对应其对应值域代码。
		    传染病人群分类为“其它”时不能为空；其它具体职业最长15个字。*/
		if (form.findField("patientJob").value==undefined || form.findField("patientJob").value=="") {
			msg += "人群分类不能为空！</br>";
		}
		else if(age>14 && (form.findField("patientJob").value=="1" || form.findField("patientJob").value=="2")){
			msg += "14岁以上（不含）不能是幼托儿童和散居儿童！</br>";
		}
		else if(age<=14 && !(form.findField("patientJob").value=="1" || form.findField("patientJob").value=="2" || form.findField("patientJob").value=="3")){
			msg += "14岁以下（含）只能是幼托儿童、散居儿童、学生！</br>";
		}
		if(form.findField("patientJob").value=="29" && (form.findField("otherPatientJob").getValue()==undefined || form.findField("otherPatientJob").getValue()=="")){
			msg += "人群分类选择“其它”时，其它职业不能为空！</br>";
		}
		if(form.findField("patientJob").value=="29" && !(form.findField("otherPatientJob").getValue()==undefined || form.findField("otherPatientJob").getValue()=="") && form.findField("otherPatientJob").getValue().length>15){
			msg += "其它职业最长15个字！</br>";
		}
		/*工作单位 传染病要求：工作单位最长50个字；
		患者工作单位不能为空填报情形是：教师、医务人员、工人、民工、干部职员；如患者是：幼托儿童填所在托幼机构，学生填所在学校，民工填所工作工地或建筑队。*/
		if((workPlace.getValue()=="" || workPlace.getValue()=="/") 
			&& (form.findField("patientJob").value=="4" || form.findField("patientJob").value=="9" 
			|| form.findField("patientJob").value=="16" || form.findField("patientJob").value=="17" 
			|| form.findField("patientJob").value=="22")){
			msg += "教师、医务人员、工人、民工、干部职员的工作单位不能为空！</br>";
		}else if(!(workPlace.getValue()=="" || workPlace.getValue()=="/") && workPlace.getValue().length>50){
			msg += "工作单位最长50个字！</br>";
		}
		if(dateAccident.activeError!=undefined || dateAccident.value==undefined){
			msg += "发病日期不能为空！</br>";
		}
		if(diagnosedDate.activeError!=undefined || diagnosedDate.value==undefined){
			msg += "诊断日期不能为空！</br>";
		}
		if(fillDate.activeError!=undefined || fillDate.value==undefined){
			msg += "填卡日期不能为空！</br>";
		}
		/*出生日期 传染病要求：必填；出生日期须小于等于发病日期、当前时间、诊断时间、报告日期、死亡日期，
		   出生日期格式不能错误，须为不带时分秒的日期格式(YYYY-MM-DD)；
		   新生儿破伤风疾病患者出生日期和报告日期最大只能差28天；
		   从业人员年龄不能在14岁以下(含14岁)。*/
		var serverDateTime = Date.getServerDateTime();
		if(serverDateTime<birthdayfield.value){
			msg += "出生日期须小于等于当前日期！</br>";
		}
		if(diagnosedDate.getValue()<birthday){
			msg += "出生日期须小于等于诊断时间！</br>";
		}
		if(dateAccident.getValue()<birthday){
			msg += "出生日期须小于等于发病时间！</br>";
		}
		if(fillDate.getValue()<birthday){
			msg += "出生日期须小于等于填卡时间！</br>";
		}
		/*待完善 出生日期须小于等于死亡日期*/
		
		/*发病日期 传染病要求：必填，且须小于等于当前时间、死亡日期、报告日期、诊断时间，发病日期格式错误须为不带时分秒的日期格式(YYYY-MM-DD)。*/
		if(serverDateTime<dateAccident.value){
			msg += "发病日期须小于等于当前日期！</br>";
		}
		if(diagnosedDate.getValue()<dateAccident.getValue()){
			msg += "发病日期须小于等于诊断时间！</br>";
		}
		if(fillDate.getValue()<dateAccident.getValue()){
			msg += "发病日期须小于等于填卡时间！</br>";
		}
		/*待完善 发病日期须小于等于死亡日期*/
		
		/*诊断日期（上报时后台已按要求截取） 传染病要求：必填，且须小于等于当前时间、医生填卡日期；诊断时间须等于大于发病日期；诊断时间格式错误须为带时的时间格式。
		 * 其格式可为YYYY-MM-DD或YYYY-MM-DD HH(天数与小时之间带空格)，日期未精确至小时，默认小时为00。例：2000-01-01 00*/
		if(serverDateTime<diagnosedDate.value){
			msg += "诊断日期须小于等于当前日期！</br>";
		}
		if(fillDate.getValue()<diagnosedDate.getValue()){
			msg += "诊断日期须小于等于填卡时间！</br>";
		}
		/*填卡日期 传染病要求：必填，且须小于等于当前时间,大于死亡日期、诊断时间，新生儿破伤风疾病患者的出生日期和报告日期最大只能相差28天。
		 * 发病日期格式错误须为不带时分秒的日期格式(YYYY-MM-DD)*/
		if(serverDateTime<fillDate.value){
			msg += "填卡日期须小于等于当前日期！</br>";
		}
		/*待完善 填卡日期须大于死亡日期*/
		
		//新生儿破伤风疾病患者
		if(this.categoryBInfectious.getValue() == "2500" && Date.getDatedif_days(birthdayfield.value.substr(0,10),fillDate.value.substr(0,10))>28){
			msg += "新生儿破伤风疾病患者出生日期和报告日期最大只能差28天！</br>";
		}
		/*联系电话 传染病要求：患者小于等于14岁时，联系电话必须填写，且小于18位。*/
		if(mobileNumber.getValue().length>0 && (!chis.script.util.helper.Helper.isNum(mobileNumber.getValue()) || mobileNumber.getValue().length>=18)){
			msg += "联系电话格式不正确，且小于18位！</br>";
		}
		if(age <= 14 && mobileNumber.getValue().length==0){
			msg += "患者小于等于14岁时，联系电话必须填写，且小于18位！</br>";
		}
		/*详细现住地址 传染病要求：必填；详细现住地址最长50个字。*/
		if(address.getValue()=="" || address.getValue().length>50){
			msg += "现住址必须填写，且最长50个字！</br>";
		}
		/*患者/死者家属姓名 传染病要求：患者家长/死者姓名不能为空，2到20个字；患者姓名不能全为数字或特殊字符且首位不能为数字或特殊字符。*/
		if(age <= 14 && (parentsName.getValue()=="" || parentsName.getValue().length<2 || parentsName.getValue().length>20 
		|| chis.script.util.helper.Helper.isNum(parentsName.getValue()) 
		|| chis.script.util.helper.Helper.isNum(parentsName.getValue().substr(0,1))
		|| chis.script.util.helper.Helper.isExistsSpecialCharacters(parentsName.getValue()))){
			msg += "患儿家长姓名不能为空，且为2到20个字；患儿家长姓名不能全为数字或含有特殊字符且首位不能为数字！</br>";
		}
		/*传染病诊断类型编码（病例分类1/casemixCategory1） 
		 * 传染病要求：必填，其编码不能错误；
		 * 疾病名称为梅毒/淋病时，诊断类型是确诊病例和疑似病例；
		 * 疾病名称为尖锐湿疣/生殖器疱疹时，诊断类型是确诊病例和临床诊断病例；
		 * 疾病名称为生殖道沙眼衣原体感染时，病诊断类型是确诊病例；
		 * 疾病名称为霍乱、脊灰、乙肝、伤寒、副伤寒、间日疟或恶性疟时，诊断类型是病原携带者；
		 * 当疾病名称为AFP时， 疾病分类不能选择确诊病例；
		 * 当疾病名称为脊灰、AFP、艾滋病、HIV时， 疾病分类不能选择疑似病例；
		 * 当疾病名称为脊灰、HIV时， 疾病分类不能选择临床诊断病例；
		 * 疾病名称为埃博拉出血热时，诊断类型为埃博拉留观病例；
		 * 当疾病名称为利福平耐药、病原学阳性时，疾病分类必须选择确诊病例。*/
		if(casemixCategory1.value==""){
			msg += "病例分类1不能为空！</br>";
		}
		if (this.categoryAInfectious.getValue() == ""
				&& this.categoryBInfectious.getValue() == ""
				&& this.categoryCInfectious.getValue() == ""
				&& this.otherCategoryInfectious.getValue() == "") {
			msg += "甲类传染病、乙类传染病、丙类传染病、其他传染病必填其中一个！</br>";
		} else if (this.categoryBInfectious.getValue() != "") {
			var categoryBInfectious = this.categoryBInfectious
					.getValue();
			var adressArray = categoryBInfectious.split(",");
			for (var i = 0; i < adressArray.length; i++) {
				if (adressArray[i] == "0300"
						&& viralHepatitis.getValue() == "") { // 如果病毒性肝炎
					msg += "请填写病毒性肝炎类型！</br>";
				} else if (adressArray[i] == "0300"
						&& (form.findField("casemixCategory1").value == "3" || form
								.findField("casemixCategory1").value == "1")
						&& form.findField("viralHepatitis").value == "0302") {
					msg += "乙型病毒性肝炎不能选择疑似病例或临床诊断！</br>";
				} else if (adressArray[i] == "0600"
						&& aids.getValue() == "") {// 艾滋病
					msg += "请填写艾滋病类型！</br>";
				} else if (adressArray[i] == "1900"
						&& anthrax.getValue() == "") {// 炭疽
					msg += "请填写炭疽类型！</br>";
				} else if (adressArray[i] == "0400"
						&& dysentery.getValue() == "") {// 痢疾
					msg += "请填写痢疾类型！</br>";
				} else if (adressArray[i] == "2600"
						&& phthisis.getValue() == "") {// 肺结核
					msg += "请填写肺结核类型！</br>";
				} else if (adressArray[i] == "0500"
						&& typhia.getValue() == "") {// 伤寒
					msg += "请填写伤寒类型！</br>";
				} else if (adressArray[i] == "0800"
						&& syphilis.getValue() == "") {// 梅毒
					msg += "请填写梅毒类型！</br>";
				} else if (adressArray[i] == "2300"
						&& malaria.getValue() == "") {// 疟疾
					msg += "请填写疟疾类型！</br>";
				}
			}
		}
		/*病例分类编码（病例分类2/casemixCategory2） 
		 * 传染病要求：必填；病例分类编码不能错误；疾病名称为乙肝、丙肝或血吸虫病时，病例分类中才可为急性或慢性；
		 * 疾病名称为其它疾病时，病例分类为未分型。*/
		if(casemixCategory2.value==""){
			msg += "病例分类2不能为空！</br>";
		}
		if ((((this.viralHepatitis.getValue() == '0303' && this.categoryBInfectious
				.getValue() == '0300') || (this.categoryBInfectious
				.getValue() == '0300' && this.viralHepatitis.getValue() == '0302')) || this.categoryBInfectious
				.getValue() == '3100')
				&& casemixCategory2.getValue() == '0') {
			msg += "传染病病种为“乙型肝炎、丙肝、血吸虫病”时，“病例分类2”里不能为“未分型”！</br>";
		}
		/*备注 传染病要求：非必填，小于50字。*/
		if(comments.getValue()!="" && comments.getValue().length>50){
			msg += "备注必须小于50字！</br>";
		}
		if(msg != ""){
			MyMessageTip.msg("提示",msg, true);
			return false;
		}
		return true;
	},
	doPrint : function(){
		body = {};
		body["IDR_Report"] = this.getFormData();
		var empiId=body["IDR_Report"].empiId;
	
		//console.log(body["IDR_Report"].empiId);
		//alert(body["icd10"]);
	          var pages="phis.prints.jrxml.infectiousDisease";
			var url="resources/"+pages+".print?type=1";
			url += "&empiId="+body["IDR_Report"].empiId;
			
			var LODOP=getLodop();
			LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.PREVIEW();
			},
			beforeclose : function() {
				if(!this.opener.doSave()){
					this.opener.doSaveDiagnosis();
				}
			},
	doClose : function() {
		var win = this.getWin();
		if (win) {
			win.hide();
		}
	}
})