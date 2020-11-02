$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseStoreroomInventoryDetailsList = function(
		cfg) {
	cfg.autoLoadData = false;
	phis.application.sto.script.StorehouseStoreroomInventoryDetailsList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sto.script.StorehouseStoreroomInventoryDetailsList,
		phis.script.EditorList, {
			getData : function() {
				// var count = this.store.getCount();
				// var ret = new Array();
				// for (var i = 0; i < count; i++) {
				// var body = {};
				// body["KCSB"] = this.store.getAt(i).data.KCSB;
				// var spsl = this.store.getAt(i).data.SPSL;
				// if (spsl == null || spsl == undefined || spsl == "") {
				// spsl = 0;
				// }
				// body["SPSL"] = spsl;
				// ret.push(body);
				// }
				// return ret;
				return this.change_kcsl;
			},
			onBeforeCellEdit : function() {
				if (this.isRead) {
					return false;
				}
				return true;
			},
			onAfterCellEdit : function(it, record, field, v) {
				if (v == "" || v == undefined) {
					v = 0;
				}
				if (it.id == "SPSL") {
					var length = this.change_kcsl.length;
					for (var i = 0; i < length; i++) {
						if (record.get("KCSB") == this.change_kcsl[i].KCSB) {
							this.change_kcsl[i].SPSL = v;
						}
					}
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				} else {
					this.selectRow(this.selectedIndex);
				}
				var count=store.getCount();
				var length=this.change_kcsl.length;
				for(var i=0;i<count;i++){
					for(var j=0;j<length;j++){
					if(store.getAt(i).get("KCSB")==this.change_kcsl[j].KCSB){
					store.getAt(i).set("SPSL",this.change_kcsl[j].SPSL)
					}
					}
					
				}
			}
		})