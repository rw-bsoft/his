$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.UserFulExpressionsForm = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 300;
	phis.application.cic.script.UserFulExpressionsForm.superclass.constructor.apply(
			this, [cfg]);
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("beforeclose", this.beforeFClose, this);
}

Ext.extend(phis.application.cic.script.UserFulExpressionsForm, phis.script.TableForm,
		{
			onBeforeSave : function(entryName, op, saveData) {
				var flag = true;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "userFulExpressionsService",
							serviceAction : "getCountByPTNAME",
							PTNAME : saveData.PTNAME,
							SPTTYPE : saveData.SPTTYPE,
							SPTCODE : this.mainApp.uid
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return
				}
				if (r.json.flag != null) {
					flag = r.json.flag;
				}
				return flag;
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					Ext.Msg.alert("提示", "常用语名称已存在，请重新输入常用语名称！");
					return;
				}
				this.beforeSaveRequest(saveData);
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "userFulExpressionsService",
							serviceAction : "saveUserFulExpressions",
							op : this.op,
							schema : this.entryName,
							body : saveData
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [saveData]);
								return
							}
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op,
										json, this.data)
							}
							this.op = "update"
							if (this.win) {
								this.win.hide();
							}
						}, this)
			},
			beforeSaveRequest : function(saveData) {
				saveData.PTTYPE = 3;
				saveData.PTTEMPLATE = this.xmlData;
				saveData.PTTEMPLATETEXT = this.txtData;
				saveData.PTSTATE = 0;
				saveData.FRAMEWORKCODE = "  ";
				saveData.TEMPLATETYPE = "  ";
				if(saveData.SPTTYPE==1){
					saveData.SPTCODE = this.mainApp.uid
				}else if(saveData.SPTTYPE==0){
					saveData.SPTCODE = this.KSDM;
				}else{
					saveData.SPTCODE = "all";
				}
			},
			beforeFClose : function() {
				this.opener.showEmr();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
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
								modal : this.modal || true,
								items : this.initPanel()
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
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
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
			}
		});