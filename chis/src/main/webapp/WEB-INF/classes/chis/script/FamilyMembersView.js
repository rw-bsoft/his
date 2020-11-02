$package("chis.script")

$import(
	"app.modules.list.IconListView",
	"util.dictionary.DictionaryLoader",
	"util.rmi.miniJsonRequestSync"
)
$styleSheet("chis.resources.app.modules.list.IconListView")
chis.script.FamilyMembersView = function(cfg){
	chis.script.FamilyMembersView.superclass.constructor.apply(this,[cfg])
	this.width = 816
	this.height = 400
	this.dispProps = [
		'PERSONNAME',
		'PHOTO'
	]
	
}
Ext.extend(chis.script.FamilyMembersView, app.modules.list.IconListView,{
	onReady:function(){
		this.RecordTemplate = Ext.data.Record.create(this.dispProps)
		if(this.regionCode){
			this.loadData()
		}		
	},
	onIconDblClick:function(view,index,node,e){
		var r = view.getStore().getAt(index);
		var empiId = r.get("EMPIID")
		var m = this.midiModules["ehrview"]
		if(!m){
			$import("chis.script.EHRView")
			m = new chis.script.EHRView({
				initModules : ['B_01'
//				, 'B_02', 'B_03', 'B_04','B_05'
				],
				empiId : empiId,
				closeNav : true,
				mainApp : this.mainApp
			})
			this.midiModules["ehrview"] = m
		}else{
			m.exContext.ids = {}
			m.actionName = "EHR_HealthRecord"
			m.exContext.ids["empiId"] = empiId
			m.refresh();
		}
		m.getWin().show()
	},
	createTemplate:function(){
    	var tpl = this.template
    	if(!tpl){
    		var props = this.dispProps;
    		var propsHtml = "";
    		var photoUrl = ClassLoader.appRootOffsetPath + "photo/{PHOTO}";
    		propsHtml = "<img src='"+ photoUrl + "' width='110' height='120' />"
    		tpl =  new Ext.XTemplate(
				'<tpl for=".">',
		            '<div class="x-icon-wrap-photo">',
				    	'<span>{PERSONNAME}</span>',
				    	'<div class="x-icon-body">',
		            	propsHtml,
		            	'</div>',
				    '</div>',
		        '</tpl>'
			);
			this.template = tpl;
    	}
    	return tpl;
	},
	createDataView:function(){
		var view = new Ext.DataView({
	            store: this.createStore(),
	            tpl: this.createTemplate(),
	            singleSelect: true,
	            //autoScroll:true,
	            style : "overflow-x: hidden;overflow-y: auto;",
	            cls:"x-icon-list-view-photo",
	            overClass:'x-icon-over-photo',
	            selectedClass:"x-icon-selected-photo",
	            itemSelector:'div.x-icon-wrap-photo'
	       })  
	     view.on("click",this.onIconClick,this)
	     view.on("dblclick",this.onIconDblClick,this)
	     view.on("containercontextmenu",this.onContainerCtxMenu,this)
	     view.on("contextmenu",this.onIconCtxMenu ,this)
	     this.view = view
	     return view;
	},
	refresh:function(){
		this.loadData()
	},
	loadData:function(){
		if(!this.regionCode){
			return
		}
		var RecordTemplate = this.RecordTemplate
		var store = this.store
		store.removeAll()
		util.rmi.jsonRequest({
				serviceId:"chis.customQuery",
				serviceAction:"findPhoto",
				method:"execute",
				regionCode:this.regionCode
			},function(code,msg,json){
				if(code == 200){
					var data = json.body;
					for(var i=0;i<data.length;i++){
						if(data[i]["MASTERFLAG"] && data[i]["MASTERFLAG"]=="y"){
							data[i]["PERSONNAME"] = "<span style='color:blue'><b>"+data[i]["PERSONNAME"]+"</b>(户主)</span>"
						}
						store.add(new RecordTemplate(data[i]))
					}
				}
			},this)
	},
	getWin: function(){
		var win = this.win
		closeAction = "hide"
		if(!win){
			win = new Ext.Window({
				id: this.id,
		        title: this.title,
		        width: this.width,
		        iconCls: 'icon-grid',
		        shim:true,
		        layout:"fit",
		        animCollapse:true,
		        closeAction:closeAction,
		        constrainHeader:true,
		        minimizable: true,
		        constrain:true,
		        maximizable: true,
		        shadow:false,
		        items:this.initPanel()
            })
		    var renderToEl = this.getRenderToEl()
            if(renderToEl){
            	win.render(renderToEl)
            }
			win.on("add",function(){
				this.win.doLayout()
			},this)
			win.on("close",function(){
				this.fireEvent("close",this)
			},this)
			this.win = win
		}
		win.instance = this;
		return win;
	}
});