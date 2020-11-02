$package("chis.script.viewport")
$import(
			 "org.ext.ux.portal.Portal",
			 "app.modules.list.SimpleListView", 
			 "app.modules.chart.DiggerChartView"
			 )
chis.script.viewport.WelcomePortal = function(cfg) {
	this.portlets = {}
	this.portletModules = {}
	this.colCount = 2;
	this.rowCount = 3;
	this.title = "我的首页"
	chis.script.viewport.WelcomePortal.superclass.constructor.apply(this,[cfg])
}
Ext.extend(chis.script.viewport.WelcomePortal, app.desktop.Module, {
	initPanel : function() {
		if(this.portal){
		    return this.portal;
		}
		var myPage = this.myPage
		this.modules = myPage.items
		if(this.modules){
			this.colCount = 3//Math.ceil(this.modules.length/2.0)
		}
		var portlets = this.portlets;
		var cfg = {};
		cfg.items = [];
		cfg.bodyBorder = false;
		//cfg.title = this.title;
		cfg.renderTo = '_index';
		this.initPortlets();
		for (var i = 0; i < this.colCount; i++) {
			var columnWidth = 0.2;
			if(i == 1){
				columnWidth = 0.5;
			}
			if(i == 2){
				columnWidth = 0.3;
			}
			var column = {
				columnWidth : columnWidth,//1/this.rowCount,
				style : 'padding:5px 5px 0px 5px',
				items : []
			}
			cfg.items.push(column)
			for (var j = 0; j < this.rowCount; j++) {
				var index = i + "." + j
				var p = portlets[index]
				if(p){
					column.items.push(p)
				}
			}
		}
		this.portal = new Ext.ux.Portal(cfg)
		return this.portal;
	},
	initPortlets:function(){
		var modules = this.modules
		if(!modules){
			return
		}
		var portlets = this.portlets
		var col = 0
		var row = 0
		for(var i=0;i<modules.length;i++){
			var mod = modules[i]
			if(mod.script){
				$import(mod.script)
				Ext.apply(mod,{
					enableCnd:false,
					autoLoadSchema:false,
					isCombined:true,
					disablePagingTbr:true,
					showCndsBar:false,
					mainApp : this.mainApp
				});
				if(mod.properties){
				   Ext.apply(mod, mod.properties);
				}
				var m = eval("new "+mod.script+"(mod)");
				m.on("openWin", this.onOpenWin, this);
				var p = this.getPortlet(m);
				if(col>=this.colCount){
					col = 0
					row++
				}
				portlets[col+"."+row] = p
				this.portletModules[col+"."+row] = m
				col++
			}			
		}
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
	getPortlet:function(module){
		var backgroundUrl = "height:19px; line-height:19px;padding-left:12px;font-weight:bold;";
		var bgUrl = "";
		if(module.id == 'MYPAGE03' || module.id == 'MYPAGE06'){
			bgUrl = "background:url("+ClassLoader.stylesheetHome+"chis/resources/app/desktop/images/homepage/nav_bg_yellow.gif) repeat-x 0 0;color:#fff;";
		}else{
			bgUrl = "background:url("+ClassLoader.stylesheetHome+"chis/resources/app/desktop/images/homepage/colum_bg.gif) repeat-x left top;color:#000;";
		}
		backgroundUrl = bgUrl+backgroundUrl;
		var p =  module.initPanel();
		p.frame = false;
		p.border = false;
		return new Ext.ux.Portlet({
			title : module.name,
			pkey : module.id,
			border:false,
			frame:false,
			collapsible:false,  //去掉伸缩
			draggable:false, //拖拽
			headerStyle:backgroundUrl,
			style:'margin-bottom:5px;border: 1px solid #CCCCCC;margin-bottom: 5px',
			height : 250,
			width : '110%',
			layout : 'fit',
			items : p
		})
	},
	onOpenWin : function(lo,needRefresh,replaceCnd,initCnd) {
		this.fireEvent("openWin", lo,needRefresh,replaceCnd,initCnd);
	}
})