$package("chis.application.quality.script")

$import("chis.script.BizTableFormView")

chis.application.quality.script.QualityControl_form = function(cfg) {
	cfg.autoFieldWidth = false
	this.autoLoadData = true;
	cfg.fldDefaultWidth = 135;
	cfg.labelWidth = 100;
	cfg.colCount = 3;
	chis.application.quality.script.QualityControl_form.superclass.constructor.apply(
			this, [cfg]);
  this.disablePagingTbr = true;
}
Ext.extend(chis.application.quality.script.QualityControl_form,
	 chis.script.BizTableFormView, {
 	onReady: function(){
 			chis.application.quality.script.QualityControl_form.superclass.onReady.call(this);
 			    var form = this.form.getForm()
				var XMLB = form.findField("XMLB")//添加事件生成项目标识
				if (XMLB) {
					XMLB.on("select", this.onVisitBlur, this)
					XMLB.on("blur", this.onVisitBlur, this)
				}
				this.XMLB = XMLB
				var XMMC = form.findField("XMMC")//添加事件生成项目标识
				if (XMMC) {
					XMMC.on("select", this.onVisitBlur, this)
					XMMC.on("blur", this.onVisitBlur, this)
				}
				this.XMMC = XMMC;
				var XMZLB =form.findField("XMZLB");
				this.XMZLB=XMZLB;
 	},
    doSave: function(){
    	        var form = this.form.getForm()
				var XMMC = form.findField("XMMC")//添加事件生成项目标识
				if(this.saving){
					return
				}
				var values = this.getFormData();
				if(!values){
					return;
				}
				Ext.apply(this.data,values);
				this.saveToServer(values)
				this.fireEvent("save",values);
	  },
    onVisitBlur: function(key){
    	   var mc="";
    	   var lb="";
    	   var bs="";
    	  this.XMZLB.disabled =false;
    	  this.XMZLB.clearCls="x-form-invalid" ;
    	   this.XMZLB.cls  ="x-form-invalid" ;
			if( this.XMMC && this.XMLB){
				var mcValue= this.XMMC.getValue();
				var lbValue= this.XMLB.getValue();
				if(mcValue!=null && mcValue !="" && lbValue!=null && lbValue!=""){
					  
					bs=lbValue+"_"+mcValue;
					var form = this.form.getForm()
					var XMBS = form.findField("XMBS")//添加事件生成项目标识
					if (XMBS) {
						  XMBS.setValue(bs);
					}
				}
			}
	  },doClose :function(){
	  	this.fireEvent("formClose", this);
	  } ,doModify:function(){
	  	 this.op="create";
	      this.doNew();
		  this.initDataId="";
		  this.loadData();
		  this.fireEvent("modify",this);
	  }
   })