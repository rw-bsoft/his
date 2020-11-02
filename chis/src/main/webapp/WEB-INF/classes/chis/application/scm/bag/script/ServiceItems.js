$package("chis.application.scm.bag.script")
$import("chis.script.BizSelectListView")
chis.application.scm.bag.script.ServiceItems = function(cfg){
	this.initCnd = ['eq', ['$', 'itemType'],
					 ['s', '4']]
	chis.application.scm.bag.script.ServiceItems.superclass.constructor.apply(this,[cfg]);
}

Ext.extend(chis.application.scm.bag.script.ServiceItems,chis.script.BizSelectListView,{

});