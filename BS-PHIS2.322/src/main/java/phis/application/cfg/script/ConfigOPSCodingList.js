$package("phis.application.cfg.script");
$import("phis.script.SimpleList")

phis.application.cfg.script.ConfigOPSCodingList = function (cfg) {
    cfg.modal = true;
    phis.application.cfg.script.ConfigOPSCodingList.superclass.constructor.apply(this,
        [cfg])
};
Ext.extend(phis.application.cfg.script.ConfigOPSCodingList, phis.script.SimpleList, {
    doCndQuery: function () {
        if (this.cndField.getValue()){
            this.cndField.setValue(this.cndField.getValue().toUpperCase());
        }
        phis.application.cfg.script.ConfigOPSCodingList.superclass.doCndQuery.call(this)
    },
    //手术内码引入
    doLsjdr: function () {
        var drList = this.createModule("lsjdrList", "phis.application.cfg.CFG/CFG/CFG1501")
        // drList.on("qrdr", this.onQrdr, this);
        var win = drList.getWin();
        win.setWidth(1000);
        win.setHeight(600);
        win.add(drList.initPanel());
        win.show();
    },
})