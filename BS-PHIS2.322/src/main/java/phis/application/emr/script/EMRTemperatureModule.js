$package("phis.application.emr.script")

$import("phis.script.SimpleModule")

phis.application.emr.script.EMRTemperatureModule = function(cfg) {
	phis.application.emr.script.EMRTemperatureModule.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.application.emr.script.EMRTemperatureModule,
		phis.script.SimpleModule, {
			maxWeek:0,
			currentWeek:null,
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.info未定义）
				if(this.info || this.exContext.brxx){
					this.doFirst=this.doFirst.createSequence(this.loadCharData,this);
					this.doPre=this.doPre.createSequence(this.loadCharData,this);
					this.doNext=this.doNext.createSequence(this.loadCharData,this);
					this.doLast=this.doLast.createSequence(this.loadCharData,this);
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getLModule()
									}, {
										layout : "fit",
										border : false,
										width : 540,
										split : true,
										collapsible : true,
										titleCollapse : true,
										region : 'east',
										items : this.getRModule()
									}],
									tbar : (this.tbar || []).concat(this.createMyButtons())//zhaojian 2017-10-11 医生站打开病人体温单，操作按钮默认为灰色问题
						});
				this.panel = panel;
				return panel;
			},
			getLModule : function() {
				var module = this.createModule("refCharModule", this.refCharModule);
				this.charModule = module;
				//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.info未定义）
				if(this.info==undefined){module.params={zyh: this.exContext.brxx.data.ZYH,"currentWeek":this.currentWeek};}
				else{module.params={zyh: this.info.ZYH,"currentWeek":this.currentWeek};}
				//module.params={zyh: this.info.ZYH,"currentWeek":this.currentWeek};
				var m = module.initPanel()
				return m;
			},
			getRModule : function() {
				var module = this.createModule("logRModule", this.refTableModule);
				if(this.readOnly){
				module.readOnly=true;
				}
				module.opener=this;
				module.freshChar=this.charModule;
				this.rModule = module;
				return module.initPanel();
			},
			doFirst:function(){
				this.currentWeek=0;
			},
			doPre:function(){
				if(this.currentWeek==0){
					MyMessageTip.msg("提示", "已经是首周!", true);
					return false;
				}
				this.currentWeek-=1;
			},
			doNext:function(){
				if(this.currentWeek==this.maxWeek){
					MyMessageTip.msg("提示", "已经是末周!", true);
					return false;
				}
				this.currentWeek+=1;
			},
			doLast:function(){
				this.currentWeek=this.maxWeek;
			},
			doPrint : function(){
				var LODOP=getLodop();
				var url = ClassLoader.appRootOffsetPath + 'temperature.jsp';
				LODOP.PRINT_INIT("打印控件");
//				LODOP.SET_PRINT_PAGESIZE("1","2000","2800","");
				//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.info未定义）
				if(this.info==undefined){LODOP.ADD_PRINT_HTM (0,0, "100%","100%","URL:"+url+"?zyh="+
						                       this.exContext.brxx.data.ZYH+"&currentWeek="+
						                       this.currentWeek+"&tempType="+this.tempType);}
				else{LODOP.ADD_PRINT_HTM (0,0, "100%","100%","URL:"+url+"?zyh="+
						                       this.info.ZYH+"&currentWeek="+
						                       this.currentWeek+"&tempType="+this.tempType);}
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Page");
				LODOP.PREVIEW();
			},
			doRefresh : function(){
				if(this.currentWeek == null){
					this.currentWeek=this.maxWeek;
				}
				var tempType="2";
				if(!Ext.isEmpty(this.rModule.midiModules["SMTZForm"])){
					var body=this.rModule.midiModules["SMTZForm"].form.getForm().getValues();
					if(body["XMXB"]){
						if(body["XMXB"]=="腋温"){
							tempType="2";
						}else if(body["XMXB"]=="口温"){
							tempType="1";
						}else if(body["XMXB"]=="肛温"){
							tempType="3";
						}
					}
				}
				this.tempType=tempType;
				//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.info未定义）
				if(this.info==undefined){this.charModule.params={zyh: this.exContext.brxx.data.ZYH,"currentWeek":this.currentWeek,"tempType":this.tempType};}
				else{this.charModule.params={zyh: this.info.ZYH,"currentWeek":this.currentWeek,"tempType":this.tempType};}
				//this.charModule.params={zyh: this.info.ZYH,"currentWeek":this.currentWeek,"tempType":this.tempType};
				this.charModule.onReady();
			},
			loadCharData:function(){
				var tempType="2";
				if(!Ext.isEmpty(this.rModule.midiModules["SMTZForm"])){
					this.rModule.midiModules["SMTZForm"].doNew();
					this.rModule.tab.setActiveTab(0);
					var body=this.rModule.midiModules["SMTZForm"].form.getForm().getValues();
					if(body["XMXB"]){
						if(body["XMXB"]=="腋温"){
							tempType="2";
						}else if(body["XMXB"]=="口温"){
							tempType="1";
						}else if(body["XMXB"]=="肛温"){
							tempType="3";
						}
					}
				}
				this.tempType=tempType;
				//zhaojian 2017-10-09 医生站打开病人体温单，无法获取到ZHY问题（this.info未定义）
				if(this.info==undefined){this.charModule.params={zyh: this.exContext.brxx.data.ZYH,"currentWeek":this.currentWeek,"tempType":this.tempType};}
				else{this.charModule.params={zyh: this.info.ZYH,"currentWeek":this.currentWeek,"tempType":this.tempType};}
				//this.charModule.params={zyh: this.info.ZYH,"currentWeek":this.currentWeek,"tempType":this.tempType};
				this.charModule.onReady();
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								maximized : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.loadCharData();
								this.rModule.info=this.info;
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.rModule.midiModules["SMTZForm"].form.getForm().findField("FC").setValue(false);
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			},
	//zhaojian 2017-10-11 医生站打开病人体温单，操作按钮默认为灰色问题
	createMyButtons : function() {
		var actions = this.actions
		var buttons = []
		if (!actions) {
			return buttons
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			if (action.hide) {
				continue
			}
			var btn = {
				accessKey : f1 + i,
				text : action.name + "(F" + (i + 1) + ")",
				ref : action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),
				scale : action.scale || "small",
				// ** add by yzh **
				notReadOnly : action.notReadOnly,

				script : action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		return buttons
	}
})