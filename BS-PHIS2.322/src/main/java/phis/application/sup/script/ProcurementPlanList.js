/**
 * �ɹ��ƻ�
 * 
 * @author gaof
 */
$package("phis.application.sup.script")
$import("phis.script.SimpleList")

phis.application.sup.script.ProcurementPlanList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.ProcurementPlanList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sup.script.ProcurementPlanList, phis.script.SimpleList, {
	onReady : function() {
		this.requestData.cnd = ['and',
				['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
				['eq', ['$', 'DJZT'], ['i', 0]]];
		phis.application.sup.script.ProcurementPlanList.superclass.onReady.call(this);
	},
	initPanel : function(sc) {
		if (this.mainApp['phis'].treasuryId == null || this.mainApp['phis'].treasuryId == ""
				|| this.mainApp['phis'].treasuryId == undefined) {
			Ext.Msg.alert("提示", "未设置登录库房,请先设置");
			return null;
		}
		if (this.mainApp['phis'].treasuryEjkf != 0) {
			Ext.MessageBox.alert("提示", "该库房不是一级库房!");
			return;
		}
		if (this.mainApp['phis'].treasuryCsbz != 1) {
			Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
			return;
		}
		if (this.grid) {
			if (!this.isCombined) {
				this.fireEvent("beforeAddToWin", this.grid)
				this.addPanelToWin();
			}
			return this.grid;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		this.isCompositeKey = schema.isCompositeKey;
		var items = schema.items
		if (!items) {
			return;
		}

		this.store = this.getStore(items)
		if (this.mutiSelect) {
			this.sm = new Ext.grid.CheckboxSelectionModel()
		}
		this.cm = new Ext.grid.ColumnModel(this.getCM(items))
		var cfg = {
			stripeRows : true,
			border : false,
			store : this.store,
			cm : this.cm,
			sm : this.sm,
			height : this.height,
			loadMask : {
				msg : '正在加载数据...',
				msgCls : 'x-mask-loading'
			},
			buttonAlign : 'center',
			clicksToEdit : true,
			frame : true,
			plugins : this.rowExpander,
			enableHdMenu : this.enableHdMenu,
			viewConfig : {
				enableRowBody : this.enableRowBody,
				getRowClass : this.getRowClass
			}
		}
		if (this.group)
			cfg.view = new Ext.grid.GroupingView({
						showGroupName : true,
						enableNoGroups : false,
						hideGroupedColumn : true,
						enableGroupingMenu : false,
						columnsText : "表格字段",
						groupByText : "使用当前字段进行分组",
						showGroupsText : "表格分组",
						groupTextTpl : this.groupTextTpl
					});
		if (this.gridDDGroup) {
			cfg.ddGroup = this.gridDDGroup;
			cfg.enableDragDrop = true
		}
		if (this.summaryable) {
			$import("org.ext.ux.grid.GridSummary");
			var summary = new org.ext.ux.grid.GridSummary();
			cfg.plugins = [summary]
			this.summary = summary;
		}
		var cndbars = this.getCndBar(items)
		if (!this.disablePagingTbr) {
			cfg.bbar = this.getPagingToolbar(this.store)
		} else {
			cfg.bbar = this.bbar
		}
		if (!this.showButtonOnPT) {
			if (this.showButtonOnTop) {
				cfg.tbar = (cndbars.concat(this.tbar || [])).concat(this
						.createButtons())
			} else {
				cfg.tbar = cndbars.concat(this.tbar || [])
				cfg.buttons = this.createButtons()
			}
		}

		if (this.disableBar) {
			delete cfg.tbar;
			delete cfg.bbar;
			cfg.autoHeight = true;
			cfg.frame = false;
		}
		this.expansion(cfg);// add by yangl
		this.grid = new this.gridCreator(cfg)
		this.schema = schema;
		this.grid.on("afterrender", this.onReady, this)
		this.grid.on("contextmenu", function(e) {
					e.stopEvent()
				})
		this.grid.on("rowcontextmenu", this.onContextMenu, this)
		this.grid.on("rowdblclick", this.onDblClick, this)
		this.grid.on("rowclick", this.onRowClick, this)
		this.grid.on("keydown", function(e) {
					if (e.getKey() == e.PAGEDOWN) {
						e.stopEvent()
						this.pagingToolbar.nextPage()
						return
					}
					if (e.getKey() == e.PAGEUP) {
						e.stopEvent()
						this.pagingToolbar.prevPage()
						return
					}
					if (e.getKey() == e.ESC) {
						if (this.onESCKey) {
							this.onESCKey();
						}
						return
					}
				}, this)
		if (!this.isCombined) {
			this.fireEvent("beforeAddToWin", this.grid)
			this.addPanelToWin();
		}
		return this.grid
	},
	getCndBar : function(items) {
		var filelable = new Ext.form.Label({
			   text : "单据状态:"
				})
		this.statusRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 100,
					id : 'ProcurementPlanListdjzt',
					//name : 'ProcurementPlanListdjzt', ֵ
					value : "0",
					items : [{
						        boxLabel : '制单',
								name : 'ProcurementPlanListdjzt',
								inputValue : 0
							}, {
								boxLabel : '审核',
								name : 'ProcurementPlanListdjzt',
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
		var dateFromValue = dat.substring(0, dat.lastIndexOf("-")) + "-01";
		var datelable = new Ext.form.Label({
			    text : "审核日期:"
				})
		this.dateFrom = new Ext.form.DateField({
					id : 'ProcurementPlanListdateFrom',
					name : 'ProcurementPlanListdateFrom',
					value : dateFromValue,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				});
		var tolable = new Ext.form.Label({
			        ext : " 到 "
				});
		this.dateTo = new Ext.form.DateField({
					id : 'ProcurementPlanListdateTo',
					name : 'ProcurementPlanListdateTo',
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

		return [filelable, this.statusRadio, '-', datelable, this.dateFrom,
				tolable, this.dateTo, '-'];
	},
	doRefreshWin : function() {
		if (this.statusRadio.getValue() != null) {
			if (this.statusRadio.getValue().inputValue == 0) {

				this.requestData.cnd = ['and',
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
						['eq', ['$', 'DJZT'], ['i', 0]]];
				this.refresh();
				return;
			} else if (this.statusRadio.getValue().inputValue == 1) {
				var addCndDjzt = ['and',
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]],
						['eq', ['$', 'DJZT'], ['i', 1]]];
				var addCndDate = [
						'and',
						['ge', ['$', "str(SHRQ,'yyyy-mm-dd')"],
								['s', this.dateFrom.getValue().format('Y-m-d')]],
						['le', ['$', "str(SHRQ,'yyyy-mm-dd')"],
								['s', this.dateTo.getValue().format('Y-m-d')]]]
				this.requestData.cnd = ['and', addCndDjzt, addCndDate];
				this.refresh();
				return;
			}
		}
	},
	doAdd : function() {
		this.procurementPlanDetailModule = this.createModule(
				"procurementPlanDetailModule", this.addRef);
		this.procurementPlanDetailModule.on("save", this.onSave, this);
		this.procurementPlanDetailModule.on("winClose", this.onClose, this);
		var win = this.getWin();
		win.add(this.procurementPlanDetailModule.initPanel());
		win.show()
		win.center()
		if (!win.hidden) {
			this.procurementPlanDetailModule.op = "create";
			this.procurementPlanDetailModule.doNew();
		}
	},
	doUpd : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return;
		}
		var initDataBody = {};
		initDataBody["DJXH"] = r.data.DJXH;
		this.procurementPlanDetailModule = this.createModule(
				"procurementPlanDetailModule", this.addRef);
		this.procurementPlanDetailModule.on("save", this.onSave, this);
		this.procurementPlanDetailModule.on("winClose", this.onClose, this);
		var win = this.getWin();
		win.add(this.procurementPlanDetailModule.initPanel());
		var djzt = r.data.DJZT;
		if (djzt == 0) {
			this.procurementPlanDetailModule.changeButtonState("new");
		} else if (djzt == 1) {
			this.procurementPlanDetailModule.changeButtonState("verified");
		}
		win.show()
		win.center()
		if (!win.hidden) {
			this.procurementPlanDetailModule.op = "update";
			this.procurementPlanDetailModule.initDataBody = initDataBody;
			this.procurementPlanDetailModule.loadData(initDataBody);
		}

	},
	onClose : function() {
		this.getWin().hide();
		this.refresh();
	},
	doCancel : function() {
		if (this.procurementPlanDetailModule) {
			return this.procurementPlanDetailModule.doClose();
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
	doRemove : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return
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
										body : body,
										op : this.op
									});
							if (r1.code > 300) {
								this.processReturnMsg(r1.code, r1.msg,
										this.onBeforeSave);
								return false;
							} else {
								this.refresh();
							}
						}
					},
					scope : this
				})
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
			win.on("hide", function() {
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		return win;
	}
})