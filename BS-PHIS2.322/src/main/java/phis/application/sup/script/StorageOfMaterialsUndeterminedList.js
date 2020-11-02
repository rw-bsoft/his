$package("phis.application.sup.script");
$import("phis.script.SimpleList");
 
phis.application.sup.script.StorageOfMaterialsUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.StorageOfMaterialsUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.StorageOfMaterialsUndeterminedList,
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
					id : 'storageOfMaterialsUndeterminedListdjzt',
					name : 'storageOfMaterialsUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
					value : "0",
					items : [{
								boxLabel : '制单',
								name : 'storageOfMaterialsUndeterminedListdjzt',
								inputValue : 0
							}, {
								boxLabel : '审核',
								name : 'storageOfMaterialsUndeterminedListdjzt',
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
							id : 'storageOfMaterialsUndeterminedListdateFrom',
							name : 'storageOfMaterialsUndeterminedListdateFrom',
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
							id : 'storageOfMaterialsUndeterminedListdateTo',
							name : 'storageOfMaterialsUndeterminedListdateTo',
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
		
				return ["<h1 style='text-align:center'>未记账入库单:</h1>", '-',
						filelable, this.statusRadio, '-', datelable,
						this.dateFrom, tolable, this.dateTo, '-'];
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.statusRadio.getValue() != null) {
					if (this.statusRadio.getValue().inputValue == 0) {
						this.requestData.cnd = [
								'and',
								[
										'and',
										['eq', ['$', 'ZBLB'], ['i', this.zblb]],
										['eq', ['$', 'KFXH'],
												['i', this.mainApp['phis'].treasuryId]]],
								[
										'eq',
										['$', 'DJZT'],
										[
												'i',
												this.statusRadio.getValue().inputValue]]];
						this.refresh();
						return;
					} else if (this.statusRadio.getValue().inputValue == 1) {
						var addCndDjzt = [
								'and',
								[
										'and',
										['eq', ['$', 'ZBLB'], ['i', this.zblb]],
										['eq', ['$', 'KFXH'],
												['i', this.mainApp['phis'].treasuryId]]],
								[
										'eq',
										['$', 'DJZT'],
										[
												'i',
												this.statusRadio.getValue().inputValue]]];
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
						this.requestData.cnd = ['and', addCnd, this.cnds];
						this.refresh();
						return;
					}
				}
			},
			// 新增
			doAdd : function() {
				this.storageOfMaterialsModule = this.oper.createModule(
						"storageOfMaterialsModule", this.refAdd);
				this.storageOfMaterialsModule.on("save", this.onSave, this);
				// 账簿类别
				this.storageOfMaterialsModule.zblb = this.zblb;
				var win = this.storageOfMaterialsModule.getWin();
				win.add(this.storageOfMaterialsModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.storageOfMaterialsModule.op = "create";
					this.storageOfMaterialsModule.type = "normal";
					this.storageOfMaterialsModule.doNew();
					this.storageOfMaterialsModule.list.remoteDic.lastQuery = "";
					this.storageOfMaterialsModule.list.remoteDicStore.baseParams = {
							"type" : "",
							"zblb" : this.zblb
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
				this.storageOfMaterialsModule = this.oper.createModule(
						"storageOfMaterialsModule", this.refAdd);
				this.storageOfMaterialsModule.zblb = this.zblb;
				this.storageOfMaterialsModule.DWMC = r.data.DWXH_text;
				this.storageOfMaterialsModule.DWXH = r.data.DWXH;
				this.storageOfMaterialsModule.on("save", this.onSave, this);
				var win = this.storageOfMaterialsModule.getWin();
				win.add(this.storageOfMaterialsModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageOfMaterialsModule.op = "update";
					this.storageOfMaterialsModule.initDataBody = initDataBody;
					this.storageOfMaterialsModule.loadData(initDataBody);
					var djzt = r.data.DJZT;
					if (djzt == 0) {
						this.storageOfMaterialsModule.changeButtonState("new");
					} else if (djzt == 1) {
						this.storageOfMaterialsModule
								.changeButtonState("verified");
					}
					if(r.data.THDJ==-1){
						this.storageOfMaterialsModule.list.remoteDic.lastQuery = "";
						this.storageOfMaterialsModule.list.remoteDicStore.baseParams = {
								"type" : "reject",
								"zblb" : this.zblb
							}
					}else{
						this.storageOfMaterialsModule.list.remoteDic.lastQuery = "";
						this.storageOfMaterialsModule.list.remoteDicStore.baseParams = {
								"type" : "",
								"zblb" : this.zblb
							}
					}
				}

			},
			// 冲红
			doReject : function() {
				this.storageOfMaterialsModule = this.oper.createModule(
						"storageOfMaterialsModule", this.refAdd);
				this.storageOfMaterialsModule.zblb = this.zblb;
				this.storageOfMaterialsModule.op = "create";
				this.storageOfMaterialsModule.type = "reject";
				this.storageOfMaterialsModule.on("save", this.onSave, this);
				var win = this.storageOfMaterialsModule.getWin();
				win.add(this.storageOfMaterialsModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageOfMaterialsModule.form.op = "create";
					this.storageOfMaterialsModule.form.doNew();

					this.storageOfMaterialsModule.list.op = "create";
					this.storageOfMaterialsModule.list.th = 1;
					this.storageOfMaterialsModule.list.clear();
					this.storageOfMaterialsModule.list.editRecords = [];
					this.storageOfMaterialsModule.list.doCreate();
					this.storageOfMaterialsModule.changeButtonState("blank");
					// 改变流转方式
					this.storageOfMaterialsModule.form.doIs("back");
					this.storageOfMaterialsModule.list.remoteDic.lastQuery = "";
					this.storageOfMaterialsModule.list.remoteDicStore.baseParams = {
						"type" : "reject",
						"zblb" : this.zblb
					}
					// 增加一列可退数量
					this.storageOfMaterialsModule.list.grid.getColumnModel()
							.setHidden(
									this.storageOfMaterialsModule.list.grid
											.getColumnModel()
											.getIndexById("KTSL"), false);
				}
			},
			doCancel : function() {
				if (this.storageOfMaterialsModule) {
					return this.storageOfMaterialsModule.doClose();
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
									this.processRemove(r);
								}
							},
							scope : this
						})
			},
            doPrint : function() {
            	var r = this.getSelectedRecord();
            	if (r == null) {
            		MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
            		return;
            	}
            	if(r.get("DJZT")==0){
            		MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印!", true);
            		return;
            	}
                var module = this.createModule("storeOfMaterialsPrint",this.refStoreOfMaterialsPrint)
// if(r.data.DJZT==0){
// MyMessageTip.msg("提示", "打印失败：未审核的单据不能打印", true);
// return;
// }
                module.djxh=r.data.DJXH;
                module.initPanel();
                module.doPrint();
            }
		})