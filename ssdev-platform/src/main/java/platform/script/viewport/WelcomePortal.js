$package("platform.script.viewport")
$import(
			 "org.ext.ux.portal.Portal",
			 "app.modules.list.SimpleListView", 
			 "app.modules.chart.DiggerChartView",
			 "util.rmi.jsonRequest"
			 )
platform.script.viewport.WelcomePortal = function(cfg) {
	this.portlets = {}
	this.portletModules = {}
	this.pModules = {}
	this.cmd = "weladd"
	this.count = 4
	this.colCount = 2; //列数(竖)
	this.rowCount = 2; //行数(横)
	this.midiModules = {}
	this.saveServiceId = "platform.userService"
	this.serviceId = "welcomeService"
	this.entryName = "platform.index.schemas.SYS_HomePage"
	this.actions  = [
		{text:"2格布局",iconCls:"laytwo",count:2,row:1},
		{text:"3格布局",iconCls:"laythree",count:3,row:2},
		{text:"4格布局",iconCls:"layfour",count:4,row:2},
		{text:"5格布局",iconCls:"layfive",count:5,row:2},
		{text:"6格布局",iconCls:"laysix",count:6,row:2}	
	]
	//this.title = "我的首页"
	platform.script.viewport.WelcomePortal.superclass.constructor.apply(this,[cfg])
}
Ext.extend(platform.script.viewport.WelcomePortal, app.desktop.Module, {
	
	init : function(){
		this.initCount()
	},

	initPanel : function() {		
		if(this.portal){
		    return this.portal		
		}
		var dataview = this.initPagingBar()	
		var items = this.initMenuItems()
		var laybut = new Ext.Toolbar.SplitButton({
			text : '',
			iconCls : "setlay",
			notReadOnly : true,
			menu : new Ext.menu.Menu({
				items :items
			})
		})
		var changbut = new Ext.Button({
		    text:'保存布局?',
		    hidden:true,
		    handler:this.doLaySave,
		    scope:this
		})
		this.changbut = changbut
		var cfg = {
		   //renderTo:'_index',
		   border:false,
           height:this.height,
           autoScroll : false,
		   buttonAlign : 'center',
		   fbar : [laybut,dataview,changbut],
		   items : []
		}
		if(this.renderTo){
			cfg.renderTo = this.renderTo
		}
		this.portal = new Ext.ux.Portal(cfg)		
		this.addPortalItems()

		return this.portal;
	},
	
	initPagingBar:function(){
		var data = this.getPages()
	    var tpl =  new Ext.XTemplate(
			   '<tpl for=".">',
	             '<a href="#">{page}</a>',
			   '</tpl>'
		)
		var store = new Ext.data.Store({
	        reader: new Ext.data.JsonReader({},['page']),
	        data: data
	    })
	    
		var dataview = new Ext.DataView({
           store: store,
           tpl:tpl,  
		   cls:'manu2',
           itemSelector: 'a',
           singleSelect: true,
		   selectedClass:"current"
           //autoScroll  : true
        })
        dataview.on("click",this.nextPage,this)
        dataview.on("afterrender",function(v){
            v.select(0)
        },this)
        this.dataview = dataview
        return dataview
        
	},
	
	initMenuItems:function(){
		var actions = this.actions
	    var buttons = []
	    if(!actions){
			return buttons
		}
		
		for(var i = 0; i < actions.length; i ++){
			var action = actions[i]
			var btn = {
			     handler:this.changeLayout,
			     scope:this			
		    }
			Ext.apply(btn,action)
			buttons.push(btn)
		}
		return buttons
	},
	
	getPages:function(){
		var modules = this.getAllModuleId()
		var cellNum = this.count
		var pages = Math.ceil(modules.length/cellNum)
	//若总数刚好一整页,就增加一个空白页面
		if(modules.length%cellNum == 0){
		    pages = pages + 1
		}
		var data = []
		for(var i=0; i<pages; i++){
		    data.push({page:i+1})
		}
		return data
	
	},
	
	nextPage:function(view,index,node,e){
		var page = view.store.getAt(index).data.page
		this.portal.removeAll()
		this.addPortalItems(page)
		this.portal.doLayout()
		this.refresh()		
	},
	
	addPortalItems:function(pageNo){
		this.portlets = {}
	    this.portletModules = {}
	    
		var modules = this.getAllModuleId()
		if(!pageNo){
		    pageNo = 1
		}
		var pageSize = this.count
		var first = pageSize * (pageNo-1)
		var end = pageSize * pageNo
		var ms = modules.slice(first,end);
	    var portlets = this.portlets
		this.initPortlets(ms);
		
		var col = 0
		var row = 0
		var num = Math.floor(this.count/this.rowCount)
		for (var i = 0; i < this.count; i++) {
		    var column = {
				columnWidth : 1/num,
				style : 'padding:4px 4px 0px 4px',
				items : []
			}
			var index = col + "." + row
			var p = portlets[index]
			if(p){
				column.items.push(p)
				column.pos = index    //位置
			}
			this.portal.add(column)
			if(++row >= num){
			    row = 0
			    col++
			    if(this.count%this.rowCount != 0){
		       	   num = Math.ceil(this.count/this.rowCount)
		       }
			}			
		}
	},
	
	initPortlets:function(modules){
		if(!modules){
			return
		}
		var portlets = this.portlets  //{}		
		var col = 0
		var row = 0

		var n = this.count
		var num = Math.floor(this.count/this.rowCount)
		for(var i=0; i<n; i++){
			var mod = modules[i]
			if(mod){
			   mod = this.loadModuleCfg(mod.moduleId)			   
			}			
			var m = this.createModule(mod)
			var p = this.getPortlet(m)
			portlets[col+"."+row] = p 
			if(m){
			   this.portletModules[col+"."+row] = m
			}

		    if(++row >= num){
		       row = 0
		       col++
		       if(this.count%this.rowCount != 0){
		       	   num = Math.ceil(this.count/this.rowCount)
		       }
		    }
		}
	},

	getPortlet:function(module){
		var backgroundUrl = "background-image: url(" +ClassLoader.stylesheetHome 
		               + "app/desktop/images/homepage/titlebg.jpg);border:1px solid #FFFFFF;"
		if(module){
		   var p =  module.initPanel()
		   p.frame = false
		   p.border = false
		   var cfg = {
		   	  title : module.name, 
		   	  //pkey : module.pkey,
		   	  moduleId : module.fullId,
		   	  headerStyle:backgroundUrl,
		   	  border:false,
		   	  bodyStyle:'padding:1px;',
		   	  style:'border: 1px solid #CCCCCC;margin-bottom: 5px',
		      height : Math.ceil((this.height-60)/this.rowCount),
			  frame:false,
			  collapsible:false,  //去掉伸缩
			  draggable:false,    //拖拽
			  autoScroll : false,
			  width : '110%',
			  layout : "fit",
			  tools : [			  	
			  	{id:'maximize',handler:this.maxPanel,scope:this},
			  	{id:'close',handler:this.removePortlet,scope:this}
			  ],
			  items: [p]
		   }		   
		   return new Ext.ux.Portlet(cfg)
		}else{
		   return this.emptyPortlet()
		}
	},
	
	emptyPortlet:function(){
	   var cfg = {
		    height : Math.ceil((this.height-60)/this.rowCount),
			frame:false,
			border:false,
			collapsible:false,  //去掉伸缩
			draggable:false, //拖拽
			width : '110%',
			style : 'border: 1px dashed #99BBE8;margin-bottom: 5px',   //边框虚线化
		    bodyStyle : 'background-color:#ECECEC',
		    layout : {
		      type:'hbox',
		      align:'middle',
              pack:'center'
		    },
		    items:[new Ext.Toolbar({
		    	buttonAlign:'center',
		        style:'border-width:0px;background:none',
		        frame:false,
		        items:[{scale:'large',text:'',iconCls:'addItem',handler:this.doNew,scope:this}]
		    })]
		}
		return new Ext.ux.Portlet(cfg)
	},
	
	addNewPortlet:function(pc,data){
		var portlet = pc.items.item(0)
		pc.remove(portlet,true)   //portletColumn
		
		var mod = this.loadModuleCfg(data.moduleId)
		if(mod == null){
		   return
		}
		var m = this.createModule(mod)
		if(m){
		   this.portletModules[mod.id] = m
		   var p = this.getPortlet(m)
		   if(p){
		   	  pc.add(p)
		   }		   
		   pc.doLayout()
		   if(m.loadData){
		       m.loadData()		   
		   }
		   this.clearCacheModules()
		}
		delete portlet	
		//刷新页数
		this.refreshPage()
		this.roleModule = false

	},
	
	refreshPage:function(){
		var page = this.dataview.getSelectedRecords()
		var p_index = page[0].data.page
		var data = this.getPages()
		this.dataview.getStore().loadData(data)
		this.dataview.select(p_index-1)
	},
	
	changeLayout:function(item){
		var c = item.count
		if(this.count == c){
			this.refresh()
			return
		}
		this.portal.removeAll()
		this.count = item.count
		this.rowCount = item.row
	    this.addPortalItems()
		var data = this.getPages()
		this.dataview.getStore().loadData(data)
		this.dataview.select(0)
		this.portal.doLayout()
		this.refresh()
		
		if(this.current == this.count){
			this.changbut.setVisible(false)
		}else{
			this.changbut.setVisible(true)
		}
	   
	},
	
	removePortlet:function(e, target, panel){
		Ext.MessageBox.show({
              title: '确认删除[' + panel.title + ']',
              msg: '删除操作将无法恢复，是否继续?',
              buttons: Ext.MessageBox.OKCANCEL,
              icon: Ext.MessageBox.WARNING,
              fn: function(btn, text){
		   	     if(btn == "ok"){
		   	 	   this.processRemove(panel);
		   	     }
		      },
		      scope:this
              
        }) 
    },
    
    processRemove:function(panel){
    	var data = panel.moduleId
    	var cmd = "remove"
    	if(this.roleModule){
    		cmd = "saveModule"   		
    		data = this.filterRoleModules(panel.moduleId)
    	}
    	
    	this.portal.el.mask("在正删除数据...")
		util.rmi.jsonRequest({
				serviceId: this.serviceId,
				method: cmd, //'remove',
				body : [data]
			},
			function(code,msg,json){
				this.portal.el.unmask()
				if(code < 300){
		            var pc = panel.ownerCt
		            this.clearPortlet(pc)
		            var m = this.pModules[this.cmd]
		            if(m){
			            m.parseViewData({moduleId:panel.moduleId},"0")
		            }			
				}else{
					Ext.MessageBox.alert("错误",re.msg)
				}
		},this)   
    },
    
    clearPortlet:function(pc){
    	var portlet = pc.items.item(0)
		pc.remove(portlet,true)   //portletColumn
		var portlet = this.emptyPortlet()
        pc.add(portlet)
		pc.doLayout()
		this.clearCacheModules()
		delete portlet
		//刷新页数
		this.refreshPage()
		this.roleModule = false
		
    },
	
	refresh:function(){
		this.loadData()	
	},
	
	loadData:function(){
		var ms = this.portletModules
		for(var m in ms){
			if(ms[m] && ms[m].loadData){
				ms[m].loadData()
			}
		}
	},
	
	loadModuleCfg:function(id){
		var result = util.rmi.miniJsonRequestSync({
			url:'app/loadModule',
			id: id
		})
		if(result.code != 200){
			if(result.msg == "NotLogon"){
				this.mainApp.logon(this.loadModuleCfg,this,[id])
			}
			return null;
		}
		var m = result.json.body
		Ext.apply(m, m.properties)
		return m
	},
	
	createModule:function(mod){
		var m = ""
	    if(mod && mod.script){
		    $import(mod.script)
		    Ext.apply(mod,{
			    enableCnd:false,
				autoLoadSchema:false,
				isCombined:true,
				disablePagingTbr:true,
				showButtonOnTop:true,
				actions : []   //去掉按钮
				
			})
			m = eval("new "+mod.script+"(mod)")
			m.setMainApp(this.mainApp)
	    }
	    return m
	},
	
	clearCacheModules:function(){
	    this.modules = ""
	},
	
	getAllModuleId:function(){
		if(this.modules){
		    return this.modules
		}
		var modules = ""
		if(this.mainApp){
		    var uid = this.mainApp.uid
		    var re = util.rmi.miniJsonRequestSync({
				serviceId:"platform.simpleQuery",
				method:"execute",
				schema:this.entryName,
				cnd:['and',['eq',['$','userId'],['s',uid]],['eq',['$','appId'],['s',this.appId]]]
			})
			if(re.code == 200){
			    modules = re.json.body
			    //数据库查询为空, 则使用角色的配置module
			    if(!this.isAppWel && modules.length == 0){
			    	modules = this.findRoleModules()
			    }
			    this.modules = modules
			}else{
			    Ext.MessageBox.alert("错误",re.msg)
			}			
		}
		return modules
	},

	doNew:function(button){
		var portlet = button.ownerCt.ownerCt
		var pc = portlet.ownerCt     //portletColumn
			
		var cls = "app.modules.config.homePage.WelcomeConfig"
		var m = this.pModules[this.cmd]
		if(!m){
			var cfg = {
			   appId : this.appId,
			   entryName : this.entryName
			}
			$import(cls)
			m = eval("new " + cls + "(cfg)")
			m.setMainApp(this.mainApp)
			m.opener = this
			m.on("add",this.addNewPortlet,this)
			m.on("remove",this.clearPortlet,this)
			this.pModules[this.cmd] = m
			var p = m.initPanel()
			if(!p){
			   return
			}
		}
		m.pcol = pc
		m.roleModule = this.roleModule ? this.modules : []
		var win = m.getWin()
		if(win){			
		    win.show()
		    if(!win.hidden){
		    	m.loadData()
		    }
		}
	
	},
	
	doLaySave:function(){
		if(this.portal && this.portal.el){
			this.portal.el.mask("在正保存数据...","x-mask-loading")
		}
		var data = {
		   id: this.mainApp.uid,
		   pageCount: this.count
		}
		var re = util.rmi.miniJsonRequestSync({
			serviceId:this.saveServiceId,
			op:"changeValue",
			method:"execute",
			body:data
		})
		if(this.portal && this.portal.el){
			this.portal.el.unmask()
		}
		if(re.code == 200){
		    this.current = this.count
		    this.changbut.setVisible(false)
		}else{
			if(re.msg == "NotLogon"){
				this.mainApp.logon(this.doLaySave,this,[])
				return
			}
		    Ext.MessageBox.alert("错误",re.msg)
		}
		
	},
	
	initCount:function(){
		if(this.count == 0){
			this.count = 4
		}
		if(this.count == 2 || this.count == 1){
			this.rowCount = 1
		}
		this.current = this.count   //当前数量
	},
	
	openWin:function(module,xy){
		if(module){
			var win = module.getWin()
			if(xy){
				win.setPosition(xy[0],xy[1])
			}
			win.setTitle(module.title)
			win.show()
		}
	},
	
	maxPanel:function(e, target, panel){
		if(!panel.moduleId){
			return
		}
		var mCfg = this.loadModuleCfg(panel.moduleId)
		if(!mCfg){
			return
		}
		var cls = "app.modules.config.homePage.MaximizePortlet"
		var m = this.maximizePortlet
		if(!m){
			var cfg = {}
			$import(cls)
			m = eval("new " + cls + "(cfg)")
			m.setMainApp(this.mainApp)
			m.opener = this
			this.maximizePortlet = m
			var p = m.initPanel()
			if(!p){
			   return
			}
		}
		var win = m.getWin()
		if(win){			
		    win.show()
		    if(!win.hidden){
		    	m.loadData(mCfg)
		    	win.setTitle(mCfg.name)
		    }
		}		
	},
	
	findRoleModules:function(){
		var re = util.rmi.miniJsonRequestSync({
			serviceId:this.serviceId,
			method:'findRoleModule'
		})
		if(re.code == 200){
			var ms = re.json.body
			if(ms.length > 0){
				for(var i=0; i<ms.length; i++){
					var m = ms[i]
					m.appId = this.appId
				}
				this.roleModule = true
				return ms
			}			
		}
		return []
	},
	
	filterRoleModules:function(moduleId){
		for(var i=0; i<this.modules.length; i++){			
		    var m = this.modules[i]
    		if(m.moduleId == moduleId){
    			this.modules.remove(m)
    			break
    		}
    	}
    	return this.modules
	}
		
})