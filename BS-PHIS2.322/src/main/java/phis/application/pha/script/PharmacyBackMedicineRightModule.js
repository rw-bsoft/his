/**
 * 取消发药右边module
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyBackMedicineRightModule = function(cfg) {
	this.width = 280;
	this.height = 450;
	phis.application.pha.script.PharmacyBackMedicineRightModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	
}
Ext.extend(phis.application.pha.script.PharmacyBackMedicineRightModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									},
									{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										width : 960,
										height : 87,	
										region : 'north',
										items : this.getForm()
									}
									]
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
				this.cfForm.doNew();
				this.cfDetaiList.clear();
			},
			getForm : function() {
				this.cfForm = this.createModule("cfForm", this.refForm);
				return this.cfForm.initPanel() ;
			},
			getList:function(){
				this.cfDetaiList = this.createModule("cfDetaiList", this.refList);
				return this.cfDetaiList.initPanel();
			},
			showDetail:function(cfsb,cflx){
			var cfForm = this.midiModules['cfForm'];
			cfForm.initDataId=cfsb;
			cfForm.loadData(cflx);		
			var cfDetailList = this.midiModules['cfDetaiList'];	
			var body={};
			body["CFSB"]=cfsb;
			cfDetailList.requestData.body=body;
			cfDetailList.requestData.cnd=['eq',['$','CFSB'],['s',cfsb]];
			cfDetailList.refresh();	
			},
			getTymx:function(){
			return this.cfDetaiList.getTymx();
			},
			getSfqt:function(){
			return this.cfDetaiList.getSfqt();
			}
		});