$package("phis.application.war.script")
$import("app.desktop.Module", "util.widgets.MyRadioGroup",
		"util.dictionary.TreeDicFactory", "util.helper.Helper")

phis.application.war.script.NursePlanDataShowModule = function(cfg) {

	this.printurl = util.helper.Helper.getUrl();
	phis.application.war.script.NursePlanDataShowModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.NursePlanDataShowModule,
		app.desktop.Module, {
			initPanel : function(data) {
				this.frameId = "NursePlanFid";
				this.mainFormId = "NursePlanPanel";
				if (this.panel) {
					return this.panel;
				}
				this.zyh = data.zyh;
				var panel = new Ext.Panel({
					id : this.mainFormId,
					title : '护理计划单',
					layout : 'fit',
					// height : 600,
					// width : 480,
					// autoHeight : true,
					// autoWidth : true,
					html : "<iframe id='"
							+ this.frameId
							// + "' width='100%' height='100%' src='" +
							// this.printurl +
							// "*.print?pages=phis.prints.jrxml.NursePlanPrint&zyh="
							// + this.zyh + "'></iframe>"
							+ "' width='100%' height='100%' src='resources/phis.prints.jrxml.NursePlanPrint.print?zyh="
							+ this.zyh + "'></iframe>"
				});
				this.panel = panel;
				return this.panel;
			},
			/**
			 * 重新加载数据
			 * 
			 * @param data
			 */
			reloadData : function(data) {
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "publicService",
					serviceAction : "loadSystemParams",
					body : {}
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, pdms.msg, this.reloadData,
							data);
					this.opener.getWin.hide();
					return;
				}
				this.zyh = data.zyh;
				Ext.getCmp(this.mainFormId).body
						.update("<iframe id='"
								+ this.frameId
								// + "' width='100%' height='100%' src='" +
								// this.printurl +
								// "*.print?pages=phis.prints.jrxml.NursePlanPrint&zyh="
								// + this.zyh + "'></iframe>");
								+ "' width='100%' height='100%' src='resources/phis.prints.jrxml.NursePlanPrint.print?zyh="
								+ this.zyh + "'></iframe>");
			},
			doPrint : function() {
				var pdms = phis.script.rmi.miniJsonRequestSync({
					serviceId : "publicService",
					serviceAction : "loadSystemParams",
					body : {}
						// cardOrMZHM : data.cardOrMZHM
					});
				if (pdms.code > 300) {
					this.processReturnMsg(pdms.code, pdms.msg, this.reloadData);
					this.opener.getWin.hide();
					return;
				}
				// var pages="phis.prints.jrxml.AccountsDetail";
				var pages = "phis.prints.jrxml.NursePlanPrint";
				var url = "resources/" + pages + ".print?type=" + 1;
				// url += "&jzrq=" + this.jzrq + "&jzbs=" + this.jzbs+ "&czgh="
				// + this.mainApp.logonName;
				url += "&zyh=" + this.zyh;
				window
						.open(
								url,
								"",
								"height="
										+ (screen.height - 100)
										+ ", width="
										+ (screen.width - 10)
										+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
			}

		});