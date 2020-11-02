$package("chis.application.her.script")

$import("chis.script.BizSimpleListView", "chis.script.util.helper.Helper")

chis.application.her.script.PlanRecordList = function(cfg) {
	cfg.height = 352;
	chis.application.her.script.PlanRecordList.superclass.constructor.apply(this, [cfg]);
	this.autoLoadData = false;
	this.printurl = chis.script.util.helper.Helper.getUrl();
	this.on("rowclick", this.onRowClick, this);
	this.on("remove",this.onRemove,this);
}

Ext.extend(chis.application.her.script.PlanRecordList, chis.script.BizSimpleListView, {
//	loadData : function(){
//		this.requestData.exeId = this.exContext.args.exeId;
//		//this.requestData.cnd = this.initCnd || ['eq',['$','setId'],['s'+this.exContext.args.exeId || '']];
//		chis.script.client.her.PlanRecordList.superclass.loadData.call(this);
//	},
	
	onRowClick : function(grid, index, e) {
//		var r = this.getSelectedRecord();
//		if (!r) {
//			return
//		}
//		this.initDataId = r.get("recordId");
	},
	
	doCreateHEC : function(){
		var module = this.createSimpleModule("refHECModule",this.refHECModule);
		module.on("save", this.onHECSave, this);
		//module.initDataId = null;
		module.op = "create";
		//module.exContext.control = {"update":true};
		this.showWin(module);
		module.doNew();
	},
	
	doUpdateHEC : function(){
		var r = this.getSelectedRecord();
		if (!r) {
			return
		}
		var module = this.createSimpleModule("refHECModule",this.refHECModule);
		module.op = "update";
		module.exContext.args.setId = r.get("setId");
		module.exContext.args.exeId = r.get("exeId");
		module.exContext.args.recordId = r.get("recordId");
		module.on("save", this.onHECSave, this);
		this.showWin(module);
		module.loadData();
	},
	
	onHECSave : function(entryName, op, json, data){
		this.refresh();
	},
	
	onDblClick : function(){
		this.doUpdateHEC();
	},
	
	onRemove : function(entryName,op,json,recordData){
		this.refresh();
	},
	
	doRemove : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return
		}
		Ext.Msg.show({
					title : '确认删除记录',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processRemove();
						}
					},
					scope : this
				});
	},
	
	doPrintHER : function() {
		if (!this.initDataId) {
			return
		}
		var url = "resources/chis.prints.template.healthEducation.print?type=" + 1 + "&recordId="
				+ this.initDataId;
		url += "&temp=" + new Date().getTime()
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
	}
})