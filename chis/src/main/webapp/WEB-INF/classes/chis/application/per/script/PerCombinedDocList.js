$package("chis.application.per.script");
$import("chis.script.CombinedDocList");
chis.application.per.script.PerCombinedDocList = function(cfg) {
	cfg.frame = false
	chis.application.per.script.PerCombinedDocList.superclass.constructor.apply(this, [cfg]);

};
Ext.extend(chis.application.per.script.PerCombinedDocList, chis.script.CombinedDocList, {

			createNavTab : function() {

				var manageUnit = this.mainApp.deptId;
				var manageUnitText = this.mainApp.dept;
				if (manageUnit && manageUnit.length >= 9) {
					manageUnit = manageUnit.substring(0, 9);
				}

				var manageUnitDicTree = util.dictionary.TreeDicFactory
						.createTree({
									id : "chis.@manageUnit",
									parentKey : manageUnit,
									parentText : manageUnitText,
									rootVisible : true
								});
				this.manageUnitDicTree = manageUnitDicTree;
				manageUnitDicTree.title = "管辖机构";
				// manageUnitDicTree.on("click",this.onManageUnitTreeSelect,this)
				manageUnitDicTree.on("contextmenu",
						this.onManageUnitTreeContextmenu, this);

				// @@ 只有一个页签，直接使用面板。
				var panel = new Ext.Panel({
							// activeTab:0,
							// tabPosition: 'bottom',
							border : false,
							frame : false,
							layout : "fit",
							width : this.width,
							height : this.height,
							items : [manageUnitDicTree]
						});
				this.navTab = panel;
				return panel;
			},
			createListTab : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = [];
				var actions = this.actions;
				var overriders = {
					"list" : false,
					"areaGrid" : false,
					"gis" : false
				};
				if (actions) {
					for (var i = 0; i < actions.length; i++) {
						var ac = actions[i];
						// @@ added by chinnsii,2010-09-07,使得可以从外面传递一个初始查询条件
						ac.cnds = this.cnds;
						var vt = ac.viewType;
						overriders[vt] = true;
						tabItems.push({
									viewType : vt,
									layout : "fit",
									title : ac.name,
									exCfg : ac
								});
					}
				}

				var defaultCfg = [{
							viewType : "list",
							layout : "fit",
							title : "列表视图",
							exCfg : {
								script : "app.modules.list.SimpleListView",
								entryName : this.entryName,
								cnds : this.cnds
							}
						}];

				var cfg = defaultCfg[0];
				var vt = cfg.viewType;
				if (!overriders[vt]) {
					tabItems.push(cfg);
				}

				var listTab = new Ext.TabPanel({
							activeTab : 0,
							deferredRender : false,
							tabPosition : 'bottom',
							items : tabItems
						});
				listTab.on("tabchange", this.onListTabChange, this);
				this.listTab = listTab;
				return listTab;
			}

		});