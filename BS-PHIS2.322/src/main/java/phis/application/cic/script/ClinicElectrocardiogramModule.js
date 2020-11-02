$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
phis.application.cic.script.ClinicElectrocardiogramModule = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicElectrocardiogramModule.superclass.constructor.apply(this, [ cfg ]);
}

Ext.extend(phis.application.cic.script.ClinicElectrocardiogramModule,
		   phis.script.SimpleModule,{
		 	initPanel : function() {
				var empiData=this.exContext.empiData;
			                var res = phis.script.rmi.miniJsonRequestSync({
					              serviceId : "clinicManageService",
					              serviceAction : "getjsdqyjcxx",
					              body : {
						                      "empiid" :empiData.empiId
					                         }
				         });
			               var xdxx={};
			                xdxx.manageUnit=res.json.manageUnit;
			if( xdxx.manageUnit.indexOf("320124004")!=-1 || xdxx.manageUnit.indexOf("320124008")!=-1){                
		 		    if(this.id=="WAR641"){
		 			     this.xdturl="http://10.2.202.138:80/HisReportList.aspx?HospCode="+this.exContext.brxx.data.ZYHM+" and Address="+xdxx.manageUnit;
		 		    }else if(this.id=="CIC471"){
		 			 	 this.xdturl="http://10.2.202.138:80/HisReportList.aspx?ClinicCode="+this.exContext.JZKH;
		 		    }	
		  	}else{
		 		  if(this.id=="WAR641"){
		 			    this.xdturl="http://10.2.200.202:8001/HisReportList.aspx?HospCode="+this.exContext.brxx.data.ZYHM;
		 		  }else if(this.id=="CIC471"){
		 			 	 this.xdturl="http://10.2.200.202:8001/HisReportList.aspx?ClinicCode="+this.exContext.JZKH;
		 		  }
		 	}
				if (this.panel){
					return this.panel;
				}
				var panel = new Ext.Panel({
					id : 'DXT_'+this.exContext.empiData.empiId,
					border : false,
					html : "<iframe id='DXT_"+this.exContext.empiData.empiId+"_iframe' src='"+this.xdturl+"' scrolling='yes' frameborder=0 width=100% height=600 ></iframe>",
					frame : true,
					autoScroll : true
				});
				this.panel = panel;
				return panel;
			}
});