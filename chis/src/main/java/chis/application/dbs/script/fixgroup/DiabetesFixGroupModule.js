$package("chis.application.dbs.script.fixgroup")
//$import("util.Accredit", "app.desktop.Module")
$import("util.Accredit","chis.script.BizCombinedModule2");

chis.application.dbs.script.fixgroup.DiabetesFixGroupModule = function(cfg) {
	this.autoLoadData = false
	chis.application.dbs.script.fixgroup.DiabetesFixGroupModule.superclass.constructor
			.apply(this, [cfg])
	this.itemWidth = 210;
}
Ext.extend(chis.application.dbs.script.fixgroup.DiabetesFixGroupModule,chis.script.BizCombinedModule2, {
//			initPanel : function() {
//				var panel = new Ext.Panel({
//							border : false,
//							frame : true,
//							layout : 'border',
//							width : 960,
//							height : 480,
//							items : [{
//										layout : "fit",
//										split : true,
//                                        collapsible : true,
//										title : '',
//										region : 'west',
//										width : 250,
//										height : 400,
//										items : this.getGrid()
//									}, {
//										layout : "fit",
//										split : true,
//										title : '',
//										region : 'center',
//										height : 470,
//										width : 700,
//										items : this.getForm()
//									}]
//						});
////				this.form = this.midiModules[this.actions[1].id];
//				this.tnbdapanel=panel;
//				return panel
//
//			},
	initPanel : function(){
				var panel = chis.application.dbs.script.fixgroup.DiabetesFixGroupModule.superclass.initPanel.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.list.on("toLoadFrom",this.onToLoadFrom,this);
				this.grid = this.list.grid;
        		this.grid.on("rowClick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save",this.onSave,this);
				return panel;
			},
			
			getLoadRequest : function() {
				var body = {
					phrId : this.exContext.ids.phrId,
					empiId : this.exContext.ids.empiId
				};
				return body;
			},
			
//			loadData : function(){
//				Ext.apply(this.list.exContext,this.exContext);
//				Ext.apply(this.form.exContext,this.exContext);
//				this.form.sex = this.exContext.empiData.sexCode;
//				this.selectedIndex = 0;
//				this.list.loadData();
//			},
			
			onLoadGridData : function(store){
				if (store.getCount() == 0) {
		            return;
		        }
		        var index = this.selectedIndex;
		        if(!index){
		        	index = 0;
		        }
		        if(this.op && this.op =="create"){
		        	index = store.getCount() - 1;
		        	this.form.autoCreate = true;
		        }else{
		        	this.form.autoCreate = false;
		        }
		        this.selectedIndex = index;
		        this.list.selectRow(index);
		        var r = store.getAt(index);
		        this.process(r, index);
			},
			
			onToLoadFrom : function(grid,index){
				this.onRowClick(grid,index);
			},
			
			onRowClick : function(grid, index, e) {
		    	this.selectedIndex = index;
				var r = grid.store.getAt(index);
				this.process(r, index);
			},
	
			process : function(r, n) {
				if (!r) {
					return;
				}
				var fixId = r.get("fixId");
				var fixType = r.get("fixType");
				this.form.initDataId = fixId;
				var formData = this.castListDataToForm(r.data,this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},
//			getForm : function() {
//				$import("chis.application.dbs.script.fixgroup.DiabetesFixGroupForm")
//				var module = this.midiModules["DiabetesFixGroupForm"]
//				var cfg = {};
//				var moduleCfg = this.mainApp.taskManager.loadModuleCfg("chis.application.dbs.DBS/DBS/D11-2-1");
//				Ext.apply(cfg, moduleCfg.json.body);
//				Ext.apply(cfg, moduleCfg.json.body.properties);
//				cfg.empiId = this.empiId
//				cfg.phrId = this.phrId
//				cfg.autoLoadSchema = false
//				cfg.autoLoadData = true
//				cfg.showButtonOnTop = false
//				cfg.isCombined = true
//				cfg.colCount = 2
//				cfg.fldDefaultWidth = 150
//				cfg.autoFieldWidth = false
//				cfg.readOnly = this.readOnly
//				cfg.mainApp = this.mainApp
//				cfg.exContext = this.exContext
//				if (!module) {
//					module = new chis.application.dbs.script.fixgroup.DiabetesFixGroupForm(cfg)
//					this.midiModules["DiabetesFixGroupForm"] = module
//					var form = module.initPanel()
//					form.border = false
//					form.frame = false
//					this.form = form
//					return form;
//				}
//			},
//			// ----------------------------------------------------------------------------form
//			getGrid : function() {
//				$import("chis.application.dbs.script.fixgroup.DiabetesFixGroupList")
//				var module = this.midiModules["DiabetesFixGroupList"]
//				var cfg = {}
//				cfg.autoLoadSchema = false
//				cfg.showButtonOnTop = true
//				cfg.autoLoadData = false
//				cfg.isCombined = true
//				cfg.disablePagingTbr = true
//				cfg.empiId = this.empiId
//				cfg.readOnly = this.readOnly
//				cfg.mainApp = this.mainApp
//				cfg.exContext = this.exContext
//				if (!module) {
//					module = new chis.application.dbs.script.fixgroup.DiabetesFixGroupList(cfg)
//					this.midiModules["DiabetesFixGroupList"] = module
//					var grid = module.initPanel()
//					grid.border = false
//					grid.frame = false
//					this.grid = grid
//					return grid
//				}
//			},
//			
			loadData : function() {
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.diabetesRecordService",
					serviceAction : "initializeFixGroup",
					method:"execute",
					body : {
						empiId : this.exContext.ids.empiId,
						phrId: this.exContext.ids.phrId
					}
				})
				this.result= result
				var actions = this.actions
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					if (this.midiModules[ac.id]) {
						this.midiModules[ac.id].loadDataByLocal(result.json.body)
					}
				}
			},
			onSave : function(entryName,op,json,data){
				//通知档案表单刷新数据
				this.fireEvent("refreshData","all");
				this.list.selectedIndex = this.selectedIndex;
				this.list.loadData();
				this.op = op;
				this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
			}
//			,
//			getWin : function() {
//				var win = this.win
//				if (!win) {
//					win = new Ext.Window({
//								id : this.id,
//								title : this.title,
//								width : this.width,
//								height : 570,
//								iconCls : 'icon-form',
//								closeAction : 'hide',
//								shim : true,
//								layout : "fit",
//								plain : true,
//								minimizable : true,
//								maximizable : true,
//								constrain:true,
//								shadow : false,
//								buttonAlign : 'center',
//								items : this.initPanel()
//							})
//					win.on("show", function() {
//								this.fireEvent("winShow")
//							}, this)
//					win.on("close", function() {
//								this.fireEvent("close", this)
//							}, this)
//					win.on("hide", function() {
//								this.fireEvent("close", this)
//							}, this)
//					var renderToEl = this.getRenderToEl()
//					if (renderToEl) {
//						win.render(renderToEl)
//					}
//					this.win = win
//				}
//				win.instance = this;
//				return win;
//			}
		});