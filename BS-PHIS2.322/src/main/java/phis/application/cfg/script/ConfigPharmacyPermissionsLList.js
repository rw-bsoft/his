$package("phis.application.cfg.script")

$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigPharmacyPermissionsLList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.gridDDGroup = "firstGridDDGroup";
	this.permissionType = "1"
	this.ksdm = null;
	phis.application.cfg.script.ConfigPharmacyPermissionsLList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.cfg.script.ConfigPharmacyPermissionsLList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				delete cfg.tbar;
				cfg.tbar = [];
				cfg.tbar.push('->', this.getTopCombo({
									key : 1,
									text : "药房业务"
								}));
			},
			onSelect : function(params, record) {
				var key = record.data.key
				this.ksdm = key;
				this.requestData.cnd = ['and',
						['eq', ['$', 'a.YWLB'], ['s', this.permissionType]],
						['eq', ['$', 'a.KSDM'], ['d', key]]];
				this.refresh();
				// this.fireEvent("pharmarcyChange",key);
			},
			getTopCombo : function(permissionType) {
				var dicId = "";
				var filter = "";
				var labelText = new Ext.form.Label({
							text : permissionType.text
						});
				if (permissionType.key == 1) {
					dicId = "phis.dictionary.pharmacyYwfl";
				}
				if (permissionType.key == 2) {
					dicId = "phis.dictionary.department_reg";
//					filter = "['eq',['$','JGID'],['s','"
//							+ this.mainApp['phisApp'].deptId + "']]"
				}

				if (permissionType.key == 3) {
					dicId = "phis.dictionary.clinicType";
//					filter = "['eq',['$',['s','JGID']],['s','"
//							+ this.mainApp['phisApp'].deptId + "']]"
				}

				if (permissionType.key == 4) {
					dicId = "phis.dictionary.department_bq";
//					filter = "['eq',['$',['s','JGID']],['s','"
//							+ this.mainApp['phisApp'].deptId + "']]"
				}

				if (permissionType.key == 5) {
					dicId = "phis.dictionary.storehouse";
//					filter = "['eq',['$',['s','JGID']],['s','"
//							+ this.mainApp['phisApp'].deptId + "']]"
				}
				if (permissionType.key == 6) {
					dicId = "phis.dictionary.treasury";
				}
				
				if (permissionType.key == 9) {
					dicId = "phis.dictionary.department_yj";
//					filter = "['eq',['$','item.properties.JGID']],['s','"
//							+ this.mainApp['phisApp'].deptId + "']]"
				}

				var dic = {
					id : dicId,
					// searchField : "PYDM",
					autoLoad : true,
					defaultValue : "",
					filter : filter
				}
				var partmentCombo = util.dictionary.SimpleDicFactory
						.createDic(dic);
				this.partmentCombo = partmentCombo;
				var partmentStore = partmentCombo.store;
				var _ctx = this;
				var firstLoad = true;
				partmentStore.on("load", function() { // 对 ComboBox 的数据源加上
							if (firstLoad) {
								if (this.getCount() > 0) {
									partmentCombo.setValue(this.getAt(0)
											.get('key'));
									_ctx.onSelect(null, this.getAt(0))
								}
								firstLoad = false;
							}
						});
				partmentCombo.on("select", this.onSelect, this)
				partmentCombo.on("beforeselect", this.opener.onBeforeBusSelect,
						this.opener)
				return ["->", labelText, '-', partmentCombo];
			}
		});