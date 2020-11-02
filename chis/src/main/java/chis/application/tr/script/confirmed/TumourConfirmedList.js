$package("chis.application.tr.script.confirmed")

$import("chis.script.BizSimpleListView","chis.script.EHRView","chis.script.util.widgets.MyMessageTip","util.dictionary.CheckboxDicFactory")

chis.application.tr.script.confirmed.TumourConfirmedList = function(cfg){
	this.initCnd = cfg.cnds || ['and',["eq", ["$", "a.status"], ["s", "0"]],['eq',['$','a.cancerCase'],['s','2']]];
	chis.application.tr.script.confirmed.TumourConfirmedList.superclass.constructor.apply(this,[cfg]);
	this.on("firstRowSelected",this.onFirstRowSelected,this);
}

Ext.extend(chis.application.tr.script.confirmed.TumourConfirmedList,chis.script.BizSimpleListView,{
	getCndBar : function(items){
			this.manaUtil = this.createDicField({
				"src" : "",
				"width" : 120,
				"id" : "chis.@manageUnit",
				"render" : "Tree",
				//"onlySelectLeaf":true,
				//"filter" : "['le',['len',['$','item.key']],['i',9]]",
				"parentKey" : this.mainApp.deptId || {},
				//"defaultValue":this.mainApp.deptId,
				"rootVisible" : "true"
			});
			this.manaUtil.width=120;
			this.manaUtil.tree.expandAll();
			this.manaUtil.tree.on("expandnode", function(node) {
				var key = node.attributes["key"];
				if(key == this.mainApp.deptId){
					this.manaUtil.select(node);
				}
			},this);
			this.manaUtil.on("specialkey", this.onQueryFieldEnter, this);
			//this.manaUtil.on("blur",this.onComboBoxBlur,this);
			var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
			var startDateValue = new Date(curDate.getFullYear(),0,1);
			this.startDate = new Ext.form.DateField({
						name : 'startDate',
						value : startDateValue,
						width : 90,
						altFormats : 'Y-m-d',
						format : 'Y-m-d',
						emptyText : '开始日期'
					});
			this.endDate = new Ext.form.DateField({
						name : 'endDate',
						value : curDate,
						width : 90,
						altFormats : 'Y-m-d',
						format : 'Y-m-d',
						emptyText : '结束日期',
						title:'有开始日期，结束日期为空是默认为当天日期'
					});
			var queryBtn = new Ext.Toolbar.SplitButton({
			iconCls : "query",
			style:{marginLeft:'5px'},
			width:50,
			menu : new Ext.menu.Menu({
				items : {
					text : "高级查询",
					iconCls : "common_query",
					handler : this.doAdvancedQuery,
					scope : this
				}
			})
		})
		this.queryBtn = queryBtn;
		queryBtn.on("click",this.doCndQuery,this);
		return [
			{xtype: 'tbtext', text: '管辖机构:',width:65,style:{textAlign:'center'}},this.manaUtil,
			{xtype: 'tbtext', text: '转高危日期:',width:75,style:{textAlign:'center'}},this.startDate,
			{xtype: 'tbtext', text: '→',width:40,style:{textAlign:'center',fontSize: '18px'}},
			this.endDate,queryBtn,'-']
	},
	getPagingToolbar : function(store) {
		var cfg = {
			pageSize : 25,
			store : store,
			requestData : this.requestData,
			displayInfo : true,
			emptyMsg : "无相关记录"
		};
		var comb = util.dictionary.SimpleDicFactory.createDic({
					id : "chis.dictionary.docStatu",
					forceSelection : true,
					defaultValue : {
						key : "0",
						text : "正常"
					}
				});
		comb.on("select", this.radioChanged, this);
		comb.setValue("01");
		comb.setWidth(80);
		var lab = new Ext.form.Label({
					html : "&nbsp;&nbsp;状态:"
				});
		this.hrtCB = util.dictionary.CheckboxDicFactory.createDic({
			id:"chis.dictionary.tumourHighRiskType",
			columnWidth:45,
			columns:6
		});
		this.hrtCB.setValue("1,2,3,4,5,6");
		var items = this.hrtCB.items;
		for (var i = 0, len = items.length; i < len; i++) {
			var box = items[i];
			box.listeners = {
				'check' : function(checkedBox, checked) {
					this.doCndQuery()
				},
				scope : this
			}
		}
		cfg.items = [lab, comb,'-',this.hrtCB];
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	radioChanged : function(r) {
		var status = r.getValue();
		var navCnd = this.navCnd;
		var queryCnd = this.queryCnd;
		var statusCnd = ['and',["eq", ["$", "a.status"], ["s", status]],['eq',['$','a.cancerCase'],['s','2']]];
		this.initCnd = statusCnd;
		var cnd = statusCnd;
		if (navCnd || queryCnd) {
			cnd = ['and', cnd];
			if (navCnd) {
				cnd.push(navCnd);
			}
			if (queryCnd) {
				cnd.push(queryCnd);
			}
		}

		var bts = this.grid.getTopToolbar().items;
		var btn = bts.items[13];
		var reportCardBtn = bts.items[11];
		if (btn) {
			if (status != "0") {
				btn.disable();
			} else {
				btn.enable();
			}
		}
		if(reportCardBtn){
			if (status != "0") {
				reportCardBtn.disable();
			} else {
				reportCardBtn.enable();
			}
		}
		this.requestData.cnd = cnd;
		this.requestData.pageNo = 1;
		this.refresh();
	},
	loadData : function(){
		this.parent.superLeftPanel.collapse(true);
		if(!this.firstLoad){
			var endDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
			var startDate = new Date(endDate.getFullYear(),0,1);
			var sd = startDate.format("Y-m-d");
			var ed = endDate.format("Y-m-d");
			var cnd = ['and',
								['like',['$','a.manaUnitId'],['s',this.mainApp.deptId]],
								['ge',['$',"to_char(a.confirmedDate,'yyyy-MM-dd')"],['s',sd]],
								['le',['$',"to_char(a.confirmedDate,'yyyy-MM-dd')"],['s',ed]]
								]
			this.queryCnd = cnd.slice(0);
			cnd.push(this.initCnd);					
			this.requestData.cnd = cnd;
			this.firstLoad = true;
		}
		chis.application.tr.script.confirmed.TumourConfirmedList.superclass.loadData.call(this);
	},
	doModify : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var tcrData = this.castListDataToForm(r.data,this.schema);
		this.empiId = r.get("empiId");
		this.recordStatus = r.get("status");
		this.highRiskType = r.get("highRiskType");
		this.activeTab = 0;
		this.showTCEhrViewWin(tcrData);
//		var module = this.createSimpleModule("TumourConfirmedModule",this.refConfirmedModule);
//		module.initPanel();
//		module.on("save", this.refresh, this);
//		module.initDataId = null;
//		module.exContext.control = {};
//		module.exContext.args={};
//		module.exContext.args.tcrData = tcrData;
//		module.saveServiceId = "chis.tumourConfirmedService";
//		module.saveAction = "saveTumourConfirmed";
//		this.showWin(module);
//		module.doNew();
	},
	onDblClick : function(){
		this.doModify();
	},
	showTCEhrViewWin : function(tcrData) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_06'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourConfirmed_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourConfirmed_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType || '';
			module.exContext.ids.TCID = tcrData.TCID;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args["highRiskType"] = this.highRiskType || '';
			module.exContext.args.tcrData = tcrData;
			module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
			module.exContext.args.saveAction = "saveTumourConfirmed";
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType || '';
			module.exContext.ids.TCID = tcrData.TCID;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args["highRiskType"] = this.highRiskType || '';
			module.exContext.args.tcrData = tcrData;
			module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
			module.exContext.args.saveAction = "saveTumourConfirmed";
			module.refresh();
		}
		module.getWin().show();
	},
	doExpertJudgment : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var TERID = r.get("TERID");
		var args = {};
		args.TCID = r.get("TCID");
		args.phrId = r.get("phrId");
		args.empiId = r.get("empiId");
		args.cancerCase = r.get("cancerCase");
		args.status = r.get("status");
		args.nature = r.get('nature');
		var module = this.createSimpleModule("TumourConfirmedReviewModule",this.reviewModule);
		module.initPanel();
		module.on("save", this.refresh, this);
		module.initDataId = TERID || '';
		module.exContext.control = {};
		module.exContext.args = args;
		this.showWin(module);
		module.loadData();
	},
	doTurnHighRisk : function(){
		Ext.Msg.show({
								title : '提示信息',
								msg : '请确认将该人转入高危人群管理，是否确认？',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										var body = this.confirmedToHighRisk();
										if(body.success){
											Ext.Msg.alert("提示","转高危成功！");
											this.refresh();
										}else{
											Ext.Msg.alert("提示","转高危操作失败！请重试");
										}
									}
									return ;
								},
								scope : this
							});
	},
	confirmedToHighRisk : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		var phrId = r.get("phrId");
		var TCID = r.get("TCID");
		var highRiskType = r.get("highRiskType");
		var year = r.get("year");
		var highRiskSource = r.get("highRiskSource");
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.tumourConfirmedService",
					serviceAction : "saveConfirmedToHighRisk",
					method:"execute",
					body : {
								"empiId":empiId,
								"phrId":phrId,
								"TCID":TCID,
								"highRiskType":highRiskType,
								"year":year,
								highRiskSource : highRiskSource
								}
				});
		if (result.code != 200) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		return result.json.body;
	},
	onFirstRowSelected : function(){
		this.onRowClick();
	},
	onRowClick:function(grid,index,e){
		this.selectedIndex = index
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var nature = r.get("nature");
		var status = r.get("status");
		var notification = r.get("notification");
		var bts = this.grid.getTopToolbar().items;
		var reportCardBtn = bts.items[11];
		if(reportCardBtn){
			if(status != 0){
				if(notification == 'y'){
					reportCardBtn.enable();
				}else{
					reportCardBtn.disable();
				}
			}else{
				reportCardBtn.enable();
			}
		}
	},
	doReportCard : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		this.empiId = r.get("empiId");
		this.highRiskType = r.get("highRiskType");
		this.recordStatus = "0";
		this.activeTab = 0;
		//判断健康档案的注销原因是否为 选出 或 死亡
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.tumourConfirmedService",
					serviceAction : "judgeHRisED",
					method:"execute",
					body : {"empiId":this.empiId}
				});
		if (result.code != 200) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		var body = result.json.body;
		var isED = body.isED;
		if(!isED){
			this.showRCWin();
		}else{
			Ext.Msg.alert("提示","该人的的健康档案已经迁出或为死亡状态，不能进行肿瘤确诊操作!");
			return;
		}
	},
	showRCWin : function() {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_04','T_05'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = this.activeTab;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourPatientReportCard_EHRView"];
		if (!module) {
			$import("chis.script.EHRView");
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourPatientReportCard_EHRView"] = module;
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.refresh();
		}
		module.exContext.args={};
		module.exContext.args["empiId"] = this.empiId;
		module.exContext.args["highRiskType"] = this.highRiskType;
		module.exContext.args["turnReport"] = true;
		module.exContext.ids.recordStatus = this.recordStatus;
		module.getWin().show();
	},
	doWriteOff : function() {
		var r = this.getSelectedRecord();
		if (this.store.getCount() == 0) {
			return;
		}
		var cfg = {
			title : "肿瘤确诊注销",
			TCID : r.get("TCID"),
			phrId : r.get("phrId"),
			personName : r.get("personName"),
			empiId : r.get("empiId"),
			highRiskType : r.get("highRiskType"),
			mainApp : this.mainApp
		};
		var module = this.midiModules["TumourConfirmedLogoutForm"];
		if (!module) {
			$import("chis.application.tr.script.confirmed.TumourConfirmedLogoutForm");
			module = new chis.application.tr.script.confirmed.TumourConfirmedLogoutForm(cfg);
			module.on("writeOff", this.refresh, this);
			module.initPanel();
			this.midiModules["TumourConfirmedLogoutForm"] = module;
		} else {
			Ext.apply(module, cfg);
		}
		module.getWin().show();
	},
	doViewPMH : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		var status = r.get("status");
		var control = {"create":true,"update":true};
		var uid = this.mainApp.uid
		if(status != 0 && uid != 'system'){
			control = {"create":false,"update":false};
		}
		var module = this.createSimpleModule("TumourPastMedicalHistoryView",this.refPMHModule);
		module.initPanel();
		module.on("save", this.refresh, this);
		module.initDataId = null;
		module.exContext.control = control;
		module.exContext.args.empiId = empiId;
		var win = module.getWin();
		var width = (Ext.getBody().getWidth()-990)/2
		win.setPosition(width, 10);
		win.show();
		module.loadData();
	},
	doTHRView:function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		this.highRiskType = r.get("highRiskType");
		var phrId = r.get("phrId");
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.tumourHighRiskService",
					serviceAction : "isExitTHR",
					method:"execute",
					body : {
								"empiId":empiId,
								"phrId":phrId,
								"highRiskType":this.highRiskType
								}
				});
		if (result.code != 200) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		var isExistTHR = result.json.body.isExistTHR;
		if(isExistTHR){
			this.showEhrViewWin(empiId);
		}else{
			MyMessageTip.msg("提示", "该病人未建肿瘤高危人群档案！", true);
		}
	},
	showEhrViewWin : function(empiId) {
		var cfg = {};
		cfg.closeNav = true;
		cfg.initModules = ['T_01','T_02','T_03'];
		cfg.mainApp = this.mainApp;
		cfg.activeTab = 0;
		cfg.needInitFirstPanel = true
		var module = this.midiModules["TumourHighRisk_EHRView"];
		if (!module) {
			module = new chis.script.EHRView(cfg);
			this.midiModules["TumourHighRisk_EHRView"] = module;
			module.exContext.ids["empiId"] = empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = empiId;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.refresh();
		}
		module.exContext.ids.recordStatus = this.recordStatus;
		if(!module.exContext.args){
			module.exContext.args={};
		}
		module.exContext.args["highRiskType"] = this.highRiskType;
		module.getWin().show();
	},
	doCndQuery : function(button, e, addNavCnd) {
		var initCnd = this.initCnd || [];
		if (addNavCnd && this.navCnd) {
			if(this.initCnd){
				if(initCnd.length > 0){
					initCnd = ['and', initCnd, this.navCnd];
				}else{
					initCnd = this.navCnd;
				}
			}
		} 
		//取管辖机构
		var cnd = [];
		var manaUnitId = this.manaUtil.getValue();
		if(manaUnitId){
			cnd = ['like',['$','a.manaUnitId'],['s',manaUnitId]]
		}
		//开始、结束日期
		var startDate = this.startDate.getValue();
		var endDate = this.endDate.getValue();
		if(startDate && !endDate){
			endDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
		}
		if(startDate && endDate){
			var df = "yyyy-MM-dd";
			var sd = startDate.format("Y-m-d");
			var ed = endDate.format("Y-m-d");
			var dcnd = ['and',['ge',['$',"to_char(a.confirmedDate,'yyyy-MM-dd')"],['s',sd]],
										  ['le',['$',"to_char(a.confirmedDate,'yyyy-MM-dd')"],['s',ed]]
										  ]
			if(cnd.length > 0){
				cnd = ['and', cnd, dcnd];
			}else{
				cnd = dcnd;
			}
		}
		//高危类型
		var hrtv = this.hrtCB.getValue();
		if(hrtv){
			var hrtvArray = hrtv.split(',');
			var hcnd = [];
			if(hrtvArray.length == 1){
				hcnd = ['eq',['$','a.highRiskType'],['s',hrtv || '']]
			}else{
				hcnd = ['in',['$','a.highRiskType'],[['s',hrtvArray.join(',') || '']]]
			}
			if(cnd.length > 0){
				cnd = ['and', cnd, hcnd];
			}else{
				cnd = hcnd;
			}
		}
		if(initCnd && initCnd.length > 0 && cnd.length > 0){
			cnd = ['and', cnd, initCnd];
		}else{
			cnd = initCnd;
		}
		this.queryCnd = cnd
		this.requestData.cnd = cnd
		this.refresh()
	}
});