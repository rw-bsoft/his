$package("chis.application.pub.script");

$import("chis.script.BizSimpleListView","chis.application.pub.script.DrugImportModule");

chis.application.pub.script.DrugDirectoryList = function(cfg){
	chis.application.pub.script.DrugDirectoryList.superclass.constructor.apply(this,[cfg]);
	this.saveServiceId = "chis.drugManageService";
	this.saveAction = "saveImportDrug";
};

Ext.extend(chis.application.pub.script.DrugDirectoryList,chis.script.BizSimpleListView,{
	
	doImportDrug : function(){
		this.openImportModule();
	},
	
	openImportModule : function(){
		var DrugQueryWin = this.midiModules["DrugQueryWin"];
		if (!DrugQueryWin) {
			DrugQueryWin = new chis.application.pub.script.DrugImportModule({
				title : "基层医疗药品查询",
				autoLoadSchema : true,
				isCombined : true,
				autoLoadData : false,
				mutiSelect : true,
				queryCndsType : "1",
				formEntryName : "chis.application.pub.schemas.PUB_BaseDrugQueryForm",
				listEntryName : "chis.application.pub.schemas.PUB_BaseDrugQueryList",
				buttonIndex : 3,
				height : 500,
				itemHeight : 195,
				mainApp : this.mainApp
			});
			DrugQueryWin.on("recordSelected", function(records,mdcUse) {
				if (!records) {
					return;
				}
				this.makeData(records,mdcUse);
			}, this);
			this.midiModules["DrugQueryWin"] = DrugQueryWin;
		}
		var win = DrugQueryWin.getWin();
		win.setPosition(250, 100);
		win.show();
	},
	
	makeData : function(records,mdcUse){
		var rs = [];
		for (var i = 0; i < records.length; i++) {
			var record = records[i];
			var data = record.data;
			var r = {};
			r.drugName = data.YPMC;
			r.drugspec = data.YPGG;
			r.drugunits = data.YPDW;
			r.minunits = data.ZXDW;
			r.packnum = data.ZXBZ;
			r.drugtype = data.TYPE;
			r.classcode = data.YPDM;
			r.formulation = data.SXMC;
			r.pycode = data.PYDM;
			r.invalid = data.YPXQ;
			r.mdcUse = mdcUse;
			r.YPXH = data.YPXH;
			rs.push(r);
		}
		var body = {};
		body.drugList = rs;
		this.saveToServer(body);
	},
	
	getSaveRequest : function(saveData){
		return saveData;
	},
	
	saveToServer : function(saveData) {
		var saveRequest = this.getSaveRequest(saveData); // ** 获取保存条件数据
		if (!saveRequest) {
			return;
		}
		if (!this.fireEvent("beforeSave", this.entryName, this.op, saveRequest)) {
			return;
		}
		this.op = "create";
		this.saving = true;
		this.grid.el.mask("正在保存数据...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction || "",
					schema : this.entryName,
					op : this.op,
					method:"execute",
					body : saveRequest
				}, function(code, msg, json) {
					this.grid.el.unmask();
					this.saving = false;
					var resBody = json.body;
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[saveData], resBody);
						this.fireEvent("exception", code, msg, saveData);
						return;
					}
					if (json.success) {
						this.refresh();
					}
					this.fireEvent("save",this.entryName, this.op, json, this.data);
					this.op = "update";
				}, this);
	}
});