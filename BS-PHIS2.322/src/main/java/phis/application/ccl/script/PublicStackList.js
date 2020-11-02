
$package("phis.application.ccl.script");

$import("phis.script.SimpleList")

phis.application.ccl.script.PublicStackList = function(cfg) {
	phis.application.ccl.script.PublicStackList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.ccl.script.PublicStackList,
		phis.script.SimpleList, {
			loadData : function() {
				var tcpydm = "%"+Ext.get("tcpydm").getValue().toUpperCase()+"%";
				if(tcpydm.trim()==""){
					this.requestData.cnd = 	['and',['and',['and', ['and', ['and', ['eq', ['$', 'ZTLB'], ['i', 4]], ['eq', ['$', 'SSLB'], ['i', 2]]], ['eq', ['$', 'SSLB'], ['i', 2]]],['eq', ['$', 'KSDM'], ['s', '9999999999']]],
					['eq', ['$', 'JGID'], ['s', this.mainApp['phis'].phisApp.deptId]]], ['eq', ['$', 'SFQY'], ['i', 1]]]
					this.requestData.pageNo = 1;
				}else{
					this.requestData.cnd = 	['and',['and',['and', ['and', ['and', ['eq', ['$', 'ZTLB'], ['i', 4]], ['eq', ['$', 'SSLB'], ['i', 2]]],['eq', ['$', 'KSDM'], ['s', '9999999999']]],
					['eq', ['$', 'JGID'], ['s', this.mainApp['phis'].phisApp.deptId]]], ['eq', ['$', 'SFQY'], ['i', 1]]],['like', ['$', 'PYDM'], ['s', tcpydm]]]
					this.requestData.pageNo = 1;
				}
				phis.application.ccl.script.PublicStackList.superclass.loadData
						.call(this)
			},
			onDblClick : function(){
				var ztbh = this.getSelectedRecord().data.ZTBH;
				var module = this.createModule("publicStackDetailsModule",this.refPublicStackDetailsModule);
				module.ZTBH = ztbh;//传递组套编号
				if (module) {
					module.opener = this;
					var win = module.getWin();
					win.setWidth(400);
					win.setHeight(300);
					win.show();
					module.init();
				}
			},
			doAdd : function(){
				//获得检查项目列表中选中的信息
				var checkList = this.opener.midiModules["checkList"];
				var selectCheckRecord = checkList.getSelectedRecord();
				var selectLbid = selectCheckRecord.data.LBID;
				var selectLbmc = selectCheckRecord.data.LBMC;
				var selectBwid = selectCheckRecord.data.BWID;
				var selectBwmc = selectCheckRecord.data.BWMC;
				var selectXmid = selectCheckRecord.data.XMID;
				var selectXmmc = selectCheckRecord.data.XMMC;
				//获得套餐列表中选中的信息
				var jgid = this.mainApp.deptId;
				var selectZtbh = this.getSelectedRecord().data.ZTBH;
				var selectZtmc = this.getSelectedRecord().data.ZTMC;
				//获得项目与费用绑定的信息
				var feeBoundList = this.opener.midiModules["feeBoundList"];
				var store = feeBoundList.grid.getStore();// 已经存在的费用绑定
				
				for(var i=0;i<store.getCount();i++){
					var lbid = store.getAt(i).get("LBID");
					var bwid = store.getAt(i).get("BWID");
					var xmid = store.getAt(i).get("XMID");
					if(lbid==selectLbid&&bwid==selectBwid&&xmid==selectXmid){
						Ext.Msg.alert("提示", "选中检查项目绑定信息已存在,添加失败");
						return
					}
				}
				var record = new Ext.data.Record();
				record.set("JGID",jgid);
				record.set("LBID",selectLbid);
				record.set("BWID",selectBwid);
				record.set("XMID",selectXmid);
				record.set("LBMC",selectLbmc);
				record.set("BWMC",selectBwmc);
				record.set("XMMC",selectXmmc);
				record.set("ZTBH",selectZtbh);
				record.set("ZTMC",selectZtmc);
				store.add(record);
			}
			
		});