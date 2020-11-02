$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalCostsListQueryModule = function(cfg) {
	cfg.width = 960;
	cfg.height = 500;
	cfg.modal = true;
	Ext.apply(this, app.modules.common)
	phis.application.hos.script.HospitalCostsListQueryModule.superclass.constructor
			.apply(this, [cfg])
}
var radioValue = 1;
Ext.extend(phis.application.hos.script.HospitalCostsListQueryModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var actions = this.actions;
				var bar = [];
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					if (ac.id != 'print' && ac.id != 'cancel'&&ac.id != 'refresh') {
						bar.push({
									boxLabel : ac.name,
									inputValue : ac.properties.value,
									name : "stack",
									clearCls : true
								})
					}
				}
				var radioGroup = new Ext.form.RadioGroup({
							width : 160,
							disabled : false,
							items : bar,
							listeners : {
								change : function(group, newValue, oldValue) {
									radioValue = newValue.inputValue;
									if (newValue.inputValue == 1) {
										this.doWhole();
									} else if (newValue.inputValue == 2) {
										this.doMedical();
									} else {
										this.doDrugs();
									}
									this.form.form.getForm().findField("FYHJ")
									.setValue(this.FYHJ);
									this.form.form.getForm().findField("ZFHJ")
									.setValue(this.ZFHJ);
								},
								scope : this
							}
						});
				this.radioGroup = radioGroup
				radioGroup.setValue(1);// 设置默认值
				
				var fyrqCheckBox=new Ext.form.CheckboxGroup({
		            columns: 1,
		            width:110,
		            items: [
		                {boxLabel: '按费用日期检索:', name: 'fyrqdf',width : 110}
		            ],
		            listeners:{
		            	change:function(t,checked)
		            	{
		            		
		            		if(checked.length>0)
		            		{
		            			fyrqStart.enable();
		            			fyrqEnd.enable();
		            			fyrqStart.setValue(new Date());
		            			fyrqEnd.setValue(new Date());
		            		}else
		            		{
		            			fyrqStart.reset();
		            			fyrqEnd.reset();
		            			fyrqStart.disable();
		            			fyrqEnd.disable();	
		            		}
		            		
		            	}
		            }
		        });
			this.fyrqCheckBox=fyrqCheckBox;

			var fyrqStart=new Ext.form.DateField(
					{
						fieldLabel : '',
						name : 'startDate',
						editable:false,
						labelWidth : 80,
						width : 100,
						allowBlank : false,
						altFormats : 'Y-m-d',
						format : 'Y-m-d',
						//value:new Date(),
						disabled:true,
						emptyText : '开始时间'
					});
			this.fyrqStart=fyrqStart;
			var fyrqLabel=new Ext.form.Label(
					{
						width:30,
						text : '至',
					});
			var fyrqEnd=new Ext.form.DateField(
					{
						fieldLabel : '至',
						name : 'endDate',
						editable:false,
						labelWidth : 80,
						width : 100,
						allowBlank : false,
						altFormats : 'Y-m-d',
						format : 'Y-m-d',
						//value:new Date(),
						disabled:true,
						emptyText : '结束时间'
					});
			this.fyrqEnd=fyrqEnd;
				
				
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
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 77,
										items : this.getForm()
									}],
							tbar : [
							        radioGroup,fyrqCheckBox,fyrqStart,fyrqLabel,fyrqEnd,
									(this.tbar || []).concat(this
											.createButton())]
						});

				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				// this.doWhole();
				this.on("winShow", this.onWinShow, this);
			},
			onWinShow : function() {
				this.radioGroup.setValue(1);
				this.fyrqCheckBox.setValue([false]);
				this.fyrqStart.reset();
				this.fyrqEnd.reset();
				this.form.doNew();
				this.initFormData(this.data)
				this.list.requestData.pageNo = 1;
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]],
						['eq', ['$', 'a.JSCS'], ['i', this.data.JSCS]]];
				this.list.refresh();
			},
			getList : function() {
				var module = this
						.createModule("refCostsListList", this.refList);
				// module.on("loadData", this.initFormData, this);
				this.list = module;
				module.opener = this;
				return module.initPanel();
			},
			getForm : function() {
				var module = this
						.createModule("refCostsListForm", this.refForm);
				this.form = module;
				var form = module.initPanel();
				module.opener = this;
				return form
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.id == 'print' || action.id == 'cancel'||action.id == 'refresh') {
						var btn = {};
						btn.accessKey = f1 + i;
						btn.cmd = action.id;
						btn.text = action.name + "(F" + (i + 1) + ")";
						btn.iconCls = action.iconCls || action.id;
						btn.script = action.script;
						btn.handler = this.doAction;
						btn.notReadOnly = action.notReadOnly;
						btn.scope = this;
						buttons.push(btn);
					}
				}
				return buttons;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var ref = item.ref
				if (ref) {
					this.loadRemote(ref, item)
					return;
				}
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			initFormData : function(data) {
				if (data.RYRQ) {
					data.RYRQ = data.RYRQ.substring(0, 10);
				}
				if (data.CYRQ) {
					data.CYRQ = data.CYRQ.substring(0, 10);
				}
				data.ZFLJ=data.ZFHJ;
				data.FYLJ=data.FYHJ;
				this.form.initFormData(data)
			},
			// 全部
			doWhole : function() {
				this.form.doNew();
				this.initFormData(this.data)
				var fyrq=this.fyrqCheckBox.getValue().length>0?true:false;
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]],
						['eq', ['$', 'a.JSCS'], ['i', this.data.JSCS]]];
				
				if(fyrq)
				{
					var startDate=(new Date(this.fyrqStart.getValue())).format('Y-m-d')
					var endDate=(new Date(this.fyrqEnd.getValue()).add(Date.DAY,1)).format('Y-m-d')
				this.list.requestData.cnd.push(
						['ge',['$','a.FYRQ'],
						 ['todate',['s',startDate],['s','yyyy-mm-dd']]
						]);
			    this.list.requestData.cnd.push(
			    		['lt',['$','a.FYRQ'],
			             ['todate',['s',endDate],['s','yyyy-mm-dd']]				    		
			    		]);
			    
				}
				
				this.list.requestData.pageNo = 1;
				this.list.refresh();
				
			},
			// 医疗
			doMedical : function() {
//				this.form.form.getForm().findField("FYHJ")
//						.setValue(this.data.YL_FYHJ);
//				this.form.form.getForm().findField("ZFHJ")
//						.setValue(this.data.YL_ZFHJ);
				var fyrq=this.fyrqCheckBox.getValue().length>0?true:false;
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]],
						['eq', ['$', 'a.YPLX'], ['i', 0]],
						['eq', ['$', 'a.JSCS'], ['i', this.data.JSCS]]];
				
				if(fyrq)
				{
					var startDate=(new Date(this.fyrqStart.getValue())).format('Y-m-d')
					var endDate=(new Date(this.fyrqEnd.getValue()).add(Date.DAY,1)).format('Y-m-d')
				this.list.requestData.cnd.push(
						['ge',['$','a.FYRQ'],
						 ['todate',['s',startDate],['s','yyyy-mm-dd']]
						]);
			    this.list.requestData.cnd.push(
			    		['lt',['$','a.FYRQ'],
			             ['todate',['s',endDate],['s','yyyy-mm-dd']]				    		
			    		]);
			    
				}
				this.list.requestData.pageNo = 1;
				this.list.refresh();
				//获取自负合计
				this.doZfhj(this.list.requestData.cnd);
			},
			// 药品
			doDrugs : function() {
//				this.form.form.getForm().findField("FYHJ")
//						.setValue(this.data.YP_FYHJ);
//				this.form.form.getForm().findField("ZFHJ")
//						.setValue(this.data.YP_ZFHJ);
				var fyrq=this.fyrqCheckBox.getValue().length>0?true:false;
				this.list.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]],
						['in', ['$', 'a.YPLX'], [1, 2, 3], 'd'],
						['eq', ['$', 'a.JSCS'], ['i', this.data.JSCS]]];
				
				if(fyrq)
				{
					var startDate=(new Date(this.fyrqStart.getValue())).format('Y-m-d')
					var endDate=(new Date(this.fyrqEnd.getValue()).add(Date.DAY,1)).format('Y-m-d')
				this.list.requestData.cnd.push(
						['ge',['$','a.FYRQ'],
						 ['todate',['s',startDate],['s','yyyy-mm-dd']]
						]);
			    this.list.requestData.cnd.push(
			    		['lt',['$','a.FYRQ'],
			             ['todate',['s',endDate],['s','yyyy-mm-dd']]				    		
			    		]);
			    
				}
				
				this.list.requestData.pageNo = 1;
				this.list.refresh();
				
				//获取自负合计
				this.doZfhj(this.list.requestData.cnd);
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			doPrint : function() {
				
				//console.log(this.list.tab);
				var printStyle=this.list.tab.activeTab.exCfg.name;
				var pages='phis.prints.jrxml.HospitalCostMxgs';
				if(printStyle=='汇总格式'){pages='phis.prints.jrxml.HospitalCostHzgs';}
				if(printStyle=='医嘱格式'){pages='phis.prints.jrxml.HospitalCostYzgs';}
				
				//console.log(this.data);
				var BRXM = this.data.BRXM;
				var ZYH = this.data.ZYH;
				var BRXZ_text = this.form.form.getForm().findField("BRXZ").getRawValue();
				var BRKS_text = this.form.form.getForm().findField("BRKS").getRawValue();
				var ZYHM = this.data.ZYHM;
				var RYRQ = this.form.form.getForm().findField("RYRQ").getRawValue();
				var CYRQ = this.form.form.getForm().findField("CYRQ").getRawValue();
				var DAYS = this.data.ZYTS;
				var BRCH = this.data.BRCH;
				var ZFLJ = this.data.ZFLJ;
				var ZFHJ = this.data.ZFHJ;
				var startDate=RYRQ;
				
				var nowdate=new Date();
				var nowdatestr=nowdate.getFullYear()+'-'+(nowdate.getMonth()+1)+'-'+nowdate.getDate()+' '+
								nowdate.getHours()+':'+nowdate.getMinutes()+':'+nowdate.getSeconds();
				var endDate=!CYRQ?CYRQ:nowdatestr;
				
				if(this.fyrqStart.getValue())
					startDate=(new Date(this.fyrqStart.getValue())).format('Y-m-d H:i:s');
				if(this.fyrqEnd.getValue())
					endDate=(new Date(this.fyrqEnd.getValue()).add(Date.DAY,1)).format('Y-m-d H:i:s');
				
				var param={
						record:{
							BRXM:BRXM,
							ZYH:ZYH,
							BRXZ_text:BRXZ_text,
							BRKS_text:BRKS_text,
							ZYHM:ZYHM,
							RYRQ:RYRQ,
							CYRQ:CYRQ,
							DAYS:DAYS,
							BRCH:BRCH,
							ZFLJ:ZFLJ,
							ZFHJ:ZFHJ
						},
						ZYH:ZYH,
						startDate:startDate,
						endDate:endDate,
						cnd:this.list.requestData.cnd
				};
				console.log(this.list.requestData.cnd)
				console.log(param)
//				return;
				
				var LODOP=getLodop();
				
				var url="resources/"+pages+".print?param="
				+ encodeURI(encodeURI(Ext.encode(param)))
				+ "&silentPrint=1";
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				// 预览LODOP.PREVIEW();
				// LODOP.PRINT_DESIGN();
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				// 预览
				LODOP.PREVIEW();
				//LODOP.PRINT();
				
				
//				var module = this.createModule("dayList", this.refDayList);
//				var ZYH = this.data.ZYH;
//				if (ZYH == null) {
//					MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
//					return;
//				}
//				var YPLX = radioValue;
//				var BRXM = this.form.form.getForm().findField("BRXM")
//						.getValue();
//				var BRXZ = this.form.form.getForm().findField("BRXZ")
//						.getRawValue();
//				var DAYS = this.form.form.getForm().findField("ZYTS")
//						.getValue();
//				var RYRQ = this.form.form.getForm().findField("RYRQ")
//						.getValue();
//				var ZFHJ = this.form.form.getForm().findField("ZFHJ")
//						.getValue();
//				if(YPLX!=1){
//					ZFHJ=this.data.ZFHJ;
//				}
//				var ZYHM = this.form.form.getForm().findField("ZYHM")
//						.getValue();
//				var BRKS = this.form.form.getForm().findField("BRKS")
//						.getRawValue();
//				var BRCH = this.form.form.getForm().findField("BRCH")
//						.getValue();
//				var CYRQ = this.form.form.getForm().findField("CYRQ")
//						.getValue();
//				var FYHJ = this.form.form.getForm().findField("FYHJ")
//						.getValue();
//				module.ZYH = ZYH;
//				module.BRXM = encodeURIComponent(BRXM);
//				module.BRXZ = encodeURIComponent(BRXZ);
//				module.DAYS = DAYS;
//				module.RYRQ = RYRQ;
//				module.ZFHJ = ZFHJ;
//				module.ZYHM = ZYHM;
//				module.BRKS = encodeURIComponent(BRKS);
//				module.BRCH = BRCH;
//				module.CYRQ = CYRQ;
//				module.FYHJ = FYHJ;
//				module.YPLX = YPLX;
//				module.JSJE = this.JSJE;
//				module.JKHJ = this.data.JKHJ;
//				module.NL = this.data.RYNL;
//				module.BRXB = this.data.BRXB
//				module.CZGH = this.data.CZGH;
//				module.JSLX=9;
//				module.JSCS=this.data.JSCS;
//				module.initPanel();
//				module.doPrint();
			},
			doZfhj:function()
			{
				var res = util.rmi.miniJsonRequestSync({
					serviceId : "phis.hospitalCostProcessingService",
					serviceAction : "HospitalCostMxQuery",
					cnd:this.list.requestData.cnd							

				});
				if (res.code > 200) {
					Ext.MessageBox.alert("提示","费用查询失败...");
				}
				console.log(res);
				this.ZFHJ=res.json.ZFHJ;
				this.FYHJ=res.json.FYHJ;
				
			},
			doRefresh:function()
			{
				var inputValue=this.radioGroup.getValue().inputValue;
				if (inputValue == 1) {
					this.doWhole();
				} else if (inputValue == 2) {
					this.doMedical();
				} else {
					this.doDrugs();
				}
				this.form.form.getForm().findField("FYHJ")
				.setValue(this.FYHJ);
				this.form.form.getForm().findField("ZFHJ")
				.setValue(this.ZFHJ);
			}
		});