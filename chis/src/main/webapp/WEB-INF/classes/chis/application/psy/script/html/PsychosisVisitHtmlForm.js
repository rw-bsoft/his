$package("chis.application.psy.script.html");

$import("chis.script.BizHtmlFormView","chis.application.psy.script.html.PsychosisVisitHtmlTemplate","chis.script.util.widgets.MyMessageTip");
$styleSheet("chis.css.PsychosisHtml")

chis.application.psy.script.html.PsychosisVisitHtmlForm = function(cfg){
	chis.application.psy.script.html.PsychosisVisitHtmlForm.superclass.constructor.apply(this,[cfg]);
	Ext.apply(this,chis.application.psy.script.html.PsychosisVisitHtmlTemplate);
	this.loadServiceId = "chis.psychosisVisitService";
	this.loadAction = "loadHtmlVisitInfo";
	this.createFields=["visitDate","lastHospitalizationTime","nextDate","visitDoctor"];
	//this.notDicFlds=["type","visitEffect","createUnit","createUser","lastModifyUser","lastModifyUnit"];
	this.disableFlds = ["dangerousGrade_0","dangerousGrade_1","dangerousGrade_2","dangerousGrade_3","dangerousGrade_4","dangerousGrade_5",
	"symptomText","lightAffray","causeTrouble","causeAccident","selfHurt","suicide",
	"labCheckup","adverseReactionsText","reason","doccol","healingText",
	"this.medicineName_1","medicineFrequency_1","medicineDosage_1",
	"this.medicineName_2","medicineFrequency_2","medicineDosage_2",
	"this.medicineName_3","medicineFrequency_3","medicineDosage_3",
	"visitType_1","visitType_2","visitType_3","visitType_0","this.lastHospitalizationTime","lostReason"];
	this.otherDisable = [
		{fld:"symptom",type:"checkbox",control:[{key:"99",exp:'eq',field:["symptomText"]}]},
		{fld:"familySocialImpact",type:"checkbox",control:[
			{key:"1",exp:"eq",field:["lightAffray"]},
			{key:"2",exp:"eq",field:["causeTrouble"]},
			{key:"3",exp:"eq",field:["causeAccident"]},
			{key:"4",exp:"eq",field:["selfHurt"]},
			{key:"5",exp:"eq",field:["suicide"]}]},
		{fld:"isLabCheckup",type:"radio",control:[{key:'2',exp:'eq',field:["labCheckup"]}]},
		{fld:"referral",type:"radio",control:[{key:'y',exp:'eq',field:["reason","doccol"]}]},
		{fld:"healing",type:"checkbox",control:[{key:'5',exp:'eq',field:["healingText"]}]},
		{fld:"medicine",type:'radio',control:[{key:'3',exp:'ne',field:["adverseReactions_n","adverseReactions_y","this.medicineName_1","this.medicineName_2","this.medicineName_3"],disField:["adverseReactionsText","medicineFrequency_1","medicineDosage_1","medicineFrequency_2","medicineDosage_2","medicineFrequency_3","medicineDosage_3"]}]},
		{fld:"adverseReactions",type:"radio",control:[{key:'y',exp:'eq',field:["adverseReactionsText"]}]},
		{fld:"hospitalization",type:"radio",control:[{key:'2',exp:'eq',field:["this.lastHospitalizationTime"]}]},
		{fld:"visitEffect",type:"radio",control:[{key:'1',exp:'eq',mustField:["this.visitDate","this.visitDoctor","div_medicine","div_visitType"],redLabels:['SFRQ','FYYCX','BCSFFL','SFYSQM'],oppositeMF:["lostReason"]}]}
		];
	
	this.mutualExclusion = [{fld:"familySocialImpact",key:"0",other:["lightAffray","causeTrouble","causeAccident","selfHurt","suicide"]}];
	this.on("loadData",this.onLoadData,this);
	this.on("loadNoData",this.onLoadNoData,this);
}

