$package("phis.application.cic.script")

$import("phis.script.SimpleList")

phis.application.cic.script.UserMZDataGroupList = function(cfg) {
	cfg.autoLoadData = true;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	cfg.entryName = this.entryName || "phis.application.cic.schemas.YS_MZ_JZLS_EMR";
	this.serverParams = {
		serviceAction : cfg.serviceAction
	}
//	cfg.group = "RYRQ";
	phis.application.cic.script.UserMZDataGroupList.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.cic.script.UserMZDataGroupList, phis.script.SimpleList, {	
			initPanel : function(sc) {
				// 判断是否是EMRView内部打开
				var grid = phis.application.cic.script.UserMZDataGroupList.superclass.initPanel
						.call(this, sc);
				if (this.exContext != null && this.exContext.ids != null) {
					this.initCnd = ['and',
							        ['eq', ['$', 'a.BRBH'],['d', this.exContext.ids.brid]],
									['eq', ['$', 'e.YDLBBM'],['i', 17]],
									['ne', ['$', 'd.BLZT'],['i', 9]]
							];
					this.requestData.cnd = ['and',
					        ['eq', ['$', 'a.BRBH'],['d', this.exContext.ids.brid]],
							['eq', ['$', 'e.YDLBBM'],['i', 17]],
							['ne', ['$', 'd.BLZT'],['i', 9]]
					];
				} else {
					this.initCnd = ['eq', ['$', 'a.JGID'],
							["$", "'" + this.mainApp['phisApp'].deptId + "'"]]
					this.requestData.cnd = ['eq', ['$', 'a.JGID'],
							["$", "'" + this.mainApp['phisApp'].deptId + "'"]]
				}
				clinicPerson_ctx = this;
				return grid;
			},
			doCancel : function() {
				this.fireEvent("cancel", this);
			},
			onDblClick : function(grid, index, e) {
				var record = this.getSelectedRecord();
				if (record == null) {
					return
				}
				this.fireEvent("appoint", record.data, 3);
			},
			doOpen : function() {
				this.onDblClick();
			}
		});