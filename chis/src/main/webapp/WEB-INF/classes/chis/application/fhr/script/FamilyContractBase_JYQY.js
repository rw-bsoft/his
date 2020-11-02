$package("chis.application.fhr.script")
$import("app.desktop.Module")
// 健康档案嵌入家庭医生、签约服务模块 Wangjl 2018-09-18
chis.application.fhr.script.FamilyContractBase_JYQY = function(cfg) {
	cfg.width = 810;
	cfg.modal = true
	chis.application.fhr.script.FamilyContractBase_JYQY.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(chis.application.fhr.script.FamilyContractBase_JYQY,
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
	                var jyqyxx={};
	                
       	             jyqyxx.userId=res.json.userIdcode;
                     jyqyxx.idCard=empiData.idCard;
    	             jyqyxx.personName=empiData.personName;
   	                 jyqyxx.phoneNo=empiData.mobileNumber?empiData.mobileNumber:"" ;
                     jyqyxx.province=res.json.signer_provincecode;
                     jyqyxx.city=res.json.signer_citycode;
                     jyqyxx.district=res.json.signer_districtcode;
                     jyqyxx.street=res.json.signer_streetcode;
                     jyqyxx.address=empiData.address?empiData.address:"" ;
                     jyqyxx.contactName=empiData.contact;
					 jyqyxx.contactPhone=empiData.contactPhone;
					 
				var url = "http://10.2.202.34/hc-admin/module/addSign_other.html?userId="+jyqyxx.userId+"&idCard="+jyqyxx.idCard+"&personName="+jyqyxx.personName+"&phoneNo="+
	                           jyqyxx.phoneNo+"&province="+jyqyxx.province+"0000000000&city="+jyqyxx.city+"00000000&district="+jyqyxx.district+"000000&street="+
                               jyqyxx.street+"000&address="+jyqyxx.address+"&contactName="+ jyqyxx.contactName+"&contactPhone="+ jyqyxx.contactPhone
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