/**
 * 物资出库确认（二级）
 * 
 * @author gaof
 */
$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.SecondaryMaterialsOutUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	// cfg.autoLoadData = false;
	phis.application.sup.script.SecondaryMaterialsOutUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.SecondaryMaterialsOutUndeterminedList,
		phis.script.SimpleList, {
             loadData : function() {
                this.clear(); 
                this.requestData.serviceId = this.serviceId;
                this.requestData.serviceAction = "getCK01Info";
                
                if (this.store) {
                    if (this.disablePagingTbr) {
                        this.store.load()
                    } else {
                        var pt = this.grid.getBottomToolbar()
                        if (this.requestData.pageNo == 1) {
                            pt.cursor = 0;
                        }
                        pt.doLoad(pt.cursor)
                    }
                }
                this.resetButtons();
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
					name : 'SecondaryMaterialsOutUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
					value : "0",
					items : [{
								boxLabel : '制单',
								name : 'SecondaryMaterialsOutUndeterminedListdjzt',
								inputValue : 0
							}, {
								boxLabel : '审核',
								name : 'SecondaryMaterialsOutUndeterminedListdjzt',
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
							this.doRefreshWin();

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
							name : 'SecondaryMaterialsOutUndeterminedListdateFrom',
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
							name : 'SecondaryMaterialsOutUndeterminedListdateTo',
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
			// 刷新页面
			doRefreshWin : function() {
				if (this.statusRadio.getValue() != null) {
					if (this.statusRadio.getValue().inputValue == 0) {
//						this.requestData.cnd = [
//								'and',
//								['eq', ['$', 'KFXH'],
//										['i', this.mainApp['phis'].treasuryId]],
//								['eq', ['$', 'DJZT'], ['i', 0]]];
                        this.requestData.DJZT=0;
						this.refresh();
						return;
					} else if (this.statusRadio.getValue().inputValue == 1) {
//						var addCndDjzt = [
//								'and',
//								['eq', ['$', 'KFXH'],
//										['i', this.mainApp['phis'].treasuryId]],
//								['eq', ['$', 'DJZT'], ['i', 1]]];
//						var addCndDate = [
//								'and',
//								[
//										'ge',
//										['$', "str(SHRQ,'yyyy-mm-dd')"],
//										[
//												's',
//												this.dateFrom.getValue()
//														.format('Y-m-d')]],
//								[
//										'le',
//										['$', "str(SHRQ,'yyyy-mm-dd')"],
//										[
//												's',
//												this.dateTo.getValue()
//														.format('Y-m-d')]]]
//						this.requestData.cnd = ['and', addCndDjzt, addCndDate];
                        this.requestData.DJZT=1;
                        this.requestData.SHRQQ=this.dateFrom.getValue().format('Y-m-d');     
                        this.requestData.SHRQZ=this.dateTo.getValue().format('Y-m-d');
						this.refresh();
						return;
					}
				}
			},
			// 新增
			doAdd : function() {
				this.secondaryMaterialsOutDetailModule = this.oper.createModule(
						"secondaryMaterialsOutDetailModule", this.addRef);
				this.secondaryMaterialsOutDetailModule.on("save", this.onSave,
						this);
				this.secondaryMaterialsOutDetailModule.initPanel();
				var win = this.secondaryMaterialsOutDetailModule.getWin();
				win.add(this.secondaryMaterialsOutDetailModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.secondaryMaterialsOutDetailModule.op = "create";
					this.secondaryMaterialsOutDetailModule.doNew();
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.secondaryMaterialsOutDetailModule = this.oper.createModule(
						"secondaryMaterialsOutDetailModule", this.addRef);
				this.secondaryMaterialsOutDetailModule.on("save", this.onSave,
						this);
				var win = this.secondaryMaterialsOutDetailModule.getWin();
				win.add(this.secondaryMaterialsOutDetailModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.secondaryMaterialsOutDetailModule
							.changeButtonState("new");
				} else if (djzt == 1) {
					this.secondaryMaterialsOutDetailModule
							.changeButtonState("verified");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.secondaryMaterialsOutDetailModule.op = "update";
					this.secondaryMaterialsOutDetailModule.initDataBody = initDataBody;
					this.secondaryMaterialsOutDetailModule
							.loadData(initDataBody);
				}

			},
			// 冲红
			doReject : function() {
				this.secondaryMaterialsOutDetailModule = this.oper.createModule(
						"secondaryMaterialsOutDetailModule", this.addRef);
				this.secondaryMaterialsOutDetailModule.on("save", this.onSave,
						this);
				this.secondaryMaterialsOutDetailModule.initPanel();
				var win = this.secondaryMaterialsOutDetailModule.getWin();
				win.add(this.secondaryMaterialsOutDetailModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.secondaryMaterialsOutDetailModule.op = "create";
					this.secondaryMaterialsOutDetailModule.type = "reject";
					this.secondaryMaterialsOutDetailModule.doNew();
					// 改变流转方式
					this.secondaryMaterialsOutDetailModule.form.doIs("back");
				}
			},
			doCancel : function() {
				if (this.secondaryMaterialsOutDetailModule) {
					return this.secondaryMaterialsOutDetailModule.doClose();
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
									var body = {};
									body["DJXH"] = r.id;
									var r1 = phis.script.rmi.miniJsonRequestSync({
												serviceId : this.serviceId,
												serviceAction : "delete",
												method:"execute",
												body : body
											});
									if (r1.code > 300) {
										this.processReturnMsg(r1.code, r1.msg,
												this.onBeforeSave);
										return false;
									} else {
										// this.fireEvent("winClose", this);
										// this.fireEvent("save", this);
										this.refresh();
									}
								}
							},
							scope : this
						})
			},
			doPrint : function() {
				var module = this.createModule("noStoreListPrint",
						this.refNoStoreListPrint)
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
					return;
				}
				if (r.get("DJZT") == 0) {
					MyMessageTip.msg("提示", "打印失败：没有审核不能打印!", true);
					return;
				}
				module.DJXH = r.data.DJXH;
				module.initPanel();
				module.doPrint();
			},
			doDelete : function() {
				var record = this.getSelectedRecord()
				if (record == null) {
					return;
				}

				var body = [];
//                alert(record.data["DJLX"])
//console.debug(record)
				if (record.data["DJLX"] != 2) {
					Ext.Msg.alert("提示", "非消耗明细产生单据，不允许撤销");
					return;
				}
				if (record.data["DJZT"] == 1) {
					Ext.Msg.alert("提示", "单据状态不为制单的不允许撤销");
					return;
				}
				body.push(record.data.DJXH);

				Ext.Msg.show({
							title : '确认撤销记录',
							msg : '撤销操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {

									var r1 = phis.script.rmi.miniJsonRequestSync({
												serviceId : "consumptionService",
												serviceAction : "delete",
												method:"execute",
												body : body
											});
									if (r1.code > 300) {
										this.processReturnMsg(r1.code, r1.msg,
												this.onBeforeSave);
										return false;
									} else {
										// this.fireEvent("winClose", this);
										this.fireEvent("delete", this);
										this.refresh();
									}
								}
							},
							scope : this
						})
			}
		})