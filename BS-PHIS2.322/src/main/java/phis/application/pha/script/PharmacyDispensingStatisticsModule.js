/**
 * 药房发药统计模块 该模块中包含两个列表，分为左右两个 左边列表为统计方式列表 右边列表为统计明细列表
 * 
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyDispensingStatisticsModule = function(cfg) {
	this.exContext = {};
	phis.application.pha.script.PharmacyDispensingStatisticsModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.pha.script.PharmacyDispensingStatisticsModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'west',
										width : 450,
										items : this.getLeftList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getRightList()
									}],
							tbar : this.getTbar()
						});
				this.panel = panel;
				return panel;
			},
			/**
			 * 左边列表
			 * 
			 * @return {}
			 */
			getLeftList : function() {
				this.refLeftList = this.createModule("refLeftList",
						this.refLeftList);
				this.refLeftList.opener = this;
				return this.refLeftList.initPanel();
			},
			/**
			 * 右边列表
			 * 
			 * @return {}
			 */
			getRightList : function() {
				this.refRightList = this.createModule("refRightList",
						this.refRightList);
				this.refRightList.on("showRecord", this.doShowDetails, this);
				this.refRightList.on("showDetail", this.doShowDetailsDetail,
						this);
				return this.refRightList.initPanel();
			},
			doShowDetailsDetail : function() {
				var record = this.refRightList.getSelectedRecord();
				if (!record) {
					return;
				}
				var parameter = this.getQueryPar();
				parameter.VIRTUAL_FIELD=this.VIRTUAL_FIELD;
				parameter.YPXH = record.data.YPXH;
				this.ddlist = this.createModule("ddlist", this.refddlist);
				this.ddlist.on("winClose", this.onClose, this);
				var win = this.ddlist.getWin();
				win.add(this.ddlist.initPanel());
				win.show()
				win.center();
				this.ddlist.requestData.body=parameter;
				this.ddlist.refresh();
			},
			/**
			 * 双击显示明细
			 */
			doShowDetails : function() {
				var record = this.refLeftList.getSelectedRecord()
				if (!record) {
					return;
				}
				var pydm = this.refRightList.cndField.getValue().toUpperCase();
				var parameter = this.getQueryPar();
				if (parameter == null) {
					return;
				}
				parameter.VIRTUAL_FIELD = record.data.VIRTUAL_FIELD;
				this.VIRTUAL_FIELD=record.data.VIRTUAL_FIELD;
				if (pydm && pydm != null && pydm != "" && pydm != undefined) {
					parameter.cnd1 = ['and', parameter.cnd1,
							['like', ['$', 'c.PYDM'], ['s', pydm + "%"]]]
					parameter.cnd2 = ['and', parameter.cnd2,
							['like', ['$', 'b.PYDM'], ['s', pydm + "%"]]]
					// parameter.PYDM = pydm;
				}
				this.refRightList.requestData.body = parameter;
				this.refRightList.refresh();
			},

			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var yplxStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '全部'], [1, '西药'], [2, '中成药'], [3, '草药']]
						});

				var yplxCombox = new Ext.form.ComboBox({
							store : yplxStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 100,
							value : 0
						});
						//[6, '按开单医生']
				var tjfsStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[1, '按发药窗口'], [2, '按病人性质'], [3, '按特殊药品'],
									[4, '按开单科室'], [5, '发药人'],[6, '按开单医生'],[7, '按基本药物（全院）'],[8, '按基本药物（医生）']]
						});
				var tjfsCombox = new Ext.form.ComboBox({
							store : tjfsStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 130,
							value : 1
						});
				var fylbstore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '全部'], [1, '门诊'], [2, '住院'], [3, '家床']]
						});
				var fylb = new Ext.form.ComboBox({
							store : fylbstore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 0
						});
				var simple = new Ext.FormPanel({
							labelWidth : 50, // label settings here cascade
							title : '',
							layout : "table",
							bodyStyle : 'padding:5px 5px 5px 5px',
							defaults : {},
							defaultType : 'textfield',
							items : [{
										xtype : "label",
										forId : "window",
										text : "发药时间 "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '发药时间开始',
												name : 'dateFrom',
												value : new Date()
														.format('Y-m-d 00:00:00'),
												strategy : {
													xtype : "dateTime"
												},
												width : 150
											}), {
										xtype : "label",
										forId : "window",
										text : "-"
									}, new Ext.ux.form.Spinner({
												fieldLabel : '发药时间结束',
												name : 'dateTo',
												value : new Date()
														.format('Y-m-d 23:59:59'),
												strategy : {
													xtype : "dateTime"
												},
												width : 150
											}), {
										xtype : "label",
										forId : "window",
										text : "药品类型"
									}, yplxCombox, {
										xtype : "label",
										forId : "window",
										text : "统计方式"
									}, tjfsCombox, {
										xtype : "label",
										forId : "window",
										text : "发药类别"
									}, fylb]
						});
				this.simple = simple;
				this.fylx = yplxCombox;
				this.tjfs = tjfsCombox;
				this.fylb = fylb
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			/**
			 * 统计按钮
			 */
			doTj : function() {
				var parameter = this.getQueryPar();
				if (parameter == null) {
					return;
				}
				this.refRightList.clear();
				this.refLeftList.requestData.body = parameter;
				this.refLeftList.refresh();
				this.doShowDetails();
			},
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.close();
			},
			getQueryPar : function() {
				var fylx = this.fylx.getValue();// 获取当前药品类型下拉框中的值
				var tjfs = this.tjfs.getValue();// 获取当前统计方式下拉框中的值
				var datefrom = this.simple.items.get(1).getValue();// 开始时间
				var dateTo = this.simple.items.get(3).getValue();// 结束时间
				var fylb = this.fylb.getValue();// 发药类别,门诊或住院
				// 判断是否选择发药类型
				if (!fylx && fylx != 0) {
					MyMessageTip.msg("提示", "请选择发药类型", true);
					return null;
				}
				// 判断是否选择统计方式
				if (!tjfs) {
					MyMessageTip.msg("提示", "请选择统计方式", true);
					return null;
				}
				if (!fylb && fylb != 0) {
					MyMessageTip.msg("提示", "请选择发药类别", true);
					return null;
				}
				if (datefrom == null || datefrom == "" || datefrom == undefined
						|| dateTo == null || dateTo == ""
						|| dateTo == undefined) {
					MyMessageTip.msg("提示", "请选择正确的时间范围", true);
					return null;
				}
				if (fylb == 0 || fylb == 2||fylb == 3) {
					if (tjfs == 1) {
						if(fylb == 3){
						MyMessageTip.msg("提示", "家床不能按发药窗口统计", true);
						}else{
						MyMessageTip.msg("提示", "住院不能按发药窗口统计", true);
						}
						return null;
					}
					if (tjfs == 4) {
						if(fylb == 3){
						MyMessageTip.msg("提示", "家床不能按开单科室统计", true);
						}else{
						MyMessageTip.msg("提示", "住院不能按开单科室统计", true);
						}
						return null;
					}
					if (tjfs == 6) {
						if(fylb == 3){
						MyMessageTip.msg("提示", "家床不能按开单医生统计", true);
						}else{
						MyMessageTip.msg("提示", "住院不能按开单医生统计", true);
						}
						return null;
					}
				}
				var cnd1 = ['ne',['$','b.ZFYP'],['i',1]];
				var cnd2 = null;
				if (fylx != 0) {
					cnd1 =['and', cnd1, ['eq', ['$', 'a.CFLX'], ['i', fylx]]] 
					cnd2 = ['eq', ['$', 'a.YPLX'], ['i', fylx]]
				}
				var cnd_time1 = [
						'and',
						['ge',
							['$', "to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', datefrom]],
						['le',
							['$', "to_char(a.FYRQ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', dateTo]]]
				var cnd_time2 = [
						'and',
						['ge',
							['$', "to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', datefrom]],
						['le',
							['$', "to_char(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', dateTo]]]
				if (cnd1 != null) {
					cnd1 = ['and', cnd1, cnd_time1];
				} else {
					cnd1 = cnd_time1;
				}
				if (cnd2 != null) {
					cnd2 = ['and', cnd2, cnd_time2];
				} else {
					cnd2 = cnd_time2;
				}
				var parameter = {};
				parameter.TJFS = tjfs;
				parameter.cnd1 = cnd1;
				parameter.cnd2 = cnd2;
				parameter.FYLB = fylb;
				return parameter;
			},
			doDy1 : function() {
				var datefrom = this.simple.items.get(1).getValue();// 开始时间
				var dateTo = this.simple.items.get(3).getValue();// 结束时间
				var fylx = this.fylx.getValue();// 获取当前药品类型下拉框中的值
				var yfsb = this.mainApp['phis'].pharmacyId;
				var parameter = this.getQueryPar();
				if (parameter == null) {
					return;
				}
				var url = "";
				var pages = "phis.prints.jrxml.PharmacySendMedicines";
				var url = "resources/" + pages + ".print?silentPrint=" + 0
						+ "&execJs=" + this.getExecJs();
				url += "&YPLX=" + fylx + "&TJFS=" + parameter.TJFS + "&KSSJ=" + encodeURI(encodeURI(datefrom))
						+ "&JSSJ=" + encodeURI(encodeURI(dateTo)) + "&YFSB=" + yfsb+"&FYLB="+parameter.FYLB;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览
				LODOP.PREVIEW();
			},
			doDy2 : function() {
				var record = this.refLeftList.getSelectedRecord()
				if (!record) {
					return;
				}
				var pydm = this.refRightList.cndField.getValue().toUpperCase();
				var parameter = this.getQueryPar();
				if (parameter == null) {
					return;
				}
				var datefrom = this.simple.items.get(1).getValue();// 开始时间
				var dateTo = this.simple.items.get(3).getValue();// 结束时间
				var fylx = this.fylx.getValue();// 获取当前药品类型下拉框中的值
				var url = "";
				var pages = "phis.prints.jrxml.PharmacySendMedicinesDetails";
				var url = "resources/" + pages + ".print?silentPrint=" + 0
						+ "&execJs=" + this.getExecJs();
				url += "&YPLX=" + fylx + "&TJFS=" + parameter.TJFS + "&FYLB=" + parameter.FYLB
						+ "&virtualField=" + record.data.VIRTUAL_FIELD+ "&KSSJ=" + encodeURI(encodeURI(datefrom))
						+ "&JSSJ=" + encodeURI(encodeURI(dateTo));
				if (pydm && pydm != null && pydm != "" && pydm != undefined) {
				url +="&PYDM="+pydm;
				}
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.PREVIEW();
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('A4');"
			}
		})