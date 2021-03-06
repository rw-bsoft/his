$package("phis.application.sup.script")

$import("phis.script.EditorList")

phis.application.sup.script.InventoryKSEditorList = function(cfg) {
	cfg.selectOnFocus = true;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryKSEditorList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.InventoryKSEditorList,
				phis.script.EditorList,
				{
					loadData : function() {
						this.requestData.pageNo = 1;
						this.requestData.pageSize = 25;
						if (this.op) {
							if (this.op == "update") {
								if (this.requestData.cnd) {
									this.requestData.serviceId = "phis.inventoryService";
									this.requestData.serviceAction = "getPD02KSInfo";
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
								for ( var i = 0; i < this.kszc.length; i++) {
									var sign = 0;
									var r = new Record(this.kszc[i]);
									for ( var j = 0; j < n; j++) {
										var re = store.getAt(j);
										if (re.get("KCXH") == this.kszc[i].KCXH
												&& re.get("WZXH") == this.kszc[i].WZXH) {
											sign = 1
										}
									}
									if (sign == 0) {
										store.add([ r ])
									}
								}
							}
							if (n == 0) {
								for ( var i = 0; i < this.kszc.length; i++) {
									var r = new Record(this.kszc[i]);
									store.add([ r ])
								}
							}
						}
					},
					// 设置按钮状态
					setButtonsState : function(m, enable) {
						var btns;
						var btn;
						btns = this.grid.getTopToolbar();
						if (!btns) {
							return;
						}
						for ( var j = 0; j < m.length; j++) {
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
					doInit : function() {
						this.editRecords = [];
					}
				})