/**
 * Created by Administrator on 2018-02-07.
 */
$package("chis.application.scm.sr.script")
$import("chis.script.BizCombinedModule2")

chis.application.scm.sr.script.SignContractServiceModule=function (cfg) {
    cfg.itemWidth=450;
    chis.application.scm.sr.script.SignContractServiceModule.superclass.constructor.apply(this,[cfg]);
    this.firstTitle="签约服务记录";
    this.secondTitle="人员签约列表";
    this.on("winShow",this.onWinShow,this);
}

Ext.extend(chis.application.scm.sr.script.SignContractServiceModule,chis.script.BizCombinedModule2,{
    initPanel : function() {
        var panel = chis.application.scm.sr.script.SignContractServiceModule.superclass.initPanel.call(this);
        this.panel = panel;
        this.scpList = this.midiModules[this.actions[0].id];
        this.scpList.opener = this;
        this.scpList.on("loadData", this.onSCPListLoadData, this);
        this.scpGist = this.scpList.grid;
        this.scpGist.on("rowclick", this.onSCPListRowClick, this);
        this.scsList = this.midiModules[this.actions[1].id];
        this.scsList.opener = this;
        return panel;
    },
    onReady : function() {
        //chis.application.scm.sr.script.SignContractServiceModule.superclass.onReady.call(this);
        this.loadData();
    },
    onSCPListLoadData : function (store) {
        if (!store || store.getCount() == 0) {
            return;
        }
        if (!this.scpList.selectedIndex) {
            this.scpList.selectedIndex = 0;
        }
        var r = store.getAt(this.scpList.selectedIndex);
        this.process(r);
    },
    onSCPListRowClick : function (grid, index, e) {
        var r = this.scpList.getSelectedRecord();
        this.process(r);
    },
    process : function(r) {
        if (!r) {
            return;
        }
        var empiId = r.get("favoreeEmpiId") ||'';
        var SCID = r.get("SCID")||'';
        var date=new Date;  
        var year=date.getFullYear(); 
        var cnd = ['and',['eq',['$','a.empiId'],['s',empiId]],['eq',['$','a.SCID'],['s',SCID]]];
        this.scsList.requestData.cnd = cnd;
        this.scsList.loadData();
    },
    loadData : function () {
        this.scpList.loadData();
    }
})