/**
 * 组合模块公共页面，支持普通Module与tabModule"左右"/"上下" 布局格式的组合，
 * 
 * @author : yaozh
 */
$package("chis.script")
$import("chis.script.BizTabModule")
chis.script.BizCombinedTabModule = function(cfg) {
	this.layOutRegion = "west" // ** " west" or "north"
	this.itemWidth = 255 // ** 第一个Item的宽度
	this.itemHeight = 800 // ** 第一个Item的高度
	this.itemCollapsible = true // ** 是否可伸缩
	this.oneCombinedRef = cfg.oneCombinedRef // ** 除tab外另外需要组合的模块编号
	chis.script.BizCombinedTabModule.superclass.constructor.apply(this, [cfg])

}
Ext.extend(chis.script.BizCombinedTabModule, chis.script.BizTabModule, {

			/**
			 * 初始化面板
			 * 
			 * @return {} panel
			 */
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var actions = this.groupActionsByTabType();
				this.actions = actions.tab;
				this.otherActions = actions.other;
				var tab = chis.script.BizCombinedTabModule.superclass.initPanel
						.call(this);
				this.tab = tab;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							items : [{
										layout : "fit",
										split : true,
										title : '',
										region : this.layOutRegion,
										width : this.itemWidth || this.width,
										height : this.itemHeight || this.height,
										collapsible : this.itemCollapsible,
										items : this.getCombinedItem()
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										items : tab
									}]
						});
				this.panel = panel;
				return panel;
			},

			/**
			 * 获取被组合模块
			 * 
			 * @return {}
			 */
			getCombinedItem : function() {
				return this.getCombinedModule(this.otherActions.id,
						this.otherActions.ref);
			},

			loadData : function() {

				if (!this.otherActions) {
					return;
				}
				var m = this.midiModules[this.otherActions.id];
				this.refreshExContextData(m, this.exContext);
				m.loadData();
			},
			keyManageFunc : function(keyCode, keyName) {
				for (var i = 0; i < this.actions.length; i++) {
					var m = this.midiModules[this.actions[i].id];
					if (this.tab.activeTab.name != this.actions[i].id) {
						continue;
					}
					if (m) {
						if (m.keyManageFunc) {
							m.keyManageFunc(keyCode, keyName)
						} else {
							if (m.btnAccessKeys) {
								var btn = m.btnAccessKeys[keyCode];
								if (btn && btn.disabled) {
									continue;
								}
							}
							m.doAction(m.btnAccessKeys[keyCode]);
						}
					}
				}
			},

			/**
			 * 根据类型过滤actions
			 * 
			 * @param {}
			 *            actionType
			 * @return {}
			 */
			filterActionsByType : function(actionType) {
				var actions = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					if (type == actionType) {
						actions.push(action);
					}
				}
				return actions;
			},

			/**
			 * actions分组
			 * 
			 * @return {}
			 */
			groupActionsByTabType : function() {
				var tabAction = [];
				var otherAction = [];
				for (var i = 0; i < this.actions.length; i++) {
					var action = this.actions[i];
					var type = action.type;
					if (type == "tab") {
						tabAction.push(action);
					} else {
						otherAction.push(action);
					}
				}
				return {
					"tab" : tabAction,
					"other" : otherAction[0]
				}
			}

		})