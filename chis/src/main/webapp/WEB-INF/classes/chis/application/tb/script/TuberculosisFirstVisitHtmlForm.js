$package("chis.application.tb.script");

$import("chis.script.BizHtmlFormView","chis.application.tb.script.TuberculosisFirstVisitHtmlTemplate","chis.script.util.widgets.MyMessageTip");
$styleSheet("chis.css.PsychosisHtml")

chis.application.tb.script.TuberculosisFirstVisitHtmlForm = function(cfg){
	chis.application.tb.script.TuberculosisFirstVisitHtmlForm.superclass.constructor.apply(this,[cfg]);
	Ext.apply(this,chis.application.tb.script.TuberculosisFirstVisitHtmlTemplate);
	this.createFields=["visitDate","takeTime","nextDate","visitDoctor"];
	this.otherDisable = [
		{fld:"symptomSign",type:"checkbox",control:[{key:"0",exp:'ne',disField:["symptomSign_1","symptomSign_2",
			"symptomSign_3","symptomSign_4","symptomSign_5","symptomSign_6","symptomSign_7", "symptomSign_8",
			"symptomSign_9","otherSymptomSign"]}]}
	];
}

Ext.extend(chis.application.tb.script.TuberculosisFirstVisitHtmlForm,chis.script.BizHtmlFormView,{
	getLoadRequest : function() {
		var body = {
			fieldName : "RecordId",
			fieldValue : this.exContext.args.recordId
		};
		return body;
	},
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
		this.takeTime = new Ext.form.DateField({
			name : 'takeTime' + this.idPostfix,
			width : 160,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '取药时间',
			maxValue:curDate,
			fieldLabel : "取药时间",
			renderTo : Ext.get("div_takeTime"+ this.idPostfix)
		});
		this.nextDate = new Ext.form.DateField({
			name : 'nextDate' + this.idPostfix,
			width : 150,
			altFormats : 'Y-m-d',
			format : 'Y-m-d',
			emptyText : '下次随访时间',
			minValue:curDate,
			allowBlank:false,
			invalidText : "必填字段",
			fieldLabel : "下次随访时间",
			renderTo : Ext.get("div_nextDate"+ this.idPostfix)
		});
		// 随访医生签名
		this.visitDoctor = this.createDicField({
					"width" : 150,
					"defaultIndex" : 0,
					"id" : "chis.dictionary.user01",
					"render" : "Tree",
					onlySelectLeaf : true,
					"selectOnFocus" : true,
					parentKey : this.mainApp.topUnitId
				});
		Ext.apply(this.visitDoctor, {
					name : "visitDoctor"
				})
		this.visitDoctor.fieldLabel = "随访医生";
		this.visitDoctor.render("div_visitDoctor" + this.idPostfix);
	},
	symptomSignChange : function(fld) {
		var form = this.form.form;
		var symptomSign = form.findField("symptomSign");
		var val = symptomSign.getValue();
		if(val==0) {
			var symptomSignDesc = form.findField("otherSymptomSign");
			symptomSignDesc.disable();
		}else{
			symptomSignDesc.enable();
		}
	},
	doSave : function() {
		if(this.saving){
			return
		}
		if(!this.htmlFormSaveValidate(this.schema)){
			return;
		}
		if(!this.checkFldValidate()){
			return;
		}
		chis.application.tb.script.TuberculosisFirstVisitHtmlForm.superclass.doSave.call(this);
	},
	saveToServer:function(saveData){
		if(!this.fireEvent("beforeSave",this.entryName,this.op,saveData)){
			return;
		}
		if (!this.initDataId) {
			this.op = "create";
		} else {
			this.op = "update";
		}
		this.saving = true
		this.form.el.mask("正在保存数据...","x-mask-loading")
		var saveRequest = this.getSaveRequest(saveData);
		var saveCfg = {
			serviceId:this.saveServiceId,
			method:this.saveMethod,
			op:this.op,
			schema:this.entryName,
			module:this._mId,  //增加module的id
			body:saveRequest
		}
		this.fixSaveCfg(saveCfg);
		util.rmi.jsonRequest(saveCfg,
			function(code,msg,json){
				this.form.el.unmask()
				this.saving = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.saveToServer,[saveRequest],json.body);
					this.fireEvent("exception", code, msg, saveData); // **进行异常处理
					return
				}
				Ext.apply(this.data,saveData);
				if(!this.initDataId){
					this.initDataId = saveData.visitId;
				}
				this.fireEvent("save",this.entryName,this.op,json,this.data);
				this.afterSaveData(this.entryName, this.op, json,this.data);
				this.op = "update"
			},
			this)//jsonRequest
	},
	checkFldValidate : function() {
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
				break;
			}
			if (isReturn) {
				MyMessageTip.msg("提示", "肺结核患者第一次入户随访 页面上有验证未通过的字段，请修改录入值",true);
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	},
	getFormData : function() {
		var values = chis.application.tb.script.TuberculosisFirstVisitHtmlForm.superclass.getFormData.call(this);
		values.RecordId = this.RecordID||this.exContext.args.recordId;
		values.empiId = this.exContext.ids.empiId;
		this.saveData = values;
		return values;
	}
});