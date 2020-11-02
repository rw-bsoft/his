/**
 * 转科管理
 * 
 * @author gaof
 */
$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.TransferManagementUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	// cfg.autoLoadData = false;
	phis.application.sup.script.TransferManagementUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.TransferManagementUndeterminedList,
		phis.script.SimpleList, {
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
					id : 'TransferManagementUndeterminedListdjzt',
					name : 'TransferManagementUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
					value : "0",
					items : [{
								boxLabel : '制单',
								name : 'TransferManagementUndeterminedListdjzt',
								inputValue : 0
							}, {
								boxLabel : '审核',
								name : 'TransferManagementUndeterminedListdjzt',
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
							id : 'TransferManagementUndeterminedListdateFrom',
							name : 'TransferManagementUndeterminedListdateFrom',
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
							id : 'TransferManagementUndeterminedListdateTo',
							name : 'TransferManagementUndeterminedListdateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});

				datelable.setDisabled(true);
				this.dateFrom.setDisabled(true);
				tolable.setDisabled(true);
				this.dateTo.setDisabled(true);
				return ["<h1 style='text-align:center'>未记账转科单:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.statusRadio.getValue() != null) {
					if (this.statusRadio.getValue().inputValue == 0) {
						this.requestData.cnd = [
								'and',
								['eq', ['$', 'ZBLB'], ['i', this.zblb]],
								[
										'and',
										['eq', ['$', 'KFXH'],
												['i', this.mainApp['phis'].treasuryId]],
										['eq', ['$', 'DJZT'], ['i', 0]]]];
						this.refresh();
						return;
					} else if (this.statusRadio.getValue().inputValue == 1) {
						var addCndDjzt = [
								'and',
								['eq', ['$', 'ZBLB'], ['i', this.zblb]],
								[
										'and',
										['eq', ['$', 'KFXH'],
												['i', this.mainApp['phis'].treasuryId]],
										['eq', ['$', 'DJZT'], ['i', 1]]]];
						var addCndDate = [
								'and',
								[
										'ge',
										['$', "str(SHRQ,'yyyy-mm-dd')"],
										[
												's',
												this.dateFrom.getValue()
														.format('Y-m-d')]],
								[
										'le',
										['$', "str(SHRQ,'yyyy-mm-dd')"],
										[
												's',
												this.dateTo.getValue()
														.format('Y-m-d')]]]
						var addCnd = ['and', addCndDjzt, addCndDate];
						this.requestData.cnd = addCnd;
						this.refresh();
						return;
					}
				}
			},
			// 新增
			doAdd : function() {
				this.transferManagementDetailModule = this.createModule(
						"transferManagementDetailModule", this.addRef);
				this.transferManagementDetailModule.on("save", this.onSave,
						this);
				this.transferManagementDetailModule.on("winClose",
						this.onClose, this);
				// 账簿类别
				this.transferManagementDetailModule.zblb = this.zblb;
				// this.transferManagementDetailModule.initPanel();
				var win = this.getWin();
				win.add(this.transferManagementDetailModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.transferManagementDetailModule.op = "create";
					this.transferManagementDetailModule.doNew();
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.transferManagementDetailModule = this.createModule(
						"transferManagementDetailModule", this.addRef);
				// 账簿类别
				this.transferManagementDetailModule.zblb = this.zblb;
				this.transferManagementDetailModule.on("save", this.onSave,
						this);
				this.transferManagementDetailModule.on("winClose",
						this.onClose, this);
				var win = this.getWin();
				win.add(this.transferManagementDetailModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.transferManagementDetailModule
							.changeButtonState("new");
				} else if (djzt == 1) {
					this.transferManagementDetailModule
							.changeButtonState("verified");
				}
				// else if (djzt == 2) {
				// this.transferManagementDetailModule.changeButtonState("commited");
				// }
				win.show()
				win.center()
				if (!win.hidden) {
					this.transferManagementDetailModule.op = "update";
					this.transferManagementDetailModule.initDataBody = initDataBody;
					this.transferManagementDetailModule.loadData(initDataBody);
				}

			},
			// 冲红
			doReject : function() {
				this.transferManagementDetailModule = this.createModule(
						"transferManagementDetailModule", this.addRef);
				// 账簿类别
				this.transferManagementDetailModule.zblb = this.zblb;
				this.transferManagementDetailModule.on("save", this.onSave,
						this);
				this.transferManagementDetailModule.on("winClose",
						this.onClose, this);
				// this.transferManagementDetailModule.checkInWayValue =
				// this.checkInWayValue;
				this.transferManagementDetailModule.initPanel();
				var win = this.getWin();
				win.add(this.transferManagementDetailModule.initPanel());
				win.show()
				win.center()
				// this.transferManagementDetailModule.list.store.removeAll();
				if (!win.hidden) {
					this.transferManagementDetailModule.op = "create";
					this.transferManagementDetailModule.type = "reject";
					this.transferManagementDetailModule.doNew();
					// 改变流转方式
					this.transferManagementDetailModule.form.doIs("back");
				}
			},
			onClose : function() {
				this.getWin().hide();
				this.refresh();
			},
			doCancel : function() {
				if (this.transferManagementDetailModule) {
					return this.transferManagementDetailModule.doClose();
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
									var body={};
                                    body["DJXH"]=r.id;
                                    var r1 = phis.script.rmi.miniJsonRequestSync({
                                                serviceId : this.serviceId,
                                                serviceAction : "delete",
                    							method:"execute",
                                                body : body,
                                                op : this.op
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
            	var r = this.getSelectedRecord();
            	if (r == null) {
            		MyMessageTip.msg("提示", "打印失败：无效的转科单信息!", true);
            		return;
            	}
				var module = this.createModule("storeOfTransferPrint",
						this.refStoreOfTransferPrint)
                module.djxh=r.data.DJXH;
				module.zblb=this.zblb;
				module.initPanel();
				module.doPrint();
			}
		})