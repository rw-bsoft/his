$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FamilySickBedPatientSelectionList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.showRowNumber = false;
	phis.application.fsb.script.FamilySickBedPatientSelectionList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedPatientSelectionList,
		phis.script.SimpleList, {
			onReady : function() {
				phis.application.fsb.script.FamilySickBedPatientSelectionList.superclass.onReady
						.call(this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			loadData : function() {
				this.requestData.serviceId = "phis.familySickBedPatientSelectionService";
				this.requestData.serviceAction = "getPatientList";
				this.requestData.schema = this.schema;
				phis.application.fsb.script.FamilySickBedPatientSelectionList.superclass.loadData
						.call(this);
			},
			onRowClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("click", record.data);
				}
			},
			onDblClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("dblClick", record.data);
				}
			},
			onStoreLoadData : function(store, records, ops) {
				if (records.length == 0) {
					document.getElementById("ZTJS_BRLB_1").innerHTML = "共 0 条";
					this.fireEvent("noRecord", this);
					return
				}

				var store = this.grid.getStore();
				var n = store.getCount()
				if (document.getElementById("ZTJS_BRLB_1")) {
					document.getElementById("ZTJS_BRLB_1").innerHTML = "共 " + n
							+ " 条";
				}
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='ZTJS_BRLB_1' align='center' style='color:blue'>共 0 条</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick(this.grid);
				}
			},
			doQuery : function(data) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var value = data.value.toUpperCase();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (r.get(data.key).toUpperCase().indexOf(value) >= 0) {
						this.grid.getSelectionModel().selectRow(i);
						var record = this.grid.store.getAt(i);
						if (record) {
							var rdata = record.data;
							this.fireEvent("click", rdata);
						}
						return;
					}
				}
			}
		});