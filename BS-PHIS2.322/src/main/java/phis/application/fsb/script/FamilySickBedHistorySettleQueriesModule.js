/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.fsb.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.fsb.script.FamilySickBedHistorySettleQueriesModule = function(cfg) {
	this.exContext = {};
	phis.application.fsb.script.FamilySickBedHistorySettleQueriesModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FamilySickBedHistorySettleQueriesModule,
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
//				var BRKSdic = {
//					"id" : "phis.dictionary.department_zy",
//					"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
//					"src" : "SYS_Office.ID",
//					"width" : 130
//				};
//				var BRKScombox = util.dictionary.SimpleDicFactory
//						.createDic(BRKSdic)
//				BRKScombox.name = 'BRKS';
//				BRKScombox.fieldLabel = '病人科室';
//				BRKScombox.emptyText = "所有科室"
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
				CZGHcombox.emptyText = "所有员工";
				var ZFGHcombox = util.dictionary.SimpleDicFactory
				.createDic(dic);
				ZFGHcombox.name = 'ZFGH';
				ZFGHcombox.fieldLabel = '作废人';
				ZFGHcombox.emptyText = "所有员工"
				
//				var JSLXDic = {
//					"id" : "JSLXDic",
//					"width" : 120
//				};
				this.JSLXStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 1,
										'text' : '中途结算'
									}, {
										'value' : 5,
										'text' : '撤床结算'
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
									html : "结算员工:"
								}, CZGHcombox]
					}, {
						xtype : 'panel',
						layout : "table",
						items : [{
									xtype : "panel",
									width : 60,
									html : "家床号:"
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
									html : "作废工号:"
								}, ZFGHcombox]
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
				from.findField("ZFGH").setValue("");
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
				ZFGH = from.findField("ZFGH").getValue();
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
				ZFGHCnd = ['eq', ['$', "a.ZFGH"], ['s', ZFGH]];
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
				if (ZFGH != null && ZFGH != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, ZFGHCnd];
					} else {
						cnd = ZFGHCnd;
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
	    		LODOP.PRINT_INITA(3,8,643,680,"家床发票套打");
	    		
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "familySickBedPatientSelectionService",
					serviceAction : "printMoth",
						fphm : fphm
					}); 	
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return null;
				}
				LODOP.SET_PRINT_STYLE("FontColor","#0000FF");
				LODOP.ADD_PRINT_TEXT(29,70,160,25,ret.json.FPHM);
				LODOP.ADD_PRINT_TEXT(55,70,100,25,ret.json.ZYHM);
				LODOP.ADD_PRINT_TEXT(55,410,40,25,ret.json.N);
				LODOP.ADD_PRINT_TEXT(55,470,40,25,ret.json.Y);
				LODOP.ADD_PRINT_TEXT(55,530,40,25,ret.json.R);
				LODOP.ADD_PRINT_TEXT(80,70,160,25,ret.json.RYLB);
				LODOP.ADD_PRINT_TEXT(80,410,160,25,ret.json.GZDW);
				LODOP.ADD_PRINT_TEXT(105,70,160,25,ret.json.BRXM);
				LODOP.ADD_PRINT_TEXT(105,410,160,25,ret.json.ZYRQ);
				LODOP.ADD_PRINT_TEXT(140,70,100,25,ret.json.SFXM1);
				LODOP.ADD_PRINT_TEXT(165,70,100,25,ret.json.SFXM2);
				LODOP.ADD_PRINT_TEXT(190,70,100,25,ret.json.SFXM3);
				LODOP.ADD_PRINT_TEXT(215,70,100,25,ret.json.SFXM4);
				LODOP.ADD_PRINT_TEXT(240,70,100,25,ret.json.SFXM5);
				LODOP.ADD_PRINT_TEXT(265,70,100,25,ret.json.SFXM6);
				LODOP.ADD_PRINT_TEXT(290,70,100,25,ret.json.SFXM7);
				LODOP.ADD_PRINT_TEXT(315,70,100,25,ret.json.SFXM8);
				LODOP.ADD_PRINT_TEXT(340,70,100,25,ret.json.SFXM9);
				LODOP.ADD_PRINT_TEXT(365,70,100,25,ret.json.SFXM10);
				LODOP.ADD_PRINT_TEXT(390,70,100,25,ret.json.SFXM11);
				LODOP.ADD_PRINT_TEXT(415,70,100,25,ret.json.SFXM12);
				LODOP.ADD_PRINT_TEXT(440,70,100,25,ret.json.SFXM13);
				LODOP.ADD_PRINT_TEXT(465,70,100,25,ret.json.SFXM14);
				LODOP.ADD_PRINT_TEXT(490,70,100,25,ret.json.SFXM15);
				LODOP.ADD_PRINT_TEXT(515,70,100,25,ret.json.SFXM16);
				LODOP.ADD_PRINT_TEXT(140,175,100,25,ret.json.XMJE1);
				LODOP.ADD_PRINT_TEXT(165,175,100,25,ret.json.XMJE2);
				LODOP.ADD_PRINT_TEXT(190,175,100,25,ret.json.XMJE3);
				LODOP.ADD_PRINT_TEXT(215,175,100,25,ret.json.XMJE4);
				LODOP.ADD_PRINT_TEXT(240,175,100,25,ret.json.XMJE5);
				LODOP.ADD_PRINT_TEXT(265,175,100,25,ret.json.XMJE6);
				LODOP.ADD_PRINT_TEXT(290,175,100,25,ret.json.XMJE7);
				LODOP.ADD_PRINT_TEXT(315,175,100,25,ret.json.XMJE8);
				LODOP.ADD_PRINT_TEXT(340,175,100,25,ret.json.XMJE9);
				LODOP.ADD_PRINT_TEXT(365,175,100,25,ret.json.XMJE10);
				LODOP.ADD_PRINT_TEXT(390,175,100,25,ret.json.XMJE11);
				LODOP.ADD_PRINT_TEXT(415,175,100,25,ret.json.XMJE12);
				LODOP.ADD_PRINT_TEXT(440,175,100,25,ret.json.XMJE13);
				LODOP.ADD_PRINT_TEXT(465,175,100,25,ret.json.XMJE14);
				LODOP.ADD_PRINT_TEXT(490,175,100,25,ret.json.XMJE15);
				LODOP.ADD_PRINT_TEXT(515,175,100,25,ret.json.XMJE16);
				LODOP.ADD_PRINT_TEXT(515,280,100,25,ret.json.FYHJ);
				LODOP.ADD_PRINT_TEXT(540,114,317,25,ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT(565,70,500,65,ret.json.BZ);
				LODOP.ADD_PRINT_TEXT(635,450,120,25,ret.json.SYY);
				LODOP.ADD_PRINT_TEXT(140,470,100,25,ret.json.ZFJE);
				LODOP.ADD_PRINT_TEXT(165,470,100,25,ret.json.BNZHZF);
				LODOP.ADD_PRINT_TEXT(190,470,100,25,ret.json.LNZHZF);
				LODOP.ADD_PRINT_TEXT(215,470,100,25,ret.json.YBZH);
				LODOP.ADD_PRINT_TEXT(240,470,100,25,ret.json.YBHJ);
				LODOP.ADD_PRINT_TEXT(265,470,100,25,ret.json.JKHJ);
				LODOP.ADD_PRINT_TEXT(290,470,100,25,ret.json.FYHJ);
				LODOP.ADD_PRINT_TEXT(315,470,100,25,ret.json.CYBJ);
				LODOP.ADD_PRINT_TEXT(340,470,100,25,ret.json.BJXJ);
				LODOP.ADD_PRINT_TEXT(365,470,100,25,ret.json.CYTK);
				LODOP.ADD_PRINT_TEXT(390,470,100,25,ret.json.TKXJ);
				if (LODOP.SET_PRINTER_INDEXA(ret.json.JCJSDYJMC)){
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