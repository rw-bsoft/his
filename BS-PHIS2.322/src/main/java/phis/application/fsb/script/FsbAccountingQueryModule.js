$package("phis.application.fsb.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.fsb.script.FsbAccountingQueryModule = function(cfg) {
	this.exContext = {};
	phis.application.fsb.script.FsbAccountingQueryModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FsbAccountingQueryModule,
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
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 22,
										items : this.createForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				this.doQuery();
			},
			createForm : function() {
				var form = new Ext.FormPanel({
							labelWidth : 60,
							width : this.width,
							height : this.height,
							items : [{
										autoHeight : true,
										layout : 'column',
										// layoutConfig : {
										// columns : 8,
										// tableAttrs : {
										// border : 2,
										// cellpadding : '0',
										// cellspacing : '0'
										// }
										// },
										defaultType : 'textfield',
										items : this.initConditionFields()
									}]
						})
				this.form = form
				return form
			},
			initConditionFields : function() {
				var dic = {
					"id" : "phis.dictionary.user_zy",
					"sliceType":1,
					"filter" : "['eq',['$','item.properties.manageUnit'],['$','%user.manageUnit.id']]",
					"src" : "MS_YGPJ_FP.YGDM",
					"width" : 130
				};
				var CZGHcombox = util.dictionary.SimpleDicFactory
						.createDic(dic)
				CZGHcombox.name = 'SRGH';
				CZGHcombox.width = 100;
				// CZGHcombox.fieldLabel = '员工';
				var department_zydic = {
					"id" : "phis.dictionary.department_zyyj",
					"filter" : "['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
					"width" : 130
				};
				var BRKScombox = util.dictionary.SimpleDicFactory
						.createDic(department_zydic)
				BRKScombox.name = 'ZXKS';
				BRKScombox.width = 100;
				// BRKScombox.fieldLabel = '科室';
				var items = []
				items.push({
							xtype : 'panel',
							layout : "table",
							items : [{
										xtype : "label",
										width : 65,
										text : "记账日期："
									}, new Ext.ux.form.Spinner({
												name : 'FYRQFrom',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												},
												width : 100
											})],
							width : 165
						})
				items.push({
							xtype : 'panel',
							layout : "table",
							items : [{
										xtype : "label",
										width : 22,
										text : "-至-"
									}, new Ext.ux.form.Spinner({
												name : 'FYRQTo',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												},
												width : 100
											})],
							width : 122
						})
				items.push({
							xtype : 'panel',
							layout : "table",
							items : [{
										xtype : "label",
										width : 36,
										text : "员工："
									}, CZGHcombox],
							width : 136
						})
				items.push({
							xtype : 'panel',
							layout : "table",
							items : [{
										xtype : "label",
										width : 36,
										text : "科室："
									}, BRKScombox],
							width : 136
						})
				items.push({
							xtype : 'panel',
							layout : "table",
							items : [{
										xtype : "label",
										width : 60,
										text : "家床号："
									}, new Ext.form.TextField({
												name : "ZYHM",
												width : 80
											})],
							width : 140
						})
				items.push({
							xtype : "button",
							text : "查询(Q)",
							iconCls : "query",
							scope : this,
							handler : this.doQuery,
							width : 70
						})
				items.push({
							xtype : "button",
							text : "打 印(P)",
							iconCls : "print",
							scope : this,
							handler : this.doPrint,
							width : 70
						})
				return items
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				var date = new Date();
				// module.requestData.cnd = ['eq',['$', "a.FYRQ"],
				// ['todate',date.getFullYear() + "-" + (date.getMonth() + 1) +
				// "-" + date.getDate(), 'yyyy-mm-dd']];
				var listModule = module.initPanel();
				this.listModule = module;
				module.opener = this;
				this.list = module.initPanel();
				return listModule;
			},
			doQuery : function() {
				var form = this.form.getForm();
				var FYRQFrom = form.findField("FYRQFrom").getValue();
				var FYRQTo = form.findField("FYRQTo").getValue();
				var ZXKS = form.findField("ZXKS").getValue();
				var SRGH = form.findField("SRGH").getValue();
				var ZYHM = form.findField("ZYHM").getValue();
				if (ZYHM) {
					var data = {
						"ZYHM" : ZYHM
					};
					var r = phis.script.rmi.miniJsonRequestSync({// 补住院号
						serviceId : "fsbPaymentProcessingService",
						serviceAction : "getCompensationNumber",
						body : data
					});
					form.findField("ZYHM").setValue(r.json.JCHM);
				ZYHM = r.json.JCHM;
				}
				if (FYRQFrom != null && FYRQTo != null && FYRQFrom != ""
						&& FYRQTo != "" && FYRQFrom > FYRQTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				if (FYRQFrom != null) {
					var str = new Date(FYRQFrom);
					if (str == "Invalid Date") {
						Ext.MessageBox.alert("提示", "开始时间不合法!", true);
						return;
					}
				}
				if (FYRQTo != null) {
					var str = new Date(FYRQTo);
					if (str == "Invalid Date") {
						Ext.MessageBox.alert("提示", "结束时间不合法!", true);
						return;
					}
				}
				this.body = {};
				this.body.FYRQFrom = FYRQFrom
				this.body.FYRQTo = FYRQTo
				this.body.ZXKS = ZXKS
				this.body.SRGH = SRGH
				this.body.ZYHM = ZYHM
				this.listModule.requestData.body = this.body;
				// FYRQFromCnd = ['ge', ['$', "a.FYRQ"],
				// ['todate', FYRQFrom, 'yyyy-mm-dd']];
				// FYRQToCnd = ['le', ['$', "a.FYRQ"],
				// ['todate', FYRQTo, 'yyyy-mm-dd']];
				// ZXKSCnd = ['eq', ['$', "a.ZXKS"], ['d', ZXKS]];
				// SRGHCnd = ['eq', ['$', "a.SRGH"], ['d', SRGH]];
				// ZYHMCnd = ['eq', ['$', "b.ZYHM"], ['s', ZYHM]];
				// var cnd = [];
				// if (FYRQFrom != null && FYRQFrom != "") {
				// if (cnd.length > 0) {
				// cnd = ['and', cnd, FYRQFromCnd];
				// } else {
				// cnd = FYRQFromCnd;
				// }
				// }
				// if (FYRQTo != null && FYRQTo != "") {
				// if (cnd.length > 0) {
				// cnd = ['and', cnd, FYRQToCnd];
				// } else {
				// cnd = FYRQToCnd;
				// }
				// }
				// if (ZXKS != null && ZXKS != "") {
				// if (cnd.length > 0) {
				// cnd = ['and', cnd, ZXKSCnd];
				// } else {
				// cnd = ZXKSCnd;
				// }
				// }
				//
				// if (SRGH != null && SRGH != "") {
				// if (cnd.length > 0) {
				// cnd = ['and', cnd, SRGHCnd];
				// } else {
				// cnd = SRGHCnd;
				// }
				// }
				// if (ZYHM != null && ZYHM != "") {
				// if (cnd.length > 0) {
				// cnd = ['and', cnd, ZYHMCnd];
				// } else {
				// cnd = ZYHMCnd;
				// }
				// }
				// if (cnd.length == 0) {
				// cnd = null;
				// }
				// this.listModule.requestData.cnd = cnd;
				this.listModule.refresh();
			},
			doPrint : function() {
				// var form = Ext.getCmp(this.conditionFormId).getForm()
				// if (!form.isValid()) {
				// return
				// }
				// var url = this.printurl +
				// ".print?pages=costAccountingDetail";
				// var items = form.items;
				// for (var i = 0; i < items.getCount(); i++) {
				// var f = items.get(i)
				// if (f.getName() == "type" && f.getValue() == "1") {
				// Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
				// "x-mask-loading")
				// }
				// if (f.getValue() != null && f.getValue != '') {
				// if (f.getName() == "beginDate"
				// || f.getName() == "endDate") {
				// url += "&" + f.getName() + "="
				// + f.getValue().format("Y-m-d")
				// } else {
				// url += "&" + f.getName() + "=" + f.getValue();
				// }
				// }
				//
				// }
				//var url = this.printurl + "*.print?pages=phis.prints.jrxml.CostAccountingDetail";
				var pages="phis.prints.jrxml.FsbCostAccountingDetail";
				 var url="resources/"+pages+".print?type=1";
				var form = this.form.getForm();
				var FYRQFrom = form.findField("FYRQFrom");
				var FYRQTo = form.findField("FYRQTo");
				var ZXKS = form.findField("ZXKS");
				var SRGH = form.findField("SRGH");
				var ZYHM = form.findField("ZYHM");
				//var ypmc = encodeURI(encodeURI((r.get("YPMC"))));
				//url += "&ksrq=" + this.opener.opener.tbar[0].getValue().format('Ymd'); 
				 var ZXKSValue=ZXKS.getValue();
				 if(ZXKSValue==null || ZXKSValue=="" || ZXKSValue=="null"){
					 ZXKSValue="null";
				 }
				 var SRGHValue=SRGH.getValue();
				 if(SRGHValue==null || SRGHValue=="" || SRGHValue=="null"){
					 SRGHValue="null";
				 }
				 var ZYHMValue=ZYHM.getValue();
				 if(ZYHMValue==null || ZYHMValue=="" || ZYHMValue=="null"){
					 ZYHMValue="null";
				 }
				 url += "&" + FYRQFrom.getName() + "=" + encodeURI(FYRQFrom.getValue()); 
				 url += "&" + FYRQTo.getName() + "=" + encodeURI(FYRQTo.getValue());
				 url += "&" + ZXKS.getName() + "=" + encodeURI(ZXKSValue);
				 url += "&" + SRGH.getName() + "=" + encodeURI(SRGHValue);
			     url += "&" + ZYHM.getName() + "=" +encodeURI(ZYHMValue);

				url += "&temp=" + new Date().getTime()
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				//预览LODOP.PREVIEW();
				//预览LODOP.PRINT();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览
				LODOP.PREVIEW();
			},

			doClose : function() {
				this.opener.closeCurrentTab();
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
			doReSet : function() {
				var year = new Date().getFullYear();
				var month = new Date().getMonth() >= 9
						? (new Date().getMonth() + 1)
						: "0" + (new Date().getMonth() + 1)
				var date = new Date().getDate() >= 10
						? (new Date().getDate())
						: "0" + (new Date().getDate())
				var now = year + "-" + month + "-" + date;
				var from = this.form.getForm();

				from.findField("FYRQFrom").setValue(now);
				from.findField("FYRQTo").setValue(now);
				from.findField("SRGH").setValue("");
				from.findField("ZXKS").setValue("");
				from.findField("ZYHM").setValue("");
			}
		});
