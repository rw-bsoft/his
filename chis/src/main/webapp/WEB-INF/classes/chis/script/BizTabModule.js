/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("app.modules.combined.TabModule", "chis.script.BizCommon")

chis.script.BizTabModule = function(cfg) {
	Ext.apply(cfg, chis.script.BizCommon);
	chis.script.BizTabModule.superclass.constructor.apply(this, [cfg])
	this.on("close", this.onClose, this)
}

Ext.extend(chis.script.BizTabModule, app.modules.combined.TabModule, {
			initPanel : function() {
				if (this.tab) {
					return this.tab;
				}
				var tabItems = []
				var actions = this.filterActions(this.actions);
				for ( var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					tabItems.push({
						layout : "fit",
						title : ac.name,
						exCfg : ac,
						name : ac.id
					})
				}
				var cfg = {
					title : " ",
					border : false,
					width : this.width,
					activeTab : 0,
					frame : true,
					autoHeight : true,
					defaults : {
						border : false,
						autoHeight : true,
						autoWidth : true
					},
					items : tabItems
				};
		
				this.changeCfg(cfg);
				var tab = new Ext.TabPanel(cfg);
				tab.on("tabchange", this.onTabChange, this);
				tab.on("afterrender", this.onReady, this)
				tab.activate(this.activateId)
				this.tab = tab
				return tab;
			},
			filterActions : function(actions){
				if(!actions){
					actions = this.actions;
				}
				return actions
			},
			/**
			 * 面板切换事件
			 * 
			 * 
			 * @param {}
			 *            tabPanel
			 * @param {}
			 *            newTab
			 * @param {}
			 *            curTab
			 */
			onTabChange : function(tabPanel, newTab, curTab) {
				if (!newTab) {
					return;
				}
				// ** 模块是否已经激活过，即已经加载过数据，若为true则不再往下执行
				if (newTab.__actived) {
					return;
				}

				// ** 模块是否已经初始化过，即是否已经构建过，若为true则刷新页面，fase则构建页面
				if (newTab.__inited) {
					// **面板刷新时可捕获此事件，改变面板。
					this.fireEvent("refreshInitedModule", newTab);
					this.refreshModule(newTab);
					return;
				}
				var p = this.getCombinedModule(newTab.name, newTab.exCfg.ref);
				newTab.add(p);
				newTab.__inited = true;
				this.tab.doLayout()
				var m = this.midiModules[newTab.name];
				// **面板加载完成，可捕获此事件，完善面板，如增加模块的事件捕捉
				this.fireEvent("loadModule", newTab.name, m);
				if (m.loadData && m.autoLoadData == false) {
					m.loadData()
					newTab.__actived = true;
				}
			},

			/**
			 * 刷新面板
			 * 
			 * @param {}
			 *            newTab
			 */
			refreshModule : function(newTab) {
				var m = this.midiModules[newTab.name]
				if (m) {
					if (this.exContext) {
						delete m.exContext;
						m.exContext = {};
						this.refreshExContextData(m, this.exContext);
					}
					if (m.loadData) {
						m.loadData();
						newTab.__actived = true;
					} else if (m.refresh) {
						m.refresh();
						newTab.__actived = true;
					}

				}
			},

			/**
			 * 清空所有激活标识
			 */
			clearAllActived : function() {
				this.tab.items.each(function(item) {
							item.__actived = false;
						}, this);
			},

			/**
			 * 此方法主要用于左边列表右边tab点击左边记录，右边加载数据的模型。 当更换记录的时候，为了避免首次激活的面板不刷新，调用下面方法
			 * 
			 * @param {}
			 *            tabIndex
			 */
			activeModule : function(tabIndex) {
				this.clearAllActived();
				var activeTab = this.tab.activeTab;
				activeTab = Ext.isObject(activeTab)
						? activeTab
						: this.tab.items.get(activeTab);
				var newTab = this.tab.items.get(tabIndex);
				if (activeTab == newTab) {
					this.onTabChange(this, newTab, this.tab.activeTab);
				}
				this.tab.setActiveTab(tabIndex);
			},

			/**
			 * 关闭当前页面的时候，清空所有激活标识
			 */
			onClose : function() {
				this.clearAllActived();
			},

			/**
			 * 控制子标签是否可以使用
			 * 
			 * @param {}
			 *            disabled 设置tab是否可用,true为不可用,false为可用
			 * @param {}
			 *            tabName 可选，排除某个tab页一直可用
			 */
			changeSubItemDisabled : function(disabled, tabName) {
				for (var i = 0; i < this.tab.items.length; i++) {
					var subTab = this.tab.items.itemAt(i);
					if (!subTab) {
						continue;
					}
					if (subTab.name == tabName) {
						subTab.enable();
						continue;
					}
					if (disabled) {
						subTab.disable();
					} else {
						subTab.enable();
					}
				}
			},

			/**
			 * 控制单个子标签项是否可用
			 * 
			 * @param {}
			 *            index tab页索引
			 * @param {}
			 *            disabled true 不可用 false 可用
			 */
			setTabItemDisabled : function(index, disabled) {
				var subTab = this.tab.items.itemAt(index);
				if (!subTab) {
					return;
				}
				if (disabled) {
					subTab.disable();
				} else {
					subTab.enable();
				}
			},

			refreshExcontext : function() {
				for (var id in this.midiModules) {
					var m = this.midiModules[id]
					m.exContext = this.exContext
				}
			},

			changeCfg : function(cfg) {
				if (this.isAutoScroll) {
					delete cfg.defaults;
					delete cfg.autoHeight;
					delete cfg.width;
					cfg.frame = true;
					cfg.defaults = {
						border : false
					};
				}
			},
			keyManageFunc : function(keyCode, keyName) {
				for (var i = 0; i < this.actions.length; i++) {
					var m = this.midiModules[this.actions[i].id];
					if (this.tab.activeTab.name != this.actions[i].id) {
						continue;
					}
					if (m) {
						if (m.keyManageFunc) {
							m.keyManageFunc(keyCode, keyName);
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

			onReady : function() {
				// ** 设置滚动条
				if (this.isAutoScroll) {
					this.tab.setWidth(this.getFormWidth());
					this.tab.setHeight(this.getFormHeight());
					this.tab.items.each(function(item) {
								item.setWidth(this.getFormWidth());
								item.setHeight(this.getFormHeight());
							}, this);
				}
			},

			getParentCMP : function() {
				return this.tab.findParentBy(function(ct, tab) {
							if (ct.items.itemAt(0).getId() == tab.getId()) {
								return true;
							} else {
								return false
							}
						})
			},

			getFormHeight : function() {// 本计算只用于EHRViwe
				var parent = this.getParentCMP();
				if (parent) {
					return parent.getHeight();
				} else {
					return Ext.getBody().getHeight();
				}
			},

			getFormWidth : function() {// 本计算只用于EHRViwe
				var parent = this.getParentCMP();
				if (parent) {
					return parent.getWidth();
				} else {
					return Ext.getBody().getWidth();
				}
			},
			getWin : function() {
				var win = this.win
				var cfg = {
								title : this.title||this.name,
								width : this.width,
								constrain : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || false,
								buttonAlign : 'center',
								items : this.initPanel()
							};
				if(!this.height){
					cfg.autoHeight = true;
				}else{
					cfg.height = this.height;
					cfg.autoScroll = true;
				}
				if (!win) {
					win = new Ext.Window(cfg)
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
		})