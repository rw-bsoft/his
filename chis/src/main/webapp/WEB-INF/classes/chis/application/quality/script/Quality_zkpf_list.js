$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView") 

chis.application.quality.script.Quality_zkpf_list = function(cfg) {
	cfg.autoLoadData = false
	chis.application.quality.script.Quality_zkpf_list.superclass.constructor.apply(
			this, [cfg]);
	this.disablePagingTbr = true
}
Ext.extend(chis.application.quality.script.Quality_zkpf_list,
		chis.script.BizSimpleListView, {
         onReady: function(){
 			chis.application.quality.script.Quality_zkpf_list.superclass.onReady.call(this);
		 },doInitLoad:function(){
		 	this.doQuery();
		 },
		doQuery : function() {
			var cnd = [];
//			var manaUtil =values["manaUtil"];
//			if(manaUtil){
//				cnd = ['like',['$','MANAUNITID'],['s',manaUtil]];
//			}
			//项目类别
			var XMLB = "GXYSF";
			if(XMLB){
				hcnd = ['eq',['$','XMLB'],['s',XMLB || '']]
				if(cnd.length > 0){
					cnd = ['and', cnd, hcnd];
				}else{
					cnd = hcnd;
				}
			}
			this.requestData.cnd = cnd
			this.refresh()
		},
		loadData: function(){
			chis.application.quality.script.Quality_zkpf_list.superclass.loadData.call(this); 
		}
 })