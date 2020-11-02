$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyUndeterminedApplyRefundList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds = ['ne', ['$', 'LYPB'], ['i', 1]];
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	this.isfirst = 0;// 控制出库方式下拉框
	cfg.gridDDGroup = "firstGridDDGroup";
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyUndeterminedApplyRefundList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.pha.script.PharmacyUndeterminedApplyRefundList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			getCndBar : function(items) {
				this.storehouse = this.getStorehouse();
				var filelable = new Ext.form.Label({
							text : "领药库房"
						})

				this.storehouse.on("select", this.onStorehouseSelect, this)
				return [filelable, '-', this.storehouse];
			},
			// 选中药库抛出事件,刷新两边界面
			onStorehouseSelect : function(item, record, e) {
				this.fireEvent("storehouseSelect", record);
			},
			doRefresh : function() {
				var record = {};
				record["data"] = this.yksb;
				this.fireEvent("storehouseSelect", record);
			},
			// 刷新页面
			doRefreshWin : function() {
				var body = {};
				body["yksb"] = this.yksb.yksb;
				body["cnd"] = this.cnds;
				this.requestData.body = body;
				this.refresh();
			},
			// 新增
			doAdd : function() {
				if (!this.yksb) {
					Ext.Msg.alert("提示", "请先选择领药库房");
					return;
				}
				var body = {};
				body["yksb"] = this.yksb.yksb
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryCkfsActionId,
							body : body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doAdd);
					return;
				}
				var ckfs = ret.json.ckfs;
				var fsmc = ret.json.fsmc;
				this.refundModule = this.createModule("refundModule",
						this.addRef);
				this.refundModule.on("save", this.doRefresh, this);
				this.refundModule.on("winClose", this.onClose, this);
				this.refundModule.yksb = this.yksb;
				this.refundModule.ckfs = ckfs;
				this.refundModule.fsmc = fsmc;
				this.refundModule.initPanel();
				var win = this.getWin();
				win.add(this.refundModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.refundModule.op = "create";
					this.refundModule.doNew();
				}
			},
			onClose : function() {
				if(this.refundModule.doClose()){
					this.getWin().hide();
				}
			},
			doCancel : function() {
				if (this.storageModule) {
					return this.storageModule.doClose();
				}
			},
			doUpd : function(a, b, tag) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				if (!tag) {
					tag = 1;
				}
				var initDataBody = {};
				initDataBody["xtsb"] = r.data.XTSB;
				initDataBody["ckfs"] = r.data.CKFS;
				initDataBody["ckdh"] = r.data.CKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationApplyDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doUpd);
					this.doRefresh();
					return;
				}
				this.refundModule = this.createModule("refundModule",
						this.addRef);
				this.refundModule.on("save", this.doRefresh, this);
				this.refundModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				this.refundModule.yksb = this.yksb;
				win.add(this.refundModule.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.refundModule.op = "update";
					initDataBody["czpb"] = tag;
					this.refundModule.loadData(initDataBody);
				}

			},
			onDblClick : function(grid, index, e) {
				var item = {};
				item.text = "修改";
				item.cmd = "upd";
				this.doAction(item, e)
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
				body["xtsb"] = r.data.XTSB;
				body["ckfs"] = r.data.CKFS;
				body["ckdh"] = r.data.CKDH;
				var record = {};
				var data = {};
				data["key"] = this.checkOutWayValue;
				record["data"] = data;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeApplyDataActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
							this.doRefresh();
						}, this)
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
			getStorehouse : function() {
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryStorehouseActionID
						});
				if (ret.code > 300) {
					this
							.processReturnMsg(ret.code, ret.msg,
									this.getStorehouse);
					return null;
				}
				var yklb = ret.json.yklb;
				var proxy = new Ext.data.MemoryProxy(yklb);
				var YKLB = Ext.data.Record.create([{
							name : "yksb",
							type : "long",
							mapping : 0
						}, {
							name : "ykmc",
							type : "string",
							mapping : 1
						}]);
				var reader = new Ext.data.ArrayReader({}, YKLB);
				var store = new Ext.data.Store({
							proxy : proxy,
							reader : reader
						});
				store.load();
				var lb = new Ext.form.ComboBox({
							name : "yklb",
							mode : "local",
							triggerAction : "all",
							displayField : "ykmc",
							valueField : "yksb",
							store : store
						})
				if (ret.json.yklb.length > 0) {
					lb.setValue(ret.json.yklb[0][0]);
					this.yksb = {
						"yksb" : ret.json.yklb[0][0],
						"ykmc" : ret.json.yklb[0][1]
					};
				}
				return lb;
			},
			doCommit : function() {
				this.doUpd(null, null, 2);
			},
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/bogus.png'/>"
				//return "<img src='images/bogus.png'/>";
			}
		})