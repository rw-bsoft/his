/**
 * 修改各档责任医生整合页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.manage")
$import("chis.script.BizCombinedModule2")
chis.application.mov.script.manage.ManaInfoChangeModule = function(cfg) {
	chis.application.mov.script.manage.ManaInfoChangeModule.superclass.constructor.apply(this,
			[cfg]);
	this.layOutRegion = "north"
	this.width = 880
	this.height = 600
	this.itemHeight = 170
	this.on("beforeLoadModule", this.beforeLoadModule, this)
	this.on("beforeCreate", this.beforeCreate, this);
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(chis.application.mov.script.manage.ManaInfoChangeModule, chis.script.BizCombinedModule2,
		{

			initPanel : function() {
				var panel = chis.application.mov.script.manage.ManaInfoChangeModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.list = this.midiModules[this.actions[1].id];
				this.form.on("queryEmpiId", this.onQueryEmpiId, this);
				this.form.on("save", this.doSave, this);
				this.form.on("cancel", this.doCancel, this);
				this.form.on("confirm", this.doConfirm, this);
				this.form.on("reject", this.doReject, this);
				return panel;
			},

			beforeLoadModule : function(moduleName, cfg) {
				if (this.moduleName == "ManageApply"
						&& moduleName == this.actions[0].id) {
					cfg.entryName = "chis.application.mov.schemas.MOV_ManaInfoChangeApply";
				} else if (this.moduleName == "ManageConfirm"
						&& moduleName == this.actions[0].id) {
					cfg.entryName = "chis.application.mov.schemas.MOV_ManaInfoChangeConfirm";
				}

				if (moduleName == this.actions[1].id) {
					cfg.initCnd = ['eq', ['$', 'archiveMoveId'],
							['s', this.initDataId]]
				}
			},

			onQueryEmpiId : function(empiId) {
				this.list.clear();
				this.list.empiId = empiId;
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
							method:"execute",
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
									formData["_actions"] = control;
									this.form.initFormData(formData);
									Ext.apply(this.exContext.control, control);
									var listData = resBody.listData;
									this.refreshExContextData(this.list,
											this.exContext);
									this.list.empiId = formData.empiId;
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
				var listData = this.list.getSaveRecords();
				if (!listData || listData.length < 1) {
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
				var listData = this.list.getSelectRecords();
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
							method:"execute",
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
								id : this.id,
								title : this.name,
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
						// ** 设置 查找用户的控件可用
						this.form.setLookUpDisabled(false);
					} else if (this.actionName == "confirm") {
						// ** 隐藏确认，退回按钮
						this.form.setGroupBtnVisible("confirm", true);
						// ** 设置 查找用户的控件不可用
						this.form.setLookUpDisabled(true);
					}
				}
				if (this.list) {
					this.list.empiId = null;
					this.list.delRows = null;
					this.list.clearSelect();
				}
			}

		})