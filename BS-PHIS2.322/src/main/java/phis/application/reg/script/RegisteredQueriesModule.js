/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.reg.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout", "util.helper.Helper","util.rmi.loadXML","phis.script.widgets.LodopFuncs");
phis.application.reg.script.RegisteredQueriesModule = function(cfg) {
	this.exContext = {};
	this.printurl = util.helper.Helper.getUrl();
	phis.application.reg.script.RegisteredQueriesModule.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext
		.extend(
				phis.application.reg.script.RegisteredQueriesModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (this.panel) {
							return this.panel;
						}
						var form = this.createForm();
						var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'north',
								height : 68,
								items : form
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								items : this.getList()
							} ],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});
						this.panel = panel;
						// this.query = false;
						return panel;
					},
					createForm : function() {
						var dic = {
							"id" : "phis.dictionary.doctor",
							"filter" : "['and',['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['eq',['$','item.properties.PRESCRIBERIGHT'],['s','1']]],['ne',['$','item.properties.LOGOFF'],['s',1]]]",
							"width" : 147
						};
						var GHYScombox = util.dictionary.SimpleDicFactory
								.createDic(dic)
						GHYScombox.name = 'GHYS'
						GHYScombox.fieldLabel = '挂号医生'
						var dic1 = {
							id : "phis.dictionary.doctor",
							sliceType : 1,
							filter : "['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']],['ne',['$','item.properties.LOGOFF'],['s',1]]]",
							width : 147
						}

						var GHYcombox = util.dictionary.SimpleDicFactory
								.createDic(dic1)
						GHYcombox.name = 'GHY'
						GHYcombox.fieldLabel = '挂号员'
						var THRcombox = util.dictionary.SimpleDicFactory
								.createDic(dic1)
						THRcombox.name = 'THR'
						THRcombox.fieldLabel = '退号人'
						var form = new Ext.FormPanel({
							labelWidth : 60,
							frame : true,
							defaultType : 'textfield',
							layout : 'tableform',
							defaults: {
								bodyStyle:'padding-left:3px;'
							},
							layoutConfig : {
								columns : 6,
								tableAttrs : {
									border : 0
								}
							},
							items : [ {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 60,
									html : "就诊号码:"
								}, new Ext.form.TextField({
									name : "JZHM",
									width:147
								}) ]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 60,
									html : "卡号:"
								}, new Ext.form.TextField({
									name : "JZKH",
									width:147
								}) ]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 60,
									html : "病人姓名:"
								}, new Ext.form.TextField({
									name : "BRXM",
									width:147
								}) ]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 65,
									html : "挂号医生:"
								}, GHYScombox ]
							}, {
								xtype : 'panel',
								layout : "table",
								layoutConfig : {
									rowspan : 2
								},
								items : [ new Ext.Button({
									iconCls : 'query',
									text : '查询',
									handler : this.doQuery,
									scope : this
								}) ]
							}, {
								xtype : 'panel'
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 60,
									html : "挂号员:"
								}, GHYcombox ]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 60,
									html : "挂号日期:"
								}, new Ext.ux.form.Spinner({
									name : 'GHRQFrom',
									value : new Date().format('Y-m-d'),
									width:147,
									strategy : {
										xtype : "date"
									}
								}) ]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 60,
									html : "至",
									style : "text-align:center;"
								}, new Ext.ux.form.Spinner({
									name : 'GHRQTo',
									value : new Date().format('Y-m-d'),
									width:147,
									strategy : {
										xtype : "date"
									}
								}) ]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [ {
									xtype : "panel",
									width : 65,
									html : "退号人:"
								}, THRcombox ]
							}, {
								xtype : 'panel',
								layout : "table",
								layoutConfig : {
									rowspan : 2
								},
								items : [ new Ext.Button({
									iconCls : 'create',
									text : '重置',
									handler : this.doNewQuery,
									scope : this
								}), new Ext.Button({
									iconCls : 'create',
									text : '打印',
									handler : this.doPrint,
									scope : this
								}) ]
							} ]
						});
						this.form = form
						return form
					},
					getList : function() {
						var module = this.createModule("List", this.refList);
						var listModule = module.initPanel();
						this.listModulePanel = listModule
						this.listModule = module;
						module.opener = this;
						return listModule;
					},
					createButton : function() {
						if (this.op == 'read') {
							return [];
						}
						var actions = this.actions;
						var buttons = [];
						if (!actions) {
							return buttons;
						}
						var f1 = 112;
						for ( var i = 0; i < actions.length; i++) {
							var action = actions[i];
							/*
							 * if(this.person.ZFPB){ if(action.id=="fpzf"){
							 * continue; } }else{ if(action.id=="qxzf"){
							 * continue; } }
							 */
							var btn = {};
							btn.accessKey = f1 + i;
							btn.cmd = action.id;
							btn.text = action.name + "(F" + (i + 1) + ")";
							btn.iconCls = action.iconCls || action.id;
							btn.script = action.script;
							btn.handler = this.doAction;
							btn.notReadOnly = action.notReadOnly;
							btn.scope = this;
							// btn.scale = "large";
							// btn.iconAlign = "top";
							buttons.push(btn, '');
						}
						this.buttons = buttons;
						return buttons;
					},
					doAction : function(item, e) {
						var cmd = item.cmd
						var script = item.script
						cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
						if (script) {
							$require(script, [
									function() {
										eval(script + '.do' + cmd
												+ '.apply(this,[item,e])')
									}, this ])
						} else {
							var action = this["do" + cmd]
							if (action) {
								action.apply(this, [ item, e ])
							}
						}
					},
					doNewQuery : function() {
						var year = new Date().getFullYear();
						var month = new Date().getMonth() >= 9 ? (new Date()
								.getMonth() + 1) : "0"
								+ (new Date().getMonth() + 1)
						var date = new Date().getDate() >= 10 ? (new Date()
								.getDate()) : "0" + (new Date().getDate())
						var now = year + "-" + month + "-" + date;
						var from = this.form.getForm();
						from.findField("JZHM").setValue("");
						from.findField("JZKH").setValue("");
						from.findField("BRXM").setValue("");
						from.findField("GHYS").setValue("");
						from.findField("GHY").setValue("");
						from.findField("GHRQFrom").setValue(now);
						from.findField("GHRQTo").setValue(now);
						from.findField("THR").setValue("");
					},
					doQuery : function() {
						/*
						 * if(!this.query){ this.defaultCnd =
						 * this.listModule.requestData.cnd; this.query = true; }
						 */
						var from = this.form.getForm();
						JZHM = from.findField("JZHM").getValue();
						JZKH = from.findField("JZKH").getValue();
						BRXM = from.findField("BRXM").getValue();
						GHYS = from.findField("GHYS").getValue();
						GHY = from.findField("GHY").getValue();
						GHRQFrom = from.findField("GHRQFrom").getValue();
						GHRQTo = from.findField("GHRQTo").getValue()
								+ ' 23:59:59';

						THR = from.findField("THR").getValue();
						if (GHRQFrom != null && GHRQTo != null
								&& GHRQFrom != "" && GHRQTo != ""
								&& GHRQFrom > GHRQTo) {
							Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
							return;
						}
						JZHMCnd = [ 'eq', [ '$', "b.JZHM" ], [ 's', JZHM ] ];
						JZKHCnd = [ 'like', [ '$', "d.CardNo" ],
								[ 's', JZKH + "%" ] ];
						GHRQFromCnd = [ 'ge', [ '$', "b.GHSJ" ],
								[ 'todate', ['s',GHRQFrom], ['s','yyyy-mm-dd'] ] ];
						GHRQToCnd = [ 'le', [ '$', "b.GHSJ" ],
								[ 'todate', ['s',GHRQTo], ['s','yyyy-mm-dd hh24:mi:ss'] ] ];
						BRXMCnd = [ 'like', [ '$', "a.BRXM" ],
								[ 's', "%" + BRXM + "%" ] ];
						GHYSCnd = [ 'eq', [ '$', "b.YSDM" ], [ 's', GHYS ] ];
						GHYCnd = [ 'eq', [ '$', "b.CZGH" ], [ 's', GHY ] ];
						THRCnd = [ 'eq', [ '$', "c.CZGH" ], [ 's', THR ] ];
						var cnd = [];
						if (JZHM != null && JZHM != "") {
							cnd = JZHMCnd;
						}
						if (JZKH != null && JZKH != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, JZKHCnd ];
							} else {
								cnd = JZKHCnd;
							}
						}
						if (GHRQFrom != null && GHRQFrom != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, GHRQFromCnd ];
							} else {
								cnd = GHRQFromCnd;
							}
						}
						if (GHRQTo != null && GHRQTo != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, GHRQToCnd ];
							} else {
								cnd = GHRQToCnd;
							}
						}
						if (GHYS != null && GHYS != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, GHYSCnd ];
							} else {
								cnd = GHYSCnd;
							}
						}
						if (BRXM != null && BRXM != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, BRXMCnd ];
							} else {
								cnd = BRXMCnd;
							}
						}
						if (GHY != null && GHY != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, GHYCnd ];
							} else {
								cnd = GHYCnd;
							}
						}
						if (THR != null && THR != "") {
							if (cnd.length > 0) {
								cnd = [ 'and', cnd, THRCnd ];
							} else {
								cnd = THRCnd;
							}
						}
						// if(this.defaultCnd != null && MZHM != ""){
						// this.listModule.requestData.cnd =
						// ['and',cnd,this.defaultCnd];
						// }else{
						this.listModule.requestData.cnd = cnd;
						// }
						this.listModule.refresh();
					},
