$package("phis.application.sto.script");
$import("phis.script.SimpleModule")

phis.application.sto.script.StorehouseAccountingBooksDetailModule = function(
		cfg) {
	this.printurl = util.helper.Helper.getUrl();
	this.exContext = {};
	this.data = [];
	phis.application.sto.script.StorehouseAccountingBooksDetailModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseAccountingBooksDetailModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				this.frameId = "SimplePrint_frame_StorehouseAccountingBooksDetail";
				var panel = new Ext.Panel({
							id : "StorehouseAccountingBooksDetail",
							width : 1000,
							height : 500,
							title : "",
							tbar : this.createButtons(),
							html : "<iframe id='" + this.frameId
									+ "' width='100%' height='100%' ></iframe>"
						})
				this.panel = panel
				this.panel.on("afterrender", this.onReady, this);
				return panel
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
								_ctx.simplePrintMask(iframe);
							});
				} else {
					iframe.onload = function() {
						_ctx.simplePrintMask(iframe);
					};
				}
			},
			doQuery : function(tag) {
				this.data = [];
				this.panel.el.mask("正在生成报表...", "x-mask-loading");
				var pages = "phis.prints.jrxml.StorehouseAccountingBooksDetail";
				if (tag == 1) {
					pages = "phis.prints.jrxml.StorehouseAccountingBooksDetail_dy";
				}
				var url = "resources/" + pages + ".print?type=1";
				url += "&dateFrom=" + this.dateF + "&dateTo=" + this.dateT
						+ "&ypxh=" + this.YPXH + "&ypcd=" + this.YPCD;
				if (tag == 1) {
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
				} else {
					document.getElementById(this.frameId).src = url;
				}
			},
			doPrint : function() {
				this.doQuery(1)
			},
			/**
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
					var headerRow = 7;// 标题头占的行数,第一行数据是期初数据也不能点击
					var footerRow = 2;// 标题尾占的行数
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
						tr.cells[12].style = "display:none;";// 隐藏第12列（BZ）
						tr.cells[13].style = "display:none;";// 隐藏第13列（XTSB）
						tr.cells[14].style = "display:none;";// 隐藏第14列（FS）
						tr.cells[15].style = "display:none;";// 隐藏第15列（JGID）
						tr.cells[16].style = "display:none;";// 隐藏第16列（YSDG）
						tr.cells[17].style = "display:none;";// 隐藏第17列（DWXH）
					}
					var lastSelectedRow = null;
					// *********选中行颜色变化*********
					function tronmousedown(obj) {
						if (obj != lastSelectedRow) {
							for (var j = 1; j < obj.cells.length - 1; j++) {
								obj.cells[j].style.backgroundColor = '#DFEBF2';
							}
							// obj.style.backgroundColor = '#DFEBF2';
							if (lastSelectedRow) {
								for (var j = 1; j < obj.cells.length - 1; j++) {
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
						for (var j = 1; j < obj.cells.length - 1; j++) {
							if (obj.cells[j].firstChild.firstChild) {
								data
										.push(obj.cells[j].firstChild.firstChild.innerHTML);
							}
						}
						_ctx.data = data;
					}
					function ondblclick(obj) {
						var data = [];
						for (var j = 1; j < obj.cells.length - 1; j++) {
							data
									.push(obj.cells[j].firstChild.firstChild.innerHTML);
						}
						_ctx.data = data;
						_ctx.doDetail();
					}
				}
			},
			doDetail : function() {
				if (!this.data || this.data.length == 0) {
					return;
				}
				if (this.data[10] == 0) {// 平账记录
					MyMessageTip.msg("提示", "平账记录没有明细!", true);
					return;
				}
				if (this.data[10] == 1) {// 入库
					var initDataBody = {};
					initDataBody["XTSB"] = this.data[11];
					initDataBody["RKFS"] = this.data[12];
					initDataBody["RKDH"] = this.data[1];
					initDataBody["YSDH"] = this.data[14];
					var ref=this.cgrkRef
					if(!this.data[16]||this.data[16]==null||this.data[16]==0){//第16个是单位序号,如果不为空则为采购入库
					ref=this.qtrkRef;
					this.rkModule = this.createModule("qtrkModule", ref);
					}else{
					this.rkModule = this.createModule("rkModule", ref);
					}
					this.rkModule.on("winClose", this.onRkClose, this);
					var win = this.rkModule.getWin();
					win.add(this.rkModule.initPanel());
					this.rkModule.condition = {};
					win.show()
					win.center()
					if (!win.hidden) {
						this.rkModule.isRead = true;
						this.rkModule.doRead(initDataBody);
					}
				} else if (this.data[10] == 2) {// 出库
					var initDataBody = {};
					initDataBody["xtsb"] = this.data[11];
					initDataBody["ckfs"] = this.data[12];
					initDataBody["ckdh"] = this.data[1];
					this.ckModule = this.createModule("ckModule", this.ckRef);
					this.ckModule.on("winClose", this.onCkClose, this);
					var win = this.ckModule.getWin();
					win.add(this.ckModule.initPanel());
					this.ckModule.condition = {
						"ksly" : this.data[15]
					};
					win.show()
					win.center()
					if (!win.hidden) {
						this.ckModule.isRead = true;
						this.ckModule.doRead(initDataBody);
					}
				} else if (this.data[10] == 3) {// 调价
					var initDataBody = {};
					initDataBody["TJFS"] = this.data[12];
					initDataBody["TJDH"] = this.data[1];
					initDataBody["JGID"] = this.data[13];
					initDataBody["XTSB"] = this.data[11];
					this.tjModule = this.createModule(
							"tjModule", this.tjRef);
					this.tjModule.on("winClose", this.onTjClose, this);
					var win = this.tjModule.getWin();
					win.add(this.tjModule.initPanel());
					win.show()
					win.center()
					if (!win.hidden) {
						this.tjModule.doRead(initDataBody);
					}
				}
			},
			doNew : function() {
				document.getElementById(this.frameId).src = "";
			},
			doCancel : function() {
				this.getWin().hide();
			},
			onRkClose:function(){
			this.rkModule.getWin().hide();
			},
			onCkClose:function(){
			this.ckModule.getWin().hide();
			},
			onTjClose:function(){
			this.tjModule.getWin().hide();
			}
		})
