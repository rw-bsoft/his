$package("chis.application.cvd.script");

$import("chis.script.BizModule");

chis.application.cvd.script.AssessRegisterPic = function(cfg) {
	this.data = {}
	chis.application.cvd.script.AssessRegisterPic.superclass.constructor.apply(this, [cfg]);
};

Ext.extend(chis.application.cvd.script.AssessRegisterPic, chis.script.BizModule, {
	initPanel : function() {
		var tpl = this.getTemplate()
		var panel = new Ext.Panel({
					border : false,
					frame : false,
					collapsible : false,
					layout : 'fit',
					html : tpl.apply(this.exContext.args.data)
				})
		this.panel = panel;
		return panel;
	},
	getTemplate : function() {
		if (this.tpl) {
			return this.tpl;
		}
		this.photoUrl = ClassLoader.appRootOffsetPath + "resources/chis/resources/app/desktop/images/cvd/{url}.jpg"
		var tpl = new Ext.XTemplate('<div height="400" style="overflow-y:scroll"><img src="'
						+ this.photoUrl + '" width="700" height="400"/> </div>');
		this.tpl = tpl
		return tpl;
	},
	loadData : function() {
		var tpl = this.getTemplate()
		this.panel.body.update(tpl.apply(this.exContext.args.data))
	}
});