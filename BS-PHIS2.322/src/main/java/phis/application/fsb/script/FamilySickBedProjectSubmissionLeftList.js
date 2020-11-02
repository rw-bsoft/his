$package("phis.application.fsb.script")

$import("phis.script.SelectList",
		"phis.application.mds.script.MySimpleListCommon")

phis.application.fsb.script.FamilySickBedProjectSubmissionLeftList = function(cfg) {
	Ext.apply(this, phis.application.mds.script.MySimpleListCommon)
	phis.application.fsb.script.FamilySickBedProjectSubmissionLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedProjectSubmissionLeftList,
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
			},
			getCM : function(items) {
				var cm = phis.script.SelectList.superclass.getCM.call(this,
						items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : this.checkOnly,
							singleSelect : !this.mutiSelect,
							header:""
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
			}
		})