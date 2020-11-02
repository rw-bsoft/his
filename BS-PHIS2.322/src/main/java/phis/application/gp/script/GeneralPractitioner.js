/**
 *家庭医生公共方法
 *@author: fengld
 *@email:fengld@bsoft.com.cn
 */
$package("phis.application.gp.script")
$import("util.schema.SchemaLoader", "phis.script.widgets.MyMessageTip",
    "phis.script.util.DateUtil", "phis.script.rmi.jsonRequest",
    "phis.script.rmi.miniJsonRequestSync",
    "phis.script.rmi.miniJsonRequestAsync", "phis.script.widgets.ymPrompt",
    "phis.script.widgets.PrintWin");
phis.application.gp.script.GeneralPractitioner = {
    //判断是否家医病人
    isGP:function(brid,data){
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "phis.GeneralPractitionersService",
            serviceAction: "queryIsGP",
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

    logOnGP:function(brid , sbxh){
        var result = phis.script.rmi.miniJsonRequestSync({
            serviceId: "phis.GeneralPractitionersService",
            serviceAction: "logOnGP",
            body: {"brid":brid,"SBXH":sbxh}
        });
    },
    //显示家医减免清单
    showJYModule : function(data){
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
    },

    updateServiceTimes :function(data){
        var info = new Array();
        for(var i = 0 ; i < data.length ; i++){
        	if(data[i].data.FPHM==undefined){
        		continue;
        	}
            info[info.length] = data[i].data;
        }
        if(info.length>0){
	        var res = phis.script.rmi.miniJsonRequestSync({
	            serviceId: "phis.GeneralPractitionersService",
	            serviceAction: "updateServiceTimes",
	            body: info
	        });
	        var code = res.code;
	        var msg = res.msg;
	        if (code >= 300) {
	            MyMessageTip.msg("提示", msg, true);
	        } else {
	            if (msg && msg.length>0) {
	            	MyMessageTip.msg("提示", msg, true);
	            }
	        }
        }
    },

    getGpParms : function(){
        //获取家医相关参数
        var res = phis.script.rmi.miniJsonRequestSync({
            serviceId: "publicService",
            serviceAction: "loadSystemParams",
            body: {
                // 私有参数
                privates: ['SFQYJYQY', 'JYFY', 'JYKS']
            }
        });
        var code = res.code;
        var msg = res.msg;
        if (code >= 300) {
            this.processReturnMsg(code, msg);
            return;
        } else {
            if (res.json.body) {
                res.json.body.SFQYJYQY ? this.SFQYJYQY = res.json.body.SFQYJYQY : {};
                this.SFQYJYQY && res.json.body.JYFY ? this.JYFY = res.json.body.JYFY : MyMessageTip.msg("提示", "请先维护家医对应费用参数", true)
                this.SFQYJYQY && res.json.body.JYKS ? this.JYKS = res.json.body.JYKS : MyMessageTip.msg("提示", "请先维护家医收费科室参数", true)
            }
        }
        return res.json.body;
    },
    /**
     * 根据系统参数返回家医病人性质，判断类型
     * @returns {*}
     */
    getGpBRXZ: function () {
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "queryGpbrxz"
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    getGpXTCS: function(empiid){
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "phis.GeneralPractitionersService",
            serviceAction: "queryGpxtcs",
            body: empiid
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }else{
            this.SFQYJYQY = ret.json.body.SFQYJYQY;
            this.JYFY = ret.json.body.JYFY;
            this.JYKS = ret.json.body.JYKS;
        }
        return ret.json.body;
    },
    getSCIDWithIdcard: function (idcard) {
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "getSCID",
            body: {Idcard: idcard}
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    getSCIDWithEmpiId: function (empiId) {
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "getSCID",
            body: {EmpiId: empiId}
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    /**
     * 家医门诊结算
     */
    doGpMzjs: function (mzjsInfo) {
        //todo
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "gpLogon",
            body: mzjsInfo
        })
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    /**
     * 家医门诊退费
     */
    doGpMztf: function (mzjsInfo) {
        //todo
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "gpLogoff",
            body: mzjsInfo
        })
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    /**
     * 获取家医签约明细
     * @param mpi
     * @returns {*}
     */
    getGpDetail: function (SCID) {
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "queryGpDetail",
            body: SCID
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    /**
     * 更改家医签约激活状态
     * @param SCID
     * @returns {*}
     */
    changeGpStatus: function (SCID) {
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "updateGpStatus",
            body: SCID
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },
    /**
     * 更改家医收费项目打折剩余次数，此处对sererDetails进行约定
     * {detail1:-1,
     *  detail2:-1,
     *  detail3:+2}负数减次数，正数加次数
     * @param serverDetails
     * @returns {*}
     */
    changeGpServerNums: function (serverDetails) {
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "updateGpServerNums",
            body: serverDetails
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;

    },
    /**
     * 此处用于读卡
     * @returns {*}
     */
    gpDk: function () {
        alert("家医读卡，请自行实现");
    },

    /**
     * 保存增值服务
     * @param items
     * @returns {*}
     */
    saveGPService: function(items){
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "saveGpService",
            body: items
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    },

    /**
     * 注销增值服务项
     * @param items
     * @returns {*}
     */
    logoffGPServiceItems: function(items){
        var ret = util.rmi.miniJsonRequestSync({
            serviceId: "generalPractitionersService",
            serviceAction: "logoffGpServiceItems",
            body: items
        });
        if (ret.code > 300) {
            this.processReturnMsg(ret.code, ret.msg);
        }
        return ret.json.body;
    }
}
;