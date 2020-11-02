$package("chis.application.tb.script");

$import("chis.script.BizHtmlFormView","chis.application.tb.script.TuberculosisVisitHtmlTemplate","chis.script.util.widgets.MyMessageTip");
$styleSheet("chis.css.PsychosisHtml")

chis.application.tb.script.TuberculosisVisitHtmlForm = function(cfg){
	chis.application.tb.script.TuberculosisVisitHtmlForm.superclass.constructor.apply(this,[cfg]);
	Ext.apply(this,chis.application.tb.script.TuberculosisVisitHtmlTemplate);
	
	this.createFields=["visitDate","stopTreatmentTime","nextDate","visitDoctor","assessDoctor"];
	this.otherDisable = [
		{fld:"symptomSign",type:"checkbox",control:[{key:"0",exp:'ne',disField:["symptomSign_1","symptomSign_2",
			"symptomSign_3","symptomSign_4","symptomSign_5","symptomSign_6","symptomSign_7", "symptomSign_8",
			"symptomSign_9","symptomSign_10","otherSymptomSign"]}]},
		{fld:"medicineBadReaction",type:"radio",control:[{key:"2",exp:"eq",field:["medicineBadReactionDesc"]}]},
		{fld:"complicationComorbidity",type:"radio",control:[{key:"2",exp:"eq",field:["complicationComorbidityDesc"]}]}
	];
}

Ext.extend(chis.application.tb.script.TuberculosisVisitHtmlForm,chis.script.BizHtmlFormView,{
//	getLoadRequest : function() {
//		var body = {
//			fieldName : "RecordId",
//			fieldValue : this.exContext.args.recordId
//		};
//		return body;
//	},
	getHTMLTemplate : function(){
		return this.getTBVisitHTMLTemplate();
	},
	addFieldAfterRender : function() {
		// 在HTML中手动增加一些ExtJS创建的对象
		//随访日期
		var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		//知情同意 签字时间
		this.visitDate = new Ext.form.DateField({
			name : 'visitDate' + this.idPostfix,
			width : 150,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '随访日期',
			maxValue:curDate,
			allowBlank:false,
			invalidText : "必填字段",
			fieldLabel : "随访日期",
			renderTo : Ext.get("div_visitDate"+ this.idPostfix)
		});
		this.nextDate = new Ext.form.DateField({
			name : 'nextDate' + this.idPostfix,
			width : 150,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '下次随访时间',
			minValue:curDate,
			fieldLabel : "时间:",
			allowBlank:false,
			invalidText : "必填字段",
			renderTo : Ext.get("div_nextDate"+ this.idPostfix)
		});
		this.stopTreatmentTime = new Ext.form.DateField({
			name : 'stopTreatmentTime' + this.idPostfix,
			width : 150,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '停止治疗时间',
			maxValue:curDate,
			fieldLabel : "停止治疗时间",
			renderTo : Ext.get("div_stopTreatmentTime"+ this.idPostfix)
		});
		// 随访医生签名
		this.visitDoctor = this.createDicField({
					"width" : 150,
					"defaultIndex" : 0,
					"id" : "chis.dictionary.user01",
					"render" : "Tree",
					onlySelectLeaf : true,
					"selectOnFocus" : true,
					allowBlank:false,
					invalidText : "必填字段",
					parentKey : this.mainApp.topUnitId
				});
		Ext.apply(this.visitDoctor, {
					name : "visitDoctor"
				})
		this.visitDoctor.fieldLabel = "随访医生";
		this.visitDoctor.render("div_visitDoctor" + this.idPostfix);
		// 评估医生签名
		this.assessDoctor = this.createDicField({
					"width" : 150,
					"defaultIndex" : 0,
					"id" : "chis.dictionary.user01",
					"render" : "Tree",
					onlySelectLeaf : true,
					"selectOnFocus" : true,
					allowBlank:false,
					invalidText : "必填字段",
					parentKey : this.mainApp.topUnitId
				});
		Ext.apply(this.assessDoctor, {
					name : "assessDoctor"
				})
		this.assessDoctor.fieldLabel = "评估医生";
		this.assessDoctor.render("div_assessDoctor" + this.idPostfix);
	},
	doSave : function(){
		if(this.saving){
			return
		}
		if(!this.htmlFormSaveValidate(this.schema)){
			return;
		}
		if(!this.checkFldValidate()){
			return;
		}
		chis.application.tb.script.TuberculosisVisitHtmlForm.superclass.doSave.call(this);
	},
	checkFldValidate : function(){
		var errFlds = Ext.query(".my .x-form-invalid");
		var eflen = errFlds.length;
		if (eflen > 0) {
			var isReturn = false;
			for (var i = 0; i < eflen; i++) {
				var fid = errFlds[i].id
				var fn = fid.substring(0, fid.indexOf("_"));
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
			if (isReturn) {
				MyMessageTip.msg("提示", "肺结核患者随访 页面上有验证未通过的字段，请修改录入值",true);
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	},
	getFormData : function() {
		if(!this.validate()){
			return
		}
		if(!this.schema){
			return
		}
		var values = chis.application.tb.script.TuberculosisVisitHtmlForm.superclass.getFormData.call(this);
		this.initDataId = values.visitId;
		values.RecordId = this.exContext.args.recordId;
		values.empiId = this.exContext.ids.empiId;
		return values;
	},
	doDelete : function() {
		this.list.doRemove();
	}
});