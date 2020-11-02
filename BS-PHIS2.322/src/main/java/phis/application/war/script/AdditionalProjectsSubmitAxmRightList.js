$package("phis.application.war.script")
$import("phis.script.EditorList")

phis.application.war.script.AdditionalProjectsSubmitAxmRightList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.war.script.AdditionalProjectsSubmitAxmRightList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadDate, this);
	this.on("afterCellEdit", this.afterGridEdit, this)
}
Ext.extend(phis.application.war.script.AdditionalProjectsSubmitAxmRightList,
		phis.script.EditorList, {
			init : function() {
				this.addEvents({
							"select" : true
						})
				if (this.mutiSelect) {
					this.selectFirst = false
				}
				this.selects = {}
				this.singleSelect = {}
				phis.script.SelectList.superclass.init.call(this)
			},
			loadData : function() {
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.war.script.AdditionalProjectsSubmitAxmRightList.superclass.loadData
						.call(this);
			},
			onStoreLoadData : function(store, records, ops) {
				phis.script.SelectList.superclass.onStoreLoadData.call(this,
						store, records, ops)
				this.clearSelect();
				this.fireEvent("afterLoadData", this)
				if (records.length == 0) {
					return
				}
				this.grid.getSelectionModel().selectAll();
			},
			getCM : function(items) {
				var cm = phis.application.war.script.AdditionalProjectsSubmitAxmRightList.superclass.getCM
						.call(this, items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : this.checkOnly,
							singleSelect : false
						})
				this.sm = sm
				sm.on("rowselect", function(sm, rowIndex, record) {
								this.selects[record.id] = record
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
								delete this.selects[record.id]
						}, this)
				return [sm].concat(cm)
			},
			getSelectedRecords : function() {
				var records = []
				for (var id in this.selects) {
					records.push(this.selects[id])
				}
				return records
			},
			afterLoadDate : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					var YCSL = r.get("YCSL");
					var FYCS = r.get("FYCS");
					var YPDJ = r.get("YPDJ");
					r.set("QRSL", YCSL * FYCS);
					r.set("ZJE", YCSL * FYCS * YPDJ);
				}
				this.getYzzh();
			},
			afterGridEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var curRow = cell[0];
				for (var i = 0; i < this.store.getCount(); i++) {
					if (i != curRow) {
						var YPDJ = record.get("YPDJ");
						var QRSL = record.get("QRSL");
						record.set("ZJE", QRSL * YPDJ);
						this.grid.startEditing(curRow, 4);
					}
				}
				return true;
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			},
			doRender : function(v, params, record) {
				var zje = record.get("FYCS") * record.get("YCSL")
				return parseFloat(zje).toFixed(2);
			},
			getYzzh : function() {
				yzzh = 1;
				var store = this.grid.getStore();
				var n = store.getCount()
				var YZZHs = [];
				for (var i = 0; i < n; i++) {
					if (i == 0) {
						var r = store.getAt(i)
						yzzh = r.get('YZZH') % 2 + 1;
						YZZHs.push(yzzh)
					} else {
						var r1 = store.getAt(i - 1)
						var r = store.getAt(i)
						if (r1.get('YZZH') == r.get('YZZH')) {
							YZZHs.push(yzzh)
						} else {
							YZZHs.push(++yzzh)
						}
					}
				}
				for (var i = 0; i < YZZHs.length; i++) {
					var r = store.getAt(i);
					r.set('YZZH', YZZHs[i]);
				}
			},
			clearSelect : function() {
				this.selects = {};
				this.singleSelect = {};
				this.sm.clearSelections();
				var checker = Ext.fly(this.grid.getView().innerHd)
						.child('.x-grid3-hd-checker')
				if (checker) {
					checker.removeClass('x-grid3-hd-checker-on');
				}
				// Ext.fly(this.grid.getView().innerHd).child('.x-grid3-hd-checker').removeClass('x-grid3-hd-checker-on');
			}
		})