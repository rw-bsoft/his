$package("phis.prints.script")
$import("phis.script.SimpleModule", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.prints.script.DRRegisterReqOrderPrintView = function(cfg) {
	this.printurl = util.helper.Helper.getUrl();
	phis.prints.script.DRRegisterReqOrderPrintView.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.prints.script.DRRegisterReqOrderPrintView,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.frameId = "SimplePrint_frame_phis.prints.jrxml.RegistHospitalReqOrder";
				this.conditionFormId = "SimplePrint_form_phis.prints.jrxml.RegistHospitalReqOrder";
				this.mainFormId = "SimplePrint_mainform_phis.prints.jrxml.RegistHospitalReqOrder";
				var panel = new Ext.Panel({
					id : this.mainFormId,
					width : this.width,
					height : this.height,
					title : this.title,
					tbar : {
						layoutConfig : {
							pack : 'start',
							align : 'middle'
						},
						frame : true,
						enableOverflow : true
					},
					html : "<iframe id='"
							+ this.frameId
							+ "' width='100%' height='100%' onload='simplePrintMask(\"phis.prints.jrxml.RegistHospitalReqOrder\")'></iframe>"
				})
				this.panel = panel
				panel.on("afterrender", this.onReady, this);
				return panel
			},
			onReady : function() {
				Ext.getCmp(this.mainFormId).el.mask("正在生成报表...",
						"x-mask-loading");
				// 后台servelt
				// var url =
				// "*.print?pages=phis.prints.jrxml.DoctorsAccountingServlet&config="
				// + encodeURI(encodeURI(Ext.encode(printConfig)))
				// + "&silentPrint=1";
				var pages = "phis.prints.jrxml.RegistHospitalReqOrder";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&zhuanzhendh="
						+ this.zhuanzhendh;
				document.getElementById(this.frameId).src = url
			},
			loadData : function() {
				var pages = "phis.prints.jrxml.RegistHospitalReqOrder";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&zhuanzhendh="
						+ this.zhuanzhendh;
				document.getElementById(this.frameId).src = url
			},
			doPrint : function() {
				var pages = "phis.prints.jrxml.RegistHospitalReqOrder";
				var url = "resources/" + pages
						+ ".print?silentPrint=1&zhuanzhendh="
						+ this.zhuanzhendh;
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
			}
		})
simplePrintMask = function(printId) {
	Ext.getCmp("SimplePrint_mainform_" + printId).el.unmask()
}