$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseFinancialAcceptanceList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds = ['eq', ['$', 'a.RKPB'], ['i', 1]]
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	cfg.gridDDGroup=true;
	phis.application.sto.script.StorehouseFinancialAcceptanceList.superclass.constructor.apply(this,
			[cfg])
	//this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceList, phis.script.SimpleList,
		{
			// 生成日期框
			getCndBar : function(items) {
//				var simple = new Ext.Panel({
//							labelWidth : 50,
//							defaults : {},
//							width:132,
//							height:25,
//							defaultType : 'textfield',
//							items : [new Ext.ux.form.Spinner({
//										fieldLabel : '财务月份',
//										name : 'storeDate',
//										value : this.getDate(),
//										strategy : {
//											xtype : "month"
//										}
//									})]
//						});
//				this.simple = simple;
//				 var queryButton = new Ext.Button({
//				 text : '刷新(F1)',
//				 iconCls : "query"
//				 })
//				 queryButton.on("click", this.doCndQuery, this);
				var simple=new Ext.ux.form.Spinner({
										fieldLabel : '财务月份',
										name : 'storeDate',
										value : this.getDate(),
										strategy : {
											xtype : "month"
										}
									})
									this.simple=simple;
				 return [simple];
				//return [simple];
			},
			// 条件查询
			doCndQuery : function(button, e, addNavCnd) {
				var initCnd = this.initCnd;
				var simple = this.simple;
				var cnd = null;
				if (simple) {
					var stroeDate = simple.getValue();
					if (stroeDate != null && stroeDate != "") {
						//var begin = stroeDate + "-01";// 页面财务月份起始时间
						var dateSplit = stroeDate.split("-");
						//var end = this.getLastDay(dateSplit[0], dateSplit[1]);// 页面财务月份的结束时间
//						var prior_begin = this.getLastMonth(dateSplit[0],
//								dateSplit[1]);// 界面财务月份前一个月的月初时间
						var body = {};
//						body["year"] = begin;
//						body["end"] = end;
//						body["prior_begin"] = prior_begin;
						body["year"]=dateSplit[0];
						body["month"]=dateSplit[1];
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.dateQueryServicesId,
									serviceAction : this.dateQueryActionId,
									body : body
								});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							return;
						} else {
							var dates = r.json.body;
							if (dates.length != 2) {
								return;
							}
							cnd = [
									'and',
									[
											'ge',
											['$',
													"str(c.YSRQ,'yyyy-mm-dd hh24:mi:ss')"],
											['s', dates[0]]],
									[
											'le',
											['$',
													"str(c.YSRQ,'yyyy-mm-dd hh24:mi:ss')"],
											['s', dates[1]]]];
							if (initCnd) {
								cnd = ['and', initCnd, cnd];
							}

						}

					}
				}
				// 没选财务月份不让查询
				if (cnd == null) {
					return;
				}
				cnd = ['and', cnd, this.cnds];
				if (!this.checkInWayValue) {
					return;
				}
				cnd = ['and',
							['eq', ['$', 'a.RKFS'], ['i', this.checkInWayValue.fsId]],
							cnd];
				this.requestData.cnd = cnd;
				this.requestData.serviceId=this.fullserviceId;
				this.requestData.serviceAction=this.dataQueryActionId;
				this.refresh();
			},
			// 页面打开时默认的时间
			getDate : function() {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.dateQueryServicesId,
							serviceAction : this.initDateQueryActionId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.getDate);
					return;
				}
				return r.json.date;
			},
			// 获取该月最后一天
			getLastDay : function(year, month) {
				var new_year = year; // 取当前的年份
				var new_month = month;// 取下一个月的第一天，方便计算（最后一天不固定）
				if (new_month >= 12) // 如果当前大于12月，则年份转到下一年
				{
					new_month -= 12; // 月份减
					new_year++; // 年份增
				}
				var newnew_date = new Date(new_year, new_month, 1); // 取当年当月中的第一天
				return year
						+ "-"
						+ month
						+ "-"
						+ (new Date(newnew_date.getTime() - 1000 * 60 * 60 * 24))
								.getDate();// 获取当月最后一天日期
			},
			// 获取界面财务月份前一个月的月初时间
			getLastMonth : function(year, month) {
				var new_year = year; // 取当前的年份
				var new_month = month - 1;// 取上一个月
				if (month == 1) {
					new_month = 12;
					new_year--;
				}
				return new_year + "-" + new_month + "-01";
			},
			// 查看
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "未选中记录!", true);
					return;
				}
				var initDataBody = {};
				initDataBody["XTSB"] = r.data.XTSB;
				initDataBody["RKFS"] = r.data.RKFS;
				initDataBody["RKDH"] = r.data.RKDH;
				initDataBody["YSDH"] = r.data.YSDH;
				this.storageModule = this.createModule("storageModule",
						this.readRef);
				this.storageModule.on("save", this.onSave, this);
				this.storageModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.storageModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageModule.doRead(initDataBody);
				}

			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "查看";
				item.cmd = "look";
				this.doAction(item, e)

			},
			onClose : function() {
				this.getWin().hide();
			},
			doCancel : function() {
				if (this.storageModule) {
					return this.storageModule.doClose();
				}
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title||this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
			doPrint : function() {
				var module = this.createModule("storehouseinprint",
						this.refStorehouseListPrint)
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
					return;
				}
				module.xtsb = r.data.XTSB;
				module.rkfs = r.data.RKFS;
				module.rkdh = r.data.RKDH;
				module.pwd = r.data.PWD;
				module.fdjs = r.data.FDJS;
				module.initPanel();
				module.doPrint();
			}
		})