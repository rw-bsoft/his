$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicProjectComboUserModule = function(cfg) {
	phis.application.cic.script.ClinicProjectComboUserModule.superclass.constructor
			.apply(this, [ cfg ])
}
var sslbValue = 1;// 所属类别
var cflbValue = 1;// 处方类别
Ext
		.extend(
				phis.application.cic.script.ClinicProjectComboUserModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (!this.mainApp['phis'].departmentId) {
							MyMessageTip.msg("提示", "未设置当前科室!", true);
							return;
						}
						if (this.panel) {
							return this.panel;
						}
						var actions = this.actions;
						var barCY = [];
						var barAll = [];
						sslbValue = 1;// 所属类别
						cflbValue = 1;// 处方类别

						for ( var i = 0; i < 2; i++) {
							var ac = actions[i];
							var config = {
								boxLabel : ac.name,
								inputValue : ac.properties.value,
								name : "stackCY",
								clearCls : true
							}
							barCY.push(config)
						}
						for ( var i = 2; i < actions.length; i++) {
							var ac = actions[i];
							var config = {
								boxLabel : ac.name,
								inputValue : ac.properties.value,
								name : "stackAll",
								clearCls : true
							}
							barAll.push(config)
						}

						// 西药 中药 草药 其他
						var radioGroupCY = new Ext.form.RadioGroup(
								{
									width : 200,
									disabled : false,
									items : barCY,
									listeners : {
										change : function(group, newValue,
												oldValue) {
											if (this.projectComboUseList) {
												cflbValue = parseInt(newValue.inputValue);
												this.projectComboUseList.SSLB = sslbValue;
												if (this.projectComboUseList) {
													this.projectComboUseList.CFLX = cflbValue;
												}
												this.projectComboUseList
														.loadData();
											}
											if (this.clinicAllList) {
												this.clinicAllList.CFLX = cflbValue;
											}
										},
										scope : this
									}
								});

						// 西药 中药 草药 其他
						var radioGroupAll = new Ext.form.RadioGroup(
								{
									width : 200,
									disabled : false,
									items : barAll,
									listeners : {
										change : function(group, newValue,
												oldValue) {
											if (this.projectComboUseList) {
												sslbValue = parseInt(newValue.inputValue);
												this.projectComboUseList.SSLB = sslbValue;
												if (this.projectComboUseList) {
													this.projectComboUseList.CFLX = cflbValue;
												}
												this.projectComboUseList
														.loadData();
											}
											if (this.clinicAllList) {
												this.clinicAllList.CFLX = cflbValue;
											}
										},
										scope : this
									}
								});

						radioGroupCY.setValue(1);// 设置默认值
						radioGroupAll.setValue(1);// 设置默认值

						var tbar = [ '', radioGroupCY, '->', radioGroupAll ];

						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : tbar,
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								region : 'west',
								width : 480,
								items : this.getProjectComboUseList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
								items : this.getClinicAllList()
							} ]
						});
						this.panel = panel;
						this.panel.on("beforeclose", this.onBeforeBusSelect,
								this);
						return panel;
					},
					onBeforeBusSelect : function() {
						if (this.isModify) {
							if (confirm('数据已修改，是否保存?')) {
								return this.projectComboUseList.doSave();
							} else {
								return true;
							}
						}
						return true;
					},
					// 左边列表
					getProjectComboUseList : function() {
						this.projectComboUseList = this.createModule(
								"getProjectComboUseList",
								this.refProjectComboUseList);
						// 左边双击事件
						var _ctx = this;
						this.projectComboUseList.opener = this;
						this.projectComboUseList.CFLX = cflbValue;
						this.projectComboUseList.onDblClick = function() {
							var r = this.getSelectedRecord();
							_ctx.isModify = true;
							if (r) {
								Ext.each(r, this.grid.store.remove,
										this.grid.store);
								r.set("JBMC", r.get("ZDMC"));
								_ctx.clinicAllList.store.add(r);
								_ctx.clinicAllList.store.sort('JBXH', 'ASC');
							}
						}
						this.projectComboUseList.on("loadData",
								this.useListLoadData, this);
						// 保存
						this.projectComboUseList.doSave = function() {
							_ctx.panel.el.mask();
							var body = [];
							var store = _ctx.projectComboUseList.grid
									.getStore();
							for ( var i = 0; i < store.getCount(); i++) {
								var r = store.getAt(i);
								body.push(r.data);
							}
							var resData = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicProjectComboUserService",
								serviceAction : "saveComboUser",
								body : body,
								CFLX : cflbValue
							});
							var code = resData.code;
							var msg = resData.msg;
							var json = resData.json;
							_ctx.panel.el.unmask();
							if (code < 300) {
								MyMessageTip.msg("提示", "保存成功!", true);
								_ctx.isModify = false;
								_ctx.projectComboUseList.refresh();
								return true;
							} else {
								_ctx.processReturnMsg(code, msg)
								return false;
							}
						}
						return _ctx.projectComboUseList.initPanel();
					},
					useListLoadData : function(store) {
						this.clinicAllList.loadData();
					},
					getClinicAllList : function() {
						this.clinicAllList = this.createModule(
								"getClinicAllList", this.refClinicAllList);
						var _ctx = this;
						this.clinicAllList.CFLX = cflbValue;
						this.clinicAllList.onDblClick = function() {
							var r = this.getSelectedRecord();
							_ctx.isModify = true;
							if (r) {
								Ext.each(r, this.grid.store.remove,
										this.grid.store);
								r.set("ZDMC", r.get("JBMC"));
								_ctx.projectComboUseList.store.add(r);
								_ctx.projectComboUseList.store.sort('JBXH',
										'ASC');
							}
						}
						this.clinicAllList.on("loadData", this.allListLoadData,
								this);
						return this.clinicAllList.initPanel();

					},
					allListLoadData : function(store) {
						store
								.filterBy(
										function(f_record, id) {
											var count = this.projectComboUseList.grid.store
													.getCount();
											for ( var i = 0; i < count; i++) {
												if (f_record.data.ICD10 == this.projectComboUseList.store
														.getAt(i).data.ICD10)
													return false;
											}
											return true;
										}, this);
					},
					afterOpen : function() {
						// 拖动操作
						var _ctx = this;
						var firstGrid = this.projectComboUseList.grid;
						var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
						var firstGridDropTarget = new Ext.dd.DropTarget(
								firstGridDropTargetEl,
								{
									ddGroup : 'refClinicAllList',
									notifyDrop : function(ddSource, e, data) {
										_ctx.isModify = true;
										var records = ddSource.dragData.selections;
										Ext.each(records,
												ddSource.grid.store.remove,
												ddSource.grid.store);
										for ( var i = 0; i < records.length; i++) {
											var record = records[i];
											record.set("ZDMC", record
													.get("JBMC"));
										}
										firstGrid.store.add(records);
										firstGrid.store.sort('JBXH', 'ASC');
										return true
									}
								});
						var secondGrid = this.clinicAllList.grid;
						var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
						var secondGridDropTarget = new Ext.dd.DropTarget(
								secondGridDropTargetEl,
								{
									ddGroup : 'refProjectComboUseList',
									notifyDrop : function(ddSource, e, data) {
										_ctx.isModify = true;
										var records = ddSource.dragData.selections;
										Ext.each(records,
												ddSource.grid.store.remove,
												ddSource.grid.store);
										for ( var i = 0; i < records.length; i++) {
											var record = records[i];
											record.set("JBMC", record
													.get("ZDMC"));
										}
										_ctx.clinicAllList.store.add(records);
										_ctx.clinicAllList.store.sort('JBXH',
												'ASC');
										return true
									}
								});
					}
				})
