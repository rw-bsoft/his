$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView") 

chis.application.quality.script.QualityControl_list = function(cfg) {
	this.autoLoadData = true
	this.xmxhId="";
	chis.application.quality.script.QualityControl_list.superclass.constructor.apply(
			this, [cfg]);
	 this.disablePagingTbr = true
}
Ext.extend(chis.application.quality.script.QualityControl_list,
		chis.script.BizSimpleListView, {
         onReady: function(){
 			chis.application.quality.script.QualityControl_list.superclass.onReady.call(this);
				this.initCnd = ["eq", ["$", "XMBS"],
						["s", this.xmxhId]]
				this.requestData.cnd = this.initCnd
				this.doNewSet();
			},
		  doNewSet: function(){
		 	  	if(this.gxsdCfg){
		           this.gxsdCfg.setValue("");	  	
			  	}
			   if(this.sjczCfg){
			     	this.sjczCfg.setValue("");
			   }
			  if(this.dfCfg){
			     	this.dfCfg.setValue("");
			   }
		  },
		  getCndBar : function(items){
			 
					// 关系设定
				this.gxsdCfg= this.createDicField({
							"width" :100,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.QualityControl_GXSD",
							"render" : "Tree",
						 	"selectOnFocus" : true,
							"allowBlank" : "false",
							"defaultValue" : {
								"key" : this.gxsdCfgKey,
								"text" : this.gxsdCfgText
							}
					});
					this.sjczCfg =new Ext.form.TextField({
					name : "SJCZ",
				 	fieldLabel : "数据差值",
					xtype : "textfield",
					width : 100
				   });
				   this.dfCfg =new Ext.form.TextField({
						name : "DF",
					 	fieldLabel : "得分",
						xtype : "textfield",
						width : 100
				   });
				return [
					{xtype: 'tbtext', text: '关系:',width:65,style:{textAlign:'center'}},this.gxsdCfg,
					{xtype: 'tbtext', text: '差值:',width:65,style:{textAlign:'center'}},this.sjczCfg,
					{xtype: 'tbtext', text: '得分',width:40,style:{textAlign:'center'}},
					this.dfCfg]
			},doOnsave:function(){
				//this.xmxhId="GXYSF_FYQK";
				var saveValues={};
				      saveValues["GXSD"]=this.gxsdCfg.getValue();
				       saveValues["XMBS"]=this.xmxhId;
				      saveValues["SJCZ"]=this.sjczCfg.getValue();
				      saveValues["DF"]=this.dfCfg.getValue();
				if(!this.xmxhId || this.xmxhId==null || this.xmxhId==""){
					MyMessageTip.msg("提示", "项目序号不能为空！",true);
					return
				}
				if(saveValues && (saveValues["GXSD"]==null || saveValues["GXSD"]=="")){
					MyMessageTip.msg("提示", "关系不能为空！",true);
					return
				}
				if(saveValues && (saveValues["SJCZ"]==null || saveValues["SJCZ"]=="")){
					MyMessageTip.msg("提示", "差值不能为空！",true);
					return
				}
				if(saveValues && (saveValues["DF"]==null || saveValues["DF"]=="")){
					MyMessageTip.msg("提示", "得分不能为空！",true);
					return
				}
				//对大于 ，小于号进行判断
				if(saveValues &&  saveValues["GXSD"]!=null && saveValues["GXSD"]!="" ){
					if(saveValues["GXSD"]=="2" ||  saveValues["GXSD"]=="3" ){
					  //待完成
					}
				}
				var result = util.rmi.miniJsonRequestSync({
					 	serviceId : "chis.qualityControlService",//doInitQualityModel
						serviceAction : "saveList",//getQualityModel  loadHtmlData
						method : "execute",
						body : {
							"values" :saveValues,
							"entryName":"QUALITY_ZK_GXSD",
							op:"create"
						  }
						});
					if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg);
						return null;
					} else {
						//var bodyBack = result.json.body;
						this.loadData();
					}
				}
		
		})