$package("chis.application.hq.script");

$import("chis.script.BizTableFormView");

chis.application.hq.script.HighRiskVisitForm = function(cfg) {
	chis.application.hq.script.HighRiskVisitForm.superclass.constructor
			.apply(this, [cfg]);
	this.serviceId = "chis.hqQueryService";
	this.serviceAction = "saveHighRiskVisit";
};

Ext.extend(chis.application.hq.script.HighRiskVisitForm,
		chis.script.BizTableFormView, {
		doNew : function() {
			chis.script.BizTableFormView.superclass.doNew.call(this);
			this.initDataId = this.exContext.args.visitId;
			if (this.initDataId) {
				this.fireEvent("beforeUpdate", this); // **
				// 在数据加载之前做一些初始化操作
			} else {
				this.fireEvent("beforeCreate", this); // ** 在页面新建时做一些初始化操作
			}
		},	
		onReady : function() {
			chis.application.hq.script.HighRiskVisitForm.superclass.onReady.call(this);
		}
		,doSavedata : function() {
			this.data.empiId=this.exContext.args.empiId;
			this.data.phrId=this.exContext.args.phrId;
			var plandata=this.exContext.args;
			var visitdata=this.getFormData();
			var result = util.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceAction,
					method:"execute",
					plandata :plandata,
					visitdata:visitdata
				})
			if(result.code>300){
				Ext.Msg.alert("提示", result.msg);
			}else{
				this.fireEvent("savehighriskvist");
			}
		}
		,onBeforeLoadData : function(entryName, initDataId) {
			this.phrId = this.exContext.args.phrId;
			this.empiId = this.exContext.args.empiId;
			this.visitId = this.exContext.args.visitId;
			this.planDate = this.exContext.args.planDate;
			this.planId = this.exContext.args.planId;
			this.sn = this.exContext.args.sn;
			return true;
		}
		,doAddplan:function() {
			var plandata={};
			plandata.empiId=this.exContext.ids.empiId;
			plandata.phrId=this.exContext.ids.phrId;
			Ext.MessageBox.prompt("提示","请输计划日期：格式(yyyymmdd)",function(btn,value) {
				if(btn=="ok"){
					if(value.length!=8){
						Ext.Msg.alert("提示", "日期格式不对！");
						return;
					}
					plandata.planDate=value;
	       			var result = util.rmi.miniJsonRequestSync({
						serviceId : this.serviceId,
						serviceAction : "addHighRiskPlan",
						method:"execute",
						plandata :plandata
					})
					if(result.code>300){
						Ext.Msg.alert("提示", result.msg);
					}
					this.fireEvent("addhighriskplan");
				}
   			},this,false,this.mainApp.serverDate.substring(0,4));
		},doDeleteplan:function() {
			var plandata=this.exContext.args;
			if(!plandata){
				Ext.Msg.alert("提示", "没有计划可以删除！");
			}
			var result = util.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "deleteHighRiskPlan",
					method:"execute",
					plandata :plandata
				})
			if(result.code>300){
				Ext.Msg.alert("提示", result.msg);
			}
			this.fireEvent("deletehighriskplan");
		}
})