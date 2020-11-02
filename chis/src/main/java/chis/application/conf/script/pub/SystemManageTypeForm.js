$package("chis.application.conf.script.pub")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.conf.script.pub.SystemManageTypeForm = function(cfg) {
  cfg.fldDefaultWidth = 220;
  cfg.labelWidth = 180;
  cfg.autoFieldWidth = false;
  chis.application.conf.script.pub.SystemManageTypeForm.superclass.constructor.apply(this,
      [cfg])
  this.colCount = 1;
  this.saveServiceId = "chis.systemManageTypeService";
  this.saveAction = "saveConfig";
  this.loadServiceId = "chis.systemManageTypeService";
  this.loadAction = "getConfig"
  this.c = null;
  this.p = null;
  this.on("save", this.onSave, this)
  this.on("loadData", this.onLoadData, this);
}
Ext.extend(chis.application.conf.script.pub.SystemManageTypeForm,
    chis.application.conf.script.SystemConfigUtilForm, {

      doSave : function() {
        this.saveToServer(this.getFormData());
      },

      onSave : function(entryName, op, json, data) {
      	var canUpdate=data.canUpdate;
      	var form = this.form.getForm();

        var areaGridType = form.findField("areaGridType");
      	if(canUpdate==false){
      		areaGridType.disable();
      	}else{
      		areaGridType.enable();
      	}
        Ext.Msg.show({
              title : '提示信息',
              msg : '配置完成,请重新登录以激活配置！',
              modal : true,
              width : 300,
              buttons : Ext.MessageBox.OKCANCEL,
              multiline : false,
              fn : function(btn, text) {
                if (btn == "ok") {
                  location.reload();
                }
              },
              scope : this
            })
      },
      onLoadData:function(entryName,data){
      	if(!data){
      		return;
      	}
      	var canUpdate=data.canUpdate;
      	var form = this.form.getForm();

        var areaGridType = form.findField("areaGridType");
      	if(canUpdate==false){
      		areaGridType.disable();
      	}else{
      		areaGridType.enable();
      	}
      }

    });