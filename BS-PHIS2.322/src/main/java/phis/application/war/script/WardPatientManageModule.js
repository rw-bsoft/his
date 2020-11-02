/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule", "phis.application.war.script.HDRView", "phis.script.common");

phis.application.war.script.WardPatientManageModule = function(cfg) {
	this.exContext = {};
	this.listServiceId = "wardPatientManageService";
	this.modeName = "dataview";
	phis.application.war.script.WardPatientManageModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.war.script.WardPatientManageModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				// 判断是否有病区
				if (this.openBy == "nurse" && !this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				this.exContext.systemParams = this.loadSystemParams({
							commons : ['QYDZBL'],
							privates : ['ZYYSQY', 'QYSXZZ', 'QYJYBZ','HSZDSTX']
						});
				if (this.panel) {
					return this.panel;
				}
				// 切换功能
				var text = "切换";
				var filter = "";
				var dicId = "viewMode";
				if (this.openBy == "doctor") {// 医生站
					text = "切换";
					filter = "['eq',['$','item.properties.ORGANIZCODE'],['s','"
							+ this.mainApp.deptRef + "']]";
					dicId = "phis.dictionary.department_zy";
				}
				var label = new Ext.form.Label({
							text : text
						});
				var _ctx = this;
				var viewModeComb = null;
				if (this.openBy == "doctor") {// 医生站
					viewModeComb = util.dictionary.SimpleDicFactory.createDic({
								id : dicId,
								defaultIndex : "0",
								width : 80,
								filter : filter
							});

					viewModeComb.setEditable(false);
					viewModeComb.on("select", this.viewModeSelect, this);
					this.viewModeComb = viewModeComb;
					this.viewModeComb.store.on("load", this.viewModeLoad, this);
					this.viewModeComb.store.load();
					viewModeComb = [label, '-', viewModeComb];
				} else {
					viewModeComb = new Ext.Button({
								text : '床位列表',
								iconCls : 'bed',
								menu : [{
											text : '床位列表',
											iconCls : 'bed',
											handler : function() {
												viewModeComb.setText("床位列表");
												viewModeComb
														.setIconClass("bed");
												this.viewChange(1);
											},
											scope : this
										}, {
											text : '病人列表',
											iconCls : 'user',
											handler : function() {
												viewModeComb.setText("病人列表");
												viewModeComb
														.setIconClass("user");
												this.viewChange(2);
											},
											scope : this
										}, {
											text : '出院72小时内',
											iconCls : 'user',
											handler : function() {
												viewModeComb.setText("出院72小时内");
												viewModeComb
														.setIconClass("user");
												this.viewChange(3);
											},
											scope : this
										}],
								reorderable : true
							});
				}

				var detailPanel = new Ext.Panel({
							// id : 'patient-details',
							layout : "fit",
							border : false,
							split : true,
							collapsible : true,
							titleCollapse : true,
							region : 'east',
							width : 250,
							items : [this.getDetailsTab()]
						});
				var viewPanel = new Ext.Panel({
							id : 'patient-view',
							layout : "fit",
							border : false,
							split : true,
							region : 'center',
							// autoScroll : true,
							items : [this.getDataView()]
						});
				var filter;
				if (this.openBy == 'doctor') {
					filter = {
						iconCls : "query",
						text : '过滤选项',
						enableToggle : true,
						toggleHandler : this.onItemToggle,
						scope : this
					};
				}
				var panel = new Ext.Panel({
							frame : false,
							layout : 'border',
							title : this.name,
							tbar : new Ext.Toolbar({
										enableOverflow : true,
										items : [this.createMyButtons(),
												filter, '->', viewModeComb]
									}),

							items : [viewPanel, detailPanel]
						});
				this.panel = panel;
				this.detailPanel = detailPanel;
				this.viewPanel = viewPanel;
				this.panel.on("afterrender", this.onReady, this);
				return this.panel;
			},
			/**
			 * 定时提醒任务实现接口,只要是从菜单列表打开的最外层的js 1、ymPrompt.js 公用提醒框 2、hashcode
			 * 用来控制相同提示信息不再重复提示(message的hashcode值)
			 */
			taskRun : function(t) {
				//zhaojian 2017-09-25 定时提醒只在护士站显示
				if(t.mainApp.jobtitleId != "phis.52"){
					return;
				}
				// 病区新医嘱或者变动医嘱信息提醒
				phis.script.rmi.jsonRequest({
							serviceId : "wardPatientManageService",
							serviceAction : "loadWardRemindInfo",
							body : {
								// wardId : this.mainApp['phis'].wardId,
								// JGID : this.mainApp['phisApp'].deptId,
								//interval : interval
							}
						}, function(code, msg, json) {
							if (code > 200) {
								alert(msg);
								// this.processReturnMsg(code, msg)
								return;
							}
							var body = json.body;
							if (body) {
								ymPrompt.alert({
											_mId : this.fullId,// 模块Id
											hashcode : body.hashcode,
											message : body.message,
											title : body.title + "-"
													+ Date.getServerDateTime(),
											winPos : 'rb',
											showMask : false,
											useSlide : true
										})
							}
						}, this)
			},
			onItemToggle : function(item, pressed) {
				this.otherTbar.setVisible(pressed);
			},
			getRadioGroup : function() {
				if (this.openBy != 'doctor')
					return [];
				if (this.radioGroup)
					return this.radioGroup;
				var radioGroup = [{
							xtype : 'radiogroup',
							width : 150,
							id : 'zyRadiogroup',
							listeners : {
								'change' : function(radiogroup, radio) {
									this.doRefresh();
								},
								scope : this
							},
							items : [{
										name : "BRZT",
										inputValue : 1,
										boxLabel : '在院',
										clearCls : true,
										checked : true
									}, {
										boxLabel : '出院',
										name : 'BRZT',
										clearCls : true,
										inputValue : 2
									}, {
										boxLabel : '全部',
										name : 'BRZT',
										clearCls : true,
										inputValue : 9
									}]
						}];
				this.radioGroup = radioGroup;
				return radioGroup;
			},
			getCheckGroup : function() {
				if (this.openBy != 'doctor')
					return [];
				if (this.checkGroup)
					return this.checkGroup;
				var checkGroup = [{
							id : 'bwCheckBoxs',
							xtype : 'checkboxgroup',
							width : 110,
							style : 'padding-left:10px;',
							items : [{
										boxLabel : '<font color="red">病危</font>',
										name : 1,
										clearCls : true,
										listeners : {
											'check' : function(obj, ischecked) {
												this.doRefresh();
											},
											scope : this
										}
									}, {
										boxLabel : '病重',
										name : 2,
										clearCls : true,
										listeners : {
											'check' : function(obj, ischecked) {
												this.doRefresh();
											},
											scope : this
										}

									}]
						}, {
							id : 'hlCheckBoxs',
							xtype : 'checkboxgroup',
							width : 300,
							style : 'padding-left:10px;',
							items : [{
										boxLabel : '<font color="red">特级护理</font>',
										name : 0,
										clearCls : true,
										listeners : {
											'check' : function(obj, ischecked) {
												this.doRefresh();
											},
											scope : this
										}
									}, {
										boxLabel : '一级护理',
										name : 1,
										clearCls : true,
										listeners : {
											'check' : function(obj, ischecked) {
												this.doRefresh();
											},
											scope : this
										}
									}, {
										boxLabel : '二级护理',
										name : 2,
										clearCls : true,
										listeners : {
											'check' : function(obj, ischecked) {
												this.doRefresh();
											},
											scope : this
										}
									}, {
										boxLabel : '三级护理',
										name : 3,
										clearCls : true,
										listeners : {
											'check' : function(obj, ischecked) {
												this.doRefresh();
											},
											scope : this
										}
									}]
						}];
				this.checkGroup = checkGroup;
				return checkGroup;
			},
			viewModeLoad : function(store) {
				var r1 = new Ext.data.Record({
							key : "0",
							text : "全院病人"
						})
				var r2 = new Ext.data.Record({
							key : "-1",
							text : "我的病人"
						})
				var r3 = new Ext.data.Record({
							key : "2",
							text : "会诊病人"
						})
				var r4 = new Ext.data.Record({
							key : "-3",
							text : "出院72小时内"
						})
				store.insert(0, [r1, r2, r3, r4]);
				if (!this.firstLoad) {
					this.viewModeComb.setValue("-1")
					this.firstLoad = true;
					this.doRefresh();
					this.otherTbar.setVisible(false);
				}
			},
			onReady : function() {
				if (this.exContext.systemParams.QYSXZZ == "0") {
					var zyzz = this.panel.getTopToolbar().find('cmd', 'zyzz');
					if (zyzz[0]) {
						zyzz[0].hide();
					}
				}
				if (this.openBy == "nurse") {
					if (this.exContext.systemParams.ZYYSQY == "1") {
						this.panel.getTopToolbar().find('cmd', 'cyzgl')[0]
								.hide();
						this.panel.getTopToolbar().find('cmd', 'zkcl')[0]
								.hide();
					}
				} else {
					var tbar = this.panel.getTopToolbar();
					var cwfp = tbar.find('cmd', 'cwfp');
					if (cwfp[0]) {
						cwfp[0].hide();
					}
					var tysq = tbar.find('cmd', 'tysq');
					if (tysq[0]) {
						tysq[0].hide();
					}
					var zccl = tbar.find('cmd', 'zccl');
					if (zccl[0]) {
						zccl[0].hide();
					}
					var tccl = tbar.find('cmd', 'tccl');
					if (tccl[0]) {
						tccl[0].hide();
					}
					var tzcy = tbar.find('cmd', 'tzcy');
					if (tzcy[0]) {
						tzcy[0].hide();
					}
					var hljl = tbar.find('cmd', 'hljl');
					if (hljl[0]) {
						hljl[0].hide();
					}
					var twd = tbar.find('cmd', 'twd');
					if (twd[0]) {
						twd[0].hide();
					}
					// this.panel.getTopToolbar().find('cmd', 'cwfp')[0].hide();
					// this.panel.getTopToolbar().find('cmd', 'tysq')[0].hide();
					// this.panel.getTopToolbar().find('cmd', 'zccl')[0].hide();
					// this.panel.getTopToolbar().find('cmd', 'tccl')[0].hide();
					// this.panel.getTopToolbar().find('cmd', 'tzcy')[0].hide();
					// this.panel.getTopToolbar().find('cmd', 'hljl')[0].hide();
					var fields = [];
					fields.push({
									value : '1',
									text : '住院号'
								});
					fields.push({
									value : '2',
									text : '姓名'
								});
					fields.push({
									value : '3',
									text : '出院X周'
								});
					var comboxStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
					var combox = new Ext.form.ComboBox({
								store : comboxStore,
								valueField : "value",
								displayField : "text",
								value : null,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 80
							});
					this.queryCombo = combox;
					var queryTextField = new Ext.form.TextField({
							id : "queryTxt",
							width : 100,
							enableKeyEvents : true,
							selectOnFocus : true
						});
					this.queryTextField = queryTextField;
					this.queryTextField.on('keyup', this.onKeyUp, this);
					var queryBtn = new Ext.Button({
							text : "查询",
							width : 70,
							iconCls : "query"
						});
					this.queryBtn = queryBtn;
					queryBtn.on("click", this.doCndQuery, this);
					var otherTbar = new Ext.Toolbar([this.getCheckGroup(),
							this.getRadioGroup(),combox,queryTextField,queryBtn]);
					this.otherTbar = otherTbar;
					this.panel.add(otherTbar);
					this.panel.doLayout();
				}
				if (document.readyState == "complete") {
					if (this.openBy == "doctor"){
						var s = 10;//页面刷新频率：10秒一次
						this.startRefresh(s);
					}
				}
			},
			afterOpen : function() {
				if (this.openBy == "nurse" && this.mainApp['phis'].wardId) {
					this.viewChange(1);
					// var task = new Ext.util.TaskRunner();
					// task.start({
					// run : this.taskRun,
					// interval : 10000
					// });
				}
			},
			getDataView : function() {
				if (this.patinetView)
					return this.patinetView;
				this.dataview = this.createModule("patientView",
						this.refPatientView);
				this.dataview.parent = this;
				this.dataview.requestData.openBy = this.openBy;
				this.dataview.on("click", this.onClick, this);
				this.dataview.on("mydblClick", this.onDblClick, this);
				this.patinetView = this.dataview.initPanel();
				return this.patinetView;
			},
			getPatientList : function() {
				if (this.patinetList)
					return this.patinetList;
				this.datalist = this.createModule("patientList",
						this.refPatientList);
				this.datalist.parent = this;
				this.datalist.on("click", this.onClick, this);
				this.datalist.on("mydblClick", this.onDblClick, this);
				this.patinetList = this.datalist.initPanel();
				return this.patinetList;
			},
			createMyButtons : function() {
				var actions = this.actions
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}

					// ** add by yzh **
					var btnFlag;
					if (action.notReadOnly)
						btnFlag = false
					else
						btnFlag = this.exContext.readOnly || false

					var btn = {
						accessKey : f1 + i,
						text : action.name + "&nbsp;",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle == "true"),
						scale : action.scale || "small",
						// ** add by yzh **
						disabled : btnFlag,
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			getFilterCheckBox : function() {
				var checkBox_1 = Ext.form.CheckboxGroup({
							width : 130,
							style : 'padding-left:15px;',
							items : [{
								boxLabel : '<font color="red">病危</font>',
								name : 'level_9'
									// listeners : {
									// change : this.doRefresh()
									// }
								}, {
								boxLabel : '病重',
								name : 'level_8'
							}]
						});
				return [checkBox_1];
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			viewModeSelect : function(f, r, index) {
				var v = f.getValue();
				this.alias = v;
				if (this.openBy == "doctor") {				
					this.curView = this.dataview;
					if (this.lastKSDM != v) {
						this.lastKSDM = v;
						if (v != -3) {
							this.panel.getTopToolbar().find('cmd', 'twd')[0]
									.setDisabled(false)
							this.panel.getTopToolbar().find('cmd', 'consApply')[0]
									.setDisabled(false)
							var view = Ext.getCmp('patient-view');
							if (this.patinetList) {
								this.patinetList.hide();
							}
							this.getDataView().show();
							// this.getDataView().doLayout();
							this.curView = this.dataview;
							this.modeName = "dataview";
							if (this.firstLoad) {
								this.doRefresh();
							}
						} else {//出院
							this.panel.getTopToolbar().find('cmd', 'twd')[0]
									.setDisabled(true)
							this.panel.getTopToolbar().find('cmd', 'consApply')[0]
									.setDisabled(true)
							var view = Ext.getCmp('patient-view');
							this.getDataView().hide();
							if (!this.patinetList) {
								view.insert(0, this.getPatientList());
								view.doLayout();
							} else {
								this.patinetList.show();
							}
							this.curView = this.datalist;
							this.modeName = "listview";
							// this.curView.listServiceId = "simpleQuery";
							this.curView.requestData.CY72 = 1;
							this.curView.requestData.cnd = [
									'and',
									[
											'eq',
											['$', 'JGID'],
											[
													's',
													this.mainApp['phisApp'].deptId]],
									['eq', ['$', 'CYPB'], ['i', 8]]]
							this.curView.refresh();
						}
					}
				} else {
					// 通过按钮菜单方式改变视图模式
					// this.tab.tab.setActiveTab(0);
					// if (v == 1) {// 床位模式
					// var view = Ext.getCmp('patient-view');
					// if (this.patinetList) {
					// this.patinetList.hide();
					// }
					// this.getDataView().show();
					// // this.getDataView().doLayout();
					// this.curView = this.dataview;
					// this.modeName = "dataview";
					// } else if (v == 2) {// 病人列表
					// var view = Ext.getCmp('patient-view');
					// this.getDataView().hide();
					// if (!this.patinetList) {
					// view.insert(0, this.getPatientList());
					// view.doLayout();
					// } else {
					// this.patinetList.show();
					// }
					// this.curView = this.datalist;
					// this.modeName = "listview";
					// }
					// this.curView.requestData.cnd = ['and',
					// ['eq', ['$', 'BRBQ'], ['d',
					// this.mainApp['phis'].wardId]],
					// ['le', ['$', 'CYPB'], ['i', 1]]]
					// this.curView.refresh();
				}
			},
			viewChange : function(v) {
				// 通过按钮菜单方式改变视图模式
				this.tab.tab.setActiveTab(0);
				if (v == 1) {// 床位模式
					var view = Ext.getCmp('patient-view');
					if (this.patinetList) {
						this.patinetList.hide();
					}
					this.getDataView().show();
					// this.getDataView().doLayout();
					this.curView = this.dataview;
					this.modeName = "dataview";
					this.curView.requestData.CY72 = null;
					this.curView.requestData.cnd = [
					'and',
					['eq', ['$', 'BRBQ'],
							['d', this.mainApp['phis'].wardId]],
					['le', ['$', 'CYPB'], ['i', 1]]]
				} else if (v == 2) {// 病人列表
					var view = Ext.getCmp('patient-view');
					this.getDataView().hide();
					if (!this.patinetList) {
						view.insert(0, this.getPatientList());
						view.doLayout();
					} else {
						this.patinetList.show();
					}
					this.curView = this.datalist;
					this.modeName = "listview";
					this.curView.requestData.CY72 = null;
					this.curView.requestData.cnd = [
					'and',
					['eq', ['$', 'BRBQ'],
							['d', this.mainApp['phis'].wardId]],
					['le', ['$', 'CYPB'], ['i', 1]]]
				} else if (v == 3) {// 出院72小时
					var view = Ext.getCmp('patient-view');
					this.getDataView().hide();
					if (!this.patinetList) {
						view.insert(0, this.getPatientList());
						view.doLayout();
					} else {
						this.patinetList.show();
					}
					this.curView = this.datalist;
					this.modeName = "listview";
					this.curView.requestData.CY72 = 1;
					this.curView.requestData.cnd = [
							'and',
							['eq',['$', 'JGID'],
									['s',this.mainApp['phisApp'].deptId]],
							['eq', ['$', 'CYPB'], ['i', 8]]]
				}
				this.curView.refresh();
			},
			getDetailsTab : function() {
				var tab = this.createModule("refPatientDetailsTab",
						this.refDetailsTab);
				tab.initPanel = function() {
					if (this.tab) {
						return this.tab;
					}
					var tabItems = []
					var actions = this.actions
					for (var i = 0; i < actions.length; i++) {
						var ac = actions[i];
						tabItems.push({
									frame : this.frame,
									layout : "fit",
									title : ac.name,
									exCfg : ac,
									id : ac.id
								})
					}
					var tab = new Ext.TabPanel({
								border : false,
								width : this.width,
								activeTab : 0,
								frame : true,
								resizeTabs : this.resizeTabs,
								tabPosition : this.tabPosition || "top",
								// autoHeight : true,
								defaults : {
									border : false
									// autoHeight : true,
									// autoWidth : true
								},
								items : tabItems
							})
					tab.on("tabchange", this.onTabChange, this);
					tab.on("beforetabchange", this.onBeforeTabChange, this);
					tab.activate(this.activateId)
					this.tab = tab
					return tab;
				}
				tab.on("tabchange", this.tabChange, this);
				this.tab = tab;
				return tab.initPanel();
			},
			doSpecialkey : function(field, e) {
				/** modified by gaof 2013-9-19 病区护士医嘱输入页面的病人信息表单中可以输入住院号码切换病人* */
				// e.stopEvent();
				if (e.getKey() == e.ENTER) { //
					// 触发了listener后，如果按回车，执行相应的方法
					var name = field.getName();
					var value = field.getValue();
					if (name == "ZYHM" && value) {
						var dataview = this.dataview;
						var store = this.dataview.store;
						var isFindRecord = false;
						store.each(function() {
									if (value == this.get("ZYHM")) {
										dataview.selectRow(store.indexOf(this));
										isFindRecord = true;
										return;
									}
								})
						if (isFindRecord == false) {
							Ext.Msg.alert("提示", "该住院号码判断不存在!", function() {
										field.focus(false, false);
									});

						}

					}
				}
			},
			onClick : function(view, index, item, e) {
				// alert('onClick');
				// 1、填写详细信息tab页
				// 2、改变按钮状态
				// if (this.curView.lastIndex == index)// 相同记录重复点击不处理
				// return;
				this.curView.lastIndex = index;
				var data = view.store.getAt(index).data;

				this.lastData = data;
				var form;
				/** modified by gaof 2013-9-19 病区护士医嘱输入页面的病人信息表单中可以输入住院号码切换病人* */
				if (this.tab.tab.getActiveTab().id == "patientTab") {
					form = this.tab.midiModules[this.tab.tab.getActiveTab().id];
					var zyhm = form.form.getForm().findField("ZYHM");
					// console.debug(zyhm)
					zyhm.un("specialkey", form.onFieldSpecialkey, form);
					zyhm.on("specialkey", this.doSpecialkey, this);

				}

				if (data.ZYH) {
					this.tab.tab.setActiveTab(0);
					form = this.tab.midiModules[this.tab.tab.getActiveTab().id];
					form.doNew();
					form.initFormData(data);
				} else {
					this.tab.tab.setActiveTab(1);
					form = this.tab.midiModules[this.tab.tab.getActiveTab().id];
					form.doNew();
					form.initFormData(data);
				}
				this.changeBtnStatus(data);
				// if (this.modeName == "listview") {
				if (!data.ZYH)
					return;
				phis.script.rmi.jsonRequest({
							serviceId : this.listServiceId,
							serviceAction : "loadPatientExInfo",
							body : {
								ZYH : data.ZYH,
								BRID : data.BRID
							}
						}, function(code, msg, json) {
							if (code < 300) {
								var body = json.body;
								if (body) {
									var gmywList = body.GMYW;
									var gmyws = "";
									for (var i = 0; i < gmywList.length; i++) {
										gmyws += gmywList[i].YPMC + ",";
									}
									var GMYW = gmyws.substring(0, gmyws.length
													- 1);
									form.form.getForm().findField("GMYW")
											.setValue(GMYW);
									data.GMYW = GMYW;
									// var ryzdList = body.RYZD;
									// var ryzds = "";
									// for (var i = 0; i < ryzdList.length; i++)
									// {
									// ryzds += ryzdList[i].JBMC + ",";
									// }
									// form.form.getForm().findField("JBMC")
									// .setValue(ryzds.substring(0,
									// ryzds.length - 1));
									var cyz = body.CYZ;
									// if (data.CYPB > 0 || cyz > 0) {
									// if (this.midiMenus['ContextMenu']) {
									// this.midiMenus['ContextMenu'].items
									// .itemAt(5).enable();
									// }
									// this.panel.getTopToolbar().items
									// .item(5).enable();
									// } else {
									// if (this.midiMenus['ContextMenu']) {
									// this.midiMenus['ContextMenu'].items
									// .itemAt(5).disable();
									// }
									// this.panel.getTopToolbar().items
									// .item(5).disable();
									// }
								}
							} else {
								this.processReturnMsg(code, msg)
							}
						}, this)
				// }
			},
			onDblClick : function() {
				var r = this.curView.getSelectedRecord();
				var data = r.data;
				if (!data.ZKZT) {
					data.ZKZT = 0;
				}
				if (data.ZKZT == 0) { // 当含有转科状态值且为0时才能打开医嘱处理
					//2019-10-15 zhaojian 住院医生站病人列表双击病人关闭自动刷新功能
					this.stopRefresh();
					this.doYzcl();
				} else if (data.ZKZT == 1) {
					MyMessageTip.msg("提示", "该病人为转科状态，不能进行医嘱录入!", true);
				}
			},
			tabChange : function(tabPanel, newTab, curTab) {
				if (this.lastData) {
					this.tab.midiModules[newTab.id].doNew();
					if (!this.lastData.ZYH && newTab.id == "patientTab")
						return;
					if (this.modeName == "dataview") {
						this.tab.midiModules[newTab.id]
								.initFormData(this.lastData);
					} else {
						var form = this.tab.midiModules[newTab.id];
						if (!this.lastData.BRCH)
							return;
						form.initDataBody = {
							JGID : this.mainApp['phisApp'].deptId,
							BRCH : this.lastData.BRCH
						}
						form.loadData();
					}
				}
			},
			onContextMenu : function(view, rowIndex, e) {
				if (e) {
					e.stopEvent()
				}
				if (this.disableContextMenu) {
					return
				}
				this.curView.selectRow(rowIndex)
				var cmenu = this.midiMenus['ContextMenu']
				if (!cmenu) {
					var items = [];
					var actions = this.actions
					if (!actions) {
						return;
					}
					for (var i = 0; i < actions.length; i++) {
						var action = actions[i];
						var it = {}
						it.cmd = action.id
						it.ref = action.ref
						it.iconCls = action.iconCls || action.id
						it.script = action.script
						it.text = action.name
						it.handler = this.doAction
						it.scope = this
						items.push(it)
					}
					// 病案首页、入院记录、病程记录、手术记录、出院记录、知情同意书、其他病历、EHRView、费用清单
					items.push({
								cmd : "2000001",
								iconCls : "basy",
								handler : this.doOpenEmrEditor,
								text : "病案首页",
								scope : this
							});
					items.push({
								cmd : "292",
								iconCls : "ryjl",
								handler : this.doOpenEmrEditor,
								text : "入院记录",
								scope : this
							});
					items.push({
								cmd : "294",
								iconCls : "bcjl",
								handler : this.doOpenEmrEditor,
								text : "病程记录",
								scope : this
							});
					items.push({
								cmd : "303",
								iconCls : "ssjl",
								handler : this.doOpenEmrEditor,
								text : "手术记录",
								scope : this
							});
					items.push({
								cmd : "51",
								iconCls : "cyjl",
								handler : this.doOpenEmrEditor,
								text : "出院记录",
								scope : this
							});
					items.push({
								cmd : "329",
								iconCls : "zqtys",
								handler : this.doOpenEmrEditor,
								text : "知情同意书",
								scope : this
							});
					// items.push({
					// cmd : "zyzz",
					// iconCls : "transfer",
					// handler : this.doZyzz,
					// text : "住院转诊申请",
					// scope : this
					// });
					items.push({
								cmd : "qtbl",
								iconCls : "qtbl",
								handler : this.doZyysz,
								text : "其他病历",
								scope : this
							});
					items.push({
								cmd : "ehrView",
								iconCls : "ehrview",
								handler : this.doOpenEHRViewEditor,
								text : "EHRView",
								scope : this
							});
					// add by chzhxiang 2013.09.17
					if (this.exContext.systemParams.QYJYBZ == '1') {
						items.push({
									cmd : "jykd",
									iconCls : "advice",
									handler : this.doJykd,
									text : "检验开单",
									scope : this
								});
						if (this.openBy == "nurse") {
							items.push({
										cmd : "jyzx",
										iconCls : "images_send",
										handler : this.doJyzx,
										text : "检验执行",
										scope : this
									});
						}
						items.push({
									cmd : "jybg",
									iconCls : "temperature",
									handler : this.doJybg,
									text : "检验报告",
									scope : this
								});

					}
					// var result = phis.script.rmi.miniJsonRequestSync({
					// serviceId : "emrManageService",
					// serviceAction : "loadBllb",
					// body : {
					// ZYBZ : 1
					// }
					// })
					//
					// if (result.code == 200) {
					// var BLLB = result.json.BLLB;
					// for (var i = 0; i < BLLB.length; i++) {
					// var it = {};
					// it.cmd = "openEmr"
					// it.lbbh = BLLB[i].LBBH
					// it.iconCls = "openEmr"
					// it.text = BLLB[i].YDLBMC
					// it.handler = this.doAction
					// it.scope = this
					// items.push(it)
					// }
					// }
					cmenu = new Ext.menu.Menu({
								items : items
							})
					this.midiMenus['ContextMenu'] = cmenu
				}
				// @@ to set menuItem disable or enable according to buttons of
				// toptoolbar.
				var data = view.store.getAt(rowIndex).data;
				this.changeMenuStatus(data, cmenu)
				cmenu.showAt([e.getPageX() + 5, e.getPageY() + 5])
			},
			doOpenEHRViewEditor : function(item, f) {
				/*
				 * var r = this.curView.getSelectedRecord(); var body = {};
				 * body.empiId = r.get("EMPIID") || ''; body.logonName =
				 * this.mainApp.logonName || ''; body.manaUtilId =
				 * this.mainApp['phisApp'].deptId || ''; var result =
				 * phis.script.rmi.miniJsonRequestSync({ serviceId :
				 * "bschisEntranceService", serviceAction : "getEHRViewURL",
				 * body : body }); if (result.code > 300) { app.modules.common
				 * .processReturnMsg(result.code, result.msg); return } var
				 * owurl = result.json.ehrViewURL; var panel = new Ext.Panel({
				 * id : "ehrview", border : false, html : '<iframe id="ehrview"
				 * src="' + owurl + '" width="100%" height="100%"
				 * frameborder="0" scrolling="auto"></iframe>', autoScroll :
				 * true }); this.ehrPanel = panel; if (!this.chiswin) { var win =
				 * new Ext.Window({ title : '健康档案', closable : true, width :
				 * "100%", height : Ext.getBody().getHeight() + 42, iconCls :
				 * 'icon-grid', shim : true, // border:false, plain : true,
				 * layout : "fit", animCollapse : true, closeAction : "hide",
				 * constrainHeader : true, minimizable : false, resizable :
				 * false, maximizable : false, shadow : false, modal : true,
				 * draggable : false }); win.on("hide", this.dowinclose, this);
				 * this.chiswin = win } this.chiswin.removeAll();
				 * this.chiswin.add(panel); this.chiswin.show();
				 */
				var r = this.curView.getSelectedRecord();
				var body = {};
				body["jzksdm"] = this.mainApp['phis'].departmentId;
				body["empiId"] = r.get("EMPIID");
				body["personName"] = r.get("BRXM");
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
				// kh : '102828372',
				// klx : 2,
				// xm : '张三',
				// jzksdm : '015',
				// agentid : '1010001',
				// ip : '10.1.44.12',
				// mac : '20-7C-8F-73-FD-10'
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
			doOpenEmrEditor : function(item, f) {
				var cmd = item.cmd;
				var r = this.curView.getSelectedRecord();
				if (r) {
					// 判断是否设置发药药房
					var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.listServiceId,
								serviceAction : "loadHospitalParams",
								body : {
									BQDM : r.get("BRBQ")
								}
							})
					if (res.code == 200) {
						var fyyfs = res.json.body.fyyf;
						if (!fyyfs) {
							MyMessageTip.msg("提示", "请先维护病区发药药房信息!", true);
							return;
						}
						var exContext = {};
						exContext.yfxx = {};
						for (var i = 0; i < fyyfs.length; i++) {
							var fyyf = fyyfs[i];
							if (fyyf.DMSB == 1) {
								exContext.yfxx.bqxyf = fyyf.YFSB;
								exContext.yfxx.bqxyf_text = fyyf.YFMC;
							} else if (fyyf.DMSB == 2) {
								exContext.yfxx.bqzyf = fyyf.YFSB;
								exContext.yfxx.bqzyf_text = fyyf.YFMC;
							} else if (fyyf.DMSB == 3) {
								exContext.yfxx.bqcyf = fyyf.YFSB;
								exContext.yfxx.bqcyf_text = fyyf.YFMC;
							}
						}
						if (!exContext.yfxx.bqxyf || !exContext.yfxx.bqzyf
								|| !exContext.yfxx.bqcyf) {
							MyMessageTip.msg("提示", "请先维护病区发药药房信息!", true);
							return;
						}
						var docPermissions = res.json.body.MZ_YSQX;
						// if (!docPermissions || !docPermissions.KCFQ
						// || docPermissions.KCFQ < 1) {
						// MyMessageTip.msg("提示", "对不起，您没有开处方的权限!", true);
						// return;
						// }
						exContext.docPermissions = docPermissions;
					} else {
						this.processReturnMsg(res.code, res.msg);
						return;
					}
					exContext.brxx = r;
					exContext.alias = this.alias;
					var m = new phis.application.war.script.HDRView({
								empiId : r.get("EMPIID"),
								clinicId : r.get("ZYH"),
								brid : r.get("BRID")
							})
					m.openBy = this.openBy;
					m.initOpenEmrNode = cmd;
					m.on("close", this.doRefresh, this);
					Ext.applyIf(m.exContext, exContext);
					m.setMainApp(this.mainApp);
					m.getWin().show()
					m.getWin().maximize()
				}
			},
			changeMenuStatus : function(data, cmenu) {
				if (this.openBy == "doctor") {
					cmenu.find('cmd', 'cwfp')[0].hide();
					cmenu.find('cmd', 'tysq')[0].hide();
					cmenu.find('cmd', 'zccl')[0].hide();
					cmenu.find('cmd', 'tccl')[0].hide();
					cmenu.find('cmd', 'tzcy')[0].hide();
					cmenu.find('cmd', 'hljl')[0].hide();
					cmenu.find('cmd', 'twd')[0].hide();
					if (this.exContext.systemParams.QYDZBL !== '1') {
						// for (var i = 14; i < 21; i++) {
						for (var i = 15; i < 22; i++) {// 添加了住院转诊申请按钮，因此相应的遍历数据加1
							cmenu.items.itemAt(i).hide();
						}
					}
				} else {
					if (this.exContext.systemParams.ZYYSQY == "1") {
						cmenu.find('cmd', 'zkcl')[0].hide();
						cmenu.find('cmd', 'cyzgl')[0].hide();
					}
					// for (var i = 12; i < 19; i++) {
					for (var i = 13; i < 20; i++) {// 添加了住院转诊申请按钮，因此相应的遍历数据加1
						cmenu.items.itemAt(i).hide();
					}
				}
				// 是否启用双向转诊
				if (this.exContext.systemParams.QYSXZZ == "0") {
					var zyzz = cmenu.find('cmd', 'zyzz');
					if (zyzz[0]) {
						zyzz[0].hide();
					}
				}
				this.setBtnStatus(cmenu, data);

			},
			changeBtnStatus : function(data) {
				// var btns = this.panel.getTopToolbar().items;
				var toolBar = this.panel.getTopToolbar();
				this.setBtnStatus(toolBar, data);
			},
			setBtnStatus : function(toolBar, data) {
				if (data.ZYH) {
					toolBar.find('cmd', 'cwfp')[0].disable();
					toolBar.find('cmd', 'brxx')[0].enable();
					if (data.CYZ > 0) {
						toolBar.find('cmd', 'zkcl')[0].disable();
					} else {
						toolBar.find('cmd', 'zkcl')[0].enable();
					}
					if (data.ZKZT > 0) {
						toolBar.find('cmd', 'yzcl')[0].disable();
						toolBar.find('cmd', 'tysq')[0].disable();
						toolBar.find('cmd', 'zccl')[0].disable();
						toolBar.find('cmd', 'tccl')[0].disable();
						toolBar.find('cmd', 'zkcl')[0].enable();
						toolBar.find('cmd', 'twd')[0].disable();
						toolBar.find('cmd', 'hljl')[0].disable();
						toolBar.find('cmd', 'zkgl')[0].disable();
						toolBar.find('cmd', 'zkcl')[0].setText("取消转科");
						toolBar.find('cmd', 'cyzgl')[0].disable();
						toolBar.find('cmd', 'tzcy')[0].disable();
						return;
					} else {
						toolBar.find('cmd', 'zkcl')[0].setText("转科");
					}
					if (data.CYPB == 8) {
						toolBar.find('cmd', 'zccl')[0].disable();
						toolBar.find('cmd', 'zkcl')[0].disable();
//						toolBar.find('cmd', 'hljl')[0].disable();
						/****************modify by lizhi出院证明状态可打印体温单护理记录单*****************/
						toolBar.find('cmd', 'twd')[0].enable();
						toolBar.find('cmd', 'hljl')[0].enable();
						toolBar.find('cmd', 'cyzgl')[0].disable();
						toolBar.find('cmd', 'tzcy')[0].disable();
						return;
					} else if (data.CYPB == 1) {
						toolBar.find('cmd', 'tysq')[0].disable();
						toolBar.find('cmd', 'zccl')[0].disable();
//						toolBar.find('cmd', 'twd')[0].disable();
//						toolBar.find('cmd', 'hljl')[0].disable();
						/****************modify by lizhi出院证明状态可打印体温单护理记录单*****************/
						toolBar.find('cmd', 'twd')[0].enable();
						toolBar.find('cmd', 'hljl')[0].enable();
						toolBar.find('cmd', 'zkgl')[0].enable();
						toolBar.find('cmd', 'cyzgl')[0].enable();
					} else {
						toolBar.find('cmd', 'yzcl')[0].enable();
						toolBar.find('cmd', 'tysq')[0].enable();
						toolBar.find('cmd', 'zccl')[0].enable();
						toolBar.find('cmd', 'tccl')[0].enable();
						toolBar.find('cmd', 'zkcl')[0].enable();
						toolBar.find('cmd', 'twd')[0].enable();
						toolBar.find('cmd', 'hljl')[0].enable();
						toolBar.find('cmd', 'zkgl')[0].enable();
						toolBar.find('cmd', 'cyzgl')[0].enable();
					}
					if (data.BRCH) {
						if (data.CYPB >= 1 || data.CYZ > 0) {
							toolBar.find('cmd', 'tccl')[0].enable();
							toolBar.find('cmd', 'zkcl')[0].disable();
						} else {
							toolBar.find('cmd', 'tccl')[0].disable();
							toolBar.find('cmd', 'zkcl')[0].enable();
						}
					} else {
						toolBar.find('cmd', 'zccl')[0].disable();
						toolBar.find('cmd', 'tccl')[0].disable();
					}
					toolBar.find('cmd', 'tzcy')[0].enable();
					if (toolBar.find('cmd', 'zyzz')[0])
						toolBar.find('cmd', 'zyzz')[0].enable();// 住院转诊按钮启用
					if (toolBar.find('cmd', 'jykd')[0])
						toolBar.find('cmd', 'jykd')[0].enable();// 检验开单按钮启用
					if (toolBar.find('cmd', 'jybg')[0])
						toolBar.find('cmd', 'jybg')[0].enable();// 检验报告按钮启用
					if (toolBar.find('cmd', 'jyzx')[0])
						toolBar.find('cmd', 'jyzx')[0].enable();// 检验执行按钮启用
				} else {
					toolBar.find('cmd', 'cwfp')[0].enable();
					toolBar.find('cmd', 'brxx')[0].disable();
					toolBar.find('cmd', 'yzcl')[0].disable();
					toolBar.find('cmd', 'tysq')[0].disable();
					toolBar.find('cmd', 'zccl')[0].disable();
					toolBar.find('cmd', 'tccl')[0].disable();
					toolBar.find('cmd', 'zkcl')[0].disable();
					toolBar.find('cmd', 'twd')[0].disable();
					toolBar.find('cmd', 'hljl')[0].disable();
					toolBar.find('cmd', 'zkgl')[0].disable();
					toolBar.find('cmd', 'cyzgl')[0].disable();
					toolBar.find('cmd', 'tzcy')[0].disable();
					if (toolBar.find('cmd', 'zyzz')[0])
						toolBar.find('cmd', 'zyzz')[0].disable();// 住院转诊按钮禁用
					if (toolBar.find('cmd', 'jykd')[0])
						toolBar.find('cmd', 'jykd')[0].disable();// 检验开单按钮禁用
					if (toolBar.find('cmd', 'jybg')[0])
						toolBar.find('cmd', 'jybg')[0].disable();// 检验报告按钮禁用
					if (toolBar.find('cmd', 'jyzx')[0])
						toolBar.find('cmd', 'jyzx')[0].disable();// 检验执行按钮禁用
				}
			},
			/**
			 * 分配床位
			 */
			doCwfp : function() {
				var r = this.curView.getSelectedRecord();
				var module = this
						.createModule("cwfpModule", this.refCwfpModule);
				if (module) {
					var win = module.getWin();
					if (!this.cwfpWin) {
						win.add(module.initPanel());
						this.cwfpWin = win;
					}
					var cwhm = r.get("BRCH");
					var cwks = r.get("CWKS");
					module.cwhm = cwhm;
					module.cwks = cwks;
					module.opener = this;
					this.cwfpWin.setTitle("床位分配-请选择分配到【" + cwhm + "】床的病人");
					this.cwfpWin.show();
				}
			},
			/**
			 * 打开病人信息界面
			 */
			doBrxx : function() {
				var r = this.curView.getSelectedRecord();
				var module = this
						.createModule("brxxModule", this.refBrxxModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					module.exContext.record = r;
					if (this.brxxWin) {
						this.brxxWin.show();
						// 转科状态的病人只能查看
						if (r.data.ZKZT == 1) {
							module.tab.buttons[0].setDisabled(true);
							MyMessageTip.msg("提示", "该病人为转科状态，只能查看病人信息!", true);
						} else {
							module.tab.buttons[0].setDisabled(false);
						}
						return;
					}
					module.opener = this;
					this.brxxWin = module.getWin();
					this.brxxWin.setHeight(440);
					this.brxxWin.show();
					// 转科状态的病人只能查看
					if (r.data.ZKZT == 1) {
						module.tab.buttons[0].setDisabled(true);
						MyMessageTip.msg("提示", "该病人为转科状态，只能查看病人信息!", true);
					} else {
						module.tab.buttons[0].setDisabled(false);
					}
				}
			},
			/**
			 * 打开住院转诊申请界面
			 */
			doZyzz : function() {
				if (this.exContext.systemParams.QYSXZZ == "1") {
					var r = this.curView.getSelectedRecord();
					r.data["mark"] = "HSZ";
					if (!this.zyzzWin) {
						var module = this.createModule("zyzzModule",
								this.refZyzzModule);
						if (module) {
							module.exContext = r.data;
							module.opener = this;
							this.zyzzMod = module;
							this.zyzzWin = module.getWin();
							this.zyzzWin.show();
						}
					} else {
						this.zyzzMod.exContext = r.data;
						this.zyzzMod.resetData();
						this.zyzzWin.show();
					}
				} else {
					MyMessageTip.msg("提示", "未启用双向转诊配置!", true);
				}
			},
			/**
			 * 医嘱处理
			 */
			doYzcl : function() {
				var r = this.curView.getSelectedRecord();
				if (!r.get("ZYH"))
					return;
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.listServiceId,
							serviceAction : "queryZY_BRRY",
							ZYH : r.data.ZYH
						})

				if (res.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				var data = res.json.ZY_BRRY;
				Ext.apply(r.data, data);
				// r.data = data;
//				if (data.CYPB >= 8) {
//					MyMessageTip.msg("提示", "该病人已出院,不能操作!", true);
//					this.doRefresh();
//					return;
//				}
//				if(data.CYPB==1){
//					MyMessageTip.msg("提示", "该病人已通知出院,不能操作!", true);
//					this.doRefresh();
//					return;
//				}
				if (this.openBy != "doctor" && r.get("CYPB") == 1) {
					var module = this.createModule("wardAdviceQueryModule",
							this.refWardAdviceQueryModule);
					if (module) {
						module.initDataId = r.get("ZYH");
						module.opener = this;
						module.exContext.systemParams = this.loadSystemParams({
									commons : ['MZYP', 'JSYP'],
									privates : ['XSFJJJ_YS', 'XSFJJJ_HS',
											'ZYYSQY']
								});
						if (this.adviceQueryWin) {
							this.adviceQueryWin.show();
							return;
						}
						// module.opener = this;
						this.adviceQueryWin = this.getWin();
						this.on("winShow", module.onWinShow, module);
						this.adviceQueryWin.add(module.initPanel());
						this.adviceQueryWin.setHeight(600);
						this.adviceQueryWin.setWidth(1024);
						this.adviceQueryWin.maximize();
						this.adviceQueryWin.show();
					}
					return;
				}

				if (!r.get("ZSYS")) {
					MyMessageTip.msg("提示", "该病人未维护主任医生，不能进行医嘱录入!", true);
					return;
				}
				if (r.get("ZKZT") > 0) {
					MyMessageTip.msg("提示", "该病人已转科，不能进行医嘱录入!", true);
					return;
				}
				// var p = {};
				// p.YWXH = this.openBy == "nurse" ? "1006" : "1007";
				// p.SDXH = r.get("ZYH") + "";
				// p.BRID = r.get("BRID");
				// if (!this.bclLock(p))// 业务锁
				// return false;
				if (this.openBy == "doctor") {
					this.doZyysz();
					return;
				}
				// 判断是否设置发药药房
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.listServiceId,
							serviceAction : "loadHospitalParams",
							body : {
								BQDM : this.mainApp['phis'].wardId
							}
						})
				if (res.code == 200) {
					var fyyfs = res.json.body.fyyf;
					if (!fyyfs) {
						MyMessageTip.msg("提示", "请先维护病区发药药房信息!", true);
						return;
					}
					var exContext = {};
					exContext.yfxx = {};
					for (var i = 0; i < fyyfs.length; i++) {
						var fyyf = fyyfs[i];
						if (fyyf.DMSB == 1) {
							exContext.yfxx.bqxyf = fyyf.YFSB;
							exContext.yfxx.bqxyf_text = fyyf.YFMC;
						} else if (fyyf.DMSB == 2) {
							exContext.yfxx.bqzyf = fyyf.YFSB;
							exContext.yfxx.bqzyf_text = fyyf.YFMC;
						} else if (fyyf.DMSB == 3) {
							exContext.yfxx.bqcyf = fyyf.YFSB;
							exContext.yfxx.bqcyf_text = fyyf.YFMC;
						}
					}
					if (!exContext.yfxx.bqxyf || !exContext.yfxx.bqzyf
							|| !exContext.yfxx.bqcyf) {
						MyMessageTip.msg("提示", "请先维护病区发药药房信息!", true);
						return;
					}
					var docPermissions = res.json.body.MZ_YSQX;
					// if (!docPermissions || !docPermissions.KCFQ
					// || docPermissions.KCFQ < 1) {
					// MyMessageTip.msg("提示", "对不起，您没有开处方的权限!", true);
					// return;
					// }
					exContext.docPermissions = docPermissions;
				} else {
					this.processReturnMsg(res.code, res.msg);
					return;
				}

				exContext.brxx = r;
				var module = this
						.createModule("yzclModule", this.refYzclModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					if (!this.yzclWin) {
						module.exContext = exContext;
						module.on("close", this.doRefresh, this);
						var win = module.getWin();
						win.setHeight(600);
						win.add(module.initPanel());
						this.yzclWin = win;
					} else {
						module.exContext.yfxx = exContext.yfxx;
						module.exContext.brxx = exContext.brxx;
						module.exContext.docPermissions = docPermissions;
					}
					this.yzclWin.maximize();
					this.yzclWin.show();
				}
			},
			/**
			 * 退药申请
			 */
			doTysq : function() {
				var r = this.curView.getSelectedRecord();
				var module = this
						.createModule("tysqModule", this.refTysqModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					var win = module.getWin();
					if (!this.tysqWin) {
						win.setWidth(1024);
						win.setHeight(600);
						win.add(module.initPanel());
						this.tysqWin = win;
					}
					this.tysqWin.show();
				}
			},
			/**
			 * 转床处理
			 */
			doZccl : function() {
				var r = this.curView.getSelectedRecord();
				if (!r)
					return;
				var module = this
						.createModule("zcclModule", this.refZcclModule)
				// alert(Ext.encode(r.data))
				module.jgid = r.get("JGID");
				module.brch = r.get("BRCH");
				module.brxb = r.get("BRXB");
				module.cwxb = r.get("CWXB");
				module.brbq = r.get("BRBQ");
				module.jcpb = r.get("JCPB");
				module.cwks = r.get("CWKS");
				module.zyh = r.get("ZYH");
				module.brxm = r.get("BRXM");
				if (!this.zcclWin) {
					module.on("doSave", this.doRefresh, this);
					this.zcclWin = module.getWin();
				}

				this.zcclWin.show();
			},
			/**
			 * 转科处理
			 */
			doZkcl : function() {
				var r = this.curView.getSelectedRecord();
				if (!r)
					return;
				var body1 = {
					zyh : r.get("ZYH"),
					CWHM : r.get("BRKS"),
					type : 1
				};
				var body2 = {
					zyh : r.get("ZYH"),
					CWHM : r.get("BRKS"),
					type : 2
				};

				var body3 = {
					zyh : r.get("ZYH"),
					CWHM : r.get("BRKS"),
					type : 3
				};
				var body4 = {
					zyh : r.get("ZYH"),
					CWHM : r.get("BRKS"),
					type : 4
				};

				var body5 = {
					zyh : r.get("ZYH"),
					CWHM : r.get("BRKS"),
					type : 5
				}; // MyMessageTip.msg("提示", "出院方式不能为空!", true);
				// this.form.getForm().findField("CYFS").focus();

				// 查询是否有未退包房
				var result2 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceZkId,
							serviceAction : "queryInfo",
							body : body2
						})
				if (result2.json.count > 1) {
					MyMessageTip.msg("提示", "病人有未退包床，请先退包床，再做转科处理!", true);
					return;
				}
				// 查询是否有出院证明
				var result3 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceZkId,
							serviceAction : "queryInfo",
							body : body3
						})
				if (result3.json.count > 0) {
					MyMessageTip.msg("提示", "病人已经操作出院证，不能进行转科处理!", true);
					return;
				}
				// 有会诊申请转科给予提示
				var result4 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceZkId,
							serviceAction : "queryInfo",
							body : body4
						})
				if (result4.json.count > 0) {
					Ext.Msg.show({
								title : '确认进行转科',
								msg : '病人有会诊申请，是否继续做转科处理?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn != "ok") {
										return;
									}
								},
								scope : this
							})
					// MyMessageTip.msg("提示", "病人有会诊申请，继续做转科处理!", true);
				}
				// 查看是否有皮试药品未做
				var result5 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceZkId,
							serviceAction : "queryInfo",
							body : body5
						})
				if (result5.json.count > 0) {
					MyMessageTip.msg("提示", "该病人有皮试药品未做，不能转科!", true);
					return;
				}

				var module = this
						.createModule("zkclModule", this.refZkclModule);
				// 查询是否有转科记录
				var result1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceZkId,
							serviceAction : "querySFZK",
							ZYH : r.get("ZYH")
						})
				if (result1.json.count > 0) {
					module.operate = -1;

				} else {
					module.operate = 1;
				}
				if (result1.json.body) {
					module.HHKS = result1.json.body.HHKS;
					module.HHYS = result1.json.body.HHYS;
					module.HHBQ = result1.json.body.HHBQ;
				} else {
					module.HHKS = "";
					module.HHYS = "";
					module.HHBQ = "";
				}
				// alert(Ext.encode(r.data))
				module.jgid = r.get("JGID");// 机构ID
				module.brch = r.get("BRCH");// 病人床号
				module.zyh = r.get("ZYH");// 住院号
				module.zyhm = r.get("ZYHM");// 住院号码
				module.brxm = r.get("BRXM");// 病人姓名
				module.brks = r.get("BRKS");// 转前科室
				module.brbq = r.get("BRBQ");// 转前病区
				module.brys = r.get("ZSYS");// 转前医生(主任医生)
				if (!this.zkclWin) {
					module.on("doSave", this.doRefresh, this);
					this.zkclWin = module.getWin();
				}
				this.zkclWin.show();
			},
			/**
			 * 退床处理
			 */
			doTccl : function() {
				var r = this.curView.getSelectedRecord();
				Ext.Msg.confirm("提示", "确认把该床位腾空吗？", function(btn, text) {
							if (btn == "yes") {
								var body = {};
								body.ZYH = r.get("ZYH");
								body.CWHM = r.get("BRCH");
								this.panel.el.mask("在正处理数据...");
								phis.script.rmi.jsonRequest({
											serviceId : this.listServiceId,
											serviceAction : "saveTccl",
											body : body
										}, function(code, msg, json) {
											this.panel.el.unmask()
											if (code < 300) {
												MyMessageTip.msg("提示", "【"
																+ body.CWHM
																+ "】号床退床操作成功!",
														true);
												this.curView.refresh();
											} else {
												this.processReturnMsg(code,
														msg, this.doRemove)
											}
										}, this)
							}
						}, this);
			},
			/**
			 * 帐卡查询
			 */
			doZkgl : function() {
				var r = this.curView.getSelectedRecord();
				if (this.openBy == "doctor" && this.lastKSDM == "-3") {
					var cymodule = this.createModule("cyzkglModule",
							this.refcyZkglModule);
					if (cymodule) {
						var data = {};
						data = r.data;
						data.JSLX = 10;
						// alert(r.get("JSCS"))
						if (this.cyzkglWin) {
							cymodule.doFillIn(data);
							this.cyzkglWin.show();
							return;
						}
						cymodule.data = data;
						cymodule.opener = this;
						this.cyzkglWin = cymodule.getWin();
						this.cyzkglWin.add(cymodule.initPanel());
						this.cyzkglWin.setWidth(800);
						this.cyzkglWin.setHeight(500);
						this.cyzkglWin.show();
						// module.doFillIn(data);
					}
				} else {
					// alert(this.refZkglModule)
					var module = this.createModule("zkglModule",
							this.refZkglModule);
					if (module) {
						var data = {};
						data = r.data;
						data.JSLX = 10;
						// alert(r.get("JSCS"))
						if (this.zkglWin) {
							module.doFillIn(data);
							this.zkglWin.show();
							return;
						}
						module.data = data;
						module.opener = this;
						this.zkglWin = module.getWin();
						this.zkglWin.add(module.initPanel());
						this.zkglWin.setWidth(800);
						this.zkglWin.setHeight(500);
						this.zkglWin.show();
					}
				}
			},
			/**
			 * 护理记录
			 */
			doHljl : function() {
				var r = this.curView.getSelectedRecord();
				var module = this.createModule(this.refHljl, this.refHljl);
				if (module) {
					var data = {};
					data.ZYH = r.get("ZYH");// 住院号
					data.BRBQ = r.get("BRBQ");// 病人病区
					data.BRCH = r.get("BRCH");// 病人床号
					data.BRXM = r.get("BRXM");// 病人姓名
					data.ZYHM = r.get("ZYHM");// 住院号吗
					data.BRXB_text = r.get("BRXB_text");// 性别中文
					data.AGE = r.get("AGE");// 年龄
					data.BRKS_text = r.get("BRKS_text");// 病人科室中文
					if (r.get("JBMC")) {
						data.JBMC = r.get("JBMC");
					} else if (r.get("MQZD")) {
						data.JBMC = r.get("MQZD");
					} else {
						data.JBMC = "";
					}// 疾病名称
					data.BRQK = r.get("BRQK");// 病人情况
					data.GMYW = r.get("GMYW");// 过敏药物
					// data.ICU = r.get("ICU");
					if (this.hljlWin) {
						module.doFillIn(data);
						this.hljlWin.show();
						return;
					}
					// module.data = data;
					// module.opener = this;
					module.doInitData(data);
					this.hljlWin = module.getWin();
					this.hljlWin.add(module.initPanel());
					this.hljlWin.setWidth(1024);
					this.hljlWin.setHeight(600);
					this.hljlWin.maximize();
					this.hljlWin.show();
					// module.doFillIn(data);
				}
			},
			/**
			 * 护理计划
			 */
			doHljh : function() {
				var r = this.curView.getSelectedRecord();
				var module = this.createModule(this.refHljh, this.refHljh);
				if (module) {
					var data = {};
					data.ZYH = r.get("ZYH");// 住院号
					data.BRBQ = r.get("BRBQ");// 病人病区
					data.BRCH = r.get("BRCH");// 病人床号
					data.BRXM = r.get("BRXM");// 病人姓名
					data.ZYHM = r.get("ZYHM");// 住院号吗
					data.BRXB_text = r.get("BRXB_text");// 性别中文
					data.AGE = r.get("AGE");// 年龄
					data.BRKS_text = r.get("BRKS_text");// 病人科室中文
					if (r.get("JBMC")) {
						data.JBMC = r.get("JBMC");
					} else if (r.get("MQZD")) {
						data.JBMC = r.get("MQZD");
					} else {
						data.JBMC = "";
					}// 疾病名称
					data.BRQK = r.get("BRQK");// 病人情况
					data.GMYW = r.get("GMYW");// 过敏药物
					// data.ICU = r.get("ICU");
					if (this.hljhWin) {
						module.doFillIn(data);
						this.hljhWin.show();
						return;
					}
					// module.data = data;
					// module.opener = this;
					module.doInitData(data);
					this.hljhWin = module.getWin();
					this.hljhWin.add(module.initPanel());
					this.hljhWin.setWidth(1024);
					this.hljhWin.setHeight(600);
					this.hljhWin.maximize();
					this.hljhWin.show();
					// module.doFillIn(data);
				}
			},
			/**
			 * 出院证管理
			 */
			doCyzgl : function() {
				var r = this.curView.getSelectedRecord();
				var module = this.createModule("cyzglModule",
						this.refCyzglModule);
				if (module) {
					module.initDataId = r.get("ZYH");
					if (this.cyzWin) {
						this.cyzWin.show();
						return;
					}
					module.opener = this;
					module.on("doSave", this.doRefresh, this);
					this.cyzWin = module.getWin();
					this.cyzWin.add(module.initPanel());
					this.cyzWin.setWidth(800);
					this.cyzWin.setHeight(400);
					this.cyzWin.show();
				}
			},
			/**
			 * 通知出院
			 */
			doTzcy : function() {
				this.panel.el.mask("功能模块加载中...");
				var r = this.curView.getSelectedRecord();
				if (r) {
					// 判断是否办理出院证
					var module = this.createModule("tzcyModule",
							this.refTzcyModule);
					if (module) {
						module.initDataId = r.get("ZYH");
						if (this.tzcyWin) {
							this.tzcyWin.show();
							this.panel.el.unmask();
							return;
						}
						module.opener = this;
						this.tzcyWin = module.getWin();
						this.tzcyWin.show();
					}
				}
				this.panel.el.unmask();
			},
			doZyysz : function(f, e, initModules, activeTab) {
				var r = this.curView.getSelectedRecord();
				if (r) {
					// 判断是否设置发药药房
					var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.listServiceId,
								serviceAction : "loadHospitalParams",
								body : {
									BQDM : r.get("BRBQ")
								}
							})
					if (res.code == 200) {
						var fyyfs = res.json.body.fyyf;
						if (!fyyfs) {
							MyMessageTip.msg("提示", "请先维护病区发药药房信息!", true);
							return;
						}
						var exContext = {};
						exContext.yfxx = {};
						for (var i = 0; i < fyyfs.length; i++) {
							var fyyf = fyyfs[i];
							if (fyyf.DMSB == 1) {
								exContext.yfxx.bqxyf = fyyf.YFSB;
								exContext.yfxx.bqxyf_text = fyyf.YFMC;
							} else if (fyyf.DMSB == 2) {
								exContext.yfxx.bqzyf = fyyf.YFSB;
								exContext.yfxx.bqzyf_text = fyyf.YFMC;
							} else if (fyyf.DMSB == 3) {
								exContext.yfxx.bqcyf = fyyf.YFSB;
								exContext.yfxx.bqcyf_text = fyyf.YFMC;
							}
						}
						if (!exContext.yfxx.bqxyf || !exContext.yfxx.bqzyf
								|| !exContext.yfxx.bqcyf) {
							MyMessageTip.msg("提示", "请先维护病区发药药房信息!", true);
							return;
						}
						var docPermissions = res.json.body.MZ_YSQX;
						// if (!docPermissions || !docPermissions.KCFQ
						// || docPermissions.KCFQ < 1) {
						// MyMessageTip.msg("提示", "对不起，您没有开处方的权限!", true);
						// return;
						// }
						exContext.docPermissions = docPermissions;
					} else {
						this.processReturnMsg(res.code, res.msg);
						return;
					}
					exContext.brxx = r;
					exContext.alias = this.alias;
					var m = new phis.application.war.script.HDRView({
								empiId : r.get("EMPIID"),
								clinicId : r.get("ZYH"),
								brid : r.get("BRID")
							})
					m.initModules = initModules;
					if (activeTab) {
						m.activeTab = activeTab;
					}
					m.opener = this;
					m.on("close", this.doRefresh, this);
					Ext.applyIf(m.exContext, exContext);
					m.setMainApp(this.mainApp);
					m.getWin().show()
					m.getWin().maximize()

					var xzResult = util.rmi.miniJsonRequestSync({
						serviceId : "hai.hmsInterfaceService",
						serviceAction : "getDownRecord",
						method : "execute",
				                body : {		                    
			                    	"idcard":r.data.SFZH,           
				                }
					});
					if(xzResult.code == 200){
						var body = xzResult.json.body;
						var xzTime = body.xzTime;
						var yyName = body.yyName;
						if(typeof xzTime != "undefined" && typeof yyName != "undefined"){
							MyMessageTip.msg("提示", "此患者 "+xzTime+"从"+yyName+"下转诊患者！", false)
						}	
					}
													
					var szResult = util.rmi.miniJsonRequestSync({
						serviceId : "hai.hmsInterfaceService",
						serviceAction : "getUpRecord",
						method : "execute",
				                body : {		                    
			                    	"idcard":r.data.SFZH,           
				                }
					});	
					if(szResult.code == 200){
						var body = szResult.json.body;
						var szTime = body.szTime;
						var yyName = body.yyName;
						if(typeof szTime != "undefined" || typeof yyName != "undefined"){
							MyMessageTip.msg("提示", "此患者 "+szTime+"从"+yyName+"上转诊患者！", false)
						}							
					}
				}
			},
			/**
			 * 会诊申请
			 */
			doConsApply : function() {
				// this.panel.el.mask("功能模块加载中...");
				// var r = this.curView.getSelectedRecord();
				// // if (!r.get("ZYH"))
				// // return;
				// var module = this.createModule("hzsqModule", "WAR13")
				//
				// if (!this.zkclWin) {
				// module.on("doSave", this.doRefresh, this);
				// this.zkclWin = module.getWin();
				// }
				// module.exContext = this.exContext;
				// // console.debug("brxx data:{}",this.exContext.brxx.data);
				// // Ext.applyIf(module.exContext.empiData,
				// // this.exContext.brxx.data);
				// this.zkclWin.show();
				var initModules = [];
				initModules.push("C04");
				this.doZyysz(null, null, initModules, 1);
			},
			onConsApplySave : function(entryName, op, json, data) {
				this.consWin.hide();
				this.doRefresh();
			},
			/**
			 * 刷新
			 */
			doRefresh : function() {
				var BRQK = [];
				var HLJB = [];
				if (this.otherTbar) {
					var zyRadiogroup = this.otherTbar.findById('zyRadiogroup');
					if (zyRadiogroup) {
						this.curView.requestData.ZYZT = zyRadiogroup.getValue().inputValue;
					}
					var bwItems = this.otherTbar.findById('bwCheckBoxs').items;
					for (var i = 0; i < bwItems.length; i++) {
						if (bwItems.itemAt(i).checked) {
							BRQK.push(bwItems.itemAt(i).name)
						}
					}
					var careItems = this.otherTbar.findById('hlCheckBoxs').items;
					for (var i = 0; i < careItems.length; i++) {
						if (careItems.itemAt(i).checked) {
							HLJB.push(careItems.itemAt(i).name)
						}
					}
					var zyhTxt = this.otherTbar.findById('zyhTxt');
					if (zyhTxt) {
						this.curView.requestData.ZYHM = zyhTxt.getValue();
					}else{
						delete this.curView.requestData.ZYHM;
					}
					var brxmTxt = this.otherTbar.findById('brxmTxt');
					if (brxmTxt) {
						this.curView.requestData.BRXM = brxmTxt.getValue();
					}else{
						delete this.curView.requestData.BRXM;
					}
				}
				if (BRQK.length > 0) {
					this.curView.requestData.BRQK = BRQK;
				} else {
					delete this.curView.requestData.BRQK;
				}
				if (HLJB.length > 0) {
					this.curView.requestData.HLJB = HLJB;
				} else {
					delete this.curView.requestData.HLJB;
				}

				this.curView.requestData.KSDM = this.lastKSDM;
				this.curView.refresh();
			},
			onKeyUp : function(f, e) {
				var k = e.getKey();
				if (k == e.ENTER) {
					var v = f.getValue();
					if(!v){
						this.curView.requestData.cnd = null;
						this.curView.refresh();
						return;
					}
					var itid = this.queryCombo.getValue();
					var cnd = [];
					if (this.otherTbar) {
						var queryTxt = this.otherTbar.findById('queryTxt');
						if (!queryTxt) {
							return;
						}
						if (itid && itid!="") {
							if(itid=="1"){
								cnd[0] = "eq";
								cnd[1]=['$','a.ZYHM'];
								cnd.push(['s',v]);
							}else if(itid=="2"){
								cnd[0] = "like";
								cnd[1]=['$','a.BRXM'];
								cnd.push(['s','%'+v+'%']);
							}else if(itid=="3"){
								if(isNaN(v)){
									MyMessageTip.msg("提示", "请输入数字!",true);
									return;
								}
								//设置日期，当前日期的前七天
								var myDate = new Date(); //获取今天日期
								myDate.setDate(myDate.getDate() - (7*v));
								var endDateTime = this.dateToString(myDate);
								cnd[0] = "ge";
								cnd[1]=['$',"TO_CHAR(a.CYRQ,'yyyy-MM-dd hh24:mi:ss')"];
								cnd.push(['s', endDateTime]);
							}
						}
						this.curView.requestData.cnd = cnd.length == 0 ? null : cnd;
						this.curView.refresh();
					}
				}
			},
			doCndQuery : function() {
				var itid = this.queryCombo.getValue();
				var cnd = [];
				if (this.otherTbar) {
					var queryTxt = this.otherTbar.findById('queryTxt');
					var queryValue = queryTxt.getValue();			
					if (!queryTxt) {
						return;
					}
					if(queryValue==""){
						this.curView.requestData.cnd = cnd.length == 0 ? null : cnd;
						this.curView.refresh();
						return;
					}
					if (itid && itid!="") {
						if(itid=="1"){
							cnd[0] = "eq";
							cnd[1]=['$','a.ZYHM'];
							cnd.push(['s',queryValue]);
						}else if(itid=="2"){
							cnd[0] = "like";
							cnd[1]=['$','a.BRXM'];
							cnd.push(['s','%'+queryValue+'%']);
						}else if(itid=="3"){
							if(isNaN(queryValue)){
								MyMessageTip.msg("提示", "请输入数字!",true);
								return;
							}
							//设置日期，当前日期的前七天
							var myDate = new Date(); //获取今天日期
							myDate.setDate(myDate.getDate() - (7*queryValue));
							var endDateTime = this.dateToString(myDate);
							cnd[0] = "ge";
							cnd[1]=['$',"TO_CHAR(a.CYRQ,'yyyy-MM-dd hh24:mi:ss')"];
							cnd.push(['s', endDateTime]);
						}
					}
					this.curView.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.curView.refresh();
				}
			},
			dateToString : function(now){
			    var year = now.getFullYear();
			    var month =(now.getMonth() + 1).toString();
			    var day = (now.getDate()).toString();
			    var hour = (now.getHours()).toString();
			    var minute = (now.getMinutes()).toString();
			    var second = (now.getSeconds()).toString();
			    if (month.length == 1) {
			        month = "0" + month;
			    }
			    if (day.length == 1) {
			        day = "0" + day;
			    }
			    if (hour.length == 1) {
			        hour = "0" + hour;
			    }
			    if (minute.length == 1) {
			        minute = "0" + minute;
			    }
			    if (second.length == 1) {
			        second = "0" + second;
			    }
			    var dateTime = year + "-" + month + "-" + day +" "+ hour +":"+minute+":"+second;
			    return dateTime;
			},
			doTwd : function() {
				var r = this.curView.getSelectedRecord();
				if (!r)
					return;
				var module = this.createModule("wdjModule", this.refWdjModule)
				var ryrq = Date.parseDate(r.get("RYRQ").split(' ')[0], "Y-m-d")// 修正病人视图下时间格式不对的问题
				var cyrq = new Date();
				if (!Ext.isEmpty(r.get("CYRQ"))) {
					cyrq = Date.parseDate(r.get("CYRQ").split(' ')[0], "Y-m-d")
				}
				module.maxWeek = Math.floor((Math.floor((cyrq.getTime() - ryrq
						.getTime())
						/ 1000 / 60 / 60 / 24))
						/ 7);
				module.currentWeek = module.maxWeek;
				module.info = r.data;
				if (module.rModule && module.rModule.smtzForm) {
					module.rModule.smtzForm.form.getForm().findField("CJSJ")
							.setValue(new Date());
				}
				/*
				 * module.info.jgid = r.get("JGID");// 机构ID module.info.brch =
				 * r.get("BRCH");// 病人床号 module.info.zyh = r.get("ZYH");// 住院号
				 * module.info.zyhm = r.get("ZYHM");// 住院号码 module.info.brxm =
				 * r.get("BRXM");// 病人姓名 module.info.brks = r.get("BRKS");//
				 * 转前科室 module.info.brbq = r.get("BRBQ");// 转前病区
				 * module.info.brys = r.get("ZZYS");// 转前医生
				 */
				if (!this.wdjWin) {
					// module.on("doSave", this.doRefresh, this);
					this.wdjWin = module.getWin();
				}
				this.wdjWin.show();

			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
			/**
			 * 检验开单 add by chzhxiang 2013.09.17
			 */
			doJykd : function() {
				var r = this.curView.getSelectedRecord();
				var module = this.createModule("jykdModule", this.refJykd);
				if (module) {
					module.data.jgdm = this.mainApp['phisApp'].deptId;
					module.data.zyh = r.get("ZYH");
					module.data.ysgh = this.mainApp.uid;
					module.data.ks = r.get("BRKS");
					if (r.get("AGE") <= 1) {// 婴儿标志，小于1岁
						module.data.yebz = 1;
					} else {
						module.data.yebz = 0;
					}

					var win = module.getWin();
					// if (!this.yjkdWin) {//
					win.setWidth(1024);
					win.setHeight(600);
					win.add(module.initPanel());
					this.yjkdWin = win;
					// }
					this.yjkdWin.maximize();
					this.yjkdWin.show();
				}
			},
			/**
			 * 检验执行 add by chzhxiang 2013.09.17
			 */
			doJyzx : function() {
				var r = this.curView.getSelectedRecord();
				var module = this.createModule("jyzxModule", this.refJyzx);
				if (module) {
					module.wardId = this.mainApp['phis'].wardId;
					module.uid = this.mainApp.uid;
					var win = module.getWin();
					// if (!this.yjzxkdWin) {
					win.setWidth(1024);
					win.setHeight(600);
					win.add(module.initPanel());
					this.yjzxkdWin = win;
					// }
					this.yjzxkdWin.maximize();
					this.yjzxkdWin.show();
				}
			},
			/**
			 * 检验报告 add by chzhxiang 2013.12.05
			 */
			doJybg : function() {
				var r = this.curView.getSelectedRecord();
				var module = this.createModule("jybgModule", this.refJybg);
				if (module) {
					module.data.jgdm = this.mainApp['phisApp'].deptId;
					module.data.zyh = r.get("ZYH");
					module.data.zyhm = r.get("ZYHM");
					module.data.ysgh = this.mainApp.uid;
					module.data.ks = r.get("BRKS");
					var win = module.getWin();
					// if (!this.yjzxkdWin) {
					win.setWidth(1024);
					win.setHeight(600);
					win.add(module.initPanel());
					this.yjzxkdWin = win;
					// }
					this.yjzxkdWin.maximize();
					this.yjzxkdWin.show();
				}
			},// 抗菌药物申请
			doAmqcApply : function() {
				var r = this.curView.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中病人信息!", true);
					return;
				}
				var module = this.createModule("amqcApplyList",
						this.refAmqcApplyList);
				module.initDataId = r.get("ZYH");
				module.brxx = r.data;
				if (!this.amqcApplyListWin) {
					this.amqcApplyListWin = module.getWin();
					this.amqcApplyListWin.add(module.initPanel());
					this.amqcApplyListWin.setWidth(1000);
					this.amqcApplyListWin.setHeight(500);
				}
				this.amqcApplyListWin.show();
			},
			doCtk : function() {
				var r = this.curView.getSelectedRecord();
				var data = {};
				data.ZYH = r.get("ZYH");// 住院号
				data.BRBQ = r.get("BRBQ");// 病人病区
				data.BRCH = r.get("BRCH");// 病人床号
				data.BRXM = r.get("BRXM");// 病人姓名
				data.ZYHM = r.get("ZYHM");// 住院号吗
				data.BRXB_text = r.get("BRXB_text");// 性别中文
				data.AGE = r.get("AGE");// 年龄
				data.BRKS_text = r.get("BRKS_text");// 病人科室中文
				data.RYRQ = r.get("RYRQ");// 病人床号
				data.SZYS = r.get("SZYS");// 收治医生
				var url="resources/phis.prints.jrxml.BedCard.print?type=1";
				url += "&ZYH="+data.ZYH+"&BRKS_text="+data.BRKS_text
					+"&BRCH="+data.BRCH+"&BRXM="+data.BRXM
					+"&BRXB_text="+data.BRXB_text+"&RYRQ="+data.RYRQ.replace(" ","_")
					+"&AGE="+data.AGE+"&ZYHM="+data.ZYHM+"&SZYS="+data.SZYS;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
//				LODOP.PREVIEW();
				LODOP.PRINT();
			},
			// 定时刷新
			autoFresh : function(obj, seconds) {
				var task = {
					run : function() {
						if(obj.curView!=undefined){
							var this1 = obj;
							var lastRunTime = this1.task.lastRunTime;
							var currentTime = new Date().getTime();
							if (currentTime - lastRunTime < 1000) {								
								this1.stopRefresh();
								return false;
							}
							this.lastRunTime = currentTime;
							if(this1.viewModeComb.value == -1){//只对当前登录医生“我的病人”列表进行刷新
								this1.doRefresh();
							}
						}
					},
					interval : seconds * 1000,
					status : false,
					lastRunTime : 0
				}
				return task;
			},
			stopRefresh : function() {// 停止定时刷新
				if (this.task) {
					Ext.TaskMgr.stop(this.task);
					this.task.status = false;
				}
			},
			startRefresh : function(seconds) {// 启动定时刷新
				if (this.task) {
					if (!this.task.status) {
						Ext.TaskMgr.start(this.task);
					}
				} else {
					this.task = this.autoFresh(this,seconds);
					var t = Ext.TaskMgr.start(this.task);
					this.task.status = true;
				}
			}
		});