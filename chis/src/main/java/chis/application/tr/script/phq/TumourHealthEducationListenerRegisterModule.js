$package("chis.application.tr.script.phq");

$import("chis.script.BizCombinedModule3","chis.script.util.widgets.MyMessageTip");

chis.application.tr.script.phq.TumourHealthEducationListenerRegisterModule = function(cfg){
	chis.application.tr.script.phq.TumourHealthEducationListenerRegisterModule.superclass.constructor.apply(this,[cfg]);
	this.frame = true;
	this.width=1020;
	this.westWidth = 240;
	this.centerWidth = 520;
	this.eastWidth = 260;
	
}

Ext.extend(chis.application.tr.script.phq.TumourHealthEducationListenerRegisterModule,chis.script.BizCombinedModule3,{
	initPanel : function() {
		var panel = chis.application.tr.script.phq.TumourHealthEducationListenerRegisterModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.firstCollapse = panel.items.items[0];
		this.form=this.midiModules[this.actions[0].id];
		this.list = this.midiModules[this.actions[1].id];
		this.list.on("firstRowSelected",this.ListenerGridOnFirstRowSelected,this);
		this.list.on("rowclick",this.ListenerGridOnRowClick,this);
		this.list.on("createTPHQ",this.onCreateTPHQ,this);
		//this.list.on("saveTHQ",this.onSaveTHQ,this);
		this.hqList = this.midiModules[this.actions[2].id];
		this.hqList.on("createTPHQ",this.onCreateTPHQ,this);
		return panel;
	},
	loadData : function(){
		this.firstCollapse.collapse(true);
		if(this.form){
			this.form.initDataId = this.exContext.args.courseId;
			this.form.loadData();
		}
		if(this.list){
			this.list.initCnd = ['eq',['$','a.courseId'],['s',this.exContext.args.courseId]]
			this.list.requestData.cnd=['eq',['$','a.courseId'],['s',this.exContext.args.courseId]];
			this.list.loadData();
			Ext.apply(this.list.exContext,this.exContext);
		}
		if(this.hqList){
			this.hqList.requestData.cnd=['eq',['$','empiId'],['s','']];
			this.hqList.loadData();
		}
	},
	getPanelItems : function() {
		var firstItem = this.getFirstItem();
		var secondItem = this.getSecondItem();
		var thirdItem = this.getThirdItem();
		var items = [{
					layout : "fit",
					border : false,
					frame : false,
					split : true,
					title : '',
					region : "west",
					width : this.westWidth,
					heigth : this.height,
					collapsible : this.itemCollapsible,
					items : firstItem
				}, {
					layout : "border",
					border : false,
					frame : true,
					split : true,
					title : '',
					region : "center",
					items : [{
								layout : "fit",
								border : false,
								frame : false,
								split : true,
								title : '',
								region : "center",
								width : this.centerWidth || this.width,
								height : this.centerHeight || this.height,
								items : secondItem
							}, {
								collapsible : this.itemCollapsible,
								layout : "fit",
								border : false,
								frame : false,
								split : true,
								title : '',
								region : "east",
								width : this.eastWidth || this.width,
								height : this.eastHeight || this.height,
								items : thirdItem
							}]
				}]
		return items;
	},
	ListenerGridOnFirstRowSelected : function(){
		this.ListenerGridOnRowClick();
	},
	ListenerGridOnRowClick : function(grid,index,e){
		var r = this.list.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		this.hqList.requestData.cnd=['eq',['$','empiId'],['s',empiId]];
		this.hqList.refresh();
	},
	turnDicKey : function(type){
		//字典tumourHighRiskType 转为masterplate字典的对应的key
		//1,2,3,4,5,6 ===> 03,04,05,06,07,08
		var typeArrs = type.split(',');
		var mk = [];
		for(var i=0,len=typeArrs.length; i<len; i++){
			mk.push('\'0'+(parseInt(typeArrs[i])+2)+'\'');
		}
		return mk;
	},
	onCreateTPHQ : function(){
		var r = this.list.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		var sexCode = r.get("sexCode");
		var type = this.exContext.args.type;
		var queryData = {
			empiId:empiId,
			type:type,
			addHEListener : false
		};
		this.panel.el.mask("正在查询,请稍后...", "x-mask-loading")
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.tumourQuestionnaireService",
					serviceAction : "beforAddTHQ",
					method : "execute",
					body : queryData
				})
		this.panel.el.unmask()
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		var existHR = false;
		var body = result.json.body;
		if(body){
			existHR = body.existHR;
		}
		if(existHR){
			var noTRtypes = body.noTRtypes;
			var hasTRtypes = body.hasTRtypes;
			if(hasTRtypes){
				var msg = "";
				var types = type.split(",");
				for(var t = 0, tLen = types.length; t < tLen; t++){
					if(hasTRtypes[types[t]]){
						if(msg == ''){
							msg = hasTRtypes[types[t]] || ''
						}else{
							msg = msg+"</br>"+(hasTRtypes[types[t]] || '')
						}
					}
				}
				if(msg != ''){
					MyMessageTip.msg("提示",msg,true);
				}
			}
			if(noTRtypes && noTRtypes.length > 0){
				this.hqList.empiId = empiId;
				//var type = this.exContext.args.type;
				var mk = this.turnDicKey(noTRtypes);
				this.openTHQModule(mk,empiId,sexCode);
				//this.selectTHQTemplate(mk,empiId);
				if(mk.length == 0 && (!hasTRtypes || (hasTRtypes && hasTRtypes.length == 0)) ){
					MyMessageTip.msg("提示","该听课人员没有可做的问卷",true);
				}
			}else{
				MyMessageTip.msg("提示","该听课人员没有可做的问卷",true);
			}
		}else{
			MyMessageTip.msg("提示","该听课人员没有健康档案或健康档案被注销，不能进行问卷！请先去创建健康档案或恢复健康档案",true);
		}
	},
	openTHQModule : function(mk,empiId,sexCode){
		var module = this.createSimpleModule("THQMModule",this.THQMModule);
		module.exContext.args = {};
		module.exContext.args.sexCode=sexCode;
		module.exContext.args.empiId = empiId;
		module.exContext.args.masterplateTypes = mk;
		module.exContext.args.type=this.exContext.args.type;
		module.exContext.args.modality = this.form.getModalityValue();
		module.exContext.args.courseId = this.exContext.args.courseId;
		module.exContext.args.content = this.exContext.args.content;
		module.exContext.args.source = this.exContext.args.source;
		module.initPanel();
		module.initDataId = null;
		module.exContext.control = {};
		module.on("saveTHQ",this.THQViewSave,this);
		module.on("close",this.onToRefreshTHEListenersList,this);
		this.showWin(module);
		module.loadData();
	},
	onSelectTHQTemplate : function(){
		var r = this.list.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		var sexCode = r.get("sexCode");
		var queryData = {
			empiId:empiId
		};
		this.panel.el.mask("正在查询,请稍后...", "x-mask-loading")
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.tumourQuestionnaireService",
					serviceAction : "existHR",
					method : "execute",
					body : queryData
				})
		this.panel.el.unmask()
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return null;
		}
		var existHR = false;
		if(result.json.body){
			existHR = result.json.body.existHR;
		}
		if(existHR){
			this.hqList.empiId = empiId;
			var type = this.exContext.args.type;
			var mk = this.turnDicKey(type);
			this.selectTHQTemplate(mk,empiId);
		}else{
			Ext.Msg.alert("提示","该听课人员没有健康档案或健康档案被注销，不能进行问卷！请先去创建健康档案或恢复健康档案");
		}
	},
	selectTHQTemplate : function(mk,empiId) {
		this.empiId = empiId;
		var cfg = {
						initCnd:['in',['$','whmb'],[['s',mk.join(',') || '']]],
						entryName : "chis.application.mpm.schemas.MPM_MasterplateMaintain",
						disablePagingTbr : true,
						autoLoadData : false,
						enableCnd : false,
						autoLoadSchema : false,
						modal : true,
						title : "请选择一个肿瘤健康问卷模板",
						width : 600,
						height : 300
					}
		var cls = "chis.application.mpi.script.CombinationSelect";
		var recordSelectView = this.midiModules["THQT_recordSelectView"];
		if (!recordSelectView) {
			$import(cls);
			recordSelectView = eval("new " + cls + "(cfg)");
			recordSelectView.on("onSelect", function(r) {
				var masterplateId = r.get("masterplateId");
				var whmb = r.get("whmb");
				if(masterplateId){
					this.showTHQViewWin(masterplateId,"",whmb,"");
				}
			}, this);
			recordSelectView.initPanel();
			this.midiModules["THQT_recordSelectView"] = recordSelectView;
		}
		recordSelectView.empiId = empiId;
		recordSelectView.getWin().show();
		recordSelectView.requestData.cnd=['in',['$','whmb'],[['s',mk.join(',') || '']]]
		recordSelectView.loadData();
	},
	showTHQViewWin : function(masterplateId,gcId,masterplateType,JZXH) {
		var module = this.createSimpleModule("TPHQModule",this.TPHQModule);
		module.exContext.args = {};
		module.exContext.args.masterplateId=masterplateId;
		module.exContext.args.empiId = this.empiId;
		module.exContext.args.gcId = gcId;
		module.exContext.args.masterplateType = masterplateType;
		module.exContext.args.JZXH = JZXH;
		module.initPanel();
		module.initDataId = null;
		module.exContext.control = {};
		module.on("saveTHQ",this.THQViewSave,this);
		this.showWin(module);
		module.loadData();
	},
	THQViewSave : function(){
		var r = this.list.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		this.hqList.requestData.cnd=['eq',['$','empiId'],['s',empiId]];
		this.hqList.refresh();
	},
	onToRefreshTHEListenersList : function(){
		this.list.refresh();
	}
});