$package("phis.application.stm.script")

$import("phis.script.SimpleModule")

phis.application.stm.script.ClinicSkinTestRecordModule = function(cfg) {
	phis.application.stm.script.ClinicSkinTestRecordModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.stm.script.ClinicSkinTestRecordModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							// frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										region : 'center',
										items : [this.getDoingList()]
									}, {
										layout : "fit",
										border : false,
										region : 'south',
										height : 300,
										items : this.getRecordList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getDoingList : function() {
				var module = this.createModule("refDoingList",
						this.refSkinTestDoingList);
				this.doingList = module;
				this.doingList.systemParams = this.systemParams;
				this.doingList.on("skDblclick", function(r) {
							this.fireEvent('skDblclick', r)
						}, this)

				return this.doingList.initPanel();

			},
			getRecordList : function() {
				var module = this.createModule("refRecordList",
						this.refSkinTestRecordList);
				this.recordList = module;
				this.recordList.on("skDblclick", function(r) {
							this.fireEvent('skDblclick', r)
						}, this)
				this.recordList.on('lookupRecord', this.lookupRecord, this);
				return this.recordList.initPanel();
			},
			lookupRecord : function(key) {
				// filter store
				var fiterField = isNaN(key) ? "BRXM" : "CFHM";// 是否汉字
				var store = this.recordList.grid.store;
				for (var i = 0; i < store.getCount(); i++) {
					var v = store.getAt(i).get(fiterField).toUpperCase();
					if (v.indexOf(key) >= 0) {
						this.recordList.grid.getSelectionModel().selectRow(i);
						break;
					}
				}
				var store = this.doingList.grid.store;
				for (var i = 0; i < store.getCount(); i++) {
					var v = store.getAt(i).get(fiterField).toUpperCase();
					if (v.indexOf(key) >= 0) {
						this.doingList.grid.getSelectionModel().selectRow(i);
						break;
					}
				}
			},
			refresh : function() {
				this.recordList.loadData();
				this.doingList.loadData();
			}
		});