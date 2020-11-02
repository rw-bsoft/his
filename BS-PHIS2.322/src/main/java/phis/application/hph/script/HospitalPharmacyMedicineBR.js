$package("phis.application.hph.script");

$import("phis.script.SimpleModule");

phis.application.hph.script.HospitalPharmacyMedicineBR = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.hph.script.HospitalPharmacyMedicineBR.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.hph.script.HospitalPharmacyMedicineBR,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						// var ret = phis.script.rmi.miniJsonRequestSync({
						// serviceId : this.serviceId,
						// serviceAction : this.queryWardActionID
						// });
						// if (ret.code > 300) {
						// this.processReturnMsg(ret.code, ret.msg,
						// this.initPanel);
						// return null;
						// }
						// var ret_lyks = util.rmi.miniJsonRequestSync({
						// serviceId : this.serviceId,
						// serviceAction : this.queryMedicineDepartmentActionID
						// });
						// if (ret_lyks.code > 300) {
						// this.processReturnMsg(ret_lyks.code, ret_lyks.msg,
						// this.initPanel);
						// return null;
						// }

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
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'west',
								width : 470,
								items : this.getLeftList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '',
								region : 'center',
								items : this.getRightList()
							} ],
							tbar : this.getTbar()
						});
						this.panel = panel;
						this.panel.on("afterrender", this.onReady, this)
						return panel;
					},
					onReady : function() {
						this.doQueryLoad();
					},
					getLeftList : function() {
						this.leftList = this.createModule("leftList",
								this.refLeftList);
						this.leftList.on("recordClick", this.onRecordClick,
								this);
						return this.leftList.initPanel();
					},
					getRightList : function() {
						this.rightList = this.createModule("rightList",
								this.refRightList);
						return this.rightList.initPanel();
					},
					onRecordClick : function(data) {
						this.rightList.requestData.body = data;
						this.rightList.requestData.pageNo = 1;
						this.rightList.loadData();
					},
					getTbar : function() {
						var tbar = new Ext.Toolbar();
						// tbar.enableOverflow=true;
						var lx = this.createDicField({
							"src" : "YF_FYJL_BQFY.FYLX",
							"width" : 100,
							"id" : "phis.dictionary.hairMedicineWay",
							"emptyText" : "全部发药方式"
						})
						var yf = this.createDicField({
							"width" : 100,
							"id" : "phis.dictionary.wardPharmacy",
							"filter" : [ 'eq', [ '$', 'item.properties.BQDM' ],
									[ 'l', this.mainApp['phis'].wardId ] ],
							"emptyText" : "全部药房"
						})
						var simple = new Ext.FormPanel({
							labelWidth : 50, // label settings here cascade
							title : '',
							layout : "table",
							bodyStyle : 'padding:5px 5px 5px 5px',
							defaults : {},
							defaultType : 'textfield',
							items : [ {
								xtype : "label",
								forId : "window",
								text : "日期 "
							}, new Ext.ux.form.Spinner({
								fieldLabel : '发药时间开始',
								name : 'dateFrom',
								value : new Date().format('Y-m-d'),
								strategy : {
									xtype : "date"
								},
								width : 100
							}), {
								xtype : "label",
								forId : "window",
								text : "至"
							}, new Ext.ux.form.Spinner({
								fieldLabel : '发药时间结束',
								name : 'dateTo',
								value : new Date().format('Y-m-d'),
								strategy : {
									xtype : "date"
								},
								width : 100
							}), {
								xtype : "label",
								forId : "window",
								text : "发药方式"
							}, lx, {
								xtype : "label",
								forId : "window",
								text : "药房"
							}, yf ]
						});
						this.simple = simple;
						this.lx = lx;
						this.yf = yf;
						tbar.add(simple, this.createButtons());
						return tbar;
					},
					// 查询
					doQuery : function() {
						var dateFrom = this.simple.items.get(1).getValue();
						var dateTo = this.simple.items.get(3).getValue();
						var FYFS = this.simple.items.get(5).getValue();
						var YF = this.simple.items.get(7).getValue();
						var bq = this.mainApp['phis'].wardId;

						if (dateFrom != null && dateTo != null
								&& dateFrom != "" && dateTo != "") {
							try {
								var df = new Date(Date.parse(dateFrom.replace(
										/-/g, "/")));
								var dt = new Date(Date.parse(dateTo.replace(
										/-/g, "/")));
								if (df.getTime() > dt.getTime()) {
									Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
									return;
								}
							} catch (e) {
								MyMessageTip.msg("提示",
										"时间格式错误,正确格式2000-01-01 15:01:01", true);
								return;
							}

						} else {
							Ext.MessageBox.alert("提示", "开始日期或者终止日期不能为空!");
							return;
						}
						var body = {
							"dateFrom" : dateFrom + " 00:00:00",
							"dateTo" : dateTo + " 23:59:59",
							"FYFS" : FYFS,
							"YF" : YF,
							"bq" : bq,
							"FYSB" : "FY"
						};

						if (this.leftList) {
							this.leftList.requestData.body = body;
							this.leftList.body = body;
							this.leftList.loadData();
							this.rightList.clear();
						}

					},
					// 页面加载就查询
					doQueryLoad : function() {
						var dateFrom = this.simple.items.get(1).getValue();
						var dateTo = this.simple.items.get(3).getValue();
						var FYFS = this.simple.items.get(5).getValue();
						var YF = this.simple.items.get(7).getValue();
						var bq = this.mainApp['phis'].wardId;

						if (dateFrom != null && dateTo != null
								&& dateFrom != "" && dateTo != "") {
							try {
								var df = new Date(Date.parse(dateFrom.replace(
										/-/g, "/")));
								var dt = new Date(Date.parse(dateTo.replace(
										/-/g, "/")));
								if (df.getTime() > dt.getTime()) {
									Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
									return;
								}
							} catch (e) {
								MyMessageTip.msg("提示",
										"时间格式错误,正确格式2000-01-01 15:01:01", true);
								return;
							}

						}
						var body = {
							"dateFrom" : dateFrom + " 00:00:00",
							"dateTo" : dateTo + " 23:59:59",
							"FYFS" : FYFS,
							"YF" : YF,
							"bq" : bq,
							"FYSB" : "FY"
						};

						if (this.leftList) {
							this.leftList.requestData.body = body;
							this.leftList.body = body;
							this.rightList.requestData.body = body;
							this.leftList.loadData();
							this.rightList.loadData();
						}

					},
					createDicField : function(dic) {
						if (dic.id == "phis.dictionary.department_bq") {
							var arr = new Array();
							arr.push(31);
							// dic.filter="['eq',['$map',['s','JGID']],['s','1']]";
						}
						var cls = "util.dictionary.";
						if (!dic.render) {
							cls += "Simple";
						} else {
							cls += dic.render
						}
						cls += "DicFactory"
						$import(cls)
						var factory = eval("(" + cls + ")")
						var field = factory.createDic(dic)
						return field
					},
					doPrint : function() {
						var dateFrom = this.simple.items.get(1).getValue();
						var dateTo = this.simple.items.get(3).getValue();
						if (!dateFrom || !dateTo) {
							Ext.MessageBox.alert("提示", "开始日期或者终止日期不能为空!");
							return;
						}
						var FYFS = this.simple.items.get(5).getValue();
						var YF = this.simple.items.get(7).getValue();
						if(FYFS==null||FYFS==""){
						FYFS=0
						}
						if(YF==null||YF==""){
						YF=0
						}
						var bq = this.mainApp['phis'].wardId;
						var r = this.leftList.getSelectedRecord();
						var ZYH=0;
						if (r) {
							ZYH = r.get("ZYH");
						} 
						var pages="phis.prints.jrxml.HospitalPharmacyMedicineBR";
						var url = "resources/" + pages + ".print?type=1";
						url += "&dateFrom=" + dateFrom + "&dateTo=" + dateTo
								+ "&FYFS=" + FYFS + "&YF=" + YF + "&ZYH="
								+ ZYH + "&bq=" + bq;
						var LODOP = getLodop();
						LODOP.PRINT_INIT("打印控件");
						LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
						// 预览LODOP.PREVIEW();
						// 预览LODOP.PRINT();
						// LODOP.PRINT_DESIGN();
						LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
										{
											url : url,
											httpMethod : "get"
										}));
						LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
						//预览
						LODOP.PREVIEW();
						
						module.initPanel();
						module.doPrint();
					},
					doNew : function() {
						this.simple.items.get(5).setValue("");
						this.simple.items.get(7).setValue("");
						this.simple.items.get(1).setValue(
								new Date().format('Y-m-d'));
						this.simple.items.get(3).setValue(
								new Date().format('Y-m-d'));
					}
				});