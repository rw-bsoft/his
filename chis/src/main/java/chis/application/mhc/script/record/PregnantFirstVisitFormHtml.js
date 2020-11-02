/**
 * 孕妇首次随访表单页面
 * 
 * @author : xuzb
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizHtmlFormView",
	"chis.application.mhc.script.record.PregnantFirstVisitFormHTMLTemplate",
	"chis.script.util.widgets.MyMessageTip");
$styleSheet("chis.css.PregnantFirstVisitFormHtml")

chis.application.mhc.script.record.PregnantFirstVisitFormHtml = function(cfg) {
	cfg.fldDefaultWidth = 160
	cfg.labelWidth = 90;
	cfg.autoFieldWidth = false;
	this.autoLoadData = true;
	chis.application.mhc.script.record.PregnantFirstVisitFormHtml.superclass.constructor
			.apply(this, [cfg])
	Ext.apply(this,chis.application.mhc.script.record.PregnantFirstVisitFormHTMLTemplate);
	this.disableFlds = ["otherPastHistory", "otherFamilyHistory",
			"otherPersonHistory", "otherSuggestion", "JY_508_other"];
	this.createFields = ["createDate", "diagnosisMethod", "visitDoctorCode",
			"visitPrecontractTime", "diagnosisDate", "dateOfPrenatal",
			"lastMenstrualPeriod"];
	this.on("loadData", this.onLoadData, this);
	this.on("beforeCreate", this.onBeforeCreate, this)
//	this.on("doNew", this.onDoNew, this);
	this.DA = {};// 档案数据
	this.SF = {};// 随访数据
	this.JY = {};// 检验数据
	this.LoadData = {};// 总数据
	this.otherDisable = [
			{
				fld : "suggestion",
				type : "checkbox",
				control : [{
							key : "6",
							exp : 'eq',
							field : ["otherSuggestion"]
						}]
			}, {
				fld : "JY_508",
				type : "checkbox",
				control : [{
							key : "4",
							exp : 'eq',
							field : ["JY_508_other"]
						}]
			}, {
				fld : "JY_101",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_101_other"]
						}]
			}, {
				fld : "JY_102",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_102_other"]
						}]
			}, {
				fld : "JY_301",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_301_other"]
						}]
			}, {
				fld : "JY_302",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_302_other"]
						}]
			}, {
				fld : "JY_303",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_303_other"]
						}]
			}, {
				fld : "JY_304",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_304_other"]
						}]
			}, {
				fld : "JY_305",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["JY_305_other"]
						}]
			}, {
				fld : "generalComment",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["commentText"]
						}]
			}, {
				fld : "referral",
				type : "radio",
				control : [{
							key : '2',
							exp : 'eq',
							field : ["reason", "doccol"]
						}]
			}];
}
var Ctx_ = null;//
function doOnblur(id, value, type) {
	// return
	if (type == "radio") {
		return
		Ctx_.setRadioType(id, value, type);
	} else if (type == "checkbox") {
		return
		Ctx_.setCheckboxType(id, value, type);
	} else {
		Ctx_.doOnclock(id, value);
	}
}
Ext.extend(chis.application.mhc.script.record.PregnantFirstVisitFormHtml,
		chis.script.BizHtmlFormView, {
			getHTMLTemplate : function() {
				return this.getPFVHTMLTemplate();
			},
			onReady : function() {
				this.addFieldAfterRender();
				// 字段校验
				this.initFieldDisable();
				this.addFieldDataValidateFun(this.schema);
				this.controlOtherFld();
				this.mutualExclusionSet();
				// 扩展
				this.onReadyAffter();
				this.initDataId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
			},
			getLoadRequest : function(){
				var body = {
								"pregnantId" : this.pregnantId || '',
								"empiId":this.exContext.ids.empiId
							}
				return body;
			},
			loadData : function(FuData,initKey) {
				this.DA = FuData;
				 this.initKey=initKey;
				 if (!this.pregnantId || this.pregnantId == "null") {
					this.op = "create";
					if(FuData){
						Ext.apply(FuData, this.exContext.control);
						this.LoadData.DA = FuData;// 页面数据
						this.DA = FuData;
						this.initFormData(FuData);
					}
				}
				 if(this.pregnantId && this.pregnantId == this.exContext.ids["MHC_PregnantRecord.pregnantId"]){
				 	//不重复请求后台
				 	return;
				 }
				this.pregnantId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				//====================
				if(this.loadDataByDefaultValue){
					this.doNew();
				}else{
					this.doNew(1);
				}
				if(this.loading){
					return
				}
				if(!this.schema){
					return
				}
				var loadRequest = this.getLoadRequest();
				if(!this.initDataId && !this.initDataBody && !loadRequest){
					return;
				}
				if(!this.fireEvent("beforeLoadData",this.entryName,this.initDataId||this.initDataBody, loadRequest)){
					return
				}
				if(this.form && this.form.el){
					this.form.el.mask("正在载入数据...","x-mask-loading")
				}
				this.loading = true
				var loadCfg = {
					serviceId:this.loadServiceId,
					method:this.loadMethod,
					schema:this.entryName,
					pkey:this.initDataId||this.initDataBody,
					body:loadRequest,
					action:this.op,    //按钮事件
					module:this._mId   //增加module的id
				}
				this.fixLoadCfg(loadCfg);
				util.rmi.jsonRequest(loadCfg,
					function(code,msg,json){
						if(this.form && this.form.el){
							this.form.el.unmask()
						}
						this.loading = false
						if(code > 300){
							this.processReturnMsg(code,msg,this.loadData)
							this.fireEvent("exception", code, msg, loadRequest, this.initDataId||this.initDataBody); // ** 用于一些异常处理
							return
						}
						var body = json.body;
						if(body){
							this.xueX=body.DA;
							this.DA = body.DA;
							if(!this.DA.pregnantId){
								Ext.apply(this.DA,FuData);
							}
							this.KEY_SF = body.KEY_SF;// 判断是否有值返回
							
							this.SF = body.SF;
							if(body.JY){
								this.JY = body.JY;
							}
							this.LoadData = body;
							this.initFormData(body)
							this.fireEvent("loadData",this.entryName,body);
						}else{
							this.initDataId = null;
							// ** 没有加载到数据，通常用于以fieldName和fieldValue为条件去加载记录，如果没有返回数据，则为新建操作，此处可做一些新建初始化操作
							this.fireEvent("loadNoData", this);
						}
						if(this.op == 'create'){
							this.op = "update"
						}
						
					},
					this);
//				return;
//				//--------------------------------------------
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "chis.pregnantRecordService",
//							serviceAction : "loadHtmlData",
//							method : "execute",
//							body : {
//								"pregnantId" : this.initDataId,
//								"empiId":this.exContext.ids.empiId,
//								"husbandEmpiId":husbandEmpiId
//							}
//						});
//				if (result.code > 300) {
//					this.processReturnMsg(result.code, result.msg);
//					return null;
//				} else {
//					var bodyBack = result.json.body;
//					this.xueX=bodyBack.DA;
//					this.KEY_SF = bodyBack.KEY_SF;// 判断是否有值返回
//					
//					this.SF = bodyBack.SF;
//					if(bodyBack.JY){
//						this.JY = bodyBack.JY;
//					}
//					this.LoadData = bodyBack;
//					this.initFormData(bodyBack);
//				}
//				if (this.initDataId == "" || this.initDataId == null
//						|| this.initDataId == "null") {
//					this.op = "create";
//					Ext.apply(FuData, this.exContext.control);
//					this.LoadData.DA = FuData;// 页面数据
//					this.DA = FuData;
//					this.initFormData(FuData);
//				} else {
//					//this.op = "update";
//				}
			},
			onLoadData : function(entryName, body) {
//				var createDate = body["createDate"];
//				var d = Date.parseDate(createDate.substr(0, 10), "Y-m-d")
//				this.visitPrecontractTime
//						.setMinValue(chis.script.util.helper.Helper
//								.getOneDayAfterDate(d));
//				this.visitPrecontractTime.validate();
				//TODO
				
			},
			
			addFieldAfterRender : function() {
				var hiddenField = ["pregnantId", "empiId", "hospitalCode",
						"createUnit", "visitId", "createUser",
						"lastModifyUnit", "lastModifyUser", "lastModifyDate"];
				this.hiddenField = hiddenField; // 隐藏字段

				var yanZ = ["weight", "abdomenCircumFerence",
						"heightFundusUterus", "sbp", "dbp"];
				this.yanZ = yanZ;// 必填字体
				var arrey = ["xueY_type", "abdomenCircumFerence_type",
						"heightFundusUterus_type", "weight_type"];
				this.arrey = arrey;// not-null 验证
				var serverDate = this.mainApp.serverDate;

				var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				this.createDate = new Ext.form.DateField({
							name : 'createDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							maxValue : curDate,
							allowBlank : false,
							invalidText : "必填字段",
							fieldLabel : "随访日期",
							renderTo : Ext.get("div_createDate"
									+ this.idPostfix)
						});

				// 随访医生 this.manaDoctorId.invalidText = "必填字段";
				this.visitDoctorCode = this.createDicField({
							"width" : 200,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.user",
							"render" : "Tree",
							"selectOnFocus" : true,
							"onlySelectLeaf" : true,
							"parentKey" : "%user.manageUnit.id",
							"defaultValue" : {
								"key" : this.mainApp.uid,
								"text" : this.mainApp.uname + "--"
										+ this.mainApp.jobtitle
							}
						});
				this.visitDoctorCode.render("div_visitDoctorCode"
						+ this.idPostfix);


				// 妊娠确诊时间
				this.diagnosisDate = new Ext.form.DateField({
							name : 'diagnosisDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							invalidText : "必填字段",
							allowBlank : false,
							maxValue : curDate,
							fieldLabel : "随访日期",
							renderTo : Ext.get("div_diagnosisDate"
									+ this.idPostfix)
						});
				// 下次随访日期
				this.visitPrecontractTime = new Ext.form.DateField({
							name : 'visitPrecontractTime' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							minValue : curDate,
							fieldLabel : "随访日期",
							renderTo : Ext.get("div_visitPrecontractTime"
									+ this.idPostfix)
						});
				// 妊娠确诊方法
				this.diagnosisMethod = this.createDicField({
							"width" : 200,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.CV5201_08",
							"render" : "Tree",
						 	"selectOnFocus" : true,
							"invalidText" : "必填字段",
							"allowBlank" : "false",
						//	"parentKey" : "%chis.dictionary.CV5201_08",
							"defaultValue" : {
								"key" : this.diagnosisMethodKey,
								"text" : this.diagnosisMethodText
							}
						});
				this.diagnosisMethod.validate();
				this.diagnosisMethod.render("div_diagnosisMethod"
						+ this.idPostfix); 
				// 末次月经时间
				this.lastMenstrualPeriod = new Ext.form.DateField({
							name : 'lastMenstrualPeriod' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '末次月经时间',
							allowBlank : false,
							fieldLabel : "末次月经时间",
							renderTo : Ext.get("div_lastMenstrualPeriod"
									+ this.idPostfix)
						});

				this.lastMenstrualPeriod.disable();
				// 预产期
				this.dateOfPrenatal = new Ext.form.DateField({
							name : 'dateOfPrenatal' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							invalidText : "必填字段",
							allowBlank : false,
							emptyText : '末次月经时间',
							fieldLabel : "末次月经时间",
							renderTo : Ext.get("div_dateOfPrenatal"
									+ this.idPostfix)
						});
				this.dateOfPrenatal.disable();
				var highRiskLevel = document.getElementById("highRiskLevel"
						+ this.idPostfix);
				highRiskLevel.disabled = true;
				var bmi = document.getElementById("bmi" + this.idPostfix);
				bmi.disabled = true;
				Ctx_ = this;
			},
			onBeforeCreate : function() {
				this.diagnosisDate.enable();
				this.fireEvent("firstVisitData");
			},
			initHTMLFormData : function(date, schema, LoadData) {
				if (!schema) {
					schema = this.schema;
				}
				// Ext.apply(this.data, data)
				var items = schema.items
				var n = items.length
				var htmlData;
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var v = "";
			        var LB=it.Lb;
			        if(LB=="SF"){
			        	if(this.SF!=null && this.SF!=""){
			        		if(this.SF[it.id]){
			        			v=this.SF[it.id]
			        		}
			        	}
			        }
			        if(LB=="DA"){
			        	if(this.DA!=null && this.DA!=""){
			        		if(this.DA[it.id]){
			        			v=this.DA[it.id]
			        		}
			        	}
			        }
			         if(LB=="JY"){
			        	if(this.JY!=null && this.JY!=""){
			        		if(this.JY[it.id]){
			        			v=this.JY[it.id]
			        		}
			        	}
			        }
			         if(LB=="ZFXX"){
			        	if(this.ZFXX!=null && this.ZFXX!=""){
			        		if(this.JY[it.id]){
			        			v=this.JY[it.id]
			        		}
			        	}
			        }
					if (v == null || v == "null") {
						v = "";
					}
					if (it.id == "weight" || it.id == "height") {
						if (it.id == "weight") {
							this.weight = v;
						} else {
							this.height = v;
						}
					}
					if (this.isCreateField(it.id)) {
						var cfv = v;
						if (!cfv) {
							cfv = "";
						}
						if (it.type == "date" || it.type == "datetime") {
							if (typeof cfv != "string") {
								cfv = Ext.util.Format.date(cfv, 'Y-m-d');
							} else {
								cfv = cfv.substring(0, 10);
							}
						}
						eval("this." + it.id + ".setValue(cfv);this." + it.id
								+ ".validate();");
					} else {
						if (it.dic) {
							if (!this.fireEvent("dicFldSetValue", it.id,
									htmlData)) {
								continue;
							} else {
								var dfs = document.getElementsByName(it.id);
								if (!dfs) {
									continue;
								}
								var dicFV = v;
								var fv = "";
								if (dicFV) {
									fv = dicFV.key;
									if (fv == "" || fv == null) {
										fv = v
									}
								}
								if (!fv) {// yubo
									continue;
								}
								var dvs = fv.split(",");
								for (var j = 0, len = dvs.length; j < len; j++) {
									var f = document.getElementById(it.id + "_"
											+ dvs[j] + this.idPostfix);
									if (f) {
										f.checked = true;
									}
								}
							}
						} else {
							var f = document.getElementById(it.id
									+ this.idPostfix)
							if (f) {
								if (!v) {
									v = f.defaultValue || "";
									if (f.defaultValue) {
										f.style.color = "#999";
									}
								} else {
									f.style.color = "#000";// 不是注释文字，改黑色字体
								}
								f.value = v;
								if (it['not-null'] == "1"
										|| it['not-null'] == "true") {
									if (v && v != "") {
										this.removeClass(f, "x-form-invalid");
									}
								}
							}
						}
					}
				}
				//this.setKeyReadOnly(true)
				// this.startValues = form.getValues(true);
				//this.resetButtons(); // ** 用于页面按钮权限控制
				// this.focusFieldAfter(-1, 800);
			},
			initFormData : function(data) {
				//this.beforeInitFormData(data); // ** 在将数据填充入表单之前做一些操作
				Ext.apply(this.data, data)
				this.initDataId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
		    	this.onDoNew();// 清空
				
				this.initHTMLFormData(data, this.schema, this.LoadData);
				this.setForm_DA();// 数据初始化后在对某些数据操作，
				// this.setKeyReadOnly(true)
				// this.startValues = form.getValues(true);
				this.setOnren();
				//this.resetButtons(); // ** 用于页面按钮权限控制
				this.focusFieldAfter(-1, 800);
				this.resetControlOtherFld();// 检验
			},
			doSave : function() {
				// 验证方法
				var saveValue = this.getFormData(this.schema);
				if (!saveValue) {
					return;
				}
				var pdKey=this.doYZ();//额外的验证
				if(pdKey==false){
					return;
				}
				if (!this.htmlFormSaveValidate(this.schema)) {
					// Ext.Msg.alert("提示", "精神病档案 页面上有验证未通过的字段，请修改录入值");
					// MyMessageTip.msg("提示", "精神病档案 页面上有验证未通过的字段，请修改录入值",true);
					return;
				}
				this.fireEvent("recordSave",saveValue);
			},
			getHTMLFormData : function(schema) {
				if (!schema) {
					schema = this.schema;
				}
				// 取表单数据
				if (!schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				this.ZFXX = {};// 随访数据
				this.JyKey = [];// 检验项目Id
				var items = schema.items;
				var n = items.length

				var frmEl = this.form.getEl();
				for (var i = 0; i < n; i++) {
					var it = items[i]
					// 从页面上取
					var v="";
					if (this.isCreateField(it.id)) {
						var cfv = eval("this." + it.id + ".getValue()");
						if(it.id=="diagnosisMethod"){
							 if(cfv=="血HCG"){
						     	 cfv=1;
						     }
						      if(cfv=="尿HCG"){
						     	cfv=2
						     }
						      if(cfv=="B超"){
						     	cfv=3
						     }
						      if(cfv=="其他"){
						     	cfv=3
						     }
						}
						// values[it.id] = cfv;
						if (it.Lb == "JY") {// 对检验信息进行填充
							this.JY[it.id] = cfv;
						} else if (it.Lb == "DA") {// 对档案信息进行填充
							continue;
							//this.DA[it.id] = cfv;
						} else if (it.Lb == "ZFXX") {// 对档案信息进行填充
							this.ZFXX[it.id] = cfv;
						} else if (it.Lb == "SF") {// 对档案信息进行填充
							this.SF[it.id] = cfv;
						}
					} else {
						if (it.dic) {
							var fs = document.getElementsByName(it.id);
							if (!fs) {
								continue;
							}
							var vs = [];
							if (fs && fs.length > 0) {
								for (var j = 0, len = fs.length; j < len; j++) {
									var f = fs[j];
									if (frmEl.contains(f)) {
										if (f.type == "checkbox"
												|| f.type == "radio") {
											if (f.checked) {
												vs.push(f.value);
											}
										} else if (f.type == "hidden") {
											vs.push(f.value || '');
										}
									}
								}
							}
							if (vs.length > 1) {
								v = vs.join(',') || ''
							} else {
								v = vs[0] || ''
							}
							if (it.Lb == "JY") {// 对检验信息进行填充 
								this.JY[it.id] = v;
								if(it.idIf){
									this.JY[it.idIf] = v;
								}
								this.setJyId(it, v);
							} else if (it.Lb == "DA") {// 对档案信息进行填充
								//continue;
								//this.DA[it.id] = v;
							} else if (it.Lb == "ZFXX") {// 对档案信息进行填充
								this.ZFXX[it.id] = v;
							} else if (it.Lb == "SF") {// 对档案信息进行填充
								this.SF[it.id] = v;
							}
						} else {
							var f = document.getElementById(it.id
									+ this.idPostfix)
							if (f) {
								v = f.value || v || '';
								if (v == f.defaultValue && f.type != "hidden") {
									v = '';
								}
								if (it.Lb == "JY") {// 对检验信息进行填充
									this.JY[it.id] = v;
									this.setJyId(it, v);
								} else if (it.Lb == "DA") {// 对档案信息进行填充
								//	this.DA[it.id] = v;
								} else if (it.Lb == "ZFXX") {// 对档案信息进行填充
									this.ZFXX[it.id] = v;
								} else if (it.Lb == "SF") {// 对档案信息进行填充
									this.SF[it.id] = v;
								}
							}
						}
					}
				}
			//	values["DA"] = this.DA;
				values["JY"] = this.JY;
				this.JY["JyKey"] = this.JyKey;
				values["ZFXX"] = this.ZFXX;
				values["SF"] = this.SF;
				this.LoadData.JY=this.JY;
				this.LoadData.JY=this.SF;
				return values;
			},
			setJyId : function(it, v) {// 对检验值进行拼接，好在后台保存
				var fv = it.id;
				var dvs = fv.split("_");
				for (var j = 0, len = dvs.length; j < len; j++) {
					if (len == 2) {// other过滤
						if (j == 1) {// 取Id
							this.JyKey.push(dvs[j]);
						}
					}
				}
			},

			getSaveData : function() {// 父页面调用保存
				var values = this.SF;
				values["highRisknessesChanged"] = true;
				var pdKey=this.htmlFormSaveValidate(this.schema);
				if(pdKey==false){
					values=pdKey;
				}
				return values;
			}
			,
			doOnclock : function(id, value) {
				var dom = document.getElementById(id + this.idPostfix);
				if (id == "weight" || id == "height") {
					var weight = document.getElementById("weight"
							+ this.idPostfix);
					var height = document.getElementById("height"
							+ this.idPostfix);
					this.weight = weight.value;
					this.height = height.value;
					if (this.weight != "" && this.height != "") {
						var gao = this.height / 100;
						var bmi = this.weight / gao / gao;
						var bmi = bmi.toFixed(2);
						var mbBmi = document.getElementById("bmi"
								+ this.idPostfix)
						mbBmi.value = bmi;
					}
				} else if (id == "sbp" || id == "dbp") {
					var sbp = document.getElementById("sbp" + this.idPostfix)// 收缩压
					var dbp = document.getElementById("dbp" + this.idPostfix)// 舒张压
					var v = sbp.value;
					var k = dbp.value;
					if (id == "sbp" && value == 2) {
						k = parseInt(k);
						v = parseInt(v);
						if (v > 500 || v < 10) {
							Ext.Msg.alert("提示", "收缩压必须在10到500之间！");
							return;
						}
						if (k > v) {
							Ext.Msg.alert("提示", "收缩压应该大于舒张压！");
							return;
						}

					} else if (id == "dbp" && value == 2) {
						k = parseInt(k);
						v = parseInt(v);
						if (k > 500 || k < 10) {
							Ext.Msg.alert("提示", "舒张压(mmHg)必须在10到500之间！");
							return;
						}
						if (k > v) {
							Ext.Msg.alert("提示", "收缩压应该大于舒张压！");
							return;
						}
					}
				} else if (id == "highRiskScore") {// 高危页面
					this.openHighRisknessForm();
				} else {
					//
				}
			},

			openHighRisknessForm : function(risknesses) {
                 this.opGW=true;
				var module = this.createCombinedModule("HighRiskModule",
						this.refHighRiskModule);
				module.on("moduleClose", this.onModuleClose, this);
				module.__actived = this.riskStore == null ? false : true;
				var args = {
					"visitId" : "0000000000000000"
				};
				if (this.riskStore) {
					args.initRisknesses = null;
				} else if (this.op == "create") {
					args.initRisknesses = risknesses;
				}
				Ext.apply(this.exContext.args, args);
				this.refreshExContextData(module, this.exContext);
				module.getWin().show();
			},
			onModuleClose : function(records, store) {
				var highRiskScore = records.highRiskScore;
				var highRiskLevel = records.highRiskLevel;
				var highRisknesses = records.highRisknesses;
				this.riskStore = store;
				this.data["highRisknesses"] = highRisknesses;
				this.data["highRisknessesChanged"] = true;

				var gaoW = document.getElementById("highRiskScore"
						+ this.idPostfix);
				var pingF = document.getElementById("highRiskLevel"
						+ this.idPostfix);
				if (highRiskScore != null && highRiskScore != "") {
					gaoW.value = highRiskScore;
					gaoW.style.color = "#000";
					this.removeClass(gaoW, "x-form-invalid");
				} else {
					gaoW.value = 0;
					gaoW.style.color = "#999";
					this.removeClass(gaoW, "x-form-invalid");
				}
				if (highRiskLevel != null && highRiskLevel != "") {
					pingF.value = highRiskLevel;
					pingF.style.color = "#000";
					this.removeClass(pingF, "x-form-invalid");
				} else {
					pingF.value = "";
					pingF.style.color = "#999";
					this.removeClass(pingF, "x-form-invalid");
				}
				// this.data["highRisknessesChanged"] = true;
			},
			onDoNew : function() {
				var schema = this.schema;
				// Ext.apply(this.data, data)
				if(this.initKey == true){//如果不是首次加载，不清页面数据
					 return;
				 }
				var items = schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var v = "";
					if (this.isBackField(this.hiddenField, it.id)) { // 隐藏的信息设初始值
						continue;
					}else if (this.isCreateField(it.id)) {
						var cfv ="";
						if (!cfv) {
							cfv = "";
						}
						if (it.type == "date" || it.type == "datetime") {
							if (typeof cfv != "string") {
								cfv = Ext.util.Format.date(cfv, 'Y-m-d');
							} else {
								cfv = cfv.substring(0, 10);
							}
						}
						eval("this." + it.id + ".setValue(cfv);this." + it.id
								+ ".validate();");
					} else if (this.isBackField(this.createFields, it.id)) {// 创建的时间类型
						continue;
					}
					if (it.dic) {
						var dfs = document.getElementsByName(it.id);
						var len = dfs.length;
						for (var j = 0; j < len; j++) {
							dfs[j].checked = false;
						}
					} else {
						var f = document.getElementById(it.id + this.idPostfix)
						if (f) {
							if (!v) {
								v = f.defaultValue || "";
								if (f.defaultValue) {
									f.style.color = "#999";
								}
							} else {
								f.style.color = "#000";// 不是注释文字，改黑色字体
							}
							f.value = v;
							if (it['not-null'] == "1"
									|| it['not-null'] == "true") {
								if (v && v != "") {
									this.removeClass(f, "x-form-invalid");
								}
							}
						}
					}
				}
			},
			setOnren : function() {
				if (this.weight != "" && this.height != "") {
					var gao = this.height / 100;
					var bmi = this.weight / gao / gao;
					var bmi = bmi.toFixed(2);
					var mbBmi = document.getElementById("bmi" + this.idPostfix)
					mbBmi.value = bmi;
				}
			},
			isBackField : function(arly, fldId) {
				var isCF = false;
				var len = arly.length;
				for (var i = 0; i < len; i++) {
					var cf = arly[i];
					if (cf == fldId) {
						isCF = true;
						break;
					}
				}
				return isCF;
			},
			doPrintHtml : function() {
				return
				var win = window
						.open(
								this.printHtmlBack(),
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
			},
			setForm_DA : function() {
				if (this.op == "create") {
					var curDate = Date.parseDate(this.mainApp.serverDate,
							"Y-m-d");
					this.createDate.setValue(curDate)
				}
				var highRiskScore = document.getElementById("highRiskScore"
						+ this.idPostfix);
				if (highRiskScore.value == null || highRiskScore.value == "") {
					highRiskScore.value = 0
					this.removeClass(highRiskScore, "x-form-invalid");
				}
				if (this.DA != null && this.DA != "") {// 把档案的一些数据初始化到随访的字段中
					var sbp = document.getElementById("sbp" + this.idPostfix);
					sbp.value = this.DA["sbp"]
					var dbp = document.getElementById("dbp" + this.idPostfix);
					dbp.value = this.DA["dbp"]
					var quickeningWeek = document
							.getElementById("quickeningWeek" + this.idPostfix);
					quickeningWeek.value = this.DA["gestationalWeeks"]
					var weight = document.getElementById("weight"
							+ this.idPostfix);
					weight.value = this.DA["weight"]
					if (this.DA["weight"] != null && this.DA["weight"] != "") {
						this.removeClass(weight, "x-form-invalid");
					}
				}
				var bloodTypeCode = document.getElementById("bloodTypeCode" + this.idPostfix);
			    var rhBloodCode = document.getElementById("rhBloodCode" + this.idPostfix);
			    var age = document.getElementById("age" + this.idPostfix);
				if (this.xueX) {
					if(this.xueX["bloodTypeCode"]!=null ){
					  	if( this.xueX["bloodTypeCode"]=="1"){
						   	 bloodTypeCode.value ="A型"
						   }else  if( this.xueX["bloodTypeCode"]=="2"){
						   	 bloodTypeCode.value ="B型"
						   }else  if( this.xueX["bloodTypeCode"]=="3"){
						   	 bloodTypeCode.value ="O型"
						   }else  if( this.xueX["bloodTypeCode"]=="4"){
						   	 bloodTypeCode.value ="AB型"
						   }else  if( this.xueX["bloodTypeCode"]=="5"){
						   	 bloodTypeCode.value ="不详"
						   }else{
						     bloodTypeCode.value =""
						   }
					}
					if(this.xueX["rhBloodCode"]!=null ){
						   if( this.xueX["rhBloodCode"]=="1"){
						   	 rhBloodCode.value =   "RH阳性"
						   }else  if( this.xueX["rhBloodCode"]=="2"){
						   	 rhBloodCode.value =   "RH阴性"
						   }else  if( this.xueX["rhBloodCode"]=="3"){
						   	 rhBloodCode.value =   "不详"
						   }else{
						     rhBloodCode.value =   ""
						   }
					}
					if(this.xueX["age"]!=null ){
				        age.value =    this.xueX["age"];
					}
		     }
			 rhBloodCode.disabled = true
			 bloodTypeCode.disabled = true
			 if (this.SF != null && this.SF != "") {
			     var value=this.SF["diagnosisMethod"];
			     if(value=="1"){
			     	 this.diagnosisMethod.setValue("血HCG");
			     }
			      if(value=="2"){
			     	this.diagnosisMethod.setValue("尿HCG");
			     }
			      if(value=="3"){
			     	this.diagnosisMethod.setValue("B超");
			     }
			      if(value=="9"){
			     	this.diagnosisMethod.setValue("其他");
			     }
			     if(this.JY){
			     	if(	this.JY["JY_508"]!="" &&	this.JY["JY_508"]!=null){
			     	    var divId = "div_" +"JY_508" + this.idPostfix;
						var fdiv = document.getElementById(divId);
			     		this.removeClass(fdiv, "x-form-invalid");
			     	}
			     	if(	this.JY["JY_512"]!="" &&	this.JY["JY_512"]!=null){
			     	    var divId2 = "div_" +"JY_512" + this.idPostfix;
						var fdiv2 = document.getElementById(divId2);
			     		this.removeClass(fdiv2, "x-form-invalid");
			     	}
			     }
			    if(this.SF["visitDoctorCode"]==null || this.SF["visitDoctorCode"]=="" ){
			    	//alert(Ext.encode(this.exContext.ids))
			    //	this.visitDoctorCode.setValue(this.exContext.ids.pregnantId);
			    }
			 }
			},
			// 第1次产前随访服务记录表
			doPrintPregnantRecord : function() {this.initDataIds=this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				if (!this.initDataIds) {
					this.initDataIds="222222222";//随便设置的值
				}
				var url = "resources/chis.prints.template.FirstVisitRecord.print?type="
						+ 1 + "&pregnantId=" + this.initDataIds
				url += "&temp=" + new Date().getTime()
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
			},
			doYZ : function() {
				if(this.SF["diagnosisMethod"]==null || this.SF["diagnosisMethod"]==""){
			        MyMessageTip.msg("提示", "妊娠确诊方法不能为空！",true);
					return false;
				}
				var highRiskLevel = document.getElementById("highRiskLevel" + this.idPostfix).value// 高危评级
				if(this.opGW!=true &&(highRiskLevel == null || highRiskLevel=="")){
					this.openHighRisknessForm();
					return false;
				}
				return true;
			}
		});