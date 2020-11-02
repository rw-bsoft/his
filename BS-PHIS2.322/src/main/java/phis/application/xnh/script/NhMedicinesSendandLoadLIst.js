$package("phis.application.xnh.script");

$import("phis.script.SimpleList");

phis.application.xnh.script.NhMedicinesSendandLoadLIst = function(cfg) {
	this.exContext = {};
	phis.application.xnh.script.NhMedicinesSendandLoadLIst.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhMedicinesSendandLoadLIst,
		phis.script.SimpleList, {
			doSendmatch :function (){
				var r=this.getSelectedRecord();
				if(!r){
					MyMessageTip.msg("提示", "请选择一条记录", true);
					return;
				}
				var body={};
				this.mask("*****回首向来萧瑟处，归去，也无风雨也无晴*****");
				body.dgdzxx=r.data;
				body.type="1";
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "senddzxx",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				this.unmask();
			},
			doSendmatchall:function (){
				this.mask("*****请耐心等待*****");
				var dicName = {
            		 id : "phis.dictionary.Hcnqzj"
          		    };
				var qzj=util.dictionary.DictionaryLoader.load(dicName);
				alert(this.mainApp.deptId);
				var di = qzj.wraper[this.mainApp.deptId];
				var ipandport=""
				if (di) {
					ipandport = di.text;
				}
				alert(di);
				alert(1);
				alert(ipandport);
				if(ipandport==""){
					alert("未找到当前操作员工所在的医院的农合前置机配置！")
				}
				
				var body={};
				body.ipandport=ipandport;
				body.type="1";
				body.deptId=this.mainApp.deptId;
				body.operator=this.mainApp.uid;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "senddzxx",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				this.unmask();
			},
			doLoadmatch:function (){
				var r=this.getSelectedRecord();
				if(!r){
					MyMessageTip.msg("提示", "请选择一条记录", true);
					return;
				}
				this.mask("*****请耐心等待*****");
				var body={};
				body.type="1";
				body.dgypxz=r.data;//单个药品下载
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "loaddzxx",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				this.unmask();
			},
			doLoadmatchall:function (){
				this.mask("*****请耐心等待*****");
				var body={};
				body.type="1";
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "loaddzxx",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				this.unmask();
			}
		});
