$package("phis.application.cic.script")
$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory",
		"util.dictionary.DictionaryLoader")
var icos = null;		
phis.application.cic.script.PEMRView = function(cfg) {
	this.westWidth = 180
	this.width = 1000;
	this.height = 580
	this.showemrRootPage = true
	this.closeNav = false
	this.exContext = cfg.exContext || {}
	this.exContext.ids = {
		empiId : cfg.empiId,
		recordId : cfg.recordId,
		clinicId : cfg.clinicId,// 就诊序号
		brid : cfg.brid,// 病人ID
		ghxh : cfg.ghxh,
		age : cfg.age
	};
	// 病人信息刷新会清空this.exContext.ids，复制一份
	this.exContext.idss = {
		empiId : this.exContext.ids.empiId,
		recordId : this.exContext.ids.recordId,
		clinicId : this.exContext.ids.clinicId,// 就诊序号
		brid : this.exContext.ids.brid
		// 病人ID
	};
	this.exContext.empiData = {};
	this.cachePermission = {};
	this.exContext.args = cfg.args || {};
	this.activeTab = cfg.activeTab || 0
	phis.application.cic.script.PEMRView.superclass.constructor.apply(this,
			[cfg])
	this.on("close", this.onClose, this)
	this.on("beforeclose", this.beforeEMRViewClose, this);
	this.JKCFRecords = {};
}
Ext.extend(phis.application.cic.script.PEMRView, phis.script.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}

		var top = this.createTopPanel()
		var nav = this.createNavPanel()
		var tab = this.createTabPanel()
		tab.split = true
		tab.region = "center"
		var panel = new Ext.Panel({
					border : false,
					hideBorders : true,
					frame : false,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [top, nav, tab]
				});
		this.panel = panel
		// 取EMRView系统参数
		this.loadSystemParam();
		return panel
	},
	loadSystemParam : function() {
		var body = {
			"commons" : ['MZYP', 'JSYP', 'EMRVERSION'],
			"privates" : ['QMYZXJY', 'SFJYWBKB', 'YYJGTS',
					'YS_MZ_FYYF_' + this.mainApp.phis.reg_departmentId + '_XY',
					'YS_MZ_FYYF_' + this.mainApp.phis.reg_departmentId + '_ZY',
					'QZWZXJY', 'DYQWZXJY', 'QYCFCZQZTJ', 'QYSXZZ',
					'YS_MZ_FYYF_' + this.mainApp.phis.reg_departmentId + '_CY',
					'YXXJYSXG', 'YSZJS', 'XSFJJJ', 'HQFYYF', 'QYJYBZ',
					'QYTJBGBZ', 'QYKJYWGL', 'QYKJYYY', 'QYSJYSSQ', 'KJYSYTS' , 'QYJCGL'],

			"personals" : ['XSBL', 'BCJG', 'TZYS']
		}
		var rv = this.loadSystemParams(body);
		this.exContext.systemParams = rv;
		this.exContext.systemParams.YS_MZ_FYYF_XY = this.exContext.systemParams['YS_MZ_FYYF_'
				+ this.mainApp.phis.reg_departmentId + '_XY'] == 'null'
				? "0"
				: this.exContext.systemParams['YS_MZ_FYYF_'
						+ this.mainApp.phis.reg_departmentId + '_XY'];
		this.exContext.systemParams.YS_MZ_FYYF_ZY = this.exContext.systemParams['YS_MZ_FYYF_'
				+ this.mainApp.phis.reg_departmentId + '_ZY'] == 'null'
				? "0"
				: this.exContext.systemParams['YS_MZ_FYYF_'
						+ this.mainApp.phis.reg_departmentId + '_ZY'];
		this.exContext.systemParams.YS_MZ_FYYF_CY = this.exContext.systemParams['YS_MZ_FYYF_'
				+ this.mainApp.phis.reg_departmentId + '_CY'] == 'null'
				? "0"
				: this.exContext.systemParams['YS_MZ_FYYF_'
						+ this.mainApp.phis.reg_departmentId + '_CY'];
	},
	onWinShow : function() {

		this.closeAllModules()
		// this.loadTJJYTree();
		this.activeModules = {};
		this.loadEmrTreeNode(true);

		/*
		 * 注释拱墅区版本中有关医保的代码// 查询该病人是否为血透病人
		 * this.doQueryCBRYXXByBrid(this.exContext.ids.brid);
		 */

		this.win.maximize()
		this.refreshTopIcon();
		this.getHasClinicRecord();
	},
	createNavPanel : function() {
		if (this.mainApp.chisActive) {
			this.getTreeFilterChisParameters(1);
		}
		var emrNavTree = util.dictionary.TreeDicFactory.createTree({
					id : "phis.dictionary.emrViewJK"
				});
		this.emrNavTree = emrNavTree;
		emrNavTree.expandAll();
		emrNavTree.on("click", this.onNavTreeClick, this);
		var root = emrNavTree.getRootNode();
		root.on("expand", function(root) {
					var lastNode = root.lastChild;
					lastNode.on("load", function() {
								this.emrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
					lastNode.on("beforeappend", function(emrNavTree, lastNode,
									node) {
								this.emrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
				}, this);
		// var emrNavTree = util.dictionary.TreeDicFactory.createTree({
		// id : ""
		// })
		// this.emrNavTree = emrNavTree
		// // 获取EMR的导航
		// emrNavTree.expandAll()
		// emrNavTree.on("click", this.onNavTreeClick, this)
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
	openEHRModel : function(refModules) {
		var modules = this.ehrView.getEHRModuleCfg(refModules);
		for (var i = 0, len = refModules.length; i < len; i++) {
			var key = refModules[i];
			if (!this.midiModules[key]) {
				var module = modules[key];
				this.emrNavTreePanel.collapse(false);
				var p = module.initPanel();
				p.mKey = key;
				p.key = key;
				p.title = module.title
				p.closable = true;
				p.border = false
				p.frame = false
				this.mainTab.add(p);
				p.__formChis = true;
				this.midiModules[p.mKey] = p;
				this.mainTab.doLayout();
				this.activeModules[key] = true;
				if (module.loadData && module.autoLoadData == false) {
					module.loadData();
				}
				if (key == "D_0101") {
					var result = util.rmi.miniJsonRequestSync({
								serviceId : "chis.chisRecordFilter",
								serviceAction : "getRiskFactors",
								body : {}
							});
					if (result.code != 200) {
						this.processReturnMsg(result.code, result.msg);
						return;
					} else {
						var riskFactors = result.json.riskFactors;
						if (riskFactors && riskFactors.length > 0) {
							if (module && module.initRiskFactors) {
								module.initRiskFactors(riskFactors);
							}
						}
					}
				}
				p.on("destroy", this.onModuleClose, this);
			}
		}
		this.mainTab.activate(refModules[0]);
	},
	onNavTreeClick : function(node, e) {
		if (!node.leaf) {
			return;
		}
		// if (node.id == 'C04' && this.openBy == "nurse") {
		// MyMessageTip.msg("提示", "对不起，病区无法打开会诊申请!", true);
		// return;
		// }
		var key = node.attributes.key;
		// if (!key) {
		// // @@ 打开社区EHRView
		// this.openEHRView(node);
		// return;
		// }
		// var blbh = node.attributes.BLBH;
		// add by yangl emrEditor
		if (node.attributes.emrEditor
				&& node.attributes.emrEditor != "undefined") {
			if (node.attributes.YDLBBM
					&& node.attributes.YDLBBM.indexOf('19') >= 0) {
				this.openEmrEditorModule(node.attributes);
			} else {
				this.mainTab.setActiveTab(0)
				this.midiModules['B01'].openEmrEditorModule(node.attributes);
			}
			return;
		} else {
			if (key) {
				if (key.indexOf("A") != -1) {
					var type = node.attributes.type;
					if (type == "module") {
						var cfg = this.getModuleCfg(key)
						if (cfg.disabled) {
							return;
						}
						if (this.activeModules[key]) {
							var finds = this.mainTab.find("mKey", key)
							if (finds.length == 1) {
								var p = finds[0];
								if (key == "A30") {
									if (!this.exContext.args) {
										this.exContext.args = {};
									}
									this.exContext.args.masterplateTypes = this.mtList;
									this.exContext.args.empiId = this.exContext.ids.empiId;
								}
								this.exContext.args.MS_BRZD_JLBH = this.JLBH || "";
								this.exContext.args.JZXH = this.exContext.ids.clinicId
										|| "";
								this.refreshModule(p, this.exContext);
								this.mainTab.activate(p);
							}
							return;
						}
						cfg.closable = true;
						// cfg.__formChis = true;
						if (key == "A30") {
							if (!this.exContext.args) {
								this.exContext.args = {};
							}
							this.exContext.args.masterplateTypes = this.mtList;
							this.exContext.args.empiId = this.exContext.ids.empiId;
						}
						this.exContext.args.MS_BRZD_JLBH = this.JLBH || "";
						this.exContext.args.JZXH = this.exContext.ids.clinicId
								|| "";
						cfg.exContext = this.exContext
						this.emrNavTreePanel.collapse(false)
						var p = this.mainTab.add(cfg);
						this.mainTab.doLayout()
						this.mainTab.activate(p)
						this.activeModules[key] = true
					} else {
						var ref = node.attributes.ref;

						// 如果是个人健康档案
						if (key == 'A01') {
							// 获取 是否启用公卫系统的参数
							if (!this.SFQYGWXT) {
								var publicParam = {
									"commons" : ['SFQYGWXT']
								}
								this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
							}
							if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
								var flag = false;
								var comreq1 = util.rmi.miniJsonRequestSync({
											serviceId : "chis.CommonService",
											serviceAction : "getPhisShowEhrView",
											method : "execute",
											body : {}
										});
								if (comreq1.code != 200) {
									this
											.processReturnMsg(comreq1.code,
													comreq1.msg);
									return;
								} else {
									if (comreq1.json.PhisShowEhrViewType
											&& comreq1.json.PhisShowEhrViewType == 'paper')
										flag = true;
								}
								if (flag) {
									if (node.attributes.paperRef)
										ref = node.attributes.paperRef;
								}

							}
						}
						//

						var refs = ref.split(',');
						$import("chis.script.EHRView");
						var ehrView = new chis.script.EHRView({
									initModules : refs,
									empiId : this.exContext.ids.empiId,
									mainApp : this.mainApp,
									closeNav : true,
									activeTab : 0
								});
						ehrView.mainTab = this.mainTab;
						ehrView.on("chisSave", this.refreshEMRNavTree, this);
						ehrView.on("ehrAddModule", this.openEHRModel, this);
						this.ehrView = ehrView;
						this.ehrView.emrView = this;
						var len = refs.length
						var refModules = [];
						for (var i = 0; i < len; i++) {
							var key = refs[i].substring(refs[i].lastIndexOf("/")
									+ 1);
							refModules.push(key);
						}
						this.openEHRModel(refModules);
						if (len > 0) {
							var key = refs[0].substring(refs[0].lastIndexOf("/")
									+ 1);
							if (this.activeModules[key]) {
								var finds = this.mainTab.find("mKey", key)
								if (finds.length == 1) {
									var p = finds[0]
									if (key == 'B_011') {
										p.setTitle('个人健康档案');
										var btss = p.topToolbar.items.items;
										for (var ii = 0; ii < btss.length; ii++) {
											if (btss[ii].cmd == 'qk'
													|| btss[ii].cmd == 'close')
												p.topToolbar.remove(btss[ii]);
										}

									}
									this.mainTab.activate(p)
								}
								return;
							}
						}
					}
					return;
				}
				if (key == "B06") {// 诊断编辑为空不能打开检验录入
					var body = {};
					body["JZXH"] = this.exContext.ids.clinicId;
					var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "queryZDLR",
								body : body
							});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return false;
					} else {
						if (r.json.isAllowed == 0) {
							Ext.MessageBox.alert("提示", "请先录入诊断");
							return;
						}
					}
				}
				// add by yuhua
				this.idsParentNode = node.parentNode.attributes.key
				if (!this.exContext.ids[this.exContext.ids.empiId + "_"
						+ this.idsParentNode]) {
					this.exContext.ids.idsLoader = node.parentNode.attributes.idsLoader
					this.exContext.ids[this.exContext.ids.empiId + "_"
							+ this.idsParentNode] = this.exContext.ids.empiId + "_"
							+ this.idsParentNode
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
				if (cfg.showWinOnly == "true" && cfg.key == "C01") {
					var width = 800;
					var module = this.createModule(cfg.key, cfg.ref);
					module.key = cfg.key;
					// module.initDataId = this.exContext.ids.clinicId;
					module.exContext = this.exContext;
					// if (this.exContext.empiData.CYPB > 1) {
					// module.exContext.readOnly = true;
					// }
					if (!this.midiWins[cfg.key]) {
						this.midiWins[cfg.key] = module.getWin();
						module.getWin().add(module.initPanel());
					}
					this.midiWins[cfg.key].setWidth(width);
					this.midiWins[cfg.key].setHeight(module.height || 355);
					this.midiWins[cfg.key].show();
					module.getBRXX("MZHM", this.exContext.empiData.MZHM);
					return;
				}
				if (cfg.showWinOnly == "true" && cfg.key == "D04") {
					var width = 800;
					var module = this.createModule(cfg.key, cfg.ref);
					module.key = cfg.key;
					// module.initDataId = this.exContext.ids.clinicId;
					module.exContext = this.exContext;
					// if (this.exContext.empiData.CYPB > 1) {
					// module.exContext.readOnly = true;
					// }
					if (!this.midiWins[cfg.key]) {
						this.midiWins[cfg.key] = module.getWin();
						module.getWin().add(module.initPanel());
					}
					this.midiWins[cfg.key].setWidth(width);
					this.midiWins[cfg.key].setHeight(module.height || 400);
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
				this.emrNavTreePanel.collapse(false)
				var p = this.mainTab.add(cfg)
				this.mainTab.doLayout()
				this.mainTab.activate(p)
				this.activeModules[key] = true
			}
		}
	},
	showDicInfo : function(paraA, paraB, ctx) {
		var entryName;
		if (paraB) {
			entryName = paraB.toString().split("|")[0];
		}
		if (!entryName) {
			return;
		}
		entryName = "phis.application.cic.schemas." + entryName;
		if (this.midiWins["refDicInfoList_win"]
				&& this.midiModules['refDicInfoList']) {
			if (this.dicParaB != paraB) {
				this.midiModules['refDicInfoList'].paraB = paraB;
				this.midiModules['refDicInfoList'].entryName = entryName;
				this.midiModules['refDicInfoList'].replaceCmData();
				this.dicParaB = paraB;
			}
			this.midiModules['refDicInfoList'].paraA = paraA;
			this.midiWins["refDicInfoList_win"].setHeight(300);
			this.midiWins["refDicInfoList_win"].setWidth(520);
			this.midiWins["refDicInfoList_win"].setPosition(420, 120);
			this.midiWins["refDicInfoList_win"].show();
			return;
		}
		var module = this.createModule("refDicInfoList",
				"phis.application.war.WAR/WAR/WAR81");
		module.exContext = this.exContext;
		module.paraA = paraA;
		module.paraB = paraB;
		module.entryName = entryName;
		this.dicParaB = paraB;
		module.parent = ctx;
		module.on("appoint", ctx.setElementValue, ctx);
		var win = module.getWin();
		this.midiWins["refDicInfoList_win"] = win;
		win.setHeight(300);
		win.setWidth(520);
		win.setPosition(420, 120);
		// ctx.hideEmr();
		win.show();
	},

	loadEmrTreeNode : function(firstOpen) {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "phis.emrManageService",
					serviceAction : "loadEMRNavTree",
					body : {
						JZH : this.exContext.ids.clinicId,
						empiId : this.exContext.ids.empiId
					}
				});
		if (result.code >= 300) {
			this.processReturnMsg(result.code, result.msg);
			return;
		}
		var root = this.emrNavTree.getRootNode();
		if (this.blNode) {
			this.blNode.remove();
		}
		if (this.ehrNode) {
			this.ehrNode.remove();
		}
		this.initEmrLastNodes = {};
		var blNode = new Ext.tree.TreeNode({
					key : "D",
					text : "病历书写",
					expend : true
				});
		this.blNode = blNode;
		var emrNodeList = result.json.emrTree;
		var ehrNodeList = result.json.ehrTree;
		this.getEMRChildNode(blNode, emrNodeList);
		if (root.hasChildNodes()) {
			if (root.findChild('id', 'A')
					&& root.findChild('id', 'A').isFirst()) {
				root.insertBefore(blNode, root.findChild('id', 'A'));
			} else {
				root.appendChild(blNode);
			}
		} else {
			root.appendChild(blNode);
		}
		root.expandChildNodes(true);
		this.getEHRChildNode(root, ehrNodeList);
		if (firstOpen) {
			// 备份恢复
			this.recoverRecord();
			// 快捷打开
			this.loadTJJYTree(firstOpen);
			if (!this.tabsInited) {
				this.emrN = this.initEmrLastNodes[this.initbl];
				this.initTabItems("B01");
			}
		} else {
			this.loadTJJYTree();
		}
	},
	getEMRChildNode : function(pNode, nodeList, needOpen) {
		for (var i = 0; i < nodeList.length; i++) {
			var node = nodeList[i];
			var n = new Ext.tree.TreeNode({
						key : node.ID,
						text : node.TEXT,
						DYWD : node.DYWD,
						BLBH : node.BLBH,
						BLLX : node.BLLX,
						XSMC : node.XSMC,
						LBMC : node.LBMC,
						MBMC : node.MBMC,
						MBLB : node.MBLB,
						YDLBBM : node.YDLBBM,
						emrEditor : node.emrEditor ? true : false,
						leaf : (node.children) ? false : true
					});
			if (needOpen) {
				if (this.needOpenNode) {
					this.needOpenNode.push(n.attributes);
				} else {
					this.needOpenNode = []
					this.needOpenNode.push(n.attributes);
				}
			}
			if (node.YDLBBM == '17') {
				this.initbl = node.ID;
			}
			if (node.children && node.YDLBBM == '17') {
				this.getEMRChildNode(n, node.children, true);
			} else if (node.children) {
				this.getEMRChildNode(n, node.children, false);
			} else {
				this.initEmrLastNodes[node.ID] = n.attributes;
				if ((i == nodeList.length - 1)) {
					this.initEmrLastNodes[node.ID] = n.attributes;
				}
			}
			pNode.appendChild(n);
		}
	},
	getTJChildNode : function(pNode, nodeList) {
		for (var i = 0; i < nodeList.length; i++) {
			var node = nodeList[i];
			var n = new Ext.tree.TreeNode({
						id : node.key,
						key : node.key,
						text : node.text,
						requireKeys : node.requireKeys,
						ref : node.ref,
						tjEditor : true,
						leaf : (node.children) ? false : true
					});
			if (node.children) {
				this.getTJChildNode(n, node.children);
			} else {
				this.initEmrLastNodes[node.key] = n.attributes;
				if ((i == nodeList.length - 1)) {
					this.initEmrLastNodes[node.key] = n.attributes;
				}
			}
			pNode.appendChild(n);
		}
	},
	getEHRChildNode : function(pNode, nodeList) {
		if (!nodeList[0]) {
			return;
		}
		var ehrNode = new Ext.tree.TreeNode({
					key : nodeList[0].key,
					text : nodeList[0].text,
					expend : nodeList[0].expanded,
					pkey : nodeList[0].pkey
				});
		this.ehrNode = ehrNode;
		pNode.appendChild(ehrNode);
		pNode.expandChildNodes(true);
		var childrenList = nodeList[0].children;
		if (childrenList) {
			for (var i = 0; i < childrenList.length; i++) {
				var node = childrenList[i];
				var leaf = (node.leaf) ? true : false;
				var n = new Ext.tree.TreeNode({
							id : node.id,
							text : node.text,
							mustDo : node.mustDo,
							actionType : node.actionType,
							leaf : leaf
						});
				if (node.leaf) {
					this.getEHRChildNode(n, node.leaf);
				}
				ehrNode.appendChild(n);
			}
		}
	},
	recoverRecord : function() {
		var emr = document.getElementById("emrOcx_hide");
		try {
			if (!emr
					|| emr.FunActiveXInterface("BsGetFileVersion", '', '') !== 0)
				return;
		} catch (e) {
			return;
		}
		emr.FunActiveXInterface("BsEditAutoData", '9', '');
		if (emr.WordData) {
			var bl01s = [];
			var retArray = eval("[" + emr.WordData + "]")
			for (var i = 0; i < retArray.length; i++) {
				var t = eval(retArray[i]);
				if (t.JZXH == this.exContext.ids.clinicId
						&& t.SXYS == this.mainApp.uid) {
					bl01s.push(t);
				}
			}
			if (bl01s.length > 0) {
				this.showRecoverRecord(bl01s, emr);
			}
		}
	},
	// 数据盒
	showEmrUserDataBox : function(ctx) {
		var moduleCfg = this
				.loadModuleCfg("phis.application.cic.CIC/CIC/CIC37");
		var cfg = {
			showButtonOnTop : true,
			border : false,
			frame : false,
			autoLoadSchema : false,
			isCombined : true,
			exContext : this.exContext
		};
		Ext.apply(cfg, moduleCfg);
		var cls = moduleCfg.script;
		if (!cls) {
			return;
		}
		$import(cls);
		var module = eval("new " + cls + "(cfg)");
		module.setMainApp(this.mainApp);
		module.opener = this;
		module.exContext = this.exContext;
		module.parent = ctx;
		module.on("appoint", ctx.loadAppoint, ctx);
		var win = module.getWin();
		this.midiWins["emrUserDataBox_win"] = win;
		win.show();
	},
	// 显示常用语
	showUserFul : function(ctx) {
		var module = this.createModule("refUserFulList",
				"phis.application.war.WAR/WAR/WAR90");
		module.exContext = this.exContext;
		// module.on("close", function() {
		// ctx.showEmr();
		// }, this);
		module.emr = ctx.emr;
		module.emr_parent = ctx;
		module.KSDM = this.exContext.empiData.BRKS;
		var win = module.getWin();
		if (win.isVisible()) {
			win.toFront();
			return;
		}
		this.midiWins["refUserFulList_win"] = win;
		win.setHeight(450);
		win.setWidth(240);
		win.setPosition(2000, 100);
		win.show();
	},
	showModifyRecord : function(blbh, ctx) {
		var module = this.createModule("modifyRecordList",
				"phis.application.war.WAR/WAR/WAR41");
		module.blbh = blbh;
		module.opener = this;
		module.exContext = this.exContext;
		module.on("close", function() {
					if (ctx) {
						ctx.showEmr();
					}
				}, this);
		module.emr = ctx.emr;
		var win = module.getWin();
		this.midiWins["modifyRecordList_win"] = win;
		win.setHeight(505);
		win.setWidth(1000);
		win.show();
	},
	// 显示未填写的元素列表
	showUnSetElements : function(data, ctx) {
		var module = this.createModule("unsetElementList",
				"phis.application.war.WAR/WAR/WAR42");
		module.exContext = this.exContext;
		// module.on("close", function() {
		// ctx.showEmr();
		// }, this);
		module.emr = ctx.emr;
		var win = module.getWin();
		module.loadDataByLocal(data);
		if (win.isVisible()) {
			win.toFront();
			return;
		}
		this.midiWins["unsetElementList_win"] = win;
		win.setHeight(550);
		win.setWidth(270);
		win.setPosition(2000, 100);
		win.show();
	},
	// 电子病历打印预览
	showEmrPrint : function(ctx, printType, paraA, paraB, printBlbh) {
		var module = this.createModule("emrPrintViewModule",
				"phis.application.war.WAR/WAR/WAR43");
		module.exContext = this.exContext;
		module.parent = ctx;
		module.printType = printType;
		module.paraA = paraA;
		module.paraB = paraB;
		module.printBlbh = printBlbh;
		var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "emrManageService",
					serviceAction : "loadPrtSetup",
					body : {
						BLLB : ctx.node.key
					}
				})
		if (res.code >= 300) {
			MyMessageTip.msg("提示", res.msg, true);
			return;
		}
		module.cachePrtSetup = res.json.body;
		var win = module.getWin();
		this.midiWins["emrPrintView_win"] = win;
		win.show();
		// win.maximize();
	},
	showEmrPersonalForm : function(ctx, blbh) {
		var module = this.createModule("emrPersonalForm",
				"phis.application.war.WAR/WAR/WAR44");
		module.on("close", function() {
					ctx.showEmr();
				}, this);
		module.exContext = this.exContext;
		module.parent = ctx;
		module.BLBH = blbh;
		module.openBy = "clinic";// 门诊打开
		var win = module.getWin();
		this.midiWins["emrPersonalForm_win"] = win;
		win.setHeight(240);
		win.setWidth(370);
		win.show();
	},
	openEmrEditorModule : function(node, recoveredBl01, firstOpen) {
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
		// var key = "phis.application.war.WAR/WAR/WAR36";
		var key = "phis.application.cic.CIC/CIC/CIC29";
		// if (node.key == 2000001) {
		// key = "phis.application.war.WAR/WAR/WAR21"
		// }
		if (this.activeModules[key + nodeId]) {
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
		var m = this.createModule(key + nodeId, key);
		m.recoveredBl01 = recoveredBl01;
		if (m.initPanel) {
			m.exContext = this.exContext
			m.mKey = key + nodeId;
			m.opener = this;
			m.node = node;
			// m.on("activeModule", this.onActiveModule, this);
			m.on("refreshModule", this.onRefreshModule, this)
			m.on("clearCache", this.onClearCache, this)
			var p = m.initPanel();
			if (firstOpen) {
				p.closable = false;
				m.closable = false;
			} else {
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
			tab.on("destroy", this.onModuleClose, this)
			this.mainTab.doLayout()
			this.mainTab.activate(p)
			this.activeModules[key + nodeId] = true;
		}
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
	getNewTopTemplate : function() {
		if (this.tpl) {
			return this.tpl;
		}
		$styleSheet("phis.resources.css.app.biz.style")
		debugger;
		this.ehrviewId = Ext.id();
		var photoUrl = ClassLoader.appRootOffsetPath + "{photo}"
		var tpl = new Ext.XTemplate(
				'<div class="head_bg">',
				'<div id="emr_header">',
				'<div id="topMenu_out"><div id="topMenu_in">',
				'<ul class="top_menu">',
				/**
				 * 2014-1-15号，与张伟确认先将病人列表隐藏 <a href="#" style="width:76px;">病人列表</a></li>|
				 */
				/**
				 * 2014-2-12，放出病人列表
				 */
				/**
				 * 2014-2-12，放出病人列表end
				 */
				'<a style="width:74px;" class="topLine" id="MPI">个人信息</a>|<a style="width:74px;" class="topLine" id="PZYYY">住院预约</a>|<a style="width:74px;" class="topLine" id="ZG">暂挂</a>|<a style="width:74px;" class="topLine" id="CLOSE">就诊结束</a>|<a style="width:74px;" class="topLine" id="HP">帮助</a>',
				'</ul>',
				'</div></div>',
				'<div class="TopMessage">',
				'<img src="' + photoUrl
						+ '" width="71" height="71"  class="fleft photo" />',
				'<div  class="fleft" style="width: 1009px;">',

				'<div class="ehrviewClear">',
				'<h2 class="fleft">{personName}</h2>',
				'<div class="fleft" style="margin:0px 80px 0px 0px">&nbsp;</div>',
				'<div id="recordIcoDiv_' + this.ehrviewId
						+ '" class="ehrviewInfo_ico"></div>',

				'<div style="margin:4px 0px 0px 0px"><label class="fleft"><a id="skinTest" class="topLine"><img id="skinTest" title="查看皮试历史记录" src="'
						+ ClassLoader.appRootOffsetPath
						+ 'photo/M.png" class="fleft"/></a></label>',
				'</div></div>',

				'<ul class="mdetail">',
				'<li class="width15">',
				'<p><label>性&nbsp;&nbsp;&nbsp;&nbsp;别：</label>{sexCode_text}</p>',
				'<p><label>出生日期：</label>{birthday}</p>',
				'</li>',
				'<li class="width15">',
				'<p><label>门诊号码：</label>{MZHM}</p>',
				'<p><label>责任医生：</label>{manaDoctorId_text}</p>',
				'</li>',
				'<li class="width20">',
				'<p><label>病人性质：</label>{BRXZ_text}</p>',
				'<p><label>身份证号：</label>{idCard}</p>',
				'</li>',
				'<li class="none width30">',
				'<p><label>电&nbsp;&nbsp;&nbsp;&nbsp;话：</label>{phoneNumber}</p>',
				'<p><label>住&nbsp;&nbsp;&nbsp;&nbsp;址：</label><span title="{address}">{address:substr(0,15)}<tpl if="address.length &gt; 3">...</tpl></span></p>',
				'</li>', '</ul>', '</div>', '</div>', '</div></div>');
		this.tpl = tpl
		return tpl;
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
		var tpl = this.getNewTopTemplate()
		if (!this.exContext.empiData.address) {
			this.exContext.empiData.address = "";
		}
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
	attachTopLnkEnvents : function() {
		var lnks = this.topPanel.body.query("a.topLine")
		if (lnks) {
			for (var i = 0; i < lnks.length; i++) {
				var lnk = Ext.get(lnks[i])
				lnk.on("click", this.onTopLnkClick, this)
			}
		}
	},
	dettachTopLnkEnvents : function() {
		var lnks = this.topPanel.body.query("a.topLine")
		if (lnks) {
			for (var i = 0; i < lnks.length; i++) {
				var lnk = Ext.get(lnks[i])
				lnk.un("click", this.onTopLnkClick, this)
			}
		}
	},
	onTopLnkClick : function(e) {
		var lnk = e.getTarget();
		var cmd = lnk.id
		switch (cmd) {
			case "HP" :
				window.open("resources/phis/help/BS-PHIS-HELP.html");
				break;
			// case "PRT" :
			// this.showPrintView()
			// break
			case "SWH" :
				break
			case "skinTest" :
				this.showSkinTest();
				break
			case "MPI" :
				this.showMPIInfo()
				break;
			case "PZYYY" :
				this.showPZYYYInfo()
				break;
			case "NAV" :
				var navPanel = this.panel.items.item(1)
				var status = navPanel.isVisible()
				if (status) {
					navPanel.collapse()
				} else {
					navPanel.expand()
				}
				break;
			case "HME" :
				this.mainTab.setActiveTab(0)
				break;
			case "CLOSE" :
				this.openReferralAppointment();
				break;
			case "HZXX" :
				this.doDyhzxx();
				break;
			case "ZG" :
				this.doClinicFinish(2);
				break;
		}
	},
	doClinicFinish : function(type) {
		if (!this.beforeEnd("ZG")) {
			return;
		}
		var msg = type == 2 ? '确认要进行暂挂操作吗?' : '确认要结束就诊吗?';
		Ext.Msg.confirm("提示", msg, function(btn) {
					if (btn == "yes") {
						if (!this.doSave(true)) {
							return false;
						}
						if (type == 2) {
							this.closeing = true
						}
						this.clinicFinish(type);
					}
				}, this);
	},
	clinicFinish : function(type) {
		// 结束就诊，暂时只实现更新就诊状态
		this.panel.el.mask("处理中...");
		// if (this.isModify) {// 如果病历数据有修改，则调用保存
		// this.doSave(true);
		// }
		// alert(this.BRQX);
		phis.script.rmi.jsonRequest({
					serviceId : "clinicManageService",
					serviceAction : "saveClinicFinish",
					body : {
						JZXH : this.exContext.idss.clinicId,// add by
						// zhouyl
						JZZT : type,
						BRID : this.exContext.ids.brid,
						BRQX : this.BRQX,
						JKJY : this.JKJY
					}
				}, function(code, msg, json) {
					this.panel.el.unmask();
					if (code < 300) {
						// 关闭EMRView
						// if (!this.emrview.win.closeing) {
						if (type == 9) {
							this.getReferralAppointment_win.hide();
						}
						this.doClose();
						// }
					} else {
						this.processReturnMsg(code, msg);
					}
				}, this);
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
	},
	doSave : function(noMess) {
		this.midiModules['B01'].midiModules[this.BLSY].emr.FunActiveXInterface(
				'BsDocGetElemvalue', 'element..体格检查', "#收缩压");
		var ssy = this.midiModules['B01'].midiModules[this.BLSY].emr.StrReturnData;
		if (!isNaN(ssy)) {
			this.SSY = ssy;
		}
		this.midiModules['B01'].midiModules[this.BLSY].emr.FunActiveXInterface(
				'BsDocGetElemvalue', 'element..体格检查', "#舒张压");
		var szy = this.midiModules['B01'].midiModules[this.BLSY].emr.StrReturnData;
		if (!isNaN(szy)) {
			this.SZY = szy;
		}
		// 保存病历信息ms_bcjl
		this.data = {};
		// this.data.ZSXX = document.getElementById("ZSXX").value;
		// this.data.XBS = document.getElementById("XBS").value;
		// this.data.JWS = document.getElementById("JWS").value;
		// this.data.TGJC = document.getElementById("TGJC").value;
		// this.data.FZJC = document.getElementById("FZJC").value;
		// this.data.T = document.getElementById("T").value;
		// this.data.R = document.getElementById("R").value;
		// this.data.P = document.getElementById("P").value;
		this.data.SSY = this.SSY;
		this.data.SZY = this.SZY;
		// this.data.KS = document.getElementById("KS").checked == true
		// ? 1
		// : 0;
		// this.data.YT = document.getElementById("YT").checked == true
		// ? 1
		// : 0;
		// this.data.HXKN = document.getElementById("HXKN").checked == true
		// ? 1
		// : 0;
		// this.data.OT = document.getElementById("OT").checked == true
		// ? 1
		// : 0;
		// this.data.FT = document.getElementById("FT").checked == true
		// ? 1
		// : 0;
		// this.data.FX = document.getElementById("FX").checked == true
		// ? 1
		// : 0;
		// this.data.PZ = document.getElementById("PZ").checked == true
		// ? 1
		// : 0;
		// this.data.QT = document.getElementById("QT2").checked == true
		// ? 1
		// : 0;
		this.data.JZXH = this.exContext.ids.clinicId;
		this.data.BRID = this.exContext.ids.brid;
		/**
		 * modified by gaof 2014-1-10 刷新病人信息后无法保存
		 */
		if (!this.data.JZXH) {
			this.data.JZXH = this.exContext.idss.clinicId;
		}
		if (!this.data.BRID) {
			this.data.BRID = this.exContext.idss.brid;
		}
		this.data.JKCF = this.JKCFRecords;
		/**
		 * modified by gaof 2014-1-10 end
		 */
		this.panel.el.mask("保存中...");
		phis.script.rmi.jsonRequest({
					serviceId : "clinicManageService",
					serviceAction : "saveMsBcjl",
					body : this.data
				}, function(code, msg, json) {
					this.panel.el.unmask();
					if (code < 300) {
						// this.isModify = false;
						if (noMess) {
							return;
						}
						// EMRView判断是否增加高血压疑似记录
						if (this.SSY >= 140 || this.SZY >= 90) {
							if (this.mainApp.chisActive) {
								var body = {
									empiId : this.exContext.ids.empiId,
									constriction : this.SSY,
									diastolic : this.SZY
								}
								util.rmi.jsonRequest({
									serviceId : "chis.chisRecordFilter",
									serviceAction : "saveHypertensionSuspectedCase",
									method : "execute",
									body : body
								}, function(code, msg, json) {
									if (code > 300) {
										this.processReturnMsg(code, msg);
										return
									}
								});
							}
						}
						// by
						// CXR
						MyMessageTip.msg("提示", "病历信息保存成功!", true);
						// if (json.body) {
						// var JKCFRecords = json.body.JKCFRecords;
						// this.createAllJKJYHTML(JKCFRecords);
						// // document.getElementById("div_JKJY").innerHTML =
						// jkjyHtml;
						// }
					} else {
						this.processReturnMsg(code, msg);
					}
				}, this);
		return true;
	},
	doClose : function() {
		this.win.hide();
	},
	showSkinTest : function() {
		module = this.createModule("skintestHistroyList",
				"phis.application.cic.CIC/CIC/CIC0103");
		module.exContext = this.exContext;
		module.getWin().show();
	},
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
	refreshEMRNavTree : function() {
		if (this.mainApp.chisActive) {
			this.treeNodeStatusMap = "";
			this.getTreeFilterChisParameters(2);
			this.emrNavTree.filter.filterBy(this.filterNavTree, this)
		}
	},
	chisModelSave : function(msg) {
		if (msg) {
			MyMessageTip.msg("提示", msg, true);
		}
		this.refreshEMRNavTree()
	},
	CMRSave : function(data) {
		var SSY = data.SSY;
		var SZY = data.SZY;
		if (SSY >= 140 || SZY >= 90) {
			if (this.mainApp.chisActive) {
				var body = {
					empiId : this.exContext.ids.empiId,
					constriction : SSY,
					diastolic : SZY
				}
				util.rmi.jsonRequest({
							serviceId : "chis.chisRecordFilter",
							serviceAction : "saveHypertensionSuspectedCase",
							method : "execute",
							body : body
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveRequest],
										json.body);
								return
							}
						});
			}
		}
		// this.refreshEMRNavTree();
	},
	getTreeFilterChisParameters : function(type) {
		if(this.SFQYGWXT != "1")
		{return;}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "phis.publicService",
					serviceAction : "getDiagnosisInfoForCHIS",
					method : "execute",
					body : {
						empiId : this.exContext.ids.empiId,
						JZXH : '' + this.exContext.ids.clinicId
					}
				});
		var GXY = false;
		var TLB = false;
		var BGK = false;
		var HYSC = false;
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
		} else {
			var resBody = result.json.body;
			GXY = resBody.GXY;
			TLB = resBody.TLB;
			BGK = resBody.BGK;
			// HYSC=resBody.HYSC;
		}
		var body = {
			empiId : this.exContext.ids.empiId,
			JZXH : '' + this.exContext.ids.clinicId,
			GXY : GXY,
			TLB : TLB,
			BGK : BGK,
			HYSC : HYSC
		};
		phis.script.rmi.jsonRequest({
					serviceId : "chis.chisRecordFilter",
					serviceAction : "getNodeFilterParameters",
					method : "execute",
					body : body
				}, function(code, msg, json) {
					if (code > 300) {
						return;
					}
					var resBody = json.body;
					if (!this.treeNodeStatusMap) {
						this.treeNodeStatusMap = resBody;
					} else {
						Ext.apply(this.treeNodeStatusMap, resBody);
					}
					this.mtList = {};
					this.mtList = resBody.mtList;
					var rsMsgList = resBody.rsMsgList;
					this.emrNavTree.filter.filterBy(this.filterNavTree, this);
					if (type != 2) {
						this.showTSMsg(rsMsgList);
					}
				}, this);
	},
	showTSMsg : function(rsMsgList) {
		if (rsMsgList.length > 0) {
			var ts = [];
			for (var i = 0, len = rsMsgList.length; i < len; i++) {
				var nd = rsMsgList[i];
				ts.push(nd.nodeName);
			}
			var msg = "该病人有如下档案需要完善，请点击左侧菜单执行:<div style='margin-left:10px;'><font color='red'>"
					+ ts.join("<br/>") + "</font></div>";
			MyMessageTip.msg("提示", msg, true, 120);
		}
	},
	filterNavTree : function(node) {
		var key = node.attributes.key
		if ((key + "").indexOf("A") != -1) {
			if (!this.treeNodeStatusMap) {
				return false;
			}
			var currRoleId = this.mainApp.jobtitleId;
			var allowRole = "50,51,52,53,55,56";
			var trafficPermit = false;
			if (allowRole.indexOf(currRoleId.substring(currRoleId.indexOf(".")
					+ 1)) > -1) {
				trafficPermit = true;
			}
			if (!trafficPermit) {
				return false;
			}
			if (this.mainApp.chisActive) {
				var nodeStatus = "";
				if (key == "A") {
					nodeStatus = this.treeNodeStatusMap["A"];
				} else {
					if (node.attributes.ref) {
						var ref = node.attributes.ref;
						nodeStatus = this.treeNodeStatusMap[ref];
					}
				}
				if (nodeStatus == "create") {
					if (currRoleId == "phis.50") {
						if (!node.attributes.changeNodeText) {
							node.setText("<font style='color:red'>需创建"
									+ node.attributes.text + "</font>");
							node.attributes.changeNodeText = true;
						}
						nodeStatus = "";
						return true;
					} else {
						return false;
					}
				} else if (nodeStatus == "read") {
					var nodeText = node.attributes.text;
					if (key == 'A') {
						nodeStatus = "";
						return true;
					}
					if (node.attributes.icos) {
						nodeStatus = "";
						return false;
					}
					if (nodeText.indexOf("需创建") != -1) {
						var si = nodeText.indexOf('>') + 4;
						var ei = nodeText.lastIndexOf('<');
						node.setText(nodeText.substring(si, ei));
					}
					nodeStatus = "";
					return false;
				} else if (nodeStatus == "hide") {
					nodeStatus = "";
					return false;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		else if ((key+"").indexOf("C") != -1) {
			// 判断家床是否启用
			if (this.exContext.systemParams.QYJCGL != 1)
				return false;
		}
		return true;
	},
	createTabPanel : function() {
		var items = []
		// if (this.showemrRootPage) {
		// items.push(this.createemrRootPage())
		// }

		var mainTab = new Ext.TabPanel({
					frame : false,
					border : false,
					deferredRender : false,
					// layoutOnTabChange:true,
					activeTab : this.activeTab,
					enableTabScroll : true,
					items : items,
					defaults : ({
						// 解决tab切换时电子病历控件被销毁的问题
						hideMode : 'offsets'
					})
				})
		mainTab.on("tabchange", this.onModuleActive, this)
		this.mainTab = mainTab;
		return mainTab;
	},
	// onNavTreeClick : function(node, e) {
	// if (!node.leaf) {
	// return;
	// }
	// if (node.attributes.key) {
	// var key = node.attributes.key;
	// if (key.indexOf("A") != -1) {
	// var type = node.attributes.type;
	// if (type == "module") {
	// var cfg = this.getModuleCfg(key)
	// if (cfg.disabled) {
	// return;
	// }
	// if (this.activeModules[key]) {
	// var finds = this.mainTab.find("mKey", key)
	// if (finds.length == 1) {
	// var p = finds[0]
	// this.mainTab.activate(p)
	// }
	// return;
	// }
	// cfg.closable = true;
	// // cfg.__formChis = true;
	// cfg.exContext = this.exContext
	// this.emrNavTreePanel.collapse(false)
	// var p = this.mainTab.add(cfg);
	// this.mainTab.doLayout()
	// this.mainTab.activate(p)
	// this.activeModules[key] = true
	// } else {
	// var ref = node.attributes.ref;
	// var refs = ref.split(',');
	// $import("chis.script.EHRView");
	// var ehrView = new chis.script.EHRView({
	// initModules : refs,
	// empiId : this.exContext.ids.empiId,
	// mainApp : this.mainApp,
	// closeNav : true,
	// activeTab : 0
	// });
	// ehrView.mainTab = this.mainTab;
	// ehrView.on("chisSave", this.refreshEMRNavTree, this);
	// this.ehrView = ehrView;
	// var len = refs.length
	// for (var i = 0; i < len; i++) {
	// var key = refs[i].substring(refs[i].lastIndexOf("/")
	// + 1);
	// if (!this.midiModules[key]) {
	// var cfg = ehrView.getEHRModuleCfg(key);
	// this.emrNavTreePanel.collapse(false);
	// var p = cfg.initPanel();
	// p.mKey = key;
	// p.key = key;
	// this.mainTab.add(p);
	// p.__formChis = true;
	// this.midiModules[p.mKey] = p;
	// this.mainTab.doLayout();
	// this.mainTab.activate(p);
	// this.activeModules[key] = true;
	// if (cfg.loadData && cfg.autoLoadData == false) {
	// cfg.loadData();
	// }
	// p.on("destroy", this.onModuleClose, this);
	// } else {
	// this.mainTab.activate(key);
	// }
	// }
	// if (len > 0) {
	// var key = refs[0].substring(refs[0].lastIndexOf("/")
	// + 1);
	// if (this.activeModules[key]) {
	// var finds = this.mainTab.find("mKey", key)
	// if (finds.length == 1) {
	// var p = finds[0]
	// this.mainTab.activate(p)
	// }
	// return;
	// }
	// }
	// }
	// return;
	// }
	// if (key == "B04") {// 诊断编辑为空不能打开检验录入
	// var body = {};
	// body["JZXH"] = this.exContext.ids.clinicId;
	// var r = phis.script.rmi.miniJsonRequestSync({
	// serviceId : "clinicManageService",
	// serviceAction : "queryIsAllowed",
	// body : body
	// });
	// if (r.code > 300) {
	// this.processReturnMsg(r.code, r.msg);
	// return false;
	// } else {
	// if (r.json.isAllowed == 0) {
	// Ext.MessageBox.alert("提示", "请先录入诊断");
	// return;
	// }
	// }
	// }
	// // add by yuhua
	// this.idsParentNode = node.parentNode.attributes.key
	// if (!this.exContext.ids[this.exContext.ids.empiId + "_"
	// + this.idsParentNode]) {
	// this.exContext.ids.idsLoader = node.parentNode.attributes.idsLoader
	// this.exContext.ids[this.exContext.ids.empiId + "_"
	// + this.idsParentNode] = this.exContext.ids.empiId + "_"
	// + this.idsParentNode
	// var data = this.loadEmpiInfo()
	// if (data) {
	// Ext.apply(this.exContext.ids, data.ids);
	// Ext.apply(this.exContext.empiData, data.empiData);
	// }
	// }
	// var cfg = this.getModuleCfg(key)
	// if (cfg.disabled) {
	// return;
	// }
	//
	// if (this.activeModules[key]) {
	// var finds = this.mainTab.find("mKey", key)
	// if (finds.length == 1) {
	// var p = finds[0]
	// this.mainTab.activate(p)
	// }
	// return;
	// }
	//
	// cfg.closable = true;
	// cfg.exContext = this.exContext
	// this.emrNavTreePanel.collapse(false)
	// var p = this.mainTab.add(cfg)
	// this.mainTab.doLayout()
	// this.mainTab.activate(p)
	// this.activeModules[key] = true
	// }
	// },
	dowinclose : function() {
		this.tree.getLoader().load(this.root);
	},
	initTabItems : function(key) {
		var items = []
		var ims = this.initModules
		var empiId = this.exContext.ids["empiId"]
		var phrId = this.exContext.ids["phrId"]
		// this.closeAllModules()
		if (!this.activeModules) {
			this.activeModules = {};
		}
		if (!phrId) {
			this.mainTab.add(this.getModuleCfg(key))
			this.activeModules[key] = true
		}

		if (ims) {
			for (var i = 0; i < ims.length; i++) {
				var im = ims[i]
				if (!this.activeModules[im]) {
					this.mainTab.add(this.getModuleCfg(im))
					this.activeModules[im] = true
				}
			}
		}

		// this.zdactiveModules = activeModules
		// this.mainTab.doLayout()
		// this.win.doLayout()
		this.mainTab.setActiveTab(this.activeTab);
		this.tabsInited = true;
	},
	// initcfTabItems : function() {
	// var items = []
	// var ims = this.initModules
	// var empiId = this.exContext.ids["empiId"]
	// var phrId = this.exContext.ids["phrId"]
	// // this.closeAllModules()
	// // var activeModules = {}
	// if (!phrId) {
	// this.mainTab.add(this.getModuleCfg("B04"))
	// this.activeModules["B04"] = true
	// }
	//
	// if (ims) {
	// for (var i = 0; i < ims.length; i++) {
	// var im = ims[i]
	// if (!this.activeModules[im]) {
	// this.mainTab.add(this.getModuleCfg(im))
	// this.activeModules[im] = true
	// }
	// }
	// }
	//
	// // this.cfactiveModules = activeModules
	// // this.mainTab.doLayout()
	// // this.win.doLayout()
	// // this.mainTab.setActiveTab(this.activeTab);
	// // this.tabsInited = true;
	// },
	// initczTabItems : function() {
	// var items = []
	// var ims = this.initModules
	// var empiId = this.exContext.ids["empiId"]
	// var phrId = this.exContext.ids["phrId"]
	// // this.closeAllModules()
	// // var activeModules = {}
	// if (!phrId) {
	// this.mainTab.add(this.getModuleCfg("B05"))
	// this.activeModules["B05"] = true
	// }
	//
	// if (ims) {
	// for (var i = 0; i < ims.length; i++) {
	// var im = ims[i]
	// if (!this.activeModules[im]) {
	// this.mainTab.add(this.getModuleCfg(im))
	// this.activeModules[im] = true
	// }
	// }
	// }
	//
	// // this.activeModules = activeModules
	// // this.mainTab.doLayout()
	// // this.win.doLayout()
	// // this.mainTab.setActiveTab(this.activeTab);
	// this.tabsInited = true;
	// },
	refreshModule : function(newTab, exContext) {
		var key = newTab.mKey;
		var m = this.midiModules[key]
		if (m) {
			if (exContext) {
				delete m.exContext;
				m.exContext = exContext;
			}
			// ** add by yzh
			m.exContext.readOnly = this.exContext.ids[m.readOnlyKey];
			if (m.loadData) {
				m.loadData()
			} else if (m.refresh) {
				m.refresh();
			}
			newTab.__actived = true;
		}
	},
	/**
	 * 清空模块中的ids及empiData数据
	 * 
	 * @param {}
	 *            m
	 */
	clearModuleData : function(m) {
		// m.exContext.ids = {};
		m.exContext.empiData = {};
	},

	onModuleActive : function(tabPanel, newTab, curTab) {
		if (!newTab) {
			return;
		}
		if ((newTab.mKey == 'B03' || newTab.mKey == 'B04' || newTab.mKey == 'B05')
				&& this.midiModules[newTab.mKey]) {
			var phModule = this.midiModules[newTab.mKey];
			phModule.onTabShow();
		}
		if (newTab.__formChis) {
			return;
		}
		if (newTab.__actived) {
			return;
		}
		var acM = this.midiModules[newTab.mKey];
		if (acM && acM.refreshWhenTabChange) {
			acM.refreshWhenTabChange();
		};
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
						m.on("save", this.onModuleSave, this)
						m.on("activeModule", this.onActiveModule, this);
						m.on("refreshModule", this.onRefreshModule, this)
						m.on("clearCache", this.onClearCache, this)
						m.on("chisSave", this.chisModelSave, this);
						m.on("CMRSave", this.CMRSave, this);
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
					if ((newTab.mKey == 'B03' || newTab.mKey == 'B05')
							&& this.midiModules[newTab.mKey]) {
						var phModule = this.midiModules[newTab.mKey];
						phModule.onWinShow();
					}
						// this.mainTab.doLayout()
				}, this])
	},
	loadModuleCfg : function(id) {
		var result = phis.script.rmi.miniJsonRequestSync({
					url : 'app/loadModule',
					id : id
				})
		if (result.code != 200) {
			if (result.msg == "NotLogon") {
				this.mainApp.logon(this.loadModuleCfg, this, [id])
			}
			return null;
		}
		var m = result.json.body
		Ext.apply(m, m.properties)
		return m
	},
	onModuleClose : function(p) {
		var m = this.midiModules[p.mKey]
		if (m) {
			if (!m.__formChis) {
				m.destory()
			}
			delete this.midiModules[p.mKey]
			delete this.activeModules[p.key]
		}
	},
	onModuleSave : function(entryName, op, json, data) {
		this.fireEvent("save", entryName, op, json, data)
	},
	onActiveModule : function(module, exContext) {
		var phModule = this.mainTab.find("mKey", module)[0];
		if (phModule) {
			Ext.apply(phModule.exContext, exContext);
			this.activeModules[module] = true;
			this.mainTab.activate(phModule)
		} else {
			this.activeModules[module] = true
			phModule = this.getModuleCfg(module);
			Ext.apply(phModule.exContext, exContext);
			var activeTab = this.mainTab.getActiveTab();
			var index = 1;
			if (this.mainTab.items.length > 1) {
				index = this.mainTab.items.indexOf(activeTab) + 1;
			}
			this.mainTab.insert(index, phModule);
			this.mainTab.activate(index);
		}
	},
	onRefreshModule : function(dataKey, dataValue) { // ** modify by yzh
		var empiId = this.exContext.ids["empiId"]
		this.exContext.ids[dataKey] = dataValue;
		if (empiId) {
			var data = this.loadEmpiInfo(empiId)
			if (data) {
				this.clearModuleData(this);
				Ext.apply(this.exContext.ids, data.ids);
				Ext.apply(this.exContext.empiData, data.empiData);
			}
		}
		this.refreshModule(this.mainTab.getActiveTab());
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
		} else {
			n = this.initEmrLastNodes[key];
			Ext.apply(cfg, n)
			cfg.title = n.text;
		}
		var rks = cfg.requireKeys
		if (rks) {
			if (rks.indexOf(",") > -1) {
				var keys = rks.split(",")
				for (var i = 0; i < keys.length; i++) {
					var k = keys[i]
					var v = this.exContext.ids[k]
					if (!v) {
						cfg.disabled = true
						break;
					}
				}
			} else {
				var v = this.exContext.ids[rks]
				if (!v) {
					cfg.disabled = true
				}
			}
		}
		cfg.exContext = this.exContext;
		cfg.opener = this;
		// ** add by yzh
		var readOnlyKey = cfg.readOnlyKey
		if (readOnlyKey) {
			cfg.exContext.readOnly = this.exContext.ids[readOnlyKey]
		}
		return cfg;
	},
	// getModuleCfg : function(key, closeable) {
	// var dic = this.emrNavDic
	// if (!dic) {
	// dic = util.dictionary.DictionaryLoader.load({
	// id : 'phis.dictionary.hdrViewNav'
	// })
	// this.emrNavDic = dic;
	//
	// }
	//
	// if (!dic) {
	// return {};
	// }
	// var n = dic.wraper[key]
	//
	// var cfg = {
	// closable : closeable || this.initTabClosable,
	// frame : true,
	// mKey : key,
	// layout : "fit"
	// }
	// if (n) {
	// Ext.apply(cfg, n)
	// cfg.title = n.text;
	// }
	// // var rks = cfg.requireKeys
	// // if (rks) {
	// // if (rks.indexOf(",") > -1) {
	// // var keys = rks.split(",")
	// // for (var i = 0; i < keys.length; i++) {
	// // var k = keys[i]
	// // var v = this.exContext.ids[k]
	// // if (!v) {
	// // cfg.disabled = true
	// // break;
	// // }
	// // }
	// // } else {
	// // var v = this.exContext.ids[rks]
	// // if (!v) {
	// // cfg.readOnly = true
	// // }
	// // }
	// // }
	// cfg.exContext = this.exContext;
	// // ** add by yzh
	// var readOnlyKey = cfg.readOnlyKey
	// if (readOnlyKey) {
	// cfg.exContext.readOnly = this.exContext.ids[readOnlyKey]
	// }
	// return cfg;
	// },
	refresh : function(exContext) {
		// modify by yuhua
	},
	existsRootPage : function() {
		if (this.mainTab.find("mKey", "INDEX").length > 0) {
			return true;
		} else {
			return false;
		}
	},
	closeCurrentTab : function() {
		var mainTab = this.mainTab
		var act = mainTab.getActiveTab()
		if (act && act.mKey) {
			mainTab.remove(act)
		}
	},
	refreshTopEmpi : function() { // ** add by yzh 20100708
		// var empiId = this.exContext.ids["empiId"]
		var empiId = this.exContext.empiData.empiId // add by zhouyl 2013.09.27
		if (empiId) {
			var data = this.refreshEmpiInfo(empiId)
			if (data) {
				this.clearModuleData(this);
				Ext.apply(this.exContext.ids, data.ids);
				Ext.apply(this.exContext.empiData, data.empiData);
				console.debug(this.exContext);
			}
		}
		var tpl = this.getNewTopTemplate()
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
		}
		this.dettachTopLnkEnvents()
		if (!this.exContext.empiData.address) {
			this.exContext.empiData.address = "";
		}
		this.topPanel.body.update(tpl.apply(this.exContext.empiData))
		this.attachTopLnkEnvents();
		this.loadTJJYTree();
	},
	// 修改EmpiInfo信息,刷新加载模版数据add by zhouyl 2013.09.27
	refreshEmpiInfo : function(empiId) {
		var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "empiLoader",
					body : {
						empiId : empiId
					}
				})
		if (res.code == 200) {
			if (!res.json.empiData.photo) {
				// if (res.json.empiData.idCard) {
				// res.json.empiData.photo = "photo/"+res.json.empiData.idCard
				// } else {
				res.json.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
				// }
			} else {
				res.json.empiData.photo = "photo/" + res.json.empiData.photo
			}
			return res.json
		}
		return null;
	},
	loadData : this.refresh,
	closeAllModules : function() {
		this.mainTab.items.each(function(item) {
					if (item.mKey != "INDEX") {
						this.mainTab.remove(item);
					}
				}, this);
	},
	getWin : function() {
		var win = this.win
		var closeAction = "hide"
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						resizable : false,
						maximizable : false,
						shadow : false,
						modal : true,
						items : this.initPanel()
					})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("show", this.onWinShow, this)
			win.on("hide", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this)
					}, this)
			this.win = win
			win.isEMRWin = true;
			win.mainTab = this.mainTab;
		}
		win.getEl().first().applyStyles("display:none;");
		return win;
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
	beforeEMRViewClose : function() {
		// 关闭病历首页
		this.win.closeing = true;
		if (!Ext.isIE) {
			if (document.getElementById("lodopOcx")) {// 增加条件，不知道是否会影响其它稳定性
				var lodop = getLodop();
				if (lodop) {
					lodop.PRINT_INIT();
				}
			}
			var nodes = document.getElementsByName("emrOcxFrame");
			for (var i = 0; i < nodes.length; i++) {
				var iframe = nodes[i];
				var ocx = iframe.contentWindow.document
						.getElementById("emrOcx");
				if (ocx) {
					ocx.FunActiveXInterface("BsCloseDocument", '', '');
				}
				iframe.contentWindow.document.body.innerHTML = "";
			}
		}
		for (var i = 0; i < this.mainTab.items.length; i++) {
			var tab = this.mainTab.getItem(i);
			var m = this.midiModules[tab.mKey];
			if (m && m.beforeClose) {
				if (!m.beforeClose(true)) {
					this.win.closeing = false;
					return false;
				}
				// if(m.emrEdit){
				// m.doClose();
				// }
			}
		}
		// this.fireEvent("close");
		this.win.closeing = false;
		if (this.getReferralAppointment_win) {
			if (this.getReferralAppointment.SettlementWin) {
				this.getReferralAppointment.SettlementWin.close();
			}
			this.getReferralAppointment_win.close();
		}
		if (this.clinicHospitalAppointmentModule_win) {
			this.clinicHospitalAppointmentModule_win.close();
		}
		return true;
	},
	// 关闭病人信息
	onClose : function() {
		this.mainTab.items.each(function(item) {
					var key = item.mKey;
					if (key != "INDEX") {
						this.mainTab.remove(item);
					}
				}, this);
		this.clearModuleData(this);
		this.clearAllActived();
		this.clearAllWins();
		// 额外关闭皮试历史记录
		var m = this.midiModules['skintestHistroyList']
		if (m) {
			m.destory();
		}
	},
	clearAllWins : function() {
		for (var id in this.midiWins) {
			var win = this.midiWins[id]
			win.close()
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
	loadEmpiInfo : function() {
		// 判断是否存在公卫系统
		this.exContext.ids.qygwxt = '0'
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
			this.exContext.ids.qygwxt = '1';
		}
		var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : "empiLoader",
					body : this.exContext.ids
				})
		if (res.code == 200) {
			if (!res.json.empiData.photo) {
				// if (res.json.empiData.idCard) {
				// res.json.empiData.photo = "photo/"+res.json.empiData.idCard
				// } else {
				res.json.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
				// }
			} else {
				res.json.empiData.photo = "photo/" + res.json.empiData.photo
			}
			// 20131204 modify by gejj 如果CardNo不存在时直接使用MZHM
			if (!res.json.empiData.cardNo) {
				res.json.empiData.cardNo = res.json.empiData.MZHM;
			}
			return res.json
		}
		return null;
	},
	onClearCache : function(mId) {
		if (this.exContext.ids[this.exContext.ids.empiId + "_" + mId]) {
			this.exContext.ids[this.exContext.ids.empiId + "_" + mId] = ""
		}
	},
	// 打开患者健康档案信息
	doDyhzxx : function() {
		var body = {};
		body["jzksdm"] = this.mainApp['phis'].departmentId;
		body["empiId"] = this.exContext.empiData.empiId;
		body["personName"] = this.exContext.empiData.personName;
		// 打开前判断是否有患者信息
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "interfaceForWDService",
					serviceAction : "validateExistQueryInfo",
					body : body
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return;
		}
		var csxx = ret.json.body;// 后台返回的参数信息
		// var csxx = {
		// jgid : '491203063360121',
		// kh : '360121200802220520',
		// klx : 8,
		// xm : '罗紫怡',
		// jzksdm : '275',
		// agentid : '212',
		// ip : '10.21.96.137',
		// mac : 'E0-06-E6-C1-F6-F6'
		// };
		var myForm = document.createElement("form");
		myForm.method = "post";
		myForm.action = "http://10.21.96.133/query/outlink/outQueryAction.action";
		var myInput = document.createElement("input");
		myInput.setAttribute("name", "yljgdm");
		myInput.setAttribute("value", csxx.jgid);
		myInput.setAttribute("hidden", true);
		myForm.appendChild(myInput);
		var myInput2 = document.createElement("input");
		myInput2.setAttribute("name", "daszjgdm");
		myInput2.setAttribute("value", csxx.jgid);
		myInput2.setAttribute("hidden", true);
		myForm.appendChild(myInput2);
		var myInput3 = document.createElement("input");
		myInput3.setAttribute("name", "kh");
		myInput3.setAttribute("value", csxx.kh);
		myInput3.setAttribute("hidden", true);
		myForm.appendChild(myInput3);
		var myInput4 = document.createElement("input");
		myInput4.setAttribute("name", "ktype");
		myInput4.setAttribute("value", csxx.klx);
		myInput4.setAttribute("hidden", true);
		myForm.appendChild(myInput4);
		var myInput5 = document.createElement("input");
		myInput5.setAttribute("name", "xm");
		myInput5.setAttribute("value", csxx.xm);
		myInput5.setAttribute("hidden", true);
		myForm.appendChild(myInput5);
		var myInput6 = document.createElement("input");
		myInput6.setAttribute("name", "jzksdm");
		myInput6.setAttribute("value", csxx.jzksdm);
		myInput6.setAttribute("hidden", true);
		myForm.appendChild(myInput6);
		var myInput7 = document.createElement("input");
		myInput7.setAttribute("name", "agentid");
		myInput7.setAttribute("value", csxx.agentid);
		myInput7.setAttribute("hidden", true);
		myForm.appendChild(myInput7);
		var myInput8 = document.createElement("input");
		myInput8.setAttribute("name", "agentip");
		myInput8.setAttribute("value", csxx.ip);
		myInput8.setAttribute("hidden", true);
		myForm.appendChild(myInput8);
		var myInput9 = document.createElement("input");
		myInput9.setAttribute("name", "agentmac");
		myInput9.setAttribute("value", csxx.mac);
		myInput9.setAttribute("hidden", true);
		myForm.appendChild(myInput9);
		var win = window.open("", "newwin");
		win.document.body.appendChild(myForm);
		myForm.submit();
	},
	/**
	 * 加载体检检验树
	 */
	loadTJJYTree : function(firstOpen) {
		if (this.exContext.systemParams.QYJYBZ == 1
				|| this.exContext.systemParams.QYTJBGBZ == 1) {
			phis.script.rmi.jsonRequest({
						serviceId : "emrTreeService",
						serviceAction : "loadNavTree",
						body : {}
					}, function(code, msg, json) {
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							return;
						}
						var root = this.emrNavTree.getRootNode();
						if (this.TJNode) {
							this.TJNode.remove();
						}
						if (this.ehrNode) {
							this.ehrNode.remove();
						}
						if (!this.initEmrLastNodes) {
							this.initEmrLastNodes = {};
						}
						var TJNode = new Ext.tree.TreeNode({
									key : "C",
									text : "体检检验",
									expend : true
								});
						this.TJNode = TJNode;
						var emrNodeList = json.tjjyTree;
						this.getTJChildNode(TJNode, emrNodeList);
						root.appendChild(TJNode);
						root.expandChildNodes(true);
					}, this)
		} else {
			if (this.TJNode) {
				this.TJNode.remove();
			}
		}
	},
	// Backfill : function(lastClick , backStr){
	// if(this.midiModules['B01'].midiModules[this.midiModules['B01'].clinicModule]){
	// var m =
	// this.midiModules['B01'].midiModules[this.midiModules['B01'].clinicModule];
	// if(lastClick==this.midiModules['B01'].lastClick){
	// if (m.paraA) {
	// var startIndex = m.paraA.toString().split("|")[2];
	// m.setElementValue(backStr,startIndex)
	// this.refreshEMRNavTree();
	// }
	// }
	// }
	// },
	changeTab : function(index) {
		var nextTab = this.mainTab.items.itemAt(index);
		this.mainTab.activate(nextTab);

		// nextTab.focus(false, 10);
	},
	showPZYYYInfo : function() {
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicHospitalAppointmentService",
					serviceAction : "queryBrryinfo",
					BRID : this.exContext.empiData.BRID
				});
		if (r.code == 600) {
			MyMessageTip.msg("提示", r.msg, true);
		} else {
			this.clinicHospitalAppointmentModule = this.createModule(
					"clinicHospitalAppointmentModule",
					"phis.application.cic.CIC/CIC/CIC39");
			this.clinicHospitalAppointmentModule.brxx = this.exContext.empiData;
			this.clinicHospitalAppointmentModule.jzxh = this.exContext.ids.clinicId;
			var win = this.clinicHospitalAppointmentModule.getWin();
			this.clinicHospitalAppointmentModule_win = win;
			win.show();
			if (!win.hidden) {
				this.clinicHospitalAppointmentModule
						.loadData(this.exContext.empiData.BRID);
			}
		}
	},
	getNodeById : function(id) {
		var me = this;
		var icoId = id;
		var attributes = {};
		Ext.apply(attributes, me.emrNavDic.wraper[icoId]);
		return {
			leaf : true,
			attributes : attributes,
			parentNode : {
				attributes : {
					key : icoId,
					idsLoader : me.emrNavDic.wraper[icoId].idsLoader
				}
			}
		};
	},
	loadZldata : function(item, zlBao) {
		var comreq1 = util.rmi.miniJsonRequestSync({
					serviceId : "chis.CommonService",
					serviceAction : "loadTrRecordId",
					method : "execute",
					body : {
						empiId : this.exContext.ids.empiId,
						highRiskType : '' + item.typeValue
					}
				});
		if (comreq1.code != 200) {
			this.processReturnMsg(comreq1.code, comreq1.msg);
			return;
		} else {
		}
		this.exContext.ids["highRiskType"] = item.typeValue;
		this.exContext.ids["TPRCID"] = comreq1.json.TPRCID;
		this.exContext.ids["THRID"] = comreq1.json.THRID;
		this.exContext.ids["TCID"] = comreq1.json.TCID;

		this.exContext.args["highRiskType"] = item.typeValue;
		this.exContext.args.saveServiceId = "chis.tumourConfirmedService";
		this.exContext.args.saveAction = "saveTumourConfirmed";
		this.exContext.args.loadServiceId = "chis.tumourConfirmedService";
		this.exContext.args.loadAction = "getTCByEH";

		var schema = '';
		var re = util.schema
				.loadSync('chis.application.tr.schemas.MDC_TumourConfirmed')
		if (re.code == 200) {
			schema = re.schema;
		} else {
			return;
		}
		if (comreq1.json.tcrData)
			this.exContext.args.tcrData = this.castListDataToForm(
					comreq1.json.tcrData, schema);

		// 肿瘤报告卡
		if (zlBao == '1') {
			var tempNode = this.getNodeById('A41');
			this.onNavTreeClick(tempNode);
		}
		// 存在易患
		if (item.XIAN == 1) {
			var tempNode = this.getNodeById('A34');
			this.onNavTreeClick(tempNode);
		}
		if (item.YI == 1) {
			var tempNode = this.getNodeById('A33');
			this.onNavTreeClick(tempNode);
		}
	},
	showZlmenu : function(button, zlList, zlBao) {
		// 显示肿瘤情况
		var me = this;
		// 如果只有一种肿瘤则点击直接显示易患,现患,报告卡。
		if (zlList.length == 1) {
			var item = zlList[0];

			this.loadZldata(item, zlBao);
			return;
		}
		// 如果多种类型的肿瘤
		if (!this.zlMenu) {
			this.zlMenu = new Ext.menu.Menu();
		}
		this.zlMenu.removeAll();
		for (var i = 0, l = zlList.length; i < l; i++) {
			var it = zlList[i];

			var clickFun = (function() {
				var item = it;
				return function() {
					me.loadZldata(item, zlBao);
				}
			})()
			var menuItem = {
				text : it.typeName,
				handler : clickFun
			}
			this.zlMenu.add(menuItem);
		}
		this.zlMenu.show(button);
	},
	castListDataToForm : function(data, schema) {
		var formData = {};
		var items = schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var key = it.id;
			if (it.dic) {
				var dicData = {
					"key" : data[key],
					"text" : data[key + "_text"]
				};
				formData[key] = dicData;
			} else {
				formData[key] = data[key];
			}
		}
		Ext.applyIf(formData, data)
		return formData;
	},
	// 刷新头部档案图标
	refreshTopIcon : function() {
		// 获取 是否启用公卫系统的参数
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		if (this.SFQYGWXT == '0' || !this.mainApp.chisActive) {
			return;
		}
		if (!this.mainApp.chisActive) {
			return;
		}
		// 添加头部图标
		var me = this;
		var empiId = me.exContext.empiData.empiId;
		// 获取档案
		util.rmi.miniJsonRequestAsync({
					serviceId : "chis.CommonService",
					serviceAction : "loadRecordInfo",
					method : "execute",
					body : {
						empiId : empiId,
						recordType : 'phis'
					}
				}, function(code, msg, json) {
					if (code < 300) {
						var recordIcoDiv = Ext.get('recordIcoDiv_'
								+ me.ehrviewId);
						// 清空div里的内容
						recordIcoDiv.update("");
						var recordIcoHtml = '';
						// 处理肿瘤易患和现患 以及 残疾人
						var cfObject = {};

						icos = json.icos;
						for (var k = 0; k < icos.length; k++) {
							var ico = this.emrNavDic.wraper[icos[k]];
							if (ico) {
								if (ico.icosColumnName == "JING") {
									// 如果责任医生不是登陆者则精神病不显示
									if (this.exContext.empiData.manaDoctorId != this.mainApp.uid) {
										continue;
									}
								}
								// 防止重复图标
								if (!cfObject.hasOwnProperty(ico.icos)) {
									recordIcoHtml += '<label class="fleft"><a id="recordIcoDiv_'
											+ icos[k]
											+ '_'
											+ me.ehrviewId
											+ '"><img src="photo/'
											+ ico.icos
											+ '.png" title="'
											+ ico.icosTitle
											+ '" class="fleft" /></a>&nbsp;&nbsp;&nbsp;</label>';
									cfObject[ico.icos] = [icos[k]];
								} else {
									cfObject[ico.icos].push(icos[k]);
								}

							}
						}
						recordIcoDiv.update(recordIcoHtml);

						for (var k = 0; k < icos.length; k++) {
							var aa = Ext.get("recordIcoDiv_" + icos[k] + '_'
									+ me.ehrviewId);
							if (aa) {
								var cfKey = me.emrNavDic.wraper[icos[k]].icos;
								// 肿瘤特殊操作
								if (cfKey == 'YI') {

									var clickfunction = (function() {
										return function() {
											me.showZlmenu(aa, json.zlList,
													json.zlBao)
										}
									})();
									aa.on('click', clickfunction);
									continue;

								}

								if (cfObject[cfKey].length < 2) {
									var clickfunction = (function(th) {
										var tempNode = me.getNodeById(icos[k]);
										return function() {
											me.onNavTreeClick(tempNode)
										}
									})();
									aa.on('click', clickfunction)
								} else {
									var clickfunction = (function(th) {
										var funs = cfObject[cfKey];
										var secondNodes = [];
										for (var j = 0; j < funs.length; j++) {
											var tempNode = me
													.getNodeById(funs[j]);
											secondNodes.push(tempNode);
										}
										return function() {
											for (var jj = 0; jj < funs.length; jj++) {
												me
														.onNavTreeClick(secondNodes[jj]);
											}
										}
									})();
									aa.on('click', clickfunction)
								}

							}
						}

					} else {
						this.processReturnMsg(code, msg);
					}
				}, this);

	},
	getHasClinicRecord : function() {
		this.hasClinicRecord = false;
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicManageService",
					serviceAction : "getHasClinicRecord",
					body : {
						"brid" : this.exContext.ids.brid,
						"clinicId" : this.exContext.ids.clinicId,
						"empiId" : this.exContext.ids["empiId"]
					}
				});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg);
			return false;
		} else {
			this.hasClinicRecord = r.json.hasClinicRecord
		}
	}
})

// ================ print win ===============
