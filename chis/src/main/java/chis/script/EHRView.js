$package("chis.script")
$styleSheet("chis.resources.app.biz.EHRView")
$import("chis.script.BizModule", "chis.script.BizPrintWin",
		"util.dictionary.TreeDicFactory", "util.dictionary.DictionaryLoader")
var icos = null;
chis.script.EHRView = function(cfg) {
	this.westWidth = 180
	this.width = 1035;
	this.height = 580
	this.closeNav = false
	this.exContext = cfg.exContext || {}
	this.exContext.ids = {
		empiId : cfg.empiId
	};
	if(cfg.activeTab){
		this.activeTab=cfg.activeTab;
	}
	this.exContext.empiData = {};
	this.exContext.control = {};
	this.exContext.special = {};
	this.exContext.args = cfg.args || {};
	this.activeTab = cfg.activeTab || 0
	this.needInitFirstPanel = cfg.needInitFirstPanel || false
	chis.script.EHRView.superclass.constructor.apply(this, [cfg])
	this.on("close", this.onClose, this)
}
Ext.extend(chis.script.EHRView, chis.script.BizModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		var dic = this.ehrNavDic
		if (!dic) {
			dic = util.dictionary.DictionaryLoader.load({
						id : 'chis.dictionary.ehrViewNav',
						sliceType : 0
					})
			this.ehrNavDic = dic;
		}
		// 根据初始化模块的参数 获取根节点的id 然后获取idloader
		var dic = this.ehrNavDic
		this.idsParentNode = this.initModules[0].substring(0,
				this.initModules[0].indexOf("_"));
		if (!this.exContext.ids[this.exContext.ids.empiId + "_"
				+ this.idsParentNode]) {
			this.exContext.ids.idsLoader = this.ehrNavDic.wraper[this.idsParentNode].idsLoader
			this.exContext.ids[this.exContext.ids.empiId + "_"
					+ this.idsParentNode] = this.exContext.ids.empiId + "_"
					+ this.idsParentNode
		}
		var top = this.createTopPanel();
		var nav = this.createNavPanel();
		var tab = this.createTabPanel();
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
		return panel
	},
	onAfterRender : function() {
		Ext.get(document.body).on("keydown", function(e) {
			var keyCode = e.getKey();
			// 全局键盘事件
			if (e.ctrlKey == true) {
				// ctrl+c ctrl+v 等系统快捷键不屏蔽
				// 86, 90, 88, 67, 65
				if (keyCode == 86 || keyCode == 90 || keyCode == 88
						|| keyCode == 67 || keyCode == 65) {
					return true;
				}
			}
			/**
			 * 屏蔽组合功能键及F1-F12
			 */
			// MyMessageTip.msg("tt",keyCode,true,2);
			if (e.ctrlKey || e.altKey || (keyCode >= 112 && keyCode <= 123)) {
				e.preventDefault();// or e.stopEvent()
				// e.stopEvent();
			}

			// 判断当前是否遮罩状态
			if (Ext.WindowMgr.getActive()
					&& Ext.WindowMgr.getActive().getId() != this.win.getId()) {
				return false;
			}
			/**
			 * 快捷功能一：alt+数字键切换tab页
			 */
			if (e.ctrlKey == true) {
				if (keyCode >= 48 && keyCode <= 57) {
					var tabIndex = keyCode - 48;
					var n = this.mainTab.items.getCount()
					if (n <= 0)
						return;
					tabIndex = tabIndex > n ? n : tabIndex;
					this.mainTab.setActiveTab(this.mainTab.items.item(tabIndex
							- 1));
				}
			}
			/**
			 * 屏蔽组合功能键及F1-F12
			 */
			// MyMessageTip.msg("tt",keyCode,true,2);
			if (e.ctrlKey || e.altKey || (keyCode >= 112 && keyCode <= 123)) {
				e.preventDefault();// or e.stopEvent()
				// e.stopEvent();
			}
			return false;
		}, this);
		Ext.get(document.body).on("keyup", function(e) {
			var keyCode = e.getKey();
			var keyName = "";
			var isCombo = false;
			var f1_f12 = ['F1', 'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9',
					'F10', 'F11', 'F12'];
			if (keyCode >= 16 && keyCode <= 18)
				return;
			if (keyCode >= 112 && keyCode <= 123) {// F1-F12
				keyName = f1_f12[keyCode - 112];
			} else if (isCombo) {// 组合键
				keyName = keyName + String.fromCharCode(keyCode);
			}
			if (!this.lastEnterTime) {
				this.lastEnterTime = 0;
			}
			var thisEnterTime = new Date().getTime();
			// 两次执行之间的间隔，防止重复操作（如果有必要，可增加参数控制哪些按钮需要这个延迟判断）
			if (thisEnterTime - this.lastEnterTime < 500) {
				// MyMessageTip.msg("提示", "两次操作间隔太短，忽略本次操作", false);
				return;
			}
			this.lastEnterTime = thisEnterTime;
			// 全局键盘事件
			if (keyName == "F12") {
				var topDiv = Ext.get("head_bg2_" + this.initModules[0])
						.parent().parent().parent().parent();
				if (topDiv.isDisplayed()) {
					topDiv.setDisplayed('none');
					topDiv.setHeight(0);
					// this.ehrNavTreePanel.hide();
					// 设置mainTab宽度
					this.mainTab.setHeight(this.mainTab.getHeight() + 87)
				} else {
					topDiv.setDisplayed('block');
					// this.ehrNavTreePanel.show();
					topDiv.setHeight(87);
					this.mainTab.setHeight(this.mainTab.getHeight() - 87)
				}
				this.ehrNavTreePanel.setHeight(this.mainTab.getHeight());
				this.panel.doLayout();
				return;
			}
		}, this);

	},
	keyManageFunc : function(keyCode, keyName) {
		var activeTab = this.mainTab.getActiveTab();
		if (activeTab) {
			var module = this.midiModules[activeTab.key];
			if (!module) {
				return;
			}
			if (module.keyManageFunc) {
				module.keyManageFunc(keyCode, keyName);
			} else {
				if (module.btnAccessKeys) {
					var btn = module.btnAccessKeys[keyCode];
					if (btn && btn.disabled) {
						return true;
					}
				}
				module.doAction(module.btnAccessKeys[keyCode]);
			}
		}
	},
	onWinShow : function() {
		this.onAfterRender();
		if (!this.tabsInited) {
			this.initTabItems();
		} else {
			this.refresh();
		}
		this.win.maximize();
		this.refreshTopIcon();
	},
	loadEmpiInfo : function() {
		var res = util.rmi.miniJsonRequestSync({
					serviceId : "chis.idsLoader",
					method : "execute",
					body : this.exContext.ids
				})
		if (res.code == 200) {
			if (!res.json.empiData.photo) {
				res.json.empiData.photo = '0000000000000000.jpg';
			}
			return res.json
		}
		return null;
	},
	getMZZYXX : function() {
		var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.PHSAPeopleInfoService",
					method : "execute",
					serviceAction : "getMZZYXX",
					empiId : this.exContext.ids.empiId
				})
		if (res.code == 200) {
			if (res.json.ZYH) {
				this.exContext.hasHealthRecord = "0"
				this.exContext.ZYH = res.json.ZYH
			}
			if (res.json.JZXH) {
				this.exContext.hasHealthRecord = "0"
				this.exContext.JZXH = res.json.JZXH
			}
		}
	},
	filterNavTree : function(node) {
		// 如果是家庭医生角色过滤
		if (this.mainApp.jobId == 'gp.100' || this.mainApp.jobId == 'gp.101') {
			var gps = ['B_08', 'B_09', 'T', 'G', 'H', 'I', 'X', 'J', 'DC', 'N',
					'P', 'DEF', 'DCIDR', 'M'];
			for (var i = 0; i < gps.length; i++) {
				if (node.id == gps[i]) {
					return false;
				}
			}
		}
		// ------------------------
		// this.getMZZYXX();
		if (this.exContext["hasHealthRecord"] == "0") {
			if (node.id == "N") {
				return true;
			}
			if (this.exContext.ZYH && node.id == "N_02") {
				return true;
			}
			if (this.exContext.JZXH && node.id == "N_01") {
				return true;
			}
			return false;
		}
		// ** 性别为男不显示围产保健管理
		if (node.id == "G"
				&& (this.exContext.empiData["sexCode"] == "1" || this.exContext.empiData.age < 18)) {
			return false;
		}

		// ** 年龄大于等于儿童建册截至年龄的不显示儿童保健管理
		if (node.id == "H"
				&& this.exContext.empiData.age > this.mainApp.exContext.childrenRegisterAge) {
			return false;
		}

		// ** 年龄小于3周岁，不显示3-6岁儿童体格检查
		if (this.exContext.empiData.age < 3) {
			// ** 年龄小于1周岁，不显示1岁以内儿童体格检查
			if (this.exContext.empiData.age < 1) {
				if (node.id == "H_98") {
					return false;
				}
			}
			if (node.id == "H_99") {
				return false;
			}

		}

		// ** 年龄小于等于老年人起始年龄定义的不显示老年人管理
		if ((this.exContext.empiData.age < this.mainApp.exContext.oldPeopleAge || this.exContext.empiData.age <= this.mainApp.exContext.childrenRegisterAge)
				&& node.id == "E") {
			return false;
		}

		if (this.exContext.empiData.age <= 40 && node.id == "Q") {
			return false;
		}
		// ** 年龄小于14周岁的不显示脑残
		if (this.exContext.empiData.age > 14 && node.id == "DEF02") {
			return false;
		}

		// ** 年龄小于14周岁的不显示智力
		if (this.exContext.empiData.age > 14 && node.id == "DEF03") {
			return false;
		}

		// 精神病的不显示
		if (node.id == 'P') {
			return false;
		}
		//add by Wangjl 人群分类不显示
		 if (node.id == 'RQFL') {
				return false;
			}
		// 精神病的不显示
		if (node.id == 'C_06' || node.id == 'C_07' || node.id == 'D_06'
				|| node.id == 'D_07') {
			return false;
		}

		// 体弱儿随访
		if (node.id == 'H_10') {
			return false;
		}

		if (node.id == 'DCIDR') {
			return false;
		}

		// 心血管
		if (this.exContext.empiData.age < 35 && node.id == "M") {
			return false;
		}

		if (node.id == "B_10" || node.id == "B_10_HTML") {
			if (this.mainApp.exContext.healthCheckType == 'form'
					&& node.id == "B_10") {
				return true;
			} else if (this.mainApp.exContext.healthCheckType == 'paper'
					&& node.id == "B_10_HTML") {
				return true;
			} else {
				return false;
			}
		}

		// 妇保新生儿访视html or form

		if (node.id == "G_06" || node.id == "G_06_html") {
			if (this.mainApp.exContext.postnatal42dayType == 'form'
					&& node.id == "G_06") {
				return true;
			} else if (this.mainApp.exContext.postnatal42dayType == 'paper'
					&& node.id == "G_06_html") {
				return true;
			} else {
				return false;
			}
		}
		if (node.id == "G_07" || node.id == "G_07_html") {
			if (this.mainApp.exContext.postnatalVisitType == 'form'
					&& node.id == "G_07") {
				return true;
			} else if (this.mainApp.exContext.postnatalVisitType == 'paper'
					&& node.id == "G_07_html") {
				return true;
			} else {
				return false;
			}
		}

		if (node.id == "G_10" || node.id == "G_10_html") {
			if (this.mainApp.exContext.debilityShowType == 'form'
					&& node.id == "G_10") {
				return true;
			} else if (this.mainApp.exContext.debilityShowType == 'paper'
					&& node.id == "G_10_html") {
				return true;
			} else {
				return false;
			}
		}
		
		//如果没有家庭档案不显示
		if(node.id=='JY')
		{
			if(this.exContext.ids.familyId==null)
				return false;
		}
		return true;
	},
	getTopTemplate : function() {
		if (this.tpl) {
			return this.tpl;
		}
		var photoUrl = ClassLoader.appRootOffsetPath
				+ "photo/{photo}?temp={temp}";
		var html = '<div id="header2">' + '<div id="head_bg2_'
				+ this.initModules[0] + '" class="head_bg2"></div>'
				+ '<div id="topMenu_out">' + '<div id="topMenu_in">'
				+ '<ul class="top_menu_ehr">'
		// + '<li><a href="javascript:void(0)" id="HME">首页</a></li>'
		if (!this.exContext.hisGetEHR) {
			html += '<li><a id="NAV" class="select">导航</a></li>'
					+ '<li><a id="MPI">个人</a></li>'
					+ '<li><a id="FMY">家庭</a></li>'
					// + '<li><a href="javascript:void(0)" id="SWH">切换</a></li>'
					+ '<li><a id="PRT">打印</a></li>'
					+ '<li><a id="HPH">帮助</a></li>';
		}
		this.ehrviewId=Ext.id();
		html += '<li class="none"><a href="javascript:void(0)" id="CLOSE" style="color:red;font-weight:bold;">关闭</a></li>'
				+ '</ul>'
				+ '</div>'
				+ '</div>'
				+ '<div class="TopMessage">'
				+ '<img src="'
				+ photoUrl
				+ '" width="71" height="71"  class="fleft photo" />'
				+ '<div class="fleft">'

				+ '<div class="ehrviewClear">'
				+ '<h2 class="fleft">{personName}<span>({sexCode_text}-{lifeCycle_text})</span><label class ="tit">身份证号:</label><label class="info">{idCard}</label><label class ="tit">编号:</label><label  class="info">{phrid}</label><label class ="tit"> 联系电话:</label><label  class="info">{mobileNumber}</label></h2>'
				//+ '<h2 class="fleft">{personName}<span>({sexCode_text}-{lifeCycle_text})</span><span>身份证号:{idCard}</span><span>编号:{phrid}</span></h2>'
				+ '<div class="fleft" style="margin:0px 20px 20px 20px">&nbsp;</div>'
				+ '<div id="recordIcoDiv_'+this.ehrviewId+'" class="ehrviewInfo_ico"></div>'
				//+ '<div style="margin:4px 0px 0px 0px"><label class="fleft"><a id="ehrviewInfo_skinTest_'+this.ehrviewId+'"><img id="skinTest_'+this.ehrviewId+'" title="查看皮试历史记录" src="'
				//+ ClassLoader.appRootOffsetPath
				//+ 'photo/M.png"/ class="fleft"></a></label>'

				//+ '</div></div>'
				+ '</div>'
				+ '<ul class="detail_ehr">'
				+ '<li class="width20">'
				+ '<p><label>现 住 址：</label>{province}{city}{district}{town}{addr}</p>'
				+ '<p><label>户籍地址：</label>{province}{city}{district}{town}{village}{village2}</p>'
				+ '</li>'
				+ '<li class="width20">'
				+ '<p><label>乡镇(街道)名称：</label>{town}</p>'
				+ '<p><label>村(居)委会名称：</label>{village}</p>' 
				+ '</li>';
				//+ '<li class="width20" >'
				//+ '<p><label>联系电话：</label>{mobileNumber}</p>'
				//+ '<p><label>档案编号：</label></p>'
				//+ '</li>';
		
		//if (this.exContext.hisGetEHR) {
		//	html += '<li class="width20">'
		//			+ '<p><label>门诊号码：</label>{MZHM}</p>'
		//			+ '<p><label>病人性质：</label>{BRXZ}</p></li>'
		//			+ '<li class="none width30">'
		//			+ '<p><label>电&nbsp;&nbsp;&nbsp;&nbsp;话：</label>{mobileNumber}</p>'
		//			+ '<p><label>医疗支付：</label>{insuranceCode_text}</p>'
		//			+ '</li>';
		//} else {
		//	html += '<li class="width30">'
		//			+ '<p><label>电&nbsp;&nbsp;&nbsp;&nbsp;话：</label>{mobileNumber}</p>'
		//			+ '<p><label>住&nbsp;&nbsp;&nbsp;&nbsp;址：</label>{address}</p>'
		//			+ '</li>';
		//}
		html += '<li class="width20">'
					+ '<p><label>建档单位：</label>{createunit}</p>'
					+ '<p><label>建 档 人：</label>{createuser}</p>'
					+ '</li>';
		html += '<li class="width20">'
				+ '<p><label>责任医生：</label>{manaDoctorId_text}</p>'
				+ '<p><label>建档时间：</label>{createdate}</p>'
				+ '</li>';
		html += '<li class="width20">'
				//+ '<p><label>联系电话：</label>{mobileNumber}</p>'
				+ '<p><label>家庭医生：</label>{ysxm}</p>'
				+ '</li>';
		html += '</ul>' + '</div>' + '</div>' + '</div>';
		var tpl = new Ext.XTemplate(html);
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
				Ext.apply(this.exContext.control, data.control);
				Ext.apply(this.exContext.special, data.special);
				if (this.mainApp && this.mainApp.exContext) {
					var tempModules = [];
					for (var i = 0; i < this.initModules.length; i++) {
						var module = this.initModules[i];
						if (data.healthCheckType == 'form'
								&& module == "B_10_HTML") {
							module = "B_10";
						} else if (data.healthCheckType == 'paper'
								&& module == "B_10") {
							module = "B_10_HTML";
						}

						if (data.debilityShowType == 'form'
								&& module == "G_10_html") {
							module = "G_10";
						} else if (data.debilityShowType == 'paper'
								&& module == "G_10") {
							module = "G_10_html";
						}

						if (data.postnatal42dayType == 'form'
								&& module == "G_06_html") {
							module = "G_06";
						} else if (data.postnatal42dayType == 'paper'
								&& module == "G_06") {
							module = "G_06_html";
						}
						if (data.postnatalVisitType == 'form'
								&& module == "G_07_html") {
							module = "G_07";
						} else if (data.postnatalVisitType == 'paper'
								&& module == "G_07") {
							module = "G_07_html";
						}

						tempModules.push(module);
					}
					this.initModules = tempModules;
					this.mainApp.exContext.healthCheckType = data.healthCheckType;

					this.mainApp.exContext.areaGridShowType = data.areaGridShowType;
					this.mainApp.exContext.debilityShowType = data.debilityShowType

					this.mainApp.exContext.areaGridShowType = data.areaGridShowType;
					this.mainApp.exContext.postnatal42dayType = data.postnatal42dayType;
					this.mainApp.exContext.postnatalVisitType = data.postnatalVisitType;

				}
			}
		}
		var tpl = this.getTopTemplate()
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
		var lnks = this.topPanel.body.query(".top_menu_ehr a")
		if (lnks) {
			for (var i = 0; i < lnks.length; i++) {
				var lnk = Ext.get(lnks[i])
				lnk.on("click", this.onTopLnkClick, this)
			}
		}

		// 添加过敏药物
		//var gmyw = Ext.get('ehrviewInfo_skinTest_'+this.ehrviewId);
	//	gmyw.on('click', this.showSkinTest, this);

		var closeLnk = Ext.get('CLOSE');
		if (closeLnk) {
			closeLnk.on("click", function() {
						this.win.hide();
					}, this);
		}
	},
	showSkinTest : function() {
		// 如果存在医疗系统则调用医疗皮试，不存在则打开个人既往史
		var me = this;
		if (me.mainApp.phisActive) {
			var module = me.loadNewModule("skintestHistroyList",
					"phis.application.cic.CIC/CIC/CIC0103", true);
			module.exContext = this.exContext;
			var win = module.getWin();
			win.add(module.initPanel());
			win.show();
		} else {
			var icoId = 'B_03';
			var strs = icoId.split('_');
			var secondNode = {
				leaf : true,
				attributes : {
					key : icoId
				},
				parentNode : {
					attributes : {
						key : strs[0],
						idsLoader : me.ehrNavDic.wraper[strs[0]]
					}
				}
			};
			me.onNavTreeClick(secondNode)

		}
	},
	dettachTopLnkEnvents : function() {
		var lnks = this.topPanel.body.query(".top_menu_ehr a")
		if (lnks) {
			for (var i = 0; i < lnks.length; i++) {
				var lnk = Ext.get(lnks[i])
				lnk.un("click", this.onTopLnkClick, this)
			}
		}
	},
	onTopLnkClick : function(e) {
		var lnk = e.getTarget();
		var cmd = lnk.id;
		switch (cmd) {
			case "HPH" :
				window.open("resources/chis/help/BS-CHIS-HELP.html");
				break;
			case "PRT" :
				this.showPrintView()
				break
			case "SWH" :
				break
			case "FMY" :
				this.showFMYInfo();
				break;
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
				this.win.hide();
				break;
		}
	},
	showPrintView : function() {
		var tw = this.midiModules["printView"]
		if (tw) {
			Ext.apply(tw, {
						ids : this.exContext.ids
					})
			tw.getWin().show()
			return
		}
		tw = new chis.script.BizPrintWin({
					ids : this.exContext.ids
				})
		this.midiModules["printView"] = tw
		tw.getWin().show()
	},
	showMPIInfo : function() {
		var m = this.midiModules['mpi']
		if (!m) {
			$import("chis.application.mpi.script.EMPIInfoModule")
			m = new chis.application.mpi.script.EMPIInfoModule({
						serviceAction : "updatePerson",
						title : "个人基本信息",
						modal : true,
						mainApp : this.mainApp
					});
			m.on("onEmpiReturn", function(data) {
						m.getWin().hide();
						this.refreshTopEmpi();
						if (!this.activeModules["H_01"]) {
							return;
						}
						this.onRefreshModuleData("H_01");
						var active = this.mainTab.getActiveTab();
						if (active.mKey == "H_01") {
							this.refreshModule(active);
						}
						var visitPlanChanged = data.visitPlanChanged;
						if (visitPlanChanged) {
							var refreshModules = ["H_03", "H_97", "H_98",
									"H_99"];
							var active = this.mainTab.getActiveTab();
							for (var i = 0; i < refreshModules.length; i++) {
								var module = refreshModules[i];
								if (!this.activeModules[module]) {
									continue;
								}
								this.onRefreshModuleData(module);
								if (active.mKey == module) {
									this.refreshModule(active);
								}
							}
						}
					}, this);
			this.midiModules['mpi'] = m;
		}
		m.getWin().show();
		m.clear();
		m.setRecord(this.exContext.ids.empiId)
	},
	showFMYInfo : function() {
		var familyId = this.exContext.ids.familyId;
		if (!familyId) {
			Ext.MessageBox.alert("提示信息", "该用户没有建立所属的家庭档案！")
			return;
		}
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.familyRecordService",
					serviceAction : "getFamilyForEHRView",
					method : "execute",
					body : {
						"pkey" : familyId
					}
				})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		var body = result.json.body;
		if (body) {
			var record = new Ext.data.Record(body);
			var module = this.midiModules["FamilyRecordModule"]
			if (!module) {
				module = this.createCombinedModule("FamilyRecordModule",
						"chis.application.fhr.FHR/FHR/B011");
			}
			module.initDataId = familyId;
			module.exContext["chis.application.fhr.schemas.EHR_FamilyRecord"] = record;
			var win = module.getWin();
			win.setPosition(250, 150);
			win.show();
			module.loadData();
		}
	},
	createNavPanel : function() {
		var ehrNavTree = util.dictionary.TreeDicFactory.createTree({
					id : "chis.dictionary.ehrViewNav",
					sliceType : 0
				})
		ehrNavTree.on("click", this.onNavTreeClick, this)
		this.ehrNavTree = ehrNavTree

		var root = ehrNavTree.getRootNode()
		root.on("load", function() {
					this.ehrNavTree.filter.filterBy(this.filterNavTree, this)
				}, this)
		root.on("expand", function(root) {
					var firstNode = root.firstChild;
					firstNode.on("load", function() {
								this.ehrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
					var childNode = root.findChild("id", "H")
					childNode.on("load", function() {
								this.ehrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
					var WemanNode = root.findChild("id", "G")
					WemanNode.on("load", function() {
								this.ehrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
					var defNode = root.findChild("id", "DEF")
					defNode.on("load", function() {
								this.ehrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
					var hisNode = root.findChild("id", "N")
					hisNode.on("load", function() {
								this.ehrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
					var mhcNode = root.findChild("id", "G")
					mhcNode.on("load", function() {
								this.ehrNavTree.filter.filterBy(
										this.filterNavTree, this)
							}, this)
				}, this);

		var ehrNavTreePanel = new Ext.Panel({
					border : false,
					frame : false,
					layout : "fit",
					split : true,
					collapsible : true,
					title : '文件夹',
					region : 'west',
					width : this.westWidth,
					collapsed : this.closeNav,
					items : ehrNavTree
				})
		this.ehrNavTreePanel = ehrNavTreePanel
		return ehrNavTreePanel
	},
	onNavTreeClick : function(node, e) {
		if (!node.leaf) {
			return;
		}
		var key = node.attributes.key
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
				Ext.apply(this.exContext.control, data.control);
				Ext.apply(this.exContext.special, data.special);
			}
		}
		var cfg = this.getModuleCfg(key)
		if (cfg.disabled) {
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
		this.ehrNavTreePanel.collapse(false)
		var p = this.mainTab.add(cfg)
		this.mainTab.doLayout()
		this.mainTab.activate(p)
		if (key == 'B_011') {
			var topbar = p.items.items[0].topToolbar;
			var btss = topbar.items.items;
			for (var ii = 0; ii < btss.length; ii++) {
				if (btss[ii].cmd == 'qk' || btss[ii].cmd == 'close')
					topbar.remove(btss[ii]);
			}
		}
		this.activeModules[key] = true
	},
	createTabPanel : function() {
		var items = []
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
	refreshModule : function(newTab) {
		if (!newTab) {
			return;
		}
		var key = newTab.mKey;
		var m = this.midiModules[key]
		if (m) {
			delete m.exContext;
			m.exContext = {};
			Ext.apply(m.exContext, this.exContext);
			m.exContext.control = this.exContext.control[m.controlKey];
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
	clearModuleData : function() {
		this.exContext.ids = {};
		this.exContext.empiData = {};
		this.exContext.control = {};
		this.fireEvent("clearModuleData", this.exContext)
	},

	onModuleActive : function(tabPanel, newTab, curTab) {
		// modify by yuhua 2013-07-05
		if (!newTab) {
			return;
		}
		if (newTab.disabled && !this.needInitFirstPanel) {
			return;
		}
		if (newTab.__actived) {
			var acM = this.midiModules[newTab.mKey];
			if (acM.refreshWhenTabChange) {
				acM.refreshWhenTabChange();
			};
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
			autoLoadData : false,
			isCombined : true,
			isAtEHRView : true,
			mainApp : this.mainApp,
			exContext : this.exContext
		}
		Ext.apply(cfg, exCfg);
		var ref = cfg.properties.ref
		if (ref) {
			var result = util.rmi.miniJsonRequestSync({
						url : 'app/loadModule',
						id : ref
					})
			if (result.code == 200) {
				Ext.apply(cfg, result.json.body)
				Ext.apply(cfg, result.json.body.properties)
			}
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
		m.on("save", this.onModuleSave, this)
		m.on("activeModule", this.onActiveModule, this);
		// 增加一个标签页，不激活
		m.on("addModule", this.onAddModule, this)
		m.on("refreshEhrView", this.onRefreshEhrView, this)
		m.on("refreshData", this.onRefreshModuleData, this); // **
		// 清除某个页面的__actived标志,下次打开改页面刷新数据
		m.on("refreshModule", this.onRefreshModule, this) // **
		// 刷新某个module,idLoader会重新加载
		m.on("clearCache", this.onClearCache, this)
		m.ehrview = this;
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
	},
	onModuleClose : function(p) {
		var m = this.midiModules[p.mKey]
		if (m) {
			m.destory()
			delete this.midiModules[p.mKey]
			delete this.activeModules[p.key]
		}
	},
	onChisSave : function() {
		this.fireEvent("chisSave");
	},
	onModuleSave : function(entryName, op, json, data) {
		this.fireEvent("save", entryName, op, json, data)
		var body = json.body;
		var result = util.schema.loadSync(entryName);
		if (result.code != 200) {
			this.processReturnMsg(result.code, result.msg);
		}
		var schema = result.schema;
		var pkeyName = schema.pkey;
		var entityName = entryName.substr(entryName.lastIndexOf(".") + 1);
		var fullPkeyName;
		if (entryName == "chis.application.hr.schemas.EHR_HealthRecord") {
			fullPkeyName = pkeyName;
		} else {
			fullPkeyName = entityName + "." + pkeyName;
		}
		var idValue = body[pkeyName];
		if (!idValue) {
			return;
		}
		if (op == "create") {
			var controlKey = entityName + "_control";
			var control = this.exContext.control[controlKey];
			if (control && control.create) {
				control.update = control.create;
			}
		}
		// ** 孕妇档案根据主键查找数据，因此需要为主键存放一个值
		if (entityName == "MHC_PregnantRecord") {
			this.exContext.ids[pkeyName] = idValue;
		}
		this.exContext.ids[fullPkeyName] = idValue;
		var curKey = this.mainTab.getActiveTab().mKey
		this.mainTab.items.each(function(p) {
					var key = p.mKey
					if (key == curKey) {
						return;
					}
					if (!p.disabled) {
						return;
					}
					var cfg = this.getModuleCfg(key)
					if (!cfg.disabled) {
						p.enable()
					}
				}, this)
	},
	onActiveModule : function(module, args) {
		if (!this.activeModules) {
			return;
		}
		var phModule = this.mainTab.find("mKey", module)[0];
		Ext.apply(this.exContext.args, args);
		if (phModule) {
			this.activeModules[module] = true;
			if (phModule.__actived == true) {
				phModule.__actived = false;
			}
			this.mainTab.activate(phModule)
		} else {
			this.activeModules[module] = true
			phModule = this.getModuleCfg(module);
			var activeTab = this.mainTab.getActiveTab();
			var index = 0;
			if (this.mainTab.items.length > 1) {
				index = this.mainTab.items.indexOf(activeTab) + 1;
			}
			this.mainTab.insert(index, phModule);
			this.mainTab.activate(index);
		}
	},

	onAddModule : function(module, closable) {
		if (!this.activeModules) {
			this.fireEvent("ehrAddModule", [module]);
			return;
		}
		var phModule = this.mainTab.find("mKey", module)[0];
		if (!phModule) {
			phModule = this.getModuleCfg(module);
			if (closable) {
				phModule.closable = closable;
			}
			if (this.mainTab.items.length > 1) {
				var index = this.mainTab.items.length;
				this.mainTab.insert(index, phModule);
				this.activeModules[module] = true;
			}
		}
	},

	onRefreshEhrView : function(nodeId) {
		this.idsParentNode = nodeId.substring(0, nodeId.indexOf("_"));
		this.exContext.ids[this.exContext.ids.empiId + "_" + this.idsParentNode] = this.exContext.ids.empiId
				+ "_" + this.idsParentNode
		var data = this.loadEmpiInfo()
		if (data) {
			Ext.apply(this.exContext.ids, data.ids);
			Ext.apply(this.exContext.empiData, data.empiData);
			Ext.apply(this.exContext.control, data.control);
			Ext.apply(this.exContext.special, data.special);
		}
		this.clearAllActived();
		this.refreshModule(this.mainTab.find("mKey", nodeId)[0])
	},

	onRefreshModule : function(nodeId) {
		this.idsParentNode = nodeId.substring(0, nodeId.indexOf("_"));
		this.exContext.ids[this.exContext.ids.empiId + "_" + this.idsParentNode] = this.exContext.ids.empiId
				+ "_" + this.idsParentNode
		if (nodeId == "H_09") {
			this.exContext.ids.idsLoader = "chis.debilityChildrenIdLoader"
		}
		var data = this.loadEmpiInfo()
		if (data) {
			Ext.apply(this.exContext.ids, data.ids);
			Ext.apply(this.exContext.empiData, data.empiData);
			Ext.apply(this.exContext.control, data.control);
			Ext.apply(this.exContext.special, data.special);
		}
	},
	onRefreshModuleData : function(key) {
		if (key == "all") { // ** 清空当前ehrView的所有__actived标志
			this.clearAllActived();
		} else { // ** 清空某一个页面的__actived标志
			var finds = this.mainTab.find("mKey", key)
			if (finds.length == 1) {
				var p = finds[0]
				p.__actived = false;
			}
		}
	},
	getModuleCfg : function(key) {
		debugger;
		var dic = this.ehrNavDic
		if (!dic) {
			dic = util.dictionary.DictionaryLoader.load({
						id : 'chis.dictionary.ehrViewNav'
					})
			this.ehrNavDic = dic;

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
		cfg.exContext = {};
		Ext.apply(cfg.exContext, this.exContext);
		var controlKey = cfg.controlKey
		if (controlKey) {
			cfg.exContext.control = this.exContext.control[controlKey]
		}
		return cfg;
	},
	initTabItems : function() {
		var items = []
		var ims = this.initModules
		var phrId = this.exContext.ids["phrId"]
		var p;
		this.closeAllModules()
		var activeModules = {}
		if (!phrId && !this.exContext["hasHealthRecord"]) {
			this.mainTab.add(this.getModuleCfg("B_01"))
			activeModules["B_01"] = true
		}

		if (ims) {
			for (var i = 0; i < ims.length; i++) {
				var im = ims[i]
				if (!activeModules[im]) {
					p=this.mainTab.add(this.getModuleCfg(im))
					activeModules[im] = true
				}
			}
		}

		this.activeModules = activeModules
		if(p){
			this.mainTab.activate(p)
		}
		this.mainTab.setActiveTab(this.activeTab);
		this.tabsInited = true;
	},
	refreshInfo:function()
	{
		var data = this.loadEmpiInfo()
		if (data) {
			this.clearModuleData();
			Ext.apply(this.exContext.ids, data.ids);
			Ext.apply(this.exContext.empiData, data.empiData);
			Ext.apply(this.exContext.control, data.control);
			Ext.apply(this.exContext.special, data.special);
			this.exContext.ids.idsLoader = this.ehrNavDic.wraper[this.idsParentNode].idsLoader
			this.exContext.ids[this.exContext.ids.empiId + "_"
					+ this.idsParentNode] = this.exContext.ids.empiId + "_"
					+ this.idsParentNode
			}
		var tpl = this.getTopTemplate()
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = '0000000000000000.jpg'
		}
		this.dettachTopLnkEnvents()
		this.topPanel.body.update(tpl.apply(this.exContext.empiData))
		this.attachTopLnkEnvents()
	},
	refresh : function() {
		var dic = this.ehrNavDic
		this.idsParentNode = this.initModules[0].substring(0,
				this.initModules[0].indexOf("_"))
		if (!this.exContext.ids[this.exContext.ids.empiId + "_"
				+ this.idsParentNode]) {
			this.exContext.ids.idsLoader = this.ehrNavDic.wraper[this.idsParentNode].idsLoader
			this.exContext.ids[this.exContext.ids.empiId + "_"
					+ this.idsParentNode] = this.exContext.ids.empiId + "_"
					+ this.idsParentNode
			var data = this.loadEmpiInfo()
			if (data) {
				this.clearModuleData();
				Ext.apply(this.exContext.ids, data.ids);
				Ext.apply(this.exContext.empiData, data.empiData);
				Ext.apply(this.exContext.control, data.control);
				Ext.apply(this.exContext.special, data.special);
				this.exContext.ids.idsLoader = this.ehrNavDic.wraper[this.idsParentNode].idsLoader
				this.exContext.ids[this.exContext.ids.empiId + "_"
						+ this.idsParentNode] = this.exContext.ids.empiId + "_"
						+ this.idsParentNode
			}
		}
		var tpl = this.getTopTemplate()
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = '0000000000000000.jpg'
		}
		this.dettachTopLnkEnvents()
		this.topPanel.body.update(tpl.apply(this.exContext.empiData))
		this.attachTopLnkEnvents()
		// actionName is the module name
		if (this.actionName != "EHR_HealthRecord") {
			// for phrdoc
			var phrId = this.exContext.ids.phrId
			if (phrId && this.activeModules["B_01"]) {
				this.activeModules["B_01"] = false
				if (this.mainTab.find("mKey", "B_01")) {
					this.mainTab.remove(this.mainTab.find("mKey", "B_01")[0])
				}
			} else if ((phrId == "" || phrId == null)
					&& !this.activeModules["B_01"]) {
				this.activeModules["B_01"] = true
				this.mainTab.insert(0, this.getModuleCfg("B_01"))
			}

			if (this.activeModules["B_03"] && !this.exContext["needB03"]) {
				this.activeModules["B_03"] = false
				if (this.mainTab.find("mKey", "B_03")) {
					this.mainTab.remove(this.mainTab.find("mKey", "B_03")[0])
				}
			}
			if (this.actionName != "MDC_OldPeopleRecord") {
				if (this.activeModules["B_07"]) {
					this.activeModules["B_07"] = false
					if (this.mainTab.find("mKey", "B_07")) {
						this.mainTab
								.remove(this.mainTab.find("mKey", "B_07")[0])
					}
				}
			}
		}

		// // process tabs
		var active = this.mainTab.getActiveTab()
		var needActive = this.mainTab.items.item(this.activeTab);
		var tabs = this.mainTab.items
		var n = tabs.getCount()
		for (var i = 0; i < n; i++) {
			var t = tabs.item(i)
			var mKey = t.mKey
			var cfg = this.getModuleCfg(mKey)
			if (cfg.disabled) {
				t.disable()
				continue;
			} else {
				t.enable();
			}
			if (active && active.mKey == needActive.mKey && active.mKey == mKey) {
				this.refreshModule(active)
			} else if (needActive.mKey == mKey) {
				this.mainTab.activate(this.activeTab);
			}
		}
		// filter nav tree
		this.ehrNavTree.filter.filterBy(this.filterNavTree, this)
	},
	refreshTopEmpi : function() {
//		var empiId = this.exContext.ids["empiId"]
//		if (!empiId) {
//			return;
//		}
//		var res = util.rmi.miniJsonRequestSync({
//					serviceId : "chis.empiiLoader",
//					method : "execute",
//					body : {
//						"empiId" : empiId
//					}
//				})
//		if (res.code != 200) {
//			return;
//		}
//		var empiData = res.json.body;
		var empiData = this.loadEmpiInfo().empiData;
		if (!empiData.photo) {
			empiData.photo = '0000000000000000.jpg';
		}
		this.exContext.empiData = {};
		Ext.apply(this.exContext.empiData, empiData);
		var tpl = this.getTopTemplate()
		if (!this.exContext.empiData.photo) {
			this.exContext.empiData.photo = '0000000000000000.jpg'
		}
		this.dettachTopLnkEnvents()
		this.topPanel.body.update(tpl.apply(this.exContext.empiData))
		this.attachTopLnkEnvents()
		this.refreshTopIcon();
	},
	loadData : this.refresh,
	closeAllModules : function() {
		this.mainTab.items.each(function(item) {
					var key = item.mKey;
					this.mainTab.remove(item);
					this.activeModules[key] = false
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
						closable : false,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : false,
						resizable : false,
						maximizable : false,
						shadow : false,
						modal : true,
						border : false,
						plain : true,
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
					}, this);
			this.win = win;
		}
		win.getEl().first().applyStyles("display:none;");
		win.instance = this;
		return win;
	},
	onClose : function() {
		this.mainTab.setActiveTab(this.activeTab);
		this.mainTab.items.each(function(item) {
					var key = item.mKey;
					var isInitModule = false;
					for (var i = 0; i < this.initModules.length; i++) {
						var im = this.initModules[i]
						if (im == key) {
							isInitModule = true;
						}
					}
					// alert(!isInitModule)
					if (!isInitModule) {
						this.mainTab.remove(item, true);
						this.activeModules[key] = false
					}
				}, this);
		this.clearModuleData(this);
		this.clearAllActived();
	},
	/**
	 * 清空所有激活标识
	 */
	clearAllActived : function() {
		this.mainTab.items.each(function(item) {
					item.__actived = false;
				}, this);
	},
	onClearCache : function(mId) {
		if (this.exContext.ids[this.exContext.ids.empiId + "_" + mId]) {
			this.exContext.ids[this.exContext.ids.empiId + "_" + mId] = ""
		}
	},

	// -----------------提供运程调用------------------------
	getEHRModuleCfg : function(refModules) {
		var dic = util.dictionary.DictionaryLoader.load({
					id : 'chis.dictionary.ehrViewNav'
				})
		var modules = {};
		for (var i = 0, len = refModules.length; i < len; i++) {
			var nodeId = refModules[i];
			this.idsParentNode = nodeId.substring(0, nodeId.indexOf("_"));
			if (!this.exContext.ids[this.exContext.ids.empiId + "_"
					+ this.idsParentNode]) {
				this.exContext.ids.idsLoader = dic.wraper[this.idsParentNode].idsLoader
				this.exContext.ids[this.exContext.ids.empiId + "_"
						+ this.idsParentNode] = this.exContext.ids.empiId + "_"
						+ this.idsParentNode
				var data = this.loadEmpiInfo()
				if (data) {
					Ext.apply(this.exContext.ids, data.ids);
					Ext.apply(this.exContext.empiData, data.empiData);
					Ext.apply(this.exContext.control, data.control);
					Ext.apply(this.exContext.special, data.special);
				}
			}
			var exCfg = this.getModuleCfg(nodeId)
			var cfg = {
				showButtonOnTop : true,
				autoLoadSchema : false,
				autoLoadData : false,
				isCombined : true,
				isAtEHRView : true,
				closable : true,
				mainApp : this.mainApp,
				exContext : this.exContext
			}
			Ext.apply(cfg, exCfg);
			cfg.closable = true;
			var ref = cfg.properties.ref
			if (ref) {
				var result = util.rmi.miniJsonRequestSync({
							url : 'app/loadModule',
							id : ref
						})
				if (result.code == 200) {
					Ext.apply(cfg, result.json.body)
					Ext.apply(cfg, result.json.body.properties)
				}
			}
			var cls = cfg.script
			if (!cls) {
				return;
			}
			if (!this.fireEvent("beforeload", cfg)) {
				return;
			}
			$import(cls);
			var m = eval("new " + cls + "(cfg)")
			m.setMainApp(this.mainApp);
			m.on("save", this.onModuleSave, this)
			m.on("chisSave", this.onChisSave, this)
			m.on("activeModule", this.onActiveModule, this);
			// 增加一个标签页，不激活
			m.on("addModule", this.onAddModule, this)
			m.on("refreshEhrView", this.onRefreshEhrView, this)
			m.on("refreshData", this.onRefreshModuleData, this); // **
			// 清除某个页面的__actived标志,下次打开改页面刷新数据
			m.on("refreshModule", this.onRefreshModule, this) // **
			// 刷新某个module,idLoader会重新加载
			m.on("clearCache", this.onClearCache, this)
			m.ehrview = this;
			modules[nodeId] = m
		}
		return modules;
	},
	getNodeById:function(id)
	{
		var me=this;
		var icoId =id;
		var strs = icoId.split('_');
		return {
					leaf : true,
					attributes : {
						key : icoId
					},
					parentNode : {
						attributes : {
							key : strs[0],
							idsLoader : me.ehrNavDic.wraper[strs[0]].idsLoader
						}
					}
				};
	},
	loadZldata:function(item,zlBao)
	{
		var comreq1 = util.rmi.miniJsonRequestSync({
			serviceId : "chis.CommonService",
			serviceAction : "loadTrRecordId",
			method : "execute",
			body : {
				empiId : this.exContext.ids.empiId,
				highRiskType:''+item.typeValue
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
		this.exContext.args.loadAction="getTCByEH";
		
		var schema='';
		var re = util.schema.loadSync('chis.application.tr.schemas.MDC_TumourConfirmed')
			if(re.code == 200){
				schema = re.schema;
			}
			else{
				return;
			}
		if(comreq1.json.tcrData)
			this.exContext.args.tcrData=this.castListDataToForm(comreq1.json.tcrData,schema);
		
		//肿瘤报告卡
		if(zlBao=='1'){
			var tempNode = this.getNodeById('T_04');
			this.onNavTreeClick(tempNode);
		}
		//存在易患
		if(item.XIAN==1)
		{
			var tempNode = this.getNodeById('T_06');
			this.onNavTreeClick(tempNode);	
		}
		if(item.YI==1)
		{
			var tempNode = this.getNodeById('T_01');
			this.onNavTreeClick(tempNode);
		}
	},
	showZlmenu : function(button,zlList,zlBao) {
		//显示肿瘤情况
		var me = this;
		//如果只有一种肿瘤则点击直接显示易患,现患,报告卡。
		if(zlList.length==1)
		{
			var item=zlList[0];

			this.loadZldata(item,zlBao);
			return;
		}
		//如果多种类型的肿瘤
		if(!this.zlMenu)
		{
			this.zlMenu= new Ext.menu.Menu();
		}
		this.zlMenu.removeAll();
		for(var i=0,l=zlList.length;i<l;i++)
		{
			var it=zlList[i];
			
			var clickFun=(function(){
				var item=it;
				return function()
			 	{
					me.loadZldata(item,zlBao);
			 	}
			})()
			var menuItem={
						 	text:it.typeName,
						 	handler:clickFun
			 		 }
			this.zlMenu.add(menuItem);
		}
		this.zlMenu.show(button);
	},
	// 刷新头部档案图标
	refreshTopIcon : function() {
		// 判断是否启用参数
		if (false)
			return;

		var me = this;
		var empiId = me.exContext.empiData.empiId;
		// 获取档案
		util.rmi.miniJsonRequestAsync({
					serviceId : "chis.CommonService",
					serviceAction : "loadRecordInfo",
					method : "execute",
					body : {
						empiId : empiId
					}
				}, function(code, msg, json) {
					debugger;
					if (code < 300) {
						var recordIcoDiv = Ext.get('recordIcoDiv_'+me.ehrviewId);
						// 清空div里的内容
						recordIcoDiv.update("");
						var recordIcoHtml = '';
						icos = json.icos;
       					//2019-10-22 zhaojian 公卫系统签约校验人群分类是否包含健康档案图标分类
						this.icos = json.icos;
						// 处理肿瘤易患和现患 以及 残疾人
						var cfObject = {};
						if (icos) {
							for (var k = 0; k < icos.length; k++) {
								var ico = this.ehrNavDic.wraper[icos[k]];
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
												+ icos[k]+'_'+me.ehrviewId
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

								var aa = Ext.get("recordIcoDiv_" + icos[k]+'_'+me.ehrviewId);
								if (aa) {
									var cfKey = me.ehrNavDic.wraper[icos[k]].icos;
									
									//肿瘤特殊操作
									if(cfKey=='YI')
									{
										
										var clickfunction = (function() {
											return function() {
												me.showZlmenu(aa,json.zlList,json.zlBao)
											}
										})();
										aa.on('click',clickfunction);
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
													me
															.onNavTreeClick(secondNodes[jj]);
												}
											}
										})();

										aa.on('click', clickfunction)

									}

								}
							}
						}

					} else {
						this.processReturnMsg(code, msg);
					}
				}, this);
	}
})

// ================ print win ===============
