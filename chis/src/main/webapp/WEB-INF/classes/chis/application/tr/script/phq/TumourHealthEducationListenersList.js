$package("chis.application.tr.script.phq");

$import("chis.script.BizSimpleListView","chis.script.util.widgets.MyMessageTip");

chis.application.tr.script.phq.TumourHealthEducationListenersList = function(cfg){
	chis.application.tr.script.phq.TumourHealthEducationListenersList.superclass.constructor.apply(this,[cfg]);
	this.autoLoadData = false;
	this.saveMethod="execute";
	this.saveServiceId="chis.tumourHealthEducationCourseService";
	this.saveAction="saveListener";
	this.cndFieldWidth=100;
	this.on("loadData",this.onLoadData,this);
}

Ext.extend(chis.application.tr.script.phq.TumourHealthEducationListenersList,chis.script.BizSimpleListView,{
	initPanel:function(sc){
		if(this.grid){
			if(!this.isCombined){
				this.fireEvent("beforeAddToWin",this.grid)
				this.addPanelToWin();
			}
			return this.grid;
		}
		var schema = sc
		if(!schema){
			var re = util.schema.loadSync(this.entryName)
			if(re.code == 200){
				schema = re.schema;
			}
			else{
				this.processReturnMsg(re.code,re.msg,this.initPanel)
				return;
			}
		}
		this.schema = schema;
		this.isCompositeKey = schema.compositeKey;
		var items = schema.items
		if(!items){
			return;
		}
		this.store = this.getStore(items)
		this.cm = new Ext.grid.ColumnModel(this.getCM(items))
		var cfg = {
			stripeRows : true,
	        border:false,
			store: this.store,
        	cm: this.cm,
        	height:this.height,
        	loadMask:{msg:'正在加载数据...',msgCls:'x-mask-loading'},
        	buttonAlign:'center',
        	clicksToEdit:1,
        	frame:true,
			plugins: this.rowExpander,
			hideHeaders:this.hideHeaders||false,
			viewConfig:{
				//forceFit : true, 
				getRowClass:this.getRowClass
			}
		}
		if(this.sm){
			cfg.sm = this.sm
		}
		if(this.viewConfig){
			Ext.apply(cfg.viewConfig,this.viewConfig)
		}
		if(this.gridDDGroup){
			cfg.ddGroup = this.gridDDGroup;
			cfg.enableDragDrop = true
		}
		var cndbars = this.getCndBar(items)
		if(!this.disablePagingTbr){
			cfg.bbar = this.getPagingToolbar(this.store)
		}
		else{
			cfg.bbar = this.bbar
		}
		if(!this.showButtonOnPT){
			if(this.showButtonOnTop){
				cfg.tbar = (cndbars.concat(this.tbar || [])).concat(this.createButtons())
			}
			else{
				cfg.tbar = cndbars.concat(this.tbar || [])
				cfg.buttons = this.createButtons()
			}
		}
		if (this.disableBar) {
			delete cfg.tbar;
			delete cfg.bbar;
			cfg.autoHeight = true;
			cfg.frame = false;
		}
		this.grid = new this.gridCreator(cfg)
		// *huangpf
		if (this.showButtonOnTop) {
			tbar = this.grid.getTopToolbar();
			if (tbar) {
				tbar.enableOverflow = true
			}
		} else {
			bbar = this.grid.getBottomToolbar();
			if (bbar) {
				bbar.enableOverflow = true
			}
		}
		// *
		this.grid.on("render",this.onReady,this)
		this.grid.on("contextmenu",function(e){e.stopEvent()})
		this.grid.on("rowcontextmenu",this.onContextMenu,this)
		this.grid.on("rowdblclick",this.onDblClick,this)
		this.grid.on("rowclick",this.onRowClick,this)
		this.grid.on("keydown",function(e){
			if(e.getKey()== e.PAGEDOWN){
				e.stopEvent()
				this.pagingToolbar.nextPage()
				return
			}
			if(e.getKey() == e.PAGEUP){
				e.stopEvent()
				this.pagingToolbar.prevPage()
				return
			}
			if(e.getKey() == e.ENTER){
				this.fireEvent("createTPHQ");
			}
		},this)
		
		if(!this.isCombined){
			this.fireEvent("beforeAddToWin",this.grid)
			this.addPanelToWin();
		}
		return this.grid
	},
	getCndBar:function(items){
		var fields = [];
		if(!this.enableCnd){
			return []
		}
		for(var i = 0; i < items.length; i ++){
			var it = items[i]
			if(!(it.queryable)){
				continue
			}
			fields.push({
				value : it.id,
				text : it.alias
			})
		}
		if(fields.length == 0){
			return fields;
		}
		var store = new Ext.data.JsonStore({
	        fields: ['value', 'text'],
	        data : fields 
	    });
	    var combox = new Ext.form.ComboBox({
	        store: store,
    	    valueField:"value",
    		displayField:"text",
	        mode: 'local',
	        triggerAction: 'all',
	        emptyText:'选择查询字段',
	        selectOnFocus:true,
	        editable:false,
	        width:this.queryComboBoxWidth || 120
	    });
	    combox.on("select",this.onCndFieldSelect,this)
	    this.cndFldCombox = combox
	    var cndField = new Ext.form.TextField({width:this.cndFieldWidth || 200,selectOnFocus:true,name:"dftcndfld"})
       	this.cndField = cndField
		var queryBtn = new Ext.Toolbar.SplitButton({
			iconCls : "query",
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
		//---cxr add--
		return [combox,'-',cndField,'-',queryBtn,'-',
			{xtype: 'tbtext', text: '刷卡:',width:40,style:{textAlign:'center'}},
			{xtype:'textfield',name:'cardNo',width:100,listeners:{
				"specialkey":this.onCardNoKeyup,
				scope : this
				}},
			'-']
	},
	onCardNoKeyup : function(cardNofld,e){
		if (e.getKey() == e.ENTER) {
			var body = {
				cardNo : cardNofld.getValue(),
				courseId : this.exContext.args.courseId
			};
			var result = util.rmi.miniJsonRequestSync({
						serviceId : "chis.tumourHealthEducationCourseService",
						serviceAction : "swipingCardHandle",
						method : "execute",
						body : body
					});
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return null;
			}
			var body = result.json.body;
			if(body){
				var hasMany = body.hasMany;
				var havePerson = body.havePerson;
				this.selectedEmpiId = body.empiId;
				if(havePerson){
					if(hasMany){
						var pList = body.pList;
						this.showDataInSelectView(pList);
					}else{
						var existListener = body.existListener;
						if(existListener){
							var empiId = body.empiId;
							var count = this.store.getCount();
							var isExist = false;
							for (var ri = 0; ri < count; ri++) {
								var r = this.store.getAt(ri);
								if(r.get("empiId") == empiId){
									this.selectRow(ri);
									isExist = true;
									break;
								}
							}
							if(!isExist){
								this.requestData.cnd = ['and',['eq',['$','a.courseId'],['s',this.exContext.args.courseId||'']],['eq',['$','a.empiId'],['s',empiId||'']]];
								this.refresh();
							}
						}else{
							this.refresh();
						}
					}
				}else{
					MyMessageTip.msg("提示","无此卡人员信息，请核对卡号是否正确！",true);
				}
			}
		}
	},
	onLoadData : function(){
		this.requestData.cnd = ['eq',['$','a.courseId'],['s',this.exContext.args.courseId||'']];
	},
	onStoreLoadData : function(store,records,ops){
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if(records.length == 0){
			return
		}
		this.totalCount = store.getTotalCount()
		if (this.selectedEmpiId) {
			this.selectSpecifiedRow();
		} else {
			this.selectFirstRow();
		}
	},
	selectSpecifiedRow : function() {//选中指定记录行
		for (var i = 0; i < this.store.getCount(); i++) {
			var r = this.store.getAt(i);
			if (r.get("empiId") == this.selectedEmpiId) {
				this.grid.getSelectionModel().selectRecords([r]);
				this.grid.fireEvent("rowclick", this, i);
				var n = this.store.indexOf(r);
				if (n > -1) {
					this.selectedIndex = n;
				}
				return;
			}
		}
		this.selectedPlanId = null;
		this.selectedIndex = 0;
		this.selectFirstRow();
	},
	doCreateListener : function(){
		this.op = "create";
		var advancedSearchView = this.midiModules["TumourHEL_EMPIInfoModule_NEW"];
		if (!advancedSearchView) {
			$import("chis.application.mpi.script.EMPIInfoModule")
			advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
					{
						title : "个人基本信息查找",
						mainApp : this.mainApp,
						modal : true
					});
			advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,this);
			this.midiModules["TumourHEL_EMPIInfoModule_NEW"] = advancedSearchView;
		}
		var win = advancedSearchView.getWin();
		win.setPosition(250, 100);
		win.show();
		advancedSearchView.clear();
	},
	onEmpiSelected : function(data) {
		if(this.op == "update"){
			return;
		}
		this.empiId = data.empiId;
		this.selectedEmpiId = data.empiId;
		var count = this.store.getCount();
		var isExist = false;
		for (var ri = 0; ri < count; ri++) {
			var r = this.store.getAt(ri);
			var empiId = r.get("empiId");
			if(empiId == this.empiId){
				this.selectRow(ri);
				isExist = true;
				break;
			}
		}
		if(isExist){
			return;
		}
		var saveCfg = {
			serviceId:this.saveServiceId,
			method:this.saveMethod,
			serviceAction : this.saveAction || "",
			schema:this.entryName,
			module:this._mId,  //增加module的id
			body:{
				empiId:this.empiId,
				courseId:this.exContext.args.courseId
			}
		}
		util.rmi.jsonRequest(saveCfg,
			function(code,msg,json){
				this.saving = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.onEmpiSelected,[saveRequest],json.body);
					this.fireEvent("exception", code, msg, saveData); // **进行异常处理
					return
				}
				this.refresh();
			},
			this);
	},
	showDataInSelectView : function(data) {
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var r = data[i];
			if (this.queryBy == "baseInfo" && idCard.length > 0
					&& IdCardValidate) {
				if (r.idCard && r.idCard.length > 0) {
					continue;
				}
			}
			var record = new Ext.data.Record(r);
			records.push(record);
		}
		if (records.length == 0) {
			return;
		}
		var empiIdSelectView = this.midiModules["empiIdSelectView"];
		if (!empiIdSelectView) {
			$import("chis.application.mpi.script.CombinationSelect");
			var empiIdSelectView = new chis.application.mpi.script.CombinationSelect(
					{
						entryName : this.entryName,
						autoLoadData : false,
						enableCnd : false,
						modal : true,
						title : "选择个人记录",
						width : 500,
						height : 300
					});
			empiIdSelectView.on("onSelect", function(r) {
				this.onEmpiSelected(r.data);
			}, this);
		}
		empiIdSelectView.getWin().show();
		empiIdSelectView.setRecords(records);
	},
	doModify : function(){
		this.op = "update";
		var r = this.getSelectedRecord();
		if(!r){
			return;
		}
		var empiId = r.get("empiId");
		var advancedSearchView = this.midiModules["TumourHEL_EMPIInfoModule"];
		if (!advancedSearchView) {
			$import("chis.application.mpi.script.EMPIInfoModule")
			advancedSearchView = new chis.application.mpi.script.EMPIInfoModule(
					{
						serviceAction : "updatePerson",
						title : "个人基本信息查找",
						mainApp : this.mainApp,
						modal : true
					});
			advancedSearchView.on("onEmpiReturn", this.onEmpiSelected,
					this)
			this.midiModules["TumourHEL_EMPIInfoModule"] = advancedSearchView;
		}
		var win = advancedSearchView.getWin();
		win.setPosition(250, 100);
		win.show();
		advancedSearchView.setRecord(empiId);
	},
	onDblClick : function(grid){
//			this.fireEvent("createTPHQ");
		//this.doModify();
	},
	onRowClick:function(grid,index,e){
		this.selectedIndex = index
		this.fireEvent("rowclick",grid,index,e);
	},
	getRemoveRequest : function(r){
		var body = {};
		body.APID = r.get("APID");
		body.empiId = r.get("empiId");
		body.courseId = r.get("courseId");
		return body;
	}
	
});