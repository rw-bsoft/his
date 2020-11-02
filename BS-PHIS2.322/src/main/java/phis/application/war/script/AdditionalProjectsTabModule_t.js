/**
 * 病区项目Tab 该JS类中主要加载页面上的"按项目提交"和"按病人提交"两个Tab页 并控制一些属性
 * 
 * @author : gejj
 */
$package("phis.application.war.script");

$import("phis.script.TabModule");
phis.application.war.script.AdditionalProjectsTabModule_t = function(cfg) {
	this.westWidth = cfg.westWidth || 250;
	this.showNav = true;
	this.height = 450;
	phis.application.war.script.AdditionalProjectsTabModule_t.superclass.constructor
			.apply(this, [cfg]);
	this.on("tabchange",this.afterTabChange,this)
},

Ext.extend(phis.application.war.script.AdditionalProjectsTabModule_t,
		phis.script.TabModule, {
////			initPanel : function() {
////				if (this.tab) {
////					return this.tab;
////				}
////				var tabItems = [];
////				var actions = this.actions;
////				// 遍历Applications.xml中配置的Tab页，并将其添加到tabItems中
////				for (var i = 0; i < actions.length; i++) {
////					var ac = actions[i];
////					// 多个TAB同时加载
////					var exCfg = ac;
////					var cfg = {
////						showButtonOnTop : true,
////						autoLoadSchema : false,
////						isCombined : true
////					}
////					Ext.apply(cfg, exCfg);
////					var ref = exCfg.ref
////					if (ref) {
////						var body = this.loadModuleCfg(ref);
////						Ext.apply(cfg, body)
////					}
////					var cls = cfg.script;
////					if (!cls) {
////						return;
////					}
////					var m = this.createModule(ac.id, ac.ref);
////					if(this.initDataId){
////					  m.initDataId = this.initDataId;
////					}
////					var p = m.initPanel();
////					/** 2013-08-21 注释以下代码,用于修正页面报this.grid.el is undefined错误**/
//////					if (m.loadData) {
//////						m.loadData();
//////					}
////					/** end**/
////					tabItems.push({
////								layout : "fit",
////								title : ac.name,
////								exCfg : ac,
////								id : ac.id,
////								items : p
////							});
////				}
////				// 创建一个TabPanel，用于加载"按项目提交"和"按病人提交"两个Tab页
////				var tab = new Ext.TabPanel({
////							title : " ",
////							border : false,
////							width : this.width,// 设置宽度
////							frame : true,
////							defaults : {
////								border : false,
////								autoWidth : true
////							},
////							items : tabItems
////						});
////				// 添加Tab页改变后的监听事件处理方法
////			    tab.on("beforetabchange", this.beforetabchange, this);
////			    tab.on("tabchange", this.onTabChange, this);
////				tab.activate(this.activateId)
////				this.tab = tab;
////				return tab;
////			},
//			/**
//			 * Tab页改变后事件处理方法
//			 * 
//			 * @param tabPanel
//			 * @param newTab
//			 * @param curTab
//			 */
//			 beforetabchange : function(tabPanel, newTab, curTab){//为今后业务变化做扩展
//			   this.fireEvent("beforetabchange",newTab.id);
//			 },
			 afterTabChange:function(tabPanel, newTab, curTab){
			 	this.midiModules[newTab.id].doRefresh()
			 }
		});