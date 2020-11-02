$package("phis.application.cfg.script")

$import("phis.script.SimpleModule")

phis.application.cfg.script.CaseHistoryControlModule = function(cfg) {
	phis.application.cfg.script.CaseHistoryControlModule.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.cfg.script.CaseHistoryControlModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
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
										region : 'west',
										width : '30%',
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getRList()
									}]
						});
				this.panel = panel;
				panel.on("beforeclose", this.onPanelBeforeclose, this)
				return panel;
			},
			onPanelBeforeclose:function(){
				var flag=this.rlist.getHasChange();
				if(flag){
					Ext.Msg.show({
								title : '提示',
								msg : '当前页面已被修改，是否保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									this.rlist.setHasChange(false)
									if (btn == "ok") {
										this.rlist.saveContory();
										this.panel.destroy();
									}else{
										this.panel.destroy();
									}
								},
								scope : this
							})
				}else{
					this.panel.destroy();
				}
				return false;
			},
			getLList : function() {
				var module = this.createModule("controlLlist", this.refLList);
				this.llist = module;
				var lGrid = module.initPanel();
				this.lGrid = lGrid;
				lGrid.on("rowclick", this.onRowBeforeClick, this);
				module.on("loadData", this.onLoadData, this);
				module.on("save", this.onLListSave, this);
				module.on("remove", this.onLListRemove, this);
				return lGrid;
			},
			getRList : function() {
				var module = this.createModule("controlRlist", this.refRList);
				this.rlist = module;
				return module.initPanel();
			},
			onRowBeforeClick:function(){
				var flag=this.rlist.getHasChange();
				if(flag){
					Ext.Msg.show({
								title : '提示',
								msg : '当前页面已被修改，是否保存?',
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									this.rlist.setHasChange(false)
									if (btn == "ok") {
										this.rlist.saveContory();
										this.onRowClick();
									}else{
										this.onRowClick();
									}
								},
								scope : this
							})
				}else{
					this.onRowClick();
				}
			},
			onRowClick : function() {
				var r = this.lGrid.getSelectionModel().getSelected();
				if (!r) {
					return
				}
				var jsxh = r.get("JSXH");
				if(this.JSXH==jsxh){
					return;
				}
				this.JSXH = jsxh;
				this.midiModules["controlRlist"].requestData.serviceId = "phis.caseHistoryControlService";
				this.midiModules["controlRlist"].requestData.serviceAction = "saveAndListRolesControy";
				this.midiModules["controlRlist"].requestData.cnd = ['eq',
						['$', 'JSXH'], ['i', jsxh]];
				this.midiModules["controlRlist"].loadData();
				this.midiModules["controlRlist"].doNewCheckBox();
			},
			onLListRemove : function() {
				this.rlist.store.removeAll();
			},
			onLoadData : function(store) {
				var sm = this.lGrid.getSelectionModel();
				var rowid = -1;
				var jsxh=parseInt(this.JSXH);
				this.JSXH=jsxh.toString();
				if (this.JSXH && this.JSXH != "") {
					rowid = store.find('JSXH', this.JSXH);
				}
				if (rowid != -1) {
					sm.selectRow(rowid);
				} else {
					sm.selectFirstRow();
				}
				this.lGrid.fireEvent("rowclick", this.grid);
			},
			onLListSave : function(entryName, op, json, rec) {
				this.JSXH = rec.JSXH;
				this.fireEvent("save", entryName, op, json, rec);
				this.llist.refresh()
			}
		});