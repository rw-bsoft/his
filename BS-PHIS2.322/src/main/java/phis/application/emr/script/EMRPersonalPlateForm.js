$package("phis.application.emr.script")

$import("phis.script.SimpleForm")
phis.application.emr.script.EMRPersonalPlateForm = function(cfg) {
	cfg.colCount = 1;
	cfg.remoteUrl = "Disease";
	cfg.minListWidth = 80;
	cfg.remoteTpl = '<td width="14px" style="background-color:#deecfd">{numKey}.</td><td width="170px">{JBMC}</td>';
	phis.application.emr.script.EMRPersonalPlateForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.emr.script.EMRPersonalPlateForm,
		phis.script.SimpleForm, {
			onReady : function() {
				phis.application.emr.script.EMRPersonalPlateForm.superclass.onReady
						.call(this);
				this.firstLoad = false;
				var combox = this.form.getForm().findField("SPTTYPE");
				combox.getStore().on("load", this.ptNameLoad, this);
				combox.store.load()
				this.ready = true;
			},
			ptNameLoad : function(store) {
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("key") == 0) {
						r
								.set(
										"text",
										r.get("text")
												+ "("
												+ (this.openBy == 'clinic'
														? this.mainApp['phis'].departmentName
														: this.exContext.empiData.BRKS_text)
												+ ")");
					} else {
						r.set("text", r.get("text") + "(" + this.mainApp.uname
										+ ")");
					}
					if (!this.firstLoad) {
						this.form.getForm().findField("SPTTYPE").setValue("1");
						this.firstLoad = true;
					}
				}
			},
			onWinShow : function() {
				if (this.form && this.ready) {
					this.form.getForm().findField("DISEASEID").setValue("");
					this.form.getForm().findField("PTNAME").setValue("");
					var combox = this.form.getForm().findField("SPTTYPE")
							.setValue("1");
				}
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中，继承后实现具体功能
				obj.setValue(record.get("JBMC"));
				obj.collapse();
				this.JBXH = record.get("JBXH");
				this.form.getForm().findField("PTNAME")
						.setValue(this.parent.node.MBMC + "("
								+ record.get("JBMC") + ")");
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'JBMC'
								}, {
									name : 'ICD10'
								}]);
			},
			doSave : function() {
				var body = this.getFormData();
				var PTNAME = body.PTNAME;
				if (!PTNAME) {
					return;
				}
				var flag = false;
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrManageService",
							serviceAction : "checkHasPersonalName",
							PTNAME : PTNAME
						});
				if (result.code > 200) {
					MyMessageTip.msg("提示", result.msg, true);
					return;
				}
				if (result.json.flag) {
					MyMessageTip.msg("提示", "当前个人模版名称已存在!", true);
					return;
				}
				if (this.parent.node.BLLX == 1) {
					this.parent.emr.FunActiveXInterface('BsSaveAsPrivateData',
							"1", "element..Illrc_1_" + this.BLBH);
				} else {
					this.parent.emr.FunActiveXInterface('BsSaveAsPrivateData',
							"0", "");
				}
				var body = this.getFormData();
				body.PTTEMPLATE = this.parent.emr.WordXML;
				body.PTTEMPLATETEXT = this.parent.emr.WordData;
				body.FRAMEWORKCODE = this.parent.node.key;
				body.TEMPLATETYPE = this.parent.bl01.MBLB;
				body.TEMPLATECODE = this.parent.bl01.MBBH;
				body.DISEASEID = this.JBXH;
				var spttype = this.form.getForm().findField("SPTTYPE")
						.getValue();
				body.PTTYPE = this.parent.node.BLLX;
				if (spttype == 1) {
					body.SPTCODE = this.mainApp.uid;
				} else {
					body.SPTCODE = (this.openBy == 'clinic'
							? this.mainApp['phis'].departmentId
							: this.exContext.empiData.BRKS)
				}
				phis.script.rmi.jsonRequest({
							serviceId : "emrManageService",
							serviceAction : "saveAsPrivatePlate",
							body : body
						}, function(code, msg, json) {
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							MyMessageTip.msg("提示", "当前病历/病程文档已经成功另存为病历模板!",
									true);
							this.doClose();
						}, this);
			},
			doClose : function() {
				this.win.hide();
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
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					// win.on("close", function() {
					// this.fireEvent("close", this);
					// }, this);
					// win.on("beforeclose", function() {
					// return this.fireEvent("beforeclose", this);
					// }, this);
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