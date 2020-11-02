$package("phis.application.ccl.script")
$import("phis.script.SelectList")

phis.application.ccl.script.CheckProjectPutInModule1 = function(cfg) {
	phis.application.ccl.script.CheckProjectPutInModule1.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ccl.script.CheckProjectPutInModule1, phis.script.SelectList,
		{
			loadData : function() {
				this.requestData.cnd = 	['eq', ['$', 'JGID'],['s', this.mainApp['phis'].phisApp.deptId]]
				phis.application.ccl.script.CheckProjectPutInModule1.superclass.loadData
						.call(this)
			}
		})
