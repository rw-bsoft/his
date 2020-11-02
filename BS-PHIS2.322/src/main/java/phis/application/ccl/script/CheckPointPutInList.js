$package("phis.application.ccl.script")
$import("phis.script.SelectList")

phis.application.ccl.script.CheckPointPutInList = function(cfg) {
	phis.application.ccl.script.CheckPointPutInList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ccl.script.CheckPointPutInList,
		phis.script.SelectList, {
			loadData : function() {
				this.requestData.cnd = 	['eq', ['$', 'JGID'],['s', this.mainApp['phis'].phisApp.deptId]]
				phis.application.ccl.script.CheckPointPutInList.superclass.loadData
						.call(this)
			}
		
		
		})
