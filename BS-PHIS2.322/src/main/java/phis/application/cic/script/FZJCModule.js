$package("phis.application.cic.script")

$import("phis.script.SimpleModule")

phis.application.cic.script.FZJCModule = function(cfg) {
	phis.application.cic.script.FZJCModule.superclass.constructor
			.apply(this, [cfg])
			this.height = 400;
}

Ext.extend(phis.application.cic.script.FZJCModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
//							border : false,
							width : 600,
							height : 400,
							buttonAlign : 'center',
//							frame : true,
							layout : 'border',
//							defaults : {
//								border : false
//							},
							tbar : this.createTabButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : '33%',
										items : this.getList1()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getList2()
									},{
										xtype:'textarea',
										id:'cqdtext',
										region : 'south',
										readOnly:true
									}]
						});

				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
//				this.list1.loadData();
//				this.list2.loadData();
			},
			createTabButtons : function() {
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
						disabled : false,
						notReadOnly : action.notReadOnly,

						script : action.script,
						handler : this.doAction,
						scope : this
					}
					buttons.push(btn)
				}
				return buttons

			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doCancel:function(){
				this.getWin().hide();
			},
			getList1 : function() {
				var module = this.createModule("refList1",
						this.refList1);
				module.opener = this;
//				module.brid=this.brid;
				module.on("rowSelect",this.onRowSelect,this);
				this.list = module;
				module.opener = this;
				return module.initPanel();
			},
			onRowSelect:function(SAMPLENO){
				if(SAMPLENO){
					this.list2.requestData.serviceId = "phis.userDataBoxService";
					this.list2.requestData.serviceAction = "GetFzjcListDetails";
					this.list2.requestData.SAMPLENO = SAMPLENO;
					this.list2.loadData();
				}else{
					this.list2.clear();
				}
				recordIds = new Array();
				this.onRowDetailsSelect("");
			},
			getList2 : function() {
				var module = this.createModule("refList2",
						this.refList2);
				module.opener = this;
				module.on("rowSelect",this.onRowDetailsSelect,this);
				var list2 = module.initPanel();
				this.list2 = module;
				return list2;
			},
			onRowDetailsSelect : function(JYYRResult){
				this.JYYRResult = JYYRResult;
				Ext.getCmp('cqdtext').setValue(JYYRResult);
			},
			doAppoint : function() {
				var XMMC=this.JYYRResult;
				if(XMMC){
					this.fireEvent("appoint", XMMC,2);
				}
				this.getWin().hide();
			},
		});