//					doPrint : function() {
//						var rs = this.listModulePanel.getSelectionModel()
//								.getSelected()
//						var sbxh = 0;
//						var thbz = "0";
//						if (rs.get("THBZ")) {
//							thbz = rs.get("THBZ");
//						}
//						if (thbz == "1") {
//							MyMessageTip.msg("提示", "退号不需要打印!", true);
//							return;
//						}
//						if (rs.get("SBXH")) {
//							sbxh = rs.get("SBXH")
//						}
//						// var url = this.printurl +
//						// ".print?pages=registrationForm&silentPrint=1&execJs="
//						// + this.getExecJs()
//						// + "&sbxh=" + sbxh;
//						var pages="";
//						//var url = this.printurl + "*.print?sbxh=" + sbxh
//						//		+ "&silentPrint=1&execJs=" + this.getExecJs();
//						if (this.mainApp['phisApp'].deptId == '330105104') {// 祥符挂号单打印
//							pages="phis.prints.jrxml.RegistrationForm2"
//						} else {// 其它医院
//							pages="phis.prints.jrxml.RegistrationForm"
//						}
//						 var url="resources/"+pages+".print?sbxh=" + sbxh
//							+ "&silentPrint=1&execJs=" + this.getExecJs();
//						url += "&temp=" + new Date().getTime();
//						var LODOP=getLodop();
//						LODOP.PRINT_INIT("打印控件");
//						LODOP.SET_PRINT_PAGESIZE("1","96mm","94mm","");
//						LODOP.ADD_PRINT_HTM("0",
//								"0",
//								"100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
//						LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Page");
//						LODOP.PREVIEW();
//					},
					doPrint : function(sbxh){
						var rs = this.listModulePanel.getSelectionModel()
						.getSelected()
						var sbxh = 0;
						var thbz = "0";
						if (rs.get("THBZ")) {
							thbz = rs.get("THBZ");
						}
						if (thbz == "1") {
							MyMessageTip.msg("提示", "退号不需要打印!", true);
							return;
						}
						if (rs.get("SBXH")) {
							sbxh = rs.get("SBXH")
						}
						this.CreateDataBill1(sbxh);
					},
					CreateDataBill1 : function(sbxh) {
			    		var LODOP=getLodop();  
			    		LODOP.PRINT_INITA(10,10,501,499,"挂号收费发票");
						var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registeredManagementService",
							serviceAction : "printMoth2",
							sbxh : sbxh
							});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return null;
						}
				LODOP.SET_PRINT_STYLE("ItemType",4);
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				LODOP.SET_PRINT_PAGESIZE(0,'21cm','12.7cm',"");
				LODOP.ADD_PRINT_TEXT("10mm", 60, 130, 25, ret.json.MZXH);
				LODOP.ADD_PRINT_TEXT("10mm", 240, 100, 25, "非盈利");
				LODOP.ADD_PRINT_TEXT("10mm", 350, 130, 25, ret.json.MZHM);//add by lizhi 2017-11-06 门诊号码显示在发票号码前
				LODOP.ADD_PRINT_TEXT("10mm", 490, 150, 25, ret.json.FPHM);
				LODOP.ADD_PRINT_TEXT("15mm", 30, 100, 25, ret.json.XM);//姓名
				LODOP.ADD_PRINT_TEXT("15mm", 140, 100, 25, ret.json.XB);//性别
				LODOP.ADD_PRINT_TEXT("15mm", 240, 80, 25, ret.json.JSFS);//结算方式
				LODOP.ADD_PRINT_TEXT("30mm", "2mm", "60mm", 20, ret.json.MXMC1);
				LODOP.ADD_PRINT_TEXT("30mm", 164, 25, 20, ret.json.MXSL1);
				LODOP.ADD_PRINT_TEXT("30mm", 190, "20mm", 20, ret.json.MXJE1);
				LODOP.ADD_PRINT_TEXT("55mm", 60, "100mm", 30, "门诊号码："+ret.json.MZHM);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 22);
				LODOP.ADD_PRINT_TEXT("70mm", 60, "100mm", 30, "就诊序号："+ret.json.KSMC+" "+ret.json.PLXH+"号");
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 22);
				LODOP.ADD_PRINT_TEXT("83mm", 60, 500, 25, ret.json.BXMX);//报销明细
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 11);
				LODOP.ADD_PRINT_TEXT("93mm", 120, 300, 25, ret.json.DXZJE);
				LODOP.ADD_PRINT_TEXT("93mm", 480, 100, 25, ret.json.HJJE);
				LODOP.ADD_PRINT_TEXT("98mm", 480, 80, 25, ret.json.XJJE);
				LODOP.ADD_PRINT_TEXT("103mm", 60, 180, 25, ret.json.JGMC);
				LODOP.ADD_PRINT_TEXT("103mm", 270, 100, 25, ret.json.SFY);
				LODOP.ADD_PRINT_TEXT("103mm", 400, 60, 25, ret.json.YYYY);
				LODOP.ADD_PRINT_TEXT("103mm", 465, 40, 25, ret.json.MM);
				LODOP.ADD_PRINT_TEXT("103mm", 510, 40, 25, ret.json.DD);
				LODOP.ADD_PRINT_TEXT("10mm", "185mm", 130, 15, ret.json.MZHM);
				LODOP.ADD_PRINT_TEXT("14mm", "185mm", 130, 15, ret.json.XM);
				LODOP.ADD_PRINT_TEXT("18mm", "185mm", 90, 15, ret.json.SFXM1);
				LODOP.ADD_PRINT_TEXT("22mm", "185mm", 90, 15, ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT("26mm", "185mm", 90, 15, ret.json.GHSJ);
				LODOP.ADD_PRINT_TEXT("30mm", "185mm", 90, 15, ret.json.SFY);
				LODOP.ADD_PRINT_TEXT("34mm", "185mm", 90, 15, ret.json.MZXH);
				LODOP.ADD_PRINT_TEXT("50mm", "185mm", "10mm", 60,"作");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
            	LODOP.ADD_PRINT_TEXT("60mm", "185mm", "10mm", 70,"废");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
            	LODOP.ADD_PRINT_TEXT("90mm", "185mm", "10mm", 60, "作");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
            	LODOP.ADD_PRINT_TEXT("100mm", "185mm", 130, "10mm", "废");
            	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				
				if (LODOP.SET_PRINTER_INDEXA(ret.json.GHDYJMC)){
					if((ret.json.FPYL+"")=='1'){
						LODOP.PREVIEW();
					}else{
						LODOP.PRINT();
					}
				}else{
					LODOP.PREVIEW();
				}
			    	},
					getExecJs : function() {
						return "jsPrintSetup.setPrinter('ghfp');"
					}
				});