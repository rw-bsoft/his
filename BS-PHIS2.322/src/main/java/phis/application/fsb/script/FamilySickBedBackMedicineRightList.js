$package("phis.application.fsb.script")

$import("phis.script.SelectList")

phis.application.fsb.script.FamilySickBedBackMedicineRightList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.cnds = ['and', ['eq', ['$', 'a.TJBZ'], ['i', 1]],
			['isNull', ['$', 'a.TYRQ']]];
	phis.application.fsb.script.FamilySickBedBackMedicineRightList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedBackMedicineRightList,
		phis.script.SelectList, {
			doNew : function() {
				this.clear();
				this.clearSelect();
			},
			onRenderer : function(value, metaData, r) {
				// alert(Math.round(r.data.YPSL * (-r.data.YPJG)*100))
				return (-Math.round(r.data.YPSL * (-r.data.YPJG) * 100) / 100).toFixed(2)
			},
			// 加载数据时全选
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				this.sm.selectAll();
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
							this.selects[record.json.JLXH] = record
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							delete this.selects[record.json.JLXH]
						}, this)
				return [sm].concat(cm)
			},
		onRendererNull : function(value, metaData, r) {
				if(value==null||value=="null"){
				return "";}else{
				return value;
				}
			}
		});