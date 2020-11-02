<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.20" name="责任医生助理" pageCount="1" type="T20" version="1445568104459">
  <accredit>
    <apps>
      <app id="chis.application.index.INDEX">
		<others/>
	  </app>
      <app id="chis.application.healthmanage.HEALTHMANAGE">
        <others/>
      </app>
      <app id="chis.application.diseasemanage.DISEASEMANAGE">
        <others/>
      </app>
      <app id="chis.application.gynecology.GYNECOLOGY">
        <others/>
      </app>
      <app id="chis.application.diseasecontrol.DISEASECONTROL">
        <others/>
      </app>
      <app id="chis.application.common.COMMON">
        <others/>
      </app>
      <app id="chis.application.hy.HY">
        <others/>
      </app>
      <app id="chis.application.tr.TR">
        <others/>
      </app>
      <app id="chis.application.yfgl.YFGL">
        <others/>
      </app>
      <app id="chis.application.systemmanage.SYSTEMMANAGE">
      	<catagory id="CHIS_REL" name="责医助理关联">
			<module id="REL03" ref="chis.application.rel.REL/REL/REL03" >
				<others/>
			</module>
			<module id="REL04" ref="chis.application.rel.REL/REL/REL04" />
		</catagory>
      </app> 
    </apps>
    <storage acType="whitelist"> 
      <others acValue="1111"/> 
    </storage>
    <workList acType="whitelist">
			<work id="01" />
			<work id="08" />
			<work id="09" />
			<work id="10" />
			<work id="11" />
			<work id="12" />
			<work id="13" />
			<work id="14" />
			<work id="15" />
			<work id="16" />
			<work id="17" />
			<work id="18" />
			<work id="19" />
			<work id="20" />
			<work id="21" />
			<work id="22" />
			<work id="23" />
		</workList>
		<reminderList acType="whitelist">
			<reminder id="1" />
			<reminder id="2" />
			<reminder id="4" />
			<reminder id="10" />
			<reminder id="13" />
			<reminder id="15" />
		</reminderList>
  </accredit>
</role>
