/**
 * 修改各档责任医生明细列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.manage")
$import("chis.script.BizEditorListView")
chis.application.mov.script.manage.ManaInfoChangeDetailList = function(cfg) {
	cfg.pageSize = -1;
	chis.application.mov.script.manage.ManaInfoChangeDetailList.superclass.constructor.apply(
			this, [cfg]);
	this.disablePagingTbr = true;
	this.mutiSelect = true
	this.selects = {}
	this.singleSelect = {}
	this.enableCnd = false
};
Ext.extend(chis.application.mov.script.manage.ManaInfoChangeDetailList,
		chis.script.BizEditorListView, {

			getCM : function(items) {
				var cm = chis.application.mov.script.manage.ManaInfoChangeDetailList.superclass.getCM
						.call(this, items)
				var sm = new Ext.grid.CheckboxSelectionModel({
							singleSelect : !this.mutiSelect
						})
				this.sm = sm
				sm.on("rowselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								this.selects[record.id] = record
							} else {
								this.singleSelect = record
							}
						}, this)
				sm.on("rowdeselect", function(sm, rowIndex, record) {
							if (this.mutiSelect) {
								delete this.selects[record.id]
							}
						}, this)
				return [sm].concat(cm)
			},

			getSelectedRecords : function() {
				var records = []
				if (this.mutiSelect) {
					for (var id in this.selects) {
						records.push(this.selects[id])
					}
				} else {
					records[0] = this.singleSelect
				}
				return records
			},

			clearSelect : function() {
				this.selects = {};
				this.singleSelect = {};
				this.sm.clearSelections();
				Ext.fly(this.grid.getView().innerHd)
						.child('.x-grid3-hd-checker')
						.removeClass('x-grid3-hd-checker-on');
			},

			beforeCellEdit : function(e) {
				chis.application.mov.script.manage.ManaInfoChangeDetailList.superclass.beforeCellEdit
						.call(this, e);
				this.record = e.record;
				var cm = this.grid.getColumnModel();
				var c = cm.config[e.column];
				var it = c.schemaItem;
				if (it.id == "targetDoctor") {
					var enditor = cm.getCellEditor(e.column, e.row);
					var field = enditor.field;
					this.unitColumn = e.column + 1;
					this.unitRow = e.row;
					if (field) {
						field.on("select", this.changeManaUnit, this);
					}
				} else if (it.id == "targetUnit") {
					var enditor = cm.getCellEditor(e.column, e.row);
					var field = enditor.field;
					field.disable();
				}
			},

			changeManaUnit : function(combo, node) {
				if (!node.attributes['key']) {
					return
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.publicService",
							serviceAction : "getManageUnit",
							method:"execute",
							body : {
								manaUnitId : node.attributes["manageUnit"]
							}
						})
				this.setManaUnit(result.json.manageUnit)
			},

			setManaUnit : function(manageUnit) {
				var cm = this.grid.getColumnModel();
				var enditor = cm.getCellEditor(this.unitColumn, this.unitRow);
				var combox = enditor.field;
				if (!combox) {
					return;
				}
				if (!manageUnit) {
					combox.enable();
					combox.reset();
				} else {
					var f = combox.getId
					this.record.set("targetUnit", manageUnit.key);
					this.record.set("targetUnit_text", manageUnit.text);
					combox.disable();
				}
			},

			loadData : function() {
				this.initCnd = ['eq', ['$', 'archiveMoveId'],
						['s', this.initDataId]]
				this.requestData.cnd = this.initCnd
				chis.application.mov.script.manage.ManaInfoChangeDetailList.superclass.loadData
						.call(this);
			},

			doAdd : function() {
				
				if (!this.empiId) {
					Ext.MessageBox.alert("提示", "请先选择被迁移人员信息！")
					return;
				}
				var module = this.midiModules["QueryModule"];
				if (!module) {
					var cls = "chis.application.mov.script.manage.PeopleRecordQueryList";
					$import(cls);
					var cfg = {
						isCombined : false,
						autoLoadData : true,
						autoLoadSchema : true,
						entryName : "chis.application.mov.schemas.MOV_PeopleRecordsQuery",
						listServiceId : "chis.manaInfoChangeService",
						method:"execute",
						listAction : "getPeopleAllRecords"
					};
					module = eval("new " + cls + "(cfg)");
					module.on("recordSelected", this.onRecordSelected, this);
					this.midiModules["QueryModule"] = module;
				}
				module.requestData.empiId = this.empiId;
				var win = module.getWin();
				win.setPosition(300, 200);
				win.show();
			},

			onRecordSelected : function(records) {
				if (!records) {
					return;
				}
				this.store.add(this.makeData(records));
				this.store.commitChanges();
			},

			makeData : function(records) {
				var rs = [];
				var repit;
				for (var i = 0; i < records.length; i++) {
					repit = false;
					var record = records[i];
					var data = record.data;
					for (var j = 0; j < this.store.data.length; j++) {
						var old = this.store.getAt(j).data;
						if (old.archiveId == data.recordId
								&& old.archiveType == data.recordType) {
							repit = true;
							break;
						}
					}
					if (repit == true) {
						continue;
					}
					var r = {};
					r.archiveId = data.recordId;
					r.archiveType = data.recordType;
					r.archiveType_text = data.recordType_text;
					r.sourceDoctor = data.manaDoctorId;
					r.sourceDoctor_text = data.manaDoctorId_text;
					r.sourceUnit = data.manaUnitId;
					r.sourceUnit_text = data.manaUnitId_text;
					r.empiId = data.empiId;
					r.affirmType = "n";
					r.affirmType_text = "否";
					rs.push(new Ext.data.Record(r));
				}
				return rs;
			},

			doRemove : function(item, e) {
				
				var records = this.getSelectedRecords();
				if (records == null) {
					return;
				}
				if (this.delRows == null) {
					this.delRows = [];
				}
				Ext.Msg.show({
							title : "删除确认",
							msg : '是否确定要删除所选档案?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.removeRecord(records);
								}
							},
							scope : this
						});
			},

			removeRecord : function(records) {
				for (var i = 0; i < records.length; i++) {
					var record = records[i];
					var detailId = record.get("detailId");
					if (detailId) {
						this.delRows.push(detailId);
					}
				}
				this.removeListRecords(records);
			},

			removeListRecords : function(records) {
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					this.store.remove(r);
				}
				this.clearSelect();
				this.store.commitChanges();
			},

			getSelectRecords : function() {
				var records = this.getSelectedRecords()
				if (!records || records.length < 1) {
					Ext.MessageBox.alert("提示", "未选择需要修改的档案！")
					return null;
				}
				return this.getListRecords(records);
			},

			getSaveRecords : function() {
				var count = this.store.data.length;
				if (count < 1) {
					Ext.MessageBox.alert("提示", "未选择需要修改的档案！")
					return null;
				}
				var records = [];
				for (var i = 0; i < count; i++) {
					var storeItem = this.store.getAt(i);
					records.push(storeItem);
				}
				return this.getListRecords(records);
			},

			getListRecords : function(records) {
				var list = [];
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					var data = r.data;
					var targetDoctor = data.targetDoctor;
					if (!targetDoctor) {
						Ext.MessageBox.alert("提示", "第" + (i + 1) + "行现医生不能为空！")
						this.grid.getView().focusRow(i);
						this.grid.getSelectionModel().selectRow(i);
						return null;
					}
					var targetUnit = data.targetUnit;
					if (!targetUnit) {
						Ext.MessageBox.alert("提示", "第" + (i + 1)
										+ "行现管辖机构不能为空！")
						// this.grid.getView().focusRow(i);
						// this.grid.getSelectionModel().selectRow(i);
						return null;
					}
					var sourceUnit = data.sourceUnit;
					var sourceDoctor = data.sourceDoctor;
					if (sourceDoctor == targetDoctor
							&& sourceUnit == targetUnit) {
						Ext.MessageBox.alert("提示", "第" + (i + 1)
										+ "行原现管理信息相同，无需迁移！")
						return null;
					}

					list.push(r.data);
				}
				return list;
			},

			initListData : function(listData) {
				this.store.removeAll();
				this.resetButtons()
				var records = this.getExtRecord(listData);
				this.store.add(records);
				this.store.commitChanges();
				this.clearSelect();
			},

			setBtnApply : function() {
				if (!this.grid.getTopToolbar()) {
					return;
				}
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount()
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					if (btn) {
						btn.enable();
					}
				}
			}

		})