/**
 * 待发药处方列表
 * 
 * @author : caijy
 */
$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyBackMedicineList = function(cfg) {
//	this.initCnd = [
//			'and',
//			[
//					'and',
//					['and', ['eq', ['$', 'FYBZ'], ['i', 1]],
//							['ne', ['$', 'TYBZ'], ['i', 1]]],
//					['notNull', ['$', 'a.FPHM']]],
//			['eq', ['$', 'ZFPB'], ['i', 0]]];
	cfg.autoLoadData = this.autoLoadData = false;
	cfg.queryComboBoxWidth = 80;
	cfg.queryWidth = 80;
	phis.application.pha.script.PharmacyBackMedicineList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.pha.script.PharmacyBackMedicineList,
		phis.script.SimpleList, {
			onRowClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("cfSelect", record);
				}
			},
			onESCKey : function() {
				this.fireEvent("cfCancleSelect");
			},
			// 查询
			doCndQuery : function(button, e, addNavCnd) {
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					if (initCnd)
						cnd = initCnd
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.fireEvent("query", this);
					return
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				cnd[0] = 'like'
				cnd.push(['s', v + '%'])
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.fireEvent("query", this);
			},
			// 上下时自动查询明细
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick(this.grid);
				}
			},
			// 刚打开页面时候默认选中第一条数据
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.fireEvent("noRecord", this);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick(this.grid);
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick(this.grid);
				}
			}

		});