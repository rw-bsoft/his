$package("chis.application.conf.script")

$import(
	"chis.script.BizModule",
	"org.ext.ux.TabCloseMenu",
	"chis.script.app.modules.common",
	"util.dictionary.TreeDicFactory",
	"util.rmi.jsonRequest",
	"util.dictionary.TreeCheckDicFactory"
)

chis.application.conf.script.UnitTypeForm = function(cfg){
	this.height = 300
	this.dicId = "chis.dictionary.unitType"
	Ext.apply(this,chis.script.app.modules.common)
	chis.application.conf.script.UnitTypeForm.superclass.constructor.apply(this,[cfg])
}

Ext.extend(chis.application.conf.script.UnitTypeForm, chis.script.BizModule,{
	initPanel:function(){
		var tree = util.dictionary.TreeDicFactory.createTree({id:this.dicId,keyNotUniquely:true})
		tree.autoScroll = true
		var form = this.createForm()
		var panel = new Ext.Panel({
			border:false,
			frame:true,
		    layout:'border',
		    width:this.width,
		    height:this.height,
		    tbar:[
		    	{text:'增加',iconCls:'treeInsert',handler:this.doInsertItem,scope:this},
		    	{text:'保存',iconCls:'save',handler:this.doSaveItem,scope:this},
		    	{text:'删除项',iconCls:'treeRemove',handler:this.doRemoveItem,scope:this}
		    ],
		    items: [{
		    	layout:"fit",
		    	split:true,
		    	bodyStyle:'padding:5px 0 0 0',
		        region:'west',
		        width:200,
		        items:tree
		    },{
		    	layout:"fit",
		    	split:true,
		        title: '',
		        region: 'center',
		        width: 400,
		        items:form
		    }]
		});	
		tree.on("click",this.onTreeClick,this)
		tree.expand()
		this.tree = tree;
		this.panel = panel;
		return panel
	},
	createForm:function(){
		var roles = util.dictionary.TreeCheckDicFactory.createDic({id:"rolelist",editable:false,onlyLeafCheckable:true})
		roles.on("render",function(){roles.onTriggerClick();roles.onTriggerClick()});
		roles.tree.on("render",function(){roles.tree.expandAll()})
		roles.name = "roles"
		roles.fieldLabel = "角色列表"
		roles.allowBlank = false
	    var form = new Ext.FormPanel({
	        labelWidth: 75, 
	        frame:true,
	        bodyStyle:'padding:2px 20px 0 0',
	        width: 580,
	        defaults: {width: 550},
	        autoScroll:true,
	        labelAlign:'top',
	        defaultType: 'textfield',
	        items: [
	        	{
	                fieldLabel: '机构类型编码',
	                name: 'key',
	                allowBlank:false
	            }, {
	                fieldLabel: '机构类型名称',
	                name: 'text',
	                allowBlank:false
	            }, roles,
	            {
	                fieldLabel: '编码位数',
	                name: 'codeLength',
	                xtype : "numberfield",
	                decimalPrecision:0,
	                allowDecimals : false,
	                allowBlank:false
	            }
	        ]});
	    this.roles = roles
	    this.form = form
        return form;
	},
	doInsertItem:function(){
		this.cmd = "createItem"
		this.form.getForm().reset()
		var fields = this.getFields()
		fields.key.setDisabled(false)
		fields.key.focus()
	},
	doSaveItem:function(){
		if(!this.form.getForm().isValid()){
			return
		}
		var fields = this.getFields();
		var data = {
			dicId:this.dicId,
			key:fields.key.getValue(),
			text:fields.text.getValue(),
			props:[{
				name:"roles",
				value:fields.roles.getValue()
			}],
			codeLength:fields.codeLength.getValue()
		}
		var result = this.saveToServer(this.cmd,data)
		if(result.code == 200){
			if(result.msg == "Created"){
				this.cmd = "updateItem";
				var parent = this.tree.getRootNode()
				var node = parent.appendChild({key:data.key,text:data.text,leaf:true,codeLength:data.codeLength})
				for(var i=0;i<data.props.length;i++){
					node.attributes[data.props[i].name] = data.props[i].value
				}
				node.select()
				node.ensureVisible()
				this.onTreeClick(node)
			}
			if(result.msg == "Update"){
				var node = this.selectedNode
				node.setText(data.text)
				node.attributes.codeLength = data.codeLength
				for(var i=0;i<data.props.length;i++){
					node.attributes[data.props[i].name] = data.props[i].value
				}
				node.select()
				node.ensureVisible()
				this.onTreeClick(node)
			}
		}
		else{
			Ext.Msg.alert("消息",result.msg)
		}
	},
	doRemoveItem:function(){
		var node = this.selectedNode
		if(!node){
			return
		}
		var fields = this.getFields();
		var text = fields.text.getValue()
		Ext.Msg.show({
		   title: '确认删除机构类型',
		   msg: '[' + text +']' +'删除将无法回复，是否继续?',
		   modal:false,
		   width: 300,
		   buttons: Ext.MessageBox.OKCANCEL,
		   multiline: false,
		   fn: function(btn, text){
		   	 if(btn == "ok"){
		   	 	this.processItemRemove();
		   	 }
		   },
		   scope:this
		})		
	
	},
	processItemRemove:function(){
		var fields = this.getFields();
		var key = fields.key.getValue()
		var data = {
			dicId:this.dicId,
			key:key
		}
		var cmd = "removeItem"
		var result = this.saveToServer(cmd,data)
		if(result.code == 200){
			var node = this.selectedNode
			if(node){
				var next = node.nextSibling || node.previousSibling
				var parent = node.parentNode
				parent.removeChild(node)
				if(next){
					next.select()
					next.ensureVisible()
					this.onTreeClick(next)
				}
			}				
		}
	},
	onTreeClick:function(node,e){
		this.selectedNode = node
		this.cmd = "updateItem"
		this.form.getForm().reset();
		var n = node
		var fields = this.getFields()
		fields.key.setValue(n.attributes.key)
		fields.text.setValue(n.attributes.text)
		fields.key.setDisabled(true)
		fields.roles.setValue({key:n.attributes.roles})
		fields.codeLength.setValue(n.attributes.codeLength)
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
	saveToServer:function(cmd,data){
		if(this.form && this.form.el){
			this.form.el.mask("正在保存数据...","x-mask-loading")
		}
		
		var result = util.rmi.miniJsonRequestSync({
			serviceId:"chis.systemUserService",
			serviceAction:"checkRoleUse",
			method:"execute",
			body:data
		})
		if(result.code >= 400){
			if(result.code == 401){
				if(result.json.body){
					var roles = result.json.body.roles
					for(var i=0;i<roles.length;i++){
						var node = this.roles.tree.getNodeById(roles[i])
						node.getUI().check(true)
					}
				}
			}
			if(result.code == 400){
				if(result.json.body){
					var role = result.json.body.role
					var node = this.roles.tree.getNodeById(role)
					node.getUI().check(false)
				}
			}
			if(this.form && this.form.el){
				this.form.el.unmask()
			}
			return result
		}
		var rel = util.rmi.miniJsonRequestSync({
			serviceId:'chis.dictionaryUtil',
			method:"execute",
			cmd:cmd,
			body:data
		})
		if(this.form && this.form.el){
			this.form.el.unmask()
		}
		var code = rel.code
		var msg = rel.msg
		if(code > 300){
			this.processReturnMsg(code,msg,this.saveToServer,[cmd,data]);
			return
		}
		return rel;
	},
	getWin: function(){
		var win = this.win
		if(!win){
			win = new Ext.Window({
				id: this.id,
		        title: this.title,
		        width: 815,
		        height:500,
		        iconCls: 'icon-grid',
		        shim:true,
		        layout:"fit",
		        animCollapse:true,
		        items:this.initPanel(),
		        closeAction:'hide',
		        constrainHeader:true,
		        //minimizable: true,
		        //maximizable: true,
		        shadow:false
            })
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			this.win = win
		}
		win.instance = this;
		return win;
	}	
})