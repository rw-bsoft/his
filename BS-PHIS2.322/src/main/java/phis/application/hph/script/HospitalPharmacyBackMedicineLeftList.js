/**
 * 病区退药-退药处理左边LIST
 * 
 * @author : caijy
 */
$package("phis.application.hph.script")

$import("phis.script.SelectList")

phis.application.hph.script.HospitalPharmacyBackMedicineLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
//	cfg.cnds = ['and', ['eq', ['$', 'a.TJBZ'], ['i', 1]],
//			['isNull', ['s', 'is'], ['$', 'a.TYRQ']]];
	phis.application.hph.script.HospitalPharmacyBackMedicineLeftList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hph.script.HospitalPharmacyBackMedicineLeftList,
		phis.script.SelectList, {
			loadData:function(){
				this.requestData.serviceId=this.fullserviceId;
				this.requestData.serviceAction=this.queryServiceActionID;
				phis.application.hph.script.HospitalPharmacyBackMedicineLeftList.superclass.loadData.call(this);
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
			}
		});