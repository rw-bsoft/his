$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
phis.application.cic.script.ClinicTnbbfzscModule = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicTnbbfzscModule.superclass.constructor.apply(this, [ cfg ]);
}

Ext.extend(phis.application.cic.script.ClinicTnbbfzscModule,
		   phis.script.SimpleModule,{
		 	initPanel : function() {
				var empiData=this.exContext.empiData;
				var hzjzkh=empiData.BRID;
				var hzsfzh=empiData.idCard;
				var mzh=empiData.MZHM;
			                var res = phis.script.rmi.miniJsonRequestSync({
					              serviceId : "clinicManageService",
					              serviceAction : "getjsdqyjcxx",
					              body : {
						                      "empiid" :empiData.empiId
					                         }
				         });
			               var tnbbfzsc={};
			                 tnbbfzsc.yybh=res.json.manageUnit;
			                tnbbfzsc.ysbh=res.json.userIdcode;
			     if(this.id=="CIC48"){
		 			 	 this.tnbbfzscurl="http://10.2.200.202:8070/his/memberIntelligentResult.do?yybh="+ tnbbfzsc.yybh+"&ysbh="+tnbbfzsc.ysbh+"&hzjzkh="+hzjzkh+"&hzsfzh="+hzsfzh+"&mzh="+mzh+"&zyh=&viewReportOnly=1";
		 		  }else if(this.id=="WAR65"){
                       this.tnbbfzscurl="http://10.2.200.202:8070/web/his/memberIntelligentResult.do?yybh="+ tnbbfzsc.yybh+"&ysbh="+tnbbfzsc.ysbh+"&hzjzkh="+hzjzkh+"&hzsfzh="+hzsfzh+"&mzh="+mzh+"&zyh="+this.exContext.brxx.data.ZYHM+"&viewReportOnly=1";
		 		  }
				if (this.panel){
					return this.panel;
				}
				var panel = new Ext.Panel({
					id : 'DXT_'+this.exContext.empiData.empiId,
					border : false,
					html : "<iframe id='DXT_"+this.exContext.empiData.empiId+"_iframe' src='"+this.tnbbfzscurl+"' scrolling='yes' frameborder=0 width=100% height=600 ></iframe>",
					frame : true,
					autoScroll : true
				});
				this.panel = panel;
				return panel;
			}
});