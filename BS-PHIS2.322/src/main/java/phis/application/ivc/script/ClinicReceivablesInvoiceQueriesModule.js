/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout");

phis.application.ivc.script.ClinicReceivablesInvoiceQueriesModule = function(cfg) {
	this.exContext = {};
	phis.application.ivc.script.ClinicReceivablesInvoiceQueriesModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.ivc.script.ClinicReceivablesInvoiceQueriesModule,
		phis.script.SimpleModule, {
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
							items : [{
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
									}],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});
				this.panel = panel;
				// 发票
				this.frameId = "SimplePrint_frame_invoice";
				this.conditionFormId = "SimplePrint_form_invoice";
				this.mainFormId = "SimplePrint_mainform_invoice";
				var fppanel = new Ext.Panel({
					id : this.mainFormId,
					width : 800,
					height : 500,
					title : '发票打印',
					tbar : {
						id : this.conditionFormId,
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"invoice\")'></iframe>"
				})
				this.fppanel = fppanel
				return panel;
			},
			createForm : function() {
				var BRXZcombox = util.dictionary.SimpleDicFactory.createDic({
							id : 'phis.dictionary.patientProperties',
							width : 147
						})
				BRXZcombox.name = 'BRXZ';
				BRXZcombox.fieldLabel = '病人性质';
				var dic = {
					"id" : "phis.dictionary.user_bill",
					"sliceType" : 1,
					"filter" : "['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]",
//					"src" : "MS_YGPJ_FP.YGDM",
					"width" : 147
				};
				var CZGHcombox = util.dictionary.SimpleDicFactory
						.createDic(dic)
				CZGHcombox.name = 'CZGH';
				CZGHcombox.fieldLabel = '收款员';
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
									border : 0,
									cellpadding : '2',
									cellspacing : "2"
								}
							},
							items : [{
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "发票号码:"
												}, new Ext.form.TextField({
															name : "FPHMFrom",
															width:147
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "至",
													style : "text-align:center;"
												}, new Ext.form.TextField({
															name : "FPHMTo",
															width:147
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "收费日期:"
												}, new Ext.ux.form.Spinner({
															name : 'SFRQFrom',
															value : new Date().format('Y-m-d'),
															width:147,
															strategy : {
																xtype : "date"
															}
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 50,
													html : "至",
													style : "text-align:center;"
												}, new Ext.ux.form.Spinner({
															name : 'SFRQTo',
															value : new Date().format('Y-m-d'),
															width:147,
															strategy : {
																xtype : "date"
															}
														})]
									}, {
										xtype : 'panel',
										rowspan : "2",
										layout : "table",
										layoutConfig : {
											rowspan : 2
										},
										items : [new Ext.Button({
													iconCls : 'query',
													height : 40,
													width : 80,
													text : '查询',
													handler : this.doQuery,
													scope : this
												})]
									}, {
										xtype : "panel"
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "病人性质:"
												}, BRXZcombox]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "病人姓名:"
												}, new Ext.form.TextField({
															name : "BRXM",
															width:147
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "卡号:"
												}, new Ext.form.TextField({
															name : "JZKH",
															width:147
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 50,
													html : "收款员:"
												}, CZGHcombox]
									}]
						});
				this.form = form
				return form
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				var listModule = module.initPanel();
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
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
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
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('mzfp');"
			},
			doFp : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}

				var fphm = record.data.FPHM;
//				if (record.data.YWLSH) {
//					this.opener.lssbxx = false;
//					var module = this.createModule("fpprint2", "IVC0206")
//					module.fphm = fphm;
//					module.flag = "1"; // add zhangch flag=="1" 表示补打
//					module.initPanel();
//					module.doPrint();
//				} else {
					this.conditionFormId = "SimplePrint_form_invoice";
					var form = Ext.getCmp(this.conditionFormId).getForm()
					if (!form.isValid()) {
						return
					}
					var items = form.items
					this.printurl = util.helper.Helper.getUrl();
					// var url = this.printurl +
					// ".print?pages=invoice&silentPrint=1";
					//var url = "";
					//url = this.printurl
					//		+ "*.print?pages=phis.prints.jrxml.Invoice&silentPrint=1&execJs="
						//	+ this.getExecJs();
					var pages="phis.prints.jrxml.Invoice";
					 var url="resources/"+pages+".print?silentPrint=1&execJs="
							+ this.getExecJs();
					for (var i = 0; i < items.getCount(); i++) {
						var f = items.get(i)
						url += "&" + f.getName() + "=" + f.getValue()
					}
					url += "&temp=" + new Date().getTime() + "&fphm=" + fphm
							+ "&flag=true";
					var printWin = window
							.open(
									url,
									"",
									"height="
											+ (screen.height - 100)
											+ ", width="
											+ (screen.width - 10)
											+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
					printWin.onafterprint = function() {
						printWin.close();
					};
// }
			},
			doFpbd : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var fphm = record.data.FPHM;// 发票号码
				 
