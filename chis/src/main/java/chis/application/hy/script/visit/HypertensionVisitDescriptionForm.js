$package("chis.application.hy.script.visit");

$import("util.Accredit", "chis.script.BizTableFormView");

chis.application.hy.script.visit.HypertensionVisitDescriptionForm = function(cfg) {
    cfg.autoFieldWidth = false;
	cfg.colCount = 2;
    cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 300;
	chis.application.hy.script.visit.HypertensionVisitDescriptionForm.superclass.constructor
			.apply(this, [cfg]);
	this.loadServiceId = "chis.hypertensionVisitService";
	this.loadAction = "getHyperVisitDescriptionByVisitId";
	this.saveServiceId = "chis.hypertensionVisitService";
	this.saveAction = "saveHyperVisitDescription";
	this.on("loadData", this.onLoadData, this);
};

Ext.extend(chis.application.hy.script.visit.HypertensionVisitDescriptionForm,
		chis.script.BizTableFormView, {
	onReady : function() {
        chis.application.hy.script.visit.HypertensionVisitDescriptionForm.superclass.onReady
                        .call(this);
        var form = this.form.getForm();
        form.findField("tongueApprence").on("select",
        this.onTongueApprenceSelect, this);
        this.setBtnable();
    },
    onTongueApprenceSelect : function(combo, record, index) {
        var value = combo.getValue();
        var valueArray = value.split(",");
        if (valueArray.indexOf("01") != -1) {
            combo.clearValue();
	        if (record.data.key == "01") {
	            combo.setValue({
	                key : "01",
	                text : "正常"
	            });
	        } else {
	            combo.setValue(record.data.key);
	        }
        }
        if (value == "") {
            combo.setValue({
                key : "01",
                text : "正常"
            });
        }
    },

    getLoadRequest : function(){
    	var body={};
    	body.phrId = this.exContext.ids.phrId;
    	body.empiId = this.exContext.ids.empiId;
    	body.visitId = this.exContext.args.visitId;
    	body.planId = this.exContext.args.planId;
    	return body;
    },
    
    onLoadData : function(entryName,body){
    	this.initDataId = body.recordId;
    	this.setBtnable();
    },
    
    getSaveRequest : function(saveData){
    	saveData.phrId = this.exContext.args.phrId;
        saveData.visitId = this.exContext.args.visitId;
        return saveData;
    },
    
    setBtnable : function(){
      if (!this.form.getTopToolbar()) {
          return;
        }
    	var btns = this.form.getTopToolbar().items;
		if(!btns.item(0)){
			return;
		}
		var rdStatus = this.exContext.ids.recordStatus;
		if(rdStatus && rdStatus == '1'){
			for(var i = 0;i<btns.getCount();i++){
				var btn = btns.item(i);
				if(btn){
					btn.disable();
				}
			}
		}
    }
	
});