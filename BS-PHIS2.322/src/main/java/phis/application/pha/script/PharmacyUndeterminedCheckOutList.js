$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyUndeterminedCheckOutList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds = ['ne', ['$', 'CKPB'], ['i', 1]];
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	this.isfirst = 0;// 控制出库方式下拉框
	cfg.autoLoadData = false;
	phis.application.pha.script.PharmacyUndeterminedCheckOutList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.pha.script.PharmacyUndeterminedCheckOutList,
		phis.script.SimpleList, {
			lock : function(r) {
				var p = {};
				p.YWXH = '1012';
				p.SDXH = r.data.YFSB + '-' + r.data.CKFS + '-' + r.data.CKDH;
				return this.bclLock(p);
			},
			unlock : function(r) {
				var p = {};
				p.YWXH = '1012';
				p.SDXH = r.data.YFSB + '-' + r.data.CKFS + '-' + r.data.CKDH;
				return this.bclUnlock(p);
			},
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			},
			getCndBar : function(items) {
				this.checkOutWay = this.getcheckOutWay();
				var filelable = new Ext.form.Label({
							text : "出库方式"
						})

				this.checkOutWay.on("select", this.oncheckOutWaySelect, this)
				var combox = this.checkOutWay;
				var _ctr = this;
				this.checkOutWay.store.on("load", function() {
							if (this.getCount() == 0 || _ctr.isfirst == 1)
								return;
							combox.setValue(this.getAt(0).get('key'))
							_ctr.oncheckOutWaySelect(null, this.getAt(0))
							_ctr.isfirst = 1;
						});
				return [filelable, '-', this.checkOutWay];
			},
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				return "<img src='" + ClassLoader.appRootOffsetPath
						+ "resources/phis/resources/images/bogus.png'/>"
				// return "<img src='images/bogus.png'/>";
			},
			// 生成出库方式下拉框
			getcheckOutWay : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "CKFS") {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 150;
				var yfsb = this.mainApp['phis'].pharmacyId == undefined
						? 0
						: this.mainApp['phis'].pharmacyId;
				// it.dic.filter = "['eq',['$map',['s','YFSB']],['i'," + yfsb
				// + "]]";
				it.dic.filter = "['eq',['$','item.properties.YFSB'],['l',"
						+ yfsb + "]]";
				f = this.createDicField(it.dic);
				f.on("specialkey", this.onQueryFieldEnter, this);
				return f;
			},
			// 选中入库方式抛出事件,刷新两边界面
			oncheckOutWaySelect : function(item, record, e) {
				this.fireEvent("checkOutWaySelect", record);
			},
			doRefresh : function() {
				var record = {};
				var data = {};
				data["key"] = this.checkOutWayValue;
				record["data"] = data;
				this.fireEvent("checkOutWaySelect", record);
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.checkOutWayValue) {
					var addCnd = ['eq', ['$', 'CKFS'],
							['s', this.checkOutWayValue]];
					this.requestData.cnd = ['and', addCnd, this.cnds];
					this.refresh();
					return;
				}
			},
			// 新增
			doAdd : function() {
				if (!this.checkOutWayValue) {
					Ext.Msg.alert("提示", "请先选择出库方式");
					return;
				}
				this.storageModule = this.createModule("storageModule",
						this.addRef);
				this.storageModule.on("save", this.onSave, this);
				this.storageModule.on("winClose", this.onClose, this);
				this.storageModule.checkOutWayValue = this.checkOutWayValue;
				this.storageModule.initPanel();
				var win = this.getWin();
				win.add(this.storageModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageModule.op = "create";
					this.storageModule.doNew();
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
					return;
				}
				var initDataBody = {};
				initDataBody["YFSB"] = r.data.YFSB;
				initDataBody["CKFS"] = r.data.CKFS;
				initDataBody["CKDH"] = r.data.CKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationCheckOutDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					this.doRefresh();
					return;
				}
				// 业务锁 add by yangl
				if (!this.lock(r))
					return;
				this.storageModule = this.createModule("storageModule",
						this.addRef);
				this.storageModule.on("commit", this.onCommit, this);
				this.storageModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.storageModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageModule.doOpneCommit(initDataBody);
				}
			},
			onCommit : function() {
				var record = {};
				var data = {};
				data["key"] = this.checkOutWayValue;
				record["data"] = data;
				this.fireEvent("checkOutWaySelect", record);
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["YFSB"] = r.data.YFSB;
				initDataBody["CKFS"] = r.data.CKFS;
				initDataBody["CKDH"] = r.data.CKDH;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationCheckOutDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doUpd);
					this.doRefresh();
					return;
				}
				// 业务锁 add by yangl
				if (!this.lock(r))
					return;
				this.storageModule = this.createModule("storageModule",
						this.addRef);
				this.storageModule.on("save", this.onSave, this);
				this.storageModule.on("winClose", this.onClose, this);
				this.storageModule.opener = this;
				var win = this.getWin();
				win.add(this.storageModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.storageModule.op = "update";
					this.storageModule.loadData(initDataBody);
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
				body["YFSB"] = r.data.YFSB;
				body["CKFS"] = r.data.CKFS;
				body["CKDH"] = r.data.CKDH;
				var record = {};
				var data = {};
				data["key"] = this.checkOutWayValue;
				record["data"] = data;
				if (!this.lock(r))
					return;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removeCheckOutDataActionId,
							body : body
						}, function(code, msg, json) {
							this.unmask()
							this.unlock(r);
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
								this.fireEvent("checkOutWaySelect", record)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title || this.name,
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
				var module = this.createModule("nostorehouselistprint",
						this.refNoPharmacyOutPrint)
				var r = this.getSelectedRecord()
				if (r == null) {
					MyMessageTip.msg("提示", "打印失败：无效的出库单信息!", true);
					return;
				}
				module.yfsb = r.data.YFSB;
				module.ckfs = r.data.CKFS;
				module.ckdh = r.data.CKDH;
				module.initPanel();
				module.doPrint();
			}
		})