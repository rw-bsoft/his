/**
 * 
 * @author tianj
 */
$package("phis.application.fsb.script");

phis.application.fsb.script.JCBusiListProjectManage = function(cfg) {
	this.width = 960;
	this.height = 600;
	this.modal = true
	phis.application.fsb.script.JCBusiListProjectManage.superclass.constructor
			.apply(this, [ cfg ]);
	this.exContext.args = this.exContext.args || {};
	this.on('doSave', this.doSave, this);

}

Ext
		.extend(
				phis.application.fsb.script.JCBusiListProjectManage,
				phis.script.SimpleModule,
				{
					onBeforeCellEdit : function(it, record, field, value) {
						if (this.param.XZPB != 1) {
							if (this.param.TYPE == 0) {
								if (it.id == "KSDM") {
									return false;
								}
							} else {
								if (it.id == "MZHM" || it.id == "YSDM"
										|| it.id == "KSDM" || it.id == "XMMC") {
									return false;
								}
							}
						}
					},
					onReady : function() {

						if (this.param.XZPB != 1) { // 新增判别 不是新增
						} else { // 新增
							// this.formModule.doNew();

						}
						// this.formModule.doNew();
					},
					initPanel : function() {
						if (this.panel) {
							return this.panel;
						}
						var panel = new Ext.Panel({
							border : false,
							height : this.height,
							frame : true,
							layout : 'border',
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								region : 'north',
								width : 600,
								height : 104,
								items : this.getForm()
							}, {
								layout : "fit",
								border : false,
								split : false,
								title : '',
								width : 600,
								region : 'center',
								items : this.getList()
							} ],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
						this.form.on("afterrender", this.onReady, this)
						this.panel = panel;
						return panel;
					},
					getForm : function() {
						var formModule = this.createModule("JCListForm",
								this.JCListForm);
						formModule.on("setBake", this.listCreate, this);
						formModule.on("startEditing", this.listStartEditing,
								this);
						formModule.opener = this;
						formModule.on("loadData", this.thisFormLoadData, this);
						formModule.initDataId = this.param.YJXH;
						this.formModule = formModule;
						return this.form = formModule.initPanel();
					},
					listCreate : function() {
						this.listModule.doFirstCreate();
					},
					listStartEditing : function() {
						this.listModule.StartEditing();
					},
					getList : function() {
						var listModule = this
								.createModule("JCProjectEditorList",
										this.JCProjectEditorList);
						listModule.opener = this;
						listModule.queryParams = {};
						listModule.queryParams.FYKS = this.mainApp['phis'].MedicalId;
						this.listModule = listModule;
						this.listModule.on("beforeCellEdit",
								this.onBeforeCellEdit, this);
						this.listModule.on("focusZXYS", this.formfocusZXYS,
								this);
						var list = listModule.initPanel();
						return list;
					},
					formfocusZXYS : function() {
						this.formModule.focusZXYS();
					},
					// removeEmptyRecord : function() {
					// var store = this.listModule.grid.getStore();
					// for (var i = 0; i < store.getCount(); i++) {
					// var r = store.getAt(i);
					// if (r.get("FYMC") == null) {
					// store.remove(r);
					// }
					// }
					// this.listModule.grid.getView().refresh();
					// },
					doSave : function() {
						if (this.listModule.grid.activeEditor != null) {
							this.listModule.grid.activeEditor.completeEdit();
						}
						// 判断数据有效性
						// this.removeEmptyRecord();
						var exAdviceRecords = [];
						var store = this.listModule.grid.getStore();
						var n = store.getCount()
						if (n == 0) {
							MyMessageTip.msg("提示", "请先录入医嘱数据!", true);
							this.listModule.doCreate();
							return false;
						}
						var formValue = this.formModule.getSaveData();
						if (!formValue.YSDM) {
							MyMessageTip.msg("提示", "申检医生不能为空或输入有错!", true);
							return;
						}
						if (!formValue.ZXYS) {
							MyMessageTip.msg("提示", "检查医生不能为空或输入有错!", true);
							return;
						}
						if (!formValue.KSDM) {
							MyMessageTip.msg("提示", "申检科室不能为空或输入有错!", true);
							return;
						}
						formValue.ZXKS = this.mainApp['phis'].MedicalId;
						Ext.apply(this.formModule.data, formValue);
						var listValue = this.listModule.getEditorListData();
						if (listValue.length == 0) {
							MyMessageTip.msg("提示", "输入未完成，不能保存!", true);
							return;
						}
						this.panel.el.mask("正在执行操作...");
						phis.script.rmi.jsonRequest({
							serviceId : "medicalTechnicalSectionService",
							serviceAction : "saveJCYJAndProject",
							body : {
								formValue : this.formModule.data,
								listValue : listValue,
								param : this.param
							}
						}, function(code, msg, json) {
							this.panel.el.unmask();
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.fireEvent("save");
							this.getWin().hide();
						}, this);
					},
					doClose : function() {
						var win = this.getWin();
						if (win)
							win.hide();
					},
					// createButton : function() {
					// if (this.op == 'read') {
					// return [];
					// }
					// var actions = this.actions;
					// var buttons = [];
					// if (!actions) {
					// return buttons;
					// }
					// var f1 = 112;
					// for (var i = 0; i < actions.length; i++) {
					// var action = actions[i];
					// var btn = {};
					// btn.accessKey = f1 + i;
					// btn.cmd = action.id;
					// btn.text = action.name + "(F" + (i + 1) + ")";
					// btn.iconCls = action.iconCls || action.id;
					// btn.script = action.script;
					// btn.handler = this.doAction;
					// btn.notReadOnly = action.notReadOnly;
					// btn.scope = this;
					// btn.scale = "large";
					// // btn.iconAlign = "top";
					// if (i < actions.length - 1)
					// buttons.push(btn, '-');
					// else
					// buttons.push(btn);
					// }
					// return buttons;
					// },
					doAction : function(item, e) {
						var cmd = item.cmd
						var script = item.script
						cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
						if (script) {
							$require(script, [
									function() {
										eval(script + '.do' + cmd
												+ '.apply(this,[item,e])')
									}, this ])
						} else {
							var action = this["do" + cmd]
							if (action) {
								action.apply(this, [ item, e ])
							}
						}
					},
					doNew : function() {
						var toolBar = this.panel.getTopToolbar();
						var btn = toolBar.find("cmd", "add");
						var btn1 = toolBar.find("cmd", "delete");
						var btn2 = toolBar.find("cmd", "clear");
						var btn3 = toolBar.find("cmd", "save");
						btn[0].enable();
						btn1[0].enable();
						btn2[0].enable();
						btn3[0].enable();
						this.op = "create";
						if (this.midiModules["JCListForm"]) {
							this.midiModules["JCListForm"].doNew();
							this.midiModules["JCListForm"].XZPB = 1
						}
						if (this.midiModules["JCProjectEditorList"]) {
							this.midiModules["JCProjectEditorList"].doNew();
						}
					},
					doDelete : function() {
						this.listModule.doRemove();
					},
					thisFormLoadData : function() {
						var toolBar = this.panel.getTopToolbar();
						var btn3 = toolBar.find("cmd", "save");
						if (this.param.XZPB != 1) {
							if (this.param.TYPE == 1) { // 病区提交的检查不能修改
								var zyhm = this.form.getForm()
										.findField("ZYHM");
								var ysdm = this.form.getForm()
										.findField("YSDM");
								var ksdm = this.form.getForm()
										.findField("KSDM");
								ksdm.setDisabled(true);
								ysdm.setDisabled(true);
								zyhm.setDisabled(true);
								btn3[0].disable();
							} else {
								var zyhm = this.form.getForm()
										.findField("ZYHM");
								zyhm.setDisabled(true);
								// btn[0].disable();
								// btn1[0].disable();
								// btn2[0].disable();
							}
						}
					},
					onReady : function() {
						var zyhm = this.form.getForm().findField("ZYHM");
						var ysdm = this.form.getForm().findField("YSDM");
						var ksdm = this.form.getForm().findField("KSDM");
						ksdm.setDisabled(false);
						ysdm.setDisabled(false);
						zyhm.setDisabled(false);

					},
					loadData : function() {
						var toolBar = this.panel.getTopToolbar();
						var btn = toolBar.find("cmd", "add");
						var btn1 = toolBar.find("cmd", "delete");
						var btn2 = toolBar.find("cmd", "clear");
						var btn3 = toolBar.find("cmd", "save");
						btn[0].enable();
						btn1[0].enable();
						btn2[0].enable();
						if (this.param.XZPB != 1) {
							if (this.param.TYPE == 1) { // 病区提交的检查不能修改
								var zyhm = this.form.getForm()
										.findField("ZYHM");
								var ysdm = this.form.getForm()
										.findField("YSDM");
								var ksdm = this.form.getForm()
										.findField("KSDM");
								ksdm.setDisabled(true);
								ysdm.setDisabled(true);
								zyhm.setDisabled(true);
								btn3[0].disable();
							} else {
								// btn[0].disable();
								// btn1[0].disable();
								// btn2[0].disable();
							}
						}
						this.exContext.args["listRequestData"] = {
							serviceAction : "getJCEditList",
							yjxh : this.param.YJXH,
							jgid : this.mainApp['phisApp'].deptId
						}
						this.exContext.args["formRequestData"] = {
							yjxh : this.param.YJXH,
							jgid : this.mainApp['phisApp'].deptId,
							zxks : this.mainApp['phis'].MedicalId
						};
						if (this.midiModules["JCListForm"]) {
							this.midiModules["JCListForm"].exContext = this.exContext;
							this.midiModules["JCListForm"].XZPB = 0;
							this.midiModules["JCListForm"].loadData();
						}
						if (this.midiModules["JCProjectEditorList"]) {
							Ext
									.apply(
											this.midiModules["JCProjectEditorList"].requestData,
											this.exContext.args["listRequestData"]);
							this.midiModules["JCProjectEditorList"].loadData();
						}
					},
					doAdd : function() {
						if (this.formModule.data.ZYHM
								|| this.formModule.queryData) {
							this.listModule.doCreate();
						}
					},
					doClear : function() {
						this.formModule.queryData = false;
						this.formModule.doNew()
						this.listModule.doNew();
					}
				});