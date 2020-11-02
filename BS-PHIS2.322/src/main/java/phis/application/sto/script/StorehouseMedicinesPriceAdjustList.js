$package("phis.application.sto.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sto.script.StorehouseMedicinesPriceAdjustList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.cnds = ['eq', ['$', 'ZYPB'], ['i', 1]];
	cfg.modal = this.modal = true;
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = true
	cfg.gridDDGroup = true;
	phis.application.sto.script.StorehouseMedicinesPriceAdjustList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesPriceAdjustList,
		phis.script.SimpleList, {
			getCndBar : function(items) {
				// var simple = new Ext.FormPanel({
				// labelWidth : 50,
				// title : '',
				// defaults : {},
				// defaultType : 'textfield',
				// width : 190,
				// height : 25,
				// items : [ new Ext.ux.form.Spinner({
				// fieldLabel : '财务月份',
				// name : 'storeDate',
				// value : this.getDate(),
				// strategy : {
				// xtype : "month"
				// }
				// }) ]
				// });
				// this.simple = simple;
				this.simple = new Ext.ux.form.Spinner({
							fieldLabel : '财务月份',
							name : 'storeDate',
							value : this.getDate(),
							strategy : {
								xtype : "month"
							}
						})
				return [this.simple];
			},

			loadData : function() {
				var initCnd = this.initCnd;
				var simple = this.simple;
				var cnd = null;
				if (simple) {
					var stroeDate = simple.getValue();
					if (stroeDate != null && stroeDate != "") {
						var body = {};
						var dateSplit = stroeDate.split("-");
						body["year"] = dateSplit[0];
						body["month"] = dateSplit[1];
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
													"str(ZXRQ,'yyyy-mm-dd hh24:mi:ss')"],
											['s', dates[1]]],
									[
											'le',
											['$',
													"str(ZXRQ,'yyyy-mm-dd hh24:mi:ss')"],
											['s', dates[0]]]];
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
				if (this.priceAdjustWayValue) {
					cnd = [
							'and',
							['eq', ['$', 'TJFS'],
									['i', this.priceAdjustWayValue]], cnd];
				}
				this.requestData.cnd = cnd;
				phis.application.sto.script.StorehouseMedicinesPriceAdjustList.superclass.loadData
						.call(this);
			},
			onRenderer : function(value, metaData, r) {
				return "<img src='images/grid.png'/>";
			},
			// 页面打开时默认的时间
			getDate : function() {
				// var r = phis.script.rmi.miniJsonRequestSync({
				// serviceId : this.dateQueryServicesId,
				// serviceAction : this.initDateQueryActionId
				// });
				// if (r.code > 300) {
				// this.processReturnMsg(r.code, r.msg,
				// this.getDate);
				// return;
				// }
				// return r.json.date;
				var myDate = new Date();
				var month = myDate.getMonth() + 1;
				if (month < 10) {
					month = "0" + month;
				}
				return myDate.getFullYear() + "-" + month;
			},
			// 获取该月最后一天
			getLastDay : function(year, month) {
				var new_year = year; // 取当前的年份
				var new_month = month + 1;// 取下一个月的第一天，方便计算（最后一天不固定）
				if (new_month > 12) // 如果当前大于12月，则年份转到下一年
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
					return;
				}
				var initDataBody = {};
				initDataBody["TJFS"] = r.data.TJFS;
				initDataBody["TJDH"] = r.data.TJDH;
				initDataBody["JGID"] = r.data.JGID;
				initDataBody["XTSB"] = r.data.XTSB;
				this.priceAdjustModule = this.createModule("priceAdjustModule",
						this.readRef);
				this.priceAdjustModule.on("save", this.onSave, this);
				// modify by gejj 添加关闭触发事件
				this.priceAdjustModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.priceAdjustModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.priceAdjustModule.doRead(initDataBody);
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
						title : this.title || this.name,
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
					win.on("hide", function() { // ** add by yzh
								// 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
			//测试调价增加导出功能
			doPrint:function(){
				var r = this.getSelectedRecord()
			    if (r == null) {
				return;
		     	}
				var url = "resources/phis.prints.jrxml.StorehouseMedicinesPriceAdjust.print?type=3&TJFS="
        				+  r.data.TJFS+"&TJDH="+r.data.TJDH+"&XTSB="+r.data.XTSB+"&JGID="+r.data.JGID;
				if(this.flag)
				{
					url+="&flag=1";
				}
				var printWin=window.open(
								 url,
								 "",
								 "height="
								 + (screen.height - 100)
								 + ", width="
								 + (screen.width - 10)
								 + ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			}
			//原调价打印功能
//			doPrint : function() {
//				var r = this.getSelectedRecord()
//				if (r == null) {
//					return;
//				}
//				var url = "resources/phis.prints.jrxml.StorehouseMedicinesPriceAdjust.print?type=1&TJFS="
//						+  r.data.TJFS+"&TJDH="+r.data.TJDH+"&XTSB="+r.data.XTSB+"&JGID="+r.data.JGID;
//				var LODOP = getLodop();
//				LODOP.PRINT_INIT("打印控件");
//				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
//				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
//								{
//									url : url,
//									httpMethod : "get"
//								}));
//				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
//				LODOP.PREVIEW();
//			}
		})