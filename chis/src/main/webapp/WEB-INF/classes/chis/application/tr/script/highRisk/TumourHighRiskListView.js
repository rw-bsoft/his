$package("chis.application.tr.script.highRisk")

$import("chis.script.BizSimpleListView","chis.script.EHRView","chis.application.mpi.script.EMPIInfoModule","util.dictionary.CheckboxDicFactory");

chis.application.tr.script.highRisk.TumourHighRiskListView = function(cfg){
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	chis.application.tr.script.highRisk.TumourHighRiskListView.superclass.constructor.apply(this,[cfg]);
	this.on("firstRowSelected",this.onFirstRowSelected,this);
	this.businessType = "15";
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskListView,chis.script.BizSimpleListView,{
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
					var startDateValue = this.getStartDate(this.businessType);
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
				comb.setWidth(70);
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
					name : 'nature',
					disabled : false,
					width:130,
					items : [{
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
				cfg.items = [lab, comb,'-',this.hrtCB,'-',this.natureCBG];
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
				var logoutBtn = bts.items[13];
				if (logoutBtn) {
					if (status != "0") {
						logoutBtn.disable();
					} else {
						logoutBtn.enable();
					}
				}
				var visitBtn = bts.items[9];
				var confirmedBtn = bts.items[11];
				var normalBtn = bts.items[12];
				//var logoutBtn = bts.items[13];
				if(visitBtn){
					if (status != "0") {
						visitBtn.disable();
					} else {
						visitBtn.enable();
					}
				}
				if(confirmedBtn){
					if (status != "0") {
						confirmedBtn.disable();
					} else {
						confirmedBtn.enable();
					}
				}
				if(normalBtn){
					if (status != "0") {
						normalBtn.disable();
					} else {
						normalBtn.enable();
					}
				}
				if(logoutBtn){
					if (status != "0") {
						logoutBtn.disable();
					} else {
						logoutBtn.enable();
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
				this.refresh();
			},
			natureCheckboxChange : function(field, newValue, oldValue){
				this.doCndQuery()
			},
			loadData : function(){
				if(!this.firstLoad){
					var endDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
					var startDate = new Date(endDate.getFullYear(),0,1);
					var sd = startDate.format("Y-m-d");
					var ed = endDate.format("Y-m-d");
					var cnd = ['and',
										['like',['$','a.manaUnitId'],['s',this.mainApp.deptId]],
										['ge',['$',"str(a.turnHighRiskDate,'yyyy-MM-dd')"],['s',sd]],
										['le',['$',"str(a.turnHighRiskDate,'yyyy-MM-dd')"],['s',ed]]
										]
					this.queryCnd = cnd.slice(0);
					cnd.push(this.initCnd);
					this.requestData.cnd = cnd;
					this.firstLoad = true;
				}
				chis.application.tr.script.highRisk.TumourHighRiskListView.superclass.loadData.call(this);
			},
			doCreateTHR : function(){
				var advancedSearchView = this.midiModules["TumourHighRisk_EMPIInfoModule"];
				if (!advancedSearchView) {
					$import("chis.application.mpi.script.EMPIInfoModule")
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
							{
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this)
					this.midiModules["TumourHighRisk_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			onEmpiSelected : function(data) {
				this.empiId = data.empiId;
				this.highRiskType =  'n';
				this.recordStatus = 0;
				this.activeTab = 0;
				var THRID=null;
				this.showEhrViewWin(THRID);
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				this.highRiskType = r.get("highRiskType");
				this.highRiskSource = r.get("highRiskSource");
				var THRID = r.get("THRID");
				this.activeTab = 0;
				this.showEhrViewWin(THRID);
			},
			onDblClick : function() {
				this.doModify();
			},
			showEhrViewWin : function(THRID) {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['T_01','T_02','T_03'];
				cfg.mainApp = this.mainApp;
				cfg.activeTab = this.activeTab;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["TumourHighRisk_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["TumourHighRisk_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.exContext.ids["highRiskType"] = this.highRiskType || '';
					module.exContext.ids.THRID = THRID;
					module.exContext.ids.recordStatus = this.recordStatus;
					if(!module.exContext.args){
						module.exContext.args={};
					}
					module.exContext.args["highRiskType"] = this.highRiskType || '';
					module.exContext.args.highRiskSource = this.highRiskSource ||'';
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.exContext.ids["highRiskType"] = this.highRiskType || '';
					module.exContext.ids.THRID = THRID;
					module.exContext.ids.recordStatus = this.recordStatus;
					if(!module.exContext.args){
						module.exContext.args={};
					}
					module.exContext.args["highRiskType"] = this.highRiskType || '';
					module.exContext.args.highRiskSource = this.highRiskSource ||'';
					module.refresh();
				}
				module.getWin().show();
			},
			doVisit:function(){
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				this.highRiskType = r.get("highRiskType");
				this.highRiskSource = r.get("highRiskSource");
				var THRID = r.get("THRID");
				this.activeTab = 2;
				this.showEhrViewWin(THRID);
			},
			doWriteOff : function() {
				if (this.store.getCount() == 0) {
					return;
				}
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var cfg = {
					title : "肿瘤高危档案注销",
					THRID : r.get("THRID"),
					phrId : r.get("phrId"),
					personName : r.get("personName"),
					empiId : r.get("empiId"),
					highRiskType : r.get("highRiskType"),
					mainApp : this.mainApp
				};
				var module = this.midiModules["TumourHighRiskRecordLogoutForm"];
				if (!module) {
					$import("chis.application.tr.script.highRisk.TumourHighRiskRecordLogoutForm");
					module = new chis.application.tr.script.highRisk.TumourHighRiskRecordLogoutForm(cfg);
					module.on("writeOff", this.onWriteOff, this);
					module.initPanel();
					this.midiModules["TumourHighRiskRecordLogoutForm"] = module;
				} else {
					Ext.apply(module, cfg);
				}
				module.getWin().show();
			},
			onWriteOff : function() {
				this.refresh();
			},
			doDefiniteDiagnosis : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				this.empiId = r.get("empiId");
				var highRiskType = r.get("highRiskType");
				var proof = false;
				//是否存在健康档案和肿瘤既往史
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.tumourHighRiskService",
							serviceAction : "existTPMHRecord",
							method:"execute",
							body : {"empiId":this.empiId}
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				var body = result.json.body;
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
					var thrData = this.castListDataToForm(r.data,this.schema);
					this.showConfirmedWin(thrData);
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
									this.showPMHView(2);
								}
							},
							scope : this
						});
				}else if(isED){
					Ext.Msg.alert("提示","该人的的健康档案已经迁出或为死亡状态，不能进行肿瘤确诊操作!");
					return;
				}else{
					Ext.Msg.alert("提示","抱歉，程序不知道什么情况，请与开发商联系！！！")
					return;
				}
			},
			showConfirmedWin : function(thrData){
				var tcrData = {};
				tcrData.phrId = thrData.phrId;
				tcrData.empiId = thrData.empiId;
				tcrData.highRiskType = thrData.highRiskType;
				tcrData.highRiskSource = thrData.highRiskSource;
				tcrData.confirmedSource = {"key":"2","text":"高危人群"};
				tcrData.year = thrData.year;
				tcrData.notification = {"key":"n","text":"否"};
				tcrData.status = {"key":"0","text":"正常"};
				tcrData.nature = {"key":"4","text":"确诊"};
				
//				var module = this.createSimpleModule("TumourConfirmedModule",this.refConfirmedModule);
//				module.initPanel();
//				module.on("save", this.refresh, this);
//				module.initDataId = null;
//				module.exContext.control = {};
//				module.exContext.args={};
//				module.exContext.args.tcrData = tcrData;
//				module.exContext.args.turnConfirmed = true;
//				module.saveServiceId = "chis.tumourConfirmedService";
//				module.saveAction = "saveTHRtoConfirmed";
//				module.loadServiceId="chis.tumourConfirmedService";
//				module.loadAction="getTCByEH";
//				this.showWin(module);
//				module.loadData();
				this.showTCEhrViewWin(tcrData);
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
					module.exContext.args.saveAction = "saveTHRtoConfirmed";
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
					module.exContext.args.saveAction = "saveTHRtoConfirmed";
					module.exContext.args.loadServiceId = "chis.tumourConfirmedService";
					module.exContext.args.loadAction="getTCByEH";
					module.exContext.control = {};
					module.refresh();
				}
				module.getWin().show();
			},
			doNormal : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				Ext.Msg.show({
				   title: '确认转正常',
				   msg: '该记录转正常后将不能进行该记录的其他业务操作，是否继续?',
				   modal:true,
				   width: 300,
				   buttons: Ext.MessageBox.OKCANCEL,
				   multiline: false,
				   fn: function(btn, text){
				   	 if(btn == "ok"){
				   	 	this.processTurnNormal();
				   	 }
				   },
				   scope:this
				})
			},
			processTurnNormal : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var saveRequest = {
					phrId : r.get("phrId"),
					empiId : r.get("empiId"),
					highRiskType : r.get("highRiskType")
				};
				util.rmi.jsonRequest({
						serviceId:"chis.tumourHighRiskService",
						serviceAction:"turnNormal",
						method:"execute",
						op:"update",
						schema:this.entryName,
						body:saveRequest
					},
					function(code,msg,json){
						this.saving = false
						if(code > 300){
							this.processReturnMsg(code,msg,this.doWriteOff,[saveRequest],json.body);
							this.fireEvent("exception", code, msg, saveData); // **进行异常处理
							return
						}
						this.refresh();
					},
				this)
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
				var visitBtn = bts.items[9];
				var confirmedBtn = bts.items[11];
				var normalBtn = bts.items[12];
				var logoutBtn = bts.items[13];
				if(visitBtn){
					if(nature == "3" && status == "0"){
						visitBtn.enable();
					}else{
						visitBtn.disable();
					}
				}
				if(confirmedBtn){
					if(nature == "3" && status == "0"){
						confirmedBtn.enable();
					}else{
						confirmedBtn.disable();
					}
				}
				if(normalBtn){
					if(nature == "3" && status == "0"){
						normalBtn.enable();
					}else{
						normalBtn.disable();
					}
				}
				if(logoutBtn){
					if(nature == "3" && status == "0"){
						logoutBtn.enable();
					}else{
						logoutBtn.disable();
					}
				}
			},
			doViewPMH : function(){
				this.showPMHView(0)
			},
			showPMHView : function(activeTabIndex){
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
				module.activeTabIndex = activeTabIndex || 0;
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
					var dcnd = ['and',['ge',['$',"str(a.turnHighRiskDate,'yyyy-MM-dd')"],['s',sd]],
												  ['le',['$',"str(a.turnHighRiskDate,'yyyy-MM-dd')"],['s',ed]]
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