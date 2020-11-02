
$package("phis.application.sup.script")
$import("phis.script.SelectList")

phis.application.sup.script.ConsumptionList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.winState = "center";
	cfg.selectOnFocus = true;
	cfg.showBtnOnLevel = true;
	cfg.mutiSelect = true;
	phis.application.sup.script.ConsumptionList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.ConsumptionList, phis.script.SelectList, {
	onReady : function() {
		this.requestData.cnd = [
				'and',
				[
						'and',
						['eq', ['$', 'a.KFXH'], ['i', this.mainApp['phis'].treasuryId]],
						['eq', ['$', 'a.ZTBZ'], ['i', 0]]],
				['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
		phis.application.sup.script.ConsumptionList.superclass.onReady.call(this);
	},
	initPanel : function(sc) {
		if (this.mainApp['phis'].treasuryId == null || this.mainApp['phis'].treasuryId == ""
				|| this.mainApp['phis'].treasuryId == undefined) {
			Ext.Msg.alert("提示", "未设置登录库房,请先设置");
			return null;
		}
		if (this.mainApp['phis'].treasuryEjkf == 0) {
			Ext.MessageBox.alert("提示", "该库房不是二级库房!");
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
			// --plugins : this.array.length == 0 ? null : this.array,// modife
			enableHdMenu : this.enableHdMenu,
			viewConfig : {
				enableRowBody : this.enableRowBody,
				getRowClass : this.getRowClass
			}
		}
		if (this.group)
			cfg.view = new Ext.grid.GroupingView({
						// forceFit : true,
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
			        text : "生成方式: ",
					width : 100
				})
		this.statusRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 90,
					id : 'ConsumptionListdjzt',
					//name : 'ConsumptionListdjzt', ֵ
					value : "2",
					items : [
//						{
//							boxLabel : '按病人生成',
//							name : 'ConsumptionListdjzt',
//							inputValue : 0,
//							disabled : true
//						}, {
//							boxLabel : '按汇总生成',
//							name : 'ConsumptionListdjzt',
//							inputValue : 1,
//							disabled : true
//						}, 
							{
							boxLabel : '按科室生成',
							name : 'ConsumptionListdjzt',
							inputValue : 2
						}]
				});
		return [filelable, this.statusRadio, '-'];
	},
	doCreateDoc : function() {
		var records = this.getSelectedRecords()
		if (records == null) {
			return;
		}

		var sclx = -1;
		if (this.statusRadio.getValue() != null) {
			sclx = this.statusRadio.getValue().inputValue;
		}
		var body = {};
		body["records"] = [];
		Ext.each(records, function() {
					body["records"].push(this.data);
				});
		body["sclx"] = sclx;
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "createDocument",
					body : body
				});
		if (r.code > 300) {
			this.clearSelect();
			this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			return false;
		} else {
			if (r.json.WZMC) {
				MyMessageTip.msg("提示", "物资" + r.json.WZMC + "库存不足,不能审核!", true);
			}
			this.clearSelect();
			this.refresh();
		}

	},
	doCommit : function() {
		this.clearSelect();

		this.consumptionConfirmModule = this.createModule(
				"consumptionConfirmModule", this.addRef);
		this.consumptionConfirmModule.on("save", this.onSave, this);
		this.consumptionConfirmModule.on("winClose", this.onClose, this);
		var win = this.getWin();
		win.add(this.consumptionConfirmModule.initPanel());
		this.consumptionConfirmModule.uCheckOutList.on("delete", this.onDelete,
				this);
		win.show()
		win.center()
		this.consumptionConfirmModule.uCheckOutList.doRefreshWin();
		this.consumptionConfirmModule.checkOutList.doRefreshWin();
	},
	doCancellation : function() {
		var records = this.getSelectedRecords()
		if (records.length == 0) {
			return;
		}

		var body = {};
		body["records"] = [];
		Ext.each(records, function() {
					body["records"].push(this.data);
				});
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "cancellation",
					body : body
				});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
			this.clearSelect();
			return false;
		} else {
			MyMessageTip.msg("提示", "作废成功", true);
			this.clearSelect();
			this.refresh();
		}
	},
	onClose : function() {
		this.getWin().hide();
		this.refresh();
	},
	doCancel : function() {
		if (this.consumptionConfirmModule) {
			return this.consumptionConfirmModule.doClose();
		}
	},
	onSave : function() {
		this.fireEvent("save", this);
	},
	onDelete : function() {
		this.refresh();
		this.consumptionConfirmModule.uCheckOutList.doRefreshWin();
		this.consumptionConfirmModule.checkOutList.doRefreshWin();
	},
	onDblClick : function(grid, index, e) {
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
				id : this.id,
				title : this.title,
				width : this.width,
				height : this.height,
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
	}
})