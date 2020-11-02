$package("chis.application.conf.script.guide")
$import("chis.script.BizModule")
$styleSheet("ext.ext-all")
chis.application.conf.script.guide.SystemGuideView = function(cfg) {
	chis.application.conf.script.guide.SystemGuideView.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
	this.on("beforeclose", this.onBeforeClose, this);
}
Ext.extend(chis.application.conf.script.guide.SystemGuideView, chis.script.BizModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		var panel = new Ext.Panel({
					border : false,
					split : true,
					hideBorders : true,
					frame : this.frame || false,
					layout : 'border',
					items : [{
								layout : "fit",
								split : true,
								collapsible : false,
								title : '',
								region : 'center',
								items : this.getFirstPanel()
							}]
				});
		this.panel = panel;
		return panel
	},
	getFirstPanel : function() {
		this.checkSystemInited();
		var panel = new Ext.Panel({
			html : '<div style="padding:21%;background-color:#E0FFFF;font-size:18px"><p>您的社区卫生服务信息系统未完成系统设置，</p>'
					+ '<p>以下将对社区卫生服务信息系统进行系统设置，</p>'
					+ '<p>请按"下一步"开始设置</p>'
					+ '<p>配置完成后,请重新登录以激活配置！</p></div>'
		});
		return panel;
	},

	checkSystemInited : function() {
		var result = util.rmi.miniJsonRequestSync({
					serviceId : 'chis.systemGuideService',
					serviceAction : "checkSystemInited",
					method:"execute"
				})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return;
		}
		if (result.json.body) {
			var all = result.json.body.allInited;
			var size = result.json.body.size;
			this.size = size - 1;
			if (all == "true") {
				this.flag = false;
			} else {
				this.flag = true;
				this.count = 0;
				this.cSize = 0;
				this.guideSort(result.json.body);
			}
		}
	},
	guideSort : function(body) {
		this.guideItem = {
			systemCommonInited : body.systemCommonInited,
//			unitTypeInited : body.unitTypeInited,
			interfaceInited : body.interfaceInited,
			zookeeperInited:body.zookeeperInited,
			planTypeInited : body.planTypeInited,
			oldPeopleInited : body.oldPeopleInited,
			hypertensionInited : body.hypertensionInited,
			diabetesInited : body.diabetesInited,
			childrenInited : body.childrenInited,
			debilityChildrenInited : body.debilityChildrenInited,
			pregnantInited : body.pregnantInited,
			psychosisInited : body.psychosisInited
		};
	},
	createGuideButtons : function() {
		var actions = [{
					id : "previous",
					name : "上一步",
					handler : function() {
						this.on("click", this.doPrevious(), this)
					}
				}, {
					id : "next",
					name : "下一步",
					handler : function() {
						this.on("click", this.doSave(), this)
					}
				}, {
					id : "cencel",
					name : "取消",
					handler : function() {
						this.on("click", this.doClose(), this)
					}
				}]
		if (this.count == 0) {
			var actions = [{
						id : "next",
						name : "下一步",
						handler : function() {
							this.on("click", this.doSave(), this)
						}
					}, {
						id : "cencel",
						name : "取消",
						handler : function() {
							this.on("click", this.doClose(), this)
						}
					}]
		}
		if (this.cSize == this.size) {
			var actions = [{
						id : "previous",
						name : "上一步",
						handler : function() {
							this.on("click", this.doPrevious(), this)
						}
					}, {
						id : "next",
						name : "完成",
						handler : function() {
							this.on("click", this.doSave(), this)
						}
					}, {
						id : "cencel",
						name : "取消",
						handler : function() {
							this.on("click", this.doClose(), this)
						}
					}]

		}
		var buttons = [];
		if (!actions) {
			return buttons
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {}
			btn.accessKey = f1 + i, btn.cmd = action.id
			btn.text = action.name, btn.iconCls = action.iconCls || action.id
			btn.script = action.script
			btn.handler = action.handler;
			btn.prop = {};
			btn.id = action.id;
			Ext.apply(btn.prop, action);
			btn.scope = this;
			buttons.push(btn)
		}
		return buttons;
	},
	doPrevious : function() {
		if (this.count != 0 && this.cSize == 1) {
			this.count = 0;
			this.getWin().hide();
			this.win = null;
			this.panel = null;
			var win = this.getWin();
			win.show();
		} else {
			var panelName = this.getPrePanelName();
			if (panelName) {
				this.cSize--;
			}
			this.showNewWin(panelName);
		}
		this.saveToApp(panelName, "pre");
	},
	doSave : function() {
		if (this.cSize == this.size) {
			this.doClose();
			return;
		}
		var panelName = this.getNextPanelName();
		if (panelName) {
			this.cSize++;
		}
		this.showNewWin(panelName);
		this.saveToApp(panelName,"next");
	},
	getPrePanelName : function() {
		var setPanelData = "";
		var panelName = "";
		if (!setPanelData && this.count > 10) {
			setPanelData = this.guideItem.pregnantInited;
			panelName = "pregnantInited";
			this.count = 10;
		}
		if (!setPanelData && this.count > 9) {
			setPanelData = this.guideItem.debilityChildrenInited;
			panelName = "debilityChildrenInited";
			this.count = 9;
		}
		if (!setPanelData && this.count > 8) {
			setPanelData = this.guideItem.childrenInited;
			panelName = "childrenInited";
			this.count = 8;
		}
		if (!setPanelData && this.count > 7) {
			setPanelData = this.guideItem.diabetesInited;
			panelName = "diabetesInited";
			this.count = 7;
		}
		if (!setPanelData && this.count > 6) {
			setPanelData = this.guideItem.hypertensionInited;
			panelName = "hypertensionInited";
			this.count = 6;
		}
		if (!setPanelData && this.count > 5) {
			setPanelData = this.guideItem.oldPeopleInited;
			panelName = "oldPeopleInited";
			this.count = 5;
		}
		if (!setPanelData && this.count > 4) {
			setPanelData = this.guideItem.planTypeInited;
			panelName = "planTypeInited";
			this.count = 4;
		}
		if (!setPanelData && this.count > 3) {
			setPanelData = this.guideItem.interfaceInited;
			panelName = "interfaceInited";
			this.count = 3;
		}
		if (!setPanelData && this.count > 2) {
			setPanelData = this.guideItem.zookeeperInited;
			panelName = "zookeeperInited";
			this.count = 2;
		}
		if (!setPanelData && this.count > 1) {
			setPanelData = this.guideItem.systemCommonInited;
			panelName = "systemCommonInited";
			this.count = 1;
		}
		return panelName;
	},
	getNextPanelName : function() {
		var setPanelData = "";
		var panelName = "";
		if (this.count == 0) {
			setPanelData = this.guideItem.systemCommonInited;
			panelName = "systemCommonInited";
			this.count = 1;
		}
		if (!setPanelData && this.count < 2) {
			setPanelData = this.guideItem.zookeeperInited;
			panelName = "zookeeperInited";
			this.count = 2;
		}
		if (!setPanelData && this.count < 3) {
			setPanelData = this.guideItem.interfaceInited;
			panelName = "interfaceInited";
			this.count = 3;
		}
		if (!setPanelData && this.count < 4) {
			setPanelData = this.guideItem.planTypeInited;
			panelName = "planTypeInited";
			this.count = 4;
		}
		if (!setPanelData && this.count < 5) {
			setPanelData = this.guideItem.oldPeopleInited;
			panelName = "oldPeopleInited";
			this.count = 5;
		}
		if (!setPanelData && this.count < 6) {
			setPanelData = this.guideItem.hypertensionInited;
			panelName = "hypertensionInited";
			this.count = 6;
		}
		if (!setPanelData && this.count < 7) {
			setPanelData = this.guideItem.diabetesInited;
			panelName = "diabetesInited";
			this.count = 7;
		}
		if (!setPanelData && this.count < 8) {
			setPanelData = this.guideItem.childrenInited;
			panelName = "childrenInited";
			this.count = 8;
		}
		if (!setPanelData && this.count < 9) {
			setPanelData = this.guideItem.debilityChildrenInited;
			panelName = "debilityChildrenInited";
			this.count = 9;
		}
		if (!setPanelData && this.count < 10) {
			setPanelData = this.guideItem.pregnantInited;
			panelName = "pregnantInited";
			this.count = 10;
		}
		if (!setPanelData && this.count < 11) {
			setPanelData = this.guideItem.psychosisInited;
			panelName = "psychosisInited";
			this.count = 11;
		}
		return panelName;
	},
	showNewWin : function(panelName) {
		var refId = this.getRefId(panelName);
		var cfg = this.loadModuleCfg(refId);
		cfg.exContext = {};
		$import(cfg.script);
		var p = eval("new " + cfg.script + "(cfg)");
		this.gTitle = cfg.title;
		this.panel = p.initPanel();
		if (p.loadData && p.autoLoadData == false) {
			p.loadData();
		}
		this.getWin().hide();
		this.win = null;
		var win = this.getWin();
		win.show();
	},
	saveToApp : function(panelName, type) {
		var initFlag = "false";
		if (this.cSize == this.size) {
			initFlag = "true";
		}
		util.rmi.jsonRequest({
					serviceId : "chis.systemGuideService",
					serviceAction : "saveGuideToApp",
					method:"execute",
					panelId : panelName,
					initFlag : initFlag,
					type : type
				}, function(code, msg, json) {
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToApp);
						return;
					}
				});
	},
	doClose : function() {
		var win = this.win;
		if (win) {
			win.close();
		}
	},
	doNew : function() {
	},
	getRefId : function(name) {
		var data = {
			systemCommonInited : "chis.application.conf.CONF/CONF/SC01_1",
//			unitTypeInited : "chis.application.conf.CONF/CONF/SC01_12",
			interfaceInited : "chis.application.conf.CONF/CONF/SC01_2",
			zookeeperInited : "chis.application.conf.CONF/CONF/SC01_4",
			planTypeInited : "chis.application.conf.CONF/CONF/SC01_3",
			oldPeopleInited : "chis.application.conf.CONF/CONF/SC01_5",
			hypertensionInited : "chis.application.conf.CONF/CONF/SC01_6",
			diabetesInited : "chis.application.conf.CONF/CONF/SC01_7",
			childrenInited : "chis.application.conf.CONF/CONF/SC01_8",
			debilityChildrenInited : "chis.application.conf.CONF/CONF/SC01_09",
			pregnantInited : "chis.application.conf.CONF/CONF/SC01_10",
			psychosisInited : "chis.application.conf.CONF/CONF/SC01_11"
		};
		return data[name];
	},
	onWinShow : function() {
		if (!this.gTitle) {
			this.win.setTitle("系统设置向导-首页");
		} else {
			this.win.setTitle("系统设置向导-" + this.gTitle);
		}
	},
	createCheckBox : function() {
		var checkBox = new Ext.form.Checkbox({
					boxLabel : "以后不再显示该向导",
					name : ""
				});
		this.checkBox = checkBox;
		return checkBox;
	},
	onBeforeClose : function() {
		var v = this.checkBox.getValue();
		if (v == true) {
			this.saveToApp("", "all");
		}
	},
	getWin : function() {
		var win = this.win;
		if (!win) {
			var win = new Ext.Window({
						id : this.id,
						title : "系统设置向导",
						closable : true,
						width : 750,
						height : 500,
						border : true,
						closeAction : 'close',
						shim : true,
						layout : "fit",
						plain : true,
						autoScroll : true,
						constrain : true,
						shadow : false,
						buttonAlign : 'right',
						modal : true,
						resizable : false,
						items : this.initPanel(),
						buttons : this.createGuideButtons()
					});
			win.insert(3, this.createCheckBox())
			if (this.flag == false) {
				return;
			}
			win.on("show", function() {
						this.fireEvent("winShow");
					}, this)
			win.on("close", function() {
						this.fireEvent("beforeclose", this);
						this.fireEvent("close", this);
					}, this)
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this)
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.win = win;
		}
		win.instance = this;
		return win;
	}
})
