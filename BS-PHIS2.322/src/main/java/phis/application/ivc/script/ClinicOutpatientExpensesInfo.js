$package("phis.application.ivc.script");

$import("phis.script.SimpleList","phis.application.mds.script.MySimplePagingToolbar");

phis.application.ivc.script.ClinicOutpatientExpensesInfo = function(cfg) {
	cfg.height = 200;
	cfg.autoLoadData = false;
//	cfg.disablePagingTbr = true;
	phis.application.ivc.script.ClinicOutpatientExpensesInfo.superclass.constructor.apply(this, [ cfg ]);

},

Ext.extend(phis.application.ivc.script.ClinicOutpatientExpensesInfo,
		   phis.script.SimpleList,{
			loadData : function(strdate, enddate, kdsm , jzys ) {
					this.clear(); // ** add by yzh , 2010-06-09 **
					recordIds = [];
					this.requestData.serviceId = this.listServiceId;
					this.requestData.serviceAction = this.serviceAction;
					this.requestData.strdate = strdate;
					this.requestData.enddate = enddate;
					this.requestData.kdsm = kdsm;
					this.requestData.jzys = jzys;
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
				 * 根据输入的住院号码，进行模糊查询，并选择符合条件的第一条记录
				 * 
				 * @param zyNo
				 */
				fuzzySearchPatient : function(zyNo) {
					var allItems = this.store.data.items;
					var tmp;
					var tmpZYhm;
					for ( var i = 0; i < allItems.length; i++) {
						tmp = allItems[i];
						tmpZYhm = tmp.data.ZYHM;
						if (this.startWith(tmpZYhm, zyNo)) {
							this.selectRow(i);
							this.queryProject();
							return;
						}
					}
					MyMessageTip.msg("提示", "当前页面没有该病人医技!", true);
				},
				/**
				 * 刷新住院病人列表
				 * 
				 * @param rData
				 */
				refreshPatient : function(rData) {
					Ext.apply(this.requestData, {
						"body" : rData
					});
					// 刷新(包括按住院号码过滤)按钮执行时，显示第一页
					this.requestData.pageNo = 1;
					// this.loadData();
					this.reCallData();
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
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : this.pageSize || 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录",
					divHtml:"<div id='MZFY_JE' align='center' style='color:blue'>费用合计：￥0.00（处方金额总计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;检查金额总计:￥0.00）</div>"
				}
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}
				var pagingToolbar = new phis.application.mds.script.MySimplePagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				this.pagingToolbar.on("beforePageChange",
						this.beforeStorechange);
				return pagingToolbar
			},
			onStoreLoadData : function(store, records, ops) {//add by lizhi 2017-11-13 增加总计金额
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("MZFY_JE").innerHTML = "费用合计：￥0.00（其中：处方金额总计:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;检查金额总计:￥0.00）";
					this.fireEvent("noRecord", this);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
				var store = this.grid.getStore();
				var CFHJ = 0;
				var JCJE = 0;
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (r.get("CFJE")) {
						CFHJ += r.get("CFJE");
					}
					if (r.get("JCJE")) {
						JCJE += r.get("JCJE");
					}
				}
				document.getElementById("MZFY_JE").innerHTML = "费用合计：￥"+store.reader.jsonData.totalje
						+ "（其中：处方金额总计:￥"
						+ store.reader.jsonData.totalcfje
						+ "&nbsp;&nbsp;&nbsp;&nbsp;检查金额总计:￥"
						+ store.reader.jsonData.totaljcje+"）";
			}
});