$package("app.modules.config.user")

$import("app.desktop.Module","util.dictionary.TreeDicFactory")

chis.application.conf.script.admin.UserConfigForm = function(cfg) {
	this.width = 600
	this.height = 400
	this.midiModules = {}
	this.data = {};
	this.loadServiceId = "chis.simpleLoad"
	this.serviceId = "chis.userService"
	this.formCls = "app.modules.form.TableFormView"
	this.gridCls = "chis.application.conf.script.admin.UserPropList"
	this.gridEntryName = "SYS_UserProp"
	this.userId = "userId"
	Ext.apply(this, app.modules.common)
	chis.application.conf.script.admin.UserConfigForm.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.conf.script.admin.UserConfigForm, app.desktop.Module, {
	
	init:function(){
        this.initPanel()
	},
	
	initPanel : function() {
		var panel = new Ext.Panel({
		    height : 400,
		    width:680,
		    frame : true,
			layout : "border",
			tbar:[
		    	{text:'保存',iconCls:'save',handler:this.doSave,scope:this},'-',
		    	{text:'取消',iconCls:"common_cancel",handler:this.doCancel,scope:this}
		    ],
			items:[
			   { layout:'fit',
			     region:'north',
			     //frame : true,
			     autoHeight:true,
			     items:[this.getUserForm()]
			   },{ 
			     layout:'fit',
			     title:'用户属性',
			     region:'center',
			     //frame : true,
			     items:[this.getProfileList()]
			   }
			]
		})
		this.panel = panel	
		
		if(!this.isCombined){
			this.addPanelToWin();
		}	
		return this.panel;

	},
	
	doNew: function(){
		this.op = "create"
		if(this.data){
			this.data = {}
		}
		var form = this.form.getForm()
		var items = this.formModule.schema.items
		var n = items.length
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var f = form.findField(it.id)
			if(f){
				f.setValue(it.defaultValue)
				if(it.update == "false" && !it.fixed && !it.evalOnServer){
					f.enable();
				}	
			}		
		}
		var f = form.findField(this.formModule.schema.pkey)
	    if(f){
	       f.enable()
	    }
	   
	//清空grid	
	    var store = this.grid.getStore()
	    if(store.getCount() > 0){
	        store.removeAll()
	    }
	    
	},
	
	addPanelToWin:function(){
		if(!this.fireEvent("panelInit",this.panel)){
			return;
		};
		var win = this.getWin();
		win.add(this.panel)
		if(win.el){
			win.doLayout()	
			win.center();
		}
		this.fireEvent("afterPanelInit",this.panel)
	},
		
	getUserForm : function() {		
		var cls = this.formCls
		var cfg = {}
		cfg.entryName = this.entryName
		cfg.autoLoadSchema = false
		cfg.autoLoadData = false;
		cfg.isCombined = true
		cfg.labelWidth = 70
	    cfg.actions = [] 
	    var p = this.midiModules["formss"]
	    if(!p){
			$import(cls)
			var module = eval("new " + cls + "(cfg)");			
			module.setMainApp(this.mainApp)
			this.formModule = module
			p = module.initPanel()
	    }
		this.form = p
		return this.form;
	},
	
	getProfileList : function() {
		var cls = this.gridCls
		var cfg = {}		
		cfg.entryName = this.gridEntryName //"SYS_UserProp"
		cfg.autoLoadSchema = false
		cfg.autoLoadData = false;
		cfg.showButtonOnTop = true
		cfg.isCombined = true
		//cfg.actions = this.actions
		var lp = this.midiModules["gridss"]
		if (!lp) {
			$import(cls)
			var module = eval("new " + cls + "(cfg)");
			module.on("loadData",this.onLoadData,this)
			module.setMainApp(this.mainApp)
			module.opener = this
			this.gridModule = module
			lp = module.initPanel()			
		}
		this.grid = lp
		return this.grid;
	},
	onLoadData:function(){
		this.fireEvent("save",this.entryName,this.op,this.json,this.data)
	}
	,
	loadData: function(){				
		if(this.loading){
			return
		}
		this.initDataId = this.exContext[this.entryName].data[this.userId]
		
		if(!this.fireEvent("beforeLoadData",this.entryName,this.initDataId)){
			return
		}
		if(this.panel && this.panel.el){
			this.panel.el.mask("正在载入数据...","x-mask-loading")
		}
		this.loading = true
		util.rmi.jsonRequest({
				serviceId:this.loadServiceId,
				schema:this.entryName,
				pkey:this.initDataId
			},
			function(code,msg,json){
				if(this.panel && this.panel.el){
					this.panel.el.unmask()
				}
				this.loading = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.loadData)
					return
				}
				if(json.body){
					this.doNew()
					this.initFormData(json.body)
				}
				if(this.op == 'create'){
					this.op = "update"
				}
				
			},
			this)//jsonRequest
			
			
	},
	
	initFormData:function(data){
		this.initForm(data)
		this.initGrid()	
	},
	
	initForm:function(data){
		Ext.apply(this.data,data)	
		this.initDataId = this.data[this.formModule.schema.pkey]			
		var form = this.form.getForm()
		var items = this.formModule.schema.items
		var n = items.length
		for(var i = 0; i < n; i ++){
			var it = items[i]
			var f = form.findField(it.id)
			if(f){
				var v = data[it.id]
				if(v){
					f.setValue(v)
				}
				if(it.update == "false"){
					f.disable();
				}
			}
		}
		this.formModule.setKeyReadOnly(true)	
		
	},
	
	initGrid:function(){
		if(this.gridModule){
			this.gridModule.requestData.cnd = ['eq',['$','userId'],['s',this.initDataId]]
		    this.gridModule.refresh()
		}			
	},
	
	doSave:function(){
		if(this.saving){
			return
		}
		if(!this.formModule){
		    return
		}
		var form = this.form.getForm()
		if(!this.formModule.validate()){
			return
		}
		var ac =  util.Accredit;
		var values = {};
		var items = this.formModule.schema.items
		
		Ext.apply(this.data,this.exContext)
		
		var pkey = ""
		var userId = ""
		if(items){
			var n = items.length
			for(var i = 0; i < n; i ++){
				var it = items[i]
				if(this.op == "create" && !ac.canCreate(it.acValue)){
					continue;
				}				
				var v = it.defaultValue || this.data[it.id]							
				if(v != null && typeof v == "object"){
					v = v.key
				}
				var f = form.findField(it.id)
				if(f){
					v = f.getValue()
				}
				if(it.pkey){
					pkey = it.id
					userId = v
				}
				if(v == null || v === ""){
					if(!(it.pkey == "true") && (it["not-null"]=="1"||it['not-null'] == "true") && !it.ref){
						alert(it.alias +　"不能为空")
						return;
					}
				}
				values[it.id] = v;
			}
		}		

  //获取grid信息		
		var prop = []
		var store = this.grid.getStore()
		var n = store.getCount()
		if(n == 0){
			Ext.Msg.alert("错误", "列表没有数据,不允许保存");
			return
		}
		for(var i=0; i<n; i++){
		    var rr = store.getAt(i)
		    rr.set(pkey,userId)
		    rr.commit()
//		    if(userId != rr.data[pkey]){
//		    	Ext.Msg.alert("错误", "列表中的工号与用户基本信息的工号不同");
//		        return
//		    }
		    prop.push(rr.data)
		}
		values["prop"] = prop
		
		Ext.apply(this.data,values);
		
		this.saveToServer(values)		
	},
	
	saveToServer:function(saveData){
		if(!this.fireEvent("beforeSave",this.entryName,this.op,saveData)){
			return;
		}
		this.saving = true
		this.form.el.mask("正在保存数据...","x-mask-loading")
		util.rmi.jsonRequest({
				serviceId:this.serviceId,
				//op:this.op,
				op:this.op,
				body:saveData
			},
			function(code,msg,json){
				this.form.el.unmask()
				this.saving = false
				if(code > 300){
					this.processReturnMsg(code,msg,this.saveToServer,[saveData],json.body);
					return
				}
				Ext.apply(this.data,saveData);
				if(json.body){
					this.initFormData(json.body)
					this.json = json
				}
				this.op = "update"
			},
			this)//jsonRequest
	},
	
	loadModuleCfg : function(id) {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "moduleConfigLocator",
					id : id
				})
		if (result.code != 200) {
			if (result.msg = "NotLogon") {
				this.mainApp.logon(this.loadModuleCfg, this, [id])
			}
			return null;
		}
		return result.json.body;
	},
	doCancel:function(){
		this.win.hide()	
	}
	,
    getWin: function(){
		var win = this.win
		if(!win){
			win = new Ext.Window({
				id:this.id,
		        title: this.title,
		        autoWidth: true,
		        autoHeight:true,
		        iconCls: 'icon-form',
		        bodyBorder:false,
		        closeAction:'hide',
		        shim:true,
		        layout:"fit",
		        plain:true,
		        autoScroll:false,
		        //minimizable: true,
		        maximizable: true,
		        shadow:false,
		        buttonAlign:'center',
		        modal:true
            })
		    win.on("show",function(){
		    	this.fireEvent("winShow")
		    },this)
		    win.on("close",function(){
				this.fireEvent("close",this)
			},this)
		    win.on("hide",function(){
				this.fireEvent("close",this)
			},this)
			win.on("restore",function(w){
				this.form.onBodyResize()
				this.form.doLayout()				
			    this.win.doLayout()
			},this)
			
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