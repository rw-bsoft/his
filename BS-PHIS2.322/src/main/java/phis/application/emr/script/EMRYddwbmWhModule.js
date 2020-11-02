$package("phis.application.emr.script")

$import("phis.script.SimpleModule")

phis.application.emr.script.EMRYddwbmWhModule = function(cfg) {
	phis.application.emr.script.EMRYddwbmWhModule.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.emr.script.EMRYddwbmWhModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : '30%',
										items : this.getTree()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getTree : function() {
				var module = this.createModule("refTree", this.refTree);
				module.node = this.node;
//				module.on("treeClick", this.onBeforeTreeClick, this);
				this.tree = module;
				var tree = module.initPanel();
				this.treePanel = tree;
				return tree;
			},
			getRList : function() {
				var module = this.createModule("controlRlist", this.refRList);
				this.rlist = module;
				return module.initPanel();
			}
		})
