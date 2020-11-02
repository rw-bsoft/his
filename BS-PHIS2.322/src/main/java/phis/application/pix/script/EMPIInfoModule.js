/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.pix.script");

$import("util.Accredit", "app.modules.form.SimpleFormView",
		"app.modules.form.TableFormView", "app.modules.list.SimpleListView",
		"phis.application.pix.script.SubTableList",
		"phis.application.pix.script.SubTableEditList",
		"phis.application.pix.script.EMPIDemographicInfoForm",
		"app.modules.common", "util.widgets.LookUpField", "util.Vtype",
		"phis.script.CardReader", "phis.script.SimpleModule",
		"phis.script.util.FileIoUtil","phis.script.IdCardinterface", "phis.script.Phisinterface");

phis.application.pix.script.EMPIInfoModule = function(cfg) {
	/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
	//this.entryName = "phis.application.pix.schemas.MPI_DemographicInfo";
	this.entryName = "phis.application.pix.schemas.MPI_DemographicInfo_SMQ";
	/*********************end*******************/
	this._schemas = {};
	this._schemas[this.entryName] = {
		title : "基本信息",
		view : "form",
		refresh : true
	};
	this._schemas["phis.application.pix.schemas.MPI_Address"] = {
		title : "其他联系地址",
		view : "list",
		name : "addresses",
		refresh : true
	};
	this._schemas["phis.application.pix.schemas.MPI_Phone"] = {
		title : "其他联系电话",
		view : "list",
		name : "phones",
		refresh : true
	};
	this._schemas["phis.application.pix.schemas.MPI_Certificate"] = {
		title : "其他证件",
		view : "list",
		name : "certificates",
		refresh : true
	};
	this._schemas["phis.application.pix.schemas.MPI_Card"] = {
		title : "卡管理",
		view : "list",
		name : "cards",
		refresh : true
	};
	this.addEvents({
				"winShow" : true
			});
	this.subLists = [];
	this.registServiceId = "empiService";
	this.width = 820;
	this.modified = false;
	this.addressChange = false;
	this.certificateChange = false;
	this.telephoneChange = false;
	this.extensionChange = false;
	this.cardChange = false;
	this.on("winShow", this.onWinShow, this);
	this.on("close", this.onWinHide, this);
	if (!cfg.serviceAction) {
		this.serviceAction = "submitPerson";
	} else {
		this.serviceAction = cfg.serviceAction || "updatePerson";
	}
	if (this.serviceAction == "submitPerson") {
		this.op = "create";
	} else {
		this.op = "update";
	}
	this.modal = cfg.modal;
	Ext.apply(this,app.modules.common);
	Ext.apply(this,phis.script.IdCardinterface);
	Ext.apply(this, phis.script.Phisinterface);
	phis.application.pix.script.EMPIInfoModule.superclass.constructor.apply(this, [cfg]);
	this.on("shortcutKey", this.shortcutKeyFunc, this);
}

