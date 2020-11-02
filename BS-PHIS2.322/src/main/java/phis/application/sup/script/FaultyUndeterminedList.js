$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.FaultyUndeterminedList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.FaultyUndeterminedList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.FaultyUndeterminedList,
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
					id : 'FaultyUndeterminedListdjzt',
					//name : 'FaultyUndeterminedListdjzt', // 后台返回的JSON格式，直接赋值
					value : "0",
					items : [{
								boxLabel : '制单',
								name : 'FaultyUndeterminedListdjzt',
								inputValue : 0
							}, {
								boxLabel : '审核',
								name : 'FaultyUndeterminedListdjzt',
								inputValue : 1
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
							text : "审核日期:"
						})
				this.dateFrom = new Ext.form.DateField({
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

				return ["<h1 style='text-align:center'>未记账报损单:</h1>", '-',
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
				this.faultyDetailModule = this.createModule(
						"faultyDetailModule", this.addRef);
					
				this.faultyDetailModule.on("save", this.onSave, this);
				this.faultyDetailModule
						.on("winClose", this.onClose, this);
				// 账簿类别
				this.faultyDetailModule.zblb = this.parentRadioGroup.value;
				this.faultyDetailModule.parentRadioGroup = this.parentRadioGroup;
				this.faultyDetailModule.DJXH=null;
				//this.faultyDetailModule.initPanel();
				var win = this.getWin();
				win.add(this.faultyDetailModule.initPanel());
				//this.faultyDetailModule.form.form.getForm().reset();
				win.show()
				win.center()
				this.faultyDetailModule.op = "create";
				if (!win.hidden) {
					this.faultyDetailModule.type = "normal";
					this.faultyDetailModule.doNew();
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["DJXH"] = r.data.DJXH;
				this.faultyDetailModule = this.createModule("faultyDetailModule", this.addRef);
				this.faultyDetailModule.zblb = this.zblb;
				this.faultyDetailModule.parentRadioGroup = this.parentRadioGroup;
				this.faultyDetailModule.DJXH=r.data.DJXH;
				this.faultyDetailModule.on("save", this.onSave, this);
				this.faultyDetailModule
						.on("winClose", this.onClose, this);
				this.faultyDetailModule.op="update";	
				var win = this.getWin();
				win.add(this.faultyDetailModule.initPanel());
				var djzt = r.data.DJZT;
				if (djzt == 0) {
					this.faultyDetailModule.changeButtonState("new");
				} else if (djzt == 1) {
					this.faultyDetailModule.changeButtonState("verified");
				}
				win.show()
				win.center()
				if (!win.hidden) {
					this.faultyDetailModule.op = "update";
					this.faultyDetailModule.initDataBody = initDataBody;
					this.faultyDetailModule.loadData(initDataBody);
				}

			},
			// 冲红
			doReject : function() {
				this.faultyDetailModule = this.createModule(
						"faultyDetailModule", this.addRef);
				this.faultyDetailModule.zblb = this.zblb;
				this.faultyDetailModule.on("save", this.onSave, this);
				this.faultyDetailModule
						.on("winClose", this.onClose, this);
				this.faultyDetailModule.initPanel();
				var win = this.getWin();
				win.add(this.faultyDetailModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.faultyDetailModule.op = "create";
					this.faultyDetailModule.type = "reject";
					this.faultyDetailModule.doNew();
					// 改变流转方式
					this.faultyDetailModule.form.doIs("back");
				}
			},
			onClose : function() {
				this.getWin().hide();
				this.refresh();
			},
			doCancel : function() {
				if (this.faultyDetailModule) {
					return this.faultyDetailModule.doClose();
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
			processRemove:function(r){
				var body={};
				body["DJXH"] = r.data.DJXH;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "delFaultyById",
							body : body
						});
				if (r.code > 300) {
					return false;
				}else{
					this.doRefreshWin();
				}
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title||this.name,
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
            doPrint : function() {
                var module = this.createModule("faultyManagerPrint",
                        this.refFaultyManagerPrint)
                var r = this.getSelectedRecord()
                if (r == null) {
                    MyMessageTip.msg("提示", "打印失败：无效的盘点单信息!", true);
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