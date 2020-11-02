<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_FYJL" alias="发药记录表"  sort="a.FYSJ desc">
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
  <item id="JLID" alias="记录ID" type="long" length="18" not-null="1" generator="assigned" pkey="true" display="0">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="FYSJ" alias="发药时间" type="datetime" not-null="1" width="150"/>
  <item id="FYGH" alias="操作员" length="10">
  	<dic id="phis.dictionary.user_YFYW"></dic>
  </item>
  <item id="FYBQ" alias="发药病区" type="long" length="18">
  	<dic id="phis.dictionary.department_bq" filter="['eq',['$map',['s','JGID']],['$','%user.manageUnit.id']]" autoLoad="true">
  	</dic>
  </item>
  <item id="FYLX" alias="发药类型" type="int" length="1" not-null="1">
  	<dic id="phis.dictionary.dispensingType"></dic>
  </item>
  <item id="YFSB" alias="药房识别" type="long" length="18" display="0"/>
  <item id="FYFS" alias="发药方式" type="long" length="18" not-null="1" display="0"/>
  <item id="DYPB" alias="打印判断" type="int" length="1" not-null="1" display="0"/>
</entry>
