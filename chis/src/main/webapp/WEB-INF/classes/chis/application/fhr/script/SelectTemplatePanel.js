$package("chis.application.fhr.script");

$import("chis.script.BizTableFormView");

chis.application.fhr.script.SelectTemplatePanel = function(cfg) {
	cfg.colCount = 1;
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.fhr.script.SelectTemplatePanel.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.SelectTemplatePanel,
		chis.script.BizTableFormView, {
			doSave : function() {
				var form = this.form.getForm();
				var field = form.findField("modId");
				var value = field.getValue();
				this.fireEvent("selectTemplate", value);
				this.win.hide();
			}
		});