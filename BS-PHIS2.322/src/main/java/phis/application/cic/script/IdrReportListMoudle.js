$package("phis.application.cic.script");

$import("phis.script.SimpleList");

phis.application.cic.script.IdrReportListMoudle = function(cfg) {
	this.schema = cfg.entryName;
//	cfg.cnds = ['eq', ['$', 'a.lastModifyUnit'], ['$', '%user.manageUnit.id']];
//	cfg.initCnd = ['eq', ['$', 'a.lastModifyUnit'], ['$', '%user.manageUnit.id']];
	phis.application.cic.script.IdrReportListMoudle.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cic.script.IdrReportListMoudle, phis.script.SimpleList, {
	initPanel : function(sc) {
		var grid = phis.application.cic.script.IdrReportListMoudle.superclass.initPanel
			.call(this, sc);
		var deptId = this.mainApp.deptId;
		this.requestData.cnd = ['like', ['$', 'a.lastModifyUnit'], ['s', deptId+'%']];
		this.initCnd = ['like', ['$', 'a.lastModifyUnit'], ['s', deptId+'%']];
		return grid;
	},
//	loadData : function(){
//		var deptId = this.mainApp.deptId;
//		this.requestData.cnd = ['like', ['$', 'a.lastModifyUnit'], ['s', deptId+'%']];
//		phis.application.cic.script.IdrReportListMoudle.superclass.loadData.call(this);
//	},
	onDblClick : function(grid, index, e) {
		this.doModify();
	},
	doModify : function() {
		//debugger
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var status = r.get("finishStatus");
//				if (status == "1") {
//					this.doUpdateRequest();
//				} else {
//					this.doConfirm();
//				}
		this.initDataId = r.get("RecordID");
		this.empiId = r.get("empiId");
		this.phrId = r.get("phrId");
		this.recordId = r.get("RecordID");
		var m = this.getIdrReportForm(r);
		m.op = "read";
		m.opener = this;
		var win = m.getWin();
		win.add(m.initPanel());
		win.setPosition(250, 100);
		win.show();
		var formData = this.castListDataToForm(r.data, this.schema);
		m.initFormData(formData);
			
			var uid = this.mainApp.uid;
			var role = this.mainApp.jobtitleId;
		if((status==1
		&&role == "phis.55")||role == "phis.88"
		){
			m.setButtonsState([ "save"], false);
		}
		else{
			m.setButtonsState([ "save"], true);
		}
	},
	doCancel : function() {
		debugger
		
	
		body = {};
		//body["IDR_Report"] = this.getFormData();
		body["IDR_Report"]=this.getSelectedRecord().data;
		var r = phis.script.rmi.miniJsonRequestSync({
			serviceId : "phis.clinicManageService",
			serviceAction : "cancel",
			body : body
		});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg,
					this.onBeforeSave);
			return;
		} else {
//			this.panel.el.unmask();
			this.refresh();
			Ext.Msg.alert("提示", "退回成功！");
			
			this.fireEvent("save", this);
			this.changeButtonState("new");
		}
	},
	getIdrReportForm : function(r) {
		var m = this.midiModules["idrReportForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager
					.loadModuleCfg(this.reportDetailForm);
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
			m.on("save", this.refresh, this);
			m.on("close", this.active, this);
			this.midiModules["idrReportForm"] = m;
		} else {
			m.initDataId = r.id;
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
		Ext.applyIf(formData, data)
		return formData;
	},
	onStoreLoadData : function(store, records, ops) {	
		phis.application.cic.script.IdrReportListMoudle.superclass.onStoreLoadData.call(this, store, records, ops);
		var girdcount = 0;
		store.each(function(r) {
			var needCheck = r.get("finishStatus");
			if (needCheck==null || needCheck==""||needCheck=="0") {
				this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
			}
			girdcount += 1;
		}, this);
	},
	jgRender : function(v, params, reocrd) { 
		if (v == '同意') {
			params.style = "color:blue;";
		} else if (v == '不同意') {
			params.style = "color:#FF64B2;";
		}
		return v;
	},
	reportRender : function(v, params, reocrd) { 
		if (v == '未上报') {
			params.style = "color:red;";
		} else if (v == '上报失败') {
			params.style = "color:#FF64B2;";
		} else if (v == '已上报') {
			params.style = "color:blue;";
		}
		return v;
	},
	doReport : function() {
		var r = this.getSelectedRecord();		
		if (r == null || r.data.finishStatus !="1") {//判定是否已审核通过	
			MyMessageTip.msg("提示", "请选择一条已审核通过的信息！", true);
			return;
		}
		if(r.data.categoryBInfectious=="0300"||r.data.categoryBInfectious=="1900"||r.data.categoryBInfectious=="0400"||r.data.categoryBInfectious=="2600"||r.data.categoryBInfectious=="0500"||r.data.categoryBInfectious=="0800"||r.data.categoryBInfectious=="2300"||r.data.otherCategoryInfectious!=""){
			//判定不能报告为传染病报告卡病种	
			MyMessageTip.msg("提示", "该病种只能做统计不能上报！", true);
			return;
		}
		if (r.data.reportFlag=="1") {//判定是否已上报通过	
			MyMessageTip.msg("提示", "该条记录已上报，无需再次上报！", true);
			return;
		}
		if (r.data.reportFlag=="2") {//上报失败	
			Ext.Msg.confirm("提示", "确认要重新上报吗？",
				function(btn) {
					if (btn == 'yes') {
						this.doReport2();
					}
				}, this);
			return;
		}

		this.doReport2();
	},
	doReport2 : function() {
		this.grid.el.mask("正在上报...", "x-mask-loading");
		body = {};
		body["IDR_Report"] = this.getSelectedRecord().data;
		
		var r = phis.script.rmi.miniJsonRequestSync({
			serviceId : "phis.clinicManageService",
			serviceAction : "reportIdr",
			body : body
		});
		if (r.code > 300) {
			Ext.Msg.alert("提示", "上报失败！");
		} else {
			Ext.Msg.alert("提示", r.json.reportresult);
		}
		this.grid.el.unmask();
		this.loadData();
	}
//	onRowClick : function() {
//		//debugger;
//				var r = this.getSelectedRecord();
//				if (!r) {
//					return
//				}
//				var status = r.get("reportFlag");
//				var uid = this.mainApp.uid;
//				var role = this.mainApp.jobtitleId;
//				//var status = r.get("status");
//				var applyUser = r.get("createUser");
//				var deptId = this.mainApp.deptId;
//				var sourceUnit = r.get("createUnit").substring(0, 9);
//
//				if (!bts) {
//					return;
//				}
//			
//					if (status == "0"//role == "phis.88"
//					) {
//						bts.items[5].enable();
//						bts.items[6].disable();
//					} else {
//						bts.items[5].enable();
//						bts.items[7].disable();
////					}
			//	}
				// 控制 -确认-
//				if (status == "0") {
//					if (role == 'phis.system') {
//						bts.items[3].enable();
//					} else if (role == "phis.88") {// 防保长
////						if (moveType == "1" && sourceUnit == deptId) {
//							bts.items[4].enable();
////						} else if (moveType == "2" && targetUnit == deptId) {
////							bts.items[7].enable();
////						} else {
////							bts.items[7].disable();
////						}
//					}
//				} else {
//					if (role == 'phis.system' || role == "phis.88") {
//						bts.items[4].disable();
//					}
//				}
//				// 查看
				//bts.items[5].enable();
				//打印
				//bts.items[6].enable();
		//	}
});