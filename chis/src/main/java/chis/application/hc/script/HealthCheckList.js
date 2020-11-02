$package("chis.application.hc.script");

$import("chis.script.BizSimpleListView");

chis.application.hc.script.HealthCheckList = function(cfg) {
	cfg.showButtonOnTop = true;
	cfg.disablePagingTbr = true;
	chis.application.hc.script.HealthCheckList.superclass.constructor.apply(this,
			[cfg]);
	this.requestData.actions = "update";// 修改权限判断
	this.on("getStoreFields", this.onGetStoreFields, this)
};

Ext.extend(chis.application.hc.script.HealthCheckList, chis.script.BizSimpleListView, {
	loadData : function() {
		if(this.exContext.args["id"] !=null){
		      if(this.exContext.args["id"] == "D20_11"){
		              this.requestData.cnd =  ['and',['eq', ['$', "a.empiId"],['s', this.exContext.ids.empiId]],['in', ['$', 'a.createUnit'], ['320124011', '320124010']]]
		       }else{
				      this.requestData.cnd =  ['and',['eq', ['$', "a.empiId"],['s', this.exContext.ids.empiId]],['ne', ['$', 'a.createUnit'], ['s','320124011']],['ne', ['$', 'a.createUnit'], ['s','320124010']]]
			   }
		}else{
		 this.requestData.cnd =  ['and',['eq', ['$', "a.empiId"],['s', this.exContext.ids.empiId]],['ne', ['$', 'a.createUnit'], ['s','320124011']],['ne', ['$', 'a.createUnit'], ['s','320124010']]]
		}
		chis.application.hc.script.HealthCheckList.superclass.loadData.call(this);
	},
	
	onGetStoreFields : function(fields) {
		fields.push({
					name : "_actions",
					type : "object"
				});
	}
});