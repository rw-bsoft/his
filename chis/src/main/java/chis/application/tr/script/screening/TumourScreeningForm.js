$package("chis.application.tr.script.screening")

$import("chis.script.BizTableFormView")

chis.application.tr.script.screening.TumourScreeningForm = function(cfg){
	cfg.autoLoadSchema = false;
    cfg.isCombined = true;
    cfg.autoLoadData = false;
    cfg.showButtonOnTop = true;
    cfg.autoWidth = true;
	cfg.colCount = 2;
	chis.application.tr.script.screening.TumourScreeningForm.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningForm,chis.script.BizTableFormView,{
	doSave:function(){
		this.data.empiId = this.exContext.args.empiId;
		this.data.phrId = this.exContext.args.phrId;
		chis.application.tr.script.screening.TumourScreeningForm.superclass.doSave.call(this);
	},
	getSaveRequest:function(savaData){
		savaData.highRiskType_text = this.form.getForm().findField("highRiskType").getRawValue();
		return savaData;
	},
	loadData : function(){
		
	},
	onReady : function(){
		chis.application.tr.script.screening.TumourScreeningForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		form.findField("highRiskFactor").on("blur",this.onHighRiskFactorBlur,this);
	},
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