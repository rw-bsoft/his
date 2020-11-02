/**
 * 血吸虫module
 * 
 * @author : yub
 */
$package("chis.application.sch.script")

$import("chis.script.BizCombinedTabModule")
chis.application.sch.script.SchistospmaRecordModule = function(cfg) {
	cfg.autoLoadData = false;
	//cfg.width = 770;
	cfg.itemWidth = 220;
	chis.application.sch.script.SchistospmaRecordModule.superclass.constructor.apply(this,
			[cfg]);
	this.on("loadModule", this.onLoadModule, this);
	this.saveServiceId = "chis.schistospmaService";
	this.saveAction = "recordIfHereAndStatus";
}

Ext.extend(chis.application.sch.script.SchistospmaRecordModule, chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.sch.script.SchistospmaRecordModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onListLoadData, this)
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this)
				return panel;
			},

			onRowClick : function(grid, index, e) {
				var store=grid.store;
				var r = store.getAt(index);
				var flag=true;
				store.each(function(record){
					var status=record.get("status");
					var cancellationReason=record.get("cancellationReason");
					if(status=="1"&&cancellationReason!="6"){
						flag=false;
					}
				},this);
				this.exContext.args.selectModuleId = 0;
				this.process(r);
				if(!flag){
					this.midiModules["action2"].setButton([0, 1, 2], false);
				}
			},

			onLoadModule : function(moduleId, module) {
				if (moduleId == this.actions[0].id) {
					module.on("save", this.onSave, this);
					module.on("close", this.onClose, this);
					module.on("create", this.onCreate, this);
				}
			},

			onCreate : function() {
				this.tab.items.itemAt(1).disable();
			},

			onClose : function() {
				this.list.refresh();
//				this.exContext.args.schisRecordId = json.body.schisRecordId;
				this.fireEvent("save");
			},

			onSave : function(entryName, op, json, data) {
				this.list.refresh();
				this.exContext.args.schisRecordId = json.body.schisRecordId;
				this.fireEvent("save");
			},

			getFlag : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.saveAction,
							method:"execute",
							empiId : this.exContext.ids.empiId
						})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				return result.json.body.doFlag;
			},

			onListLoadData : function(store, records, ops) {
				this.doFlag = this.getFlag();
				if (store.getCount() == 0) {
					var exc = {
						empiId : this.exContext.ids.empiId,
						phrId : this.exContext.ids.phrId,
						initDataId : null,
						data : null,
						doFlag : this.doFlag
					}
					Ext.apply(this.exContext.args, exc);
					this.tab.items.itemAt(1).disable();
					this.activeModule(0);
					return;
				}
				var index = store.find("schisRecordId",
						this.exContext.args.schisRecordId);
				if (this.exContext.args.schisRecordId) {
					this.exContext.args.schisRecordId = null; // 清空从第一个页面列表传过来的参数
				}
				if (index == -1) {
					index = 0
				}
				this.list.selectedIndex = index;
				this.list.selectRow(index);
				var flag=true;
				store.each(function(record){
					var status=record.get("status");
					var cancellationReason=record.get("cancellationReason");
					if(status=="1"&&cancellationReason!="6"){
						flag=false;
					}
				},this);
				var r = store.getAt(index);
				this.process(r);
				if(!flag){
					this.midiModules["action2"].setButton([0, 1, 2], false);
				}
			},

			process : function(r) {
				var selectId = this.exContext.args.selectModuleId;
				if (!selectId) {
					selectId = 0;
				}
				var schisRecordId = null;
				var data = null;
				var closeFlag = 0;
				var status = 0;
				var doFlag = this.doFlag;
				if (r) {
					schisRecordId = r.get("schisRecordId");
					data = r.data;
					closeFlag = data.closeFlag;
					status = data.status
					if (doFlag && data.createUnit != this.mainApp.deptId) {
						doFlag = false;
					}
				}
				this.tab.items.itemAt(1).enable();
				var exc = {
					empiId : this.exContext.ids.empiId,
					phrId : this.exContext.ids.phrId,
					schisRecordId : schisRecordId,
					closeFlag : closeFlag,
					status : status,
					doFlag : doFlag,
					data : data
				}
				Ext.apply(this.exContext.args, exc);
				this.activeModule(selectId);
			}
		})