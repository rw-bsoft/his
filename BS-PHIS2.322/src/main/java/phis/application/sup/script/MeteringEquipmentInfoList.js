$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")
phis.application.sup.script.MeteringEquipmentInfoList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.MeteringEquipmentInfoList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.MeteringEquipmentInfoList,
				phis.script.SimpleList,
				{
					initPanel : function(sc) {
						if (!this.mainApp['phis'].treasuryId) {
							Ext.Msg.alert("提示", "未设置登录库房,请先设置");
							return null;
						}
						if (this.mainApp['phis'].treasuryCsbz == "0") {
							Ext.Msg.alert("提示", "该库房未做账册初始化!");
							return null;
						}
						if (this.mainApp['phis'].treasuryEjkf != 0) {
							Ext.MessageBox.alert("提示", "该库房不是一级库房!");
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
						this.isCompositeKey = schema.compositeKey;
						var items = schema.items
						if (!items) {
							return;
						}
						this.store = this.getStore(items)
						// if (this.mutiSelect) {
						// this.sm = new Ext.grid.CheckboxSelectionModel()
						// }
						this.cm = new Ext.grid.ColumnModel(this.getCM(items))
						var cfg = {
							border : false,
							store : this.store,
							cm : this.cm,
							height : this.height,
							loadMask : {
								msg : '正在加载数据...',
								msgCls : 'x-mask-loading'
							},
							buttonAlign : 'center',
							clicksToEdit : 1,
							frame : true,
							plugins : this.rowExpander,
							viewConfig : {
								// forceFit : true,
								enableRowBody : this.enableRowBody,
								getRowClass : this.getRowClass
							}
						}
						if (this.sm) {
							cfg.sm = this.sm
						}
						if (this.viewConfig) {
							Ext.apply(cfg.viewConfig, this.viewConfig)
						}
						if (this.group) {
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
						}
						if (this.gridDDGroup) {
							cfg.ddGroup = this.gridDDGroup;
							cfg.enableDragDrop = true
						}
						if (this.summaryable) {
							$import("phis.script.ux.GridSummary");
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
						this.expansion(cfg);// add by yangl
						this.grid = new this.gridCreator(cfg)
						// this.grid.getTopToolbar().enableOverflow = true
						this.grid.on("render", this.onReady, this)
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
						return this.grid
					},
					loadData : function() {
						this.clear();
						this.requestData.serviceId = "phis.equipmentWeighingManagementService";
						this.requestData.serviceAction = "queryJlsb";
						this.requestData.datefrom = this.dateFrom.getValue();
						this.requestData.dateto = this.dateTo.getValue();
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
					getCndBar : function(items) {
						var dat = new Date().format('Y-m-d');
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";
						var datelable = new Ext.form.Label({
							text : "单据日期:"
						})
						this.dateFrom = new Ext.form.DateField({
//							id : 'stardate',
							name : 'stardate',
							value : dateFromValue,
							width : 150,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						});
						var tolable = new Ext.form.Label({
							text : " 到 "
						});
						this.dateTo = new Ext.form.DateField({
//							id : 'enddate',
							name : 'enddate',
							value : new Date().format('Y-m-d'),
							width : 150,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
						var sbfllable = new Ext.form.Label({
							text : " 设备分类: "
						});
						var JLQJFLdic = {
							"id" : "phis.dictionary.jlqjfl",
							"src" : "WL_JLSBCX.JLQJFL",
							"width" : 130,
							"emptyText" : '全部'
						};
						var JLQJFLdics = util.dictionary.SimpleDicFactory
								.createDic(JLQJFLdic);
						this.JLQJFLdics = JLQJFLdics;

						var jlfllable = new Ext.form.Label({
							text : " 设备类别: "
						});
						var JLFLdic = {
							"id" : "phis.dictionary.jlfl",
							"src" : "WL_JLSBCX.JLLB",
							"width" : 130,
							"emptyText" : '全部'
						};
						var JLFLdics = util.dictionary.SimpleDicFactory
								.createDic(JLFLdic);
						this.JLFLdics = JLFLdics;

						var ksmclable = new Ext.form.Label({
							// width : 200,
							text : " 科室: "
						});
						// 科室
						var ZYKSdic = {
							"id" : "phis.dictionary.department",
							"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
							"src" : "WL_JLSBCX.KSMC",
							"width" : 130,
							"emptyText" : '全部'
						};
						var ZYKSdics = util.dictionary.SimpleDicFactory
								.createDic(ZYKSdic);
						this.ZYKSdics = ZYKSdics;
						return [ datelable, this.dateFrom, tolable,
								this.dateTo, sbfllable, JLQJFLdics, jlfllable,
								JLFLdics, ksmclable, ZYKSdics ];
					},
					doRefreshWin : function() {
						if (!this.dateFrom.getValue()
								|| !this.dateTo.getValue()) {
							MyMessageTip.msg("提示", "开始日期或者结束日期不能为空", true);
							return;
						}
						this.clear();
						this.requestData.serviceId = "phis.equipmentWeighingManagementService";
						this.requestData.serviceAction = "queryJlsb";
						this.requestData.datefrom = this.dateFrom.getValue();
						this.requestData.dateto = this.dateTo.getValue();
						this.requestData.jlqjfldics = this.JLQJFLdics.value;
						this.requestData.jlfldics = this.JLFLdics.value;
						this.requestData.zyksdics = this.ZYKSdics.value;
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
					}
				})