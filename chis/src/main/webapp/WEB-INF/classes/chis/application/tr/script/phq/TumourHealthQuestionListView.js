$package("chis.application.tr.script.phq")

$import("chis.script.BizSimpleListSpanView","util.dictionary.CheckboxDicFactory");

chis.application.tr.script.phq.TumourHealthQuestionListView = function(cfg) {
	cfg.initCnd = cfg.cnds || ["eq", ["$", "a.status"], ["s", "0"]];
	cfg.serverParams = {}
	cfg.serverParams.isFQ = false;
	cfg.isFirstRowShowMergeCells = true;
	chis.application.tr.script.phq.TumourHealthQuestionListView.superclass.constructor
			.apply(this, [cfg]);
	this.querySchema = "chis.application.mpm.schemas.MPM_MasterplateMaintain";
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.phq.TumourHealthQuestionListView,
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
					{xtype: 'tbtext', text: '调查机构:',width:65,style:{textAlign:'center'}},this.manaUtil,
					{xtype: 'tbtext', text: '调查日期:',width:65,style:{textAlign:'center'}},this.startDate,
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
				this.fCb = new Ext.form.Checkbox({
					name: 'FTHQ', 
					hideLabel : true,
					boxLabel: '阴性问卷'
				});
				this.fCb.on("check",function(checkedBox, checked) {
					this.doCndQuery()
				},this);
				cfg.items = [lab, comb,'-',this.hrtCB,'-',this.fCb];
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
				var logoutBtn = bts.items[6];
				var deleteBtn = bts.items[7];
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
			loadData : function(){
				if(!this.firstLoad){
					var endDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
					var startDate = new Date(endDate.getFullYear(),0,1);
					var sd = startDate.format("Y-m-d");
					var ed = endDate.format("Y-m-d");
					var cnd = ['and',["eq", ["$", "a.status"], ["s", "0"]],
										['like',['$','a.surveyUnit'],['s',this.mainApp.deptId]],
										['ge',['$',"str(a.surveyDate,'yyyy-MM-dd')"],['s',sd]],
										['le',['$',"str(a.surveyDate,'yyyy-MM-dd')"],['s',ed]]
										]
					this.requestData.cnd = cnd;
					this.firstLoad = true;
				}
				chis.application.tr.script.phq.TumourHealthQuestionListView.superclass.loadData.call(this);
			},
			onLoadData : function(store){
				this.gridSpan(this.grid,"row","[personName],[sexCode_text],[age][idCard]","personName")
			},
			doModify : function() {
				var r = this.getSelectedRecord();
				this.empiId = r.get("empiId");
				var masterplateId = r.get("masterplateId");
				var gcId = r.get("gcId");
				var masterplateType = r.get("masterplateType");
				var JZXH = r.get("JZXH");
				var status = r.get("status");
				this.showTHQViewWin(masterplateId,gcId,masterplateType,JZXH,status);
			},
			onDblClick : function() {
				this.doModify();
			},
			showTHQViewWin : function(masterplateId,gcId,masterplateType,JZXH,status) {
				var control = {"create":true,"update":true};
				var uid = this.mainApp.uid
				if(status != 0 && uid != 'system'){
					control = {"create":false,"update":false};
				}
				var module = this.createSimpleModule("TPHQModule",this.TPHQModule);
				module.exContext.args = {};
				module.exContext.args.masterplateId=masterplateId;
				module.exContext.args.empiId = this.empiId;
				module.exContext.args.gcId = gcId;
				module.exContext.args.masterplateType = masterplateType;
				module.exContext.args.source = "2";
				module.exContext.args.JZXH = JZXH;
				module.initPanel();
				module.initDataId = null;
				module.exContext.control = control;
				module.on("saveTHQ",this.THQViewSave,this);
				this.showWin(module);
				module.loadData();
			},
			THQViewSave : function(){
				this.refresh();
			},
			doHealthQuestion : function(){
				this.fireEvent("createTPHQ");
			},
			doWriteOff : function() {
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				Ext.Msg.show({
				   title: '确认注销记录',
				   msg: '该记录注销后将不能进行该记录的其他业务操作，是否继续?',
				   modal:true,
				   width: 300,
				   buttons: Ext.MessageBox.OKCANCEL,
				   multiline: false,
				   fn: function(btn, text){
				   	 if(btn == "ok"){
				   	 	this.processLogout();
				   	 }
				   },
				   scope:this
				})
			},
			processLogout : function(){
				var r = this.getSelectedRecord();
				if(!r){
					return;
				}
				var saveRequest = {
					gcId : r.get("gcId"),
					empiId : r.get("empiId"),
					status : "1"
				};
				util.rmi.jsonRequest({
						serviceId:this.saveServiceId,
						serviceAction:this.writeOffAction,
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
					cnd = ['like',['$','a.surveyUnit'],['s',manaUnitId]]
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
					var dcnd = ['and',['ge',['$',"str(a.surveyDate,'yyyy-MM-dd')"],['s',sd]],
												  ['le',['$',"str(a.surveyDate,'yyyy-MM-dd')"],['s',ed]]
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
				//阴性问卷
				if(this.fCb.checked){
					this.requestData.isFQ = true;
				}else{
					this.requestData.isFQ = false;
				}
				if(initCnd && initCnd.length > 0 && cnd.length > 0){
					cnd = ['and', cnd, initCnd];
				}else{
					cnd = initCnd;
				}
				this.queryCnd = cnd
				this.requestData.cnd = cnd
				this.refresh()
			},
			doOneKeyRemove : function(){//删除所选问卷及初筛人群记录，不可恢复，是否确定删除？
				Ext.Msg.show({
					title : '提示',
					msg : '删除所选问卷及初筛人群记录，不可恢复，是否确定删除？',
					modal : true,
					width : 400,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							this.executeOneKeyRemove();
						}
					},
					scope : this
				});
			},
			executeOneKeyRemove : function(){
				var r = this.getSelectedRecord();
				var body = {
					empiId : r.get("empiId"),
					gcId : r.get("gcId"),
					highRiskType : r.get('highRiskType')
				}
				util.rmi.jsonRequest({
						serviceId:this.removeServiceId,
						serviceAction:this.oneKeyRemoveAction,
						method:"execute",
						op:"update",
						schema:this.entryName,
						body:body
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
				this);
				
			}
		});