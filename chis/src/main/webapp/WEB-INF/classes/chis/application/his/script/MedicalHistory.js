$package("chis.application.his.script")

$import("app.desktop.Module", "chis.script.area")

chis.application.his.script.MedicalHistory = function(cfg) {
	chis.application.his.script.MedicalHistory.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(chis.application.his.script.MedicalHistory, app.desktop.Module, {
	url : null,
	port : null,
	appName : null,
	param : null,
	initPanel : function() {
		Ext.apply(this, chis.script.area);
		this.area = this.getArea();
		var idcardnow = this.exContext.empiData.idCard;
		if (this.area == "ly") {
			this.url = "http://172.31.167.105:8081/ehrview/EhrLogonService?user=gwsystem&pwd=123&idcard="
					+ idcardnow;
		} else {
			this.url = "http://32.33.1.76:8899/ehrview/EhrLogonService?user=sqxtgl&pwd=123&idcard="
					+ idcardnow;
		}
		var html;
		if (this.url) {
			html = "<iframe src=" + this.url
					+ " width='100%' height='100%' frameborder='no'></iframe>";
		} else {
			html = "<img src='image/system.jpg'>";
		}
		var panel = new Ext.Panel({
					frame : false,
					autoScroll : true,
					html : html
				})
		this.panel = panel;
		return panel;
	}
});