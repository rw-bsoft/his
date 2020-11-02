$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.InventoryInKSEditorList = function(cfg) {
	cfg.selectOnFocus = true;
    cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryInKSEditorList.superclass.constructor.apply(
			this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
}
Ext.extend(phis.application.sup.script.InventoryInKSEditorList,
		phis.script.EditorList, {
			loadData : function() {
				this.requestData.pageNo = 1;
				this.requestData.pageSize = 25;
				if (this.op) {
					if (this.op == "update") {
						if (this.requestData.cnd) {
							this.requestData.serviceId = "phis.inventoryService";
							this.requestData.serviceAction = "getLRMXKSInfo";
						}
					}
				}
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			doCre : function() {
				var store = this.grid.getStore();
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)

				if (this.kszc) {
					var n = store.getCount()
					if (n > 0) {
						for (var i = 0; i < this.kszc.length; i++) {
							var sign = 0;
							var r = new Record(this.kszc[i]);
							for (var j = 0; j < n; j++) {
								var re = store.getAt(j);
								if (re.get("MXXH") == this.kszc[i].MXXH) {
									sign = 1
								}
							}
							if (sign == 0) {
								store.add([r])
							}
						}
					}
					if (n == 0) {
						for (var i = 0; i < this.kszc.length; i++) {
							var r = new Record(this.kszc[i]);
							store.add([r])
						}
					}
				}
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				btns = this.grid.getTopToolbar();
				if (!btns) {
					return;
				}
				for (var j = 0; j < m.length; j++) {
					if (!isNaN(m[j])) {
						btn = btns.items.item(m[j]);
					} else {
						btn = btns.find("cmd", m[j]);
						btn = btn[0];
					}
					if (btn) {
						(enable) ? btn.enable() : btn.disable();
					}
				}
			},
			onAfterCellEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				if (it.id == "PCSL") {
					var pcje = 0;
					if (((v != null && v != "") || v == 0)) {
						var pcje = (v * record.get("WZJG")).toFixed(2);
					}
					record.set("PCJE", pcje);
					if (!this.editRecords) {
						this.editRecords = [];
					}
					this.editRecords.push(record.data);
				}
			},
			doInit : function() {
				this.editRecords = [];
			}
		})