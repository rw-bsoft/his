$package("chis.application.conf.script")
$import("util.dictionary.TreeDicFactory")
$import("util.dictionary.DictionaryLoader", "chis.script.BizCombinedModule2")
chis.application.conf.script.SystemConfigModule = function(cfg) {
	Ext.apply(this, app.modules.common)
	chis.application.conf.script.SystemConfigModule.superclass.constructor.apply(this,
			[cfg])
	this.itemWidth = 200
	this.roles = {}
	this.activeModules = {};
}
Ext.extend(chis.application.conf.script.SystemConfigModule, chis.script.BizCombinedModule2,
		{

			getFirstItem : function() {
				var data = this.loadRoles()
				if (data) {
					Ext.apply(this.roles, data);
				}
				var navDic = "chis.dictionary.sysConfNav"
				var tf = util.dictionary.TreeDicFactory.createDic({
							id : navDic
						})
				tf.tree.expandAll();
				tf.tree.on("click", this.onNavTreeClick, this)
				tf.tree.on("append", function(tree, parent, node, index) {
							var autoActive = node.attributes["autoActive"]
							if (autoActive) {
								this.onNavTreeClick(node, null);
							}
						}, this);
				this.tree = tf.tree
				return tf.tree;
			},

			loadRoles : function() {
				var res = util.rmi.miniJsonRequestSync({
							serviceId : "chis.configControllorService",
							method : "execute"
						})

				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg);
					return
				}

				if (res.code == 200) {
					return res.json.body;
				}
				return null;
			},

			onNavTreeClick : function(node, e) {
				var key = node.attributes.key
				if (this.activeModules[key]) {
					var finds = this.mainTab.find("mKey", key)
					if (finds.length == 1) {
						var p = finds[0]
						this.mainTab.activate(p)
					}
					return;
				}
				var cfg = this.getModuleCfg(key)
				cfg.closable = true;
				var p = this.mainTab.add(cfg)
				this.mainTab.doLayout()
				this.mainTab.activate(p)
				this.activeModules[key] = true
			},

			getModuleCfg : function(key) {
				var dic = this.sysNavDic
				if (!dic) {
					dic = util.dictionary.DictionaryLoader.load({
								id : "chis.dictionary.sysConfNav"
							})
					this.sysNavDic = dic;

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
				cfg.readOnly = this.roles[key + "_readOnly"]
				cfg.fieldReadOnly = this.roles[key + "_fieldReadOnly"] || null;
				cfg.exContext = {};
				Ext.apply(cfg.exContext, this.exContext);
				return cfg;
			},

			getSecondItem : function() {
				var items = []
				var mainTab = new Ext.TabPanel({
							frame : false,
							border : false,
							deferredRender : false,
							layoutOnTabChange : true,
							activeTab : this.activeTab,
							items : items
						})
				mainTab.on("tabchange", this.onModuleActive, this)
				this.mainTab = mainTab;
				return mainTab;
			},

			onModuleActive : function(tabPanel, newTab, curTab) {
				if (!newTab) {
					return;
				}
				if (newTab.__actived) {
					return;
				}
				if (newTab.__inited) {
					this.refreshModule(newTab);
					return;
				}
				var exCfg = this.getModuleCfg(newTab.mKey)
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true,
					mainApp : this.mainApp
				}
				Ext.apply(cfg, exCfg);
				var ref = cfg.ref

				if (ref) {
					var moduleCfg = this.mainApp.taskManager.loadModuleCfg(ref);
					Ext.apply(cfg, moduleCfg.json.body);
					Ext.apply(cfg, moduleCfg.json.body.properties);
				}

				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$import(cls);
				var me = this.mainTab.el
				if (me) {
					me.mask("正在加载...")
				}
				var m = eval("new " + cls + "(cfg)")
				this.midiModules[newTab.mKey] = m;
				var p = m.initPanel();
				p.title = null
				p.border = false
				p.frame = false

				newTab.on("destroy", this.onModuleClose, this)
				newTab.add(p);
				newTab.__inited = true
				if (me) {
					me.unmask()
				}
				this.mainTab.doLayout()
				if (m.loadData && m.autoLoadData == false) {
					m.loadData();
					newTab.__actived = true;
				}
			},

			/**
			 * 清空所有激活标识
			 */
			clearAllActived : function() {
				this.mainTab.items.each(function(item) {
							item.__actived = false;
						}, this);
			},

			onModuleClose : function(p) {
				var m = this.midiModules[p.mKey]
				if (m) {
					m.destory()
					delete this.midiModules[p.mKey]
					delete this.activeModules[p.key]
				}
				this.clearAllActived();
			},

			refreshModule : function(key, exContext) {
				var m = this.midiModules[key]
				if (m) {
					if (exContext) {
						delete m.exContext;
						m.exContext = exContext;
					}
					m.readOnly = this.roles[key + "_readOnly"];
					m.fieldReadOnly = this.roles[key + "_fieldReadOnly"]
							|| null;
					if (m.loadData) {
						m.loadData()
					} else if (m.refresh) {
						m.refresh();
					}
				}
			}
		});