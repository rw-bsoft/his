//糖尿病档案管理主页面
$package("chis.application.dbs.script.record");

$import("chis.script.BizSimpleListView","chis.script.EHRView",
		"chis.application.mpi.script.EMPIInfoModule",
		"chis.application.dbs.script.record.DiabetesRecordLogoutFormView");

chis.application.dbs.script.record.DiabetesRecordListView = function(cfg) {
	this.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	//this.needOwnerBar=true;
	chis.application.dbs.script.record.DiabetesRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.businessType="2";
}

Ext.extend(chis.application.dbs.script.record.DiabetesRecordListView,
		chis.script.BizSimpleListView, {
			init : function() {
				this.addEvents({
							"gridInit" : true,
							"beforeLoadData" : true,
							"loadData" : true,
							"loadSchema" : true
						})
				this.requestData = {
					serviceId : this.listServiceId,
					serviceAction : this.listAction,
					method:"execute",
					schema : this.entryName,
					cnd : this.initCnd,
					pageSize : this.pageSize || 25,
					pageNo : 1
				}
				if (this.serverParams) {
					Ext.apply(this.requestData, this.serverParams)
				}
				if (this.autoLoadSchema) {
					this.getSchema();
				}
			},
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}

				var comb = util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.docStatu",
							forceSelection : true,
							defaultValue : {
								key : "0",
								text : "正常"
							}
						})
				comb.on("select", this.radioChanged, this)
				comb.setValue("01")
				comb.setWidth(60)
				var ndsfcheck=util.dictionary.SimpleDicFactory.createDic({
							id : "chis.dictionary.yearvisit",
							forceSelection : true,
							defaultValue : {
								key : "9",
								text : "不关联"
							}
						});
				ndsfcheck.on("select", this.ndsfradioChanged, this);
				ndsfcheck.setValue("9");
				ndsfcheck.setWidth(60);
				this.ndsfcheck=ndsfcheck;				
				
				var lab = new Ext.form.Label({
							html : "&nbsp;&nbsp;状态:"
						});
				var ndsflab = new Ext.form.Label({
							html : "&nbsp;&nbsp;年随:"
						});		
				var yearCheckItems=this.getYearCheckItems();
				cfg.items = [lab, comb,yearCheckItems,ndsflab,ndsfcheck]
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},
			yearFieldSelect : function(combo, record, index) {
				this.requestData.year = this.yearField.getValue();
				if(this.ndsfcheck){
					this.ndsfradiocnd(this.ndsfcheck.getValue());
				}
				this.doCndQuery();
			},			
			radioChanged : function(r) {
				var status = r.getValue()
				var navCnd = this.navCnd
				var queryCnd = this.queryCnd
				var statusCnd = ['eq', ['$', 'a.status'], ['s', status]]
				//yx-2017-05-14查询全部档案-开始 
				if(status==8){
					var str="0"+"','"+1;
					statusCnd =["in",["$", "a.status"],[str]];
				}
				//yx-2017-05-14查询全部档案-结束
				this.initCnd = statusCnd;
				var cnd = statusCnd;
				if (navCnd || queryCnd) {
					cnd = ['and', cnd];
					if (navCnd) {
						cnd.push(navCnd)
					}
					if (queryCnd) {
						cnd.push(queryCnd)
					}
				}
				this.requestData.cnd = cnd
				this.requestData.pageNo = 1
				this.refresh()
			},
			ndsfradioChanged : function(r) {
				this.ndsfradiocnd(r.getValue());
				this.refresh();
			},
			ndsfradiocnd:function(status){
				var navCnd = this.navCnd;
				var queryCnd = this.queryCnd;
				var initCnd=this.initCnd
				var ndsfCnd=['notNull',['$', 'a.empiId']];
				if(status==1){
					ndsfCnd= ['in', ['$', 'a.empiId'], 
				             ["select dia.empiId from MDC_DiabetesVisit dia where a.empiId=dia.empiId and to_char(dia.visitDate,'yyyy')='"+this.yearField.getValue()+"'"]];
				}else
				if(status==0){
					ndsfCnd= ['notin', ['$', 'a.empiId'], 
				             ["select dia.empiId from MDC_DiabetesVisit dia where a.empiId=dia.empiId and to_char(dia.visitDate,'yyyy')='"+this.yearField.getValue()+"'"]];
				}
				this.ndsfCnd = ndsfCnd;
				var cnd = ndsfCnd;
				if (navCnd || queryCnd ||initCnd) {
					cnd = ['and', cnd];
					if (navCnd && navCnd.length > 0) {
						cnd.push(navCnd);
					}
					if (queryCnd && queryCnd.length > 0) {
						cnd.push(queryCnd);
					}
					if (initCnd && initCnd.length > 0) {
						cnd.push(initCnd);
					}
				}
				this.requestData.cnd = cnd;
				this.requestData.pageNo = 1;
			} ,
			doCreateByEmpi : function() {
				var advancedSearchView = this.midiModules["DiabetesRecordListView_EMPIInfoModule"];
				if (!advancedSearchView) {
					advancedSearchView = new chis.application.mpi.script.EMPIInfoModule({
								title : "个人基本信息查找",
								mainApp : this.mainApp,
								modal : true
							});
					advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
							this)
					this.midiModules["DiabetesRecordListView_EMPIInfoModule"] = advancedSearchView;
				}
				var win = advancedSearchView.getWin();
				win.setPosition(250, 100);
				win.show();
			},
			
			onEmpiSelected : function(data) {
				this.empiId = data["empiId"];
				this.recordStatus = 0;
				this.activeTab = 0;
				this.showEhrViewWin();
			},
			
			showEhrViewWin : function() {
				var cfg = {};
				cfg.closeNav = true;
				cfg.initModules = ['D_01', 'D_02', 'D_03', 'D_05', 'D_04','D_08']
				cfg.mainApp = this.mainApp;
				cfg.activeTab = this.activeTab;
				cfg.needInitFirstPanel = true
				var module = this.midiModules["DiabetesRecordListView_EHRView"];
				if (!module) {
					module = new chis.script.EHRView(cfg);
					this.midiModules["DiabetesRecordListView_EHRView"] = module;
					module.exContext.ids["empiId"] = this.empiId;
					module.on("save", this.refresh, this);
				} else {
					Ext.apply(module, cfg);
					module.exContext.ids = {};
					module.exContext.ids["empiId"] = this.empiId;
					module.refresh();
				}
				module.getWin().show();
			},
			
			doModify : function() {
				if (this.store.getCount() == 0) {
					return
				}
				var r = this.grid.getSelectionModel().getSelected()
				this.empiId = r.get("empiId");
				debugger;
				this.recordStatus = r.get("status");
				var date = new Date()
				if(date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()=="2016-4-12"){
				this.doLook();
				}
				this.activeTab = 0;
				this.showEhrViewWin();
			},
			onDblClick : function(grid, index, e) {
				this.doModify()
			},
			onSave : function(entryName, op, json, data) {
				if(op =="create"){
					this.refresh()
				}
			},
