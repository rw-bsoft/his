$package("chis.application.scm.tj.script")

$import("chis.script.BizCombinedModule2")
chis.application.scm.tj.script.HXZBMXModule = function(cfg) {
	this.isModify = false;
	chis.application.scm.tj.script.HXZBMXModule.superclass.constructor.apply(this, [ cfg ]);
}

Ext.extend(chis.application.scm.tj.script.HXZBMXModule,chis.script.BizCombinedModule2,
		{
			initPanel : function() {
				// alert("123");
				debugger;
				// url="http://192.168.10.201:8080/webroot/decision/view/report?viewlet=GW.cpt&op=write";
				// url="http://10.2.202.62:8080/list/reportlist.aspx?ConType=0&clinicId=0&sqbh=404506579514&sqbh=320124001"
				url="http://192.168.10.201:8080/webroot/decision/view/report?viewlet=homedoc_healthedu.cpt";
				// url="http://172.16.50.85:8091/"
				if (this.panel){
					return this.panel;
				}
				debugger;
				var panel = new Ext.Panel({
					border : false,
					html : "<iframe src='"+url+"'scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
					// html : "window.open("+url+")",
					frame : true,
					autoScroll : true
				});
				this.panel = panel;
				return panel;
			}
		});