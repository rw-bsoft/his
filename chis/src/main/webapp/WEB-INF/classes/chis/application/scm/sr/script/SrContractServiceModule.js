$package("chis.application.scm.sr.script")
$import("chis.script.BizModule")
//$import("chis.script.BizCombinedModule2")

chis.application.scm.sr.script.SrContractServiceModule=function(cfg){
	chis.application.scm.sr.script.SrContractServiceModule.superclass.constructor.apply(this,[cfg]);
	
}

Ext.extend(chis.application.scm.sr.script.SrContractServiceModule, chis.script.BizModule, {
	initPanel : function() {
		var me = this;
		var panel = new Ext.Panel({
					layout : 'fit',
					items : [{
								layout : 'border',
								items : [{  title : '签约人员列表',
											border : false,
											region : 'north',
											layout : 'fit',
											height : 300,
											split : true,
											items : me
													.getRefModule('refSCMPersonList')
										}, {
											title : '项目服务记录列表',
											border : false,
											region : 'center',
											layout : 'fit',
											width : 400,
											split : true,
											items : me
													.getRefModule('refSCMServiceList')
										}]
							}]
				});
		panel.on("afterrender", this.onReady, this)
		this.panel = panel;
		return panel;
	},
	onReady : function(){
		this.firstList = this.midiModules["refSCMPersonList"];
		this.secondList = this.midiModules["refSCMServiceList"];
		if(this.firstList){
			this.firstList.on("loadTaskDetail",this.refreshSecondList,this);
		}
	},
	refreshSecondList : function(cnd){
		this.secondList.requestData.cnd = cnd;
		this.secondList.refresh();
	},
	getRefModule : function(refModuleName) {
		var module = this
				.createSimpleModule(refModuleName, this[refModuleName]);
		this[refModuleName + '_refModule'] = module;
		module.opener = this;
		return module.initPanel();
	}
})