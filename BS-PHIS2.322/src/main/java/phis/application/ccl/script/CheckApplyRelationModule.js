$package("phis.application.ccl.script")
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckApplyRelationModule = function(cfg) {
	phis.application.ccl.script.CheckApplyRelationModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.ccl.script.CheckApplyRelationModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "检查类别",
										region : 'west',
										width : 300,
										items : this.getCheckTypeList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "检查部位",
										region : 'center',
										width : 300,
										items : this.getCheckPointList()
									},{
										layout : "fit",
										border : false,
										split : true,
										title : "检查项目",
										region : 'east',
										width : 350,
										items : this.getCheckProjectList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getCheckTypeList : function() {
				this.checkTypeList = this.createModule("checkTypeList",
						this.refCheckTypeList);
				this.checkTypeList.opener = this;
				this.checkTypeGrid = this.checkTypeList.initPanel();
				return this.checkTypeGrid;

			},
			getCheckPointList : function() {
				this.checkPointList = this.createModule("checkPointList",
						this.refCheckPointList);
				this.checkPointList.opener = this;
				this.checkPointGrid = this.checkPointList.initPanel();
				return this.checkPointGrid;

			},
			getCheckProjectList : function() {
				this.checkProjectList = this.createModule("checkProjectList",
						this.refCheckProjectList);
				this.checkProjectList.opener = this;
				this.checkProjectGrid = this.checkProjectList.initPanel();
				return this.checkProjectGrid;
			}
		})
