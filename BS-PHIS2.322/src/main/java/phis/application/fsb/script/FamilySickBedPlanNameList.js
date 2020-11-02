$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedPlanNameList = function(cfg) {
	phis.application.fsb.script.FamilySickBedPlanNameList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedPlanNameList,
		phis.script.SimpleList, {
			onReady : function() {
				this.requestData.cnd = ['and',
						['eq', ['$', 'a.SFQY'], ['i', 1]],
						['eq', ['$', 'a.JGID'], ['s', this.mainApp.deptId]]]
				this.loadData();
			},
			onDblClick : function(grid, index, e) {
				this.fireEvent('quickInput', this.store.getAt(index));
			},
			onRenderer : function(value, metaData, r) {
				var SFQY = r.get("SFQY");
				var src = (SFQY == 1) ? "yes" : "no";
				return "<img src='" + ClassLoader.appRootOffsetPath
						+ "resources/phis/resources/images/" + src + ".png'/>";
			}
		});