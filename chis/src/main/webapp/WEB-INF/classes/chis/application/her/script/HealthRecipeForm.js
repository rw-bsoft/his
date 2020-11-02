$package("chis.application.her.script");

$import("chis.script.BizTableFormView");

chis.application.her.script.HealthRecipeForm = function(cfg) {
	chis.application.her.script.HealthRecipeForm.superclass.constructor.apply(
			this, [cfg]);
	this.autoLoadSchema = false
	this.autoLoadData = false;
};

Ext.extend(chis.application.her.script.HealthRecipeForm,
		chis.script.BizTableFormView, {
			doPrintRecipe : function() {
				var url = "resources/chis.prints.template.HealthRecipelManage.print?type="
						+ 1 + "&id=" + this.initDataId
				url += "&temp=" + new Date().getTime()
				var win = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

				if (Ext.isIE6) {
					win.print()
				} else {
					win.onload = function() {
						win.print()
					}
				}
			}
		});