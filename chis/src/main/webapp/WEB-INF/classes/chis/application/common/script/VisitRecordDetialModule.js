$package("chis.application.common.script")
$import("util.Accredit", "chis.script.BizModule",
		"chis.script.util.widgets.MyMessageTip")
chis.application.common.script.VisitRecordDetialModule = function(cfg) {
	chis.application.common.script.VisitRecordDetialModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.common.script.VisitRecordDetialModule,
		chis.script.BizModule, {
			initPanel : function() {
				var me=this;
				var panelcfg={
						layout:'card',
						activeItem: 0,
						items: [
						        this.getVisitRecordDetialList(),
						        this.getVisitRecordMuseList(),
						        this.getVisitRecordPacsListView()
						        ]

				};
				var panel=new Ext.Panel(panelcfg);
				this.panel=panel;
				return panel;
			},
			getVisitRecordDetialList:function()
			{
				var module = this.createSimpleModule("VisitRecordDetialList",this.VisitRecordDetialList);
				this.VisitRecordDetialList = module;
				module.opener=this;
				return module.initPanel();

			},
			getVisitRecordMuseList:function()
			{
				var module = this.createSimpleModule("VisitRecordMuseList",this.VisitRecordMuseList);
				this.VisitRecordMuseList = module;
				module.opener=this;
				return module.initPanel();
			},
			getVisitRecordPacsListView:function()
			{
				var module = this.createSimpleModule("VisitRecordPacsList",this.VisitRecordPacsList);
				this.VisitRecordPacsList = module;
				module.opener=this;
				return module.initPanel();
			},
			setActivePanel:function(r)
			{
				if(!r)
					return;
				if(r.data.ZBLB==10){
					this.panel.layout.setActiveItem(1);
					this.VisitRecordMuseList.queryParam=r;
					this.VisitRecordMuseList.loadData();
				}else if(r.data.ZBLB==9)
				{
					this.panel.layout.setActiveItem(2);	
					this.VisitRecordPacsList.queryParam=r;
					this.VisitRecordPacsList.loadData();
				}else
				{
					this.panel.layout.setActiveItem(0);
					this.VisitRecordDetialList.queryParam=r;
					this.VisitRecordDetialList.loadData();	
				}
			}
		});