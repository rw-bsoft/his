$package("chis.application.tr.script.highRiskCriterion")

$import("chis.script.BizSimpleListView");

chis.application.tr.script.highRiskCriterion.TumourHRCDetailList = function(cfg){
	chis.application.tr.script.highRiskCriterion.TumourHRCDetailList.superclass.constructor.apply(this,[cfg]);
	this.initCnd = ['eq',['$','criterionSerialNumber'],['s',this.exContext.args.hrcId||'']]
	this.on("loadModule",this.onLoadModule,this);
}

Ext.extend(chis.application.tr.script.highRiskCriterion.TumourHRCDetailList,chis.script.BizSimpleListView,{
	loadData : function(){
		this.requestData.cnd = ['eq',['$','criterionSerialNumber'],['s',this.exContext.args.hrcId||'']];
		chis.application.tr.script.highRiskCriterion.TumourHRCDetailList.superclass.loadData.call(this);
	},
	onLoadModule : function(module){
		module.on("beforeSave", this.onModuleBeforeSave, this);
	},
	onModuleBeforeSave : function(entryName, op, saveData) {
		var vr = true;
		if (op == "create") {
			var inspectionItem = saveData.inspectionItem;
			var len = this.store.getCount();
			for (var i = 0; i < len; i++) {
				var r = this.store.getAt(i);
				var curii = r.get("inspectionItem");
				if (curii == inspectionItem) {
					vr = false;
					Ext.Msg.alert("提示", "该项目已经存在！");
					break;
				}
			}
		}else{
			var inspectionItem = saveData.inspectionItem;
			var hrcdId = saveData.hrcdId;
			var len = this.store.getCount();
			for (var i = 0; i < len; i++) {
				var r = this.store.getAt(i);
				var curii = r.get("inspectionItem");
				var curid = r.get("hrcdId");
				if (curii == inspectionItem && curid != hrcdId) {
					vr = false;
					Ext.Msg.alert("提示", "该项目已经存在！");
					break;
				}
			}
		}
		return vr;
	}
});