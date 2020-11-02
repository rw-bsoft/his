$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyApplyRefundList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds = ['eq', ['$', 'LYPB'], ['i', 1]];
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyApplyRefundList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.pha.script.PharmacyApplyRefundList, phis.script.SimpleList,
		{// 生成日期框
			getCndBar : function(items) {
//				var simple = new Ext.FormPanel({
//							labelWidth : 50,
//							title : '',
//							defaults : {},
//							defaultType : 'textfield',
//							width:190,
//							height:25,
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
				this.simple=new Ext.ux.form.Spinner({
										fieldLabel : '财务月份',
										name : 'storeDate',
										value : this.getDate(),
										strategy : {
											xtype : "month"
										}
									})
				
				return [this.simple];
			},
			// 条件查询
			doCndQuery : function(button, e, addNavCnd) {
					var initCnd = this.initCnd;
				var simple = this.simple;
				var cnd = null;
				if (simple) {
					var stroeDate = simple.getValue();
					if (stroeDate != null && stroeDate != "") {
						var begin = stroeDate + "-01";// 页面财务月份起始时间
						var dateSplit = stroeDate.split("-");
						var end = this.getLastDay(dateSplit[0], dateSplit[1]);// 页面财务月份的结束时间
						var prior_begin = this.getLastMonth(dateSplit[0],
								dateSplit[1]);// 界面财务月份前一个月的月初时间
						var body = {};
						body["begin"] = begin;
						body["end"] = end;
						body["prior_begin"] = prior_begin;
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
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
													"str(LYRQ,'yyyy-mm-dd hh24:mi:ss')"],
											['s', dates[0]]],
									[
											'le',
											['$',
													"str(LYRQ,'yyyy-mm-dd hh24:mi:ss')"],
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
				if (!this.yksb) {
					return;
				}
				var body={};
				body["cnd"]=cnd;
				body["yksb"]=this.yksb.yksb;
				this.requestData.body = body;
				this.refresh();
			},
			// 页面打开时默认的时间
			getDate : function() {
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initDateQueryActionId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.getDate);
					return;
				}
				return r.json.date;
			},
			// 查看
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["xtsb"] = r.data.XTSB;
				initDataBody["ckfs"] = r.data.CKFS;
				initDataBody["ckdh"] = r.data.CKDH;
				this.applyModule = this.createModule("applyModule",
						this.addRef);
				this.applyModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				this.applyModule.yksb = this.yksb;
				win.add(this.applyModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.applyModule.isRead = true;
					initDataBody["czpb"] = 3;
					this.applyModule.loadData(initDataBody);
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
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				if(r.data.LYPB==1){
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/grid.png'/>"
				//return "<img src='images/grid.png'/>";
				}
			}
		})