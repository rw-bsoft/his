$package("platform.dataset.client")

$import("app.lang.UIModule",
	"util.dictionary.SimpleDicFactory",
	"platform.dataset.client.DataElementView",
	"platform.dataset.client.DataSetView",
	"platform.dataset.client.DataDicView",
	"app.modules.list.SimpleListView",
	"app.modules.list.SimpleListView")

platform.dataset.client.DataMain = function(cfg){
	platform.dataset.client.DataMain.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(platform.dataset.client.DataMain, app.lang.UIModule, {
	resDataStandard:null,
	initPanel:function(){
		var comb = this.initDataStandardComb();
		var tabItems = []
		var actions = this.actions
		for ( var i = 0; i < actions.length; i++) {
			var ac = actions[i];
			tabItems.push({
				layout : "fit",
				title : ac.name,
				exCfg : ac,
				name : ac.id
			})
		}
		
		var tabPanel = new Ext.TabPanel({
			activeTab:0,
			resizeTabs:true,
			minTabWidth:60,
//			deferredRender:false,
			tabWidth:80,
			items:tabItems
		});
		tabPanel.on("beforetabchange",this.onBeforeTabChange,this);
		this.tabPanel = tabPanel;
		var panel = new Ext.Panel({
			tbar:['数据标准:',comb,{
				iconCls:"add",
				handler:this.showDataStandardList,
				scope:this
			}],
			layout:"fit",
			items:[tabPanel]
		})
		tabPanel.on("tabchange", this.onTabChange, this);
		this.panel = panel;
		return panel;
	},
	
	onTabChange : function(tabPanel, newTab, curTab) {
		if (newTab.__inited) {
			return;
		}
		var exCfg = newTab.exCfg
		var cfg = {
			resDataStandard:this.resDataStandard
		}
		Ext.apply(cfg, exCfg);
		var ref = exCfg.ref
		if (ref) {
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(ref);
			if(moduleCfg.json&&moduleCfg.json.body){
				Ext.apply(cfg, moduleCfg.json.body);
				Ext.apply(cfg, moduleCfg.json.body.properties);
			}else{
				Ext.apply(cfg, moduleCfg);
				Ext.apply(cfg, moduleCfg.properties);
			}
		}
		var cls = cfg.script
		if (!cls) {
			return;
		}

		if (!this.fireEvent("beforeload", cfg)) {
			return;
		}
		$require(cls, [ function() {
			var m = eval("new " + cls + "(cfg)")
			this.midiModules[newTab.name] = m;
			var p = m.initPanel();
			m.on("save", this.onSuperFormRefresh, this)
			newTab.add(p);
			newTab._module=m
			newTab.__inited = true
			newTab.doLayout()
		}, this ])

	},

	onBeforeTabChange:function(tab,np,cp){
		var m = np._module;
		if(m){
			if(!np.rendered){
				m.resDataStandard = this.resDataStandard;
				if(m instanceof platform.dataset.client.DataDicView){
					m.dicId = "platform.dataset.dic.res."+this.resDataStandard;
				}
				return;
			}
			if(this.resDataStandard != m.resDataStandard){
				m.resDataStandard = this.resDataStandard;
				m.reset();
			}
		}
	},
	initDataStandardComb:function(){
		var dataStandardDicId = "platform.dataset.dic.resDataStandard";
		var comb = util.dictionary.SimpleDicFactory.createDic({
			id:dataStandardDicId,
			emptyText:"请选择数据标准..",
			width:200,
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
				var initCnd = ['eq',['$','DataStandardId'],['s',this.resDataStandard]];
//				this.dev.initCnd = initCnd;
//				this.dsv.initCnd = initCnd;
//				this.ddv.initCnd = initCnd;
				var m = this.tabPanel.getActiveTab()._module;
				if(this.resDataStandard != m.resDataStandard){
					m.resDataStandard = this.resDataStandard;
					m.reset();
				}
			},
			scope:this
		});
		this.dataStandardComb = comb;
		return comb;
	},
	showDataStandardList:function(btn){
		if(!this.dsl){
			var dsl = new app.modules.list.SimpleListView({
				title:"数据标准管理",
				entryName:"platform.dataset.schemas.RES_DataStandard",
				saveServiceId:"dataStandardSave",
				removeServiceId:"dataStandardRemove",
				showButtonOnTop:true,
				width:750,
				actions:[
					{id:"read",name:"查看"},
					{id:"create",name:"新建"},
					{id:"update",name:"修改"}
//					,{id:"remove",name:"删除"}
				]
			});
			dsl.on({
				save:this.doRefresh,
				remove:this.doRefresh,
				scope:this
			});
			this.dsl = dsl;
		}
		var win = this.dsl.getWin();
		win.setPosition(200,100);
		win.modal = true;
		win.show(btn.el);
	},
	doRefresh:function(){
		this.dataStandardComb.store.reload();
	}
})