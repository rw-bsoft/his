$package("chis.application.hc.script");

$import("chis.script.BizCombinedModule2");

chis.application.hc.script.MProgestationCheckModule= function(cfg) {
	cfg.autoLoadData = false;
	chis.application.hc.script.MProgestationCheckModule.superclass.constructor
			.apply(this, [cfg]);
	this.itemWidth = 210;
};

Ext.extend(chis.application.hc.script.MProgestationCheckModule,
		chis.script.BizCombinedModule2, {
			initPanel : function(){
				var panel = chis.application.hc.script.MProgestationCheckModule.superclass.initPanel.call(this);
				this.panel = panel;
				this.list = this.midiModules[this.actions[0].id];
				this.list.on("loadData", this.onLoadGridData, this);
				this.list.on("toLoadFrom",this.onToLoadFrom,this);
				this.grid = this.list.grid;
        		this.grid.on("rowClick", this.onRowClick, this);
				this.form = this.midiModules[this.actions[1].id];
				this.form.on("save",this.onSave,this);
				this.form.on("add",this.onAdd,this);
				return panel;
			},
			
			getLoadRequest : function() {
				var body = {
					phrId : this.exContext.ids.phrId,
					empiId : this.exContext.ids.empiId
				};
				return body;
			},
			
			loadData : function(){
				Ext.apply(this.list.exContext,this.exContext);
				Ext.apply(this.form.exContext,this.exContext);
				this.form.sex = this.exContext.empiData.sexCode;
				this.selectedIndex = 0;
				this.list.loadData();
			},
			
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
				var recordId = r.get("recordId");
				this.form.initDataId = recordId;
				var formData = this.castListDataToForm(r.data,this.list.schema);
				this.form.initFormData(formData);
				this.form.validate();
			},
			
//			getFixGroupControl : function(fixId,fixType,hypertensionGroup){
//				this.panel.el.mask("正在获取操作权限...", "x-mask-loading");
//				var result = util.rmi.miniJsonRequestSync({
//							serviceId : "chis.hypertensionService",
//							serviceAction : "getHyperFixGroupControl",
//							method:"execute",
//							body : {
//								"empiId" : this.exContext.ids.empiId,
//								"phrId" : this.exContext.ids.phrId,
//								"fixType" : fixType,
//								"hypertensionGroup" : hypertensionGroup,
//								"fixId" : fixId
//							}
//						});
//				this.panel.el.unmask();
//				if (result.code != 200) {
//					this.processReturnMsg(result.code, result.msg);
//					return null;
//				}
//				return result.json.body;
//			},
			
			onSave : function(entryName,op,json,data){
				//通知档案表单刷新数据
				this.fireEvent("refreshData","all");
//				this.fireEvent("save","chis.application.pub.schemas.PUB_VisitPlan", op, json, data);
				this.list.selectedIndex = this.selectedIndex;
				this.list.loadData();
				this.op = op;
				this.fireEvent("chisSave");//phis中用于通知刷新emrView左边树
			},
			
			onAdd : function() {
				var recordNum = this.list.store.getCount();
				if(recordNum > 0){
					var r = this.list.store.getAt(recordNum - 1);
//					if(!r.get("hypertensionGroup")){
//						Ext.Msg.alert("提示","有未做评估记录!");
//						return;
//					}
//					if(!r.get("riskLevel")){
//						Ext.Msg.alert("提示","有未做评估记录!");
//						return;
//					}
//					if(!r.get("hypertensionLevel")){
//						Ext.Msg.alert("提示","有未做评估记录!");
//						return;
//					}
//					if (r.get("fixId") == null) {
//						Ext.Msg.alert("提示","有未做评估记录!");
//						return;
//					}
				}
				var data = {};
				if(this.list.store.getCount()>0){
					var frmData = this.form.getFormDataDic();
					frmData.lastModifyUser = {"key":this.mainApp.uid,"text":this.mainApp.uname};
					frmData.lastModifyUnit = {"key":this.mainApp.deptId,"text":this.mainApp.dept};
					frmData.lastModifyDate = this.mainApp.serverDate;
					//frmData.fixDate = this.mainApp.serverDate 
					data = this.castFormDataToList(frmData,this.list.schema);
					delete data.fixId;
				}else{
					data = this.form.loadInitData();
				}
				data.fixId = null;
				this.form.initDataId = null;
				this.form.data.fixId = null;
				this.list.doAdd(data);
			},
			
			copyValue : function(data1,data2){
				
			}
		});