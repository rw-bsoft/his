/**
 * 新生儿访视调用妇保的页面
 * 
 * @author : zhouw
 */
$package("chis.application.cdh.script.newBorn")
$import("util.Accredit", "chis.script.BizCombinedModule2")
chis.application.cdh.script.newBorn.ToMHCRecordModule = function(cfg) {
	cfg.layOutRegion = "north";

	cfg.itemHeight = 200;
	chis.application.cdh.script.newBorn.ToMHCRecordModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.cdh.script.newBorn.ToMHCRecordModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.cdh.script.newBorn.ToMHCRecordModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.listInfo = this.midiModules[this.actions[0].id];
				this.gridInfo = this.listInfo.grid;
				this.listInfo.selectId = this.initDataId;
				this.listInfo.on("loadData", this.onLoadGridData, this)
				this.gridInfo.on("rowclick", this.onRowClick, this)
				this.listRecord = this.midiModules[this.actions[1].id];
				this.listRecord.on("importIn", this.importTn, this);
				return panel;
			},
			onFormSave : function(entryName, op, json, data) {
				this.listInfo.loadData();
			},
			process : function() {
				
			},
			loadData : function() {
				this.exContext.args.businessType = "5";
				this.exContext.args.checkupType = null;
				this.refreshExContextData(this.listInfo, this.exContext);
				this.listInfo.loadData();
			},
			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index);
				this.listRecord.reData = r.data;
				this.listRecord.loadData(r.get("babyId"));

			},
			importTn : function(data) {
				this.fireEvent("importIn1", data);
			},
			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					return;
				}
				var r = store.getAt(0).data;
				this.listRecord.reData = r;
				this.listRecord.loadData(r.babyId);
			}

		})