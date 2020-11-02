$package("phis.application.hph.script");

$import("phis.script.SimpleModule");
				   
phis.application.hph.script.HospitalPharmacyHistoryDispensing = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.hph.script.HospitalPharmacyHistoryDispensing.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.hph.script.HospitalPharmacyHistoryDispensing,
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
				// 是否维护领药科室验证
				var ret_lyks = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryMedicineDepartmentActionID
						});
				if (ret_lyks.code > 300) {
					this.processReturnMsg(ret_lyks.code, ret_lyks.msg,
							this.initPanel);
					return null;
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
			getLeftList : function() {
				this.leftList = this.createModule("leftList", this.refLeftList);
				this.leftList.on("recordClick",
								this.onRecordClick, this);
				this.leftList.on("recordClick_br",
						this.onRecordClick_br, this);
				this.leftList.on("BeforeLoadDataLeft",
						this.onBeforeLoadDataLeft, this);
				this.leftList.on("LoadDataLeft", this.onLoadDataLeft,
						this);
				this.leftList.opener = this;
				return this.leftList.initPanel();
			},
			getRightList : function() {
				this.rightList = this.createModule("rightList",
						this.refRightList);
				return this.rightList.initPanel();
			},
			// 单击发药记录显示明细(提交单)
			onRecordClick : function(data) {
//				this.rightList.requestData.cnd = ['eq', ['$', 'JLID'],
//						['d', jlid]];
				this.rightList.requestData.body = data;
				this.rightList.requestData.pageNo = 1;
				this.rightList.loadData();
			},
			// 单击发药记录显示明细(病人)
			onRecordClick_br : function(data) {
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				var lx = this.simple.items.get(5).getValue();
				var bq = this.simple.items.get(7).getValue();
				var yg = this.simple.items.get(9).getValue();
				if (dateFrom != null && dateFrom != ""
						&& (dateTo == null || dateTo == "")) {
					data.dateFrom = dateFrom;
				} else if (dateTo != null && dateTo != ""
						&& (dateFrom == null || dateFrom == "")) {
					data.dateTo = dateTo;
				} else if (dateTo != null && dateTo != "" && dateFrom != null
						&& dateFrom != "") {
					data.dateFrom = dateFrom;
					data.dateTo = dateTo;
				}
				if (lx != null && lx != "" && lx != undefined) {
					data.FYFS = lx;
				}
				if (bq != null && bq != "" && bq != undefined) {
					data.FYBQ = bq;
				}
				if (yg != null && yg != "" && yg != undefined) {
					data.FYGH = yg;
				}
				data.YF = this.mainApp['phis'].pharmacyId;
				this.rightList.requestData.body = data;
				this.rightList.requestData.pageNo = 1;
				this.rightList.loadData();
			},
			onBeforeLoadDataLeft : function() {
				this.fireEvent("BeforeLoadDataLeft");
			},
			onLoadDataLeft : function() {
				this.fireEvent("LoadDataLeft");
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref

				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				if (cmd == "create") {
					if (!script) {
						script = this.createCls
					}
					this.loadModule(script, this.entryName, item)
					return
				}
				if (cmd == "update" || cmd == "read") {
					var r = this.getSelectedRecord()
					if (r == null) {
						return
					}
					if (!script) {
						script = this.updateCls
					}
					this.loadModule(script, this.entryName, item, r)
					return
				}
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
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				// tbar.enableOverflow=true;
				var lx = this.createDicField({
							"src" : "YF_FYJL_LSCX.FYLX",
							"defaultIndex" : "0",
							"width" : 100,
							"id" : "phis.dictionary.dispensingType"
						});
				// var bq =
				// this.createDicField({"src":"YF_FYJL_LSCX.FYBQ","defaultValue":0,"width":100,"id":"department_bq","filter":"['eq',['$map',['s','JGID']],['$','%user.manageUnit.id']]","autoLoad":true});
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryWardActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				var bqs = ret.json.bqs;
				// [[1,"襄樊市"],[2,"武汉市"],[3,"枣阳市"],[4,"宜昌市"]];
				bqs.push([0,"全部病区"])
				var proxy = new Ext.data.MemoryProxy(bqs);
				var BQ = Ext.data.Record.create([{
							name : "bqId",
							type : "int",
							mapping : 0
						}, {
							name : "bqName",
							type : "string",
							mapping : 1
						}]);
				var reader = new Ext.data.ArrayReader({}, BQ);
				var store = new Ext.data.Store({
							proxy : proxy,
							reader : reader
						});
				store.load();
				var bq = new Ext.form.ComboBox({
							width:100,
							name : "bq",
							mode : "local",
							triggerAction : "all",
							displayField : "bqName",
							valueField : "bqId",
							store : store,
							editable:false,
							value:0
						})
				var yg = this.createDicField({
							"src" : "YF_FYJL_LSCX.FYGH",
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
										text : "类型"
									}, lx, {
										xtype : "label",
										forId : "window",
										text : "病区"
									}, bq, {
										xtype : "label",
										forId : "window",
										text : "操作员"
									}, yg]
						});
				this.simple = simple;
				this.lx = lx;
				this.bq = bq;
				this.yg = yg;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			// 查询
			doQuery : function() {
				this.leftList.showRecord();
				this.rightList.clear();
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
// var r = this.leftList.getSelectedRecord();
// var rr = this.rightList.getSelectedRecord();
// var BS = rr.get("YPSL");
// var JLID = r.get("JLID");
// if (JLID == null) {
// MyMessageTip.msg("提示", "打印失败：发药信息!", true);
// return;
// }
// var FYSJ = r.get("FYSJ");
// var FYBQ = r.get("FYBQ");
// var module = this.createModule("dispensingDetails",
// this.refDispensingDetailsPrint);
// module.JLID = JLID;
// module.FYSJ = encodeURI(encodeURI(FYSJ));
// module.FYBQ = FYBQ;
// module.BS = BS;
// module.initPanel();
// module.doPrint();
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				var lx = this.simple.items.get(5).getValue();
				var bq = this.simple.items.get(7).getValue();
				var yg = this.simple.items.get(9).getValue();
				var data = [];
				if (dateFrom != null && dateFrom != ""
						&& (dateTo == null || dateTo == "")) {
					data.dateFrom = dateFrom;
				} else if (dateTo != null && dateTo != ""
						&& (dateFrom == null || dateFrom == "")) {
					data.dateTo = dateTo;
				} else if (dateTo != null && dateTo != "" && dateFrom != null
						&& dateFrom != "") {
					data.dateFrom = dateFrom;
					data.dateTo = dateTo;
				}
				if (lx != null && lx != "" && lx != undefined) {
					data.FYFS = lx;
				}else{
					data.FYFS = 0;
				}
				if (bq != null && bq != "" && bq != undefined) {
					data.FYBQ = bq;
				}else{
					data.FYBQ = this.mainApp['phis'].wardId;
				}
				if (yg != null && yg != "" && yg != undefined) {
					data.FYGH = yg;
				}else{
					data.FYGH = "";
				}
				data.YF = this.mainApp['phis'].pharmacyId;
				var r = this.leftList.getData();
				var url="";
				if(this.leftList.tab.getActiveTab().id=="tjd") {//提交单
					url+="resources/phis.prints.jrxml.DispensingDetails.print?type=1&JLID="+r.data.JLID;
				}else{//病人
					data.ZYH = r.data.ZYH;
					var datefrom = data.dateFrom;
					var dateto = data.dateTo;
					url+="resources/phis.prints.jrxml.DispensingDetailsBR.print?type=1" 
							+ "&dateFrom=" + dateFrom.replace(" ","_") + "&dateTo=" + dateto.replace(" ","_")
							+ "&FYFS=" + data.FYFS + "&YF=" + data.YF + "&ZYH="
							+ data.ZYH + "&bq=" + data.FYBQ + "&FYGH=" + data.FYGH;
				}
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
				this.lx.setValue("1");
				this.yg.setValue("");
				this.bq.setValue("");
				this.simple.items.get(1).setValue(new Date()
						.format('Y-m-d 00:00:00'));
				this.simple.items.get(3).setValue(new Date()
						.format('Y-m-d H:i:s'));
			}
		});