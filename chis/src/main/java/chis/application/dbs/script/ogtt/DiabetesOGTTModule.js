$package("chis.application.dbs.script.ogtt")
$import("chis.script.BizCombinedModule2")
chis.application.dbs.script.ogtt.DiabetesOGTTModule = function(cfg) {
	this.autoLoadData = false;
	cfg.itemWidth = 200;
	chis.application.dbs.script.ogtt.DiabetesOGTTModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(chis.application.dbs.script.ogtt.DiabetesOGTTModule,
		chis.script.BizCombinedModule2, {
			initPanel : function(sc) {
				var panel = chis.application.dbs.script.ogtt.DiabetesOGTTModule.superclass.initPanel
						.call(this, sc);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onListLoadData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				// this.form.on("save", this.onSave, this);
				// this.list.on("addModule", this.onAddModule, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("addModule", this.onAddModule, this)
				this.form.on("save", this.onSave, this);
				return panel;
			},
			onAddModule:function(module){
				this.fireEvent("addModule", module);
				this.fireEvent("activeModule", module, {
						});
			},
			loadData : function() {
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.empiId'],
								['s', this.exContext.ids.empiId]],
						['eq', ['$', 'a.businessType'], ['s', '17']]];
				this.list.loadData();
			},
			initRiskFactors : function(riskFactors) {
				this.riskFactors = riskFactors;
				if (this.form) {
					this.form.initRiskFactors(riskFactors);
				}
			},
			onListLoadData : function(store) {
				if (store.getCount() == 0) {
					if (this.form && this.riskFactors) {
						this.form.initRiskFactors(this.riskFactors);
					}
					return;
				}
				var index = 0;
				var OGTTID = this.exContext.args.OGTTID;
				if (OGTTID) {
					index = this.list.store.find("visitId", OGTTID);
				}
				this.list.selectedIndex = index < 0 ? 0 : index;
				var r = store.getAt(this.list.selectedIndex);
				this.proccess(r, this.list.selectedIndex);
			},
			onRowClick : function(grid, index, e) {
				var r = grid.getStore().getAt(index)
				this.list.selectedIndex = index
				this.proccess(r, index);
			},
			proccess : function(r, index) {
				this.form.initDataId = r.get("visitId");
				this.form.planId = r.get("planId");
				this.form.planDate = r.get("planDate");
				this.refreshExContextData(this.form, this.exContext);
				this.form.loadData();
			},
			onSave : function(entryName, op, json, data) {
				this.exContext.args.OGTTID = this.form.initDataId;
				this.list.refresh();
				var body = json.body;
				if (!body) {
					return;
				}
				this.fireEvent("save", entryName, op, json, data);
				this.fireEvent("chisSave")
			}
		});