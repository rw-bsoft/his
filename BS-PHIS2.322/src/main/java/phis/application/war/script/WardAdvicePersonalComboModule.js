$package("phis.application.war.script")
$import("phis.script.SimpleModule")

phis.application.war.script.WardAdvicePersonalComboModule = function(cfg) {
	cfg.modal = true;
	phis.application.war.script.WardAdvicePersonalComboModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.war.script.WardAdvicePersonalComboModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel)
					return this.panel;
				var actions = this.actions;
				var barLeft = [];
				var ztlbValue = 1;
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					barLeft.push({
								boxLabel : ac.name,
								inputValue : ac.value,
								name : "stackLeft",
								clearCls : true
							})
				}
				// 西药 中药 草药 其他
				var radioGroupLeft = new Ext.form.RadioGroup({
							width : 300,
							disabled : false,
							items : barLeft,
							listeners : {
								change : this.raidoChange,
								scope : this
							}
						});
				radioGroupLeft.setValue(1);// 设置默认值
				this.radioGroupLeft = radioGroupLeft;
				var buttons = [];
				var confirmBtn = {
					text : "调入组套",
					cmd : "confirm",
					iconCls : "confirm",
					scale : "small",
					handler : this.doConfirm,
					scope : this
				}
				var closeBtn = {
					text : "关闭",
					cmd : "close",
					iconCls : "common_cancel",
					scale : "small",
					handler : this.doClose,
					scope : this
				}
				buttons.push(confirmBtn)
				buttons.push(closeBtn)
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : ['', radioGroupLeft, '->', buttons],
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 220,
										items : this.getComboNameList()
									}, {
										layout : "fit",
										// title : "组套明细",
										border : false,
										split : true,
										region : 'center',
										items : this.getComboNameDetailList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			onWinShow : function() {
				this.comboNameDetailList.store.removeAll();
				this.radioGroupLeft.setValue(1);
				this.comboNameList.cndField.setValue("");
				this.comboNameList.doCndQuery();
			},
			getComboNameList : function() {
				this.comboNameList = this.createModule("getComboNameList",
						this.refComboNameList);
				this.comboNameGrid = this.comboNameList.initPanel();
				// 左边list加载时 调用onListLoadData 并把记录store带过来
				this.comboNameList.on("loadData", this.onListLoadData, this);
				// this.comboNameGrid.on("loadData", this.onListLoadData, this);
				// 点击每一行时调用onListLoadData
				this.comboNameGrid.on("rowClick", this.onListRowClick, this);
				return this.comboNameGrid;
			},
			raidoChange : function(group, newValue, oldValue) {
				if (this.comboNameList) {
					ztlbValue = parseInt(newValue.inputValue)
					this.comboNameList.ZTLB = ztlbValue// 下拉框值
					// 点击插入一行时给把ZT类别传到模糊查询的后台作为过滤条件
					var baseParams = {};
					if (ztlbValue == 4) {
						this.comboNameDetailList.grid.getColumnModel()
								.setColumnHeader(2, "项目名称");
						// this.comboNameDetailList.grid.getColumnModel().setColumnHeader(8,
						// "用法") ;
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(3, false);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(4, true);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(5, true);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(7, true);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(6, true);
					} else {
						this.comboNameDetailList.grid.getColumnModel()
								.setColumnHeader(2, "药品名称");
						// this.comboNameDetailList.grid.getColumnModel().setColumnHeader(8,
						// "药品用法") ;
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(3, true);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(4, false);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(5, false);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(8, false);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(7, false);
						this.comboNameDetailList.grid.getColumnModel()
								.setHidden(6, false);
					}
					baseParams.drugType = ztlbValue;
					this.comboNameList.ZTLBKEY = newValue.boxLabel;// 下拉框键
					// 根据组套类别 和所属类别显示
					this.comboNameList.requestData.cnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'ZTLB'],
															['i', ztlbValue]],
													['eq', ['$', 'SSLB'],
															['i', 2]]],
											[
													'eq',
													['$', 'JGID'],
													['$', '%user.manageUnit.id']]],
									['eq', ['$', 'KSDM'],
											["$", '%user.prop.wardId']]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
					this.comboNameList.initCnd = [
							'and',
							[
									'and',
									[
											'and',
											[
													'and',
													['eq', ['$', 'ZTLB'],
															['i', ztlbValue]],
													['eq', ['$', 'SSLB'],
															['i', 2]]],
											[
													'eq',
													['$', 'JGID'],
													['$', '%user.manageUnit.id']]],
									['eq', ['$', 'KSDM'],
											["$", '%user.prop.wardId']]],
							['eq', ['$', 'SFQY'], ['i', 1]]];
					// 设置分页信息
					var pagingToolbar = this.comboNameList.pagingToolbar;
					pagingToolbar.changePage(1);
					this.comboNameList.loadData();
				}
			},
			doConfirm : function() {
				// 载入组套明细信息
				// alert("功能完善中..")
				var r = this.comboNameList.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请选择要载入的组套信息!", true);
					return;
				}
				this.fireEvent("loadSet", r.data);
				this.win.hide();
			},
			doClose : function() {
				this.win.hide();
			},
			onListLoadData : function(store) {
				// 如果第一次打开页面，默认模拟选中第一行
				if (this.comboNameDetailList) {
					this.comboNameDetailList.clear();
				}
				if (store.getCount() > 0) {
					if (!this.initDataId) {
						this.comboNameGrid.fireEvent("rowclick",
								this.comboNameGrid, 0);
					}
				} else {
					this.comboNameDetailList.ZTBH = "";
				}
			},
			onListRowClick : function(comboNameGrid, rowIndex, e) {
				var r = comboNameGrid.store.getAt(rowIndex);
				if (!r)
					return;
				var ZTLB = this.comboNameList.ZTLB || 1;
				this.comboNameDetailList.requestData.schema = (ZTLB == 4
						? "BQ_ZT02_XM"
						: "YS_MZ_ZT02_BQ")
				this.comboNameDetailList.ZTBH = r.id;
				this.comboNameDetailList.requestData.cnd = ['eq',
						['$', 'ZTBH'], ['d', r.id]];
				this.comboNameDetailList.loadData();
			},
			getComboNameDetailList : function() {
				this.comboNameDetailList = this.createModule(
						"getComboNameDetailList", this.refComboNameDetailList);
				this.comboNameDetailGrid = this.comboNameDetailList.initPanel();
				this.comboNameDetailList.grid.getColumnModel().setHidden(3,
						true);
				return this.comboNameDetailGrid;
			}
		})
