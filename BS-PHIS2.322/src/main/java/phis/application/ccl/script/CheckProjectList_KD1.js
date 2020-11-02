$package("phis.application.ccl.script");
$import("phis.script.SimpleList");

phis.application.ccl.script.CheckProjectList_KD1 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.ccl.script.CheckProjectList_KD1.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.ccl.script.CheckProjectList_KD1,
		phis.script.SimpleList, {
			loadData : function() {
				var checkPointList = this.opener.checkPointList;
				var lbid = checkPointList.getSelectedRecord().data.LBID;//类别ID
				var bwid = checkPointList.getSelectedRecord().data.BWID;//部位ID
				var xmpydm = "%"+Ext.get("xmpydm").getValue().toUpperCase()+"%";
				if(xmpydm.trim()==""){
					this.requestData.cnd = ['and',['eq', ['$', 'lbid'],
						['i', lbid]],['eq', ['$', 'bwid'],['i', bwid]]];
					this.requestData.pageNo = 1;
				}else{
					this.requestData.cnd =  ['and',['and',['eq', ['$', 'lbid'],
						['i', lbid]],['eq', ['$', 'bwid'],['i', bwid]]],['like', ['$', 'pydm'], ['s', xmpydm]]];
					this.requestData.pageNo = 1;
				}
				phis.application.ccl.script.CheckProjectList_KD1.superclass.loadData
						.call(this)
			},
			onDblClick : function() {
				var existFlag = false;//判定是否已加该项目到清单
				var twbgFlag = false;//图文报告flag,若已存在，则不加入费用到列表，写死控制
				var record = this.getSelectedRecord();
				var form = this.opener.opener.opener.midiModules["checkApplyForm"].form.getForm();
				var selectLbid = record.data.LBID;
				var selectBwid = record.data.BWID;
				var selectXmid = record.data.XMID;
				var selectXmmc = record.data.XMMC;
				var bz = record.data.BZ;
				if(bz!=""){
					form.findField("BZXX").setValue(bz);//备注信息
				}
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "checkApplyService",
							serviceAction : "getCheckApplyFeeDetailsInfo",
							body : {
								lbid : selectLbid,
								bwid : selectBwid,
								xmid : selectXmid,
								EMPIID : this.opener.opener.opener.exContext.empiData.empiId
							}
						});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				var list = res.json.list;
				if(list==null){
					Ext.Msg.alert("提示","该项目未维护绑定组套，请绑定后再使用");
					return;
				}
				//若已添加项目，则提示是否继续添加
				var sslx = this.opener.midiModules["checkTypeList"].getSelectedRecord().data.SSLX;
				var lbmc = this.opener.midiModules["checkTypeList"].getSelectedRecord().data.LBMC;
				var bwmc = this.opener.midiModules["checkPointList"].getSelectedRecord().data.BWMC;
				var feeDetailsList = this.opener.midiModules["feeDetailsList"];
				var store = feeDetailsList.grid.getStore();
				for(var i=0;i<store.getCount();i++){
					var lbid = store.getAt(i).get("LBID");
					var bwid = store.getAt(i).get("BWID");
					var xmid = store.getAt(i).get("XMID");
					var fyxh = store.getAt(i).get("FYXH");
					
					if(lbid==selectLbid&&bwid==selectBwid&&xmid==selectXmid){
						existFlag=true;
					}
					if(fyxh==177||fyxh==5997){//计算机图文报告只收一次，写死
						twbgFlag=true;
					}
				}
				if(!existFlag){
					for(var i=0;i<list.length;i++){
						if(twbgFlag&&(list[i].XMBH==177||list[i].XMBH==5997)){//计算机图文报告只收一次，写死
							continue;
						}
						var record = new Ext.data.Record();
						record.set("JYBS",list[i].JYBS);
						record.set("LBID",selectLbid);
						record.set("SSLX",sslx);
						record.set("BWID",selectBwid);
						record.set("XMID",selectXmid);
						record.set("XMMC",selectXmmc);
						record.set("FYXH",list[i].XMBH);
						record.set("FYDW",list[i].FYDW);
						record.set("FYMC",list[i].XMMC);
						record.set("FYSL",list[i].XMSL);
						record.set("FYDJ",list[i].FYDJ);
						record.set("FYZJ",list[i].FYZJ);
						record.set("LBMC",lbmc);
						record.set("BWMC",bwmc);
						store.add(record);
					}
				}else{
					Ext.Msg.show({
					title : '确认',
					msg : '已经添加该项目，并生成费用明细，是否继续添加？',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							for(var i=0;i<list.length;i++){
								if(twbgFlag&&(list[i].XMBH==177||list[i].XMBH==5997)){//计算机图文报告只收一次，写死
									continue;
								}
								var record = new Ext.data.Record();
								record.set("JYBS",list[i].JYBS);
								record.set("LBID",selectLbid);
								record.set("SSLX",sslx);
								record.set("BWID",selectBwid);
								record.set("XMID",selectXmid);
								record.set("XMMC",selectXmmc);
								record.set("FYXH",list[i].XMBH);
								record.set("FYDW",list[i].FYDW);
								record.set("FYMC",list[i].XMMC);
								record.set("FYSL",list[i].XMSL);
								record.set("FYDJ",list[i].FYDJ);
								record.set("FYZJ",list[i].FYZJ);
								record.set("LBMC",lbmc);
								record.set("BWMC",bwmc);
								store.add(record);
							}
						}
					},
					scope : this
					});
				}
				
				//var model = feeDetailsList.grid.getSelectionModel(); 
				//model.selectAll(); 
			}
		});