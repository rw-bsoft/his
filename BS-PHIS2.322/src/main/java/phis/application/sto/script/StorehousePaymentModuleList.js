/**
 * 初始账册界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.EditorList",
		"phis.application.sto.script.StorehousePaymentProcessingPrintView");

phis.application.sto.script.StorehousePaymentModuleList = function(cfg) { 
	cfg.width = 730;
	cfg.height = 400;
	cfg.modal = true;
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.entryName="phis.application.sto.schemas.YK_FKCL"; 
	phis.application.sto.script.StorehousePaymentModuleList.superclass.constructor.apply(this, [cfg]); 
}
Ext.extend(phis.application.sto.script.StorehousePaymentModuleList, phis.script.EditorList, {
			initPanel : function(sc) {  
				var grid = phis.application.sto.script.StorehousePaymentModuleList.superclass.initPanel.call(this, sc)
				grid.on("beforeedit", this.beforeCellEdit, this); 
				this.grid = grid;
				var sm = grid.getSelectionModel(); 
				sm.onEditorKey = function(field, e) { 
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor; 
					if (k == e.ENTER) {
						e.stopEvent();
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}

					} else if (k == e.TAB) {
						e.stopEvent();
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}
					} else if (k == e.ESC) {
						ed.cancelEdit();
					}
					if (newCell) {
						r = newCell[0];
						c = newCell[1];
						this.select(r, c);
						if (g.isEditor && !g.editing) {
							ae = g.activeEditor;
							if (ae && ae.field.triggerBlur) {
								ae.field.triggerBlur();
							}
							g.startEditing(r, c);
						}
					}

				}; 
				return grid 
			},
			// 刚打开页面时候默认选中第一条数据
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					document.getElementById("YK_FKCL_1").innerHTML = "合计 付款金额:0.00 已付金额:0.00 未付金额:0.00";
					this.fireEvent("noRecord", this);
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0);
					this.onRowClick(this.grid);
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
					this.onRowClick(this.grid);
				}
				var store = this.grid.getStore();
				var n = store.getCount()
				var sum_fkje = 0;
				var sum_yfje = 0;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					sum_fkje += parseFloat(r.data.FKJE)
					sum_yfje += parseFloat(r.data.YFJE)
				}

				var sum_wfje = (parseFloat(sum_fkje) - parseFloat(sum_yfje)).toFixed(4);
				sum_fkje = parseFloat(sum_fkje).toFixed(4);
				sum_yfje = parseFloat(sum_yfje).toFixed(4);
				document.getElementById("YK_FKCL_1").innerHTML = "合计 付款金额:"
						+ sum_fkje + " 已付金额:" + sum_yfje + " 未付金额:" + sum_wfje;
				if (sum_wfje == 0) {
					// var btns = this.grid.getTopToolbar();
					// var btn = btns.find("cmd", "payment");
					// btn = btn[0];
					// btn.setDisabled(true);
					this.radiogroup.setDisabled(true);
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "payment");
					btn = btn[0];
					btn.setDisabled(false);
					this.radiogroup.setDisabled(false);
				}
			},
			expansion : function(cfg) {
				var radiogroup = new Ext.form.RadioGroup({
							fieldLabel : "状态",
							width : 380,
							items : [{
										boxLabel : '全额付款（含余额）',
										inputValue : 1,
										cmd : "jzzt",
										name : "jzzt",
										checked : true,
										clearCls : true
									}, {
										boxLabel : '部分金额分摊付款',
										cmd : "jzzt",
										name : "jzzt",
										inputValue : 2,
										clearCls : true
									}, {
										boxLabel : '输入付款',
										cmd : "jzzt",
										name : "jzzt",
										inputValue : 3,
										clearCls : true
									}]
						});
				this.jzzt = 1;

				radiogroup.on('change', function(radiogroup, radio) {
							var jzzt = radio.inputValue;
							this.jzzt = jzzt
							if (jzzt == 1) {
								var count = this.store.getCount();
								for (var i = 0; i < count; i++) {
									var r = this.store.getAt(i);
									r.set("BCJE", r.get("FKJE")- r.get("YFJE"));
								}
							} else if (jzzt == 2) {
								Ext.MessageBox.prompt('输入', '请输入要分摊的金额:',
										this.afterImportation, this);
							} else if (jzzt == 3) {
								this.grid.startEditing(0, 6);
							}
						}, this);
				this.radiogroup = radiogroup;
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([radiogroup, "-", tbar]);
				var label = new Ext.form.Label({
					html : "<div id='YK_FKCL_1' align='center' style='color:blue'>合计 付款金额:0.00 已付金额:0.00 未付金额:0.00</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			loadData : function() {
//				this.requestData.pageNo = 1;
				this.requestData.serviceId = "phis.storehouseManageService";
				this.requestData.serviceAction = "queryPaymentProcessing";
				phis.application.sto.script.StorehousePaymentModuleList.superclass.loadData.call(this);
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			},
			doInsertAfter : function(item, e, newGroup) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var count = this.store.getCount();
				if ((row + 1) < count) {
					this.grid.startEditing(row + 1, 6);
				}
			},
			beforeCellEdit : function(e) {
				var cm = this.grid.getColumnModel()
				var c = cm.config[e.column]
				var it = c.schemaItem
				if (this.jzzt == 1 || this.jzzt == 2) {
					return false;
				}
			},
			afterImportation : function(btn, ldc_fkje) {
				if (btn == 'ok') {
					if (!ldc_fkje) {
						ldc_fkje = 0;
					}
					if (isNaN(ldc_fkje)) {
						Ext.MessageBox.alert('提示', '付款金额为非数字，本操作不能执行!',
								function() {
									Ext.MessageBox.prompt('输入', '请输入要分摊的金额:',
											this.afterImportation, this);
								}, this);
						var count = this.store.getCount();
						for (var i = 0; i < count; i++) {
							var r = this.store.getAt(i);
							r.set("BCJE", 0);
						}
						return;
					}
					var sum_FKJE = 0;
					var sum_YFJE = 0;
					var count = this.store.getCount();
					for (var i = 0; i < count; i++) {
						var r = this.store.getAt(i);
						sum_FKJE += parseFloat(r.get("FKJE"));
						sum_YFJE += parseFloat(r.get("YFJE"));
					}
					if (sum_FKJE > 0) {
						if (ldc_fkje <= 0) {
							var count = this.store.getCount();
							for (var i = 0; i < count; i++) {
								var r = this.store.getAt(i);
								r.set("BCJE", 0);
							}
							return;
						}
						if (ldc_fkje > (sum_FKJE - sum_YFJE)) {
							Ext.MessageBox.alert('提示',
									'付款金额大于剩余未付款金额，本操作不能执行!', function() {
										Ext.MessageBox.prompt('输入',
												'请输入要分摊的金额:',
												this.afterImportation, this);
									}, this);
							var count = this.store.getCount();
							for (var i = 0; i < count; i++) {
								var r = this.store.getAt(i);
								r.set("BCJE", 0);
							}
							return;
						}
					} else if (sum_FKJE < 0) {
						if (ldc_fkje >= 0) {
							var count = this.store.getCount();
							for (var i = 0; i < count; i++) {
								var r = this.store.getAt(i);
								r.set("BCJE", 0);
							}
							return;
						}
						if (ldc_fkje < (sum_FKJE - sum_YFJE)) {
							Ext.MessageBox.alert('提示',
									'付款金额大于剩余未付款金额，本操作不能执行!', function() {
										Ext.MessageBox.prompt('输入',
												'请输入要分摊的金额:',
												this.afterImportation, this);
									}, this);
							var count = this.store.getCount();
							for (var i = 0; i < count; i++) {
								var r = this.store.getAt(i);
								r.set("BCJE", 0);
							}
							return;
						}
					}
					var ldc_tmp = 0;
					var li_row_tmp = 0;
					for (var i = 0; i < count; i++) {
						var r = this.store.getAt(i);
						if (r.get("FKJE") == r.get("YFJE")) {
							continue;
						}
						var ldc_tmp1 = ldc_fkje
								/ (sum_FKJE - sum_YFJE)
								* (parseFloat(r.get("FKJE")) - parseFloat(r
										.get("YFJE")));
						r.set("BCJE", ldc_tmp1);
						ldc_tmp += parseFloat(r.get("BCJE"));
						li_row_tmp = i;
					}
					if ((parseFloat(ldc_tmp) - parseFloat(ldc_fkje)) != 0) {
						r.set("BCJE", parseFloat(r.get("BCJE"))
										- parseFloat(ldc_tmp)
										+ parseFloat(ldc_fkje));
					}
				} else {
					var count = this.store.getCount();
					for (var i = 0; i < count; i++) {
						var r = this.store.getAt(i);
						r.set("BCJE", 0);
					}
				}
			},
			doPayment : function() {
				var count = this.store.getCount();
				var sum_bcfk = 0;
				this.YK_FK01 = {};
				this.YK_FKJL = [];
				for (var i = 0; i < count; i++) {
					var r = this.store.getAt(i);
					if (parseFloat(r.get("FKJE")) > 0) {
						if (r.get("BCJE") < 0) {
							Ext.MessageBox.alert('提示', '付款金额不能小于零,请修改!',
									function() {
										this.grid.startEditing(i, 6);
									}, this);
							this.grid.startEditing(i, 6);
							return;
						}
						// alert(parseFloat(r.get("YFJE")) +
						// parseFloat(r.get("BCJE")))
						if ((parseFloat(r.get("YFJE")) + parseFloat(r
								.get("BCJE"))) > parseFloat(r.get("FKJE"))) {
							Ext.MessageBox.alert('提示', '付款金额大于总付款金额,请修改!',
									function() {
										this.grid.startEditing(i, 6);
									}, this);
							return;
						}
					} else if (parseFloat(r.get("FKJE")) < 0) {
						if (r.get("BCJE") > 0) {
							Ext.MessageBox.alert('提示', '付款金额不能大于零,请修改!',
									function() {
										this.grid.startEditing(i, 6);
									}, this);
							this.grid.startEditing(i, 6);
							return;
						}
						// alert(parseFloat(r.get("YFJE")) +
						// parseFloat(r.get("BCJE")))
						if ((parseFloat(r.get("YFJE")) + parseFloat(r
								.get("BCJE"))) < parseFloat(r.get("FKJE"))) {
							Ext.MessageBox.alert('提示', '付款金额大于总付款金额,请修改!',
									function() {
										this.grid.startEditing(i, 6);
									}, this);
							return;
						}
					}
					if (r.get("BCJE") != 0) {  
						sum_bcfk +=parseFloat(r.get("BCJE"))
						this.YK_FKJL.push(r.data);
					}
				}
				if (this.YK_FKJL.length == 0) {
					Ext.MessageBox.alert('提示', '没有可付款单据,不能进行付款处理!');
					return;
				}
				this.YK_FK01.FKJE = sum_bcfk;
				Ext.MessageBox.prompt('输入', '请输入付款凭证号码:',
						this.afterImportCertificate, this);
			},
			afterImportCertificate : function(btn, is_pzhm) {
				if (btn == 'ok') {
					is_pzhm = is_pzhm.replace(/[ ]/g, "");
					if (is_pzhm == -1 || is_pzhm == '' || is_pzhm == null) {
						Ext.MessageBox.alert('提示', '凭证号码必须输入!', function() {
									Ext.MessageBox.prompt('输入', '请输入付款凭证号码:',
											this.afterImportCertificate, this);
								}, this);
						return;
					}
					if (/.*[\u4e00-\u9fa5]+.*$/.test(is_pzhm)) {
						Ext.MessageBox.alert('提示', '凭证号码不能为中文!');
						return
					}
					if (is_pzhm.length > 10) {
						Ext.MessageBox.alert('提示', '凭证号码最大长度不能超过10位!');
						return;
					}
					this.YK_FK01.PZHM = is_pzhm
					phis.script.rmi.jsonRequest({
								serviceId : "storehouseManageService",
								serviceAction : "updatePaymentProcessing",
								body : {
									YK_FK01 : this.YK_FK01,
									YK_FKJL : this.YK_FKJL
								}
							}, function(code, msg, json) {
								if (code > 300) {
									this.processReturnMsg(code, msg,
											this.doPayment, this);
									return;
								} else {
									Ext.MessageBox.alert('提示', '付款成功!',
											function() {
												this.radiogroup.setValue(1);
												this.requestData.pageNo = 1;
												this.loadData();
											}, this);
									this.fireEvent("paymentSuccessful", this)
								}
							}, this);
				}
			},
			doPrint : function() {
				var ids = [];
				var ysdh = 0;
				var rkfs = 0;
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					ids.push(r.get("YPXH"))
				}
				ysdh = this.requestData.body.YSDH
				rkfs = this.requestData.body.RKFS
			 	 var pWin = this.midiModules["StorehousePaymentProcessingPrintView"]
				var cfg = {
					requestData : ids,
					ysdh : ysdh,
					rkfs : rkfs
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
			  pWin = new phis.application.sto.script.StorehousePaymentProcessingPrintView(cfg)
			 	 this.midiModules["StorehousePaymentProcessingPrintView"] = pWin
				pWin.getWin().show()
			}
		});