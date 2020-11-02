$package("chis.application.psy.script.record");

$import("chis.script.BizSimpleListView", "chis.application.psy.script.record.PsychosisFirstVisitMedicineForm",
		"util.dictionary.DictionaryLoader");

chis.application.psy.script.record.PsychosisFirstVisitMedicineList = function(cfg) {
	//cfg.initCnd = ["eq", ['$', 'visitId'], ['s', cfg.visitId||null]];
	cfg.entryName = "chis.application.psy.schemas.PSY_PsychosisVisitMedicine";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	chis.application.psy.script.record.PsychosisFirstVisitMedicineList.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.psy.script.record.PsychosisFirstVisitMedicineList, chis.script.BizSimpleListView, {

			doAdd : function() {
				var module = this.midiModules["PsychosisFirstVisitMedicineForm"];
				var cfg = {};
				cfg.mainApp = this.mainApp;
				cfg.showButtonOnTop = true;
				cfg.autoLoadSchema = false;
				cfg.autoLoadData = false;
				cfg.isCombined = true;
				cfg.op = "create";
				cfg.title = "增加用药种类";
				cfg.phrId = this.exContext.ids.phrId;
				cfg.visitId = this.visitId;
				cfg.entryName = this.entryName;
				if (!module) {
					module = new chis.application.psy.script.record.PsychosisFirstVisitMedicineForm(cfg);
					this.midiModules["PsychosisFirstVisitMedicineForm"] = module;
					var form = module.initPanel();
					this.form = form;
					module.on("addItem", this.onAddItem, this);
				} else {
					module.phrId = this.phrId;
					module.visitId = this.visitId;
				}
				var win = module.getWin();
				win.add(this.form);
				win.show();
			},

			onAddItem : function(data) {
				if (!this.schema) {
					return;
				}
				var items = this.schema.items;
				var r = {};
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (it.type == "date" && data[it.id]) {
						r[it.id] = data[it.id].format("Y-m-d");
						continue;
					}
					if (it.dic && data[it.id]) {
						var dic = util.dictionary.DictionaryLoader.load(it.dic);
						r[it.id] = data[it.id];
						r[it.id + "_text"] = dic["wraper"][data[it.id]].text;
						continue;
					}
					r[it.id] = data[it.id];
				}
				r.createUnit = this.mainApp.deptId;
				r.createUnit_text = this.mainApp.dept;
				r.createDate = this.mainApp.serverDate;
				r.createUser = this.mainApp.uid;
				r.createUser_text = this.mainApp.uname;
				r.lastModifyUnit = this.mainApp.deptId;
				r.lastModifyUnit_text = this.mainApp.dept;
				r.lastModifyDate = this.mainApp.serverDate;
				r.lastModifyUser = this.mainApp.uid;
				r.lastModifyUser_text = this.mainApp.uname;
				
				var record = new Ext.data.Record(r);
				this.store.add(record);
				this.grid.getSelectionModel().selectLastRow();
				this.fireEvent("medicineModified");
			},

			doDelete : function(item, e) {
				Ext.Msg.show({
							title : "删除确认",
							msg : '是否确定要删除该项药品?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									var r = this.getSelectedRecord(false);
									if (!r.get("recordId")) {
										var rowIndex = this.store.indexOf(r);
										this.store.remove(r);
										rowIndex = rowIndex >= this.store
												.getCount()
												? rowIndex - 1
												: rowIndex;
										this.grid.getSelectionModel()
												.selectRow(rowIndex);
									} else {
										//有Id直接从库中删除
										this.processRemove();
									}
								}
							},
							scope : this
						});
			},
			
			getMedicineList : function() {
				var array = [];
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i);
					array.push(r.data);
				}
				return array;
			},
			/**
			 * 设置药品管理按钮是否可用
			 * @param {true|false} ableFlag true 可用 false 不可用
			 */
			setMedicineManagerButtonState : function(ableFlag){
				var btns = this.grid.getTopToolbar().items;
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i);
					if(ableFlag){
						btn.enable();
					}else{
						btn.disable();
					}
				}
			},
			/**
			 * 获取是否要提示删除药品信息的状态
			 * @param {1|2|3} medicine 服药依从性的值[1:规律 2：间断 3 不服药]
			 */
			getDeleteMedicineState : function(medicine){
				var medicineRecordNum = this.store.getCount();
				if(medicineRecordNum>0 && (!medicine || medicine=='3' || medicine=='')){
					/*Ext.Msg.show({
						title : '消息提示',
						msg : '将同时删除服药情况中全部药品,是否继续?',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.YESNO,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "yes") {
								this.store.removeAll();
								this.store.commitChanges();
							}else {
								this.onRegainMedicineValue();
								return
							}
						},
						scope : this
					});*/
					return true;
				}else{
					return false;
				}
			},
			deleteAllMedicine : function(){
				this.store.removeAll();
				this.store.commitChanges();
			}
		});