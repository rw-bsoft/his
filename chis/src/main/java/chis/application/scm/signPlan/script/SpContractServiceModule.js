$package("chis.application.scm.signPlan.script")
$import("chis.script.BizModule")
//$import("chis.script.BizCombinedModule2")

chis.application.scm.signPlan.script.SpContractServiceModule=function(cfg){
	chis.application.scm.signPlan.script.SpContractServiceModule.superclass.constructor.apply(this,[cfg]);
	
}

Ext.extend(chis.application.scm.signPlan.script.SpContractServiceModule, chis.script.BizModule, {
	initPanel : function() {
		var me = this;
		var panel = new Ext.Panel({
					layout : 'fit',
					items : [{
								layout : 'border',
								items : [{  title : '签约记录',
											border : false,
											region : 'north',
											layout : 'fit',
											height : 300,
											split : true,
											items : me
													.getRefModule('refSCMRecordList')
										}, {
											title : '计划列表',
											border : false,
											region : 'west',
											layout : 'fit',
											width : 500,
											split : true,
											items : me
													.getRefModule('refSCMPlanList')
										}, {
											title : '任务列表',
											border : false,
											region : 'center',
											layout : 'fit',
											split : true,
											items : me
													.getRefModule('refSCMTaskList')
										}]
							}]
				});
		this.panel = panel;
		return panel;
	},
	getRefModule : function(refModuleName) {
		var module = this
				.createSimpleModule(refModuleName, this[refModuleName]);
		this[refModuleName + '_refModule'] = module;
		module.opener = this;
		return module.initPanel();
	}
})