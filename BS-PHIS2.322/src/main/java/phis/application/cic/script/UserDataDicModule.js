$package("phis.application.cic.script")
$import("phis.script.SimpleModule")
phis.application.cic.script.UserDataDicModule = function(cfg) {
	phis.application.cic.script.UserDataDicModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.cic.script.UserDataDicModule, phis.script.SimpleModule, {
			initPanel : function() {
				var buttons = this.createTabButtons();
				var dicList=this.createDicList();
				var itemList=this.createItemList();
				var panel = new Ext.Panel({
							border : false,
							//frame : true,
							layout : 'border',
							tbar : buttons,
							items : [{
										layout : "fit",
										split : true,
										title : '',
										region : 'west',
										width : '35%',
										items : dicList
									}, {
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										items : itemList
									}]
						});
				this.panel = panel;
				return panel
			},
			createDicList:function(){
				var module = this.createModule("dicList", this.dicList);
				this.dList = module;
				module.requestData.serviceId="phis.userDataBoxService";
				module.requestData.serviceAction="listUserNormalDic";
				module.on("rowClick",this.onDicRowClick,this);
				var dGrid = module.initPanel();
				return dGrid;
			},
			createItemList:function(){
				var module = this.createModule("itemList", this.itemList);
				this.iList = module;
				module.on("itemDbClick",this.onItemDbClick,this);
				var iGrid = module.initPanel();
				return iGrid;
			},
			onItemDbClick:function(){
				this.doAppoint();
			},
			doAppoint : function() {
				var r=this.iList.getSelectedRecord();
				if (r == null) {
					return
				}
				var XMMC=r.get("XMMC");
				this.fireEvent("appoint", XMMC,2);
			},
			onDicRowClick:function(ZDID){
				if(this.ZDID==ZDID){
					return;
				}
				this.ZDID=ZDID;
				this.iList.requestData.serviceId="phis.userDataBoxService";
				this.iList.requestData.serviceAction="listUserDicItem";
				this.iList.requestData.ZDID=ZDID;
				this.iList.loadData();
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
				this.fireEvent("cancel",this);
			}
		})
