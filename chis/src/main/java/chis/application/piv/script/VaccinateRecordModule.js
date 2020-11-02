$package("chis.application.piv.script")
$import("util.Accredit", 
"chis.script.BizModule",
"chis.application.piv.script.VaccinateRecordForm",
"chis.application.piv.script.VaccinateRecordListView"
)
chis.application.piv.script.VaccinateRecordModule = function(cfg) {
	chis.application.piv.script.VaccinateRecordModule.superclass.constructor.apply(this, [cfg])
	this.entryName = "chis.application.piv.schemas.PIV_VaccinateRecord"
	this.autoLoadData = false ;
	this.width = 1000
}
Ext.extend(chis.application.piv.script.VaccinateRecordModule, chis.script.BizModule, {
			initPanel : function(sc) {
				var form = this.getForm()
				var grid = this.getGrid()
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'anchor',
							width : 900,
							height : 500,
							autoWidth : true,
							items : [{
										layout : "fit",
										border : false,
										frame : false,
										split : true,
										title : '',
										region : 'north',
										width : 900,
										anchor:'100% 30%',
										items : form
									}, {
										layout : "fit",
										border : false,
										frame : false,
										split : true,
										title : '',
										region : 'center',
										width : 900,
										anchor:'100% 70%',
										items : grid
									}]
						});
				return panel
			},
			
			loadData:function(){
				var formModule = this.midiModules["VaccinateRecordForm"] ;
				var gridModule = this.midiModules["VaccinateList"];
				var phrId = this.exContext.ids["PIV_VaccinateRecord.phrId"]
							||this.exContext.ids.phrId;
				if(phrId && phrId.length >0){
					if(formModule){
						formModule.phrId =phrId ;
						formModule.initDataId = phrId;
						formModule.loadData();
					}
					if(gridModule){
						gridModule.phrId =phrId ;
						gridModule.refresh();
					}	
				}
			},
			
			getForm : function(sc) {
				if(this.form)
					return this.form ;
					
//				var config = this.loadAppConfig(this.actions[0].ref);
				var config = this.loadModuleCfg(this.actions[0].ref)
				var cfg = {
							autoLoadSchema : false,
							isCombined : true,
							entryName : config.entryName,
							actions : config.actions
					};
				Ext.apply(cfg,config.properties)
				var module = eval("new " + config.script + "(cfg)");
				this.midiModules["VaccinateRecordForm"] = module
				var form = module.initPanel();
				form.frame = false
				form.border = false
				this.form = form
				return form;
			}, 
			
//			loadAppConfig : function(id) {
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "moduleConfigLocator",
//							id : id
//						});
//				if (result.code == 200) {
//					return result.json.body;
//				}
//			},
			
			getGrid : function() {
				if(this.grid){
					return this.grid ;
				}
				
				$import("chis.application.piv.script.VaccinateList")
				module = new chis.application.piv.script.VaccinateList({
					autoLoadSchema:false ,
					autoLoadData : false,
					isCombined: true,
					entryName :"chis.application.piv.schemas.PIV_VaccinateList"
				})
				this.midiModules["VaccinateList"] = module
				var grid = module.initPanel()
				grid.frame = false
				grid.border = false
				this.grid = grid
				return grid
			},
			
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								height : 570,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								constrain:true,
								maximizable : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel()
							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				win.instance = this;
				return win;
			}
		});