$package("chis.application.common.script")
$import("chis.script.BizModule")
/**
 * 药品导入情况
 */
chis.application.common.script.DrugInfoListModule= function(cfg) {
	chis.application.common.script.DrugInfoListModule.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(chis.application.common.script.DrugInfoListModule,chis.script.BizModule,{
	initPanel:function()
	{
		var me=this;
		var panel = new Ext.Panel({
			layout:'border',
			items:[{
					border:false,
					region:'center',
					layout:'fit',
					items:me.getDrugInfoList()}]
		});
		this.panel = panel;
		return panel;
	},
	getDrugInfoList : function() {
		var module = this.createSimpleModule("refList",this.refList);
		this.refList = module;
		module.opener=this;
		return module.initPanel();
	}
})
