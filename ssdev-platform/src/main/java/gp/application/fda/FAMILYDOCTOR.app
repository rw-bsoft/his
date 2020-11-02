<?xml version="1.0" encoding="UTF-8"?>
<application id="gp.application.fda.FAMILYDOCTOR"
	name="家庭医生助理">
	<catagory id="REL" ref="gp.application.rel.REL/REL" />
	<catagory id="FHR" ref="chis.application.fhr.FHR/FHR" />
	<catagory id="CVM" ref="gp.application.cvm.CVM/CVM" />
	<catagory id="RVC" ref="chis.application.rvc.RVC/RVC" />
	<catagory id="FD" ref="gp.application.fd.FAMILYDOCTOR/FD" />
	<catagory id="FDA"  name="家庭医生助理" type="1">
		<module id="FDA1" name="预约服务" type="1" script="gp.application.fda.script.AppointmentQueryList"> 
			<properties> 
				<p name="entryName">gp.application.fda.schemas.GP_YYGL</p>  
			</properties>  
			<action id="refreshWin" name="查询" iconCls="query" />
			<action id="cancel" name="取消" iconCls="common_cancel"/> 
		</module>   
	</catagory>
</application>

