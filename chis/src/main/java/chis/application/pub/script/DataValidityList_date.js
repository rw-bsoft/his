/**
 * @author : yaozh
 */
$package("chis.application.pub.script")
$import("chis.script.BizTableFormView");
chis.application.pub.script.DataValidityList_date = function(cfg) {
	this.title  = "请选择时间范围"
	cfg.width = 500 ;
	cfg.colCount = 2;
	cfg.autoWidth = false;
	chis.application.pub.script.DataValidityList_date.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(chis.application.pub.script.DataValidityList_date, chis.script.BizTableFormView,{
	doOpsure : function(){
		if(!this.form.getForm().isValid()||this.isUnUseAble){
			return ;
		}
		if (this.form && this.form.el) {
			this.form.el.mask("正在处理...", "x-mask-loading");
		}
		this.loading = true;
		util.rmi.jsonRequest({
				serviceId : 'chis.gpsDataValidityService',
				serviceAction : "autoMatch",
				method:"execute",
				body : {beginTime:this.form.getForm().findField("beginDate").getValue().format("Y-m-d"),
					    endTime:this.form.getForm().findField("endDate").getValue().format("Y-m-d"),
					    serviceBusiness:this.cndValue}
			}, function(code, msg, json) {
				if (this.form && this.form.el) {
					this.form.el.unmask();
				}
				this.loading = false;
				if (code > 300) {
					this.processReturnMsg(code, msg, this.onWinShow);
					return;
				}
				if (json.body) {
					var data = json.body;
					if (data.msg) {
						Ext.MessageBox.alert("提示",data.msg);
						this.fireEvent("save");
						this.doCancel();
					}
				}
			}, this);
		if (!this.data) {
			this.data = {};
		}
	},
	doOpall : function(){
		this.isUnUseAble = true ; 
		this.form.getForm().findField("beginDate").setDisabled(true);
		this.form.getForm().findField("endDate").setDisabled(true);
		if (this.form && this.form.el) {
			this.form.el.mask("正在处理...", "x-mask-loading");
		}
		this.loading = true;
		util.rmi.jsonRequest({
				serviceId : 'chis.gpsDataValidityService',
				serviceAction : "allMatch",
				method:"execute"
			 },function(code, msg, json) {
				if (this.form && this.form.el) {
					this.form.el.unmask();
				}
				this.loading = false;
				if (code > 300) {
					this.processReturnMsg(code, msg, this.onWinShow);
					return;
				}
				if (json.body) {
					var data = json.body;
					if (data.msg) {
						Ext.MessageBox.alert("提示",data.msg);
						this.fireEvent("save");
						this.doCancel();
					}
				}
			}, this);
		if (!this.data) {
			this.data = {};
		}
	}
});