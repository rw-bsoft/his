/**
 * 批量修改管理医生明细列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mov.script.batch")
$import("chis.script.BizSelectListView")
chis.application.mov.script.batch.ManaInfoBatchChangeDetailList = function(cfg) {
	cfg.pageSize = -1;
	chis.application.mov.script.batch.ManaInfoBatchChangeDetailList.superclass.constructor
			.apply(this, [cfg]);
	this.enableCnd = false;
	this.disablePagingTbr = true;
};
Ext.extend(chis.application.mov.script.batch.ManaInfoBatchChangeDetailList,
		chis.script.BizSelectListView, {

			initPanel : function(schema) {
				var grid = app.modules.list.SelectListView.superclass.initPanel
						.call(this, schema);
				this.grid = grid;
				if (this.win) {
					this.win.on("show", function() {
								if (this.sm) {
									this.sm.clearSelections();
								}
							}, this);
				}
				return grid;
			},

			doAdd : function() {
				
				var moduleName = this.queryEntryName + "QueryModule";
				var module = this.midiModules[moduleName];
				if (!module) {
					var cls = "chis.application.mov.script.util.QueryModule";
					$import(cls);
					var cfg = {
						title : this.queryFrmName,
						buttonIndex : 3,
						isCombined : true,
						mutiSelect : true,
						autoLoadData : false,
						autoLoadSchema : true,
						queryCndsType : "filter",
						entryName : this.queryEntryName,
						mainApp:this.mainApp
					};
					module = eval("new " + cls + "(cfg)");
					module.on("recordSelected", this.onRecordSelected, this);
					this.midiModules[moduleName] = module;
				}
				this.showWin(module);
			},

			onRecordSelected : function(records) {
				if (!records) {
					return;
				}
				this.store.add(this.makeData(records));
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
						if (old.archiveId == (data.pregnantId || data.phrId)) {
							repit = true;
							break;
						}
					}
					if (repit == true) {
						continue;
					}
					var r = {};
					if (this.queryEntryName == "chis.application.mov.schemas.MOV_HealthRecordQuery") {
						
						r.archiveId = data.phrId;
						r.sourceDoctor = data.manaDoctorId;
						r.sourceDoctor_text = data.manaDoctorId_text;
					} else if (this.queryEntryName == "chis.application.mov.schemas.MOV_HealthCardQuery") {
							
						r.archiveId = data.phrId;
						r.sourceDoctor = data.cdhDoctorId;
						r.sourceDoctor_text = data.cdhDoctorId_text;
					} else if (this.queryEntryName == "chis.application.mov.schemas.MOV_PregnantRecordQuery") {
							
						r.archiveId = data.pregnantId;
						r.sourceDoctor = data.mhcDoctorId;
						r.sourceDoctor_text = data.mhcDoctorId_text;
					}
					r.empiId = data.empiId;
					r.sexCode = data.sexCode;
					r.sexCode_text = data.sexCode_text;
					r.birthday = data.birthday;
					r.idCard = data.idCard;
					r.personName = data.personName;
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

			getSaveRecords : function() {
				var records = this.getSelectedRecords()
				if (!records || records.length < 1) {
					return null;
				}
				var list = [];
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					list.push(r.data);
				}
				return list;
			},

			initListData : function(listData) {
				this.store.removeAll();
				this.resetButtons();
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