//			onStoreLoadData : function(store, records, ops) {
//				chis.application.dbs.script.record.DiabetesRecordListView.superclass.onStoreLoadData
//						.call(this, store, records, ops);
//				this.onRowClick()
//			},
			onStoreLoadData : function(store, records, ops) { // add by zhangwei 2015-07-02
				chis.application.dbs.script.record.DiabetesRecordListView.superclass.onStoreLoadData
						.call(this, store, records, ops);
				// this.grid.fireEvent("rowclick", this);
				this.onRowClick();
				
				var girdcount = 0;
				store.each(function(r) {
					//alert(r.get("needDoVisit"));
					var needVisit = r.get("needDoVisit");
					if (needVisit) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					girdcount += 1;
				}, this);
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return
				}
				
				var bts = this.grid.getTopToolbar().items;
				if(bts.items[8]){
					var status = r.get("status");
					if (status == 1) {
						bts.items[8].disable();
					} else {
						bts.items[8].enable();
					}
				}
			},
			doWriteOff : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
                if(!(this.mainApp.jobId=='chis.14'|| this.mainApp.jobId=='chis.system')){
                	if(r.get("manaDoctorId")!=this.mainApp.uid){
                	alert("只有防保科长或现责任医生能注销档案！")
                	return;
                	}
                }
				var cfg = {}
				cfg.record = r
				cfg.mainApp = this.mainApp
				var module = this.midiModules["DiabetesRecordLogoutFormView"];
				if (!module) {
					module = new chis.application.dbs.script.record.DiabetesRecordLogoutFormView(cfg);
					module.on("writeOff", this.onWriteOff, this);
					this.midiModules["DiabetesRecordLogoutFormView"] = module;
				} else {
					Ext.apply(module, cfg)
				}
				module.getWin().show();
			},
			onWriteOff : function(entryName, op, json, data) {
				var index = this.grid.store.find("phrId", data.phrId);
				var r = this.grid.store.getAt(index);
				if (!r) {
					return
				}
				this.store.remove(r)
				this.store.commitChanges()
			},
			doVisit : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				this.recordStatus = r.get("status");
				this.activeTab = 2;
				this.showEhrViewWin();
			},
			doDelete : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				if(this.mainApp.jobId=='chis.14'|| this.mainApp.jobId=='chis.system'||
                	record.get("manaDoctorId")==this.mainApp.uid){
                }else{
                	alert("只有防保科长或现责任医生能注销档案！");
                	return;
                }
                Ext.Msg.show({
							title : '档案删除',
							msg : '档案删除后将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var deletemdc = this.midiModules["MDC.deletemdc"];
				if (!deletemdc) {
					deletemdc = new chis.application.dbs.script.record.DiabetesRecordDelete(
							{
								record : record,
								mainApp : this.mainApp
							});
					deletemdc.on("writeOff", this.onWriteOff, this);
					this.midiModules["MDC.deletemdc"] = deletemdc;
				} else {
					deletemdc.record = record;
				}
				deletemdc.getWin().show();
								}
							},
							scope : this
						});
			},
			doLook : function(){
				Ext.Msg.alert("友情提醒：","糖尿病分组已修改为分组评估生成随访模式，如没有随访计划请先评估，再去随访页面点击【本年度】刷新随访！");
			}
		});