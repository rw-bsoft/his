/**
 * 个人基本信息综合模块
 * 
 * @author tianj
 */
$package("phis.application.reg.script");

$import("phis.script.SimpleDataView");

phis.application.reg.script.RegistrationPreflightManageModule = function(cfg) {
	cfg.listServiceId = "registeredManagementService";
	cfg.pageSize = 25
	cfg.disablePagingTbr = true;
	this.serverParams = {
		serviceAction : "querySchedulingDepartment"
	}
//	cfg.autoLoadData = false;
	phis.application.reg.script.RegistrationPreflightManageModule.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.reg.script.RegistrationPreflightManageModule,
		phis.script.SimpleDataView, {
			getTpl : function() {
				return new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="thumb-wrap" style="padding:10px;margin:10px;">',
//						'<div style="float:left;border:2px solid #dddddd;padding:5px;margin:5px;cursor:pointer;">',
						'<table style="width:120px;height:30px;table-layout:fixed;white-space:nowrap;overflow:hidden;">',
						'<tr><td style="font-size:22px;line-height:30px;height:30px;" title="{KSMC}">{KSMC:substr(0,8)}</td></tr>',
						'</table></div></tpl>',
						'<div class="x-clear"></div>');
			},
			getJZHM : function() {
				var this_ = this;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registeredManagementService",
					serviceAction : "updateDoctorNumbers"
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg,
							this.onBeforeSave);
					return;
				} else {
					if (!r.json.JZHM) {
						Ext.Msg.alert("提示", "请先维护就诊号码!", function() {
							this_.opener.closeCurrentTab();// 关闭收费模块
						});
						return;
					}
					this.ZBLB = r.json.ZBLB;
					this.GHSJ = r.json.GHSJ;
				}
			},
			onDataViewReady : function(){
				
			},
			storeLoadData : function() {
				this.getJZHM();
			},
			onClick : function(view, index, item, e) {
				var data = view.store.getAt(index).data;
				if (data.GHXE != 0
						&& data.GHXE <= (data.YGRS + data.YYRS)) {
					Ext.Msg.alert("提示", "该科室挂号人数已满，不能挂号预检!");
					return;
				}
				this.KSDM = data.KSDM;
				var module = this.midiModules["yslist"];
				if (!module) {
					module = this.createModule("yslist", "phis.application.reg.REG/REG/REG0103");
					module.width = 550;
					module.on("loadData", this.doctorloadData, this);
					module.on("doctorChoose", this.doctorChoose, this);
					module.KSDM = data.KSDM;
					module.requestData.cnd = ['eq',[ '$','a.KSDM' ],[ 'i', data.KSDM]];
					this.midiModules["yslist"] = module;
					module.opener = this;
					var sm = module.initPanel();
					var win = module.getWin();
					module.loadData();
					win.add(sm);
				} else {
					module.KSDM = data.KSDM;
					module.requestData.cnd = ['eq',[ '$','a.KSDM' ],[ 'i', data.KSDM]];
					module.loadData();
				}
				module.cndField.setValue();
			},
			doctorloadData : function(store){
				if(store.getCount()>0){
					var win = this.midiModules["yslist"].getWin();
					win.show();
				}else{
					this.YSDM = '';
					Ext.Msg.show({
						title : '请输入卡号:',
//						msg : '请输入卡号:',
						modal : true,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						prompt : true,
						fn : function(btn, cardNo) {
							if (btn == "ok") {
								this.ghyj(cardNo);
							}
						},
						scope : this
					});
				}
			},
			doctorChoose : function(record){
				if (record.data.GHXE != 0
						&& record.data.GHXE <= (record.data.YGRS + record.data.YYRS)) {
					Ext.Msg.alert("提示", "该医生挂号人数已满，不能挂号预检!");
					return;
				}
				this.YSDM = record.data.YSDM;
				Ext.Msg.show({
					title : '请输入卡号:',
//					msg : '请输入卡号:',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					prompt : true,
					fn : function(btn, cardNo) {
						if (btn == "ok") {
							this.ghyj(cardNo);
						}
					},
					scope : this
				});
			},
			ghyj : function(cardNo){
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : "clinicChargesProcessingService",
					serviceAction : "queryPerson",
					JZKH : cardNo
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return;
				} else {
					if (!r.json.body) {
						Ext.Msg.alert("提示", "该卡号不存在!");
						return;
					}
				}
				var body = {};
				body.BRID = r.json.body.BRID;
				body.KSDM = this.KSDM;
				body.YSDM = "";
				if(this.YSDM){
					body.YSDM = this.YSDM;
				}
				body.ZBLB = this.ZBLB;
				body.GHSJ = this.GHSJ;
				var reg = phis.script.rmi.miniJsonRequestSync({
					serviceId : "registeredManagementService",
					serviceAction : "saveGHYJ",
					body : body
				});
				if (reg.code > 300) {
					this.processReturnMsg(reg.code, reg.msg,
							this.onBeforeSave);
					return;
				}
				var win = this.midiModules["yslist"].getWin();
				win.hide();
				this.refresh();
				Ext.Msg.alert("提示", "预检成功!");
			},
			onDblClick : function(view, index, item, e) {
				// 打开医嘱处理
//				this.fireEvent("mydblClick");
			},
			refresh : function() {
				this.panel.el.mask("正在加载数据...");
				var pt = this.pagingToolbar;
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor);
				this.panel.el.unmask();
			}
		});