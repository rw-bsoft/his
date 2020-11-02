$package("phis.application.yb.script")
$import("phis.script.SimpleList")

phis.application.yb.script.fydzRightList = function(cfg) {
	phis.application.yb.script.fydzRightList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.yb.script.fydzRightList,
		phis.script.SimpleList, {
			loadData:function(){
			if(this.ybfybm){
				this.requestData.cnd=['eq',['$','a.YBFYBM'],['s',this.ybfybm]];
				this.ybypbm=false;
			}
			if(this.FYMC){
			this.requestData.cnd=['like',['$','a.YBFYMC'],['s',this.FYMC]];
			this.FYMC=false;
			}
			phis.application.yb.script.fydzRightList.superclass.loadData.call(this)
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