/**
 * 卡管理列表
 * 
 * @author tianj
 */
$package("chis.application.mpi.script");

$import("chis.script.BizSimpleListView", "chis.application.mpi.script.CardForm");

chis.application.mpi.script.CardList = function(cfg) {
	cfg.title = "卡管理"
	cfg.entryName = "chis.application.mpi.schemas.MPI_Card"
	cfg.width = 800;
	cfg.height = 500
	cfg.lockCardServiceAction = "lockCard"
	cfg.unLockCardServiceAction = "unlockCard"
	cfg.writeOffCardServiceAction = "writeOffCard"
	cfg.modal = cfg.modal;
	cfg.pixServiceId = "chis.empiService"
	cfg.showButtonOnTop = true
	cfg.selectFirst = false;
	cfg.createCls = "chis.application.mpi.script.CardForm"
	cfg.actions = [{
				name : "新建",
				id : "new"
			}, {
				name : "挂失",
				id : "lock",
				iconCls : "empi_lock"
			}, {
				name : "解挂",
				id : "unLock",
				iconCls : "empi_unlock"
			}, {
				name : "注销",
				id : "writeOff",
				iconCls : "common_writeOff"
			}, {
				name : "关闭",
				id : "cancel",
				iconCls : "common_cancel"
			}]

	chis.application.mpi.script.CardList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.mpi.script.CardList, chis.script.BizSimpleListView, {
	doCancel : function() {
		this.close();
	},
	
	close : function() {
		this.getWin().hide();
	},
	
	doLock : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var status = r.get("status")
		if (status != 1) {
			Ext.Msg.alert("提示！","该卡不处于正常使用状态！");
			return;
		}

		Ext.Msg.show({
					title : '确认挂失卡[' + r.id + ']',
					msg : '卡挂失后将被置为不可用状态，是否继续?',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processAction(this.lockCardServiceAction, r);
						}
					},
					scope : this
				});
	},
	
	doUnLock : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var status = r.get("status");
		if (status == 1) {
			Ext.Msg.alert("提示！","该卡处正常使用状态！");
			return;
		}
		Ext.Msg.show({
					title : '确认卡解挂[' + r.id + ']',
					msg : '卡解除挂失后将被置为可用状态，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processAction(this.unLockCardServiceAction, r);
						}
					},
					scope : this
				});
	},
	
	doWriteOff : function() {
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}

		Ext.Msg.show({
					title : '确认注销[' + r.id + ']',
					msg : '卡注销后将置为不可用状态且无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.processAction(this.writeOffCardServiceAction,
									r);
						}
					},
					scope : this
				});
	},

	processAction : function(action, r) {
		this.mask("正在执行操作...");
		util.rmi.jsonRequest({
					serviceId : this.pixServiceId,
					method:"execute",
					body : r.data,
					serviceAction : action,
					schema : this.entryName
				}, function(code, msg, json) {
					this.unmask()
					if (code < 300) {
						this.refresh()
					} else {
						this.processReturnMsg(code, msg)
					}
				}, this)
	},
	
	doNew : function() {
		var createView = this.midiModules["create"];
		if (!createView) {
			createView = new chis.application.mpi.script.CardForm({
						entryName : this.entryName,
						autoLoadSchema : true,
						mainApp : this.mainApp,
						autoLoadData : false,
						colCount : 1,
						title : "新建卡"
					})
			createView.on("save", function() {
						this.refresh();
					}, this)
			this.midiModules["create"] = createView;
		}

		var data = {};
		data["empiId"] = this.r.id;
		var win = createView.getWin();
		win.minimizable = false;
		win.maximizable = false;
		win.show();
		createView.doNew();
		createView.initFormData(data);
	},

	/**
	 * 为该窗口的调用者提供设置数据加载主键。
	 * @param {} r
	 */
	setRecord : function(r) {
		if (r != this.r) {
			this.r = r;
			this.requestData.cnd = ['and',
					['eq', ['$', 'empiId'], ['s', r.id]],
					['ne', ['$', 'status'], ['s', '2']]];
			this.refresh();
		}
	},

	onDblClick : function() {
		this.openModule("read");
	},
	
	getWin : function() {
		var win = this.win;
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : 'hide',
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false
					});
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			win.on("add", function() {
						this.win.doLayout();
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this)
			this.win = win;
		}
		win.instance = this;
		return win;
	}
});
