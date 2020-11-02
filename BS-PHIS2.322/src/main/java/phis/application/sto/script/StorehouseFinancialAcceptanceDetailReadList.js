$package("phis.application.sto.script")

$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseFinancialAcceptanceDetailReadList,
		phis.script.SimpleList, {
			onRenderer : function(value, metaData, r) {
				return (parseFloat(r.data.LSJE) - parseFloat(r.data.JHHJ))
						.toFixed(4);
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				this.label = new Ext.form.Label({
					text : "合计   零售金额:0  进货金额:0 进销差价:0"
				})
				cfg.bbar = [];
				cfg.bbar.push(this.label);
			},
			// 数据加载时计算总金额
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
					this.doJshj();
			},
			doJshj : function() {
				var n = this.store.getCount()
				var hjje = 0;
				var lsje = 0;
				for (var i = 0; i < n; i++) {
					var r = this.store.getAt(i);
					hjje += parseFloat(r.get("JHHJ"));
					lsje += parseFloat(r.get("LSJE"));
				}
				this.label.setText("合计   零售金额:"+ lsje.toFixed(4) + " 进货金额:" + hjje.toFixed(4)+ " 进销差价:" + (lsje-hjje).toFixed(4));
				//this.label.style='color:blue';
//				document.getElementById("YK_RK02_read").innerHTML = "合计   零售金额:"
//						+ lsje.toFixed(2) + " 进货金额:" + hjje.toFixed(2)
//						+ " 进销差价:" + (lsje-hjje).toFixed(2);
			}
		})