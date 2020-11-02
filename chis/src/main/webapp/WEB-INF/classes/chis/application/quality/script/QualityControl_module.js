$package("chis.application.quality.script")

$import("chis.script.BizCombinedModule2")

chis.application.quality.script.QualityControl_module = function(cfg) {
	cfg.autoLoadData = false
	chis.application.quality.script.QualityControl_module.superclass.constructor.apply(
			this, [cfg]);
   	this.itemCollapsible = false
	this.layOutRegion = "north"
	this.itemWidth = 800
	this.itemHeight = 130
	this.width = 1000
	this.height = 450
}
Ext.extend(chis.application.quality.script.QualityControl_module,
		chis.script.BizCombinedModule2, {
			initPanel : function() {
				var panel = chis.application.quality.script.QualityControl_module.superclass.initPanel
						.call(this)
				this.panel = panel;
				this.list = this.midiModules[this.actions[1].id];
				this.form = this.midiModules[this.actions[0].id];
				this.form.on("formClose", function() {
						   	this.fireEvent("close", this);
							}, this);
				
				if(this.op){
					this.form.op=this.op;
				}
			 	this.form.on("save", this.onFormSave, this);
			 	this.form.on("modify", this.onFormModify, this)
				return panel;
		  },
		  doLoad : function(r) {
		  	if(r && this.op && this.op=="update"){
		  		var formId=r.get("ID");
			  	this.form.doNew();
			  	this.form.initDataId=formId;
			  	this.form.loadData();
			  	
	        	var listXh=r.get("XMBS");
			    this.list.xmxhId=listXh;
		    	this.list.onReady();
		    	this.list.loadData();
		  	}else{
		  	   this.form.doNew();
			  	this.form.initDataId="";
			  	this.form.loadData();
			    this.list.xmxhId="";
		    	this.list.onReady();
		    	this.list.loadData();
		  	}
		  
//		  	 return;
//	       	 if(ID && ID!=null ){
//	       	 
//	       	 	var result = util.rmi.miniJsonRequestSync({
//							 	serviceId : "chis.qualityControlService",//doInitQualityModel
//								serviceAction : "initQualityModel",//getQualityModel  loadHtmlData
//								method : "execute",
//								body : {
//									"ID" : ID
//								}
//							});
//					if (result.code > 300) {
//						this.processReturnMsg(result.code, result.msg);
//						return null;
//					} else {
//						var bodyBack = result.json.body;
//					}
//	       	 }
	       },
	       onFormSave : function(value) {
	        if(value && value["XMBS"] && value["XMBS"]!=""){
	            this.list.xmxhId= value["XMBS"];
		    	this.list.onReady();
		    	this.list.loadData();
	        }
	       },onFormModify:function(){
	           this.list.xmxhId="";
		    	this.list.onReady();
		    	this.list.loadData();
	       },
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
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
		})