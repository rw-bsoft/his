$package("chis.application.dbs.script.visit")

$import("chis.script.BizSimpleListView","chis.script.util.helper.Helper")

chis.application.dbs.script.visit.DiabetesComplicationListView = function(cfg) {
	this.entryName = "chis.application.dbs.schemas.MDC_DiabetesComplication"
	cfg.listServiceId = "chis.diabetesVisitService"
	chis.application.dbs.script.visit.DiabetesComplicationListView.superclass.constructor.apply(this,
			[cfg]);
	this.height = 400
	this.width = 980
	this.updateCls = "chis.application.dbs.script.visit.DiabetesComplicationFormView"
}

Ext.extend(chis.application.dbs.script.visit.DiabetesComplicationListView,
		chis.script.BizSimpleListView, {
//			init : function() {
//				chis.application.dbs.script.visit.DiabetesComplicationListView.superclass.init
//						.call(this);
//				if (!this.schema) {
//					var re = util.schema.loadSync(this.entryName)
//					if (re.code == 200) {
//						this.schema = re.schema;
//					} else {
//						this.processReturnMsg(re.code, re.msg, this.init)
//						return;
//					}
//				}
//				var years = {
//					id : "years",
//					alias : "病程年数",
//					type : "string",
//					acValue : "1111",
//					display : "1"
//				}
//
//				if (!this.checkItemExists(this.schema, years)) {
//					this.schema.items.splice(4, 0, years);
//				}
//
//			},
//			checkItemExists : function(schema, item) {
//				for (var i = 0; i < schema.items.length; i++) {
//					var schemaItem = schema.items[i];
//					if (schemaItem.id == item.id)
//						return true;
//				}
//				return false;
//			},
			loadData:function(){
				this.requestData.serviceAction = "getVisitComplication";
				this.requestData.r = this.exContext.args.r.data
				chis.application.dbs.script.visit.DiabetesComplicationListView.superclass.loadData.call(this)
				this.resetButton(this.exContext.control)
			}
			,
			resetButton : function(data) {
				var btns = this.grid.getTopToolbar().items;
				if (!btns) {
					return;
				}
				var n = btns.getCount();
				for (var i = 0; i < n; i++) {
					var btn = btns.item(i)
					var obj = data["_actions"]
					if (obj) {
						var status = obj["update"]
						if (status) {
							btn.enable()
						} else {
							btn.disable()
						}
					}
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store)
				if (records.length == 0) {
					return
				}
				if (this.win) {
					this.win.doLayout()
				}
				this.selectFirstRow()
				for (var i = 0; i < records.length; i++) {
					var diagnosisDate = records[i].get("diagnosisDate")
					var v = this.calculateYears(diagnosisDate)
					records[i].set("years", v)
					records[i].commit()
				}
			},
			doAction : function(item, e) {
				this.dbClick = true
				if (item.cmd == "add") {
					var cfg = {}
					cfg.title = "糖尿病并发症"
					cfg.mainApp = this.mainApp
					cfg.exContext = this.exContext
					var module = this.midiModules["DiabetesComplicationModule"]
					if (!module) {
						$import("chis.application.dbs.script.visit.DiabetesComplicationModule")
						module = new chis.application.dbs.script.visit.DiabetesComplicationModule(cfg)
						module.on("saveRecord", this.onSaveRecord, this)
						this.midiModules["DiabetesComplicationModule"] = module
					} else {
						Ext.apply(module, cfg)
					}
					module.getWin().show()
				} else {
					this.dbClick = false
					var cmd = item.cmd
					cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
					var action = this["do" + cmd]
					action.apply(this, [item, e])
				}
			},

			loadModule : function(cls, entryName, item) {
				if (this.loading) {
					return
				}
				var cmd = item.cmd
				var cfg = {}
				cfg.title = "并发症增加"
				cfg.entryName = entryName
				cfg.op = "create"
				cfg.exContext = this.exContext
				cfg.mainApp = this.mainApp
				cfg.actions = [{
							id : "save",
							name : "确定"
						}, {
							id : "cancel",
							name : "取消",
							iconCls : "common_cancel"
						}]
				Ext.apply(cfg.exContext, this.exContext)

				var m = this.midiModules["DiabetesComplicationFormView"]
				if (!m) {
					this.loading = true
					$require(cls, [function() {
						this.loading = false
						cfg.autoLoadData = false;
						var module = eval("new " + cls + "(cfg)")
						module.on("save", this.onSave, this)
						module.on("close", this.active, this)
						this.midiModules["DiabetesComplicationFormView"] = module
						this.fireEvent("loadModule", module)
						this.openModule(cmd, 100, 50)
					}, this])
				} else {
					Ext.apply(m, cfg)
					this.openModule(cmd)
				}
			},
			openModule : function(cmd, xy) {
				var module = this.midiModules["DiabetesComplicationFormView"]
				if (module) {
					var win = module.getWin()
					if (xy) {
						win.setPosition(xy[0], xy[1])
					}
					win.setTitle(module.title)
					win.add(module.initPanel())
					win.show()
				}
			},
			onSave : function(entryName, op, json, rec) {
				this.midiModules["DiabetesComplicationFormView"].onEsc()
				this.requestData.cnd = ["eq", ["$", "visitId"],
						["s", this.visitId]]
				this.refresh()
			},
			onReady : function() {
				chis.application.dbs.script.visit.DiabetesComplicationListView.superclass.onReady
						.call(this)
				if (this.action == "create") {
					this.setButtonsDisable()
				}
			},
			setButtonsDisable : function() {
				if (this.showButtonOnTop) {
					var btns = this.grid.getTopToolbar().items;
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						btn.disable()
					}
				} else {
					var btns = this.grid.buttons
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						btn.disable()
					}
				}
			},
			setButtonsEnable : function() {
				if (this.showButtonOnTop) {
					var btns = this.grid.getTopToolbar().items;
					var n = btns.getCount()
					for (var i = 0; i < n; i++) {
						var btn = btns.item(i)
						btn.enable()
					}
				} else {
					var btns = this.grid.buttons
					for (var i = 0; i < btns.length; i++) {
						var btn = btns[i]
						btn.enable()
					}
				}
			},
			active : function() {

			},
			onDblClick : function(grid, index, e) {
				if (!this.dbClick) {
					return
				}
				var r = grid.getSelectionModel().getSelected()
				var cfg = {}
				cfg.title = "并发症修改"
				cfg.initDataId = r.get("complicationId")
				cfg.autoLoadData = true
				cfg.showButtonTop = false
				cfg.isCombined = true
				cfg.autoLoadSchema = false
				cfg.mainApp = this.mainApp
				cfg.actions = [{
							id : "save",
							name : "确定"
						}, {
							id : "cancel",
							name : "取消",
							iconCls : "common_cancel"
						}]
				$import(this.updateCls)
				var module = this.midiModules["DiabetesComplicationFormView"]
				if (!module) {
					module = eval("new " + this.updateCls + "(cfg)")
					module.on("save", this.onSave, this)
					this.midiModules["DiabetesComplicationFormView"] = module
				} else {
					Ext.apply(module, cfg)
					module.loadData()
				}
				module.getWin().show();

			},
			doAddRecord : function() {
				this.modified = true
				this.fireEvent("addRecord")
			},
			// 外面列表的删除功能
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				Ext.Msg.show({
							title : '确认删除记录[' + r.get('complicationCode_text') + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove();
								}
							},
							scope : this
						})
			},
			doRemoveRecord : function() {
				this.modified = true
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.processRemoveReocrd(r)
			},
			processRemoveReocrd : function(r) {
				var rowIndex = this.store.indexOf(r)
				this.store.remove(r)
				rowIndex = rowIndex >= this.store.getCount()
						? rowIndex - 1
						: rowIndex;
				this.grid.getSelectionModel().selectRow(rowIndex);
			},
			doSaveRecord : function() {
				var array = []
				for (var i = 0; i < this.store.getCount(); i++) {
					var r = this.store.getAt(i)
					r.data["visitId"] = this.exContext.args.r.get("visitId");
					array.push(r.data)
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest({
							serviceId : "chis.diabetesVisitService",
							schema : "MDC_DiabetesComplication",
							array : array,
							method:"execute",
							body:{phrId:this.exContext.ids.phrId,visitId:this.exContext.args.r.get("visitId")},
							serviceAction : "saveDiabetesComplications"
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg)
								return
							}
							this.grid.el.unmask()
							this.fireEvent("saveRecord")
						}, this)
				this.modified = false
			},
			doCancel : function() {
				if (this.modified) {
					Ext.Msg.show({
								title : "提示消息",
								msg : '数据已经改变，是否需要保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.YESNO,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "yes") {
										this.doSaveRecord()
									} else {
										this.fireEvent("cancel")
									}
								},
								scope : this
							});
				} else {
					this.fireEvent("cancel")
				}
			},
			addRecord : function(r) {
				var record = r

				var complication = record.get("complicationCode")
				if (this.store.find("complicationCode", complication) > -1) {
					return
				}

				var diagnosisDate = record.get("diagnosisDate")
				var v = this.calculateYears(diagnosisDate)
				record.set("years", v)
				record.commit()
				this.store.add(record)
			},
			calculateYears : function(d) {
				var month = chis.script.util.helper.Helper.getAgeMonths(Date.parseDate(d, "Y-m-d"),
						Date.parseDate(this.mainApp.serverDate, "Y-m-d"));
				if (month < 1) {
					var days = chis.script.util.helper.Helper.getPeriod(Date.parseDate(d, "Y-m-d"),
							Date.parseDate(this.mainApp.serverDate, "Y-m-d"));
					return days + "天";
				}
				if (month >= 12) {
					var years = parseInt(month / 12);
					var remainMonth = month - years * 12;
					if (remainMonth == 0) {
						return years + "年";
					}
					return years + "年" + remainMonth + "月";
				}
				return month + "月";
			},
			onSaveRecord : function() {
				this.refresh()
			}
		});