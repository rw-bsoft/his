$package("chis.script")

$import(
	"app.modules.list.IconListView",
	"util.dictionary.DictionaryLoader",
	"util.rmi.miniJsonRequestSync"
)
chis.script.AreaGridView = function(cfg){
	this.dicId = "chis.dictionary.areaGrid";
	Ext.apply(this, app.modules.common)
	chis.script.AreaGridView.superclass.constructor.apply(this,[cfg])
	this.dispProps = [
		'text',
		{name:"jkdas",label:"健康档案数"},
		{name:"jtdas",label:"家庭档案数"},
		{name:"gxyrs",label:"高血压人数"},
		{name:"tnbrs",label:"糖尿病人数"}
//		{name:"zlrs",label:"肿瘤人数"}
	]
	
}
Ext.extend(chis.script.AreaGridView, app.modules.list.IconListView,{
	onReady:function(){
		this.RecordTemplate = Ext.data.Record.create(this.dispProps)
		if(!this.parentKey){
			this.parentKey = this.mainApp.regionCode
		}
		this.loadDicItem(this.parentKey)
	},
	onIconDblClick:function(view,index,node,e){
		var o = this.jsonData[index]		
		this.parentKey = o.key
		if(o.isBottom == "y"){
			var m = this.midiModules["familyMembersView"]
			var cfg = {
				autoLoadSchema:false,
				title:o.text,
				regionCode:this.parentKey,
				entryName:this.entryName
			}
			if(!m){
				var cls = "chis.script.FamilyMembersView";
				$import(cls);
				$require(cls,[function(){
					m = eval("new "+cls+"(cfg)");
					this.midiModules["familyMembersView"] = m;
					m.opener = this;
					m.setMainApp(this.mainApp);
					var w=m.getWin();
					w.setPosition(100,50);
					m.getWin().show();
				},this])
			}else{
				Ext.apply(m,cfg);
				m.getWin().setTitle(o.text);
				m.refresh();
				m.getWin().show();	
			}			
		}else
			this.loadDicItem(this.parentKey)
	},
	loadDicItem:function(parentKey){
		if(this.panel.el.isMasked()){
			return
		}
		var loader = util.dictionary.DictionaryLoader
		this.panel.el.mask("正在加载数据...","x-mask-loading")
		loader.require({id:this.dicId,parentKey:parentKey,sliceType:3},this.onDicItemLoad,this,true)
	},
	onDicItemLoad:function(o){
		var items = o.items
		var RecordTemplate = this.RecordTemplate
		var store = this.store
//		if(items.length == 0){
//			this.panel.el.unmask()
//			return;
//		}
		store.removeAll()
		util.rmi.jsonRequest({
				serviceId:"chis.simpleGrid",
				method:"execute",
				parentKey:this.parentKey,
				items:items
			},function(code,msg,json){
				if(code == 200){
					var _items = json.body
					if(_items){
						for(var i = 0; i < _items.length; i ++){
							store.add(new RecordTemplate(_items[i]))
						}
					}
					this.jsonData = _items;
				}else{
					this.processReturnMsg(code, msg, this.onDicItemLoad, o);
				}
				this.panel.el.unmask()
			},this)
		
	},
	refresh:function(){
		this.loadDicItem(this.parentKey)
	},
	loadData:function(){
		
	}
});