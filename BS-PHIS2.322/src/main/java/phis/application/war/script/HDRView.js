$package("phis.application.war.script")
$import("phis.script.SimpleModule", "phis.script.PrintWin",
		"util.dictionary.TreeDicFactory", "util.dictionary.DictionaryLoader")
phis.application.war.script.HDRView = function(cfg) {
	this.westWidth = 180
	this.width = 1000;
	this.height = 580
	this.title = "诊疗管理"
	this.showemrRootPage = true
	this.closeNav = false
	this.exContext = cfg.exContext || {}
	this.exContext.ids = {
		empiId : cfg.empiId,
		recordId : cfg.recordId,
		clinicId : cfg.clinicId,// 就诊序号
		brid : cfg.brid
		// 病人ID
	};
	this.exContext.empiData = {};
	this.exContext.args = cfg.args || {};
	this.activeTab = cfg.activeTab || 0
	phis.application.war.script.HDRView.superclass.constructor.apply(this,
			[cfg])
	this.on("close", this.onClose, this)
	this.on("beforeclose", this.beforeEMRViewClose, this);
}
Ext.extend(phis.application.war.script.HDRView, phis.script.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		if (this.exContext.systemParams) {
			Ext.apply(this.exContext.systemParams, this.loadSystemParams({
								// 公共参数
								commons : ['MZYP', 'JSYP', 'EMRVERSION',
										'QYDZBL'],
								// 私有参数
								privates : ['YYJGTS', 'YS_MZ_FYYF_XY',
										'YS_MZ_FYYF_ZY', 'YS_MZ_FYYF_CY',
										'YXXJYSXG', 'QZWZXJY', 'DYQWZXJY',
										'XSFJJJ_YS', 'XSFJJJ_HS', 'ZYYSQY',
										'QMYZXJY', 'SFJYWBKB', 'QYKJYWGL',
										'QYKJYYY', 'KJYSYTS', 'QYZYKJYWSP',
										'QYMZDZBL', 'QYJCGL','QYJYBZ'],
								// 个人参数
								personals : ['XSBL', 'BCJG', 'TZYS']
							}))
		} else {
			this.exContext.systemParams = this.loadSystemParams({
						// 公共参数
						commons : ['MZYP', 'JSYP', 'EMRVERSION', 'QYDZBL'],
						// 私有参数
						privates : ['YYJGTS', 'YS_MZ_FYYF_XY', 'YS_MZ_FYYF_ZY',
								'YS_MZ_FYYF_CY', 'YXXJYSXG', 'QZWZXJY',
								'DYQWZXJY', 'XSFJJJ_YS', 'XSFJJJ_HS', 'ZYYSQY',
								'QMYZXJY', 'SFJYWBKB', 'QYKJYWGL', 'QYKJYYY',
								'KJYSYTS', 'QYZYKJYWSP', 'QYMZDZBL', 'QYJYBZ','QYJCGL','QYJCBZ','JCIP','YXIP'],
						// 个人参数
						personals : ['XSBL', 'BCJG', 'TZYS']
					});
		};
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
				// items : [nav, tab]
			});
		this.panel = panel
		// 取EMRView系统参数
		return panel
	},
	// loadSystemParams : function() {
	// var res = phis.script.rmi.miniJsonRequestSync({
	// serviceId : "clinicManageService",
	// serviceAction : "loadSystemParams",
	// body : {
	// // 公共参数
	// commons : ['MZYP', 'JSYP', 'XYF', 'ZYF', 'CYF',
	// 'EMRVERSION', 'QYDZBL'],
	// // 私有参数
	// privates : ['YYJGTS', 'YS_MZ_FYYF_XY', 'YS_MZ_FYYF_ZY',
	// 'YS_MZ_FYYF_CY', 'YXXJYSXG', 'QZWZXJY',
	// 'DYQWZXJY', 'XSFJJJ_YS', 'XSFJJJ_HS', 'ZYYSQY',
	// 'QMYZXJY', 'SFJYWBKB'],
	// // 个人参数
	// personals : ['XSBL', 'BCJG', 'TZYS']
	// }
	// });
	//
	// var code = res.code;
	// var msg = res.msg;
	// if (code >= 300) {
	// this.processReturnMsg(code, msg);
	// return;
	// }
	// if (this.exContext.systemParams) {
	// Ext.apply(this.exContext.systemParams, res.json.body)
	// } else {
	// this.exContext.systemParams = res.json.body;
	// }
	//
	// },
	onWinShow : function() {
		// 判断是否有需要恢复的病历
		this.loadEmrTreeNode(true);
		//加载pacs树
		this.loadPACSTree();
		if (!this.tabsInited) {
			this.initTabItems();
		}
		this.initEmrLastNodes = {};
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
	loadEmrTreeNode : function(firstOpen) {
		if (this.exContext.systemParams.QYDZBL !== '1')
			return;
		phis.script.rmi.jsonRequest({
					serviceId : "emrManageService",
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
					this.initEmrLastNodes = {};
					var blNode = new Ext.tree.TreeNode({
								key : "D",
								text : "书写病历",
								expend : true
							});
					this.blNode = blNode;
					var emrNodeList = json.emrTree;
					var ehrNodeList = json.ehrTree;
					this.getEMRChildNode(blNode, emrNodeList);
					root.appendChild(blNode);
					root.expandChildNodes(true);
					this.getEHRChildNode(root, ehrNodeList);
				}, this)
	},
	getEMRChildNode : function(pNode, nodeList) {
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
						emrEditor : true,
						leaf : (node.children) ? false : true
					});
			if (node.children) {
				this.getEMRChildNode(n, node.children);
			} else if(node.key){
				this.tjjyNodes[node.key] = node;
				this.initEmrLastNodes[node.ID] = n.attributes;
				if ((i == nodeList.length - 1)) {
					this.initEmrLastNodes[node.ID] = n.attributes;
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
				'<a style="width:76px;">病人列表</a></li>|<a  style="width:76px;" class="topLine">个人信息</a>|<a>打印</a>|<a class="topLine" id="HP">帮助</a>|<a class="topLine" id="CLOSE">关闭</a>',
				'</ul>',
				'</div></div>',
				'<div class="TopMessage">',
				'<img src="' + photoUrl
						+ '" width="71" height="71"  class="fleft photo" />',
				'<div  class="fleft">',
				'<h2>{personName}</h2>',
				'<ul class="mdetail">',
				'<li class="width15">',
				'<p><label>性&nbsp;&nbsp;&nbsp;&nbsp;别：</label>{BRXB_text}</p>',
				'<p><label>年&nbsp;&nbsp;&nbsp;&nbsp;龄：</label>{AGE}</p>',
				'</li>',
				'<li class="width20">',
				'<p><label>住院号码：</label>{ZYHM}</p>',
				'<p><label>入院诊断：</label><label title="{JBMC}">{JBMC:substr(0,25)}<tpl if="JBMC.length &gt; 25">...</label></tpl></p>',
				'</li>',
				'<li class="width15">',
				'<p><label>科&nbsp;&nbsp;&nbsp;&nbsp;室：</label>{BRKS_text}</p>',
				'<p><label>过敏药物：</label><font style="color:#C40600;font-weight:bold;">{GMYW}</font></p>',
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
		// 病人住院信息
		Ext.applyIf(this.exContext.empiData, this.exContext.brxx.data);
		if (!this.exContext.empiData.JBMC) {
			this.exContext.empiData.JBMC = '';
		}
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
		}
		// alert(Ext.encode(this.exContext.brxx.data))
		var tpl = this.getNewTopTemplate()
		// 增加EMR控件，用来判断是否有恢复文件
		var ocxStr = ""
		if (Ext.isIE) {
			ocxStr = "<div style='display:none'><OBJECT id='emrOcx_hide' name='emrOcx_hide' classid='clsid:FFAA1970-287B-4359-93B5-644F6C8190BB'></OBJECT></div>"
		} else {
			ocxStr = "<div><OBJECT id='emrOcx_hide' TYPE='application/x-itst-activex' WIDTH='0' HEIGHT='0' clsid='{FFAA1970-287B-4359-93B5-644F6C8190BB}'></OBJECT></div>"
		}
		var panel = new Ext.Panel({
					border : false,
					frame : false,
					height : 87,
					collapsible : false,
					layout : 'fit',
					region : 'north',
					html : ocxStr + tpl.apply(this.exContext.empiData)
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
			case "HPH" :
				// window.open("help/BSCHIS2.1.html");
				break;
			case "PRT" :
				this.showPrintView()
				break
			case "SWH" :
				break
			case "skinTest" :
				this.showSkinTest();
				break
			case "MPI" :
				this.showMPIInfo()
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
				this.doClose();
				break;
		}
	},
	doClose : function() {
		//2019-10-15 zhaojian 住院医生站病人列表自动刷新功能
		if(this.opener != null && typeof(this.opener.task) == "object"){
			this.opener.startRefresh();
		}
		this.win.hide();
	},
	showRecoverRecord : function(data, emr) {
		var module = this.createModule("recoverRecordList",
				"phis.application.war.WAR/WAR/WAR35");
		module.exContext = this.exContext;
		module.emr = emr;
		module.opener = this;
		var win = module.getWin();
		module.loadDataByLocal(data);
		this.midiWins["recoverRecordList_win"] = win;
		win.setHeight(550);
		win.setWidth(270);
		win.setPosition(0, 100);
		module.getWin().show();
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
	showDicInfo : function(paraA, paraB, ctx) {
		var entryName, p;
		if (paraB) {
			entryName = paraB.toString().split("|")[0];
			p = paraB.toString().split("|")[0];
		}
		if (!entryName) {
			return;
		}
		entryName = "phis.application.cic.schemas." + entryName;
		if (this.midiWins["refDicInfoList_win_" + p]
				&& this.midiModules['refDicInfoList' + p]) {
			if (this.dicParaB != paraB) {
				this.midiModules['refDicInfoList' + p].paraB = paraB;
				this.midiModules['refDicInfoList' + p].entryName = entryName;
				// this.midiModules['refDicInfoList' +
				// p].replaceCmData();
				this.dicParaB = paraB;
			}
			this.midiModules['refDicInfoList' + p].paraA = paraA;
			this.midiWins["refDicInfoList_win_" + p].setHeight(300);
			this.midiWins["refDicInfoList_win_" + p].setWidth(520);
			this.midiWins["refDicInfoList_win_" + p].setPosition(420, 120);
			this.midiWins["refDicInfoList_win_" + p].show();
			return;
		}
		// var module = this.createModule("refDicInfoList" + p,
		// "phis.application.war.WAR/WAR/WAR81");
		var moduleCfg = this
				.loadModuleCfg("phis.application.war.WAR/WAR/WAR81");
		moduleCfg.id = p;
		var cfg = {
			showButtonOnTop : true,
			border : false,
			frame : false,
			autoLoadSchema : false,
			isCombined : true,
			exContext : {}
		};
		Ext.apply(cfg, moduleCfg);
		var cls = moduleCfg.script;
		if (!cls) {
			return;
		}
		$import(cls);
		var module = eval("new " + cls + "(cfg)");
		this.midiModules['refDicInfoList' + p] = module;
		module.setMainApp(this.mainApp);
		module.exContext = this.exContext;
		module.paraA = paraA;
		module.paraB = paraB;
		module.entryName = entryName;
		this.dicParaB = paraB;
		module.parent = ctx;
		module.on("appoint", ctx.setElementValue, ctx);
		var win = module.getWin();
		win.add(module.initPanel())
		this.midiWins["refDicInfoList_win_" + p] = win;
		win.setHeight(300);
		win.setWidth(520);
		win.setPosition(420, 120);
		// ctx.hideEmr();
		win.show();
	},
	showSkinTest : function() {
		module = this.createModule("skintestHistroyList",
				"phis.application.cic.CIC/CIC/CIC0103");
		module.exContext = this.exContext;
		var win = module.getWin();
		win.add(module.initPanel())
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
		var win = module.getWin();
		this.midiWins["emrPersonalForm_win"] = win;
		win.setHeight(240);
		win.setWidth(370);
		win.show();
	},
	// 数据盒
	showEmrUserDataBox : function(ctx) {
		var moduleCfg = this
				.loadModuleCfg("phis.application.war.WAR/WAR/WAR95");
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
	showMPIInfo : function() {
		var m = this.midiModules['mpi']
		if (!m) {
			$import("phis.script.pix.EMPIInfoModule")
			m = new phis.script.pix.EMPIInfoModule({
						serviceAction : "updatePerson",
						title : "个人基本信息",
						mainApp : this.mainApp
					});
			m.on("onEmpiReturn", function() {
				m.getWin().hide(); // ** add by yzh 20100724
				// close the
				// EMPIInfoModule
				this.refreshTopEmpi(); // ** add by yzh
					// 20100708
				}, this)
			this.midiModules['mpi'] = m;
			this.midiWins['mpi_win'] = m.getWin();
		}
		m.getWin().show();
		m.clear(); // ** add by yzh 20100818
		m.setRecord(this.exContext.ids.empiId)
	},
	createNavPanel : function() {
		var id = 'phis.dictionary.hdrViewNav';
		if (this.exContext.systemParams.QYMZDZBL + "" == '1') {
			id = 'phis.dictionary.hdrView';
		}
		var emrNavTree = util.dictionary.TreeDicFactory.createTree({
					id : id
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
		emrNavTree.on('load', function() {
					return emrNavTree.filter.filterBy(this.filterNavTree, this)
				}, this);
		this.emrNavTreePanel = emrNavTreePanel
		return emrNavTreePanel
	},
	filterNavTree : function(node) {
		var key = node.attributes.key
		if (key.indexOf("C06") !== -1 || key.indexOf("C07") !== -1) {
			return (this.exContext.systemParams.QYJCGL == 1)
		} else if (key.indexOf("G") != -1) {
			return (this.exContext.systemParams.QYJYBZ == 1)
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
			p.closable = true;
			if (node.BLLX == 1) {
				p.title = node.LBMC || node.text;
			} else {
				p.title = node.text;
			}
			p.mKey = key + nodeId;
			p.key = key + nodeId;
//			this.emrNavTreePanel.collapse(false)
			var tab = this.mainTab.add(p)
			tab.on("destroy", this.onModuleClose, this)
			this.mainTab.doLayout()
			this.mainTab.activate(p)
			this.activeModules[key + nodeId] = true
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
		if (cfg.showWinOnly == "true") {
			var width = 800
			var module = this.createModule(cfg.key, cfg.ref);
			module.key = cfg.key;
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
				var ryrq = Date.parseDate((this.exContext.empiData.RYRQ)
								.split(' ')[0], "Y-m-d")// 修正病人视图下时间格式不对的问题
				var cyrq = new Date();
				if (!Ext.isEmpty(this.exContext.empiData.CYRQ)) {
					cyrq = Date.parseDate(this.exContext.empiData.CYRQ
									.split(' ')[0], "Y-m-d")
				}
				module.maxWeek = Math.floor((Math.floor((cyrq.getTime() - ryrq
						.getTime())
						/ 1000 / 60 / 60 / 24))
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
			if (this.exContext.empiData.CYPB > 1) {
				module.exContext.readOnly = true;
			}
			if (!this.midiWins[cfg.key]) {
				this.midiWins[cfg.key] = module.getWin();
				module.getWin().add(module.initPanel());
			}
			this.midiWins[cfg.key].setWidth(width);
			this.midiWins[cfg.key].setHeight(module.height || 355);
			this.midiWins[cfg.key].show();
			if (key == "C06") {
				module.getBRXX("ZYHM", this.exContext.empiData.ZYHM);
			}
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
		if (key=="F01") {
				cfg.exContext.pacsid='pacsfs_zy'
				cfg.exContext.url=this.exContext.systemParams.JCIP+"?inhosptId="+this.exContext.empiData.ZYH+"&ConType=0&sqbh="+this.mainApp['phisApp'].deptId;
			}
			if (key=="F02") {
				cfg.exContext.pacsid='pacscs_zy'
				cfg.exContext.url=this.exContext.systemParams.JCIP+"?inhosptId="+this.exContext.empiData.ZYH+"&ConType=1&sqbh="+this.mainApp['phisApp'].deptId;
			}
			if (key=="F03") {
				cfg.exContext.pacsid='pacsyx_zy'
				cfg.exContext.url=this.exContext.systemParams.YXIP+"?zynum="+this.exContext.empiData.ZYH+"&mznum=&studydate=&sqbh=";
			}
//		if (this.exContext.empiData.CYPB > 1) {
//			cfg.exContext.readOnly = true;
//		}
//		this.emrNavTreePanel.collapse(false)
		var p = this.mainTab.add(cfg)
		this.mainTab.doLayout()
		this.mainTab.activate(p)
		this.activeModules[key] = true
	},
	// add by caijy for 拼装打开护理界面需要传过去的数据
	getHljlData : function() {
		var data = {};
		var r = this.exContext.empiData;
		data.ZYH = r.ZYH;// 住院号
		data.BRBQ = r.BRBQ;// 病人病区
		data.BRCH = r.BRCH;// 病人床号
		data.BRXM = r.BRXM;// 病人姓名
		data.ZYHM = r.ZYHM;// 住院号吗
		data.BRXB_text = r.BRXB_text;// 性别中文
		data.AGE = r.AGE;// 年龄
		data.BRKS_text = r.BRKS_text;// 病人科室中文
		if (r.JBMC) {
			data.JBMC = r.JBMC;
		} else {
			data.JBMC = "";
		}// 疾病名称
		data.BRQK = r.BRQK;// 病人情况
		data.GMYW = r.GMYW;// 过敏药物
		return data;
	},
	openEHRView : function(node) {
		var owurl = node.id;
		var panel = new Ext.Panel({
			id : "ehrview",
			border : false,
			html : '<iframe id="ehrview" src="'
					+ owurl
					+ '" width="100%" height="100%" frameborder="0" scrolling="auto"></iframe>',
			autoScroll : true
		});
		this.ehrPanel = panel;
		if (!this.chiswin) {
			var win = new Ext.Window({
						title : '健康档案',
						closable : true,
						width : "100%",
						height : Ext.getBody().getHeight() + 42,
						iconCls : 'icon-grid',
						shim : true,
						// border:false,
						plain : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						minimizable : false,
						resizable : false,
						maximizable : false,
						shadow : false,
						modal : true,
						draggable : false
					});
			win.on("hide", this.dowinclose, this);
			this.chiswin = win
		}
		this.chiswin.removeAll();
		this.chiswin.add(panel);
		this.chiswin.show();
	},
	dowinclose : function() {
		this.loadEmrTreeNode(true);// 刷新任务列表
	},
	initTabItems : function() {
		var items = []
		var ims = this.initModules
		var empiId = this.exContext.ids["empiId"]
		var phrId = this.exContext.ids["phrId"]
		this.closeAllModules()
		var activeModules = {}
		if (!phrId) {
			if (this.exContext.empiData.CYPB >= 1) {
				var module = this.createModule("wardAdviceQueryModule",
						"phis.application.war.WAR/WAR/WAR030404");
				if (module) {
					module.initDataId = this.exContext.empiData.ZYH;
					module.opener = this;
					var cfg = {
						closable : false,
						frame : true,
						mKey : "C01",
						title : "医嘱处理",
						layout : "fit",
						items : module.initPanel()
					}
					cfg.exContext = this.exContext;
					this.mainTab.add(cfg);
					module.onWinShow();
					activeModules["C01"] = true
				}
			} else {
				this.mainTab.add(this.getModuleCfg("C01"))
				activeModules["C01"] = true
			}
		}

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
		m.exContext.ids = {};
		m.exContext.empiData = {};
	},

	onModuleActive : function(tabPanel, newTab, curTab) {
		if (!newTab) {
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
		// if (ref) {
		// var result = phis.script.rmi.miniJsonRequestSync({
		// serviceId : "moduleConfigLocator",
		// id : ref
		// })
		//
		// if (result.code == 200) {
		// Ext.apply(cfg, result.json.body)
		// }
		//
		// }
		var cls = cfg.script
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
					var m = eval("new " + cls + "(cfg)")
					m.on("save", this.onModuleSave, this)
					m.on("activeModule", this.onActiveModule, this);
					m.on("refreshModule", this.onRefreshModule, this)
					m.on("clearCache", this.onClearCache, this)
					m.emrview = this;
					m.exContext = this.exContext;
					if (this.exContext.empiData.CYPB > 1) {
						m.exContext.readOnly = true;
					}
					if (cfg.key != "C06") {
						m.initDataId = this.exContext.ids.clinicId;
					}
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
					if (m.loadData && m.autoLoadData == false) {
						m.loadData();
						newTab.__actived = true;
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
			m.destory()
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
	closeCurrentTab : function() {
		var mainTab = this.mainTab
		var act = mainTab.getActiveTab()
		if (act && act.mKey) {
			mainTab.remove(act)
		}
	},
	onRefreshModule : function(dataKey, dataValue) { // **
		// modify
		// by
		// yzh
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
	getModuleCfg : function(key, closeable) {
		var dic = this.emrNavDic
		if (!dic) {
			var id = 'phis.dictionary.hdrViewNav';
			if (this.exContext.systemParams.QYMZDZBL + "" == '1') {
				id = 'phis.dictionary.hdrView';
			}
			dic = util.dictionary.DictionaryLoader.load({
						id : id
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
		}else {
			n = this.tjjyNodes[key];
			if(n){
				Ext.apply(cfg, n);
				cfg.title = n.text;
			}
		}
		// var rks = cfg.requireKeys
		// if (rks) {
		// if (rks.indexOf(",") > -1) {
		// var keys = rks.split(",")
		// for (var i = 0; i < keys.length; i++) {
		// var k = keys[i]
		// var v = this.exContext.ids[k]
		// if (!v) {
		// cfg.disabled = true
		// break;
		// }
		// }
		// } else {
		// var v = this.exContext.ids[rks]
		// if (!v) {
		// cfg.readOnly = true
		// }
		// }
		// }
		cfg.exContext = this.exContext;
		// ** add by yzh
		var readOnlyKey = cfg.readOnlyKey
		if (readOnlyKey) {
			cfg.exContext.readOnly = this.exContext.ids[readOnlyKey]
		}
		return cfg;
	},
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
	refreshTopEmpi : function() { // ** add by yzh 20100708
		var empiId = this.exContext.ids["empiId"]
		if (empiId) {
			var data = this.loadEmpiInfo(empiId)
			if (data) {
				Ext.apply(this.exContext.empiData, data.empiData);
			}
		}
		var tpl = this.getNewTopTemplate()
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
		}
		if (!this.exContext.empiData.JBMC) {
			this.exContext.empiData.JBMC = '';
		}
		this.dettachTopLnkEnvents()
		this.topPanel.body.update(tpl.apply(this.exContext.empiData))
		this.attachTopLnkEnvents()
		this.loadEmrTreeNode();
	},
	loadData : this.refresh,
	closeAllModules : function() {
		this.mainTab.items.each(function(item) {
					if (item.mKey != "INDEX" && item.mKey != "C01") {
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
						minimizable : false,
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
			win.on("beforeshow", this.onWinShow, this)
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
	beforeEMRViewClose : function() {
		if (document.getElementById("lodopOcx")) {// 增加条件，不知道是否会影响其它稳定性
			var lodop = getLodop();
			if (lodop) {
				lodop.PRINT_INIT();
			}
		}
		// 关闭病历首页
		this.win.closeing = true;
		for (var i = 0; i < this.mainTab.items.length; i++) {
			var tab = this.mainTab.getItem(i);
			var m = this.midiModules[tab.mKey];
			if (m && m.beforeClose) {
				if (!m.beforeClose(true)) {
					this.win.closeing = false;
					return false;
				}
			}
		}
		this.fireEvent("close");
		this.win.closeing = false;
		return true;
	},
	onClose : function() {
		this.mainTab.items.each(function(item) {
					var key = item.mKey;
					if (key != "INDEX") {
						var isInitModule = false;
						// for (var i = 0; i < this.initModules.length;
						// i++) {
						// var im = this.initModules[i]
						// if (im == key) {
						// isInitModule = true;
						// }
						// }
						if (!isInitModule) {
							this.mainTab.remove(item);
						}
					}
				}, this);
		this.clearModuleData(this);
		this.clearAllActived();
		this.clearAllWins();
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
			return res.json
		}
		return null;
	},
	onClearCache : function(mId) {
		if (this.exContext.ids[this.exContext.ids.empiId + "_" + mId]) {
			this.exContext.ids[this.exContext.ids.empiId + "_" + mId] = ""
		}
	},
	loadPACSTree : function() {
		if (this.exContext.systemParams.QYJCBZ == 1&&this.exContext.systemParams.JCIP) {
			this.tjjyNodes = {};
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
						if (this.pacsNode) {
							this.pacsNode.remove();
						}
						if (this.ehrNode) {
							this.ehrNode.remove();
						}
						this.initEmrLastNodes = {};
						var pacsNode = new Ext.tree.TreeNode({
									key : "F",
									text : "检查开单",
									expend : true
								});
						this.pacsNode = pacsNode;
						var emrNodeList = json.pacsTree_zy;
						this.getPACSChildNode(pacsNode, emrNodeList);
						root.appendChild(pacsNode);
						root.expandChildNodes(true);
					}, this)
		} else {
			if (this.pacsNode) {
				this.pacsNode.remove();
			}
		}
	},
getPACSChildNode : function(pNode, nodeList) {
		for (var i = 0; i < nodeList.length; i++) {
			var node = nodeList[i];
			var n = new Ext.tree.TreeNode({
						id : node.key,
						key : node.key,
						text : node.text,
						requireKeys : node.requireKeys,
						ref : node.ref,
						emrEditor : false,
						leaf : (node.children) ? false : true
					});
			if (node.children) {
				this.getEMRChildNode(n, node.children);
			} else {
				this.tjjyNodes[node.key] = node;
				this.initEmrLastNodes[node.key] = n.attributes;
				if ((i == nodeList.length - 1)) {
					this.initEmrLastNodes[node.key] = n.attributes;
				}
			}
			pNode.appendChild(n);
		}
	}
})

// ================ print win ===============
