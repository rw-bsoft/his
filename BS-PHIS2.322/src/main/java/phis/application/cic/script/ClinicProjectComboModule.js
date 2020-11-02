

$package("phis.application.cic.script")

/**
 * 项目组套维护module shiwy 2012.05.25
 */
$import("phis.script.SimpleModule")

phis.application.cic.script.ClinicProjectComboModule = function(cfg) {
	this.sslbsign = 1;
	phis.application.cic.script.ClinicProjectComboModule.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicProjectComboModule,
		phis.script.SimpleModule, {
			initPanel : function() {// 加载panel
				var actions = this.actions;
				var bar = [];
				this.ZT_CY_FLAG = "1";
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					bar.push({
						xtype : "radio",
						checked : i == 0,
						boxLabel : ac.name,
						inputValue : ac.properties.value,
						name : "stack",
						listeners : {
							check : function(group, checked) {
								if (this.projectComboList && checked) {
									this.ZT_CY_FLAG = group.inputValue;
									this.projectComboList.SSLB = group.inputValue;
									this.sslbsign = parseInt(group.inputValue);
									this.projectComboList.requestData.cnd = [
											'and',
											['eq', ['$', 'ZTLB'], ['i', 4]],
											['eq', ['$', 'SSLB'],['i', this.sslbsign]],
											['isNull', ['$', 'KSDM']]];
									this.projectComboList.initCnd = [
											'and',
											['eq', ['$', 'ZTLB'], ['i', 4]],
											['eq', ['$', 'SSLB'],['i', this.sslbsign]],
											['isNull', ['$', 'KSDM']]];
									if (group.inputValue == "4") {
										var data = {
											"ZTMC" : "个人常用",
											"SSLB" : 4,
											"ZTLB" : 4,
											"PYDM" : "GRCY",
											"GLJB" : 0
										};
										var r = phis.script.rmi
												.miniJsonRequestSync({
													serviceId : this.serviceId,
													serviceAction : this.serviceAction,
													body : data
												});
									}
									// 设置分页信息
									var pagingToolbar = this.projectComboList.pagingToolbar;
									pagingToolbar.changePage(1);
									// 保存完后刷新
									this.projectComboList.loadData();
									// 刷新完后按钮变灰
									if (group.inputValue == "4") {
										this.projectComboList.setButtonsState([
														'add', 'update',
														'remove'], false);
									} else {
										this.projectComboList.setButtonsState([
														'add', 'update',
														'remove'], true);
									}
								}
							},
							scope : this
						}
					})
				}
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : [bar],
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										// title : '组套名称',
										height : 200,
										width : 470,
										items : this.getProjectComboList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										// title : '组套明细',
										region : 'center',
										height : 200,
										width : 600,
										items : this
												.getProjectComboDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getProjectComboList : function() {
				this.projectComboList = this.createModule(
						"getProjectComboList", this.refProjectComboList);
				this.projectComGrid = this.projectComboList.initPanel();
				this.projectComboList.on("loadData", this.onListLoadData, this);
				this.projectComGrid.on("loadData", this.onListLoadData, this);
				this.projectComGrid.on("rowClick", this.onListRowClick, this);
				return this.projectComGrid;
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.projectComGrid.fireEvent("rowclick",
								this.projectComGrid, 0);
					}
				} else {
					this.projectComboDetailList.ZTBH = "";
				}
			},
			onListRowClick : function(projectComGrid, rowIndex, e) {
				var r = projectComGrid.store.getAt(rowIndex);
				if (!r)
					return;
				this.projectComboDetailList.ZTBH = r.id;
				this.projectComboDetailList.requestData.cnd = ['eq',
						['$', 'ZTBH'], ['d', r.id]];
				this.projectComboDetailList.loadData();
				if (this.sslbsign == "4") {
					var btns = this.projectComboDetailGrid.getTopToolbar();
					var btn = btns.find("cmd", "insert");
					btn[0].hide();
					this.projectComboDetailList.checkBox.hide();
					this.projectComboDetailList.checkBox.setValue(1);
				} else {
					var btns = this.projectComboDetailGrid.getTopToolbar();
					var btn = btns.find("cmd", "insert");
					btn[0].show();
					this.projectComboDetailList.checkBox.show()
					this.projectComboDetailList.checkBox.setValue(0);
				}
			},
			getProjectComboDetailList : function() {
				this.projectComboDetailList = this.createModule(
						"getProjectComboDetailList",
						this.refProjectComboDetailList);
				this.projectComboDetailList.opener = this;
				this.projectComboDetailList.on("afterRemove", this.afterRemove,
						this);
				this.projectComboDetailGrid = this.projectComboDetailList
						.initPanel();
				return this.projectComboDetailGrid;
			},
			afterRemove : function() {
				if (this.projectComboList) {
					// 在项目组套明细中删除明细后，项目组套左边列表不进行刷新！以防刷新后会重新定位到首个项目组套
					// this.projectComboList.loadData();
					if (this.sslbsign == "4") {
						this.projectComboList.setButtonsState(['create',
										'update', 'remove'], false);
					}
				}
			}
		})
