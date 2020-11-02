$package("chis.application.tr.script.phq");

$import("chis.script.BizTableFormView");

chis.application.tr.script.phq.TumourHealthEducationCourseForm = function(cfg){
	cfg.colCount = 2;
	cfg.width=750;
	chis.application.tr.script.phq.TumourHealthEducationCourseForm.superclass.constructor.apply(this,[cfg]);
	this.on("beforeCreate",this.onBeforeCreate,this);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.phq.TumourHealthEducationCourseForm,chis.script.BizTableFormView,{
	onBeforeCreate: function(){
		var frm = this.form.getForm();
		var startDateFld = frm.findField("startDate");
		if(startDateFld){
			var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
			startDateFld.setMinValue(curDate);
			startDateFld.validate();
		}
		this.exContext.control = {"create":true,"update":true};
		this.resetButtons();
	},
	onLoadData : function(entryName,body){
		var createUser = body.createUser.key;
		var curUser = this.mainApp.uid;
		var jobId = this.mainApp.jobId
		var update = false;
		if(createUser == curUser || curUser=='system'){
			update = true;
		}else if(jobId == "chis.05" || jobId == "chis.15"){//团队 防保员
			update = true;
		}
		this.exContext.control = {"create":true,"update":update};
		this.resetButtons();
		var frm = this.form.getForm();
		var startDateFld = frm.findField("startDate");
		var sdv = startDateFld.getValue();
		if(sdv){
			startDateFld.setMinValue(sdv);
			startDateFld.validate();
		}else{
			var curDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
			startDateFld.setMinValue(curDate);
			startDateFld.validate();
			
		}
	},
	afterSaveData : function(entryName, op, json, data) {
		this.win.hide();
	},
	onReady : function(){
		chis.application.tr.script.phq.TumourHealthEducationCourseForm.superclass.onReady.call(this);
		var frm = this.form.getForm();
		var otherAspectFld = frm.findField("otherAspect");
		if(otherAspectFld){
			otherAspectFld.disable();
		}
		var aspectFld = frm.findField("aspect");
		if(aspectFld){
			var aItems = aspectFld.items;
			for(var i = 0,len = aItems.length; i< len; i++){
				var box = aItems[i];
				box.listeners = {
					'check':function(checkedBox,checked){
						this.onAspectFldItemCheck(checkedBox,checked);
					},
					scope:this
				}
			}
		}
		var contentFld = frm.findField("content");
		if(contentFld){
			contentFld.on("select",this.onContentFldSelect,this);
		}
	},
	onAspectFldItemCheck : function(checkedBox,checked){
		var frm = this.form.getForm();
		var otherAspectFld = frm.findField("otherAspect");
		if(!otherAspectFld){
			return;
		}
		var aspectFld = frm.findField("aspect");
		if(!aspectFld){
			return;
		}
		var afv = aspectFld.getValue();
		if(checkedBox.inputValue=="9" && checked){
			otherAspectFld.enable();
		}else{
			if(afv.indexOf('9') == -1){
				otherAspectFld.setValue();
				otherAspectFld.disable();
			}else{
				otherAspectFld.enable();
			}
		}
	},
	onContentFldSelect : function(){
		var frm = this.form.getForm();
		var contentFld = frm.findField("content");
		if(contentFld){
			var cv = contentFld.getValue();
			var typeFld = frm.findField("type");
			if(typeFld){
				if(cv == '01'){
					typeFld.setValue();
					typeFld.enable();
				}else if(cv == "02"){
					typeFld.setValue("1");
					typeFld.enable();
				}else if(cv == "03"){
					typeFld.setValue("2");
					typeFld.enable();
				}else if(cv == "04"){
					typeFld.setValue("3");
					typeFld.enable();
				}else if(cv == "05"){
					typeFld.setValue("4");
					typeFld.enable();
				}else if(cv == "06"){
					typeFld.setValue("5");
					typeFld.enable();
				}else if(cv == "07"){
					typeFld.setValue("6");
					typeFld.enable();
				}else if(cv == "08"){
					typeFld.setValue("5,6");
					typeFld.enable();
				}else{
					typeFld.setValue();
					typeFld.disable();
				}
			}
		}
	}
});