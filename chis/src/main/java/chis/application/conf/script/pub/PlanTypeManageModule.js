$package("chis.application.conf.script.pub")
$import("chis.script.BizCombinedModule2")
chis.application.conf.script.pub.PlanTypeManageModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.conf.script.pub.PlanTypeManageModule.superclass.constructor.apply(this,
			[cfg])
	this.on("save", this.onSave, this)
	this.layOutRegion = "north";
	this.itemWidth = this.width;
	this.itemHeight = 170;
}
Ext.extend(chis.application.conf.script.pub.PlanTypeManageModule, chis.script.BizCombinedModule2, {

			initPanel : function() {
				var panel = chis.application.conf.script.pub.PlanTypeManageModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("saveAll", this.onSaveAll, this)
				this.list = this.midiModules[this.actions[1].id];
				this.list.on("add", this.onAdd, this)
				this.list.on("update", this.onUpdate, this)
				this.list.on("doSave", this.onDoSave, this)
				this.list.on("remove", this.onRemove, this);
				this.list.on("afterLoadData", function() {
							this.list.clickRow();
						}, this);
				this.grid = this.list.grid;
				this.grid.setAutoScroll(true);
				return panel;
			},

			loadData : function() {
				if (this.form) {
					this.form.doNew();
				}
				if (this.list) {
					this.list.readOnlyFlag = this.readOnly;
					this.list.loadData();
				}
			},

			onAdd : function() {
				this.op = "create";
				this.form.doNew();
			},

			onUpdate : function(data) {
				this.op = "update";
				this.form.doNew();
				this.form.initFormData(data);
			},

			onDoSave : function() {
				var data = this.form.getFormData();
				if (!data) {
					return;
				}
				var typeName = data.planTypeName;
				var cycle = data.cycle;
				var frequency = data.frequency;
				var result = this.checkTypeExists(cycle, frequency);
				if (result) {
					Ext.Msg.alert("提示信息", "计划类型[周期为" + cycle + "频率为"
									+ data.frequency_text + "]的记录已经存在！")
					return;
				}
				this.saveToServer(data);
			},

			onSave : function(entryName, op, json, data) {
				this.list.refresh();
				this.form.doNew();
			},

			onRemove : function(entryName, op, json, data) {
				this.op = "create";
				this.form.doNew();
			},

			checkTypeExists : function(cycle, frequency) {
				var listCount = this.list.store.getCount();
				for (var i = 0; i < listCount; i++) {
					var storeItem = this.list.store.getAt(i);
					{
						if (storeItem.id == this.list.selectRowId) {
							continue;
						}
						if (storeItem.data.cycle == cycle
								&& storeItem.data.frequency == frequency) {
							return true;
						}
						if (i == listCount - 1) {
							return false;
						}
					}
				}
			}
		});
