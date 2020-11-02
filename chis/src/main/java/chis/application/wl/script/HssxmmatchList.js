$package("chis.application.wl.script")
$import("util.Accredit", "app.modules.form.TableFormView",
		"chis.script.BizSimpleListView")

chis.application.wl.script.HssxmmatchList = function(cfg) {
	this.initCnd = ['like', ['$', 'a.UNITID'], ["$",'%user.manageUnit.id']]
	chis.application.wl.script.HssxmmatchList.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.wl.script.HssxmmatchList, chis.script.BizSimpleListView, {
	doMatch:function(){
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var module = this.createSimpleModule("Hssmatchform",this.refform);
		module.initPanel();
		module.on("save", this.refresh, this);
		module.initDataId = r.data.XMZHM;
		this.showWin(module);
		module.loadData();
	}
	,doSynchronize:function(){
		if(this.mainApp.jobId!="chis.system"){
			alert("该功能只能由管理员操作！");
			return;
		}
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		var EHRMATCH=r.data.EHRMATCH;
		if(!EHRMATCH){
			alert("您还没有匹配，不能同步！");
			return;
		}
		var values = r.data;
		values.SYNCHRONIZE="1";
		var result = util.rmi.miniJsonRequestSync({
			serviceId : "chis.hssXmManage",
			serviceAction : "matchXm",
			method:"execute",
			data : values
		})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		this.refresh();
	}
	,doSynchronizeAll:function(){
		var result = util.rmi.miniJsonRequestSync({
			serviceId : "chis.hssXmManage",
			serviceAction : "synchronizeAll",
			method:"execute",
			jgid:this.mainApp.deptId
		})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		this.refresh();
	}
});