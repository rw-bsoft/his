$package("phis.application.med.script");

$import("phis.script.SimpleList", "phis.script.widgets.MyMessageTip");

		phis.application.med.script.MSPatientList = function(cfg) {
			cfg.height = 200;
			cfg.autoLoadData = false;
			phis.application.med.script.MSPatientList.superclass.constructor
					.apply(this, [ cfg ]);
		},

		Ext
				.extend(
						phis.application.med.script.MSPatientList,
						phis.script.SimpleList,
						{
							loadData : function(strdate, enddate, carno) {
								this.clear(); // ** add by yzh , 2010-06-09 **
								recordIds = [];
								this.requestData.serviceId = "phis.medicalTechnologyProjectService";
								this.requestData.serviceAction = this.serviceAction;
								this.requestData.strdate = new Date(strdate
										.getValue()).format("Y-m-d");
								this.requestData.enddate = new Date(enddate
										.getValue()).format("Y-m-d");
								this.requestData.carno = carno.getValue();
								this.requestData.zxks = this.mainApp['phis'].MedicalId;
								if (this.store) {
									if (this.disablePagingTbr) {
										this.store.load()
									} else {
										var pt = this.grid.getBottomToolbar()
										if (this.requestData.pageNo == 1) {
											pt.cursor = 0;
										}
										pt.doLoad(pt.cursor)
									}
								}
								this.resetButtons();
							},
							onRowClick : function() {
								this.queryProject();
							},
							// 刚打开页面时候默认选中数据,这时候判断下作废按钮
							onStoreLoadData : function(store, records, ops) {
								this.fireEvent("loadData", store); // **
								// 不管是否有记录，都fire出该事件
								if (records.length == 0) {
									this.fireEvent("queryProject");
									return;
								}
								if (!this.selectedIndex
										|| this.selectedIndex >= records.length) {
									this.selectRow(0);
									this.queryProject();
								} else {
									this.selectRow(this.selectedIndex);
									this.selectedIndex = 0;
									this.queryProject();
								}
							},
							/**
							 * 返回当前选择行
							 * 
							 * @returns
							 */
							getSelectRow : function() {
								var selectRow = this.grid.getSelectionModel()
										.getSelected();
								return selectRow;
							},
							/**
							 * 查询病人项目
							 */
							queryProject : function() {
								if (this.grid.getSelectionModel().getCount() > 0) {
									var selectRow = this.getSelectRow();
									this.fireEvent("queryProject",
											selectRow.data);
								}
							},
							startWith : function(str1, str2) {
								if (str1 == null || str2 == null)
									return false;
								if (str2.length > str1.length)
									return false;
								if (str1.substr(0, str2.length) == str2)
									return true;
								return false;
							},
							/**
							 * 根据输入的门诊号，进行模糊查询，并选择符合条件的第一条记录
							 * 
							 * @param msNo
							 */
							fuzzySearchPatient : function(msNo) {
								var allItems = this.store.data.items;
								var tmp;
								var tmpMzhm;
								for ( var i = 0; i < allItems.length; i++) {
									tmp = allItems[i];
									tmpMzhm = tmp.data.MZHM;
									if (this.startWith(tmpMzhm, msNo)) {
										this.selectRow(i);
										this.queryProject();
										return;
									}
								}
								MyMessageTip.msg("提示", "当前页面没有该病人医技!", true);
							},
							/**
							 * 刷新门诊病人列表
							 * 
							 * @param rData
							 */
							refreshPatient : function(rData) {
								Ext.apply(this.requestData, {
									"body" : rData
								});
								// 刷新(包括按门诊号码过滤)按钮执行时，显示第一页
								this.requestData.pageNo = 1;
								// this.loadData();
								this.reCallData();
							},
							/**
							 * 医技取消
							 */
							cancel : function() {
								var selectRow = this.getSelectRow();
								if (!selectRow) {
									return;
								} else {
									// 显示一个使用配置选项的对话框： Show a dialog using
									// config options:
									Ext.Msg.show({
										title : '确认取消医技执行?',
										msg : '是否取消选择的医技执行?',
										buttons : Ext.Msg.YESNO,
										fn : this.processResult,
										scope : this,
										icon : Ext.MessageBox.QUESTION
									});

								}
							},
							/**
							 * 向前翻页，当有多页时，全部取消完成，自动向前翻页
							 */
							reCallData : function() {
								var pageNo = this.requestData.pageNo;
								if (pageNo < 1) {// 当PageNo<1时，退出递归
									// 当PageNo小于1时，将请求页码置为1
									this.requestData.pageNo = 1;
									return;
								}
								if (pageNo > 1) {
									this.store
											.reload({
												callback : function(records,
														options, success) {
													if (this.store.getCount() < 1) {
														// 请求页面减一
														this.requestData.pageNo = this.requestData.pageNo - 1;
														// 递归调用当前方法
														this.reCallData();
													}
												},
												scope : this
											// 作用域为this。必须加上否则this.initSelect();
											// 无法调用
											});
								} else {
									this.store
											.load({
												callback : function(records,
														options, success) {
													if (this.store.getCount() < 1) {
														// 请求页面减一
														this.requestData.pageNo = this.requestData.pageNo - 1;
														// 递归调用当前方法
														this.reCallData();
													}
												},
												scope : this
											// 作用域为this。必须加上否则this.initSelect();
											// 无法调用
											});
								}
							},
							/**
							 * 确认选择框执行方法
							 * 
							 * @param btnId
							 * @param text
							 * @param opt
							 */
							processResult : function(btnId, text, opt) {
								if (btnId == "yes") {// 选择是
									var body = {};
									body['bq'] = this.mainApp['phis'].MedicalId;
									var r = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : "configLogisticsInventoryControlService",
												serviceAction : "verificationWPJFBZ",
												body : body
											});
									if (r.code > 300) {
										this.processReturnMsg(r.code, r.msg);
										return;
									}
									var json = [];
									json.push(this.getSelectRow().data);
									var r = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : "medicalTechnologyProjectService",
												serviceAction : "cancelMSBusi",
												body : json
											});
									if (r.code > 300) {
										this.processReturnMsg(r.code, r.msg);
										return;
									} else {
										MyMessageTip.msg("提示", "取消执行成功!", true);
									}
									this.loadData(this.oper.strdate,
											this.oper.enddate, this.oper.carno);
									this.reCallData();
									if (this.grid.getSelectionModel()
											.getCount() < 1) {
										this.fireEvent("queryProject");
									}
								} else {
									return;
								}
							},
							print : function() {
								var selectRow = this.getSelectRow();
								if (!selectRow) {
									return;
								} else {
									var ZDID = 0;
									var YJXH = selectRow.data.YJXH;
									var r = phis.script.rmi
											.miniJsonRequestSync({
												serviceId : "medicalTechnologyProjectService",
												serviceAction : "yJBG01Search",
												YJXH : YJXH
											});
									if (r.code > 200) {
										MyMessageTip.msg("提示", "打印失败!", true);
										return;
									}
									if (r.json.ret == 0) {
										MyMessageTip.msg("提示",
												"所选记录没有诊断结果不能打印!", true);
										return;
									} else {
										ZDID = r.json.ret;
									}

									var arg = {
										brid : selectRow.data.BRID,
										mzzypb : 0,
										zdid : ZDID
									}
									$import("phis.prints.script.PatientYJZDJGPrintView");
									module = new phis.prints.script.PatientYJZDJGPrintView(
											this.cfg);
									module.initPanel();
									Ext.apply(module, arg);
									module.doPrint();
								}
							}

						});