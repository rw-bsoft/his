$package("phis.application.pcm.script")
$import("phis.script.SimpleList")

phis.application.pcm.script.PrescriptionCommentsTjMXList = function(cfg) {
	cfg.autoLoadData=false;
	//cfg.disablePagingTbr=true;
	cfg.modal=true;
	cfg.width=800
	phis.application.pcm.script.PrescriptionCommentsTjMXList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.pcm.script.PrescriptionCommentsTjMXList,
		phis.script.SimpleList, {
		loadData:function(){
		this.requestData.serviceId=this.serviceId;
		this.requestData.serviceAction=this.serviceAction;
		phis.application.pcm.script.PrescriptionCommentsTjMXList.superclass.loadData.call(this)
		},
		doPrint:function(){
				var url = "resources/phis.prints.jrxml.PrescriptionCommentsTjMX.print?type=1&cnd="
						+  Ext.encode(this.requestData.cnd);
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
		}
		})