$package("phis.application.sto.script")

$import("phis.script.EditorList")

phis.application.sto.script.StorehouseStoreroomInventoryCollectConfirmList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.modal = true;
	cfg.disablePagingTbr=true;
	phis.application.sto.script.StorehouseStoreroomInventoryCollectConfirmList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseStoreroomInventoryCollectConfirmList,
		phis.script.EditorList, {
			doCommit : function() {
				this.grid.stopEditing();//修复IE部分版本下实盘数量编辑后鼠标移除直接保存数量还是编辑前的值
				var count = this.store.getCount();
				var ret = new Array();
				for (var i = 0; i < count; i++) {
					var body = {};
					body["KCSB"] = this.store.getAt(i).get("KCSB");
					body["SPSL"] = this.store.getAt(i).get("SPSL");
					if(body["SPSL"]==null||body["SPSL"]==undefined||body["SPSL"]==""){
					body["SPSL"]=0;
					}
					ret.push(body);
				}
				this.fireEvent("save", ret);
				this.getWin().hide();
			},
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide"
				if (!this.mainApp || this.closeAction == true) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title||this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : false,
						maximizable : false,
						closable : false,
						shadow : false,
						modal : this.modal || false
							// add by huangpf.
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
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				var count = this.store.getCount();
				var length = this.change_kcsl.length;
				for (var i = 0; i < count; i++) {
					for (var j = 0; j < length; j++) {
					if(this.store.getAt(i).get("KCSB")==this.change_kcsl[j].KCSB){
					this.store.getAt(i).set("SPSL",this.change_kcsl[j].SPSL);
					break;
					}
					}
				}
			}
		})