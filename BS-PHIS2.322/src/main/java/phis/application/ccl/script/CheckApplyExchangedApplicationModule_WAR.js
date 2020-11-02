$package("phis.application.ccl.script");
$import("phis.script.SimpleModule")

phis.application.ccl.script.CheckApplyExchangedApplicationModule_WAR = function(
		cfg) {
	cfg.printurl = util.helper.Helper.getUrl();
	phis.application.ccl.script.CheckApplyExchangedApplicationModule_WAR.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(
		phis.application.ccl.script.CheckApplyExchangedApplicationModule_WAR,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
					border : false,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [{
						layout : "fit",
						border : false,
						split : true,
						region : 'center',
						height : 300,
						width : "60%",
						items : this.getCheckApplyExchangedApplicationList()
					}, {
						layout : "fit",
						border : false,
						split : true,
						region : 'east',
						height : 300,
						width : "40%",
						items : this
								.getCheckApplyExchangedApplicationDetailsList()
					}],
					tbar : this.getTbar()
				});
				this.panel = panel;
				return panel;
			},
			getTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
					text : "日期:"
				}));
				tbar.push(new Ext.form.DateField({
					id : 'dateFrom',
					name : 'dateFrom',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '请选择'
				}));
				tbar.push(new Ext.form.Label({
					text : " - "
				}));
				tbar.push(new Ext.form.DateField({
					id : 'dateTo',
					name : 'dateTo',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '请选择'
				}));
				// var ztStore = new Ext.data.SimpleStore({
				// fields : ['value', 'text'],
				// data : [[0, '未执行'], [1, '已执行']]
				// });
				// var combox = new Ext.form.ComboBox({
				// id : "zt",
				// store : ztStore,
				// width : 80,
				// valueField : "value",
				// displayField : "text",
				// editable : false,
				// selectOnFocus : true,
				// triggerAction : 'all',
				// mode : 'local',
				// value : 0
				// })
				// tbar.push(combox);
				tbar.push({
					xtype : "button",
					text : "查询",
					iconCls : "query",
					scope : this,
					handler : this.doQuery
				});
				tbar.push({
					xtype : "button",
					text : "删除",
					iconCls : "common_cancel",
					scope : this,
					handler : this.doRemove
				});
				tbar.push({
					xtype : "button",
					text : "打印申请单",
					iconCls : "print",
					scope : this,
					handler : this.doPrint
				});
				// tbar.push({
				// xtype : "button",
				// text : "取消执行",
				// iconCls : "remove",
				// scope : this,
				// handler : this.doCacelExe
				// });
				/*tbar.push({
					xtype : "button",
					text : "查看检查结果",
					iconCls : "query",
					scope : this,
					handler : this.doResult
				});*/
				return tbar;
			},
			getCheckApplyExchangedApplicationList : function() {
				this.checkApplyExchangedApplicationList = this.createModule(
						"checkApplyExchangedApplicationList",
						this.refCheckApplyExchangedApplicationList);
				this.checkApplyExchangedApplicationList.opener = this;
				this.checkApplyExchangedApplicationGrid = this.checkApplyExchangedApplicationList
						.initPanel();
				return this.checkApplyExchangedApplicationGrid;
			},
			getCheckApplyExchangedApplicationDetailsList : function() {
				this.checkApplyExchangedApplicationDetailsList = this
						.createModule(
								"checkApplyExchangedApplicationDetailsList",
								this.refCheckApplyExchangedApplicationDetailsList);
				this.checkApplyExchangedApplicationDetailsList.opener = this;
				this.checkApplyExchangedApplicationDetailsGrid = this.checkApplyExchangedApplicationDetailsList
						.initPanel();
				return this.checkApplyExchangedApplicationDetailsGrid;
			},
			doRemove : function() {
				var list = this.checkApplyExchangedApplicationList;
				if (list.getSelectedRecord() == undefined) {
					Ext.Msg.alert("提示", "请选中要删除的项目")
					return;
				}
				if (list.getSelectedRecord().data.LSBZ == 1) {
					Ext.Msg.alert("提示", "该申请单已执行，删除失败");
					return;
				}
				var yjxh = list.getSelectedRecord().data.SQDH;
				Ext.Msg.show({
					title : '删除确认',
					msg : '删除该申请单将同时删除该申请单上的所有检查项目，确认删除?',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = util.rmi.miniJsonRequestSync({
								serviceId : "phis.checkApplyService",
								serviceAction : "removeCheckApplyProject",
								body : {
									lb : 2,// 2表示住院
									zt : 1,// 1表示删除
									yjxh : yjxh,
									ysdm : this.mainApp['phis'].phisApp.uid
								}
							});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg);
								return;
							} else {
								if (r.json.code == 200) {
									MyMessageTip.msg("提示", "删除成功!", true);
									this.checkApplyExchangedApplicationList
											.refresh();
								} else {
									Ext.Msg.alert("提示", "该申请单已计费，删除失败");
								}
							}
						}
					},
					scope : this
				});

			},
			doCacelExe : function() {
				var list = this.checkApplyExchangedApplicationList;
				if (list.getSelectedRecord() == undefined) {
					Ext.Msg.alert("提示", "请选中要取消执行的项目")
					return;
				}
				if (list.getSelectedRecord().data.LSBZ == 0) {
					Ext.Msg.alert("提示", "取消执行只针对已执行的申请单");
					return;
				}
				var yjxh = list.getSelectedRecord().data.SQDH;
				Ext.Msg.show({
					title : '取消执行确认',
					msg : '取消执行只针对作废发票的特殊作用，是否继续?',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var r = util.rmi.miniJsonRequestSync({
								serviceId : "checkApplyService",
								serviceAction : "removeCheckApplyProject",
								body : {
									lb : 2,// 1表示门诊
									zt : 2,// 2表示取消执行
									yjxh : yjxh
								}
							});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg);
								return;
							} else {
								if (r.json.code == 200) {
									MyMessageTip.msg("提示", "取消执行成功!", true);
									this.checkApplyExchangedApplicationList
											.refresh();
								} else {
									Ext.Msg.alert("提示", "取消执行失败");
								}
							}
						}
					},
					scope : this
				});
			},
			doQuery : function() {
				var dateFrom = Ext.getDom("dateFrom").value;
				var dateTo = Ext.getDom("dateTo").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				this.checkApplyExchangedApplicationList.refresh();
			},
			doResult : function() {

				var list = this.checkApplyExchangedApplicationList;
				var yjxh = list.getSelectedRecord().data.SQDH + "2";
				window
						.open("http://192.168.10.3/Response/HisRedirect.asp?LoginId=HIS&Password=123456&HisId="
								+ yjxh + "&kind=3  ");

			},
			doPrint : function() {
				var form = this.opener.opener.midiModules["checkApplyForm"].form
						.getForm();
				var jgid = this.mainApp['phis'].phisApp.deptId;
				jgid = jgid.substring(jgid.length - 2, jgid.length);
				var list = this.checkApplyExchangedApplicationList;
				if (list.getSelectedRecord() == undefined) {
					Ext.Msg.alert("提示", "请选中要打印的申请单")
					return;
				}
				var record = list.getSelectedRecord();
				var sslx = record.get("SSLX");
				var sqdh = record.get("SQDH");
				var sjsj = record.get("KSSJ");
				var kdInfo = this.opener.opener.exContext.empiData;
				var brid = kdInfo.BRID;
				var age = kdInfo.AGE;
				var zyh = kdInfo.ZYH;
				var zyhm = kdInfo.ZYHM;
				var brch = kdInfo.BRCH;
				var brksdm = kdInfo.BRKS;
				var zrysdm = kdInfo.ZSYS;
				var lczd = form.findField("RYZD").getValue();// 诊断
				var yllb = 2;// 住院
				var url = this.printurl + "resources/";
				// 根据所属类型跳到指定打印界面
				if (sslx == 1) {
					url += "phis.prints.jrxml.CheckApplyBillForECG.print?";
				} else if (sslx == 2) {
					url += "phis.prints.jrxml.CheckApplyBillForRAD.print?";
				} else if (sslx == 3){
					url += "phis.prints.jrxml.CheckApplyBillForBC.print?";

				}else {
					url += "phis.prints.jrxml.CheckApplyBillForWCJ.print?";

				}
				url += "&execJs=" + this.getExecJs() + "&brid=" + brid
						+ "&sqdh=" + sqdh + "&age=" + age + "&yllb=" + yllb
						+ "&zyh=" + zyh + "&zyhm=" + zyhm + "&brch=" + brch
						+ "&brksdm=" + brksdm + "&zrysdm=" + zrysdm;
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				// LODOP.SET_PRINT_PAGESIZE("2",0,0,"A4");
				//LODOP.SET_PRINT_PAGESIZE("1", "148mm", "210mm", "");

				LODOP.ADD_PRINT_HTM(50, 10, "100%", "100%", util.rmi.loadXML({
					url : url,
					httpMethod : "get"
				}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.ADD_PRINT_BARCODE(80, "70%", 138, 38, "128Auto", zyhm);
				// 直接打印
				LODOP.PRINT();
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			}
		})
