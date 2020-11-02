$package("chis.script")
$import(
	"app.desktop.Module","chis.script.CombinedDocList",
	"util.dictionary.TreeDicFactory"
)
chis.script.CombinedManageUnitDocList=function(cfg){
	chis.script.CombinedManageUnitDocList.superclass.constructor.apply(this,[cfg])
}
Ext.extend(chis.script.CombinedManageUnitDocList,chis.script.CombinedDocList,{
createNavTab:function (){
	var manageUnit = this.mainApp.deptId ;
    var manageUnitText = this.mainApp.dept;
//	if(manageUnit && manageUnit.length>=6){
//		manageUnit = manageUnit.substring(0,6)
//	}
	
	var manageUnitDicTree = util.dictionary.TreeDicFactory.createTree({
		id:"manageUnit",
		parentKey:  manageUnit ,
        parentText: manageUnitText,
        lengthLimit : "12",
		rootVisible:true
	})
	this.manageUnitDicTree = manageUnitDicTree
	manageUnitDicTree.title = "管辖机构"
	manageUnitDicTree.on("click",this.onManageUnitTreeSelect,this)
	manageUnitDicTree.on("contextmenu", this.onManageUnitTreeContextmenu,this)
	
	
	var navTab = new Ext.TabPanel({
           activeTab:0,
           tabPosition: 'bottom',
           items:[manageUnitDicTree]
    })
    this.navTab = navTab
    return navTab
}	
})