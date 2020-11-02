$package("phis.application.cfg.script")
$import("phis.script.SimpleModule")

phis.application.cfg.script.AdvicePersonalComboModule = function(cfg) {
	phis.application.cfg.script.AdvicePersonalComboModule.superclass.constructor
			.apply(this, [cfg])
}
var ztlbValue = 1;
var sslbValue = 2; // 1,个人 2、科室
Ext.extend(phis.application.cfg.script.AdvicePersonalComboModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				ztlbValue = 1;
				sslbValue = 2; // 1,个人 2、科室
				var actions = this.actions;
				var barLeft = [];
				var barRight = [];
				if(!this.cnds||this.cnds==null||this.cnds=='undefiend'){
					this.cnds=1;
				}
				sslbValue = this.cnds; // 1,个人 2、科室
				for (var i = 0; i < 5; i++) {
					var ac = actions[i];
					var config = {
						boxLabel : ac.name,
						inputValue : ac.properties.value,
						name : "stackLeft",
						clearCls : true
					}
					barLeft.push(config)
				}
				for (var i = 5; i < 7; i++) {
					var ac = actions[i];
					var config = {
						boxLabel : ac.name,
						inputValue : ac.properties.value,
						name : "stackRight",
						clearCls : true
					}
					barRight.push(config)
				}
				// 西药 中药 草药 其他
				var radioGroupLeft = new Ext.form.RadioGroup({
					width : 220,
					disabled : false,
					items : barLeft,
					listeners : {
						change : function(group, newValue, oldValue) {
							if (this.comboNameList) {
								// this.beforeclose();
								ztlbValue = parseInt(newValue.inputValue)
								this.ztlbValue = ztlbValue;
								this.comboNameList.ZTLB = ztlbValue// 下拉框值
								this.comboNameList.SSLB = sslbValue// 下拉框值
								// 点击插入一行时给把ZT类别传到模糊查询的后台作为过滤条件
								var baseParams = {};
								baseParams.drugType = ztlbValue;
								this.comboNameDetailList.remoteDicStore.baseParams = baseParams;
								this.comboNameDetailList.remoteDic.lastQuery = "";
								this.comboNameList.ZTLBKEY = newValue.boxLabel;// 下拉框键
								if (ztlbValue == 5) {//文字组套
									this.comboNameDetailList.grid
											.getColumnModel().setColumnHeader(
													2, "内容");
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(3,
													true);//剂量
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(4, true);//计量单位
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(5, true);//天数
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(7, true);//途径
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(6, true);//总量
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(8,
													true);//频次
								}else if (ztlbValue == 4) {
									this.comboNameDetailList.grid
											.getColumnModel().setColumnHeader(
													2, "项目名称");
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(3,
													true);//剂量
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(4, true);//计量单位
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(5, true);//天数
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(7, true);//途径
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(6, false);//总量
								} else {
									this.comboNameDetailList.grid
											.getColumnModel().setColumnHeader(
													2, "药品名称");
									this.comboNameDetailList.grid
											.getColumnModel()
											.setHidden(3, false);
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(4,
													false);
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(5,
													false);
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(8,
													false);//频次
									this.comboNameDetailList.grid
											.getColumnModel().setHidden(7,
													false);
									//this.comboNameDetailList.grid
											//.getColumnModel().setHidden(6,
													//false);
								}
								baseParams.drugType = ztlbValue;
								this.comboNameDetailList.remoteDicStore.baseParams = baseParams;
								this.comboNameDetailList.remoteDic.lastQuery = "";
								this.comboNameList.ZTLBKEY = newValue.boxLabel;// 下拉框键
								// 根据组套类别 和所属类别显示
								var sslbValue2 = sslbValue;
								if (this.lastKSDM) {
									if (this.lastKSDM == -1) {
										this.comboNameList.requestData.cnd = [
												'and',
												[
														'and',
														[
																'and',
																[
																		'eq',
																		['$',
																				'ZTLB'],
																		['i',
																				ztlbValue]],
																[
																		'eq',
																		['$',
																				'SSLB'],
																		['i',
																				sslbValue]]],
														[
																'eq',
																['$', 'JGID'],
																['$',
																		'%user.manageUnit.id']]],
												['eq', ['$', 'YGDM'],
														["$", '%user.userId']]];
										this.comboNameList.initCnd = [
												'and',
												[
														'and',
														[
																'and',
																[
																		'eq',
																		['$',
																				'ZTLB'],
																		['i',
																				ztlbValue]],
																[
																		'eq',
																		['$',
																				'SSLB'],
																		['i',
																				sslbValue]]],
														[
																'eq',
																['$', 'JGID'],
																['$',
																		'%user.manageUnit.id']]],
												['eq', ['$', 'YGDM'],
														["$", '%user.userId']]];

									} else {
										if (this.lastKSDM == 0) {
											if (sslbValue == 2) {
												sslbValue2 = 3;
											}
											if (sslbValue == 5) {
												sslbValue2 = 6;
											}
											this.comboNameList.requestData.cnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue2]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
											this.comboNameList.initCnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue2]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
										} else {
											this.comboNameList.requestData.cnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
											this.comboNameList.initCnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
										}
									}
									// 设置分页信息
									var pagingToolbar = this.comboNameList.pagingToolbar;
									// pagingToolbar.changePage(1);
									this.comboNameList.requestData.pageNo = 1;
									this.comboNameList.loadData();
									if (sslbValue == "4" || sslbValue == "5"
											|| sslbValue2 == "6") {
										this.comboNameList.setButtonsState([
														'add', 'update',
														'remove'], false);
									}
								}
							}
						},
						scope : this
					}
				});
				// 常用药,组套
				var radioGroupRight = new Ext.form.RadioGroup({
					width : 380,
					style : "padding-left:260px;",
					disabled : false,
					items : barRight,
					listeners : {
						change : function(group, newValue, oldValue) {
							if (this.comboNameList) {
								// this.beforeclose();
								this.comboNameList.SSLB = newValue.inputValue;
								sslbValue = parseInt(newValue.inputValue);
								this.sslbsign = parseInt(newValue.inputValue);
								// 根据组套类别 和所属类别显示
								var sslbValue2 = sslbValue;
								if (this.lastKSDM) {
									if (this.lastKSDM == -1) {
										this.comboNameList.requestData.cnd = [
												'and',
												[
														'and',
														[
																'and',
																[
																		'eq',
																		['$',
																				'ZTLB'],
																		['i',
																				ztlbValue]],
																[
																		'eq',
																		['$',
																				'SSLB'],
																		['i',
																				sslbValue]]],
														[
																'eq',
																['$', 'JGID'],
																['$',
																		'%user.manageUnit.id']]],
												['eq', ['$', 'YGDM'],
														["$", '%user.userId']]];
										this.comboNameList.initCnd = [
												'and',
												[
														'and',
														[
																'and',
																[
																		'eq',
																		['$',
																				'ZTLB'],
																		['i',
																				ztlbValue]],
																[
																		'eq',
																		['$',
																				'SSLB'],
																		['i',
																				sslbValue]]],
														[
																'eq',
																['$', 'JGID'],
																['$',
																		'%user.manageUnit.id']]],
												['eq', ['$', 'YGDM'],
														["$", '%user.userId']]];

									} else {
										if (this.lastKSDM == 0) {
											if (sslbValue == 2) {
												sslbValue2 = 3;
											}
											if (sslbValue == 5) {
												sslbValue2 = 6;
											}
											this.comboNameList.requestData.cnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue2]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
											this.comboNameList.initCnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue2]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
										} else {
											this.comboNameList.requestData.cnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
											this.comboNameList.initCnd = [
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'eq',
																			[
																					'$',
																					'ZTLB'],
																			[
																					'i',
																					ztlbValue]],
																	[
																			'eq',
																			[
																					'$',
																					'SSLB'],
																			[
																					'i',
																					sslbValue]]],
															[
																	'eq',
																	['$',
																			'JGID'],
																	['$',
																			'%user.manageUnit.id']]],
													[
															'eq',
															['$', 'KSDM'],
															["$", this.lastKSDM]]];
										}
									}
									if (sslbValue == "4") {
										var data = {
											"ZTMC" : "个人常用",
											"SSLB" : 4,
											"PYDM" : "GRCYY",
											"GLJB" : 0,
											"KSDM" : this.mainApp['phis'].wardId
										};
										var r = phis.script.rmi
												.miniJsonRequestSync({
													serviceId : this.serviceId,
													serviceAction : this.serviceAction,
													method : "execute",
													body : data
												});
									}
									if (sslbValue == "5") {
										var KSDM = 0;
										if (this.lastKSDM > 0) {
											KSDM = this.lastKSDM;
										}
										var data = {
											"ZTMC" : "科室常用",
											"SSLB" : 5,
											"PYDM" : "KSCYY",
											"GLJB" : 0,
											"KSDM" : KSDM
										};
										var r = phis.script.rmi
												.miniJsonRequestSync({
													serviceId : this.serviceId,
													serviceAction : this.serviceAction,
													method : "execute",
													body : data
												});
									}

									if (sslbValue2 == "6") {
										var data = {
											"ZTMC" : "全院常用",
											"SSLB" : 6,
											"PYDM" : "GRCYY",
											"GLJB" : 0,
											"KSDM" : 0
										};
										var r = phis.script.rmi
												.miniJsonRequestSync({
													serviceId : this.serviceId,
													serviceAction : this.serviceAction,
													method : "execute",
													body : data
												});
									}

									// 设置分页信息
									var pagingToolbar = this.comboNameList.pagingToolbar;
									// pagingToolbar.changePage(1);
									this.comboNameList.requestData.pageNo = 1;
									this.comboNameList.loadData();// 切换按钮
									// 从新加载数据
									// 刷新完后按钮变灰
									if (sslbValue == "4" || sslbValue == "5"
											|| sslbValue2 == "6") {
										this.comboNameList.setButtonsState([
														'add', 'update',
														'remove'], false);
									}else {
										this.comboNameList.setButtonsState([
														'add', 'update',
														'remove'], true);
									}
								}
							}
						},
						scope : this
					}
				});
				radioGroupLeft.setValue(1);// 设置默认值
				radioGroupRight.setValue(1);// 设置默认值
				// 科室组套要加下拉框。个人组套不需要
				var tbar = ['', radioGroupLeft, '', radioGroupRight];
				if (this.openBy == "office") {
					// 科室切换 start
					text = "科室选择";
					dicId = "phis.dictionary.department_yzzt";
					var label = new Ext.form.Label({
								text : text
							});
					var viewModeComb = util.dictionary.SimpleDicFactory
							.createDic({
										id : dicId,
										width : 100
									});
					viewModeComb.on("select", this.viewModeSelect, this);

					this.viewModeComb = viewModeComb;
					this.viewModeComb.store.on("load", this.viewModeLoad, this);
					tbar = ['', radioGroupLeft, '', radioGroupRight, '->',
							label, '-', viewModeComb];
				} else {
					this.lastKSDM = -1;
				}

				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : tbar,
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
			viewModeSelect : function(f, r, index) {
				var v = f.getValue();
				if (this.lastKSDM != v) {
					this.lastKSDM = v;
				}
				var sslbValue2 = sslbValue;
				if (this.lastKSDM) {
					if (this.lastKSDM == 0) {
						if (sslbValue == 2) {
							sslbValue2 = 3;
						}
						if (sslbValue == 5) {
							sslbValue2 = 6;
						}
						this.comboNameList.requestData.cnd = [
								'and',
								[
										'and',
										[
												'and',
												['eq', ['$', 'ZTLB'],
														['i', ztlbValue]],
												['eq', ['$', 'SSLB'],
														['i', sslbValue2]]],
										['eq', ['$', 'JGID'],
												['$', '%user.manageUnit.id']]],
								['eq', ['$', 'KSDM'], ["$", this.lastKSDM]]];
						this.comboNameList.initCnd = [
								'and',
								[
										'and',
										[
												'and',
												['eq', ['$', 'ZTLB'],
														['i', ztlbValue]],
												['eq', ['$', 'SSLB'],
														['i', sslbValue2]]],
										['eq', ['$', 'JGID'],
												['$', '%user.manageUnit.id']]],
								['eq', ['$', 'KSDM'], ["$", this.lastKSDM]]];
					} else {
						if (this.lastKSDM > 0) {
							this.comboNameList.requestData.cnd = [
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'ZTLB'],
															['i', ztlbValue]],
													['eq', ['$', 'SSLB'],
															['i', sslbValue]]],
											[
													'eq',
													['$', 'JGID'],
													['$', '%user.manageUnit.id']]],
									['eq', ['$', 'KSDM'], ["$", this.lastKSDM]]];
							this.comboNameList.initCnd = [
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'ZTLB'],
															['i', ztlbValue]],
													['eq', ['$', 'SSLB'],
															['i', sslbValue]]],
											[
													'eq',
													['$', 'JGID'],
													['$', '%user.manageUnit.id']]],
									['eq', ['$', 'KSDM'], ["$", this.lastKSDM]]];
						} else {
							this.comboNameList.requestData.cnd = [
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'ZTLB'],
															['i', ztlbValue]],
													['eq', ['$', 'SSLB'],
															['i', sslbValue]]],
											[
													'eq',
													['$', 'JGID'],
													['$', '%user.manageUnit.id']]],
									['eq', ['$', 'YGDM'], ["$", '%user.userId']]];
							this.comboNameList.initCnd = [
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'ZTLB'],
															['i', ztlbValue]],
													['eq', ['$', 'SSLB'],
															['i', sslbValue]]],
											[
													'eq',
													['$', 'JGID'],
													['$', '%user.manageUnit.id']]],
									['eq', ['$', 'YGDM'], ["$", '%user.userId']]];

						}
					}

					if (sslbValue2 == "6") {
						var data = {
							"ZTMC" : "全院常用",
							"SSLB" : 6,
							"PYDM" : "GRCYY",
							"GLJB" : 0,
							"KSDM" : 0
						};
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.serviceAction,
									method : "execute",
									body : data
								});
					}
					var KSDM = this.lastKSDM;
					if (sslbValue == "5") {
						var data = {
							"ZTMC" : "科室常用",
							"SSLB" : 5,
							"PYDM" : "KSCYY",
							"GLJB" : 0,
							"KSDM" : KSDM
						};
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.serviceAction,
									method : "execute",
									body : data
								});
					}

					var pagingToolbar = this.comboNameList.pagingToolbar;
					// pagingToolbar.changePage(1);
					this.comboNameList.requestData.pageNo = 1;
					this.comboNameList.loadData();
				}
				// 刷新完后按钮变灰
				if (sslbValue == "5" || sslbValue2 == "6") {
					this.comboNameList.setButtonsState(['add', 'update',
									'remove'], false);
				}
			},
			viewModeLoad : function(store) {
				var r1 = new Ext.data.Record({
							key : "0",
							text : "全院科室"
						})
				store.insert(0, [r1]);
				/*
				 * add by zhaojian  2017-06-05 增加用于检查开单的检查组套
				 */
				var r2 = new Ext.data.Record({
							key : "9999999999",
							text : "检查组套"
						})
				store.insert(1, [r2]);
				/*
				 * if (!this.firstLoad) { this.viewModeComb.setValue("0");
				 * this.firstLoad = true; }
				 */
				// alert(this.firstLoad);
			},
			getComboNameList : function() {
				this.comboNameList = this.createModule("getComboNameList",this.refComboNameList);
				this.comboNameList.opener = this;
				this.comboNameList.SSLB = sslbValue;
			    this.comboNameList.ZTLB = ztlbValue;
				this.comboNameGrid = this.comboNameList.initPanel();
				this.comboNameList.on("beforeclose", this.beforeclose, this);
				this.comboNameList.on("loadData", this.onListLoadData, this);
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
					document.getElementById("totcount").innerHTML = "明细条数：0";
					this.comboNameDetailList.ZTBH = "";
				}
			},
			onListRowClick : function(comboNameGrid, rowIndex, e) {
				this.beforeclose();
				var r = comboNameGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.comboNameDetailList.ZTBH = r.id;
				var ztlbValue = this.ztlbValue || 1;
				if (sslbValue == "4" || sslbValue == "5") {
					this.comboNameList.setButtonsState(['add', 'update',
									'remove'], false);
				}
//				if(ztlbValue == 5){//add by lizhi 2017-12-01增加文字组套
//					this.comboNameList.setButtonsState(['newGroup'], false);
//				}

				this.comboNameDetailList.requestData.schema = (ztlbValue == 4
						? "phis.application.cfg.schemas.BQ_ZT02_XM"
						: ztlbValue == 5 
						? "phis.application.cfg.schemas.BQ_ZT02_WZ" : "phis.application.cfg.schemas.BQ_ZT02")
				this.comboNameDetailList.requestData.cnd = ['eq',
						['$', 'ZTBH'], ['d', r.id]];
				this.comboNameDetailList.loadData();
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
				//this.comboNameDetailList.grid.getColumnModel().setHidden(3,
						//true);//总量
				/**************add by lizhi 2017-11-07 增加混合组套*****************/
//				this.comboNameDetailList.ZTLB = this.comboNameList.ZTLB;
				this.comboNameDetailList.opener = this;
				/**************add by lizhi 2017-11-07 增加混合组套*****************/
				return this.comboNameDetailGrid;
			},
			afterRemove : function() {
				if (this.comboNameList) {
					this.comboNameList.loadData();
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
