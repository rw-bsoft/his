/**
 * 待发药处方药品详情列表
 * 
 * @author : caijy
 */
$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyBackMedicineDetailList = function(cfg) {
	// cfg.cnds=this.cnds=['eq',['s','1'],['s','2']];
	cfg.autoLoadData = this.autoLoadData = false;
	cfg.disablePagingTbr = true;
	//cfg.summaryable = true;
	phis.application.pha.script.PharmacyBackMedicineDetailList.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.pha.script.PharmacyBackMedicineDetailList,
		phis.script.SimpleList, {
			/*add by zhaojian 2017-05-31 处方明细中增加库存数量 begin*/
			loadData : function() {
				this.loading = true;
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.queryServiceAction;
				this.requestData.body = {
					cfsb : this.requestData.body.CFSB
				}
				phis.application.pha.script.PharmacyBackMedicineDetailList.superclass.loadData
						.call(this);
			},
			/* add by zhaojian 2017-05-31 处方明细中增加库存数量 end*/
			doNew : function() {
				this.clear()
				document.getElementById("QXFY").innerHTML = "总金额：";
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='QXFY' align='center' style='color:blue'>总金额：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
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
				var store = this.grid.getStore();
				var n = store.getCount()
				var hjje = 0;
				this.CFSBS = new Array();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					hjje += r.get("HJJE");
				}
				document.getElementById("QXFY").innerHTML = "总金额："
						+ hjje.toFixed(4);
			},
	onRenderer:function(value, metaData, r){
	if(r.get("ZFYP")==1){
	return '<span style="font-size:12px;color:red;">(自备)</span>' + value 
	}
	return value;
	}
		});