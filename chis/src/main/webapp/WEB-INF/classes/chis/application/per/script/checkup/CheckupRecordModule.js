$package("chis.application.per.script.checkup");

$import("chis.script.BizCombinedTabModule");

chis.application.per.script.checkup.CheckupRecordModule = function(cfg) {
	cfg.autoLoadData = false;
	chis.application.per.script.checkup.CheckupRecordModule.superclass.constructor
			.apply(this, [cfg]);
	// this.width = 850;
	this.itemWidth = 170;
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.per.script.checkup.CheckupRecordModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.per.script.checkup.CheckupRecordModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowClick", this.onRowClick, this);
				return panel;
			},
			onLoadModule : function(moduleId, module) {
				Ext.apply(moduleId.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					module.on("save", this.onRegisterFormSave, this);
					module.on("finalCheck", this.refreshLeftList, this);
					module.on("create", this.onCreateCheckup, this);
					this.perRecordModule = module;
					this.perRecordModule.ehrStatus=this.ehrStatus;
				}
				if (moduleId == this.actions[1].id) {
					this.perDetailModule = module;
					this.perDetailModule.ehrStatus=this.ehrStatus;
					this.perDetailModule.on("detailSave", this.onDetailSave,
							this);
				}
			},
			onRegisterFormSave : function(entryName, op, json, data) {
				this.list.exContext.args.selectCheckupNo = json.body.checkupNo;
				if (op == "create") {
					this.listSelectedIndex = this.list.store.getCount();
				} else {
					this.listSelectedIndex = this.list.selectedIndex;
				}
				this.list.refresh();
				if (this.perDetailModule) {
					this.perDetailModule.projectOfficeCode = null;
				}
				this.fireEvent("save", entryName, op, json, data);
			},
			refreshLeftList : function(entryName, op, json, data) {
				this.list.refresh();
			},
			onCreateCheckup : function() {
				this.setTabItemDisabled(1, true);
				this.ehrStatus = this.getEHRstatus();
				if (this.perDetailModule) {
					this.perDetailModule.ehrStatus=this.ehrStatus;
				}
				if (this.perRecordModule) {
					this.perRecordModule.ehrStatus=this.ehrStatus;
				}
				Ext.apply(this.list.exContext, this.exContext);
			},
			onDetailSave : function(sumupException) {
				var checkupExce = this.perRecordModule.form.form.getForm()
						.findField("checkupExce");
				checkupExce.setValue(sumupException);
			},
			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.exContext.args = {};
					this.activeModule(0);
					return
				}
				if (this.list.selectedCheckupNo) {
					for (var i = 0; i < store.getCount(); i++) {
						var r = store.getAt(i);
						if (r.get("checkupNo") == this.list.selectedCheckupNo) {
							this.grid.getSelectionModel().selectRecords([r]);
							this.list.selectedIndex = i;
							break;
						}
					}
				} else {
					if (this.listSelectedIndex) {
						this.list.selectedIndex = this.listSelectedIndex;
					} else {
						this.list.selectedIndex = 0;
					}
				}
				var index = this.list.selectedIndex;
				if (!index) {
					index = 0;
				}
				this.list.selectedIndex = index;
				this.list.selectRow(index);
				var r = store.getAt(index);
				this.process(r, index);
			},
			onRowClick : function(grid, index, e) {
				if (!this.list) {
					this.exContext.args = {};
					this.activeModule(0);
					return;
				}
				var r = this.list.grid.getSelectionModel().getSelected();
				if (!r) {
					this.exContext.args = {};
					this.activeModule(0);
					return;
				}
				this.list.selectedIndex = index;
				this.process(r, index);
			},
			process : function(r, index) {
				if (!r) {
					this.exContext.args = {};
					return;
				}
				this.checkupNo = r.id;
				var status = r.get("status");
				var totalCheckupDate = r.get("totalCheckupDate");
				var checkupOrganization = r.get("checkupOrganization");
				this.ready = false;

				// 注销只读
				if (status == 1) {
					this.ready = true;
				}
				// 总检只读
				if (totalCheckupDate) {
					this.ready = true;
				}
				// 非本中心只读
				if (checkupOrganization) {
					var deptId = this.mainApp.deptId;
					if (deptId.indexOf(checkupOrganization) == -1) {
						this.ready = true;
					}
				}

				this.exContext.args = {};
				this.exContext.args.ready = this.ready;
				this.exContext.args.initDataId = this.checkupNo;
				this.exContext.args.checkupNo = this.checkupNo;
				this.exContext.args.selectCheckupNo = this.checkupNo;
				this.exContext.args.empiId = this.exContext.ids.empiId;
				this.exContext.args.phrId = this.exContext.ids.phrId;
				this.exContext.args.checkupType = r.get("checkupType");
				this.exContext.args.totalCheckupDate = totalCheckupDate;
				this.exContext.args.status = status;
				this.exContext.args.checkupOrganization = r
						.get("checkupOrganization");

				if (status == '1' && !totalCheckupDate) {// 未总检 作废 先灰掉明细标签
					this.setTabItemDisabled(1, true);
				} else {
					this.setTabItemDisabled(1, false);
				}

				if (this.perRecordModule) {
					this.perRecordModule.exContext.args = {};
					Ext.apply(this.perRecordModule.exContext, this.exContext);
				}
				if (this.perDetailModule) {
					Ext.apply(this.perDetailModule.exContext, this.exContext);
				}
				this.activeModule(0);
				// this.checkupRecord.loadData();
			},
			getEHRstatus : function() {
				var cnds = ['and',['eq',['$','a.empiId'],['s',this.exContext.ids.empiId]],['like',['$','a.manaUnitId'],['s',this.mainApp.deptId+"%"]]]
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.simpleQuery",
							schema : "chis.application.hr.schemas.EHR_HealthRecord",
							method : "execute",
							cnd : cnds
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg,
							this.getEHRstatus);
					return;
				}
				if(result.json.body.length == 0){
					return "1"
				}else{
					return "0"
				}
			},
			loadData : function() {
				this.ehrStatus = this.getEHRstatus();
				this.listSelectedIndex = undefined;
				if (this.perDetailModule) {
					this.perDetailModule.ehrStatus=this.ehrStatus;
					this.perDetailModule.projectOfficeCode = null;
					this.perDetailModule.tree.getLoader().load(this.perDetailModule.tree.getRootNode());
				}
				if (this.perRecordModule) {
					this.perRecordModule.ehrStatus=this.ehrStatus;
				}
				Ext.apply(this.list.exContext, this.exContext);
				this.list.loadData();
			}
		});