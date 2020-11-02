$package("phis.application.war.script");

$import("phis.script.SimpleModule",
		"phis.prints.script.DoctorAdviceSubmitPrintView");

phis.application.war.script.DoctorAdviceSubmitModule = function(cfg) {
	phis.application.war.script.DoctorAdviceSubmitModule.superclass.constructor
			.apply(this, [cfg]);
	this.fyfs = 0;
	this.lsyz = 2;
	this.yfsb = 0;
}
Ext.extend(phis.application.war.script.DoctorAdviceSubmitModule,
		phis.script.SimpleModule, {
			// 页面初始化
			initPanel : function(sc) {
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : this.leftTitle,
										region : 'west',
										width : 220,
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : this.rightTitle,
										region : 'center',
										items : this.getRList()
									}],
							tbar : this.getTbar(),
							bbar : this.getBbar()
						});
				this.panel = panel;
				return panel;
			},
			// 获取左边的list
			getLList : function() {
				this.leftList = this.createModule("leftList", this.refLList);
				this.leftList.on("selectRecord",this.onSelectRecord,this);
				return this.leftList.initPanel();
			},
			// 获取右边的list
			getRList : function() {
				this.rightList = this.createModule("rightList", this.refRList);
				this.rightList.on("afterLoadData",this.onAfterLoadData,this)
				return this.rightList.initPanel();
			},
			// 左边选中刷新右边
			onSelectRecord:function(){
			var records=this.leftList.getSelectedRecords();
			this.rightList.clear();
			var length=records.length;
			if(length==0||!this.cnd){
			return;}
			var zyhs=new Array();
			for(var i=0;i<length;i++){
			var r=records[i];
			zyhs.push(r.get("ZYH"));
			}
			this.panel.el.mask("正在查询数据...","x-mask-loading")
			this.rightList.requestData.cnd=this.cnd;
			this.rightList.requestData.ZYHS=zyhs;
			this.rightList.loadData();
			},
			onAfterLoadData:function(){
			this.panel.el.unmask()
			},
			// 刷新
			doRefresh : function() {
			this.leftList.clear();
			this.rightList.clear();
			var ldt_lyrq = "";// 领用日期
				if (this.dateField) {
					ldt_lyrq = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (ldt_lyrq < new Date().format('Y-m-d')) {
					MyMessageTip.msg("提示", "领药日期不能小于当前日期!", true);
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					return;
				}
				var al_zyh = "0";// 病人住院号
				if(this.initDataId){
				al_zyh=this.initDataId;
				}
				var data = {
					"LYRQ" : ldt_lyrq
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceQuery,
							body : data
						});
				if (r.code == 600) {
					MyMessageTip.msg("提示", "提交天数超过最大天数控制!", true);
					return;
				}
			this.cnd=al_zyh + "#" + ldt_lyrq + "#"
						+ this.fyfs + "#" + this.lsyz + "#" + this.yfsb
			this.leftList.requestData.cnd=this.cnd;
			this.leftList.loadData();
			},
			getTbar : function() {
				var label = new Ext.form.Label({
							text : "领药日期至"
						});
				this.dateField = new Ext.form.DateField({
							name : 'storeDate',
							value : new Date(),
							width : 100,
							allowBlank : false,
							altFormats : 'Y-m-d',
							format : 'Y-m-d'
						});
				return [label, '-', this.dateField]
						.concat(this.createButtons());
			},
			getBbar : function() {
				var lysfLabel = new Ext.form.Label({
							text : "领药方式"
						});
				var lyfsDic = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.hairMedicineWay",
							width : 150,
							emptyText : "全部"
						});
				this.lyfsDc = lyfsDic;
				lyfsDic.on("select", this.onFsSelect, this);
				lyfsDic.store.on("load", this.fsDicLoad)
				var yz_radiogroup = new Ext.form.RadioGroup({
							width : 400,
							style : "padding-left : 30px",
							items : [{
										boxLabel : '全部医嘱',
										inputValue : 1,
										name : "yzlx",
										checked : true,
										clearCls : true
									}, {
										boxLabel : '长期医嘱',
										name : "yzlx",
										inputValue : 2,
										clearCls : true
									}, {
										boxLabel : '临时医嘱',
										name : "yzlx",
										inputValue : 3,
										clearCls : true
									}, {
										boxLabel : '急诊用药',
										name : "yzlx",
										inputValue : 4,
										clearCls : true
									}, {
										boxLabel : '出院带药',
										name : "yzlx",
										inputValue : 5,
										clearCls : true
									}],
							listeners : {
								change : function(group, newValue, oldValue) {
									if (newValue.inputValue == 1) {
										this.lsyz = 2;
										this.doRefresh();
									} else if (newValue.inputValue == 2) {
										this.lsyz = 0;
										this.doRefresh();
									} else if (newValue.inputValue == 3) {
										this.lsyz = 1;
										this.doRefresh();
									} else if (newValue.inputValue == 4) {
										this.lsyz = 3;
										this.doRefresh();
									}else if (newValue.inputValue == 5) {
										this.lsyz = 4;
										this.doRefresh();
									}
								},
								scope : this
							}
						});
				this.yz_radiogrup = yz_radiogroup;
				var fyyfLabel = new Ext.form.Label({
							text : "发药药房  "
						});
				var fyyfDic = util.dictionary.SimpleDicFactory.createDic({
					id : "phis.dictionary.pharmacy_bq",
					width : 150,
					filter : "['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]",
					emptyText : "全部药房"
				});
				this.fyyfdc = fyyfDic;
				fyyfDic.on("select", this.onYfSelect, this);
				fyyfDic.store.on("load", this.yfDicLoad)
				return [lysfLabel, '-', lyfsDic, yz_radiogroup, fyyfLabel, '-',
						fyyfDic]
			},
			yfDicLoad : function() {
				var data = {
					"key" : "0",
					"text" : "全部药房"
				};
				var r = new Ext.data.Record(data);
				this.insert(0, r);
			},
			fsDicLoad : function() {
				var data = {
					"key" : "0",
					"text" : "全部"
				};
				var r = new Ext.data.Record(data);
				this.insert(0, r);
			},
			onYfSelect : function(fyyfDic) {
				fyyfDic.emptyText = "";
				this.yfsb = fyyfDic.value;
				this.doRefresh();
			},
			onFsSelect : function(fyfsDic) {
				fyfsDic.emptyText = "";
				this.fyfs = fyfsDic.value;
				this.doRefresh();
			},
			afterOpen : function() {
				if (this.panel) {
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					if (this.fyyfdc) {
						this.fyyfdc.setValue("0");
					}
					if (this.lyfsDc) {
						this.lyfsDc.setValue("0");
					}
					if (this.yz_radiogrup) {
						this.yz_radiogrup.setValue("1");
					}
					this.doRefresh();
				}
			},
			doConfirm : function() {
				var rs=this.leftList.getSelectedRecords();
				if(rs.length==0){
				MyMessageTip.msg("提示","请先选择记录",true)
				return ;
				}
				var records=this.rightList.getSelectedRecords();
				var length=records.length;
				if(length==0){
					MyMessageTip.msg("提示","请先选择记录",true)
				return;}
				if(length>300){
				MyMessageTip.msg("提示","记录数超过300,可能无法正常打印,请分次提交",true)
				}
				Ext.Msg.confirm("请确认", "是否打印?", function(btn1) {
								if (btn1 == 'yes') {
									this.doPrint(1);
									this.confirm();
								}else{
								this.confirm();	
								}
								},this)
			},
			confirm:function(){
			var ldt_lyrq = "";// 领用日期
				if (this.dateField) {
					ldt_lyrq = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (ldt_lyrq < new Date().format('Y-m-d')) {
					MyMessageTip.msg("提示", "领药日期不能小于当前日期!", true);
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					return;
				}
				var al_zyh = "0";// 病人住院号
				var records=this.rightList.getSelectedRecords();
				var length=records.length;
				if(length==0){
					MyMessageTip.msg("提示","请先选择记录",true)
				return;}
				var jlxhs=new Array();
				for(var i=0;i<length;i++){
				var r=records[i];
				jlxhs.push(r.get("JLXH"));
				}
				Ext.Msg.confirm("请确认", "药品医嘱提交药房?", function(btn) {
					if (btn == 'yes') {
						var data = {
							"ZYH" : al_zyh,
							"LYRQ" : ldt_lyrq,
							"FYFS" : this.fyfs,
							"LSYZ" : this.lsyz,
							"YFSB" : this.yfsb,
							"JLXHS":jlxhs
						};
						this.panel.el.mask("正在查询数据...","x-mask-loading")
						phis.script.rmi.jsonRequest({
									serviceId : this.serviceId,
									serviceAction : this.serviceActionSave,
									body : data
								}, function(code, msg, json) {
									this.panel.el.unmask()
									if (code >= 300) {
										this.processReturnMsg(code, msg);
										return;
									}
									if (json.RES_MESSAGE) {
										MyMessageTip.msg("提示",
												json.RES_MESSAGE, true);
									}
									// add by yangl 增加抗菌药物提醒
									if (json.warnMsg) {
										var s = "";
										for (var i = 0; i < json.warnMsg.length; i++) {
											s += json.warnMsg[i];
											if (i < json.warnMsg.length - 1) {
												s += "<br>";
											}
										}
										Ext.Msg.alert("警告", s);
									}
									this.doRefresh(this.fyfs, this.lsyz,
											this.yfsb);
									if (this.needToClose) {
										this.opener.win.hide();
										this.needToClose = false;
									}
									 this.fireEvent("doSave",json.body);
								}, this)
					}
				}, this);
				return;
			},
			doPrint : function(tag) {
				var al_zyh = "0";// 病人住院号
				var ldt_lyrq = "";// 领用日期
				if (this.dateField) {
					ldt_lyrq = new Date(this.dateField.getValue())
							.format("Y-m-d");
				}
				if (ldt_lyrq < new Date().format('Y-m-d')) {
					MyMessageTip.msg("提示", "领药日期不能小于当前日期!", true);
					if (this.dateField) {
						this.dateField.setValue(this.mainApp.serverDate);
					}
					return false;
				}
				// 预领日期控制
				var data = {
					"LYRQ" : ldt_lyrq
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceQuery,
							body : data
						});
				if (r.code == 600) {
					MyMessageTip.msg("提示", "提交天数超过最大天数控制!", true);
					return false;
				}
				var records=this.rightList.getSelectedRecords();
				var length=records.length;
				if(length==0){
				return;}
				var jlxhs=new Array();
				for(var i=0;i<length;i++){
				var r=records[i];
				jlxhs.push(r.get("JLXH"));
				}
				var pages="phis.prints.jrxml.DoctorAdviceSubmit";
				var url="resources/"+pages+".print?silentPrint=1&type=1&al_zyh=" + al_zyh + "&ldt_lyrq=" +ldt_lyrq
				+ "&fyfs=" + this.fyfs + "&lsyz=" + this.lsyz + "&yfsb="
				+ this.yfsb+"&jlxhs="+jlxhs;
				if(url.length>3000){
				MyMessageTip.msg("提示", "提交数据过多,会导致打印失败,请分次提交!", true);
					return false;
				}
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件"); 
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				if(tag==1){//直接打印
				LODOP.PRINT();
				}else{
				LODOP.PREVIEW();
				}
//				var pWin = this.midiModules["DoctorAdviceSubmitPrintView"]
//				var cfg = {
//					al_zyh : al_zyh,
//					ldt_lyrq : ldt_lyrq,
//					fyfs : this.fyfs,
//					lsyz : this.lsyz,
//					yfsb : this.yfsb,
//					jlxhs:jlxhs
//				}
//				if (pWin) {
//					Ext.apply(pWin, cfg)
//					pWin.getWin().show()
//					return
//				}
//				pWin = new phis.prints.script.DoctorAdviceSubmitPrintView(cfg)
//				this.midiModules["DoctorAdviceSubmitPrintView"] = pWin
//				pWin.getWin().show()

			},
			doClose:function(){
			this.getWin().hide();
			}
		});