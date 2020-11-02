$package("chis.application.tr.script.highRisk")

$import("chis.script.BizSelectListView")

chis.application.tr.script.highRisk.TumourHighRiskVisitCheckResultList = function(cfg){
	cfg.initCnd = ['and',['eq',['$','a.empiId'],['s',cfg.exContext.args.empiId||'']],['eq',['$','a.planId'],['s',cfg.exContext.args.planId||'']]];
	cfg.createCls = "chis.application.tr.script.screening.TumourScreeningCheckResultForm";
	cfg.updateCls = "chis.application.tr.script.screening.TumourScreeningCheckResultForm";
	cfg.actions = [ {
									id : "select",
									name : "选择",
									iconCls : "common_select"
								}, {
									id : "create",
									name : "新增",
									iconCls:"create"
								}, {
									id : "update",
									name : "修改",
									iconCls:"create"
								},{
									id:"remove",
									name:"删除"
								},{
									id : "cancel",
									name : "取消",
									iconCls : "common_cancel"
								}];
	chis.application.tr.script.highRisk.TumourHighRiskVisitCheckResultList.superclass.constructor.apply(this, [cfg]);
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(chis.application.tr.script.highRisk.TumourHighRiskVisitCheckResultList,chis.script.BizSelectListView,{
	loadData : function(){
		this.requestData.cnd = ['and',['eq',['$','a.empiId'],['s',this.exContext.args.empiId||'']],['eq',['$','a.planId'],['s',this.exContext.args.planId||'']]];
		chis.application.tr.script.highRisk.TumourHighRiskVisitCheckResultList.superclass.loadData.call(this);
	},
//	onDblClick:function(grid,index,e){
//		this.doConfirmSelect()
//	},
	doSelect : function(){
		this.doConfirmSelect();
	},
	doCancel : function(){
		this.win.hide();
	},
	getWin : function() {
		var win = this.win
		var closeAction = "close"
		if (!this.mainApp || this.closeAction) {
			closeAction = "hide"
		}
		if (!win) {
			win = new Ext.Window({
				title : this.title || this.name,
				width : this.width,
				height:this.height,
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
				modal : this.modal || false
				})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() {
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		win.instance = this;
		return win;
	},
	loadModule:function(cls,entryName,item,r){
		if(this.loading){
			return
		}
		var cmd = item.cmd
		var cfg = {}
		cfg._mId = this.grid._mId  //增加module的id
		cfg.title = (this.title||this.name) + '-' + item.text
		cfg.entryName = entryName
		cfg.op = cmd
		cfg.butRule = this.getButRule()
		cfg.exContext = {}
		Ext.apply(cfg.exContext,this.exContext)		
		
		if(cmd  != 'create'){
			if(this.isCompositeKey){
				var keys = this.schema.keys;
				var initDataBody = {};
				for(var i=0;i<keys.length;i++){
					initDataBody[keys[i]] = r.get(keys[i])
				}
				cfg.initDataBody = initDataBody;
			}else{
				cfg.initDataId = r.id;
			}
			cfg.exContext[entryName] = r;
		}
		if(this.saveServiceId){
			cfg.saveServiceId = this.saveServiceId;
		}
		if(this.loadServiceId){
			cfg.loadServiceId = this.loadServiceId;
		}
		var m =  this.midiModules[cmd]
		if(!m){
			this.loading = true
			$require(cls,[function(){
				this.loading = false
				cfg.autoLoadData = false;
				cfg.mainApp = this.mainApp// initPanel中用到mainApp可能未设置
				var module = eval("new " + cls + "(cfg)")
				module.on("save",this.onSave,this)
				module.on("close",this.active,this)
				module.opener = this
				module.setMainApp(this.mainApp)
				this.midiModules[cmd] = module
				this.fireEvent("loadModule",module)
				this.openModule(cmd, r, [300, 330])
			},this])
		}
		else{
			Ext.apply(m,cfg)
			this.openModule(cmd,r)
		}
	},
	onWinShow : function(){
		
	}
});