/**
 * 家床发药-右边发药明细
 * 
 * @author : caijy
 */
$package("phis.application.fsb.script")

$import("phis.script.SelectList")

phis.application.fsb.script.FamilySickBedDispensingRightDetailList = function(
		cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.cnds = ['and', ['eq', ['$', 'a.FYBZ'], ['i', 0]],
			['eq', ['$', 'd.FYBZ'], ['i', 0]]];
	phis.application.fsb.script.FamilySickBedDispensingRightDetailList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(phis.application.fsb.script.FamilySickBedDispensingRightDetailList,
		phis.script.SelectList, {
			onRenderer : function(value, metaData, r) {
				return (r.data.YCSL * (r.data.YPDJ)).toFixed(2)
			},
			onStoreBeforeLoad : function(store, op) {
				var r = this.getSelectedRecord()
				var n = this.store.indexOf(r)
				if (n > -1) {
					this.selectedIndex = n
				}
				this.fireEvent("BeforeLoadData");
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
							this.fireEvent("fy", 2);
							this.checkAll(sm, rowIndex, record)
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							delete this.selects[record.json.JLXH]
							if (this.getSelectedRecords().length == 0) {
								this.fireEvent("fy", 2);
							}
							this.unCheckAll(sm, rowIndex, record)
						}, this)
				return [sm].concat(cm)
			},
			// 选中一条记录,该记录下面的相同医嘱的全选
			checkAll : function(sm, rowIndex, record) {
				var yzxh = record.data.YZXH;
				var count = this.store.getCount();
				if (rowIndex + 1 < count) {
					var data = this.store.getAt(rowIndex + 1).data;
					if (yzxh == data["YZXH"]) {
						sm.selectRow(rowIndex + 1, true);
					}
				}
			},
			// 取消选中一条,该记录上面的相同的医嘱全部取消选中
			unCheckAll : function(sm, rowIndex, record) {
				var yzxh = record.data.YZXH;
				if (rowIndex - 1 >= 0) {
					var data = this.store.getAt(rowIndex - 1).data;
					if (yzxh == data["YZXH"]) {
						sm.deselectRow(rowIndex - 1);
					}
				}
			},
			onLoadData : function() {
				this.fireEvent("loading", 2);
			},
			onRendererNull : function(value, metaData, r) {
				if (value == null || value == "null") {
					return "";
				} else {
					return value;
				}
			}
		});