$package("phis.application.ivc.script")

$import("phis.script.SimpleList", "phis.script.SimpleForm",
		"phis.application.ivc.script.ClinicInvoiceQueriesPagingToolbar")

phis.application.ivc.script.ClinicReceivablesInvoiceQueriesList = function(cfg) {
	phis.application.ivc.script.ClinicReceivablesInvoiceQueriesList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.ivc.script.ClinicReceivablesInvoiceQueriesList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var labelText = new Ext.form.Label({
						html : "<div id='TOP_SHOW' align='center' style='color:blue'>合计金额:￥0.00</div>"
						});
				var otherTbar = new Ext.Toolbar();
				otherTbar.add(labelText);
				cfg.bbar = [otherTbar];
			},
			onReady : function() {
				phis.script.SimpleList.superclass.onReady.call(this);
				var pageBar = this.getPagingToolbar(this.store);
				pageBar.render(this.grid.bbar);
				this.loadData();
			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				var body = {
					"BRXZ" : this.mainApp.BRXZ
				};
				this.requestData.serviceId = "phis.clinicChargesProcessingService";
				this.requestData.serviceAction = "queryReceivablesInvoice";
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
			onDblClick : function(grid, index, e) {
				this.opener.doDj();
			},
			onStoreLoadData : function(store, records, ops) {
				if (records.length == 0) {
					document.getElementById("TOP_SHOW").innerHTML = "合计金额:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;现金:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;微信:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;账户:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;其它:￥0.00";
					this.fireEvent("noRecord", this);
					return
				}
				//改成展示后台统计的所有费用信息
				if(this.store && this.store.reader && this.store.reader.jsonData && this.store.reader.jsonData.totalbody){
					var totalbody=this.store.reader.jsonData.totalbody;
					document.getElementById("TOP_SHOW").innerHTML = "合计总金额:￥"+totalbody.ZJJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;现金:￥"+totalbody.XJJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;微信:￥"+totalbody.WXJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥"+totalbody.ZFBJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;账户:￥"+totalbody.ZHJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;其它:￥"+ totalbody.QTYS+
					"&nbsp;&nbsp;&nbsp;&nbsp;货币误差:￥"+totalbody.HBWC;
				}
			}
		});