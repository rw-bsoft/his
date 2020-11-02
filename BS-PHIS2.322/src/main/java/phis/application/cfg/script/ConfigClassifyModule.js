$package("phis.application.cfg.script")

/**
 * 分类类别维护module gaof 2013.03.18
 */
$import("phis.script.SimpleModule")

phis.application.cfg.script.ConfigClassifyModule = function(cfg) {
	phis.application.cfg.script.ConfigClassifyModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.cfg.script.ConfigClassifyModule,
		phis.script.SimpleModule, {
			initPanel : function() {// 加载panel
				var actions = this.actions;
				var bar = [];
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					bar.push({
								boxLabel : ac.name,
								inputValue : ac.value,
								name : "stack",
								clearCls : true
							})
				}

				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										// title : '分类名称',
										height : 200,
										width : 600,
										items : this.getClassifyComboList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										// title : '分类明细',
										region : 'center',
										height : 200,
										width : 420,
										items : this
												.getClassifyComboDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getClassifyComboList : function() {
				this.ClassifyComboList = this.createModule(
						"getClassifyComboList", this.refClassifyComboList);
				this.ClassifyComGrid = this.ClassifyComboList.initPanel();
				this.ClassifyComboList
						.on("loadData", this.onListLoadData, this);
				// this.ClassifyComboList
				// .on("beforeclose", this.beforeclose, this);
				this.ClassifyComGrid.on("loadData", this.onListLoadData, this);
				this.ClassifyComGrid.on("rowClick", this.onListRowClick, this);
				return this.ClassifyComGrid;
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.ClassifyComGrid.fireEvent("rowclick",
								this.ClassifyComGrid, 0);
					}
				} else {
					this.ClassifyComboDetailList.LBXH = "";
				}
			},
			onListRowClick : function(ClassifyComGrid, rowIndex, e) {
				// this.beforeclose();
				var type = "beforeclose";
				if (this.ClassifyComboDetailList.store.getModifiedRecords().length > 0) {
					// for (var i = 0; i < this.ClassifyComboDetailList.store
					// .getCount(); i++) {
					// if (this.ClassifyComboDetailList.store.getAt(i)
					// .get("LBXH")) {
					Ext.MessageBox.confirm('提示', '规则已修改，是否保存?',
							function(btn) {
								if (btn == "yes") {
									// 某一分类下已维护了物资分类，则不能修改分类长度，只能修改分类名称
									var data = {};
									data["LBXH"] = this.ClassifyComboDetailList.LBXH;
									// alert(this.LBXH)
									phis.script.rmi.jsonRequest({
												serviceId : this.serviceId,
												serviceAction : "isClassifyUsed",
												body : data
											}, function(code, msg, json) {
												if (code >= 300) {
													MyMessageTip.msg("提示", msg,
															true);
												}
												if (0 != json.countKFXX) {
													MyMessageTip.msg("提示",
															"当前分类在库房中已使用，不能修改",
															true);
													return false;
												}
												if (0 != json.countFLZD) {
													MyMessageTip.msg("提示",
															"当前分类已有分类的物资，不能修改",
															true);
													return false;
												}
												var detailStore = [];
												var countDetailList = this.ClassifyComboDetailList.store
														.getCount();
												if (countDetailList == 0) {
													detailStore.push({
														"LBXH" : this.ClassifyComboDetailList.LBXH
													});
												} else {
													for (var i = 0; i < countDetailList; i++) {
														if (this.ClassifyComboDetailList.store
																.getAt(i).data["GZMC"] == null
																|| this.ClassifyComboDetailList.store
																		.getAt(i).data["GZMC"] == "") {
															Ext.Msg.alert("提示",
																	"规则名称不能为空");
															return false;
														}
														detailStore
																.push(this.ClassifyComboDetailList.store
																		.getAt(i).data);
													}
												}
												// alert(Ext.encode(detailStore))
												phis.script.rmi.jsonRequest({
													serviceId : this.serviceId,
													serviceAction : "saveDetailList",
													body : detailStore
												}, function(code, msg, json) {
													if (code >= 300) {
														this.processReturnMsg(
																code, msg);
														return false;
													}

													var r = ClassifyComGrid.store
															.getAt(rowIndex);
													if (!r)
														return;
													this.ClassifyComboDetailList.LBXH = r.id;
													this.ClassifyComboDetailList.requestData.cnd = [
															'eq',
															['$', 'LBXH'],
															['d', r.id]];
													this.ClassifyComboDetailList
															.loadData();
													this.ClassifyComboDetailList.store
															.rejectChanges();
													this.ClassifyComboDetailList
															.refresh();
													return true;
														// this.op = "update";
												}, this);
											}, this)

								} else {
									var r = ClassifyComGrid.store
											.getAt(rowIndex);
									if (!r)
										return;
									this.ClassifyComboDetailList.LBXH = r.id;
									this.ClassifyComboDetailList.requestData.cnd = [
											'eq', ['$', 'LBXH'], ['d', r.id]];
									this.ClassifyComboDetailList.loadData();
									this.ClassifyComboDetailList.store
											.rejectChanges();
								}
							}, this)

					// if (confirm('报表归并已经修改，是否保存?')) {
					// this.ClassifyComboDetailList.doSave(type)
					// } else {
					// break;
					// }

					// }
					// }
				} else {
					var r = ClassifyComGrid.store.getAt(rowIndex);
					if (!r)
						return;
					this.ClassifyComboDetailList.LBXH = r.id;
					this.ClassifyComboDetailList.requestData.cnd = ['eq',
							['$', 'LBXH'], ['d', r.id]];
					this.ClassifyComboDetailList.loadData();
					this.ClassifyComboDetailList.store.rejectChanges();
				}

				// var r = ClassifyComGrid.store.getAt(rowIndex);
				// if (!r)
				// return;
				// this.ClassifyComboDetailList.LBXH = r.id;
				// this.ClassifyComboDetailList.requestData.cnd = ['eq',
				// ['$', 'LBXH'], ['d', r.id]];
				// this.ClassifyComboDetailList.loadData();
			},
			getClassifyComboDetailList : function() {
				this.ClassifyComboDetailList = this.createModule(
						"getClassifyComboDetailList",
						this.refClassifyComboDetailList);
				// this.ClassifyComboDetailList.on("afterRemove",
				// this.afterRemove, this);
				// this.ClassifyComboDetailList.on("doInsert", this.doInsert,
				// this);
				this.ClassifyComboDetailGrid = this.ClassifyComboDetailList
						.initPanel();
				return this.ClassifyComboDetailGrid;
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
				var type = "beforeclose";
				if (this.ClassifyComboDetailList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.ClassifyComboDetailList.store
							.getCount(); i++) {
						if (this.ClassifyComboDetailList.store.getAt(i)
								.get("LBXH")) {
							if (confirm('报表归并已经修改，是否保存?')) {
								return this.ClassifyComboDetailList
										.doSave(type)
							} else {
								break;
							}
						}
					}
					this.ClassifyComboDetailList.store.rejectChanges();
				}
				return true;
			}
			// ,
			// doSave : function() {
			// // 分类名称列表
			// var listStore = [];
			// var countList = this.ClassifyComboList.store.getCount();
			// for (var i = 0; i < countList; i++) {
			// listStore.push(this.ClassifyComboList.store.getAt(i).data);
			// }
			//
			// // 分类规则列表
			// var detailStore = [];
			// var countDetailList =
			// this.ClassifyComboDetailList.store.getCount();
			// for (var i = 0; i < countDetailList; i++) {
			// if (this.ClassifyComboDetailList.store.getAt(i).data["GZMC"] ==
			// null
			// || this.ClassifyComboDetailList.store.getAt(i).data["GZMC"] ==
			// "") {
			// Ext.Msg.alert("提示", "规则名称不能为空");
			// return;
			// }
			// detailStore.push(this.ClassifyComboDetailList.store.getAt(i).data);
			// }
			// alert(Ext.encode(listStore));
			// alert(Ext.encode(detailStore));
			// }
			// afterRemove : function() {
			// if (this.ClassifyComboList) {
			// this.ClassifyComboList.onStoreLoadData();
			// }
			// }
			// ,
			// doInsert : function() {
			//                
			// }
		})
