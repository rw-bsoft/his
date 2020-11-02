$package("platform.dataset.client")
$styleSheet("platform.dataset.client.resources.dataset")
$import(
	"platform.dataset.client.DataElementView"
)

platform.dataset.client.DataSetView = function(cfg) {
	this.westTree = "platform.dataset.dic.resDataSet";
	this.colCount = 2;
	this.colWidth = 600;
	cfg.entryName = "platform.dataset.schemas.RES_DataSetContent";
	cfg.needInitDataWhenCreate = false;
	cfg.viewDataId = "DataSetContentId";
	cfg.needFlashDicField = ["DataGroup","ParentGroupId"];
	cfg.dataElementSaveService = "dataSetCatalogSave";
	cfg.dateElementLoadService = "platform.dataSetElementLoad";
	cfg.showPageToolbar = false;
	this.pageSize = 1000;
	platform.dataset.client.DataSetView.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(platform.dataset.client.DataSetView, platform.dataset.client.DataElementView, {
	init:function(){
		this.requestData = {
			serviceId:"platform.elementAndGroupQuery",
			method:"execute",
			pageSize:this.pageSize || 25,
			pageNo:1,
			schema:this.entryName,
			cnd:this.getInitCnd(['eq',['i',1],['i',2]]),
			withOutInGroup:true
		};
	},
	initPanel:function(){
		if(this.panel){
			return this.panel;
		}
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
			contextmenu :this.rightClick,
			dblclick:this.onDBClick,
			render:this.doDrag,
			scope:this
		})
		this.view = view;
		var panel = new Ext.Panel({
			layout:"border",
			items:[{
		    	layout:"fit",
		    	split:true,
		     	region:'west',
		    	width:200,
		     	items:this.getWestPanel(),
		     	tbar:[this.setSearchField()]
		 	   },
				{
					region:"center",
					layout:"fit",
					items:view,
					tbar:this.createButtons(),
					bbar:this.getPagingToolBar()
				}
			]
		})
		panel.on({
            	beforehide:this.checkSequence,
            	beforedestroy:this.checkSequence,
            	scope:this
         })
		this.panel = panel;
		return panel;
	},
	createButtons:function(){
		var actions = this.actions;
		var buttons = [];
		if(!actions){
			return buttons;
		}
		for(var i = 0; i < actions.length; i ++){
			var action = actions[i];
			if(action.hide||(action.id!="addGroup"&&action.id!="add"&&action.id!="sequence"&&action.id!="update"&&action.id!="remove")){
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
	getWestPanel:function(){
		var wp = util.dictionary.TreeDicFactory.createTree({
			keyNotUniquely:true,
			id:this.westTree
		});
		wp.autoScroll = true;
		wp.getLoader().on("beforeload",function(loader){
			loader.dic.filter = this.getInitCnd(null, "item.properties");
		},this)
		wp.width = 180;
		wp.split = true,
		wp.on({
			click:this.onNodeClick,
			containercontextmenu:this.containercontextmenu,
			contextmenu:this.onContextMenu,
			scope:this
		})
		this.wp = wp;
		return wp
	},
	doDrag:function(v) {
	    v.dragZone = new Ext.dd.DragZone(v.getEl(), {
	        getDragData: function(e) {
	          	var sourceEl = e.getTarget(v.itemSelector, 10);
	          	idx = v.indexOf(sourceEl);
	          	record = v.getStore().getAt(idx);
	          	if (sourceEl) {
                return {
                    sourceEl: sourceEl,
             //     repairXY: Ext.fly(sourceEl).getXY(),
                    ddel: sourceEl,
                    lastIdx : idx, 
                    record  : record   
                }
            }
	        },
	        getRepairXY: function() {
	        	return false
	         //   return this.dragData.repairXY;
	        },
	        endDrag :function(e){
	        	v.dragFlag=true
	        	var targetEl,idx,record,data = this.dragData;    
				targetEl = e.getTarget(v.itemSelector);     
				idx = v.indexOf(targetEl);          
				record = v.getStore().getAt(idx);       
				if (idx === data.lastIdx) { 
					return; 
				}     
				//将数据插入到新的位置       
				if (targetEl && record) {              
					data.lastIdx = idx;             
					v.getStore().remove(data.record);             
					v.getStore().insert(idx, [data.record]);           
		//			Ext.fly(v.getNode(idx)).addClass('thumb');          
					return;          
				}  		
	        }
	    }).proxy.el.child(".x-dd-drop-icon").setDisplayed(false);
	},
	
	doSequence:function(){
		Ext.MessageBox.confirm("提示","是否要保存当前改动顺序？",function(okay){
			if("yes"==okay){
				var num=this.store.getCount();
				var sData={}
				var sequence=1
				if(num>0){
					for(var i=0;i<num;i++){
						var record=this.store.getAt(i);
						var initDataId = record.data[this.viewDataId];
						if(initDataId){
							sData[sequence]=record.data["DataSetContentId"];
							sequence++
						}
					}
				}
				var r = util.rmi.miniJsonRequestSync({
					serviceId:"dataSetCatalogSave",
					method:"setSequence",
					body:{
						data:sData
					}
				})
				if(r.code==200){
					this.refresh()
				}
			}
		},this)
	
	},
	checkSequence:function(){
		if(this.view.dragFlag){
			this.doSequence()
			this.view.dragFlag=false
		}
	},
	onNodeClick:function(node,e){
		if(!node.isLeaf()){
			return;
		}
		this.selectedNode = node;
		this.requestData.cnd = this.getInitCnd(['eq',['$','a.DataSetId'],['i',node.attributes.key]]);
		this.requestData.withOutInGroup = true;
		this.requestData.setId = node.attributes.key;
		this.resetFirstPage();
		this.refresh();
	},
	rightClick:function(dataView,index,node,e){
		e.stopEvent();
		this.view.select(index);
		var r=dataView.getRecord(node)
		this.moveRecord=r
		var cmenu = this.midiMenus['viewMenus']
		if(!cmenu){
			cmenu = new Ext.menu.Menu({
				items:[
					{name:"moveUp",text:"上移",iconCls:"arrow-up",scope:this,handler:this.doMove},
					{name:"moveDown",text:"下移",iconCls:"arrow-down",scope:this,handler:this.doMove},
					{name:"moveToTop",text:"上移至顶部",iconCls:"green_up",scope:this,handler:this.doMove},
					{name:"moveToButtom",text:"下移至底部",iconCls:"green_down",scope:this,handler:this.doMove},
					{name:"updateGroup",text:"修改",iconCls:"update",scope:this,handler:this.updateGroup}
				]
			});
			this.midiMenus['viewMenus'] = cmenu
		}
		if(!r.get("DataSetContentId")){
			cmenu.items.itemAt(0).hide();
			cmenu.items.itemAt(1).hide();
			cmenu.items.itemAt(2).hide();
			cmenu.items.itemAt(3).hide();
			if(this.canUpdate){
				cmenu.items.itemAt(4).show();
			}else{
				cmenu.items.itemAt(4).hide();
			}
		}else{
			cmenu.items.itemAt(0).show();
			cmenu.items.itemAt(1).show();
			cmenu.items.itemAt(2).show();
			cmenu.items.itemAt(3).show();
			cmenu.items.itemAt(4).hide();
		}
		cmenu.showAt([e.getPageX(),e.getPageY()]);
	},
	updateGroup:function(){
		var r = this.moveRecord;
		var groupPanel = this.getGroupPanel();
		groupPanel.initDataId = r.data["DataGroupId"];
		groupPanel.getWin().show();
	},
	doMove:function(btn){
		this.view.dragFlag=true
		switch(btn.name){
			case "moveUp":
				var num=this.store.getCount()
				var groupNum=0
				for(var i=0;i<num;i++){
					var record=this.store.getAt(i);
					var initDataId = record.data[this.viewDataId];
					if(!initDataId){
						groupNum++
					}
				}
				var index=this.store.indexOf(this.moveRecord)-1
				if(index>=groupNum){
					this.store.remove(this.moveRecord);             
					this.store.insert(index, this.moveRecord);  
					this.view.select(index);
				}
				break;
			case "moveDown":
				var index=this.store.indexOf(this.moveRecord)+1
				if(index<this.store.getCount()){
					this.store.remove(this.moveRecord);             
					this.store.insert(index, this.moveRecord); 
					this.view.select(index);
				}
				break;
			case "moveToTop":
				var num=this.store.getCount()
				var groupNum=0
				for(var i=0;i<num;i++){
					var record=this.store.getAt(i);
					var initDataId = record.data[this.viewDataId];
					if(!initDataId){
						groupNum++
					}
				}
				this.store.remove(this.moveRecord);             
				this.store.insert(groupNum, this.moveRecord);
				this.view.select(groupNum);
				this.view.el.dom.scrollTop = 54*groupNum;
				break;
			case "moveToButtom":
				var lastIndex=this.store.getCount()-1
				this.store.remove(this.moveRecord);             
				this.store.insert(lastIndex, this.moveRecord); 
				this.view.select(lastIndex);
				this.view.el.dom.scrollTop = 54*lastIndex;
				break;
		}
	},
	onDBClick:function(view,index,htmlNode,e){
		var r = view.getRecord(htmlNode);
		var initDataId = r.data[this.viewDataId];
		if(initDataId){
			var dataElementForm = this.getDataElementForm();
			dataElementForm.initDataId = initDataId;
			dataElementForm.getWin().show();
			return;
		}
		var cnd = this.getInitCnd(['eq',['$','a.DataSetId'],['s',r.data["DataSetId"]]]);
		this.requestData.cnd = ['and',cnd[1],cnd[2],['eq',['$','a.DataGroup'],['i',r.data["DataGroupId"]]]];
		this.requestData.withOutInGroup = false;
		this.requestData.setId = r.data["DataSetId"];
		this.requestData.gId = r.data["DataGroupId"];
		this.requestData.gName = r.data["DName"];
		this.resetFirstPage();
		this.refresh();
	},
	containercontextmenu:function(tree,e){
		e.stopEvent();
		this.selectedNode = this.wp.getRootNode();
		var cmenu = this.midiMenus['westTreeContainerMenu']
		if(!cmenu){
			var actions = this.actions;
			var items = [];
			if(!actions){
				return;
			}
			for(var i = 0; i < actions.length; i ++){
				var action = actions[i];
				if(action.hide||action.id!="addSetOrCatalog"){
					continue;
				}
				var item={
					cmd: action.id,
					text : action.name,
					iconCls : action.iconCls || action.id,
					menu:{items:[
						{text:"目录",iconCls:"add",scope:this,handler:this.doAddCatalog}
					]},
					hideOnClick:false
				}
				items.push(item)
			}
			cmenu = new Ext.menu.Menu({
				items:items
			});
			this.midiMenus['westTreeContainerMenu'] = cmenu
		}
		cmenu.showAt([e.getPageX(),e.getPageY()]);
	},
	onContextMenu:function(node,e){
		e.stopEvent();
		node.select();
		this.selectedNode = node;
		var cmenu = this.midiMenus['westTreeMenu']
		if(!cmenu){
			var actions = this.actions;
			var items = [];
			if(!actions){
				return;
			}
			for(var i = 0; i < actions.length; i ++){
				var action = actions[i];
				if(action.hide||(action.id!="addSetOrCatalog"&&action.id!="updateSetOrCatalog"&&action.id!="deleteSetOrCatalog")){
					continue;
				}
				if(action.id=="addSetOrCatalog"){
					var item={
						cmd: action.id,
						text : action.name,
						iconCls : action.iconCls || action.id,
						menu:{items:[
							{text:"目录",iconCls:"add",scope:this,handler:this.doAddCatalog},
							{text:"子目录",iconCls:"add",scope:this,handler:this.doAddChildCatalog},
							{text:"数据集",iconCls:"add",scope:this,handler:this.doAddSet}
						]},
						hideOnClick:false
					}
				}else{
					var item = {
						cmd: action.id,
						text : action.name,
						iconCls : action.iconCls || action.id,
						scope : this
					}
					if(action.properties){
						item.handler=eval(action.properties.handler)
					}
				}
				items.push(item)
			}
			cmenu = new Ext.menu.Menu({
				items:items
			});
			this.midiMenus['westTreeMenu'] = cmenu
		}
		cmenu.items.each(function(item,index){
			if(item.cmd=="addSetOrCatalog")
				item.show()
		},this)
		if(node.isLeaf()){
			cmenu.items.each(function(item,index){
				if(item.cmd=="addSetOrCatalog")
					item.hide()
			},this)
		}
		cmenu.showAt([e.getPageX(),e.getPageY()]);
	},
	
	doAddCatalog:function(){
		if(this.selectedNode.parentNode){
			this.selectedNode = this.selectedNode.parentNode;
		}
		this.doAddChildCatalog();
	},
	doAddChildCatalog:function(){
		var form = this.getCatalogFrom();
		Ext.apply(form,{data:{DataStandardId:this.resDataStandard}});
		form.initDataId = null;
		form.getWin().show();
	},
	doAddSet:function(){
		var form = this.getDataSetForm();
		Ext.apply(form,{data:{DataStandardId:this.resDataStandard}});
		form.initDataId = null;
		form.getWin().show();
	},
	doUpdateCatalogOrSet:function(){
		var node = this.selectedNode;
		if(!node.isLeaf()){
			var form = this.getCatalogFrom();
			form.initDataId = node.attributes.key;
			form.getWin().show();
		}else{
			var form = this.getDataSetForm();
			form.initDataId = node.attributes.key;
			form.getWin().show();
		}
	},
	doRemoveCatalogOrSet:function(){
		var node = this.selectedNode;
		Ext.MessageBox.confirm("提示","是否删除["+node.text+"]",function(okay){
			if("yes"==okay){
				if(node.isLeaf()){
					util.rmi.jsonRequest({
						serviceId:"dataStandardRemove",
						method:"execute",
						schema:"platform.dataset.schemas.RES_DataSet",
						pkey:node.attributes.key
					},function(code,msg,json){
						if(code == 200){
							this.requestData.cnd = this.getInitCnd(['eq',['i',1],['i',2]]);
							node.remove();
							this.clear();
						}
					},this)
				}else{
					if(node.hasChildNodes()){
						Ext.Msg.alert("提示","目录下还存在数据集，无法删除.");
					}else{
						util.rmi.jsonRequest({
							serviceId:"dataStandardRemove",
							method:"execute",
							schema:"platform.dataset.schemas.RES_DataSetCatalog",
							pkey:node.attributes.key
						},function(code,msg,json){
							if(code == 200){
								this.requestData.cnd = this.getInitCnd(['eq',['i',1],['i',2]]);
								node.remove();
								this.clear();
								this.reloadDicField(this.catalogFrom,"ParentCatalogId",true);
								this.reloadDicField(this.dataSetForm,"Catalog",true);
							}
						},this)
					}
				}
			}
		},this)
	},
	getCatalogFrom:function(){
		var form = this.catalogFrom;
		if(!form){
			form = new app.modules.form.TableFormView({
				title:"数据集目录维护",
				entryName:"platform.dataset.schemas.RES_DataSetCatalog",
				saveServiceId:"dataSetCatalogSave",
				autoLoadSchema:false,
				autoLoadData : false,
				actions:[
					{id:"save",name:"确定"},
					{id:"cancel",name:"取消",iconCls:"common_cancel"}
				]
			})
			form.on("addfield",function(f,it){
				if(it.id == "ParentCatalogId"){
					f.tree.getLoader().on("beforeload",function(loader){
						loader.dic.filter = this.getInitCnd(null, "item.properties");
					},this)
				}
			},this)
			form.initPanel();
			form.on("winShow",function(){
				if(form.initDataId == null){
					var dataBak = form.data;
					form.doNew();
					form.data = dataBak;
					if(this.selectedNode.text.length != 0){
						var ParentCatalogId = form.form.getForm().findField("ParentCatalogId");
						ParentCatalogId.setValue({key:this.selectedNode.attributes.key,text:this.selectedNode.text});
					}
				}else{
					form.loadData();
				}
				form.form.getForm().isValid();
			},this)
			form.on("save",function(a,b,c,d){
				if(b == "update"){
					this.selectedNode.setText(d["CatalogName"]);
				}
				if(b == "create"){
					var n = this.selectedNode.appendChild({
						text:d["CatalogName"],
						leaf:false
					});
					n.attributes.key = c.body["CatalogId"];
					n.expand();
				}				
				this.catalogFrom.getWin().hide();
				this.reloadDicField(this.catalogFrom,"ParentCatalogId",true);
				this.reloadDicField(this.dataSetForm,"Catalog",true);
			},this)
			this.catalogFrom = form;
		}
		Ext.apply(form,{
			selectedNode:this.selectedNode
		})
		return form;
	},
	getDataSetForm:function(){
		var form = this.dataSetForm;
		if(!form){
			form = new app.modules.form.TableFormView({
				title:"数据集维护",
				entryName:"platform.dataset.schemas.RES_DataSet",
				saveServiceId:"dataSetCatalogSave",
				width:700,
				autoLoadSchema:false,
				autoLoadData : false,
				actions:[
					{id:"save",name:"确定"},
					{id:"cancel",name:"取消",iconCls:"common_cancel"}
				]
			})
			form.on("addfield",function(f,it){
				if(it.id == "Catalog"){
					f.tree.getLoader().on("beforeload",function(loader){
						loader.dic.filter = this.getInitCnd(null, "item.properties");
					},this)
				}
			},this)			
			form.initPanel();
			form.on("winShow",function(){
				if(form.initDataId == null){
					var dataBak = form.data;
					form.doNew();
					form.data = dataBak;
					var Catalog = form.form.getForm().findField("Catalog");
					Catalog.setValue({key:this.selectedNode.attributes.key,text:this.selectedNode.text});
				}else{
					form.loadData();
				}
				form.form.getForm().isValid();
			},this)
			form.on("beforeSave",function(a,b,c){
				if(!(c.DataSetMapping)){
					return true;
				}
				var r=this.validateXML(c.DataSetMapping)
				if(r.error_code==0){
					return true;
				}
				c.DataSetMapping=null;
				Ext.Msg.alert("提示","映射内容语法有误")
				return false
			},this)
			form.on("save",function(a,b,c,d){
				if(b == "update"){
					var node = this.selectedNode;
					node.setText(d["DName"]);
					if(node.parentNode.attributes.key != d["Catalog"]){
						var rNode = this.wp.getRootNode();
						var pNode = rNode.findChild("key",d["Catalog"], true);
						if(pNode){
							if(pNode.isExpanded()){
								var nNode = pNode.appendChild({text:node.text,leaf:true});
								nNode.attributes.key = node.attributes.key;
								nNode.attributes.text = node.attributes.text;
								nNode.select();
							}else{
								pNode.expand(false,true,function(){
									if(!pNode.findChild("key",node.attributes.key)){
										var nNode = pNode.appendChild({text:node.text,leaf:true});
										nNode.attributes.key = node.attributes.key;
										nNode.attributes.text = node.attributes.text;
										nNode.select();
									}
								},this)
							}
						}
						node.remove();
					}
				}
				if(b == "create"){
					var node = this.selectedNode.appendChild({
						text:d["DName"],
						leaf:true
					});
					node.attributes.key = d["DataSetId"]
				}				
				this.dataSetForm.getWin().hide();
			},this)
			this.dataSetForm = form;
		}
		return form;
	},
	doAdd:function(){
		var n = this.wp.getSelectionModel().getSelectedNode();
		if(!n || !n.isLeaf()){
			Ext.MessageBox.alert("提示","请先在左边树状菜单中选择数据集.")
			return;
		}
		var panel = this.getJoinPanel();
		panel.joinSetId = n.attributes.key;
		if(panel.resDataStandard != this.resDataStandard){
			panel.resDataStandard = this.resDataStandard;
			panel.reset();
		}
		panel.getWin().show();		
	},
	doAddGroup:function(){
		var n = this.wp.getSelectionModel().getSelectedNode();
		if(!n || !n.isLeaf()){
			Ext.MessageBox.alert("提示","请先在左边树状菜单中选择数据集.")
			return;
		}
		var groupPanel = this.getGroupPanel();
		groupPanel.initDataId = null;
		groupPanel.getWin().show();
	},
	getGroupPanel:function(){
		var groupPanel = this.groupPanel;
		if(!groupPanel){
			groupPanel = new app.modules.form.TableFormView({
				title:"添加数据组",
				colCount:2,
				entryName:"platform.dataset.schemas.RES_DataGroup",
				saveServiceId:"dataSetCatalogSave",
				width:680,
				autoLoadSchema:false,
				actions:[
					{id:"save",name:"确定"},
					{id:"cancel",name:"取消",iconCls:"common_cancel"}
				]
			})
			groupPanel.initPanel();
			groupPanel.on({
				winShow:function(){
					if(groupPanel.initDataId == null){
						groupPanel.doNew();
						var data = {
							DataStandardId:this.resDataStandard
						}
						if(!this.requestData.withOutInGroup){
							data["ParentGroupId"] = {key:this.requestData.gId,text:this.requestData.gName};
						}
						groupPanel.initFormData(data);
					}else{
						groupPanel.loadData();
					}
				},
				beforeSave:function(a,b,c){
					if(b == "update"){
						if(c["DataGroupId"] == c["ParentGroupId"]){
							Ext.MessageBox.alert("提示","自己不能为自己的子组.")
							return false;
						}
					}
				},
				save:function(a,b,c,d){
					this.reloadDicField(this.groupPanel,this.needFlashDicField,false);
					this.reloadDicField(this.dataElementForm,this.needFlashDicField,false);
					if(groupPanel.op == 'create'){
						d.DataElementId = null;
						d.ForceIdentify = null;
						d.CustomIdentify = d.GroupIdentify;
						d.DataSetId = this.selectedNode.attributes.key;
						var r = new this.store.recordType(d);
						this.store.insert(0,r);
					}
					if(groupPanel.op == 'update'){
						this.refresh();
					}
					groupPanel.getWin().hide();
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
					var f = groupPanel.form.getForm().findField(sf);
					if(f){
						f.getStore().on("beforeload",function(store){
							store.setBaseParam("filter",Ext.encode(this.getInitCnd(null, "item.properties")));
						},this);
					}
				}
			}
			this.groupPanel = groupPanel;
		}
		return groupPanel;
	},
	getJoinPanel:function(){
		var panel = this.choicePanel;
		if(!panel){
			panel = new platform.dataset.client.DataElementView({
				resDataStandard:this.resDataStandard,
				doubleClickHander:"choice",
				parentPanel:this
			});
			this.choicePanel = panel;
		}
		return panel;
	},
	reset:function(){
		this.selectedNode = null;
		this.wp.getLoader().load(this.wp.getRootNode());
		this.wp.getSelectionModel().clearSelections();
		this.requestData.cnd = this.getInitCnd(['eq',['i',1],['i',2]]);
		this.clear();
		this.reloadDicField(this.catalogFrom,"ParentCatalogId",true);
		this.reloadDicField(this.dataSetForm,"Catalog",true);
		this.reloadDicField(this.dataElementForm,this.needFlashDicField,false);
		this.reloadDicField(this.groupPanel,this.needFlashDicField,false);
	},
	getTpl:function(){
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="data-item">',
					'<tpl if="DataElementId">',
						'<div class="data-element-item">',
		//					'<div class="data-item-rownum">{DSequence}</div>',
							'<div class="data-element-item-icon"></div>',
					'</tpl>',
					'<tpl if="DataGroupId">',
						'<div class="data-group-item">',
//							'<div class="data-item-rownum">{#}</div>',
							'<div class="data-group-item-icon"></div>',
					'</tpl>',
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
								'<tpl if="DataElementId&&CodeSystem!=1">',
									'<span style="width:180px">{bCodeSystem}</span>',
								'</tpl>',
							'</div>',
							'<div>',
								'<span style="width:615px">{DComment}</span>',
								'<tpl if="ForceIdentify == 1">',
									'<span>必填√</span>',
								'</tpl>',
							'</div>',
						'</div>',
					'</div>',
				'</div>',
			'</tpl>'
		);
		this.tpl = tpl;
		return tpl;
	},
	getPagingToolBar:function(){
		var ptb = platform.dataset.client.DataSetView.superclass.getPagingToolBar.call(this);
		var cb = ptb.items.itemAt(12);
		if(cb){
			cb.setDisabled(true);
		}
		return ptb;
	},
	getSchemaItems:function(){
		var items = platform.dataset.client.DataSetView.superclass.getSchemaItems.call(this);
		items.push({id:"DataGroupId"});
		return items;
	},
	getSearchCnd:function(search, group){
		if(group){
			return 	['or',
				['like',['$', 'a.GroupIdentify'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.DName'],['s','%' + search.getValue() + '%']]
			];
		}else
			return ['or',
				['like',['$', 'a.PyCode'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.CustomIdentify'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.DName'],['s','%' + search.getValue() + '%']],
				['like',['$', 'StandardIdentify'],['s','%' + search.getValue() + '%']]
			];
	},
	validateXML : function(xmlContent) {
		// errorCode 0是xml正确，1是xml错误，2是无法验证
		var xmlDoc, errorMessage, errorCode = 0;
		// code for IE
		if (window.ActiveXObject) {
			xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async = "false";
			xmlDoc.loadXML(xmlContent);

			if (xmlDoc.parseError.errorCode != 0) {
				errorMessage = "错误code: " + xmlDoc.parseError.errorCode + "\n";
				errorMessage = errorMessage + "错误原因: "
						+ xmlDoc.parseError.reason;
				errorMessage = errorMessage + "错误位置: " + xmlDoc.parseError.line;
				errorCode = 1;
			} else {
				errorMessage = "格式正确";
			}
		}
		// code for Mozilla, Firefox, Opera, chrome, safari,etc.
		else if (document.implementation.createDocument) {
			var parser = new DOMParser();
			xmlDoc = parser.parseFromString(xmlContent, "text/xml");
			var error = xmlDoc.getElementsByTagName("parsererror");
			if (error.length > 0) {
				if (xmlDoc.documentElement.nodeName == "parsererror") {
					errorCode = 1;
					errorMessage = xmlDoc.documentElement.childNodes[0].nodeValue;
				} else {
					errorCode = 1;
					errorMessage = xmlDoc.getElementsByTagName("parsererror")[0].innerHTML;
				}
			} else {
				errorMessage = "格式正确";
			}
		} else {
			errorCode = 2;
			errorMessage = "浏览器不支持验证，无法验证xml正确性";
		}
		return {
			"msg" : errorMessage,
			"error_code" : errorCode
		}
	},
	createSearchField:function(){
		var reader = new Ext.data.JsonReader({
            root: 'body',
            totalProperty: 'totalCount',
            fields:['DataSetContentId','DataElementId','DataGroupId','CustomIdentify','StandardIdentify','DName','DComment']
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
	        hideTrigger:true,
	        tpl: tpl,
	        itemSelector: 'div.data-item'
	    })
		search.on("select",this.onSearchSelect,this)
		search.on("beforequery",function(qe){
			var n = this.wp.getSelectionModel().getSelectedNode();
			if(!n || !n.isLeaf()){
				return false
			}else{
				delete qe.combo.lastQuery;
			}
		},this)
	    store.on("beforeload", function() {
			var n = this.wp.getSelectionModel().getSelectedNode();
			var cnd = this.getSearchCnd(search);
			cnd = this.getInitCnd(cnd);
			cnd.push(['eq',['$','DataSetId'],['s',n.attributes.key]])
			this.requestData.cnd = cnd;
			this.requestData.setId = n.attributes.key;
		}, this)
	    return search
	},
	onSearchSelect:function(cb,r,i){
		var index = this.store.find("DataSetContentId",r.get("DataSetContentId"));
		if(index == -1){
			index = this.store.find("DataGroupId",r.get("DataGroupId"));
		}
		if(index > -1){
			this.view.select(index);
			this.view.el.dom.scrollTop = 54*index;
		}
	},
	setSearchField:function(){
		var setSearchRequest={
				serviceId:"simpleQuery",
				method:"execute",
				schema:"platform.dataset.schemas.RES_DataSet"
			}
		var reader = new Ext.data.JsonReader({
            root: 'body',
            totalProperty: 'totalCount',
            fields:['DataSetId','CustomIdentify','StandardIdentify','DName','Catalog']
        })
		var url = ClassLoader.serverAppUrl || "";
		var proxy = new Ext.data.HttpProxy({
			url : url + '*.jsonRequest',
			method : 'post',
			jsonData :setSearchRequest
		})
		var store = new Ext.data.Store({
			proxy: proxy,
			reader:reader
		})
		var tpl = new Ext.XTemplate(
			'<tpl for=".">',
				'<div class="set-item">',
					'<table>',
						'<tr>',
							'<td width="200"><b>{DName}</b></td>',
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
		var setSearch = new util.widgets.MyCombox({
	        store: store,
	        emptyText:"搜索数据集..",
	        displayField:'DName',
	        typeAhead: false,
	        loadingText: 'Searching...',
	        minChars:2,
	        hideTrigger:true,
	        tpl: tpl,
	        itemSelector: 'div.set-item',
	        listeners: {
		        beforequery: function(qe){
		            delete qe.combo.lastQuery;
		        }
  			}
	    })
		setSearch.on("select",this.onSetSelect,this)
	    store.on("beforeload", function() {
			var cnd = this.getSetSearchCnd(setSearch);
			var initCnd = ['eq',['$','a.DataStandardId'],['s',this.resDataStandard]];
			cnd= cnd?['and',initCnd,cnd]:initCnd;
			setSearchRequest.cnd = cnd;
		}, this)
	    return setSearch
	},
	getSetSearchCnd:function(search){
		return ['or',
				['like',['$', 'a.CustomIdentify'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.DName'],['s','%' + search.getValue() + '%']],
				['like',['$', 'a.StandardIdentify'],['s','%' + search.getValue() + '%']]
			];
	},
	onSetSelect:function(cb,r,i){
		var r = util.rmi.miniJsonRequestSync({
			serviceId:"searchSet",
			method:"execute",
			catalog:r.get("Catalog"),
			setName:r.get("DName")
		})
		if(r.code==200){
			var node = this.wp.getRootNode()
			var paths=r.json.path.split("/")
			this.expandNodes(node,paths,0)
		}
	},
	expandNodes:function(parentNode,paths,j) {
	    var nodes = parentNode.childNodes;
		for(var i = 0; i < nodes.length; i++) {
			if(nodes[i].text==paths[j]){
				if(!nodes[i].isLeaf()){
					var nodepath=nodes[i].getPath()
					nodes[i].a=this
					nodes[i].expand("","",function(){
						this.expandNodes(nodes[i],paths,++j)
					},this)
					return
				}else{
					nodes[i].select();
					nodes[i].fireEvent('click', nodes[i]);
				}
			}
		}
    },
    doRemove:function(){
		var rs = this.view.getSelectedRecords();
		if(rs.length != 1){
			return;
		}
		var r = rs[0];
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
	}
});
