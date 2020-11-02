/**
 * 病区项目Tab
 *     该JS类中主要加载页面上的"按项目提交"和"按病人提交"两个Tab页
 *     并控制一些属性
 * @author : gejj
 */
$package("phis.application.med.script");

$import("phis.script.TabModule");
phis.application.med.script.WardProjectTabModule = function(cfg) {
	this.westWidth = cfg.westWidth || 250;
	this.showNav = true;
	this.height = 450;
	var oldTab = "";
	phis.application.med.script.WardProjectTabModule.superclass.constructor.apply(
			this, [cfg]);
},

Ext.extend(phis.application.med.script.WardProjectTabModule,
		phis.script.TabModule, {
	initPanel : function() {
		if (this.tab) {
			return this.tab;
		}
		var tabItems = [];
		var actions = this.actions;
		//遍历Applications.xml中配置的Tab页，并将其添加到tabItems中
		for (var i = 0; i < actions.length; i++) {
			var ac = actions[i];
			tabItems.push({
						layout : "fit",
						title : ac.name,
						exCfg : ac,
						id : ac.id
					});
		}
		//创建一个TabPanel，用于加载"按项目提交"和"按病人提交"两个Tab页
		var tab = new Ext.TabPanel({
					title : " ",
					border : false,
					width : this.width,//设置宽度
					activeTab : 0,//加载第1项标签页
					frame : true,
					defaults : {
						border : false,
						autoWidth : true
					},
					items : tabItems
				});
		//添加Tab页改变后的监听事件处理方法
		tab.on("beforetabchange", this.beforetabchange, this);
		this.tab = tab;
		return tab;
	},
	/**
	 * Tab页改变后事件处理方法
	 * @param tabPanel
	 * @param newTab
	 * @param curTab
	 */
	beforetabchange : function(tabPanel, newTab, curTab){//为今后业务变化做扩展
		this.fireEvent("beforetabchange",newTab.id);
		//该方法调用的是TabModule类中的onTabChange方法,用于重新加载选择的Tab页
		this.onTabChange(tabPanel, newTab, curTab);
	}
});