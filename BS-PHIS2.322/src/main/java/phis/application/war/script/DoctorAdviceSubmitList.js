/**
 * @include "../../desktop/Module.js"
 * @include "../common.js"
 * @include "../../../util/Accredit.js"
 * 
 */
$package("phis.application.war.script")

$import("phis.script.SimpleList", "util.dictionary.SimpleDicFactory",
		"phis.prints.script.DoctorAdviceSubmitPrintView")

phis.application.war.script.DoctorAdviceSubmitList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.group = "BRCH";
	cfg.groupTextTpl = "<table width='45%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='20%'>&nbsp;&nbsp;<b>床号:{[values.rs[0].data.BRCH]}</b></td><td width='24%'><b>姓名:{[values.rs[0].data.BRXM]}</b></td><td width='20%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	phis.application.war.script.DoctorAdviceSubmitList.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
	this.on("hide", this.onWinClose, this);// add by yangl 关闭时抛出事件
	this.fyfs = 0;
	this.lsyz = 2;
	this.yfsb = 0;
}
Ext.extend(phis.application.war.script.DoctorAdviceSubmitList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				if (this.grid) {
					if (!this.isCombined) {
						this.fireEvent("beforeAddToWin", this.grid)
						this.addPanelToWin();
					}
					return this.grid;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				this.isCompositeKey = schema.isCompositeKey;
				var items = schema.items
				if (!items) {
					return;
				}

				this.store = this.getStore(items)
				if (this.mutiSelect) {
					this.sm = new Ext.grid.CheckboxSelectionModel()
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				if (!this.array) {
					this.array = [];
				}
				var cfg = {
					stripeRows : true,
					border : false,
					store : this.store,
					cm : this.cm,
					sm : this.sm,
					height : this.height,
					loadMask : {
						msg : '正在加载数据...',
						msgCls : 'x-mask-loading'
					},
					buttonAlign : 'center',
					clicksToEdit : true,
					frame : true,
					plugins : this.array.length == 0 ? null : this.array,// modife
					// by
					// taoy
					// /*this.rowExpander
					// stripeRows : true,
					viewConfig : {
						// forceFit : true,
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
					}
				}
				if (this.group)
					cfg.view = new Ext.grid.GroupingView({
								// forceFit : true,
								showGroupName : true,
								enableNoGroups : false,
								hideGroupedColumn : true,
								enableGroupingMenu : false,
								columnsText : "表格字段",
								groupByText : "使用当前字段进行分组",
								showGroupsText : "表格分组",
								groupTextTpl : this.groupTextTpl
							});
				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
				}
				if (this.summaryable) {
					$import("org.ext.ux.grid.GridSummary");
					var summary = new org.ext.ux.grid.GridSummary();
					cfg.plugins = [summary]
					this.summary = summary;
				}
				var cndbars = this.getCndBar(items)
				if (!this.disablePagingTbr) {
					cfg.bbar = this.getPagingToolbar(this.store)
				} else {
					cfg.bbar = this.bbar
				}
				if (!this.showButtonOnPT) {
					if (this.showButtonOnTop) {
						cfg.tbar = (cndbars.concat(this.tbar || []))
								.concat(this.createButtons())
					} else {
						cfg.tbar = cndbars.concat(this.tbar || [])
						cfg.buttons = this.createButtons()
					}
				}

				if (this.disableBar) {
					delete cfg.tbar;
					delete cfg.bbar;
					cfg.autoHeight = true;
					cfg.frame = false;
				}
				this.expansion(cfg);// add by yangl
				this.grid = new this.gridCreator(cfg)
				this.schema = schema;
				this.grid.on("afterrender", this.onReady, this)
				this.grid.on("contextmenu", function(e) {
							e.stopEvent()
						})
				this.grid.on("rowcontextmenu", this.onContextMenu, this)
				this.grid.on("rowdblclick", this.onDblClick, this)
				this.grid.on("rowclick", this.onRowClick, this)
				this.grid.on("keydown", function(e) {
							if (e.getKey() == e.PAGEDOWN) {
								e.stopEvent()
								this.pagingToolbar.nextPage()
								return
							}
							if (e.getKey() == e.PAGEUP) {
								e.stopEvent()
								this.pagingToolbar.prevPage()
								return
							}
							if (e.getKey() == e.ESC) {
								if (this.onESCKey) {
									this.onESCKey();
								}
								return
							}
						}, this)
				// add by yangl tpl
				// this.grid.on('render', function(grid) {
				// var store = grid.getStore(); // Capture the Store.
				//			 
				// var view = grid.getView();
				// grid.tip = new Ext.ToolTip({
				// target: view.mainBody,
				// title :"详细信息",
				// delegate: '.x-grid3-row',
				// dismissDelay : 0,//延迟多少秒后自动关闭,0不关闭
				// trackMouse: true,
				// renderTo: document.body,
				// listeners: {
				// beforeshow: function updateTipBody(tip) {
				// var rowIndex = view.findRowIndex(tip.triggerElement);
				// var str = "<h2>当前显示第"+store.getAt(rowIndex).id+"行信息！<h2>";
				// str += "<h2>当前科室为："+store.getAt(rowIndex).data.KSMC+"<h2>";
				// var url = document.URL;
				// url = url +"resources/css/app/desktop/images/icons/AB1.gif";
				// str += "可以显示一些小的图片信息：<img src='"+url+"' />";
				// tip.body.dom.innerHTML = "<div style='padding:10px;1px solid
				// #999;
				// color:#555;background:#f9f9f9;'>" + str+"</div>";
				// }
				// }
				// });
				// });
				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				return this.grid
			},
			onWinShow : function() {
				if (this.grid) {
					if (this.dateField) {
						if(!this.dateField.getValue()){
							this.dateField.setValue(this.mainApp.serverDate);
						}
					}
					if (this.fyyfdc) {
						this.fyyfdc.setValue("0");
					}
					if (this.lyfsDc) {
						this.lyfsDc.setValue("0");
					}
					if (this.yz_radiogrup) {
						this.yz_radiogrup.setValue("1");
					}
					this.doRefresh();
				}
			},
			expansion : function(cfg) {
				// 顶部工具栏
				var label = new Ext.form.Label({
							text : "领药日期至"
						});
				this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
				var tbar = cfg.tbar;
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push([label, '-', this.dateField, tbar]);

				// 底部工具栏
				var lysfLabel = new Ext.form.Label({
							text : "领药方式"
						});
				var lyfsDic = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.hairMedicineWay",
							width : 150,
							emptyText : "全部"
						});
				this.lyfsDc = lyfsDic;
				lyfsDic.on("select", this.onFsSelect, this);
				lyfsDic.store.on("load", this.fsDicLoad)
				var yz_radiogroup = new Ext.form.RadioGroup({
							width : 400,
							style : "padding-left : 30px",
							items : [{
										boxLabel : '全部医嘱',
										inputValue : 1,
										name : "yzlx",
										checked : true,
										clearCls : true
									}, {
										boxLabel : '长期医嘱',
										name : "yzlx",
										inputValue : 2,
										clearCls : true
									}, {
										boxLabel : '临时医嘱',
										name : "yzlx",
										inputValue : 3,
										clearCls : true
									}, {
										boxLabel : '急诊用药',
										name : "yzlx",
										inputValue : 4,
										clearCls : true
									}, {
										boxLabel : '出院带药',
										name : "yzlx",
										inputValue : 5,
										clearCls : true
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									if (newValue.inputValue == 1) {
										this.lsyz = 2;
										this.doRefresh();
									} else if (newValue.inputValue == 2) {
										this.lsyz = 0;
										this.doRefresh();
									} else if (newValue.inputValue == 3){
										this.lsyz = 1;
										this.doRefresh();
									}else if (newValue.inputValue == 4){
										this.lsyz = 3;
										this.doRefresh();
									}else if (newValue.inputValue == 5){
										this.lsyz = 4;
										this.doRefresh();
									}
								},
								scope : this
							}
						});
				this.yz_radiogrup = yz_radiogroup;
				var fyyfLabel = new Ext.form.Label({
							text : "发药药房  "
						});
				var fyyfDic = util.dictionary.SimpleDicFactory.createDic({
					id : "phis.dictionary.pharmacy_bq",
					width : 150,
					filter : "['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]",
					emptyText : "全部药房"
				});
				this.fyyfdc = fyyfDic;
				fyyfDic.on("select", this.onYfSelect, this);
				fyyfDic.store.on("load", this.yfDicLoad)
				cfg.bbar = [];
				cfg.bbar.push([lysfLabel, '-', lyfsDic, yz_radiogroup,
						fyyfLabel, '-', fyyfDic])
			},
			yfDicLoad : function() {
				var data = {
					"key" : "0",
					"text" : "全部药房"
				};
				var r = new Ext.data.Record(data);
				this.insert(0, r);
			},
			fsDicLoad : function() {
				var data = {
					"key" : "0",
					"text" : "全部"
				};
				var r = new Ext.data.Record(data);
				this.insert(0, r);
			},
			onYfSelect : function(fyyfDic) {
				fyyfDic.emptyText = "";
				this.yfsb = fyyfDic.value;
				this.doRefresh();
			},
			onFsSelect : function(fyfsDic) {
				fyfsDic.emptyText = "";
				this.fyfs = fyfsDic.value;
				this.doRefresh();
			},
			loadData : function() {
				this.doRefresh();
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
			doRefresh : function() {
				this.clear();
				var ldt_lyrq = "";// 领用日期
				if (this.dateField) {
					ldt_lyrq = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (ldt_lyrq < new Date().format('Y-m-d')) {
					MyMessageTip.msg("提示", "领药日期不能小于当前日期!", true);
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					return;
				}
				var al_zyh = "0";// 病人住院号
				if (this.initDataId) {
					al_zyh = this.initDataId;
				} else {
					var btns = this.grid.getTopToolbar();
					var btn = btns.find("cmd", "close");
					btn[0].hide();
				}
				// 预领日期控制
				var data = {
					"LYRQ" : ldt_lyrq
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceQuery,
							body : data
						});
				if (r.code == 600) {
					MyMessageTip.msg("提示", "提交天数超过最大天数控制!", true);
					return;
				}
				// 检索领药明细单
				this.requestData.serviceId = "phis.doctorAdviceSubmitQueryService";
				this.requestData.serviceAction = "getDoctorAdviceSubmitQuery";
				this.requestData.cnd = al_zyh + "#" + ldt_lyrq + "#"
						+ this.fyfs + "#" + this.lsyz + "#" + this.yfsb;
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
			doConfirm : function() {
				var ldt_lyrq = "";// 领用日期
				if (this.dateField) {
					ldt_lyrq = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (ldt_lyrq < new Date().format('Y-m-d')) {
					MyMessageTip.msg("提示", "领药日期不能小于当前日期!", true);
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					return;
				}
				var al_zyh = "0";// 病人住院号
				if (this.initDataId) {
					al_zyh = this.initDataId;
				}
				if (this.grid.getStore().getCount() == 0)
					return;
				Ext.Msg.confirm("请确认", "药品医嘱提交药房?", function(btn) {
					if (btn == 'yes') {
						var data = {
							"ZYH" : al_zyh,
							"LYRQ" : ldt_lyrq,
							"FYFS" : this.fyfs,
							"LSYZ" : this.lsyz,
							"YFSB" : this.yfsb
						};
						phis.script.rmi.jsonRequest({
									serviceId : this.serviceId,
									serviceAction : this.serviceActionSave,
									body : data
								}, function(code, msg, json) {
									this.grid.el.unmask()
									if (code >= 300) {
										this.processReturnMsg(code, msg);
										return;
									}
									if (json.RES_MESSAGE) {
										MyMessageTip.msg("提示",
												json.RES_MESSAGE, true);
									}
									// add by yangl 增加抗菌药物提醒
									if (json.warnMsg) {
										var s = "";
										for (var i = 0; i < json.warnMsg.length; i++) {
											s += json.warnMsg[i];
											if (i < json.warnMsg.length - 1) {
												s += "<br>";
											}
										}
										Ext.Msg.alert("警告", s);
									}
									this.doRefresh(this.fyfs, this.lsyz,
											this.yfsb);
									if (this.needToClose) {
										this.opener.win.hide();
										this.needToClose = false;
									}
									// this.fireEvent("doSave",
									// json.body);
								}, this)
					}
				}, this);
				return;
			},
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			onWinClose : function() {
				this.fireEvent("doSave");
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.getYzzh();
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
				var al_zyh = "0";// 病人住院号
				if (this.initDataId) {
					al_zyh = this.initDataId;
				}
				var ldt_lyrq = "";// 领用日期
				if (this.dateField) {
					ldt_lyrq = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (ldt_lyrq < new Date().format('Y-m-d')) {
					MyMessageTip.msg("提示", "领药日期不能小于当前日期!", true);
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					return;
				}
				// 预领日期控制
				var data = {
					"LYRQ" : ldt_lyrq
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceQuery,
							body : data
						});
				if (r.code == 600) {
					MyMessageTip.msg("提示", "提交天数超过最大天数控制!", true);
					return;
				}
				var pWin = this.midiModules["DoctorAdviceSubmitPrintView"]
				var cfg = {
					al_zyh : al_zyh,
					ldt_lyrq : ldt_lyrq,
					fyfs : this.fyfs,
					lsyz : this.lsyz,
					yfsb : this.yfsb
				}
				if (pWin) {
					Ext.apply(pWin, cfg)
					pWin.getWin().show()
					return
				}
				pWin = new phis.prints.script.DoctorAdviceSubmitPrintView(cfg)
				this.midiModules["DoctorAdviceSubmitPrintView"] = pWin
				pWin.getWin().show()

			}
		});
