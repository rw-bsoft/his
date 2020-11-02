$package("phis.application.yb.script")
$import("phis.script.SimpleList","phis.application.mds.script.MySimpleListCommon")

phis.application.yb.script.fydzLeftList = function(cfg) {
	Ext.apply(this,phis.application.mds.script.MySimpleListCommon);
	phis.application.yb.script.fydzLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.yb.script.fydzLeftList,
		phis.script.SimpleList, {
			onDblClick:function(grid,index,e){
			var r=this.getSelectedRecord();
			if(!r||r.get("YBFYBM")==null||r.get("YBFYBM")==""){
			return;
			}
			this.fireEvent("dblClick",r.get("YBFYBM"));
			}
		})