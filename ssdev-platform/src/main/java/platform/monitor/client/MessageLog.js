$package("platform.monitor.client")
$import("app.desktop.Module","util.dictionary.TreeDicFactory","platform.monitor.client.MessageLogList")

platform.monitor.client.MessageLog = function(cfg) {
	this.width = 720
	this.height = 450
	this.title = "字典目录"
	this.dicId="platform.monitor.dic.messageLogDic"
	this.gridCls = "platform.monitor.client.MessageLogList"
	this.entryName="platform.monitor.schemas.SYS_MessageLog"
	platform.monitor.client.MessageLog.superclass.constructor.apply(this, [cfg])
}

Ext.extend(platform.monitor.client.MessageLog, app.desktop.Module, {
	initPanel : function() {
		var grid = this.getMessageList();
		if (!grid) {
			return
		}
		this.reloadTree()
		var tree = util.dictionary.TreeDicFactory.createTree({id:this.dicId,keyNotUniquely:true})
		tree.autoScroll = true
		tree.on({
			click:this.onTreeClick,
			scope:this
		})
		tree.expandAll()
		this.tree = tree
		var panel = new Ext.Panel({
		    height : this.height,
			layout : "border",
			items:[
			   { layout:'fit',
			     title : "节点列表",
			     region:'west',
			     split:true,
		    	 collapsible:true,
			     width : 200,
			     items:[tree]
			   },{ 
			     layout:'fit',
			     region:'center',
			     //frame : true,
			     items:[grid]
			   }
			]
		})
		this.panel = panel	
		return panel	
	},
	onTreeClick:function(node,e){
		if(node.attributes.leaf){
			var ip=node.parentNode.attributes.key+"@"+node.attributes.key
			this.sendGrid.fresh(ip,true)
			this.receiveGrid.fresh(ip,true)
		}else{
			this.sendGrid.fresh(node.attributes.key,false)
			this.receiveGrid.fresh(node.attributes.key,false)
		}
	},
	getMessageList:function(){
		if(this.tabPanel){
			return this.tabPanel
		}
		var tabPanel = new Ext.TabPanel({
					title : 'TabPanel',
					activeTab :0,// 默认激活第一个Tab页
					animScroll : true,
					enableTabScroll : true,
					scrollRepeatInterval :100,
					items : [this.getSendGrid(),this.getReceiveGrid()]
		})
		this.tabPanel=tabPanel
		return this.tabPanel;
	},
	getSendGrid:function(){
		var cls = this.gridCls
		$import(cls)
		var cfg = {}		
		cfg.entryName = this.entryName
		cfg.autoLoadSchema = false
		cfg.autoLoadData=false
		cfg.initCnd=['isNull',['$','Receiver']]
		cfg.type="send"
		cfg.hideCol=['Receiver','ReceiveDate']
		var module = eval("new " + cls + "(cfg)");
		module.setMainApp(this.mainApp)
		module.opener = this
		lp = module.initPanel()		
		lp.setTitle("发送消息日志")
		this.sendGrid = module
		return lp
	},
	getReceiveGrid:function(){
		var cls = this.gridCls
		$import(cls)
		var cfg = {}		
		cfg.entryName = this.entryName
		cfg.autoLoadSchema = false
		cfg.initCnd=['notNull',['$','Receiver']]
		cfg.type="receive"
		cfg.hideCol=[]
		cfg.autoLoadData=false
		var module = eval("new " + cls + "(cfg)");
		module.setMainApp(this.mainApp)
		module.opener = this
		lp = module.initPanel()		
		lp.setTitle("接收消息日志")
		this.receiveGrid = module
		return lp
	},
	reloadTree:function(){
		var r = util.rmi.miniJsonRequestSync({
					serviceId:"dicConfig",
					method:"reloadSingleDic",
					body:{
						"dicId":this.dicId
					}
		})
	}
})