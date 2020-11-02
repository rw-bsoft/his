$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.sup.script.MaterialsUndeterminedOutList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	cfg.autoLoadData = false;
	phis.application.sup.script.MaterialsUndeterminedOutList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.sup.script.MaterialsUndeterminedOutList,
		phis.script.SimpleList, {
			onWinShow : function() {
				this.materialsOutDetailModule.zblb = this.zblb;
			},
			onReady : function() {
				if (this.grid) {
					phis.application.sup.script.MaterialsUndeterminedOutList.superclass.onReady
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
													['eq', ['$', 'DJZT'],
															['i', 0]]],
											[
													'eq',
													['$', 'KFXH'],
													[
															'i',
															this.mainApp['phis'].treasuryId]]],
									['eq', ['$', 'ZBLB'], ['i', this.zblb]]],
							['le', ['$', 'DJLX'], ['i', 4]]];
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
													['eq', ['$', 'DJZT'],
															['i', 0]]],
											[
													'eq',
													['$', 'KFXH'],
													[
															'i',
															this.mainApp['phis'].treasuryId]]],
									['eq', ['$', 'ZBLB'], ['i', this.zblb]]],
							['le', ['$', 'DJLX'], ['i', 4]]];
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
				// this.checkInWay = this.getCheckInWay();
				var filelable = new Ext.form.Label({
							text : "单据状态:"
						})
				this.statusRadio = new Ext.form.RadioGroup({
							height : 20,
							width : 100,
							id : 'djztck',
							name : 'djztck', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '制单',
										name : 'djztck',
										inputValue : 0
									}, {
										boxLabel : '审核',
										name : 'djztck',
										inputValue : 1
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									this.doRefreshWin();
		
								},
								scope : this
							}
						});
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var datelable = new Ext.form.Label({
							text : "单据日期:"
						})
				this.dateFrom = new Ext.form.DateField({
							id : 'stardate',
							name : 'stardate',
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
							id : 'enddate',
							name : 'enddate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
				return ["<h1 style='text-align:center'>未记账出库单:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
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
																['s', startDate]],
														[
																'le',
																['$',
																		"str(a.CKRQ,'yyyy-mm-dd')"],
																['s', endDate]]],
												[
														'eq',
														['$', 'DJZT'],
														[
																'i',
																this.statusRadio
																		.getValue().inputValue]]],
										['eq', ['$', 'KFXH'],
												['i', this.mainApp['phis'].treasuryId]]],
								['eq', ['$', 'ZBLB'], ['i', this.zblb]]],
						['le', ['$', 'DJLX'], ['i', 4]]];
				this.loadData();
			},
			// 新增
			doAdd : function() {
				this.materialsOutDetailModule = this.oper.createModule(
						"materialsOutDetailModule", this.addRef);
				this.materialsOutDetailModule.zblb = this.zblb;
				this.materialsOutDetailModule.on("save", this.onSave, this);
				this.materialsOutDetailModule.oper = this;
				this.materialsOutDetailModule.initPanel();
				var win = this.materialsOutDetailModule.getWin();
				win.add(this.materialsOutDetailModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.materialsOutDetailModule.op = "create";
					this.materialsOutDetailModule.doNew();
				}
			},
			// 提交
			doComit : function() {
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
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.verificationMaterialsOutDeleteActionId,
					body : initDataBody
				});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doUpd);
					this.doRefresh();
					return;
				}
				this.materialsOutDetailModule = this.oper.createModule(
						"materialsOutDetailModule", this.addRef);
				this.materialsOutDetailModule.zblb = this.zblb;
				this.materialsOutDetailModule.on("save", this.onSave, this);
				this.materialsOutDetailModule.oper = this;
				var win = this.materialsOutDetailModule.getWin();
				win.add(this.materialsOutDetailModule.initPanel());
				this.materialsOutDetailModule.doUpd();
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.materialsOutDetailModule.changeButtonState("save");
				} else if (djzt == 1) {
					this.materialsOutDetailModule.changeButtonState("verified");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.materialsOutDetailModule.op = "update";
					this.materialsOutDetailModule.initDataBody = initDataBody;
					this.materialsOutDetailModule.loadData(initDataBody);
				}

			},
			onDblClick : function(grid, index, e) {
				this.doUpd();

			},
			doReject : function() {
				this.materialsOutDetailModule = this.oper.createModule(
						"materialsOutDetailModule", this.addRef);
				this.materialsOutDetailModule.zblb = this.zblb;
				this.materialsOutDetailModule.on("save", this.onSave, this);
				this.materialsOutDetailModule.initPanel();
				var win = this.materialsOutDetailModule.getWin();
				win.add(this.materialsOutDetailModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.materialsOutDetailModule.op = "create";
					this.materialsOutDetailModule.type = "reject";
					this.materialsOutDetailModule.doNew();
				}
			},
			onSave : function() {
				this.fireEvent("save", this);
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("DJLX") == 3) {
					Ext.Msg.alert("提示", "盘点产生单据不允许删除");
					return;
				}
                if (r.get("DJLX") == 2) {
                    Ext.Msg.alert("提示", "消耗明细产生单据不允许删除");
                    return;
                }
				if (r.get("DJZT") == 1) {
					Ext.Msg.alert("提示", "单据状态不为制单的不允许删除");
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
			},
			doPrint : function() {
				var module = this.createModule("noStoreListPrint",
						this.refNoStoreListPrint)
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
					return;
				}
				if(r.get("DJZT") == 0){
					MyMessageTip.msg("提示", "打印失败：没有审核不能打印!", true);
					return;
				}
				module.DJXH = r.data.DJXH;
				module.initPanel();
				module.doPrint();
			}
		})