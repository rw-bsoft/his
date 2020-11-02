$package("phis.application.med.script");
$import("phis.script.TabModule", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil");
		phis.application.med.script.MedicalTechnologyProjectCancel = function(
				cfg) {
			phis.application.med.script.MedicalTechnologyProjectCancel.superclass.constructor
					.apply(this, [ cfg ]);
		},

		Ext
				.extend(
						phis.application.med.script.MedicalTechnologyProjectCancel,
						phis.script.TabModule,
						{
							initPanel : function() {
								if (!this.mainApp['phis'].MedicalId) {
									MyMessageTip.msg("提示", "请选择医技科室!", true);
									return;
								}
								// if (this.initMTDept() == 0) {
								// MyMessageTip.msg("提示", "初始化科室信息失败!", true);
								// return;
								// }
								if (this.tab) {
									return this.tab;
								}
								var re = phis.script.rmi
										.miniJsonRequestSync({
											serviceId : "medicalTechnicalSectionService",
											serviceAction : "queryXTCS",
											CSMC : "YJZXQXZD"
										});
								if (re.code > 300) {
									this.processReturnMsg(re.code, re.msg,
											this.initPanel);
									return;
								} else {
									this.YJZXQXZD = re.json.body;
								}
								var tbar = [];
								// 添加日历控件
								this.dateField1 = new Ext.form.DateField({
									name : 'storeDate',
									minValue : '1900-01-01',
									width : 100,
									allowBlank : false,
									altFormats : 'Y-m-d',
									value : this.getServerDate(),
									format : 'Y-m-d'
								});
								// 添加日历控
								this.dateField2 = new Ext.form.DateField({
									name : 'storeDate',
									minValue : '1900-01-01',
									width : 100,
									allowBlank : false,
									altFormats : 'Y-m-d',
									value : this.getServerDate(),
									format : 'Y-m-d'
								});

								tbar = [ '执行日期:', this.dateField1, '至',
										this.dateField2 ];
								this.textField = new Ext.form.TextField({
									xtype : 'textfield',
									id : "txt_name",
									fieldLabel : "号码"
								});
								// 添加文本框的键盘按下时间,需要和enableKeyEvents :
								// true,一起使用才有效
								// this.textField.on("specialkey",
								// this.textEnterEven, this);
								// this.textField.on("change", this.textChange,
								// this);
								this.lable = new Ext.form.Label({
									id : 'label',
									text : '号码:'
								});
								tbar.push(this.lable);
								tbar.push(this.textField);
								var actions = this.actions;
								// this.param == "3";
								// alert(this.param);
								// 遍历Applications.xml中配置的Tab页，并将其添加到tabItems中
								for ( var i = 0; i < actions.length; i++) {
									var ac = actions[i];
									if (this.YJZXQXZD != 1 && ac.id == 'print') {
										continue;
									}
									// 添加Button按钮
									var btn = {};
									btn.id = ac.id;
									btn.text = ac.name,
											btn.iconCls = ac.iconCls || ac.id;
									// 添加doAction点击事件,调用doAction方法
									btn.handler = this.doAction;
									btn.name = ac.id;
									btn.scope = this;
									tbar.push(btn);
								}
								var panel = new Ext.Panel({
									border : false,
									frame : true,
									layout : 'border',
									defaults : {
										border : false
									},
									buttonAlign : 'center',
									items : [ {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getTabs()
									} ],
									tbar : tbar
								})
								// panel.on("render", this.onReady, this);
								return (this.panel = panel)

							},
							getTabs : function() {
								this.module = this.createModule("medModuleTab",
										this.refTable);
								this.module.opener = this;
								this.module.strdate = this.dateField1;
								this.module.enddate = this.dateField2;
								this.module.carno = this.textField;
								return this.module.initPanel();
							},
							// initPanel : function() {
							// if (!this.mainApp['phis'].MedicalId) {
							// MyMessageTip.msg("提示", "请选择医技科室!", true);
							// return;
							// }
							// if (this.initMTDept() == 0) {
							// MyMessageTip.msg("提示", "初始化科室信息失败!", true);
							// return;
							// }
							// if (this.tab) {
							// return this.tab;
							// }
							// var re = phis.script.rmi
							// .miniJsonRequestSync({
							// serviceId : "medicalTechnicalSectionService",
							// serviceAction : "queryXTCS",
							// CSMC : "YJZXQXZD"
							// });
							// if (re.code > 300) {
							// this.processReturnMsg(re.code, re.msg,
							// this.initPanel);
							// return;
							// } else {
							// this.YJZXQXZD = re.json.body;
							// }
							// var tabItems = [];
							// var tbar = [];
							// var bbar = [];
							// // 添加日历控件
							// this.dateField1 = new Ext.form.DateField({
							// name : 'storeDate',
							// minValue : '1900-01-01',
							// width : 100,
							// allowBlank : false,
							// altFormats : 'Y-m-d',
							// value : this.getServerDate(),
							// format : 'Y-m-d'
							// });
							// // 添加日历控
							// this.dateField2 = new Ext.form.DateField({
							// name : 'storeDate',
							// minValue : '1900-01-01',
							// width : 100,
							// allowBlank : false,
							// altFormats : 'Y-m-d',
							// value : this.getServerDate(),
							// format : 'Y-m-d'
							// });
							//
							// tbar = [ '执行日期:', this.dateField1, '至',
							// this.dateField2 ];
							// this.textField = new Ext.form.TextField({
							// xtype : 'textfield',
							// id : "txt_name",
							// fieldLabel : "姓名"
							// });
							// // 添加文本框的键盘按下时间,需要和enableKeyEvents :
							// // true,一起使用才有效
							// this.textField.on("specialkey",
							// this.textEnterEven, this);
							// this.textField.on("change", this.textChange,
							// this);
							// this.lable = new Ext.form.Label({
							// id : 'label',
							// text : ''
							// });
							// tbar.push(this.lable);
							// tbar.push(this.textField);
							// var actions = this.actions;
							// // this.param == "3";
							// // alert(this.param);
							// // 遍历Applications.xml中配置的Tab页，并将其添加到tabItems中
							// for ( var i = 0; i < actions.length; i++) {
							// var ac = actions[i];
							// if (this.YJZXQXZD != 1 && ac.id == 'print') {
							// continue;
							// }
							// // 添加Button按钮
							// var btn = {};
							// btn.id = ac.id;
							// btn.text = ac.name,
							// btn.iconCls = ac.iconCls || ac.id;
							// // 添加doAction点击事件,调用doAction方法
							// btn.handler = this.doAction;
							// btn.name = ac.id;
							// btn.scope = this;
							// tbar.push(btn);
							// }
							//
							// // 创建一个TabPanel，用于加载"门诊业务"和"住院业务"两个Tab页
							// var tab = new Ext.TabPanel({
							// title : " ",
							// border : false,
							// // layout:"border",
							// width : this.width,// 设置宽度
							// activeTab : 0,// 加载第1项标签页
							// tbar : tbar,
							// frame : true,
							// defaults : {
							// border : false,
							// autoWidth : true
							// },
							// items : tabItems
							// });
							// // 添加Tab页改变后的监听事件处理方法
							// tab.on("beforetabchange", this.beforetabchange,
							// this);
							// this.tab = tab;
							// return tab;
							// },
							getServerDate : function() {
								return Date.getServerDate();
							},
							doAction : function(item, e) {
								if (item.id == "refresh") {
									this.refresh();
								} else if (item.id == "cancel") {
									this.cancel();
								} else if (item.id == "print") {
									this.print();
								}
							},
							refresh : function() {
								this.module
										.moduleLoadData(this.module.newTabqj.id);
							},
							cancel : function() {
								this.module.doCancel();
							},
						// /**
						// * 医技取消
						// */
						// cancel : function() {
						// if (this.showType == "msBusiTab") {
						// this.midiModules[this.MSPatient].cancel();
						// } else if (this.showType == "zyBusiTab") {
						// this.midiModules[this.ZYPatient].cancel();
						// }
						// },
						// /**
						// * 刷新
						// */
						// refresh : function() {
						// var d1 = this.dateField1.getRawValue();
						// var d2 = this.dateField2.getRawValue();
						// if (!d1) {
						// MyMessageTip.msg("提示", "开始时间不能为空!", true);
						// return;
						// }
						// if (!d2) {
						// MyMessageTip.msg("提示", "结束时间不能为空!", true);
						// return;
						// }
						// var df = new Date(Date.parse(d1.replace(/-/g,
						// "/")));
						// var dt = new Date(Date.parse(d2.replace(/-/g,
						// "/")));
						// if (df.getTime() > dt.getTime()) {
						// Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
						// return;
						// }
						// // 门诊业务
						// if (this.showType == "msBusiTab") {
						// var rData = [ {
						// "nl_zxks" : this.mainApp['phis'].MedicalId,
						// "al_jgid" : this.mainApp['phisApp'].deptId,
						// "adt_Begin" : d1,
						// "adt_End" : d2,
						// "filterFactor" : this.textField
						// .getValue()
						// } ];
						// this.midiModules[this.MSPatient]
						// .refreshPatient(rData);
						// } else if (this.showType == "zyBusiTab") {
						// var rData = [ {
						// "al_zxks" : this.mainApp['phis'].MedicalId,
						// "al_jgid" : this.mainApp['phisApp'].deptId,
						// "adt_Begin" : d1,
						// "adt_End" : d2,
						// "filterFactor" : this.textField
						// .getValue()
						// } ];
						// this.midiModules[this.ZYPatient]
						// .refreshPatient(rData);
						// }
						// },
						// /**
						// * 添加键盘Enter事件
						// *
						// * @param textfield
						// * @param e
						// */
						// textEnterEven : function(textfield, e) {
						// // 判断键盘按下的是否为Enter键
						// if (e.getKey() == Ext.EventObject.ENTER) {
						// this.textChange("", textfield.getValue());
						// }
						// },
						// /**
						// * 文本框改变事件触发方法
						// *
						// * @param textF
						// * @param newValue
						// * @param oldValue
						// */
						// textChange : function(textF, newValue, oldValue)
						// {
						// // if(this.showType == "msBusiTab"){
						// //
						// this.midiModules[this.MSPatient].fuzzySearchPatient(newValue);
						// // }else if(this.showType == "zyBusiTab"){
						// //
						// this.midiModules[this.ZYPatient].fuzzySearchPatient(newValue);
						// // }
						// this.refresh();
						// },
						// /**
						// * Tab页改变后事件处理方法
						// *
						// * @param tabPanel
						// * @param newTab
						// * @param curTab
						// */
						// beforetabchange : function(tabPanel, newTab,
						// curTab) {// 为今后业务变化做扩展
						// this.showType = newTab.id;
						// if (newTab.id == "msBusiTab") {
						// this.lable.setText("卡号：");
						// } else if (newTab.id == "zyBusiTab") {
						// this.lable.setText("住院号码：");
						// }
						// // 该方法调用的是TabModule类中的onTabChange方法,用于重新加载选择的Tab页
						// this.onTabChange(tabPanel, newTab, curTab);
						// },
						// /**
						// * 获取病人列表
						// */
						// getPatient : function() {
						// var patientList;
						// if (this.showType == "msBusiTab") {// 门诊业务
						// var rData = {
						// "nl_zxks" : this.mainApp['phis'].MedicalId,
						// "al_jgid" : this.mainApp['phisApp'].deptId
						// };
						// // this.msPatient有Applications.xml中配置
						// patientList = this.createModule(
						// this.MSPatient, this.MSPatient,
						// rData);
						//
						// } else {// 住院业务
						// var rData = {
						// "al_zxks" : this.mainApp['phis'].MedicalId,
						// "al_jgid" : this.mainApp['phisApp'].deptId
						// };
						// // this.ZYPatient有Applications.xml中配置
						// patientList = this.createModule(
						// this.ZYPatient, this.ZYPatient,
						// rData);
						// }
						// patientList.on("processResult",
						// this.mimiJsonRequest2Return, this);
						// patientList.on("queryProject",
						// this.queryProject, this);
						// return patientList.initPanel();
						// },
						// /**
						// * 获取项目列表
						// */
						// getProject : function() {
						// var projectList;
						// if (this.showType == "msBusiTab") {// 门诊业务
						// projectList = this.createModule(
						// this.MSProject, this.MSProject);
						// } else {// 住院业务
						// // this.ZYProject有Applications.xml中配置
						// projectList = this.createModule(
						// this.ZYProject, this.ZYProject);
						// }
						// return projectList.initPanel();
						// },
						// /**
						// * 查询病人项目
						// *
						// * @param tmp
						// */
						// queryProject : function(selectRow) {
						// if (this.showType == "msBusiTab") {
						// this.midiModules[this.MSProject]
						// .reLoadData(selectRow);
						// } else if (this.showType == "zyBusiTab") {
						// /**
						// * 2013-07-09 修改直接调用医技执行中查询项目MED010104
						// *
						// 修改描述：由于原有MED0204中使用框架查询，在MED_ZY_PROJECT.xml中用到了TPLJ字段作为金额字段，所以修改成使用医技执行中的MED010104
						// * 注释下面代码，并添加注释到修改结束代码
						// */
						// //
						// this.midiModules[this.ZYProject].reLoadData(selectRow);
						// if (selectRow) {
						// this.midiModules[this.ZYProject].listServiceId =
						// "medicalTechnicalSectionService";
						// this.midiModules[this.ZYProject].requestData["serviceAction"]
						// = "getZyList_Proj";
						// this.midiModules[this.ZYProject].requestData["jgid"]
						// = this.mainApp['phisApp'].deptId;
						// this.midiModules[this.ZYProject].requestData["yjxh"]
						// = selectRow.YJXH;
						// this.midiModules[this.ZYProject]
						// .loadData();
						// } else {
						// this.midiModules[this.ZYProject]
						// .clear();
						// }
						// /** 2013-07-09修改结束 * */
						// }
						//
						// },
						// /**
						// * 初始化科室信息
						// *
						// * @returns {Number} 1 成功 ; 0 失败
						// */
						// initMTDept : function() {
						// var medicalId = this.mainApp['phis'].MedicalId;
						// var jgid = this.mainApp['phisApp'].deptId;
						// if (!medicalId
						// || typeof (medicalId) == "undefined"
						// || medicalId == 0) {
						// return 0;
						// }
						//
						// if (!jgid || typeof (jgid) == "undefined"
						// || jgid == 0) {
						// return 0;
						// }
						// var json = {
						// "al_DeptId" : medicalId,
						// "gl_jgid" : jgid
						// };
						// var result = this.mimiJsonRequest2Return(
						// this.sID, this.sAction, json);
						// if (result.code == 200) {
						// this.param = result.json.body;
						// return 1;
						// } else {
						// return 0;
						// }
						// },
						// /**
						// * JSON同步提交请求
						// *
						// * @param sId
						// * 配置的ServiceID
						// * @param sAction
						// * 请求的方法名(例如在Service中的方法名为doQuery，此时应传入query)
						// * @param parameter
						// * 需要发送给服务器的参数值，可在req.get("body")获取
						// * @param showMsg
						// * 请求成功后弹出的提示框 诺不传该参数则不弹出提示框
						// * 诺传入参数，成功：弹出showMsg; 失败：弹出"execute
						// * fail"消息框
						// * @returns
						// */
						// mimiJsonRequest2Return : function(sId, sAction,
						// parameter, showMsg) {
						// // add by caijy for 物资接口
						// var body = {};
						// body['bq'] = this.mainApp['phis'].MedicalId;
						// var r = phis.script.rmi
						// .miniJsonRequestSync({
						// serviceId : "configLogisticsInventoryControlService",
						// serviceAction : "verificationWPJFBZ",
						// body : body
						// });
						// if (r.code > 300) {
						// this.processReturnMsg(r.code, r.msg);
						// return;
						// }
						// var result = phis.script.rmi
						// .miniJsonRequestSync({
						// serviceId : sId,
						// serviceAction : sAction,
						// body : parameter,
						// medicalId : this.mainApp['phis'].MedicalId
						// });
						// if (showMsg != null) {
						// if (result.code == 200) {
						// Ext.Msg.alert('状态:', showMsg);
						// } else {
						// if (result.msg && result.msg != "") {
						// Ext.Msg.alert('状态:', result.msg);
						// } else {
						// Ext.Msg.alert('状态:', "执行失败！");
						// }
						// }
						// }
						// return result;
						// } ,
						// print : function() {
						// if (this.showType == "msBusiTab") {
						// this.midiModules[this.MSPatient].print();
						// } else if (this.showType == "zyBusiTab") {
						// this.midiModules[this.ZYPatient].print();
						// }
						// }

						});