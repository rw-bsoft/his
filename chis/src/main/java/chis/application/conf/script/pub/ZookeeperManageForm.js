$package("chis.application.conf.script.pub")
$import("chis.application.conf.script.SystemConfigUtilForm", "util.Accredit")
chis.application.conf.script.pub.ZookeeperManageForm = function(cfg) {
	cfg.labelWidth = 120;
	cfg.fldDefaultWidth = 200;
	cfg.autoFieldWidth = false;
	chis.application.conf.script.pub.ZookeeperManageForm.superclass.constructor.apply(this,
			[cfg])
	this.colCount = 2;
	this.saveServiceId = "chis.zookeeperManageService";
	this.saveAction = "saveZkConfig";
	this.loadServiceId = "chis.zookeeperManageService";
	this.loadAction = "getZkConfig"
	this.on("loadData", this.onLoadData, this);
	this.on("save", this.onSave, this);
}
Ext.extend(chis.application.conf.script.pub.ZookeeperManageForm,
		chis.application.conf.script.SystemConfigUtilForm, {
			onReady : function() {
				chis.application.conf.script.pub.ZookeeperManageForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var ifNeedPix = form.findField("ifNeedPix");
				if (ifNeedPix) {
					ifNeedPix.on("blur", this.onIfNeedPix, this);
					ifNeedPix.on("select", this.onIfNeedPix, this);
				}
			},

			onIfNeedPix : function(field) {
				var form = this.form.getForm();
				var ifNeedPix = form.findField("ifNeedPix");
				var zkAddress = form.findField("zkAddress");
				var zkPort = form.findField("zkPort");
				if (ifNeedPix.getValue() == "y") {
					zkAddress.enable();
					zkPort.enable();
					this.ifNotNull(true);
				} else {
					zkAddress.disable();
					zkAddress.clearInvalid();
					zkPort.disable();
					zkPort.clearInvalid();
					this.ifNotNull(false);
				}
			},

			ifNotNull : function(flag) {
				var items = this.schema.items;
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "zkAddress") {
						items[i]["not-null"] = flag;
					}
					if (items[i].id == "zkPort") {
						items[i]["not-null"] = flag;
					}
				}
			},

			onLoadData : function() {
				this.onIfNeedPix();
			},

			onSave : function(entryName, op, json, data) {
				Ext.Msg.alert("注意","配置保存成功，必须手动重启服务器才能生效。");
			}
		})