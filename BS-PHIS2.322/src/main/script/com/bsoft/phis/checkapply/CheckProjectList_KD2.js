$package("com.bsoft.phis.checkapply");
$import("com.bsoft.phis.SimpleList");

com.bsoft.phis.checkapply.CheckProjectList_KD2 = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	com.bsoft.phis.checkapply.CheckProjectList_KD2.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(com.bsoft.phis.checkapply.CheckProjectList_KD2,
		com.bsoft.phis.SimpleList, {
			loadData : function() {
				var checkPointList = this.opener.checkPointList;
				var lbid = checkPointList.getSelectedRecord().data.LBID;//���ID
				var bwid = checkPointList.getSelectedRecord().data.BWID;//��λID
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
				com.bsoft.phis.checkapply.CheckProjectList_KD2.superclass.loadData
						.call(this)
			},
			onRowClick : function() {
				var existFlag = false;//�ж��Ƿ��ѼӸ���Ŀ���嵥
				var twbgFlag = false;//ͼ�ı���flag,���Ѵ��ڣ��򲻼�����õ��б?д������
				var record = this.getSelectedRecord();
				var selectLbid = record.data.LBID;
				var selectBwid = record.data.BWID;
				var selectXmid = record.data.XMID;
				var selectXmmc = record.data.XMMC;
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "checkApplyService",
							serviceAction : "getCheckApplyFeeDetailsInfo",
							body : {
								lbid : selectLbid,
								bwid : selectBwid,
								xmid : selectXmid
							}
						});
				if (res.code >= 300) {
					this.processReturnMsg(res.code, res.msg);
					return;
				}
				var list = res.json.list;
				if(list==null){
					Ext.Msg.alert("��ʾ","����Ŀδά�������ף���󶨺���ʹ��");
					return;
				}
				//���������Ŀ������ʾ�Ƿ�������
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
					if(fyxh==177||fyxh==5997){//�����ͼ�ı���ֻ��һ�Σ�д��
						twbgFlag=true;
					}
				}
				if(!existFlag){
					for(var i=0;i<list.length;i++){
						if(twbgFlag&&(list[i].XMBH==177||list[i].XMBH==5997)){//�����ͼ�ı���ֻ��һ�Σ�д��
							continue;
						}
						var record = new Ext.data.Record();
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
					title : 'ȷ��',
					msg : '�Ѿ���Ӹ���Ŀ������ɷ�����ϸ���Ƿ������ӣ�',
					modal : false,
					width : 300,
					buttons : Ext.MessageBox.YESNO,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "yes") {
							for(var i=0;i<list.length;i++){
								if(twbgFlag&&(list[i].XMBH==177||list[i].XMBH==5997)){//�����ͼ�ı���ֻ��һ�Σ�д��
									continue;
								}
								var record = new Ext.data.Record();
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
			}
		});