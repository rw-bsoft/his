$package("phis.application.fsb.script")
$import("phis.script.SimpleModule", "phis.script.PrintWin",
		"util.dictionary.TreeDicFactory", "util.dictionary.DictionaryLoader",
		"phis.application.war.script.HDRView")
phis.application.fsb.script.FDRView = function(cfg) {
	cfg.title = "导航栏"
	phis.application.fsb.script.FDRView.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.fsb.script.FDRView,
		phis.application.war.script.HDRView, {
			onWinShow : function() {
				// 判断是否有需要恢复的病历
				this.loadEmrTreeNode(true);
				if (!this.tabsInited) {
					this.initTabItems();
				}
				// 备份恢复
				this.recoverRecord();
				// 快捷打开
				if (this.initOpenEmrNode) {
					var n = this.initEmrLastNodes[this.initOpenEmrNode]
					if (n) {
						this.openEmrEditorModule(n);
					}
				}
				// this.topPanel.hide();
			},
			createNavPanel : function() {
				var emrNavTree = util.dictionary.TreeDicFactory.createTree({
							id : "phis.dictionary.fdrViewNav"
						})
				this.emrNavTree = emrNavTree
				// 获取EMR的导航
				emrNavTree.expandAll()
				emrNavTree.on("click", this.onNavTreeClick, this)
				// var root = emrNavTree.getRootNode()
				var emrNavTreePanel = new Ext.Panel({
							border : false,
							frame : true,
							layout : "fit",
							split : true,
							collapsible : true,
							title : '导航栏',
							region : 'west',
							width : this.westWidth,
							collapsed : false,
							items : emrNavTree
						})
				this.emrNavTreePanel = emrNavTreePanel
				return emrNavTreePanel
			},
			getNewTopTemplate : function() {
				if (this.tpl) {
					return this.tpl;
				}
				$styleSheet("phis.resources.css.app.biz.style")
				var photoUrl = ClassLoader.appRootOffsetPath + "{photo}"
				var tpl = new Ext.XTemplate(
						'<div class="head_bg">',
						'<div id="emr_header">',
						'<div id="topMenu_out"><div id="topMenu_in">',
						'<ul class="top_menu_hdr">',
						'<a href="javascript:void(0)" style="width:76px;">病人列表</a></li>|<a href="javascript:void(0)" style="width:76px;" class="topLine" id="MPI">个人信息</a>|<a href="javascript:void(0)" id="PRT">打印</a>|<a class="topLine" id="HP">帮助</a>|<a class="topLine" id="CLOSE">关闭</a>',
						'</ul>',
						'</div></div>',
						'<div class="TopMessage">',
						'<img src="'
								+ photoUrl
								+ '" width="71" height="71"  class="fleft photo" />',
						'<div  class="fleft">',
						'<div class="ehrviewClear">',
						'<h2 class="fleft">{BRXM}</h2>',
						'<div class="fleft" style="margin:0px 80px 0px 0px">&nbsp;</div>',
						'<div style="margin:4px 0px 0px 0px"><label class="fleft"><a id="skinTest" class="topLine"><img id="skinTest" title="查看病人过敏记录" src="'
								+ ClassLoader.appRootOffsetPath
								+ 'photo/M.png" class="fleft"/></a></label>',
						'</div></div>',
						'<ul class="mdetail">',
						'<li class="width15">',
						'<p><label>性&nbsp;&nbsp;&nbsp;&nbsp;别：</label>{BRXB_text}</p>',
						'<p><label>年&nbsp;&nbsp;&nbsp;&nbsp;龄：</label>{RYNL}</p>',
						'</li>',
						'<li class="width20">',
						'<p><label>家床号码：</label>{ZYHM}</p>',
						'<p><label>建床诊断：</label><font title="{JCZD}">{JCZD:substr(0,25)}<tpl if="JCZD.length &gt; 25">...</tpl></font></p>',
						'</li>', '<li class="width15">',
						'<p><label>联系电话：</label>{LXDH}</p>',
						'<p><label>联系地址：</label><font title="{JCZD}">{LXDZ}</font></p>',
						'</li>', '</ul>', '</div>', '</div>', '</div></div>');
				this.tpl = tpl
				return tpl;
			},
			loadEmrTreeNode : function(firstOpen) {
				phis.script.rmi.jsonRequest({
							serviceId : "familySickBedManageService",
							serviceAction : "loadNavTree",
							body : {
								JZH : this.exContext.ids.clinicId,
								empiId : this.exContext.ids.empiId
							}
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							var root = this.emrNavTree.getRootNode();
							if (this.blNode) {
								this.blNode.remove();
							}
							if (this.ehrNode) {
								this.ehrNode.remove();
							}
							if (this.ccNode) {
								this.ccNode.remove();
							}
							var emrNodeList = json.emrTree;
							var ehrNodeList = json.ehrTree;
							var fdrNodeList = json.fdrTree;
							var ccjlNode = new Ext.tree.TreeNode({
										key : "E",
										text : "查床记录",
										expend : true
									});
							this.ccNode = ccjlNode;
							root.insertBefore(ccjlNode, root.firstChild);
							this.getFDRChildNode(ccjlNode, fdrNodeList,
									firstOpen);
							// if (this.exContext.systemParams.QYDZBL == '1') {
							this.initEmrLastNodes = {};
							var blNode = new Ext.tree.TreeNode({
										key : "D",
										text : "书写病历",
										expend : true
									});
							this.blNode = blNode;
							this.getEMRChildNode(blNode, emrNodeList);
							root.appendChild(blNode);
							// }
							root.expandChildNodes(true);
							if (firstOpen && this.ccNode.firstChild) {
								this.ccNode.firstChild.select();
							}
							// this.getEHRChildNode(root, ehrNodeList);

					}, this)
			},
//	onTopLnkClick : function(e) {
//		var lnk = e.getTarget();
//		var cmd = lnk.id
//		switch (cmd) {
//			case "HP" :
//				window.open("resources/phis/help/BS-PHIS-HELP.html");
//				break;
//			// case "PRT" :
//			// this.showPrintView()
//			// break
//			case "SWH" :
//				break
//			case "skinTest" :
//				this.showSkinTest();
//				break
//			case "MPI" :
//				this.showMPIInfo()
//				break;
//			case "PZYYY" :
//				this.showPZYYYInfo()
//				break;
//			case "NAV" :
//				var navPanel = this.panel.items.item(1)
//				var status = navPanel.isVisible()
//				if (status) {
//					navPanel.collapse()
//				} else {
//					navPanel.expand()
//				}
//				break;
//			case "HME" :
//				this.mainTab.setActiveTab(0)
//				break;
//			case "CLOSE" :
//				this.openReferralAppointment();
//				break;
//			case "HZXX" :
//				this.doDyhzxx();
//				break;
//			case "ZG" :
//				this.doClinicFinish(2);
//				break;
//		}
//	},
//	showMPIInfo : function() {
//		var m = this.midiModules['mpi'];
//		if (!m) {
//			$import("phis.application.pix.script.EMPIInfoModule")
//			m = new phis.application.pix.script.EMPIInfoModule({
//						serviceAction : "updatePerson",
//						title : "个人基本信息",
//						mainApp : this.mainApp,
//						modal : true
//					});
//			m.on("onEmpiReturn", function() {
//						m.getWin().hide(); // ** add by yzh 20100724 close the
//						// EMPIInfoModule
//						this.refreshTopEmpi(); // ** add by yzh 20100708
//						// refresh top empiData
//						if (this.activeModules["H01"]) // ** add by yzh
//							// 20100721 refresh
//							// child health record
//							this
//									.refreshModule(this.mainTab.find("mKey",
//											"H01")[0])
//					}, this)
//			this.midiModules['mpi'] = m;
//		}
//		m.getWin().show();
//		m.clear(); // ** add by yzh 20100818
//		// m.setRecord(this.exContext.ids.empiId)
//		m.setRecord(this.exContext.empiData.empiId);// add by zhouyl 20130927
//	},
			getFDRChildNode : function(pNode, nodeList, firstOpen) {
				if (nodeList.length == 0) {
					this.openAdviceEditorModule();
				}
				for (var i = 0; i < nodeList.length; i++) {
					var node = nodeList[i];
					var n = new Ext.tree.TreeNode({
								key : node.SBXH,
								text : node.CCSJ,
								adviceEditor : true,
								leaf : true
							});
					if (node.children) {
						this.getFDRChildNode(n, node.children);
					} else {
						if (i == 0 && firstOpen) {
							this.openAdviceEditorModule(node.SBXH);
						}
					}
					pNode.appendChild(n);
				}
			},
			onNavTreeClick : function(node, e) {
				if (!node.leaf) {
					return;
				}
				if (node.id == 'C04' && this.openBy == "nurse") {
					MyMessageTip.msg("提示", "对不起，病区无法打开会诊申请!", true);
					return;
				}
				var key = node.attributes.key;
				if (!key) {
					// @@ 打开社区EHRView
					this.openEHRView(node);
					return;
				}
				var blbh = node.attributes.BLBH;
				// add by yangl emrEditor
				if (node.attributes.emrEditor) {
					this.openEmrEditorModule(node.attributes);
					return;
				}
				if (node.attributes.adviceEditor) {
					this.openAdviceEditorModule(node.attributes.key);
					return;
				}
				// add by yuhua
				this.idsParentNode = node.parentNode.attributes.key
				if (!this.exContext.ids[this.exContext.ids.empiId + "_"
						+ this.idsParentNode]) {
					this.exContext.ids.idsLoader = node.parentNode.attributes.idsLoader
					this.exContext.ids[this.exContext.ids.empiId + "_"
							+ this.idsParentNode] = this.exContext.ids.empiId
							+ "_" + this.idsParentNode
					var data = this.loadEmpiInfo()
					if (data) {
						Ext.apply(this.exContext.ids, data.ids);
						Ext.apply(this.exContext.empiData, data.empiData);
					}
				}
				var cfg = this.getModuleCfg(key)
				if (cfg.disabled) {
					return;
				}
				if (cfg.showWinOnly == "true") {
					var width = 800
					var module = this.createModule(cfg.key, cfg.ref);
					if (key == "E01") {
						width = 1024;
						module.height = 600
						module.readOnly = true;
						var moduleData = this.getHljlData();
						if (this.midiWins[cfg.key]) {
							module.doFillIn(moduleData);
						} else {
							module.doInitData(moduleData);
						}
					}
					if (key == "E02") {
						var ryrq = Date.parseDate(
								(this.exContext.empiData.RYRQ).split(' ')[0],
								"Y-m-d")// 修正病人视图下时间格式不对的问题
						var cyrq = new Date();
						if (!Ext.isEmpty(this.exContext.empiData.CYRQ)) {
							cyrq = Date.parseDate(this.exContext.empiData.CYRQ
											.split(' ')[0], "Y-m-d")
						}
						module.maxWeek = Math.floor((Math
								.floor((cyrq.getTime() - ryrq.getTime()) / 1000
										/ 60 / 60 / 24))
								/ 7);
						module.currentWeek = module.maxWeek;
						module.readOnly = true;
						module.info = this.exContext.empiData
					}
					if (key == "E03") {
						width = 1024;
						module.height = 600
						module.readOnly = true;
						var moduleData = this.getHljlData();
						if (this.midiWins[cfg.key]) {
							module.doFillIn(moduleData);
						} else {
							module.doInitData(moduleData);
						}
					}
					module.initDataId = this.exContext.ids.clinicId;
					module.exContext = this.exContext;
					if (this.exContext.empiData.CYPB >= 1) {
						module.exContext.readOnly = true;
					}
					if (!this.midiWins[cfg.key]) {
						this.midiWins[cfg.key] = module.getWin();
						module.getWin().add(module.initPanel());
					}
					this.midiWins[cfg.key].setWidth(width);
					this.midiWins[cfg.key].setHeight(module.height || 355);
					this.midiWins[cfg.key].show();
					return;
				}
				if (this.activeModules[key]) {
					var finds = this.mainTab.find("mKey", key)
					if (finds.length == 1) {
						var p = finds[0]
						this.mainTab.activate(p)
					}
					return;
				}

				cfg.closable = true;
				cfg.exContext = this.exContext
				if (this.exContext.empiData.CYPB > 1) {
					cfg.exContext.readOnly = true;
				}
				this.emrNavTreePanel.collapse(false)
				var p = this.mainTab.add(cfg)
				this.mainTab.doLayout()
				this.mainTab.activate(p)
				this.activeModules[key] = true
			},
			openAdviceEditorModule : function(ccxh) {
				// 是否单文档
				var moduleId = "phis.application.fsb.FSB/FSB/FSB13";
				var finds = this.mainTab.find("mKey", moduleId)
				if (finds.length == 1) {
					var p = finds[0]
					this.mainTab.activate(p);
					if (this.midiModules[moduleId]) {
						this.midiModules[moduleId].doLoadCcjl(ccxh);
					}
					return;
				} else {
					delete this.midiModules[moduleId];
				}
				var m = this
						.createModule("familySickBedAdviceModule", moduleId);
				if (m.initPanel) {
					m.exContext = this.exContext
					if (this.exContext.empiData.CYPB >= 1) {
						m.exContext.readOnly = true;
					}
					m.mKey = moduleId;
					m.opener = this;
					m.ccxh = ccxh;
					// m.on("activeModule", this.onActiveModule, this);
					m.on("refreshModule", this.onRefreshModule, this)
					m.on("clearCache", this.onClearCache, this)
					this.midiModules[moduleId] = m;
					var p = m.initPanel();
					// p.closable = true;
					p.title = "医嘱录入";
					p.mKey = moduleId;
					p.key = moduleId;
					// this.emrNavTreePanel.collapse(false)
					var tab = this.mainTab.add(p)
					tab.on("destroy", this.onModuleClose, this)
					this.mainTab.doLayout()
					this.mainTab.activate(p)
					this.activeModules[moduleId] = true
				}
			},
			openEmrEditorModule : function(node, recoveredBl01) {
				// alert(key)
				// 权限验证
				if (!this.checkEmrPermission("CKQX", node.MBLB)) {
					MyMessageTip.msg("提示", "对不起，您没有查看该病历/病程的权限!", true);
					return;
				}
				// 是否单文档
				var nodeId = node.BLBH || node.key;
				if (node.BLLX == 1) {// 病程
					nodeId = node.key;
				}
				var key = "phis.application.war.WAR/WAR/WAR36";
				if (node.key == 2000001) {
					key = "phis.application.war.WAR/WAR/WAR21"
				}
				if (this.activeModules[key + nodeId]) {
					var finds = this.mainTab.find("mKey", key + nodeId)
					if (finds.length == 1) {
						var p = finds[0]
						this.mainTab.activate(p);
						if (this.midiModules[key + nodeId]) {
							this.midiModules[key + nodeId].recoveredBl01 = recoveredBl01;
							this.midiModules[key + nodeId]
									.doLoadBcjl(node.BLBH);
						}
						return;
					} else {
						delete this.midiModules[key + nodeId];
					}
				}
				var m = this.createModule(key + nodeId, key);
				m.recoveredBl01 = recoveredBl01;
				if (m.initPanel) {
					m.exContext = this.exContext
					m.mKey = key + nodeId;
					m.opener = this;
					m.openBy = "FSB";// 家床打开标志
					m.node = node;
					// m.on("activeModule", this.onActiveModule, this);
					m.on("refreshModule", this.onRefreshModule, this)
					m.on("clearCache", this.onClearCache, this)
					var p = m.initPanel();
					p.closable = true;
					if (node.BLLX == 1) {
						p.title = node.LBMC || node.text;
					} else {
						p.title = node.text;
					}
					p.mKey = key + nodeId;
					p.key = key + nodeId;
					// this.emrNavTreePanel.collapse(false)
					var tab = this.mainTab.add(p)
					tab.on("destroy", this.onModuleClose, this)
					this.mainTab.doLayout()
					this.mainTab.activate(p)
					this.activeModules[key + nodeId] = true
				}
			},
			createTopPanel : function() {
				var empiId = this.exContext.ids["empiId"]
				if (empiId) {
					var data = this.loadEmpiInfo(empiId)
					if (data) {
						Ext.apply(this.exContext.ids, data.ids);
						Ext.apply(this.exContext.empiData, data.empiData);
					}
				}
				for (prop in this.exContext.empiData) {
					if (this.exContext.empiData[prop] == null
							|| this.exContext.empiData[prop] == 'null') {
						this.exContext.empiData[prop] = "";
					}
				}
				// 病人住院信息
				Ext.applyIf(this.exContext.empiData, this.exContext.brxx.data);
				if (!this.exContext.empiData.JBMC) {
					this.exContext.empiData.JBMC = '';
				}
				if (!this.exContext.empiData.photo) {
					this.exContext.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
				}
				// alert(Ext.encode(this.exContext.empiData))
				var tpl = this.getNewTopTemplate()
				// 增加EMR控件，用来判断是否有恢复文件
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							height : 87,
							collapsible : false,
							layout : 'fit',
							region : 'north',
							html : tpl.apply(this.exContext.empiData)
						})
				panel.on("afterrender", this.attachTopLnkEnvents, this)
				this.topPanel = panel;
				return panel;
			},
			initTabItems : function() {
				var items = []
				var ims = this.initModules
				var empiId = this.exContext.ids["empiId"]
				var phrId = this.exContext.ids["phrId"]
				this.closeAllModules()
				var activeModules = {}
				// if (!phrId) {
				// this.mainTab.add(this.getModuleCfg("C01"))
				// activeModules["C01"] = true
				// }

				if (ims) {
					for (var i = 0; i < ims.length; i++) {
						var im = ims[i]
						if (!activeModules[im]) {
							this.mainTab.add(this.getModuleCfg(im, true))
							activeModules[im] = true
						}
					}
				}
				this.activeModules = activeModules
				// this.mainTab.doLayout()
				// this.win.doLayout()
				this.mainTab.setActiveTab(this.activeTab);
				this.tabsInited = true;
			},
			getModuleCfg : function(key, closeable) {
				var dic = this.emrNavDic
				if (!dic) {
					dic = util.dictionary.DictionaryLoader.load({
								id : 'phis.dictionary.fdrViewNav'
							})
					this.emrNavDic = dic;

				}

				if (!dic) {
					return {};
				}
				var n = dic.wraper[key]

				var cfg = {
					closable : closeable || this.initTabClosable,
					frame : true,
					mKey : key,
					layout : "fit"
				}
				if (n) {
					Ext.apply(cfg, n)
					cfg.title = n.text;
				}
				cfg.exContext = this.exContext;
				// ** add by yzh
				var readOnlyKey = cfg.readOnlyKey
				if (readOnlyKey) {
					cfg.exContext.readOnly = this.exContext.ids[readOnlyKey]
				}
				return cfg;
			},
