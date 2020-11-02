$package("chis.application.tr.script.confirmed")

$import("chis.script.BizTableFormView")

chis.application.tr.script.confirmed.TumourConfirmedForm = function(cfg){
	cfg.autoLoadSchema = false;
    cfg.autoLoadData = false;
    cfg.autoFieldWidth = false;
    cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	chis.application.tr.script.confirmed.TumourConfirmedForm.superclass.constructor.apply(this,[cfg]);
	this.on("doNew",this.onDoNew,this);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.confirmed.TumourConfirmedForm,chis.script.BizTableFormView,{
	onReady : function(){
		chis.application.tr.script.confirmed.TumourConfirmedForm.superclass.onReady.call(this);
		var frm = this.form.getForm();
		var cancerCaseFld = frm.findField("cancerCase");
		if(cancerCaseFld){
			cancerCaseFld.on("change",this.onCancerCaseChange,this);
		}
	},
	onCancerCaseChange : function(radioGroup, checkedRadio){
		var frm = this.form.getForm();
		if (!radioGroup) {
			radioGroup = frm.findField("cancerCase");
		}
		var ccVal = radioGroup.getValue();
		if(ccVal == "2" && this.cancerCase=='1'){
			var confirmedSourceFld = frm.findField("confirmedSource");
			if(confirmedSourceFld){
				this.oldCSv = {key:confirmedSourceFld.getValue(),text:confirmedSourceFld.getRawValue()};
				confirmedSourceFld.setValue({key:"3",text:"癌前期"});
			}
		}else{
			if(this.oldCSv){
				var confirmedSourceFld = frm.findField("confirmedSource");
				if(confirmedSourceFld){
					confirmedSourceFld.setValue(this.oldCSv);
				}
			}
		}
	},
	turnTCInitData : function(){
		if(this.exContext.args){
			var tcrData = this.exContext.args.tcrData;
			if(tcrData){
				this.initFormData(tcrData);
				if(tcrData.cancerCase){
					this.cancerCase = tcrData.cancerCase.key;
				}
				this.getControl(tcrData)
			}
			var confirmedSource = tcrData.confirmedSource.key;
			//var from = this.exContext.args.from;
			var frm = this.form.getForm();
			if(confirmedSource == "1" || confirmedSource == "2"){
				var highRiskTypeFld = frm.findField("highRiskType");
				if(highRiskTypeFld){
					highRiskTypeFld.disable();
				}
				var highRiskSourceFld = frm.findField("highRiskSource");
				if(highRiskSourceFld){
					highRiskSourceFld.disable();
				}
			}
			var TPtoTC = tcrData.TPtoTC;
			if(TPtoTC && TPtoTC == true || (tcrData.cancerCase && tcrData.cancerCase.key == '2')){
				var cancerCaseFld = frm.findField("cancerCase");
				if(cancerCaseFld){
					cancerCaseFld.disable();
				}
			}
		}
	},
	onDoNew : function(form){
		this.turnTCInitData();
	},
	getLoadRequest:function(){
		var body = {};
		if(this.exContext.args){
			var tcrData = this.exContext.args.tcrData;
			body.empiId = tcrData.empiId;
			body.highRiskType = tcrData.highRiskType.key;
			body.THRID=tcrData.THRID;
			body.confirmedSource = tcrData.confirmedSource;
		}
		return body;
	},
	onLoadData : function(entryName,body){
		this.turnTCInitData();
		var turnConfirmed = this.exContext.args.turnConfirmed;
		if(turnConfirmed && turnConfirmed == true){
			this.exContext.control.update=true
			this.resetButtons();
		}
		if(body.cancerCase){
			this.cancerCase = body.cancerCase.key;
		}
	},
	getSaveRequest:function(saveData){
		var recordId = this.exContext.args.recordId;
		if(recordId){
			saveData.recordId = recordId;
		}
		saveData.turnConfirmed = this.exContext.args.turnConfirmed || false;
		if(!saveData.status || saveData.status == ''){
			saveData.status='0'
		}
		return saveData;
	},
	getControl : function(tcrData){
		var loadCfg = {
			serviceId:"chis.tumourConfirmedService",
			serviceAction:"GetTCRControl",
			method:"execute",
			body:tcrData,
			module:this._mId   //增加module的id
		}
		util.rmi.jsonRequest(loadCfg,
			function(code,msg,json){
				if(code > 300){
					return
				}
				var body = json.body;
				if(body){
					this.exContext.control = body;
					this.resetButtons();
				}
			},
			this)
	}
});