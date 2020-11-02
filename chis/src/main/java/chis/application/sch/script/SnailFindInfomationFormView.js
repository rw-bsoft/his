$package("chis.application.sch.script");

$import("chis.script.BizTableFormView");

chis.application.sch.script.SnailFindInfomationFormView = function(cfg) {
	cfg.labelWidth = 100;
	cfg.width=800;
	cfg.showButtonOnTop = true;
	chis.application.sch.script.SnailFindInfomationFormView.superclass.constructor.apply(this,[cfg]);
	this.on("winShow",this.onWinShow,this);
	this.saveServiceId="chis.snailBaseInfoService";
	this.saveAction="saveSnailFindInfo";
};

Ext.extend(chis.application.sch.script.SnailFindInfomationFormView, chis.script.BizTableFormView, {
	onWinShow:function(){
		if(this.op == "create"){
			this.doCreate();
			var snailBaseInfoId = this.form.getForm().findField("snailBaseInfoId");
			snailBaseInfoId.setValue(this.snailBaseInfoId);
		}
	}
});
