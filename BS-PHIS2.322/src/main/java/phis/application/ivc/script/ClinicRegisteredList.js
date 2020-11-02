$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.ClinicRegisteredList = function(cfg) {
	this.hide = false;
	phis.application.ivc.script.ClinicRegisteredList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.ClinicRegisteredList, phis.script.SimpleList,{
			onEnterKey : function() {
				var r = this.grid.getSelectionModel().getSelected();
			}
		})