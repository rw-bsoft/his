$package("phis.application.emr.script");

$import("phis.script.common", "phis.script.TabModule");

phis.application.emr.script.EMRTabModule = function(cfg) {
	cfg.activateId = 0;
//	this.serviceId = cfg.serviceId;
//	this.saveServiceAction = cfg.saveServiceAction;
	Ext.apply(this, app.modules.common);
	phis.application.emr.script.EMRTabModule.superclass.constructor.apply(this,[cfg]);
}
Ext.extend(phis.application.emr.script.EMRTabModule,phis.script.TabModule, {
			initPanel : function() {
				if (this.mainTab) {
					return this.mainTab;
				}
				var tabItems = []
//				var actions = this.actions
//				var bar = [];
//				for (var i = 0; i < actions.length; i++) {
//					var ac = actions[i];
//					tabItems.push({
//								frame : this.frame,
//								layout : "fit",
//								title : ac.name,
//								exCfg : ac,
//								id : ac.id
//							})
//				}
				var tab = new Ext.TabPanel({
					frame : false,
					border : false,
					deferredRender : false,
					// layoutOnTabChange:true,
					activeTab : 0,
					enableTabScroll : true,
					items : tabItems,
					autoHeight : false,
					tabPosition : 'bottom',
					defaults : ({
//						border : false,
						// 解决tab切换时电子病历控件被销毁的问题
						hideMode : 'offsets'
					})
						})
				tab.on("tabchange", this.onTabChange, this);
				tab.on("beforetabchange", this.onBeforeTabChange, this);
				tab.on("render",this.onReady,this)
				tab.activate(this.activateId)
				this.mainTab = tab
				return tab;
			},
			onBeforeTabChange : function(tabPanel, newTab, curTab) {

			},
			onTabChange :  function(tabPanel, newTab, curTab) {
				if (!newTab) {
					return;
				}
				if (newTab.__formChis) {
					return;
				}
				if (newTab.__actived) {
					return;
				}
				var exContext;
				var mainTabCfg = this.mainTab.find("mKey", newTab.mKey)[0];
				if (mainTabCfg) {
					exContext = mainTabCfg.exContext;
					delete mainTabCfg.exContext;
				}

				if (newTab.__inited) {
					if (newTab.mKey == "INDEX" && this.doctorPanel) {
						this.doctorPanel.body.update(this.getManaDoctorTpl());
					}
					this.refreshModule(newTab, exContext);
					return;
				}
				var exCfg = (newTab.myPage ? Ext.apply(exCfg || {}, newTab) : this
						.getModuleCfg(newTab.mKey))
				Ext.apply(exCfg.exContext, exContext);
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true,
					mainApp : this.mainApp
				}
				Ext.apply(cfg, exCfg);
				var ref = cfg.ref
				if (ref) {
					var body = this.loadModuleCfg(ref);
					Ext.apply(cfg, body)
				}
				var cls = cfg.script// ClinicMedicalRecordModule.js
				if (!cls) {
					return;
				}
				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
							var me = this.mainTab.el
							if (me) {
								me.mask("正在加载...")
							}
							try {
								var m = eval("new " + cls + "(cfg)")
								m.on("save", this.opener.onModuleSave, this)
								m.on("activeModule", this.opener.onActiveModule, this);
								m.on("refreshModule", this.opener.onRefreshModule, this)
								m.on("clearCache", this.opener.onClearCache, this)
								m.on("chisSave", this.opener.chisModelSave, this);
								m.on("CMRSave", this.opener.CMRSave, this);
								m.emrview = this;
								m.exContext = this.exContext;
								// m.exContext.pdgdbz = this.exContext.pdgdbz;// 规定病种
								this.midiModules[newTab.mKey] = m;
								var p = m.initPanel();
								p.title = null
								p.border = false
								p.frame = false
								newTab.on("destroy", this.onModuleClose, this)
								newTab.add(p);
								newTab.__inited = true
							} finally {
								if (me) {
									me.unmask()
								}
							}
							if (m.loadData && m.autoLoadData == false) {
								m.loadData();
								newTab.__actived = true;
							}
							}, this])
//							this.mainTab.doLayout()
			},
			onModuleClose : function(p) {
				var m = this.midiModules[p.mKey]
				if (m) {
					if (!m.__formChis) {
						m.destory()
					}
					delete this.midiModules[p.mKey]
					delete this.opener.activeModules[p.key]
				}
			},
			getModuleCfg : function(key) {
				var dic = this.emrNavDic
				if (!dic) {
					dic = util.dictionary.DictionaryLoader.load({
								id : 'phis.dictionary.emrView'
							})
					this.emrNavDic = dic;

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
//				} else {
//					n = this.opener.tjjyNodes[key];
//					Ext.apply(cfg, n)
//					cfg.title = n.text;
				}
//				var rks = cfg.requireKeys
//				if (rks) {
//					if (rks.indexOf(",") > -1) {
//						var keys = rks.split(",")
//						for (var i = 0; i < keys.length; i++) {
//							var k = keys[i]
//							var v = this.exContext.ids[k]
//							if (!v) {
//								cfg.disabled = true
//								break;
//							}
//						}
//					} else {
//						var v = this.exContext.ids[rks]
//						if (!v) {
//							cfg.disabled = true
//						}
//					}
//				}
				cfg.exContext = this.exContext;
				cfg.opener = this;
				// ** add by yzh
				var readOnlyKey = cfg.readOnlyKey
				if (readOnlyKey) {
					cfg.exContext.readOnly = this.exContext.ids[readOnlyKey]
				}
				return cfg;
			},
			loadEmrTreeNode : function(firstOpen) {
				this.opener.loadEmrTreeNode(firstOpen);
			},
			onQuickInput : function(newTab, grid) {
//				this.fireEvent("quickInput", newTab, grid);
			},
			onSuperRefresh : function(entryName, op, json, rec) {
			},
