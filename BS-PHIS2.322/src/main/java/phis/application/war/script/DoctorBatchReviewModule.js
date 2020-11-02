$package("phis.application.war.script");
$import("phis.script.SimpleModule");

phis.application.war.script.DoctorBatchReviewModule = function(cfg) {
	phis.application.war.script.DoctorBatchReviewModule.superclass.constructor
			.apply(this, [cfg]);
};

Ext.extend(phis.application.war.script.DoctorBatchReviewModule,phis.script.SimpleModule,{
	initPanel : function(sc) {
		if (this.panel){
			return this.panel;
		}
		var panel = new Ext.Panel({
						border : false,
						frame : true,
						layout : 'border',
						items : [{
									layout : "fit",
									border : false,
									split : false,
									title : '',
									width : 600,
									region : 'center',
									items : this.getMainList()
								}]
					});
		this.panel = panel;
		return this.panel;
	},
	/**
	 * 获取医嘱复核列表Module
	 * @return {}
	 */
	getMainList : function(){
		var module = this.createModule("doctorReviewList", this.refDoctorReviewList);
			if (module) {
				module.opener = this;
				this.refDocRevList = module;
				
			}
		return module.initPanel();
		
	}
})