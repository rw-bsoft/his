$package("phis.prints.script")
$import("phis.script.SimpleModule")

phis.prints.script.PharmacyCustodianBooksPrintView = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.exContext = {};
	this.data=[];
	phis.prints.script.PharmacyCustodianBooksPrintView.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.prints.script.PharmacyCustodianBooksPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_PharmacyCustodianBooks";
				if (this.mainApp.pharmacyId == null
						|| this.mainApp.pharmacyId == ""
						|| this.mainApp.pharmacyId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药房,请先设置");
					return null;
				}
				var panel = new Ext.Panel({
					id:"PharmacyCustodianBooks",
					//width : 200,
					//height : 200,
					title : "",
					tbar : this.initConditionFields(),
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' ></iframe>"
				})
				this.panel = panel
				this.panel.on("afterrender", this.onReady, this);
				return panel
			},
			initConditionFields : function() {
				var tbar = new Ext.Toolbar();
				var yplxStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[0, '全部'], [1, '西药'], [2, '中成药'], [3, '草药']]
						});

				this.yplxCombox = new Ext.form.ComboBox({
							store : yplxStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 80,
							value : 0
						});
				this.pydmText = new Ext.form.TextField();
				this.dateFrom = new Ext.ux.form.Spinner({
							fieldLabel : '结账日期开始',
							name : 'dateFrom',
							value : new Date().format('Y-m'),
							strategy : {
								xtype : "month"
							},
							width : 100
						})
				this.dateTo = new Ext.ux.form.Spinner({
							fieldLabel : '结账日期结束',
							name : 'dateTo',
							value : new Date().format('Y-m'),
							strategy : {
								xtype : "month"
							},
							width : 100
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
										text : "药品类型 "
									}, this.yplxCombox, {
										xtype : "label",
										forId : "window",
										text : "拼音码"
									}, this.pydmText,  {
										xtype : "label",
										forId : "window",
										text : "结账日期"
									}, this.dateFrom, {
										xtype : "label",
										forId : "window",
										text : "至"
									}, this.dateTo]

						});
				this.simple = simple;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			/**
			 * 监听报表load事件,并增加表格事件监听
			 */
			onReady : function() {
				var _ctx = this;
				var iframe = Ext.isIE
						? document.frames[this.frameId]
						: document.getElementById(this.frameId);
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", function() {
								_ctx
										.simplePrintMask(iframe);
							});
				} else {
					iframe.onload = function() {
						_ctx.simplePrintMask(iframe);
					};
				}
			},
			doQuery : function(tag) {
				this.data=[];
				var dateF= this.dateFrom.getValue();
				var dateT = this.dateTo.getValue();
				if (!dateF || !dateT) {
					Ext.MessageBox.alert("提示", "请输入月结日期");
					return
				}
				if (dateF != null && dateT != null && dateF != ""
						&& dateT != "" && dateF > dateT) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
				this.panel.el.mask("正在生成报表...","x-mask-loading");
				var pages = "phis.prints.jrxml.PharmacyCustodianBooks";
				if(tag==1){
					pages = "phis.prints.jrxml.PharmacyCustodianBooks_dy";	
						}
				var url = "resources/" + pages + ".print?type=1";
				url += "&dateFrom=" + dateF + "&dateTo=" + dateT;
				var yplx = this.yplxCombox.getValue();
				if (yplx && yplx != null && yplx != "") {
					url +="&yplx="+ yplx
				}
				var pydm=this.pydmText.getValue();
				if (pydm &&pydm != null && pydm != "") {
					url +="&pydm="+ pydm.toUpperCase();
				}
				if(tag==1){
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				// 预览LODOP.PREVIEW();
				// 预览LODOP.PRINT();
				// LODOP.PRINT_DESIGN();
				var rehtm = util.rmi.loadXML({
							url : url,
							httpMethod : "get"
						})
				rehtm = rehtm.replace(/table style=\"/g,
						"table style=\"page-break-after:always;")
				rehtm.lastIndexOf("page-break-after:always;");
				rehtm = rehtm.substr(0, rehtm
								.lastIndexOf("page-break-after:always;"))
						+ rehtm.substr(rehtm
								.lastIndexOf("page-break-after:always;")
								+ 24);
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", rehtm);
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				// 预览
				LODOP.PREVIEW();
				this.panel.el.unmask()
				}else{
				document.getElementById(this.frameId).src = url;
				}
			},
			doPrint : function() {
				this.doQuery(1)
			}
			,/**
			 * 实现报表事件监听及相应业务处理
			 */
			simplePrintMask : function(iframe) {
				this.panel.el.unmask();
				// add by yangl 报表事件
				var _ctx = this;
				var iframeDoc = Ext.isIE
						? iframe.document
						: iframe.contentWindow.document
				var tables = iframeDoc.getElementsByTagName('table');
				if (tables.length > 1) {
					var trs = tables[1].getElementsByTagName('tr');
					// *********具体列数根据报表决定**************
					var headerRow = 5;// 标题头占的行数,
					var footerRow = 0;// 标题尾占的行数
					if(trs.length==5){
					MyMessageTip.msg("提示","开始月份未月结!",false);
					//return;
					}
					for (var i = headerRow; i < trs.length - footerRow; i++) {// 只监听表格内容列
						var tr = trs[i];
						tr.onmousedown = function() {
							tronmousedown(this);
						}
						tr.onclick = function() {
							tronclick(this);
						}
						tr.ondblclick = function() {
							ondblclick(this);
						}
					tr.cells[12].style = "display:none;";//隐藏第13列（YPXH）
					tr.cells[13].style = "display:none;";//隐藏第14列（YPCD）
					tr.cells[14].style = "display:none;";//隐藏第15列（KSSJ）
					tr.cells[15].style = "display:none;";//隐藏第16列（ZZSJ）
//					tr.cells[16].style = "display:none;";//隐藏第17列（CWYF）
//					trs[i].cells[11].style = "display:none;";//隐藏第13列（YPXH）
//					trs[i].cells[12].style = "display:none;";//隐藏第14列（YPCD）
//					trs[i].cells[14].style = "display:none;";//隐藏第15列（KSSJ）
//					trs[i].cells[15].style = "display:none;";//隐藏第16列（ZZSJ）
//					trs[i].cells[16].style = "display:none;";//隐藏第17列（CWYF）
					}
					var lastSelectedRow = null;
					// *********选中行颜色变化*********
					function tronmousedown(obj) {
						if (obj != lastSelectedRow) {
							for (var j = 0; j < obj.cells.length - 1; j++) {
								obj.cells[j].style.backgroundColor = '#DFEBF2';
							}
							// obj.style.backgroundColor = '#DFEBF2';
							if (lastSelectedRow) {
								for (var j = 0; j < obj.cells.length - 1; j++) {
									lastSelectedRow.cells[j].style.backgroundColor = '';
								}
							}
						}
						lastSelectedRow = obj;
					}
					// *********单击事件(或者监听双击也可以)*********
					function tronclick(obj) {
						// 组装数据,数据为每行单元格中的内容
						var data = [];
						for (var j = 0; j < obj.cells.length-1 ; j++) {
							data.push(obj.cells[j].firstChild.firstChild.innerHTML);
						}
						_ctx.data=data;
					}
					function ondblclick(obj) {
						var data = [];
						for (var j = 0; j < obj.cells.length-1 ; j++) {
							data.push(obj.cells[j].firstChild.firstChild.innerHTML);
						}
						_ctx.data=data;
						_ctx.doDetail();
					}
				}
			},
			doDetail:function(){
			if(!this.data||this.data.length==0){
			return;}
			var body={"YPMC":this.data[0],"YFGG":this.data[1],"YFDW":this.data[2],"YPXH":this.data[12],"YPCD":this.data[13],"KSSJ":this.data[14],"ZZSJ":this.data[15],"QMSL":this.data[10],"QMJE":this.data[11]}
			this.m=this.createModule("MXmodule",this.refModule);
			var win=this.m.getWin();
			win.add(this.m.initPanel());
			win.show();
			win.center();
			if (!win.hidden) {
				this.m.loadData(body);
			}
			}
		})
