$package("phis.application.pha.script")
$import("phis.script.SimpleList", "util.dictionary.TreeDicFactory",
		"org.ext.ux.ColumnHeaderGroup")

phis.application.pha.script.PharmacyMedicinesBalanceSummaryList = function(cfg) {
	phis.application.pha.script.PharmacyMedicinesBalanceSummaryList.superclass.constructor
			.apply(this, [cfg]);
	this.selectFirst = false;
	this.summaryable = false;
	this.searchFormWidth = 700;
	this.searchFormHeight = 20;
	this.searchFormId = 'phis_mds_pharmacy_medicines_balanceList';
	this.bodyStyle = 'border:0px';
}

Ext.extend(phis.application.pha.script.PharmacyMedicinesBalanceSummaryList,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 初始化判断
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.initializationServiceAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				var dates = ret.json.body;
				if (dates.length != 3) {
					Ext.Msg.alert('提示', dates[dates.length - 1]);
					return null;
				}
				if (this.grid) {
					return this.grid;
				}
				var schema = sc;
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				var items = schema.items;
				if (!items) {
					return;
				}

				this.store = this.getStore(items)
				if (this.mutiSelect) {
					this.sm = new Ext.grid.CheckboxSelectionModel()
				}
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var group = new Ext.ux.grid.ColumnHeaderGroup({
							rows : [[{
										header : "药品基本信息",
										colspan : 5,
										align : 'center'
									}, {
										header : "期初结存",
										colspan : 2,
										align : 'center'
									}, {
										header : "本期入库",
										colspan : 2,
										align : 'center'
									}, {
										header : "本期出库",
										colspan : 2,
										align : 'center'
									}, {
										header : "期末结存",
										colspan : 2,
										align : 'center'
									}, {
										header : "库存信息",
										colspan : 4,
										align : 'center'
									}]]
						});

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
					// plugins : (this.array.length == 0 ? null : this.array),
					viewConfig : {
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
					}
				}
				cfg.plugins = [];
				if (this.headPlug) {
					cfg.plugins[cfg.plugins.length] = group;
				}
				if (this.group)
					cfg.view = new Ext.grid.GroupingView({
								forceFit : true,
								groupTextTpl : '{text} ({[values.rs.length]} 条记录)'
							});
				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
				}
				if (this.summaryable) {
					$import("org.ext.ux.GridSummary");
					var summary = new org.ext.ux.grid.GridSummary();
					cfg.plugins[cfg.plugins.length] = summary
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
						}, this)
				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				return this.grid;
			},
			// 对账库存数量有问题的突出显示
			getRowClass : function(record, rowIndex, rowParams, store) {
				// QCSL=QMSL+RKSL-CKSL //QCJE=QCJE+RKJR-CKJR
				if (record.get('QCSL') != record.get('QMSL')
						+ record.get('CKSL') - record.get('RKSL')) {
					return 'x-grid-record-red';
				}
				// if (record.get('QMSL') != record.get('YPSL') ||
				// record.get('QMJE') != record.get('LSJE')) {
				// return 'x-grid-record-red';
				// }
				return ""
			},
			totalHJJE : function(v, params, data) {
				return v == null
						? '0'
						: ('<span style="font-size:12px;color:black;">总金额:&#160;'
								+ parseFloat(v).toFixed(4) + '</span>');
			},
			totalHJSL : function(v, params, data) {
				return v == null
						? '0'
						: ('<span style="font-size:12px;color:black;">总数量:&#160;'
								+ parseFloat(v).toFixed(2) + '</span>');
			},
			getCndBar : function(items) {
				var simple = new Ext.form.FormPanel({
							id : this.searchFormId,
							frame : false,
							layout : 'column',
							bodyStyle : this.bodyStyle,
							width : this.searchFormWidth,
							height : this.searchFormHeight,
							items : this.initConditionFields()
						});
				this.simple = simple;
				return [simple];
			},
			// 生成查询框
			initConditionFields : function(items) {
				var items = [];
				var ypdmCombox = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.prescriptionType",
							// autoLoad : true,
							editable : false,
							width : 130
						});
				this.ypdmCombox = ypdmCombox;
				ypdmCombox.fieldLabel = '药品类型';
				ypdmCombox.columnWidth = .3;
				ypdmCombox.name = 'medicinesCode';
				ypdmCombox.on("select", this.onLeafClick, this);
				ypdmCombox.store.on("load", this.ypdmLoad, this);
				ypdmCombox.store.load()
				var conditionField = [];
				conditionField[conditionField.length] = ypdmCombox;
				conditionField[conditionField.length] = {
					fieldLabel : '拼音码',
					name : 'pydm',
					regex : /^[A-Za-z0-9]+$/
				}
				conditionField[conditionField.length] = {
					xtype : 'uxspinner',
					fieldLabel : '财务月份',
					name : 'storeDate',
					value : this.getDate(),
					strategy : {
						xtype : 'month'
					},
					columnWidth : .4
				}
				conditionField[conditionField.length] = {
					xtype : 'hidden',
					name : 'type'
				}
				for (var i = 0; i < conditionField.length; i++) {
					var config = {};
					Ext.apply(config, conditionField[i]);
					config.xtype = config.xtype || 'textfield', config.anchor = config.anchor
							|| '95%';
					items[i] = {
						layout : 'form',
						labelWidth : conditionField[i].labelWidth || 60,
						bodyStyle : this.bodyStyle,
						columnWidth : conditionField[i].columnWidth || .3,
						items : [config]
					};
				}
				return items;
			},
			getDate : function() {
				var short_date = new Date();
				return short_date.format('Y-m');
			},
			loadData : function() {
				var params = this.getQueryParams();
				if (!Ext.isEmpty(params, false)) {
					this.requestData.body = params;
					this.requestData.serviceId = this.fullserviceId;
					this.requestData.serviceAction = this.serviceAction;
					phis.application.pha.script.PharmacyMedicinesBalanceSummaryList.superclass.loadData
							.call(this);
				}
			},
			// 没有参数时不能刷新
			onStoreBeforeLoad : function() {
				if (!this.params) {
					return false;
				}
			},
			getQueryParams : function() {
				var simple = this.simple;
				var params = {};
				if (simple) {
					var formVal = simple.form.getValues();
					var stroeDate = formVal.storeDate;
					if (stroeDate != null && stroeDate != "") {
						var body = {};
						body["date"] = stroeDate;
						var r = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.dateQueryActionId,
									body : body
								});
						if (r.code > 300) {
							this.processReturnMsg(r.code, r.msg,
									this.onBeforeSave);
							return;
						} else {
							var dates = r.json.body;
							if (dates.length != 3) {
								Ext.Msg.alert('提示', dates[dates.length - 1]);
								return null;
							}
							params.date_begin = dates[2];
							params.cwyf = dates[1];
							params.date_end = dates[0];
							// 门诊和住院的返回时间刚好相反 判断互换下
							if (params.date_begin > params.date_end) {
								var dateChang = params.date_begin;
								params.date_begin = params.date_end;
								params.date_end = dateChang;
							}
							Ext.apply(params, formVal);
							if (!Ext.isEmpty(params.pydm)) {
								params.pydm = '%' + params.pydm + '%';
							}
						}
					}
				}
				// 没选财务月份不让查询
				if (params.date_begin == null || params.date_end == null) {
					return;
				}
				this.params = params;
				return params;
			},
			ypdmLoad : function(store) {
				var r = new Ext.data.Record({
							key : "-1",
							text : "全部"
						})
				store.insert(0, [r]);
				// this.ypdmCombox.setValue(store.getAt(0).get("key"))//不知道哪里有代码影响了这个默认值的设置
			},
			// 药品类型树点击事件
			onLeafClick : function(f, v) {
				this.simple.form.findField('type').setValue(f.getValue());
			},
			// 刷新
			doQuery : function() {
				this.refresh();
			},
			// 重置
			doReset : function() {
				this.simple.form.reset();
			},
			// 打印
			doPrint : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中要打印的药品记录!", true);
					return;
				}
			},
			onDblClick : function() {
				this.doDetail();
			},
			/*
			 * onStoreLoadData : function(store, records, ops) {
			 * this.fireEvent("loadData", store) if (records.length == 0) {
			 * return } if (!this.selectedIndex || this.selectedIndex >=
			 * records.length) { this.selectRow(0); this.onRowClick(); } else {
			 * this.selectRow(this.selectedIndex); this.selectedIndex = 0;
			 * this.onRowClick(); } },
			 */
			// 查询明细
			doDetail : function() {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请先选中要查看的药品记录!", true);
					return;
				}
				Ext.apply(r.data, this.requestData.body);
				if (!this.module) {
					this.module = this.createModule('detailList',
							this.refDetailList);
				}
				var _win = this.module.getWin();
				_win.add(this.module.initPanel());
				_win.setWidth(720);
				_win.setHeight(510);
				this.module.loadData(r.data);
				_win.show();
				_win.center()
			}
		});