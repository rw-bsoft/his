$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseInitialBooks = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	cfg.modal = true;
	cfg.isInitial=0;
	cfg.initCnd=cfg.cnds=['and',['and',['and',['eq',['$','e.YKZF'],['i',0]],['eq',['$','d.ZFPB'],['i',0]]],['eq',['$','a.ZFPB'],['i',0]]],['eq',['$','c.ZFPB'],['i',0]]];
	phis.application.sto.script.StorehouseInitialBooks.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseInitialBooks,
		phis.script.SimpleList, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].storehouseId == null
						|| this.mainApp['phis'].storehouseId == ""
						|| this.mainApp['phis'].storehouseId == undefined) {
					Ext.Msg.alert("提示", "未设置登录药库,请先设置");
					return null;
				}
				//进行是否初始化验证
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, "药库未初始化", this.initPanel);
					return null;
				}
				var grid = phis.application.sto.script.StorehouseInitialBooks.superclass.initPanel
						.call(this, sc);
				this.grid = grid;
				return grid;
			},
		onDblClick : function(grid, index, e) {
			if(this.isInitial==0){
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryInitialServiceAction
						});
				if (ret.code ==200) {
					this.isInitial=1;
					return;
				}
				this.isInitial=2;
			}else if(this.isInitial==1){
			return;
			}
			
			var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				var initDataBody = {};
				initDataBody["YPXH"] = r.data.YPXH;
				initDataBody["JGID"] = r.data.JGID;
				initDataBody["YPCD"] = r.data.YPCD;
				this.module = this.createModule("module",
						this.refMoudel);
				this.module.on("save", this.onSave, this);
				this.module.on("winClose", this.onClose, this);
				var win = this.getWin();
				win.add(this.module.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.module.load(initDataBody);
				}
		},
		onClose:function(){
		this.getWin().hide();
		}
//		,
//		doCndQuery : function(button, e, addNavCnd) {
//		
//		},
//		loadData:function(){
//		this.requestData.serviceId = this.serviceId
//		this.requestData.serviceAction = this.listActionid;
//		phis.application.sto.script.StorehouseInitialBooks.superclass.loadData.call(this);
//		}
		})