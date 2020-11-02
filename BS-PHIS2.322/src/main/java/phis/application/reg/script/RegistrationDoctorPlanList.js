/**
 * 医生排班功能
 * 
 * @author liyl
 */
$package("phis.application.reg.script");

$import("phis.script.EditorList");

phis.application.reg.script.RegistrationDoctorPlanList = function(cfg) {
	// cfg.gridDDGroup = "secondRegDDGroup";
	phis.application.reg.script.RegistrationDoctorPlanList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.reg.script.RegistrationDoctorPlanList,
		phis.script.EditorList, {
			expansion : function(cfg) {
				cfg.sm = new Ext.grid.RowSelectionModel({
							singleSelect : true
						});
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([tbar[0], tbar[1], '->', tbar[2], tbar[3]]);
			},
			doSave : function() {
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("GHXE") == null) {
						r.data.GHXE = 0;

					}
					if (r.get("YYXE") == null) {
						r.data.YYXE = 0;
					}
					r.data['_opStatus'] = 'create';
					data.push(r.data)
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionSave,
							body : data
						}, function(code, msg, json) {

							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.loadData();
						}, this)
				this.store.rejectChanges();
			},
			doRemove : function() {
				// var cm = this.grid.getSelectionModel();
				// var cell = cm.getSelectedCell();
				var lastIndex = this.grid.getSelectionModel().lastActive;
				var r = this.grid.store.getAt(lastIndex);
				if (r == null) {
					return
				}
				var date = {};
				date["KSDM"] = r.get("KSDM");
				date["YSDM"] = r.get("YSDM");
				date["ZBLB"] = r.get("ZBLB");
				date["GZRQ"] = r.get("GZRQ");
				date["JGID"] = r.get("JGID");
				Ext.Msg.confirm("请确认", "确认删除" + r.get("KSDM_text") + "【"
								+ r.data.YSDM_text + "】医生的排班吗？", function(btn) {
							if (btn == 'yes') {
								this.grid.el
										.mask("正在删除数据...", "x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : this.removeAction,
											body : date
										}, function(code, msg, json) {
											this.grid.el.unmask()
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												if (code != 604) {
													return;
												}
											}
											this.store.remove(r);
											// 移除之后焦点定位
											var count = this.store.getCount();
											if (count > 0) {
												var lastIndex = this.grid
														.getSelectionModel().lastActive;
												this.selectRow(lastIndex);
											}
											this.fireEvent("afterRemove",
													this.grid);
										}, this)
							}
						}, this);
				return
			},
			doMorning : function() {
				this.opener.onToggleHandler("morning", 1);
			},
			doAfternoon : function() {
				this.opener.onToggleHandler("afternoon", 1);
			}
		});
