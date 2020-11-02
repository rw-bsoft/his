$package("chis.application.hq.script")

$import("chis.script.BizSimpleListView","chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule")

chis.application.hq.script.HighRiskRecordList = function(cfg) {
	cfg.aotuLoadData = false;
	this.aotuLoadData = false;
	this.needOwnerBar = false;
	chis.application.hq.script.HighRiskRecordList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.hq.script.HighRiskRecordList, chis.script.BizSimpleListView, {
	loadData : function(){
		var deptId = this.mainApp.centerUnit;
		var userId = this.mainApp.uid;
		var jobType = this.mainApp.jobType;
		if(!this.initCnd){
			this.requestData.serviceId = "chis.hqQueryService";
			this.requestData.serviceAction = "queryHighRiskRecordList";
			if(jobType=="T01"){//责任医生
				this.initCnd = ['and',['like', ['$', 'a.createUnit'], ['s', deptId+'%']],['eq', ['$', 'a.manaDoctorId'], ['s', userId]]];
			}else{
				this.initCnd = ['like', ['$', 'a.createUnit'], ['s', deptId+'%']];
			}
			this.requestData.cnd = this.initCnd;
		}else{
			debugger;
		}
		chis.application.hq.script.HighRiskRecordList.superclass.loadData.call(this);
	},
	doVisit : function(item, e) {
		var r = this.getSelectedRecord();
		this.empiId = r.get("EMPIID");
		if(this.empiId)
		this.showEhrViewWin();
	}
	,showEhrViewWin : function() {
		var cfg = {};
		cfg.closeNav = true;
		var visitModule = ['HQ_01'];
		cfg.initModules = visitModule;
		cfg.mainApp = this.mainApp;
		cfg.activeTab =0;
		cfg.needInitFirstPanel = true
		var module= new chis.script.EHRView(cfg);
		module.exContext.ids["empiId"] = this.empiId;
		module.on("save", this.refresh, this);
		module.exContext.ids.recordStatus = this.recordStatus;
		module.getWin().show();
	}
	,onDblClick : function() {
		this.doVisit();
	},
	doModifyStatus : function(){
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var m = this.getHealthStatusModifyForm(r);
		m.opener = this;
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
		var formData = this.castListDataToForm(r.data, this.schema);
		m.initFormData(formData);
	},
	getHealthStatusModifyForm : function(r) {
		var m = this.midiModules["healthStatusModifyForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(this.healthStatusModifyForm);
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
			m.on("save", this.refresh, this);
			m.on("close", this.active, this);
			this.midiModules["healthStatusModifyForm"] = m;
		}else {
			m.initDataId = r.EMPIID;
		}
		return m;
	},
	castListDataToForm : function(data, schema) {
		var formData = {};
		var items = schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var key = it.id;
			if (it.dic) {
				var dicData = {
					"key" : data[key],
					"text" : data[key + "_text"]
				};
				formData[key] = dicData;
			} else {
				formData[key] = data[key];
			}
		}
		Ext.applyIf(formData, data);
		return formData;
	}
				
});