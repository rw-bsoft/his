$package("phis.application.med.script");

$import("phis.script.SimpleModule");

phis.application.med.script.MedicalTechnologyModule = function(cfg) {
	this.exContext = {};
	phis.application.med.script.MedicalTechnologyModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.med.script.MedicalTechnologyModule,
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
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'west',
										width : 350,
										items : this.getLeftList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getRightList()
									}],
							tbar : this.getTbar()
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
				this.leftList = this.createModule("refLeftList",
						this.refLeftList);
				this.leftList.on("click",this.onLeftClick,this);	
				return this.leftList.initPanel();
			},
			/**
			 * 右边列表
			 * 
			 * @return {}
			 */
			getRightList : function() {
				this.rightList = this.createModule("refRightList",
						this.refRightList);
				this.rightList.on("loadData",this.onRightLoad,this)	
				return this.rightList.initPanel();
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				var tjfsStore = new Ext.data.SimpleStore({
							fields : ['value', 'text'],
							data : [[1, '按申检项目'], [2, '按申检医生'], [3, '按申检科室'], [4, '按执行科室']]
						});

				var tjfsCombox = new Ext.form.ComboBox({
							store : tjfsStore,
							valueField : "value",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							triggerAction : 'all',
							mode : 'local',
							emptyText : '',
							width : 120,
							value : 1
						});
				var simple = new Ext.FormPanel({
							labelWidth : 50, // label settings here cascade
							title : '',
							layout : "table",
							bodyStyle : 'padding:5px 5px 5px 5px',
							defaults : {},
							defaultType : 'textfield',
							items : [{
										xtype : "label",
										forId : "window",
										text : "检查日期 "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '检查日期开始',
												name : 'dateFrom',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												},
												width : 100
											}), {
										xtype : "label",
										forId : "window",
										text : "-"
									}, new Ext.ux.form.Spinner({
												fieldLabel : '检查日期结束',
												name : 'dateTo',
												value : new Date()
														.format('Y-m-d'),
												strategy : {
													xtype : "date"
												},
												width : 100
											}), {
										xtype : "label",
										forId : "window",
										text : "统计方式"
									}, tjfsCombox]
						});
				this.simple = simple;
				this.tjfs = tjfsCombox;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			getQueryPar : function() {
				var tjfs = this.tjfs.getValue();// 统计方式
				var datefrom = this.simple.items.get(1).getValue();// 开始时间
				var dateTo = this.simple.items.get(3).getValue();// 结束时间
				// 判断是否选择发药类型
				if (!tjfs ||tjfs=="") {
					MyMessageTip.msg("提示", "请选择统计类型", true);
					return null;
				}
				if (datefrom == null || datefrom == "" || datefrom == undefined
						|| dateTo == null || dateTo == ""
						|| dateTo == undefined) {
					MyMessageTip.msg("提示", "请选择正确的时间范围", true);
					return null;
				}
				var parameter={"TJFS":tjfs,"DATEFROM":datefrom,"DATETO":dateTo};
				return parameter;
			},
			doQuery:function(){
			var body=this.getQueryPar();
			if(body==null){
			return;}
			this.rightList.clear();
			this.leftList.requestData.serviceId=this.serviceId;
			this.leftList.requestData.serviceAction=this.leftActionId;
			this.leftList.requestData.body=body;
			this.leftList.loadData();
			},
			onLeftClick:function(){
			var r=this.leftList.getSelectedRecord();
			if(!r){
			return;}
			if(r.get("SJBM")==4){//4是合计
			return;
			}
			var body=this.getQueryPar();
			if(body==null){
			return;}
			if(r.get("SJBM")==3&&body.TJFS==3){
			MyMessageTip.msg("提示","家庭病床无按申检科室统计明细",true);
			this.rightList.clear();
			return;
			}
			if(body.TJFS==1){//按申检项目
				this.rightList.grid.getColumnModel().setColumnHeader(1, "检查项目");
			}else if(body.TJFS==2){//按申检医生
			this.rightList.grid.getColumnModel().setColumnHeader(1, "申检医生");
			}else if(body.TJFS==3){//按申检科室
			this.rightList.grid.getColumnModel().setColumnHeader(1, "申检科室");
			}else if(body.TJFS==4){//按执行科室
			this.rightList.grid.getColumnModel().setColumnHeader(1, "执行科室");
			}
			body["TJBM"]=r.get("SJBM");
			this.panel.el.mask("正在加载","x-mask-loading")
			this.rightList.requestData.serviceId=this.serviceId;
			this.rightList.requestData.serviceAction=this.rightActionId;
			this.rightList.requestData.body=body;
			this.rightList.loadData();
			},
			onRightLoad:function(){
			this.panel.el.unmask();
			}
		})