$package("chis.application.scm.bag.script")
$import("chis.script.BizCombinedModule2")
chis.application.scm.bag.script.ServicePackageModule = function (cfg) {
    chis.application.scm.bag.script.ServicePackageModule.superclass.constructor
        .apply(this, [cfg]);
    this.width = 1000;
    this.height = 600;
    this.title = "服务包维护";
}

Ext.extend(chis.application.scm.bag.script.ServicePackageModule,
    chis.script.BizCombinedModule2, {
        initPanel: function () {
            if (this.panel) {
                return this.panel;
            }
            var items = this.getPanelItems();
            var panel = new Ext.Panel({
                border: false,
                split: this.split,
                hideBorders: true,
                frame: this.frame || false,
                layout: 'border',
                width: this.width || 600,
                height: this.height || 300,
                items: items
            });
            this.panel = panel
            this.getModules();
            panel.on("afterrender", this.onReady, this)
            return panel
        },
        // onLoadModule : function(moduleName,module){
        // Ext.apply(module.exContext,this.exContext);
        // if(moduleName == this.actions[0].id){
        // this.spForm = module;
        // this.spForm.on("save", this.onSPFormSave, this);
        // }
        // if(moduleName == this.actions[1].id){
        // this.spiModule = module;
        // this.spiModule.SPID = this.SPID;
        // }
        // },
        getModules: function () {
            this.spForm = this.midiModules[this.actions[0].id]
            this.spiModule = this.midiModules[this.actions[1].id]
            Ext.apply(this.spForm.exContext, this.exContext);
            Ext.apply(this.spiModule.exContext, this.exContext);
            this.spForm.on("save", this.onSPFormSave, this);

        },
        onSPFormSave: function (entryName, op, json, data) {
            this.SPID = data.SPID;
            this.loadData();
            this.fireEvent("SPFormSave",this);
        },
        getPanelItems: function () {
            var spFormPanel = this.getFirstItem();
            var spiModulePanel = this.getSecondItem();
            // this.spiModule.SPID = this.SPID;
            // this.spiModule.opener = this;
            var items = [{
                layout: "fit",
                border: false,
                frame: false,
                split: true,
                title: this.firstTitle || '',
                region: 'north',
                height: 250,
                items: spFormPanel
            }, {
                layout: "fit",
                border: false,
                frame: false,
                split: true,
                title: this.secondTitle || '',
                region: 'center',
                items: spiModulePanel
            }]
            return items;
        },
        doNew: function () {
        },
        loadData: function () {

            if (this.spForm) {
                this.spForm.initDataId = this.SPID || '';
                this.spForm.loadData();
            }
            if (this.spiModule) {
                this.spiModule.SPID = this.SPID;
                this.spiModule.loadData();
            }
            this.setFormFiledStatus();
        },
        /*
         * 对已经签约的包设置不可改动
         */
        setFormFiledStatus: function () {
            var res = util.rmi.miniJsonRequestSync({
                serviceId: "chis.signContractRecordService",
                serviceAction: "queryTask",
                method: "execute"
            });
            if (res.json.body && res.json.body.length)
                for (var i = 0; i < res.json.body.length; i++) {
                    if (this.SPID == res.json.body[i]) {
                        // this.spiModule.leftList.grid.disable();
                        // this.spiModule.rightList.grid.disable();
                        // this.spForm.form.disable();
                        /*var btns = this.spiModule.rightList.grid.getTopToolbar().items;
                        var btn = btns.item(5);
                        btn.disable();*/
                    }
                }
        }
    });