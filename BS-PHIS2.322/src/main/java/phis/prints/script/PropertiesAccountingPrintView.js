﻿$package("phis.prints.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.PropertiesAccountingPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.brxzValue = [];
	phis.prints.script.PropertiesAccountingPrintView.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.prints.script.PropertiesAccountingPrintView, app.desktop.Module,
		{
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.PropertiesAccounting";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.PropertiesAccounting";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.PropertiesAccounting";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : {
//						id : this.conditionFormId,
//						xtype : "form",
//						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						enableOverflow : true,
						items : this.getTbar()
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.PropertiesAccounting\")'></iframe>"
				})
				this.panel = panel
				this.panel.getTopToolbar().getComponent('mxhz0')
						.setDisabled(true);
				this.panel.getTopToolbar().getComponent('mxhz1')
				.setDisabled(true);
				return panel
			},
			getTbar : function() {
				var dat = new Date().format('Y-m-d');
				var dateFromValue = dat.substring(0, dat.lastIndexOf("-"))
						+ "-01";
				var tbar = [];
				tbar.push(new Ext.form.Label({
					text : "从:"
				}));
				tbar.push(new Ext.form.DateField({
							id : 'dateFrom_proAcc',
							name : 'dateFrom',
							value : dateFromValue,
							width : 90,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间'
						}));
				tbar.push(new Ext.form.Label({
							text : " 到 "
						}));
				tbar.push(new Ext.form.DateField({
							id : 'dateTo_proAcc',
							name : 'dateTo',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						}));
				tbar.push(new Ext.form.Label({
							// width : 200,
							text : " 性质: "
						}));
				// 病人性质树
				var brxz = util.dictionary.TreeDicFactory.createDic({
					id : 'phis.dictionary.patientProperties_tree',
					name : 'phis.dictionary.patientProperties_tree',
					sliceType : 5,
					parentKey : -2,
					width : 120
				});
				brxz.tree.on("click", this.onCatalogChanage, this);
				brxz.tree.expandAll()// 展开树
				tbar.push(brxz);
				tbar.push(new Ext.form.Label({
							text : " 显示统计方式: "
						}));
				this.mxhz = 1;
				var boxLabels1 = ['明细','汇总'];
				for(var i = 0 ; i < boxLabels1.length ; i ++){
					tbar.push({
						xtype : "radio",
						checked : (i+1) == 1,
						boxLabel : boxLabels1[i],
						inputValue : i+1,
						name : "mxhz",
						id : "mxhz"+i,
						listeners : {
							check : function(group,checked) {
								if(checked){
									this.mxhz = group.inputValue;
								}
							},
							scope : this
						}
					});
				}
//				tbar.push(new Ext.form.RadioGroup({
//							height : 20,
//							width : 90,
//							id : 'mxhz',
//							name : 'mxhz', // 后台返回的JSON格式，直接赋值
//							value : "1",
//							items : [{
//										boxLabel : '明细',
//										name : 'mxhz',
//										inputValue : 1
//									}, {
//										boxLabel : '汇总',
//										name : 'mxhz',
//										inputValue : 2
//									}]
//						}));
						
						
						// lable
						tbar.push(new Ext.form.Label({
							text : " 核算方式:  "
						}));
						this.chargeMode = 1;
						var boxLabels2 = ['收费日期','汇总日期'];
						for(var i = 0 ; i < boxLabels2.length ; i ++){
							tbar.push({
								xtype : "radio",
								checked : (i+1) == 1,
								boxLabel : boxLabels2[i],
								inputValue : i+1,
								name : "chargeMode",
								id : "chargeMode"+i,
								listeners : {
									check : function(group,checked) {
										if(checked){
											this.chargeMode = group.inputValue;
										}
									},
									scope : this
								}
							});
						}
								// 选择按钮
//					tbar.push(new Ext.form.RadioGroup({
//							height : 20,
//							width : 140,
//							id : 'chargeMode',
//							name : 'chargeMode', // 后台返回的JSON格式，直接赋值
//							value : "1",
//							items : [{
//										boxLabel : '收费日期',
//										name : 'chargeMode',
//										inputValue : 1
//									}, {
//										boxLabel : '汇总日期',
//										name : 'chargeMode',
//										inputValue : 2
//									}]
//						}));
						
						//
						
						
				tbar.push({
							xtype : "button",
							text : "刷新",
							iconCls : "query",
							scope : this,
							handler : this.doQuery,
							disabled : false
						});
				tbar.push({
							xtype : "button",
							text : "打印",
							iconCls : "print",
							scope : this,
							handler : this.doPrint,
							disabled : false
						});
				tbar.push({
					xtype : "button",
					text : "导出",
					iconCls : "excel",
					scope : this,
					handler : this.doExcel
				})
						
				return tbar;
			},
			findChildNodes : function(node, value) {
				var childnodes = node.childNodes;
				for (var i = 0; i < childnodes.length; i++) {
					var nd = childnodes[i];
					value.push(nd.id);
					if (nd.hasChildNodes()) {
						this.findChildNodes(nd, value);
					}
				}
			},
			onCatalogChanage : function(node, e) {
				this.brxzValue = [];
				var value = [];
				var tbar = this.panel.getTopToolbar();
				if (node.hasChildNodes()) {// 有子节点
					tbar.getComponent('mxhz0').setDisabled(true);
					tbar.getComponent('mxhz1').setDisabled(true);
					// 得到所有子节点
					this.findChildNodes(node, value);
				} else {// 没有子节点
					tbar.getComponent('mxhz0').setDisabled(false);
					tbar.getComponent('mxhz1').setDisabled(false);
					value.push(node.id);
				}
				this.brxzValue = value;
			},
			doQuery : function() {
				if (this.brxzValue.length == 0) {
					Ext.MessageBox.alert("提示", "请选择性质");
					return;
				}
				var tbar = this.panel.getTopToolbar();
				var dateFrom = Ext.getDom("dateFrom_proAcc").value;
				var dateTo = Ext.getDom("dateTo_proAcc").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				// var node = Ext.getDom("patientProperties_tree").value;
//				var mxhz = Ext.getCmp('mxhz').getValue().inputValue;
				var mxhz = this.mxhz;
//				var chargeMode = Ext.getCmp('chargeMode').getValue().inputValue;
				var chargeMode = this.chargeMode;
				var body = {};
				body["dateFrom"] = dateFrom + " 00:00:00";
				body["dateTo"] = dateTo + " 23:59:59";
				body["brxzValue"] = this.brxzValue;
				body["type"] = "";
				body["chargeMode"] = chargeMode + "";
				
				
				//
				if (this.brxzValue.length > 1) {
					body["type"] = "zf";
				} else {
					if (mxhz == 1) {
						body["type"] = "mxzf";
					}
					if (mxhz == 2) {
						body["type"] = "hzzf";
					}
				}

				// 必须配
				// 把要传的参数放到body里去
				var printConfig = {
					 //title : "性质费用核算表",
					page : "whole",
					requestData : body
				}
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading");

				// 后台servelt
				//var url = "*.print?pages=phis.prints.jrxml.PropertiesAccountingServlet&config="
					//+ encodeURI(encodeURI(Ext.encode(printConfig)))
				//	+ "&silentPrint=1";
				var pages="phis.prints.jrxml.PropertiesAccountingServlet";
				 var url="resources/"+pages+".print?config="
					+ encodeURI(encodeURI(Ext.encode(printConfig)))
					+ "&silentPrint=1";
				document.getElementById(this.frameId).src = url
			},
			doPrint : function() {
				if (this.brxzValue.length == 0) {
					Ext.MessageBox.alert("提示", "请选择性质");
					return;
				}
				var tbar = this.panel.getTopToolbar();
				var dateFrom = Ext.getDom("dateFrom_proAcc").value;
				var dateTo = Ext.getDom("dateTo_proAcc").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var chargeMode = this.chargeMode;
				var mxhz = this.mxhz;
				var body = {};
				body["dateFrom"] = dateFrom + " 00:00:00";
				body["dateTo"] = dateTo + " 23:59:59";
				body["brxzValue"] = this.brxzValue;
				body["type"] = "";
				body["chargeMode"] = chargeMode + "";
				if (this.brxzValue.length > 1) {
					body["type"] = "zf";
				} else {
					if (mxhz == 1) {
						body["type"] = "mxzf";
					}
					if (mxhz == 2) {
						body["type"] = "hzzf";
					}
				}

				// 必须配
				// 把要传的参数放到body里去
				var printConfig = {
					//title : "性质费用核算表",
					page : "whole",
					requestData : body
				}
				var pages="phis.prints.jrxml.PropertiesAccountingServlet";
				 var url="resources/"+pages+".print?config="
					+ encodeURI(encodeURI(Ext.encode(printConfig)))
					var LODOP=getLodop();
					LODOP.PRINT_INIT("打印控件");
					LODOP.SET_PRINT_PAGESIZE("0","","","");
					//预览LODOP.PREVIEW();
					//预览LODOP.PRINT();
					//LODOP.PRINT_DESIGN();
					LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					//预览
					LODOP.PREVIEW();
			},
			doExcel : function() {
				if (this.brxzValue.length == 0) {
					Ext.MessageBox.alert("提示", "请选择性质");
					return;
				}
				var tbar = this.panel.getTopToolbar();
				var dateFrom = Ext.getDom("dateFrom_proAcc").value;
				var dateTo = Ext.getDom("dateTo_proAcc").value;
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var chargeMode = this.chargeMode;
				var mxhz = this.mxhz;
				var body = {};
				body["dateFrom"] = dateFrom + " 00:00:00";
				body["dateTo"] = dateTo + " 23:59:59";
				body["brxzValue"] = this.brxzValue;
				body["type"] = "";
				body["chargeMode"] = chargeMode + "";
				if (this.brxzValue.length > 1) {
					body["type"] = "zf";
				} else {
					if (mxhz == 1) {
						body["type"] = "mxzf";
					}
					if (mxhz == 2) {
						body["type"] = "hzzf";
					}
				}

				// 必须配
				// 把要传的参数放到body里去
				var printConfig = {
					//title : "性质费用核算表",
					page : "whole",
					requestData : body
				}
				var pages="phis.prints.jrxml.PropertiesAccountingServlet";
				var url="resources/"+pages+".print?type=3&config="
					+ encodeURI(encodeURI(Ext.encode(printConfig)))
				var printWin = window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			onTreeNotifyDrop : function(dd, e, data) {
				var n = this.getTargetFromEvent(e);
				var r = dd.dragData.selections[0];
				var node = n.node
				var ctx = dd.grid.__this

				if (!node.leaf || node.id == r.data[ctx.navField]) {
					return false
				}
				var updateData = {}
				updateData[ctx.schema.pkey] = r.id
				updateData[ctx.navField] = node.attributes.key
				ctx.saveToServer(updateData, r)
				// node.expand()
			}

		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
