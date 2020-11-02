$package("phis.application.cfg.script");

$import("phis.script.SimpleModule");

phis.application.cfg.script.LogisticsInventoryControlRightModule = function(cfg) {
	phis.application.cfg.script.LogisticsInventoryControlRightModule.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext
		.extend(
				phis.application.cfg.script.LogisticsInventoryControlRightModule,
				phis.script.SimpleModule,
				{
					initPanel : function(sc) {
						if (this.panel) {
							return this.panel;
						}
						var schema = sc
						if (!schema) {
							var re = util.schema.loadSync(this.entryName)
							if (re.code == 200) {
								schema = re.schema;
							} else {
								this.processReturnMsg(re.code, re.msg,
										this.initPanel)
								return;
							}
						}
						this.schema = schema;
						var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								title : '匹配物资',
								region : 'west',
								width : 400,
								items : this.getLeftList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								title : '所有物资',
								region : 'center',
								items : this.getRightList()
							} ]
						});
						this.panel = panel;
						return panel;
					},
					getRightList : function() {
						this.rightList = this.createModule("rightList",
								this.refRightList);
						this.rightList.on("wzClick", this.onWzClick, this);
						return this.rightList.initPanel();
					},
					getLeftList : function() {
						this.leftList = this.createModule("leftList",
								this.refLeftList)
						return this.leftList.initPanel();
					},
					// 刷新对应物资信息
					loadData : function(d) {
						this.leftList.isEdit = 0;
						if (d == null) {
							this.leftList.clear();
							return;
						}
						this.checkData = d;
						this.leftList.requestData.cnd = [
								'and',
								[
										'and',
										[
												'or',
												[
														'eq',
														[ '$', 'a.JGID' ],
														[
																's',
																this.mainApp['phisApp'].deptId ] ],
												[
														'eq',
														[ '$', 'a.JGID' ],
														[
																's',
																this.mainApp.topUnitId ] ] ],
										[ 'eq', [ '$', 'FYLB' ],
												[ 'd', d.FYGB ] ] ],
								[ 'eq', [ '$', 'FYXH' ], [ 'd', d.FYXH ] ] ]
						/**
						 * ['or', ['eq', ['$', 'b.JGID'], ['s',
						 * this.mainApp['phisApp'].deptId]], ['eq', ['$', 'b.JGID'], ['s',
						 * this.mainApp.topUnitId]] ]
						 */
						this.leftList.loadData();
					},
					// 物资单击增加
					onWzClick : function(d) {
						d["FYXH"] = this.checkData.FYXH;
						d["FYLB"] = this.checkData.FYGB;
						d["JGID"] = this.mainApp['phisApp'].deptId;
						d["WZSL"] = 1;
						this.leftList.doCreate(d);
						this.leftList.isEdit = 1;
					},
					getData : function() {
						var count = this.leftList.store.getCount();
						// if(count==0){
						// return null;
						// }
						var r = new Array();
						for ( var i = 0; i < count; i++) {
							r.push(this.leftList.store.getAt(i).data);
						}
						return r;
					}

				});