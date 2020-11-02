$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigDepartmentEJKFlist = function(cfg) {
	this.height = "250";
	this.closeAction = true;
	this.disablePagingTbr = cfg.disablePagingTbr = true;
	cfg.cnds = [
			'and',
			[
					'and',
					[ 'eq', [ '$', 'a.JGID' ], [ '$', '%user.manageUnit.id' ] ],
					[ 'eq', [ '$', 'a.KFZT' ], [ '$', '1' ] ] ],
			[ 'ge', [ '$', 'EJKF' ], [ 'i', 0 ] ] ];
	cfg.initCnd = [
			'and',
			[
					'and',
					[ 'eq', [ '$', 'a.JGID' ], [ '$', '%user.manageUnit.id' ] ],
					[ 'eq', [ '$', 'a.KFZT' ], [ '$', '1' ] ] ],
			[ 'ge', [ '$', 'EJKF' ], [ 'i', 0 ] ] ];
	phis.application.cfg.script.ConfigDepartmentEJKFlist.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext.extend(phis.application.cfg.script.ConfigDepartmentEJKFlist,
		phis.script.SimpleList, {

		});