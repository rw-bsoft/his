/**
 * 医嘱发药
 * 
 * @author caijy
 */
$package("phis.application.hph.script");

$import("phis.script.SimpleModule");

phis.application.hph.script.HospitalPharmacyHistoryDispensingCollect = function(cfg) {
	this.exContext = {};
	this.width = 1024;
	this.height = 550;
	phis.application.hph.script.HospitalPharmacyHistoryDispensingCollect.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.hph.script.HospitalPharmacyHistoryDispensingCollect,
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
							serviceId : this.serviceid,
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
				this.leftList.on("recordClick", this.onRecordClick, this);
				this.leftList.on("noRecord", this.onNoRecord, this);
				return this.leftList.initPanel();
			},
			getRightList : function() {
				this.rightList = this.createModule("rightList",
						this.refRightList);
				return this.rightList.initPanel();
			},
			onRecordClick : function(ypxh, ypcd, ypgg, ypdj) {
				this.rightList.requestData.cnd = [
						'and',this.cnd,['eq', ['$', 'a.YPXH'], ['d', ypxh]],['eq', ['$', 'a.YPCD'], ['d', ypcd]],['eq', ['$', 'a.YPGG'], ['s', ypgg]],['eq', ['$', 'a.YPDJ'], ['d', ypdj]],['eq', ['$', 'a.YFSB'], ['s', this.mainApp['phis'].pharmacyId]]];
				this.rightList.loadData();
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
				var lx = this.createDicField({
					"src" : "YF_ZYFYMX_HZCX_RIGHT.FYLX",
					"defaultIndex" : "0",
					"width" : 100,
					"id" : "phis.dictionary.dispensingType"
				});
				// var bq =
				// this.createDicField({"src":"YF_FYJL_LSCX.LYBQ","defaultValue":0,"width":100,"id":"department_bq","filter":"['eq',['$map',['s','JGID']],['$','%user.manageUnit.id']]","autoLoad":true})
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceid,
							serviceAction : this.queryWardActionID
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.initPanel);
					return null;
				}
				var bqs = ret.json.bqs;
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
							name : "bq",
							mode : "local",
							triggerAction : "all",
							displayField : "bqName",
							valueField : "bqId",
							store : store,
							editable:false,
							value:0
						})

				this.xradio = new Ext.form.RadioGroup({
							height:20,
							width:140,
							name : 'ypsl', // 后台返回的JSON格式，直接赋值
							value : "0",
							items : [{
										boxLabel : '全部',
										name : 'ypsl',
										inputValue : 0
									}, {
										boxLabel : '发药',
										name : 'ypsl',
										inputValue : 1
									}, {
										boxLabel : '退药',
										name : 'ypsl',
										inputValue : 2
									}]
						});
				this.xradio.on("change", this.onChange, this);
				this.fylx = 0;
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
										text : "类型"
									}, lx, {
										xtype : "label",
										forId : "window",
										text : "病区"
									}, bq, {
										xtype : "label",
										forId : "window",
										text : "时间 "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '发药时间开始',
												name : 'dateFrom',
// value : new Date()
// .format('Y-m-d H:i:s'),
												value : new Date()
												.format('Y-m-d') + " 00:00:00",
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
											}), this.xradio]
						});
						this.lx=lx;
						this.bq=bq;
				this.simple = simple;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			// 查询
			doQuery : function() {
				var datefrom = this.simple.items.get(5).getValue();
				var dateTo = this.simple.items.get(7).getValue();
				var lx = this.simple.items.get(1).getValue();
				var bq = this.simple.items.get(3).getValue();
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
				var body = {};
				if (datefrom != null && datefrom != "") {
					body["datefrom"] = datefrom;
				}
				if (dateTo != null && dateTo != "") {
					body["dateTo"] = dateTo;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge',
							['$', "str(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le',
							['$', "str(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							[
									'ge',
									['$', "str(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', datefrom]],
							[
									'le',
									['$', "str(a.JFRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dateTo]]];
				}
				var lxCnd = null;
				if (lx != null && lx != "" && lx != undefined) {
					body["lx"] = lx
					lxCnd = ['eq', ['$', 'f.FYLX'], ['d', lx]];
				}
				var bqCnd = null;
				if (bq != null && bq != "" && bq != undefined) {
					body["bq"] = bq;
					bqCnd = ['eq', ['$', 'a.LYBQ'], ['d', bq]]
				}
				var fylxCnd = null;
				if (this.fylx != 0) {
					body["fylx"] = this.fylx;
					if (this.fylx == 1) {
						fylxCnd = ['ge', ['$', 'a.YPSL'], ['d', 0]];
					} else {
						fylxCnd = ['le', ['$', 'a.YPSL'], ['d', 0]];
						lxCnd = null;
					}
				} else {
					lxCnd = null;
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
					if (lxCnd != null) {
						if (cnd != null) {
							cnd = ['and', cnd, lxCnd]
						} else {
							cnd = lxCnd;
						}
					}
					if (bqCnd != null) {
						if (cnd != null) {
							cnd = ['and', cnd, bqCnd]
						} else {
							cnd = bqCnd;
						}
					}
					if (fylxCnd != null) {
						if (cnd != null) {
							cnd = ['and', cnd, fylxCnd]
						} else {
							cnd = fylxCnd;
						}
					}
					this.cnd = cnd;
					this.leftList.requestData.body = body;
					this.leftList.requestData.serviceId = this.fullserviceid;
					this.leftList.requestData.serviceAction = this.queryServiceActionID;
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
			onChange : function(radiofield, oldvalue) {
				this.fylx = oldvalue.inputValue;
			},
			// 没有选中记录时清空右下角的记录条数
			onNoRecord : function() {
				this.rightList.requestData.cnd = ['eq', ['$', '1'], ['i', 2]];
				this.rightList.loadData();
			},
			doNew:function(){
				this.lx.setValue("");
				this.bq.setValue("");
				this.simple.items.get(5).setValue(new Date().format('Y-m-d')+ " 00:00:00");
				this.simple.items.get(7).setValue(new Date().format('Y-m-d H:i:s'));
			},
			doPrint : function() {
				var record = this.leftList.getSelectedRecord();
				if (record == null) {
					return
				}
				var lx = this.simple.items.get(1).getValue();
				var bq = this.simple.items.get(3).getValue();
				var dateFrom = this.simple.items.get(5).getValue();
				var dateTo = this.simple.items.get(7).getValue();
				if (!dateFrom || !dateTo) {
					Ext.MessageBox.alert("提示", "请输入统计时间");
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "") {
					try {
						var df = new Date(Date.parse(dateFrom
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
				var type="1";
				if(this.type && this.type=="3"){
					type=this.type;
				}
				this.type="1";
				var pages="phis.prints.jrxml.PharmacyHistoryDispensingCollect";
				 var url="resources/"+pages+".print?type="+type;
				url += "&dateFrom="+dateFrom.replace(" ","_")+"&dateTo="+dateTo.replace(" ","_")
					+"&lx="+lx+"&bq="+bq+"&ypxh="+record.data.YPXH+"&ypcd="+record.data.YPCD
					+"&yfsb="+this.mainApp['phis'].pharmacyId;
				if(type=="3"){
					var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
					return printWin;
				}else{
					var LODOP=getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0","","","");
					var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
					rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
					rehtm.lastIndexOf("page-break-after:always;");
					rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
					LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					LODOP.PREVIEW();
				}
			},
			doExport : function() {
				this.type="3";
				this.doPrint();
			}
});