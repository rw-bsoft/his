$package("chis.application.cvd.script");

$import("chis.script.BizTableFormView");

chis.application.cvd.script.AppraisalForm = function(cfg) {
	cfg.colCount = 2;
	cfg.isAutoScroll = true
	cfg.fldDefaultWidth = 300;
	cfg.autoFieldWidth = false;
	chis.application.cvd.script.AppraisalForm.superclass.constructor.apply(this, [cfg]);
	this.on("beforePrint", this.onBeforePrint, this);
};
Ext.extend(chis.application.cvd.script.AppraisalForm, chis.script.BizTableFormView, {
	onReady : function() {
		chis.application.cvd.script.AppraisalForm.superclass.onReady.call(this)
	},
	doNew : function() {
		chis.application.cvd.script.AppraisalForm.superclass.doNew.call(this)
	},
	loadData : function() {
		var result = util.rmi.miniJsonRequestSync({
			cnd : ['eq', ['$', 'inquireId'], ['s', this.exContext.args.visitId]],
			method : "execute",
			schema : this.entryName,
			serviceId : "chis.simpleQuery"
		})
		if (result.json.body[0]) {
			this.initDataId = result.json.body[0].recordId
		} else {
			this.initDataId = null
		}
		chis.application.cvd.script.AppraisalForm.superclass.loadData.call(this)
		var form = this.form.getForm()
		var lifeStyle = form.findField("lifeStyle")
		lifeStyle.getToolbar().setVisible(false);

		var drugs = form.findField("drugs")
		drugs.getToolbar().setVisible(false);
	},
	onBeforePrint : function(type, pages, ids_str) {
		pages.value = "chis.prints.template.cvd";
		ids_str.value = "&empiId=" + this.exContext.ids["empiId"] + "&phrId="
				+ this.exContext.ids["phrId"] + "&inquireId="
				+ this.exContext.args.visitId + "&temp=" + new Date().getTime();
		return true;
	},
	doPrint : function() {
		var url = "resources/chis.prints.template.cvd.print?type=" + 1 + "&inquireId="
				+ this.inquireId
		url += "&temp=" + new Date().getTime()
		var win = window
				.open(
						url,
						"",
						"height="
								+ (screen.height - 100)
								+ ", width="
								+ (screen.width - 10)
								+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")

		if (Ext.isIE6) {
			win.print()
		} else {
			win.onload = function() {
				win.print()
			}
		}
	},
	saveToServer : function(saveData) {
		saveData.modified = "1"
		chis.application.cvd.script.AppraisalForm.superclass.saveToServer.call(this,
				saveData)
	}
});