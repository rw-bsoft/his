$package("chis.application.tr.script.phq");

$import("chis.script.BizSimpleListView");

chis.application.tr.script.phq.TumourHealthEducationCourseList=function(cfg){
	chis.application.tr.script.phq.TumourHealthEducationCourseList.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.tr.script.phq.TumourHealthEducationCourseList,chis.script.BizSimpleListView,{
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
			{xtype: 'tbtext', text: '创建机构:',width:65,style:{textAlign:'center'}},this.manaUtil,
			{xtype: 'tbtext', text: '教育日期:',width:65,style:{textAlign:'center'}},this.startDate,
			{xtype: 'tbtext', text: '→',width:40,style:{textAlign:'center',fontSize: '18px'}},
			this.endDate,queryBtn,'-']
	},
	loadData : function(){
		if(!this.firstLoad){
			var endDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
			var startDate = new Date(endDate.getFullYear(),0,1);
			var sd = startDate.format("Y-m-d");
			var ed = endDate.format("Y-m-d");
			var cnd = ['and',
								['like',['$','a.createUnit'],['s',this.mainApp.deptId]],
								['ge',['$',"str(a.startDate,'yyyy-MM-dd')"],['s',sd]],
								['le',['$',"str(a.startDate,'yyyy-MM-dd')"],['s',ed]]
								]
			this.requestData.cnd = cnd;
			this.firstLoad = true;
		}
		chis.application.tr.script.phq.TumourHealthEducationCourseList.superclass.loadData.call(this);
	},
	doCreateTHECForm : function(){
		var module = this.createSimpleModule("TumourHealthEducationCourse_Form", this.refTHECForm);
		module.initPanel();
		module.on("save", this.afterSave, this);
		module.initDataId = null;
		module.exContext.control = {};
		this.showWin(module);
		module.doNew();
	},
	afterSave : function(entryName,op,json,data){
		this.refresh();
	},
	doModify : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var courseId = r.get("courseId");
		var module = this.createSimpleModule("TumourHealthEducationCourse_Form", this.refTHECForm);
		module.initPanel();
		module.on("save", this.afterSave, this);
		module.initDataId = courseId;
		module.exContext.control = {};
		module.exContext.args={
			courseId:courseId
		};
		this.showWin(module);
		module.loadData();
	},
	onDblClick : function(){
		this.doModify();
	},
	doListenerRegister : function(){
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var courseId = r.get("courseId");
		var type = r.get("type");
		var content = r.get("content");
		var source = r.get("source");
		var thelrModule = this.createSimpleModule("TumourHealthEducationListenerRegister_Module", this.refListenerRegisterModule);
		thelrModule.initPanel();
		thelrModule.on("close",this.refresh,this);
		thelrModule.exContext.control = {};
		thelrModule.exContext.args={
			courseId:courseId,
			type:type,
			content:content,
			source:source
		};
		this.showWin(thelrModule);
		thelrModule.loadData();
	},
	onRowClick:function(grid,index,e){
		this.selectedIndex = index
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var createUser = r.get("createUser");
		var curUser = this.mainApp.uid
		var bts = this.grid.getTopToolbar().items;
		var delBtn = bts.items[8];
		var cruUser = this.mainApp.uid
		if(createUser == curUser || cruUser == 'system'){
			delBtn.enable();
		}else{
			delBtn.disable();
		}
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
			cnd = ['like',['$','a.createUnit'],['s',manaUnitId]]
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
			var dcnd = ['and',['ge',['$',"str(a.startDate,'yyyy-MM-dd')"],['s',sd]],
										  ['le',['$',"str(a.startDate,'yyyy-MM-dd')"],['s',ed]]
										  ]
			if(cnd.length > 0){
				cnd = ['and', cnd, dcnd];
			}else{
				cnd = dcnd;
			}
		}
		this.queryCnd = cnd
		this.requestData.cnd = cnd
		this.refresh()
	}
});