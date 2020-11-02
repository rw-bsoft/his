$package("chis.application.conf.script.pub")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.conf.script.pub.SystemCommonManageForm = function(cfg) {
  cfg.fldDefaultWidth = 220;
  cfg.labelWidth = 120;
  cfg.autoFieldWidth = false;
  chis.application.conf.script.pub.SystemCommonManageForm.superclass.constructor.apply(this,
      [cfg])
  this.colCount = 2;
  this.saveServiceId = "chis.systemCommonManageService";
  this.saveAction = "saveConfig";
  this.loadServiceId = "chis.systemCommonManageService";
  this.loadAction = "getConfig"
  this.c = null;
  this.p = null;
  this.on("save", this.onSave, this)
  this.on("loadData", this.onLoadData, this);
}
Ext.extend(chis.application.conf.script.pub.SystemCommonManageForm,
    chis.application.conf.script.SystemConfigUtilForm, {

      onReady : function() {
        chis.application.conf.script.pub.SystemCommonManageForm.superclass.onReady
            .call(this);
        var form = this.form.getForm();

        var province = form.findField("province");
        if (province) {
          province.on("change", this.provinceChange, this);
        }

        var city = form.findField("city");
        if (city) {
          city.notfocusLoad = true;
          city.on("change", this.cityChange, this);
          city.store.on("beforeload", function() {
            var city = this.form.getForm().findField("city");
            var province = this.form.getForm()
                .findField("province");
            var value = province.getValue();
            if (value) {
              city.store.proxy.conn.url = 'chis.dictionary.areaGrid.dic?src=chis.application.conf.schemas.SYS_CommonConfig.city&sliceType=3&parentKey='
                  + value;
            } else {
              return false;
            }
          }, this)
        }

        var region = form.findField("region")
        if (region) {
          region.notfocusLoad = true;
          region.store.on("beforeload", function() {
            var region = this.form.getForm().findField("region");
            var city = this.form.getForm().findField("city");
            var value = city.getValue();
            if (value) {
              region.store.proxy.conn.url = 'chis.dictionary.areaGrid.dic?src=chis.application.conf.schemas.SYS_CommonConfig.region&sliceType=3&parentKey='
                  + value;
            } else {
              return false;
            }
          }, this)
        }

      },

      provinceChange : function(f, n, o) {
        var form = this.form.getForm();
        var city = form.findField("city");
        var region = form.findField("region");
        if (region) {
          region.clearValue();
        }
        if (city) {
          city.enable();
          city.clearValue()
          if (n == "50" || n == "11" || n == "12" || n == "31") {
            city.setValue({
                  key : n,
                  text : f.getRawValue()
                })
            city.disable();
          }
        }
      },

      cityChange : function(f, n, o) {
        var form = this.form.getForm();
        var region = form.findField("region");
        if (region) {
          region.clearValue()
        }
      },

      doSave : function() {
        this.saveToServer(this.getFormData());
      },

      onSave : function(entryName, op, json, data) {
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

      onLoadData : function(entryName, body) {
        var province = body.province.key
        if (province == "50" || province == "11" || province == "12"
            || province == "31") {
          var city = this.form.getForm().findField("city");
          if (city)
            city.disable();
        }
      }
    });