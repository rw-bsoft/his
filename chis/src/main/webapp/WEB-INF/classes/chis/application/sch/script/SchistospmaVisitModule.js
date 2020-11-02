/**
 * 血吸虫module
 * 
 * @author : yub
 */
$package("chis.application.sch.script")

$import("chis.script.BizCombinedModule2")

chis.application.sch.script.SchistospmaVisitModule = function(cfg) {
	cfg.itemHeight = 500;
	cfg.itemWidth = 156;
	cfg.height = 292;
	cfg.itemCollapsible = false;
	chis.application.sch.script.SchistospmaVisitModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.sch.script.SchistospmaVisitModule, chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.sch.script.SchistospmaVisitModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadData, this);
				this.list.on("firstRowSelected", this.onRowClick, this);
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this)
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("beforeSaveRecord", this.onBeforeSaveRecord, this);
				return panel;
			},

			onBeforeSaveRecord : function(date) {
				var store = this.grid.store;
				var year = date.getFullYear();
				var month = date.getMonth() + 1;
				var day = date.getDate();
				if (month < 10) {
					month = "0" + month;
				}
				if (day < 10) {
					day = "0" + day;
				}
				var time = year + "-" + month + "-" + day;
				var index = store.find("visitDate", time);
				if (index == -1) {
					return true;
				} else {
					return false;
				}
			},

			onLoadData : function(store) {
				if (store.getCount() == 0) {
					this.form.op = "create";
					this.form.initDataId = null;
					this.form.loadFormData();
				}
			},

			loadData : function() {
				this.refreshExContextData(this.list, this.exContext);
				this.refreshExContextData(this.form, this.exContext);
				this.list.loadData();
			},

			onFormSave : function() {
				this.list.loadData();
			},

			onRowClick : function(grid, index, e) {
				var r = this.grid.getSelectionModel().getSelected();
				this.refreshExContextData(this.form, this.exContext);
				if (r) {
					this.form.op = "update";
					var formData = this.castListDataToForm(r.data,
							this.form.schema);
					this.form.loadFormData(formData);
				} else {
					this.form.op = "create";
					this.form.initDataId = null;
					this.form.loadFormData();
				}
			}

		})