//			doAction : function(item, e) {
//				var cmd = item.cmd
//				var script = item.script
//				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
//				if (script) {
//					$require(script, [function() {
//								eval(script + '.do' + cmd
//										+ '.apply(this,[item,e])')
//							}, this])
//				} else {
//					var action = this["do" + cmd]
//					if (action) {
//						action.apply(this, [item, e])
//					}
//				}
//			}
			onReady : function(){
//				var n = this.opener.initEmrLastNodes['2000'];
//				n = {};
//				n.BLBH = '2000';
//				n.key = '2000';
//				n.BLLX = '0';
//				n.LBMC = '门急诊病历';
//				n.text = '病历首页';
//				n.ref = 'phis.application.cic.CIC/CIC/CIC29';
				if(this.opener.needOpenNode){
					for(var i = 0 ; i < this.opener.needOpenNode.length ; i ++){
						var node = this.opener.needOpenNode[i];
						if(i==0){
							this.openEmrEditorModule(node,null,true);
//						}else{
//							this.openEmrEditorModule(node,null,null,true);
						}
					}
				}else if (this.opener.emrN) {
					this.openEmrEditorModule(this.opener.emrN,null,true);
				}
			},
			doClose : function() {
				this.opener.win.hide();
			},
			beforeClose : function(cloab) {
				// 关闭病历首页
//				this.win.closeing = true;
				for (var i = 0; i < this.mainTab.items.length; i++) {
					var tab = this.mainTab.getItem(i);
					var m = this.midiModules[tab.mKey];
					if (m && m.beforeClose) {
						if (!m.beforeClose(cloab)) {
							this.opener.win.closeing = false;
							return false;
						}
						m.doClose();
					}
				}
//				 this.fireEvent("close");
				this.opener.win.closeing = false;
				return true;
			},
			beforeEnd : function(str) {
				// 关闭病历首页
				for (var i = 0; i < this.mainTab.items.length; i++) {
					var tab = this.mainTab.getItem(i);
					var m = this.midiModules[tab.mKey];
					if (m && m.beforeEnd) {
						if (!m.beforeEnd(str)) {
							return false;
						}
					}
				}
				return true;
			},
			checkEmrPermission : function(op, bllb) {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "checkPermission",
							body : {
								BLLB : bllb
							}
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return false;
				}
				return result.json.emrPermissions[op]
			},
			openEmrEditorModule : function(node, recoveredBl01,firstOpen,notActivate) {
//				 权限验证
				if (!this.checkEmrPermission("CKQX", node.MBLB)) {
					MyMessageTip.msg("提示", "对不起，您没有查看该病历/病程的权限!", true);
					return;
				}
				// 是否单文档
				var nodeId = node.BLBH || node.key;
				if (node.BLLX == 1) {// 病程
					nodeId = node.key;
				}
				var key = "phis.application.cic.CIC/CIC/CIC29";
//				if (node.key == 2000001) {
//					key = "phis.application.war.WAR/WAR/WAR21"
//				}
				if (this.opener.activeModules[key + nodeId]) {
					var finds = this.mainTab.find("mKey", key + nodeId)
					if (finds.length == 1) {
						var p = finds[0]
						this.mainTab.activate(p);
						if (this.midiModules[key + nodeId]) {
							this.midiModules[key + nodeId].recoveredBl01 = recoveredBl01;
							this.midiModules[key + nodeId].doLoadBcjl(node.BLBH);
						}
						return;
					} else {
						delete this.midiModules[key + nodeId];
					}
				}
				if(!this.opener.BLSY){
					this.opener.BLSY = key + nodeId;
				}
				var m = this.createModule(key + nodeId, key);
				m.recoveredBl01 = recoveredBl01;
				if (m.initPanel) {
					m.exContext = this.exContext
					m.mKey = key + nodeId;
					m.opener = this.opener;
					m.node = node;
					// m.on("activeModule", this.onActiveModule, this);
					m.on("refreshModule", this.opener.onRefreshModule, this.opener)
					m.on("clearCache", this.opener.onClearCache, this.opener)
					var p = m.initPanel();
					if(firstOpen){
						p.closable = false;
						m.closable = false;
					}else{
						p.closable = true;
						m.closable = true;
					}
					if (node.BLLX == 1) {
						p.title = node.LBMC || node.text;
					} else {
						p.title = node.text;
					}
					p.mKey = key + nodeId;
					p.key = key + nodeId;
					// this.emrNavTreePanel.collapse(false)
					var tab = this.mainTab.add(p)
					tab.on("destroy", this.onModuleClose, this.opener)
					this.mainTab.doLayout()
//					this.opener.tjjyNodes[key + nodeId] = node;
//					if(!notActivate){
						this.mainTab.activate(p)
//					}
					this.opener.activeModules[key + nodeId] = true;
				}
//				this.mainTab.setActiveTab(0);
			},
			changeTab : function(index) {
				this.opener.changeTab(index);
			},
			closeCurrentTab : function() {
				var mainTab = this.mainTab
				var act = mainTab.getActiveTab()
				if (act && act.mKey) {
					mainTab.remove(act)
				}
			}
			
		});