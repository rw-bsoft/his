$package("chis.application.tr.script.screening")

$import("chis.script.BizSimpleListSpanView","util.dictionary.CheckboxDicFactory","chis.script.util.widgets.MyMessageTip")

chis.application.tr.script.screening.TumourScreeningList = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	cfg.lockingGrid = true;
	cfg.isFirstRowShowMergeCells = true;
	chis.application.tr.script.screening.TumourScreeningList.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId="chis.tumourScreeningService";
	this.on("firstRowSelected",this.onFirstRowSelected,this);
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.screening.TumourScreeningList,
		chis.script.BizSimpleListSpanView, {
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
					//this.manaUtil.setValue({key:this.mainApp.deptId,text:this.mainApp.dept})
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
					{xtype: 'tbtext', text: '初筛机构:',width:65,style:{textAlign:'center'}},this.manaUtil,
					{xtype: 'tbtext', text: '初筛日期:',width:65,style:{textAlign:'center'}},this.startDate,
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

				this.statusComb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						});
				this.statusComb.on("select", this.radioChanged, this);
				this.statusComb.setValue("01");
				this.statusComb.setWidth(80);
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
				this.natureCBG = new Ext.form.CheckboxGroup({
							width : 200,
							disabled : false,
							items : [
								{
									boxLabel : "初筛",
									inputValue : "1",
									name : "nature"
								},{
									boxLabel : "高危",
									inputValue : "3",
									name : "nature"
								},{
									boxLabel : "正常",
									inputValue : "2",
									name : "nature"
								},{
									boxLabel : "确诊",
									inputValue : "4",
									name : "nature"
								}],
							listeners : {
								change : this.natureCheckboxChange,
								scope : this
							}
						});
				this.cancellationReasonCBG = new Ext.form.CheckboxGroup({
							width : 100,
							disabled : false,
							items : [
								{
									boxLabel : "死亡",
									inputValue : "1",
									name : "cancellationReason"
								},{
									boxLabel : "迁出",
									inputValue : "2",
									name : "cancellationReason"
								}],
							listeners : {
								change : this.cancellationReasonCheckboxChange,
								scope : this
							}
						});
				cfg.items = [lab, this.statusComb,'-',this.hrtCB,'-',this.natureCBG,'-',this.cancellationReasonCBG];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			radioChanged : function(r) {
				var status = r.getValue();
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var statusCnd = ["eq", ["$", "a.status"], ["s", status]];
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
				var criBtn = bts.items[10]
				var turnHighRiskBtn = bts.items[11];
				var definiteDiagnosisBtn = bts.items[12];
				var logoutBtn = bts.items[13];
				var deleteBtn = bts.items[14];
				if(criBtn){
					if (status != "0") {
						criBtn.disable();
					} else {
						criBtn.enable();
					}
				}
				if(turnHighRiskBtn){
					if (status != "0") {
						turnHighRiskBtn.disable();
					} else {
						turnHighRiskBtn.enable();
					}
				}
				if(definiteDiagnosisBtn){
					if (status != "0") {
						definiteDiagnosisBtn.disable();
					} else {
						definiteDiagnosisBtn.enable();
					}
				}
				if(logoutBtn){
					if (status != "0") {
						logoutBtn.disable();
					} else {
						logoutBtn.enable();
					}
				}
				if(deleteBtn){
					if (status != "0") {
						deleteBtn.disable();
					} else {
						deleteBtn.enable();
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			natureCheckboxChange : function(field, newValue, oldValue){
				this.doCndQuery()
			},
			cancellationReasonCheckboxChange : function(field, newValue, oldValue){
				this.statusComb.setValue({key:'1',text:'已注销'});
				this.doCndQuery()
			},
			loadData : function(){
				if(!this.firstLoad){
					var endDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
					var startDate = new Date(endDate.getFullYear(),0,1);
					var sd = startDate.format("Y-m-d");
					var ed = endDate.format("Y-m-d");
					var cnd = ['and',
										['like',['$','a.screeningUnit'],['s',this.mainApp.deptId]],
										['ge',['$',"str(a.screeningDate,'yyyy-MM-dd')"],['s',sd]],
										['le',['$',"str(a.screeningDate,'yyyy-MM-dd')"],['s',ed]]
										]
					this.queryCnd = cnd.slice(0);
					cnd.push(this.initCnd);
					this.requestData.cnd = cnd;
					this.firstLoad = true;
				}
				chis.application.tr.script.screening.TumourScreeningList.superclass.loadData.call(this);
			},
			onLoadData : function(store){
				this.gridSpan(this.grid,"row","[personName],[sexCode_text],[birthday],[idCard],[age],[mobileNumber]","personName")
			},
			afterSave : function(){
				this.refresh();
			},
			doModify : function(){
				this.openModule(0,'modify')
			},
			onDblClick : function(){
				this.doModify();
			},
			doCheckResultInput : function(){
				this.openModule(1,'checkResultInput')
			},
			openModule : function(activeTabIndex,comefrom){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var empiId = r.get("empiId");
				var phrId = r.get("phrId");
				var screeningId = r.get("recordId");
				var status = r.get("status");
				var highRiskSource = r.get("highRiskSource");
				var control = {"create":true,"update":true};
				var uid = this.mainApp.uid
				if(status != 0 && uid != 'system'){
					control = {"create":false,"update":false};
				}
				var module = this.createSimpleModule("TumourScreeningModule",this.refModule);
				module.initPanel();
				module.on("save", this.afterSave, this);
				module.on("close",this.refresh,this);
				module.initDataId = null;
				module.exContext.control = control;
				module.exContext.args.empiId = empiId;
				module.exContext.args.phrId = phrId;
				module.exContext.args.screeningId = screeningId;
				module.exContext.args.highRiskSource = highRiskSource;
				module.exContext.args.comefrom = comefrom;
				module.exContext.args.tsData = this.castListDataToForm(r.data,this.schema);
				module.activeTabIndex = activeTabIndex;
				this.showWin(module);
				module.loadData();
			},
			doWriteOff : function() {
				if (this.store.getCount() == 0) {
					return;
				}
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var personName = r.get("personName");
				if(!personName || personName == ''){
					var rsi = this.selectedIndex-1;
					while(rsi >= 0){
						var lr = this.store.getAt(rsi);
						if(lr){
							var pn = lr.get("personName");
							if(pn && pn.length > 0){
								personName = pn;
								break;
							}
						}
						rsi-=1;
					}
				}
				var cfg = {
					title : "肿瘤初筛记录注销",
					recordId : r.get("recordId"),
					phrId : r.get("phrId"),
					personName : personName,
					empiId : r.get("empiId"),
					mainApp : this.mainApp
				};
				var module = this.midiModules["TumourScreeningLogoutForm"];
				if (!module) {
					$import("chis.application.tr.script.screening.TumourScreeningLogoutForm");
					module = new chis.application.tr.script.screening.TumourScreeningLogoutForm(cfg);
					module.on("writeOff", this.onWriteOff, this);
					module.initPanel();
					this.midiModules["TumourScreeningLogoutForm"] = module;
				} else {
					Ext.apply(module, cfg);
				}
				module.getWin().show();
			},
			onWriteOff : function() {
				this.refresh();
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
				var bts = this.grid.getTopToolbar().items;
				var criBtn = bts.items[10]
				var turnHighRiskBtn = bts.items[11];
				var definiteDiagnosisBtn = bts.items[12];
				var logoutBtn = bts.items[13];
				var deleteBtn = bts.items[14];
				if(criBtn){
					if(status == "0"){
						criBtn.enable();
					}else{
						criBtn.disable();
					}
				}
				if(turnHighRiskBtn){
					if(nature == "1" && status == "0"){
						turnHighRiskBtn.enable();
					}else{
						turnHighRiskBtn.disable();
					}
				}
				if(definiteDiagnosisBtn){
					if(nature == "1" && status == "0"){
						definiteDiagnosisBtn.enable();
					}else{
						definiteDiagnosisBtn.disable();
					}
				}
				if(logoutBtn){
					if (nature == "1" && status == "0") {
						logoutBtn.enable();
					} else {
						logoutBtn.disable();
					}
				}
				if (deleteBtn) {
					if (nature == "1" && status == "0") {
						deleteBtn.enable();
					} else {
						deleteBtn.disable();
					}
				}
			},
			doTurnHighRisk : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var empiId = r.get("empiId");
				var phrId = r.get('phrId');
				if(!empiId){
					Ext.Msg.alert("提示","该记录empiId为空，是不正确的数据,不能执行该操作");
					return ;
				}
				//是否符合转高危标准
				var isAccordWithCriterion = false;
				var body = {
				   empiId:empiId,
				   phrId : phrId,
				   highRiskType:r.get("highRiskType"),
				   recordId : r.get("recordId"),
				   highRiskSource : r.get("highRiskSource")
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.tumourCriterionService",
							serviceAction : "checkTHRCriterion",
							method:"execute",
							body : body
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				var rsBody = result.json.body;
				var isAccordWithCriterion = rsBody.passport;
				var turnTHRSucceed =rsBody.turnTHRSucceed;
				if(turnTHRSucceed){
					MyMessageTip.msg("提示","该病人已转入高危人群管理，请完善其高危人群档案信息！",true);
					var THRID = rsBody.THRID;
					var tsData = this.castListDataToForm(r.data,this.schema);
					tsData.THRID = THRID || '';
					this.refresh();
					this.showTHROfEhrViewWin(tsData);
				}else{
					if(!isAccordWithCriterion){
						MyMessageTip.msg("提示","该病人的初筛记录不符合高危标准，不能转高危",true);
					}else{
						MyMessageTip.msg("提示","转高危 失败 , 请重试",true);
					}
				}
//				isAccordWithCriterion = result.json.body.passport;
//				if(isAccordWithCriterion){
//					var tsData = this.castListDataToForm(r.data,this.schema);
//					this.showTHROfEhrViewWin(tsData);
//				}else{
//					Ext.Msg.alert("提示信息","该人员不符合转高危标准，不能转为肿瘤高危管理！");
//				}
			},
			showTHROfEhrViewWin : function(tsData) {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['T_01','T_02','T_03'];
				cfg.mainApp = this.mainApp;
				cfg.activeTab = 0;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["TumourHighRisk_EHRView"];
				if (!module) {
					$import("chis.script.EHRView");
					module = new chis.script.EHRView(cfg);
					this.midiModules["TumourHighRisk_EHRView"] = module;
					module.exContext.ids["empiId"] = tsData.empiId;
					module.exContext.ids["highRiskType"] = tsData.highRiskType.key;
					module.exContext.ids["THRID"] = tsData.THRID;
					module.exContext.ids.recordStatus = this.recordStatus;
					module.exContext.args = {};
					module.exContext.args.screeningId = tsData.recordId;
					module.exContext.args.highRiskType = tsData.highRiskType.key;
					module.exContext.args.turnHighRisk=true;
					module.exContext.args.tsData = tsData;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = tsData.empiId;
					module.exContext.ids["highRiskType"] = tsData.highRiskType.key;
					module.exContext.ids["THRID"] = tsData.THRID;
					module.exContext.ids.recordStatus = this.recordStatus;
					module.exContext.args = {};
					module.exContext.args.screeningId = tsData.recordId;
					module.exContext.args.highRiskType = tsData.highRiskType.key;
					module.exContext.args.turnHighRisk=true;
					module.exContext.args.tsData = tsData;
					module.refresh();
				}
				module.getWin().show();
			},
			doDefiniteDiagnosis : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				this.empiId = r.get("empiId");
				var highRiskType = r.get("highRiskType");
				//是否存在健康档案和肿瘤既往史
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.tumourScreeningService",
							serviceAction : "confirmedQualificationExamination",
							method:"execute",
							body : {"empiId":this.empiId}
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				var body = result.json.body;
				if(body.success == true){
					var isExistTPMH = body.isExistTPMH;
					var validateTPMH = false;
					if(highRiskType == "1"){//大肠类必须要有 肿瘤既史
						if(isExistTPMH){
							validateTPMH = true;
						}
					}else{
						validateTPMH = true;
					}
					var isED = body.isED;
					if(validateTPMH && !isED){
						var phrId = body.phrId;
						this.showConfirmedWin(phrId,r.data);
					}else if(!validateTPMH){
						Ext.Msg.show({
								title : '提示信息',
								msg : '该人没有肿瘤既往史，请先为该人创建肿瘤既往史，是否现在创建肿瘤既往史?',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.openModule(2,'definiteDiagnosis');
									}
								},
								scope : this
							});
					}else if(isED){
						Ext.Msg.alert("提示","该人的的健康档案已经迁出或为死亡状态，不能进行肿瘤确诊操作!");
						return;
					}
				}else{
					Ext.Msg.show({
								title : '提示信息',
								msg : '该人没有健康档案，请先为该人创建健康档案，是否现在建立健康档案?',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										var cfg = {
												closeNav : true,
												initModules : ['B_01', 'B_02', 'B_03', 'B_04', 'B_05'],
												mainApp : this.mainApp,
												empiId : this.empiId,
												activeTab : 0
											};
											$import("chis.script.EHRView");
											var m = new chis.script.EHRView(cfg);
											m.getWin().show();
									}
									return ;
								},
								scope : this
							});
				}
			},
			showConfirmedWin : function(phrId,tsData){
				var tsfData = this.castListDataToForm(tsData,this.schema);
				var tcrData = {};
				tcrData.phrId = phrId;
				tcrData.empiId = tsfData.empiId;
				tcrData.highRiskType = tsfData.highRiskType;
				tcrData.highRiskSource = tsfData.highRiskSource;
				tcrData.year = tsfData.year;
				tcrData.confirmedSource = {"key":"1","text":"初筛"};
				tcrData.notification = {"key":"n","text":"否"};
				tcrData.status = {"key":"0","text":"正常"};
				tcrData.nature = {"key":"4","text":"确诊"};
				tcrData.recordId = tsData.recordId;
				this.showTCEhrViewWin(tcrData)
			},
			showTCEhrViewWin : function(tcrData) {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['T_06'];
				cfg.mainApp = this.mainApp;
				cfg.activeTab = 0;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["TumourConfirmed_EHRView"];
				if (!module) {
					$import("chis.script.EHRView");
					module = new chis.script.EHRView(cfg);
					this.midiModules["TumourConfirmed_EHRView"] = module;
					module.exContext.ids.empiId = tcrData.empiId;
					module.exContext.ids.highRiskType = tcrData.highRiskType.key || '';
					module.exContext.ids.TCID = tcrData.TCID;
					module.exContext.ids.recordStatus = 0;
					if(!module.exContext.args){
						module.exContext.args={};
					}
					module.exContext.args.empiId = tcrData.empiId;;
					module.exContext.args.highRiskType = tcrData.highRiskType.key || '';
					module.exContext.args.tcrData = tcrData;
					module.exContext.args.turnConfirmed = true;
					module.exContext.args.recordId = tcrData.recordId;
					module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
					module.exContext.args.saveAction = "saveTumourScreeningToConfirmed";
					module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
					module.exContext.args.loadAction="getTCByEH";
					module.exContext.control = {};
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids.empiId = tcrData.empiId;
					module.exContext.ids.highRiskType = tcrData.highRiskType.key || '';
					module.exContext.ids.TCID = tcrData.TCID;
					module.exContext.ids.recordStatus = 0;
					if(!module.exContext.args){
						module.exContext.args={};
					}
					module.exContext.args.empiId = tcrData.empiId;;
					module.exContext.args.highRiskType = tcrData.highRiskType.key || '';
					module.exContext.args.tcrData = tcrData;
					module.exContext.args.turnConfirmed = true;
					module.exContext.args.recordId = tcrData.recordId;
					module.exContext.args.saveServiceId = "chis.tumourConfirmedService";
					module.exContext.args.saveAction = "saveTumourScreeningToConfirmed";
					module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
					module.exContext.args.loadAction="getTCByEH";
					module.exContext.control = {};
					module.refresh();
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
					cnd = ['like',['$','a.screeningUnit'],['s',manaUnitId]]
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
					var dcnd = ['and',['ge',['$',"str(a.screeningDate,'yyyy-MM-dd')"],['s',sd]],
												  ['le',['$',"str(a.screeningDate,'yyyy-MM-dd')"],['s',ed]]
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
				//性质
				var nArr = this.natureCBG.getValue();
				var nv = "";
				for(var ni = 0,nlen=nArr.length;ni<nlen;ni++){
					nv += nArr[ni].inputValue
					if(ni < nlen-1){
						nv+=',';
					}
				}
				if(nv.length == 1){
					var ncnd = ['eq',['$','a.nature'],['s',nv]]
					if(cnd.length > 0){
						cnd = ['and', cnd, ncnd];
					}else{
						cnd = ncnd;
					}
				}else if(nv.length > 1){
					var ncnd = ['in',['$','a.nature'],[['s',nv]]] 
					if(cnd.length > 0){
						cnd = ['and', cnd, ncnd];
					}else{
						cnd = ncnd;
					}
				}
				//注销原因
				var crArr = this.cancellationReasonCBG.getValue();
				var crv = "";
				for(var cri = 0,crlen=crArr.length;cri<crlen;cri++){
					crv += crArr[cri].inputValue
					if(cri < crlen-1){
						crv+=',';
					}
				}
				if(crv.length == 1){
					var crcnd = ['and',['eq', ['$', 'a.status'], ['s', '1']],['eq',['$','a.nature'],['s',crv]]]
					if(cnd.length > 0){
						cnd = ['and', cnd, crcnd];
					}else{
						cnd = crcnd;
					}
				}else if(crv.length > 1){
					var crcnd = ['and',['eq', ['$', 'a.status'], ['s', '1']],['in',['$','a.nature'],[['s',crv]]]] 
					if(cnd.length > 0){
						cnd = ['and', cnd, crcnd];
					}else{
						cnd = crcnd;
					}
				}
				if(initCnd && initCnd.length > 0 && cnd.length > 0){
					if(crv.length == 0){
						cnd = ['and', cnd, initCnd];
						this.statusComb.setValue({key:'0',text:'正常'});
					}
				}else{
					cnd = initCnd;
				}
				this.queryCnd = cnd
				this.requestData.cnd = cnd
				this.refresh()
			}
});