$package("chis.application.conf.script.cdh")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.conf.script.cdh.ChildrenConfigForm = function(cfg) {
	cfg.fldDefaultWidth = 220
    cfg.autoFieldWidth = false;
    cfg.labelWidth = 120;
    cfg.colCount = 2;
	chis.application.conf.script.cdh.ChildrenConfigForm.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.cdh.ChildrenConfigForm, chis.application.conf.script.SystemConfigUtilForm,
{
  
    doSave : function(){
      if(this.data.childrenRegisterAge){
        Ext.Msg.show({
	        title : '确认修改儿童模块相关参数设置',
	        msg : '参数修改后此后的业务数据将按照此规则生成，是否继续?',
	        modal : true,
	        width : 300,
	        buttons : Ext.MessageBox.OKCANCEL,
	        multiline : false,
	        fn : function(btn, text) {
	          if (btn == "ok") {
	             chis.application.conf.script.cdh.ChildrenConfigForm.superclass.doSave.call(this);
	        }
          },
           scope : this
        })
      }else{
        chis.application.conf.script.cdh.ChildrenConfigForm.superclass.doSave.call(this);
      }
    },
    
	saveToServer : function(saveData){
		if(saveData.childrenRegisterAge>0&&saveData.childrenDieAge>0){
        	this.fireEvent("save",saveData,this);
		}else
			Ext.Msg.alert("提示","建册截至年龄和死亡登记截至年龄必须大于0!");
	},
  
	onReady : function() {
		var form = this.form.getForm();
		var visitIntervalSame = form.findField("visitIntervalSame");
		visitIntervalSame.on("check",function(){
			this.fireEvent("check",visitIntervalSame.checked ,this)
		},this)
	}
});