/**
 * 待发药处方药品详情列表
 * 
 * @author : caijy
 */
$package("phis.application.pha.script")

$import("phis.script.EditorList")

phis.application.pha.script.PharmacyBackPartMedicineDetailList = function(cfg) {
	// cfg.cnds=this.cnds=['eq',['s','1'],['s','2']];
	cfg.autoLoadData = this.autoLoadData = false;
	cfg.disablePagingTbr = true;
	//cfg.summaryable = true;
	phis.application.pha.script.PharmacyBackPartMedicineDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);

}

Ext.extend(phis.application.pha.script.PharmacyBackPartMedicineDetailList,
		phis.script.EditorList, {
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
			onBeforeCellEdit:function(it, record, field, v){
			if(it.id!="TYSL"){
			return false;
			}
			if(record.get("ZFYP")==1){
			return false;
			}
			return true;
			},
			onAfterCellEdit : function(it, record, field, v) {
			if(v>parseFloat(record.get("YPSL"))){
			MyMessageTip.msg("提示", "退药数量不能超过发药数量", true);
			record.set("TYSL",parseFloat(record.get("YPSL")));
				return;
			}
			},
			getTymx:function(){
			var count =this.store.getCount();
			var body={};
			for(var i=0;i<count;i++){
			var tysl=this.store.getAt(i).get("TYSL");
			if(tysl!=null&&tysl!=""&&tysl!=undefined&&tysl!=0){
			body[this.store.getAt(i).get("SBXH")]=tysl;
			}
			}
			return body;
			},
			//判断是否全退
			getSfqt:function(){
			var count =this.store.getCount();
			var tag=true;
			for(var i=0;i<count;i++){
			if(this.store.getAt(i).get("TYSL")!=this.store.getAt(i).get("YPSL")){
				tag=false;
			}
			}
			return tag;
			},
	onRenderer:function(value, metaData, r){
	if(r.get("ZFYP")==1){
	return '<span style="font-size:12px;color:red;">(自备)</span>' + value 
	}
	return value;
	}
		});