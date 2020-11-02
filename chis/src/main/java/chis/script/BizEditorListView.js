/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("app.modules.list.EditorListView", "chis.script.BizCommon",
		"chis.script.BizListCommon")

chis.script.BizEditorListView = function(cfg) {
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.listServiceId = cfg.listServiceId || "chis.simpleQuery"
	cfg.removeServiceId = cfg.removeServiceId || "chis.simpleRemove"
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizListCommon);
	chis.script.BizEditorListView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.BizEditorListView, app.modules.list.EditorListView, {
	
	getRemoveRequest : function(r) {
		return {
			pkey : r.id
		};
	}

})