/**
 * 非重点人群随访module
 * 
 * @author tianj
 */
$package("chis.application.npvr.script")

$import("chis.script.BizCombinedTabModule")

chis.application.npvr.script.NormalPopulationVisitModule = function(cfg) {
	cfg.itemWidth = 228
//	cfg.width = 750;
//	cfg.height = 300;
	chis.application.npvr.script.NormalPopulationVisitModule.superclass.constructor.apply(this,
			[cfg]);
	this.on("loadModule", this.onLoadModule, this);
}

Ext.extend(chis.application.npvr.script.NormalPopulationVisitModule,chis.script.BizCombinedTabModule, {
	initPanel : function() {
		var panel = chis.application.npvr.script.NormalPopulationVisitModule.superclass.initPanel
				.call(this);
		this.panel = panel;
		this.list = this.midiModules[this.otherActions.id];
		this.list.on("loadData", this.onListLoadData, this)
		this.grid = this.list.grid;
		this.grid.on("rowClick", this.onRowClick, this)
		return panel;
	},

	onRowClick : function(grid, index, e) {
		this.from.doNew();
		var r = grid.store.getAt(index);
		this.process(r, index);
	},

	onLoadModule : function(moduleId, module) {
		this.from = this.midiModules[this.actions[0].id];
		module.on("save", this.onSave, this);
	},

	onSave : function(entryName, op, json, data) {
		this.list.refresh();
		this.exContext.args.visitId = json.body.id;
		this.fireEvent("save",entryName, op, json, data);
	},

	onListLoadData : function(store, records, ops) {
		//** * 控制新建按钮权限
		var result = util.rmi.miniJsonRequestSync({
						serviceId : this.saveServiceId,
						serviceAction : "loadCreateControl",
						method:"execute",
						empiId : this.exContext.ids.empiId
					})
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return
		}
		Ext.apply(this.exContext.control, result.json.body);
		
		if (store.getCount() == 0) {
			var exc = {
				empiId : this.exContext.ids.empiId,
				phrId : this.exContext.ids.phrId,
				initDataId : null,
				data : null
			}
			Ext.apply(this.exContext.args, exc);
			this.activeModule(0);
			return;
		}

		var index = store.find("id", this.exContext.args.visitId);
		if (this.exContext.args.visitId) {
			this.exContext.args.visitId = null; // 清空从第一个页面列表传过来的参数
		}
		if (index == -1) {
			index = 0
		}
		this.list.selectedIndex = index;
		this.list.selectRow(index);
		var r = store.getAt(index);
		this.process(r, index);
	},

	process : function(r, index) {
		var initDataId = null;
		var data = null;
		if (r) {
			initDataId = r.get("id");
			data = r.data;
		}
		var exc = {
			empiId : this.exContext.ids.empiId,
			phrId : this.exContext.ids.phrId,
			initDataId : initDataId,
			data : data
		}
		Ext.apply(this.exContext.args, exc);
		this.activeModule(0);
	}
});