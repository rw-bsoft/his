$package("phis.application.war.script");

$import("phis.script.SimpleList", "phis.script.rmi.miniJsonRequestSync",
		"phis.script.util.DateUtil", "phis.script.widgets.DatetimeField");

phis.application.war.script.NursePlanDataInportList = function(cfg) {
	phis.application.war.script.NursePlanDataInportList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.war.script.NursePlanDataInportList,
		phis.script.SimpleList, {
			doInport : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				this.opener.form.getForm().findField("HLZD")
						.setValue(r.data.ZDMC);
				this.getWin().hide();
			},
			onDblClick : function() {
				this.doInport();
			}
		});