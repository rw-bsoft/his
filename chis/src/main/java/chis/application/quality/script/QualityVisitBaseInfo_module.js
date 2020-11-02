$package("chis.application.quality.script")

$import("chis.script.BizTabModule") 

chis.application.quality.script.QualityVisitBaseInfo_module = function(cfg) {
	cfg.autoLoadData = true
	chis.application.quality.script.QualityVisitBaseInfo_module.superclass.constructor.apply(
			this, [cfg]);
   	this.itemCollapsible = false 
	this.width = 1000
	this.height = 450
}
Ext.extend(chis.application.quality.script.QualityVisitBaseInfo_module,
		chis.script.BizTabModule, {
	initPanel : function() {
		var panel = chis.application.quality.script.QualityVisitBaseInfo_module.superclass.initPanel
					.call(this)
	//	 this.activeModule(0);
		 this.panel=panel;
		 return panel;
	},
	doInitLoad:function(data,type){
	 	this.activeModule(0);
	 	if(type=="lrzksj"|| type=="xgzksj"|| type=="ckzksj"){
	 		this.form = this.midiModules[this.actions[0].id];
		 	this.form.on("formSave",this.formSave,this);//质控保存
			var CODERNO=data["CODERNO"];
		    this.CODERNO=CODERNO;
		    this.prartNo=data["prartNo"];
			var result = util.rmi.miniJsonRequestSync({
						 	serviceId : "chis.qualityControlService", 
							serviceAction : "createZk", 
							method : "execute",
							body : {
								CODERNO :CODERNO,
								"entryName":"QUALITY_ZK_SJ"
							  }
					});
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return null;
			} else {
				var back= result.json;
				if(back["CODERNO"]){
					this.form.partLoat(back);
				}else{
				   this.form.partLoat(data);
				}
			}
	 	}
	},formSave:function(data){
		if(!data){
			return 
		} 
		data["CODERNO"]=this.CODERNO;
		data["prartNo"]=this.prartNo;
		var result = util.rmi.miniJsonRequestSync({
					 	serviceId : "chis.qualityControlService", 
						serviceAction : "saveFormZk", 
						method : "execute",
						body : {
							data:data,
							"entryName":"QUALITY_ZK_SJ"
						  }
				});
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		} else {
		    var back= result.json;
				if(back["CODERNO"]){
					this.form.partLoat(back);
				}
		}
	}
 })