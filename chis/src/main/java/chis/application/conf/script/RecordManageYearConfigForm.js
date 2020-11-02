$package("chis.application.conf.script")
$import("chis.application.conf.script.SystemConfigUtilForm")
chis.application.conf.script.RecordManageYearConfigForm = function(cfg) {
	chis.application.conf.script.RecordManageYearConfigForm.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.RecordManageYearConfigForm, chis.application.conf.script.SystemConfigUtilForm,
	{
    
         onReady : function (){
            chis.application.conf.script.RecordManageYearConfigForm.superclass.onReady.call(this);
            var form = this.form.getForm();
            var startMonth = form.findField("startMonth");
            if(startMonth){
              startMonth.setEditable(false);
              startMonth.on("select",this.changeManageMonth,this);
            }
            
            var endMonth = form.findField("endMonth");
            if(endMonth){
              endMonth.setEditable(false);
              endMonth.on("select",this.changeManageMonth,this);
            }
            
            var planMode = form.findField("planMode");
            if(planMode){
              planMode.on("select",this.changePlanMode,this);
            }
         },
         
         changeManageMonth : function (combo,record,index){
	          var fieldName = combo.getName();
	          var form = this.form.getForm();
	          if(fieldName == "startMonth"){
	             var endMonth =  form.findField("endMonth");
                 if(endMonth){
                     if(index < 1)
                      index = combo.getStore().getCount() ;
                      var endRecord = combo.getStore().getAt(index-1);
                      endMonth.setValue({
                      "key":endRecord.get("key"),
                      "text":endRecord.get("text")
                      });
                 }
	          }else{
                 var startMonth =  form.findField("startMonth");
                 if(startMonth){
                     if(index == combo.getStore().getCount()-1)
                      index = -1  ;
                      var startRecord = combo.getStore().getAt(index+1);
                      startMonth.setValue({
                      "key":startRecord.get("key"),
                      "text":startRecord.get("text")
                      });
                 }
             }
         },
         
         validate : function() {
            if(!this.form.getForm().isValid())
             return false;
            var form = this.form.getForm();
            var startMonth = form.findField("startMonth");
            if(!startMonth)
              return 
            var start = startMonth.getValue();
            if(!start){
              startMonth.markInvalid("年度开始月份不能为空！");
              return false;
            }
            var endMonth = form.findField("endMonth");
            if(!endMonth)
              return;
            var end = endMonth.getValue();
            if(!end){
              endMonth.markInvalid("年度结束月份不能为空！");
              return false;
            }
            if(start == "01"){
              if(end != "12"){
                startMonth.markInvalid("起止月份必须满足一年！");
                endMonth.markInvalid("起止月份必须满足一年！");
                return false
              }else{
                return true
              }
            }else if(start - end != 1){
                startMonth.markInvalid("起止月份必须满足一年！");
                endMonth.markInvalid("起止月份必须满足一年！");
              return false;
            }else{
              return true;
            }
          }
});