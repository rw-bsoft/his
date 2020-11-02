/**
 * 采购入库详细list
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailList");

phis.application.sto.script.StorehouseMedicinesConservationDetailList = function(cfg) {
	//cfg.columnNum=3;//换行
	cfg.count=9;
	cfg.toColumnNum=9;//换行到下一行的哪列
	cfg.labelText=" ";//底部合计
	cfg.disablePagingTbr=false;
	this.editorData=[];
	phis.application.sto.script.StorehouseMedicinesConservationDetailList.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesConservationDetailList,
				phis.application.sto.script.StorehouseMySimpleDetailList, {
			// 数量操作后
			onAfterCellEdit : function(it, record, field, v) {
				if(it.id=="SHSL"){
				if(parseFloat(v)>parseFloat(record.get("YKKC"))){
				MyMessageTip.msg("提示", "破损数量不能大于库存总量", true);
				record.set("SHSL",parseFloat(record.get("YKKC")));
				record.set("KCSL",0);
				}else{
				record.set("KCSL",parseFloat(record.get("YKKC"))-parseFloat(v));
				}
				var length=this.editorData.length;
				var b=false;
				for(i=0;i<length;i++){
				var yh02=this.editorData[i];
				if(yh02.KCSB==record.get("KCSB")){
				yh02.SHSL=record.get(SHSL);
				yh02.KCSL=record.get(KCSL);
				b=true;
				break;
				}
				}
				if(!b){
				this.editorData.push(record.data);
				}
				}
				
			},
			loadData:function(){
			this.requestData.serviceId=this.fullServiceId;
			this.requestData.serviceAction=this.queryActionId;
			this.requestData.op=this.op;
			phis.application.sto.script.StorehouseMedicinesConservationDetailList.superclass.loadData.call(this);
			},
			//新增
			doNew:function(){
			this.clear();
			this.editorData=[];
			this.requestData.body={"YHDH":0};
			this.loadData();
			},
			doJshj : function() {
			},
			expansion : function(cfg) {
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
				var count=store.getCount();
				for(var i=0;i<count;i++){
				var length=this.editorData.length;
				for(var j=0;j<length;j++){
				if(store.getAt(i).get("KCSB")==this.editorData[j].KCSB){
				store.getAt(i).set("SHSL",this.editorData[j].SHSL);
				store.getAt(i).set("KCSL",this.editorData[j].KCSL);
				break;
				}
				}
				}
			}
});