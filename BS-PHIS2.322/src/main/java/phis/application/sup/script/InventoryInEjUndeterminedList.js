$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.InventoryInEjUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.InventoryInEjUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.InventoryInEjUndeterminedList,
		phis.script.SimpleList, {
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.InventoryInEjUndeterminedList.superclass.onReady
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
							['eq', ['$', 'KFXH'],
									['i', this.mainApp['phis'].treasuryId]]];
					this.initCnd = [
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
							id : 'inventoryInEjUndeterminedListdjzt',
							name : 'inventoryInEjUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '制单',
										name : 'inventoryInEjUndeterminedListdjzt',
										inputValue : 0
									}, {
										boxLabel : '提交',
										name : 'inventoryInEjUndeterminedListdjzt',
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
							id : 'inventoryInEjUndeterminedListdateFrom',
							name : 'inventoryInEjUndeterminedListdateFrom',
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
							id : 'inventoryInEjUndeterminedListdateTo',
							name : 'inventoryInEjUndeterminedListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});

				return ["<h1 style='text-align:center'>未记账入库单:</h1>", '-',
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
												this.statusRadio.getValue().inputValue]]],
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]]];
				this.loadData();
			},
			// 新增
			doAdd : function() {
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "inventoryService",
							serviceAction : "queryPDXXEJ",
							method : "execute"
						});
				if (r1.code == 701) {
					MyMessageTip.msg("提示", "该库房没有生成盘点单，不能盘点录入!", true);
					return;
				}
				this.inventoryInEjModule = this.oper.createModule(
						"inventoryInEjModule", this.addRefkc);
				this.inventoryInEjModule.on("save", this.onSave, this);
				this.inventoryInEjModule.initPanel();
				var win = this.inventoryInEjModule.getWin();
				win.add(this.inventoryInEjModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.inventoryInEjModule.op = "create";
					this.inventoryInEjModule.type = "normal";
					this.inventoryInEjModule.doNew();
				}

			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["LRXH"] = r.data.LRXH;
				this.inventoryInEjModule = this.oper.createModule(
						"inventoryInEjModule", this.addRefkc);
				this.inventoryInEjModule.on("save", this.onSave, this);
				var win = this.inventoryInEjModule.getWin();
				win.add(this.inventoryInEjModule.initPanel());
				this.inventoryInEjModule.doUpd();
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.inventoryInEjModule.changeButtonState("new");
				} else if (djzt == 1) {
					this.inventoryInEjModule.changeButtonState("subt");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.inventoryInEjModule.op = "update";
					this.inventoryInEjModule.initDataBody = initDataBody;
					this.inventoryInEjModule.loadData(initDataBody);
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
							method:"execute",
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