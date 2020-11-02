$package("chis.application.config.script")

$import("chis.script.BizSimpleListView", "util.rmi.jsonRequest",
		"app.modules.list.SimpleListView")

chis.application.config.script.JgListView = function(cfg) {
	chis.application.config.script.JgListView.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(chis.application.config.script.JgListView,
		chis.script.BizSimpleListView, {
			doTbjg : function(item, e) {
				var me = this;
				util.rmi.miniJsonRequestAsync({
							serviceId : 'chis.relevanceManageService',
							method :'execute',
							serviceAction:'updateJgzd',
							body : {}
						}, function(code, msg, json) {
							Ext.Msg.alert('提示', '机构数据已经同步到数据库中');
							me.store.reload();
						}, this);

			}
		})