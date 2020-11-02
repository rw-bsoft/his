$package("chis.application.scm.sign.script")
$import("chis.script.BizCombinedModule2")

chis.application.scm.sign.script.PersonalSignContractModule2 = function (cfg) {
    cfg.itemWidth = 260;
    this.layOutRegion = "north" // ** "west" or "north"
    cfg.modal = true;
    cfg.frame = true;
    chis.application.scm.sign.script.PersonalSignContractModule2.superclass.constructor.apply(this, [cfg]);
    this.on("winShow",this.onWinShow,this)
}

Ext.extend(chis.application.scm.sign.script.PersonalSignContractModule2, chis.script.BizCombinedModule2, {
    onWinShow : function(){
        this.scModule.haveRecord = false;
    },
    /**
     * 初始化面板
     *
     * @return {} panel
     */
    initPanel: function () {
        var panel = chis.application.scm.sign.script.PersonalSignContractModule2.superclass.initPanel.call(this);
        this.panel = panel;
        this.pscForm = this.midiModules[this.actions[0].id];
        this.scModule = this.midiModules[this.actions[1].id];
        this.pscForm.scModuleSubList = this.scModule.midiModules["pscList"];
        this.pscForm.scModule = this.scModule;
        this.scModule.father = this;
        return panel;
    },
    extendCfg: function (cfg) {

    },
    onReady: function () {
        // ** 设置滚动条
        this.panel.items.each(function (item) {
            item.setAutoScroll(true);
        }, this);
    },

    /**
     * 获取面板上的各项
     *
     * @return {}
     */
    getPanelItems: function () {
        var firstItem = this.getFirstItem();
        var secondItem = this.getSecondItem();
        var items = [{
            layout: "fit",
            border: false,
            frame: false,
            split: this.split,
            title: this.firstTitle || '',
            region: "north",
            width: this.itemWidth || this.width,
            height: 70,
            collapsible: true,
            items: firstItem
        }, {
            layout: "fit",
            border: false,
            frame: this.secondFrame || false,
            split: this.split,
            title: this.secondTitle || '',
            region: 'center',
            items: secondItem
        }]
        return items;
    },
    loadData: function () {
        Ext.apply(this.pscForm.exContext, this.exContext);
        Ext.apply(this.scModule.exContext, this.exContext);
        this.pscForm.loadData();
        this.scModule.loadData();
    },
    //刷新列表数据
    refreshList: function () {
        this.fireEvent("save", this);
    }
});