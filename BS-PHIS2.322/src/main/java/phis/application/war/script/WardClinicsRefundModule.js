$package("phis.application.war.script")

$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory")
phis.application.war.script.WardClinicsRefundModule = function(cfg) {
	phis.application.war.script.WardClinicsRefundModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.war.script.WardClinicsRefundModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'east',
										width : "50%",
										items : this.getRefundList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getChargeList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 80,
										items : this.getForm()
									}]
						});
				this.panel = panel;
				var FYSL = this.unusedList.grid.getColumnModel()
						.getColumnById("FYSL").editor;
				FYSL.on("change", this.beforeChange, this);
				this.usedCollections = new Ext.util.MixedCollection();
				return panel;
			},
			getForm : function() {
				this.baseForm = this.createModule("refBaseForm",
						this.refBaseForm);
				this.baseForm.opener = this;
				return this.baseForm.initPanel();
			},
			getChargeList : function() {
				this.usedList = this.createModule("refChargeList",
						this.refUsedList);

				var _ctx = this;
				this.usedList.onDblClick = function(grid, index, e) {
					_ctx.isModify = true;
					var records = grid.getSelectionModel().getSelected();
					// _ctx.changeRecords("remove", records)
					// Ext.each(records, grid.store.remove, grid.store);
					Ext.each(records, function(r) {

								var rd = this.hasRecord(this.unusedList, r);
								if (rd) {
									rd
											.set(
													"FYSL",
													parseFloat(rd.get("FYSL"))
															+ parseFloat(r
																	.get("FYSL") > 1
																	? 1
																	: r
																			.get("FYSL")))
								} else {
									var newRd = r.copy();
									newRd.set("TFRQ", Date.getServerDate());
									newRd.set("TFYS", this.mainApp.uname);
									newRd.set("FYSL", parseFloat(r.get("FYSL"))>1?1:parseFloat(r.get("FYSL")));
									this.unusedList.grid.store.add(newRd);
								}
								if (r.get("FYSL") > 1) {
									r.set("FYSL", r.get("FYSL") - 1);
								} else {
									this.usedList.store.remove(r);
								}
							}, _ctx)
					// _ctx.unusedList.grid.store.add(records);
					_ctx.unusedList.grid.store.sort('FYRQ,FYMC', 'ASC');
					_ctx.unusedList.grid.getView().refresh()// 刷新行号
					_ctx.usedList.grid.getView().refresh()// 刷新行号
				}
				this.usedList.on("loadData", this.onUsedListLoadData, this);
				return this.usedList.initPanel();
			},
			getRefundList : function() {
				this.unusedList = this.createModule("refRefundList",
						this.refUnUsedList);
				var _ctx = this;
				// this.unusedList.on("afterCellEdit", this.afterCellEdit,
				// this);
				this.unusedList.requestData.initCnd = ['eq', ['$', 'JGID'],
						['s', this.mainApp['phisApp'].deptId]]
				this.unusedList.requestData.cnd = ['eq', ['$', 'JGID'],
						['s', this.mainApp['phisApp'].deptId]];
				this.unusedList.onDblClick = function(grid, index, e) {
					_ctx.isModify = true;
					var records = grid.getSelectionModel().getSelected();
					// _ctx.changeRecords("add", records)
					Ext.each(records, function(r) {

								var rd = this.hasRecord(this.usedList, r);
								if (rd) {
									rd
											.set(
													"FYSL",
													parseFloat(rd.get("FYSL"))
															+ parseFloat(r
																	.get("FYSL") > 1
																	? 1
																	: r
																			.get("FYSL")))
								} else {
									var newRd = r.copy();
									newRd.set("TFRQ", Date.getServerDate());
									newRd.set("TFYS", this.mainApp.uname);
									newRd.set("FYSL", parseFloat(r.get("FYSL"))>1?1:parseFloat(r.get("FYSL")));
									this.usedList.grid.store.add(newRd);
								}
								if (r.get("FYSL") > 1) {
									r.set("FYSL", r.get("FYSL") - 1);
								} else {
									this.unusedList.store.remove(r);
								}
							}, _ctx)
					_ctx.usedList.grid.store.sort('FYRQ,FYMC', 'ASC');
					_ctx.usedList.grid.getView().refresh()// 刷新行号
					_ctx.unusedList.grid.getView().refresh()// 刷新行号
				}
				return this.unusedList.initPanel();
			},
			// taskRun : function() {
			// ymPrompt.alert({
			// _mId : this.fullId,// 模块Id
			// hashcode : 123123,
			// message : "病区退费提示",
			// title : "病区退费" + "-" + Date.getServerDateTime(),
			// winPos : 'rb',
			// showMask : false,
			// useSlide : true
			//						})
			//			},
			beforeChange : function(f, v, lastValue) {
				var record = this.unusedList.grid.getSelectionModel()
						.getSelected();
				if (v < 0) {
					MyMessageTip.msg("提示", "退费数量必须大于0，请重新输入!", true);
					f.setValue(lastValue);
					record.set("FYSL", lastValue);
					return;
				}

				var rd = this.hasRecord(this.usedList, record);
				if (!rd) {
					if (v - lastValue > 0) {
						MyMessageTip.msg("提示", "退费数量超过可退的最大值，请重新输入1!", true);
						f.setValue(lastValue);
						record.set("FYSL", lastValue);
						return;
					}
					rd = record.copy();
					rd.set("TFRQ", Date.getServerDate());
					rd.set("TFYS", this.mainApp.uname);
					rd.set("FYSL", 0);
					this.usedList.grid.store.add(rd);
				}
				if (v - lastValue > rd.get("FYSL")) {
					MyMessageTip.msg("提示", "退费数量超过可退的最大值，请重新输入2!", true);
					record.set("FYSL", lastValue);
					f.setValue(lastValue);
					return;
				}
				rd.set("FYSL", rd.get("FYSL") + parseFloat(lastValue - v));
				if (rd.get("FYSL") == 0) {
					this.usedList.store.remove(rd);
				}
				if (v == 0) {
					this.unusedList.store.remove(record);
				}
			},
			hasRecord : function(list, r) {
				for (var i = 0; i < list.store.getCount(); i++) {
					var rd = list.store.getAt(i);
					if (r.get("FYRQ") == rd.get("FYRQ")
							&& r.get("FYXH") == rd.get("FYXH")
							&& r.get("FYDJ") == rd.get("FYDJ")
							&& r.get("YSXM") == rd.get("YSXM")) {
						return rd;
					}
				}
				return null;
			},
			onUsedListLoadData : function(store) {
				this.unusedList.store.removeAll();
			},
			afterOpen : function() {
				// 拖动操作
				var _ctx = this;
				var firstGrid = this.usedList.grid;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								Ext.each(records, function(r) {
									var rd = this.hasRecord(this.usedList, r);
									if (rd) {
										rd.set("FYSL", parseFloat(rd
														.get("FYSL"))
														+ parseFloat(r
																.get("FYSL")))
									} else {
										var newRd = r.copy();
										newRd.set("TFRQ", Date.getServerDate());
										newRd.set("TFYS", this.mainApp.uname);
										firstGrid.store.add(newRd);
									}
								}, _ctx);
								// firstGrid.store.add(records);
								secondGrid.store.sort('FYRQ,FYMC', 'ASC');
								firstGrid.getView().refresh()// 刷新行号
								secondGrid.getView().refresh()// 刷新行号
								return true
							}
						});
				var secondGrid = this.unusedList.grid;
				var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
				var secondGridDropTarget = new Ext.dd.DropTarget(
						secondGridDropTargetEl, {
							ddGroup : 'secondGridGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								// _ctx.changeRecords("remove", records)
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								Ext.each(records, function(r) {
									var rd = this.hasRecord(this.unusedList, r);
									if (rd) {
										rd.set("FYSL", parseFloat(rd
														.get("FYSL"))
														+ parseFloat(r
																.get("FYSL")))
									} else {
										var newRd = r.copy();
										newRd.set("TFRQ", Date.getServerDate());
										newRd.set("TFYS", this.mainApp.uname);
										secondGrid.store.add(newRd);
									}
								}, _ctx);
								// secondGrid.store.add(records);
								firstGrid.store.sort('FYRQ,FYMC', 'ASC');
								firstGrid.getView().refresh()// 刷新行号
								secondGrid.getView().refresh()// 刷新行号
								return true
							}
						});
			},
			beforeClose : function() {
				if (this.isModify && this.unusedList.store.getCount() > 0) {
					if (confirm('当前数据已修改，是否保存?')) {
						return this.doSave()
					} else {
						return true;
					}
				}
				return true;
			},
			doSave : function() {
				var data = [];
				for (var i = 0; i < this.unusedList.store.getCount(); i++) {
					data.push(this.unusedList.store.getAt(i).data);
				}
				if (data.length == 0) {
					MyMessageTip.msg("提示", "没有需要保存的数据!", true);
					return;
				}
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wardPatientManageService",
							serviceAction : "saveRefundClinic",
							body : data
						});
				if (res.code > 200) {
					this.processReturnMsg(res.code, res.msg)
					return false;
				}
				this.isModify = false;
				MyMessageTip.msg("提示", "保存成功!", true);
				this.doRefresh();
				return true;
			},
			doRefresh : function() {
				this.baseForm.loadData();
				this.usedList.loadData();
			},
			doClose : function() {
				if (this.beforeClose()) {
					this.opener.closeCurrentTab();
				}
			}
		});