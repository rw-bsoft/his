$package("phis.application.cfg.script");
$import("phis.script.SelectList")

phis.application.cfg.script.LsjdrList = function (cfg) {
    cfg.autoLoadData = false;
    cfg.modal = true;
    phis.application.cfg.script.LsjdrList.superclass.constructor
        .apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.LsjdrList,
    phis.script.SelectList, {
        //加载数据
        loadData: function () {
            var ssmc = this.ssmcText.getValue();
            var pym = this.pymText.getValue();
            var cnd = ['and', ['like', ['$', 'SSMC'], ['s', ssmc]], ['like', ['$', 'ZJDM'], ['s', pym]]];
            this.requestData.cnd = cnd;
            phis.application.cfg.script.LsjdrList.superclass.loadData.call(this);
        },
        //查询框
        getCndBar: function (items) {
            var ssmcLab = new Ext.form.Label({text: "手术名称"});
            this.ssmcText = new Ext.form.TextField({name: 'SSMC'});
            var pymLab = new Ext.form.Label({text: "拼音码"})
            this.pymText = new Ext.form.TextField({name: 'ZJDM'});
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
            return [ssmcLab, '-', this.ssmcText, '-', pymLab, '-', this.pymText, '-', btn, '-', btn_qr];
        },
        //确认调入
        doSave: function () {
            var r = this.grid.getSelectionModel().getSelections();
            if (!r || r.length == 0) {
                MyMessageTip.msg("提示", "没有选中的记录", true);
                return;
            }
            this.mask("正在调入...");
            var ssnmList = [];
            for(var i=0 ; i<r.length ; i++ ){
                var ssnmxx = {};
                //手术内码信息
                ssnmxx["SSDM"] = r[i].data.SSDM;
                ssnmxx['SSMC'] = r[i].data.SSMC;
                ssnmxx["SSDJ"] = 1;
                ssnmxx["ZJDM"] = r[i].data.ZJDM;
                ssnmxx["FYXH"] = 0;
                ssnmxx["ZYBM"] = r[i].data.SSDM;
                ssnmList.push(ssnmxx);
            }
            var ret = phis.script.rmi.miniJsonRequestSync({
                serviceId: "OperationCodeService",
                serviceAction: "saveSsnm",
                body: ssnmList
            });
            this.unmask();
            if (ret.code > 200) {
                this.processReturnMsg(ret.code, ret.msg);
                return;
            }
            this.getWin().hide();
            MyMessageTip.msg("提示","手术内码调入成功",true);
        }
    })