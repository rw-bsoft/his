$package("phis.application.fsb.script")

$import("phis.script.SelectList")

phis.application.fsb.script.FamilySickBedBackMedicineLeftList = function(cfg) {
	//cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.fsb.script.FamilySickBedBackMedicineLeftList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedBackMedicineLeftList,
		phis.script.SelectList, {
			loadData:function(){
				this.requestData.serviceId=this.fullserviceId;
				this.requestData.serviceAction=this.queryServiceActionID;
				phis.application.fsb.script.FamilySickBedBackMedicineLeftList.superclass.loadData.call(this);
			}
			,
			// 单击时调出退药处理和退回病区
			onRowClick : function() {
				var r = this.getSelectedRecords();
				if (r == null) {
					return;
				}
				this.fireEvent("recordClick", r);
			},
			doNew : function() {
				this.clear();
				this.clearSelect();
			},
			getCM : function(items) {
				var cm = phis.script.SelectList.superclass.getCM.call(this,
						items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : this.checkOnly,
							singleSelect : !this.mutiSelect
						})
				this.sm = sm
				sm.on("rowselect", function(sm, rowIndex, record) {
								this.selects[record.id] = record
								this.onRowClick();
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
								delete this.selects[record.id]
								this.onRowClick();
						}, this)
				return [sm].concat(cm)
			},
			getRecords:function(){
			var record= this.getSelectedRecords();
			var ret=new Array();
			for(var i=0;i<record.length;i++){
			ret.push(record[i].json);
			}
			return ret;
			}
		});