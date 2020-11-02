/**
 * 药房发药右边module
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyDispensingRightModule = function(cfg) {
	this.width = 280;
	this.height = 450;
	phis.application.pha.script.PharmacyDispensingRightModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.pha.script.PharmacyDispensingRightModule,
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
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										width : 960,
										height : 88,
										region : 'north',
										items : this.getForm()
									}]
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
				this.cfForm.doNew();
				this.cfDetaiList.doNew();
			},
			getForm : function() {
				this.cfForm = this.createModule("cfForm", this.refForm);
				this.cfForm.on("loadData",this.onFormLoad,this)
				return this.cfForm.initPanel();
			},
			getList : function() {
				this.cfDetaiList = this.createModule("cfDetaiList",
						this.refList);
				return this.cfDetaiList.initPanel();
			},
			showDetail : function(cfsb,cflx) {
				if (!this.cfForm || !this.cfDetaiList) {
					return;
				}
				this.cfForm.initDataId = cfsb;
				this.cfForm.loadData(cflx);
				//this.cfDetaiList.cflxsb(cflx);
				this.cfDetaiList.requestData.cnd =['and',['eq', ['$', 'CFSB'],
						['i', cfsb]],['ne', ['$', 'a.YPXH'],
						['i', 0]]] ;
				this.cfDetaiList.requestData.cfsb=cfsb;
				this.cfDetaiList.requestData.yfsb=this.mainApp['phis'].pharmacyId;
				this.cfDetaiList.refresh();
			},
			onFormLoad:function(){
			this.fireEvent("afterFormload",this);
			}
		});