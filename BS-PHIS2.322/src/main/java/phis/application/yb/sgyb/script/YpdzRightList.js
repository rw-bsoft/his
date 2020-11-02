$package("phis.application.yb.script")
$import("phis.script.SimpleList")

phis.application.yb.script.YpdzRightList = function(cfg) {
	phis.application.yb.script.YpdzRightList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.yb.script.YpdzRightList,
		phis.script.SimpleList, {
			loadData:function(){
			if(this.ybypbm){
				this.requestData.cnd=['eq',['$','a.YBYPBM'],['s',this.ybypbm]];
				this.ybypbm=false;
			}
			if(this.YPMC){
			this.requestData.cnd=['like',['$','a.YBYPMC'],['s',this.YPMC]];
			this.YPMC=false;
			}
			phis.application.yb.script.YpdzRightList.superclass.loadData.call(this)
			this.requestData.cnd=null;
			},
			onDblClick:function(grid,index,e){
			var r=this.getSelectedRecord();
			if(!r){
			return;
			}
			this.fireEvent("dblClick",r.data);
			}
		})