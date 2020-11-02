$package("phis.application.med.script");
$import("phis.script.SimpleModule", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil");
phis.application.med.script.MedicalProjectBackWardModule = function(cfg) {

	phis.application.med.script.MedicalProjectBackWardModule.superclass.constructor
			.apply(this, [cfg]);
},

Ext.extend(phis.application.med.script.MedicalProjectBackWardModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : this.getTbar(),
							items : [{
										layout : "fit",
										split : true,
										// collapsible : true,
										bodyStyle : 'padding:5px 0',
										title : '',
										width : 240,
										region : 'west',
										items : this.getWestModule()
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										items : this.getMedProjectModule()

									}]
						});

				this.panel = panel;

				return panel
			},
			getTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "住院号码 :"
						}));

				tbar.push(new Ext.form.TextField({
							name : 'zyh',
							width : 100
						}));

				//添加Button按钮
                tbar.push({  
				id : "retreat",
				text : "退回",
				handler :this.doAction,
				iconCls :"arrow_undo" ,
				scope : this 
				});				
				tbar.push({  
				id : "refresh",
				text : "刷新",
				handler :this.doAction,
				iconCls :"arrow_refresh",
				scope : this 
				});
				tbar.push({  
				id : "close",
				text : "关闭",
				handler :this.doAction,
				iconCls:"common_cancel",
				scope : this 
				});
				return tbar;
			},
			getWestModule : function() {
				var module = this.createModule("refWestAppModule",
						this.refWestAppModule);
				this.MedicalDepartmentNameList = module;
				module.opener = this;
				return module.initPanel();
			},
			getMedProjectModule : function() {
				var module = this.createModule("refMedAppModule",
						this.refMedAppModule);
				this.MedicalProjectModule = module;
				module.opener = this;
				return module.initPanel();
			},	
			
			doAction : function(item, e) {
		      if(item.id=="refresh"){
			       this.refresh();
		     }else if(item.id=="retreat"){
			       this.retreat();
		     }else if(item.id=="close"){
			       this.close();
		     }
		     
	         },	         
			refresh : function() {	
//				alert(this.module2.classList.refresh);
				var r=this.MedicalDepartmentNameList.grid.getSelectionModel().getSelected();
				var ksdm=r.get("KSDM");
				
				this.MedicalProjectModule.classList.requestData.ksdm=ksdm;
				this.MedicalProjectModule.classList.refresh();
				
			},retreat : function() {
				
			},close : function() {
				
			}
			
			});