$package("phis.application.sto.script")
$import("phis.application.sto.script.StorehouseMySimpleRightList");
phis.application.sto.script.StorehouseCheckOutList = function(cfg) {
	cfg.cnds = ['eq', ['$', 'CKPB'], ['i', 1]];
	phis.application.sto.script.StorehouseCheckOutList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.sto.script.StorehouseCheckOutList, phis.application.sto.script.StorehouseMySimpleRightList,
		{
			getQueryCnd : function(tag,dates) {
				if(tag==1){
				return ['and',['ge',['$', "str(CKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[0]]],
							  ['le',['$', "str(CKRQ,'yyyy-mm-dd hh24:mi:ss')"],
									['s', dates[1]]]];
				}else{
					return ['eq', ['$', 'a.CKFS'], ['i', this.selectValue]]
				}
			}
			,
			getInitDataBody:function(r){
				var initDataBody = {};
				initDataBody["xtsb"] = r.data.XTSB;
				initDataBody["ckfs"] = r.data.CKFS;
				initDataBody["ckdh"] = r.data.CKDH;
				return initDataBody;
			},		
			getReadCondition:function(){
			return {"ksly":this.ksly};
			},
			doExport:function() {
				var r = this.getSelectedRecord()
                if (r == null) {
                    MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
                    return;
                }	
				var pages="phis.prints.jrxml.StorehouseOut";
				var url="resources/"+pages+".print?silentPrint=3&type=3";
				var ckks=r.data.CKKS
				if(!ckks){
                    ckks = -1;
                }
		       	url += "&temp=" + new Date().getTime() + "&yfsb=" + r.data.YFSB
				+ "&ckfs=" + r.data.CKFS + "&ckdh=" + r.data.CKDH + "&ckks=" + ckks
		        var printWin = window.open(url,"","height="+(screen.height-100)+", width="+(screen.width-10)+", top=0, left=0, toolbar=no, menubar=yes, scrollbars=yes, resizable=yes,location=no, status=no")
				return printWin;
			},
			doUpload : function() {
			var url = "http://10.2.202.21:8280/services/upmedicalbusiness"
				var payapi = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.SptService",
							serviceAction : "upLoadphysicalDetails",
							body : {
								url : url
							}
						});
						var json=payapi.json;
						var body=json.body;
				if 	(payapi.code !=200){
					MyMessageTip.msg("提示",payapi.msg , true);
					return;
				}else if (body.code!=200){
					MyMessageTip.msg("提示",body.msg , true);
					return;
				}else {
					MyMessageTip.msg("提示", "出库信息上传成功!", true);
				}
				this.refresh();
			},
            doPrint : function() {
                var module = this.createModule("storehouseoutprint",
                        this.refStorehouseListPrint)
                var r = this.getSelectedRecord()
                if (r == null) {
                    MyMessageTip.msg("提示", "打印失败：无效的入库单信息!", true);
                    return;
                }
                module.yfsb = r.data.YFSB;
                module.ckfs = r.data.CKFS;
                module.ckdh = r.data.CKDH;
                if(r.data.CKKS){
                    module.ckks = r.data.CKKS;
                }else {
                    module.ckks = -1;
                }
                module.initPanel();
                module.doPrint();
            }
		})