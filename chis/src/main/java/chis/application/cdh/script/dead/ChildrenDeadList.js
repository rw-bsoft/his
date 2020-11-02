/**
 * 儿童死亡登记列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.dead")
$import("chis.script.BizSimpleListView")
chis.application.cdh.script.dead.ChildrenDeadList = function(cfg) {
	cfg.isCombined = true;
	chis.application.cdh.script.dead.ChildrenDeadList.superclass.constructor.apply(this, [cfg]);
	this.removeServiceAction = "removeChildrenDeadRecord"
	this.checkServiceAction = "checkDeadRecordExistsByEmpiId"
}

Ext.extend(chis.application.cdh.script.dead.ChildrenDeadList, chis.script.BizSimpleListView, {

			getStore : function(items) {
				if (this.mainApp.jobtitleId == "chis.01") {
					this.requestData.schema = "chis.application.cdh.schemas.CDH_DeadRegister_Module";
				}
				return chis.application.cdh.script.dead.ChildrenDeadList.superclass.getStore
						.call(this, items);
			},

			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var phrId = r.get("phrId");
				if (phrId) {
					Ext.Msg.alert("提示信息", "该用户已经存在档案，无法删除死亡记录，请走注销恢复流程!");
					return;
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.id + ']',
							msg : r.get("childName") + '的儿童死亡记录将删除，是否确定要删除该档案?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.YESNO,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "yes") {
									this.processRemove();
								}
							},
							scope : this
						})

			},

			doCreateNh : function(item, e) {
				this.showForm(this.noRecordCreateRef, false);
			},

			doCreateDead : function(item, e) {
				var m = this.midiModules["ChildInfoForm"];
				if (!m) {
					$import("chis.application.cdh.script.base.ChildInfoForm")
					m = new chis.application.cdh.script.base.ChildInfoForm({
								entryName : "chis.application.mpi.schemas.MPI_ChildBaseInfo",
								title : "儿童基本信息查询",
								height : 450,
								width : 780,
								modal : true,
								mainApp : this.mainApp,
								isDeadRegist : true
							})
					m.on("save", this.onAddDead, this)
					this.midiModules["ChildInfoForm"] = m;
				}
				var win = m.getWin();
				win.setPosition(250, 100);
				win.show();
			},

			onAddDead : function(entryName, op, json, data) {
				var empiId = data.empiId;
				this.empiId = empiId;
				util.rmi.jsonRequest({
							serviceId : this.saveServiceId,
							serviceAction : this.checkServiceAction,
							method:"execute",
							body : {
								"empiId" : this.empiId
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							var body = json.body;
							if (body) {
								var hasDeadRecord = body.hasDeadRecord
								if (hasDeadRecord) {
									this.initDataId = body.deadRegisterId;
									this.showForm( this.recordCreateRef, true);
								} else {
									var hasHealthCard = body.hasHealthCard;
									if (hasHealthCard) {
										this.initDataId = null;
										this.showForm(this.recordCreateRef, true)
									} else {
										Ext.Msg.alert("提示信息", "该用户未建档！");
									}
								}
							}
						}, this)
			},

			doModify : function(item, e) {
				var r = this.getSelectedRecord();
				if (!r) {
					return;
				}
				this.initDataId = r.id
				this.empiId = r.get("empiId");
				this.showForm(this.recordCreateRef, true);
			},

			showForm : function(ref, checkControl) {
				var module = this.createSimpleModule(ref, ref);
				module.on("save", this.refresh, this);
				module.empiId = this.empiId;
				module.initDataId = this.initDataId;
				this.showFormWin(module, checkControl);
			},

			showFormWin : function(module, checkControl) {
				if (checkControl) {
					var data = this.getFormControl();
					if (data) {
						var control = data["_actions"];
						if (!control) {
							return;
						}
						Ext.apply(module.exContext.control, control);
					}
					this.showWin(module);
				} else {
					this.showWin(module);
				}
			},

			getFormControl : function() {
				this.grid.el.mask("正在获取操作权限...", "x-mask-loading")
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : "checkDeadControl",
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

			onDblClick : function(grid, index, e) {
				var item = {
					"text" : "查看"
				};
				this.doModify(item);
			}
		});