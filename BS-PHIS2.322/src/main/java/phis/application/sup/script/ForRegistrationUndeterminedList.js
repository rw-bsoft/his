$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.ForRegistrationUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.ForRegistrationUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.ForRegistrationUndeterminedList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.ForRegistrationUndeterminedList.superclass.onReady
							.call(this);
					var startDate = "";
					var endDate = "";
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
															'and',
															[
																	'and',
																	[
																			'le',
																			[
																					'$',
																					'DJZT'],
																			[
																					'i',
																					2]],
																	[
																			'ge',
																			[
																					'$',
																					"str(a.CKRQ,'yyyy-mm-dd')"],
																			[
																					's',
																					startDate]]],
															[
																	'le',
																	['$',
																			"str(a.CKRQ,'yyyy-mm-dd')"],
																	['s',
																			endDate]]],
													['eq', ['$', 'DJLX'],
															['i', 7]]],
											[
													'eq',
													['$', 'CKKF'],
													[
															'i',
															this.mainApp['phis'].treasuryId]]],
									['eq', ['$', 'QRBZ'], ['i', 0]]],
							['eq', ['$', 'DJZT'], ['i', -1]]];
					this.initCnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													[
															'and',
															[
																	'and',
																	[
																			'le',
																			[
																					'$',
																					'DJZT'],
																			[
																					'i',
																					2]],
																	[
																			'ge',
																			[
																					'$',
																					"str(a.CKRQ,'yyyy-mm-dd')"],
																			[
																					's',
																					startDate]]],
															[
																	'le',
																	['$',
																			"str(a.CKRQ,'yyyy-mm-dd')"],
																	['s',
																			endDate]]],
													['eq', ['$', 'DJLX'],
															['i', 7]]],
											[
													'eq',
													['$', 'CKKF'],
													[
															'i',
															this.mainApp['phis'].treasuryId]]],
									['eq', ['$', 'QRBZ'], ['i', 0]]],
							['eq', ['$', 'DJZT'], ['i', -1]]];

					this.loadData();
				}
			},
			getCndBar : function(items) {
				var filelable = new Ext.form.Label({
					      text : "单据状态:"
						})
				this.statusRadio = new Ext.form.RadioGroup({
							height : 20,
							width : 100,
							id : 'forRegistrationUndeterminedListdjzt',
							name : 'forRegistrationUndeterminedListdjzt', // 锟斤拷台锟斤拷锟截碉拷JSON锟斤拷式锟斤拷直锟接革拷值
							value : "-1",
							items : [{
								        boxLabel : '制单',
										name : 'forRegistrationUndeterminedListdjzt',
										inputValue : -1
									}, {
										boxLabel : '提交',
										name : 'forRegistrationUndeterminedListdjzt',
										inputValue : 0
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									djztValue = parseInt(newValue.inputValue);
									this.doRefreshWin();
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
							id : 'forRegistrationUndeterminedListdateFrom',
							name : 'forRegistrationUndeterminedListdateFrom',
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
							id : 'forRegistrationUndeterminedListdateTo',
							name : 'forRegistrationUndeterminedListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});

				return ["<h1 style='text-align:center'>未确认登记单:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
			// 刷新页面
			doRefreshWin : function() {
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
				if (this.statusRadio.getValue().inputValue == -1) {
					this.requestData.cnd = [
							'and',
							[
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
																			"str(a.CKRQ,'yyyy-mm-dd')"],
																	['s',
																			startDate]],
															[
																	'le',
																	['$',
																			"str(a.CKRQ,'yyyy-mm-dd')"],
																	['s',
																			endDate]]],
													['eq', ['$', 'DJLX'],
															['i', 7]]],
											[
													'eq',
													['$', 'DJZT'],
													[
															'i',
															this.statusRadio
																	.getValue().inputValue]]],
									['eq', ['$', 'CKKF'],
											['i', this.mainApp['phis'].treasuryId]]],
							['eq', ['$', 'QRBZ'], ['i', 0]]];
				} else {
					this.requestData.cnd = [
							'and',
							[
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
																			"str(a.CKRQ,'yyyy-mm-dd')"],
																	['s',
																			startDate]],
															[
																	'le',
																	['$',
																			"str(a.CKRQ,'yyyy-mm-dd')"],
																	['s',
																			endDate]]],
													['eq', ['$', 'DJLX'],
															['i', 7]]],
											[
													'ge',
													['$', 'DJZT'],
													[
															'i',
															this.statusRadio
																	.getValue().inputValue]]],
									['eq', ['$', 'CKKF'],
											['i', this.mainApp['phis'].treasuryId]]],
							['eq', ['$', 'QRBZ'], ['i', 0]]];
				}
				this.loadData();
			},
			doAdd : function() {
				this.forRegistrationModule = this.oper.createModule(
						"forRegistrationModule", this.addRef);
				this.forRegistrationModule.on("save", this.onSave, this);
				this.forRegistrationModule.initPanel();
				var win = this.forRegistrationModule.getWin();
				win.add(this.forRegistrationModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.forRegistrationModule.op = "create";
					this.forRegistrationModule.type = "normal";
					this.forRegistrationModule.loadQRRK(0);
					this.forRegistrationModule.doNew();
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.forRegistrationModule = this.oper.createModule(
						"forRegistrationModule", this.addRef);
				this.forRegistrationModule.on("save", this.onSave, this);
				var win = this.forRegistrationModule.getWin();
				win.add(this.forRegistrationModule.initPanel());
				this.forRegistrationModule.doUpd();
				var djzt = r.data.DJZT;
				if (djzt == -1) {
					this.forRegistrationModule.changeButtonState("new");
				} else if (djzt >= 0 && djzt < 2) {
					this.forRegistrationModule.changeButtonState("verified");
				} else if (djzt == 2) {
					this.forRegistrationModule.changeButtonState("commit");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.forRegistrationModule.op = "update";
					this.forRegistrationModule.initDataBody = initDataBody;
					this.forRegistrationModule.loadData(initDataBody);
					this.forRegistrationModule.loadQRRK(r.data.THDJ);
				}
			},
			onSave : function() {
				this.fireEvent("save", this);
			},
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
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("DJZT") != -1) {
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
							serviceAction : this.removeMaterialsOutActionId,
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