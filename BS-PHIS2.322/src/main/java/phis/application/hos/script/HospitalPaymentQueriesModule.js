/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.hos.script")

//$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
//		"app.desktop.Module");
$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout", "phis.script.widgets.Spinner",
		"app.desktop.Module");

phis.application.hos.script.HospitalPaymentQueriesModule = function(cfg) {
	this.exContext = {};
	phis.application.hos.script.HospitalPaymentQueriesModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.hos.script.HospitalPaymentQueriesModule,
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
				var paymentdic = {
					"id" : "phis.dictionary.payment",
					"filter" : "['and',['eq',['$','item.properties.SYLX'],['s',2]],['eq',['$','item.properties.ZFBZ'],['s',0]],['eq',['$','item.properties.HBWC'],['s',0]]]",
					"width" : 130
				};
				var FKFScombox = util.dictionary.SimpleDicFactory
						.createDic(paymentdic)
				FKFScombox.name = 'FKFS';
				FKFScombox.fieldLabel = '缴款方式';
				var dic = {
					"id" : "phis.dictionary.user_zy",
					"sliceType":1,
					"filter" : "['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]",
					"src" : "MS_YGPJ_FP.YGDM",
					"width" : 130
				};
				var CZGHcombox = util.dictionary.SimpleDicFactory
						.createDic(dic)
				CZGHcombox.name = 'CZGH';
				CZGHcombox.fieldLabel = '收款员';
				var department_zydic = {
					"id" : "phis.dictionary.department_zy",
					"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
					"width" : 130
				};
				var BRKScombox = util.dictionary.SimpleDicFactory
						.createDic(department_zydic)
				BRKScombox.name = 'BRKS';
				BRKScombox.fieldLabel = '科室';
				var form = new Ext.FormPanel({
							labelWidth : 60,
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
											width : 65,
											html : "缴款日期:"
										}, new Ext.ux.form.Spinner({
													name : 'JKRQFrom',
													value : new Date()
															.format('Y-m-d'),
													strategy : {
														xtype : "date"
													}
												})]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 65,
											html : "至"
										}, new Ext.ux.form.Spinner({
													name : 'JKRQTo',
													value : new Date()
															.format('Y-m-d'),
													strategy : {
														xtype : "date"
													}
												})]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 65,
											html : "缴款方式:"
										}, FKFScombox]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [new Ext.Button({
											iconCls : 'query',
											width : 80,
											text : '查询',
											handler : this.doQuery,
											scope : this
										})]
							}, {
								xtype : 'panel'
							}, {
								xtype : 'panel'
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 65,
											html : "收款员:"
										}, CZGHcombox]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 65,
											html : "科室:"
										}, BRKScombox]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 60,
											html : "住院号码:"
										}, new Ext.form.TextField({
													name : "ZYHM"
												})]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [new Ext.Button({
											iconCls : 'print',
											width : 80,
											text : '打印',
											handler : this.doPrint,
											scope : this
										})]
							}]
						});
				this.form = form
				return form
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				var date = new Date();
				module.requestData.cnd = [
						'eq',
						['$', "a.JKRQ"],
						[
								'todate',
								date.getFullYear() + "-"
										+ (date.getMonth() + 1) + "-"
										+ date.getDate(), 'yyyy-mm-dd']];
				var listModule = module.initPanel();
				this.listModule = module;
				return listModule;
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
			doQuery : function() {
				var from = this.form.getForm();
				var JKRQFrom = from.findField("JKRQFrom").getValue();
				if (JKRQFrom) {
					if (JKRQFrom.length != 10) {
						Ext.MessageBox.alert("提示", "缴款开始日期格式不对", function() {
									from.findField("JKRQFrom")
											.focus(false, 100);
								}, this);
						return;
					}
					if (new Date(JKRQFrom) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "缴款开始日期格式不对", function() {
									from.findField("JKRQFrom")
											.focus(false, 100);
								}, this);
						return;
					}
				}
				var JKRQTo = from.findField("JKRQTo").getValue();
				if (JKRQTo) {
					if (JKRQTo.length != 10) {
						Ext.MessageBox.alert("提示", "缴款结束日期格式不对", function() {
									from.findField("JKRQTo").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(JKRQTo) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "缴款结束日期格式不对", function() {
									from.findField("JKRQTo").focus(false, 100);
								}, this);
						return;
					}
					JKRQTo = JKRQTo + ' 23:59:59';
				}
				var FKFS = from.findField("FKFS").getValue();

				var CZGH = from.findField("CZGH").getValue();
				var BRKS = from.findField("BRKS").getValue();

				var ZYHM = from.findField("ZYHM").getValue();
				if (JKRQFrom != null && JKRQTo != null && JKRQFrom != ""
						&& JKRQTo != "" && JKRQFrom > JKRQTo) {
					Ext.MessageBox.alert("提示", "开始日期不能大于终止日期");
					return;
				}
				var JKRQFromCnd = ['ge', ['$', "a.JKRQ"],
						['todate', ['s',JKRQFrom], ['s','yyyy-mm-dd hh24:mi:ss']]];
				var JKRQToCnd = ['le', ['$', "a.JKRQ"],
						['todate', ['s',JKRQTo], ['s','yyyy-mm-dd hh24:mi:ss']]];
				var FKFSCnd = ['eq', ['$', "a.JKFS"], ['d', FKFS]];
				var CZGHCnd = ['eq', ['$', "a.CZGH"], ['s', CZGH]];
				var BRKSCnd = ['eq', ['$', "b.BRKS"], ['d', BRKS]];
				var ZYHMCnd = ['like', ['$', "b.ZYHM"], ['s', "%" + ZYHM + "%"]];
				var cnd = [];
				if (JKRQFrom != null && JKRQFrom != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, JKRQFromCnd];
					} else {
						cnd = JKRQFromCnd;
					}
				}
				if (JKRQTo != null && JKRQTo != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, JKRQToCnd];
					} else {
						cnd = JKRQToCnd;
					}
				}
				if (FKFS != null && FKFS != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, FKFSCnd];
					} else {
						cnd = FKFSCnd;
					}
				}
				if (CZGH != null && CZGH != "") {
					if (cnd.length > 0) {
						cnd = ['and', cnd, CZGHCnd];
					} else {
						cnd = CZGHCnd;
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
				if (cnd.length == 0) {
					cnd = null;
				}
				this.listModule.requestData.cnd = cnd;
				this.listModule.refresh();
			},
			doNew : function() {
				var year = new Date().getFullYear();
				var month = new Date().getMonth() >= 9
						? (new Date().getMonth() + 1)
						: "0" + (new Date().getMonth() + 1)
				var date = new Date().getDate() >= 10
						? (new Date().getDate())
						: "0" + (new Date().getDate())
				var now = year + "-" + month + "-" + date;
				var from = this.form.getForm();

				from.findField("JKRQFrom").setValue(now);
				from.findField("JKRQTo").setValue(now);
				from.findField("FKFS").setValue("");
				from.findField("CZGH").setValue("");
				from.findField("BRKS").setValue("");
				from.findField("ZYHM").setValue("");
			},
			doPrint : function() {
				this.listModule.doPrint();
//				var r = this.listModule.getSelectedRecord()
//				if (r == null) {
//					return;
//				}
//				var module = this.createModule("paymentprint", "HOS11")
//				var form = this.form.getForm();
//				if (form) {
//					module.jkxh = r.get("JKXH");
//					module.initPanel();
//					module.doPrint();
//				} else {
//					MyMessageTip.msg("提示", "打印失败：无效的缴款信息!", true);
//				}
			}
		});