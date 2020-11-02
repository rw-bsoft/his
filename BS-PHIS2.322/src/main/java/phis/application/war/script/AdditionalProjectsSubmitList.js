$package("phis.application.war.script")
$import("phis.script.EditorList")
// $import("phis.script.SimpleList", "util.dictionary.SimpleDicFactory")

phis.application.war.script.AdditionalProjectsSubmitList = function(cfg) {
	phis.application.war.script.AdditionalProjectsSubmitList.superclass.constructor
			.apply(this, [cfg])
	this.on("loadData", this.afterLoadDate, this);
	this.on("afterCellEdit", this.afterGridEdit, this)
}

Ext.extend(phis.application.war.script.AdditionalProjectsSubmitList,
		phis.script.EditorList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis.doctorAdviceExecuteService";
				this.requestData.serviceAction = "additionProjectsQuery";
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
					r.set("QRSL", YCSL * FYCS);
					r.set("ZJE", YCSL * FYCS * YPDJ);
				}
				this.getYzzh();
			},
			afterGridEdit : function(it, record, field, v) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var curRow = cell[0];
				for (var i = 0; i < this.store.getCount(); i++) {
					if (i != curRow) {
						var YPDJ = record.get("YPDJ");
						var QRSL = record.get("QRSL");
						record.set("ZJE", QRSL * YPDJ);
						this.grid.startEditing(curRow, 4);
					}
				}
				return true;
			},
			initPanel : function(sc) {
				var grid = phis.application.war.script.AdditionalProjectsSubmitList.superclass.initPanel
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
								/*var datas = []; 判断物质库存
								for (var i = 0; i < store.getCount(); i++) {
									var r = store.getAt(i);
									var data = {};
									data["FYMC"] = r.data.YZMC; // 费用名称
									data["YLSL"] = r.data.YCSL*r.data.FYCS; // 费用名称
									data["YLXH"] = r.data.YPXH; // 费用名称
									datas[i] = data;
								}
								var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : "clinicDisposalEntryService",
									serviceAction : "checkProjectMaterials",
									bodys : datas
								});
							    if (r.code > 300) {
									this.processReturnMsg(r.code, r.msg);
									return;
							    }*/
								phis.script.rmi.jsonRequest({
											serviceId : "doctorAdviceExecuteService",
											serviceAction : "SaveConfirmAdditional",
											body : body
										}, function(code, msg, json) {
											this.grid.el.unmask()
											if (code >= 300) {
												this.processReturnMsg(code,msg);
												return;
											}
											this.doRefresh();
											if(json.RES_MESSAGE){
												MyMessageTip.msg("提示", json.RES_MESSAGE, true);
											}else{
												MyMessageTip.msg("提示", "确认成功", true);
											}
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
