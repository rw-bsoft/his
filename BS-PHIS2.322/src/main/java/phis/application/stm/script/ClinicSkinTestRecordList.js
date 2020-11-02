$package("phis.application.stm.script")

$import("phis.script.SimpleList")

phis.application.stm.script.ClinicSkinTestRecordList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = false;
	phis.application.stm.script.ClinicSkinTestRecordList.superclass.constructor
			.apply(this, [cfg])
	this.on("firstRowSelected", this.onDblClick, this)
	this.on("loadData", this.afterLoad, this)
}
Ext.extend(phis.application.stm.script.ClinicSkinTestRecordList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				delete cfg.tbar;
				var label = new Ext.form.Label({
							text : "处方号码"
						});
				var queryText = new Ext.form.TextField({
							width : 200,
							enableKeyEvents : true,
							selectOnFocus : true
						});
				this.queryText = queryText;
				this.queryText.on('keyup', this.onKeyUp, this)
				this.queryText.on('afterrender', this.initKey, this)
				cfg.bbar = [label, '-', queryText];
			},
			initKey : function() {
				this.keyNav = new Ext.KeyNav(this.queryText.el, {
					"up" : function(e) {
						var lastIndex = this.grid.getSelectionModel().lastActive;
						if (lastIndex > 0) {
							this.grid.getSelectionModel().selectRow(lastIndex
									- 1)
						} else {
							this.grid.getSelectionModel().selectRow(lastIndex)
						}
					},
					"down" : function(e) {
						var lastIndex = this.grid.getSelectionModel().lastActive;
						var n = this.grid.store.getCount()
						if (lastIndex < n) {
							this.grid.getSelectionModel().selectRow(lastIndex
									+ 1)
						} else {
							this.grid.getSelectionModel().selectRow(lastIndex)
						}
					},
					"enter" : function(e) {
						var lastIndex = this.grid.getSelectionModel().lastActive;
						var record = this.grid.store.getAt(lastIndex);
						if (record) {
							this.fireEvent("skDblclick", record);
						}
					},
					"esc" : function(e) {
						this.queryText.setValue("");
					},
					scope : this,
					forceKeyDown : true,
					defaultEventAction : 'stopEvent'
				});
			},
			afterLoad : function() {
				this.queryText.focus(false, 200);
			},
			onKeyUp : function(f, e) {
				var k = e.getKey();
				if (k == e.BACKSPACE
						|| !(e.isSpecialKey() || (k >= 112 && k <= 123))) {
					var v = f.getValue();
					if (v) {
						this.currentInputTime = new Date().getTime();// 如果正在进行数据过滤,则不进行刷新操作
						this.fireEvent('lookupRecord', v.toUpperCase());
					}
				}
			},
			onDblClick : function() {
				this.onRowClick();
			},
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (!r)
					return;
				this.fireEvent('skDblclick', r)
			}
		});