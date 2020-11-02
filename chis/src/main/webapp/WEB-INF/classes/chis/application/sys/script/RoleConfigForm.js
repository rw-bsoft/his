$package("chis.application.conf.script.admin")

$import("app.desktop.Module",
		"chis.script.chis.script.util.dictionary.TreeCheckDicFactory",
		"chis.script.chis.script.util.dictionary.LovComboDicFactory",
		"util.rmi.jsonRequest",
		"chis.script.app.modules.common",
		"util.rmi.miniJsonRequestSync"
		)
chis.application.conf.script.admin.RoleConfigForm = function(cfg){
	this.autoLoadSchema = true
	this.entryName = "RoleConfig"
	this.serviceId="chis.roleConfig"
	cfg.removeServiceId="chis.roleConfig"
	this.subWindows = {}
	Ext.apply(this,chis.script.app.modules.common)
	chis.application.conf.script.admin.RoleConfigForm.superclass.constructor.apply(this,[cfg])
}

Ext.extend(chis.application.conf.script.admin.RoleConfigForm,app.desktop.Module,{
	init:function(){
		this.addEvents({
			"save":true
		})
		if(this.autoLoadSchema){
			this.initPanel()
		}
	},
	initPanel:function(){
		var editpanel = new Ext.grid.EditorGridPanel({
			height: 120,
			width: 350,
			clicksToEdit : 1,
			tbar:[
				{text:"增加属性",iconCls:"add",handler:this.addRoleProp,scope:this},
				{text:"删除属性",iconCls:"remove",handler:this.removeProp,scope:this}
			],
			cm : new Ext.grid.ColumnModel([{
			  	header : "属性",
				dataIndex : "p_key",
				width:150,
				editor : new Ext.form.TextField()
			},{
			   	header : "值",
				dataIndex : "p_value",
				width:150,
				editor : new Ext.form.TextField()
			}]),
			store:new Ext.data.JsonStore({
			   	data:{},
			   	fields:["p_key","p_value"]
			})
		})
		this.editPanel = editpanel
		
		//editpanel.on("contextmenu",this.onCellContextMenu,this)
		
//		var combox = chis.script.chis.script.util.dictionary.SimpleDicFactory.createDic({id:'boolType',width:200,editable:false,defaultValue:"false"})
//        combox.name = 'lock'
//        combox.fieldLabel = '是否可修改'
//        combox.allowBlank = false
        
		var baseFS = new Ext.form.FieldSet({
			title:"基本信息",
			defaultType:"textfield",
			collapsible:true,
			animCollapse:true,
			autoHeight:true,
			autoWidth:true,
			items:[{
				fieldLabel:"角色编号",
				value:this.initDataId,
				width:200,
				allowBlank:false,
				blankText:"此项不能为空！",
				name:"id"
			},{
				fieldLabel:"中文名称",
				value:this.title=="新建角色"?"":this.title,
				width:200,
				name:"name"
			},{
				fieldLabel : "锁定级别",
				width : 200,
				name : "lockLevel",
				xtype : "numberfield",
				decimalPrecision : 0,
				allowDecimals : false,
				allowBlank : false,
				blankText : "此项不能为空！"
			},{xtype:"panel",html:"<span style='font-size:12'>属性:</span><br/><br/>"},editpanel]
		})
			
		var apps = chis.script.chis.script.util.dictionary.TreeCheckDicFactory.createDic({id:"applist",checkModel:"parentCascade",width:250})
		apps.fieldLabel = "菜单选择"
		this.apps = apps
		
		var appsFS = new Ext.form.FieldSet({
			title:"菜单权限",
			defaultType:"textfield",
			collapsible:true,
			animCollapse:true,
			autoHeight:true,
			autoWidth:true,
			items:[apps]
		})

		var service = chis.script.chis.script.util.dictionary.LovComboDicFactory.createDic({id:"servicelist",width:250,editable:false})
		service.fieldLabel = "服务接口方法"
		service.store.on("load",function(){
		     service.setValue({
		         key:'logon,schemaLoader,simpleSave,simpleLoad,simpleQuery,simpleRemove,others',
		         text:'logon,schemaLoader,simpleSave,simpleLoad,simpleQuery,simpleRemove,其它'
		     })
		},this)
		this.service = service
		
		var serviceFS = new Ext.form.FieldSet({
			title:"服务接口权限",
			defaultType:"textfield",
			collapsible:true,
			animCollapse:true,
			autoHeight:true,
			autoWidth:true,
			items:[service]
		})
		
		var tbar = this.initBars()
		

		var panel = new Ext.FormPanel({
			frame:true,
			labelWidth:100,
//			labelAlign:"left",
			bodyStyle:"padding:10px",
			tbar:tbar,
			
//			[{
//				text:"权限保存",
//				iconCls:"save",
//				handler:this.doSave,
//				scope:this
//			}],
			items:[baseFS,appsFS,serviceFS]
		})
		this.panel = panel				
		return panel
	},
	
	doSave:function(){
		var panel = this.panel
		var baseFS = panel.items.itemAt(0)
		var id = baseFS.items.itemAt(0).getValue()
		if(id == ""){
			baseFS.items.itemAt(0).isValid()
			return;
		}
		var values = {}
		values["id"] = id
		var appsFS = panel.items.itemAt(1)
		var st = this.editPanel.getStore()		
		values["name"] = baseFS.items.itemAt(1).getValue()
		this.title = baseFS.items.itemAt(1).getValue()
		values["lockLevel"] = baseFS.items.itemAt(2).getValue()
		this.lockLevel = baseFS.items.itemAt(2).getValue()
		if(this.parent){
			values["parent"] = this.parent
		}
		var props = {}
		for(var num=0;num<st.getCount();num++){
			var r = st.getAt(num)
			if(r.get("p_key"))
				props[r.get("p_key")] = r.get("p_value")
		}
		values["props"] = props
		values["apps"] = this.apps.getValue()	
		values["service"] = this.service.getValue()
		this.saveToServer(values)
		
	},

	doNew:function(){

		this.op = "create"
		this.initDataId = ""
		this.panel.items.itemAt(0).items.itemAt(0).setDisabled(false)
		this.panel.items.itemAt(0).items.itemAt(0).setValue("")
		this.panel.items.itemAt(0).items.itemAt(1).setValue("")
		this.apps.setValue({key:""})
		this.service.setValue({key:""})
		var st = this.editPanel.getStore()
		st.removeAll()
	},
	
	loadData:function(){	
		//this.doNew()
		if(!this.initDataId){
			return
		}
		this.initLoad(this.initDataId)
	},
	initLoad:function(pkey){
		if(this.panel && this.panel.el){
			this.panel.el.mask("正在保存数据...","x-mask-loading")
		}
		var data={}
		data['pkey']=pkey
		util.rmi.jsonRequest({
				serviceId:this.serviceId,
				cmd:"loadById",
				domain:this.domain,
				module:this._mId,  //增加module的id
				body:data
			},
			function(code,msg,json){
				if(this.panel && this.panel.el){
					this.panel.el.unmask()
				}
				if(code > 300){
					this.processReturnMsg(code,msg,this.initLoad,[pkey])
					return
				}
				if(json.body){
					this.initFormData(json.body)
				}
				if(this.op == 'create'){
					this.op = "update"
				}
			},
			this)//jsonRequest
	},
	initFormData:function(data){ //显示数据到combox
		this.editPanel.store.removeAll()
		for(var id in data){
			var v = data[id]
			if(id == "proptities"){
				for(var j=0;j<v.length;j++){
					this.addRoleProp(v[j].name,v[j].value)
				}
				continue;
			}
			if(id == "apps"){
				this.apps.setValue(v)
				continue
			}
			if(id == "service"){
				this.service.setValue(v)
				continue
			}
		}
		var idf = this.panel.items.itemAt(0).items.itemAt(0)
		idf.setValue(this.initDataId)
		if(idf){
		    idf.setDisabled(true)
		    this.initDataId = idf.getValue()		    
		}
		var idt = this.panel.items.itemAt(0).items.itemAt(1)
		idt.setValue(this.title)
		if(idt){
		    this.panel.setTitle(idt.getValue())
		}
		var idl = this.panel.items.itemAt(0).items.itemAt(2)
		idl.setValue(this.lockLevel)
//		var lock = {}
//		lock.key = this.lock
//		if(this.lock =='true'){
//			lock.text = "是"
//		}else{
//			lock.text = "否"
//		}
//		idl.setValue(lock)
	},
	
	saveToServer:function(saveData){
		if(!this.fireEvent("beforeSave",this.entryName,this.op,saveData)){
			return;
		}
		if(this.initDataId == null){
			this.op = "create";
		}
		this.saving = true
		this.panel.el.mask("正在保存数据...","x-mask-loading")
		util.rmi.jsonRequest({
				serviceId:this.serviceId,
				cmd:this.op,
				domain:this.domain,
				body:saveData,
				isValid:false,
				module:this._mId  //增加module的id
			},
			function(code,msg,json){
				this.panel.el.unmask()
				this.saving = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.saveToServer,[saveData]);
					return
				}
				if(json.body){
					this.initFormData(json.body)
					this.fireEvent("save",json.body,this.op)
				}
				this.op = "update"
			},
			this)//jsonRequest
	},
	
	onCellContextMenu:function(e){
		e.stopEvent()
		var ep = this.editPanel
		var cmenu = this.midiMenus['role_props']
		if(!cmenu){
			var items = [{
				text:"增加属性",
				iconCls:"add",
				handler:this.addRoleProp,
				scope:this
			},{
				text:"删除属性",
				iconCls:"remove",
				handler:this.removeProp,
				scope:this
			}];
			cmenu = new Ext.menu.Menu({items:items})
			this.midiMenus['role_props'] = cmenu
		}
		cmenu.showAt([e.getPageX(),e.getPageY()])
	},
	
	removeProp:function(){
		var ep = this.editPanel
	   	var cell = ep.getSelectionModel().getSelectedCell()
		if(cell){
			var row = cell[0]
			var r = ep.getStore().getAt(row)
			ep.getStore().remove(r)
		}
	},
	
	addRoleProp:function(key,value){
		var ep = this.editPanel
		var record = Ext.data.Record.create([
			{name:"p_key"},
			{name:"p_value"}
		])		
		ep.getStore()
			.add(new record({p_key:typeof(key)!="object"?key:"",p_value:typeof(value)!="object"?value:""}))
		var count = ep.getStore().getCount()
		ep.getSelectionModel().select(count-1,0)
	},
	
	initBars:function(){
		var tbar = []
	    var actions = this.actions
	    if(actions){
	       	var n = actions.length;
		    for(var i = 0; i < n; i ++){
			   var action = actions[i];
			   if(action.id == "create" || action.id == "update"){
			      var button = {
			         text: "权限保存",
			         name: action.id,
			         iconCls: action.iconCls || action.id,
			         handler: this.doSave,
			         scope:this
			      }
			      tbar.push(button)
			      break
			   }
		    }		    
	    }
	    return tbar
	}
})