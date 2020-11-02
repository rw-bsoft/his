$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"phis.script.util.DateUtil", "util.dictionary.TreeDicFactory",
		"util.helper.Helper")

phis.prints.script.ChargesDailyPrintView = function(cfg) {
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.preview = [{
				value : "1",
				text : "网页预览"
			}, {
				value : "0",
				text : "PDF"
			}, {
				value : "2",
				text : "WORD"
			}, {
				value : "3",
				text : "EXCEL"
			}]
	this.conditions = []
	this.jzDate = "";
	phis.prints.script.ChargesDailyPrintView.superclass.constructor.apply(this,
			[cfg])
	// Ext.apply(this, phis.script.SimpleModule);
}
Ext.extend(phis.prints.script.ChargesDailyPrintView, phis.script.SimpleModule,
		{
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.chargesDaily";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.chargesDaily";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.chargesDaily";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : {
						id : this.conditionFormId,
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						items : this.initConditionFields()
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.chargesDaily\")'></iframe>"
				})
				this.panel = panel
				this.panelq = panel
				// this.panel.on("destroy", this.unlock, this);
				return panel
			},
			initConditionFields : function() {
				var items = []
				items.push(new Ext.form.Label({
							text : "从"
						}))
				items.push(new Ext.form.DateField({
							name : 'beginDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						}))
				items.push(new Ext.form.Label({
							text : "到"
						}))
				items.push(new Ext.form.DateField({
							name : 'endDate',
							value : Date.getServerDate(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						}))
				items.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this,
							handler : this.doquery
						})
				items.push(new Ext.form.Label({
							text : "结账时间"
						}))
				items.push(new phis.script.widgets.DateTimeField({
							name : 'jzDate',
							value : Date.getServerDateTime(),
							width : 150,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结账时间'
						}))
				// var preview = this.createCommonDic("type")
				// preview.value = "1"
				// items.push(preview)
				//items.push({
				//			xtype : "button",
				//			text : "产生",
				//			iconCls : "default",
				//			scope : this,
				//			handler : this.doLoadReport
				//		})
				items.push({
							xtype : "button",
							text : "自动对账",
							iconCls : "default",
							scope : this,
							handler : this.doLoadCheck
						})
				items.push({
							xtype : "button",
							text : "结帐",
							iconCls : "commit",
							scope : this,
							handler : this.doCommit,
							disabled : true
						})
				items.push({
							xtype : "button",
							text : "取 消 结 帐",
							iconCls : "writeoff",
							scope : this,
							handler : this.doCancelCommit
						})
				
				items.push({
							xtype : "button",
							text : "打 印",
							iconCls : "print",
							scope : this,
							handler : this.doPrint,
							disabled : true
						})
				//items.push({
				//			xtype : "button",
				//			text : "自动对账",
				//			iconCls : "default",
				//			scope : this,
				//			handler : this.doLoadCheck
				//		})
				return items
			},
			getExecJs : function() {
				return "jsPrintSetup.setPrinter('rb');"
			},
			doCancelCommit : function() {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "queryCancelCommit"
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				if (result.json.body) {
					this.cancelModule = this.createModule("cancelCommitWin",
							this.refCancelCommit);
					if (!this.cancelCommitWin) {
						this.cancelCommitWin = this.cancelModule.getWin();
						this.cancelCommitWin.add(this.cancelModule.initPanel());
						this.cancelModule.on("winShow", this.onCancelWinShow,
								this);
						// cancelModule.on("winShow", this.onQueryWinShow,
						// this);
					}
					this.cancelCommitWin.show();
				} else {
					Ext.Msg.alert("提示", "当前没有可取消的日报信息!", true);
				}
			},
			onCancelWinShow : function() {
				this.cancelModule.loadDate();
			},
			doPrint : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
//				if (this.jzDate == this.oldDate) {
//					var r = phis.script.rmi.miniJsonRequestSync({
//								serviceId : "chargesCheckout",
//								serviceAction : "getDateAction"
//							});
//					this.jzDate = r.json.jzrq;
//				}
				var pages = "phis.prints.jrxml.chargesDaily";
				var url = "resources/" + pages + ".print?type=" + 1
						+ "&execJs=" + this.getExecJs();
				url += "&save=2&jzrq=" + encodeURI(encodeURI(this.jzDate))
				/*
				 * window .open( url, "", "height=" + (screen.height - 100) + ",
				 * width=" + (screen.width - 10) + ", top=0, left=0, toolbar=no,
				 * menubar=yes, scrollbars=yes, resizable=yes,location=no,
				 * status=no")
				 */
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
				// 预览
				LODOP.PREVIEW();
			},

			doCommit : function() {
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getLastHZRQ",
							jzrq : this.jzDate
						});
				var msgLastJZRQ;
				if (!r.json.body) {
					msgLastJZRQ = "当前是第一次结账，请确认至" + this.jzDate;
							+ '进行结账';
				} else {
					var beginDate = r.json.body;
					msgLastJZRQ = "最近一次结账日期为:" + beginDate + '.';
					var msgDate = this.jzDate;
					msgLastJZRQ = msgLastJZRQ + '\n请确认至' + msgDate + '进行结账';
				}
				Ext.Msg.confirm('提示', msgLastJZRQ, function(btn) {
					if (btn != "yes")
						return
					Ext.getCmp(this.mainFormId).el.mask("正在结账...",
							"x-mask-loading")
					var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "checkout",
							jzrq : this.jzDate
						});
					Ext.getCmp(this.mainFormId).el.unmask();
					if (ret.code > 300) {
						MyMessageTip.msg("提示", "结账失败!", true);
					}else{
						MyMessageTip.msg("提示", "结账成功!", true);
						var items = this.panel.getTopToolbar().items;
						items.get(8).setDisabled(true);
						items.get(10).setDisabled(false);
					}
				}, this);
				// 业务锁
				// this.unlock();
			},
			doClose : function() {
				this.opener.closeCurrentTab();
			},
			unlock : function() {
				if (this.ownerLock) {
					var p = {};
					p.YWXH = '1008';
					p.SDXH = this.mainApp.uid
					this.bclUnlock(p)
				}
			},
			doquery : function() {
				var module = this.createModule("queryWin", this.refQueryList);
				module.autoLoadData = false;
				var _ctx = this;
				module.panel = this.panel;
				module.doSure = function() {
					var r = module.getSelectedRecord();
					if (r == null) {
						return;
					}
					var items = _ctx.panel.getTopToolbar().items;
					var jzrq = r.get("JZRQ");
					_ctx.jzDate = r.get("JZRQ");
					var form = Ext.getCmp(_ctx.conditionFormId).getForm()
					if (!form.isValid()) {
						return
					}
					// var url = _ctx.printurl +
					// "*.print?pages=phis.prints.jrxml.chargesDaily";
					var pages = "phis.prints.jrxml.chargesDaily";
					var url = "resources/" + pages + ".print";
					url += "?save=2&jzrq=" + encodeURI(encodeURI(jzrq))
					document.getElementById(_ctx.frameId).src = url
					items.get(8).setDisabled(true);
					items.get(10).setDisabled(false);
					_ctx.queryWin.hide();
				}
				module.doCancel = function() {
					_ctx.queryWin.hide();
				}

				module.onDblClick = function(grid, index, e) {

					module.doSure();
				}

				if (!this.queryWin) {
					this.queryWin = module.getWin();
					this.queryWin.add(module.initPanel());
					module.on("winShow", this.onQueryWinShow, this);
				}
				this.queryWin.show();
				this.queryWin.center();
			},
			onQueryWinShow : function() {
				var module = this.createModule("queryWin", this.refQueryList);

				var datefrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');

				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge', ['$', "str(JZRQ,'yyyy-mm-dd')"],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le', ['$', "str(JZRQ,'yyyy-mm-dd')"],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							['ge', ['$', "str(JZRQ,'yyyy-mm-dd')"],
									['s', datefrom]],
							['le', ['$', "str(JZRQ,'yyyy-mm-dd')"],
									['s', dateTo]]];
				}
				module.requestData.cnd = timeCnd;
				module.requestData.serviceAction = "querySQL";
				module.loadData();
			},
			doLoadReport : function() {
				// 增加业务锁功能
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var items1 = form.items;
				
				//yx-2017-06-09-日结前获取结账时间-b
				var re = phis.script.rmi.miniJsonRequestSync({
						serviceId : this.serviceId,
						serviceAction : "getHZRQ"
					});			
				if(re.code >300){
					this.processReturnMsg(r.code, r.msg);
					return;
				}else{
					items1.get(2).setValue(re.json.JZRQ);
				}
				//yx-2017-06-09-日结前获取结账时间-e
				
				var jzDate = items1.get(2).getValue();
				this.jzDate = jzDate;
				// 查询最后一次汇总时间
				var bars = this.panel.getTopToolbar().items;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getLastHZRQ",
							jzrq : jzDate
						});
				if (r.code > 300) {
					bars.get(8).setDisabled(true);//结账
					bars.get(10).setDisabled(true);//打印
					this.processReturnMsg(r.code, r.msg);
					return;
				}
				var pages = "phis.prints.jrxml.chargesDaily";
				var url = "resources/" + pages + ".print?type=" + 1;
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type" && f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask(
											"正在生成报表...", "x-mask-loading")
					}
					if (f.getName() == "beginDate"|| f.getName() == "endDate") {
						url += "&" + f.getName() + "="+ f.getValue().format("Y-m-d")
					}
				}
				url += "&temp=" + new Date().getTime()+"&jzrq="+encodeURI(encodeURI(jzDate));
							document.getElementById(this.frameId).src = url
							bars.get(8).setDisabled(false);//结账
							bars.get(10).setDisabled(true);//打印
			},
			createCommonDic : function(flag) {
				var fields
				var emptyText = "请选择"
				if (flag == "type") {
					fields = this.preview
					emptyText = "预览方式"
				} else {
					fields = []
					flag = ""
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = new Ext.form.ComboBox({
							store : store,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : emptyText,
							selectOnFocus : true,
							width : 100,
							name : flag,
							allowBlank : false
						})
				return combox
			},
			doLoadCheck : function() {
				// 增加业务锁功能
				var form = Ext.getCmp(this.conditionFormId).getForm()
				if (!form.isValid()) {
					return
				}
				var items1 = form.items;
				
				//yx-2017-06-09-日结前获取结账时间-b
				var re = phis.script.rmi.miniJsonRequestSync({
						serviceId : this.serviceId,
						serviceAction : "getHZRQ"
					});			
				if(re.code >300){
					this.processReturnMsg(r.code, r.msg);
					return;
				}else{
					items1.get(2).setValue(re.json.JZRQ);
				}
				//yx-2017-06-09-日结前获取结账时间-e
				
				var jzDate = items1.get(2).getValue();
				this.jzDate = jzDate;
				// 查询最后一次汇总时间
				var bars = this.panel.getTopToolbar().items;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getLastHZRQ",
							jzrq : jzDate
						});
				if (r.code > 300) {
					bars.get(8).setDisabled(true);//结账
					bars.get(10).setDisabled(true);//打印
					this.processReturnMsg(r.code, r.msg);
					return;
				}
				var pages = "phis.prints.jrxml.chargesDailycheck";
				var url = "resources/" + pages + ".print?type=" + 1;
				var items = form.items
				for (var i = 0; i < items.getCount(); i++) {
					var f = items.get(i)
					if (f.getName() == "type" && f.getValue() == "1") {
						Ext.getCmp(this.mainFormId).el.mask(
											"正在生成报表...", "x-mask-loading")
					}
					if (f.getName() == "beginDate"|| f.getName() == "endDate") {
						url += "&" + f.getName() + "="+ f.getValue().format("Y-m-d")
					}
				}
				url += "&temp=" + new Date().getTime()+"&jzrq="+encodeURI(encodeURI(jzDate));
							document.getElementById(this.frameId).src = url
							bars.get(8).setDisabled(false);//结账
							bars.get(10).setDisabled(true);//打印
			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}