$package("phis.application.fsb.script")

$import("phis.script.SelectList")

phis.application.fsb.script.FamilySickBedDrugSubmissionRightList = function(cfg) {
	phis.application.fsb.script.FamilySickBedDrugSubmissionRightList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedDrugSubmissionRightList,
		phis.script.SelectList, {
			onStoreLoadData : function(store, records, ops) {
				phis.script.SelectList.superclass.onStoreLoadData.call(this,
						store, records, ops)
				if (records.length == 0 || !this.selects || !this.mutiSelect) {
					return
				}
				this.sm.suspendEvents(false);
				this.grid.getSelectionModel().selectAll();
				this.sm.resumeEvents();
				var hd = this.grid.body.select("div.x-grid3-hd-checker")
						.first();
				hd.addClass('x-grid3-hd-checker-on');
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
							this.fireEvent("selectRecord", this);
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							delete this.selects[record.id]
							this.fireEvent("selectRecord", this);
						}, this)
				var _ctr = this;
				this.sm.selectAll = function() {
					if (this.isLocked()) {
						return
					}
					_ctr.selects = {};
					var count = _ctr.store.getCount();
					this.suspendEvents(false);
					this.selections.clear();
					for (var i = 0; i < count; i++) {
						this.selectRow(i, true)
						_ctr.selects[_ctr.store.getAt(i).id] = _ctr.store
								.getAt(i)
					}
					this.resumeEvents();
					_ctr.fireEvent("selectRecord", this);
				}

				this.sm.clearSelections = function(a) {
					_ctr.selects = {};
					if (this.isLocked()) {
						return
					}
					this.suspendEvents(false);
					if (a !== true) {
						var c = this.grid.store, b = this.selections;
						b.each(function(d) {
									this.deselectRow(c.indexOfId(d.id))
								}, this);
						b.clear()
					} else {
						this.selections.clear()
					}
					this.last = false
					this.resumeEvents();
					_ctr.fireEvent("selectRecord", this);
				}
				return [sm].concat(cm)
			},
			onRenderJe : function(value, metaData, r) {
				return (parseFloat(r.get("YCSL")) * parseFloat(r.get("YPDJ")))
						.toFixed(2)
			},
			onRenderCs : function(value, metaData, r) {
				if (r.get("YYTS") == null || r.get("YYTS") == "") {
					return 0;
				}
				return parseFloat(r.get("YYTS")) * parseFloat(r.get("MRCS"))
			}
		})