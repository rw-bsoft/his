$package("phis.application.war.script")
$import("phis.script.EditorList")

phis.application.war.script.AdditionalProjectsFeeSubmitList= function(cfg) {
	phis.application.war.script.AdditionalProjectsFeeSubmitList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadDate, this);
}

Ext.extend(phis.application.war.script.AdditionalProjectsFeeSubmitList,
		phis.script.EditorList, {
			onReady : function() {
				
			},
			loadData : function(bodys) {
				this.clear();
//				this.requestData.cnds=[];
				this.requestData.serviceId = "phis.doctorAdviceExecuteService";
				this.requestData.serviceAction = "additionProjectsFeeQuery";
				if(this.bodys){//医嘱组号
					this.requestData.cnds = this.bodys;
				}
				if(this.initDataId){//病人住院号
					this.requestData.cnd = this.initDataId;
				}
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			afterLoadDate : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					var YCSL = r.get("YCSL");
					var FYCS = r.get("FYCS");
					var YPDJ = r.get("YPDJ");
					r.set("ZJE", YCSL * FYCS * YPDJ);
				}
				this.getYzzh();
			},
			initPanel : function(sc) {
				var grid = phis.application.war.script.AdditionalProjectsFeeSubmitList.superclass.initPanel
						.call(this, sc)
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				return grid
			},
			showColor : function(v, params, data) {
				var YZZH = data.get("YZZH") % 2 + 1;
				switch (YZZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
					case 3 :
						params.css = "x-grid-cellbg-3";
						break;
					case 4 :
						params.css = "x-grid-cellbg-4";
						break;
					case 5 :
						params.css = "x-grid-cellbg-5";
						break;
				}
				return "";
			},
			doRender : function(v, params, record) {
				var zje = record.get("FYCS") * record.get("YCSL")
				return parseFloat(zje).toFixed(2);
			},
			doRefresh : function() {
				this.refresh();
			},
			doConfirm : function() {
				var store = this.grid.getStore();
				if (store.getCount() == 0) {
					MyMessageTip.msg("提示", "没有可执行的附加计价项目!", true);
					return;
				}
				var body = [];
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (!r.get("QRSL") && r.get("QRSL") != 0) {
						MyMessageTip.msg("提示", "确认数量不能为空", true);
						return;
					}
					body.push(r.data);
				}
				var body_wpjfbz = {};
				body_wpjfbz['bq'] = this.mainApp['phis'].wardId;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configLogisticsInventoryControlService",
							serviceAction : "verificationWPJFBZ",
							body : body_wpjfbz
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doConfirm);
					return;
				}
				Ext.Msg.confirm("请确认", "执行附加计价?", function(btn) {
							if (btn == 'yes') {
								phis.script.rmi.jsonRequest({
											serviceId : "doctorAdviceExecuteService",
											serviceAction : "SaveConfirmAdditional",
											body : body
										}, function(code, msg, json) {
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												return;
											}
											this.doRefresh();
											MyMessageTip
													.msg("提示", "确认成功", true);
//											var ret_body =json.body;
//											if(ret_body&&ret_body!=null&&ret_body.BRID){
//											var brid = ret_body.BRID;
//											var xhrq = ret_body.XHRQ;
//											var ksmc = ret_body.KSMC;
//											var ksdm = ret_body.KSDM;
//											var module = this.createModule(
//													"fpprintxhmx", "WAR0403")
//											module.BRID = brid;
//											module.XHRQ = xhrq;
//											module.KSMC = ksmc;
//											module.KSDM = ksdm;
//											module.initPanel();
//											module.doPrint();}
										}, this)
							}
						}, this);
			},
			getYzzh : function() {
				yzzh = 1;
				var store = this.grid.getStore();
				var n = store.getCount()
				var YZZHs = [];
				for (var i = 0; i < n; i++) {
					if (i == 0) {
						var r = store.getAt(i)
						yzzh = r.get('YZZH') % 2 + 1;
						YZZHs.push(yzzh)
					} else {
						var r1 = store.getAt(i - 1)
						var r = store.getAt(i)
						if (r1.get('YZZH') == r.get('YZZH')) {
							YZZHs.push(yzzh)
						} else {
							YZZHs.push(++yzzh)
						}
					}
				}
				for (var i = 0; i < YZZHs.length; i++) {
					var r = store.getAt(i);
					r.set('YZZH', YZZHs[i]);
				}
			},

			doPrint : function() {
				var store = this.grid.getStore();
				if (store.getCount() == 0) {
					MyMessageTip.msg("提示", "没有可执行的附加计价项目!", true);
					return;
				}
				/*
				 * var body = []; for(var i = 0;i<store.getCount();i++){ var r =
				 * store.getAt(i); body.push(r.get("ZYH"));
				 * body.push(r.get("QRSL")); } var cm =
				 * this.grid.getColumnModel()
				 */
				var module = this.createModule("additionalPricing",
						this.refAdditionalPricing);
				// module.BODY = cm;
				module.initPanel();
				module.doPrint();
			}
			/*
			 * doPrint : function() { var cm = this.grid.getColumnModel() var
			 * pWin = this.midiModules["printView"] var cfg = { title :
			 * this.title, requestData : this.requestData, cm : cm } if (pWin) {
			 * Ext.apply(pWin, cfg) pWin.getWin().show() return } pWin = new
			 * app.modules.list.PrintWin(cfg) this.midiModules["printView"] =
			 * pWin pWin.getWin().show() }
			 */
		});
