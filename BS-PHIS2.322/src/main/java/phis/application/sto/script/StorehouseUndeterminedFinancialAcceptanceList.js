$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseUndeterminedFinancialAcceptanceList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds =['and',['eq', ['$', 'a.RKPB'], ['i', 1]],['eq',['$', 'a.CWPB'], ['i', 0]]] ;
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.autoLoadData = false;
	cfg.closeAction=true;
	phis.application.sto.script.StorehouseUndeterminedFinancialAcceptanceList.superclass.constructor
			.apply(this, [cfg])
	//this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.sto.script.StorehouseUndeterminedFinancialAcceptanceList,
		phis.script.SimpleList, {
			//入库方式下拉框
			getCndBar : function(items) {
				this.checkInWay = this.getCheckInWay();
				var filelable = new Ext.form.Label({
							text : "入库方式"
						})

				this.checkInWay.on("select", this.onCheckInWaySelect, this)
				return [filelable, '-', this.checkInWay];
			},
			// 生成入库方式下拉框
			getCheckInWay : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryCheckInWayActionID,
							body:{"tag":1}
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.getCheckInWay);
					return null;
				}
				var rkfs = ret.json.rkfs;
				var proxy = new Ext.data.MemoryProxy(rkfs);
				var RKFS = Ext.data.Record.create([{
							name : "fsId",
							type : "long",
							mapping : 0
						}, {
							name : "fsName",
							type : "string",
							mapping : 1
						}]);
				var reader = new Ext.data.ArrayReader({}, RKFS);
				var store = new Ext.data.Store({
							proxy : proxy,
							reader : reader
						});
				store.load();
				var fs= new Ext.form.ComboBox({
							name : "rkfs",
							mode : "local",
							triggerAction : "all",
							displayField : "fsName",
							valueField : "fsId",
							store : store
						})
						if(ret.json.rkfs.length>0){
						fs.setValue(ret.json.rkfs[0][0]);
						this.checkInWayValue={"fsId":ret.json.rkfs[0][0],"fsName":ret.json.rkfs[0][1]};
						}
						return fs;
			},
			// 选中入库方式抛出事件,刷新两边界面
			onCheckInWaySelect : function(item, record, e) {
				this.fireEvent("checkInWaySelect", record);
			},
			doRefresh : function() {
				var record = {};
				record["data"] = this.checkInWayValue;
				this.fireEvent("checkInWaySelect", record);
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.checkInWayValue) {
					var addCnd = ['eq', ['$', 'RKFS'],
							['s', this.checkInWayValue.fsId]];
					this.requestData.cnd = ['and', addCnd, this.cnds];
					this.refresh();
					return;
				}
			},
			onClose : function() {
				this.getWin().hide();
			},
			doCancel : function() {
				if (this.storageModule) {
					return this.storageModule.doClose();
				}
			},
			// 提交
			doCommit : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "未选中记录!", true);
					return;
				}
				var initDataBody = {};
				initDataBody["XTSB"] = r.data.XTSB;
				initDataBody["RKFS"] = r.data.RKFS;
				initDataBody["RKDH"] = r.data.RKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationActionId,
							body:initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					this.doRefresh();
					return ;
				}
				this.storageModule = this.createModule("storageModule",
						this.commitRef);
				this.storageModule.on("save", this.onSave, this);
				this.storageModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.storageModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageModule.doLoad(initDataBody);
				}
			},
			onSave : function() {
				var record = {};
				var data = {};
				record["data"] = this.checkInWayValue;
				this.fireEvent("checkInWaySelect", record);
			}
			,
			// 查看
			doLook : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["XTSB"] = r.data.XTSB;
				initDataBody["RKFS"] = r.data.RKFS;
				initDataBody["RKDH"] = r.data.RKDH;
				this.storageModule_read = this.createModule("storageModule_read",
						this.readRef);
				this.storageModule_read.initPanel();
				var win = this.storageModule_read.getWin();
				win.show();
				win.center();
				if (!win.hidden) {
					this.storageModule_read.doRead(initDataBody);
				}

			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "查看";
				item.cmd = "look";
				this.doAction(item, e)
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
			}

		})