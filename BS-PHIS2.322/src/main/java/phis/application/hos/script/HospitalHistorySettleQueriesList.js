$package("phis.application.hos.script")

$import("phis.script.SimpleList","phis.application.mds.script.MySimplePagingToolbar")

phis.application.hos.script.HospitalHistorySettleQueriesList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.hos.script.HospitalHistorySettleQueriesList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalHistorySettleQueriesList,
		phis.script.SimpleList, {
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
				if (reocrd.data.JSXZ_text && reocrd.data.JSXZ_text=="全自费") {
					reocrd.data.JSXZ_text = "<span style='color:blue'>"
							+ reocrd.data.JSXZ_text + "</span>";
				}
				if (reocrd.data.FKFS_text && reocrd.data.FKFS_text!="现金") {
					reocrd.data.FKFS_text = "<span style='color:red'>"
							+ reocrd.data.FKFS_text + "</span>";
				}
				reocrd.data.JSJK = reocrd.data.ZFHJ - reocrd.data.JKHJ
				return v;
			},
			onStoreLoadData : function(store, records, ops) {
				//this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件				
				
				//zhaojian 2017-10-13 住院管理结算查询 增加费用总金额
				if (records.length == 0) {
					document.getElementById("JSCX_JE").innerHTML = "合计金额:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;现金:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;微信:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;作废:￥0.00";
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
				//zhaojian 2017-10-13 住院管理结算查询 增加费用总金额
				var data = this.grid.getStore().data;
				var FYHJ = 0;
				var XJHJ = 0;
				var WXHJ = 0;
				var ZFBHJ = 0;
				var ZFJE = 0;//作废金额
				for (var i = 0; i < data.length; i++) {
					var r = data.items[i];
					if(r.data.ZFPB==1){
						ZFJE += r.data.FYHJ;
						continue;
					}
					if (r.data.FYHJ) {
						FYHJ += r.data.FYHJ;
					}
					if (r.data.FKFS==6 && r.data.ZFHJ) {
						XJHJ += r.data.ZFHJ;
					}
					else if (r.data.FKFS==52 && r.data.ZFHJ) {
						WXHJ += r.data.ZFHJ;
					}
					else if (r.data.FKFS==53 && r.data.ZFHJ) {
						ZFBHJ += r.data.ZFHJ;
					}
				}
				document.getElementById("JSCX_JE").innerHTML = "合计金额:￥"
						+ FYHJ.toFixed(2)
						+ "&nbsp;&nbsp;&nbsp;&nbsp;现金:￥"
						+  XJHJ.toFixed(2)
						+ "&nbsp;&nbsp;&nbsp;&nbsp;微信:￥"
						+  WXHJ.toFixed(2)
						+ "&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥"
						+  ZFBHJ.toFixed(2)
						+ "&nbsp;&nbsp;&nbsp;&nbsp;作废:￥"
						+  ZFJE.toFixed(2);
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
					divHtml:"<div id='JSCX_JE' align='center' style='color:blue'>合计金额:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;现金:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;微信:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;支付宝:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;作废:￥0.00</div>"
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
			onDblClick : function(grid, index, e) {
					this.opener.doCards()
			}
		});