$package("phis.application.cfg.script")

/**
 * 病人性质维护的药品限制界面List zhangyq 2012.5.25
 */
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigPaymentTypeList = function(cfg) {
	phis.application.cfg.script.ConfigPaymentTypeList.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.cfg.script.ConfigPaymentTypeList, phis.script.SimpleList,
		{
			onReady : function() {
				phis.application.cfg.script.ConfigPaymentTypeList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (String(index) == 'false')
					return;
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record.data.MRBZ == 1) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
									qtip : '<div style="font-size: 12;">默认付款方式 </div>',
									qwidth : '200',
									qtitle : '<b>说明:<b></br>',
									qclass : ''
								}, false);
					} else if (record.data.ZFBZ == 1) {
						var rowEl = Ext.get(e.getTarget());
						rowEl.set({
							qtip : '<div style="font-size: 12;">该付款方式已作废 </div>',
							qwidth : '200',
							qtitle : '<b>说明:<b></br>',
							qclass : ''
						}, false);
					}
				}

			},
			onRendererMRBZ : function(value, metaData, r) {
				var MRBZ = r.get("MRBZ");
				var src = (MRBZ == 1) ? "yes" : "no";
				if (MRBZ == 1) {
					return "<img src='"+ClassLoader.appRootOffsetPath+"resources/phis/resources/images/" + src + ".png'/>";
				} else {
					return "";
				}
			},
			onRendererZFBZ : function(value, metaData, r) {
				var ZFBZ = r.get("ZFBZ");
				var src = (ZFBZ == 1) ? "yes" : "no";
				if (ZFBZ == 1) {
					return "<img src='"+ClassLoader.appRootOffsetPath+"resources/phis/resources/images/" + src + ".png'/>";
				} else {
					return "";
				}
			},
			doLogOut : function() {
				var cm = this.grid.getSelectionModel();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var date = {};
				if (r.get("MRBZ") == 1) {
					MyMessageTip.msg("提示", "该付款方式为默认付款方式，不能作废!", true);
					return;
				}
				if (r.get("HBWC") == 1) {
					MyMessageTip.msg("提示", "该付款方式为货币误差，不能作废!", true);
					return;
				}
				date["ZFBZ"] = "1";
				var msg="确认作废";
				if (r.get("ZFBZ") == 1) {
					date["ZFBZ"] = "0";
					msg="确认取消作废";
				}
				date["FKFS"] = r.get("FKFS");
				Ext.Msg.confirm("请确认", msg+"【" + r.get("FKMC") + "】吗？",
						function(btn) {
							if (btn == 'yes') {
								this.grid.el.mask("正在修改数据...","x-mask-loading")
								phis.script.rmi.jsonRequest({
											serviceId : "configPaymentTypeService",
											serviceAction : "paymentWayInvalidate",
											method : "execute",
											body : date
										}, function(code, msg, json) {
											this.grid.el.unmask()
											if (code >= 300) {
												this.processReturnMsg(code,msg);
												return;
											}else{
												var btns = this.grid.getTopToolbar();
												var btn = btns.find("cmd", "logOut");
												btn = btn[0];
												if (r.data.ZFBZ == 0) {
													if (btn.getText().indexOf("取消") > -1) {
															return;
													}
													btn.setText(btn.getText().replace("作废", "取消作废"));
												} else {
													btn.setText(btn.getText().replace("取消作废", "作废"));
												}
													this.refresh();
											}
										}, this)
							}
						},this);
				return
			},
			doDefault : function() {
				var cm = this.grid.getSelectionModel();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var date = {};
				if (r.get("ZFBZ") == 1) {
					MyMessageTip.msg("提示", "该付款方式已作废,不能设为默认付款方式!", true);
					return;
				}
				if (r.get("HBWC") == 1) {
					MyMessageTip.msg("提示", "默认付款方式不能为货币误差!", true);
					return;
				}
				date["FKFS"] = r.get("FKFS");
				date["SYLX"] = r.get("SYLX");
				this.grid.el.mask("正在修改数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "configPaymentTypeService",
							serviceAction : "updatePaymentDefault",
							method : "execute",
							body : date
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.loadData();
						}, this)
				return
			},
			doMError : function() {
				var cm = this.grid.getSelectionModel();
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var date = {};
				if (r.get("ZFBZ") == 1) {
					MyMessageTip.msg("提示", "该付款方式已作废,不能设为货币误差付款方式!", true);
					return;
				}
				if (r.get("MRBZ") == 1) {
					MyMessageTip.msg("提示", "默认付款方式不能为货币误差!", true);
					return;
				}
				date["FKFS"] = r.get("FKFS");
				date["SYLX"] = r.get("SYLX");
				this.grid.el.mask("正在修改数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "configPaymentTypeService",
							serviceAction : "updateCurrencyErrors",
							method : "execute",
							body : date
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.loadData();
						}, this)
				return
			},
			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "logOut");
				btn = btn[0];
				if (r.data.ZFBZ == 1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("作废", "取消作废"));
				} else {
					btn.setText(btn.getText().replace("取消作废", "作废"));
				}

			},
			// 刚打开页面时候默认选中数据,这时候判断下作废按钮
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick();
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick();
				}
			},
			// 上下时改变作废按钮
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			}
		})