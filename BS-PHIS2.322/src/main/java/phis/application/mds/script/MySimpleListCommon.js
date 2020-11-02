$package("phis.application.mds.script")
phis.application.mds.script.MySimpleListCommon = {
loadData:function(){
this.requestData.serviceId=this.fullServiceId;
this.requestData.serviceAction=this.serviceAction;
phis.script.SimpleList.superclass.loadData.call(this);
}
}