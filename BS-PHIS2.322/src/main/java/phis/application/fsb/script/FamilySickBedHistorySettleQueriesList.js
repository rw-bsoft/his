$package("phis.application.fsb.script")

$import("phis.script.SimpleList", "phis.script.SimpleForm",
"phis.application.ivc.script.ClinicInvoiceQueriesPagingToolbar")

phis.application.fsb.script.FamilySickBedHistorySettleQueriesList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FamilySickBedHistorySettleQueriesList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedHistorySettleQueriesList,
		phis.script.SimpleList, {
	expansion : function(cfg) {
		var labelText = new Ext.form.Label({
				html : "<div id='jcjscxTOP_SHOW' align='center' style='color:blue'>结算次数:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;均次费用:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;平均床日:&nbsp;&nbsp;&nbsp;&nbsp;</div>"
				});
		var otherTbar = new Ext.Toolbar();
		otherTbar.add(labelText);
		cfg.bbar = [otherTbar];
	},
	onReady : function() {
		phis.script.SimpleList.superclass.onReady.call(this);
		// otherTbar.render(this.grid.bbar);
		var pageBar = this.getPagingToolbar(this.store);
		pageBar.render(this.grid.bbar);
//		this.loadData();
	},
	loadData : function() {
		this.clear(); // ** add by yzh , 2010-06-09 **
//		this.requestData.serviceId = "phis.fsbPaymentProcessingService";
//		this.requestData.serviceAction = "queryJsxx";
		if (this.store) {
			if (this.disablePagingTbr) {
				this.store.load()
			} else {
				var pt = this.pagingToolbar;
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		}
		// ** add by yzh **
		this.resetButtons();
	},
			zfpbRender : function(v, params, reocrd) {
				if (reocrd.data.ZFRQ) {
					reocrd.data.ZFRQ = "<span style='color:red'>"
							+ reocrd.data.ZFRQ + "</span>";
				}
				if (reocrd.data.ZFGH_text) {
					reocrd.data.ZFGH_text = "<span style='color:red'>"
							+ reocrd.data.ZFGH_text + "</span>";
				}
				if (reocrd.data.ZFPB_text) {
					reocrd.data.ZFPB_text = "<span style='color:red'>"
							+ reocrd.data.ZFPB_text + "</span>";
				}
				reocrd.data.JSJK = reocrd.data.ZFHJ - reocrd.data.JKHJ
				var sDate1 = reocrd.data.KSRQ;
				var sDate2 = reocrd.data.JSRQ;
				var oDate1,oDate2,iDays;
				var navigatorName = "Microsoft Internet Explorer";
				if (sDate1) {
					if (navigator.appName == navigatorName) {
						oDate1 = new Date(Date.parse(sDate1.substring(0, 10)
								.replace(/-/, "/")))
					} else {
						oDate1 = new Date(sDate1.substring(0, 10))
					}
				} else {
					oDate1 = new Date();
				}
				if (sDate2) {
					if (navigator.appName == navigatorName) {
						oDate2 = new Date(Date.parse(sDate2.substring(0, 10)
								.replace(/-/, "/")));
					} else {
						oDate2 = new Date(sDate2.substring(0, 10))
					}
				} else {
					oDate2 = new Date();
				}
				iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60
						/ 24)
				reocrd.data.JCTS = iDays;
				return v;
			},
			onDblClick : function(grid, index, e) {
					this.opener.doCards()
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("jcjscxTOP_SHOW").innerHTML = "结算次数:0&nbsp;&nbsp;&nbsp;&nbsp;均次费用:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;均次自费:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;平均床日:0&nbsp;&nbsp;&nbsp;&nbsp;";
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
				this.requestData.cnds
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.fsbPaymentProcessingService",
					serviceAction : "queryJsxx",
						cnd : this.requestData.cnd
					}); 	
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				document.getElementById("jcjscxTOP_SHOW").innerHTML = "结算次数:"
						+ ret.json.body.JSCS + "&nbsp;&nbsp;&nbsp;&nbsp;均次费用:￥"
						+ ret.json.body.JCFY.toFixed(2) + "&nbsp;&nbsp;&nbsp;&nbsp;均次自费:￥"
						+ ret.json.body.JCZF.toFixed(2) + "&nbsp;&nbsp;&nbsp;&nbsp;平均床日:"
						+ ret.json.body.PJCR.toFixed(2);
			}
		});