$package("phis.application.war.script")

$import("phis.script.TableForm")

phis.application.war.script.MedicalConditionForm = function(cfg) {
	cfg.colCount = 1;
	phis.application.war.script.MedicalConditionForm.superclass.constructor.apply(this,
			[cfg]);
	this.saveServiceId = "medicalRecordsQueryService";
	this.saveAction = "saveMedicalCondition";
	this.on("beforeSave",this.onBeforeSave,this);
}

Ext.extend(phis.application.war.script.MedicalConditionForm, phis.script.TableForm, {
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
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
			},
			getSaveRequest : function(saveData) {
				var initData = this.initData;
				if (initData) {
					initData.TJMC = saveData.TJMC;
				} else {
					initData = saveData;
				}
				var values = initData;
				return values;
			},
			onBeforeSave:function(entryName, op, saveRequest){
				var flag = true;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "medicalRecordsQueryService",
							serviceAction : "checkHasTJMC",
							TJMC : saveRequest.TJMC,
							schema : entryName
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return
				}
				if (r.json.body!=null) {
					flag = r.json.body;
				}
				if(!flag){
					MyMessageTip.msg("提示", "条件名称["+saveRequest.TJMC+"]已存在，请重新输入!", true);
				}
				return flag;
			}
		});