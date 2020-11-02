$package("chis.application.scm.sr.script")

$import("chis.script.BizSimpleListView", "chis.script.demographicView",
        "chis.script.EHRView",
        "chis.script.area")
chis.application.scm.sr.script.QueryQyfw_RQFL= function(cfg) {
	this.isModify = false;
	chis.application.scm.sr.script.QueryQyfw_RQFL.superclass.constructor.apply(
			this, [cfg]);
}


Ext.extend(chis.application.scm.sr.script.QueryQyfw_RQFL,
		chis.script.BizSimpleListView, {
			initPanel : function() {
					 if (this.panel)
					     return this.panel;
				         var panel = new Ext.Panel({
							border : false,
							html : "<iframe src='"+"http://10.2.202.39:8081/hcms/thematic/EHR_jkda_050"+"'scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
							frame : true,
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;		
        },		
			onReady : function() {
			}
		});