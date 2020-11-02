/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.RefundProcessingModule1 = function(cfg) {
	phis.application.ivc.script.RefundProcessingModule1.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.ivc.script.RefundProcessingModule1,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
//							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
//										split : true,
										title : '',
										region : 'west',
										width : 200,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
//										split : true,
										title : '',
										region : 'center',
										items : this.getModule2()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getList : function() {
				this.tfList1 = this.createModule("tfList1", this.refList);
				this.tfList1.opener = this;
				return this.tfList1.initPanel();
			},
			getModule2 : function() {
				this.tfModule2 = this.createModule("tfModule2", this.refModule);
				this.tfModule2.opener = this;
				return this.tfModule2.initPanel();
			},
			loadData : function(fphm){
				this.tfList1.requestData.fphm=fphm;
				this.tfList1.loadData();
			},
			setDetails : function(details){
				this.tfList1.details = details;
				this.tfModule2.setDetails(details);
			},
			setDListloadData : function(data){
				this.tfModule2.setDListloadData(data);
			}
		});