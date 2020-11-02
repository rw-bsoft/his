$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView") 

chis.application.quality.script.QualityCkZkBg_tnbList = function(cfg) {
	cfg.autoLoadData = false
	cfg.listServiceId="chis.qualityControlService";
	cfg.listAction="creatTnbDzList";
	chis.application.quality.script.QualityCkZkBg_tnbList.superclass.constructor.apply(
			this, [cfg]);
	this.xmxhId="";
	this.disablePagingTbr = true
}
Ext.extend(chis.application.quality.script.QualityCkZkBg_tnbList,
		chis.script.BizSimpleListView, {
         onReady: function(){
 			chis.application.quality.script.QualityCkZkBg_tnbList.superclass.onReady.call(this);
		 },doInitLoad:function(CODERNO){
			  if(CODERNO && CODERNO!=null){
			    this.requestData.body={};
			    this.requestData.body.codeNo=CODERNO;
			    this.loadData();
			  }
		 },
		loadData: function(){
			chis.application.quality.script.QualityCkZkBg_tnbList.superclass.loadData.call(this); 
		}
 })