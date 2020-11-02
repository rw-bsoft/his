$package("chis.application.scm.sign.script.FamilyMemberList");

$import("chis.script.BizSimpleListView");
chis.application.scm.sign.script.FamilyMemberList = function(cfg){
	cfg.initCnd = [
						'and',
						['eq',['$', 'b.status'],['s','0']],
						['eq', ['$', 'b.familyId'],['s', cfg.familyId]]
						];
					
	chis.application.scm.sign.script.FamilyMemberList.superclass.constructor.apply(this,[cfg]);
};

Ext.extend(chis.application.scm.sign.script.FamilyMemberList,chis.script.BizSimpleListView,{
	
	onDblClick : function(grid, index, e) {
				if (this.opened){
					this.doConfirmSelect()
				}	
	},
	doConfirmSelect : function() {
		var r = this.getSelectedRecord();
		if (!r) {
			return;
		}
		this.fireEvent("select", r, this);
		var win = this.getWin();
		if (win) {
			win.hide();
		}
	}
});