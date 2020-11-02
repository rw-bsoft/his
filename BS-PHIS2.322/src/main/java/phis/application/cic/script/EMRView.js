// # sourceURL=phis.application.cic.script.EMRView.js
$package("phis.application.cic.script")
$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory",
		"util.dictionary.DictionaryLoader",
		"phis.script.rmi.jsonRequestOutTime")
var icos = null;
phis.application.cic.script.EMRView = function(cfg) {
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
	this.exContext.args = cfg.args || {};
	this.activeTab = cfg.activeTab || 0
	phis.application.cic.script.EMRView.superclass.constructor.apply(this,[cfg])
	this.on("close", this.onClose, this)
	this.on("beforeclose", this.beforeEMRViewClose, this);
}
Ext.extend(phis.application.cic.script.EMRView, phis.script.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		this.dybz = 0;
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
			"commons" : ['MZYP', 'JSYP', "SFQYGWXT"],
			"privates" : ['YYJGTS',
					'YS_MZ_FYYF_' + this.mainApp.reg_departmentId + '_XY',
					'YS_MZ_FYYF_' + this.mainApp.reg_departmentId + '_ZY',
					'QYCFCZQZTJ', 'QYSXZZ',
					'YS_MZ_FYYF_' + this.mainApp.reg_departmentId + '_CY',
					'YSZJS', 'XSFJJJ', 'HQFYYF', 'QYJYBZ', 'QYTJBGBZ',
					'QYKJYWGL', 'QYKJYYY', 'KJYSYTS', 'QYSJYSSQ', 'QYPSXT',
					'QYJCGL', 'ZDJSSL', 'YFS','QYJCBZ','JCIP','YXIP','YFJKJM']
		}
		var rv = this.loadSystemParams(body);
		this.exContext.systemParams = rv;
		this.exContext.systemParams.YS_MZ_FYYF_XY = this.exContext.systemParams['YS_MZ_FYYF_'
				+ this.mainApp.reg_departmentId + '_XY'] == 'null'? "0"
				: this.exContext.systemParams['YS_MZ_FYYF_'+ this.mainApp.reg_departmentId + '_XY'];
		this.exContext.systemParams.YS_MZ_FYYF_ZY = this.exContext.systemParams['YS_MZ_FYYF_'
				+ this.mainApp.reg_departmentId + '_ZY'] == 'null'? "0"
				: this.exContext.systemParams['YS_MZ_FYYF_'+ this.mainApp.reg_departmentId + '_ZY'];
		this.exContext.systemParams.YS_MZ_FYYF_CY = this.exContext.systemParams['YS_MZ_FYYF_'
				+ this.mainApp.reg_departmentId + '_CY'] == 'null'? "0"
				: this.exContext.systemParams['YS_MZ_FYYF_'+ this.mainApp.reg_departmentId + '_CY'];
	},
	onWinShow : function() {
		this.loadTJJYTree();
		this.loadPACSTree();
		/*
		 * 注释拱墅区版本中有关医保的代码// 查询该病人是否为血透病人
		 * this.doQueryCBRYXXByBrid(this.exContext.ids.brid);
		 */
		if (!this.tabsInited) {
			this.initTabItems();
		}
		this.win.maximize()
		this.refreshTopIcon();
		/**begin************zhaojian 2019-01-25 门诊医生站病人列表增加公卫相关信息创建提醒***********************/
		if(this.jdrwts){
			this.onNavTreeClick(this.getNodeById(this.jdrwts.replace('_noyearplan','')));
		}
		/**end************zhaojian 2019-01-25 门诊医生站病人列表增加公卫相关信息创建提醒***********************/
	},
	getNewTopTemplate : function() {
		debugger;
		if (this.tpl) {
			return this.tpl;
		}
		$styleSheet("phis.resources.css.app.biz.style")
		this.ehrviewId = Ext.id();
		var photoUrl = ClassLoader.appRootOffsetPath + "{photo}"
		var tpl = new Ext.XTemplate(
				'<div class="head_bg">',
				'<div id="emr_header">',
				'<div id="topMenu_out"><div id="topMenu_in">',
				'<ul class="top_menu">',
				// 2014-1-15号，与张伟确认先将病人列表隐藏 <a href="#" style="width:76px;">病人列表</a></li>|
				//2014-2-12，放出病人列表
				//2014-2-12，放出病人列表end
				'<a style="width:76px;" class="topLine" id="MPI">个人信息</a>|<a style="width:76px;" class="topLine" id="ZYYY">住院通知单</a>|<a style="width:76px;" class="topLine" id="ZG">暂挂</a>|<a style="width:76px;" class="topLine" id="CLOSE">就诊结束</a>|<a style="width:76px;" class="topLine" id="HP">帮助</a>',
				'</ul>',
				'</div></div>',
				'<div class="TopMessage">',
				'<img src="' + photoUrl+ '" width="71" height="71"  class="fleft photo" />',
				'<div  class="fleft">',
				'<div class="ehrviewClear">',
				'<h2 class="fleft">{personName}</h2><h2 class="fleft">&nbsp;'
						+ this.mainApp.departmentName + '</h2>',
				'<div class="fleft" style="margin:0px 80px 0px 0px">&nbsp;</div>',
				'<div id="recordIcoDiv_' + this.ehrviewId + '" class="ehrviewInfo_ico"></div>',
				//'<div style="margin:4px 0px 0px 0px"><label class="fleft"><a id="skinTest" class="topLine"><img id="skinTest" title="查看皮试历史记录" src="'
				//		+ ClassLoader.appRootOffsetPath + 'photo/M.png" class="fleft"/></a></label>',
				//'</div>
				'</div>',
				'<ul class="mdetail">',
				'<li>',
				'<p><label>性&nbsp;&nbsp;&nbsp;&nbsp;别：</label>{sexCode_text}</p>',
				'<p><label>出生日期：</label>{birthday}( {age} )</p>',
				'</li>',
				'<li>',
				'<p><label>门诊号码：</label>{MZHM}</p>',
				'<p><label>责任医生：</label>{manaDoctorId_text}</p>',
				'</li>',
				'<li>',
				'<p><label>病人性质：</label>{BRXZ_text}</p>',
				'<p><label>身份证号：</label>{idCard}</p>',
				'</li>',
				//'<li>',
			//	'<p><label>电&nbsp;&nbsp;&nbsp;&nbsp;话：</label>{phoneNumber}</p>',
				//'<p><label>住&nbsp;&nbsp;&nbsp;&nbsp;址：</label><span title="{address}">{address:substr(0,15)}<tpl if="address.length &gt; 3">...</tpl></span></p>',
				//'</li>',
				'<li>',
				'<p><label>&nbsp;&nbsp;&nbsp;&nbsp;</label>&nbsp;&nbsp;&nbsp;&nbsp;</p>',
				'<p><label>签约医生：</label>{ysxm}</p>',
				// '<p><label>就诊科室：</label>'+this.mainApp.departmentName+'</p>',//病历主页上方显示当前就诊的科室
				// by cqd 2015.6.30
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
			if (this.exContext.empiData[prop] == null || this.exContext.empiData[prop] == 'null') {
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
				this.midiModules['B01'].openReferralAppointment();
				// this.medCheck();
				break;
			case "HZXX" :
				this.doDyhzxx();
				break;
			case "ZG" :
				// this.doClinicFinish(2);
				this.midiModules['B01'].doClinicFinish(2);
				break;
			case "ZYYY" :
			this.showZYYYInfo()
				break;
		}
	},
	medCheck : function() {
		util.rmi.jsonRequest({
					serviceId : "phis.medCheckService",
					serviceAction : "validate",
					body : {
						jzxh : this.jzxh,
						brid : this.brid
					}
				}, function(code, msg, json) {
					if (code == 200) {
						var result = json.result;
						if (result == 1) {
							msg = msg + '<br>' + '是否继续？';
							Ext.MessageBox.confirm('请注意', msg, function(btn) {
										if (btn == 'yes') {
											this.midiModules['B01'].openReferralAppointment();
										}
									}, this);
						} else if (result == 2) {
							msg = msg + '<br>' + '处方中存在禁止开药的药品';
							Ext.MessageBox.alert('请注意', msg);
						} else {
							this.midiModules['B01'].openReferralAppointment();
						}
					}
				}, this);
	},
	doClose : function() {
		this.win.hide();
	},
	showSkinTest : function() {
		module = this.createModule("skintestHistroyList","phis.application.cic.CIC/CIC/CIC0103");
		module.exContext = this.exContext;
		var win = module.getWin();
		win.add(module.initPanel())
		win.show();
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
						this.refreshTopEmpi(); // ** add by yzh 20100708
						if (this.activeModules["H01"]) // ** add by yzh
							this.refreshModule(this.mainTab.find("mKey",
											"H01")[0])
					}, this)
			this.midiModules['mpi'] = m;
		}
		m.getWin().show();
		m.clear(); // ** add by yzh 20100818
		m.setRecord(this.exContext.empiData.empiId);// add by zhouyl 20130927
	},
	createNavPanel : function() {
		if (this.mainApp.chisActive) {
			this.getTreeFilterChisParameters();
		}
		var emrNavTree = util.dictionary.TreeDicFactory.createTree({
					id : "phis.dictionary.emrViewNav"
				});
		this.emrNavTree = emrNavTree;
		emrNavTree.expandAll();
		emrNavTree.on("click", this.onNavTreeClick, this);
		var root = emrNavTree.getRootNode();
		root.on("expand", function(root) {
					var lastNode = root.lastChild;
					lastNode.on("load", function() {this.emrNavTree.filter.filterBy(
								this.filterNavTree, this)
							}, this)
					lastNode.on("beforeappend", function(emrNavTree, lastNode,node) {
								this.emrNavTree.filter.filterBy(this.filterNavTree, this)
							}, this)
				}, this);
		var emrNavTreePanel = new Ext.Panel({
					border : false,
					frame : true,
					layout : "fit",
					split : true,
					collapsible : true,
					title : '文件夹',
					region : 'west',
					width : this.westWidth,
					collapsed : false,
					items : emrNavTree
				})
		this.emrNavTreePanel = emrNavTreePanel
		return emrNavTreePanel
	},
	refreshEMRNavTree : function() {
		if (!this.SFQYGWXT) {
			var publicParam = {
				"commons" : ['SFQYGWXT']
			}
			this.SFQYGWXT = this.loadSystemParams(publicParam).SFQYGWXT;
		}
		if (this.SFQYGWXT == '1' && this.mainApp.chisActive) {
			this.treeNodeStatusMap = "";
			this.getTreeFilterChisParameters();
			// this.emrNavTree.filter.filterBy(this.filterNavTree, this)
		}
	},
	chisModelSave : function(msg) {
		if (msg) {
			MyMessageTip.msg("提示", msg, true);
		}
		this.refreshEMRNavTree()
	},
	onOpenNextNode : function(nodeId) {
		if (nodeId) {
			var node = this.getNodeById(nodeId);
			if (node) {
				this.onNavTreeClick(node);
			}
		}
	},
	CMRSave : function(data) {
		if (data.JZXH) {
			this.exContext.ids.clinicId = data.JZXH
		}
		this.refreshEMRNavTree();
	},
	getTreeFilterChisParameters : function() {
		var body = {
			empiId : this.exContext.ids.empiId,
			JZXH : '' + this.exContext.ids.clinicId
		};
		// 请求15分钟超时
		phis.script.rmi.jsonRequestOutTime({
					serviceId : "chis.chisRecordFilter",
					serviceAction : "getNodeFilterParameters",
					method : "execute",
					body : body
				}, 900000, function(code, msg, json) {
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
					this.rsMsgList = resBody.rsMsgList;
					this.emrNavTree.filter.filterBy(this.filterNavTree, this);
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
		if (key.indexOf("RQFL") != -1) {
				return false;
			}
		if (key.indexOf("A") != -1) {
			if (!this.treeNodeStatusMap) {
				return false;
			}
			var currRoleId = this.mainApp.jobtitleId;
			var allowRole = "50,51,52,53,55,56";
			var trafficPermit = false;
			if (allowRole.indexOf(currRoleId.substring(currRoleId.indexOf(".")+ 1)) > -1) {
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
						if (this.exContext.ids.manaUnitId) {
							if (this.exContext.ids.manaUnitId.substring(0, 9) != '310112051') {
								if (ref != 'chis.application.diseasemanage.DISEASEMANAGE/HY/C32')
									return false;
							}
						}
						if (ref == 'chis.ehrViewNav/D_0101') {
							var tnbStatuse = this.treeNodeStatusMap["chis.ehrViewNav/D_01"];
							var tnbHRStatuse = this.treeNodeStatusMap["chis.ehrViewNav/D_0101"];
							if (tnbStatuse == "create"&& tnbHRStatuse == "create") {
								this.treeNodeStatusMap['chis.ehrViewNav/D_0101'] = 'hide';
								for (var ni = 0, nlen = this.rsMsgList.length; ni < nlen; ni++) {
									var ms = this.rsMsgList[ni];
									if (ms.nodeKey == 'A0701') {
										this.rsMsgList.splice(ni, 1);
										break;
									}
								}
							}
							if (tnbStatuse == "create" && tnbHRStatuse == "read") {
								this.treeNodeStatusMap['chis.ehrViewNav/D_0101'] = 'read';
							}
							if (tnbStatuse == "read" && tnbHRStatuse == "read") {
								this.treeNodeStatusMap['chis.ehrViewNav/D_0101'] = 'read';
							}
						}
						nodeStatus = this.treeNodeStatusMap[ref];
					}
				}
				if (nodeStatus == "create") {
					if (currRoleId == "phis.50"||currRoleId == "phis.55") {
						// 只有全科门诊医生可以做社区任务，其他角色只察看
						if (key == 'A0502') {
							node.attributes.text = "高血压临床检查项目";
						}
						if (key == 'A0802') {
							node.attributes.text = "糖尿病临床检查项目";
						}
						if (!node.attributes.changeNodeText) {
							node.setText("<font style='color:red'>需创建"+ node.attributes.text + "</font>");
							node.attributes.changeNodeText = true;
							if (node.attributes.autoOpen == "true") {
								if (this.mainApp.reg_departmentId != this.exContext.systemParams['YFS']) {
									this.onNavTreeClick(node, "");
								}
							}
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
					return true;
				} else if (nodeStatus == "hide") {
					nodeStatus = "";
					return false;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (key.indexOf("C") != -1) {
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
					items : items
				})
		mainTab.on("tabchange", this.onModuleActive, this)
		this.mainTab = mainTab;
		return mainTab;
	},
	openEHRModel : function(refModules) {
		var modules = this.ehrView.getEHRModuleCfg(refModules);
		for (var i = 0, len = refModules.length; i < len; i++) {
			var key = refModules[i];
			if (!this.midiModules[key]) {
				var module = modules[key];
				module.rpcFromPhis = true;
				this.emrNavTreePanel.collapse(false);
				var p = module.initPanel();
				p.mKey = key;
				p.key = key;
				p.title = module.title
				p.closable = true;
				p.border = false
				// p.frame = false
				this.mainTab.add(p);
				p.__formChis = true;
				p.on("destroy", this.onModuleClose, this)
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
								method : "execute",
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
			}
		}
		this.mainTab.activate(this.midiModules[refModules[0]]);
	},
	onNavTreeClick : function(node, e) {
		// 如果是高血压随访,糖尿病随访，则同时点开档案
		if (node.id == 'A05') {
			this.onNavTreeClick(this.getNodeById('A03'));
		}
		if (node.id == 'A08') {
			this.onNavTreeClick(this.getNodeById('A07'));
		}
		/**begin************zhaojian 2019-03-11 门诊医生站病人列表公卫相关信息提醒增加分组评估提醒***********************/
		if(this.jdrwts && this.jdrwts.indexOf("A05_noyearplan")>=0){
			MyMessageTip.msg("提示", "请先分组评估再进行随访！", true);
			this.jdrwts=this.jdrwts.replace('_noyearplan','');
			this.onNavTreeClick(this.getNodeById('A04'));
			return;
		}
		if(this.jdrwts && this.jdrwts.indexOf("A08_noyearplan")>=0){
			MyMessageTip.msg("提示", "请先分组评估再进行随访！", true);
			this.jdrwts=this.jdrwts.replace('_noyearplan','');
			this.onNavTreeClick(this.getNodeById('A0702'));
			return;
		}
		/**end************zhaojian 2019-01-25 门诊医生站病人列表增加公卫相关信息提醒增加分组评估提醒***********************/
		if (!node.leaf) {
			return;
		}
		if (node.attributes.key) {
			var key = node.attributes.key;
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
					this.exContext.args.JZXH = this.exContext.ids.clinicId|| "";
					cfg.exContext = this.exContext
//					this.emrNavTreePanel.collapse(false)
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
								this.processReturnMsg(comreq1.code,comreq1.msg);
								return;
							} else {
								if (comreq1.json.PhisShowEhrViewType&& comreq1.json.PhisShowEhrViewType == 'paper')
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
					ehrView.on("openNextNode", this.onOpenNextNode, this);
					this.ehrView = ehrView;
					this.ehrView.emrView = this;
					ehrView.exContext.formRecord = true;
					var len = refs.length
					var refModules = [];
					for (var i = 0; i < len; i++) {
						var key = refs[i].substring(refs[i].lastIndexOf("/")+ 1);
						refModules.push(key);
					}
					this.openEHRModel(refModules);
					if (len > 0) {
						var activeTab = node.attributes.activeTab || 0;
						var ak = refs[activeTab];
						if (!ak) {
							ak = refs[0];
						}
						var key = ak.substring(ak.lastIndexOf("/") + 1);
						if (this.activeModules[key]) {
							var finds = this.mainTab.find("mKey", key)
							if (finds.length == 1) {
								var p = finds[0]
								if (key == 'B_011') {
									p.setTitle('个人健康档案');
									var btss = p.topToolbar.items.items;
									for (var ii = 0; ii < btss.length; ii++) {
										if (btss[ii].cmd == 'qk'|| btss[ii].cmd == 'close')
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
							serviceAction : "queryIsAllowed",
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
			if (!this.exContext.ids[this.exContext.ids.empiId + "_"+ this.idsParentNode]) {
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
				module.exContext = this.exContext;
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
				cfg.exContext.pacsid='pacsfs'
				cfg.exContext.url=this.exContext.systemParams.JCIP+"?clinicId="+this.exContext.empiData.MZHM+"&ConType=0&sqbh="+this.mainApp['phisApp'].deptId;
			}
			if (key=="F02") {
				cfg.exContext.pacsid='pacscs'
				cfg.exContext.url=this.exContext.systemParams.JCIP+"?clinicId="+this.exContext.empiData.MZHM+"&ConType=1&sqbh="+this.mainApp['phisApp'].deptId;
			}
			if (key=="F03") {
				cfg.exContext.pacsid='pacsyx'
				cfg.exContext.url=this.exContext.systemParams.YXIP+"?mznum="+this.exContext.empiData.MZHM+"&zynum=&studydate=&sqbh=";
			}
//			this.emrNavTreePanel.collapse(false)
			var p = this.mainTab.add(cfg)
			this.mainTab.doLayout()
			this.mainTab.activate(p)
			this.activeModules[key] = true
		}
	},
	dowinclose : function() {
		this.tree.getLoader().load(this.root);
	},
	initTabItems : function() {
		var items = []
		var ims = this.initModules
		var empiId = this.exContext.ids["empiId"]
		var phrId = this.exContext.ids["phrId"]
		this.closeAllModules()
		var activeModules = {}
		if (!phrId) {
			this.mainTab.add(this.getModuleCfg("B01"))
			activeModules["B01"] = true
		}
		if (ims) {
			for (var i = 0; i < ims.length; i++) {
				var im = ims[i]
				if (!activeModules[im]) {
					this.mainTab.add(this.getModuleCfg(im))
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
		// m.exContext.ids = {};
		m.exContext.empiData = {};
	},

	onModuleActive : function(tabPanel, newTab, curTab) {
		if (!newTab) {
			return;
		}

		if (newTab.__formChis) {
			var ehrModule = this.mainTab.find("mKey", newTab.mKey)[0];
			if (ehrModule && ehrModule.instance
					&& ehrModule.instance.doWhenTabChange) {
				ehrModule.instance.doWhenTabChange();
			}
			return;
		}
		var acM = this.midiModules[newTab.mKey];
		if (acM && acM.refreshWhenTabChange) {
			acM.refreshWhenTabChange();
		};
		if (newTab.mKey.indexOf("A") != -1) {
			if (!this.exContext.args) {
				this.exContext.args = {};
			}
			this.exContext.args.masterplateTypes = this.mtList;
			this.exContext.args.empiId = this.exContext.ids.empiId;
			this.exContext.args.MS_BRZD_JLBH = this.JLBH || "";
			this.exContext.args.JZXH = this.exContext.ids.clinicId || "";
		}
		if (newTab.__actived) {
			if (newTab.mKey == "A30") {
				this.refreshModule(newTab, this.exContext);
			}
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
		this.jzxh = exContext.ids.clinicId;
		this.brid = exContext.ids.brid;
		var cfg = {
			showButtonOnTop : true,
			autoLoadSchema : false,
			isCombined : true,
			mainApp : this.mainApp,
			pinfo : this.pinfo,
			ghlx : this.ghlx,
			sbxh : this.sbxh
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
						m.on("openNextNode", this.onOpenNextNode, this);
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
						id : 'phis.dictionary.emrViewNav'
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
			n = this.tjjyNodes[key];
			if(n){
				Ext.apply(cfg, n)
				cfg.title = n.text;
			}
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
		// var empiId = this.exContext.ids["empiId"]
		var empiId = this.exContext.empiData.empiId // add by zhouyl 2013.09.27
		if (empiId) {
			// var data = this.refreshEmpiInfo(empiId)
			var data = this.loadEmpiInfo(empiId);
			if (data) {
				this.clearModuleData(this);
				Ext.apply(this.exContext.ids, data.ids);
				Ext.apply(this.exContext.empiData, data.empiData);
				console.debug(this.exContext);
			}
		}
		var tpl = this.getNewTopTemplate()
		if (!this.exContext.empiData.address) {
			this.exContext.empiData.address = "";
		}
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = 'resources/phis/resources/images/refresh.jpg';// '0'
		}
		this.dettachTopLnkEnvents()
		this.topPanel.body.update(tpl.apply(this.exContext.empiData))
		this.attachTopLnkEnvents();
		this.loadTJJYTree();
		this.refreshTopIcon();
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
			win.on("add", function() {this.win.doLayout()}, this)
			win.on("show", this.onWinShow, this)
			win.on("hide", function() {this.fireEvent("close", this)}, this)
			win.on("beforehide", function() {this.fireEvent("beforeclose", this)}, this)
			this.win = win
		}
		win.getEl().first().applyStyles("display:none;");
		return win;
	},
	beforeEMRViewClose : function() {
		// 关闭病历首页
		this.win.closeing = true;
		for (var i = 0; i < this.mainTab.items.length; i++) {
			var tab = this.mainTab.getItem(i);
			var m = this.midiModules[tab.mKey];
			if (m.beforeClose) {
				if (!m.beforeClose()) {
					this.win.closeing = false;
					return false;
				}
			}
		}
		this.win.closeing = false;
		return true;
	},
	// 关闭病人信息
	onClose : function() {
		this.mainTab.items.each(function(item) {
					var key = item.mKey;
					if (key != "INDEX") {
						var isInitModule = false;
						if (!isInitModule) {
							this.mainTab.remove(item);
						}
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

	doPrint : function() {
		var module = this.createModule("patientMedicalRecords","phis.application.cic.CIC/CIC/CIC01010101");
		var CLINICID = this.exContext.ids.clinicId;// 就诊序号
		var BRID = this.exContext.ids.brid;// 病人ID
		if (!CLINICID) {
			MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
			return;
		}
		var BRXM = this.exContext.empiData.personName;// 病人姓名
		var XB = this.exContext.empiData.sexCode // 病人性别
		var NL = this.exContext.empiData.birthday // 病人年龄
		var MZHM = this.exContext.empiData.MZHM // 门诊号码
		if (!BRXM) {
			module.BRXM = null;
		} else {
			module.BRXM = encodeURIComponent(BRXM);
		}
		if (!XB) {
			module.XB = null;
		} else {
			module.XB = XB
		}
		if (!NL) {
			module.NL = null;
		} else {
			module.NL = NL
		}
		if (!MZHM) {
			module.MZHM = null;
		} else {
			module.MZHM = MZHM
		}
		module.CLINICID = CLINICID;
		module.BRID = BRID;
		module.initPanel();
		module.doPrint();
	},
	// 打开患者健康档案信息
	doDyhzxx : function() {
		var body = {};
		body["jzksdm"] = this.mainApp.departmentId;
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
	//加载体检检验树
	loadTJJYTree : function() {
		if (this.exContext.systemParams.QYJYBZ == 1|| this.exContext.systemParams.QYTJBGBZ == 1) {
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
						if (this.blNode) {
							this.blNode.remove();
						}
						if (this.ehrNode) {
							this.ehrNode.remove();
						}
						this.initEmrLastNodes = {};
						var blNode = new Ext.tree.TreeNode({
									key : "D",
									text : "体检检验",
									expend : true
								});
						this.blNode = blNode;
						var emrNodeList = json.tjjyTree;
						this.getEMRChildNode(blNode, emrNodeList);
						root.appendChild(blNode);
						root.expandChildNodes(true);
					}, this)
		} else {
			if (this.blNode) {
				this.blNode.remove();
			}
		}
	},
	//加载pacs树
	loadPACSTree : function() {
		if (this.exContext.systemParams.QYJCBZ == 1&&this.exContext.systemParams.JCIP) {
			this.tjjyNodes = {};
			var MZHM = this.exContext.empiData.MZHM 
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
						var emrNodeList = json.pacsTree;
						this.getEMRChildNode(pacsNode, emrNodeList);
						root.appendChild(pacsNode);
						root.expandChildNodes(true);
					}, this)
		} else {
			if (this.pacsNode) {
				this.pacsNode.remove();
			}
		}
	},
	getEMRChildNode : function(pNode, nodeList) {
		for (var i = 0; i < nodeList.length; i++) {
			var node = nodeList[i];
			var n = new Ext.tree.TreeNode({
						id : node.key,
						key : node.key,
						text : node.text,
						requireKeys : node.requireKeys,
						ref : node.ref,
						emrEditor : true,
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
		var re = util.schema.loadSync('chis.application.tr.schemas.MDC_TumourConfirmed')
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
			this.mainTab.items.each(function(item) {
				if (item.mKey == 'T_06') {
					this.activeModules['T_06'] = false;
					this.mainTab.remove(item);
				}
				}, this);
		}
		if (item.YI == 1) {
			var tempNode = this.getNodeById('A33');
			this.mainTab.items.each(function(item) {
						if (item.mKey == 'T_01') {
							this.activeModules['T_01'] = false;
							this.mainTab.remove(item);
						}
					}, this);
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
						var recordIcoDiv = Ext.get('recordIcoDiv_'+ me.ehrviewId);
						// 清空div里的内容
						recordIcoDiv.update("");
						var recordIcoHtml = '';
						// 处理肿瘤易患和现患 以及 残疾人
						var cfObject = {};
						debugger;
						icos = json.icos;
						me.topIcos = icos;
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
											+ icos[k]+ '_'+ me.ehrviewId + '"><img src="photo/'
											+ ico.icos+ '.png" title="'+ ico.icosTitle
											+ '" class="fleft" /></a>&nbsp;&nbsp;&nbsp;</label>';
									cfObject[ico.icos] = [icos[k]];
								} else {
									cfObject[ico.icos].push(icos[k]);
								}

							}
						}
						recordIcoDiv.update(recordIcoHtml);

						for (var k = 0; k < icos.length; k++) {
							var aa = Ext.get("recordIcoDiv_" + icos[k] + '_'+ me.ehrviewId);
							if (aa) {
								var cfKey = me.emrNavDic.wraper[icos[k]].icos;
								// 高血压特殊操作
								if (cfKey == 'GAO') {
									cfObject[cfKey] = ['A05', 'A03'];
								}
								// 糖尿病特殊操作
								if (cfKey == 'TANG') {
									cfObject[cfKey] = ['A08', 'A07'];
								}
								// 肿瘤特殊操作
								if (cfKey == 'YI') {
									var clickfunction = (function() {
										return function() {
											me.showZlmenu(aa, json.zlList,json.zlBao)
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
											var tempNode = me.getNodeById(funs[j]);
											secondNodes.push(tempNode);
										}
										return function() {
											for (var jj = 0; jj < funs.length; jj++) {
												me.onNavTreeClick(secondNodes[jj]);
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

	}
	,showZYYYInfo : function() {
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicHospitalAppointmentService",
					serviceAction : "queryBrryinfo",
					BRID : this.exContext.empiData.BRID
				});
		if (r.code == 600) {
			MyMessageTip.msg("提示", r.msg, true);
		} else {
//			this.clinicHospitalAppointmentModule = this.createModule(
//					"clinicHospitalAppointmentModule","phis.application.cic.CIC/CIC/CIC39");
			/************add by lizhi 2017-12-07增加住院证**************/
			this.clinicHospitalAppointmentModule = this.createModule(
					"clinicHospitalAppointmentModule","phis.application.cic.CIC/CIC/CIC43");
			/************add by lizhi 2017-12-07增加住院证**************/
			this.clinicHospitalAppointmentModule.brxx = this.exContext.empiData;
			this.clinicHospitalAppointmentModule.jzxh = this.exContext.ids.clinicId;
			var win = this.clinicHospitalAppointmentModule.getWin();
			this.clinicHospitalAppointmentModule_win = win;
			win.show();
			if (!win.hidden) {
				this.clinicHospitalAppointmentModule.loadData(this.exContext.empiData.BRID);
			}
		}
	}
})

