$package("chis.application.fhr.script")
$import("app.desktop.Module")
// 健康档案嵌入家庭医生、签约服务模块 Wangjl 2018-09-18
chis.application.fhr.script.FamilyContractBase_QYFW = function(cfg) {
	cfg.width = 810;
	cfg.modal = true
	chis.application.fhr.script.FamilyContractBase_QYFW.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.FamilyContractBase_QYFW,
		app.desktop.Module, {
			initPanel : function() {
				    var empiData=this.exContext.empiData;
	                var res = util.rmi.miniJsonRequestSync({
	                	serviceId : "chis.familyRecordService",
	                    schema : "",
						serviceAction : "SaveHealthRecord", 
						method : "execute",
			              body : {
				                      "empiid" :empiData.empiId
			                         }
		         });
	                var jyqyxxfw={};
	                
       	             jyqyxxfw.userId=res.json.userIdcode;
                     jyqyxxfw.idCard=empiData.idCard;
    	             jyqyxxfw.personName=empiData.personName;
   	                // jyqyxx.phoneNo=empiData.mobileNumber?empiData.mobileNumber:"" ;
                     jyqyxxfw.province=res.json.signer_provincecode;
                     jyqyxxfw.city=res.json.signer_citycode;
                     jyqyxxfw.district=res.json.signer_districtcode;
                     jyqyxxfw.street=res.json.signer_streetcode;
                     jyqyxxfw.address=empiData.address?empiData.address:"" ;
				var url = "http://10.2.202.34/hc-admin/module/addService.html?userId="+jyqyxxfw.userId+"&personName="+jyqyxxfw.personName+
				               "&idCard="+jyqyxxfw.idCard+"&province="+jyqyxxfw.province+"0000000000&city="+jyqyxxfw.city+"00000000&district="+jyqyxxfw.district+"000000&street="+
                               jyqyxxfw.street+"000&address="+jyqyxxfw.address
				var html="<iframe src="	+ url + " width='100%' height='107%' frameborder='no'></iframe>";
				var panel = new Ext.Panel({
				frame : false,
				autoScroll : true,
				html : html
				})
				this.panel = panel
				return panel
			 }
		})