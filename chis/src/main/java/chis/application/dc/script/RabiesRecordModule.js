$package("chis.application.dc.script");

$import("chis.script.BizModule", "chis.application.dc.script.RabiesRecordList",
		"chis.application.dc.script.RabiesTabModule");

chis.application.dc.script.RabiesRecordModule = function(cfg) {
	this.autoLoadData = false;
	this.readOnly = false;
	cfg.autoLoadSchema= true;
	chis.application.dc.script.RabiesRecordModule.superclass.constructor.apply(this, [cfg]);
};
Ext.extend(chis.application.dc.script.RabiesRecordModule, chis.script.BizModule, {
	initPanel : function() {
		var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					width : this.width,
					height : this.height,
					items : [{
								layout : "fit",
								split : true,
								collapsible : true,
								title : '',
								region : 'west',
								width : this.westWidth || 210,
								items : this.getGrid()
							}, {
								layout : "fit",
								split : true,
								title : '',
								region : 'center',
								width : 400,
								height : this.height,
								items : this.getMainPanel()
							}]
				});
		this.panel = panel;
		return panel;
	},
	getGrid : function() {
		this.list = this.midiModules["DC_RabiesRecordList"];
		if (!this.list) {
			var config = this.loadModuleCfg(this.actions[0].ref);
			if (!config) {
				Ext.Msg.alert("错误", "狂犬病模块加载失败！");
				return;
			}
			var cfg = {
				autoLoadSchema : false,
				isCombined : true,
				exContext : this.exContext,
				entryName : 'chis.application.dc.schemas.DC_RabiesRecord_list',
				initCnd : ['eq', ['$', 'a.empiId'],
						['s', this.exContext.ids.empiId]],
				listServiceId : "chis.rabiesRecordService",
				listAction : "getRabiesRecordList"
			};
			this.list = eval("new " + config.script + "(cfg)");
			this.midiModules["DC_RabiesRecordList"] = this.list;
		}
		this.grid = this.list.initPanel();
		this.grid.on("rowclick", this.onRowClick, this);
		this.list.on("loadData", this.onLoadData, this);
		this.list.on("noRecord", this.onNoRecord, this);
		return this.grid;
	},

	getMainPanel : function() {
		var mainPanel = new Ext.Panel({
					border : false,
					frame : false,
					layout : 'fit'
				});
		this.mainPanel = mainPanel;
		this.openFormModule();
		return mainPanel;
	},

	openFormModule : function() {
		this.tabM = this.midiModules["RabiesTabModule"];
		if (!this.tabM) {
			var config = this.loadModuleCfg(this.actions[1].ref);
			if (!config) {
				Ext.Msg.alert("错误", "狂犬病模块加载失败！");
			}
			var cfg = {
				actions : config.actions,
				autoLoadSchema : true,
				isCombined : true,
				autoLoadData : false,
				entryName : this.entryName,
				schema : this.entryName,
				mainApp : this.mainApp,
				serviceId : this.serviceId,
				exContext : this.exContext,
				labelWidth : 130
			};
			this.tabM = eval("new " + config.script + "(cfg)");
			this.midiModules["RabiesTabModule"] = this.tabM;
		}
		this.tabM.on("save", this.onFormSave, this);
		this.tabM.on("close", this.onClose, this);
		this.tabM.on("add", this.onAdd, this);
		var p = this.tabM.initPanel();
		this.mainPanel.add(p);
		this.tabM.opener = this;
	},

	onNoRecord : function() {
		this.getControl();
		this.setButton(1, "", "");
		this.tabM.doNew();
	},

	getControl : function() {
		var empiId = this.exContext.ids.empiId;
		var result = util.rmi.miniJsonRequestSync({
					serviceId : 'chis.rabiesRecordService',
					serviceAction : "loadControl",
					method:"execute",
					empiId : empiId
				});
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return;
		}
		if (result.json.body) {
			this.exContext.control = result.json.body;
			if (result.json.body.update == false
					&& result.json.body.create == false) {
				this.readOnly = true;
			}else{
				this.readOnly = false;
			}
		}
	},
	onRowClick : function(grid, index, e) {
		this.getControl();	
		var r = this.grid.getSelectionModel().getSelected();
		if (!r) {
			return;
		}
		var rabiesId = r.get("rabiesId");
		var status = r.get("status");
		var closeFlag = r.get("closeFlag");
		var cancellationReason=r.get("cancellationReason");
		this.exContext.args.rabiesId = rabiesId;
		this.readOnly = false;
		this.hasNotLogOut = null;

		if (this.exContext.control.update == false && this.exContext.control.create == false) {
			this.readOnly = true;
		} else {
			if (closeFlag != "0" || status != "0") {
				this.readOnly = true;
				this.checkRecord();
			}
			if (this.hasNotLogOut == false) {
				this.readOnly = false;
				if(cancellationReason!="6"){
					this.readOnly=true;
				}
			}else{
				if(closeFlag != "0"){
					this.readOnly = false
				}
			}
		}
		this.setButton(2, status, closeFlag);
		if (this.initDataId == rabiesId) {
			return;
		}
		if (rabiesId && rabiesId != "") {
			this.initDataId = rabiesId;
			this.list.selectLast = false;
			this.tabM.exContext.args.rabiesId = rabiesId;
			this.tabM.loadData();
		} else {
			this.tabM.loadInitData();//
			this.list.selectLast = true;
		}
	},
	checkRecord : function() {
		var empiId = this.exContext.ids.empiId;
		var result = util.rmi.miniJsonRequestSync({
					serviceId : 'chis.rabiesRecordService',
					serviceAction : "checkHasNotLogOut",
					method:"execute",
					empiId : empiId
				});
		if (result.code > 300) {
			this.processReturnMsg(result.code, result.msg);
			return;
		}
		if (result.json.body) {
			var hasNotLogOut = result.json.body.hasNotLogOut;
			this.hasNotLogOut = hasNotLogOut;
		}
	},

	onClose : function(rabiesId) {
		this.exContext.args.rabiesId = rabiesId;
		this.setButton(2, "", "");
		this.list.refresh();
	},
	onFormSave : function(entryName, op, json, data) {
		this.setButton(2, "0", "0");
		this.exContext.args.rabiesId = data.rabiesId;
		this.fireEvent("save", entryName, op, json, data);
		this.list.refresh();
	},

	onDelete : function() {
		this.list.refresh();
	},

	onAdd : function() {
		var r = this.list.store.getAt(this.list.store.getCount() - 1);
		if (r && r.get("discoverDate") == null) {
			return;
		}
		this.setButton(1, "", "");
		this.initDataId = null;
		var json = this.tabM.loadInitData();
		this.list.doAdd(json);
	},

	loadData : function() {
		this.list.exContext.ids.empiId = this.exContext.ids.empiId;
		this.tabM.exContext.ids.empiId = this.exContext.ids.empiId;
		this.list.exContext.ids.phrId = this.exContext.ids.phrId;
		this.tabM.exContext.ids.phrId = this.exContext.ids.phrId;
		this.tabM.exContext.args.rabiesId = this.exContext.args.rabiesId;
		this.cnd = ['eq', ['$', 'a.empiId'], ['s', this.exContext.ids.empiId]];
		if (this.list) {
			this.list.requestData.cnd = this.cnd;
			this.list.selectLast = true;
			this.list.loadData();
		}
	},

	setButton : function(type, status, flag) {
		if(!this.tabM.midiModules["RabiesForm"].form.getTopToolbar()){
			return;
		}
		var bts = this.tabM.midiModules["RabiesForm"].form.getTopToolbar().items;
		if (bts.items.length == 0) {
			return;
		}
		var lbts = this.tabM.midiModules["VaccinationList"].grid
				.getTopToolbar().items;
		if (lbts.items.length == 0) {
			return;
		}
		bts.items[0].disable();
		bts.items[1].disable();
		bts.items[2].disable();
		lbts.items[0].disable();
		lbts.items[1].disable();
		lbts.items[2].disable();
		if (!this.readOnly) {
			if (type == 1) {
				if (bts && lbts) {
					bts.items[0].enable();
					bts.items[2].enable();
				}
			} else if (type == 2) {
				if (bts && lbts) {
					if (status == "0" && flag == "0") {
						bts.items[0].enable();
						bts.items[1].enable();
						lbts.items[0].enable();
						lbts.items[1].enable();
						lbts.items[2].enable();
					} else {
						bts.items[2].enable();
					}
				}
			}
		}
	},
	onLoadData : function(store) {
		var sm = this.list.grid.getSelectionModel();
		var rowid = -1;
		if (this.exContext.args.rabiesId && this.exContext.args.rabiesId != "") {
			rowid = store.find('rabiesId', this.exContext.args.rabiesId);
		}
		if (rowid != -1) {
			sm.selectRow(rowid);
		} else {
			sm.selectLastRow();
		}
		this.list.grid.fireEvent("rowclick", this.grid);
	},

	getWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						items : this.initPanel()
					});
			win.on("show", function() {
						this.fireEvent("winShow");
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this);
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.win = win;
		}
		win.instance = this;
		return win;
	}
});