/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.hos.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.hos.script.HospitalHistorySettleQueriesModule = function(cfg) {
	this.exContext = {};
	phis.application.hos.script.HospitalHistorySettleQueriesModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalHistorySettleQueriesModule,
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
										height : 78,
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
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				this.doQuery();
			},
			createForm : function() {
				var BRKSdic = {
					"id" : "phis.dictionary.department_zy",
					"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
					"src" : "SYS_Office.ID",
					"width" : 130
				};
				var BRKScombox = util.dictionary.SimpleDicFactory
						.createDic(BRKSdic)
				BRKScombox.name = 'BRKS';
				BRKScombox.fieldLabel = '病人科室';
				BRKScombox.emptyText = "所有科室"
				var dic = {
					"id" : "phis.dictionary.user_zybill",
					"sliceType":1,
					"filter" : "['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]",
					"src" : "MS_YGPJ_FP.YGDM",
					"width" : 130
				};
				var CZGHcombox = util.dictionary.SimpleDicFactory
						.createDic(dic)
				CZGHcombox.name = 'CZGH';
				CZGHcombox.fieldLabel = '收款员';
				CZGHcombox.emptyText = "所有员工"
				var JSLXDic = {
					"id" : "JSLXDic",
					"width" : 120
				};
				this.JSLXStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 1,
										'text' : '中途结算'
									}, {
										'value' : 5,
										'text' : '出院结算'
									}]
						});
				var JSLXCombox = new Ext.form.ComboBox({
							name : 'JSLX',
							store : this.JSLXStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "所 有 类 型",
							selectOnFocus : true,
							forceSelection : true,
							width : 120
						});
				var form = new Ext.FormPanel({
					// labelWidth : 60,
					frame : true,
					defaultType : 'textfield',
					layout : 'tableform',
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
							width : 120,
							html : "结&nbsp;&nbsp;&nbsp;&nbsp;算&nbsp;&nbsp;&nbsp;&nbsp;类&nbsp;&nbsp;&nbsp;&nbsp;型&nbsp;&nbsp;&nbsp;&nbsp;:"
						}]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "开始日期:"
								}, new Ext.ux.form.Spinner({
											name : 'KSRQ',
											value : new Date().format('Y-m-d'),
											strategy : {
												xtype : "date"
											}
										})]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "员工:"
								}, CZGHcombox]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "住院号:"
								}, new Ext.form.TextField({
											name : "ZYHM"
										})]
					}, {
						xtype : 'panel',
						rowspan : "2",
						layout : "table",
						// layoutConfig : {
						// rowspan : 2
						// },
						items : [new Ext.Button({
											iconCls : 'query',
											height : 40,
											width : 70,
											text : '查询',
											handler : this.doQuery,
											scope : this
										})]
					}, {
						xtype : 'panel'
					}, {
						xtype : 'panel',
						layout : "table",
						items : [JSLXCombox],
						width : 120
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "终止日期:"
								}, new Ext.ux.form.Spinner({
											name : 'ZZRQ',
											value : new Date().format('Y-m-d'),
											strategy : {
												xtype : "date"
											}
										})]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "科室:"
								}, BRKScombox]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "发票号:"
								}, new Ext.form.TextField({
											name : "FPHM"
										})]
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
						module = this.createModule("mzModule", "IVC0203");
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
				from.findField("KSRQ").setValue(now);
				from.findField("ZZRQ").setValue(now);
				from.findField("JSLX").setValue("");
				from.findField("CZGH").setValue("");
				from.findField("BRKS").setValue("");
				from.findField("ZYHM").setValue("");
				from.findField("FPHM").setValue("");
			},
			doQuery : function() {
				var from = this.form.getForm();
				KSRQ = from.findField("KSRQ").getValue();
				if (KSRQ) {
					if (KSRQ.length != 10) {
						Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
									from.findField("KSRQ").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(KSRQ) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
									from.findField("KSRQ").focus(false, 100);
								}, this);
						return;
					}
				}
				ZZRQ = from.findField("ZZRQ").getValue();
				if (ZZRQ) {
					if (ZZRQ.length != 10) {
						Ext.MessageBox.alert("提示", "终止日期格式不对", function() {
									from.findField("ZZRQ").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(ZZRQ) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "终止日期格式不对", function() {
									from.findField("ZZRQ").focus(false, 100);
								}, this);
						return;
					}
					ZZRQ = ZZRQ + ' 23:59:59';
				}
				JSLX = from.findField("JSLX").getValue();
				CZGH = from.findField("CZGH").getValue();
				BRKS = from.findField("BRKS").getValue();
				ZYHM = from.findField("ZYHM").getValue();
				FPHM = from.findField("FPHM").getValue();
				if (KSRQ != null && ZZRQ != null && KSRQ != "" && ZZRQ != ""
						&& KSRQ > ZZRQ) {
					Ext.MessageBox.alert("提示", "开始日期不能大于终止日期");
					return;
				}
				KSRQCnd = ['ge', ['$', "a.JSRQ"],
						['todate', ['s',KSRQ], ['s','yyyy-mm-dd hh24:mi:ss']]];
				ZZRQCnd = ['le', ['$', "a.JSRQ"],
						['todate', ['s',ZZRQ], ['s','yyyy-mm-dd hh24:mi:ss']]];
				JSLXCnd = ['eq', ['$', "a.JSLX"], ['d', JSLX]];
				BRKSCnd = ['eq', ['$', "b.BRKS"], ['s', BRKS]];
				ZYHMCnd = ['like', ['$', "b.ZYHM"], ['s', "%" + ZYHM + "%"]];
				FPHMCnd = ['like', ['$', "a.FPHM"], ['s', "%" + FPHM + "%"]];
				CZGHCnd = ['eq', ['$', "a.CZGH"], ['s', CZGH]];
				var cnd = [];
				if (KSRQ != null && KSRQ != "") {
					cnd = KSRQCnd;
				}
				if (ZZRQ != null && ZZRQ != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, ZZRQCnd];
					} else {
						cnd = ZZRQCnd;
					}
				}
				if (JSLX != null && JSLX != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, JSLXCnd];
					} else {
						cnd = JSLXCnd;
					}
				}
				if (BRKS != null && BRKS != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, BRKSCnd];
					} else {
						cnd = BRKSCnd;
					}
				}
				if (ZYHM != null && ZYHM != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, ZYHMCnd];
					} else {
						cnd = ZYHMCnd;
					}
				}
				if (FPHM != null && FPHM != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, FPHMCnd];
					} else {
						cnd = FPHMCnd;
					}
				}
				if (CZGH != null && CZGH != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, CZGHCnd];
					} else {
						cnd = CZGHCnd;
					}
				}
				if (cnd.length == 0) {
					cnd = ['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]];
				} else {
					cnd = ['and', cnd,
							['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
				}
				this.listModule.requestData.cnd = cnd;
				this.listModule.refresh();
			},
			doPrintFp : function(fphm) {
				var record = this.listModule.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
					return;
				}
				if (record.data.ZFPB == 1) {
					MyMessageTip.msg("提示", "该结算记录已作废!", true);
					return
				}
				var fphm = record.get('FPHM');
				if(!fphm){
					return ;
				}
				this.CreateDataBill1(fphm);
//					var module = this.createModule("hospitalfpprint",
//							this.refHospitalSettlementPrint)
//					module.fphm = fphm;
//					module.flag="1";
//					module.initPanel();
//					module.doPrint();
			},
			CreateDataBill1 : function(fphm) {
	    		var LODOP=getLodop();  
	    		
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "hospitalPatientSelectionService",
					serviceAction : "printMoth",
						fphm : fphm
					}); 	
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor", "#0000FF");
				LODOP.SET_PRINT_PAGESIZE(0,'21cm','10.1cm',"")
				LODOP.ADD_PRINT_TEXT("10mm", "30mm", "20mm", "5mm", ret.json.ZYH);
				LODOP.ADD_PRINT_TEXT("10mm", "78mm", "20mm", "5mm", "非盈利");
				LODOP.ADD_PRINT_TEXT("10mm", "112mm", "20mm", "5mm", ret.json.BAHM);
				LODOP.ADD_PRINT_TEXT("10mm", "150mm", "20mm", "5mm", ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT("15mm", "30mm", "10mm", "5mm", ret.json.RYYY);
				LODOP.ADD_PRINT_TEXT("15mm", "47mm", "8mm", "5mm", ret.json.RYMM);
				LODOP.ADD_PRINT_TEXT("15mm", "55mm", "8mm", "5mm", ret.json.RYDD);
				LODOP.ADD_PRINT_TEXT("15mm", "70mm", "10mm", "5mm", ret.json.CYYY);
				LODOP.ADD_PRINT_TEXT("15mm", "80mm", "8mm", "5mm", ret.json.CYMM);
				LODOP.ADD_PRINT_TEXT("15mm", "90mm", "8mm", "5mm", ret.json.CYDD);
				LODOP.ADD_PRINT_TEXT("15mm", "130mm", "8mm", "5mm", ret.json.days);
				
				LODOP.ADD_PRINT_TEXT("20mm", "21mm", "20mm", "5mm", ret.json.XM);
				LODOP.ADD_PRINT_TEXT("20mm", "55mm", "15mm", "5mm", ret.json.XB);//性别
				LODOP.ADD_PRINT_TEXT("20mm", "74mm", "30mm", "5mm", ret.json.JSFS);//结算方式
				LODOP.ADD_PRINT_TEXT("20mm", "136mm", "40mm", "5mm", ret.json.YLZH);//社会保障号码
				//明细打印
				LODOP.ADD_PRINT_TEXT("32mm", "13mm","26mm", "5mm", ret.json.XMMC1);
				LODOP.ADD_PRINT_TEXT("32mm", "40mm", "20mm", "5mm", ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT("37mm", "13mm","26mm", "5mm", ret.json.XMMC4);
				LODOP.ADD_PRINT_TEXT("37mm", "40mm", "20mm", "5mm", ret.json.XMJE4);
				LODOP.ADD_PRINT_TEXT("42mm", "13mm","26mm", "5mm", ret.json.XMMC7);
				LODOP.ADD_PRINT_TEXT("42mm", "40mm", "20mm", "5mm", ret.json.XMJE7);				
				LODOP.ADD_PRINT_TEXT("47mm", "13mm","26mm", "5mm", ret.json.XMMC10);
				LODOP.ADD_PRINT_TEXT("47mm", "40mm", "20mm", "5mm", ret.json.XMJE10);
				LODOP.ADD_PRINT_TEXT("52mm", "13mm","26mm", "5mm", ret.json.XMMC13);
				LODOP.ADD_PRINT_TEXT("52mm", "40mm", "20mm", "5mm", ret.json.XMJE13);				
				LODOP.ADD_PRINT_TEXT("57mm", "13mm","26mm", "5mm", ret.json.XMMC16);
				LODOP.ADD_PRINT_TEXT("57mm", "40mm", "20mm", "5mm", ret.json.XMJE16);
				
				LODOP.ADD_PRINT_TEXT("32mm", "68mm", "26mm", "5mm", ret.json.XMMC2);
				LODOP.ADD_PRINT_TEXT("32mm", "96mm", "20mm", "5mm", ret.json.XMJE2);
				LODOP.ADD_PRINT_TEXT("37mm", "68mm", "26mm", "5mm", ret.json.XMMC5);
				LODOP.ADD_PRINT_TEXT("37mm", "96mm", "20mm", "5mm", ret.json.XMJE5);
				LODOP.ADD_PRINT_TEXT("42mm", "68mm", "26mm", "5mm", ret.json.XMMC8);
				LODOP.ADD_PRINT_TEXT("42mm", "96mm", "20mm", "5mm", ret.json.XMJE8);				
				LODOP.ADD_PRINT_TEXT("47mm", "68mm", "26mm", "5mm", ret.json.XMMC11);
				LODOP.ADD_PRINT_TEXT("47mm", "96mm", "20mm", "5mm", ret.json.XMJE11);
				LODOP.ADD_PRINT_TEXT("52mm", "68mm", "26mm", "5mm", ret.json.XMMC14);
				LODOP.ADD_PRINT_TEXT("52mm", "96mm", "20mm", "5mm", ret.json.XMJE14);				
				LODOP.ADD_PRINT_TEXT("57mm", "68mm", "26mm", "5mm", ret.json.XMMC17);
				LODOP.ADD_PRINT_TEXT("57mm", "96mm", "20mm", "5mm", ret.json.XMJE17);

				LODOP.ADD_PRINT_TEXT("32mm", "124mm", "26mm", "5mm", ret.json.XMMC3);
				LODOP.ADD_PRINT_TEXT("32mm", "151mm", "20mm", "5mm", ret.json.XMJE3);
				LODOP.ADD_PRINT_TEXT("37mm", "124mm", "26mm", "5mm", ret.json.XMMC6);
				LODOP.ADD_PRINT_TEXT("37mm", "151mm", "20mm", "5mm", ret.json.XMJE6);
				LODOP.ADD_PRINT_TEXT("42mm", "124mm", "26mm", "5mm", ret.json.XMMC9);
				LODOP.ADD_PRINT_TEXT("42mm", "151mm", "20mm", "5mm", ret.json.XMJE9);				
				LODOP.ADD_PRINT_TEXT("47mm", "124mm", "26mm", "5mm", ret.json.XMMC12);
				LODOP.ADD_PRINT_TEXT("47mm", "151mm", "20mm", "5mm", ret.json.XMJE12);
				LODOP.ADD_PRINT_TEXT("52mm", "124mm", "26mm", "5mm", ret.json.XMMC15);
				LODOP.ADD_PRINT_TEXT("52mm", "151mm", "20mm", "5mm", ret.json.XMJE15);				
				LODOP.ADD_PRINT_TEXT("57mm", "124mm", "26mm", "5mm", ret.json.XMMC18);
				LODOP.ADD_PRINT_TEXT("57mm", "151mm", "20mm", "5mm", ret.json.XMJE18);
				//小计金额
				LODOP.ADD_PRINT_TEXT("62mm", "13mm", "20mm", "5mm", "小计：");
				LODOP.ADD_PRINT_TEXT("62mm", "40mm", "40mm", "5mm", ret.json.XJJE1);
				LODOP.ADD_PRINT_TEXT("62mm", "96mm", "40mm", "5mm", ret.json.XJJE2);				
				LODOP.ADD_PRINT_TEXT("62mm", "151mm", "40mm", "5mm", ret.json.XJJE3);
				
				LODOP.ADD_PRINT_TEXT("68mm", "30mm", "80mm", "6mm", ret.json.DXZJE);
				LODOP.ADD_PRINT_TEXT("68mm", "120mm", "30mm", "6mm", ret.json.FYHJ);
				LODOP.ADD_PRINT_TEXT("74mm", "30mm", "20mm", "5mm", ret.json.JKHJ);
				//LODOP.ADD_PRINT_TEXT("74mm", "85mm", "20mm", "5mm", ret.json.BJJE);
				LODOP.ADD_PRINT_TEXT("74mm", "85mm", "20mm", "5mm", ret.json.BJXJ);//zhaojian 2017-09-29 解决补缴金额不显示问题
				LODOP.ADD_PRINT_TEXT("74mm", "130mm", "20mm", "5mm", ret.json.CYTK);

				LODOP.ADD_PRINT_TEXT("80mm", "30mm", "20mm", "5mm", ret.json.YBHJ);
				LODOP.ADD_PRINT_TEXT("80mm", "65mm", "20mm", "5mm", ret.json.GRZHZF);
				LODOP.ADD_PRINT_TEXT("80mm", "95mm", "20mm", "5mm", ret.json.QTYBZF);
				LODOP.ADD_PRINT_TEXT("80mm", "135mm", "20mm", "5mm", ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT("68mm", "145mm", "40mm", "12mm", ret.json.BZ);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 7);
				
				LODOP.ADD_PRINT_TEXT("85mm", "30mm", "60mm", "5mm", ret.json.JGMC);
				LODOP.ADD_PRINT_TEXT("85mm", "100mm", "20mm", "5mm", ret.json.SYY);
				LODOP.ADD_PRINT_TEXT("85mm", "126mm", "20mm", "5mm", ret.json.N);
				LODOP.ADD_PRINT_TEXT("85mm", "138mm", "8mm", "5mm", ret.json.Y);
				LODOP.ADD_PRINT_TEXT("85mm", "148mm", "8mm", "5mm", ret.json.R);
                if(ret.json.njjbjsxx){
            		LODOP.ADD_PRINT_TEXT("54mm", "0mm", "25mm", 20, "统筹支付："+ret.json.njjbjsxx.BCTCZFJE);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
					LODOP.ADD_PRINT_TEXT("54mm", "25mm", "25mm", 20, "大病救助："+ret.json.njjbjsxx.BCDBJZZF);
					LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
					LODOP.ADD_PRINT_TEXT("54mm", "50mm", "25mm", 20, "大病保险："+ret.json.njjbjsxx.BCDBBXZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("54mm", "75mm", "25mm", 20, "民政补助："+ret.json.njjbjsxx.BCMZBZZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("54mm", "100mm", "25mm", 20, "帐户支付："+ret.json.njjbjsxx.BCZHZFZE);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("54mm", "125mm", "25mm", 20, "现金支付："+ret.json.njjbjsxx.BCXZZFZE);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "0mm", "25mm", 20, "帐户支付自付："+ret.json.njjbjsxx.BCZHZFZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "25mm", "25mm", 20, "帐户支付自理："+ret.json.njjbjsxx.BCZHZFZL);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "50mm", "25mm", 20, "现金支付自付："+ret.json.njjbjsxx.BCXJZFZF);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "75mm", "25mm", 20, "现金支付自理："+ret.json.njjbjsxx.BCXJZFZL);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "100mm", "25mm", 20, "医保范围内费用："+ret.json.njjbjsxx.YBFWNFY);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
            		LODOP.ADD_PRINT_TEXT("58mm", "125mm", "50mm", 20, "帐户消费后余额："+ret.json.njjbjsxx.ZHXFHYE);
            		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
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
               		LODOP.ADD_PRINT_TEXT("20mm", "100mm", "30mm", "5mm","("+text+")");
                }
				
				if (LODOP.SET_PRINTER_INDEXA(ret.json.ZYJSDYJMC)){
					if((ret.json.FPYL+"")=='1'){
						LODOP.PREVIEW();
					}else{
						LODOP.PRINT();
					}
				}else{
					LODOP.PREVIEW();
				}
	    	},
			doCards : function() {
				var r = this.listModule.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.data.ZFPB == 1) {
					MyMessageTip.msg("提示", "该结算记录已作废!", true);
					return
				}
				var module = this.createModule("brzkModule", this.cards);
				r.data.JSLX = -1
				module.data = r.data;
				// module.on("commit",this.doFillIn,this);
				var win = module.getWin();
				win.add(module.initPanel());
				module.doFillIn(r.data);
				win.show();
			}
		});