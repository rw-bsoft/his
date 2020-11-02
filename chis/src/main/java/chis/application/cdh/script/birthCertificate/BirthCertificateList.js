/**
 * 儿童出生医学证明列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.birthCertificate")
$import("chis.script.BizSimpleListView",
		"chis.application.cdh.script.birthCertificate.BirthCertificateForm");
chis.application.cdh.script.birthCertificate.BirthCertificateList = function(cfg) {
	chis.application.cdh.script.birthCertificate.BirthCertificateList.superclass.constructor
			.apply(this, [cfg]);
	this.on("save", this.onSave)
};
Ext.extend(chis.application.cdh.script.birthCertificate.BirthCertificateList,
		chis.script.BizSimpleListView, {

			doCreateByEmpi : function(item, e) {
				var m = this.midiModules["Birthcertificate"];
				if (!m) {
					$import("chis.application.cdh.script.base.ChildInfoForm")
					m = new chis.application.cdh.script.base.ChildInfoForm({
								entryName : "chis.application.mpi.schemas.MPI_ChildBaseInfo",
								title : "儿童基本信息查询",
								height : 450,
								width : 780,
								wi:121,
								modal : true,
								mainApp : this.mainApp,
								isDeadRegist : false
							})
					m.on("save", this.onEmpiSelected, this);
					this.midiModules["Birthcertificate"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			doRemove : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.id + ']',
							msg : r.get("personName")
									+ '的出生医学证明记录将删除，是否确定要删除该档案?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									Ext.Msg.show({
												title : '确认删除记录[' + r.id + ']',
												msg : '删除操作将无法恢复，是否继续?',
												modal : true,
												width : 300,
												buttons : Ext.MessageBox.OKCANCEL,
												multiline : false,
												fn : function(btn, text) {
													if (btn == "ok") {
														this.processRemove();
													}
												},
												scope : this
											})
								}
							},
							scope : this
						})
			},

			onEmpiSelected : function(entryName, op, json, data) {
				this.empiId = data.empiId;
				this.showModule();

			},

			showModule : function(empiId) {
				var module = this.createSimpleModule(this.refModule,
						this.refModule);
				module.on("save", this.onSave, this)
				module.empiId = empiId || this.empiId;
				var data = this.getFormControl();
				var control = data["_actions"];
				if (control) {
					Ext.apply(module.exContext.control, control);
				} else {
					module.exContext.control = {};
				}
				var win = module.getWin();
				win.show();
			},

			getFormControl : function() {
				this.grid.el.mask("正在获取操作权限...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "checkBirthCertificateControl",
							method:"execute",
							body : {
								"empiId" : this.empiId
							}
						})
				this.grid.el.unmask();
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				}
				return result.json.body;
			},

			doModify : function(item, e) {
				var r = this.grid.getSelectionModel().getSelected()
				this.empiId = r.get("empiId");
				this.showModule(this.empiId);
			},

			onDblClick : function(grid, index, e) {
				this.doModify()
			},

			onSave : function(entryName, op, json, rec) {
				this.refresh()
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var empiId = r.get("empiId");
				this.grid.el.mask("正在获取操作权限...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "checkBirthCertificateDelete",
							method:"execute",
							body : {
								"empiId" : empiId
							}
						})
				this.grid.el.unmask();
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return;
				} else {
					var canDelete = result.json.body.canDelete;
					if (canDelete) {
						chis.application.cdh.script.birthCertificate.BirthCertificateList.superclass.doRemove
								.call(this);
					} else {
						var personName = r.get("personName");
						Ext.MessageBox.alert("提示", "[" + personName
										+ "]的儿童档案已经注销，无法删除出生医学证明记录！");
					}
				}
			}

		});
