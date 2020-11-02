$package("chis.application.tr.script.highRisk")

$import("chis.script.BizTableFormView")

chis.application.tr.script.highRisk.TumourHighRiskBaseForm = function(cfg){
	cfg.colCount = 2;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	//cfg.autoLoadData = false;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = false;
	cfg.showButtonOnTop = true;
	chis.application.tr.script.highRisk.TumourHighRiskBaseForm.superclass.constructor.apply(this,[cfg]);
	this.on("loadDataByLocal",this.onLoadDataByLocal,this);
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskBaseForm,chis.script.BizTableFormView,{
	/**
	 * 功能：去除array数组中重复的元素
	 * @param  array 为一个数组
	 */
	getUniqueElementArray : function(array){
		var temp = {}, len = array.length;
	    for(var i=0; i < len; i++)  {
	        var tmp = array[i];
	        if(!temp.hasOwnProperty(tmp)) {
	            temp[array[i]] = "my god";
	        }
	    }
	    len = 0;
	    var tempArr=[];
	    for(var i in temp) {
	        tempArr[len++] = i;
	    }
	    return tempArr;
	},
	onLoadDataByLocal : function(entryName,data,op){
		if(this.exContext.args.turnHighRisk && op == "create"){
			var tsData = this.exContext.args.tsData;
			if(tsData){
				var pData = {} 
				pData.year = tsData.year;
				pData.highRiskType = tsData.highRiskType;
				pData.highRiskSource = tsData.highRiskSource;
				pData.turnHighRiskDoctor = tsData.screeningDoctor;
				var frm = this.form.getForm();
				var highRiskFactorFld = frm.findField("highRiskFactor");
				if(highRiskFactorFld){
					if(tsData.highRiskFactor){
						var hrfv = highRiskFactorFld.getValue();
						if(hrfv){
							var tsHRF = tsData.highRiskFactor;
							var hrfs = hrfv.split(";");
							var myHRFes = [];
							for(var i=0,len=hrfs.length;i<len;i++){
								if(tsHRF.indexOf(hrfs[i]) == -1){
									myHRFes.push(hrfs[i]);
								}
							}
							if(myHRFes.length > 0){
								pData.highRiskFactor = myHRFes.join(";")+";"+tsData.highRiskFactor;
							}else{
								pData.highRiskFactor = tsData.highRiskFactor;
							}
						}else{
							pData.highRiskFactor = tsData.highRiskFactor;
						}
					}
				}
				this.initFormData(pData);
			}
		}
		if(this.exContext.args.turnHighRisk){
			this.exContext.control.update=true
			this.resetButtons();
		}
	},
	getSaveRequest:function(savaData){
		if(this.exContext.args.turnHighRisk){
			savaData.turnHighRisk = this.exContext.args.turnHighRisk;
			savaData.tumourScreeningRecordId = this.exContext.args.tsData.recordId;
		}
		savaData.createStatus = "1";//建卡标识
		return savaData;
	},
	initFormData : function(data){
		chis.application.tr.script.highRisk.TumourHighRiskBaseForm.superclass.initFormData.call(this,data);
		var phrIdField = this.form.getForm().findField("phrId");
		if (!phrIdField || !phrIdField.getValue()) {
			phrIdField.setValue(this.exContext.ids.phrId);
		}
		var highRiskType = data.highRiskType;
		var managerGroup = data.managerGroup;
		var highRiskTypeFld = this.form.getForm().findField("highRiskType")
		if(highRiskType && highRiskTypeFld){
			highRiskTypeFld.disable();
		}else{
			highRiskTypeFld.enable();
		}
		var managerGroupFld = this.form.getForm().findField("managerGroup");
		if(managerGroup && managerGroupFld){
			managerGroupFld.disable();
		}else{
			managerGroupFld.enable();
		}
	},
	onReady : function(){
		chis.application.tr.script.highRisk.TumourHighRiskBaseForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("manaDoctorId").on("select",
						this.onManaDoctorIdSelect, this);
		form.findField("highRiskFactor").on("blur",this.onHighRiskFactorBlur,this);
	},
	onManaDoctorIdSelect : function(combo, node) {
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
		this.setManaUnit(result.json.manageUnit);
	},

	setManaUnit : function(manageUnit) {
		var combox = this.form.getForm().findField("manaUnitId");
		if (!combox) {
			return;
		}
		if (!manageUnit) {
			combox.enable();
			combox.reset();
			return;
		}
		if (manageUnit.key.length >= 11) { // ****责任医生所在管理单位为团队****
			combox.setValue(manageUnit);
			combox.disable();
		} else {
			combox.enable();
			combox.reset();
		}
	},
	
	onHighRiskFactorBlur : function(){
		var frm = this.form.getForm();
		var hrfFld = frm.findField("highRiskFactor");
		if(hrfFld){
			var hrfv = hrfFld.getValue();
			var hrfuArr = this.getUniqueElementArray(hrfv.split(";"));
			hrfFld.setValue(hrfuArr.join(";"));
		}
	}
});