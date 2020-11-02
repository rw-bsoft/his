$package("phis.application.odm.script");

$import("phis.script.SimpleModule");
				   
phis.application.odm.script.OwnedDrugManageModule = function(cfg) {
	phis.application.odm.script.OwnedDrugManageModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.odm.script.OwnedDrugManageModule,
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
										width : 470,
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
			getLeftList : function() {
				this.leftList = this.createModule("leftList", this.refLeft);
				this.leftList.on("recordClick", this.onRecordClick, this);
				return this.leftList.initPanel();
			},
			getRightList : function() {
				this.rightList = this.createModule("rightList",
						this.refRight);
				this.rightList.on("loadData",this.onRightLoad,this);
				return this.rightList.initPanel();
			},
			getTbar : function() {
				var tbar = new Ext.Toolbar();
				this.xradio = new Ext.form.RadioGroup({
							height:20,
							width:140,
							name : 'mzzy', 
							value : "1",
							items : [ {
										boxLabel : '门诊',
										name : 'mz',
										inputValue : 1
									}, {
										boxLabel : '住院',
										name : 'mz',
										inputValue : 2
									}]
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
										text : "开单日期 "
									}, new Ext.ux.form.Spinner({
												fieldLabel : '开单日期开始',
												name : 'dateFrom',
												value : new Date()
														.format('Y-m-d 00:00:00'),
												strategy : {
													xtype : "dateTime"
												},
												width : 150
											}), {
										xtype : "label",
										forId : "window",
										text : "-"
									}, new Ext.ux.form.Spinner({
												fieldLabel : '开单日期结束',
												name : 'dateTo',
												value : new Date()
														.format('Y-m-d H:i:s'),
												strategy : {
													xtype : "dateTime"
												},
												width : 150
											}),{
										xtype : "label",
										forId : "window",
										text : "药品名称"
									},new Ext.form.TextField({name:"ypmc"}),this.xradio]
						});
				this.simple = simple;
				tbar.add(simple, this.createButtons());
				return tbar;
			},
			// 查询
			doQuery : function() {
				var datefrom = this.simple.items.get(1).getValue();
				var dateTo = this.simple.items.get(3).getValue();
				var ypmc=this.simple.items.get(5).getValue();
				var lx = this.xradio.getValue().inputValue;
				if(datefrom==null||datefrom==""){
				MyMessageTip.msg("提示",
								"开始时间不能为空", true);
								return;
				}
				if(dateTo==null||dateTo==""){
				MyMessageTip.msg("提示",
								"结束时间不能为空", true);
								return;
				}
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "") {
					try {
						var df = new Date(Date.parse(datefrom
								.replace(/-/g, "/")));
						var dt = new Date(Date.parse(dateTo.replace(/-/g, "/")));
						if (df.getTime() > dt.getTime()) {
							Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
							return;
						}
					} catch (e) {
						MyMessageTip.msg("提示",
								"时间格式错误,正确格式2000-01-01 01:01:01!", true);
						return;
					}
				}
				this.changeWindow(lx==1?true:false);
				var body={"dateFrom":datefrom,"dateTo":dateTo,"lx":lx};
				if(ypmc!=null&&ypmc!=""){
				body["ypmc"]=ypmc
				}
				this.body=body;
				this.rightList.clear();
				this.leftList.requestData.serviceId=this.serviceId;
				this.leftList.requestData.serviceAction=this.queryLeftActionId;
				this.leftList.requestData.body=body;
				this.leftList.loadData();
			},
			//根据不同类型显示右边明细的列, 门诊lx为true
			changeWindow : function(lx) {
					this.rightList.grid.getColumnModel().setColumnHeader(this.rightList.grid.getColumnModel()
									.getIndexById('1'), lx?"开单时间":"开嘱时间");
					this.rightList.grid.getColumnModel().setColumnHeader(this.rightList.grid.getColumnModel()
									.getIndexById('2'), lx?"处方号码":"住院号码");
//					this.rightList.grid.getColumnModel().setHidden(
//							this.rightList.grid.getColumnModel()
//									.getIndexById('TZSJ'), lx);
//					alert(this.rightList.grid.getColumnModel()
//									.getIndexById('YYTS'))
//					alert(this.rightList.grid.getColumnModel()
//									.getIndexById('YPSL'))
//					alert(this.rightList.grid.getColumnModel()
//									.getIndexById('YFDW'))
					//不知道为什么按ID找不到列,只能这么写.. 11是TZSJ, 8是YYTS 9是YPSL 10是YFDW 有空了研究下.
					this.rightList.grid.getColumnModel().setHidden(
							this.rightList.grid.getColumnModel()
									.getIndexById('11'), lx);
					this.rightList.grid.getColumnModel().setHidden(
							this.rightList.grid.getColumnModel()
									.getIndexById('8'), !lx);
					this.rightList.grid.getColumnModel().setHidden(
							this.rightList.grid.getColumnModel()
									.getIndexById('9'), !lx);
					this.rightList.grid.getColumnModel().setHidden(
							this.rightList.grid.getColumnModel()
									.getIndexById('10'), !lx);
			},
			afterOpen:function(){
			this.changeWindow(true);
			},
			onRecordClick:function(record){
			if(!this.body){
			return;
			}
			this.panel.el.mask("正在加载", "x-mask-loading");
			this.body["ypxh"]=record.get("YPXH");
			this.body["ypcd"]=record.get("YPCD");
			this.rightList.requestData.serviceId=this.serviceId;
			this.rightList.requestData.serviceAction=this.queryRightActionId;
			this.rightList.requestData.body=this.body;
			this.rightList.loadData();
			},
			onRightLoad:function(){
			this.panel.el.unmask();
			}
		});