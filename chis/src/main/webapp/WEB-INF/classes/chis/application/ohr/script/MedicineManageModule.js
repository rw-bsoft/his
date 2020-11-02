$package("chis.application.ohr.script");

$import("chis.script.BizCombinedTabModule");

chis.application.ohr.script.MedicineManageModule = function(cfg) {
	chis.application.ohr.script.MedicineManageModule.superclass.constructor
			.apply(this, [cfg]);
	this.itemWidth = 180;
	this.on("loadModule", this.onLoadModule, this);
};

Ext.extend(chis.application.ohr.script.MedicineManageModule,
		chis.script.BizCombinedTabModule, {
			initPanel : function() {
				var panel = chis.application.ohr.script.MedicineManageModule.superclass.initPanel
						.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.otherActions.id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.grid = this.list.grid;
				this.grid.on("rowclick", this.onRowClick, this);
				return panel;
			},
			onLoadModule : function(moduleId, module) {
				Ext.apply(module.exContext, this.exContext);
				if (moduleId == this.actions[0].id) {
					module.on("doSave", this.onFormSave, this);
					module.on("doCreate", this.onDoCreate, this);
					this.formQ = module;
				}
				if (moduleId == this.actions[1].id) {
					module.on("save", this.onSave, this);
					this.formA = module;
				}
			},

			loadData : function() {
				Ext.apply(this.list.exContext, this.exContext);
				this.tab.setActiveTab(this.tab.items.itemAt(1));
				this.tab.setActiveTab(this.tab.items.itemAt(0));
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.empiId'],
								['s', this.exContext.ids.empiId]],
						['eq', ['$', 'a.status'], ['s', '0']]];
				this.list.loadData();
			},
			onDoCreate : function() {
				if (this.formA) {
					this.formA.doCreate();
				}
				if (this.tab.items.itemAt(1)) {
					this.tab.items.itemAt(1).disable();
				}
				var empiData=this.exContext.empiData;
				var birthDay = empiData.birthday;
				var currDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var birth;
				if ((typeof birthDay == 'object')
						&& birthDay.constructor == Date) {
					birth = birthDay;
				} else {
					birth = Date.parseDate(birthDay, "Y-m-d");
				}
				currDate.setYear(currDate.getFullYear()
						- this.mainApp.exContext.oldPeopleAge);
				if (birth.getFullYear() > currDate.getFullYear()) {
					Ext.Msg.show({
								title : '提示信息',
								msg : '年龄小于'
										+ this.mainApp.exContext.oldPeopleAge
										+ '岁不允许建立中医药健康管理',
								modal : true,
								minWidth : 300,
								maxWidth : 600,
								buttons : Ext.MessageBox.OK,
								multiline : false,
								scope : this
							});
					return;
				}
			},
			onLoadGridData : function(store) {
				if (store.getCount() == 0) {
					this.activeModule(0);
					this.formQ.doCreate();
					if (this.formA) {
						this.formA.doCreate();
					}
					return;
				}
				if (this.exContext.args["id"]) {
					for (var i = 0; i < store.getCount(); i++) {
						var r = store.getAt(i);
						if (r.get("id") == this.exContext.args["id"]) {
							this.grid.getSelectionModel().selectRecords([r]);
							var n = store.indexOf(r);
							if (n > -1) {
								this.list.selectedIndex = n;
							}
							break;
						}
					}
				}
				if (!this.list.selectedIndex) {
					this.list.selectedIndex = 0;
				}
				var r = store.getAt(this.list.selectedIndex);
				this.process(r, this.list.selectedIndex);
			},

			onRowClick : function(grid, index, e) {
				this.list.selectedIndex = index;
				var r = grid.store.getAt(index);
				this.process(r, index);
			},

			process : function(r, n) {
				if (!r) {
					this.formQ.doCreate();
					if (this.formA) {
						this.formA.doCreate();
					}
					return;
				}
				if (this.tab.items.itemAt(1)) {
					this.tab.items.itemAt(1).enable();
				}
				this.tab.setActiveTab(this.tab.items.itemAt(0));
				var id = r.get("id");
				this.formQ.initDataId = id;
				var formData = this
						.castListDataToForm(r.data, this.list.schema);
				this.formQ.initFormData(r.data);
				if (this.formA) {
					Ext.apply(this.formA.exContext, this.exContext);
					this.formA.initDataId = id;
					this.formA.initFormData(formData);
					this.formA.dataQ = this.formQ.getFormData();
				}
			},

			onFormSave : function(data) {
				if (this.tab.items.itemAt(1)) {
					this.tab.items.itemAt(1).enable();
				}
				this.tab.setActiveTab(this.tab.items.itemAt(1));
				this.formA.doDetermine(data);
			},
			onSave : function(entryName, op, json, data) {
				if (op == "create") {
					this.id = json.body.id;
				} else {
					this.id = data.id;
				}
				this.list.refresh();
				this.fireEvent("save",entryName, op, json, data);
			}
		});