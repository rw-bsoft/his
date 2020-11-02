$package("chis.application.scm.bag.script")
$import("chis.script.BizCombinedModule2")

chis.application.scm.bag.script.ServicePackageReadModule = function(cfg){
	cfg.layOutRegion = "north";
	cfg.itemHeight=215;
	chis.application.scm.bag.script.ServicePackageReadModule.superclass.constructor.apply(this,[cfg]);
	this.frame=true;
	this.firstTitle="服务包信息"
	this.secondTitle="服务包的项目信息";
}

Ext.extend(chis.application.scm.bag.script.ServicePackageReadModule,chis.script.BizCombinedModule2,{
	getFirstItem : function() {
		var module = this.midiModules["ServicePackage_Form"];
		if (!module) {
			var cls = "chis.script.BizTableFormView";
			$import(cls);
			var cfg = {
				isCombined : true,
				autoLoadData : false,
				autoLoadSchema : false,
				colCount:2,
				actions:[],
				entryName : "chis.application.scm.schemas.SCM_ServicePackage"
			};
			module = eval("new " + cls + "(cfg)");
			this.midiModules["ServicePackage_Form"] = module;
		}
		this.form = module;
		var itemPanel = module.initPanel();
		return itemPanel;
	},
	getSecondItem : function() {
		var module = this.midiModules["ServicePackageItems_List"];
		if (!module) {
			var cls = "chis.script.BizSimpleListView";
			$import(cls);
			var cfg = {
				isCombined : true,
				autoLoadSchema : false,
				enableCnd : false,
				autoLoadData : false,
				selectFirst : false,
				entryName : "chis.application.scm.schemas.SCM_ServicePackageItems"
			};
			module = eval("new " + cls + "(cfg)");
			this.midiModules["ServicePackageItems_List"] = module;
		}
		this.list = module;
		var itemPanel = module.initPanel();
		return itemPanel;
	},
	loadData : function(){
		if(this.form){
			this.form.initDataId = this.SPID;
			this.form.loadData();
		}
		if(this.list){
			this.list.requestData.cnd=['eq',['$','a.SPID'],['s',this.SPID]];
			this.list.loadData();
		}
	}
});