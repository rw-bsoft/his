$package("chis.application.tr.script.highRisk")

$import("chis.script.BizSimpleListView","chis.script.EHRView","util.dictionary.CheckboxDicFactory","chis.script.util.widgets.MyMessageTip");

chis.application.tr.script.highRisk.TumourHighRiskVisitPlanListView = function(cfg){
	this.initCnd = cfg.cnds || ["eq", ["$", "a.businessType"], ["s", "15"]];
	chis.application.tr.script.highRisk.TumourHighRiskVisitPlanListView.superclass.constructor.apply(this,[cfg]);
	this.businessType = "15";
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskVisitPlanListView,chis.script.BizSimpleListView,{
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
			{xtype: 'tbtext', text: '计划随访日期:',width:80,style:{textAlign:'center'}},this.startDate,
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
		this.hrtCB = util.dictionary.CheckboxDicFactory.createDic({
			id:"chis.dictionary.tumourHighRiskType",
			columnWidth:45,
			columns:6
		});
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
		this.planStatusCBG = new Ext.form.CheckboxGroup({
			name : 'planStatus',
			disabled : false,
			width:300,
			items : [{
						boxLabel : "应访",
						inputValue : "0",
						name : "nature"
					},{
						boxLabel : "已访",
						inputValue : "1",
						name : "nature"
					},{
						boxLabel : "失访",
						inputValue : "2",
						name : "nature"
					},{
						boxLabel : "未访",
						inputValue : "3",
						name : "nature"
					},{
						boxLabel : "过访",
						inputValue : "4",
						name : "nature"
					},{
						boxLabel : "结案",
						inputValue : "8",
						name : "nature"
					},{
						boxLabel : "注销",
						inputValue : "9",
						name : "nature"
					}],
			listeners : {
				change : this.planStatusCheckboxChange,
				scope : this
			}
		});
		cfg.items = ['-',this.hrtCB,'-',this.planStatusCBG,'-'];
		var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
		this.pagingToolbar = pagingToolbar;
		return pagingToolbar;
	},
	planStatusCheckboxChange : function(field, newValue, oldValue){
		this.doCndQuery()
	},
	loadData : function(){
		if(!this.firstLoad){
			var endDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
			var startDate = new Date(endDate.getFullYear(),0,1);
			var sd = startDate.format("Y-m-d");
			var ed = endDate.format("Y-m-d");
			var cnd = ['and',["eq", ["$", "a.businessType"], ["s", "15"]],
								['like',['$','d.manaUnitId'],['s',this.mainApp.deptId]],
								['ge',['$',"to_char(a.planDate,'yyyy-MM-dd')"],['s',sd]],
								['le',['$',"to_char(a.planDate,'yyyy-MM-dd')"],['s',ed]]
								]
			this.requestData.cnd = cnd;
			this.queryCnd = cnd;
			this.firstLoad = true;
		}
		chis.application.tr.script.highRisk.TumourHighRiskVisitPlanListView.superclass.loadData.call(this);
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
			cnd = ['like',['$','d.manaUnitId'],['s',manaUnitId]]
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
			var dcnd = ['and',['ge',['$',"to_char(a.planDate,'yyyy-MM-dd')"],['s',sd]],
										  ['le',['$',"to_char(a.planDate,'yyyy-MM-dd')"],['s',ed]]
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
				hcnd = ['eq',['$','e.highRiskType'],['s',hrtv || '']]
			}else{
				hcnd = ['in',['$','e.highRiskType'],[['s',hrtvArray.join(',') || '']]]
			}
			if(cnd.length > 0){
				cnd = ['and', cnd, hcnd];
			}else{
				cnd = hcnd;
			}
		}
		//计划状态
		var psArr = this.planStatusCBG.getValue();
		var nv = "";
		for(var ni = 0,nlen=psArr.length;ni<nlen;ni++){
			nv += psArr[ni].inputValue
			if(ni < nlen-1){
				nv+=',';
			}
		}
		if(nv.length == 1){
			var ncnd = ['eq',['$','a.planStatus'],['s',nv]]
			if(cnd.length > 0){
				cnd = ['and', cnd, ncnd];
			}else{
				cnd = ncnd;
			}
		}else if(nv.length > 1){
			var ncnd = ['in',['$','a.planStatus'],[['s',nv]]] 
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
	},
	onDblClick : function() {
		this.doVisit();
	},
	doVisit:function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		this.empiId = r.get("empiId");
		this.recordStatus = r.get("status");
		this.highRiskType = r.get("highRiskType");
		this.activeTab = 2;
		var planId = r.get("planId");
		this.THRID = r.get("recordId");
		if(this.empiId == ''){
			MyMessageTip.msg("提示","计划中empiId为空！",true);
			return;
		}
		this.showEhrViewWin(planId);
	},
	showEhrViewWin : function(planId) {
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
			module.exContext.ids["THRID"] = this.THRID;
			module.exContext.ids["Pkey"] = this.THRID;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.selectedPlanId = planId;
			module.exContext.args.THRID = this.THRID;
			module.on("save", this.refresh, this);
		} else {
			Ext.apply(module, cfg);
			module.exContext.ids = {};
			module.exContext.ids["empiId"] = this.empiId;
			module.exContext.ids["THRID"] = this.THRID;
			module.exContext.ids["Pkey"] = this.THRID;
			module.exContext.ids["highRiskType"] = this.highRiskType;
			module.exContext.ids.recordStatus = this.recordStatus;
			if(!module.exContext.args){
				module.exContext.args={};
			}
			module.exContext.args.selectedPlanId = planId;
			module.exContext.args.THRID = this.THRID;
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
		if(empiId == ''){
			MyMessageTip.msg("提示","计划中empiId为空！",true);
			return;
		}
		var module = this.createSimpleModule("TumourPastMedicalHistoryView",this.refPMHModule);
		module.initPanel();
		module.on("save", this.refresh, this);
		module.initDataId = null;
		module.exContext.control = {"create":false,"update":false};
		module.exContext.args.empiId = empiId;
		var win = module.getWin();
		var width = (Ext.getBody().getWidth()-990)/2
		win.setPosition(width, 10);
		win.show();
		module.loadData();
	}
});