Ext.extend(phis.application.pix.script.EMPIInfoModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.tab) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.tab;
				}
				this.systemParams = this.loadSystemParams({
							commons : ['KLX'],
							privates : ['MPI_ADDRESS', 'MPI_WORKPLACE',
									'MPI_WORKCODE', 'MPI_TELE', 'YMJDYSGH']
						});
				var schema = sc;
				if (!schema) {
					var re = util.schema.loadSync(this.entryName);
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel);
						return;
					}
				}
				this.schema = schema;
				var defaultWidth = this.fldDefaultWidth || 200;
				var panels = [];
				for (var entry in this._schemas) {
					var sc = this._schemas[entry];
					panels.push({
								border : false,
								frame : false,
								layout : "fit",
								autoWidth : true,
								entry : entry,
								title : sc.title
							});
				}
				var tab = new Ext.TabPanel({
							buttonAlign : "center",
							deferredRender : false,
							border : false,
							frame : false,
							activeTab : 0,
							autoHeight : true,
							items : panels,
							defaults : {
								frame : false,
								autoHeight : true,
								autoWidth : true
							},
							buttons : [{
										cmd : "save",
										xtype : "button",
										text : "确定(F1)",
										handler : this.onBeforeSave,
										scope : this
									},{
										xtype : "button",
										text : "读身份证(F2)",
										handler :this.getidCardMsg,
										scope : this
									},{
										xtype : "button",
										text : "医保签约读卡(F3)",
										handler :this.getYbIdCardMsg,
										scope : this
									},{
										xtype : "button",
										text : "取消(F4)",
										handler : function() {
											this.getWin().hide()
										},
										scope : this
									}]
						});
				tab.on("tabchange", this.onTabActive, this);
				this.tab = tab;
				return tab;
			},
			F1 : function() {
				this.onBeforeSave();
			},
			F2:function(){
				this.getidCardMsg();	
			},
			F3:function(){
				this.getYbIdCardMsg();
			},
			F4 : function() {
				this.getWin().hide()
			},
			readCard : function() {
				if (!Ext.get('Control')) {
					var html = '<object id="Control" TYPE="application/x-itst-activex" WIDTH="0" HEIGHT="0" clsid="{CBA5D514-3544-4E87-80D2-F1582FA87841}"></object>';
					var el = Ext.DomHelper.insertHtml('beforeEnd',
							document.body, html)
				}
				if (!this.cl) {
					try {
						this.cl = Ext.getDom('Control')
					} catch (e) {
						Ext.MessageBox.alert("读卡器控件初始化失败:" + e)
					}
				}
				if (!this.cl) {
					Ext.MessageBox.alert('提示', '控件未初始化!');
					return
				}
				var reStr = "";
				try {
					reStr = this.cl.ReadCard();
				} catch (e) {
					Ext.MessageBox.alert('提示', '读卡发生异常!');
					return
				}
				if (reStr < 0) {
					return;
				}
				// var reStr =
				// "330100D156000005001BD54403DF9AC0H10175617330185330124198609160012杨力
				// 11986-09-16330100"
				var idCard = reStr.substring(47, 65)
				var personName = reStr.substring(65, 93).trim();
				var cardNo = reStr.substring(32, 41);
				var demoInfoView = this.midiModules[this.entryName];
				var form = demoInfoView.form.getForm();
				form.findField("idCard").setValue(idCard);
				demoInfoView.onIdCardBlur(form.findField("idCard"));
				demoInfoView.onIdCardFilled(form.findField("idCard"));
				form.findField("cardNo").setValue(cardNo);
				form.findField("personName").setValue(personName);
			},
			onTabActive : function(tab, panel) {
				var id = panel.entry;
				sc = this._schemas[id];
				if (!this.midiModules[id]) {
					var entryName = id;
					var subTabView;
					if (sc.view == "form") {
						subTabView = new phis.application.pix.script.EMPIDemographicInfoForm(
								{
									autoLoadSchema : false,
									autoLoadData : false,
									isCombined : true,
									colCount : 3,
									width : 600,
									labelAlign : "left",
									entryName : entryName,
									actions : [],
									mainApp : this.mainApp
								});
						subTabView.on("gotEmpi", function(empiId) {
									this.serviceAction = "updatePerson";
									this.setRecord(empiId);
								}, this);
					} else if (sc.view == "list") {
						subTabView = new phis.application.pix.script.SubTableList(
								{
									autoLoadSchema : false,
									entryName : entryName,
									isCombined : true,
									formColCount : 1,
									formLabelAlign : "left",
									autoLoadData : false,
									actions : [{
												name : "新建",
												id : "create"
											}, {
												name : "修改",
												id : "update"
											}, {
												name : "删除",
												id : "remove"
											}],
									enableCnd : false,
									title : sc.title,
									showButtonOnTop : true
								});
						subTabView.on("contentChanged", function(subEntry) {
							this.modified = true;
							if (subEntry == "phis.application.pix.schemas.MPI_Address") {
								this.addressChange = true;
							}
							if (subEntry == "phis.application.pix.schemas.MPI_Certificate") {
								this.certificateChange = true;
							}
							if (subEntry == "phis.application.pix.schemas.MPI_Phone") {
								this.telephoneChange = true;
							}
							if (subEntry == "phis.application.pix.schemas.MPI_Card") {
								this.cardChange = true;
							}
						}, this);
					} else if (sc.view == "editList") {
						subTabView = new phis.application.pix.script.SubTableEditList(
								{
									autoLoadSchema : false,
									entryName : entryName,
									isCombined : true,
									formColCount : 1,
									formLabelAlign : "left",
									autoLoadData : false,
									enableCnd : false,
									title : sc.title,
									showButtonOnTop : true
								});
						subTabView.on("contentChanged", function() {
									this.extensionChange = true;
								}, this);
					}
					subTabView.opener = this;
					panel.add(subTabView.initPanel());
					this.midiModules[id] = subTabView;
					tab.doLayout();
				}// end if

				if (this.empiId && sc.refresh == true) {
					if (sc.view == "list" || sc.view == "editList") {
						var initCnd = ['eq', ['$', 'empiId'],
								['s', this.empiId]];
						this.midiModules[id].empiId = this.empiId;
						this.midiModules[id].requestData.cnd = initCnd;
						this.midiModules[id].refresh();
					} else if (sc.view = "form") {
						// 为FORM加载数据
						var form = this.midiModules[id];
						form.initDataId = this.empiId;
						form.loadData();
						form.on("loadData", this.formLoadData, form)
					}
					sc.refresh = false;
				}
			},
			formLoadData : function() {
				var f = this.form.getForm().findField("birthday");
				var body = this.getAgeFromServer(f.getValue());
				// 设置婚姻状况状态
				this.onBirthdayChange(body);
			},
			getWin : function() {
				var win = this.win;
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "form",
								plain : true,
								autoScroll : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal,
								constrain : true,
								buttonAlign : 'center',
								items : this.initPanel()
							});

					win.on("show", function(win) {
								win.center();
								this.fireEvent("winShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
				}
				win.instance = this;
				this.win = win;
				return win;
			},

			onBeforeSave : function() {
				if (this.save)
					return;
				this.save = true;
				// 判断是否需要提示加卡纳入管理
				var demoInfoView = this.midiModules[this.entryName];
				var demoSnap = demoInfoView.snap;
				var form = demoInfoView.form.getForm();
				var cardNo = form.findField("cardNo").getValue();
				var MZHM = form.findField("MZHM").getValue();
				var brxz = form.findField("BRXZ").getValue();
				var sfzh = form.findField("idCard").getValue();
				/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
				var virtualCardNum ="";
				if(form.findField("smq")){
					virtualCardNum = form.findField("smq").getValue();//电子健康卡号
				}
				/*********************end*******************/
				if(brxz == 3000) {
					if (!this.data) {
						this.data = {};
					}
					var cardText={};
					cardText['01']="健康卡";
					cardText['02']="市民卡";
					cardText['03']="社保卡";
					cardText['04']="就诊卡";
					cardText['98']="医保卡";
					this.cardNull = false;
					if(cardNo.trim().length != 0){
						this.data.cards = [{
									"status" : "0",
									"cardTypeCode_text" : cardText['98'],
									"cardTypeCode" : '98',
									"cardNo" : cardNo.trim()
								},{
									"status" : "0",
									"cardTypeCode_text" : cardText[this.systemParams.KLX],
									"cardTypeCode" : this.systemParams.KLX,
									"cardNo" : MZHM.trim()
								}];
						/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
						if (virtualCardNum.trim().length != 0) {
							this.data.cards.push({
								"status" : "0",
								"cardTypeCode_text" : cardText['01'],
								"cardTypeCode" : '01',
								"cardNo" : virtualCardNum.trim()
							});
						}
						/*********************end*******************/
					}else{//卡号为空
						this.cardNull = true;
						try{
							var xmlDoc = loadXmlDoc("c:\\njyb\\mzghxx.xml");
							var elements = xmlDoc.getElementsByTagName("RECORD");
							var datas = {};
							for (var i = 0; i < elements.length; i++) {
								datas.TBR = (elements[i].getElementsByTagName("TBR")[0].firstChild)?(elements[i].getElementsByTagName("TBR")[0].firstChild.nodeValue):"";
								form.findField("cardNo").setValue(datas.TBR);
							}
							if(typeof(datas.TBR)=="undefined"){//住院
								var xmlDoc = loadXmlDoc("c:\\njyb\\zydjxx.xml");
								var elements = xmlDoc.getElementsByTagName("RECORD");
								for (var i = 0; i < elements.length; i++) {
									datas.TBR = (elements[i].getElementsByTagName("TBR")[0].firstChild)?(elements[i].getElementsByTagName("TBR")[0].firstChild.nodeValue):"";
									form.findField("cardNo").setValue(datas.TBR);
								}
							}
							this.data.cards = [{
										"status" : "0",
										"cardTypeCode_text" : cardText['98'],
										"cardTypeCode" : '98',
										"cardNo" : datas.TBR
									},{
										"status" : "0",
										"cardTypeCode_text" : cardText[this.systemParams.KLX],
										"cardTypeCode" : this.systemParams.KLX,
										"cardNo" : MZHM.trim()
									}];
							/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
							if (virtualCardNum.trim().length != 0) {
								this.data.cards.push({
									"status" : "0",
									"cardTypeCode_text" : cardText['01'],
									"cardTypeCode" : '01',
									"cardNo" : virtualCardNum.trim()
								});
							}
							/*********************end*******************/
						}catch(e){
							MyMessageTip.msg("提示", "文件解析失败!", true);
						}
					}
					var result = phis.script.rmi.miniJsonRequestSync({
						serviceId : "yBService",
						serviceAction : "checkHasCardInfo",
						body : this.data,
						flag : this.cardNull
					});
					if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg);
						return;
					} else {
						if (!result.json.hasCardNo && !this.cardNull) {
							this.serviceAction = "submitPerson";
						}else{
							if(result.json.mzxh){
								form.findField("MZHM").setValue(result.json.mzxh);
								this.data.cards = [{
										"status" : "0",
										"cardTypeCode_text" : cardText['98'],
										"cardTypeCode" : '98',
										"cardNo" : datas.TBR
									},{
										"status" : "0",
										"cardTypeCode_text" : cardText[this.systemParams.KLX],
										"cardTypeCode" : this.systemParams.KLX,
										"cardNo" : result.json.mzxh
									}];
								/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
								if (virtualCardNum.trim().length != 0) {
									this.data.cards.push({
										"status" : "0",
										"cardTypeCode_text" : cardText['01'],
										"cardTypeCode" : '01',
										"cardNo" : virtualCardNum.trim()
									});
								}
								/*********************end*******************/
							}
							this.serviceAction = "updatePerson";
						}
					}
				}
				if(brxz == 1000 && cardNo==sfzh){
					if (!this.data) {
						this.data = {};
					}
					var cardText={};
					cardText['01']="健康卡";
					cardText['02']="市民卡";
					cardText['03']="社保卡";
					cardText['04']="就诊卡";
					cardText['98']="医保卡";
					this.cardNull = false;
					if(cardNo.trim().length != 0){
						this.data.cards = [{
									"status" : "0",
									"cardTypeCode_text" : cardText['01'],
									"cardTypeCode" : '01',
									/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
									//"cardNo" : cardNo.trim()
									"cardNo" : (virtualCardNum.trim().length != 0)?virtualCardNum.trim():cardNo.trim()									
									/*********************end*******************/
								},{
									"status" : "0",
									"cardTypeCode_text" : cardText[this.systemParams.KLX],
									"cardTypeCode" : this.systemParams.KLX,
									"cardNo" : MZHM.trim()
								}];
					}else{//卡号为空
						this.cardNull = true;
						form.findField("cardNo").setValue(sfzh);
						this.data.cards = [{
									"status" : "0",
									"cardTypeCode_text" : cardText['01'],
									"cardTypeCode" : '01',
									/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
									//"cardNo" : sfzh
									"cardNo" : (virtualCardNum.trim().length != 0)?virtualCardNum.trim():sfzh									
									/*********************end*******************/
								},{
									"status" : "0",
									"cardTypeCode_text" : cardText[this.systemParams.KLX],
									"cardTypeCode" : this.systemParams.KLX,
									"cardNo" : MZHM.trim()
								}];
					}
					var result = phis.script.rmi.miniJsonRequestSync({
						serviceId : "healthCardService",
						serviceAction : "checkHasCardInfo",
						body : this.data,
						flag : this.cardNull
					});
					if (result.code > 300) {
						this.processReturnMsg(result.code, result.msg);
						return;
					} else {
						if (!result.json.hasCardNo && !this.cardNull) {
							this.serviceAction = "submitPerson";
						}else{
							if(result.json.mzxh){
								form.findField("MZHM").setValue(result.json.mzxh);
								this.data.cards = [{
										"status" : "0",
										"cardTypeCode_text" : cardText['01'],
										"cardTypeCode" : '01',
										/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
										//"cardNo" : sfzh
										"cardNo" : (virtualCardNum.trim().length != 0)?virtualCardNum.trim():sfzh									
										/*********************end*******************/
									},{
										"status" : "0",
										"cardTypeCode_text" : cardText[this.systemParams.KLX],
										"cardTypeCode" : this.systemParams.KLX,
										"cardNo" : result.json.mzxh
									}];
							}
							this.serviceAction = "updatePerson";
						}
					}
				}
				if (demoInfoView.serviceAction != "updatePerson"
						&& (cardNo.trim().length != 0)) {
					if (!this.data) {
						this.data = {};
					}
					if (this.serviceAction == "submitPerson" && brxz != 3000 && !(brxz == 1000 && cardNo==sfzh)) {
						var cardText={};
						cardText['01']="健康卡";
						cardText['02']="市民卡";
						cardText['03']="社保卡";
						cardText['04']="就诊卡";
						this.data.cards = [{
									"status" : "0",
									"cardTypeCode_text" : cardText[this.systemParams.KLX],
									"cardTypeCode" : this.systemParams.KLX,
									"cardNo" : cardNo.trim()
								}];
						/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-15*************/
						if (virtualCardNum.trim().length != 0) {
							this.data.cards.push({
								"status" : "0",
								"cardTypeCode_text" : cardText['01'],
								"cardTypeCode" : '01',
								"cardNo" : virtualCardNum.trim()
							});
						}									
						/*********************end*******************/
					}
					// // 提示是否纳入卡管理。
					// // Ext.Msg.show({
					// // title : '新卡提示',
					// // msg : '是否将新卡纳入管理?',
					// // buttons : Ext.Msg.YESNO,
					// // fn : function(btn, text) {
					// // if (btn == "no") {
					// // this.doSave();
					// // return;
					// // }
					// var cardForm = this.midiModules["cardForm"];
					// if (!cardForm) {
					// cardForm = new phis.application.pix.script.SubTableForm(
					// {
					// entryName : "MPI_Card",
					// title : "新增卡",
					// width : 300,
					// op : "create",
					// colCount : 1,// 在LIST窗口控制新建、修改窗口打开时的列数
					// labelAlign : "left" // 在LIST窗口控制新建、修改窗口打开时的文字对齐方式
					// });
					// cardForm.initPanel();
					// cardForm.on("save", function() {
					// this.cardChange = true;
					// this.doSave();
					// }, this)
					// cardForm.on("cancel", function() {
					// this.doSave();
					// }, this)
					// this.midiModules["cardForm"] = cardForm;
					// }
					// cardForm.empiId = this.empiId;
					// var cardListTab = this.tab.items.itemAt(4);
					// this.tab.activate(cardListTab);
					// var CardList = this.midiModules["MPI_Card"];
					// cardForm.dataChanged = false;
					// cardForm.store = CardList.store;
					// cardForm.getWin().show();
					// cardForm.initFormData({
					// cardNo : cardNo
					// })
					// // },
					// // scope : this,
					// // animEl : 'elId',
					// // icon : Ext.MessageBox.QUESTION
					// // });
					this.doSave();
				} else {
					this.doSave();
				}
			},

			doSave : function() {
				var demoInfoView = this.midiModules[this.entryName];
				if (!demoInfoView.hasQuery) {
					if (demoInfoView.validate()) {
						demoInfoView.hasQuery = true;
					}
					this.save = true;
					return;
				}
				if (!demoInfoView.validate()) {
					this.save = false;
					return;
				}
				// 查询未完成。
				if (demoInfoView.serviceAction == "submitPerson"
						&& demoInfoView.needsQuery()) {
					this.save = false;
					return;
				}
				var demoInfoView = this.midiModules[this.entryName];
				// 如果执行保存，判断内容是否改变。
				// if (this.serviceAction == "updatePerson") {
				// if (!this.modified && !demoInfoView.needsUpdate()) {
				// this.getWin().hide();
				// this.fireEvent("onEmpiReturn",
				// this.midiModules[this.entryName].data);
				// return;
				// }
				// this.modified = false;
				// }
				if (!this.data) {
					this.data = {};
				}
				var values = {};
				var form = demoInfoView.form.getForm();
				// 确定按钮增加判断 门诊模式 卡号 门诊号码一致
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "checkCardOrMZHM"
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, pdms.msg,
							this.onBeforeSave);
					return;
				} else {
					if (!pdms.json.cardOrMZHM) {
						Ext.Msg.alert("提示", "该卡号门诊号码判断不存在!");
						f.focus(false, 100);
						return;
					}
				}
				if (this.serviceAction != "updatePerson") {
					var cardNo = form.findField("cardNo").getValue();
					var brxz = form.findField("BRXZ").getValue();
					var sfzh = form.findField("idCard").getValue();
					if (pdms.json.cardOrMZHM == 2) {
						var MZHM = form.findField("MZHM").getValue();
						if (brxz != 3000 && !(brxz == 1000 && cardNo==sfzh) && cardNo != MZHM
								&& form.findField("cardNo").disabled == false) {
							this.save = false;
							MyMessageTip.msg("提示", "卡号门诊号码不一致", true);
							return;
						}
					}else if(pdms.json.cardOrMZHM == 1){
						if(!cardNo) {
							MyMessageTip.msg("提示", "卡号不能为空", true);
							this.save = false;
							return;
						}
					}
				}
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];
					var field = form.findField(item.id);
					if (field) {
						var value = field.getValue();
						if (value == null || value == "") {
							if (!item.pkey && item["not-null"]) {
								this.save = false;
								Ext.Msg.alert("提示", item.alias + "不能为空");
								return;
							}
						}
						if (field.getXType() == "datefield" && value != null
								&& value != "") {
							value = value.format('Y-m-d');
						}
						if (value && typeof(value) == "string") {
							value = value.trim();
						}
						values[item.id] = value;
					}
				}

				if (brxz != 3000){
					values["cardNo"] = "";
				}
				// // 更新个人姓名服务
				// if (this.serviceAction == "updatePerson"
				// && demoInfoView.needsUpdateName()) {
				// this.tab.el.mask("正在更新姓名..");
				// var result = phis.script.rmi.miniJsonRequestSync({
				// serviceId : this.registServiceId,
				// serviceAction : 'updatePersonName',
				// body : {
				// personName : values["personName"],
				// empiId : demoInfoView.data["empiId"]
				// }
				// })
				// this.tab.el.unmask();
				// if (result.code > 300) {
				// this.processReturnMsg(result.code, result.msg);
				// return;
				// }
				// }
				Ext.apply(this.data, values);
				var listSc = this.getListTypeSchema();
				for (var sc in listSc) {
					var listView = this.midiModules[sc];
					if (listView) {
						count = listView.store.getCount();
						propName = listSc[sc].name;
						if (count > 0) {
							this.data[propName] = [];
						}
						for (var i = 0; i < count; i++) {
							this.data[propName]
									.push(listView.store.getAt(i).data);
						}
					}
				}
				if (brxz == 3000){
					var cardNo = form.findField("cardNo").getValue();
					this.data["YBKH"] = cardNo.trim();
				}
				if (this.serviceAction == "updatePerson") {
					this.data["addressChange"] = this.addressChange;
					this.data["certificateChange"] = this.certificateChange;
					this.data["telephoneChange"] = this.telephoneChange;
					this.data["extensionChange"] = this.extensionChange;
					this.data["cardChange"] = this.cardChange;
					this.data["lastModifyUser"] = this.mainApp.uid;
					this.data["lastModifyUnit"] = this.mainApp['phisApp'].deptId;
				} else {
					this.data["createUser"] = this.mainApp.uid;
					this.data["createUnit"] = this.mainApp['phisApp'].deptId;
				}
				/***************begin 浦口增加二维码扫码功能 zhaojian 2017-12-16*************/
				if( this.entryName=="phis.application.pix.schemas.MPI_DemographicInfo_SMQ" && !this.data.smq){
					this.data["smq"] = "";
				}
				/*********************end*******************/
				this.tab.el.mask("正在执行操作...");				
				phis.script.rmi.jsonRequest({
							serviceId : this.registServiceId,
							serviceAction : this.serviceAction,
							schema : this.entryName,
							body : this.data
						}, function(code, msg, json) {
							this.tab.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								this.save = false;
								return;
							}
							if (json["body"].code >= 300) {
								this.processReturnMsg(json["body"].code,
										json["body"].msg);
							}
							this.fireEvent("onSuccessed");
							if (this.serviceAction == "submitPerson") {
								var body = json["body"];
								this.empiId = body["empiId"];
								this.data["empiId"] = this.empiId;
							} else {
								if (json) {
									if (json["body"]) {
										var body = json["body"];
										if (body["empiId"]) {
											this.empiId = body["empiId"];
											this.data["empiId"] = this.empiId;
											values.empiId = this.empiId;
										}
									}
								}
							}
							demoInfoView.data = values;
							// midify by yangl 增加是否新增标志
							this.fireEvent("onEmpiReturn", this.data,
									this.serviceAction == "submitPerson");
							this.fireEvent("saveCBXX", this.data.empiId);
							this.getWin().hide();
							this.save = false;
						}, this)
			},

			setRecord : function(empiId, topClick) {
				if (topClick) {
					this.midiModules[this.entryName].topClick = topClick;
				}
				if (empiId == this.empiId) {
					return;
				}
				this.clear();
				this.empiId = empiId;
				if (!this.data) {
					this.data = {};
				}
				this.data["empiId"] = this.empiId;
				this.midiModules[this.entryName].hasQuery = true;
				this.serviceAction = "updatePerson";
				this.tab.activate(this.tab.items.itemAt(0));
				// EMPIID改变 ，重新触发tabchange事件。
				this.tab.fireEvent("tabchange", this.tab, this.tab.items
								.itemAt(0));
			},

			getListTypeSchema : function() {
				var schemas = [];
				for (var sc in this._schemas) {
					if (this._schemas[sc].view == "list"
							|| this._schemas[sc].view == "editList") {
						schemas[sc] = this._schemas[sc];
					}
				}
				return schemas;
			},

			/**
			 * 保存时判断内容是否已经改变。
			 */
			ifContentChanged : function() {
				var demoInfoForm = this.midiModules[this.entryName];
				var form = demoInfoForm.getForm();
				for (var i = 0; i < this.schema.items.length; i++) {
					var item = this.schema.items[i];
					var field = form.findField(item.id);
					if (field) {
						var value = field.getValue();
						var dataValue = data[item.id];
						if (item.dic) {

						}
					}
				}
			},

			/**
			 * 新建完成或者修改记录改变时，清理对上次操作时保存的临时数据。
			 */
			clear : function(clearSnap) {
				if (this.data) {
					this.data = {};
				}
				if (this.midiModules[this.entryName]) {
					var demoInfoForm = this.midiModules[this.entryName];
					demoInfoForm.data = {};
					demoInfoForm.queryInfo = {};
					demoInfoForm.snap = {};
					demoInfoForm.queryBy = false;
					demoInfoForm.modified = false;
					if (this.serviceAction == "submitPerson") {
						demoInfoForm.addSearchEventListeners();
					} else if (this.serviceAction == "updatePerson") {
						demoInfoForm.removeSearchEventListeners();
					}
					demoInfoForm.serviceAction = this.serviceAction;
					demoInfoForm.doNew();
					var form = demoInfoForm.form.getForm();
					var focusField;
					if (this.serviceAction == "updatePerson") {
						focusField = form.findField("personName");
					} else {
						focusField = form.findField("cardNo");
					}
					form.findField("workCode").enable();
					form.findField("workPlace").enable();
					focusField.focus(true, true);
				}
				var listSc = this.getListTypeSchema();
				for (var sc in listSc) {
					var listView = this.midiModules[sc];
					if (listView) {
						listView.modified = false;
						listView.empiId = null;
						listView.store.removeAll();
					}
				}
				for (var sc in this._schemas) {
					this._schemas[sc].refresh = true;
				}

				this.empiId = null;
				this.addressChange = false;
				this.certificateChange = false;
				this.telephoneChange = false;
				this.cardChange = false;
				this.tab.activate(this.tab.items.itemAt(0));
			},

			onWinShow : function() {
				if (this.op == "create") {
					this.clear();
					// this.midiModules[this.entryName].form.getForm()
					// .findField("idCard").enable();
					var form = this.midiModules[this.entryName];
					form.hasQuery = false;
					this.save = false;
					if (this.EMPIID) {
						this.serviceAction = "updatePerson";
						this.setRecord(this.EMPIID);
					} else {
						if (!this.brybxx) {
							this.setAdrsAndWork(form);
						}
					}
				}
				if (this.brybxx && this.brybxx != null) {
					var demoInfoView = this.midiModules[this.entryName];
					if (this.brybxx.YBQFPB == 11) {
						demoInfoView.initFormData({
									personName : this.brybxx.XM,
									BRXZ : this.brybxx.BRXZ,
									sexCode : this.brybxx.XB,
									idCard : this.brybxx.SHBZH,
									workPlace : this.brybxx.DW,
									address : this.brybxx.DW,
									workCode : 1
								});
						var form = this.midiModules[this.entryName];
						var f = form.form.getForm().findField("idCard");
						// 设置婚姻状况状态
						form.onIdCardBlur(f);
					} else {
						// modify by yangl 出身年月取医保返回的
						var birthday;
						if (this.brybxx.CSNY) {
							birthday = this.brybxx.CSNY.substr(0, 4) + '-'
									+ this.brybxx.CSNY.substr(4, 2) + '-'
									+ this.brybxx.CSNY.substr(6, 2)
						}
						demoInfoView.initFormData({
									personName : this.brybxx.XM,
									BRXZ : this.brybxx.BRXZ,
									sexCode : this.brybxx.XB,
									idCard : this.brybxx.GRSFH,
									workPlace : this.brybxx.DWMC,
									birthday : birthday
								});
						var form = this.midiModules[this.entryName];
						var f = form.form.getForm().findField("birthday");
						var body = form.getAgeFromServer(f.getValue());
						// 设置婚姻状况状态
						form.onBirthdayChange(body);
					}
					// demoInfoView.onIdCardBlur(demoInfoView.form.getForm().findField("idCard"));
					if (this.brybxx.EMPIID) {
						this.serviceAction = "updatePerson";
						this.setRecord(this.brybxx.EMPIID);
					}
					// 获取系统参数，根据参数判断是否要设置默认 联系地址及工作单位

				}
			},
			/**
			 * 根据系统参数设置默认地址及工作单位
			 */
			setAdrsAndWork : function(form) {
				if (this.systemParams.MPI_WORKPLACE) {
					form.form.getForm().findField("workPlace")
							.setValue(this.systemParams.MPI_WORKPLACE);
				}
				if (this.systemParams.MPI_WORKCODE) {
					var d = new Ext.util.DelayedTask(function() {
								form.form
										.getForm()
										.findField("workCode")
										.setValue(this.systemParams.MPI_WORKCODE);
							}, this);
					d.delay(100);
				}
				if (this.systemParams.YMJDYSGH
						&& this.systemParams.YMJDYSGH
								.indexOf(this.mainApp.logonName) >= 0) {
					if (this.systemParams.MPI_ADDRESS) {
						form.form.getForm().findField("address")
								.setValue(this.systemParams.MPI_ADDRESS);
					}
					if (this.systemParams.MPI_TELE) {
						form.form.getForm().findField("phoneNumber")
								.setValue(this.systemParams.MPI_TELE);
					}
				}

			},
			onWinHide : function() {
				if (this.op == "create") {
					this.serviceAction = "submitPerson";
				}
				if (this.op == "update") {
					this.serviceAction = "updatePerson";
				}
				this.brybxx = null;
				this.EMPIID = null;
				// 关闭所有的子窗口
				var listSc = this.getListTypeSchema();
				for (var sc in listSc) {
					var listView = this.midiModules[sc];
					if (!listView) {
						continue;
					}
					if (!listView.midiModules
							|| listView.midiModules.length == 0) {
						continue;
					}
					for (var module in listView.midiModules) {
						listView.midiModules[module].getWin().hide();
					}
				}
			}
			,close:function() {
				// 判断窗口内容是否有改动。
				if (this.modified) {

				}
			}
			,alt:function(obj) {
				var res = "";
				for (var i in obj) {
					res += i + ":" + obj[i] + "\n";
				}
				alert(res);
			}
			,getidCardMsg:function(){//读取身份证信息
				this.addIdCardObject();
				var rs=this.getIdCardInfo();
				if(rs.length>20){
					var arr=rs.split(",");
					var idCard=arr[2];
					var personName=arr[0];
					var demoInfoView = this.midiModules[this.entryName];
					var form = demoInfoView.form.getForm();
					form.findField("idCard").setValue(idCard);
					demoInfoView.onIdCardBlur(form.findField("idCard"));
					demoInfoView.onIdCardFilled(form.findField("idCard"));
					form.findField("personName").setValue(personName);
				}
			},
			getYbIdCardMsg:function(){//读取医保卡信息
				// 先执行读卡程序
				var body={};
				body.USERID=this.mainApp.uid;
				//获取业务周期号
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.NjjbService",
					serviceAction : "getywzqh",
					body:body
				});
				if (ret.code <= 300) {
					var ywzqh=ret.json.YWZQH;
					this.addPKPHISOBJHtmlElement();
					this.drinterfaceinit();
					var str=this.buildstr("2100",ywzqh,"");
					var drre=this.drinterfacebusinesshandle(str);
					var arr=drre.split("^");
					this.SHBZKH="";
					if(arr[0]=="0"){
						var canshu=arr[2].split("|")
						var idCard=canshu[3];//身份证号
						var personName=canshu[4];//姓名
						var demoInfoView = this.midiModules[this.entryName];
						var form = demoInfoView.form.getForm();
						form.findField("idCard").setValue(idCard);
						demoInfoView.onIdCardBlur(form.findField("idCard"));
						demoInfoView.onIdCardFilled(form.findField("idCard"));
						form.findField("personName").setValue(personName);
						form.findField("BRXZ").setValue({
							key : "1000",
							text : "自费"
						});
					}else{
						MyMessageTip.msg("提示：","金保返回错误:"+drre+",HIS系统提示读卡器可能没连接或没插卡！", true);
						return;
					}

				} else {
					MyMessageTip.msg("提示：",ret.msg, true);
					return;
				}
		}
		});