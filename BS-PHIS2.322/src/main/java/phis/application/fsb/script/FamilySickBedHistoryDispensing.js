$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");
				   
phis.application.fsb.script.FamilySickBedHistoryDispensing = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.fsb.script.FamilySickBedHistoryDispensing.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedHistoryDispensing,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].pharmacyId == null
						|| this.mainApp['phis'].pharmacyId == ""
						|| this.mainApp['phis'].pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				// 进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.initializationServiceID,
							serviceAction : this.initializationServiceActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				
				Ext.apply(this,this.loadSystemParams({"privates":['JCKCGL']}));
				if(this.JCKCGL!=3){
				Ext.Msg.alert("提示", "当前设置药品不在药房发药!");
				return null;}
				if (this.panel) {
					return this.panel;
				}
				
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
										width : 270,
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
			getLeftList : function() {
				this.leftList = this.createModule("leftList", this.refLeftList);
				this.leftList.on("recordClick", this.onRecordClick, this);
				return this.leftList.initPanel();
			},
			getRightList : function() {
				this.rightList = this.createModule("rightList",
						this.refRightList);
				return this.rightList.initPanel();
			},
			onRecordClick : function(jlid) {
				this.rightList.requestData.cnd = ['eq', ['$', 'JLID'],
						['d', jlid]];
				this.rightList.loadData();
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var yg = this.createDicField({
							"src" : "JC_FYJL_LSCX.FYGH",
							"defaultValue" : 0,
							"width" : 100,
							editable:false,
							"id" : "phis.dictionary.user_YFYW_FYCX"
						})
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
														.format('Y-m-d H:i:s'),
												strategy : {
													xtype : "dateTime"
												},
												width : 150
											}), {
										xtype : "label",
										forId : "window",
										text : "操作员"
									}, yg]
						});
				this.simple = simple;
				this.yg = yg;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			// 查询
			doQuery : function() {
				var datefrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				var yg = this.simple.items.get(5).getValue();

				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "") {
					try {
						var df = new Date(Date.parse(datefrom
								.replace(/-/g, "/")));
						var dt = new Date(Date.parse(dateTo.replace(/-/g, "/")));
						if (df.getTime() > dt.getTime()) {
							Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
							return;
						}
					} catch (e) {
						MyMessageTip.msg("提示",
								"时间格式错误,正确格式2000-01-01 01:01:01!", true);
						return;
					}

				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge',
							['$', "str(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le',
							['$', "str(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "str(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', datefrom]],
							['le', ['$', "str(FYSJ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dateTo]]];
				}
				var ygCnd = null;
				if (yg != null && yg != "" && yg != undefined) {
					ygCnd = ['eq', ['$', 'FYGH'], ['s', yg]]
				}
				if (this.leftList) {
					var cnd = this.leftList.initCnd;
					if (timeCnd != null) {
						if (cnd != null) {
							cnd = ['and', cnd, timeCnd]
						} else {
							cnd = timeCnd;
						}
					}
					if (ygCnd != null) {
						if (cnd != null) {
							cnd = ['and', cnd, ygCnd]
						} else {
							cnd = ygCnd;
						}
					}
					if (cnd != null) {
							cnd = ['and', cnd, ['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]]]
						} else {
							cnd = ['eq',['$','a.YFSB'],['l',this.mainApp['phis'].pharmacyId]];
						}
					this.leftList.requestData.cnd = cnd;
					this.leftList.loadData();
					this.rightList.clear();
				}

			},
			createDicField : function(dic) {
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
				var r = this.leftList.getSelectedRecord();
				var url="resources/phis.prints.jrxml.FamilySickBedDispensingDetails.print?type=1&JLID="+r.get("JLID");
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
				rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			},
			doNew : function() {
				this.bq.setValue("");
				this.simple.items.get(1).setValue(new Date()
						.format('Y-m-d 00:00:00'));
				this.simple.items.get(3).setValue(new Date()
						.format('Y-m-d H:i:s'));
			}
		});