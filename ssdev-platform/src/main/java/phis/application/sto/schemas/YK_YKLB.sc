<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YKLB" alias="药库列表" sort="a.YKSB">
  <item id="YKSB" alias="药库识别" length="18" not-null="1" display="1"  type="long" generator="assigned" pkey="true">
  		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="JGID" alias="机构ID" length="20" not-null="1" defaultValue="%user.manageUnit.id" display="0"/>
  <item id="YKMC" alias="药库名称" type="string" length="30" not-null="1" renderer="onRenderer_reg"/>
  <item id="YKLB" alias="药库类别" length="1" type="int" not-null="1" defaultValue="1">
  	<dic>
  		<item key="1" text="西药库"/>
  		<item key="2" text="中药库"/>
  	</dic>
  </item>
  <item id="SYBZ" alias="使用标志" length="1" type="int" not-null="1" defaultValue="0" fixed="true">
  	<dic>
  		<item key="0" text="未使用"/>
  		<item key="1" text="初始化"/>
  		<item key="2" text="初始建账"/>
  	</dic>
  </item>
</entry>
