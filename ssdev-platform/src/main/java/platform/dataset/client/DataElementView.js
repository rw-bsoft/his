$package("platform.dataset.client")
$styleSheet("platform.dataset.client.resources.dataset")
$import(
	"app.lang.UIModule",
	"util.dictionary.TreeDicFactory",
	"org.ext.ux.ProgressBarPager",
	"util.schema.SchemaLoader",
	"app.modules.form.TableFormView"
)

platform.dataset.client.DataElementView = function(cfg) {
	this.colCount = 2;
	this.colWidth = 600;
	this.westTopTree = this.westBottomTree = "platform.dataset.dic.resDataElementCata";
	this.entryName = "platform.dataset.schemas.RES_DataElement";
	this.needAttachmentNode = true;
	this.allowMutiSelected = false;
	this.enableSearchBar = true;
	this.needInitDataWhenCreate = true;
	this.viewDataId = "DataElementId";
	this.needFlashDicField = "Category";
	this.dataElementSaveService = "dataSetCatalogSave";
	this.dateElementLoadService = "platform.dataElementLoad";
	this.doubleClickHander = "edit";
	platform.dataset.client.DataElementView.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(platform.dataset.client.DataElementView, app.lang.UIModule, {
	init:function(){
		this.requestData = {
			serviceId:"simpleQuery",
			method:"execute",
			pageSize:this.pageSize || 25,
			pageNo:1,
			schema:this.entryName,
			cnd:this.getInitCnd()
		};
	},
	initPanel:function(){
		if(this.panel){
			return this.panel;
		}
		var westPanel = this.getWestPanel()
		westPanel.region = "west";
		var store = this.getStore();
		var cfg = {
			multiSelect:this.allowMutiSelected,
			autoScroll:true,
			singleSelect:true,
			tpl:this.getTpl(),
			store:store,
			cls:"data-element-item-own",
			itemSelector:"div.data-item",
			overClass:'data-item-over',
	        selectedClass:"data-item-selected",
	        loadingText:"正在加载数据..." 
		}
		var view = new Ext.DataView(cfg);
		view.on({
			dblclick:this.onDBClick,
			scope:this
		})
		this.view = view;
		var panel = new Ext.Panel({
			layout:"border",
			items:[
				westPanel,
				{
					region:"center",
					layout:"fit",
					items:view,
					tbar:this.createButtons(),
					bbar:this.getPagingToolBar()
				}
			]
		})
		this.panel = panel;
		return panel;
	},
	createButtons:function(){
		var actions = this.actions;
		var buttons = [];
		if(!actions){
			if(this.enableSearchBar){
				buttons= [this.createSearchField(),'->','-'].concat(buttons);
			}
			return buttons;
		}
		for(var i = 0; i < actions.length; i ++){
			var action = actions[i];
			if(action.hide||(action.id!="add"&&action.id!="update"&&action.id!="remove")){
				continue;
			}
			if(action.id=="update"){
				this.canUpdate=true
			}
			var btn = {
				text : action.name,
				ref:action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),
				script :  action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		if(this.enableSearchBar){
			buttons= [this.createSearchField(),'->','-'].concat(buttons);
		}
		return buttons;
	},
	addAttachmentNode:function(t,n){
		var root = this.S.getRootNode();
		if(n == root){
			var node = root.appendChild({
				text:"附件",
				leaf:true,
				iconCls:"new"
			})
			node.attributes.key = "attachments";
			this.attachmentsNode = node;
		}
	},
	getWestPanel:function(){
		var H = util.dictionary.TreeDicFactory.createTree({
			title:"文件头",
			id:this.westTopTree,
			keyNotUniquely:true
		});
		H.collapsed = true;
		H.getLoader().on("beforeload",function(loader){
			loader.dic.filter = this.getInitCnd(['eq',['$','item.properties.DeType'],['s','H']],'item.properties');
		},this)
		this.H = H;
		H.on({
			click:this.onNodeClick,
			containercontextmenu:this.containercontextmenu,
			contextmenu:this.containercontextmenu,
			scope:this
		})
		var S = util.dictionary.TreeDicFactory.createTree({
			title:"文件体",
			id:this.westBottomTree,
			keyNotUniquely:true
		});
		this.S = S;
		S.getLoader().on("beforeload",function(loader){
			loader.dic.filter = this.getInitCnd(['eq',['$','item.properties.DeType'],['s','S']],'item.properties');
		},this);
		if(this.needAttachmentNode){
			S.getLoader().on("load",this.addAttachmentNode,this);
		}
		S.on({
			click:this.onNodeClick,
			containercontextmenu:this.containercontextmenu,
			contextmenu:this.containercontextmenu,
			scope:this
		})
		var west = new Ext.Panel({
			layout:'accordion',
			split:true,
			defaults:{
				autoScroll: true
			},
			width:180,
			items:[H,S]
		});
		this.westPanel = west;
		return west
	},
	containercontextmenu:function(treeOrNode,e){
		e.stopEvent();
		this.selectedNode = treeOrNode instanceof Ext.tree.TreeNode?treeOrNode:null;
		var cmenu = this.midiMenus['westTreeContainerMenu']
		if(!cmenu){
			var actions = this.actions;
			var items = [];
			if(!actions){
				return;
			}
			for(var i = 0; i < actions.length; i ++){
				var action = actions[i];
				if(action.hide||(action.id!="addDeCata"&&action.id!="updateDeCata"&&action.id!="deleteDeCata")){
					continue;
				}
				var item = {
					cmd: action.id,
					text : action.name,
					iconCls : action.iconCls || action.id,
					handler : eval(action.properties.handler),
					scope : this
				}
				items.push(item)
			}
			cmenu = new Ext.menu.Menu({
				items:items
			});
			this.midiMenus['westTreeContainerMenu'] = cmenu
		}
		cmenu.items.each(function(item,index){
			if(item.cmd!="addDeCata")
				item.hide()
		},this)
		if(this.selectedNode){
			this.selectedNode.select();
			if(this.selectedNode.attributes.key != "attachments"){
				cmenu.items.each(function(item,index){
					if(item.cmd!="addDeCata")
						item.show()
				},this)
			}
		}
		cmenu.showAt([e.getPageX(),e.getPageY()]);
	},
	doAddDeCata:function(btn){
		var deTypeForm = this.deTypeForm;
		if(!deTypeForm){
			deTypeForm = new app.modules.form.TableFormView({
				colCount:1,
				width:400,
				title:"数据元分类维护",
				entryName:"platform.dataset.schemas.RES_DataElementCategory",
				saveServiceId:"dataSetCatalogSave",
				autoLoadSchema:false,
				autoLoadData : false,
				actions : [
					{id:"save",name:"确定"},
					{id:"cancel",name:"取消",iconCls:"common_cancel"}
				]
			});
			deTypeForm.initPanel();
			deTypeForm.on({
				winShow:function(){
					if(deTypeForm.initDataId == null){
						var dataBak = deTypeForm.data;
						deTypeForm.doNew();
						deTypeForm.data = dataBak;
					}else{
						deTypeForm.loadData();
					}
					deTypeForm.form.getForm().isValid();
				},
				save:function(a,b,c,d){
					if("create" == b){
						var p = this.westPanel.getLayout().activeItem;
						var root = p.getRootNode();
						var node = root.appendChild({
							key:d["DataElementCategoryId"],
							text:d["DName"],
							leaf:true
						});
					}
					if("update" == b){
						this.selectedNode.setText(d["DName"]);
					}
					deTypeForm.getWin().hide();
					this.reloadDicField(this.dataElementForm,this.needFlashDicField,false);
				},
				scope:this
			})
			this.deTypeForm = deTypeForm;
		}
		var p = this.westPanel.getLayout().activeItem;
		var data = {
			data:{
				DataStandardId:this.resDataStandard,
				DeType:{key:p.title=="文件头"?"H":"S",text:p.title}
			},
			initDataId:btn.iconCls == "update"?this.selectedNode.attributes.key:null
		};
		Ext.apply(deTypeForm,data);
		deTypeForm.getWin().show();
	},
	reloadDicField:function(form, fieldName, isTree){
		if(!form || !fieldName){
			return;
		}
		var fs=[];
		if(typeof fieldName == 'string'){
			fs.push(fieldName)
		}else{
			fs = fieldName;
		}
		for(var i=0;i<fs.length;i++){
			var fn = fs[i];
			var f = form.form.getForm().findField(fn);
			if(f){
				isTree?f.tree.getLoader().load(f.tree.getRootNode()):f.store.load();
			}
		}
	},
	doDeleteDeCata:function(){
		var n = this.selectedNode;
		Ext.Msg.show({
			title : '确认删除[' + n.attributes.text + ']',
			msg : '删除操作将无法恢复，是否继续?',
			modal : true,
			width : 300,
			buttons : Ext.MessageBox.OKCANCEL,
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					if(this.view.store.getCount()>0){
						Ext.MessageBox.alert("提示","该分类下还存在有数据元，删除失败.");
						return;
					}
					util.rmi.jsonRequest({
						serviceId : "dataStandardRemove",
						method:"execute",
						schema : "platform.dataset.schemas.RES_DataElementCategory",
						pkey : n.attributes.key
					}, function(code, msg, json) {
						if (code == 200) {
							this.requestData.cnd = this.getInitCnd();
							if(n == this.selectedNode){
								this.selectedNode = null;
							}
							n.remove();
							this.clear();
							this.reloadDicField(this.dataElementForm,this.needFlashDicField,false);
						}
						if(code == 502){
							Ext.MessageBox.alert("提示","该分类下还存在有数据元，删除失败.");
							return;
						}
					}, this)
				}
			},
			scope : this
		})
	},
	onNodeClick:function(node,e){
		this.selectedNode = node;
		if(node.attributes.key=="attachments"){
			this.requestData.cnd = this.getInitCnd(['eq',['$','DataType'],['s','$a']]);
			this.resetFirstPage();
			this.refresh();
		}else{
			this.requestData.cnd = this.getInitCnd(['eq',['$',"Category"],['i',node.attributes.key]]);
			this.resetFirstPage();
			this.refresh();
		}
	},
	onDBClick:function(view,index,htmlNode,e){
		var r = view.getRecord(htmlNode);
		if(this.doubleClickHander == "edit"){
			if(r.data["DataType"] == '$a'){
				return;
			}
			var dataElementForm = this.getDataElementForm();
			dataElementForm.initDataId = r.data[this.viewDataId];
			dataElementForm.getWin().show();
		}
		if(this.doubleClickHander == "choice"){
			this.doSelected()
		}
	},
	getDataElementForm:function(){
		var dataElementForm = this.dataElementForm;
		if(!dataElementForm){
			dataElementForm = new app.modules.form.TableFormView({
				colCount:this.colCount||1,
				width:this.colWidth||400,
				title:"数据元维护",
				entryName:this.entryName,
				autoLoadData : false,
				autoLoadSchema:false,	//如果true的话一些构造函数里的重写父类的变量可能未能在initPanel里体现
				saveServiceId:this.dataElementSaveService,
				loadServiceId:this.dateElementLoadService,
				actions : [
					{id:"save",name:"确定"},
					{id:"cancel",name:"取消",iconCls:"common_cancel"}
				]
			});
			dataElementForm.initPanel();
	//添加字典显示		
			var propsGrid = new Ext.grid.PropertyGrid({
      				autoScroll : true,
      				height: 130
				});
			propsGrid.on("beforeedit",function(e){
				e.cancel=true;
				return false;
			})
			propsGrid.getColumnModel().setColumnHeader(0,"字典(键)")
			propsGrid.getColumnModel().setColumnHeader(1,"字典(值)")
			dataElementForm.propsGrid=propsGrid
			dataElementForm.getWin().add(propsGrid)
   				
			dataElementForm.on({
				winShow:function(){
					dataElementForm.form.buttons[0].show()
					if(dataElementForm.initDataId == null){
						var dataBak = dataElementForm.data;
						dataElementForm.doNew();
						dataElementForm.data.DataStandardId=dataBak.DataStandardId
				//		dataElementForm.data = dataBak;
						var CategoryField = dataElementForm.form.getForm().findField("Category");
						if(CategoryField && this.selectedNode){
							CategoryField.setValue({key:this.selectedNode.attributes.key,text:this.selectedNode.attributes.text})
						}
					}else{
						if(!this.canUpdate){
							dataElementForm.form.buttons[0].hide()
						}
						dataElementForm.loadData();
					}
					dataElementForm.form.getForm().isValid();
				},
				save:function(){
					this.refresh();
					dataElementForm.getWin().hide();
				},
				loadData:function(entryName,body){
					if(this.entryName=="platform.dataset.schemas.RES_DataSetContent"){
						if(body.bCodeSystem==null||body.bCodeSystem==""){
							dataElementForm.form.getForm().findField("CodeSystem").setDisabled(true)
							dataElementForm.propsGrid.hide()
						}else{
							dataElementForm.form.getForm().findField("CodeSystem").setDisabled(false)
							dataElementForm.propsGrid.show()
						}
						dataElementForm.startValues = dataElementForm.form.getForm().getValues(true);
					}else{
						if(body.CodeSystem==null||body.CodeSystem==""){
							dataElementForm.propsGrid.hide()
						}else{
							dataElementForm.propsGrid.show()
						}
					}
					if(body.dicItems){
						dataElementForm.propsGrid.setSource(body.dicItems)
					}else{
						dataElementForm.propsGrid.setSource({})
					}
				},
				scope:this
			})
			if(this.needFlashDicField){
				var nfdf = [];
				if(typeof this.needFlashDicField == 'string'){
					nfdf.push(this.needFlashDicField);
				}else{
					nfdf = this.needFlashDicField;
				}
				for(var i=0;i<nfdf.length;i++){
					var sf = nfdf[i];
					var f = dataElementForm.form.getForm().findField(sf);
					if(f){
						f.getStore().on("beforeload",function(store){
							store.setBaseParam("filter",Ext.encode(this.getInitCnd(null,'item.properties')));
						},this);
					}
				}
			}
			this.dataElementForm = dataElementForm;
		}
		return dataElementForm;
	},
	doAdd:function(){
		if(this.selectedNode && this.selectedNode.attributes.key=="attachments"){
			return;
		}
		var dataElementForm = this.getDataElementForm();
		if(this.needInitDataWhenCreate){
			var data = {
				DataStandardId:this.resDataStandard
			};
			if(this.selectedNode){
				data.Category = this.selectedNode.attributes.key;
			}
			Ext.apply(dataElementForm,{data:data});
		}
		dataElementForm.initDataId = null;
		dataElementForm.propsGrid.setSource({})
		dataElementForm.getWin().show();
	},
	doUpdate:function(){
		var rs = this.view.getSelectedRecords();
		if(rs.length != 1){
			return;
		}
		var r = rs[0];
		if(r.data["DataType"] == '$a'){
			return;
		}
		if(r.data["DataGroupId"]){
			return;
		}
		var dataElementForm = this.getDataElementForm();
		dataElementForm.initDataId = r.data[this.viewDataId];
		dataElementForm.getWin().show();
	},
	doRemove:function(){
		var rs = this.view.getSelectedRecords();
		if(rs.length != 1){
			return;
		}
		var r = rs[0];
		if(r.data["DataType"] == '$a'){
			return;
		}
		if(r.data["DataGroupId"]){
			return;
		}
		Ext.Msg.show({
			title : '确认删除[' + r.data["DName"] + ']',
			msg : '删除操作将无法恢复，是否继续?',
			modal : true,
			width : 300,
			buttons : Ext.MessageBox.OKCANCEL,
			multiline : false,
			fn : function(btn, text) {
				if (btn == "ok") {
					this.processRemove(r);
				}
			},
			scope : this
		})
	},
	processRemove:function(r){
		util.rmi.jsonRequest({
			serviceId : "dataStandardRemove",
			method:"execute",
			schema : this.entryName,
			pkey : r.data[this.viewDataId]
		}, function(code, msg, json) {
			if (code == 200) {
				this.store.remove(r);
			}else{
				this.processReturnMsg(code,msg,this.doRemove)
			}
		}, this)
	},
	getWin:function(){
		var win = this.win;
		if(!win){
			win = new Ext.Window({
				title:"添加数据元",
				width:550*1.618,
				height:550,
				layout:"fit",
				items:this.initPanel(),
				modal:true,
				closeAction:"hide",
				buttonAlign:"center",
				buttons:[
					{text:"确定",iconCls:"save",handler:this.doSelected,scope:this},
					{text:"取消",iconCls:"common_cancel",handler:function(){
						this.win.hide();
					},scope:this}]
			});
			win.on("show",function(w){
				this.fireEvent("winShow",w,this)
			},this)
			this.win = win;
		}
		return win;
	},
	doSelected:function(){
		var rs = this.view.getSelectedRecords(),r;
		if(rs.length > 0){
			r = rs[0];
		}
		if(r && this.joinSetId){
			var form = this.parentPanel.getDataElementForm();
			form.initDataId = null;
			form.getWin().show();
			util.rmi.jsonRequest({
				serviceId:this.dateElementLoadService,
				method:"execute",
				schema:"platform.dataset.schemas.RES_DataElement",
				pkey:r.data["DataElementId"]
			},function(code,msg,json){
				if(code == 200){
					var body = json.body;
					body["Alias"] = body["DName"];
					if(!this.parentPanel.requestData.withOutInGroup){
						body["DataGroup"] = {key:this.parentPanel.requestData.gId,text:this.parentPanel.requestData.gName};
					}
					form.initFormData(body);
					form.data.DataSetId = this.joinSetId;
					if(body.CodeSystem==null||body.CodeSystem==""){
						form.form.getForm().findField("CodeSystem").setDisabled(true)
						form.propsGrid.hide()
					}else{
						form.form.getForm().findField("bCodeSystem").setValue(body.CodeSystem)
						form.form.getForm().findField("CodeSystem").setDisabled(false)
						form.propsGrid.show()
					}
					form.propsGrid.setSource(body.dicItems)
					form.startValues = form.form.getForm().getValues(true);
				}
			},this)		
		}
	},
	selectedSuccess:function(){
		var p = this.westPanel.getLayout().activeItem;
		p.getLoader().load(p.getRootNode());
		this.refresh();
	},
	onWinShow:function(w,v){
		if(!this.loadOnShow){
			this.loadOnShow = true;
		}else{
			var p = v.westPanel.getLayout().activeItem;
			p.getLoader().load(p.getRootNode());
		}
	},
	reset:function(){
		this.selectedNode = null;
		var loaderH = this.H.getLoader();
		var loaderS = this.S.getLoader();
		loaderH.load(this.H.getRootNode());
		loaderS.load(this.S.getRootNode());
		this.requestData.cnd = this.getInitCnd();
		this.H.getSelectionModel().clearSelections();
		this.S.getSelectionModel().clearSelections();
		this.clear();
		this.reloadDicField(this.dataElementForm,this.needFlashDicField,false);
	},
	clear:function(){
		if(this.store){
//			this.store.removeAll();
			this.store.loadData({
				totalCount:0,
				body:[]
			});
		}
	},
	resetFirstPage:function(){
		var pt = this.panel.items.itemAt(1).getBottomToolbar();
		if(pt){
			pt.cursor = 0;
		}else{
			this.requestData.pageNo = 1;
		}
	},
	refresh:function(){
		if(this.store){
			var pt = this.panel.items.itemAt(1).getBottomToolbar();
			if(pt){
				pt.doLoad(pt.cursor);
			}else
				this.store.reload();
		}
	},
	getTpl:function(){
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="data-item">',
					'<div class="data-element-item">',
//						'<div class="data-item-rownum">{#}</div>',
						'<div class="data-element-item-icon"></div>',
						'<div class="data-item-body">',
							'<div>',
								'<span style="width:340px"><b>{DName}</b></span>',
								'<span style="width:100px">{StandardIdentify}</span>',
								'<span style="width:80px">{Frequency}</span>',
								'<tpl if="DataElementId">',
									'<tpl if="DataLength">',
										'<span style="width:100px">{DataType}({DataLength})</span>',
									'</tpl>',
									'<tpl if="!DataLength">',
										'<span style="width:100px">{DataType}</span>',
									'</tpl>',
								'</tpl>',
							'</div>',
							'<div>',
								'<span style="width:440px">{CustomIdentify}</span>',
								'<tpl if="DataElementId">',
									'<span style="width:180px">{CodeSystem}</span>',
								'</tpl>',
							'</div>',
							'<div>',
								'<span style="width:620px">{DComment}</span>',
							'</div>',
						'</div>',
					'</div>',
				'</div>',
			'</tpl>'
		);
		this.tpl = tpl;
		return tpl;
	},
	createSearchField:function(){
		var reader = new Ext.data.JsonReader({
            root: 'body',
            totalProperty: 'totalCount',
            fields:['DataElementId','CustomIdentify','StandardIdentify','DName','DComment']
        })
		var url = ClassLoader.serverAppUrl || "";
		var proxy = new Ext.data.HttpProxy({
			url : url + '*.jsonRequest',
			method : 'post',
			jsonData : this.requestData
		})
		var store = new Ext.data.Store({
			proxy: proxy,
			reader:reader
		})
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="data-item">',
					'<table>',
						'<tr>',
							'<td width="240"><b>{DName}</b></td>',
							'<td>{StandardIdentify}</td>',
						'</tr>',
						'<tr>',
							'<td colspan="2">',
								'<tpl if="CustomIdentify">',
									'{CustomIdentify}',
								'</tpl>',
							'</td>',
						'</tr>',
					'</table>',
				'</div>',
			'</tpl>'
		)
		var search = new util.widgets.MyCombox({
	        store: store,
	        emptyText:"搜索数据元..",
	        displayField:'DName',
	        typeAhead: false,
	        loadingText: 'Searching...',
	        width: 420,
	        minChars:2,
	        requestData:this.requestData,
//	        pageSize:10,
	        hideTrigger:true,
	        tpl: tpl,
	        itemSelector: 'div.data-item'
	    })
		search.on("select",this.onSearchSelect,this)
	    store.on("beforeload", function() {
			var cnd = this.getSearchCnd(search);
			cnd = this.getInitCnd(cnd);
			this.requestData.cnd = cnd;
			this.requestData.withOutInGroup = false;
		}, this)
		this.store.on("load",function(){
			if(this.searchMetaIndexFlag){
				this.view.select(this.searchMetaIndex);
				this.view.el.dom.scrollTop = 54*this.searchMetaIndex;
			}
			this.searchMetaIndexFlag = false;
		},this);
	    return search
	},
	getSearchCnd:function(search){
		return ['or',
				['like',['$', 'a.CustomIdentify'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.DName'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.StandardIdentify'],['s','%' + search.getValue() + '%']]
			];
	},
	onSearchSelect:function(cb,r,i){
		this.searchMetaIndexFlag = true;
		this.searchMetaIndex = i;
		this.resetFirstPage();
		this.refresh();
	},
	getPagingToolBar:function(){
		var ptb = new util.widgets.MyPagingToolbar({
			store: this.store || this.getStore(),
			requestData:this.requestData,
			displayInfo: true,
			emptyMsg:"无相关记录",
			plugins: new Ext.ux.ProgressBarPager()
		});
		return ptb;
	},
	getSchemaItems:function(){
		var items = util.schema.loadSync(this.entryName).schema.items;
		return items;
	},
	getStore:function(){
		var items = this.getSchemaItems();
		for(var i=0;i<items.length;i++){
			items[i].name = items[i].id
		}
		var reader = new Ext.data.JsonReader({
            root: 'body',
            totalProperty: 'totalCount',
            fields:items
        })
		var url = ClassLoader.serverAppUrl || "";
		var proxy = new Ext.data.HttpProxy({
			url : url + '*.jsonRequest',
			method : 'post',
			jsonData : this.requestData
		})
		var store = new Ext.data.Store({
			proxy: proxy,
			reader:reader
		})
		this.store = store;
		return store;
	},
	getInitCnd:function(cnd,prefix){
		var initCnd = ['eq',['$',prefix?prefix+'.DataStandardId':'DataStandardId'],['s',this.resDataStandard]];
		return cnd?['and',initCnd,cnd]:initCnd;
	}
});
