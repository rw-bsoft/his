$package("chis.application.pif.script")
$import("chis.script.BizTableFormView")

chis.application.pif.script.PublicInfoFormView2 = function(cfg) {
	cfg.width = 780
	cfg.autoHeight = false
	cfg.autoFieldWidth = false
	cfg.showButtonOnTop = false
	cfg.fldDefaultWidth = 170
	this.entryName = cfg.entryName;
	this.schema=cfg.entryName;
	chis.application.pif.script.PublicInfoFormView2.superclass.constructor.apply(this, [cfg])
	this.on("doNew", this.OnDoNew, this);
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.pif.script.PublicInfoFormView2, chis.script.BizTableFormView, {
			OnDoNew : function() {
				var form = this.form.getForm();
				var infoDesc = form.findField("infoDesc");
				if (infoDesc) {
					infoDesc.setHeight(400);
				}
			},
			onWinShow : function() {
				this.win.doLayout();
				this.win.setPosition(250, 50);
			}
		})