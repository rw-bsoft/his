$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.InventoryInUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryInUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.InventoryInUndeterminedList,
		phis.script.SimpleList, {
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.InventoryInUndeterminedList.superclass.onReady
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
																	"str(a.ZDSJ,'yyyy-mm-dd')"],
															['s', startDate]],
													[
															'le',
															['$',
																	"str(a.ZDSJ,'yyyy-mm-dd')"],
															['s', endDate]]],
											['eq', ['$', 'DJZT'], ['i', 0]]],
									['eq', ['$', 'PDFS'], ['i', 1]]],
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
																	"str(a.ZDSJ,'yyyy-mm-dd')"],
															['s', startDate]],
													[
															'le',
															['$',
																	"str(a.ZDSJ,'yyyy-mm-dd')"],
															['s', endDate]]],
											['eq', ['$', 'DJZT'], ['i', 0]]],
									['eq', ['$', 'PDFS'], ['i', 1]]],
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
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
							id : 'inventoryInUndeterminedListdjzt',
							name : 'inventoryInUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '制单',
										name : 'inventoryInUndeterminedListdjzt',
										inputValue : 0
									}, {
										boxLabel : '提交',
										name : 'inventoryInUndeterminedListdjzt',
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
				         	text : "提交日期:"
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'inventoryInUndeterminedListdateFrom',
							name : 'inventoryInUndeterminedListdateFrom',
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
							id : 'inventoryInUndeterminedListdateTo',
							name : 'inventoryInUndeterminedListdateTo',
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
																"str(a.ZDSJ,'yyyy-mm-dd')"],
														['s', startDate]],
												[
														'le',
														['$',
																"str(a.ZDSJ,'yyyy-mm-dd')"],
														['s', endDate]]],
										[
												'eq',
												['$', 'DJZT'],
												[
														'i',
														this.statusRadio
																.getValue().inputValue]]],
								['eq', ['$', 'PDFS'], ['i', glfs]]],
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
																"str(a.ZDSJ,'yyyy-mm-dd')"],
														['s', startDate]],
												[
														'le',
														['$',
																"str(a.ZDSJ,'yyyy-mm-dd')"],
														['s', endDate]]],
										[
												'eq',
												['$', 'DJZT'],
												[
														'i',
														this.statusRadio
																.getValue().inputValue]]],
								['eq', ['$', 'PDFS'], ['i', glfs]]],
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
				if (r1.code == 700) {
					MyMessageTip.msg("提示", "该库房没有生成盘点单，不能盘点录入!", true);
					return;
				}
				if (this.GLFS == 1) {
					this.inventoryInModule = this.opener.createModule(
							"inventoryInModule", this.addRefkc);
					this.inventoryInModule.GLFS = this.GLFS
					this.inventoryInModule.on("save", this.onSave, this);
					this.inventoryInModule.initPanel();
					var win = this.inventoryInModule.getWin();
					win.add(this.inventoryInModule.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryInModule.op = "create";
						this.inventoryInModule.type = "normal";
						this.inventoryInModule.doNew();
					}
				} else if (this.GLFS == 2) {
					this.inventoryInModuleks = this.opener.createModule(
							"inventoryInModuleks", this.addRefks);
					this.inventoryInModuleks.GLFS = this.GLFS
					this.inventoryInModuleks.on("save", this.onSave, this);
					this.inventoryInModuleks.initPanel();
					var win = this.inventoryInModuleks.getWin();
					win.add(this.inventoryInModuleks.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryInModuleks.op = "create";
						this.inventoryInModuleks.type = "normal";
						this.inventoryInModuleks.doNew();
					}
				} else {
					this.inventoryInModuletz = this.opener.createModule(
							"inventoryInModuletz", this.addReftz);
					this.inventoryInModuletz.GLFS = this.GLFS
					this.inventoryInModuletz.on("save", this.onSave, this);
					this.inventoryInModuletz.initPanel();
					var win = this.inventoryInModuletz.getWin();
					win.add(this.inventoryInModuletz.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryInModuletz.op = "create";
						this.inventoryInModuletz.type = "normal";
						this.inventoryInModuletz.doNew();
					}
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["LRXH"] = r.data.LRXH;
				if (this.GLFS == 1) {
					this.inventoryInModule = this.opener.createModule(
							"inventoryInModule", this.addRefkc);
					this.inventoryInModule.GLFS = this.GLFS
					this.inventoryInModule.on("save", this.onSave, this);
					var win = this.inventoryInModule.getWin();
					win.add(this.inventoryInModule.initPanel());
					this.inventoryInModule.doUpd();
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.inventoryInModule.changeButtonState("save");
					} else if (djzt == 1) {
						this.inventoryInModule.changeButtonState("subt");
					}
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryInModule.op = "update";
						this.inventoryInModule.initDataBody = initDataBody;
						this.inventoryInModule.loadData(initDataBody);
					}
				} else if (this.GLFS == 2) {
					this.inventoryInModuleks = this.opener.createModule(
							"inventoryInModuleks", this.addRefks);
					this.inventoryInModuleks.GLFS = this.GLFS
					this.inventoryInModuleks.on("save", this.onSave, this);
					var win = this.inventoryInModuleks.getWin();
					win.add(this.inventoryInModuleks.initPanel());
					this.inventoryInModuleks.doUpd();
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.inventoryInModuleks.changeButtonState("save");
					} else if (djzt == 1) {
						this.inventoryInModuleks.changeButtonState("subt");
					}
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryInModuleks.op = "update";
						this.inventoryInModuleks.initDataBody = initDataBody;
						this.inventoryInModuleks.loadData(initDataBody);
					}
				} else {
					this.inventoryInModuletz = this.opener.createModule(
							"inventoryInModuletz", this.addReftz);
					this.inventoryInModuletz.GLFS = this.GLFS
					this.inventoryInModuletz.on("save", this.onSave, this);
					var win = this.inventoryInModuletz.getWin();
					win.add(this.inventoryInModuletz.initPanel());
					this.inventoryInModuletz.doUpd();
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.inventoryInModuletz.changeButtonState("save");
					} else if (djzt == 1) {
						this.inventoryInModuletz.changeButtonState("subt");
					}
					win.show()
					win.center()
					if (!win.hidden) {
						this.inventoryInModuletz.op = "update";
						this.inventoryInModuletz.initDataBody = initDataBody;
						this.inventoryInModuletz.loadData(initDataBody);
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
				body["LRXH"] = r.data.LRXH;
				var record = {};
				var data = {};
				record["data"] = data;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeInventoryInActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			}
		})