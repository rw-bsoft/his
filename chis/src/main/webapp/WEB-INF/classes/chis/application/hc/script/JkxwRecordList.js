$package("chis.application.hc.script");

$import("chis.script.BizSimpleListView","chis.script.gis.powerChartView");

chis.application.hc.script.JkxwRecordList = function(cfg) {
	cfg.showButtonOnTop = true;
//	cfg.disablePagingTbr = true;
	chis.application.hc.script.JkxwRecordList.superclass.constructor.apply(this,
			[cfg]);
//	this.requestData.actions = "update";// 修改权限判断
//	this.on("getStoreFields", this.onGetStoreFields, this)
};

Ext.extend(chis.application.hc.script.JkxwRecordList,chis.script.BizSimpleListView, {
	loadData : function() {
		if( this.exContext && this.exContext.ids && this.exContext.ids.phrId){
		 	
		var cnd	=['eq', ['$', "phrId"],['s', this.exContext.ids.phrId]];
		if (this.requestData &&this.requestData.cnd ){
			this.requestData.cnd = ['and',cnd,this.requestData.cnd];
			}
		else{
			this.requestData.cnd=cnd;
		}	
		}
		chis.application.hc.script.JkxwRecordList.superclass.loadData.call(this);
	},
	doViewqxt:function(){
		var r = this.getSelectedRecord();
		if(!r){
			alert("请先选择一条记录");
			return ;
		}
		if( !this.exContext || !this.exContext.ids || !this.exContext.ids.phrId){
			alert("这个地方不支持看曲线图！");
			return;
		}
		var viewid=r.data.xmbh;
		var entryName="";
		
		if(viewid=="000004" || viewid=="000005"){
			entryName="chis.report.ZJ_XY"
		}
		if(viewid=="000028"){
			entryName="chis.report.ZJ_XT"
		}
		if(viewid=="000002"){
			entryName="chis.report.ZJ_TZ"
		}
		if(viewid=="000003"){
			entryName="chis.report.ZJ_BMI"
		}
		if(viewid=="000007"){
			entryName="chis.report.ZJ_MB"
		}
		if(entryName.length<=0){
			alert("不好意思，还没做该自测项的曲线图！");
			return;
		}
				var zjqxtView = new chis.script.gis.powerChartView(
							{
								title : "自检曲线图",
								mainApp : this.mainApp,
								exContext:this.exContext,
								entryName:entryName
							});
				var win = zjqxtView.getWin();
				win.setPosition(250, 100);
				win.setWidth(800);
				win.setHeight(560);
				win.show();
	}
	
//	onGetStoreFields : function(fields) {
//		fields.push({
//					name : "_actions",
//					type : "object"
//				});
//	}
});