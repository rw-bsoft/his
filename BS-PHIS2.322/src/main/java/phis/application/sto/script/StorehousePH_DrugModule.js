$package("phis.application.sto.script");
$import("phis.script.SimpleModule", "util.helper.Helper");

phis.application.sto.script.StorehousePH_DrugModule = function(cfg) {
	this.exContext = {};
	phis.application.sto.script.StorehousePH_DrugModule.superclass.constructor
			.apply(this, [ cfg ]);
};
Ext.extend(phis.application.sto.script.StorehousePH_DrugModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
					border : false,
					width : this.width,
					height : this.height,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [ {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'west',
						width : 470,
						items : this.getLeftList()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'center',
						items : this.getRightList()
					} ]
				});
				this.panel = panel;
				return panel;
			},
			/**
			 * 左边列表
			 * 
			 * @return {}
			 */
			getLeftList : function() {
				this.refLeftList = this.createModule("refLeftList",
						this.refLeftList);
				this.refLeftList.opener = this;
				var body = {};
				body.KSSJ = this.opener.opener.simple.items.get(1).getValue();
				body.JSSJ = this.opener.opener.simple.items.get(3).getValue();
				body.PYDM = this.opener.opener.simple.items.get(5).getValue()
						.toUpperCase();
				this.refLeftList.requestData.body = body;
				return this.refLeftList.initPanel();
			},
			/**
			 * 双击显示明细
			 */
			doShowDetails : function(record) {
				if (!record) {
					return;
				}
				var parameter = record.data;
				this.opener.setValue(parameter);
				var datefrom = this.opener.opener.simple.items.get(1)
						.getValue();// 开始时间
				var dateTo = this.opener.opener.simple.items.get(3).getValue();// 结束时间
				parameter.KSSJ = datefrom;
				parameter.JSSJ = dateTo;
				this.refRightList.requestData.body = parameter;
				this.refRightList.refresh();
			},
			/**
			 * 右边列表
			 * 
			 * @return {}
			 */
			getRightList : function() {
				this.refRightList = this.createModule("refRightList",
						this.refRightList);
				return this.refRightList.initPanel();
			}
		});