//				LODOP.PRINT_INITA(10,10,762,533,"门诊收费发票2");
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "printMoth",
						fphm : fphm
					});
				this.fphm = false;
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				var LODOP=getLodop(); 
				for(var i = 0 ; i < ret.json.mzfps.length ; i ++){
			if(i>0){
				LODOP.NewPageA();
			}
			var mzfp = ret.json.mzfps[i];
			LODOP.SET_PRINT_STYLE("ItemType",4);
			LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
			LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
			LODOP.SET_PRINT_PAGESIZE(0,'21cm','12.7cm',"")
			LODOP.ADD_PRINT_TEXT("2mm", 470, 150, 25, "序号 "+mzfp.YFFYSL);
			LODOP.SET_PRINT_STYLEA(0, "FontSize", 15);
			LODOP.ADD_PRINT_TEXT("7mm", 60, 130, 25, mzfp.MZXH);
			LODOP.ADD_PRINT_TEXT("7mm", 240, 130, 25, "非盈利");
			LODOP.ADD_PRINT_TEXT("7mm", 490, 150, 25, mzfp.FPHM);
			LODOP.ADD_PRINT_TEXT("12mm", 30, 100, 25, mzfp.XM);//
			LODOP.ADD_PRINT_TEXT("12mm", 140, 100, 25, mzfp.XB);//性别
			//begin 2018-12-31 zhaojian 发票打印区分居民医保和职工医保
			if(ret.json.YLRYLB=="51"){//普通居民
				LODOP.ADD_PRINT_TEXT("16mm", 240, 120, 25, "城乡居民医疗保险");//结算方式
			}else{
				LODOP.ADD_PRINT_TEXT("16mm", 240, 80, 25, mzfp.JSFS);//结算方式
			}
			//end 2018-12-31 zhaojian 发票打印区分居民医保和职工医保
			LODOP.ADD_PRINT_TEXT("16mm", 460, 200, 25, mzfp.YLZH);//社会保障号码
			//非医保明细打印
			LODOP.ADD_PRINT_TEXT("27mm", "2mm", "60mm", 20, mzfp.MXMC1);
			LODOP.ADD_PRINT_TEXT("27mm", 164, 25, 20, mzfp.MXSL1);
			LODOP.ADD_PRINT_TEXT("27mm", 190, "20mm", 20, mzfp.MXJE1);
			LODOP.ADD_PRINT_TEXT("32mm", "2mm", 152, 20, mzfp.MXMC3);
			LODOP.ADD_PRINT_TEXT("32mm", 164, 25, 20, mzfp.MXSL3);
			LODOP.ADD_PRINT_TEXT("32mm", 190, "20mm", 20, mzfp.MXJE3);
			LODOP.ADD_PRINT_TEXT("37mm", "2mm", "60mm", 20, mzfp.MXMC5);
			LODOP.ADD_PRINT_TEXT("37mm", 164, 25, 20, mzfp.MXSL5);
			LODOP.ADD_PRINT_TEXT("37mm", 190, "20mm", 20, mzfp.MXJE5);
			LODOP.ADD_PRINT_TEXT("42mm", "2mm", "60mm", 20, mzfp.MXMC7);
			LODOP.ADD_PRINT_TEXT("42mm", 164, 25, 20, mzfp.MXSL7);
			LODOP.ADD_PRINT_TEXT("42mm", 190, "20mm", 20, mzfp.MXJE7);
			LODOP.ADD_PRINT_TEXT("47mm", "2mm", "60mm", 20, mzfp.MXMC9);
			LODOP.ADD_PRINT_TEXT("47mm", 164, 25, 20, mzfp.MXSL9);
			LODOP.ADD_PRINT_TEXT("47mm", 190, "20mm", 20, mzfp.MXJE9);
			LODOP.ADD_PRINT_TEXT("52mm", "2mm", "60mm", 20, mzfp.MXMC11);
			LODOP.ADD_PRINT_TEXT("52mm", 164, 25, 20, mzfp.MXSL11);
			LODOP.ADD_PRINT_TEXT("52mm", 190, "20mm", 20, mzfp.MXJE11);				
			LODOP.ADD_PRINT_TEXT("57mm", "2mm", "60mm", 20, mzfp.MXMC13);
			LODOP.ADD_PRINT_TEXT("57mm", 164, 25, 20, mzfp.MXSL13);
			LODOP.ADD_PRINT_TEXT("57mm", 190, "20mm", 20, mzfp.MXJE13);				
			LODOP.ADD_PRINT_TEXT("62mm", "2mm", "60mm", 20, mzfp.MXMC15);
			LODOP.ADD_PRINT_TEXT("62mm", 164, 25, 20, mzfp.MXSL15);
			LODOP.ADD_PRINT_TEXT("62mm", 190, "20mm", 20, mzfp.MXJE15);
				
			LODOP.ADD_PRINT_TEXT("27mm", 305, "60mm", 20, mzfp.MXMC2);
			LODOP.ADD_PRINT_TEXT("27mm", 480, 25, 20, mzfp.MXSL2);
			LODOP.ADD_PRINT_TEXT("27mm", 520, "20mm", 20, mzfp.MXJE2);
			LODOP.ADD_PRINT_TEXT("32mm", 305, "60mm", 20, mzfp.MXMC4);
			LODOP.ADD_PRINT_TEXT("32mm", 480, 25, 20, mzfp.MXSL4);
			LODOP.ADD_PRINT_TEXT("32mm", 520, "20mm", 20, mzfp.MXJE4);
			LODOP.ADD_PRINT_TEXT("37mm", 305, "60mm", 20, mzfp.MXMC6);
			LODOP.ADD_PRINT_TEXT("37mm", 480, 25, 20, mzfp.MXSL6);
			LODOP.ADD_PRINT_TEXT("37mm", 520, "20mm", 20, mzfp.MXJE6);
			LODOP.ADD_PRINT_TEXT("42mm", 305, "60mm", 20, mzfp.MXMC8);
			LODOP.ADD_PRINT_TEXT("42mm", 480, 25, 20, mzfp.MXSL8);
			LODOP.ADD_PRINT_TEXT("42mm", 520, "20mm", 20, mzfp.MXJE8);			
			LODOP.ADD_PRINT_TEXT("47mm", 305, "60mm", 20, mzfp.MXMC10);
			LODOP.ADD_PRINT_TEXT("47mm", 480, 25, 20, mzfp.MXSL10);
			LODOP.ADD_PRINT_TEXT("47mm", 520, "20mm", 20, mzfp.MXJE10);				
			LODOP.ADD_PRINT_TEXT("52mm", 305, "60mm", 20, mzfp.MXMC12);
			LODOP.ADD_PRINT_TEXT("52mm", 480, 25, 20, mzfp.MXSL12);
			LODOP.ADD_PRINT_TEXT("52mm", 520, "20mm", 20, mzfp.MXJE12);				
			LODOP.ADD_PRINT_TEXT("57mm", 305, "60mm", 20, mzfp.MXMC14);
			LODOP.ADD_PRINT_TEXT("57mm", 480, 25, 20, mzfp.MXSL14);
			LODOP.ADD_PRINT_TEXT("57mm", 520, "20mm", 20, mzfp.MXJE14);				
			LODOP.ADD_PRINT_TEXT("62mm", 305, "60mm", 20, mzfp.MXMC16);
			LODOP.ADD_PRINT_TEXT("62mm", 480, 25, 20, mzfp.MXSL16);
			LODOP.ADD_PRINT_TEXT("62mm", 520, "20mm", 20, mzfp.MXJE16);				
			LODOP.ADD_PRINT_TEXT("93mm", 120, 300, 25, mzfp.DXZJE);
			LODOP.ADD_PRINT_TEXT("93mm", 480, 100, 25, mzfp.HJJE);
			LODOP.ADD_PRINT_TEXT("98mm", 60, 40, 25, mzfp.JZ);
			LODOP.ADD_PRINT_TEXT("96mm", 100, 300, 20, mzfp.BZ);
			LODOP.SET_PRINT_STYLEA(0, "FontSize", 7);
			LODOP.ADD_PRINT_TEXT("98mm", 165, 60, 25, mzfp.GRZHZF);
			LODOP.ADD_PRINT_TEXT("98mm", 260, 60, 25, mzfp.QTYBZF);
			//zhaojian 2019-07-18 增加家医履约减免金额
			//LODOP.ADD_PRINT_TEXT("98mm", 480, 80, 25, mzfp.GRZF);
			LODOP.ADD_PRINT_TEXT("98mm", 480, 320, 25, mzfp.GRZF);
			LODOP.ADD_PRINT_TEXT("103mm", 60, 180, 25, mzfp.JGMC);
			LODOP.ADD_PRINT_TEXT("103mm", 270, 100, 25, mzfp.SFY);
			LODOP.ADD_PRINT_TEXT("103mm", 400, 60, 25, mzfp.YYYY);
			LODOP.ADD_PRINT_TEXT("103mm", 465, 40, 25, mzfp.MM);
			LODOP.ADD_PRINT_TEXT("103mm", 510, 40, 25, mzfp.DD);
			LODOP.ADD_PRINT_TEXT("103mm",570,60,20,mzfp.PAGE);//打印页
			if(mzfp.SFXM1 && i==0){
				LODOP.ADD_PRINT_TEXT("10mm", "182mm", 130, 15, mzfp.MZHM);
				LODOP.ADD_PRINT_TEXT("14mm", "182mm", 130, 15, mzfp.XM);
				LODOP.ADD_PRINT_TEXT("18mm", "182mm", 90, 15, mzfp.SFXM1);
				LODOP.ADD_PRINT_TEXT("22mm", "182mm", 90, 15, mzfp.XMJE1);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
				LODOP.ADD_PRINT_TEXT("26mm", "182mm", 90, 15, mzfp.FYRQ);
				LODOP.ADD_PRINT_TEXT("30mm", "182mm", 90, 15, mzfp.SFY);
				LODOP.ADD_PRINT_TEXT("34mm", "182mm", 90, 15, mzfp.MZXH);
			}
				if(mzfp.SFXM4 && i==1){
				LODOP.ADD_PRINT_TEXT("10mm", "182mm", 130, 15, mzfp.MZHM);
				LODOP.ADD_PRINT_TEXT("14mm", "182mm", 130, 15, mzfp.XM);
				LODOP.ADD_PRINT_TEXT("18mm", "182mm", 90, 15, mzfp.SFXM4);
				LODOP.ADD_PRINT_TEXT("22mm", "182mm", 90, 15, mzfp.XMJE4);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
				LODOP.ADD_PRINT_TEXT("26mm", "182mm", 90, 15, mzfp.FYRQ);
				LODOP.ADD_PRINT_TEXT("30mm", "182mm", 90, 15, mzfp.SFY);
				LODOP.ADD_PRINT_TEXT("34mm", "182mm", 90, 15, mzfp.MZXH);
				
				}else if (i==1){
					LODOP.ADD_PRINT_TEXT("10mm", "182mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("20mm", "182mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				}
				if(mzfp.SFXM7 && i==2){
				LODOP.ADD_PRINT_TEXT("10mm", "182mm", 130, 15, mzfp.MZHM);
				LODOP.ADD_PRINT_TEXT("14mm", "182mm", 130, 15, mzfp.XM);
				LODOP.ADD_PRINT_TEXT("18mm", "182mm", 90, 15, mzfp.SFXM7);
				LODOP.ADD_PRINT_TEXT("22mm", "182mm", 90, 15, mzfp.XMJE7);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
				LODOP.ADD_PRINT_TEXT("26mm", "182mm", 90, 15, mzfp.FYRQ);
				LODOP.ADD_PRINT_TEXT("20mm", "182mm", 90, 15, mzfp.SFY);
				LODOP.ADD_PRINT_TEXT("24mm", "182mm", 90, 15, mzfp.MZXH);
				
				}else if (i==2){
					LODOP.ADD_PRINT_TEXT("10mm", "182mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("20mm", "182mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
				}
				
                if(mzfp.SFXM2 && i==0){
					LODOP.ADD_PRINT_TEXT("45mm", "182mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("49mm", "182mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("53mm", "182mm", 90, 20, mzfp.SFXM2);
					LODOP.ADD_PRINT_TEXT("57mm", "182mm", 90, 20, mzfp.XMJE2);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
					LODOP.ADD_PRINT_TEXT("61mm", "182mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("65mm", "182mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("69mm", "182mm", 90, 20, mzfp.MZXH);
                }else if(i==0){
                	LODOP.ADD_PRINT_TEXT("50mm", "182mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("60mm", "182mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                
                if(mzfp.SFXM5 && i==1){
					LODOP.ADD_PRINT_TEXT("45mm", "182mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("49mm", "182mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("53mm", "182mm", 90, 20, mzfp.SFXM5);
					LODOP.ADD_PRINT_TEXT("57mm", "182mm", 90, 20, mzfp.XMJE5);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
					LODOP.ADD_PRINT_TEXT("61mm", "182mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("65mm", "182mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("69mm", "182mm", 90, 20, mzfp.MZXH);
                }else if(i==1){
                	LODOP.ADD_PRINT_TEXT("50mm", "182mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("60mm", "182mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                
                if(mzfp.SFXM8 && i==2){
					LODOP.ADD_PRINT_TEXT("45mm", "182mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("49mm", "182mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("53mm", "182mm", 90, 20, mzfp.SFXM8);
					LODOP.ADD_PRINT_TEXT("57mm", "182mm", 90, 20, mzfp.XMJE8);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
					LODOP.ADD_PRINT_TEXT("61mm", "182mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("65mm", "182mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("69mm", "182mm", 90, 20, mzfp.MZXH);
                }else if(i==2){
                	LODOP.ADD_PRINT_TEXT("50mm", "182mm", "10mm", 60,"作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("60mm", "182mm", "10mm", 70,"废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }                
                
                if(mzfp.SFXM3 && i==0){
					LODOP.ADD_PRINT_TEXT("82mm", "182mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("86mm", "182mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("90mm", "182mm", 90, 20, mzfp.SFXM3);
					LODOP.ADD_PRINT_TEXT("94mm", "182mm", 90, 20, mzfp.XMJE3);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
					LODOP.ADD_PRINT_TEXT("98mm", "182mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("102mm", "182mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("106mm", "182mm", 90, 20, mzfp.MZXH);				
                }else if(i==0){
                	LODOP.ADD_PRINT_TEXT("90mm", "182mm", "10mm", 60, "作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("100mm", "182mm", 130, "10mm", "废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                
                if(mzfp.SFXM6 && i==1){
					LODOP.ADD_PRINT_TEXT("82mm", "182mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("86mm", "182mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("90mm", "182mm", 90, 20, mzfp.SFXM6);
					LODOP.ADD_PRINT_TEXT("94mm", "182mm", 90, 20, mzfp.XMJE6);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
					LODOP.ADD_PRINT_TEXT("98mm", "182mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("102mm", "182mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("106mm", "182mm", 90, 20, mzfp.MZXH);			
                }else if(i==1){
                	LODOP.ADD_PRINT_TEXT("90mm", "182mm", "10mm", 60, "作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("100mm", "182mm", 130, "10mm", "废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }                
                if(mzfp.SFXM9 && i==2){
					LODOP.ADD_PRINT_TEXT("82mm", "182mm", 130, 20, mzfp.MZHM);
					LODOP.ADD_PRINT_TEXT("86mm", "182mm", 130, 20, mzfp.XM);
					LODOP.ADD_PRINT_TEXT("90mm", "182mm", 90, 20, mzfp.SFXM9);
					LODOP.ADD_PRINT_TEXT("94mm", "182mm", 90, 20, mzfp.XMJE9);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
					LODOP.ADD_PRINT_TEXT("98mm", "182mm", 90, 20, mzfp.FYRQ);
					LODOP.ADD_PRINT_TEXT("102mm", "182mm", 90, 20, mzfp.SFY);
					LODOP.ADD_PRINT_TEXT("106mm", "182mm", 90, 20, mzfp.MZXH);				
                }else if(i==2){
                	LODOP.ADD_PRINT_TEXT("90mm", "182mm", "10mm", 60, "作");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                	LODOP.ADD_PRINT_TEXT("90mm", "182mm", 130, "10mm", "废");
                	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
                }
                if(i==ret.json.mzfps.length-1){
                	if(ret.json.njjbjsxx){
                		LODOP.ADD_PRINT_TEXT("70mm", "5mm", "30mm", 20, "统筹支付："+ret.json.njjbjsxx.BCTCZFJE);
						LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
						LODOP.ADD_PRINT_TEXT("70mm", "40mm", "30mm", 20, "大病救助："+ret.json.njjbjsxx.BCDBJZZF);
						LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
						LODOP.ADD_PRINT_TEXT("70mm", "80mm", "50mm", 20, "帐户支付自理："+ret.json.njjbjsxx.BCZHZFZL);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("70mm", "130mm", "50mm", 20, "帐户支付自付："+ret.json.njjbjsxx.BCZHZFZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
						LODOP.ADD_PRINT_TEXT("75mm", "5mm", "30mm", 20, "大病保险："+ret.json.njjbjsxx.BCDBBXZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("75mm", "40mm", "30mm", 20, "民政补助："+ret.json.njjbjsxx.BCMZBZZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("75mm", "80mm", "50mm", 20, "现金支付自付："+ret.json.njjbjsxx.BCXJZFZF);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("75mm", "130mm", "50mm", 20, "现金支付自理："+ret.json.njjbjsxx.BCXJZFZL);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("80mm", "5mm", "30mm", 20, "帐户支付："+ret.json.njjbjsxx.BCZHZFZE);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize",10);
                		LODOP.ADD_PRINT_TEXT("80mm", "40mm", "30mm", 20, "现金支付："+ret.json.njjbjsxx.BCXZZFZE);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("80mm", "80mm", "50mm", 20, "医保范围内费用："+ret.json.njjbjsxx.YBFWNFY);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		LODOP.ADD_PRINT_TEXT("80mm", "130mm", "50mm", 20, "帐户消费后余额："+ret.json.njjbjsxx.ZHXFHYE);
                		LODOP.SET_PRINT_STYLEA(0, "FontSize", 10);
                		var dicName = {
            			 id : "phis.dictionary.ybyllb"
          				};
						var dic=util.dictionary.DictionaryLoader.load(dicName);
						var di;
						di = dic.wraper[ret.json.njjbjsxx.YLLB];
						var text=""
						if (di) {
							text = di.text;
						}
						text=text.substring(text.indexOf("-")+1)
               			 LODOP.ADD_PRINT_TEXT("12mm", 275, 120, 25,"("+text+")");
                	}
                }
			if (LODOP.SET_PRINTER_INDEXA(ret.json.MZHJSFDYJMC)){
				if((ret.json.FPYL+"")=='1'){
					LODOP.PREVIEW();
				}else{
					LODOP.PRINT();
				}
			}else{
				LODOP.PREVIEW();
			}
				}
			},
			/**
			 * 打印真实发票
			 */
			doDyzsfp : function(){
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var fphm = record.data.FPHM;//发票号码
				var sjfp = record.data.SJFP;//实际发票
				var sffs = record.data.SFFS;//收费方式
				if(sffs == 1 && sjfp == fphm){
					var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "saveDyzsfp",
							body : record.data
						});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
						return;
					} else {
						this.listModule.refresh();
						this.callZsdy(fphm, record.data.BRXZ, r);
					}
				}else{
					MyMessageTip.msg("提示", "本次结算已经打印真实发票!", true);
					return;
				}
			},
			/**
			 * 调用打印真是发票功能
			 * @param {} fphm
			 */
			callZsdy : function(fphm, brxz, r){
//				if (brxz == r.json.BRXZ.SHIYB
//						|| brxz == r.json.BRXZ.SHENGYB) {// 医保病人发票打印
//					var module = this.createModule("fpprint2", "IVC0206")
//					module.fphm = fphm;
//					
//					module.initPanel();
//					module.doPrint();	
//				}else{//非医保病人发票打印
					var module = this.createModule("fpprint1","IVC0205")
					module.fphm = fphm;
					module.initPanel();
					module.doPrint();
//				}
			},
			doMzqd : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var fphm = record.data.FPHM;
				this.opener.lssbxx = false;
				var module = this.createModule("fpprint3", this.refMzqd)
				module.fphm = fphm;
				module.initPanel();
				module.doPrint();

			},
			doDj : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var fphm = record.data.FPHM;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "clinicChargesProcessingService",
							serviceAction : "queryFphm",
							body : fphm
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该发票号码没有数据!");
						return;
					}
					// var cfsbs = r.json.body;
					var module = this.midiModules["mzModule"];
					if (!module) {
						module = this.createModule("mzModule", "phis.application.ivc.IVC/IVC/IVC0203");
						module.opener = this;
					}
					module.initDataId = fphm;
					module.person = r.json.body;
					module.djs = r.json.djs;
					var win = module.getWin();
					module.loadData();
					win.show();
				}
			},
			doNewQuery : function() {
				var year = new Date().getFullYear();
				var month = new Date().getMonth() >= 9
						? (new Date().getMonth() + 1)
						: "0" + (new Date().getMonth() + 1)
				var date = new Date().getDate() >= 10
						? (new Date().getDate())
						: "0" + (new Date().getDate())
				var now = year + "-" + month + "-" + date;
				var from = this.form.getForm();
				from.findField("FPHMFrom").setValue("");
				from.findField("FPHMTo").setValue("");
				from.findField("SFRQFrom").setValue(now);
				from.findField("SFRQTo").setValue(now);
				from.findField("BRXZ").setValue("");
				from.findField("BRXM").setValue("");
				from.findField("JZKH").setValue("");
				from.findField("CZGH").setValue("");
			},
			doQuery : function() {
				var from = this.form.getForm();
				FPHMFrom = from.findField("FPHMFrom").getValue();
				FPHMTo = from.findField("FPHMTo").getValue();
				SFRQFrom = from.findField("SFRQFrom").getValue();
				SFRQTo = from.findField("SFRQTo").getValue() + ' 23:59:59';
				BRXZ = from.findField("BRXZ").getValue();
				BRXM = from.findField("BRXM").getValue();
				JZKH = from.findField("JZKH").getValue();
				CZGH = from.findField("CZGH").getValue();
				if (FPHMFrom != null && FPHMTo != null && FPHMFrom != ""
						&& FPHMTo != "" && FPHMFrom > FPHMTo) {
					Ext.MessageBox.alert("提示", "开始发票号不能大于结束发票号");
					return;
				}
				if (SFRQFrom != null && SFRQTo != null && SFRQFrom != ""
						&& SFRQTo != "" && SFRQFrom > SFRQTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				FPHMFromCnd = ['ge', ['$', "a.FPHM"], ['s', FPHMFrom]];
				FPHMToCnd = ['le', ['$', "a.FPHM"], ['s', FPHMTo]];
				SFRQFromCnd = ['ge', ['$', "a.SFRQ"],
						['todate', ['s',SFRQFrom], ['s','yyyy-mm-dd']]];
				SFRQToCnd = ['le', ['$', "a.SFRQ"],
						['todate', ['s',SFRQTo], ['s','yyyy-mm-dd hh24:mi:ss']]];
				BRXZCnd = ['eq', ['$', "a.BRXZ"], ['d', BRXZ]];
				BRXMCnd = ['like', ['$', "a.BRXM"], ['s', "%" + BRXM + "%"]];
				JZKHCnd = ['like', ['$', "c.CardNo"], ['s', JZKH + "%"]];
				CZGHCnd = ['eq', ['$', "a.CZGH"], ['s', CZGH]];
				var cnd = [];
				if (FPHMFrom != null && FPHMFrom != "") {
					cnd = FPHMFromCnd;
				}
				if (FPHMTo != null && FPHMTo != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, FPHMToCnd];
					} else {
						cnd = FPHMToCnd;
					}
				}
				if (SFRQFrom != null && SFRQFrom != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, SFRQFromCnd];
					} else {
						cnd = SFRQFromCnd;
					}
				}
				if (SFRQTo != null && SFRQTo != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, SFRQToCnd];
					} else {
						cnd = SFRQToCnd;
					}
				}
				if (BRXZ != null && BRXZ != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, BRXZCnd];
					} else {
						cnd = BRXZCnd;
					}
				}
				if (BRXM != null && BRXM != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, BRXMCnd];
					} else {
						cnd = BRXMCnd;
					}
				}
				if (JZKH != null && JZKH != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, JZKHCnd];
					} else {
						cnd = JZKHCnd;
					}
				}
				if (CZGH != null && CZGH != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, CZGHCnd];
					} else {
						cnd = CZGHCnd;
					}
				}
				this.listModule.requestData.cnd = cnd;
				this.listModule.refresh();
			},
			/**
			 * 以下方法是医保接口获取数字签名方法
			 * 
			 * @return {}
			 */
			sign : function(ss) {
				var CAPICOM_STORE_OPEN_READ_ONLY = 0;
				var CAPICOM_CURRENT_USER_STORE = 2;
				var CAPICOM_CERTIFICATE_FIND_EXTENDED_PROPERTY = 6;
				var CAPICOM_CERTIFICATE_FIND_TIME_VALID = 9;
				var CAPICOM_CERTIFICATE_FIND_KEY_USAGE = 12;
				var CAPICOM_DIGITAL_SIGNATURE_KEY_USAGE = 0x00000080;
				var CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME = 0;
				var CAPICOM_E_CANCELLED = -2138568446;
				var CERT_KEY_SPEC_PROP_ID = 6;
				var CAPICOM_VERIFY_SIGNATURE_AND_CERTIFICATE = 1;
				var CAPICOM_ENCODE_BASE64 = 0;
				var CAPICOM_HASH_ALGORITHM_SHA1 = 0;
				var ret = "";
				var SignedData = new ActiveXObject("CAPICOM.SignedData"); // 数字签名对象
				var Signer = new ActiveXObject("CAPICOM.Signer"); // 签名人对象
				Signer.Options = 2;
				try {
					// 以下从证书列表中获取签名证书
					var Store = new ActiveXObject("CAPICOM.Store"); // 证书存贮对象
					Store.Open(CAPICOM_CURRENT_USER_STORE, "My",
							CAPICOM_STORE_OPEN_READ_ONLY);

					var FilteredSignCertificates = Store.Certificates.Find(
							CAPICOM_CERTIFICATE_FIND_KEY_USAGE,
							CAPICOM_DIGITAL_SIGNATURE_KEY_USAGE)
							.Find(CAPICOM_CERTIFICATE_FIND_TIME_VALID).Find(
									CAPICOM_CERTIFICATE_FIND_EXTENDED_PROPERTY,
									CERT_KEY_SPEC_PROP_ID); // 证书列表对象

					if (FilteredSignCertificates.Count == 1)
						Signer.Certificate = FilteredSignCertificates.Item(1);
					else if (FilteredSignCertificates.Count > 1)
						Signer.Certificate = FilteredSignCertificates.Select(
								"选择签名证书", "请选中你的签名证书，按确定").Item(1);
					else
						alert("无法读取可供选择的证书！");
					SignedData.Content = this.hex_sha1(ss); // 对原文摘要签名
					ret = SignedData.Sign(Signer, false, CAPICOM_ENCODE_BASE64);

				} catch (e) {
					alert("签名过程中出现错误: " + e.description);
				}
				return ret;
			},
			hex_sha1 : function(s) {
				var chrsz = 8;
				return this.binb2hex(this.core_sha1(this.str2binb(s), s.length
								* chrsz));
			},
			core_sha1 : function(x, len) {
				x[len >> 5] |= 0x80 << (24 - len % 32);
				x[((len + 64 >> 9) << 4) + 15] = len;
				var w = Array(80);
				var a = 1732584193;
				var b = -271733879;
				var c = -1732584194;
				var d = 271733878;
				var e = -1009589776;
				for (var i = 0; i < x.length; i += 16) {
					var olda = a;
					var oldb = b;
					var oldc = c;
					var oldd = d;
					var olde = e;
					for (var j = 0; j < 80; j++) {
						if (j < 16)
							w[j] = x[i + j];
						else
							w[j] = this.rol(w[j - 3] ^ w[j - 8] ^ w[j - 14]
											^ w[j - 16], 1);
						var t = this.safe_add(this.safe_add(this.rol(a, 5),
										this.sha1_ft(j, b, c, d)), this
										.safe_add(this.safe_add(e, w[j]), this
														.sha1_kt(j)));
						e = d;
						d = c;
						c = this.rol(b, 30);
						b = a;
						a = t;
					}
					a = this.safe_add(a, olda);
					b = this.safe_add(b, oldb);
					c = this.safe_add(c, oldc);
					d = this.safe_add(d, oldd);
					e = this.safe_add(e, olde);
				}

				return Array(a, b, c, d, e);
			},
			sha1_ft : function(t, b, c, d) {
				if (t < 20)
					return (b & c) | ((~b) & d);
				if (t < 40)
					return b ^ c ^ d;
				if (t < 60)
					return (b & c) | (b & d) | (c & d);
				return b ^ c ^ d;
			},
			sha1_kt : function(t) {
				return (t < 20) ? 1518500249 : (t < 40) ? 1859775393 : (t < 60)
						? -1894007588
						: -899497514;
			},
			safe_add : function(x, y) {
				var lsw = (x & 0xFFFF) + (y & 0xFFFF);
				var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
				return (msw << 16) | (lsw & 0xFFFF);
			},
			rol : function(num, cnt) {
				return (num << cnt) | (num >>> (32 - cnt));
			},

			binb2hex : function(binarray) {
				var hexcase = 0;
				var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
				var str = "";
				for (var i = 0; i < binarray.length * 4; i++)
					str += hex_tab
							.charAt((binarray[i >> 2] >> ((3 - i % 4) * 8 + 4))
									& 0xF)
							+ hex_tab
									.charAt((binarray[i >> 2] >> ((3 - i % 4) * 8))
											& 0xF);
				return str;
			},
			str2binb : function(str) {
				var chrsz = 8;
				var bin = Array();
				var mask = (1 << chrsz) - 1;
				for (var i = 0; i < str.length * chrsz; i += chrsz)
					bin[i >> 5] |= (str.charCodeAt(i / chrsz) & mask) << (32
							- chrsz - i % 32);
				return bin;
			},
			// ---------获取数字签名方法结束
			// 发票补传
			doBc : function() {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				var jslsh = record.data.JSLSH;
				if (jslsh == null || jslsh == "" || jslsh == undefined
						|| jslsh.length < 4) {
					Ext.MessageBox.alert("提示", "不是医保发票");
					return;
				}
				var fpsc = record.data.FPSC
				if (fpsc == "1") {
					Ext.MessageBox.alert("提示", "已经上传过,不需要重复上传!");
					return;
				}
				var brxz = record.data.BRXZ;
				var ss = "0";
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryNatureActionId
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doBc);
					return;
				}
				this.natureList = ret.json.natureList;
				// 查询参数里面的性质
				for (var i = 0; i < this.natureList.length; i++) {
					if (brxz == this.natureList[i]) {
						if (brxz == "6104") {
							ss = "1";
						}
					}
				}
				if (ss != "1") {
					var cas = "门诊上传发票号";
					var parXml = '<?xml version=\"1.0\" encoding=\"GB18030\"?><XML><结算流水号>'
							+ jslsh
							+ '</结算流水号><姓名>'
							+ record.data.BRXM
							+ '</姓名><参保险种>'
							+ record.data.CBXZ
							+ '</参保险种><发票号>'
							+ record.data.FPHM
							+ '</发票号><发票流水号>'
							+ record.data.FPHM + '</发票流水号></XML>';
					var ret = this.dyjk(cas, parXml);
					if (ret.状态 == "2") {
						MyMessageTip.msg("提示", ret.备注, true);
					} else if (ret.状态 == "3") {
						Ext.Msg.alert("提示", ret.备注);
						return;
					}
				} else {
					var cas = "放化疗碎石它院检查报销上传发票号";
					var parXml = '<?xml version=\"1.0\" encoding=\"GB18030\"?><XML><结算流水号>'
							+ jslsh
							+ '</结算流水号><结算日期>'
							+ record.data.SFRQ
							+ '</结算日期><姓名>'
							+ record.data.BRXM
							+ '</姓名><参保险种>'
							+ record.data.CBXZ
							+ '</参保险种><发票号>'
							+ record.data.FPHM
							+ '</发票号><发票流水号>'
							+ record.data.FPHM + '</发票流水号></XML>';
					var ret = this.dyjk(cas, parXml);
					if (ret.状态 == "2") {
						MyMessageTip.msg("提示", ret.备注, true);
					} else if (ret.状态 == "3") {
						Ext.Msg.alert("提示", ret.备注);
						return;
					}
				}
				var body_fp = {}
				body_fp["jslsh"] = jslsh
				var ret_c = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.uploadInvoiceActionId,
							body : body_fp
						});
				if (ret_c.code > 300) {
					this.processReturnMsg(ret_c.code, "发票上传失败!请重新传或者联系管理员!",
							this.doBc);
					return;
				}
				MyMessageTip.msg("提示", "发票上传成功", true);
				this.listModule.refresh();

			},
			// 接口调用方法
			dyjk : function(cas, parXml) {
				if (!this.dep || !this.cer) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryDeptActionId
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.dyjk);
						return;
					}
					this.dep = ret.json.dep;
					this.cer = ret.json.cer;
				}

				var dep = this.dep;
				var cas = cas;
				var ser = "医院联网结算";
				var cer = this.cer;
				var parXml = parXml;
				var sig = this.sign(ser + dep + cas + parXml);
				var body = {};
				body["dep"] = dep;
				body["cas"] = cas;
				body["ser"] = ser;
				body["cer"] = cer;
				body["sig"] = sig;
				body["parXML"] = parXml;
				var ret_c = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.qualificationActionId,
							body : body
						});
				if (ret_c.code > 300) {
					this.processReturnMsg(ret_c.code, ret_c.msg, this.dyjk);
					return;
				}
				return ret_c.json.body;
			}
		});
simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}