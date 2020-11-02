$package("chis.application.tb.script");

$import("chis.script.BizTabModule","chis.script.util.widgets.MyMessageTip");

chis.application.tb.script.TuberculosisModule = function(cfg){
	chis.application.tb.script.TuberculosisModule.superclass.constructor.apply(this,[cfg]);
	this.on("loadModule", this.onLoadModule, this);
}

Ext.extend(chis.application.tb.script.TuberculosisModule,chis.script.BizTabModule,{
	loadData:function() {
		this.activeModule(0);
		this.activeTab(false)
	},
	onLoadModule : function(moduleName, module) {
		Ext.apply(module.exContext,this.exContext);
		if (moduleName == this.actions[0].id) {
			this.TBModule = module;
			module.form.TBM = this;
			this.TBModule.form.on("activeTab", this.activeTab, this);
		}
		if (moduleName == this.actions[1].id) {
			this.TBFirstVisit = module;
		}
		if (moduleName == this.actions[2].id) {
			this.TBVisit = module;
		}
	},
	activeTab : function(active) {
		if(active)　{
			this.changeSubItemDisabled(false,"TBModule");
		}else {
			this.changeSubItemDisabled(true,"TBModule");
		}
	},
	onReady : function() {
		// ** 设置滚动条
		if (this.isAutoScroll) {
			this.tab.setWidth(this.getFormWidth());
			this.tab.setHeight(this.getFormHeight());
			this.tab.items.each(function(item) {
						item.setWidth(this.getFormWidth());
						item.setHeight(this.getFormHeight());
					}, this);
		}
	}
});