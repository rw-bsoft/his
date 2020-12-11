/**
 * 医嘱卡片打印模型
 * 
 * @author : liws
 */
$package("phis.application.war.script")

$import("phis.script.SimpleModule");
phis.application.war.script.OrderCardsPrintModule = function(cfg) {
	this.westWidth = cfg.westWidth || 250;
	this.showNav = true;
	this.height = 450;
	this.orderTypeValue = 3;
	this.typeValue = 1;
	this.commitType = "projectCommitTab"; // 提交类型，有按项目提交projectCommitTab
	// 和按病人提交patientCommitTab
	// 在Applications.xml中配置
	phis.application.war.script.OrderCardsPrintModule.superclass.constructor.apply(this,
			[cfg]);
},

Ext.extend(phis.application.war.script.OrderCardsPrintModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				// 创建一个Panel，该Panel中包含phis.script.med.WardProjectTabModule
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							width : this.width,
							height : this.height,
							tbar : this.createTbar(),
							items : [{
										layout : "fit",
										split : true,
										title : '',
										region : 'center',
										width : 280,
										items : this.getList()
									}]
						});
				return panel;
			},
			/**
			 * 为界面保存、打印、关闭等按钮添加事件处理
			 * 
			 * @param item
			 * @param e
			 */
			doAction : function(item, e) {
				if (item.id == "print") {// 打印按钮执行
					this.print();
				}
			},
			createTbar : function() {
				var tbar = [];
				tbar.push('-');

				var store = new Ext.data.SimpleStore({
							fields : ['key', 'text'],
							data : [['zyhm', '住院号码'], ['brxm', '姓名'],
									['brch', '床号']]
						});

				var combo = new Ext.form.ComboBox({
							id : 'myCombo',
							width : 110,
							store : store,// 填充数据
							emptyText : '请选择',
							mode : 'local',// 数据模式，local代表本地数据
							value : 'zyhm',// 默认值,要设置为提交给后台的值，不要设置为显示文本,可选
							triggerAction : 'all',// 显示所有下列数据，一定要设置属性triggerAction为all
							valueField : 'key',// 值,可选
							displayField : 'text',// 显示文本 ，对应下面store里的'text'，
							editable : false,// 是否允许输入
							forceSelection : true,// 必须选择一个选项
							blankText : '请选择'// 该项如果没有选择，则提示错误信息,
						});

				tbar.push(combo);
				tbar.push('-');

				var qValue = new Ext.form.TextField({
							id : 'qValue',
							width : 110
						});

				tbar.push(qValue);
				tbar.push('-');

				var queryPatient = {
					xtype : "button",
					text : "",
					iconCls : "query",
					scope : this,
					handler : this.doQuer
				};

				tbar.push(queryPatient);
				tbar.push('-');
				var dysj = new Ext.ux.form.Spinner({
					fieldLabel : '查询时间开始',
					name : 'zykpdydateFrom',
					value : new Date()
							.format('Y-m-d'),
					strategy : {
						xtype : "date"
					},
					width : 100
				})
	
	             this.dysj = dysj;
	             tbar.push(dysj);
				// 获取Applications.xml中配置的按钮(如打印、保存、取消等)
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.id = action.id;
					btn.accessKey = "F1", btn.cmd = action.id;
					btn.text = action.name, btn.iconCls = action.iconCls
							|| action.id;
					// 添加doAction点击事件,调用doAction方法
					btn.handler = this.doAction;
					btn.name = action.id;
					btn.notReadOnly = action.properties.notReadOnly;
					if (action.properties.notReadOnly == "true") {// 设置为启用标志
						btn.disabled = false;
					} else {// 设置为禁用标志
						btn.disabled = true;
					}
					btn.scope = this;
					tbar.push(btn);
				}

				this.orderTypeRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 190,
					id : 'OrderTypeRadio',
					name : 'OrderTypeRadio', // 后台返回的JSON格式，直接赋值
					value : 3,
					items : [{
								boxLabel : '全部',
								name : 'OrderTypeRadio',
								inputValue : 3
							},{
								boxLabel : '长期',
								name : 'OrderTypeRadio',
								inputValue : 0
							}, {
								boxLabel : '临时',
								name : 'OrderTypeRadio',
								inputValue : 1
							}],
					listeners : {
						change : function(OrderTypeRadio, newValue, oldValue, eOpts) {
							this.orderTypeValue = newValue.inputValue;
							var OrderModule = this.PrintTabModule.module;
							var arr_zyh = this.getArr_ZYH()
							OrderModule.sendToCKData(arr_zyh,
									this.typeValue,newValue.inputValue);// 当Change卡片选项时
							// 触发刷新页面
						},
						scope : this
					}
				});
				tbar.push('->');
				tbar.push('-');
				tbar.push(this.orderTypeRadio);// 将单选按钮组加到面板中
				this.CardRadio = new Ext.form.RadioGroup({
					height : 20,
					width : 190,
					id : 'CardRadio',
					name : 'CardRadio', // 后台返回的JSON格式，直接赋值
					value : 1,
					items : [{
								boxLabel : '固定卡片',
								name : 'CardRadio',
								inputValue : 1
							}, {
								boxLabel : '执行单',
								name : 'CardRadio',
								inputValue : 2
							}],
					listeners : {
						change : function(CardRadio, newValue, oldValue, eOpts) {
							this.typeValue = newValue.inputValue;
							var OrderModule = this.PrintTabModule.module;
							var arr_zyh = this.getArr_ZYH()
							OrderModule.sendToCKData(arr_zyh,
									newValue.inputValue,this.orderTypeValue);// 当Change卡片选项时
							// 触发刷新页面
						},
						scope : this
					}
				});
				tbar.push('->');
				tbar.push(this.CardRadio);// 将单选按钮组加到面板中

				return tbar;
			},
			print : function() {
				var arr_zyh = this.getArr_ZYH();// 获得当前选中病人
				if (arr_zyh.length != 0) {
					var yzlb = this.PrintTabModule.module.ActiveCardKey// 获得当前激活是哪张卡片：口服卡、注射卡、静滴卡

					var serviceId = "phis.prints.jrxml.OrderCardsMouthCard";// 默认打印口服卡
					if (this.typeValue == 2) {// 如果是执行单
						serviceId = "phis.prints.jrxml.OrderCardsMouthCardZX";// 因口服卡、注射卡、静滴卡的执行单格式相同
						// 所以采用同一Service
					} else if (this.typeValue == 1) {
						if (yzlb == 2) {
							serviceId = "phis.prints.jrxml.OrderCardsMouthCard";// 口服卡Service
						} else if (yzlb == 3) {
							serviceId = "phis.prints.jrxml.OrderCardsInjectionCard";// 注射卡Service
						} else if (yzlb == 4) {
							serviceId = "phis.prints.jrxml.OrderCardsStillDripCard";// 静滴卡Service
							var res = phis.script.rmi.miniJsonRequestSync({
								serviceId : "clinicManageService",
								serviceAction : "loadSystemParams",
								body : {
									privates : ['JDKLS']
								}
							});
							if (res && res.json.body) {
								if (res.json.body.JDKLS == "1") {
									this.JDKLS=res.json.body.JDKLS;
									serviceId = "phis.prints.jrxml.OrderCardsStillDripCardOne";
								}
							}
						}else if (yzlb == 5) {
                            serviceId = "phis.prints.jrxml.OrderCardsTransfusionPatrolCard";//输液巡视卡Service
                        }
					}
					var pages=serviceId;
					var url="resources/"+pages+".print?yzlb=" + yzlb + "&arr_zyh="
							+ arr_zyh.toString() + "&wardName="
							+ encodeURI(encodeURI(this.mainApp['phis'].wardName)) + "&orderTypeValue=" + this.orderTypeValue
							+ "&wardId=" + this.mainApp['phis'].wardId+"&dysj=" + this.dysj.getValue();
					 /*
					window
							.open(
									url,
									"",
									"height="
											+ (screen.height - 100)
											+ ", width="
											+ (screen.width - 10)
											+ ", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no");
					*/
					var LODOP=getLodop();
//						LODOP.SET_PRINT_PAGESIZE("1","3200","4570","");
					if(this.JDKLS&&this.JDKLS=="1"&&yzlb == 4){//静滴卡
					 	LODOP.SET_PRINT_PAGESIZE("2","60mm","90mm","");//60mm*90mm
					}
					if(yzlb == 5){//输液巡视卡
						LODOP.PRINT_INIT("打印控件");
						LODOP.SET_PRINT_PAGESIZE("1","","","A5");//148mm*210mm					
						var rehtm = util.rmi.loadXML({url:url,httpMethod:"get"})
						rehtm = rehtm.replace(/table style=\"/g, "table style=\"page-break-after:always;")
						rehtm.lastIndexOf("page-break-after:always;");
						rehtm = rehtm.substr(0,rehtm.lastIndexOf("page-break-after:always;"))+rehtm.substr(rehtm.lastIndexOf("page-break-after:always;")+24);
						LODOP.ADD_PRINT_HTM("0","0","100%","100%",rehtm);
						LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
					}else{
						LODOP.PRINT_INIT("打印控件");
						LODOP.SET_PRINT_PAGESIZE("1","","","A4");//148mm*210mm
						LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
						LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
					}
//					LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");

					//预览
					LODOP.PREVIEW();
				} else {
					Ext.Msg.alert("警告", "当前没有选中病人,请选择病人后再进行打印！");
				}

			},
			doQuer : function() {
				var comValue = Ext.getCmp('myCombo').getValue();
				var qValue = Ext.getCmp('qValue').getValue();
				if(qValue.length == 1){
					if(comValue != "brxm"){
						return;
					}
				}else if(qValue.length == 0){
					qValue = "";
				}
				if (comValue == "zyhm") {
					this.PrintTabModule.list.requestData.serviceAction = "queryPatientListByZYHM";
					this.PrintTabModule.list.requestData.ZYHM = qValue;
				} else if (comValue == "brxm") {
					this.PrintTabModule.list.requestData.serviceAction = "queryPatientListByBRXM";
					this.PrintTabModule.list.requestData.BRXM = qValue;
				} else if (comValue == "brch") {
					this.PrintTabModule.list.requestData.serviceAction = "queryPatientListByBRCH";
					this.PrintTabModule.list.requestData.BRCH = qValue;
				}
				this.PrintTabModule.list.store.reload();
			},
			/**
			 * 创建模型ID为WAR9601的模型
			 * 
			 * @returns
			 */
			getList : function() {
				var module = this.createModule("getList", this.refModule);
				var list = module.initPanel();
				this.PrintTabModule = module;
				this.PrintTabModule.list.CardModule = this.PrintTabModule.module// 将医嘱卡片Module传给List
				this.SelectedRecords = this.getArr_ZYH();
				this.PrintTabModule.module.parentModule = this;
				this.PrintTabModule.list.typeValue = this;
				return list;
			},
			getArr_ZYH : function() {
				// 获得医嘱病人List的module
				var patientModule = this.PrintTabModule.list;
				// 获得选中的医嘱病人List
				var records = patientModule.getSelectedRecords();

				var arr_zyh = [];
				for (i = 0; i < records.length; i++) {
					var record = records[i];
					arr_zyh.push(record.data.ZYH);
				}
				return arr_zyh;
			}
		})