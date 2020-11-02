$package("phis.application.war.script")

$import("phis.script.SimpleList")

phis.application.war.script.MedicalBackApplicationRightUnderList = function(cfg) {
	cfg.autoLoadData = true;
	phis.application.war.script.MedicalBackApplicationRightUnderList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.war.script.MedicalBackApplicationRightUnderList,
		phis.script.SimpleList, {
			loadData:function(zyh){
				if(zyh==null||zyh==""||zyh==undefined){return;}	
			this.requestData.cnd=['and',['eq',['$','a.ZYH'],['d',zyh]],['or',['eq', ['$', 'a.TJBZ'], ['i', 1]],['notNull',['$','a.TYRQ']]]] ;
			this.requestData.serviceId = "phis."+this.serviceId;
			this.requestData.serviceAction = this.queryActionId;
			phis.application.war.script.MedicalBackApplicationRightUnderList.superclass.loadData.call(this);
			},
			onRenderer:function(value, metaData, r){
				if(r.data.TYRQ!=null&&r.data.TYRQ!=""&&r.data.TYRQ!=undefined){
				return "已退药";
				}else{
				return "已提交"
				}
			},
			onRenderer_positive:function(value, metaData, r){
				if(value<0){
				return -value;
				}else{
				return value;
				}
			},
		onRendererNull : function(value, metaData, r) {
				if(value==null||value=="null"){
				return "";}else{
				return value;
				}
			}
		});