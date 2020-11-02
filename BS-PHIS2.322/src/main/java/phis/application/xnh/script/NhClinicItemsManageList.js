$package("phis.application.xnh.script")

$import("phis.script.SimpleList", "phis.script.widgets.Strategy",
		"org.ext.ux.ColumnHeaderGroup")

phis.application.xnh.script.NhClinicItemsManageList = function(cfg) {
	cfg.winState = "center";// cfg.winState=[100,50]两个写法都可以
	this.showButtonOnTop = true;
	phis.application.xnh.script.NhClinicItemsManageList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.xnh.script.NhClinicItemsManageList,
		phis.script.SimpleList, {
			doDownloads:function(){
				this.mask("正在努力下载中，可能需要很长时间，你可以喝杯茶和工程师聊聊天！");
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
				body.deptId=this.mainApp.deptId;
				body.operator=this.mainApp.uid;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.xnhService",
							serviceAction : "savedownloadzlml",
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				this.unmask();
			}
		})