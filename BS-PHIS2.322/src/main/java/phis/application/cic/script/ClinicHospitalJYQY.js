$package("phis.application.cic.script")

$import("phis.script.SimpleModule")
$styleSheet("phis.resources.css.app.biz.cic_css")
$styleSheet("phis.resources.css.app.biz.style")
phis.application.cic.script.ClinicHospitalJYQY = function(cfg) {
	this.isModify = false;
	phis.application.cic.script.ClinicHospitalJYQY.superclass.constructor.apply(
			this, [cfg]);
}

/*******************************************************************************
 * 【溧水】家医签约(家医签约系统)页面内嵌HIS Wangjl 2018-09-17
 */
Ext.extend(phis.application.cic.script.ClinicHospitalJYQY,
		phis.script.SimpleModule, {
			initPanel : function() {
				var empiData=this.exContext.empiData;
			                var res = phis.script.rmi.miniJsonRequestSync({
					              serviceId : "clinicManageService",
					              serviceAction : "getjsdqyjcxx",
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
			                     jyqyxx.contactName=empiData.contact?empiData.contact:"";
			 					 jyqyxx.contactPhone=empiData.contactPhone?empiData.contactPhone:"";
			                     url="userId="+jyqyxx.userId+"&idCard="+jyqyxx.idCard+"&personName="+jyqyxx.personName+"&phoneNo="+
		               	                jyqyxx.phoneNo+"&province="+jyqyxx.province+"0000000000&city="+jyqyxx.city+"00000000&district="+jyqyxx.district+"000000&street="+
			                            jyqyxx.street+"000&address="+jyqyxx.address+"&contactName="+ jyqyxx.contactName+"&contactPhone="+ jyqyxx.contactPhone

			             var encodeurl = this.base64encode(encodeURI(url));
				         if (this.panel)
					     return this.panel;
				         var panel = new Ext.Panel({
							border : false,
							html : "<iframe src='"+"http://10.2.202.34/hc-admin/module/addSign_other.html?"+url+"'scrolling='yes' frameborder=0 width=100% height=100%></iframe>",
							frame : true,
							autoScroll : true
						});
				this.panel = panel;
				panel.on("afterrender", this.onReady, this)
				return panel;		
        },		
			onReady : function() {
			},
			//加密
		base64encode :function (str) {
			var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
			var base64DecodeChars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
			63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
			32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
			50, 51, -1, -1, -1, -1, -1);		
	    	var out, i, len;
	    	var c1, c2, c3;
	    	len = str.length;
	    	i = 0;
		    out = "";
		    while(i < len) {
		        c1 = str.charCodeAt(i++) & 0xff;
		        if(i == len)
		        {
		            out += base64EncodeChars.charAt(c1 >> 2);
		            out += base64EncodeChars.charAt((c1 & 0x3) << 4);
		            out += "==";
		            break;
		        }
		        c2 = str.charCodeAt(i++);
		        if(i == len)
		        {
		            out += base64EncodeChars.charAt(c1 >> 2);
		            out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
		            out += base64EncodeChars.charAt((c2 & 0xF) << 2);
		            out += "=";
		            break;
		        }
		        c3 = str.charCodeAt(i++);
		        out += base64EncodeChars.charAt(c1 >> 2);
		        out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
		        out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >>6));
		        out += base64EncodeChars.charAt(c3 & 0x3F);
		    }
		    return out;	
	    }  		
    });	