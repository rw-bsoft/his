/**
 * 贫困人群随访module
 * 
 * @author : tianj
 */
$package("chis.application.ppvr.script")

$import("chis.script.BizCombinedTabModule")

chis.application.ppvr.script.PoorPeopleVisitModule = function(cfg) {
//	cfg.width = 1000;
//	cfg.height = 400;
	cfg.itemWidth = 228
	chis.application.ppvr.script.PoorPeopleVisitModule.superclass.constructor
			.apply(this, [cfg]);
	this.on("loadModule", this.onLoadModule, this)
}

Ext.extend(chis.application.ppvr.script.PoorPeopleVisitModule, chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.ppvr.script.PoorPeopleVisitModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onListLoadData, this)
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this)
				return panel;
			},

			onRowClick : function(grid, index, e) {
				var isLast = false;
				if (index == grid.store.getCount() - 1) {
					isLast = true;
				}
				var r = grid.store.getAt(index);
				this.process(r, index, isLast);
			},

			onLoadModule : function(moduleId, module) {
				module.on("save", this.onSave, this);
			},

			onSave : function(entryName, op, json, data) {
				this.list.refresh();
				this.exContext.args.visitId = json.body.visitId;
				this.fireEvent("save",entryName, op, json, data);
			},

			onListLoadData : function(store, records, ops) {
				if (store.getCount() == 0) {
					var exc = {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId,
						initDataId : null,
						data : null,
						updateIncomeSource : true
					}
					Ext.apply(this.exContext.args, exc);
					this.activeModule(0);
					return;
				}

				var index = store.find("visitId", this.exContext.args.visitId);
				if (this.exContext.args.visitId) {
					this.exContext.args.visitId = null; // 清空从第一个页面列表传过来的参数
				}
				if (index == -1) {
					index = 0
				}
				var isLast = false; // 判断是否是最后一条记录，如果是需要更新家庭档案收入来源状况
				if (index == store.getCount() - 1) {
					isLast = true;
				}
				this.list.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
				this.process(r, index, isLast);
			},

			process : function(r, index, isLast) {
				var initDataId = null;
				var data = null;
				if (r) {
					initDataId = r.get("visitId");
					data = r.data;
				}
				var exc = {
					empiId : this.exContext.ids.empiId,
					phrId : this.exContext.ids.phrId,
					initDataId : initDataId,
					updateIncomeSource : isLast,
					data : data
				}
				Ext.apply(this.exContext.args, exc);
				this.activeModule(0);
			}
		});