/**
 * 家庭档案-->成员列表-->自动匹配功能所打开的根据网格地址查出来的成员列表页面
 * 
 * @author tianj
 */
$package("chis.application.fhr.script")

$import("chis.script.BizSelectListView")

chis.application.fhr.script.AutoMatchList = function(cfg) {
	cfg.initCnd = [
			'and',
			['ne', ['$', 'a.status'], ['s', '1']],
			['eq', ['$', 'a.regionCode'], ['s', cfg.exContext.args.regionCode]],
			['or',['eq', ['$', 'a.familyId'], ['$', 'null']],['eq', ['$', 'a.familyId'], ['s', '']]]];
	cfg.showButtonOnTop = true;
	cfg.enableCnd = false;
	chis.application.fhr.script.AutoMatchList.superclass.constructor.apply(this, [cfg])
	this.requestData.queryCndsType = "1";
	this.height = 450;
	this.mutiSelect = true
	this.hasOnlyOwner = false;// 是否存在唯一户主
	this.isLeadInOwner = false;// 是否已经选择导入某个户主
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.fhr.script.AutoMatchList, chis.script.BizSelectListView, {
	loadData : function() {
		this.requestData.cnd = [
				'and',
				['ne', ['$', 'a.status'], ['s', '1']],
				['eq', ['$', 'a.regionCode'],
						['s', this.exContext.args.regionCode]],
			    ['or',['eq', ['$', 'a.familyId'], ['$', 'null']],['eq', ['$', 'a.familyId'], ['s', '']]]];
		chis.application.fhr.script.AutoMatchList.superclass.loadData.call(this);
	},

	onReady : function() {
		chis.application.fhr.script.AutoMatchList.superclass.onReady.call(this);
		var module = this.grid.getSelectionModel();
		module.on("beforerowselect", this.checkOwner, this);
		module.on("rowdeselect", this.changLeadInOwner, this);
	},

	checkOwner : function(sm, rowIndex, keepExisting, record) {
		if (this.selects[record.id] != null) {
			return true;
		}
		var isOwner = record.get("masterFlag");
		if (isOwner == "y") {
			if (this.hasOnlyOwner == true) {
				Ext.Msg.alert("提示信息", "该家庭已经存在唯一户主,无法为其再次添加户主!");
				return false;
			} else if (this.isLeadInOwner == false) {
				this.isLeadInOwner = true;
				return true;
			} else if (this.isLeadInOwner == true) {
				Ext.Msg.alert("提示信息", "无法一次导入多个户主,请重新选择!");
				return false;
			}
		} else {
			return true;
		}
	},

	onWinShow : function() {
		this.hasOnlyOwner = false;
		util.rmi.jsonRequest({
					serviceId : 'chis.simpleQuery',
					schema : "chis.application.hr.schemas.EHR_HealthRecord",
					method:"execute",
					cnd : ['and',
							["eq",["$", "familyId"],
									["s",this.exContext.args.initDataId]],
							['eq', ['$', 'masterFlag'], ["s", "y"]]]
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					if (json.body.length != 0) {
						this.hasOnlyOwner = true;
					}
				}, this)
		this.loadData();
	},

	onDblClick : function(grid, index, e) {
		// this.doConfirmSelect()
	},

	doConfirmSelect : function() {
		this.hasOnlyOwner = false;
		this.isLeadInOwner = false;
		this.leadBatch(this.getSelectedRecords())
		this.clearSelect();
		this.grid.getSelectionModel().clearSelections();
		this.win.hide();
	},

	changLeadInOwner : function(sm, rowIndex, record) {
		var isOwner = record.get("masterFlag");
		if (isOwner == "y") {
			this.isLeadInOwner = false;
		}
	},

	doCancel : function() {
		this.fireEvent("cancel");
		this.hasOnlyOwner = false;
		this.isLeadInOwner = false;
		this.clearSelect();
		this.grid.getSelectionModel().clearSelections();
		this.win.hide();
	},

	leadBatch : function(records) {
		this.mask("正在保存数据...", "x-mask-loading");
		var datas = [];
		for (var i = 0; i < records.length; i++) {
			var subData = records[i].data;
			var sendData = {
				"familyId" : subData.familyId,
				"masterFlag" : subData.masterFlag,
				"empiId" : subData.empiId,
				"phrId" : subData.phrId
			};
			datas[i] = sendData;
		}
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction,
					schema : this.entryName,
					method : "execute",
					body : {
						records : datas,
						familyId : this.exContext.args.initDataId
					}
				}, function(code, msg, json) {
					this.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg,
								this.leadBatch, [records]);
						return;
					}
					if (code == 200) {
						this.fireEvent("save", null, null, json, null);
						this.refresh();;
					}
				}, this);
	}
});