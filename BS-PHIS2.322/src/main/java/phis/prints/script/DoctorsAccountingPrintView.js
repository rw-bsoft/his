$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.DoctorsAccountingPrintView = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.DoctorsAccountingPrintView.superclass.constructor.apply(
			this, [cfg])
	this.ib_ks = 1;
	this.ib_ys = 0;
	this.ib_kd = 1;
	this.ib_zx = 0;
	this.ib_sfy = 0;
	this.depValue = [];
}
Ext.extend(phis.prints.script.DoctorsAccountingPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.DoctorsAccountingServlet";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.DoctorsAccountingServlet";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.DoctorsAccountingServlet";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : {
						// id : this.conditionFormId,
						// xtype : "form",
						// layout : "hbox",
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
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.DoctorsAccountingServlet\")'></iframe>"
				})
				this.panel = panel
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
					id : 'dateFrom',
					name : 'dateFrom',
					value : dateFromValue,
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '开始时间'
				}));
				tbar.push(new Ext.form.Label({
					text : " 到 "
				}));
				tbar.push(new Ext.form.DateField({
					id : 'dateTo',
					name : 'dateTo',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d',
					emptyText : '结束时间'
				}));
				var dep = this.createDicField({
					"src" : "",
					"defaultIndex" : "0",
					"width" : 120,
					"id" : "phis.dictionary.department_tree",
					"render" : "Tree",
					"filter" : "['and',['eq',['$','item.properties.ORGANIZCODE'],['s','"
							+ this.mainApp.deptRef
							+ "']],['eq',['$','item.properties.OUTPATIENTCLINIC'],['s','1']]]",
					// "parentKey" : this.navParentKey || {},
					"parentKey" : this.mainApp.deptRef || {},
					"rootVisible" : "true"
				});
				dep.tree.on("click", this.onCatalogChanage, this);
				dep.tree.on("beforeexpandnode", this.onExpandNode, this);
				dep.tree.on("beforecollapsenode", this.onCollapseNode, this);
				dep.tree.expandAll()// 展开树
				tbar.push(dep);
				var boxLabels1 = ['科室', '医生', '收费员'];
				for (var i = 0; i < boxLabels1.length; i++) {
					tbar.push({
						xtype : "radio",
						checked : (i + 1) == 1,
						boxLabel : boxLabels1[i],
						inputValue : i + 1,
						name : "ksys",
						listeners : {
							check : function(group, checked) {
								if (checked) {
									if (group.inputValue == 1) {
										this.ib_ks = 1;
										this.ib_ys = 0;
										this.ib_sfy = 0;
									} else if (group.inputValue == 2) {
										this.ib_ys = 1;
										this.ib_ks = 0;
										this.ib_sfy = 0;
									} else if (group.inputValue == 3) {
										this.ib_ys = 0;
										this.ib_ks = 0;
										this.ib_sfy = 1;
									}
								}
							},
							scope : this
						}
					});
				}
				// tbar.push(new Ext.form.RadioGroup({
				// height : 20,
				// width : 90,
				// name : 'ksys', // 后台返回的JSON格式，直接赋值
				// value : "1",
				// items : [{
				// boxLabel : '科室',
				// name : 'ksys',
				// inputValue : 1
				// }, {
				// boxLabel : '医生',
				// name : 'ksys',
				// inputValue : 2
				// }],
				// listeners : {
				// change : function(group, newValue, oldValue) {
				// if (newValue.inputValue == 1) {
				// this.ib_ks = 1;
				// this.ib_ys = 0;
				// } else if (newValue.inputValue == 2) {
				// this.ib_ys = 1;
				// this.ib_ks = 0;
				// }
				// },
				// scope : this
				// }
				// }));
				var boxLabels2 = ['开单', '执行'];
				for (var i = 0; i < boxLabels2.length; i++) {
					tbar.push({
						xtype : "radio",
						checked : (i + 1) == 1,
						boxLabel : boxLabels2[i],
						inputValue : i + 1,
						name : "yzlx",
						listeners : {
							check : function(group, checked) {
								if (checked) {
									if (group.inputValue == 1) {
										this.ib_kd = 1;
										this.ib_zx = 0;
									} else if (group.inputValue == 2) {
										this.ib_zx = 1;
										this.ib_kd = 0;
									}
								}
							},
							scope : this
						}
					});
				}

				// tbar.push(new Ext.form.RadioGroup({
				// height : 20,
				// width : 90,
				// name : 'yzlx', // 后台返回的JSON格式，直接赋值
				// value : "1",
				// items : [{
				// boxLabel : '开单',
				// name : 'kdzx',
				// inputValue : 1
				// }, {
				// boxLabel : '执行',
				// name : 'kdzx',
				// inputValue : 2
				// }],
				// listeners : {
				// change : function(group, newValue, oldValue) {
				// if (newValue.inputValue == 1) {
				// this.ib_kd = 1;
				// this.ib_zx = 0;
				// } else if (newValue.inputValue == 2) {
				// this.ib_zx = 1;
				// this.ib_kd = 0;
				// }
				// },
				// scope : this
				// }
				// }));

				tbar.push(new Ext.form.Label({
					text : "统计方式:"
				}));
				this.tjfs = 1;
				// var boxLabels3 = ['收费日期','结账日期','汇总日期'];
				var boxLabels3 = ['收费日期'];
				for (var i = 0; i < boxLabels3.length; i++) {
					tbar.push({
						xtype : "radio",
						checked : (i + 1) == 1,
						boxLabel : boxLabels3[i],
						inputValue : i + 1,
						name : "tjfs",
						listeners : {
							check : function(group, checked) {
								if (checked) {
									this.tjfs = group.inputValue;
								}
							},
							scope : this
						}
					});
				}
				// tbar.push(new Ext.form.RadioGroup({
				// height : 20,
				// width : 200,
				// name : 'tjfs', // 后台返回的JSON格式，直接赋值
				// value : "1",
				// items : [{
				// boxLabel : '收费日期',
				// name : 'tjfs',
				// inputValue : 1
				// }, {
				// boxLabel : '结账日期',
				// name : 'tjfs',
				// inputValue : 2
				// }, {
				// boxLabel : '汇总日期',
				// name : 'tjfs',
				// inputValue : 3
				// }],
				// listeners : {
				// change : function(group, newValue, oldValue) {
				// this.tjfs = newValue.inputValue;
				// },
				// scope : this
				// }
				// }));

				tbar.push({
					xtype : "button",
					text : "刷新",
					iconCls : "query",
					scope : this,
					handler : this.doQuery,
					disabled : false
				})
				tbar.push({
					xtype : "button",
					text : "统计",
					iconCls : "commit",
					scope : this,
					handler : this.doCommit,
					disabled : false
				})
				tbar.push({
					xtype : "button",
					text : "打印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : false
				})
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
				this.depValue = [];
				var value = [];
				if (!isNaN(node.id)) {
					value.push(node.id);
				}
				if (node.hasChildNodes()) {// 有子节点
					// 得到所有子节点
					this.findChildNodes(node, value);
				}
				this.depValue = value;
			},
			doQuery : function() {
				if (this.depValue.length == 0) {
					Ext.MessageBox.alert("提示", "请选择部门");
					return;
				}
				if (this.panel.getTopToolbar().items.get(4).value == ""
						|| this.panel.getTopToolbar().items.get(4).value == undefined) {
					this.depValue = [];
				}
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
				var body = {
					"ib_ks" : this.ib_ks,
					"ib_ys" : this.ib_ys,
					"ib_kd" : this.ib_kd,
					"ib_zx" : this.ib_zx,
					"ksdm" : this.depValue,
					"dateFrom" : dateFrom + " 00:00:00",
					"dateTo" : dateTo + " 23:59:59",
					"tjfs" : this.tjfs,
					"ib_sfy" : this.ib_sfy
				};
				// 必须配
				// 把要传的参数放到body里去
				var printConfig = {
					// title : "科室医生核算表",//因乱码问题，在后台写
					page : "whole",
					requestData : body
				}
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading");
				// 后台servelt
				// var url =
				// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&config="
				// + encodeURI(encodeURI(Ext.encode(printConfig)))
				// + "&silentPrint=1";
				var pages = "phis.prints.jrxml.DoctorsAccountingServlet";
				var url = "resources/" + pages + ".print?config="
						+ encodeURI(encodeURI(Ext.encode(printConfig)))
						+ "&silentPrint=1";
				document.getElementById(this.frameId).src = url
			},
			doPrint : function() {
				if (this.depValue.length == 0) {
					Ext.MessageBox.alert("提示", "请选择部门");
					return;
				}
				if (this.panel.getTopToolbar().items.get(4).value == ""
						|| this.panel.getTopToolbar().items.get(4).value == undefined) {
					this.depValue = [];
				}
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
				var body = {
					"ib_ks" : this.ib_ks,
					"ib_ys" : this.ib_ys,
					"ib_kd" : this.ib_kd,
					"ib_zx" : this.ib_zx,
					"ksdm" : this.depValue,
					"dateFrom" : dateFrom + " 00:00:00",
					"dateTo" : dateTo + " 23:59:59",
					"tjfs" : this.tjfs,
					"ib_sfy" : this.ib_sfy
				};
				// 必须配
				// 把要传的参数放到body里去
				var printConfig = {
					title : "科室医生核算表",
					page : "whole",
					requestData : body
				}
				// 后台servelt
				// var url =
				// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&landscape=1&silentPrint=1&config="
				// + encodeURI(encodeURI(Ext.encode(printConfig)));
				var pages = "phis.prints.jrxml.DoctorsAccountingServlet";
				var url = "resources/" + pages
						+ ".print?landscape=1&silentPrint=1&config="
						+ encodeURI(encodeURI(Ext.encode(printConfig)));
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
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi
						.loadXML({
							url : url,
							httpMethod : "get"
						}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
			},
			doExcel : function() {
				if (this.depValue.length == 0) {
					Ext.MessageBox.alert("提示", "请选择部门");
					return;
				}
				if (this.panel.getTopToolbar().items.get(4).value == ""
						|| this.panel.getTopToolbar().items.get(4).value == undefined) {
					this.depValue = [];
				}
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
				var body = {
					"ib_ks" : this.ib_ks,
					"ib_ys" : this.ib_ys,
					"ib_kd" : this.ib_kd,
					"ib_zx" : this.ib_zx,
					"ksdm" : this.depValue,
					"dateFrom" : dateFrom + " 00:00:00",
					"dateTo" : dateTo + " 23:59:59",
					"tjfs" : this.tjfs,
					"ib_sfy" : this.ib_sfy
				};
				// 必须配
				// 把要传的参数放到body里去
				var printConfig = {
					// title : "科室医生核算表",//因乱码问题，在后台写
					page : "whole",
					requestData : body
				}
				var pages = "phis.prints.jrxml.DoctorsAccountingServlet";
				var url = "resources/" + pages
						+ ".print?landscape=1&type=3&silentPrint=1&config="
						+ encodeURI(encodeURI(Ext.encode(printConfig)));
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
			doCommit : function() {
				var dateFrom = this.panel.getTopToolbar().items.get(1)
						.getValue().format('Y-m-d');
				var dateTo = this.panel.getTopToolbar().items.get(3).getValue()
						.format('Y-m-d');
				if (!dateFrom || !dateTo) {
					return
				}
				if (dateFrom != null && dateTo != null && dateFrom != ""
						&& dateTo != "" && dateFrom > dateTo) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				var data = {
					"dateFrom" : dateFrom,
					"dateTo" : dateTo,
					"tjfs" : this.tjfs
				}
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionVerification,
					body : data
				});
				if (r.code == 600) {
					Ext.Msg.confirm("请确认", "该时间段内已做过统计,是否覆盖？", function(btn) {
						if (btn == 'yes') {
							this.panel.el.mask("正在统计核算...");
							var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.serviceActionSave,
								body : data
							});
							if (r.code > 300) {
								this.processReturnMsg(r.code, r.msg);
								return
							} else {
								MyMessageTip.msg("提示", "统计成功!", true);
							}
							this.panel.el.unmask();
						}
					}, this);
					return;
				} else {
					this.panel.el.mask("正在统计核算...");
					var r = phis.script.rmi.miniJsonRequestSync({
						serviceId : this.serviceId,
						serviceAction : this.serviceActionSave,
						body : data
					});
					if (r.code > 300) {
						this.processReturnMsg(r.code, r.msg);
						return
					} else {
						MyMessageTip.msg("提示", "统计成功!", true);
					}
					this.panel.el.unmask();
				}
				// this.panel.el.unmask();
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
						eval(script + '.do' + cmd + '.apply(this,[item,e])')
					}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			onExpandNode : function(node) {
				if (node.getDepth() > 0 && !node.type) {
					node.setIcon(ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/open.png");
					// 判断node是否有type
				}
			},
			onCollapseNode : function(node) {
				if (node.getDepth() > 0 && !node.type) {
					node.setIcon(ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/close.png");
				}

			}
		})

simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}
