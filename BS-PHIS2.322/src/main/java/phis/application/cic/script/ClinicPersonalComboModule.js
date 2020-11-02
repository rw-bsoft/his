$package("phis.application.cic.script")

/**
 * 处方组套维护module zhangyq 2012.05.25
 */
$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicPersonalComboModule = function(cfg) {
	this.sslbsign = 1;
	phis.application.cic.script.ClinicPersonalComboModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicPersonalComboModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var actions = this.actions;
				var barLeft = [];
				var barRight = [];
				var ztlbValue = 1;
				var sslbValue = 1;
				// 西药 中药 草药
				for (var i = 2; i < actions.length; i++) {
					var ac = actions[i];
					barLeft.push({
						xtype : "radio",
						checked : i == 2,
						boxLabel : ac.name,
						inputValue : ac.properties.value,
						name : "stackLeft",
						listeners : {
							check : function(group,checked) {
								if (this.comboNameList && checked) {
									this.beforeclose();
									this.comboNameList.SSLB = sslbValue;
									ztlbValue = parseInt(group.inputValue)
									this.comboNameList.ZTLB = ztlbValue// 下拉框值
									// 点击插入一行时给把ZT类别传到模糊查询的后台作为过滤条件
									var baseParams = {};
									baseParams.drugType = ztlbValue;
									this.comboNameDetailList.remoteDicStore.baseParams = baseParams;
									this.comboNameDetailList.remoteDic.lastQuery = "";
									this.comboNameList.ZTLBKEY = group.boxLabel;// 下拉框键
									// 根据组套类别 和所属类别显示
									this.comboNameList.requestData.pageNo = 1;
									this.comboNameList.requestData.cnd = [
											'and',
											['eq', ['$', 'ZTLB'],
													['i', ztlbValue]],
											['eq', ['$', 'SSLB'],
													['i', sslbValue]],
											['isNull', ['$', 'KSDM']]];
									this.comboNameList.initCnd = [
											'and',
											['eq', ['$', 'ZTLB'],
													['i', ztlbValue]],
											['eq', ['$', 'SSLB'],
													['i', sslbValue]],
											['isNull', ['$', 'KSDM']]];
									// 设置分页信息
									var pagingToolbar = this.comboNameList.pagingToolbar;
									this.comboNameList.loadData();
									if (sslbValue == "4") {
										this.comboNameList.setButtonsState([
														'add', 'update',
														'remove'], false);
									}
								}
							},
							scope : this
						}
					})
				}
				// 个人常用药 个人组套
				for (var i = 0; i < 2; i++) {
					var ac = actions[i];
					barRight.push({
						xtype : "radio",
						checked : i == 0,
						boxLabel : ac.name,
						inputValue : ac.properties.value,
						name : "stackRight",
						listeners : {
							check : function(group, checked) {
								if (this.comboNameList && checked) {
									this.beforeclose();
									this.comboNameList.SSLB = group.inputValue;
									sslbValue = parseInt(group.inputValue);
									this.sslbsign = parseInt(group.inputValue);
									// 根据组套类别 和所属类别显示
									this.comboNameList.requestData.cnd = [
											'and',
											['eq', ['$', 'ZTLB'], ['i', ztlbValue]],
											['eq', ['$', 'SSLB'], ['i', sslbValue]],
											['isNull', ['$', 'KSDM']]];
									this.comboNameList.initCnd = [
											'and',
											['eq', ['$', 'ZTLB'], ['i', ztlbValue]],
											['eq', ['$', 'SSLB'], ['i', sslbValue]],
											['isNull', ['$', 'KSDM']]];
									if (sslbValue == "4") {
										var data = {
											"ZTMC" : "个人常用药",
											"SSLB" : 4,
											"PYDM" : "GRCYY",
											"GLJB" : 0
										};
										var r = phis.script.rmi
												.miniJsonRequestSync({
													serviceId : this.serviceId,
													serviceAction : this.serviceAction,
													body : data
												});
									}
									// 设置分页信息
									var pagingToolbar = this.comboNameList.pagingToolbar;
									// pagingToolbar.changePage(1);
									this.comboNameList.requestData.pageNo = 1;
									// 保存完后刷新
									this.comboNameList.loadData();
									// 刷新完后按钮变灰
									if (sslbValue == "4") {
										this.comboNameList.setButtonsState(['add',
														'update', 'remove'], false);
									} else if (sslbValue == "1") {
										this.comboNameList.setButtonsState(['add',
														'update', 'remove'], true);
									}
								}
							},
							scope : this
						}
					})
				}
//				// 西药 中药 草药
//				var radioGroupLeft = new Ext.form.RadioGroup({
//					width : 150,
//					disabled : false,
//					items : barLeft,
//					listeners : {
//						change : function(group, newValue, oldValue) {
//							if (this.comboNameList) {
//								this.beforeclose();
//								this.comboNameList.SSLB = sslbValue;
//								ztlbValue = parseInt(newValue.inputValue)
//								this.comboNameList.ZTLB = ztlbValue// 下拉框值
//								// 点击插入一行时给把ZT类别传到模糊查询的后台作为过滤条件
//								var baseParams = {};
//								baseParams.drugType = ztlbValue;
//								this.comboNameDetailList.remoteDicStore.baseParams = baseParams;
//								this.comboNameDetailList.remoteDic.lastQuery = "";
//								this.comboNameList.ZTLBKEY = newValue.boxLabel;// 下拉框键
//								// 根据组套类别 和所属类别显示
//								this.comboNameList.requestData.pageNo = 1;
//								this.comboNameList.requestData.cnd = [
//										'and',
//										['eq', ['$', 'ZTLB'], ['i', ztlbValue]],
//										['eq', ['$', 'SSLB'], ['i', sslbValue]]];
//								this.comboNameList.initCnd = [
//										'and',
//										['eq', ['$', 'ZTLB'], ['i', ztlbValue]],
//										['eq', ['$', 'SSLB'], ['i', sslbValue]]];
//								// 设置分页信息
//								var pagingToolbar = this.comboNameList.pagingToolbar;
//								this.comboNameList.loadData();
//								if (sslbValue == "4") {
//									this.comboNameList.setButtonsState(['add',
//													'update', 'remove'], false);
//								}
//							}
//						},
//						scope : this
//					}
//				});
//				// 个人常用药 个人组套
//				var radioGroupRight = new Ext.form.RadioGroup({
//					width : 160,
//					disabled : false,
//					items : barRight,
//					listeners : {
//						change : function(group, newValue, oldValue) {
//							if (this.comboNameList) {
//								this.beforeclose();
//								this.comboNameList.SSLB = newValue.inputValue;
//								sslbValue = parseInt(newValue.inputValue);
//								this.sslbsign = parseInt(newValue.inputValue);
//								// 根据组套类别 和所属类别显示
//								this.comboNameList.requestData.cnd = [
//										'and',
//										['eq', ['$', 'ZTLB'], ['i', ztlbValue]],
//										['eq', ['$', 'SSLB'], ['i', sslbValue]]];
//								this.comboNameList.initCnd = [
//										'and',
//										['eq', ['$', 'ZTLB'], ['i', ztlbValue]],
//										['eq', ['$', 'SSLB'], ['i', sslbValue]]];
//								if (sslbValue == "4") {
//									var data = {
//										"ZTMC" : "个人常用药",
//										"SSLB" : 4,
//										"PYDM" : "GRCYY",
//										"GLJB" : 0
//									};
//									var r = phis.script.rmi
//											.miniJsonRequestSync({
//												serviceId : this.serviceId,
//												serviceAction : this.serviceAction,
//												body : data
//											});
//								}
//								// 设置分页信息
//								var pagingToolbar = this.comboNameList.pagingToolbar;
//								// pagingToolbar.changePage(1);
//								this.comboNameList.requestData.pageNo = 1;
//								// 保存完后刷新
//								this.comboNameList.loadData();
//								// 刷新完后按钮变灰
//								if (sslbValue == "4") {
//									this.comboNameList.setButtonsState(['add',
//													'update', 'remove'], false);
//								} else if (sslbValue == "1") {
//									this.comboNameList.setButtonsState(['add',
//													'update', 'remove'], true);
//								}
//							}
//						},
//						scope : this
//					}
//				});
//				radioGroupLeft.setValue(1);// 设置默认值
//				radioGroupRight.setValue(1);// 设置默认值
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : [barLeft, '->', barRight],
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										height : 200,
										width : 470,
										items : this.getComboNameList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										height : 200,
										width : 600,
										items : this.getComboNameDetailList()
									}]
						});
				panel.on("beforeclose", this.beforeclose, this);
				this.panel = panel;
				return panel;
			},

			getComboNameList : function() {
				this.comboNameList = this.createModule("getComboNameList",
						this.refComboNameList);
				this.comboNameGrid = this.comboNameList.initPanel();
				// 左边list加载时 调用onListLoadData 并把记录store带过来
				this.comboNameList.on("beforeclose", this.beforeclose, this);
				this.comboNameList.on("loadData", this.onListLoadData, this);
				this.comboNameGrid.on("loadData", this.onListLoadData, this);
				// 点击每一行时调用onListLoadData
				this.comboNameGrid.on("rowClick", this.onListRowClick, this);
				return this.comboNameGrid;
			},

			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (this.comboNameDetailList) {
					this.comboNameDetailList.clear();
				}
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.comboNameGrid.fireEvent("rowclick",
								this.comboNameGrid, 0);
					}
				} else {
					this.comboNameDetailList.ZTBH = "";
				}
			},
			onListRowClick : function(comboNameGrid, rowIndex, e) {
				this.beforeclose();
				var r = comboNameGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.comboNameDetailList.ZTBH = r.id;
				this.comboNameDetailList.requestData.cnd = ['eq',
						['$', 'ZTBH'], ['d', r.id]];
				this.comboNameDetailList.loadData();
				if (this.sslbsign == "4") {
					var btns = this.comboNameDetailGrid.getTopToolbar();
					var btn = btns.find("cmd", "insert");
					btn[0].hide();
					this.comboNameDetailList.checkBox.hide();
					this.comboNameDetailList.checkBox.setValue(1);
				} else {
					var btns = this.comboNameDetailGrid.getTopToolbar();
					var btn = btns.find("cmd", "insert");
					btn[0].show();
					this.comboNameDetailList.checkBox.show();
					this.comboNameDetailList.checkBox.setValue(0);
				}
			},
			getComboNameDetailList : function() {
				this.comboNameDetailList = this.createModule(
						"getComboNameDetailList", this.refComboNameDetailList);
				this.comboNameDetailList.on("loadData", function() {
							if (this.comboNameDetailList.store.getCount() == 0) {
								this.comboNameDetailList.doInsert();
							}
						}, this);
				this.comboNameDetailList.on("afterRemove", this.afterRemove,
						this);
				this.comboNameDetailList.on("afterCellEdit",
						this.afterDetailCellEdit, this);
				this.comboNameDetailGrid = this.comboNameDetailList.initPanel();
				return this.comboNameDetailGrid;
			},
			afterRemove : function() {
				if (this.comboNameList) {
					this.comboNameList.loadData();
					if (this.sslbsign == "4") {
						this.comboNameList.setButtonsState(['add', 'update',
										'remove'], false);
					}
				}
			},
			afterDetailCellEdit : function(it, record, field, v) {
				if (it.id == "SYPC") {
					field.getStore().each(function(r) {
								if (r.data.key == v) {
									if (record.get("MRCS") != r.data.MRCS) {
										record.set("MRCS", r.data.MRCS);
									}
								}
							}, this);
				}
				if (it.id == 'SYPC' || it.id == 'GYTJ' || it.id == 'YYTS') {
					var store = this.comboNameDetailGrid.getStore();
					store.each(function(r) {
						if (r.get('YPZH') == record.get('YPZH')) {
							r.set(it.id, v);
							r.set(it.id + '_text', record.get(it.id + '_text'));
							if (it.id == 'SYPC') {
								r.set("MRCS", record.get("MRCS"));
							}
						}
					}, this)
				}
			},
			beforeclose : function(tabPanel, newTab, curTab) {
				// 判断grid中是否有修改的数据没有保存
				if (this.comboNameDetailList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.comboNameDetailList.store
							.getCount(); i++) {
						if (this.comboNameDetailList.store.getAt(i).get("XMBH")) {
							if (confirm('组套明细数据已经修改，是否保存?')) {
								// this.needToClose = true;
								return this.comboNameDetailList.doSave()
							} else {
								break;
							}
						}
					}
					this.comboNameDetailList.store.rejectChanges();
				}
				return true;
			}
		})
