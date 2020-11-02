$package("chis.application.conf.script.admin")

$import("chis.application.conf.script.admin.OrgaConfigList")

chis.application.conf.script.admin.RoleConfigList = function(cfg){
	cfg.childTab = "chis.application.conf.script.admin.RoleConfigForm"
	cfg.title = "角色列表"
	cfg.className = "RoleConfig"
	cfg.entryName = "RoleConfig"
	chis.application.conf.script.admin.RoleConfigList.superclass.constructor.apply(this,[cfg])
}

Ext.extend(chis.application.conf.script.admin.RoleConfigList, chis.application.conf.script.admin.OrgaConfigList, {
	
	initPanel : function(){		
		var panel = chis.application.conf.script.admin.RoleConfigList.superclass.initPanel.call(this)
		return panel
	},

	onTreeClick:function(node,e){
		if (!node.isLeaf()) {
			return
		}
//		if(!this.checkActions()){
//		    Ext.Msg.alert("提示","没有update操作权限")
//		    return
//		}
		this.midiModules["orag"].op = "update"
		this.midiModules["orag"].initDataId = node.id
		this.midiModules["orag"].title = node.attributes.text
		this.midiModules["orag"].lockLevel = node.attributes.lockLevel
		this.midiModules["orag"].loadData()
//		this.createRoleForm(node)
	},
	
	onSave:function(data,op){
//		this.tree.getRootNode().reload()
//		this.tree.expandAll()
		var id = data["id"]
		if(!id){
			return
		}
		var op=op;
		if(op == "update"){	
			var n = this.tree.getNodeById(id)
			if(n){
				n.setText(data["name"] || n.text)
				n.attributes.lockLevel = data.lockLevel
			}	
		}else if(op == "create"){
			var pid = data["parent"]
			var n = this.tree.getNodeById(pid)
			if(n){
				n.leaf = false
				n.expand(true)
				var cn = n.appendChild({id:id,text:data["name"]||id,leaf:true,iconCls:"user",lockLevel:data.lockLevel})
				cn.expand()
				cn.select()
			}			
		}
			
	},
	
	onContextMenu:function(node,e){
		node.select()
		var actions = this.actions
		var menu = this.midiMenus["roletreemenu"]
		if(!menu){
			var but = []
		    var n = actions.length;
		    for(var i = 0; i < n; i ++){
			   var action = actions[i];
			   var button = {
			      text: action.name,
			      name: action.id,
			      iconCls: action.iconCls || action.id,
			      handler: this.doAction,
			      hidden:true,
			      scope:this
			   }
			   but.push(button)
		    }
			menu = new Ext.menu.Menu({
				items:but
			})
			this.midiMenus["roletreemenu"] = menu
		}
		if(node.id == "base"){
			for(var i = 0; i < actions.length; i++){
			    var it = menu.items.itemAt(i)
			    if(it.name == "create"){
			       it.show()
			    }else{
			       it.hide()
			    }			    
			}
		}else{
		   	for(var i = 0; i < actions.length; i++){
			    var it = menu.items.itemAt(i)
			    if(it.name == "create"){
			       it.hide()
			    }else{
			       it.show()
			    }			    
			}
		}
		menu.showAt([e.getPageX(),e.getPageY()])
		this.fireEvent("contextmenu",node)
	},
	
	addNew:function(item){
	
		var node = this.tree.getSelectionModel().getSelectedNode()
		this.op = item.name
		if(this.op == "create"){
		   if (node.isLeaf() && node.id!="base") {
			  Ext.Msg.alert("提示","本节点不允许增加子节点!")
			  return
		   }
		   //this.op = "create"		   
		}
		this.createRoleForm(node)

	},
	
	createRoleForm:function(node){
		var cls = this.childTab
		var id = node.id
		var parent = id
		id = id +'-menu'
		if(this.op == "update"){
		   parent = node.attributes.parent
		}
		var m = this.activeModules[id]	
		if(m){
			this.tab.activate(m)
		}else{
			cfg = {}
			cfg.parent = parent
			cfg.initDataId = node.id
			cfg.title = node.text
			cfg.autoLoadSchema = false
			cfg.op = this.op
			cfg.actions = this.actions
			cfg._mId = this.panel._mId   //module的id
			$require(cls,[function(){
				m = eval("new " + cls + "(cfg)")
				m.setMainApp(this.mainApp)
				m.on("save",this.onSave,this)
				var p = m.initPanel()
				if(p){
				  p.on("destroy",this.onClose,this)
				  p._Id = id				  
				  p.title = m.title
				  p.closable = true
				  this.tab.add(p)
				  this.tab.doLayout()
				  this.tab.activate(p)
				  this.activeModules[id] = p
				  this.pModules[id] = m
				  this.initModule(id,this.op)
				}	
			},
			this])
		}	
	},
	
	initModule:function(id, op){
		var module = this.pModules[id]
		if(module){
			switch(op){
				case "create":
					module.doNew()
					break;
				case "read":
				case "update":
					module.loadData()
			}
		}
	},
	
	checkActions:function(){
	   	var actions = this.actions
		var n = actions.length;
		for(var i = 0; i < n; i ++){
		   var action = actions[i];
		   if(action.id == "update"){
		   	   return true
		   }
		}
		return false
	}
})