$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.InventoryUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryUndeterminedList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.InventoryUndeterminedList,
		phis.script.SimpleList, {
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.InventoryUndeterminedList.superclass.onReady
							.call(this);
					var startDate = "";// 开始时间
					var endDate = ""; // 结束时间
					if (this.dateFrom) {
						startDate = new Date(this.dateFrom.getValue())
								.format("Y-m-d");
					}
					if (this.dateTo) {
						endDate = new Date(this.dateTo.getValue())
								.format("Y-m-d");
					}
					this.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													[
															'ge',
															['$',
																	"str(a.ZDRQ,'yyyy-mm-dd')"],
															['s', startDate]],
													[
															'le',
															['$',
																	"str(a.ZDRQ,'yyyy-mm-dd')"],
															['s', endDate]]],
											['eq', ['$', 'DJZT'], ['i', 0]]],
									['eq', ['$', 'GLFS'], ['i', 1]]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					this.initCnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													[
															'ge',
															['$',
																	"str(a.ZDRQ,'yyyy-mm-dd')"],
															['s', startDate]],
													[
															'le',
															['$',
																	"str(a.ZDRQ,'yyyy-mm-dd')"],
															['s', endDate]]],
											['eq', ['$', 'DJZT'], ['i', 0]]],
									['eq', ['$', 'GLFS'], ['i', 1]]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					// 设置分页信息
					this.loadData();
				}
			},

			expansion : function(cfg) {
							var bar = cfg.tbar;
							cfg.tbar = {
								enableOverflow : true,
								items : bar
							}
						},
			getCndBar : function(items) {
				var filelable = new Ext.form.Label({
							text : "单据状态:"
						})
				this.statusRadio = new Ext.form.RadioGroup({
							height : 20,
							width : 100,
							id : 'inventoryUndeterminedListdjzt',
							name : 'inventoryUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '制单',
										name : 'inventoryUndeterminedListdjzt',
										inputValue : 0
									}, {
										boxLabel : '审核',
										name : 'inventoryUndeterminedListdjzt',
										inputValue : 1
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									djztValue = parseInt(newValue.inputValue);
									if (djztValue == 0) {
										datelable.setDisabled(true);
										this.dateFrom.setDisabled(true);
										tolable.setDisabled(true);
										this.dateTo.setDisabled(true);
									} else if (djztValue == 1) {
										datelable.setDisabled(false);
										this.dateFrom.setDisabled(false);
										tolable.setDisabled(false);
										this.dateTo.setDisabled(false);
									}

								},
								scope : this
							}
						});
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
							text : "审核日期:"
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'inventoryUndeterminedListdateFrom',
							name : 'inventoryUndeterminedListdateFrom',
							value : dateFromValue,
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						});
				var tolable = new Ext.form.Label({
							text : " 到 "
						});
				this.dateTo = new Ext.form.DateField({
							id : 'inventoryUndeterminedListdateTo',
							name : 'inventoryUndeterminedListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});

				return ["<h1 style='text-align:center'>未记账盘点单:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
			// 刷新页面
			doRefreshWin : function() {
				var glfs = parseInt(this.GLFS);
				this.clear();
				var startDate = "";// 开始时间
				var endDate = ""; // 结束时间
				if (this.dateFrom) {
					startDate = new Date(this.dateFrom.getValue())
							.format("Y-m-d");
				}
				if (this.dateTo) {
					endDate = new Date(this.dateTo.getValue()).format("Y-m-d");
				}
				this.requestData.cnd = [
						'and',
						[
								'and',
								[
										'and',
										[
												'and',
												[
														'ge',
														['$',
																"str(a.ZDRQ,'yyyy-mm-dd')"],
														['s', startDate]],
												[
														'le',
														['$',
																"str(a.ZDRQ,'yyyy-mm-dd')"],
														['s', endDate]]],
										[
												'eq',
												['$', 'DJZT'],
												[
														'i',
														this.statusRadio
																.getValue().inputValue]]],
								['eq', ['$', 'GLFS'], ['i', glfs]]],
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]]];
				this.loadData();
			},
			// 刷新页面
			doRefreshWinGlfs : function(glfs) {
				var glfs = parseInt(glfs);
				this.clear();
				var startDate = "";// 开始时间
				var endDate = ""; // 结束时间
				if (this.dateFrom) {
					startDate = new Date(this.dateFrom.getValue())
							.format("Y-m-d");
				}
				if (this.dateTo) {
					endDate = new Date(this.dateTo.getValue()).format("Y-m-d");
				}
				this.requestData.cnd = [
						'and',
						[
								'and',
								[
										'and',
										[
												'and',
												[
														'ge',
														['$',
																"str(a.ZDRQ,'yyyy-mm-dd')"],
														['s', startDate]],
												[
														'le',
														['$',
																"str(a.ZDRQ,'yyyy-mm-dd')"],
														['s', endDate]]],
										[
												'eq',
												['$', 'DJZT'],
												[
														'i',
														this.statusRadio
																.getValue().inputValue]]],
								['eq', ['$', 'GLFS'], ['i', glfs]]],
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]]];
				this.loadData();
			},
			// 新增
			doAdd : function() {
				var body = {};
				body["GLFS"] = this.GLFS;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "inventoryService",
							serviceAction : "queryPDXX",
							body : body
						});
				if (r1.code == 600) {
					MyMessageTip.msg("提示", "盘点单已存在，不能重复盘点!", true);
					return;
				}
				if (this.GLFS == 1) {
					this.inventoryModule = this.opener.createModule("inventoryModule",
							this.addRefkc);
					this.inventoryModule.GLFS = this.GLFS
					this.inventoryModule.on("save", this.onSave, this);
					this.inventoryModule.initPanel();
					var win = this.inventoryModule.getWin();
					win.add(this.inventoryModule.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryModule.op = "create";
						this.inventoryModule.type = "normal";
						this.inventoryModule.doNew();
					}
				} else if (this.GLFS == 2) {
					this.inventoryModuleks = this.opener.createModule(
							"inventoryModuleks", this.addRefks);
					this.inventoryModuleks.GLFS = this.GLFS
					this.inventoryModuleks.on("save", this.onSave, this);
					this.inventoryModuleks.initPanel();
					var win = this.inventoryModuleks.getWin();
					win.add(this.inventoryModuleks.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryModuleks.op = "create";
						this.inventoryModuleks.type = "normal";
						this.inventoryModuleks.doNew();
					}
				} else {
					this.inventoryModuletz = this.opener.createModule(
							"inventoryModuletz", this.addReftz);
					this.inventoryModuletz.GLFS = this.GLFS
					this.inventoryModuletz.on("save", this.onSave, this);
					this.inventoryModuletz.initPanel();
					var win = this.inventoryModuletz.getWin();
					win.add(this.inventoryModuletz.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryModuletz.op = "create";
						this.inventoryModuletz.type = "normal";
						this.inventoryModuletz.doNew();
					}
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				if (this.GLFS == 1) {
					this.inventoryModule = this.opener.createModule("inventoryModule",
							this.addRefkc);
					this.inventoryModule.GLFS = this.GLFS
					this.inventoryModule.on("save", this.onSave, this);
					var win = this.inventoryModule.getWin();
					win.add(this.inventoryModule.initPanel());
					this.inventoryModule.doUpd();
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.inventoryModule.changeButtonState("save");
					} else if (djzt == 1) {
						this.inventoryModule.changeButtonState("verified");
					}
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryModule.op = "update";
						this.inventoryModule.initDataBody = initDataBody;
						this.inventoryModule.loadData(initDataBody);
					}
				} else if (this.GLFS == 2) {
					this.inventoryModuleks = this.opener.createModule(
							"inventoryModuleks", this.addRefks);
					this.inventoryModuleks.GLFS = this.GLFS
					this.inventoryModuleks.on("save", this.onSave, this);
					var win = this.inventoryModuleks.getWin();
					win.add(this.inventoryModuleks.initPanel());
					this.inventoryModuleks.doUpd();
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.inventoryModuleks.changeButtonState("save");
					} else if (djzt == 1) {
						this.inventoryModuleks.changeButtonState("verified");
					}
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryModuleks.op = "update";
						this.inventoryModuleks.initDataBody = initDataBody;
						this.inventoryModuleks.loadData(initDataBody);
					}
				} else {
					this.inventoryModuletz = this.opener.createModule(
							"inventoryModuletz", this.addReftz);
					this.inventoryModuletz.GLFS = this.GLFS
					this.inventoryModuletz.on("save", this.onSave, this);
					var win = this.inventoryModuletz.getWin();
					win.add(this.inventoryModuletz.initPanel());
					this.inventoryModuletz.doUpd();
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.inventoryModuletz.changeButtonState("save");
					} else if (djzt == 1) {
						this.inventoryModuletz.changeButtonState("verified");
					}
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryModuletz.op = "update";
						this.inventoryModuletz.initDataBody = initDataBody;
						this.inventoryModuletz.loadData(initDataBody);
					}
				}
			},
			onSave : function() {
				this.fireEvent("save", this);
			},
			// 提交
			doCommit : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				if (r.data.DJZT != 1) {
					Ext.Msg.alert("提示", "非审核状态，不能记账");
					return;
				}
				this.doUpd();
			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "修改";
				item.cmd = "upd";
				this.doAction(item, e)
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("DJZT") != 0) {
					Ext.Msg.alert("提示", "非制单状态，不能删除!");
					return;
				}
				var body = {};
				body["DJXH"] = r.data.DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "inventoryService",
							serviceAction : "queryPDLRXX",
							body : body
						});
				if (r1.code == 601) {
					MyMessageTip.msg("提示", "盘点录入已存在，不能删除!", true);
					return;
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						title += r.get(this.schema.pkeys[i])
					}
				}
				// add by liyl 2012-06-17 提示信息增加名称显示功能
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(r);
								}
							},
							scope : this
						})
			},
			processRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				var body = {};
				body["DJXH"] = r.data.DJXH;
				var record = {};
				var data = {};
				record["data"] = data;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeInventoryActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								if (code == 102) {
									var exCfg = this.mainApp.taskManager.tasks.item(userDomain);
									if (exCfg) {
										exCfg.treasuryPdzt = 0;
									}
									this.mainApp['phis'].treasuryPdzt = 0;
								}
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},
            doPrint : function() {
                var r = this.getSelectedRecord()
                var module;
                if(r.get("GLFS") == 1){
                	module = this.createModule("inventoryPrint",
                        this.refInventoryPrint)
                }else if(r.get("GLFS") == 2){
                	module = this.createModule("inventoryKsPrint",
                        this.refInventoryKsPrint)
                }else if(r.get("GLFS") == 3){
                	module = this.createModule("inventoryTzPrint",
                        this.refInventoryTzPrint)
                }
                if (r == null) {
                    MyMessageTip.msg("提示", "打印失败：无效的报损单信息!", true);
                    return;
                }
//                if(r.data.DJZT==0){
//                    MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
//                    return;
//                }
                module.djxh=r.data.DJXH;
                module.initPanel();
                module.doPrint();
            }
		})