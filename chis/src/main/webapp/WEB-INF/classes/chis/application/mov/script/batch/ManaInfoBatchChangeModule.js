/**
 * 批量修改管理医生整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.batch")
$import("chis.script.BizCombinedModule2")
chis.application.mov.script.batch.ManaInfoBatchChangeModule = function(cfg) {
	chis.application.mov.script.batch.ManaInfoBatchChangeModule.superclass.constructor
			.apply(this, [cfg]);
	this.layOutRegion = "north"
	this.width = 750
	this.height = 600
	this.itemHeight = 195
	this.on("beforeLoadModule", this.beforeLoadModule, this)
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(chis.application.mov.script.batch.ManaInfoBatchChangeModule,
		chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.mov.script.batch.ManaInfoBatchChangeModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.list = this.midiModules[this.actions[1].id];
				this.form.on("save", this.doSave, this);
				this.form.on("cancel", this.doCancel, this);
				this.form.on("confirm", this.doConfirm, this);
				this.form.on("reject", this.doReject, this);
				return panel;
			},

			beforeLoadModule : function(moduleName, cfg) {
				if (this.moduleName == "EHRApply") {
					// ** 个档批量修改责任医生申请
					if (moduleName == this.actions[0].id) {
						cfg.entryName = "chis.application.mov.schemas.MOV_EHRManaInfoBatchChangeApply";
					} else {// ** 个人健康档案查询
						cfg.queryEntryName = "chis.application.mov.schemas.MOV_HealthRecordQuery";
						cfg.queryFrmName = "个人健康档案查询"
					}
				} else if (this.moduleName == "EHRConfirm") {
					// ** 个档批量修改责任医生申请确认
					if (moduleName == this.actions[0].id) {
						cfg.entryName = "chis.application.mov.schemas.MOV_EHRManaInfoBatchChangeConfirm";
					} else {// ** 个人健康档案查询
						cfg.queryEntryName = "chis.application.mov.schemas.MOV_HealthRecordQuery";
						cfg.queryFrmName = "个人健康档案查询";
					}
				} else if (this.moduleName == "CDHApply") {
					// ** 儿保批量修改责任医生申请
					if (moduleName == this.actions[0].id) {
						cfg.entryName = "chis.application.mov.schemas.MOV_CDHManaInfoBatchChangeApply";
					} else {// ** 儿童档案查询
						cfg.queryEntryName = "chis.application.mov.schemas.MOV_HealthCardQuery";
						cfg.queryFrmName = "儿童档案查询";
					}
				} else if (this.moduleName == "CDHConfirm") {
					// ** 儿保批量修改责任医生申请确认
					if (moduleName == this.actions[0].id) {
						cfg.entryName = "chis.application.mov.schemas.MOV_CDHManaInfoBatchChangeConfirm";
					} else {// ** 儿童档案查询
						cfg.queryEntryName = "chis.application.mov.schemas.MOV_HealthCardQuery";
						cfg.queryFrmName = "儿童档案查询";
					}
				} else if (this.moduleName == "MHCApply") {
					// ** 妇保批量修改责任医生申请
					if (moduleName == this.actions[0].id) {
						cfg.entryName = "chis.application.mov.schemas.MOV_MHCManaInfoBatchChangeApply";
					} else {// ** 孕妇档案查询
						cfg.queryEntryName = "chis.application.mov.schemas.MOV_PregnantRecordQuery";
						cfg.queryFrmName = "孕妇档案查询";
					}
				} else if (this.moduleName == "MHCConfirm") {
					// ** 妇保批量修改责任医生申请确认
					if (moduleName == this.actions[0].id) {
						cfg.entryName = "chis.application.mov.schemas.MOV_MHCManaInfoBatchChangeConfirm";
					} else {// ** 孕妇档案查询
						cfg.queryEntryName = "chis.application.mov.schemas.MOV_PregnantRecordQuery";
						cfg.queryFrmName = "孕妇档案查询";
					}
				}

				if (moduleName == this.actions[1].id) {
					cfg.initCnd = ['eq', ['$', 'archiveMoveId'],
							['s', this.initDataId]]
				}
			},

			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.initDataId) {
					this.op = "create";
					this.fireEvent("beforeCreate", this);
					return;
				} else {
					this.list.requestData.cnd = ['eq', ['$', 'archiveMoveId'],
							['s', this.initDataId]]
				}
				this.panel.el.mask("正在加载数据...")
				this.loading = true
				util.rmi.jsonRequest({
							serviceId : this.loadServiceId,
							serviceAction : this.loadAction,
							method : "execute",
							body : {
								pkey : this.initDataId
							}
						}, function(code, msg, json) {
							this.panel.el.unmask();
							this.loading = false
							if (code < 300) {
								var resBody = json.body;
								if (resBody) {
									this.op = "update";
									var control = resBody["_actions"];
									if (this.actionName == "apply") {
										// ** 确认按钮不可用
										control.confirm = false;
									} else if (this.actionName == "confirm") {
										// ** 申请按钮不可用
										control.apply = false;
									}
									var formData = resBody.formData;
									if (!formData) {
										return;
									}
									formData["_actions"] = control;
									this.form.doNew()
									this.form.initFormData(formData);
									Ext.apply(this.exContext.control, control);
									var listData = resBody.listData;
									if (!listData) {
										return;
									}
									this.refreshExContextData(this.list,
											this.exContext);
									this.list.initListData(listData);
								}
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},

			beforeCreate : function() {
				if (this.form) {
					this.form.doNew();
					this.form.setBtnApply();
				}
				if (this.list) {
					this.list.setBtnApply();
					this.list.clear();
				}
			},

			doSave : function() {
				var formData = this.form.getFormData();
				if (!formData) {
					Ext.MessageBox.alert("提示", "未填写修改信息！")
					return;
				}
				if (this.moduleName == "EHRApply") {
					formData.archiveType = "1";
				} else if (this.moduleName == "CDHApply") {
					formData.archiveType = "5";
				} else if (this.moduleName == "MHCApply") {
					formData.archiveType = "6";
				}
				var listData = this.list.getListData();
				if (!listData || listData.length < 1) {
					Ext.MessageBox.alert("提示", "未选择需要修改的档案！")
					return;
				}
				var delDetaiId = this.list.delRows;
				var body = {
					"formData" : formData,
					"listData" : listData,
					"delDetaiId" : delDetaiId
				}
				this.saveToServer("saveApplyRecord", body);
			},

			doConfirm : function() {
				var formData = this.form.getFormData();
				if (!formData) {
					Ext.MessageBox.alert("提示", "未填写修改信息！")
					return;
				}
				var listData = this.list.getSaveRecords();
				if (!listData || listData.length < 1) {
					Ext.MessageBox.alert("提示", "未选择确认修改的档案！")
					return;
				}
				var body = {
					"formData" : formData,
					"listData" : listData
				}
				this.saveToServer("saveConfirmRecord", body);
			},

			doReject : function() {
				var formData = this.form.getFormData();
				if (!formData) {
					Ext.MessageBox.alert("提示", "未填写修改信息！")
					return;
				}
				var delDetaiId = this.list.delRows;
				var body = {
					"formData" : formData
				}
				this.saveToServer("saveRejectRecord", body);
			},

			saveToServer : function(saveAction, body) {
				if (this.saving) {
					return
				}
				this.panel.el.mask("正在提交数据...")
				this.saving = true;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : saveAction,
							method : "execute",
							op : this.op,
							body : body
						}, function(code, msg, json) {
							this.panel.el.unmask();
							this.saving = false
							if (code < 300) {
								var resBody = json.body;
								if (resBody) {
									this.form.initFormData(resBody);
								}
								this.fireEvent("save", this);
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this);
			},

			doCancel : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			},

			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								title : this.title || this.name,
								constrain : true,
								width : this.width,
								height : this.height || 500,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								autoScroll : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					win.on("show", function() {
								this.win.doLayout();
								this.fireEvent("winShow", this);
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			},

			onWinShow : function() {
				this.loadData();
				if (this.form) {
					if (this.actionName == "apply") {
						// ** 隐藏确定按钮
						this.form.setGroupBtnVisible("apply", true);
					} else if (this.actionName == "confirm") {
						// ** 隐藏确认，退回按钮
						this.form.setGroupBtnVisible("confirm", true);
					}
				}
				if (this.list) {
					this.list.delRows = null;
					this.list.clearSelect();
				}
			}
		})