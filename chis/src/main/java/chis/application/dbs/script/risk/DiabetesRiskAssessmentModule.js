$package("chis.application.dbs.script.risk")
$styleSheet("chis.resources.app.biz.EHRView")
$import("chis.script.BizCombinedModule2","util.Accredit")

chis.application.dbs.script.risk.DiabetesRiskAssessmentModule = function(cfg) {
	cfg.itemWidth = 150
	chis.application.dbs.script.risk.DiabetesRiskAssessmentModule.superclass.constructor.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this)
	this.on("afterLoadModule",this.onAfterLoadModule,this)
}

Ext.extend(chis.application.dbs.script.risk.DiabetesRiskAssessmentModule, chis.script.BizCombinedModule2, {
			onAfterLoadModule:function(moduleName, cfg){
				if(moduleName == this.actions[0].id){
					this.midiModules[this.actions[0].id].on("loadData",this.onLoadListData,this)
					this.midiModules[this.actions[0].id].on("rowClick", this.onRowClick, this)
				}
				if(moduleName == this.actions[1].id){
					this.midiModules[this.actions[1].id].on("save",this.onFormSave,this)
					this.midiModules[this.actions[1].id].on("create",this.onFormCreate,this)
				}
			}
			,
			onFormCreate:function(){
				if(this.midiModules[this.actions[0].id].store.getCount() > 0){
					for(var i = 0;i<this.midiModules[this.actions[0].id].store.getCount();i++){
						var r = this.midiModules[this.actions[0].id].store.getAt(i)
						if(r.data.estimateDate.substr(0,10) == this.mainApp.serverDate.substr(0,10)){
							Ext.Msg.alert('提示', '今天已经做过评估。');
							return
						}
					}
				}
				this.midiModules[this.actions[1].id].initDataId = null
				this.midiModules[this.actions[1].id].doNew()
				var estimateType = {}
				if(this.midiModules[this.actions[0].id].store.getCount() == 0){
					estimateType.key = '1'
					estimateType.text = '首次评估'
				}else{
					estimateType.key = '2'
					estimateType.text = '不定期评估'
				}
				this.midiModules[this.actions[1].id].form.getForm().findField("estimateType").setValue(estimateType)
				this.midiModules[this.actions[1].id].onEstimateDate()
			}
			,
			onFormSave:function(entryName,op,json,data){
				this.exContext.args.recordId = data.recordId
				this.midiModules[this.actions[0].id].loadData();
				this.fireEvent("save")
			}
			,
			loadData:function(){
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.diabetesRiskService",
					serviceAction : "initializeDiabetesRiskAssessment",
					method:"execute",
					body : {
						empiId : this.exContext.ids.empiId
					}
				})
				Ext.apply(this.exContext.args,result.json.body)
				
				this.refreshExContextData(this.midiModules[this.actions[0].id], this.exContext);
				this.refreshExContextData(this.midiModules[this.actions[1].id], this.exContext);
				this.midiModules[this.actions[0].id].requestData.cnd = ['and',['eq',['$','a.empiId'],['s',this.exContext.ids.empiId]],['eq',['$','a.riskId'],['s',this.exContext.ids.riskId]]]
				this.midiModules[this.actions[0].id].loadData();
			}
			,
			onLoadListData:function(store){
				 if (store.getCount() == 0) {
			    		this.midiModules[this.actions[1].id].doCreate();
			    		return
		        }else{
		        	if(this.exContext.args.recordId){
						if(store.find("recordId", this.exContext.args.recordId)>=0){
							this.midiModules[this.actions[0].id].selectedIndex = store.find("recordId", this.exContext.args.recordId);
						}else{
							this.midiModules[this.actions[0].id].selectedIndex = 0;
						}
					}else{
						this.midiModules[this.actions[0].id].selectedIndex = 0
					}
		        }
		        var r = store.getAt(this.midiModules[this.actions[0].id].selectedIndex)
		        this.loadFormData(r)
			},
			onRowClick : function(grid, index, e) {
				var r = grid.store.getAt(index)
				this.loadFormData(r)
			},
			loadFormData:function(r){
				 this.midiModules[this.actions[1].id].exContext.args.record = r
				 this.midiModules[this.actions[1].id].initDataId = r.data.recordId
				 this.midiModules[this.actions[1].id].loadData()
			}
			,
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width || 800,
								height : this.height || 450,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.win.doLayout()
								this.fireEvent("winShow", this)
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			}
			,
			onWinShow:function(){
				this.win.maximize()
				this.loadData()
			}
		})