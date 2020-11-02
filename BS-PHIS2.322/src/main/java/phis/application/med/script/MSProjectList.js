$package("phis.application.med.script");

$import("phis.script.SimpleList");

		phis.application.med.script.MSProjectList = function(cfg) {
			cfg.disablePagingTbr = true;
			cfg.height = 220;
			cfg.autoLoadData = false;
			phis.application.med.script.MSProjectList.superclass.constructor
					.apply(this, [ cfg ]);

		},
		Ext
				.extend(
						phis.application.med.script.MSProjectList,
						phis.script.SimpleList,
						{
							refreshData : function() {
								this.clear(); // ** add by yzh , 2010-06-09 **
								if (this.store) {
									if (this.disablePagingTbr) {
										this.store.load()
									} else {
										var pt = this.grid.getBottomToolbar()
										if (this.requestData.pageNo == 1) {
											pt.cursor = 0;
										}
										pt.doLoad(pt.cursor)
									}
								}
								// ** add by yzh **
								this.resetButtons();
							},
							reLoadData : function(yjxh) {
								if (yjxh) {
									this.requestData.cnd = [
											'and',
											[ 'eq', [ '$', 'a.YJXH' ],
													[ 'd', yjxh ] ],
											[
													'eq',
													[ '$', 'a.JGID' ],
													[
															's',
															this.mainApp['phisApp'].deptId ] ], ];
									this.refreshData();
								} else {
									this.clear();
								}

							}
						});