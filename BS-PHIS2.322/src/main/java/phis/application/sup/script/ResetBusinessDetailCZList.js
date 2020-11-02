$package("phis.application.sup.script")

$import("phis.script.SelectList")
phis.application.sup.script.ResetBusinessDetailCZList = function(cfg) {
	this.autoLoadData = false;
	phis.application.sup.script.ResetBusinessDetailCZList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.ResetBusinessDetailCZList,
		phis.script.SelectList, {
			doCommit : function() {
				var records = this.getSelectedRecords()
				if (records == null) {
					return;
				}
                this.clearSelect();
				this.fireEvent("checkData", records);
			},
			doClose : function() {
				this.fireEvent("winClose", this);
			}
		})