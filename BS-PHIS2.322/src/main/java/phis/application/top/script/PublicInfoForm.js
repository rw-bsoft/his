$package("com.bsoft.phis.pub");

$import("com.bsoft.phis.TableForm");

com.bsoft.phis.pub.PublicInfoForm = function(cfg) {
	cfg.width = 900;
	// cfg.autoHeight = false
	// cfg.showButtonOnTop = true
	// cfg.autoFieldWidth = false
	// cfg.fldDefaultWidth = 180
	cfg.actions = [{
				id : "cancel",
				name : "关闭",
				iconCls : "common_cancel"
			}]
	com.bsoft.phis.pub.PublicInfoForm.superclass.constructor.apply(this, [cfg])
	// this.on("beforeLoadData", this.BeforeLoadData, this);
	// this.on("doNew", this.OnDoNew, this);
	// this.on("winShow", this.onWinShow, this)
}

Ext.extend(com.bsoft.phis.pub.PublicInfoForm, com.bsoft.phis.TableForm, {
// BeforeLoadData : function() {
		// var form = this.form.getForm();
		// var infoDesc = form.findField("infoDesc");
		// if (infoDesc) {
		// var toolBar = infoDesc.getToolbar();
		// if (toolBar)
		// if (this.op == "read") {
		// toolBar.setVisible(false);
		// } else
		// toolBar.setVisible(true);
		// }
		// },
		// OnDoNew : function() {
		// var form = this.form.getForm();
		// var infoDesc = form.findField("infoDesc");
		// if (infoDesc) {
		// infoDesc.setHeight(400);
		// }
		// },
		// onWinShow : function() {
		// this.win.doLayout();
		// this.win.setPosition(250, 250);
		// }
		})