
$package("phis.application.cfg.script")

$import("phis.script.EditorList")

phis.application.cfg.script.ConfigProjectsAliasEditorList = function(cfg) {
	cfg.autoLoadData = false;
	this.entryName=cfg.properties.entryName;
	phis.application.cfg.script.ConfigProjectsAliasEditorList.superclass.constructor.apply(this,
			[cfg])
			//alert(this.cnds);
}
Ext.extend(phis.application.cfg.script.ConfigProjectsAliasEditorList,
		phis.script.EditorList, {
			
			onReady : function() {
				phis.application.cfg.script.ConfigProjectsAliasEditorList.superclass.onReady
						.call(this);
				this.on("afterCellEdit", this.onAfterCellEdit, this);
			},
			onAfterCellEdit:function(it,record,field,v){
			var store = this.grid.getStore();
			var n = store.getCount();
			if(v){
			for(var i = 0;i < n-1; i ++){
				var r = store.getAt(i);
				if(r.data.FYMC==v){
					record.set("FYMC","");
					MyMessageTip.msg("提示", "费用别名不能重复", true);
					return;
				}
			}
			}
			}
		});
