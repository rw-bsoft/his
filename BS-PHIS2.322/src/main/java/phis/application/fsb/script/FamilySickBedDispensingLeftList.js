/**
 * 家床发药-待发药病人选择List
 * 
 * @author : caijy
 */
$package("phis.application.fsb.script")

$import("phis.script.SelectList",
		"phis.application.mds.script.MySimpleListCommon")

phis.application.fsb.script.FamilySickBedDispensingLeftList = function(cfg) {
	Ext.apply(this, phis.application.mds.script.MySimpleListCommon)
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.cnds = [ 'eq', [ '$', 'a.FYBZ' ], [ 'i', 0 ] ];
	cfg.initCnd = [ 'eq', [ '$', '1' ], [ 'i', 2 ] ];
	phis.application.fsb.script.FamilySickBedDispensingLeftList.superclass.constructor
			.apply(this, [ cfg ])
}

Ext.extend(phis.application.fsb.script.FamilySickBedDispensingLeftList,
		phis.script.SelectList, {
			// 单击时调出医嘱发药和汇总发药
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
					singleSelect : !this.mutiSelect,
					header : ""
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
				return [ sm ].concat(cm)
			},
			// 重写是因为框架不支持引用的字段不用查询
			getCndBar : function(items) {
				return [];
			},
			onStoreLoadData:function(store,records,ops){
				this.fireEvent("loadData", store)// ** 不管是否有记录，都fire出该事件
				if(records.length == 0){
					return
				}
				this.totalCount = store.getTotalCount()
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
					this.selectedIndex = 0;
				}
				else{
					this.selectRow(this.selectedIndex);
				}
			},
			getData:function(){
			var records=this.getSelectedRecords();
			var data=[];
			for(var i=0;i<records.length;i++){
			data.push(records[i].data)
			}
			return data;
			}
		});