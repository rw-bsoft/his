$package("phis.application.hos.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.hos.script.HospitalPromptManagementForm = function(cfg) {
	this.exContext = {};
	this.width = 800
	this.height = 500
	this.printurl = util.helper.Helper.getUrl();
	this.dicValue = "";
	this.selectId = "hospitalOffice";
	this.zyhStr = ""
	this.queryType = "2";
	// this.radioListData = [];
	phis.application.hos.script.HospitalPromptManagementForm.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.hos.script.HospitalPromptManagementForm, app.desktop.Module,
		{

			initPanel : function() {
				this.frameId = "SimplePrint_frame_HospitalPromptManagement";
				this.conditionFormId = "SimplePrint_form_HospitalPromptManagement";
				this.mainFormId = "SimplePrint_mainform_HospitalPromptManagement";

				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					// title : this.title,
					tbar : {
						id : this.conditionFormId,
						xtype : "form",
						layout : "hbox",
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						items : this.getTbar(1)
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' border=0 width='100%' height='100%' onload='simplePrintMask(\"HospitalPromptManagement\")'></iframe>"
				})

				this.panel = panel

				// 获取组件都触发了哪些事件
				// Ext.util.Observable.capture(prompt, function(e, node) {
				// console.debug(e, node,34)
				// })
				return panel
			},
			getTbar : function(type) {
				var cklb = this.getComboDic(type);
				cklb.setEditable(false);
				cklb.emptyText = '请选择';
				cklb.tree.on("click", this.onCatalogChanage, this);
				cklb.tree.expandAll()// 展开树
				this.cklb = cklb;
				this.type = type
				this.butD = {
					id : 'pri',
					xtype : "button",
					text : "打印",
					iconCls : "print",
					scope : this,
					handler : this.doPrint,
					disabled : false
				};

				return [
//				 this.radioGroup(type),
//				 this.labeObj('ksType3', type),
//						cklb, this.lab, this.CKJE, this.butS,
						this.butD];
			},
//			radioGroup : function(type) {
//				this.butS = {
//					id : 'fre',
//					xtype : "button",
//					text : "刷新",
//					iconCls : "query",
//					scope : this,
//					handler : this.doQuery,
//					disabled : false
//				};
//				this.butD = {
//					id : 'pri',
//					xtype : "button",
//					text : "打印",
//					iconCls : "print",
//					scope : this,
//					handler : this.doPrint,
//					disabled : false
//				};

//				this.lab = new Ext.form.Label({
//							text : " 催款金额："
//						})
//
//				this.CKJE = new Ext.form.TextField({
//							id : 'CKJE2'
//						});
//
//				this.CKJE.on("blur", function() {
//					var ckValue = this.CKJE.getValue();
//					if (ckValue.length == 0) {
//					} else if ((!(/(^((([0-9]|[1-9]\d{0,5})(\.\d{0,2})?)|(1000000\.0{0,2}))$)/
//							.test(ckValue)))) {// 催款金额范围0~100万之间 小数点精度2位
//						Ext.Msg.alert("无效", "催款金额有效数据范围(0~100万之间),精确到小数点后2位!");
//						this.CKJE.setValue("");
//					}
//				}, this);
//
//				return new Ext.form.RadioGroup({
//							height : 20,
//							width : 190,
//							id : 'prompt3',
//							name : 'prompt3', // 后台返回的JSON格式，直接赋值
//							value : type,
//							items : [{
//										boxLabel : '按科室催款',
//										name : 'prompt3',
//										inputValue : 1
//									}, {
//										boxLabel : '按病区催款',
//										name : 'prompt3',
//										inputValue : 2
//									}],
//							listeners : {
//								change : function(prompt, newValue, oldValue,
//										eOpts) {
//									var toolbar = this.panel.getTopToolbar();
//									toolbar.removeAll();
//									if (newValue.inputValue == 1) {
//										toolbar.add(this.getTbar(1));
//										this.ref = "0";
//									} else if (newValue.inputValue == 2) {
//										toolbar.add(this.getTbar(2));
//										this.ref = "0";
//									}
//									toolbar.doLayout();
//								},
//								scope : this
//							}
//						});
//			},
			labeObj : function(id, type) {
				var text = "";
				if (type == 1) {
					text = " 科室: "
				} else if (type == 2) {
					text = " 病区: "
				} else {
					text = type;
				}

				return new Ext.form.Label({
							id : id,
							text : text
						});
			},
			getComboDic : function(type) {
				this.dicId = "";
				if (type == 1) {
					this.dicId = "phis.dictionary.hospitalOffice";
				} else if (type == 2) {
					this.dicId = "phis.dictionary.lesionOffice";
				}

				var dic = {
					id : this.dicId,
					autoLoad : true,
					defaultValue : ""
				}

				return util.dictionary.TreeDicFactory.createDic(dic);
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
				this.queryType = "2";
				this.ref = "0";
				this.Nodetext = node.attributes.text;

				var value = [];
				var tbar = this.panel.getTopToolbar();
				if (node.hasChildNodes()) {// 有子节点
					// 得到所有子节点
					this.findChildNodes(node, value);
				} else {// 没有子节点
					value.push(node.id);
				}
				this.dicValue = value;
			},
			doQuery : function() {
				this.type = Ext.getCmp("prompt3").getValue().inputValue;
				this.ref = "1";
//				if (this.CKJE.getValue().length == 0) {
//					Ext.Msg.alert('提示', '请输入催款金额！');
//					return;
//				}
				var text = "";
				if (this.type == 1) {
					text = "科室";
				} else {
					text = "病区";
				}
				if (this.cklb.getValue() == 0) {
					Ext.MessageBox.alert("提示", "请选择" + text);
					return;
				}

				this.queryType = "2";
				/*
				var url = this.printurl
						+ "*.print?pages=phis.prints.jrxml.HospitalPromptManagement&queryType="
						+ this.queryType + "&ksType=" + this.type
						+ "&dicValue=" + this.dicValue + "&text="
						+ encodeURIComponent(this.Nodetext) + "&jgmc="
						+ encodeURIComponent(this.mainApp.dept + "催款单")
						+ "&CKJE=" + this.CKJE.getValue();
				*/
				
				//alert(encodeURIComponent(this.Nodetext) );
				//encodeURI(encodeURI(this.Nodetext))
				//alert(this.mainApp.dept);
				var pages="phis.prints.jrxml.HospitalPromptManagement";
				 var url="resources/"+pages+".print?queryType="
						+ this.queryType + "&ksType=" + this.type
						+ "&dicValue=" + this.dicValue + "&text="
						+encodeURI(encodeURI(this.Nodetext)) + "&jgmc="
						+ encodeURI(encodeURI(this.mainApp.dept + "催款单"))
				 + "&ckjeStr=" + this.ckjeStr;
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading");

				document.getElementById(this.frameId).src = url
			},
			doPrint : function() {
				if (this.ref != "1") {
					Ext.Msg.alert('提示', '请先刷新数据,再进行打印！');
					return;
				}
//				if (this.CKJE.getValue().length == 0) {
//					Ext.Msg.alert('提示', '请输入催款金额！');
//					return;
//				}
				/*var url = this.printurl
						+ "*.print?pages=phis.prints.jrxml.HospitalPromptManagement&queryType="
						+ this.queryType + "&ksType=" + this.type
						+ "&dicValue=" + this.dicValue + "&text="
						+ encodeURIComponent(this.Nodetext) + "&zyhStr="
						+ this.zyhStr + "&jgmc="
						+ encodeURIComponent(this.mainApp.dept + "催款单")
						+ "&CKJE=" + this.CKJE.getValue();
						*/
				var pages="phis.prints.jrxml.HospitalPromptManagement";
				 var url="resources/"+pages+".print?queryType="
						+ this.queryType + "&ksType=" + this.type
						+ "&dicValue=" + this.dicValue + "&text="
						+ encodeURI(encodeURI(this.Nodetext)) + "&zyhStr="
						+ this.zyhStr + "&jgmc="
						+ encodeURI(encodeURI(this.mainApp.dept + "催款单"))
						+ "&ckjeStr=" + this.ckjeStr;
				/*window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				*/
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
			doActivePanel : function(data, Ltype, LdicValue, LNodetext) {
				this.ref = "1";
//				if(CKJE.length == 0){
//					CKJE = "0";
//				}
//				this.CKJE=CKJE;
//				this.CKJE.setValue(CKJE)
				if (data.length != 0) {
					var zyhStr = [];// 用于存放所有用户选择"是"的住院号
					var ckjeStr = [];
					for (i = 0; i < data.length; i++) {
						if (data[i].flag) {
							zyhStr.push(data[i].zyh);
							ckjeStr.push(data[i].CKJE);
						}
					}

					this.type = Ltype;
					this.dicValue = LdicValue;
					this.Nodetext = LNodetext;
					this.queryType = "1";
					this.zyhStr = zyhStr.toString();
					this.ckjeStr = ckjeStr.toString();
					if(zyhStr.length == 0){
						document.getElementById(this.frameId).src = "";
						return;
					}

					/*var url = this.printurl
							+ "*.print?pages=phis.prints.jrxml.HospitalPromptManagement&queryType="
							+ this.queryType + "&ksType=" + Ltype
							+ "&dicValue=" + LdicValue[0] + "&text="
							+ encodeURIComponent(LNodetext) + "&zyhStr="
							+ zyhStr.toString() + "&jgmc="
							+ encodeURIComponent(this.mainApp.dept + "催款单")
							+ "&CKJE=" + CKJE;
							*/
					var pages="phis.prints.jrxml.HospitalPromptManagement";
					var url="resources/"+pages+".print?queryType="
							+ this.queryType 
							+ "&ksType=" + Ltype
							+ "&dicValue=" + LdicValue
							+ "&text="
							+ encodeURI(encodeURI(LNodetext)) 
							+ "&zyhStr="
							+ zyhStr.toString() 
							+ "&jgmc="
							+ encodeURI(encodeURI(this.mainApp.dept + "催款单"))
							+ "&ckjeStr=" + ckjeStr;
							
					Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
							"x-mask-loading");

					document.getElementById(this.frameId).src = url
				}
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