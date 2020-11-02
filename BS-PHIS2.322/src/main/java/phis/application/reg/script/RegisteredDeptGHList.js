$package("phis.application.reg.script");


$import("phis.script.SimpleList","phis.script.widgets.MyMessageTip");

phis.application.reg.script.RegisteredDeptGHList = function(cfg) {
	//cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.closeAction = "hide";
	phis.application.reg.script.RegisteredDeptGHList.superclass.constructor.apply(
			this, [cfg]);
},
Ext.extend(phis.application.reg.script.RegisteredDeptGHList,
		phis.script.SimpleList, {
			loadData : function() {
				this.clear();
				this.requestData.serviceId = "phis."+this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = this.opener.getInputValue();
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load({
							callback: function(records, options, success){
								if(records.length <= 1){
									this.doCommit();   
								}else{
									this.win.show();
								}
							},
							scope: this
						});
					} else {
						var pt = this.grid.getBottomToolbar();
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor);
					}
				}
				
				this.resetButtons();
			},
			onDblClick : function(grid, index, e) {
				this.doCommit();
			},
			onEnterKey : function() {
				this.doCommit();
			},
			
			doCommit : function() {
				var rs = this.grid.getSelectionModel().getSelected();
				if(!rs){
					this.opener.setGHDJ();
				}else{
					this.opener.setGHDJ(rs.data);
				}
				this.win.hide();
			},
			doCancel : function() {
				if (this.win) {
					this.win.hide();
				}
			},
			
			refreshData : function(){
				this.requestData.body = this.opener.getInputValue();
				this.refresh();
			}
});