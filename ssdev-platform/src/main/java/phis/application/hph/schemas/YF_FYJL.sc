<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_FYJL" alias="发药记录表">
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="JLID" alias="记录ID" type="long" length="18" not-null="1" generator="assigned" pkey="true">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="FYSJ" alias="发药时间" type="timestamp" not-null="1"/>
  <item id="FYGH" alias="发药工号" length="10">
  	<dic id="user_YFYW"></dic>
  </item>
  <item id="FYBQ" alias="发药病区" type="long" length="18">
  	<dic id="department_bq" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" autoLoad="true">
  	</dic>
  </item>
  <item id="FYLX" alias="发药类型" type="int" length="1" not-null="1">
  	<dic id="dispensingType"></dic>
  </item>
  <item id="YFSB" alias="药房识别" type="long" length="18"/>
  <item id="FYFS" alias="发药方式" type="long" length="18" not-null="1"/>
  <item id="DYPB" alias="打印判断" type="int" length="1" not-null="1"/>
</entry>
