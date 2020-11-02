$package("phis.application.war.script");

$import("phis.script.SimpleModule");

phis.application.war.script.DoctorAdviceExecuteModule_XM = function(cfg) {
	this.exContext = {};
	cfg.mutiSelect = true;
	phis.application.war.script.DoctorAdviceExecuteModule_XM.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.war.script.DoctorAdviceExecuteModule_XM,
		phis.script.SimpleModule, {
			initPanel : function() {
				// 判断是否有病区
				if (!this.mainApp['phis'].wardId) {
					MyMessageTip.msg("提示", "当前不存在病区，请先选择病区信息!", true);
					return;
				}
				if (this.panel)
					return this.panel;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							height : 500,
							items : [{
										title : "病人信息",
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 270,
										items : this.getXmList()
									}, {
										title : "医嘱明细",
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getDetailsList()
									}],
							tbar : this.createButtons()
						});
				this.panel = panel;
				return panel;
			},
			getXmList : function() {
				this.xmList = this.createModule("xmList",
						this.refLeft);
				this.xmList.on("selectRecord", this.onSelectRecord, this);
				return this.xmList.initPanel();
			},
			onSelectRecord : function(patientGrid, rowIndex, e) {
				this.detailsList.clearSelect();
				this.detailsList.clear();
				var records=this.xmList.getSelectedRecords();
				var length=records.length;
				if(length==0){
				return;}
				var xmxhs=new Array();
				for(var i=0;i<length;i++){
				var r=records[i];
				xmxhs.push(r.get("YPXH"));
				}
				this.panel.el.mask("正在查询数据...","x-mask-loading")
				var resData = phis.script.rmi.miniJsonRequestSync({
							serviceId : "doctorAdviceExecuteService",
							serviceAction : "queryFHSFXM",
							xmxhs:xmxhs
						});
				var code = resData.code;
				var msg = resData.msg;
				var json = resData.json;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					this.panel.el.unmask();
					return false;
				}
				if (json.body == 1) {
					if (json.count > 0) {
						MyMessageTip.msg("提示", "发现未复核医嘱，请复核!", true);
					}
				}
				this.detailsList.XMXHS=xmxhs;
				this.detailsList.loadData(0);
			},
			onAfterLoadData:function(){
			this.panel.el.unmask()
			},
			getDetailsList : function() {
				this.detailsList = this.createModule("rList",
						this.refRight);
				this.detailsList.on("afterLoadData",this.onAfterLoadData,this)
				this.detailsList.mutiSelect = true;
				return this.detailsList.initPanel();
			},
			// 刷新
			doRefresh : function() {
				this.detailsList.clear();
				this.xmList.clear();
				this.xmList.loadData();
			},
			// 确认
			doConfirm : function() {
				var records = this.detailsList.getSelectedRecords();
				if (records.length == 0) {
					Ext.Msg.alert("提示", "未选中任何记录");
					return;
				}
				var body = {};
				body['bq'] = this.mainApp['phis'].wardId;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configLogisticsInventoryControlService",
							serviceAction : "verificationWPJFBZ",
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doConfirm);
					return;
				}
				var bodys = [];
				for (var i = 0; i < records.length; i++) {
					var body = {};
					// body["FYRQ"] = records[i].json.QRSJ; // 费用日期
					body["FYMC"] = records[i].json.YZMC; // 费用名称
					body["FYSL"] = records[i].json.YCSL * records[i].json.FYCS;// 费用数量
					body["FYDJ"] = records[i].json.YPDJ;// 费用单价
					body["ZJJE"] = records[i].json.FYCS * records[i].json.YPDJ
							* records[i].json.YCSL;// 总计金额
					body["YSGH"] = records[i].json.YSGH;// 医生工号
					body["SRGH"] = records[i].json.CZGH;// 输入工号
					body["FYKS"] = records[i].json.BRKS;// 费用
					body["JLXH"] = records[i].json.JLXH;// 医嘱序号
					body["ZYH"] = records[i].json.ZYH;// 住院号
					body["FYXH"] = records[i].json.YPXH;// 费用序号
					body["YPCD"] = records[i].json.YPCD;// 药品产地
					body["XMLX"] = records[i].json.XMLX;// 项目类型
					body["YPLX"] = records[i].json.YPLX;// 药品类型
					body["BRXZ"] = records[i].json.BRXZ;// 病人性质
					body["ZXKS"] = records[i].json.ZXKS;// 执行科室
					body["YZXH"] = records[i].json.YZXH;// 医嘱序号
					bodys[i] = body;
				}
				this.panel.el.mask("正在确认数据,时间可能很长,请耐心等待...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionqr,
							body : bodys
						});
				this.panel.el.unmask();
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				// 执行完操作 去掉钩钩。
				this.detailsList.clearSelect();
				this.onSelectRecord();
				this.doRefresh();
				if(ret.json.RES_MESSAGE){
					MyMessageTip.msg("提示", ret.json.RES_MESSAGE, true);
				}else{
					MyMessageTip.msg("提示", "确认成功", true);
				}
			},
			// 关闭
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			afterOpen:function(){
			this.doRefresh();
			}
		});
