$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseMedicinesUndeterminedPriceAdjustList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.cnds = ['ne', ['$', 'ZYPB'], ['i', 1]];
	cfg.winState = "center";
	cfg.modal = this.modal = true;
	cfg.gridDDGroup = "firstGridDDGroup";
	this.isfirst=0;//控制调价方式下拉框
	cfg.autoLoadData=false;
	cfg.showButtonOnTop=true;
	cfg.closeAction=true;
	phis.application.sto.script.StorehouseMedicinesUndeterminedPriceAdjustList.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesUndeterminedPriceAdjustList,
		phis.script.SimpleList, {
			getCndBar : function(items) {
				this.PriceAdjustWay = this.getPriceAdjustWay();
				var filelable = new Ext.form.Label({
							text : "调价方式"
						})
				this.PriceAdjustWay.on("select", this.onPriceAdjustWaySelect,
						this)
				var combox = this.PriceAdjustWay;
				var _ctr = this;
				this.PriceAdjustWay.store.on("load", function() {
							if (this.getCount() == 0||_ctr.isfirst==1)
								return;
							combox.setValue(this.getAt(0).get('key'))
							_ctr.priceAdjustWayValue=this.getAt(0).get('key')
							_ctr.isfirst=1;
						});
				return [filelable, '-', this.PriceAdjustWay];
			},
			// 页面打开时记录前增加未确认图标
			onRenderer : function(value, metaData, r) {
				return "<img src='images/bogus.png'/>";
			},
			// 生成调价方式下拉框
			getPriceAdjustWay : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "TJFS") {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 150;
				f = this.createDicField(it.dic);
				f.on("specialkey", this.onQueryFieldEnter, this);
				return f;
			},
			// 选中调价方式抛出事件,刷新两边界面
			onPriceAdjustWaySelect : function(item, record, e) {
				this.fireEvent("priceAdjustWaySelect", record);
			},
			// 刷新页面
			doRefreshWin : function() {
				if (this.priceAdjustWayValue) {
					var addCnd = ['eq', ['$', 'TJFS'],
							['i', this.priceAdjustWayValue]];
					this.requestData.cnd = ['and', addCnd, this.cnds];
					this.refresh();
					return;
				}
			},
			// 新增
			doAdd : function() {
				if (!this.priceAdjustWayValue) {
					Ext.Msg.alert("提示", "请先选择调价方式");
					return;
				}
				this.priceAdjustModule = this.createModule("priceAdjustModule",
						this.addRef);
				this.priceAdjustModule.on("save", this.onSave, this);
				//this.priceAdjustModule.on("winClose", this.onClose, this);
				this.priceAdjustModule.priceAdjustWayValue = this.priceAdjustWayValue;
				this.priceAdjustModule.initPanel();
				var win = this.priceAdjustModule.getWin();
				this.priceAdjustModule.initPanel();
				win.show()
				win.center()
				if (!win.hidden) {
					this.priceAdjustModule.op = "create";
					this.priceAdjustModule.doNew();
				}

			},
			onClose : function() {
				this.getWin().hide();
			},
			doCancel : function() {
				if (this.priceAdjustModule) {
					return this.priceAdjustModule.doClose();
				}
			},
			// 提交
			doCommit : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["TJFS"] = r.data.TJFS;
				initDataBody["TJDH"] = r.data.TJDH;
				initDataBody["JGID"] = r.data.JGID;
				initDataBody["XTSB"] = r.data.XTSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationPriceAdjustDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					this.doRefresh();
					return;
				}
				this.priceAdjustCommitModule = this.createModule("priceAdjustCommitModule",
						this.commitRef);
				this.priceAdjustCommitModule.on("commit", this.onCommit, this);
				this.priceAdjustCommitModule.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.priceAdjustCommitModule.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.priceAdjustCommitModule.doOpneCommit(initDataBody);
				}
			},
			onCommit : function() {
				var record = {};
				var data = {};
				data["key"] = this.priceAdjustWayValue;
				record["data"] = data;
				this.fireEvent("priceAdjustWaySelect", record);	
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["TJFS"] = r.data.TJFS;
				initDataBody["TJDH"] = r.data.TJDH;
				initDataBody["JGID"] = r.data.JGID;
				initDataBody["XTSB"] = r.data.XTSB;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.verificationPriceAdjustDeleteActionId,
							body : initDataBody
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doUpd);
					this.doRefresh();
					return;
				}
				this.priceAdjustModule = this.createModule("priceAdjustModule",
						this.addRef);
				this.priceAdjustModule.on("save", this.onSave, this);
				//this.priceAdjustModule.on("winClose", this.onClose, this);
				var win = this.priceAdjustModule.getWin();
				this.priceAdjustModule.initPanel();
				win.show()
				win.center()
				if (!win.hidden) {
					this.priceAdjustModule.op = "update";
					this.priceAdjustModule.loadData(initDataBody);
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
				var initDataBody = {};
				initDataBody["TJFS"] = r.data.TJFS;
				initDataBody["TJDH"] = r.data.TJDH;
				initDataBody["JGID"] = r.data.JGID;
				initDataBody["XTSB"] = r.data.XTSB;
				var record = {};
				var data = {};
				data["key"] = this.priceAdjustWayValue;
				record["data"] = data;
				this.mask("在正删除数据...");
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.removePriceAdjustDataActionId,
							body : initDataBody
						}, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data);
								this.fireEvent("priceAdjustWaySelect", record)
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
			doRefresh:function(){
			var record = {};
				var data = {};
				data["key"] = this.priceAdjustWayValue;
				record["data"] = data;
				this.fireEvent("priceAdjustWaySelect", record);	
			},
			expansion : function(cfg) {
				var bar = cfg.tbar;
				cfg.tbar = {
					enableOverflow : true,
					items : bar
				}
			}
		})