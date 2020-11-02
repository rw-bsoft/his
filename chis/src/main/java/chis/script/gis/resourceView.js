$package("chis.script.gis");
$import("app.desktop.Module", "util.rmi.jsonRequest","app.biz.report.ComprehensiveReportListView",
		"util.dictionary.DictionaryLoader", "org.ext.ux.TabCloseMenu","app.modules.form.SimpleFormView",
		"chis.script.gis.simpleResource","app.biz.report.ComprehensiveReportForm");
chis.script.gis.resourceView = function(cfg) {
	this.width = 800;
	this.height = 500;
	this.pModules = {};
	this.activeModules = {};
	chis.script.gis.resourceView.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.script.gis.resourceView, app.desktop.Module, {
			initPanel : function() {
				var panel = new Ext.Panel({
							height : this.height,
							width : this.width,
							title : this.title,
							layout : "border",
							items : this.getResourceModule()
						});
				this.panel = panel;
				return panel;
			},
			
			getResourceModule:function(){
				var items=[{
							viewType : "gis",
							layout : "fit",
							title : "地图展示",
							exCfg : {
							script : "chis.script.gis.integratedMapView"
							}
						},{
							viewType : "gis",
							layout : "fit",
							title : "列表展示",
							exCfg : {
							entryName:"EHR_ComprehensiveReport",
							navDic:"manageUnit",
							navField:"manaUnitId",
							title : "列表展示",
							navParentKey:this.mainApp.manaUnitId,
							rootVisible:true,
							createCls:"app.biz.report.ComprehensiveReportForm",
							updateCls:"app.biz.report.ComprehensiveReportForm",
							script : "app.biz.report.ComprehensiveReportListView",
							actions:[{id:"create",name:"新建"},
									{id:"update",name:"修改"},
									{id:"remove",name:"删除"}]
							}
						}]
				var p02=new app.biz.report.ComprehensiveReportListView({
							entryName:"EHR_ComprehensiveReport",
							navDic:"manageUnit",
							navField:"manaUnitId",
							title : "列表展示",
							navParentKey:this.mainApp.manaUnitId,
							rootVisible:true,
							createCls:"app.biz.report.ComprehensiveReportForm",
							updateCls:"app.biz.report.ComprehensiveReportForm"
				})
				var tabpanel = new Ext.TabPanel({
					border : true,
					bodyBorder : true,
					region : 'center',
					activeTab : 0,
					tabPosition : 'bottom',
					items : items
				});
				//tabpanel.add(p02.initPanel())
				tabpanel.on("tabchange", this.onTabChange, this);
				this.mainTab = tabpanel
				return tabpanel;
				},
				
		getModuleCfg : function(key) {
				var dic = this.ehrNavDic
				if (!dic) {
					dic = util.dictionary.DictionaryLoader.load({
						id : 'ehrViewNav'
							})
					this.ehrNavDic = dic;

				}
				if (!dic) {
					return {};
				}
				var n = dic.wraper[key]

				var cfg = {
					closable : this.initTabClosable,
					frame : true,
					mKey : key,
					layout : "fit"
				}
				if (n) {
					Ext.apply(cfg, n)
					cfg.title = n.text;
				}
				return cfg;
		},
				
		onTabChange : function(tabPanel, newTab, curTab) {
				var viewType = newTab.viewType
				if (newTab.__inited) {
					var m = this.midiModules[viewType]
					if (m && m.refresh) {
						m.refresh()
					}
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var result = util.rmi.miniJsonRequestSync({
						serviceId : "moduleConfigLocator",
						id : ref
					})
					if (result.code == 200) {
						Ext.apply(cfg, result.json.body)
					}
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}
				this.panel.el.mask("加载中...", "x-mask-loading");
				$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)")
							m.setMainApp(this.mainApp)
							// m.mainTab=this.mainTab;
							this.midiModules[viewType] = m;
							var p = m.initPanel();
							newTab.add(p);
							newTab.__inited = true
							if (viewType == "list")
								this.doCndQuery(viewType);
							this.mainTab.doLayout()
							this.panel.el.unmask();
						}, this])
			}
		});
