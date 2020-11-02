$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyBasicInfomationList = function(cfg) {
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以
	phis.application.pha.script.PharmacyBasicInfomationList.superclass.constructor
			.apply(this, [cfg])
	this.winCls = cfg.winCls;
	this.on("beforeRemove", this.onBeforeRemove, this);
}
Ext.extend(phis.application.pha.script.PharmacyBasicInfomationList,
		phis.script.SimpleList, {
			doOpenWin : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				//this.list = this.midiModules["windowList"];
				var exContext = {};
				exContext.empiData = {};
				exContext.empiData["YFSB"] = r.json.YFSB;
				this.list=this.createModule("pharmacyWindowInfomationList",this.winCls);
				this.list.exContext = exContext;
				var m = this.list.initPanel();
				var win = this.list.getWin();
				win.add(m)
				win.show()
				win.center()
				if(!win.hidden){
				this.list.requestData.cnd = ['eq', ['$', 'YFSB'],
							['$', '' + r.json.YFSB]];
				this.list.refresh()		
				}
//				if (!this.list) {
//					var module = this.createModule("pharmacyWindowInfomationList",this.winCls);
//					module.autoLoadSchema = true;
//					module.showButtonOnTop = true;
//					module.autoLoadData = true;
//					module.isCombined = false;
//					module.disablePagingTbr = true;
//					module.modal = true;
//					module.cnds = ['eq', ['$', 'YFSB'], ['$', '' + r.json.YFSB]];
//					module.exContext = exContext;
//					this.list = module.initPanel();
//					this.midiModules["windowList"] = this.list;
//				} else {
//					this.list.requestData.cnd = ['eq', ['$', 'YFSB'],
//							['$', '' + r.json.YFSB]];
//					this.list.exContext = exContext;
//					this.list.refresh()
//				}
//				var p = this.list.getWin();
//				p.show();

			},
			// list显示数据的时候将1转换成是,0转成否
			onRenderer : function(value, metaData, r, rowIndex, columnIndex,
					store) {
				if (value == 1) {
					return "是";
				} else {
					return "否";
				}
			}
			/*
			 * , // 删除前校验有没有窗口 onBeforeRemove : function(entryName,r) { var
			 * body={}; body["yfsb"]=r.data.YFSB; var r =
			 * phis.script.rmi.miniJsonRequestSync({ serviceId :
			 * this.verifiedUsingServiceId, serviceAction :
			 * this.verifiedUsingActionId, body : body }); if (r.code > 300) {
			 * this.processReturnMsg(r.code, r.msg, this.onBeforeSave); return
			 * false; } else { return true; } }
			 */
			,
			doCancellation : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}

				var body = {};
				body["yfsb"] = r.data.YFSB;
				body["op"] = r.data.ZXBZ;
				var msg
				if (r.data.ZXBZ == 1) {
					msg = "是否取消注销" + r.data.YFMC;
				} else {
					msg = "是否注销" + r.data.YFMC;
				}
				Ext.Msg.show({
					title : "注销",
					msg : msg,
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var ret = phis.script.rmi.miniJsonRequestSync({
										serviceId : this.serviceId,
										serviceAction : this.cancellationActionId,
										body : body
									});
							if (ret.code > 300) {
								this.processReturnMsg(ret.code, ret.msg,
										this.doInvalid);
							} else {
								var btns = this.grid.getTopToolbar();
								var btn = btns.find("cmd", "cancellation");
								btn = btn[0];
								if (r.data.ZXBZ == 0) {
									if (btn.getText().indexOf("取消") > -1) {
										return;
									}
									btn.setText(btn.getText().replace("注销",
											"取消注销"));
								} else {
									btn.setText(btn.getText().replace("取消注销",
											"注销"));
								}
								this.refresh();
							}
						}
					},
					scope : this
				})

			},

			// 单击时改变作废按钮
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var btns = this.grid.getTopToolbar();
				var btn = btns.find("cmd", "cancellation");
				btn = btn[0];
				if (r.data.ZXBZ == 1) {
					if (btn.getText().indexOf("取消") > -1) {
						return;
					}
					btn.setText(btn.getText().replace("注销", "取消注销"));
				} else {
					btn.setText(btn.getText().replace("取消注销", "注销"));
				}

			},
			onTpRenderer : function(value, metaData, r) {
				if (r.data.ZXBZ == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/(00,04).png'/>"
				}
				return value;
			},// 加上鼠标移动提示记录是否已作废功能
			onReady : function() {
				phis.application.pha.script.PharmacyBasicInfomationList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
				this.grid.on("keypress", this.onKeypress, this);
			},
			// 鼠标移动提示记录是否已作废功能
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.ZXBZ == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '药房已注销'
									}, false);
						}
					}
				}
			},
			// 上下时改变作废按钮
			onKeypress : function(e) {
				if (e.getKey() == 40 || e.getKey() == 38) {
					this.onRowClick();
				}
			},
			// 刚打开页面时候默认选中第一条数据,这时候判断下作废按钮
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
			doReceiveDrugsWay : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var module = this
						.createModule("rdmList", this.receiveDrugsWay);
				var win = module.getWin();
				win.add(module.initPanel());
				module.requestData.serviceId = "phis.pharmacyManageService";
				module.requestData.serviceAction = "queryReceiveWayMaintain",
				module.requestData.body = {'YFSB':r.data.YFSB};
				module.refresh();
				win.show();
			}
		});
