/**
 * 孕妇档案整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizTabModule")
chis.application.mhc.script.record.PregnantRecordModule = function(cfg) {
	this.activateId = 0
	chis.application.mhc.script.record.PregnantRecordModule.superclass.constructor.apply(this,
			[cfg])
	this.on("loadModule", this.loadModule, this)
}
Ext.extend(chis.application.mhc.script.record.PregnantRecordModule, chis.script.BizTabModule, {

	initPanel : function() {
		if (this.tab) {
			return this.tab;
		}
		var tabItems = []
		var actions = this.actions;
		var LoadForm=this.getLoadForm();
		for ( var i = 0; i < actions.length; i++) {
			var ac = actions[i];
			var refId=ac.id;
			if (((refId == "FirstVisit" || refId == "FirstVisitHtml") && refId !=LoadForm)//过滤随访重复页面33
					|| (LoadForm=="FirstVisitHtml" && refId=="PregnantIndex")){//如果是html页面则过滤检验页面
				 
			}else {
				tabItems.push({
					layout : "fit",
					title : ac.name,
					exCfg : ac,
					name : ac.id
				});
			}
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
	loadModule : function(moduleName, module) {
		module.on("recordSave", this.onSaveAll, this)
		if (moduleName == this.actions[0].id) {
			this.recordModule = module;
			module.on("controlPFVTab",this.onControlPFVTab,this);
		} else if (moduleName == this.actions[1].id) {
			this.indexModule = module;
			this.indexModule.on("activeIndexModule", this.onActiveIndexModule,
					this);
		} else {
			this.firstVisitModule = module;
			this.firstVisitModule.on("firstVisitData", this.initFirstVisitData,
					this);
			this.firstVisitModule.on("changeBMI", this.onChangeBMI, this);
			this.firstVisitModule.on("openHighRiskForm",
					this.onOpenHighRiskForm, this);
		}
	},
	onControlPFVTab : function(enable){
		if(enable){
			this.setTabItemDisabled(1, false);
		}else{
			this.setTabItemDisabled(1, true);
		}
	},
	onOpenHighRiskForm : function(op, parent) {
		var recordBody = this.recordModule.getSaveData();
		var visitBody = this.firstVisitModule.getFormData();
		var indexBody;
		if (this.indexModule) {// @@ indexModule不一定打开过需要判断下。
			indexBody = this.indexModule.getSaveData();
		}
		this.tab.el.mask("正在执行初始化,请稍候...", "x-mask-loading")
		util.rmi.jsonRequest({
			serviceId : this.saveServiceId,
			serviceAction : "initHighRiskReason",
			method:"execute",
			op : op,
			body : {
				"empiId" : this.exContext.ids.empiId,
				"pregnantId" : this.exContext.ids["MHC_PregnantRecord.pregnantId"],
				"pregnantRecord" : recordBody,
				"visitRecord" : visitBody,
				"checkList" : indexBody
			}
		}, function(code, msg, json) {
			this.tab.el.unmask();
			if (code > 300) {
				this.processReturnMsg(code, msg, this.onOpenHighRiskForm);
				return;
			}
			parent.openHighRisknessForm(json.body);
		}, this);
	},

	loadData : function() {
		this.activeModule(0);
	},

	onActiveIndexModule : function() {
		this.tab.activate(1);
	},

	onSaveAll : function(JySave) {
		var body = {};
		for (var i = 0; i < this.tab.items.length; i++) {
			var subTab = this.tab.items.itemAt(i);
			if (!subTab.__actived
					&& !this.exContext.ids["MHC_PregnantRecord.pregnantId"]) {
				this.setTabItemDisabled(1, false);
				this.tab.activate(i);
				return;
			}
			if (subTab.__actived) {
				var tabName = subTab.name;
				var module = this.midiModules[tabName];
				if (!module) {
					return;
				}
				
				var saveDatas = module.getSaveData();
				saveDatas.empiId = this.exContext.ids.empiId;
			    var LoadForm=this.getLoadForm();
				if (!saveDatas) {
					this.tab.activate(i);
					return;
				}
				if (tabName == this.actions[0].id) {
					body["pregnantRecord"] = saveDatas;
				} else if (tabName == this.actions[1].id) {
					body["checkUpList"] = saveDatas;
				} else {
					body["firstVisit"] = saveDatas;
				}
			}
		}
		var op;
		if (this.exContext.ids["MHC_PregnantRecord.pregnantId"]) {
			op = "update"
		} else {
			op = "create"
		}
		this.tab.el.mask("正在保存数据...", "x-mask-loading")
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction,
					method:"execute",
					op : op,
					body : body
				}, function(code, msg, json) {
					this.tab.el.unmask();
					if (code > 300) {
						this.processReturnMsg(code, msg,
								this.onSuperFormRefresh, [null]);
						return
					}
					var refreshVisitModule = json.refreshVisitModule;
					if (refreshVisitModule) {
						this.fireEvent("refreshData", "G_02");
					}
					var resBody = json.body; 
					if (resBody) {
						if(LoadForm=="FirstVisitHtml"){//thml页面
							var htmlKey=resBody["firstVisit"];
							if (htmlKey) {
								var daPregnantId=htmlKey["pregnantId"]
								if(daPregnantId!="" && daPregnantId!=null){
								   	var result = util.rmi.miniJsonRequestSync({
										serviceId : "chis.pregnantRecordService",
										serviceAction : "saveHtmlData",
										method : "execute",
										body : {
											"pregnantId" : daPregnantId,
											"data" :JySave
										}
									});
								}
							}
						}
						var prBody = resBody.pregnantRecord;
						if (prBody) {
							this.recordModule.initFormData(prBody);
						}
						var fvBody = resBody.firstVisit;
						if (fvBody) {
							this.firstVisitModule.initFormData(fvBody);
						}
					}
					if (this.recordModule) {
						this.recordModule.op = "update";
					}
					if (this.firstVisitModule) {
						this.firstVisitModule.op = "update";
						this.firstVisitModule.form.riskStore = null;
					}
					if (this.indexModule) {
						this.indexModule.op = "update";
					}
					this.setTabItemDisabled(1, false);
					this.fireEvent("save", this.entryName, op, json, body)
					this.refreshEhrTopIcon();			
				}, this);
	},

	onChangeBMI : function(value, field) {
		var height = this.recordModule.form.getForm().findField("height");
		var heightValue = height.getValue();
		var hight = heightValue / 100;
		if (heightValue) {
			var bmi = value / (hight * hight);
			var roundBMI = Math.round(bmi * Math.pow(10, 2)) / Math.pow(10, 2);
			field.setValue(roundBMI);
		}
	},

	initFirstVisitData : function() {
		var recordForm = this.recordModule.form.getForm();
		var weight = recordForm.findField("weight").getValue();
		var sbp = recordForm.findField("sbp").getValue();
		var dbp = recordForm.findField("dbp").getValue();

		var firstVisitForm = this.firstVisitModule.form.getForm();
		if(firstVisitForm){
			var weightFld = firstVisitForm.findField("weight");
			if(weightFld){
				weightFld.setValue(weight);
			}
			var sbpFld = firstVisitForm.findField("sbp");
			if(sbpFld){
				sbpFld.setValue(sbp);
			}
			var dbpFld = firstVisitForm.findField("dbp");
			if(dbpFld){
				dbpFld.setValue(dbp);
			}
		}
	},
	getLoadForm : function() {
		this.LoadForm = "FirstVisit";
		var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.systemCommonManageService",
					serviceAction : "getSystemConfigValue",
					method : "execute",
					body : "pregnantFirstType"
				});
		if (result.code > 300) {
			alert("页面参数获取失败，默认为纸质页面！")
			return
		}
		if (result.json.body) {
			var value = result.json.body;
			if (value == "paper") {
				this.LoadForm = "FirstVisitHtml";
			} else {
				this.LoadForm = "FirstVisit";
			}
		}
		return this.LoadForm
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
				var loadKey=this.getLoadForm();
				 var initKey=newTab.__actived;
				if(newTab && newTab.__actived && newTab.name=="FirstVisitHtml"){//如果是纸页面，每次都执行一个方法
					 var data_DA=this.recordModule.getFormDataBackHtml();
					     this.firstVisitModule.loadData(data_DA,initKey);
				}
				if (!newTab ) {
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
					
					if(newTab.name=="FirstVisitHtml"){//如果是纸页面，每次都执行一个方法
					    var data_DA=this.recordModule.getFormDataBackHtml();
					    m.loadData(data_DA,initKey);
				     }else{
				        m.loadData()
				     }
					newTab.__actived = true;
				}
			}
});