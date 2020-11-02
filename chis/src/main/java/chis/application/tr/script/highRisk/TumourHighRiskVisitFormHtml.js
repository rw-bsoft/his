$package("chis.application.tr.script.highRisk")

$import("chis.script.BizTableFormView","chis.application.tr.script.highRisk.TumourHighRiskVisitTemplate")
$styleSheet("chis.css.tumourHighRiskVisitForm")

chis.application.tr.script.highRisk.TumourHighRiskVisitForm = function(cfg){
	chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.constructor.apply(this,[cfg]);
	Ext.apply(this,chis.application.tr.script.highRisk.TumourHighRiskVisitTemplate)
	this.width = 1120;
	this.height = 650;
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskVisitForm,chis.script.BizTableFormView,{
	initPanel : function(sc) {
		if (this.form) {
			return this.form;
		}
		
		this.data = this.getLanderInfo();
		var tpl = this.getTopTemplate();
		var cfg = {
			border : false,
			frame : true,
			collapsible : false,
			autoScroll : true,
			width : this.width,
			height : this.height,
			layout : 'fit',
			region : 'north',
			html : tpl.apply(this.data)
		}
		this.changeCfg(cfg);
		this.initBars(cfg);
		this.form = new Ext.FormPanel(cfg);
		this.form.on("afterrender",this.onReady,this);	
		return this.form;
	},
	
	getTopTemplate : function() {
		var emrHtml = this.getTumourHighRiskVisitHTML();
		var tpl = new Ext.XTemplate(emrHtml);
		return tpl;
	},
	getLanderInfo : function() {
//		var result = util.rmi.miniJsonRequestSync({
//					serviceId : "chis.tumourHighRiskService",
//					serviceAction : "initializeVisitForm",
//					body:{
//						"empiId" : this.empiId
//					}
//				});
//		if (result.code > 300) {
//			this.processReturnMsg(result.code, result.msg);
//			return
//		}
//		return result.json.body;
		return '';
	},
	onReady : function(){
		var irregularVaginalBleeding = Ext.getDoc("irregularVaginalBleeding");
		if(irregularVaginalBleeding){
			irregularVaginalBleeding.disable();
			irregularVaginalBleeding.setValue("123..a")
//			irregularVaginalBleeding.disabled=true;
//			irregularVaginalBleeding.value="abc123";
		}
		chis.application.tr.script.highRisk.TumourHighRiskVisitForm.superclass.onReady.call(this);	
	}
});