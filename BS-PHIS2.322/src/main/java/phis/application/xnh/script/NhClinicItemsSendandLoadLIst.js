$package("phis.application.xnh.script");

$import("phis.script.SimpleList");

phis.application.xnh.script.NhClinicItemsSendandLoadLIst = function(cfg) {
	this.exContext = {};
	phis.application.xnh.script.NhClinicItemsSendandLoadLIst.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.xnh.script.NhClinicItemsSendandLoadLIst,
		phis.script.SimpleList, {
			doSendmatch:function (){
				this.mask("*****请耐心等待*****");
				var dicName = {
            		 id : "phis.dictionary.Hcnqzj"
          		    };
				var qzj=util.dictionary.DictionaryLoader.load(dicName);
				var di = qzj.wraper[this.mainApp.deptId];
				var ipandport=""
				if (di) {
					ipandport = di.text;
				}
				if(ipandport==""){
					alert("未找到当前操作员工所在的医院的农合前置机配置！")
				}
				
				var body={};
				body.ipandport=ipandport;
				body.type="2";//诊疗
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
				body.type="2";//诊疗
				body.dgzlxz=r.data;//单个诊疗下载
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
				this.mask("*****陪伴是最长情的告白*****");
				var body={};
				body.type="2";//诊疗
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
