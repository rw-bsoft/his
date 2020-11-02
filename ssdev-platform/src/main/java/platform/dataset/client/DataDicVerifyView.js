$package("platform.dataset.client")
$import(
		"app.lang.UIModule",
		"util.rmi.jsonRequest",
		"util.dictionary.TreeDicFactory",
		"org.ext.ux.layout.TableFormLayout",
		"util.dictionary.SimpleDicFactory"
)
platform.dataset.client.DataDicVerifyView = function(cfg){
	this.dicIdPrefix = "dictionaries.platform.dataset.dic.verify.";
	platform.dataset.client.DataDicVerifyView.superclass.constructor.apply(this,[cfg])
	this.width = 950;
	this.height = 450;
	this.initMaskFlag = false;
}

Ext.extend(platform.dataset.client.DataDicVerifyView,app.lang.UIModule,{
	initPanel:function(){
		var comb = this.initDataStandardComb();
		var tree = util.dictionary.TreeDicFactory.createTree({id:this.dicIdPrefix+this.resDataStandard,keyNotUniquely:true});
		tree.autoScroll = true;
		var form = this.createForm()
		var panel = new Ext.Panel({
			border:false,
//			frame:true,
		    layout:'border',
		    width:this.width,
		    height:this.height,
		    items: [{
		    	layout:"fit",
		    	split:true,
//		    	collapsible:true,
//		        title: '',
		        region:'west',
		        width:200,
		        items:tree,
		        tbar:[{
		        	xtype:"textfield",
					width : 180,
					emptyText : '查找一个字典..',
					enableKeyEvents : true,
					listeners : {
						keyup : {
							fn : this.filterTree,
							buffer : 350,
							scope : this
						},
						scope : this
					}
				}],
				bbar:[comb]
		    },{
		    	layout:"fit",
//		    	split:true,
//		        title: '',
		        region: 'center',
		        width: 280,
		        items:form
		    }]
		});	
		if(this.title){
			panel.setTitle(this.title);
		}
		var p = panel.items.itemAt(0);
		p.on("afterlayout",function(){
			if(this.initMaskFlag){
				return;
			}
			if(p.el){
				p.el.mask("加载中...","x-mask-loading");
				this.initMaskFlag = true;
			}
		},this)
		tree.on("click",this.onTreeClick,this)
		tree.on("load",function(n){
			if(p.el && n == tree.getRootNode()){
				p.el.unmask();
			}
		},this)
//		tree.expand()
		this.tree = tree;
		this.panel = panel;
		return panel
	},
	initDataStandardComb:function(){
		var dataStandardDicId = "platform.dataset.dic.resDataStandard";
		var comb = util.dictionary.SimpleDicFactory.createDic({
			id:dataStandardDicId,
			emptyText:"请选择数据标准..",
			width:'100%',
			editable:false
		})
		var d = util.dictionary.DictionaryLoader.load({id:dataStandardDicId});
		if(d.items && d.items.length > 0){
			this.resDataStandard = this.resDataStandard || d.items[0]["key"];
			comb.on("afterrender",function(comb){
				if(d.wraper[this.resDataStandard]){
					comb.setValue(d.wraper[this.resDataStandard]);
				}
			},this)
		}
		comb.on({
			select:function(cb,r,index){
				this.resDataStandard = r.data.key;
				this.tree.getLoader().url = this.dicIdPrefix+this.resDataStandard+".dic";
				this.tree.getLoader().load(this.tree.getRootNode());
				this.form.getForm().reset();
			},
			scope:this
		});
		this.dataStandardComb = comb;
		return comb;
	},
	createForm:function(){
		var propGrid = this.createPropGrid();
		var cfg = {
        labelWidth: 75, 
        bodyStyle:'padding:2px 20px 0 0',
        width: 580,
        autoScroll:true,
        defaultType: 'textfield',
        defaults:{
        	width:'90%'
        },
        layout:'tableform',
        layoutConfig:{
			columns:2,
			tableAttrs:{
				border:0,
				cellpadding:'2',
				cellspacing:"2"
			}
		},
        items: [
        		{
                fieldLabel: '字典编码',
                allowBlank:false,
                name: 'dicId'
            },{
                fieldLabel: '字典名称',
                name: 'dicAlias',
                allowBlank:false
            },{
                fieldLabel: '父节点编码',
                name: 'parentKey'
            },{
                fieldLabel: '父节点名称',
                name: 'parentText'
            }, {
                fieldLabel: '节点编码',
                allowBlank:false,
                name: 'key'
            }, {
                fieldLabel: '节点名称',
                allowBlank:false,
                name: 'text'
            }, propGrid
        ]}
        var buttons = this.createButtons();
        if(buttons && buttons.length > 0){
        	cfg.tbar = buttons;
        }
	    var form = new Ext.FormPanel(cfg);
	    this.form = form
        return form;
	},
	createPropGrid:function(){
		var propGrid = new Ext.grid.EditorGridPanel({
			height: 200,
//			width:"480",
			hidden:true,
			clicksToEdit : 1,
			autoExpandColumn:"dic_prop_value",
			cm : new Ext.grid.ColumnModel([{
			  	header : "属性",
				dataIndex : "p_key",
				width:220,
				editor : new Ext.form.TextField()
			},{
				id:"dic_prop_value",
			   	header : "值",
				dataIndex : "p_value",
				width:480,
				editor : new Ext.form.TextField()
			}]),
			store:new Ext.data.JsonStore({
			   	fields:["p_key","p_value"]
			})
		})
		this.propGrid = propGrid
		propGrid.colspan = 2;
		return propGrid
	},
	
	onTreeClick:function(node,e){
		this.selectedNode = node;
		var n = node;
		while(n){
			if(n.attributes.type){
				break
			}
			n = n.parentNode
		}
		this.selectDicNode = n;
		this.form.getForm().reset();
		var fields = this.getFields();
		var index = n.attributes.key.lastIndexOf(".");
		fields.dicId.setValue(n.attributes.key.substring(index+1));
		fields.dicAlias.setValue(n.attributes.text);
		this.propGrid.getStore().removeAll();
		if(node == n){
			this.propGrid.hide();
		}
		else{
			fields.key.setValue(node.attributes.key)
			fields.text.setValue(node.attributes.text)	
			if(node.parentNode.attributes.type != "dic"){
				fields.parentKey.setValue(node.parentNode.attributes.key)
				fields.parentText.setValue(node.parentNode.text)
			}
			var index = n.attributes.key.lastIndexOf(".");
			fields.dicId.setValue(n.attributes.key.substring(index+1));
			this.propGrid.show()
			var attributes = this.selectedNode.attributes
			for(var id in attributes){
				if(id=="key"||id=="text"||id=="parent"||id=="leaf"||id=="loader"||id=="id"||id=="folder" || id=="properties"){
					continue
				}
				var _id = id
				if(id=="pyCode"){
					_id="拼音码"
				}
				if(id=="mCode"){
					_id="速记码"
				}
				this.addProp(_id,attributes[id])
			}
		}
		node.expand()
	},
	addProp:function(key,value){
		var record = Ext.data.Record.create([
			{name:"p_key"},
			{name:"p_value"}
		])
		var r = new record({p_key:typeof(key)=="string"?key:"",p_value:typeof(value)=="string"?value:""})
		this.propGrid.getStore().add(r)
		var count = this.propGrid.getStore().getCount()
	},
	getFields:function(){
		var fields = this.fields
		if(!fields){
			var items = this.form.items
			var n = items.getCount()
			var fields = {}
			for(var i = 0; i < n; i ++){
				var f = items.get(i)
				if(f.name)
					fields[f.name] = f
			}
			this.fields = fields
		}
		return fields
	},
	getDirectory:function(){
		var url = this.tree.getLoader().url;
		var directory = url.substring("dictionaries.".length);
		directory = directory.substring(0, directory.length-4);
		return directory;
	},
	filterTree:function(t,e){
		var text = t.getValue();
		if(!text){
			this.tree.filter.clear();
			return;
		}
		var re = new RegExp(Ext.escapeRe(text), 'i');
		this.tree.filter.filterBy(function(n){//parentNode
			if(n.parentNode==this.tree.getRootNode()){
				return re.test(n.attributes.text) || re.test(n.attributes.key);
			}else{
				return true			
			}
		},this);
	},
	doVerify:function(){
		if(!this.selectDicNode){
			return;
		}
		Ext.Msg.confirm("字典审核","字典["+this.selectDicNode.text+"]审核是否通过？",function(btn){
			if(btn == "yes"){
				var dicId = this.selectDicNode.attributes.key;
				util.rmi.jsonRequest({
					serviceId:"dictionaryVerify",
					method:"verify",
					body:[this.resDataStandard,dicId]
				},function(code,msg,json){
					if(code < 300 && json.body == true){
						this.selectDicNode.remove();
						this.form.getForm().reset();
					}else{
						Ext.Msg.alert("错误",msg);
					}
				},this)
			}
		},this);
	},
	doReject:function(){
		if(!this.selectDicNode){
			return;
		}
		Ext.Msg.confirm("字典审核拒绝","字典["+this.selectDicNode.text+"]审核是否拒绝？",function(btn){
			if(btn == "yes"){
				var dicId = this.selectDicNode.attributes.key;
				util.rmi.jsonRequest({
					serviceId:"dictionaryVerify",
					method:"reject",
					body:[this.resDataStandard,dicId]
				},function(code,msg,json){
					if(code < 300 && json.body == true){
						this.selectDicNode.remove();
						this.form.getForm().reset();
					}else{
						Ext.Msg.alert("错误",msg);
					}
				},this)
			}
		},this);
	},
	getWin: function(){
		var win = this.win
		var closeAction = "close"
		if(!this.mainApp){
			closeAction = "hide"
		}
		if(!win){
			win = new Ext.Window({
				id: this.id,
		        title: this.title,
		        width: this.width,
		        iconCls: 'icon-grid',
		        shim:true,
		        layout:"fit",
		        animCollapse:true,
		        items:this.initPanel(),
		        closeAction:closeAction,
		        constrainHeader:true,
		        minimizable: true,
		        maximizable: true,
		        shadow:false
            })
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			this.win = win
		}
		return win;
	}	
})
