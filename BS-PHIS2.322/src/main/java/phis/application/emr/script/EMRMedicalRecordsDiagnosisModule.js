$package("phis.application.emr.script");

$import("phis.script.SimpleModule");

phis.application.emr.script.EMRMedicalRecordsDiagnosisModule = function(cfg) {
	this.exContext = {};
	phis.application.emr.script.EMRMedicalRecordsDiagnosisModule.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.emr.script.EMRMedicalRecordsDiagnosisModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 500,
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										
										items : [this.getMainList()]
									}, {
										title : "常用诊断",
										layout : "fit",
										border : false,
										split : true,
										region : 'east',
										width : 300,
										collapsible : true,
										collapsed : true,
										items : this.getAppendList()
									}]
						});
//				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			refresh : function(){
				this.leftList.refresh();
			},
			getMainList : function() {
				this.leftList = this.createModule("refLeft", this.refLeft);
				this.leftList.exContext = this.exContext;
				this.leftList.opener = this.opener
//				var _ctx = this;
//				this.mainList.onRowClick = function() {
//					_ctx.onListRowClick();
//				};
//				this.mainList.on("loadData", this.onListLoadData, this);
				var grid = this.leftList.initPanel();
				this.grid = grid;
				return grid;
			},
			getAppendList : function() {
				this.rightList = this.createModule("refRight",
						this.refRight);
				this.rightList.requestData.serviceId="phis.emrMedicalRecordsService";
				this.rightList.requestData.serviceAction = "loadCommon";
				this.rightList.on("commit", this.leftList.backFill, this.leftList)
				return this.rightList.initPanel();
			}
//			onListLoadData : function(store) {
//				// 如果第一次打开页面，默认选中第一行
//				this.mainList.selectRow(0);
//				this.onListRowClick();
//			},
//			onListRowClick : function() {
//				this.beforeclose();
//				var r = this.mainList.getSelectedRecord();
//				if (!r) {
//					return;
//				}
//				this.appendList.XMXH = r.get("FYXH");
//				this.appendList.requestData.cnd = ['and',
//						['eq', ['$', 'a.XMXH'], ['d', r.get("FYXH")]],
//						['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]]
//				this.appendList.loadData();
//			},
//			beforeclose : function() {
//				this.appendList.removeEmptyRecord();
//				if (this.appendList.removeRecords.length > 0) {
//					if (confirm('附加项目数据已经修改，是否保存?')) {
//						return this.appendList.doSave()
//					} else {
//						this.appendList.store.rejectChanges();
//						this.appendList.removeRecords = [];
//						return true;
//					}
//				}
//				var rs = this.appendList.store.getModifiedRecords();
//				for (var i = 0; i < rs.length; i++) {
//					if (rs[i].get("GLXH")) {
//						if (confirm('附加项目数据已经修改，是否保存?')) {
//							return this.appendList.doSave()
//						} else {
//							this.appendList.store.rejectChanges();
//							this.appendList.removeRecords = [];
//							return true;
//						}
//					}
//				}
//			},
//			// 关闭
//			doClose : function() {
//
//			}
		});
