$package("phis.application.hph.script")

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"phis.script.widgets.Spinner", "app.desktop.Module");

phis.application.hph.script.HospitalPharmacyMedicineHZ = function(cfg) {
	this.exContext = {};
	phis.application.hph.script.HospitalPharmacyMedicineHZ.superclass.constructor
			.apply(this, [ cfg ]);
};
Ext.extend(phis.application.hph.script.HospitalPharmacyMedicineHZ,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.mainApp['phis'].wardId == null || this.mainApp['phis'].wardId == ""
						|| this.mainApp['phis'].wardId == undefined) {
					Ext.Msg.alert("提示", "未设置登录病区,请先设置");
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
					buttonAlign : 'center',
					items : [ {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'center',
						items : this.getList()
					} ],
					tbar : this.getTbar()
				});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this);
				return panel;
			},
			onReady : function() {
				this.doQuery();
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var lx = this.createDicField({
					"src" : "YF_FYJL_BQFY.FYLX",
					"width" : 100,
					"id" : "phis.dictionary.hairMedicineWay",
					"emptyText" : "全部发药方式"
				});
				var yf = this.createDicField({
					"width" : 100,
					"id" : "phis.dictionary.wardPharmacy",
					"filter" : [ 'eq', [ '$', 'item.properties.BQDM' ],
							[ 'l', this.mainApp['phis'].wardId ] ],
					"emptyText" : "全部药房"
				});
				var ypyf = this.createDicField({
					//"src" : "ZY_BQYZ_FY.YPYF",
					"width" : 100,
					"id" : "phis.dictionary.drugWay",
					"emptyText" : "全部给药方式"
				});
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
					}, yf, {
						xtype : "label",
						forId : "window",
						text : "给药方式"
					}, ypyf ]
				});
				this.simple = simple;
				this.lx = lx;
				this.yf = yf;
				this.ypyf = ypyf;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				var date = new Date();
				module.requestData.cnd = [
						'eq',
						[ '$', "a.JFRQ" ],
						[
								'todate',
								date.getFullYear() + "-"
										+ (date.getMonth() + 1) + "-"
										+ date.getDate(), 'yyyy-mm-dd' ] ];
				var listModule = module.initPanel();
				this.listModule = module;
				return listModule;
			},
			doAction : function(item, e) {
				var cmd = item.cmd;
				var script = item.script;
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1);
				if (script) {
					$require(script, [ function() {
						eval(script + '.do' + cmd + '.apply(this,[item,e])')
					}, this ])
				} else {
					var action = this["do" + cmd];
					if (action) {
						action.apply(this, [ item, e ])
					}
				}
			},
			createDicField : function(dic) {
				if (dic.id == "phis.dictionary.department_bq") {
					var arr = [];
					arr.push(31);
					// dic.filter="['eq',['$map',['s','JGID']],['s','1']]";
				};
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory";
				$import(cls);
				var factory = eval("(" + cls + ")");
				return factory.createDic(dic);
			},
			doQuery : function() {
				var body = {};
				var bq = this.mainApp['phis'].wardId;
				if (bq != null && bq != "") {
					body["bq"] = bq;
				}
				var dateFrom = this.simple.items.get(1).getValue();
				if (dateFrom != null && dateFrom != "") {
					dateFrom = dateFrom + " 00:00:00";
					body["dateFrom"] = dateFrom;
				} else {
					Ext.MessageBox.alert("提示", "开始日期或终止日期不能为空!");
					return;
				}
				var dateTo = this.simple.items.get(3).getValue();
				if (dateTo != null && dateTo != "") {
					dateTo = dateTo + " 23:59:59";
					body["dateTo"] = dateTo;
				} else {
					Ext.MessageBox.alert("提示", "开始日期或终止日期不能为空!");
					return;
				}
				var FYFS = this.simple.items.get(5).getValue();
				var YF = this.simple.items.get(7).getValue();
				var YPYF = this.simple.items.get(9).getValue();
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始日期不能大于终止日期");
					return;
				}
				var dateFromCnd = [
						'ge',
						[ '$', "a.JFRQ" ],
						[ 'todate', [ 's', dateFrom ],
								[ 's', 'yyyy-mm-dd hh24:mi:ss' ] ] ];
				var dateToCnd = [
						'le',
						[ '$', "a.JFRQ" ],
						[ 'todate', [ 's', dateTo ],
								[ 's', 'yyyy-mm-dd hh24:mi:ss' ] ] ];
				var FYFSCnd = [ 'eq', [ '$', "a.FYLX" ], [ 'd', FYFS ] ];
				var YFCnd = [ 'eq', [ '$', "a.YFSB" ], [ 's', YF ] ];
				var YPYFCnd = [ 'eq', [ '$', "a.YPYF" ], [ 'd', YPYF ] ];
				var cnd = [];
				if (dateFrom != null && dateFrom != "") {
					if (cnd.length > 0) {
						cnd = [ 'and', cnd, dateFromCnd ];
					} else {
						cnd = dateFromCnd;
					}
				}
				if (dateTo != null && dateTo != "") {
					if (cnd.length > 0) {
						cnd = [ 'and', cnd, dateToCnd ];
					} else {
						cnd = dateToCnd;
					}
				}
				if (FYFS != null && FYFS != "") {
					body["FYFS"] = FYFS;
					if (cnd.length > 0) {
						cnd = [ 'and', cnd, FYFSCnd ];
					} else {
						cnd = FYFSCnd;
					}
				}
				if (YF != null && YF != "") {
					body["YF"] = YF;
					if (cnd.length > 0) {
						cnd = [ 'and', cnd, YFCnd ];
					} else {
						cnd = YFCnd;
					}
				}
				if (YPYF != null && YPYF != "") {
					body["YPYF"] = YPYF;
					if (cnd.length > 0) {
						cnd = [ 'and', cnd, YPYFCnd ];
					} else {
						cnd = YPYFCnd;
					}
				}
				if (cnd.length == 0) {
					cnd = null;
				}
				this.listModule.requestData.body = body;
				this.listModule.requestData.cnd = cnd;
				this.listModule.loadData();
			},
			doNew : function() {
				this.simple.items.get(5).setValue("");
				this.simple.items.get(7).setValue("");
				this.simple.items.get(9).setValue("");
				this.simple.items.get(1).setValue(new Date().format('Y-m-d'));
				this.simple.items.get(3).setValue(new Date().format('Y-m-d'));
			},
			doPrint : function() {
				debugger;
				var dateFrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				if (!dateFrom || !dateTo) {
					Ext.MessageBox.alert("提示", "开始日期或者终止日期不能为空!");
					return;
				}
				var FYFS = this.simple.items.get(5).getValue();
				var YF = this.simple.items.get(7).getValue();
				var YPYF = this.simple.items.get(9).getValue();
				var bq = this.mainApp['phis'].wardId;
				var module = this.createModule("hospitalPharmacyMedicineHZ",
						this.refHospitalPharmacyMedicineHZPrint);
				module.dateFrom = dateFrom;
				module.dateTo = dateTo;
				module.FYFS = FYFS;
				module.YF = YF;
				module.YPYF = YPYF;
				module.bq = bq;
				module.initPanel();
				module.doPrint();
			}
		});