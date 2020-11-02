$package("phis.application.chr.script")

$import("phis.script.SelectList")

phis.application.chr.script.AllCaseSelectList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.chr.script.AllCaseSelectList.superclass.constructor.apply(this,
			[cfg])
	this.on("select", this.onSelectRecords, this);
	this.on("loadData", this.onLoadData, this)
}

Ext.extend(phis.application.chr.script.AllCaseSelectList, phis.script.SelectList, {
			onLoadData : function() {
				this.fireEvent("hasLoadData", this)
			},

			selectAllRecords : function() {
				this.sm.clearSelections();
				var len = this.store.getCount();
				for (var i = 0; i < len; i++) {
					this.sm.selectRow(i, true);
				}
			},
			onDblClick : function(grid, index, e) {
			},
			onRowClick : function() {
				this.fireEvent("recordClick", this)
			},
			onSelectRecords : function() {
				this.onRowClick();
			},
			getSM : function() {
				var sm = new Ext.grid.CheckboxSelectionModel({
							checkOnly : true,
							singleSelect : false,
							onHdMouseDown : function(e, t) {
								if (t.className == 'x-grid3-hd-checker') {
									e.stopEvent();
									var hd = Ext.fly(t.parentNode);
									var isChecked = hd
											.hasClass('x-grid3-hd-checker-on');
									if (isChecked) {
										hd.removeClass('x-grid3-hd-checker-on');
										this.clearSelections();
									} else {
										hd.addClass('x-grid3-hd-checker-on');
										this.selectAll();
									}
									this.fireEvent("selectAll");
								}
							}
						});
				return sm
			},
			getCM : function(items) {
				this.sm = this.getSM();
				var cm = phis.script.SelectList.superclass.getCM.call(this,
						items)
				sm = this.sm;
				sm.on("rowselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								this.selects[record.id] = record
							} else {
								this.singleSelect = record
							}
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								delete this.selects[record.id]
							}
						}, this);
				sm.on("selectAll", function() {
							this.onRowClick();
						}, this)
				return cm;
			}

		});