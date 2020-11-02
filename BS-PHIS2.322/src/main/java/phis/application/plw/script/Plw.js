/**
 *家庭医生公共方法
 *@author: fengld
 *@email:fengld@bsoft.com.cn
 */
$package("phis.application.plw.script")
$import("util.schema.SchemaLoader", "phis.script.widgets.MyMessageTip",
    "phis.script.util.DateUtil", "phis.script.rmi.jsonRequest",
    "phis.script.rmi.miniJsonRequestSync",
    "phis.script.rmi.miniJsonRequestAsync", "phis.script.widgets.ymPrompt",
    "phis.script.widgets.PrintWin");

phis.application.plw.script.Plw = {
    //判断是否家医病人
    isPLW:function(brid,data){
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "phis.PlwsService",
            serviceAction: "queryIsPWL",
            body: {brid:brid,EMPIID:this.MZXX.EMPIID,data:data}
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }else{
/*            this.SFQYJYQY = ret.json.body.SFQYJYQY;
            this.JYFY = ret.json.body.JYFY;
            this.JYKS = ret.json.body.JYKS;*/
        }
        return ret.json.body.ISGP == 1;
    },

    //显示家医减免清单
    showPlwModule : function(data){
        for(var i=0 ; i < data.length ; i++){
            data[i].ZFBL = 1;
        }
        this.data_bak = data ;
        var module = this.midiModules["refJYModule"];
        if (!module) {
            module = this.createModule("refJYModule", this.refJYModule);
            this.midiModules["refJYModule"] = module;

            module.opener = this
            module.EMPIID = this.MZXX.EMPIID
            module.GPData = data ;
            module.requestData.body = {
                "EMPIID" : module.EMPIID,
                "data" : module.GPData
            };
            module.on("gpDone", this.doPopJsWin, this);
        }else{
            module.requestData.body = {
                "EMPIID" : this.MZXX.EMPIID,
                "data" : data
            };
        }
        var win = module.getWin();
        win.add(module.initPanel());
        win.setWidth(700);
        win.setHeight(450);
        win.on("winShow",win.loadData,this);
        win.show();
    }
}
;