//	onTopLnkClick : function(e) {
//		var lnk = e.getTarget();
//		var cmd = lnk.id
//		switch (cmd) {
//			case "HP" :
//				window.open("resources/phis/help/BS-PHIS-HELP.html");
//				break;
//		    case "PRT" :
//			 this.showPrintView()
//			 break
//			case "SWH" :
//				break
//			case "skinTest" :
//				this.showSkinTest();
//				break
//			case "MPI" :
//				this.showMPIInfo()
//				break;
//			case "PZYYY" :
//				this.showPZYYYInfo()
//				break;
//			case "NAV" :
//				var navPanel = this.panel.items.item(1)
//				var status = navPanel.isVisible()
//				if (status) {
//					navPanel.collapse()
//				} else {
//					navPanel.expand()
//				}
//				break;
//			case "HME" :
//				this.mainTab.setActiveTab(0)
//				break;
//			case "CLOSE" :
//				this.openReferralAppointment();
//				break;
//			case "HZXX" :
//				this.doDyhzxx();
//				break;
//			case "ZG" :
//				this.doClinicFinish(2);
//				break;
//		}
//	},
	showMPIInfo : function() {
		var m = this.midiModules['mpi'];
		if (!m) {
			$import("phis.application.pix.script.EMPIInfoModule")
			m = new phis.application.pix.script.EMPIInfoModule({
						serviceAction : "updatePerson",
						title : "个人基本信息",
						mainApp : this.mainApp,
						modal : true
					});
			m.on("onEmpiReturn", function() {
						m.getWin().hide(); // ** add by yzh 20100724 close the
						// EMPIInfoModule
						this.refreshTopEmpi(); // ** add by yzh 20100708
						// refresh top empiData
						if (this.activeModules["H01"]) // ** add by yzh
							// 20100721 refresh
							// child health record
							this
									.refreshModule(this.mainTab.find("mKey",
											"H01")[0])
					}, this)
			this.midiModules['mpi'] = m;
		}
		m.getWin().show();
		m.clear(); // ** add by yzh 20100818
		// m.setRecord(this.exContext.ids.empiId)
		m.setRecord(this.exContext.empiData.empiId);// add by zhouyl 20130927
	},
	openReferralAppointment : function() {
		if (!this.beforeEnd("JS")) {
			return;
		}
		// 判断35岁病人必须测量血压
		// alert(this.exContext.ids.age)
		if (this.exContext.ids.age >= 35 && !this.hasClinicRecord) {
			var ssy = document.getElementById("SSY").value.trim();// 获得收缩压和舒张压
			var szy = document.getElementById("SZY").value.trim();
			if (ssy == null || szy == null || ssy.length == 0
					|| ssy.length == 0) {
				alert("病人超过35岁,必须测量血压");
				return;
			}
		}
		var getReferralAppointment = this.createModule(
				"getReferralAppointment",
				"phis.application.cic.CIC/CIC/CIC0101");
		this.getReferralAppointment = getReferralAppointment;
		getReferralAppointment.exContext = this.exContext;
		getReferralAppointment.opener = this;
		getReferralAppointment.YYJGTS = this.exContext.systemParams.YYJGTS;
		if (!this.getReferralAppointment_win) {
			var win = getReferralAppointment.getWin();
			win.add(getReferralAppointment.initPanel())
			this.getReferralAppointment_win = win;
		}
		this.getReferralAppointment_win.show();
		this.getReferralAppointment_win.center();
	}
      
		})

// ================ print win ===============
