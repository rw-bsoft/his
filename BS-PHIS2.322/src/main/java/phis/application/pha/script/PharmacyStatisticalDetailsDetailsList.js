$package("phis.application.pha.script");

$import("phis.script.SimpleList");

phis.application.pha.script.PharmacyStatisticalDetailsDetailsList = function(cfg) {
	cfg.autoLoadData = false;//不自动加载
	cfg.disablePagingTbr = true;//隐藏分页
	cfg.modal=true;
	phis.application.pha.script.PharmacyStatisticalDetailsDetailsList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyStatisticalDetailsDetailsList,
		phis.script.SimpleList, {
			loadData : function() {
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.listActionId
				phis.application.pha.script.PharmacyStatisticalDetailsDetailsList.superclass.loadData
						.call(this);
			},
			doClose : function() {
				this.getWin().hide();
			},
			doPrint : function() {
				var cm = this.grid.getColumnModel()
				var cm = this.cm
				var cos = cm.getColumnsBy(function(c) {
							return !c.hidden;
						})
				var cname = []
				for (var i = 0; i < cos.length; i++) {
					cname.push(cos[i].dataIndex)
				}
				var printConfig = {
					title : this.title,
					page : "whole",
					requestData : this.requestData,
					cname : cname
				}
				var type = 1;
				var url = "list.print?type=" + type + "&config="
						+ encodeURI(encodeURI(Ext.encode(printConfig)))
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE(1, "", "", "A4");
				LODOP.ADD_PRINT_HTM("5cm", "5cm", "100%", "100%", util.rmi
								.loadXML({
											url : url,
											httpMethod : "get"
										}));
				LODOP.PREVIEW()
			}
		})