Ext.extend(chis.application.psy.script.html.PsychosisVisitHtmlForm,chis.script.BizHtmlFormView,{
	getHTMLTemplate : function(){
		return this.getPsyVisitHTMLTemplate();
	},
	addFieldAfterRender : function() {
		// 在HTML中手动增加一些ExtJS创建的对象
		//随访日期
		var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		//知情同意 签字时间
		this.visitDate = new Ext.form.DateField({
							name : 'visitDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '随访日期',
							maxValue:curDate,
							allowBlank:false,
							invalidText : "必填字段",
							fieldLabel : "随访日期",
							renderTo : Ext.get("div_visitDate"+ this.idPostfix)
						});
		//末次出院时间
		this.lastHospitalizationTime = new Ext.form.DateField({
							name : 'lastHospitalizationTime' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '末次出院时间',
							maxValue:curDate,
							renderTo : Ext.get("div_lastHospitalizationTime"+ this.idPostfix)
						});
		//下次随访日期
		this.nextDate = new Ext.form.DateField({
							name : 'nextDate' + this.idPostfix,
							width : 200,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '下次随访日期',
							renderTo : Ext.get("div_nextDate"+ this.idPostfix)
						});	
		//随访医生签名
	    var cfg = {
			"width" : 200,
			"defaultIndex" : 0,
			"id" : "chis.dictionary.user",
			"render" : "Tree",
			"selectOnFocus" : true,
			"onlySelectLeaf":true
		}
		var deptId = this.mainApp.deptId;
		if (deptId.length > 9) {
			cfg.parentKey = deptId;
		}
		this.visitDoctor = this.createDicField(cfg);
		this.visitDoctor.fieldLabel = "医生签字";
		this.visitDoctor.allowBlank = false;
		this.visitDoctor.invalidText = "必填字段";
		this.visitDoctor.regex = /(^\S+)/
		this.visitDoctor.regexText = "前面不能有空格字符";
		this.visitDoctor.tree.expandAll();
		this.visitDoctor.render(Ext.get("div_visitDoctor"+ this.idPostfix));
		this.visitDoctor.setValue({
					key : this.mainApp.uid,
					text : this.mainApp.uname
				});
		this.visitDoctor.on("select", this.onVisitDoctorSelect, this);
		//用药情况
		var me = this;
		for(var i=1;i<4;i++){
			var fldId = "medicineName_"+i;
			this[fldId] = this.createLocalDicField({
						width : 480,
						id : fldId + this.idPostfix,
						name:fldId,
						afterSelect : function(t, record) {
							var id = t.container.id;
							var last_idx = id.lastIndexOf('_');
							var idx = id.substring(last_idx-1,last_idx);
							var tx = document.getElementById('medicineUnit_'+ idx+ me.idPostfix);
							tx.value = record.data.JLDW;
							var mfFld = document.getElementById("medicineFrequency_"+idx+me.idPostfix);
							if(mfFld){
								mfFld.disabled = false;
								me.addClass(mfFld, "x-form-invalid");
							}
							var mdFld = document.getElementById("medicineDosage_"+idx+me.idPostfix);
							if(mdFld){
								mdFld.value = record.data.YPJL;
								mdFld.disabled = false;
								if(mdFld.value == ''){
									me.addClass(mdFld, "x-form-invalid");
								}
							}
						},
						afterClear : function(t){
							var id = t.container.id;
							var last_idx = id.lastIndexOf('_');
							var idx = id.substring(last_idx-1,last_idx);
							var tx = document.getElementById('medicineUnit_'+ idx+ me.idPostfix);
							tx.value = "";
							var mfFld = document.getElementById("medicineFrequency_"+idx+me.idPostfix);
							if(mfFld){
								mfFld.value="";
								mfFld.disabled = true;
								me.removeClass(mfFld, "x-form-invalid");
							}
							var mdFld = document.getElementById("medicineDosage_"+idx+me.idPostfix);
							if(mdFld){
								mdFld.value="";
								mdFld.disabled = true;
								me.removeClass(mdFld, "x-form-invalid");
							}
						}
					});
			this[fldId].addClass("input_btline");
			this[fldId].render(Ext.get("div_" + fldId + this.idPostfix));
		}
	},
	onVisitDoctorSelect : function(combo, node){
		if (!node.attributes['key']) {
			return
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.publicService",
					serviceAction : "getManageUnit",
					method : "execute",
					body : {
						manaUnitId : node.attributes["manageUnit"]
					}
				});
		this.setVisitUnit(result.json.manageUnit);
	},
	setVisitUnit : function(manageUnit){
		var combox = document.getElementById("visitUnit"+this.idPostfix);
		if (!combox) {
			return;
		}
		combox.value=manageUnit.key;
	},
	//========上面为基本页面加载**###**下面为功能实现=加载数据-保存数据等===
	getLoadRequest : function() {
		var body = {
			visitId : this.exContext.args.visitId || ''
		};
		return body;
	},
	onLoadNoData : function(){
		this.fireEvent("medicineSelect");
		this.resetControlOtherFld();
		this.addFieldDataValidateFun(this.schema);
		delete this.saving;
	},
	onLoadData:function(entryName,body){
		this.resetControlOtherFld();
		this.addFieldDataValidateFun(this.schema);
		delete this.saving;
		var type = body[this.entryName+"_data"].type;
		if(type && type.key == '0'){
			var btns = this.form.getTopToolbar().items
			if(btns){
				var saveBtn =  btns.item(0);
				if(saveBtn){
					//saveBtn.disable();
				}
			}
		}
	},
	initFormData : function(data) {
		var fvData = data[this.entryName+'_data'];
		if(!fvData){
			fvData={};
		}
		var visitId = fvData.visitId;
		if(!visitId){
			this.op = "create";
		}else{
			this.op = "update";
		}
		var medicine = fvData.medicine;
		this.initHTMLFormData(fvData,this.schema);
		if(fvData.riskFactor){
			this.riskFactorValue = fvData.riskFactor.key;
		}
		var mData = data[this.medicineEntryName+'_list'];
		if(mData && mData.length > 0){
			this.setPsyVisitMedicineList(mData)	;
		}else{
			this.psyVisitMedicineListDoNew();
		}
		this.fieldValidate(this.schema);
		this.fireEvent("medicineSelect",medicine);
	},
	setPsyVisitMedicineList : function(pvmList){
		// 向 用药 列表中 set数据
		var fldNum = this.PsychosisVisitMedicineList.length;
		for (var i = 0, iLen = pvmList.length; i < iLen; i++) {
			if(i == 3){
				break;
			}
			for (var j = 0; j < fldNum; j++) {
				var fn = this.PsychosisVisitMedicineList[j];
				var fv = pvmList[i][fn.id];
				if (fn.type == "dic") {
					var dv = {key:fv,text:pvmList[i][(fn.id+'_text')]};
					var fIdx = i+1;
					eval("this." + fn.id + "_" + fIdx + ".setValue(dv);this."+fn.id+"_"+fIdx+".validate();");
				} else if(fn.type == "div"){
					var fIdx = i+1;
					this[fn.id+"_"+fIdx].setValue(fv);
				}else {
					var f = document.getElementById(fn.id + "_"
							+ (i + 1) + this.idPostfix);
					if (f) {
						f.value = fv || '';
						this.removeClass(f, "x-form-invalid");
					}
				}
			}
		}
	},
	psyVisitMedicineListDoNew : function(){
		var fldNum = this.PsychosisVisitMedicineList.length;
		for (var i = 0, iLen = 3; i < iLen; i++) {
			for (var j = 0; j < fldNum; j++) {
				var fn = this.PsychosisVisitMedicineList[j];
				if (fn.type == "dic") {
					var dv = '';
					var fIdx = i+1;
					eval("this." + fn.id + "_" + fIdx + ".setValue(dv);this."+fn.id+"_"+fIdx+".validate();");
				}  else if(fn.type == "div"){
					var fIdx = i+1;
					this[fn.id+"_"+fIdx].setValue();
				}else {
					var f = document.getElementById(fn.id + "_"
							+ (i + 1) + this.idPostfix);
					if (f) {
						f.value = '';
					}
				}
			}
		}
	},
	getPsyVisitMedicineList : function(){
		//获取 用药 列表数据
		var medicineListData = [];
		var fldNum = this.PsychosisVisitMedicineList.length;
		for(var i=0; i< 3; i++){
			var r = {};
			for(var j=0; j< fldNum; j++){
				var fn = this.PsychosisVisitMedicineList[j];
				var fIndex = (i+1);
				if(fn.type == "dic"){
					 var dv= eval("this." + fn.id + "_" + fIndex+ ".getValue()");
					if (typeof(dv) == "undefined" || dv == null) {
						dv = "";
					}
					r[fn.id] = dv;
				}else if(fn.type == "div"){
					var fIdx = i+1;
					r[fn.id] = this[fn.id+"_"+fIdx].getValue();
				}else{
					var f = document.getElementById(fn.id + "_" + fIndex + this.idPostfix);
					if (f) {
						r[fn.id] = f.value || '';
					}
				}
			}
			if(r.medicineName != ''){
				medicineListData.push(r);
			}
		}
		return medicineListData;
	},
	checkFldValidate : function(){
		var errFlds = Ext.query(".my .x-form-invalid");
		var eflen = errFlds.length;
		if (eflen > 0) {
			var isReturn = false;
			for (var i = 0; i < eflen; i++) {
				var fid = errFlds[i].id
				var fn = fid.substring(0, fid.indexOf("_"));
				if (fn != "visitEffect") {
					isReturn = true;
					if(document.getElementById(fid)){
						document.getElementById(fid).focus();
						if(document.getElementById(fid).type=='text'){
							document.getElementById(fid).select();
						}
					}
					//console.log("==================->"+fid)
					break;
				}
			}
			if (isReturn) {
				//Ext.Msg.alert("提示", "精神病随访 页面上有验证未通过的字段，请修改录入值");
				MyMessageTip.msg("提示", "精神病随访 页面上有验证未通过的字段，请修改录入值",true);
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	},
	getSaveData : function(){
		var body = {};
		body.visitData = this.getFormData();
		var visitId = body.visitData.visitId;
		if(!visitId){
			this.op = "create";
		}else{
			this.op = "update";
		}
		body.visitData.empiId = this.exContext.ids.empiId;
		body.visitData.phrId = this.exContext.ids.phrId;
		body.visitData.type = '0';
		body.visitData.visitEffect = '1';
		body.visitData.deleteMedicine = false;
		var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		var curUid = this.mainApp.uid;
		var curUnitId = this.mainApp.deptId;
		if(body.visitData.visitId != ''){
			body.visitData.lastModifyUser = curUid;
			body.visitData.lastModifyUnit = curUnitId;
			body.visitData.lastModifyDate = curDate;
		}else{
			body.visitData.createUser = curUid;
			body.visitData.createUnit = curUnitId;
			body.visitData.createDate = curDate;
			body.visitData.lastModifyUser = curUid;
			body.visitData.lastModifyUnit = curUnitId;
			body.visitData.lastModifyDate = curDate;
		}
		body.medicineList = this.getPsyVisitMedicineList();
		return body;
	},
	doSave : function(){
		if(this.saving){
			return
		}
		if(!this.htmlFormSaveValidate(this.schema)){
			//Ext.Msg.alert("提示", "精神病随访 页面上有验证未通过的字段，请修改录入值");
			//MyMessageTip.msg("提示", "精神病随访 页面上有验证未通过的字段，请修改录入值",true);
			return;
		}
		var saveData = this.getSaveData();
		this.fireEvent("save",saveData);
	},
	//=================业务控制===补充====================
	onReadyAffter : function(){
		//危害度
		var riskFactorFlds = document.getElementsByName("riskFactor");
		this.riskFactorValue = this.getHtmlFldValue("riskFactor");
		if(riskFactorFlds && riskFactorFlds.length > 0){
			var me = this;
			var handleFun = function(rfFld, me) {
				return function() {
					me.onRiskFactorRadioClick(rfFld, me);
				}
			}
			for(var rfi=0,rfLen=riskFactorFlds.length; rfi<rfLen; rfi++){
				var rfFld = riskFactorFlds[rfi];
				this.addEvent(rfFld, "click", handleFun(rfFld, me));
			}
		}
		//自知力
		var insightFlds = document.getElementsByName("insight");
		if(insightFlds && insightFlds.length > 0){
			var me = this;
			var handleFun = function(iFld, me) {
				return function() {
					me.onInsightRadioClick(iFld, me);
				}
			}
			for(var i=0,iLen=insightFlds.length; i<iLen; i++){
				var iFld = insightFlds[i];
				this.addEvent(iFld, "click", handleFun(iFld, me));
			}
		}
		//服药依从性 medicine
		var medicineFlds = document.getElementsByName("medicine");
		if(medicineFlds && medicineFlds.length > 0){
			var me = this;
			var handleFun = function(mFld, me) {
				return function() {
					me.onMedicineRadioClick(mFld, me);
				}
			}
			for(var mi=0,mLen=medicineFlds.length; mi<mLen; mi++){
				var mFld = medicineFlds[mi];
				this.addEvent(mFld, "click", handleFun(mFld, me));
			}
		}
		//药物不良反应
		var adverseReactionsFlds = document.getElementsByName("adverseReactions");
		if(adverseReactionsFlds && adverseReactionsFlds.length > 0){
			var me = this;
			var handleFun = function(arFld, me) {
				return function() {
					me.onAdverseReactionsRadioClick(arFld, me);
				}
			}
			for(var ari=0,arLen=adverseReactionsFlds.length; ari<arLen; ari++){
				var arFld = adverseReactionsFlds[ari];
				this.addEvent(arFld, "click", handleFun(arFld, me));
			}
		}
		//社会人际交往
		var socialFlds = document.getElementsByName("social");
		if(socialFlds && socialFlds.length > 0){
			var me = this;
			var handleFun = function(sFld, me) {
				return function() {
					me.onSocialRadioClick(sFld, me);
				}
			}
			for(var si=0,sLen=socialFlds.length; si<sLen; si++){
				var sFld = socialFlds[si];
				this.addEvent(sFld, "click", handleFun(sFld, me));
			}
		}
		//对用药情况中 次数、每次剂量 字段增加验证
		//次数 medicineFrequency (string not-null=1 length=20)
		
		for(var mi=1;mi<4;mi++){
			var me = this;
			var fid = "medicineFrequency_"+mi+me.idPostfix;
			var fld = document.getElementById(fid);
			var maxLength = 20;
			var notNull = true;
			var alias = "每日(月)次数"
			var handleFun = function(maxLength, notNull,
					alias, obj, me) {
				return function() {
					me.validateString(maxLength, notNull,
							alias, obj, me);
				}
			}
			this.addEvent(fld, "change", handleFun(
							maxLength, notNull, alias,
							fld, me));
		}
		//每次剂量 medicineDosage (int not-null=1 length=10)
		for(var mdi=1;mdi<4;mdi++){
			var me = this;
			var fid = "medicineDosage_"+mdi+me.idPostfix;
			var fld = document.getElementById(fid);
			var maxValue = undefined;
			var minValue = undefined;
			var notNull = true;
			var length = 10;
			var alias = "每次剂量";
			var handleFun = function(length,minValue, maxValue,
					notNull, id, alias, obj, me) {
				return function() {
					me.validateInt(length,minValue, maxValue,
							notNull, id, alias, obj, me);
				}
			}
			this.addEvent(fld, "change", handleFun(length,
							minValue, maxValue, notNull,
							"medicineDosage", alias, fld, me));
		}
		//转归
		var visitEffectFlds = document.getElementsByName("visitEffect");
		if(visitEffectFlds && visitEffectFlds.length > 0){
			var me = this;
			var handleFun = function(vFld, me) {
				return function() {
					me.onVisitEffectRadioClick(vFld, me);
				}
			}
			for(var vi=0,vLen=visitEffectFlds.length; vi<vLen; vi++){
				var vFld = visitEffectFlds[vi];
				this.addEvent(vFld, "click", handleFun(vFld, me));
			}
		}
	},
	onRiskFactorRadioClick : function(rfFld,me){
		var rfv = rfFld.value;
		var dgv = rfv;
		if(rfFld.checked && !this.FRType && rfv == this.riskFactorValue){
			rfFld.checked = false;
			dgv = "0";
			this.FRType = true;
		}else{
			rfFld.checked = true;
			me.setVisitType();
			this.riskFactorValue = rfv;
			this.FRType = false;
		}
		var dgId = "dangerousGrade_"+dgv+me.idPostfix;
		var dgFld = document.getElementById(dgId)
		if(dgFld){
			if(dgFld.value = dgv){
				dgFld.checked = true;
			}
		}
	},
	onInsightRadioClick : function(iFld,me){
		me.setVisitType();
	},
	onMedicineRadioClick:function(mFld,me){
		var mv = mFld.value;
		me.fireEvent("medicineSelect",mv);
	},
	onAdverseReactionsRadioClick : function(arFld,me){
		me.setVisitType();
	},
	onSocialRadioClick : function(sFld, me){
		me.setVisitType();
	},
	onVisitEffectRadioClick : function(vFld,me){
		me.setVisitType();
	},
	setVisitType : function() {
		// 随访分类
		var dangerousGrade = this.getHtmlFldValue("dangerousGrade");
		var insight = this.getHtmlFldValue("insight");
		var adverseReactions = this.getHtmlFldValue("adverseReactions");
		var social = this.getHtmlFldValue("social");
		var visitEffect = this.getHtmlFldValue("visitEffect");
		var visitType = '';
		if(visitEffect == '2'){
			visitType = '0';//未访到
			this.setHtmlFldValue("visitType",visitType);
			return;
		}
		// 转归为 2 暂时失访 --> 未访到 9 终止管理时，注销档案不正生成随访计划
		// 危险性为3～5级 或 自知力缺乏 或 有药物不良反应-->不稳定
		if (dangerousGrade > '2' || insight == '3'
				|| adverseReactions == 'y') {
			visitType = '1';//不稳定
			this.setHtmlFldValue("visitType",visitType);
			return;
		}
		// 若危险性为1～2级 或 自知力不全 或 社交能力较差 -->基本稳定
		if (dangerousGrade == '1' || dangerousGrade == '2'
				|| insight == '2' || social == '3') {
			visitType = '2';//基本稳定
			this.setHtmlFldValue("visitType",visitType);
			return;
		}
		// 危险性为0级，且 自知力完全，社会能力处于一般 或 良好，无药物不良反应 -->稳定
		if (dangerousGrade == '0' && insight == '1'
				&& (social == '1' || social == '2')
				&& adverseReactions == 'n') {
			visitType = '3'//稳定
			this.setHtmlFldValue("visitType",visitType);
			return;
		}
		// 没有任何状况，默认为 -稳定
		visitType = 3;//稳定
		this.setHtmlFldValue("visitType",visitType);
		return;
	}
});