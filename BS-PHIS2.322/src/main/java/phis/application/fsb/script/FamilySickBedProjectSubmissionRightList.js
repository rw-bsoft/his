$package("phis.application.fsb.script")

$import("phis.script.EditorList",
		"phis.application.mds.script.MySimpleListCommon")

phis.application.fsb.script.FamilySickBedProjectSubmissionRightList = function(
		cfg) {
	Ext.apply(this, phis.application.mds.script.MySimpleListCommon)
	this.selects=[];
	phis.application.fsb.script.FamilySickBedProjectSubmissionRightList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FamilySickBedProjectSubmissionRightList,
		phis.script.EditorList, {
			onStoreLoadData : function(store, records, ops) {
//				phis.script.SelectList.superclass.onStoreLoadData.call(this,
//						store, records, ops)
				this.fireEvent("loadData",this);
				if (records.length == 0 || !this.selects ) {
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
				var cm = phis.application.fsb.script.FamilySickBedProjectSubmissionRightList.superclass.getCM
						.call(this, items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : true,
							singleSelect : false
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
			onRenderCs : function(value, metaData, r) {
				var je = (parseFloat(r.get("YCSL")) * parseFloat(r.get("YPDJ")))
						.toFixed(2)
				return je ;
			},
			getSelectedRecords : function() {
				var records = []
				for (var id in this.selects) {
					records.push(this.selects[id])
				}
				return records
			}
		})