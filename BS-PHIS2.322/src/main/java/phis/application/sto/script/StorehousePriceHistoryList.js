/**
 * 调价历史查询
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleList");

phis.application.sto.script.StorehousePriceHistoryList = function(cfg) {
	cfg.autoLoadData = true;//自动加载
	this.serverParams = {serviceAction:cfg.listActionId};
	this.serverParams.serviceId=cfg.serviceId;
	phis.application.sto.script.StorehousePriceHistoryList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.sto.script.StorehousePriceHistoryList,
		phis.script.SimpleList, {
	/**
	 * 双击按钮 显示药品的调价明细
	 * @param {} grid
	 * @param {} index
	 * @param {} e
	 */
	onDblClick : function(grid, index, e) {
		this.doMx();
	},
	loadData : function() {
		this.clear(); 
		if (this.store) {
			if (this.disablePagingTbr) {
				this.store.load({
					callback: function(records, options, success){
						if(this.store.getCount() > 0){
							this.onDblClick();
						}
					},
					scope: this//作用域为this。必须加上否则this.initSelect(); 无法调用
				});
			} else {
				var pt = this.grid.getBottomToolbar()
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		}
		this.resetButtons();
	},
	// 用户扩展配置
	expansion : function(cfg) {
		this.pydm = new Ext.form.TextField({
			 id:"pydm",
			 width:160
		});
		var tbar = cfg.tbar;
		delete cfg.tbar;
		cfg.tbar = [];
		cfg.tbar.push(["拼音码",this.pydm, "-", tbar]);
	},
	/**
	 * 查询按钮
	 */
	doQuery : function(){
		var body = {};
		body.PYDM = this.pydm.getValue().toUpperCase();
		this.requestData.body = body;
		this.requestData.pageNo = 1;
		this.refresh();
	},
	//查看明细
	doMx:function(){
		//获取当前选中行
		var record=this.getSelectedRecord().data;
		if(!this.phDetails){
			var details = this.createModule("priceHistoryDetails", this.refPriceHistoryDetails);
			this.phDetails = details;
			this.phDetails.requestData.body = record;
			var detailsWin = details.getWin();
			detailsWin.add(details.initPanel());
			detailsWin.setSize(800,600);
			detailsWin.show();
		}else{
			this.phDetails.requestData.body = record;
			this.phDetails.refresh();
			var detailsWin = this.phDetails.getWin();
			detailsWin.setSize(800,600);
			detailsWin.show();
		}
	}
});