/**
 * 新生儿访视整体页面
 * 
 * @author : zhouw
 */
$package("chis.application.cdh.script.newBorn")
$import("util.Accredit", "chis.script.BizCombinedModule2")
chis.application.cdh.script.newBorn.NewBornRecordModule = function(cfg) {
	cfg.itemWidth = 176
	chis.application.cdh.script.newBorn.NewBornRecordModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(chis.application.cdh.script.newBorn.NewBornRecordModule,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.cdh.script.newBorn.NewBornRecordModule.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this)
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this)
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save", this.onFormSave, this);
				this.form.on("doCreate", this.onDoCreate, this)
				return panel;
			},
			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.process();
					return;
				}
				var r = store.getAt(0).data;
				var visitId = r.visitId;
				var visitDate = r.visitDate;
				var data = {};
				data["visitId"] = visitId;
				data["visitDate"] = visitDate;
				this.processInit(data);
			},
			onDoCreate : function() {
				this.process();
			},
			onFormSave : function(entryName, op, json, data) {
				this.list.loadData();
			},
			process : function() {
				var values = {};
				values["empiId"] = this.exContext.empiData.empiId;
				values["babyBirth"] = this.exContext.empiData.birthday;
				values["babySex"] = this.exContext.empiData.sexCode;
				values["babyIdCard"] = this.exContext.empiData.idCard;
				values["babyName"] = this.exContext.empiData.personName;
				var v = this.isCreatrFirstVisitRecord(values);
				Ext.apply(values, v);
				this.form.initFormData(values);
			},
			loadData : function() {
				this.exContext.args.businessType = "5";
				this.exContext.args.checkupType = null;
				this.refreshExContextData(this.list, this.exContext);
				this.list.loadData();
			},
			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index);
				var visitId = r.data.visitId;
				var visitDate = r.data.visitDate;
				var data = {};
				data["visitId"] = visitId;
				data["visitDate"] = visitDate;
				this.processInit(data);
			},
			processInit : function(r) {
				if (!r) {
					return;
				}
				var v = this.getDataInfo(r);
				this.form.doCreate();
				this.form.initFormData(v);
			},
			getDataInfo : function(data) {
				if (!data) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "getFullRecord",
							method : "execute",
							body : data
						});
				var resHealthData = result.json.body;
				if (resHealthData) {
					return resHealthData;
				}
			},
			isCreatrFirstVisitRecord : function(data) {
				if (!data) {
					return;
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.childrenHealthRecordService",
							serviceAction : "isCreatrFirstVisitRecord",
							method : "execute",
							body : data
						});
				var resHealthData = result.json.healthData;
				if (resHealthData) {
					return resHealthData;
				}
			},
			showInfoSelectView : function(data) {
				if (!data) {
					return;
				}
				var records = [];
				for (var i = 0; i < data.length; i++) {
					var r = data[i];
					Ext.apply(r, this.dataHealthData);
					var record = new Ext.data.Record(r);
					records.push(record);
				}
				if (records.length == 0) {
					return;
				}
				var visitInfoSelectView = this.midiModules["babyVisitInfo"];
				if (!visitInfoSelectView) {
					var visitInfoSelectView = new chis.application.mpi.script.CombinationSelect(
							{
								entryName : "chis.application.mhc.schemas.MHC_BabyVisitInfo",
								autoLoadData : false,
								enableCnd : false,
								modal : true,
								title : "选择访视的基本信息",
								width : 500,
								height : 300
							});
					visitInfoSelectView.on("onSelect", function(r) {
								this.form.initFormData(r.data);
							}, this);
					visitInfoSelectView.getWin().show();
					var task = new Ext.util.DelayedTask(function() {
								visitInfoSelectView.setRecords(records);
							}, this);
					task.delay(100);
				}
			},
			refreshWhenTabChange : function() {// 每次点击ehrview树，然后刷新该页面
				this.loadData();
			}
		})