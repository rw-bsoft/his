$package("phis.application.sup.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")
phis.application.sup.script.RepairRequestrInfoList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = this.modal = true;
	cfg.showBtnOnLevel = true;
	phis.application.sup.script.RepairRequestrInfoList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext
		.extend(
				phis.application.sup.script.RepairRequestrInfoList,
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
						if (this.mainApp.roleType == "group_12") {
							if (this.mainApp['phis'].treasuryEjkf == 0) {
								Ext.MessageBox.alert("提示", "该库房不是二级库房!");
								return;
							}
						} else {
							if (this.mainApp['phis'].treasuryEjkf != 0) {
								Ext.MessageBox.alert("提示", "该库房不是一级库房!");
								return;
							}
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
						var dat = new Date().format('Y-m-d');
						var dateFromValue = dat.substring(0, dat
								.lastIndexOf("-"))
								+ "-01";
						this.requestData.serviceId = "phis.repairRequestrService";
						this.requestData.serviceAction = "queryWXDJINFO";
						var body = {};
						body["WXZT"] = 3;
						body["formDate"] = dateFromValue;
						body["endDate"] = dat;
						this.requestData.cnd = body;
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
							value : new Date(),
							width : 150,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
						this.textlable = new Ext.form.Label({
							text : "维修编号: "
						});
						this.textfiled=new Ext.form.TextField({
						     id:'wxbh'
						    });
						return [ datelable, this.dateFrom, tolable, this.dateTo,this.textlable,this.textfiled ];
					},
					doRefreshWin : function() {
						this.clear();
						var startDate = "";// 开始时间
						var endDate = ""; // 结束时间
						if (this.dateFrom) {
							startDate = new Date(this.dateFrom.getValue())
									.format("Y-m-d");
						}
						if (this.dateTo) {
							endDate = new Date(this.dateTo.getValue())
									.format("Y-m-d");
						}
						var wxbh=this.textfiled.getValue();
						this.clear();
						this.requestData.serviceId = "phis.repairRequestrService";
						this.requestData.serviceAction = "queryWXDJINFO";
						var body = {};
						body["WXZT"] = 3;
						body["WXBH"] = wxbh;
						body["formDate"] = startDate;
						body["endDate"] = endDate;
						this.requestData.cnd = body;
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
					onDblClick:function(){
						var r = this.getSelectedRecord();
						this.repairRequestrYSFormcx = this.createModule(
								"repairRequestrYSFormcx", this.refYs);
						this.repairRequestrYSFormcx.on("save", this.onSave, this);
						this.repairRequestrYSFormcx.on("refresh", this.doRefresh,
								this);
						this.repairRequestrYSFormcx.WXZT = r.get("WXZT")
						this.repairRequestrYSFormcx.WXXH = r.get("WXXH");
						this.repairRequestrYSFormcx.SYKS = r.get("SYKS");
						this.repairRequestrYSFormcx.SYKS_text = r
								.get("SYKS_text");
						this.repairRequestrYSFormcx.JJCD = r.get("JJCD");
						this.repairRequestrYSFormcx.JJCD_text = r
								.get("JJCD_text");
						this.repairRequestrYSFormcx.JSCD = r.get("JSCD");
						this.repairRequestrYSFormcx.JSCD_text = r
								.get("JSCD_text");
						this.repairRequestrYSFormcx.MYCD = r.get("MYCD");
						this.repairRequestrYSFormcx.MYCD_text = r
								.get("MYCD_text");
						this.repairRequestrYSFormcx.KFXH = r.get("KFXH");
						this.repairRequestrYSFormcx.KFXH_text = r
								.get("KFXH_text");
						this.repairRequestrYSFormcx.SQGH = r.get("SQGH");
						this.repairRequestrYSFormcx.SQGH_text = r
								.get("SQGH_text");
						this.repairRequestrYSFormcx.LXDH = r.get("LXDH");
						this.repairRequestrYSFormcx.SXRQ = r.get("SXRQ");
						this.repairRequestrYSFormcx.BZXX = r.get("BZXX");
						this.repairRequestrYSFormcx.GZMS = r.get("GZMS");
						this.repairRequestrYSFormcx.initPanel();
						var win = this.repairRequestrYSFormcx.getWin();
						win.add(this.repairRequestrYSFormcx.initPanel());
						win.show();
						win.center();
						this.repairRequestrYSFormcx.doYS();
						}
				})