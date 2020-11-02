/**
 * 待发药处方列表
 * 
 * @author : caijy
 */
$package("phis.application.sto.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyDispensingList = function(cfg) {
	cfg.disablePagingTbr = true;
	// cfg.summaryable = true;
	cfg.queryComboBoxWidth = 80;
	cfg.queryWidth = 80;
	phis.application.pha.script.PharmacyDispensingList.superclass.constructor
			.apply(this, [cfg])
	this.on("startRefresh", this.startRefresh, this);
	this.on("stopRefresh", this.stopRefresh, this);
	this.task;
}

Ext.extend(phis.application.pha.script.PharmacyDispensingList,
		phis.script.SimpleList, {
			// 增加效期限制小天
			loadData : function() {
				this.loading = true;
				this.requestData.serviceId = this.fullserviceId;
				this.requestData.serviceAction = this.queryServiceActionID;
				phis.application.pha.script.PharmacyDispensingList.superclass.loadData
						.call(this);
			},
			onRowClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("cfSelect", record);
				}
			},
			// 双击直接发药
			onDblClick : function(grid, index, e) {
//				var lastIndex = grid.getSelectionModel().lastActive;
//				var record = grid.store.getAt(lastIndex);
//				if (record) {
//					this.fireEvent("cfDbClick", record);
//				}
				this.isDbClick=true;
			},
			onESCKey : function() {
				this.fireEvent("cfCancleSelect");
			},
			onReady : function() {
				phis.application.pha.script.PharmacyDispensingList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
				if (document.readyState == "complete") {
					this.startRefresh();
				}
			},
			// 上下时自动查询明细
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick(this.grid);
				}
			},
			// 刚打开页面时候默认选中第一条数据
			onStoreLoadData : function(store, records, ops) {
				this.loading = false;
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("MZSH_TJXX_2").innerHTML = "剩余发药数：0";
					this.fireEvent("noRecord", this);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick(this.grid);
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick(this.grid);
				}
				var store = this.grid.getStore();
				var n = store.getCount()
				document.getElementById("MZSH_TJXX_2").innerHTML = "剩余发药数：" + n;
			},
			totalCFS : function(v, params, data) {
				return v == null
						? '0'
						: ('<span style="font-size:14px;color:black;">剩余发药数:&#160;'
								+ v + '</span>');
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='MZSH_TJXX_2' align='center' style='color:blue'>剩余发药数：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			getStore : function(items) {
				var o = this.getStoreFields(items)
				var readCfg = {
					root : 'body',
					totalProperty : 'totalCount',
					fields : o.fields
				}
				if (!this.isCompositeKey) {
					readCfg.id = o.pkey;
				}
				var reader = new Ext.data.JsonReader(readCfg)
				var url = ClassLoader.serverAppUrl || "";
				// add by yangl 请求统一加前缀
				if (this.requestData && this.requestData.serviceId
						&& this.requestData.serviceId.indexOf(".") < 0) {
					this.requestData.serviceId = 'phis.' + this.requestData.serviceId
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.requestData,
							timeout : this.timeout || 30000
						})
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText + ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									// modified by gaof 2015-1-4 解决msg为undefined时报错的问题
									this.processReturnMsg(code, msg ? msg : "",
											this.refresh)
								}
							} else {
								this.stopRefresh();
								alert("登录超时，请重新登录！");
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)
		
				if (this.group) {
					var store = new Ext.data.GroupingStore({
								reader : reader,
								proxy : proxy,
								sortInfo : {
									field : this.groupSort || this.group,
									direction : "ASC"
								},
								groupField : this.group
							});
				} else {
					var store = new Ext.data.Store({
								proxy : proxy,
								reader : reader,
								remoteSort : this.remoteSort || false
							})
				}
				store.on("load", this.onStoreLoadData, this)
				store.on("beforeload", this.onStoreBeforeLoad, this)
				return store
		
			},
			// 定时刷新
			autoFresh : function(obj, seconds) {
				var task = {
					run : function() {
						var this1 = obj;
						var lastRunTime = this1.task.lastRunTime;
						var currentTime = new Date().getTime();
						if (currentTime - lastRunTime < 1000) {
							this1.stopRefresh();
							return false;
						}
						this.lastRunTime = currentTime;
						var selectdRecord = this1.getSelectedRecord();
						var selectRow = this1.store.indexOf(selectdRecord);
						this1.selectedIndex = selectRow;
						phis.application.pha.script.PharmacyDispensingList.superclass.loadData
								.call(this1);
						this1.selectRow(selectRow);
						this1.onRowClick(this1.grid);
					},
					interval : seconds * 1000,
					status : false,
					lastRunTime : 0
				}
				return task;
			},
			startRefresh : function() {// 启动定时刷新
				if (this.task) {
					if (!this.task.status) {
						Ext.TaskMgr.start(this.task);
					}
				} else {
					phis.script.rmi.jsonRequest({
								serviceId : this.serviceId,
								serviceAction : this.refreshServiceActionID
							}, function(code, msg, json) {
								if (code == 200) {
									json = eval(json.body)
									if (json.MZFYZDSXMS > 0) {
										this.task = this.autoFresh(this,
												json.MZFYZDSXMS);
										var t = Ext.TaskMgr.start(this.task);
										this.task.status = true;
									} else {
										this.refresh();
									}
								}
							}, this)// jsonRequest
				}
			},
			stopRefresh : function() {// 停止定时刷新
				if (this.task) {
					Ext.TaskMgr.stop(this.task);
					this.task.status = false;
				}
			}
		});