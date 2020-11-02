$package("phis.application.reg.script");

$import("phis.script.SimpleList","phis.application.mds.script.MySimplePagingToolbar")

phis.application.reg.script.RegisteredQueriesList = function(cfg) {
	phis.application.reg.script.RegisteredQueriesList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.reg.script.RegisteredQueriesList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.script.SimpleList.superclass.onReady.call(this);
				this.loadData();
			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				var body = {
					"BRXZ" : this.mainApp.BRXZ
				};
				this.requestData.serviceId = "phis.registeredManagementService";
				this.requestData.serviceAction = "registeredQuery";
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar();
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
				this.resetButtons();
			},
			//zhaojian 2017-10-13 住院管理结算查询 增加费用总金额
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : this.pageSize || 25, // ** modify by yzh ,
					// 2010-06-18 **
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录",
					divHtml:"<div id='TOP_SHOW' align='center' style='color:blue'>合计金额:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;现金:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;微信:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥0.00</div>"
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
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("TOP_SHOW").innerHTML = "合计金额:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;现金:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;微信:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥0.00";
					this.fireEvent("noRecord", this);
					return
				}
				if(this.store && this.store.reader && this.store.reader.jsonData && this.store.reader.jsonData.totalbody){
					var totalbody=this.store.reader.jsonData.totalbody;
					document.getElementById("TOP_SHOW").innerHTML = "合计总金额:￥"+totalbody.HJJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;现金:￥"+totalbody.XJJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;微信:￥"+totalbody.WXJE+
					"&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥"+totalbody.ZFBJE;
				}
			}
		});