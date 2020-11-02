$package("chis.application.pif.script")
$import("chis.script.BizTableFormView")

chis.application.pif.script.PublicInfoFormView = function(cfg) {
	cfg.width = 780
	cfg.autoHeight = false
	cfg.showButtonOnTop = true
	cfg.autoFieldWidth = false
	cfg.fldDefaultWidth = 170
	cfg.autoLoadData = false;
	this.entryName = cfg.entryName;
	this.schema = cfg.entryName;
	chis.application.pif.script.PublicInfoFormView.superclass.constructor.apply(this, [cfg])
	this.on("doNew", this.OnDoNew, this);
	this.on("winShow", this.OnWinShow, this);
	this.saveServiceId = "chis.publicInfoService";
	this.saveAction = "savePublicInfo";
}

Ext.extend(chis.application.pif.script.PublicInfoFormView, chis.script.BizTableFormView, {
			setInfoDesc : function(publishUser, uid) {
				if (this.op == "update") {
					if (publishUser == uid) {
						this.setSaveButtons(true);
					} else {
						this.setSaveButtons(false);
					}
				} else {
					this.setSaveButtons(true);
				}
			},
			setSaveButtons : function(flag) {
				if (!this.form.getTopToolbar()) {
					return;
				}
				var bts = this.form.getTopToolbar().items;
				if (bts.items.length == 0) {
					return;
				}
				if (flag) {
					bts.items[0].enable();
				} else {
					bts.items[0].disable();
				}
			},
			afterSaveData : function(entryName, op, json, data) {
				this.fireEvent("save", entryName, op, json, data);
				var form = this.form.getForm();
				var publishUser = form.findField("publishUser");
				var key = publishUser.getValue();
				// if (key == "system") {
				// publishUser.setValue({
				// key : "",
				// text : ""
				// });
				// }
			},
			doCreate : function() {
				this.initDataId = null;
				this.op = "create";
				this.setInfoDesc("1", "1");
				this.doNew();
			},
			OnDoNew : function() {
				var form = this.form.getForm();
				var infoDesc = form.findField("infoDesc");
				if (infoDesc) {
					infoDesc.setHeight(400);
				}
			},
			OnWinShow : function() {
				if (this.op == "create") {
					this.doCreate();
				}
			}
		})