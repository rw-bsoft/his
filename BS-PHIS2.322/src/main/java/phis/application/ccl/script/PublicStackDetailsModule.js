$package("phis.application.ccl.script");
$import("phis.script.SimpleModule")

phis.application.ccl.script.PublicStackDetailsModule = function(cfg) {
	phis.application.ccl.script.PublicStackDetailsModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ccl.script.PublicStackDetailsModule, phis.script.SimpleModule,
		{
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										height : 200,
										width : 200,
										items : this.getPublicStackDetailsList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getPublicStackDetailsList : function() {
				this.publicStackDetailsList = this.createModule(
						"publicStackDetailsList", this.refPublicStackDetailsList);
				this.publicStackDetailsList.opener=this;
				this.publicStackDetailsList.requestData.cnd = ['eq',['$','ZTBH'],['i',this.ZTBH]]
				this.publicStackDetailsGrid = this.publicStackDetailsList.initPanel();
				return this.publicStackDetailsGrid;
			},
			init : function(){
				if(this.publicStackDetailsList!=undefined){
					this.publicStackDetailsList.requestData.cnd = ['eq',['$','ZTBH'],['i',this.ZTBH]]
					this.publicStackDetailsList.refresh();
				}
			}
	
		})
