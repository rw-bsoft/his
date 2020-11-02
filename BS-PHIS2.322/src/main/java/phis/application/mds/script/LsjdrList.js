$package("phis.application.mds.script")

$import("phis.script.SelectList")

phis.application.mds.script.LsjdrList = function (cfg) {
    cfg.autoLoadData = false;
    cfg.modal = true;
    phis.application.mds.script.LsjdrList.superclass.constructor
        .apply(this, [cfg])
}
Ext.extend(phis.application.mds.script.LsjdrList,
    phis.script.SelectList, {
        //加载数据
        loadData: function () {
            var cpmc = this.cpmcText.getValue();
            var gg = this.ggText.getValue();
            var scqymc = this.scqymcText.getValue();
            var cnd = ['and', ['like', ['$', 'CPMC'], ['s', cpmc]], ['like', ['$', 'SCQYMC'], ['s', scqymc]], ['like', ['$', 'GG'], ['s', gg]]];
            this.requestData.cnd = cnd;
            phis.application.mds.script.LsjdrList.superclass.loadData.call(this);
        },
        //查询框
        getCndBar: function (items) {
            var cpmcLab = new Ext.form.Label({text: "产品名称"});
            this.cpmcText = new Ext.form.TextField({name: 'CPMC'});
            var ggLab = new Ext.form.Label({text: "规格"})
            this.ggText = new Ext.form.TextField({name: 'GG'});
            var scqymcLab = new Ext.form.Label({text: "生产厂家"})
            this.scqymcText = new Ext.form.TextField({name: 'SCQYMC'});
            var btn = new Ext.Button({
                text: '查询',
                handler: this.loadData,
                scope: this,
                iconCls: "vcard_edit"
            });
            var btn_qr = new Ext.Button({
                text: '引入',
                handler: this.doSave,
                scope: this,
                iconCls: "create"
            });
            return [cpmcLab, '-', this.cpmcText, '-', ggLab, '-', this.ggText, '-', scqymcLab, '-', this.scqymcText, '-', btn, '-', btn_qr];
        },
        //确认调入
        doSave: function () {
            var r = this.grid.getSelectionModel().getSelections();
            if (!r || r.length == 0) {
                MyMessageTip.msg("提示", "没有选中的记录", true);
                return;
            }
            this.mask("正在调入...");
            var ypxxList = [];
            for(var i=0 ; i<r.length ; i++ ){
                var ypxx = {};
                var body = {};
                body.codeType = ['py', 'wb', 'jx', 'bh'];
                body.value = r[i].data.CPMC;
                var ret = phis.script.rmi.miniJsonRequestSync({
                    serviceId: "codeGeneratorService",
                    serviceAction: "getCode",
                    body: body
                });
                if (ret.code > 300) {
                    this.unmask();
                    this.processReturnMsg(ret.code, ret.msg, this.onChange);
                    return;
                }
                //药品基本信息
                ypxx["PYDM"] = ret.json.body.py;
                ypxx["WBDM"] = ret.json.body.wb;
                ypxx["JXDM"] = ret.json.body.jx;
                ypxx["BHDM"] = ret.json.body.bh;
                ypxx["QTDM"] = '';
                ypxx["YPMC"] = r[i].data.CPMC;
                ypxx['TYMC'] = r[i].data.JYTYM2018;
                ypxx['YPGG'] = r[i].data.GG;
                ypxx["YPDW"] = r[i].data.ZXBZDW;
                ypxx["ZXDW"] = r[i].data.ZXZJDW;
                if (r[i].data.DL == '生物制剂') {
                    ypxx["TYPE"] = 1;
                    ypxx["ZBLB"] = 2;
                } else if (r[i].data.DL == '化学药') {
                    ypxx["TYPE"] = 1;
                    ypxx["ZBLB"] = 2;
                } else if (r[i].data.DL == '中成药') {
                    ypxx["TYPE"] = 2
                    ypxx["ZBLB"] = 3;
                }
                ypxx["YPSX"] = r[i].data.JXFLM;
                ypxx["YPDC"] = 1;
                ypxx["ABC"] = 'C';
                ypxx["PSPB"] = 0;
                ypxx["JYLX"] = 1;
                ypxx["YYBS"] = 0;
                ypxx["GMYWLB"] = 0;
                ypxx['YFGG'] = ypxx['YPGG'];
                ypxx['YFDW'] = ypxx['YPDW'];
                ypxx['ZXBZ'] = r[i].data.ZHXS;
                ypxx['YFBZ'] = ypxx['ZXBZ'];
                ypxx['BFGG'] = ypxx['YPGG'];
                ypxx['BFDW'] = ypxx['ZXDW'];
                ypxx['BFBZ'] = 1;
                ypxx['FYFS'] = 1;
                ypxx['GYFF'] = 1;
                ypxx['TSYP'] = 0;
                ypxx['YPZC'] = 1;
                ypxx['CFYP'] = 1;
                ypxx['QZCL'] = 0;
                ypxx['KSBZ'] = 0;
                ypxx['YQSYFS'] = 2;
                ypxx['SFSP'] = 2;
                ypxx["ZSSF"] = 0;
                ypxx["ZFYP"] = 0;
                ypxx["XZSJ"] = Date.getServerDateTime();
                ypxx["ZFPB"] = 0;
                ypxx["YPID"] = r[i].data.YPID;
                ypxx["YBFL"] = 1;
                ypxx["JBYWBZ"] = 1;
                //老数据不包含需要手动填入的字段
                ypxx["YPJL"] = 1;
                ypxx["JLDW"] = '';
                ypxx["YCJL"] = 1;
                //别名
                ypxx["mdsaliastab"] = [];
                var ypbm = {
                    '_opStatus': 'create'
                };
                ypbm["YPXH"] = null;
                ypbm["YPMC"] = ypxx["YPMC"];
                ypbm["PYDM"] = ypxx["PYDM"];
                ypbm["WBDM"] = ypxx["WBDM"];
                ypbm["JXDM"] = ypxx["JXDM"];
                ypbm["BHDM"] = ypxx["BHDM"];
                ypbm["QTDM"] = ypxx["QTDM"];
                ypbm["BMFL"] = 1;
                ypxx["mdsaliastab"].push(ypbm);
                //产地信息
                ypxx["mdspricetab"] = [];
                var ypcd = {
                    '_opStatus': 'create'
                }
                ypcd["YPCD"] = r[i].data.YPCD;
                ypcd["JHJG"] = 0.01;
                ypcd["LSJG"] = 0.01;
                ypcd["PZWH"] = r[i].data.PZWH;
                ypcd["GMP"] = 1;
                ypcd["DJFS"] = 0;
                ypcd["ZFPB"] = 0;
                ypcd["JHJE"] = 0;
                ypcd["PFJE"] = 0;
                ypcd["LSJE"] = 0;
                ypcd["KCSL"] = 0;
                ypcd["YPID"] = r[i].data.YPID;
                ypxx["mdspricetab"].push(ypcd);
                ypxxList.push(ypxx);
            }

            var ret = phis.script.rmi.miniJsonRequestSync({
                serviceId: "medicinesManageService",
                serviceAction: "saveZslypsjdr",
                body: ypxxList
            });
            this.unmask();
            if (ret.code > 200) {
                this.processReturnMsg(ret.code, ret.msg);
                return;
            }
            this.getWin().hide();
            MyMessageTip.msg("提示","药品调入成功",true);
            // this.fireEvent("qrdr", ret.json.YPXH);
        }
    })